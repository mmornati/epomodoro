package net.mornati.epomodoro.communication;

import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.mornati.epomodoro.Activator;
import net.mornati.epomodoro.preference.PomodoroPreferencePage;

import org.eclipse.jface.preference.IPreferenceStore;
import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.protocols.UDP;
import org.jgroups.stack.Protocol;

public class Communication {

	private static Communication communication;
	private JChannel channel;
	private List<TextMessage> receivedTextMessages=new ArrayList<TextMessage>();
	private static final Logger LOG=Logger.getLogger(Communication.class.getName());

	private Communication() {

	}

	public static Communication getInstance() {
		if (communication == null) {
			communication=new Communication();
		}
		return communication;
	}

	public void connect(final String groupName) throws Exception {
		URL url=Activator.getDefault().getBundle().getResource("resources/udp.xml");
		IPreferenceStore preferenceStore=Activator.getDefault().getPreferenceStore();
		boolean forceIPv4=preferenceStore.getBoolean(PomodoroPreferencePage.FORCE_IPV4);
		boolean discardOwnMessage=preferenceStore.getBoolean(PomodoroPreferencePage.DISCARD_OWN_MESSAGE);
		String ipAddress=preferenceStore.getString(PomodoroPreferencePage.BIND_IP_ADDR);
		if (forceIPv4) {
			LOG.log(Level.INFO, "Forcing IPv4 usage");
			System.setProperty("java.net.preferIPv4Stack", "true");
		} else {
			System.setProperty("java.net.preferIPv4Stack", "false");
		}
		channel=new JChannel();
		if (ipAddress != null && !ipAddress.equals("")) {
			bindAddress(channel, ipAddress);
		}
		channel.connect(groupName);
		channel.setDiscardOwnMessages(discardOwnMessage);
	}

	public void sendMessage(AbstractPomodoroMessage message) throws Exception {
		sendMessage(message, null);
	}

	public void sendMessage(AbstractPomodoroMessage message, Address destination) throws Exception {
		channel.send(new Message(destination, null, message));
	}

	public void close() {
		channel.close();
	}

	public void setReceiver(ReceiverAdapter receiver) {
		channel.setReceiver(receiver);
	}

	public boolean isConnected() {
		if (channel == null) {
			return false;
		}
		return channel.isConnected();
	}

	public List<TextMessage> getReceivedMessages() {
		return receivedTextMessages;
	}

	public void resetReceivedMessages() {
		receivedTextMessages.clear();
	}

	public String getBindAddress() {
		return channel.getAddressAsString();
	}

	public void addReceivedMessage(TextMessage message) {
		receivedTextMessages.add(message);
	}

	public static AbstractPomodoroMessage createMessage(Class<?> toCreate) {
		AbstractPomodoroMessage message;
		try {
			message=(AbstractPomodoroMessage) toCreate.newInstance();
		} catch (InstantiationException e1) {
			e1.printStackTrace();
			return null;
		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
			return null;
		}
		message.setCreated(new Date());
		String senderMachine="";
		try {
			senderMachine=InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		message.setSenderMachine(senderMachine);
		IPreferenceStore preferenceStore=Activator.getDefault().getPreferenceStore();
		String sender=preferenceStore.getString(PomodoroPreferencePage.CLIENT_NAME);
		if (sender != null && !sender.equals("")) {
			message.setSender(sender);
		} else {
			message.setSender(senderMachine);
		}

		return message;
	}

	private void bindAddress(JChannel channel, String ip) throws UnknownHostException {
		for (Protocol protocol : channel.getProtocolStack().getProtocols()) {
			if (protocol instanceof UDP) {
				((UDP) protocol).setValue("bind_addr", InetAddress.getByName(ip));
				break;
			}
		}
	}
}
