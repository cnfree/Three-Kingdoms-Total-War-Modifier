
package com.actuate.tool.development.installer.wizard;

import java.io.File;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.actuate.tool.development.installer.model.InstallData;
import com.actuate.tool.development.installer.model.InstallType;

public class CloneWorkspaceSettingsPage extends WizardPage
{

	private InstallData data;
	private Text txtWorkspace;
	private Text txtSource;

	CloneWorkspaceSettingsPage( InstallData data )
	{
		super( "CloneWorspaceSettingsPage" );
		setTitle( "Clone Workspace Settings" );
		setDescription( "Clone settings from an old workspace to a new workspace." );
		this.data = data;
	}

	public void createControl( Composite parent )
	{
		Composite composite = new Composite( parent, SWT.NULL );
		GridLayout gridLayout = new GridLayout( 3, false );
		gridLayout.marginWidth = 10;
		gridLayout.marginHeight = 20;
		composite.setLayout( gridLayout );

		new Label( composite, SWT.NONE ).setText( "&Target Workspace: " );
		txtWorkspace = new Text( composite, SWT.BORDER );
		GridData gd = new GridData( GridData.FILL_HORIZONTAL );
		txtWorkspace.setLayoutData( gd );

		txtWorkspace.addModifyListener( new ModifyListener( ) {

			public void modifyText( ModifyEvent e )
			{
				checkStatus( );
				setPageComplete( isPageComplete( ) );
			}

		} );

		Button workspaceButton = new Button( composite, SWT.PUSH );
		workspaceButton.setText( "Br&owse..." );
		workspaceButton.addSelectionListener( new SelectionAdapter( ) {

			public void widgetSelected( SelectionEvent e )
			{
				DirectoryDialog dialog = new DirectoryDialog( getShell( ) );
				dialog.setMessage( "Select Directory" );
				String path = dialog.open( );
				if ( path != null )
				{
					txtWorkspace.setText( path );
				}
			}

		} );

		new Label( composite, SWT.NONE ).setText( "&Source Workspace: " );
		txtSource = new Text( composite, SWT.BORDER | SWT.READ_ONLY );
		gd = new GridData( GridData.FILL_HORIZONTAL );
		txtSource.setLayoutData( gd );

		txtSource.addModifyListener( new ModifyListener( ) {

			public void modifyText( ModifyEvent e )
			{
				checkStatus( );
				setPageComplete( isPageComplete( ) );
			}

		} );

		Button targetButton = new Button( composite, SWT.PUSH );
		targetButton.setText( "B&rowse..." );
		targetButton.addSelectionListener( new SelectionAdapter( ) {

			public void widgetSelected( SelectionEvent e )
			{
				DirectoryDialog dialog = new DirectoryDialog( getShell( ) );
				dialog.setMessage( "Select Directory" );
				String path = dialog.open( );
				if ( path != null )
				{
					txtSource.setText( path );
					checkStatus( );
				}
			}

		} );
		setControl( composite );

	}

	public boolean isPageComplete( )
	{
		if ( data != null )
		{
			if ( data.getInstallType( ) != InstallType.cloneWorkspaceSettings )
				return true;

			checkStatus( );
			if ( data != null && getErrorMessage( ) == null )
			{
				data.getCloneWorkspaceData( )
						.setTargetWorkspace( txtWorkspace.getText( ).trim( ) );
				data.getCloneWorkspaceData( )
						.setSourceWorkspace( txtSource.getText( ).trim( ) );
			}
			return getErrorMessage( ) == null;

		}
		return false;
	}

	private void checkStatus( )
	{
		if ( txtWorkspace.getText( ).trim( ).length( ) == 0 )
		{
			setErrorMessage( "The path of target workspace is empty." );
			return;
		}

		File file = new File( txtWorkspace.getText( ) );
		if ( file.getParentFile( ) == null )
		{
			setErrorMessage( "The path of target workspace is invalid." );
			return;
		}

		if ( !new File( txtSource.getText( ), ".metadata" ).exists( ) )
		{
			setErrorMessage( "The source workspace is not a valid eclipse workspace." );
			return;
		}

		if ( txtWorkspace.getText( ).equals( txtSource ) )
		{
			setErrorMessage( "The target workspace is the same as the source workspace." );
			return;
		}

		setErrorMessage( null );
		return;
	}
}
