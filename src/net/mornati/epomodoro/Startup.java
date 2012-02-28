package net.mornati.epomodoro;

import net.mornati.epomodoro.util.PluginImages;

import org.eclipse.jface.action.ControlContribution;
import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
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
						GridLayout gridLayout=new GridLayout(3, false);
						gridLayout.marginWidth=gridLayout.marginHeight=gridLayout.verticalSpacing=gridLayout.horizontalSpacing=gridLayout.marginLeft=gridLayout.marginTop=gridLayout.marginRight=gridLayout.marginBottom=0;
						composite.setLayout(gridLayout);
						Image image=Activator.getImageDescriptor(PluginImages.ICONS_POMODORO).createImage();
						Label imgLabel=new Label(composite, SWT.NONE);
						imgLabel.setImage(image);
						Label countdownStatus=new Label(composite, SWT.NONE);
						countdownStatus.setText("Test");
						GridData layoutData=new GridData(SWT.FILL, SWT.CENTER, false, true);
						layoutData.widthHint=100;
						countdownStatus.setLayoutData(layoutData);
						Button button=new Button(composite, SWT.FLAT | SWT.TOGGLE);
						button.setText("Start");
						return composite;
					}
				};
				StatusLineManager statusLineManager=((WorkbenchWindow) PlatformUI.getWorkbench().getActiveWorkbenchWindow()).getStatusLineManager();
				statusLineManager.add(statusItem);
				statusLineManager.update(true);
			}
		});
	}
}
