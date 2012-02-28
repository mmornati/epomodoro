package net.mornati.epomodoro.handlers;

import java.util.Iterator;

import net.mornati.epomodoro.Activator;
import net.mornati.epomodoro.communication.Communication;
import net.mornati.epomodoro.communication.TextMessage;
import net.mornati.epomodoro.communication.TimerMessage;
import net.mornati.epomodoro.dialogs.MessageDialog;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.handlers.HandlerUtil;
import org.jgroups.Address;

public class SendMessageHandler extends AbstractHandler implements IHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection=HandlerUtil.getActiveWorkbenchWindow(event).getActivePage().getSelection();
		if (selection != null & selection instanceof IStructuredSelection) {
			IStructuredSelection strucSelection=(IStructuredSelection) selection;
			for (Iterator<Object> iterator=strucSelection.iterator(); iterator.hasNext();) {
				Object element=iterator.next();
				if (element instanceof TimerMessage) {
					TimerMessage message=(TimerMessage) element;
					Address source=message.getSourceAddress();
					try {
						MessageDialog dialog=new MessageDialog(Display.getDefault().getActiveShell(), "Write message for " + message.getSender());
						String textToSend=dialog.open();
						if (textToSend != null && !textToSend.equals("")) {
							TextMessage msg=(TextMessage) Communication.createMessage(TextMessage.class);
							msg.setMessage(textToSend);
							Activator.getDefault().getCommunication().sendMessage(msg, source);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		return null;
	}

}
