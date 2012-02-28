package net.mornati.epomodoro.views;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.mornati.epomodoro.Activator;
import net.mornati.epomodoro.communication.AbstractPomodoroMessage;
import net.mornati.epomodoro.communication.Communication;
import net.mornati.epomodoro.communication.TextMessage;
import net.mornati.epomodoro.communication.TimerMessage;
import net.mornati.epomodoro.preference.PomodoroPreferencePage;
import net.mornati.epomodoro.util.PluginImages;
import net.mornati.epomodoro.util.PomodoroComparator;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.part.ViewPart;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;

public class TeamStatus extends ViewPart implements PropertyChangeListener {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID="epomodoro.views.SampleView";

	private static final Logger LOG=Logger.getLogger(TeamStatus.class.getName());

	private TableViewer viewer;
	private Action clearTable;
	private Action connect;
	private PomodoroComparator comparator;
	private List<TimerMessage> receivedMessages=new ArrayList<TimerMessage>();

	// This will create the columns for the table
	private void createColumns(final Composite parent, final TableViewer viewer) {
		String[] titles= { "Machine", "Sender Name", "Pomodoro Status", "Pomodoro Timer" };
		int[] bounds= { 150, 100, 120, 100 };

		TableViewerColumn col=createTableViewerColumn(titles[0], bounds[0], 0);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				TimerMessage m=(TimerMessage) element;
				return m.getSenderMachine();
			}
		});

		col=createTableViewerColumn(titles[1], bounds[1], 1);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				TimerMessage m=(TimerMessage) element;
				return m.getSender();
			}
		});

		col=createTableViewerColumn(titles[2], bounds[2], 2);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				TimerMessage m=(TimerMessage) element;
				return m.getStatus();
			}
		});

		col=createTableViewerColumn(titles[3], bounds[3], 3);
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
		column.addSelectionListener(getSelectionAdapter(column, colNumber));
		return viewerColumn;
	}

	private SelectionAdapter getSelectionAdapter(final TableColumn column, final int index) {
		SelectionAdapter selectionAdapter=new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				comparator.setColumn(index);
				int dir=comparator.getDirection();
				viewer.getTable().setSortDirection(dir);
				viewer.getTable().setSortColumn(column);
				viewer.refresh();
			}
		};
		return selectionAdapter;
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
		Composite composite=new Composite(parent, SWT.NONE);
		GridLayout gridLayout=new GridLayout();
		gridLayout.marginWidth=gridLayout.marginHeight=gridLayout.verticalSpacing=gridLayout.horizontalSpacing=gridLayout.marginLeft=gridLayout.marginTop=gridLayout.marginRight=gridLayout.marginBottom=0;
		composite.setLayout(gridLayout);
		viewer=new TableViewer(composite, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		createColumns(composite, viewer);
		viewer.setContentProvider(new ArrayContentProvider());
		viewer.setSorter(new NameSorter());
		viewer.setInput(receivedMessages);
		comparator=new PomodoroComparator();
		viewer.setComparator(comparator);
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
						LOG.log(Level.SEVERE, "Error sleeping thread", e);
					}
				}
				Activator.getDefault().getCommunication().setReceiver(new ReceiverAdapter() {
					public void receive(final Message msg) {
						if (msg != null && (msg.getObject() instanceof AbstractPomodoroMessage)) {
							if (msg.getObject() instanceof TimerMessage) {
								TimerMessage tm=(TimerMessage) msg.getObject();
								if (receivedMessages.contains(tm)) {
									receivedMessages.remove(tm);
								}
								receivedMessages.add(tm);
							} else if (msg.getObject() instanceof TextMessage) {
								final TextMessage textMessage=(TextMessage) msg.getObject();
								Display.getDefault().asyncExec(new Runnable() {
									public void run() {
										MessageDialog.openInformation(Display.getDefault().getActiveShell(), "Text Received", textMessage.getMessage());
									}
								});
							}
						} else {
							LOG.log(Level.WARNING, "Received a wrong message");
						}
						Display.getDefault().asyncExec(new Runnable() {
							public void run() {
								viewer.refresh();
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
		Menu menu=menuMgr.createContextMenu(viewer.getTable());
		viewer.getTable().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
		getSite().setSelectionProvider(viewer);
	}

	private void contributeToActionBars() {
		IActionBars bars=getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(clearTable);
		manager.add(connect);
	}

	private void fillContextMenu(IMenuManager manager) {
	}

	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(clearTable);
		manager.add(connect);
	}

	private void makeActions() {
		clearTable=new Action() {
			public void run() {
				viewer.setInput(null);
			}
		};
		clearTable.setText("Clear Table");
		clearTable.setToolTipText("Clear Table Content");
		clearTable.setImageDescriptor(Activator.getImageDescriptor(PluginImages.ICONS_CLEAR));

		connect=new Action() {
			public void run() {
				Communication communication=Activator.getDefault().getCommunication();
				if (communication != null) {
					if (communication.isConnected()) {
						communication.close();
					} else {
						IPreferenceStore preferenceStore=Activator.getDefault().getPreferenceStore();
						String groupName=preferenceStore.getString(PomodoroPreferencePage.GROUP_NAME);
						boolean discardOwnMessage=preferenceStore.getBoolean(PomodoroPreferencePage.DISCARD_OWN_MESSAGE);
						try {
							communication.connect(groupName, discardOwnMessage);
						} catch (Exception e) {
							LOG.log(Level.SEVERE, "Error connecting to group", e);
						}
					}
					Display.getDefault().timerExec(4000, new Runnable() {
						public void run() {
							if (Activator.getDefault().getCommunication().isConnected()) {
								connect.setImageDescriptor(Activator.getImageDescriptor(PluginImages.ICONS_CONNECTED));
								connect.setToolTipText("Disconnect from Team");
							} else {
								connect.setImageDescriptor(Activator.getImageDescriptor(PluginImages.ICONS_DICONNECTED));
								connect.setToolTipText("Connect to Team");
							}
						}
					});
				}
			}
		};
		connect.setText("Team Connection");
		connect.setToolTipText("Disconnect from Team");
		connect.setImageDescriptor(Activator.getImageDescriptor(PluginImages.ICONS_DICONNECTED));

	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		viewer.refresh();
	}
}