package net.mornati.epomodoro.util;

import java.util.List;

import net.mornati.epomodoro.Activator;
import net.mornati.epomodoro.communication.TextMessage;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

public class UIUtil {

	public static Button createStartButton(Composite parent) {
		final Button startButton=new Button(parent, SWT.FLAT | SWT.TOGGLE);
		startButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				Activator.getDefault().getTimer().start();
				for (Button pause : Activator.getDefault().getPauseButtons()) {
					if (!pause.isDisposed()) {
						pause.redraw();
					}
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});
		startButton.addPaintListener(new PaintListener() {

			@Override
			public void paintControl(PaintEvent e) {
				Button button=(Button) e.getSource();
				if (!button.isDisposed()) {
					boolean enabledStatus=Activator.getDefault().getTimer() != null
							&& Activator.getDefault().getTimer().getStatus().equals(PomodoroTimer.STATUS_INITIALIZED);
					if (button.getEnabled() && !enabledStatus) {
						button.setEnabled(false);
					} else if (!button.getEnabled() && enabledStatus) {
						button.setEnabled(true);
					}
				}

			}
		});
		startButton.setToolTipText("Start");
		startButton.setImage(Activator.getImageDescriptor(PluginImages.ICONS_PLAY).createImage());
		return startButton;
	}

	public static Button createPauseButton(Composite parent) {
		final Button pauseButton=new Button(parent, SWT.NULL);
		pauseButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				Activator.getDefault().getTimer().pause();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});
		pauseButton.setEnabled(false);
		pauseButton.setImage(Activator.getImageDescriptor(PluginImages.ICONS_PAUSE).createImage());
		pauseButton.setToolTipText("Pause");
		pauseButton.addPaintListener(new PaintListener() {

			@Override
			public void paintControl(PaintEvent e) {
				Button button=(Button) e.getSource();
				if (!button.isDisposed()) {
					boolean enabledStatus=Activator.getDefault().getTimer() != null
							&& !Activator.getDefault().getTimer().getStatus().equals(PomodoroTimer.STATUS_INITIALIZED);
					if (button.getEnabled() && !enabledStatus) {
						button.setEnabled(false);
					} else if (!button.getEnabled() && enabledStatus) {
						button.setEnabled(true);
					}
					if (button.getToolTipText().equals("Pause") && Activator.getDefault().getTimer().getStatus().equals(PomodoroTimer.STATUS_PAUSED)) {
						button.setImage(Activator.getImageDescriptor(PluginImages.ICONS_PLAY).createImage());
						button.setToolTipText("Play");
					} else if (button.getToolTipText().equals("Play") && !Activator.getDefault().getTimer().getStatus().equals(PomodoroTimer.STATUS_PAUSED)) {
						button.setImage(Activator.getImageDescriptor(PluginImages.ICONS_PAUSE).createImage());
						pauseButton.setToolTipText("Pause");
					}
				}
			}
		});
		return pauseButton;
	}

	public static Button createResetButton(Composite parent) {
		final Button resetButton=new Button(parent, SWT.NULL);
		resetButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				Activator.getDefault().resetTimer(Activator.getDefault().getTimer().getConfigWorkTime(), PomodoroTimer.TYPE_WORK);
				List<Button> startButtons=Activator.getDefault().getStartButtons();
				for (Button startButton : startButtons) {
					if (!startButton.isDisposed()) {
						startButton.setEnabled(true);
						startButton.setImage(Activator.getImageDescriptor(PluginImages.ICONS_PLAY).createImage());
					}
				}
				List<Button> pauseButtons=Activator.getDefault().getPauseButtons();
				for (Button pauseButton : pauseButtons) {
					if (!pauseButton.isDisposed()) {
						pauseButton.setEnabled(false);
						pauseButton.setImage(Activator.getImageDescriptor(PluginImages.ICONS_PAUSE_DISABLED).createImage());
					}
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});
		resetButton.setImage(Activator.getImageDescriptor(PluginImages.ICONS_RESET).createImage());
		resetButton.setToolTipText("Reset");
		return resetButton;

	}

	public static void showReceivedMessages() {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				if (Activator.getDefault().getCommunication().getReceivedMessages().size() > 0) {
					String messages="";
					for (TextMessage textMessage : Activator.getDefault().getCommunication().getReceivedMessages()) {
						messages+=textMessage.getSender() + " - " + textMessage.getMessage() + "\n";
					}
					MessageDialog.openInformation(Display.getDefault().getActiveShell(), "Messages Received", messages);
					Activator.getDefault().getCommunication().resetReceivedMessages();
				}
			}
		});
	}

}
