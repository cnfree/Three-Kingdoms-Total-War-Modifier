
package com.actuate.tool.development.tool.wizard;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.actuate.tool.development.tool.model.ToolFeatureData;
import com.actuate.tool.development.tool.model.ToolFeature;
import com.actuate.tool.development.tool.model.Modules;
import com.actuate.tool.development.tool.util.FileSorter;
import com.actuate.tool.development.tool.util.LogUtil;
import com.actuate.tool.development.tool.util.UIUtil;

class IPortalViewerProjectPage extends WizardPage implements
		IPropertyChangeListener
{

	private Combo comboProjects;
	private Combo comboFiles;

	private ToolFeatureData data;
	private Text txtRoot;
	private Combo comboView;
	private Button forceButton;
	private Text txtServer;
	private Text txtUser;
	private Text txtPassword;
	private Text txtClient;
	private Button btnTest;

	IPortalViewerProjectPage( ToolFeatureData data )
	{
		super( "ProjectPage" );
		setTitle( "Select the iPortal Viewer Project" );
		setDescription( "Select the iPortal Viewer file." );
		this.data = data;
		this.data.addChangeListener( this );
	}

	public void createControl( Composite parent )
	{
		Composite composite = new Composite( parent, SWT.NULL );
		GridLayout gridLayout = new GridLayout( );
		gridLayout.marginWidth = 10;
		// gridLayout.marginHeight = 10;
		composite.setLayout( gridLayout );

		Group fileSelectionGroup = new Group( composite, SWT.NONE );
		fileSelectionGroup.setText( "Select Actuate Web Viewer Build" );
		GridData gd = new GridData( GridData.FILL_HORIZONTAL );
		gd.minimumWidth = 0;
		fileSelectionGroup.setLayoutData( gd );
		fileSelectionGroup.setLayout( new GridLayout( 2, false ) );

		new Label( fileSelectionGroup, SWT.NONE ).setText( "Viewer &Project: " );
		comboProjects = new Combo( fileSelectionGroup, SWT.READ_ONLY
				| SWT.BORDER );

		gd = new GridData( );
		gd.widthHint = 350;
		gd.horizontalAlignment = SWT.FILL;
		comboProjects.setLayoutData( gd );
		comboProjects.addSelectionListener( new SelectionAdapter( ) {

			public void widgetSelected( SelectionEvent e )
			{
				if ( data != null )
				{
					data.setCurrentIVProject( comboProjects.getText( ).trim( ) );
				}
				handleProjectSelection( );
			}

		} );

		new Label( fileSelectionGroup, SWT.NONE ).setText( "&Viewer File: " );
		comboFiles = new Combo( fileSelectionGroup, SWT.READ_ONLY | SWT.BORDER );

		gd = new GridData( );
		gd.widthHint = 350;
		gd.horizontalAlignment = SWT.FILL;
		comboFiles.setLayoutData( gd );
		comboFiles.addSelectionListener( new SelectionAdapter( ) {

			public void widgetSelected( SelectionEvent e )
			{
				if ( data != null )
					data.getCurrentIportalViewerData( )
							.setBirtViewerFile( comboFiles.getText( ) );
				handleProjectFileSelection( );
			}

		} );

		if ( !data.getIportalViewMap( ).isEmpty( ) )
			comboProjects.setItems( data.getIportalViewMap( )
					.keySet( )
					.toArray( new String[0] ) );
		comboProjects.getParent( ).layout( );

		Group p4ConfigGroup = new Group( composite, SWT.NONE );
		p4ConfigGroup.setText( "P4 Workspace Sync Settings" );
		gd = new GridData( GridData.FILL_HORIZONTAL );
		gd.minimumWidth = 0;
		gd.verticalIndent = 10;
		p4ConfigGroup.setLayoutData( gd );

		p4ConfigGroup.setLayout( new GridLayout( 3, false ) );

		new Label( p4ConfigGroup, SWT.NONE ).setText( "Wor&kspace: " );
		txtRoot = new Text( p4ConfigGroup, SWT.BORDER );
		gd = new GridData( GridData.FILL_HORIZONTAL );
		txtRoot.setLayoutData( gd );

		txtRoot.addModifyListener( new ModifyListener( ) {

			public void modifyText( ModifyEvent e )
			{
				if ( data != null )
					data.getCurrentIportalViewerData( )
							.setRoot( txtRoot.getText( ) );
				setPageComplete( isPageComplete( ) );
			}

		} );

		Button rootDirectoryButton = new Button( p4ConfigGroup, SWT.PUSH );
		rootDirectoryButton.setText( "Br&owse..." );
		rootDirectoryButton.addSelectionListener( new SelectionAdapter( ) {

			public void widgetSelected( SelectionEvent e )
			{
				DirectoryDialog dialog = new DirectoryDialog( getShell( ) );
				dialog.setMessage( "Select Directory" );
				String path = dialog.open( );
				if ( path != null )
				{
					txtRoot.setText( path );
				}
			}

		} );

		new Label( p4ConfigGroup, SWT.NONE ).setText( "P4 V&iew: " );
		comboView = new Combo( p4ConfigGroup, SWT.BORDER );
		comboView.setItems( Modules.getInstance( ).getIPortalViews( ) );
		gd = new GridData( GridData.FILL_HORIZONTAL );
		gd.horizontalSpan = 2;
		comboView.setLayoutData( gd );

		comboView.addModifyListener( new ModifyListener( ) {

			public void modifyText( ModifyEvent e )
			{
				if ( data != null )
					data.getCurrentIportalViewerData( )
							.setView( comboView.getText( ) );
				setPageComplete( isPageComplete( ) );
			}

		} );

		forceButton = new Button( p4ConfigGroup, SWT.CHECK );
		forceButton.setText( "Forc&e Operation" );
		gd = new GridData( GridData.FILL_HORIZONTAL );
		gd.horizontalSpan = 3;
		forceButton.setLayoutData( gd );
		forceButton.addSelectionListener( new SelectionAdapter( ) {

			public void widgetSelected( SelectionEvent e )
			{
				if ( data != null )
					data.getCurrentIportalViewerData( )
							.setForceOperation( forceButton.getSelection( ) );
			}
		} );

		Group connectionGroup = new Group( composite, SWT.NONE );
		connectionGroup.setText( "P4 Connection Settings" );
		gridLayout = new GridLayout( 2, false );
		gridLayout.marginWidth = 10;
		connectionGroup.setLayout( gridLayout );

		gd = new GridData( GridData.FILL_HORIZONTAL );
		gd.minimumWidth = 0;
		gd.verticalIndent = 10;
		connectionGroup.setLayoutData( gd );

		new Label( connectionGroup, SWT.NONE ).setText( "&Server: " );
		txtServer = new Text( connectionGroup, SWT.BORDER );
		gd = new GridData( GridData.FILL_HORIZONTAL );
		txtServer.setLayoutData( gd );

		txtServer.addModifyListener( new ModifyListener( ) {

			public void modifyText( ModifyEvent e )
			{
				if ( data != null )
					data.getCurrentIportalViewerData( )
							.setServer( txtServer.getText( ) );
				setPageComplete( isPageComplete( ) );
			}

		} );

		new Label( connectionGroup, SWT.NONE ).setText( "&User: " );
		txtUser = new Text( connectionGroup, SWT.BORDER );
		gd = new GridData( GridData.FILL_HORIZONTAL );
		txtUser.setLayoutData( gd );

		txtUser.addModifyListener( new ModifyListener( ) {

			public void modifyText( ModifyEvent e )
			{
				if ( data != null )
					data.getCurrentIportalViewerData( )
							.setUser( txtUser.getText( ) );
				setPageComplete( isPageComplete( ) );
			}

		} );

		new Label( connectionGroup, SWT.NONE ).setText( "P&assword: " );
		txtPassword = new Text( connectionGroup, SWT.BORDER | SWT.PASSWORD );
		gd = new GridData( GridData.FILL_HORIZONTAL );
		txtPassword.setLayoutData( gd );

		txtPassword.addModifyListener( new ModifyListener( ) {

			public void modifyText( ModifyEvent e )
			{
				if ( data != null )
					data.getCurrentIportalViewerData( )
							.setPassword( txtPassword.getText( ) );
				setPageComplete( isPageComplete( ) );
			}

		} );

		new Label( connectionGroup, SWT.NONE ).setText( "&Client: " );
		txtClient = new Text( connectionGroup, SWT.BORDER );
		gd = new GridData( GridData.FILL_HORIZONTAL );
		txtClient.setLayoutData( gd );

		txtClient.addModifyListener( new ModifyListener( ) {

			public void modifyText( ModifyEvent e )
			{
				if ( data != null )
					data.getCurrentIportalViewerData( )
							.setClient( txtClient.getText( ) );
				setPageComplete( isPageComplete( ) );
			}

		} );

		btnTest = new Button( connectionGroup, SWT.PUSH );
		btnTest.setText( "&Test Connection" );
		gd = new GridData( );
		gd.horizontalAlignment = SWT.RIGHT;
		gd.horizontalSpan = 2;
		btnTest.setLayoutData( gd );
		btnTest.setEnabled( false );
		btnTest.addSelectionListener( new SelectionAdapter( ) {

			public void widgetSelected( SelectionEvent selectionevent )
			{
				try
				{
					final boolean[] error = new boolean[1];
					final Process p4Process = Runtime.getRuntime( )
							.exec( "p4 -p "
									+ txtServer.getText( )
									+ " -u "
									+ txtUser.getText( )
									+ " -P "
									+ txtPassword.getText( )
									+ " clients -u "
									+ txtUser.getText( ) );

					Thread thread = new Thread( ) {

						public void run( )
						{
							try
							{
								BufferedReader input = new BufferedReader( new InputStreamReader( p4Process.getErrorStream( ) ) );
								final String[] line = new String[1];
								final StringBuffer buffer = new StringBuffer( );
								while ( ( line[0] = input.readLine( ) ) != null )
								{
									buffer.append( line[0] + "\r\n" );
								}
								input.close( );

								if ( buffer.length( ) > 0 )
								{
									error[0] = true;
									Display.getDefault( )
											.syncExec( new Runnable( ) {

												public void run( )
												{
													MessageDialog.openError( UIUtil.getShell( ),
															"Error",
															buffer.toString( ) );
												}
											} );
								}
							}
							catch ( Exception e )
							{
								Logger.getLogger( IPortalViewerProjectPage.class.getName( ) )
										.log( Level.WARNING,
												"Get error stream failed.", e ); //$NON-NLS-1$
							}
						}
					};
					thread.setDaemon( true );
					thread.start( );

					StringWriter output = new StringWriter( );
					IOUtils.copy( p4Process.getInputStream( ), output );
					p4Process.waitFor( );

					Thread.sleep( 100 );

					Pattern pattern = Pattern.compile( "(?i)Client\\s+\\S+",
							Pattern.CASE_INSENSITIVE );
					Matcher matcher = pattern.matcher( output.toString( ) );

					boolean exist = false;
					while ( matcher.find( ) )
					{
						String client = matcher.group( )
								.replaceAll( "(?i)Client\\s+", "" );
						if ( client.equalsIgnoreCase( txtClient.getText( )
								.trim( ) ) )
						{
							exist = true;
							break;
						}
					}
					if ( !error[0] )
					{
						if ( !exist )
						{
							MessageDialog.openError( UIUtil.getShell( ),
									"Error",
									"The client "
											+ txtClient.getText( ).trim( )
											+ " is unavailable." );
						}
						else
						{
							MessageDialog.openInformation( UIUtil.getShell( ),
									"Success",
									"Ping succeeded!" );
						}
					}
				}
				catch ( Exception e )
				{
					LogUtil.recordErrorMsg( e, false );
				}
			}
		} );

		setControl( composite );

		if ( data != null && data.getCurrentIVProject( ) != null )
		{
			int index = comboProjects.indexOf( data.getCurrentIVProject( ) );
			if ( index != -1 )
			{
				comboProjects.setText( data.getCurrentIVProject( ) );
				handleProjectSelection( );
			}
		}

		initPage( );

	}

	private void initPage( )
	{

		if ( data != null
				&& data.getCurrentIportalViewerData( ).getRoot( ) != null
				&& data.getCurrentIportalViewerData( )
						.getRoot( )
						.trim( )
						.length( ) > 0 )
		{
			txtRoot.setText( data.getCurrentIportalViewerData( )
					.getRoot( )
					.trim( ) );
		}
		else
		{
			txtRoot.setText( "" );
		}

		if ( data != null
				&& data.getCurrentIportalViewerData( ).getView( ) != null
				&& data.getCurrentIportalViewerData( )
						.getView( )
						.trim( )
						.length( ) > 0 )
		{
			comboView.setText( data.getCurrentIportalViewerData( )
					.getView( )
					.trim( ) );
		}
		else
		{
			comboView.setText( "" );
		}

		if ( data != null
				&& data.getCurrentIportalViewerData( ).getServer( ) != null
				&& data.getCurrentIportalViewerData( )
						.getServer( )
						.trim( )
						.length( ) > 0 )
		{
			txtServer.setText( data.getCurrentIportalViewerData( )
					.getServer( )
					.trim( ) );
		}
		else
		{
			txtServer.setText( "p4:1666" );
		}

		if ( data != null
				&& data.getCurrentIportalViewerData( ).getUser( ) != null
				&& data.getCurrentIportalViewerData( )
						.getUser( )
						.trim( )
						.length( ) > 0 )
		{
			txtUser.setText( data.getCurrentIportalViewerData( )
					.getUser( )
					.trim( ) );
		}
		else
		{
			txtUser.setText( "" );
		}

		if ( data != null
				&& data.getCurrentIportalViewerData( ).getPassword( ) != null
				&& data.getCurrentIportalViewerData( )
						.getPassword( )
						.trim( )
						.length( ) > 0 )
		{
			txtPassword.setText( data.getCurrentIportalViewerData( )
					.getPassword( )
					.trim( ) );
		}
		else
		{
			txtPassword.setText( "" );
		}

		if ( data != null
				&& data.getCurrentIportalViewerData( ).getClient( ) != null
				&& data.getCurrentIportalViewerData( )
						.getClient( )
						.trim( )
						.length( ) > 0 )
		{
			txtClient.setText( data.getCurrentIportalViewerData( )
					.getClient( )
					.trim( ) );
		}
		else
		{
			txtClient.setText( "" );
		}

		if ( data != null )
		{
			forceButton.setSelection( data.getCurrentIportalViewerData( )
					.isForceOperation( ) );
		}
		else
		{
			forceButton.setSelection( false );
		}
	}

	public boolean isPageComplete( )
	{
		if ( data != null )
		{
			if ( data.getToolFeature( ) != ToolFeature.synciPortalWorkspace )
				return true;

			checkStatus( );

			return comboProjects != null
					&& comboProjects.getSelectionIndex( ) > -1
					&& comboFiles != null
					&& comboFiles.getSelectionIndex( ) > -1
					&& getErrorMessage( ) == null;
		}
		return false;
	}

	private void handleProjectSelection( )
	{
		comboFiles.removeAll( );
		List<File> files = data.getIportalViewMap( )
				.get( comboProjects.getText( ) );

		FileSorter.sortFiles( files );

		if ( files != null && !files.isEmpty( ) )
		{
			for ( File file : files )
			{
				comboFiles.add( file.getName( ) );
			}
			comboFiles.select( comboFiles.getItemCount( ) - 1 );
			handleProjectFileSelection( );
		}

		String iPortalView = Modules.getInstance( )
				.getIPortalView( comboProjects.getText( ) );
		if ( iPortalView != null )
		{
			comboView.setText( iPortalView );
		}
		else
		{
			comboView.setText( "" );
			comboView.clearSelection( );
		}

		setPageComplete( isPageComplete( ) );
	}

	private void handleProjectFileSelection( )
	{
		if ( data != null )
		{
			data.getCurrentIportalViewerData( )
					.setBirtViewerFile( data.getIportalViewMap( )
							.get( comboProjects.getText( ) )
							.get( comboFiles.getSelectionIndex( ) )
							.getAbsolutePath( ) );
		}
		setPageComplete( isPageComplete( ) );
	}

	private void checkStatus( )
	{
		btnTest.setEnabled( txtServer.getText( ).trim( ).length( ) > 0
				&& txtUser.getText( ).trim( ).length( ) > 0
				&& txtPassword.getText( ).trim( ).length( ) > 0
				&& txtClient.getText( ).trim( ).length( ) > 0 );

		if ( txtRoot.getText( ).trim( ).length( ) == 0 )
		{
			setErrorMessage( "Must specify P4 workspace." );
			return;
		}

		File file = new File( txtRoot.getText( ) );
		if ( file.getParentFile( ) == null )
		{
			setErrorMessage( "The path of P4 workspace is invalid." );
			return;
		}

		if ( comboView.getText( ).trim( ).length( ) == 0 )
		{
			setErrorMessage( "Must specify P4 view." );
			return;
		}

		if ( txtServer.getText( ).trim( ).length( ) == 0 )
		{
			setErrorMessage( "Must specifiy server address as <host>:<port> or ssl:<host>:<port>." );
			return;
		}

		if ( txtUser.getText( ).trim( ).length( ) == 0 )
		{
			setErrorMessage( "Must specify user name." );
			return;
		}

		if ( txtPassword.getText( ).trim( ).length( ) == 0 )
		{
			setErrorMessage( "Must specify password." );
			return;
		}

		if ( txtClient.getText( ).trim( ).length( ) == 0 )
		{
			setErrorMessage( "Must specify client." );
			return;
		}

		setErrorMessage( null );
		return;
	}

	public void propertyChange( PropertyChangeEvent event )
	{
		if ( ToolkitWizard.CURRENT_IV_PROJECT.equals( event.getProperty( ) ) )
			initPage( );
	}
}
