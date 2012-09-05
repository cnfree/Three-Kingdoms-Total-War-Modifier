
package com.actuate.tool.development.installer.wizard;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import com.actuate.tool.development.installer.model.InstallData;
import com.actuate.tool.development.installer.model.InstallType;

public class InstallTypePage extends WizardPage
{

	private InstallData data;
	private Button brdproButton;
	private Button workspaceCloneButton;
	private Button iportalSyncButton;

	InstallTypePage( InstallData data )
	{
		super( "InstallTypePage" );
		setTitle( "Select the Installation Feature" );
		setDescription( "Select the installation feature." );
		this.data = data;
	}

	public void createControl( Composite parent )
	{
		Composite composite = new Composite( parent, SWT.NULL );
		GridLayout gridLayout = new GridLayout( 1, false );
		gridLayout.marginWidth = 10;
		gridLayout.marginHeight = 20;
		composite.setLayout( gridLayout );

		brdproButton = new Button( composite, SWT.RADIO );
		brdproButton.setText( "&Install the BRDPro Development Environment" );
		brdproButton.setSelection( true );
		GridData gd = new GridData( );
		brdproButton.setLayoutData( gd );
		brdproButton.addSelectionListener( new SelectionAdapter( ) {

			public void widgetSelected( SelectionEvent e )
			{
				if ( brdproButton.getSelection( ) )
				{
					workspaceCloneButton.setSelection( false );
					iportalSyncButton.setSelection( false );
				}
				checkInstallType( );
				setPageComplete( isPageComplete( ) );
			}
		} );

		workspaceCloneButton = new Button( composite, SWT.RADIO );
		workspaceCloneButton.setText( "Clone &settings from an old workspace to a new workspace" );
		gd = new GridData( );
		workspaceCloneButton.setLayoutData( gd );
		workspaceCloneButton.addSelectionListener( new SelectionAdapter( ) {

			public void widgetSelected( SelectionEvent e )
			{
				if ( workspaceCloneButton.getSelection( ) )
				{
					brdproButton.setSelection( false );
					iportalSyncButton.setSelection( false );
				}
				setPageComplete( isPageComplete( ) );
			}
		} );

		iportalSyncButton = new Button( composite, SWT.RADIO );
		iportalSyncButton.setText( "Synchronize the iportal viewer &workspace (For IV team)" );
		gd = new GridData( );
		iportalSyncButton.setLayoutData( gd );
		iportalSyncButton.addSelectionListener( new SelectionAdapter( ) {

			public void widgetSelected( SelectionEvent e )
			{
				if ( iportalSyncButton.getSelection( ) )
				{
					brdproButton.setSelection( false );
					workspaceCloneButton.setSelection( false );
				}
				setPageComplete( isPageComplete( ) );
			}
		} );

		checkInstallType( );
		setControl( composite );
	}

	public boolean isPageComplete( )
	{
		if ( data != null )
		{
			checkInstallType( );
			return true;
		}
		return false;
	}

	private void checkInstallType( )
	{
		if ( data != null )
		{
			if ( brdproButton.getSelection( ) )
			{
				data.setInstallType( InstallType.installBRDPro );
			}
			if ( workspaceCloneButton.getSelection( ) )
			{
				data.setInstallType( InstallType.cloneWorkspaceSettings );
			}
			if ( iportalSyncButton.getSelection( ) )
			{
				data.setInstallType( InstallType.synciPortalWorkspace );
			}
		}
	}
}
