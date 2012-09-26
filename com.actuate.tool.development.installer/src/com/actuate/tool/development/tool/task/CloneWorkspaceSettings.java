
package com.actuate.tool.development.tool.task;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.sf.feeling.swt.win32.extension.shell.Windows;

import com.actuate.tool.development.tool.model.InstallData;
import com.actuate.tool.development.tool.util.UIUtil;
import com.actuate.tool.development.tool.util.WorkspaceUtil;

public class CloneWorkspaceSettings
{

	private InstallData data;

	public CloneWorkspaceSettings( InstallData data )
	{
		this.data = data;
	}

	public void execute( final IProgressMonitor monitor )
	{
		if ( data == null )
			return;

		monitor.beginTask( "Cloning the workspace settings",
				IProgressMonitor.UNKNOWN );

		final boolean result = WorkspaceUtil.cloneWorkspaceSettings( data.getCloneWorkspaceData( )
				.getSourceWorkspace( ),
				data.getCloneWorkspaceData( ).getTargetWorkspace( ) );
		Display.getDefault( ).syncExec( new Runnable( ) {

			public void run( )
			{
				Windows.flashWindow( UIUtil.getShell( ).handle, true );
				if ( result )
				{
					MessageDialog.openInformation( null,
							"Information",
							"Clone the workspace settings sucessfully." );
				}
				else
				{
					MessageDialog.openError( null,
							"Error",
							"Clone the workspace settings failed." );
				}
				Windows.flashWindow( UIUtil.getShell( ).handle, false );
			}
		} );
	}
}
