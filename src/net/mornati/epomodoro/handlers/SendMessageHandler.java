package net.mornati.epomodoro.handlers;

import java.util.List;

import net.mornati.epomodoro.Activator;
import net.mornati.epomodoro.communication.Communication;
import net.mornati.epomodoro.communication.TextMessage;
import net.mornati.epomodoro.communication.TimerMessage;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.jgroups.Address;

public class SendMessageHandler extends AbstractHandler implements IHandler {

	private static final IInputValidator NON_EMPTY_VALIDATOR=new IInputValidator() {
		@Override
		public String isValid(String newText) {
			return newText.isEmpty() ? "You must enter a message text" : null;
		}
	};

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window=HandlerUtil.getActiveWorkbenchWindow(event);
		ISelection selection=window.getActivePage().getSelection();
		if (selection != null & selection instanceof IStructuredSelection) {
			IStructuredSelection strucSelection=(IStructuredSelection) selection;
			if (!strucSelection.isEmpty()) {
				String senders=null;
				for (TimerMessage timeMessage : (List<TimerMessage>) strucSelection.toList()) {
					if (senders == null)
						senders=timeMessage.getSender();
					else
						senders+=", " + timeMessage.getSender();
				}
				// See http://bingjava.appspot.com/model?id=752002
				InputDialog dialog=new InputDialog(window.getShell(), "Write message to " + senders, "Enter your message here:", "", NON_EMPTY_VALIDATOR) {
					/**
					 * Override this method to make the text field multilined and give it a scroll bar. But...
					 */
					@Override
					protected int getInputTextStyle() {
						return SWT.MULTI | SWT.BORDER | SWT.V_SCROLL;
					}

					/**
					 * ...it still is just one line high. This hack is not very nice, but at least it gets the job done... ;o)
					 */
					@Override
					protected Control createDialogArea(Composite parent) {
						Control res=super.createDialogArea(parent);
						((GridData) this.getText().getLayoutData()).heightHint=100;
						return res;
					}
				};
				if (dialog.open() == Window.OK) {
					for (TimerMessage message : (List<TimerMessage>) strucSelection.toList()) {
						Address source=message.getSourceAddress();
						try {
							TextMessage msg=(TextMessage) Communication.createMessage(TextMessage.class);
							msg.setMessage(dialog.getValue());
							Activator.getDefault().getCommunication().sendMessage(msg, source);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
		return null;
	}

}
