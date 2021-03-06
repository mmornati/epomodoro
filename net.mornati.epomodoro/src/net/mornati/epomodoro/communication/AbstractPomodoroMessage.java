package net.mornati.epomodoro.communication;

import java.io.Serializable;
import java.util.Date;

import org.jgroups.Address;

public class AbstractPomodoroMessage implements Serializable {
	private static final long serialVersionUID=5446125674462893054L;
	private Date created;
	private String sender;
	private String senderMachine;
	private Address sourceAddress;

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created=created;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender=sender;
	}

	public String getSenderMachine() {
		return senderMachine;
	}

	public void setSenderMachine(String senderMachine) {
		this.senderMachine=senderMachine;
	}

	public Address getSourceAddress() {
		return sourceAddress;
	}

	public void setSourceAddress(Address sourceAddress) {
		this.sourceAddress=sourceAddress;
	}

}
