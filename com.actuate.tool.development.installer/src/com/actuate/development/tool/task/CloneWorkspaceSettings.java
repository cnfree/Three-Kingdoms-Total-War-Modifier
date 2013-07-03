
package com.actuate.development.tool.task;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.sf.feeling.swt.win32.extension.shell.Windows;

import com.actuate.development.tool.model.feature.CloneWorkspaceData;
import com.actuate.development.tool.util.UIUtil;
import com.actuate.development.tool.util.WorkspaceUtil;

public class CloneWorkspaceSettings implements ITaskWithMonitor
{

	private CloneWorkspaceData data;

	public CloneWorkspaceSettings( CloneWorkspaceData data )
	{
		this.data = data;
	}

	public void execute( final IProgressMonitor monitor )
	{
		if ( data == null )
			return;

		monitor.beginTask( "Cloning the workspace settings",
				IProgressMonitor.UNKNOWN );

		final boolean result = WorkspaceUtil.cloneWorkspaceSettings( monitor,
				data.getSourceWorkspace( ) + "\\.metadata",
				data.getTargetWorkspace( ) + "\\.metadata" );

		monitor.subTask( "" );
		monitor.setTaskName( "Finished cloning the workspace settings" );

		Display.getDefault( ).syncExec( new Runnable( ) {

			public void run( )
			{
				if ( UIUtil.getShell( ).getMinimized( ) )
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
