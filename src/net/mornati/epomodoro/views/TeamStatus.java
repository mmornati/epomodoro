package net.mornati.epomodoro.views;

import java.util.Date;

import net.mornati.epomodoro.Activator;
import net.mornati.epomodoro.communication.AbstractPomodoroMessage;
import net.mornati.epomodoro.communication.Communication;
import net.mornati.epomodoro.communication.TimerMessage;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;

public class TeamStatus extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID="epomodoro.views.SampleView";

	private TableViewer viewer;
	private Action sendMessage;
	private Action clearTable;

	// This will create the columns for the table
	private void createColumns(final Composite parent, final TableViewer viewer) {
		String[] titles= { "Sender", "Pomodoro Status", "Pomodoro Timer" };
		int[] bounds= { 100, 100, 100 };

		TableViewerColumn col=createTableViewerColumn(titles[0], bounds[0], 0);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				TimerMessage m=(TimerMessage) element;
				return m.getSender();
			}
		});

		col=createTableViewerColumn(titles[1], bounds[1], 1);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				TimerMessage m=(TimerMessage) element;
				return m.getStatus();
			}
		});

		col=createTableViewerColumn(titles[2], bounds[2], 2);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				TimerMessage m=(TimerMessage) element;
				return m.getTimer();
			}
		});

	}

	private TableViewerColumn createTableViewerColumn(String title, int bound, final int colNumber) {
		final TableViewerColumn viewerColumn=new TableViewerColumn(viewer, SWT.NONE);
		final TableColumn column=viewerColumn.getColumn();
		column.setText(title);
		column.setWidth(bound);
		column.setResizable(true);
		column.setMoveable(true);
		return viewerColumn;
	}

	class NameSorter extends ViewerSorter {
	}

	/**
	 * The constructor.
	 */
	public TeamStatus() {
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize it.
	 */
	public void createPartControl(Composite parent) {
		viewer=new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		createColumns(parent, viewer);
		viewer.setContentProvider(new ArrayContentProvider());
		viewer.setSorter(new NameSorter());
		viewer.setInput(getViewSite());
		// Make lines and make header visible
		final Table table=viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		Job job=new Job("AddReceiver") {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				while (Job.getJobManager().currentJob() != null && Job.getJobManager().currentJob().getName().equals("ConnectToJGroups")) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				Activator.getDefault().getCommunication().setReceiver(new ReceiverAdapter() {
					public void receive(final Message msg) {
						Display.getDefault().asyncExec(new Runnable() {
							public void run() {
								if (msg != null && (msg.getObject() instanceof AbstractPomodoroMessage)) {
									if (msg.getObject() instanceof TimerMessage) {
										TimerMessage tm=(TimerMessage) msg.getObject();
										// if (tm.getSender() == null || tm.getSender().equals("")) {
										// tm.setSender(msg.getSrc().toString());
										// }
										viewer.remove(tm);
										viewer.add(tm);
									}
								} else {
									System.out.println("Received a wrong message");
								}
							}
						});
					}
				});
				return Status.OK_STATUS;
			}

		};
		job.setUser(false);
		job.setRule(Activator.getDefault().getRule());
		job.schedule();

		// Layout the viewer
		GridData gridData=new GridData();
		gridData.verticalAlignment=GridData.FILL;
		gridData.horizontalSpan=2;
		gridData.grabExcessHorizontalSpace=true;
		gridData.grabExcessVerticalSpace=true;
		gridData.horizontalAlignment=GridData.FILL;
		viewer.getControl().setLayoutData(gridData);

		// Create the help context id for the viewer's control
		PlatformUI.getWorkbench().getHelpSystem().setHelp(viewer.getControl(), "epomodoro.viewer");
		makeActions();
		hookContextMenu();
		contributeToActionBars();
	}

	private void hookContextMenu() {
		MenuManager menuMgr=new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				TeamStatus.this.fillContextMenu(manager);
			}
		});
		Menu menu=menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}

	private void contributeToActionBars() {
		IActionBars bars=getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(sendMessage);
		manager.add(clearTable);
	}

	private void fillContextMenu(IMenuManager manager) {
		manager.add(sendMessage);
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void fillLocalToolBar(IToolBarManager manager) {
//		manager.add(sendMessage);
		manager.add(clearTable);
	}

	private void makeActions() {
		sendMessage=new Action() {
			public void run() {
				try {
					TimerMessage message=new TimerMessage();
					message.setCreated(new Date());
					message.setStatus("RUNNING");
					message.setTimer(new Date().toString());
					Communication.getInstance().sendMessage(message);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		sendMessage.setText("Send Message");
		sendMessage.setToolTipText("Send Message to user");
		sendMessage.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_TOOL_FORWARD));

		clearTable=new Action() {
			public void run() {
				viewer.setInput(null);
			}
		};
		clearTable.setText("Clear Table");
		clearTable.setToolTipText("Clear Table Content");
		clearTable.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_ELCL_REMOVE));

	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}
}