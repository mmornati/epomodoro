package net.mornati.epomodoro.views;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import net.mornati.epomodoro.Activator;
import net.mornati.epomodoro.communication.TimerMessage;
import net.mornati.epomodoro.util.PomodoroTimer;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.part.ViewPart;

public class CountDownTimer extends ViewPart {

	final long THIRTY_MINUTES=10 * 1000;// 1800000;
	private long time=THIRTY_MINUTES;

	final java.text.SimpleDateFormat sdf=new java.text.SimpleDateFormat("mm : ss");

	@Override
	public void createPartControl(Composite parent) {
		Composite container=new Composite(parent, SWT.NULL);
		GridLayout layout=new GridLayout();
		container.setLayout(layout);
		layout.numColumns=2;
		layout.verticalSpacing=9;
		Label label=new Label(container, SWT.NULL);
		label.setText("Timer:");
		final Label countdown=new Label(container, SWT.NULL);
		countdown.setText(sdf.format(time));
		Button startButton=new Button(container, SWT.NULL);
		final PomodoroTimer timer=new PomodoroTimer(THIRTY_MINUTES);
		scheduleTimer(countdown, 1000, timer);
		startButton.setText("Start");
		startButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				timer.start();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub

			}
		});
		Button pauseButton=new Button(container, SWT.NULL);
		pauseButton.setText("Pause");
		pauseButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				timer.pause();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub

			}
		});
		sendTimerMessage(timer);

	}

	private void scheduleTimer(final Label label, final int changeInterval, final PomodoroTimer timer) {
		Display.getDefault().timerExec(changeInterval, new Runnable() {
			public void run() {
				label.setText(timer.getFormatTime());
				scheduleTimer(label, changeInterval, timer);
			}
		});
	}

	private void sendTimerMessage(final PomodoroTimer timer) {
		final Timer scheduler=new Timer();
		TimerTask task=new TimerTask() {

			@Override
			public void run() {
				if (Activator.getDefault().getCommunication() != null && Activator.getDefault().getCommunication().isConnected()) {
					TimerMessage message=new TimerMessage();
					message.setCreated(new Date());
					message.setTimer(timer.getFormatTime());
					message.setStatus(timer.getStatus());
					try {
						Activator.getDefault().getCommunication().sendMessage(message);
					} catch (Exception e) {
						e.printStackTrace();
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
