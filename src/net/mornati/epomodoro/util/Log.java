package net.mornati.epomodoro.util;

import net.mornati.epomodoro.Activator;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

public class Log {
	public static final Log INSTANCE=new Log();

	private Log() {
	}

	public void logError(String message, Throwable t) {
		Activator.getDefault().getLog().log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, message, t));
	}

	public void logInfo(String message) {
		Activator.getDefault().getLog().log(new Status(IStatus.INFO, Activator.PLUGIN_ID, message));
	}

	public void logWarning(String message) {
		Activator.getDefault().getLog().log(new Status(IStatus.WARNING, Activator.PLUGIN_ID, message));
	}
}
