package net.mornati.epomodoro;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.mornati.epomodoro.communication.Communication;
import net.mornati.epomodoro.communication.TimerMessage;
import net.mornati.epomodoro.preference.PomodoroPreferencePage;
import net.mornati.epomodoro.util.ConflictRule;
import net.mornati.epomodoro.util.PomodoroTimer;
import net.mornati.epomodoro.util.UIUtil;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID="net.mornati.epomodoro"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;

	private Communication communication;
	private ISchedulingRule jobRule;
	private PomodoroTimer timer;
	private boolean showDialog=false;
	private Timer scheduler=new Timer();
	final java.text.SimpleDateFormat sdf=new java.text.SimpleDateFormat("mm : ss");
	private static final Logger LOG=Logger.getLogger(Activator.class.getName());
	private List<Button> startButtons=new ArrayList<Button>();
	private List<Label> counterLabels=new ArrayList<Label>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin=this;
		IPreferenceStore preferenceStore=Activator.getDefault().getPreferenceStore();
		int preferenceTime=preferenceStore.getInt(PomodoroPreferencePage.POMODORO_TIME);
		createTimer(preferenceTime * 60 * 1000, PomodoroTimer.TYPE_WORK);
		initCommunication();
		sendTimerMessage();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		plugin=null;
		super.stop(context);
		scheduler.cancel();
		scheduler=null;
		timer.interrupt();
		timer=null;
		communication.close();
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given plug-in relative path
	 * 
	 * @param path
	 *            the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	public void initCommunication() {
		Job job=new Job("ConnectToJGroups") {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				IPreferenceStore preferenceStore=getPreferenceStore();
				String groupName=preferenceStore.getString(PomodoroPreferencePage.GROUP_NAME);
				communication=Communication.getInstance();
				try {
					communication.connect(groupName);
				} catch (Exception e) {
					e.printStackTrace();
				}

				return Status.OK_STATUS;
			}

		};
		job.setUser(false);
		job.setRule(getRule());
		job.schedule();
	}

	private void sendTimerMessage() {
		TimerTask task=new TimerTask() {

			@Override
			public void run() {
				if (communication != null && communication.isConnected() && timer != null) {
					TimerMessage message=(TimerMessage) Communication.createMessage(TimerMessage.class);
					message.setTimer(timer.getFormatTime());
					message.setStatus(timer.getStatus());
					try {
						communication.sendMessage(message);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			}
		};

		scheduler.schedule(task, 1000, 5000);
	}

	public Communication getCommunication() {
		return this.communication;
	}

	public ISchedulingRule getRule() {
		if (jobRule == null) {
			jobRule=new ConflictRule();
		}
		return jobRule;
	}

	public PomodoroTimer getTimer() {
		return timer;
	}

	public PomodoroTimer createTimer(long totalTime, int type) {
		if (timer == null || timer.getTime() == 0) {
			timer=new PomodoroTimer(totalTime, type);
		}
		return timer;
	}

	public PomodoroTimer resetTimer(long totalTime, int type) {
		if (timer != null) {
			timer.interrupt();
			timer=new PomodoroTimer(totalTime, type);
		}
		return timer;
	}

	public void setShowDialog(boolean showDialog) {
		this.showDialog=showDialog;
	}

	public boolean isShowDialog() {
		return showDialog;
	}

	public Timer getScheduler() {
		return scheduler;
	}

	public void subscribeStartButton(Button button) {
		startButtons.add(button);
	}

	public void subscribeCounterLabel(Label label) {
		counterLabels.add(label);
	}

	public List<Button> getStartButtons() {
		return startButtons;
	}
	
	public void scheduleTimer(final int changeInterval) {
		final PomodoroTimer internalTimer;
		if (timer == null) {
			internalTimer=createTimer(timer.getConfigWorkTime(), PomodoroTimer.TYPE_WORK);
		} else {
			internalTimer=timer;
		}
		Display.getDefault().timerExec(changeInterval, new Runnable() {
			public void run() {
				if (internalTimer != null) {
					for (Label countdown : counterLabels) {
						if (!countdown.isDisposed()) {
							if (timer.getType() == PomodoroTimer.TYPE_WORK) {
								countdown.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_RED));
							} else {
								countdown.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_GREEN));
							}
							countdown.setToolTipText(timer.getType() == PomodoroTimer.TYPE_PAUSE ? "Pause Timer" : "Work Timer");
							countdown.setText(internalTimer.getFormatTime());
						}
					}
					if (Activator.getDefault().isShowDialog()) {
						String message=(timer.getType() == PomodoroTimer.TYPE_WORK ? "Work " : "Pause ") + "Time finished";
						MessageDialog.openInformation(Display.getDefault().getActiveShell(), "Pomodoro Timer Finished", message);
						setShowDialog(false);
					}

					scheduleTimer(changeInterval);
				} else {
					for (Label countdown : counterLabels) {
						if (!countdown.isDisposed()) {
							countdown.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_RED));
							countdown.setText(sdf.format(new Date(timer.getConfigWorkTime())));
						}
					}
					scheduleTimer(changeInterval);
				}

			}
		});
	}

	public void checkTimerStatus() {
		TimerTask task=new TimerTask() {
			@Override
			public void run() {
				PomodoroTimer newTimer;
				if (timer == null || timer.isInterrupted()) {
					return;
				}
				if (timer.getStatus().equals(PomodoroTimer.STATUS_FINISHED)) {
					setShowDialog(true);
					while (isShowDialog()) {
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							LOG.log(Level.SEVERE, "Error sleeping Thread", e);
						}
					}
					UIUtil.showReceivedMessages();
					IPreferenceStore preferenceStore=getPreferenceStore();
					if (timer != null && timer.getType() == PomodoroTimer.TYPE_WORK) {
						int pauseTimer=preferenceStore.getInt(PomodoroPreferencePage.POMODORO_PAUSE) * 60 * 1000;
						newTimer=createTimer(pauseTimer, PomodoroTimer.TYPE_PAUSE);
						if (preferenceStore.getBoolean(PomodoroPreferencePage.WORK_PAUSE_AUTO_SWITCH)) {
							newTimer.start();
						}
					} else {
						newTimer=createTimer(timer.getConfigWorkTime(), PomodoroTimer.TYPE_WORK);
					}
				}

			}

		};
		scheduler.schedule(task, 1000, 1000);
	}
}
