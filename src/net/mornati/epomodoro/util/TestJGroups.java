package net.mornati.epomodoro.util;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.net.URL;

import net.mornati.epomodoro.Activator;

import org.jgroups.Address;
import org.jgroups.Channel;
import org.jgroups.ChannelListener;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;
import org.jgroups.stack.AddressGenerator;

public class TestJGroups extends ReceiverAdapter implements ChannelListener {

	public TestJGroups() {
		JChannel channel;
		try {
			System.setProperty("java.net.preferIPv4Stack", "true");
			channel=new JChannel();
			Address add = channel.getAddress();
			channel.setDiscardOwnMessages(true);
			channel.setReceiver(this);
			
			channel.connect("MyTeam", null, 2000);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void viewAccepted(View new_view) {
		System.out.println("** view: " + new_view);
	}

	public void receive(Message msg) {
		System.out.println(msg.getObject());
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		TestJGroups test=new TestJGroups();

	}

	@Override
	public void channelConnected(Channel channel) {
		System.out.println(channel.getName());

	}

	@Override
	public void channelDisconnected(Channel channel) {
		System.out.println(channel.getName());
	}

	@Override
	public void channelClosed(Channel channel) {
		System.out.println(channel.getName());
	}

}
