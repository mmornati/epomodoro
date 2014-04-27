package net.mornati.epomodoro;

import net.mornati.epomodoro.communication.TimerMessage;
import net.mornati.epomodoro.preference.PomodoroPreferencePage;
import net.mornati.epomodoro.util.Log;
import net.mornati.epomodoro.util.PluginImages;
import net.mornati.epomodoro.util.UIUtil;
import net.mornati.epomodoro.views.TeamStatus;

import org.eclipse.jface.action.ControlContribution;
import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.jface.window.DefaultToolTip;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.PartInitException;
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
						Image image=PluginImages.getImage(PluginImages.ICONS_POMODORO);
						Label imgLabel=new Label(composite, SWT.NONE);
						imgLabel.setImage(image);
						Label countdownStatus=new Label(composite, SWT.NONE);
						countdownStatus.addListener(SWT.MouseDoubleClick, new Listener() {
							@Override
							public void handleEvent(Event event) {
								try {
									PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(TeamStatus.ID);
								} catch (PartInitException e) {
									Log.INSTANCE.logError("Failed to open view " + TeamStatus.ID, e);
								}
							}
						});
						//						class MyToolTip extends ToolTip {
						//							private StyledText styledText;
						//
						//							public MyToolTip(Control parent) {
						//								super(parent);
						//							}
						//
						//							@Override
						//							protected Composite createToolTipContentArea(Event event, Composite parent) {
						//								Composite compo=new Composite(parent, SWT.NONE);
						//								compo.setSize(200, 200);
						//								compo.setLayout(new FillLayout());
						//								styledText=new StyledText(compo, SWT.NONE);
						//								//								styledText.setText("gfjksfdjksdfsdfhsdfjkh\nfklsdjsdjklf");
						//								return compo;
						//							}
						//
						//
						//							public StyledText getStyledText() {
						//								return styledText;
						//							}
						//						}
						final DefaultToolTip tooltip=new DefaultToolTip(countdownStatus);
						Activator.getDefault().addCommunicationListener(new Runnable() {
							@Override
							public void run() {
								StringBuffer buffer=new StringBuffer();
								for (TimerMessage msg : Activator.getDefault().getReceivedMessages()) {
									buffer.append(msg.getSender()).append(" => ").append(msg.getTimer()).append('\n');
								}
								tooltip.setText(buffer.toString());
								//								tooltip.getStyledText().setText(buffer.toString());
							}
						});
						countdownStatus.setText(Activator.getDefault().getTimer().getFormatTime());
						IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
						countdownStatus.setForeground(new Color(Display.getDefault(), StringConverter.asRGB(preferenceStore.getString(PomodoroPreferencePage.POMODORO_TIME_COLOR))));
						Activator.getDefault().subscribeCounterLabel(countdownStatus);
						GridData layoutData=new GridData(SWT.FILL, SWT.CENTER, false, true);
						layoutData.widthHint=250;
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
