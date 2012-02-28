package net.mornati.epomodoro.communication;

import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Date;

import net.mornati.epomodoro.Activator;
import net.mornati.epomodoro.preference.PomodoroPreferencePage;

import org.eclipse.jface.preference.IPreferenceStore;
import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;

public class Communication {

	private static Communication communication;
	private JChannel channel;

	private Communication() {

	}

	public static Communication getInstance() {
		if (communication == null) {
			communication=new Communication();
		}
		return communication;
	}

	public void connect(final String groupName, boolean discardOwnMessage) throws Exception {
		URL url=Activator.getDefault().getBundle().getResource("resources/udp.xml");
		channel=new JChannel(url.openStream());
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

}
