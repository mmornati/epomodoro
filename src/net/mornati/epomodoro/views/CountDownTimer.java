package net.mornati.epomodoro.views;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.mornati.epomodoro.Activator;
import net.mornati.epomodoro.communication.TimerMessage;
import net.mornati.epomodoro.preference.PomodoroPreferencePage;
import net.mornati.epomodoro.util.PomodoroTimer;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.part.ViewPart;

public class CountDownTimer extends ViewPart {

	private static final Logger LOG=Logger.getLogger(CountDownTimer.class.getName());
	long TOTAL_TIME;
	private long time;

	final java.text.SimpleDateFormat sdf=new java.text.SimpleDateFormat("mm : ss");

	public CountDownTimer() {
		IPreferenceStore preferenceStore=Activator.getDefault().getPreferenceStore();
		int preferenceTime=preferenceStore.getInt(PomodoroPreferencePage.POMODORO_TIME);
		TOTAL_TIME=preferenceTime * 60 * 1000;
		time=TOTAL_TIME;
	}

	@Override
	public void createPartControl(Composite parent) {
		Composite container=new Composite(parent, SWT.NULL);
		GridLayout layout=new GridLayout();
		container.setLayout(layout);
		layout.numColumns=3;
		layout.verticalSpacing=9;
		Label label=new Label(container, SWT.NULL);
		label.setText("Timer:");
		final Label countdown=new Label(container, SWT.NULL);
		countdown.setText(sdf.format(time));
		final Label typeLabel=new Label(container, SWT.NULL);
		typeLabel.setText("Type");
		final Button startButton=new Button(container, SWT.NULL);
		scheduleTimer(countdown, typeLabel, 1000);
		startButton.setText("Start");
		startButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				Activator.getDefault().getTimer().start();
				startButton.setEnabled(false);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub

			}
		});
		final Button pauseButton=new Button(container, SWT.NULL);
		pauseButton.setText("Pause");
		pauseButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				Activator.getDefault().getTimer().pause();
				String buttonText=Activator.getDefault().getTimer().getStatus().equals(PomodoroTimer.STATUS_PAUSED) ? "Restart" : "Pause";
				pauseButton.setText(buttonText);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub

			}
		});
		final Button resetButton=new Button(container, SWT.NULL);
		resetButton.setText("Reset");
		checkTimerStatus(Activator.getDefault().getTimer());
		sendTimerMessage(Activator.getDefault().getTimer());

	}

	private void scheduleTimer(final Label timerLabel, final Label typeLabel, final int changeInterval) {
		final PomodoroTimer internalTimer;
		if (Activator.getDefault().getTimer() == null) {
			internalTimer=Activator.getDefault().createTimer(TOTAL_TIME, PomodoroTimer.TYPE_WORK);
		} else {
			internalTimer=Activator.getDefault().getTimer();
		}
		Display.getDefault().timerExec(changeInterval, new Runnable() {
			public void run() {
				if (internalTimer != null) {
					timerLabel.setText(internalTimer.getFormatTime());
					typeLabel.setText(internalTimer.getType() == PomodoroTimer.TYPE_WORK ? "W.T." : "P.T.");
					if (Activator.getDefault().isShowDialog()) {
						String message=(Activator.getDefault().getTimer().getType() == PomodoroTimer.TYPE_WORK ? "Working " : "Pausing ") + "Time finished";
						MessageDialog.openInformation(Display.getDefault().getActiveShell(), "Pomodoro Timer Finished", message);
						Activator.getDefault().setShowDialog(false);
					}
					scheduleTimer(timerLabel, typeLabel, changeInterval);
				} else {
					timerLabel.setText(sdf.format(new Date(TOTAL_TIME)));
					scheduleTimer(timerLabel, typeLabel, changeInterval);
				}

			}
		});
	}

	private void checkTimerStatus(final PomodoroTimer timer) {
		final Timer scheduler=new Timer();
		TimerTask task=new TimerTask() {
			@Override
			public void run() {
				PomodoroTimer newTimer;
				if (timer.getStatus().equals(PomodoroTimer.STATUS_FINISHED)) {
					Activator.getDefault().setShowDialog(true);
					while (Activator.getDefault().isShowDialog()) {
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							LOG.log(Level.SEVERE, "Error sleeping Thread", e);
						}
					}
					IPreferenceStore preferenceStore=Activator.getDefault().getPreferenceStore();
					if (timer != null && timer.getType() == PomodoroTimer.TYPE_WORK) {
						int pauseTimer=preferenceStore.getInt(PomodoroPreferencePage.POMODORO_PAUSE) * 60 * 1000;
						newTimer=Activator.getDefault().createTimer(pauseTimer, PomodoroTimer.TYPE_PAUSE);
					} else {
						TOTAL_TIME=preferenceStore.getInt(PomodoroPreferencePage.POMODORO_TIME) * 60 * 1000;
						newTimer=Activator.getDefault().createTimer(TOTAL_TIME, PomodoroTimer.TYPE_WORK);
					}
					if (preferenceStore.getBoolean(PomodoroPreferencePage.WORK_PAUSE_AUTO_SWITCH)) {
						newTimer.start();
					}
				}

			}

		};
		scheduler.schedule(task, 1000, 1000);
	}

	private void sendTimerMessage(final PomodoroTimer timer) {
		final Timer scheduler=new Timer();
		TimerTask task=new TimerTask() {

			@Override
			public void run() {
				if (Activator.getDefault().getCommunication() != null && Activator.getDefault().getCommunication().isConnected() && timer != null) {
					TimerMessage message=new TimerMessage();
					message.setCreated(new Date());
					message.setTimer(timer.getFormatTime());
					message.setStatus(timer.getStatus());
					IPreferenceStore preferenceStore=Activator.getDefault().getPreferenceStore();
					String sender=preferenceStore.getString(PomodoroPreferencePage.CLIENT_NAME);
					if (sender == null || sender.equals("")) {
						try {
							sender=InetAddress.getLocalHost().getHostName();
						} catch (UnknownHostException e) {
							LOG.log(Level.SEVERE, "Error retrieving workstation name", e);
						}
					}
					message.setSender(sender);
					try {
						Activator.getDefault().getCommunication().sendMessage(message);
					} catch (Exception e) {
						LOG.log(Level.SEVERE, "Error sending message", e);
					}
				}

			}
		};

		scheduler.schedule(task, 1000, 1000);
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

}
