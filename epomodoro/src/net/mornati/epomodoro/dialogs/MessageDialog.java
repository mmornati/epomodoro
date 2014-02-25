package net.mornati.epomodoro.dialogs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class MessageDialog extends Dialog {

	private Text scrollable;
	private String inputText;

	public MessageDialog(Shell parentShell, String title) {
		this(parentShell, title, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
	}

	public MessageDialog(Shell parent, String title, int style) {
		super(parent, style);
		setText(title);
	}

	protected Control createContents(Shell parent) {
		GridLayout layout=new GridLayout();
		layout.numColumns=2;
		parent.setLayout(layout);

		GridData gridData=new GridData();
		gridData.grabExcessHorizontalSpace=true;
		gridData.horizontalAlignment=GridData.FILL;
		gridData.horizontalSpan=2;
		gridData.widthHint=100;
		gridData.heightHint=50;

		scrollable=new Text(parent, SWT.BORDER | SWT.V_SCROLL);
		scrollable.setLayoutData(gridData);
		createButtonsForButtonBar(parent);
		return parent;
	}

	public String open() {
		// Create the dialog window
		Shell shell=new Shell(getParent(), getStyle());
		shell.setText(getText());
		createContents(shell);
		shell.pack();
		shell.open();
		Display display=getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		// Return the entered value, or null
		return inputText;
	}

	protected void createButtonsForButtonBar(final Shell parent) {
		Button ok=new Button(parent, SWT.PUSH);
		ok.setText("OK");
		GridData data=new GridData(GridData.FILL_HORIZONTAL);
		ok.setLayoutData(data);
		ok.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				inputText=scrollable.getText();
				parent.close();
			}
		});

		// Create the cancel button and add a handler
		// so that pressing it will set input to null
		Button cancel=new Button(parent, SWT.PUSH);
		cancel.setText("Cancel");
		data=new GridData(GridData.FILL_HORIZONTAL);
		cancel.setLayoutData(data);
		cancel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				inputText=null;
				parent.close();
			}
		});
	}

}
