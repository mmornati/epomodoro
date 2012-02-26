package net.mornati.epomodoro.preference;

import net.mornati.epomodoro.Activator;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class PomodoroPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public static final String GROUP_NAME="GROUP_NAME";
	public static final String CLIENT_NAME="CLIENT_NAME";
	public static final String POMODORO_TIME="POMODORO_TIME";
	public static final String POMODORO_PAUSE="POMODORO_PAUSE";
	public static final String WORK_PAUSE_AUTO_SWITCH="WORK_PAUSE_AUTO_SWITCH";

	public PomodoroPreferencePage() {
		super(GRID);

	}

	public void createFieldEditors() {
		addField(new StringFieldEditor(GROUP_NAME, "Team Name:", getFieldEditorParent()));
		addField(new StringFieldEditor(CLIENT_NAME, "Your Name:", getFieldEditorParent()));
		addField(new IntegerFieldEditor(POMODORO_TIME, "Pomodoro Time (minutes):", getFieldEditorParent()));
		addField(new IntegerFieldEditor(POMODORO_PAUSE, "Pomodoro Pause (minutes):", getFieldEditorParent()));
		addField(new BooleanFieldEditor(WORK_PAUSE_AUTO_SWITCH, "Auto start pause:", getFieldEditorParent()));

	}

	@Override
	public void init(IWorkbench workbench) {
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("ePomodoro Plugin Preference Page");
	}
}
