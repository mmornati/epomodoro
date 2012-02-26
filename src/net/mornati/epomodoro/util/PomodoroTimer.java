package net.mornati.epomodoro.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PomodoroTimer extends Thread {

	private long currentTime;
	private static final SimpleDateFormat formatter=new SimpleDateFormat("mm : ss");
	private boolean paused=false;
	private boolean started=false;
	public static final String STATUS_WORKING_TIME="Working time";
	public static final String STATUS_PAUSING_TIME="Pausing time";
	public static final String STATUS_PAUSED="Paused";
	public static final String STATUS_FINISHED="Finished";
	public static final String STATUS_INITIALIZED="Initilized";

	public static final int TYPE_WORK=0;
	public static final int TYPE_PAUSE=1;
	private int type=TYPE_WORK;

	public PomodoroTimer(long time, int type) {
		this.currentTime=time;
		this.type=type;
		started=false;
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

	public int getType() {
		return type;
	}

	public String getStatus() {
		if (!started) {
			return STATUS_INITIALIZED;
		} else if (currentTime > 0 && !paused) {
			if (type == TYPE_WORK) {
				return STATUS_WORKING_TIME;
			} else {
				return STATUS_PAUSING_TIME;
			}
		} else if (currentTime > 0 && paused) {
			return STATUS_PAUSED;
		} else {
			return STATUS_FINISHED;
		}
	}

}
