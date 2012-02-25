package net.mornati.epomodoro.util;

import org.eclipse.core.runtime.jobs.ISchedulingRule;

public class ConflictRule implements ISchedulingRule {

	public boolean contains(ISchedulingRule rule) {
		return (rule == this);
	}

	public boolean isConflicting(ISchedulingRule rule) {
		return (rule == this);
	}

}
