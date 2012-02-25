package net.mornati.epomodoro.communication;

import java.io.Serializable;
import java.util.Date;

public class AbstractPomodoroMessage implements Serializable {
	private static final long serialVersionUID=5446125674462893054L;
	private Date created;
	private String sender;
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
	
	

}
