package net.mornati.epomodoro;

import java.util.logging.Logger;

import net.mornati.epomodoro.communication.Communication;
import net.mornati.epomodoro.preference.PomodoroPreferencePage;
import net.mornati.epomodoro.util.ConflictRule;

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

	private static Logger LOG=Logger.getLogger("Activator");

	// The plug-in ID
	public static final String PLUGIN_ID="epomodoro"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;

	private Communication communication;
	private ISchedulingRule jobRule;

	/**
	 * The constructor
	 */
	public Activator() {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin=this;
		initCommunication();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin=null;
		super.stop(context);
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

	public Communication getCommunication() {
		return this.communication;
	}

	public ISchedulingRule getRule() {
		if (jobRule == null) {
			jobRule=new ConflictRule();
		}
		return jobRule;
	}

}
