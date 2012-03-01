package net.mornati.epomodoro;

import net.mornati.epomodoro.preference.PomodoroPreferencePage;
import net.mornati.epomodoro.util.PluginImages;
import net.mornati.epomodoro.util.UIUtil;

import org.eclipse.jface.action.ControlContribution;
import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.WorkbenchWindow;

public class Startup implements IStartup {

	@Override
	public void earlyStartup() {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				ControlContribution statusItem=new ControlContribution("pomodoro") {

					@Override
					protected Control createControl(Composite parent) {
						Composite composite=new Composite(parent, SWT.NONE);
						GridLayout gridLayout=new GridLayout(5, false);
						gridLayout.marginWidth=gridLayout.marginHeight=gridLayout.verticalSpacing=gridLayout.horizontalSpacing=gridLayout.marginLeft=gridLayout.marginTop=gridLayout.marginRight=gridLayout.marginBottom=0;
						composite.setLayout(gridLayout);
						Image image=Activator.getImageDescriptor(PluginImages.ICONS_POMODORO).createImage();
						Label imgLabel=new Label(composite, SWT.NONE);
						imgLabel.setImage(image);
						Label countdownStatus=new Label(composite, SWT.NONE);
						countdownStatus.setText(Activator.getDefault().getTimer().getFormatTime());
						countdownStatus.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_RED));
						Activator.getDefault().subscribeCounterLabel(countdownStatus);
						GridData layoutData=new GridData(SWT.FILL, SWT.CENTER, false, true);
						layoutData.widthHint=50;
						layoutData.horizontalIndent=10;
						countdownStatus.setLayoutData(layoutData);
						Activator.getDefault().subscribeStartButton(UIUtil.createStartButton(composite));
						UIUtil.createResetButton(composite);
						return composite;
					}

				};
				IPreferenceStore preferenceStore=Activator.getDefault().getPreferenceStore();
				boolean showInStatusBar=preferenceStore.getBoolean(PomodoroPreferencePage.SHOW_TIMER_STATUS_BAR);
				if (showInStatusBar) {
					StatusLineManager statusLineManager=((WorkbenchWindow) PlatformUI.getWorkbench().getActiveWorkbenchWindow()).getStatusLineManager();
					statusLineManager.add(statusItem);
					statusLineManager.update(true);
				}
				Activator.getDefault().scheduleTimer(1000);
				Activator.getDefault().checkTimerStatus();
			}
		});
	}
}
