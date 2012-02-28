package net.mornati.epomodoro.communication;

public class TimerMessage extends AbstractPomodoroMessage {

	private static final long serialVersionUID=7309158009948016867L;
	private String timer;
	private String status;

	public TimerMessage() {
		super();
	}

	public TimerMessage(String sender, String timer, String status) {
		super();
		super.setSender(sender);
		this.timer=timer;
		this.status=status;
	}

	public String getTimer() {
		return timer;
	}

	public void setTimer(String timer) {
		this.timer=timer;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status=status;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof TimerMessage)) {
			return false;
		}
		TimerMessage t=(TimerMessage) o;
		if (this.getSender() != null && this.getSenderMachine().equals(t.getSenderMachine())) {
			return true;
		}
		return false;
	}

}
