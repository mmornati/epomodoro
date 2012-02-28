package net.mornati.epomodoro.communication;

public class TextMessage extends AbstractPomodoroMessage {

	private static final long serialVersionUID=-1159732185983323581L;
	private String message;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message=message;
	}

}
