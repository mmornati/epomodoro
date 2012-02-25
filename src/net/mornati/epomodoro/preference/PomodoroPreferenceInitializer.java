package net.mornati.epomodoro.preference;

import net.mornati.epomodoro.Activator;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

public class PomodoroPreferenceInitializer extends AbstractPreferenceInitializer {

	public PomodoroPreferenceInitializer() {
	}

	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		store.setDefault("GROUP_NAME", "MyTeam");
		store.setDefault("CLIENT_NAME", "");
		store.setDefault("POMODORO_TIME", "25");
		store.setDefault("POMODORO_PAUSE", "5");
	}

}
