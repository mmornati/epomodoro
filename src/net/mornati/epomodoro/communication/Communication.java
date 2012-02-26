package net.mornati.epomodoro.communication;

import java.net.URL;

import net.mornati.epomodoro.Activator;

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
		channel.send(new Message(null, null, message));
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

}
