package net.mornati.epomodoro.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PomodoroTimer extends Thread {

	private long currentTime;
	private static final SimpleDateFormat formatter=new SimpleDateFormat("mm : ss");
	private boolean paused=false;
	private boolean started=false;

	public PomodoroTimer(long time) {
		this.currentTime=time;
	}

	public void run() {
		started=true;
		while (currentTime > 0) {
			if (!paused) {
				currentTime=currentTime - 1000;
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void pause() {
		paused=!paused;
	}

	public String getFormatTime() {
		return formatter.format(new Date(currentTime));
	}

	public long getTime() {
		return currentTime;
	}

	public String getStatus() {
		if (!started) {
			return "Initialized";
		} else if (currentTime > 0 && !paused) {
			return "Running";
		} else if (currentTime > 0 && paused) {
			return "Paused";
		} else {
			return "Finished";
		}
	}

}
