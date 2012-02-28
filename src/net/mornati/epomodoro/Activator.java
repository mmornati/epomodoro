package net.mornati.epomodoro;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import net.mornati.epomodoro.communication.Communication;
import net.mornati.epomodoro.communication.TimerMessage;
import net.mornati.epomodoro.preference.PomodoroPreferencePage;
import net.mornati.epomodoro.util.ConflictRule;
import net.mornati.epomodoro.util.PomodoroTimer;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
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
	private Timer scheduler=new Timer();;

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
				boolean discardOwnMessage=preferenceStore.getBoolean(PomodoroPreferencePage.DISCARD_OWN_MESSAGE);
				communication=Communication.getInstance();
				try {
					communication.connect(groupName, discardOwnMessage);
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
					TimerMessage message=new TimerMessage();
					message.setCreated(new Date());
					message.setTimer(timer.getFormatTime());
					message.setStatus(timer.getStatus());
					String senderMachine="";
					try {
						senderMachine=InetAddress.getLocalHost().getHostName();
					} catch (UnknownHostException e) {
						e.printStackTrace();
					}
					message.setSenderMachine(senderMachine);
					IPreferenceStore preferenceStore=Activator.getDefault().getPreferenceStore();
					String sender=preferenceStore.getString(PomodoroPreferencePage.CLIENT_NAME);
					if (sender != null && !sender.equals("")) {
						message.setSender(sender);
					} else {
						message.setSender(senderMachine);
					}
					try {
						communication.sendMessage(message);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			}
		};

		scheduler.schedule(task, 1000, 1000);
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

}
