package net.mornati.epomodoro.util;

import java.util.List;

import net.mornati.epomodoro.Activator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public class UIUtil {

	public static Button createStartButton(Composite parent) {
		final Button startButton=new Button(parent, SWT.FLAT | SWT.TOGGLE);
		startButton.setText("Start");
		startButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				Activator.getDefault().getTimer().start();
				List<Button> startButtons=Activator.getDefault().getStartButtons();
				for (Button startButton : startButtons) {
					if (!startButton.isDisposed()) {
						startButton.setEnabled(false);
						startButton.setSelection(false);
					}
				}

				List<Button> pauseButtons=Activator.getDefault().getPauseButtons();
				for (Button pauseButton : pauseButtons) {
					if (!pauseButton.isDisposed()) {
						pauseButton.setEnabled(true);
					}
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});
		return startButton;
	}

	public static Button createPauseButton(Composite parent) {
		final Button pauseButton=new Button(parent, SWT.NULL);
		pauseButton.setText("Pause");
		pauseButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				Activator.getDefault().getTimer().pause();
				String buttonText=Activator.getDefault().getTimer().getStatus().equals(PomodoroTimer.STATUS_PAUSED) ? "Restart" : "Pause";
				List<Button> pauseButtons=Activator.getDefault().getPauseButtons();
				for (Button pauseButton : pauseButtons) {
					if (!pauseButton.isDisposed()) {
						pauseButton.setText(buttonText);
					}
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});
		pauseButton.setEnabled(false);
		return pauseButton;
	}

	public static Button createResetButton(Composite parent) {
		final Button resetButton=new Button(parent, SWT.NULL);
		resetButton.setText("Reset");
		resetButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				Activator.getDefault().resetTimer(Activator.getDefault().getTimer().getConfigWorkTime(), PomodoroTimer.TYPE_WORK);
				List<Button> startButtons=Activator.getDefault().getStartButtons();
				for (Button startButton : startButtons) {
					if (!startButton.isDisposed()) {
						startButton.setEnabled(true);
					}
				}
				List<Button> pauseButtons=Activator.getDefault().getPauseButtons();
				for (Button pauseButton : pauseButtons) {
					if (!pauseButton.isDisposed()) {
						pauseButton.setText("Pause");
						pauseButton.setEnabled(false);
					}
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});
		return resetButton;

	}

}
