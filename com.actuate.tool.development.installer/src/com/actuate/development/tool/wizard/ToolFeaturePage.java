
package com.actuate.development.tool.wizard;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

import com.actuate.development.tool.config.LocationConfig;
import com.actuate.development.tool.model.ToolFeature;
import com.actuate.development.tool.model.ToolFeatureData;

public class ToolFeaturePage extends WizardPage
{

	private ToolFeatureData data;
	private Button brdproButton;
	private Button workspaceCloneButton;
	private Button iportalSyncButton;
	private Button syncResourceButton;

	ToolFeaturePage( ToolFeatureData data )
	{
		super( "InstallTypePage" );
		this.data = data;
		setTitle( "Select the Toolkit Feature" );
		setDescription( "Select the toolkit feature." );
	}

	public void createControl( Composite parent )
	{
		Composite composite = new Composite( parent, SWT.NULL );
		GridLayout gridLayout = new GridLayout( 2, false );
		gridLayout.marginWidth = 10;
		gridLayout.marginHeight = 20;
		gridLayout.verticalSpacing = 20;
		composite.setLayout( gridLayout );

		Label locationLabel = new Label( composite, SWT.NONE );
		locationLabel.setText( "&Location: " );
		final Combo locationCombo = new Combo( composite, SWT.READ_ONLY
				| SWT.BORDER
				| SWT.SINGLE );
		locationCombo.setItems( new String[]{
				"Corporate Headquarter", "Shangai R&D Center"
		} );
		if ( LocationConfig.HEADQUARTER.equals( LocationConfig.getLocation( ) ) )
			locationCombo.select( 0 );
		else
			locationCombo.select( 1 );

		locationCombo.addSelectionListener( new SelectionAdapter( ) {

			public void widgetSelected( SelectionEvent e )
			{
				if ( locationCombo.getSelectionIndex( ) == 0 )
				{
					LocationConfig.setLocation( LocationConfig.HEADQUARTER );
				}
				else if ( locationCombo.getSelectionIndex( ) == 1 )
				{
					LocationConfig.setLocation( LocationConfig.SHANGHAI );
				}
				updateSyncButtonStatus( );
				setPageComplete( isPageComplete( ) );
			}
		} );

		GridData gd = new GridData( );
		gd.widthHint = 250;
		gd.horizontalAlignment = SWT.FILL;
		locationCombo.setLayoutData( gd );

		Group group = new Group( composite, SWT.NONE );
		group.setText( "Installation Feature" );
		gridLayout = new GridLayout( 2, false );
		group.setLayout( gridLayout );
		gd = new GridData( );
		gd.horizontalSpan = 2;
		group.setLayoutData( gd );

		brdproButton = new Button( group, SWT.RADIO );
		brdproButton.setText( "&Install the BRDPro development environment" );
		brdproButton.setSelection( true );
		gd = new GridData( );
		gd.horizontalSpan = 2;
		brdproButton.setLayoutData( gd );
		brdproButton.addSelectionListener( new SelectionAdapter( ) {

			public void widgetSelected( SelectionEvent e )
			{
				if ( brdproButton.getSelection( ) )
				{
					workspaceCloneButton.setSelection( false );
					iportalSyncButton.setSelection( false );
					syncResourceButton.setSelection( false );
				}
				checkInstallType( );
				setPageComplete( isPageComplete( ) );
			}
		} );

		iportalSyncButton = new Button( group, SWT.RADIO );
		iportalSyncButton.setText( "Synchronize the iPortal Viewer &workspace" );
		gd = new GridData( );
		gd.horizontalSpan = 2;
		iportalSyncButton.setLayoutData( gd );
		iportalSyncButton.addSelectionListener( new SelectionAdapter( ) {

			public void widgetSelected( SelectionEvent e )
			{
				if ( iportalSyncButton.getSelection( ) )
				{
					brdproButton.setSelection( false );
					workspaceCloneButton.setSelection( false );
					syncResourceButton.setSelection( false );
				}
				setPageComplete( isPageComplete( ) );
			}
		} );

		workspaceCloneButton = new Button( group, SWT.RADIO );
		workspaceCloneButton.setText( "Clone &settings from an old workspace to a new workspace" );
		gd = new GridData( );
		gd.horizontalSpan = 2;
		workspaceCloneButton.setLayoutData( gd );
		workspaceCloneButton.addSelectionListener( new SelectionAdapter( ) {

			public void widgetSelected( SelectionEvent e )
			{
				if ( workspaceCloneButton.getSelection( ) )
				{
					brdproButton.setSelection( false );
					iportalSyncButton.setSelection( false );
					syncResourceButton.setSelection( false );
				}
				setPageComplete( isPageComplete( ) );
			}
		} );

		syncResourceButton = new Button( group, SWT.RADIO );
		syncResourceButton.setText( "Synchronize BRDPro &resources from Shanghai server to the local environment" );
		gd = new GridData( );
		gd.horizontalSpan = 2;
		syncResourceButton.setLayoutData( gd );
		syncResourceButton.addSelectionListener( new SelectionAdapter( ) {

			public void widgetSelected( SelectionEvent e )
			{
				if ( syncResourceButton.getSelection( ) )
				{
					brdproButton.setSelection( false );
					iportalSyncButton.setSelection( false );
					workspaceCloneButton.setSelection( false );
				}
				setPageComplete( isPageComplete( ) );
			}
		} );

		updateSyncButtonStatus( );

		checkInstallType( );
		setControl( composite );
	}

	private void updateSyncButtonStatus( )
	{
		GridData gd = (GridData) syncResourceButton.getLayoutData( );
		if ( !LocationConfig.HEADQUARTER.equals( LocationConfig.getLocation( ) ) )
		{
			gd.exclude = true;
			syncResourceButton.setVisible( false );
		}
		else
		{
			gd.exclude = false;
			syncResourceButton.setVisible( true );
		}
		syncResourceButton.getParent( ).layout( );
		syncResourceButton.getParent( ).getParent( ).layout( );
	}

	public boolean isPageComplete( )
	{
		if ( data != null )
		{
			checkInstallType( );
			return data.getToolFeature( ) != null;
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
			if ( syncResourceButton.getSelection( ) )
			{
				if ( syncResourceButton.isVisible( ) )
					data.setToolFeature( ToolFeature.syncBRDProResources );
				else
					data.setToolFeature( null );
			}
		}
	}
}
