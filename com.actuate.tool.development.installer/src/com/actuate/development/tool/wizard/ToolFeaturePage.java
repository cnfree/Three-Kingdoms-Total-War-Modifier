
package com.actuate.development.tool.wizard;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.sf.feeling.swt.win32.internal.extension.util.ImageCache;

import com.actuate.development.tool.config.LocationConfig;
import com.actuate.development.tool.config.PathConfig;
import com.actuate.development.tool.model.feature.ToolFeature;
import com.actuate.development.tool.model.feature.ToolFeatureData;
import com.actuate.development.tool.util.LogUtil;

public class ToolFeaturePage extends WizardPage
{

	private ToolFeatureData data;
	private Button brdproButton;
	private Button workspaceCloneButton;
	private Button iportalSyncButton;
	private Button syncResourceButton;
	private ToolkitWizardHelper helper;
	private Button useLocalDataButton;

	ToolFeaturePage( ToolkitWizardHelper helper, ToolFeatureData data )
	{
		super( "ToolFeaturePage" );
		this.data = data;
		this.helper = helper;
		setTitle( "Select the Toolkit Feature" );
		setDescription( "Select the toolkit feature." );
	}

	public void createControl( Composite parent )
	{
		Composite composite = new Composite( parent, SWT.NULL );
		GridLayout gridLayout = new GridLayout( 3, false );
		gridLayout.marginWidth = 10;
		gridLayout.marginHeight = 20;
		gridLayout.verticalSpacing = 20;
		gridLayout.horizontalSpacing = 10;
		composite.setLayout( gridLayout );

		Label locationLabel = new Label( composite, SWT.NONE );
		locationLabel.setText( "&Location: " );
		GridData gd = new GridData( );
		// gd.exclude = true;
		locationLabel.setLayoutData( gd );
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
				boolean change = false;
				if ( locationCombo.getSelectionIndex( ) == 0 )
				{
					if ( !LocationConfig.HEADQUARTER.equals( LocationConfig.getLocation( ) ) )
						change = true;
					LocationConfig.setLocation( LocationConfig.HEADQUARTER );
				}
				else if ( locationCombo.getSelectionIndex( ) == 1 )
				{
					if ( !LocationConfig.SHANGHAI.equals( LocationConfig.getLocation( ) ) )
						change = true;
					LocationConfig.setLocation( LocationConfig.SHANGHAI );
				}

				updateSyncButtonStatus( );

				if ( change )
				{
					PathConfig.load( );
					try
					{
						ProgressMonitorDialog dialog = new ProgressMonitorDialog( null ) {

							protected void configureShell( Shell shell )
							{
								super.configureShell( shell );
								shell.setImages( new Image[]{
										ImageCache.getImage( "/icons/actuate_16.png" ),
										ImageCache.getImage( "/icons/actuate_32.png" ),
										ImageCache.getImage( "/icons/actuate_48.png" )
								} );
								shell.forceActive( );
							}

							protected void cancelPressed( )
							{
								System.exit( 0 );
							}
						};

						dialog.setCancelable( false );

						dialog.run( true, true, new IRunnableWithProgress( ) {

							public void run( final IProgressMonitor monitor )
									throws InvocationTargetException,
									InterruptedException
							{
								monitor.beginTask( "Collecting Actuate Build Projects...",
										IProgressMonitor.UNKNOWN );
								helper.collectInstallationFiles( monitor );
								monitor.done( );
							}
						} );
					}
					catch ( Exception e1 )
					{
						LogUtil.recordErrorMsg( e1, false );
					}
				}

				setPageComplete( isPageComplete( ) );
			}
		} );

		gd = new GridData( );
		gd.widthHint = 250;
		gd.horizontalAlignment = SWT.FILL;
		// gd.exclude = true;
		locationCombo.setLayoutData( gd );

		useLocalDataButton = new Button( composite, SWT.CHECK );
		useLocalDataButton.setText( "Use local resources" );

		gd = new GridData( );
		useLocalDataButton.setVisible( false );
		useLocalDataButton.setLayoutData( gd );

		Group group = new Group( composite, SWT.NONE );
		group.setText( "Installation Feature" );
		gridLayout = new GridLayout( 2, false );
		group.setLayout( gridLayout );
		gd = new GridData( GridData.FILL_HORIZONTAL );
		gd.horizontalSpan = 3;
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
				checkToolFeature( );
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

		checkToolFeature( );
		setControl( composite );
	}

	private void updateSyncButtonStatus( )
	{
		GridData gd = (GridData) syncResourceButton.getLayoutData( );
		GridData gd1 = (GridData) useLocalDataButton.getLayoutData( );
		if ( !LocationConfig.HEADQUARTER.equals( LocationConfig.getLocation( ) ) )
		{
			gd.exclude = true;
			syncResourceButton.setVisible( false );
			gd1.exclude = true;
			useLocalDataButton.setVisible( false );
		}
		else
		{
			gd.exclude = false;
			syncResourceButton.setVisible( true );
			gd1.exclude = false;
			useLocalDataButton.setVisible( true );
		}

		syncResourceButton.getParent( ).layout( );
		syncResourceButton.getParent( ).getParent( ).layout( );
	}

	public boolean isPageComplete( )
	{
		if ( data != null )
		{
			checkToolFeature( );
			return data.getToolFeature( ) != null;
		}
		return false;
	}

	private void checkToolFeature( )
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
				data.setToolFeature( ToolFeature.syncBRDProResources );
				if ( this.getControl( ).isVisible( )
						&& !syncResourceButton.isVisible( ) )
					data.setToolFeature( null );
			}
		}
	}
}
