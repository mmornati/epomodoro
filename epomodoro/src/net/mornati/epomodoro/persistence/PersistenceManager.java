package net.mornati.epomodoro.persistence;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

public class PersistenceManager {

	public void persistPomodoro() {
		IWorkspace workspace=ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root=workspace.getRoot();

		IPath path=new Path(root.getFullPath() + "/.epomodoro");
		IFolder pomdoroFolder=root.getFolder(path);
	}

}
