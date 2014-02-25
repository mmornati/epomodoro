package net.mornati.epomodoro.util;

import net.mornati.epomodoro.communication.TimerMessage;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;

public class PomodoroComparator extends ViewerComparator {
	private int propertyIndex;
	private static final int DESCENDING=1;
	private int direction=DESCENDING;

	public PomodoroComparator() {
		this.propertyIndex=0;
		direction=DESCENDING;
	}

	public int getDirection() {
		return direction == 1 ? SWT.DOWN : SWT.UP;
	}

	public void setColumn(int column) {
		if (column == this.propertyIndex) {
			// Same column as last sort; toggle the direction
			direction=1 - direction;
		} else {
			// New column; do an ascending sort
			this.propertyIndex=column;
			direction=DESCENDING;
		}
	}

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		TimerMessage t1=(TimerMessage) e1;
		TimerMessage t2=(TimerMessage) e2;
		int rc=0;
		switch (propertyIndex) {
			case 0:
				rc=t1.getSenderMachine().compareTo(t2.getSenderMachine());
				break;
			case 1:
				rc=t1.getSender().compareTo(t2.getSender());
				break;
			case 2:
				rc=t1.getStatus().compareTo(t2.getStatus());
				break;
			case 3:
				rc=t1.getTimer().compareTo(t2.getTimer());
				break;
			default:
				rc=0;
		}
		if (direction == DESCENDING) {
			rc=-rc;
		}
		return rc;
	}

}
