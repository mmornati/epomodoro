package net.mornati.epomodoro.views;

import java.util.logging.Logger;

import net.mornati.epomodoro.Activator;
import net.mornati.epomodoro.util.UIUtil;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.part.ViewPart;

public class CountDownTimer extends ViewPart {

	private static final Logger LOG=Logger.getLogger(CountDownTimer.class.getName());
	private long time;
	private Label countdown;

	final java.text.SimpleDateFormat sdf=new java.text.SimpleDateFormat("mm : ss");

	public CountDownTimer() {
		time=Activator.getDefault().getTimer().getConfigWorkTime();
	}

	@Override
	public void createPartControl(Composite parent) {
		Composite container=new Composite(parent, SWT.NULL);
		GridLayout layout=new GridLayout();
		container.setLayout(layout);
		layout.numColumns=3;
		layout.verticalSpacing=9;
		Label label=new Label(container, SWT.NULL);
		label.setText("Timer:");
		countdown=new Label(container, SWT.NULL);
		countdown.setText(sdf.format(time));
		Activator.getDefault().subscribeCounterLabel(countdown);
		final Label typeLabel=new Label(container, SWT.NULL);
		typeLabel.setText("");
		Activator.getDefault().subscribeStartButton(UIUtil.createStartButton(container));
		UIUtil.createResetButton(container);
	}

	@Override
	public void setFocus() {

	}

}
