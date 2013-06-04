
package com.actuate.development.tool.wizard;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.nebula.widgets.cdatetime.CDT;
import org.eclipse.nebula.widgets.cdatetime.CDateTime;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.sf.feeling.swt.win32.internal.extension.util.ImageCache;

import com.actuate.development.tool.config.PathConfig;
import com.actuate.development.tool.dialog.ChangelistDialog;
import com.actuate.development.tool.dialog.CreateClientDialog;
import com.actuate.development.tool.model.Modules;
import com.actuate.development.tool.model.feature.ToolFeature;
import com.actuate.development.tool.model.feature.ToolFeatureData;
import com.actuate.development.tool.task.SyncIPortalWorkspace;
import com.actuate.development.tool.util.FileSorter;
import com.actuate.development.tool.util.FileUtil;
import com.actuate.development.tool.util.LogUtil;
import com.actuate.development.tool.util.UIUtil;

public class IPortalViewerProjectPage extends WizardPage implements
		IPropertyChangeListener
{

	private Combo comboProjects;
	private Combo comboFiles;

	private ToolFeatureData data;
	private Text txtRoot;
	private Combo comboView;
	private Button forceButton;
	private Button skipSyncButton;
	private Text txtServer;
	private Text txtUser;
	private Text txtPassword;
	private Combo comboClient;
	private Button btnTest;
	private Button btnCreate;
	private Button btnSearch;
	private Map<String, String> rootMap = new HashMap<String, String>( );
	private Button btnDelete;
	private Button revertButton;
	private Text customProjectName;
	private Composite specifyContainer;
	private Combo choiceCombo;
	private Button specifyRivision;

	public IPortalViewerProjectPage( ToolFeatureData data )
	{
		super( "ProjectPage" );
		this.data = data;
		this.data.addChangeListener( this );
		setTitle( "Select the iPortal Viewer Project" );
		setDescription( "Select the iPortal Viewer file." );
	}

	public void createControl( Composite parent )
	{
		ScrolledComposite scrollContent = new ScrolledComposite( parent,
				SWT.H_SCROLL | SWT.V_SCROLL );
		scrollContent.setAlwaysShowScrollBars( false );
		scrollContent.setExpandHorizontal( true );
		scrollContent.setMinWidth( 550 );
		scrollContent.setLayout( new FillLayout( ) );
		scrollContent.setLayoutData( new GridData( GridData.FILL_BOTH ) );

		Composite composite = new Composite( scrollContent, SWT.NULL );
		GridLayout gridLayout = new GridLayout( );
		gridLayout.marginWidth = 10;
		// gridLayout.marginHeight = 10;
		composite.setLayout( gridLayout );

		Group fileSelectionGroup = new Group( composite, SWT.NONE );
		fileSelectionGroup.setText( "Select Actuate Web Viewer Build" );
		GridData gd = new GridData( GridData.FILL_HORIZONTAL );
		gd.minimumWidth = 0;
		fileSelectionGroup.setLayoutData( gd );
		fileSelectionGroup.setLayout( new GridLayout( 3, false ) );

		new Label( fileSelectionGroup, SWT.NONE ).setText( "Viewer &Project: " );
		comboProjects = new Combo( fileSelectionGroup, SWT.READ_ONLY
				| SWT.BORDER );

		gd = new GridData( );
		gd.widthHint = 350;
		gd.horizontalAlignment = SWT.FILL;
		gd.horizontalSpan = 2;
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
		comboFiles = new Combo( fileSelectionGroup, SWT.BORDER );

		gd = new GridData( GridData.FILL_HORIZONTAL );
		gd.widthHint = 350;
		comboFiles.setLayoutData( gd );

		comboFiles.addModifyListener( new ModifyListener( ) {

			public void modifyText( ModifyEvent e )
			{
				BusyIndicator.showWhile( Display.getDefault( ),
						new Runnable( ) {

							public void run( )
							{
								handleProjectFileSelection( );
							}
						} );

			}
		} );

		final Button browseButton = new Button( fileSelectionGroup, SWT.PUSH );
		browseButton.setText( "Bro&wse..." );
		gd = new GridData( );
		browseButton.setLayoutData( gd );
		browseButton.addSelectionListener( new SelectionAdapter( ) {

			public void widgetSelected( SelectionEvent e )
			{
				FileDialog dialog = new FileDialog( browseButton.getShell( ),
						SWT.OPEN | SWT.SINGLE );
				dialog.setFilterExtensions( new String[]{
					"*.jar;*.war;*.zip"
				} );
				dialog.setFilterNames( new String[]{
					"Archive File (*.jar;*.war;*.zip)"
				} );
				String file = dialog.open( );
				if ( file != null )
				{
					comboFiles.setText( file );
				}
			}
		} );

		new Label( fileSelectionGroup, SWT.NONE ).setText( "Custo&m Project Name: " );
		customProjectName = new Text( fileSelectionGroup, SWT.SINGLE
				| SWT.BORDER );

		gd = new GridData( );
		gd.widthHint = 350;
		gd.horizontalSpan = 2;
		gd.horizontalAlignment = SWT.FILL;
		customProjectName.setLayoutData( gd );
		customProjectName.addModifyListener( new ModifyListener( ) {

			public void modifyText( ModifyEvent e )
			{
				if ( data != null )
				{
					data.getCurrentIportalViewerData( )
							.setCustomProjectName( customProjectName.getText( ) );
				}
			}
		} );

		if ( !data.getIportalViewMap( ).isEmpty( ) )
			comboProjects.setItems( data.getIportalViewMap( )
					.keySet( )
					.toArray( new String[0] ) );
		comboProjects.getParent( ).layout( );

		Group connectionGroup = new Group( composite, SWT.NONE );
		connectionGroup.setText( "P4 Connection Settings" );
		gridLayout = new GridLayout( 5, false );
		gridLayout.marginWidth = 10;
		connectionGroup.setLayout( gridLayout );

		gd = new GridData( GridData.FILL_HORIZONTAL );
		gd.minimumWidth = 0;
		gd.verticalIndent = 10;
		connectionGroup.setLayoutData( gd );

		new Label( connectionGroup, SWT.NONE ).setText( "&Server: " );
		txtServer = new Text( connectionGroup, SWT.BORDER );
		gd = new GridData( GridData.FILL_HORIZONTAL );
		gd.horizontalSpan = 4;
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
		gd.horizontalSpan = 4;
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
		gd.horizontalSpan = 4;
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
		comboClient = new Combo( connectionGroup, SWT.BORDER );
		gd = new GridData( GridData.FILL_HORIZONTAL );
		comboClient.setLayoutData( gd );

		comboClient.addModifyListener( new ModifyListener( ) {

			public void modifyText( ModifyEvent e )
			{
				if ( data != null )
					data.getCurrentIportalViewerData( )
							.setClient( comboClient.getText( ) );
				setPageComplete( isPageComplete( ) );
			}

		} );

		comboClient.addSelectionListener( new SelectionAdapter( ) {

			public void widgetSelected( SelectionEvent e )
			{
				handleClientSelectionEvent( );
			}
		} );

		btnSearch = new Button( connectionGroup, SWT.PUSH );
		gd = new GridData( );
		int height = comboClient.computeSize( SWT.DEFAULT, SWT.DEFAULT ).y;
		gd.widthHint = gd.heightHint = height + comboClient.getBorderWidth( );
		btnSearch.setLayoutData( gd );
		btnSearch.setToolTipText( "Find all clients" );
		btnSearch.setImage( ImageCache.getImage( "/icons/search.png" ) );
		btnSearch.setEnabled( false );
		btnSearch.addSelectionListener( new SelectionAdapter( ) {

			public void widgetSelected( SelectionEvent selectionevent )
			{
				BusyIndicator.showWhile( Display.getDefault( ),
						new Runnable( ) {

							public void run( )
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
													buffer.append( line[0]
															+ "\r\n" );
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
									IOUtils.copy( p4Process.getInputStream( ),
											output );
									p4Process.waitFor( );

									Thread.sleep( 100 );

									if ( !error[0] )
									{
										List<String> clients = new ArrayList<String>( );
										Pattern pattern = Pattern.compile( "(?i)Client\\s+\\S+",
												Pattern.CASE_INSENSITIVE );
										Matcher matcher = pattern.matcher( output.toString( ) );

										while ( matcher.find( ) )
										{
											String client = matcher.group( )
													.replaceAll( "(?i)Client\\s+",
															"" );
											clients.add( client.trim( ) );
										}

										String client = comboClient.getText( );
										comboClient.removeAll( );
										comboClient.setItems( clients.toArray( new String[0] ) );
										if ( client == null
												|| client.trim( ).length( ) == 0 )
										{
											if ( comboClient.getItemCount( ) > 0 )
											{
												comboClient.select( 0 );
											}
										}
										else
										{
											comboClient.setText( client );
										}
										handleClientSelectionEvent( );
									}
								}
								catch ( Exception e )
								{
									LogUtil.recordErrorMsg( e, false );
								}
								setPageComplete( isPageComplete( ) );
							}
						} );
			}
		} );

		btnDelete = new Button( connectionGroup, SWT.PUSH );
		gd = new GridData( );
		gd.widthHint = gd.heightHint = height + comboClient.getBorderWidth( );
		btnDelete.setLayoutData( gd );
		btnDelete.setToolTipText( "Delete the specify client" );
		btnDelete.setImage( ImageCache.getImage( "/icons/delete.gif" ) );
		btnDelete.setEnabled( false );
		btnDelete.addSelectionListener( new SelectionAdapter( ) {

			public void widgetSelected( SelectionEvent selectionevent )
			{
				boolean confirm = MessageDialog.openConfirm( IPortalViewerProjectPage.this.getShell( ),
						"Confirm",
						"Are you sure you want to delete this client "
								+ comboClient.getText( )
								+ "?" );
				if ( !confirm )
					return;
				BusyIndicator.showWhile( Display.getDefault( ),
						new Runnable( ) {

							public void run( )
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
													+ " client -d "
													+ comboClient.getText( ) );

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
													buffer.append( line[0]
															+ "\r\n" );
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
									IOUtils.copy( p4Process.getInputStream( ),
											output );
									p4Process.waitFor( );

									Thread.sleep( 100 );

									if ( !error[0] )
									{
										List<String> clients = new ArrayList<String>( );
										getClients( clients, error );
										comboClient.setItems( clients.toArray( new String[0] ) );
										if ( comboClient.getItemCount( ) > 0 )
										{
											comboClient.select( 0 );
										}
										handleClientSelectionEvent( );
									}
								}
								catch ( Exception e )
								{
									LogUtil.recordErrorMsg( e, false );
								}
								setPageComplete( isPageComplete( ) );
							}
						} );
			}
		} );

		btnCreate = new Button( connectionGroup, SWT.PUSH );
		gd = new GridData( );
		gd.heightHint = height + comboClient.getBorderWidth( );
		btnCreate.setLayoutData( gd );
		btnCreate.setText( "Create &New..." );
		btnCreate.setEnabled( false );
		btnCreate.addSelectionListener( new SelectionAdapter( ) {

			public void widgetSelected( SelectionEvent selectionevent )
			{
				try
				{
					List<String> clients = new ArrayList<String>( );
					boolean[] error = new boolean[1];

					getClients( clients, error );

					if ( error[0] )
						return;

					CreateClientDialog dialog = new CreateClientDialog( null );
					dialog.setExistClients( clients );
					if ( dialog.open( ) == Dialog.OK )
					{
						String[] result = dialog.getResult( );
						if ( result != null && result.length == 2 )
						{
							updateClientSpecification( FileUtil.getTempFile( "specification.txt" ),
									result[0],
									result[1] );
							clients.clear( );
							getClients( clients, error );
							String client = comboClient.getText( );
							comboClient.removeAll( );
							comboClient.setItems( clients.toArray( new String[0] ) );
							if ( comboClient.indexOf( result[0] ) != -1 )
							{
								comboClient.setText( result[0] );
								txtRoot.setText( result[1] );
							}
							else
							{
								if ( client == null
										|| client.trim( ).length( ) == 0 )
								{
									if ( comboClient.getItemCount( ) > 0 )
										comboClient.select( 0 );
								}
								else
									comboClient.setText( client );
								handleClientSelectionEvent( );
							}
						}
					}
				}
				catch ( Exception e )
				{
					LogUtil.recordErrorMsg( e, false );
				}
				setPageComplete( isPageComplete( ) );
			}
		} );

		Label span = new Label( connectionGroup, SWT.NONE );
		gd = new GridData( );
		gd.horizontalAlignment = SWT.RIGHT;
		gd.horizontalSpan = 2;
		span.setLayoutData( gd );

		btnTest = new Button( connectionGroup, SWT.PUSH );
		btnTest.setText( "&Test Connection" );
		gd = new GridData( );
		gd.heightHint = height + comboClient.getBorderWidth( );
		gd.horizontalAlignment = SWT.FILL;
		gd.horizontalSpan = 3;
		btnTest.setLayoutData( gd );
		btnTest.setEnabled( false );
		btnTest.addSelectionListener( new SelectionAdapter( ) {

			public void widgetSelected( SelectionEvent selectionevent )
			{
				BusyIndicator.showWhile( Display.getDefault( ),
						new Runnable( ) {

							public void run( )
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
													buffer.append( line[0]
															+ "\r\n" );
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
									IOUtils.copy( p4Process.getInputStream( ),
											output );
									p4Process.waitFor( );

									Thread.sleep( 100 );

									Pattern pattern = Pattern.compile( "(?i)Client\\s+\\S+",
											Pattern.CASE_INSENSITIVE );
									Matcher matcher = pattern.matcher( output.toString( ) );

									boolean exist = false;
									while ( matcher.find( ) )
									{
										String client = matcher.group( )
												.replaceAll( "(?i)Client\\s+",
														"" );
										if ( client.equalsIgnoreCase( comboClient.getText( )
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
															+ comboClient.getText( )
																	.trim( )
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
			}
		} );

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

		Composite choiceGroup = new Composite( p4ConfigGroup, SWT.NONE );
		gd = new GridData( GridData.FILL_HORIZONTAL );
		gd.horizontalSpan = 3;
		choiceGroup.setLayoutData( gd );

		GridLayout layout = new GridLayout( );
		layout.numColumns = 4;
		layout.marginWidth = layout.marginHeight = 0;
		choiceGroup.setLayout( layout );

		final Button latestRivision = new Button( choiceGroup, SWT.RADIO );
		latestRivision.setText( "&Get latest revision" );

		specifyRivision = new Button( choiceGroup, SWT.RADIO );
		specifyRivision.setText( "Specify &revision using:" );

		choiceCombo = new Combo( choiceGroup, SWT.READ_ONLY
				| SWT.SINGLE
				| SWT.BORDER );
		choiceCombo.setItems( new String[]{
				"Revision", "Changelist", "Date/Time"
		} );
		choiceCombo.addSelectionListener( new SelectionAdapter( ) {

			public void widgetSelected( SelectionEvent e )
			{
				if ( choiceCombo.getSelectionIndex( ) == 0 )
				{
					selectRevisionChoice( );
				}
				else if ( choiceCombo.getSelectionIndex( ) == 1 )
				{
					selectChangelistChoice( );
				}
				else if ( choiceCombo.getSelectionIndex( ) == 2 )
				{
					selectDateTimeChoice( );
				}
			}
		} );

		specifyContainer = new Composite( choiceGroup, SWT.NONE );
		Button testButton = new Button( specifyContainer, SWT.PUSH );
		Text testText = new Text( specifyContainer, SWT.BORDER );
		gd = new GridData( GridData.FILL_HORIZONTAL );
		int buttonHeight = testButton.computeSize( SWT.DEFAULT, SWT.DEFAULT ).y;
		int textHeight = testText.computeSize( SWT.DEFAULT, SWT.DEFAULT ).y;
		gd.heightHint = buttonHeight > textHeight ? buttonHeight : textHeight;
		testButton.dispose( );
		testText.dispose( );
		specifyContainer.setLayoutData( gd );

		choiceCombo.select( 0 );
		selectRevisionChoice( );

		latestRivision.setSelection( true );
		specifyRivision.setSelection( false );

		resetChoiceStatus( );

		specifyRivision.addSelectionListener( new SelectionAdapter( ) {

			public void widgetSelected( SelectionEvent e )
			{
				if ( specifyRivision.getSelection( ) )
				{
					data.getCurrentIportalViewerData( )
							.setLatestRevision( false );
				}
				resetChoiceStatus( );
			}

		} );

		latestRivision.addSelectionListener( new SelectionAdapter( ) {

			public void widgetSelected( SelectionEvent e )
			{
				if ( latestRivision.getSelection( ) )
				{
					data.getCurrentIportalViewerData( )
							.setLatestRevision( true );
				}
			}

		} );

		layout = new GridLayout( );
		layout.numColumns = 3;
		layout.marginWidth = layout.marginHeight = 0;
		specifyContainer.setLayout( layout );

		Composite checkGroup = new Composite( p4ConfigGroup, SWT.CHECK );
		gd = new GridData( GridData.FILL_HORIZONTAL );
		gd.horizontalSpan = 3;
		checkGroup.setLayoutData( gd );

		layout = new GridLayout( );
		layout.numColumns = 2;
		layout.marginWidth = layout.marginHeight = 0;
		checkGroup.setLayout( layout );

		forceButton = new Button( checkGroup, SWT.CHECK );
		forceButton.setText( "Forc&e Operation" );
		gd = new GridData( );
		forceButton.setLayoutData( gd );
		forceButton.addSelectionListener( new SelectionAdapter( ) {

			public void widgetSelected( SelectionEvent e )
			{
				if ( data != null )
					data.getCurrentIportalViewerData( )
							.setForceOperation( forceButton.getSelection( ) );
			}
		} );

		revertButton = new Button( checkGroup, SWT.CHECK );
		revertButton.setText( "&Revert Files" );
		gd = new GridData( GridData.FILL_HORIZONTAL );
		gd.exclude = true;
		revertButton.setLayoutData( gd );
		revertButton.addSelectionListener( new SelectionAdapter( ) {

			public void widgetSelected( SelectionEvent e )
			{
				if ( data != null )
					data.getCurrentIportalViewerData( )
							.setRevertFiles( revertButton.getSelection( ) );
			}
		} );

		revertButton.setVisible( false );

		skipSyncButton = new Button( checkGroup, SWT.CHECK );
		skipSyncButton.setText( "Skip S&ync" );
		gd = new GridData( GridData.FILL_HORIZONTAL );
		skipSyncButton.setLayoutData( gd );
		skipSyncButton.addSelectionListener( new SelectionAdapter( ) {

			public void widgetSelected( SelectionEvent e )
			{
				if ( data != null )
					data.getCurrentIportalViewerData( )
							.setSkipSync( skipSyncButton.getSelection( ) );
			}
		} );

		Point size = composite.computeSize( SWT.DEFAULT, SWT.DEFAULT );
		composite.setSize( size );

		scrollContent.setContent( composite );

		setControl( scrollContent );

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

	protected void enableSpecifyContainer( boolean enabled )
	{
		Control[] children = specifyContainer.getChildren( );
		if ( children != null )
		{
			for ( int i = 0; i < children.length; i++ )
			{
				children[i].setEnabled( enabled );
			}
		}
		specifyContainer.setEnabled( enabled );

	}

	protected void selectDateTimeChoice( )
	{
		disposeSpecifyContainerChildren( );
		final CDateTime dateTimeText = new CDateTime( specifyContainer,
				CDT.BORDER | CDT.DROP_DOWN );
		dateTimeText.setFormat( -1 );
		dateTimeText.setPattern( "yyyy/MM/dd HH:mm:ss" );
		dateTimeText.setSelection( Calendar.getInstance( ).getTime( ) );
		GridData gd = new GridData( );
		gd.widthHint = 200;
		gd.verticalAlignment = SWT.CENTER;
		gd.grabExcessVerticalSpace = true;
		dateTimeText.setLayoutData( gd );
		dateTimeText.addSelectionListener( new SelectionAdapter( ) {

			@Override
			public void widgetSelected( SelectionEvent e )
			{
				if ( dateTimeText.getText( ).trim( ).length( ) > 0 )
				{
					data.getCurrentIportalViewerData( )
							.setRevisionArg( "@"
									+ new SimpleDateFormat( "yyyy/MM/dd:HH:mm:ss" ).format( dateTimeText.getSelection( ) ) );
				}
				else
					data.getCurrentIportalViewerData( ).setRevisionArg( null );
			}
		} );

		if ( dateTimeText.getText( ).trim( ).length( ) > 0 )
			data.getCurrentIportalViewerData( ).setRevisionArg( "@"
					+ dateTimeText.getText( ).trim( ) );
		else
			data.getCurrentIportalViewerData( ).setRevisionArg( null );

		specifyContainer.layout( );
		specifyContainer.getParent( ).layout( );
		specifyContainer.getParent( ).getParent( ).layout( );
	}

	private void disposeSpecifyContainerChildren( )
	{
		Control[] children = specifyContainer.getChildren( );
		if ( children != null )
		{
			for ( int i = 0; i < children.length; i++ )
			{
				children[i].dispose( );
			}
		}

	}

	protected void selectChangelistChoice( )
	{
		disposeSpecifyContainerChildren( );
		final Text changelistText = new Text( specifyContainer, SWT.BORDER );
		GridData gd = new GridData( );
		gd.widthHint = 200;
		gd.verticalAlignment = SWT.CENTER;
		gd.grabExcessVerticalSpace = true;
		changelistText.setLayoutData( gd );
		changelistText.addModifyListener( new ModifyListener( ) {

			public void modifyText( ModifyEvent e )
			{
				if ( changelistText.getText( ).trim( ).length( ) > 0 )
					data.getCurrentIportalViewerData( ).setRevisionArg( "@"
							+ changelistText.getText( ).trim( ) );
				else
					data.getCurrentIportalViewerData( ).setRevisionArg( null );
			}
		} );
		changelistText.addVerifyListener( new VerifyListener( ) {

			public void verifyText( VerifyEvent ve )
			{
				if ( ve.character == SWT.BS
						|| ve.keyCode == SWT.ARROW_LEFT
						|| ve.keyCode == SWT.ARROW_RIGHT
						|| ve.keyCode == SWT.DEL )
				{
					ve.doit = true;
					return;
				}
				if ( ve.character == '\0' )
				{
					try
					{
						if ( ve.text.trim( ).length( ) == 0
								|| Integer.parseInt( ve.text.trim( ) ) > 0 )
						{
							ve.doit = true;
							return;
						}
						else
						{
							ve.doit = false;
							return;
						}
					}
					catch ( NumberFormatException e )
					{
						ve.doit = false;
						return;
					}
				}
				if ( !( '0' <= ve.character && ve.character <= '9' ) )
				{
					ve.doit = false;
					return;
				}
			}
		} );
		Button browseButton = new Button( specifyContainer, SWT.PUSH );
		browseButton.setText( "Bro&wse..." );
		browseButton.addSelectionListener( new SelectionAdapter( ) {

			public void widgetSelected( SelectionEvent e )
			{
				BusyIndicator.showWhile( Display.getDefault( ),
						new Runnable( ) {

							public void run( )
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
													+ " changes -t -l -m 300 //"
													+ data.getCurrentIportalViewerData( )
															.getView( )
													+ "/..." );

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
													buffer.append( line[0]
															+ "\r\n" );
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
									IOUtils.copy( p4Process.getInputStream( ),
											output );
									p4Process.waitFor( );

									Thread.sleep( 100 );

									if ( !error[0] )
									{
										ChangelistDialog dialog = new ChangelistDialog( UIUtil.getShell( ) );
										dialog.setInput( output.toString( ) );
										if ( dialog.open( ) == Dialog.OK )
										{
											if ( dialog.getResult( ) != null )
											{
												changelistText.setText( dialog.getResult( ) );
											}
											else
											{
												changelistText.setText( "" );
											}
										};
									}
								}
								catch ( Exception e )
								{
									MessageDialog.openError( null,
											"Error",
											e.getMessage( ) );
								}

							}
						} );
			}
		} );
		gd = new GridData( );
		gd.verticalAlignment = SWT.CENTER;
		gd.grabExcessVerticalSpace = true;
		browseButton.setLayoutData( gd );
		specifyContainer.layout( );
		specifyContainer.getParent( ).layout( );
		specifyContainer.getParent( ).getParent( ).layout( );
	}

	protected void selectRevisionChoice( )
	{
		disposeSpecifyContainerChildren( );
		final Text revisionText = new Text( specifyContainer, SWT.BORDER );
		GridData gd = new GridData( );
		gd.widthHint = 200;
		gd.verticalAlignment = SWT.CENTER;
		gd.grabExcessVerticalSpace = true;
		revisionText.setLayoutData( gd );

		revisionText.addModifyListener( new ModifyListener( ) {

			public void modifyText( ModifyEvent e )
			{
				if ( revisionText.getText( ).trim( ).length( ) > 0 )
					data.getCurrentIportalViewerData( ).setRevisionArg( "#"
							+ revisionText.getText( ).trim( ) );
				else
					data.getCurrentIportalViewerData( ).setRevisionArg( null );
			}
		} );

		revisionText.addVerifyListener( new VerifyListener( ) {

			public void verifyText( VerifyEvent ve )
			{
				if ( ve.character == SWT.BS
						|| ve.keyCode == SWT.ARROW_LEFT
						|| ve.keyCode == SWT.ARROW_RIGHT
						|| ve.keyCode == SWT.DEL )
				{
					ve.doit = true;
					return;
				}
				if ( ve.character == '\0' )
				{
					try
					{
						if ( ve.text.trim( ).length( ) == 0
								|| Integer.parseInt( ve.text.trim( ) ) > 0 )
						{
							ve.doit = true;
							return;
						}
						else
						{
							ve.doit = false;
							return;
						}
					}
					catch ( NumberFormatException e )
					{
						ve.doit = false;
						return;
					}
				}
				if ( !( '0' <= ve.character && ve.character <= '9' ) )
				{
					ve.doit = false;
					return;
				}
			}
		} );
		Label revisionLabel = new Label( specifyContainer, SWT.NONE );
		revisionLabel.setText( "Enter revision number" );
		gd = new GridData( );
		gd.verticalAlignment = SWT.CENTER;
		gd.grabExcessVerticalSpace = true;
		revisionLabel.setLayoutData( gd );
		specifyContainer.layout( );
		specifyContainer.getParent( ).layout( );
		specifyContainer.getParent( ).getParent( ).layout( );
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
				&& data.getCurrentIportalViewerData( ).getCustomProjectName( ) != null
				&& data.getCurrentIportalViewerData( )
						.getCustomProjectName( )
						.trim( )
						.length( ) > 0 )
		{
			customProjectName.setText( data.getCurrentIportalViewerData( )
					.getCustomProjectName( )
					.trim( ) );
		}
		else
		{
			customProjectName.setText( "" );
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
			txtServer.setText( "p4.actuate.com:1666" );
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
			comboClient.setText( data.getCurrentIportalViewerData( )
					.getClient( )
					.trim( ) );
		}
		else
		{
			comboClient.setText( "" );
		}

		if ( data != null )
		{
			forceButton.setSelection( data.getCurrentIportalViewerData( )
					.isForceOperation( ) );
			revertButton.setSelection( data.getCurrentIportalViewerData( )
					.isRevertFiles( ) );
			skipSyncButton.setSelection( data.getCurrentIportalViewerData( )
					.isSkipSync( ) );
		}
		else
		{
			forceButton.setSelection( false );
			revertButton.setSelection( false );
			skipSyncButton.setSelection( false );
		}

		setPageComplete( isPageComplete( ) );
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
				if ( file.getAbsolutePath( )
						.startsWith( PathConfig.getProperty( PathConfig.HQ_RELEASE_ACTUATE_BUILD_DIR ) ) )
				{
					String path = file.getAbsolutePath( );
					String[] tokens = path.split( "\\\\" );
					if ( tokens.length > 3 )
						comboFiles.add( tokens[tokens.length - 3] );
				}
				else if ( file.getAbsolutePath( )
						.startsWith( PathConfig.getProperty( PathConfig.HQ_PROJECT_VIEWER_WAR_DIR ) ) )
				{
					String path = file.getAbsolutePath( );
					String[] tokens = path.split( "\\\\" );
					if ( tokens.length > 6 )
						comboFiles.add( tokens[tokens.length - 6] );
				}
				else
				{
					comboFiles.add( file.getName( ) );
				}
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
			if ( comboFiles.indexOf( comboFiles.getText( ) ) == -1 )
			{
				data.getCurrentIportalViewerData( )
						.setBirtViewerFile( comboFiles.getText( ) );
			}
			else
				data.getCurrentIportalViewerData( )
						.setBirtViewerFile( data.getIportalViewMap( )
								.get( comboProjects.getText( ) )
								.get( comboFiles.indexOf( comboFiles.getText( ) ) )
								.getAbsolutePath( ) );
		}
		setPageComplete( isPageComplete( ) );
	}

	private void checkStatus( )
	{
		btnTest.setEnabled( txtServer.getText( ).trim( ).length( ) > 0
				&& txtUser.getText( ).trim( ).length( ) > 0
				&& txtPassword.getText( ).trim( ).length( ) > 0
				&& comboClient.getText( ).trim( ).length( ) > 0 );

		btnSearch.setEnabled( txtServer.getText( ).trim( ).length( ) > 0
				&& txtUser.getText( ).trim( ).length( ) > 0
				&& txtPassword.getText( ).trim( ).length( ) > 0 );

		btnCreate.setEnabled( txtServer.getText( ).trim( ).length( ) > 0
				&& txtUser.getText( ).trim( ).length( ) > 0
				&& txtPassword.getText( ).trim( ).length( ) > 0 );

		btnDelete.setEnabled( btnTest.isEnabled( ) );

		if ( comboFiles.indexOf( comboFiles.getText( ) ) == -1 )
		{
			if ( !new File( comboFiles.getText( ) ).exists( ) )
			{
				setErrorMessage( "The path of iPortal Viewer file is invalid." );
				return;
			}
		}

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

		if ( comboClient.getText( ).trim( ).length( ) == 0 )
		{
			setErrorMessage( "Must specify client." );
			return;
		}

		setErrorMessage( null );
		return;
	}

	public void propertyChange( PropertyChangeEvent event )
	{
		if ( ToolkitConstants.CURRENT_IV_PROJECT.equals( event.getProperty( ) ) )
			initPage( );
	}

	private void getClients( final List<String> clients, final boolean[] error )
	{
		BusyIndicator.showWhile( Display.getDefault( ), new Runnable( ) {

			public void run( )
			{
				try
				{
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

					if ( !error[0] )
					{

						Pattern pattern = Pattern.compile( "(?i)Client\\s+\\S+",
								Pattern.CASE_INSENSITIVE );
						Matcher matcher = pattern.matcher( output.toString( ) );

						while ( matcher.find( ) )
						{
							String client = matcher.group( )
									.replaceAll( "(?i)Client\\s+", "" );
							clients.add( client.trim( ) );
						}
					}
				}
				catch ( Exception e )
				{
					MessageDialog.openError( null, "Error", e.getMessage( ) );
				}
			}
		} );
	}

	private void updateClientSpecification( final File specFile,
			final String client, final String root )
	{
		BusyIndicator.showWhile( Display.getDefault( ), new Runnable( ) {

			public void run( )
			{
				final String[] originRoot = new String[1];
				final String[] originClient = new String[1];

				final boolean[] error = new boolean[1];
				final String[] errorMessage = new String[1];
				try
				{
					final Process downloadProcess = Runtime.getRuntime( )
							.exec( new String[]{
									"cmd",
									"/c",
									"p4 -p "
											+ txtServer.getText( )
											+ " -u "
											+ txtUser.getText( )
											+ " -P "
											+ txtPassword.getText( )
											+ " client -o "
											+ ">"
											+ "\""
											+ specFile.getAbsolutePath( )
											+ "\""
							} );

					Thread thread = new Thread( ) {

						public void run( )
						{
							try
							{
								BufferedReader input = new BufferedReader( new InputStreamReader( downloadProcess.getErrorStream( ) ) );
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
									errorMessage[0] = buffer.toString( );
								}
							}
							catch ( Exception e )
							{
								Logger.getLogger( SyncIPortalWorkspace.class.getName( ) )
										.log( Level.WARNING,
												"Get error stream failed.", e ); //$NON-NLS-1$
							}
						}
					};
					thread.setDaemon( true );
					thread.start( );

					downloadProcess.waitFor( );

					Thread.sleep( 100 );

					if ( errorMessage[0] != null )
					{
						MessageDialog.openError( null, "Error", errorMessage[0] );
					}
					else
					{
						if ( specFile.exists( ) )
						{
							Pattern pattern = Pattern.compile( "(?i)\n\\s*Client:\\s+\\S+" );
							Matcher matcher = pattern.matcher( FileUtil.getContent( specFile ) );
							if ( matcher.find( ) )
							{
								originClient[0] = matcher.group( )
										.trim( )
										.split( "\\s+" )[1];
							}

							if ( originClient[0] != null )
							{
								FileUtil.replaceFile( specFile,
										"(?i)Client:\\s+\\S+",
										"Client:	" + client );
							}

							pattern = Pattern.compile( "(?i)\n\\s*Root:\\s+\\S+" );
							matcher = pattern.matcher( FileUtil.getContent( specFile ) );
							if ( matcher.find( ) )
							{
								originRoot[0] = matcher.group( )
										.trim( )
										.split( "\\s+" )[1];
							}

							if ( originRoot[0] != null
									&& originClient[0] != null )
							{
								FileUtil.replaceFile( specFile,
										"(?i)Root:\\s+\\S+",
										"Root:	" + root );
								FileUtil.replaceFile( specFile,
										"(?i)Client:\\s+\\S+",
										"Client:	" + client );
								FileUtil.replaceFile( specFile, "(?i)//"
										+ Pattern.quote( originClient[0] )
										+ "/", "//" + client + "/" );
							}
							else
							{
								MessageDialog.openError( null,
										"Error",
										"Parse the client workspace specification file failed." );
								return;
							}

							final Process uploadProcess = Runtime.getRuntime( )
									.exec( new String[]{
											"cmd",
											"/c",
											"p4 -p "
													+ txtServer.getText( )
													+ " -u "
													+ txtUser.getText( )
													+ " -P "
													+ txtPassword.getText( )
													+ " client -i "
													+ "<"
													+ "\""
													+ specFile.getAbsolutePath( )
													+ "\""
									} );

							thread = new Thread( ) {

								public void run( )
								{
									try
									{
										BufferedReader input = new BufferedReader( new InputStreamReader( uploadProcess.getErrorStream( ) ) );
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
											errorMessage[0] = buffer.toString( );
										}
									}
									catch ( Exception e )
									{
										Logger.getLogger( SyncIPortalWorkspace.class.getName( ) )
												.log( Level.WARNING,
														"Get error stream failed.", e ); //$NON-NLS-1$
									}
								}
							};
							thread.setDaemon( true );
							thread.start( );
							uploadProcess.waitFor( );
							Thread.sleep( 100 );
							if ( errorMessage[0] != null )
							{
								MessageDialog.openError( null,
										"Error",
										errorMessage[0] );
							}
						}
						else
						{
							MessageDialog.openError( null,
									"Error",
									"Get the client workspace specification file failed." );
						}
					}
				}
				catch ( Exception e )
				{
					MessageDialog.openError( null, "Error", e.getMessage( ) );
				}
			}
		} );
	}

	private String getClientRoot( final File specFile, final String client )
	{
		final String[] originRoot = new String[1];

		final boolean[] error = new boolean[1];
		final String[] errorMessage = new String[1];
		try
		{
			final Process downloadProcess = Runtime.getRuntime( )
					.exec( new String[]{
							"cmd",
							"/c",
							"p4 -p "
									+ txtServer.getText( )
									+ " -u "
									+ txtUser.getText( )
									+ " -P "
									+ txtPassword.getText( )
									+ " client "
									+ " -o "
									+ client
									+ ">"
									+ "\""
									+ specFile.getAbsolutePath( )
									+ "\""
					} );

			Thread thread = new Thread( ) {

				public void run( )
				{
					try
					{
						BufferedReader input = new BufferedReader( new InputStreamReader( downloadProcess.getErrorStream( ) ) );
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
							errorMessage[0] = buffer.toString( );
						}
					}
					catch ( Exception e )
					{
						Logger.getLogger( SyncIPortalWorkspace.class.getName( ) )
								.log( Level.WARNING,
										"Get error stream failed.", e ); //$NON-NLS-1$
					}
				}
			};
			thread.setDaemon( true );
			thread.start( );

			downloadProcess.waitFor( );

			Thread.sleep( 100 );

			if ( errorMessage[0] != null )
			{
				MessageDialog.openError( null, "Error", errorMessage[0] );
			}
			else
			{
				if ( specFile.exists( ) )
				{

					Pattern pattern = Pattern.compile( "(?i)\n\\s*Root:\\s+\\S+" );
					Matcher matcher = pattern.matcher( FileUtil.getContent( specFile ) );
					if ( matcher.find( ) )
					{
						originRoot[0] = matcher.group( ).trim( ).split( "\\s+" )[1];
					}

					if ( originRoot[0] != null )
					{
						return originRoot[0];
					}
					else
					{
						MessageDialog.openError( null,
								"Error",
								"Parse the client workspace specification file failed." );
					}
				}
				else
				{
					MessageDialog.openError( null,
							"Error",
							"Get the client workspace specification file failed." );
				}
			}
		}
		catch ( Exception e )
		{
			MessageDialog.openError( null, "Error", e.getMessage( ) );
		}
		return null;
	}

	private void handleClientSelectionEvent( )
	{
		BusyIndicator.showWhile( Display.getDefault( ), new Runnable( ) {

			public void run( )
			{
				String project = comboClient.getText( );
				if ( comboClient.getSelectionIndex( ) != -1 )
					project = comboClient.getItem( comboClient.getSelectionIndex( ) );
				if ( project == null || project.trim( ).length( ) == 0 )
					return;
				if ( rootMap.containsKey( project ) )
				{
					txtRoot.setText( rootMap.get( project ) );
				}
				else
				{
					String root = getClientRoot( FileUtil.getTempFile( "specification.txt" ),
							project );
					if ( root != null )
					{
						rootMap.put( project, root );
						txtRoot.setText( root );
					}
					else
					{
						txtRoot.setText( "" );
					}
				}
				setPageComplete( isPageComplete( ) );
			}
		} );
	}

	private void resetChoiceStatus( )
	{
		choiceCombo.setEnabled( specifyRivision.getSelection( ) );
		enableSpecifyContainer( specifyRivision.getSelection( ) );
	}

}
