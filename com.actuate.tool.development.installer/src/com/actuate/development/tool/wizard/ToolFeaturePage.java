
package com.actuate.development.tool.wizard;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import com.actuate.development.tool.model.ToolFeature;
import com.actuate.development.tool.model.ToolFeatureData;

public class ToolFeaturePage extends WizardPage
{

	private ToolFeatureData data;
	private Button brdproButton;
	private Button workspaceCloneButton;
	private Button iportalSyncButton;

	ToolFeaturePage( ToolFeatureData data )
	{
		super( "InstallTypePage" );
		setTitle( "Select the Toolkit Feature" );
		setDescription( "Select the toolkit feature." );
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
		brdproButton.setText( "&Install the BRDPro development environment" );
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

		iportalSyncButton = new Button( composite, SWT.RADIO );
		iportalSyncButton.setText( "Synchronize the iPortal Viewer &workspace" );
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
				data.setToolFeature( ToolFeature.installBRDPro );
			}
			if ( workspaceCloneButton.getSelection( ) )
			{
				data.setToolFeature( ToolFeature.cloneWorkspaceSettings );
			}
			if ( iportalSyncButton.getSelection( ) )
			{
				data.setToolFeature( ToolFeature.synciPortalWorkspace );
			}
		}
	}
}
