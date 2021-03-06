package net.mornati.epomodoro.preference;

import net.mornati.epomodoro.Activator;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

public class PomodoroPreferenceInitializer extends AbstractPreferenceInitializer {

	public PomodoroPreferenceInitializer() {
	}

	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store=Activator.getDefault().getPreferenceStore();
		store.setDefault(PomodoroPreferencePage.GROUP_NAME, "MyTeam");
		store.setDefault(PomodoroPreferencePage.CLIENT_NAME, "");
		store.setDefault(PomodoroPreferencePage.POMODORO_TIME, "25");
		store.setDefault(PomodoroPreferencePage.POMODORO_PAUSE, "5");
		store.setDefault(PomodoroPreferencePage.WORK_PAUSE_AUTO_SWITCH, true);
		store.setDefault(PomodoroPreferencePage.DISCARD_OWN_MESSAGE, false);
		store.setDefault(PomodoroPreferencePage.SHOW_TIMER_STATUS_BAR, true);
		store.setDefault(PomodoroPreferencePage.FORCE_IPV4, true);
		store.setDefault(PomodoroPreferencePage.POMODORO_DESCRIPTION, false);
		store.setDefault(PomodoroPreferencePage.BIND_IP_ADDR, "");
		store.setDefault(PomodoroPreferencePage.POMODORO_TIME_COLOR, "255, 0, 0" ); //RED
		store.setDefault(PomodoroPreferencePage.POMODORO_PAUSE_COLOR, "0, 255, 0" ); //GREEN
	}

}
