
package com.actuate.tool.development.installer.wizard;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.DialogSettings;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.sf.feeling.swt.win32.internal.extension.util.ImageCache;

import com.actuate.tool.development.installer.model.InstallBRDProData;
import com.actuate.tool.development.installer.model.InstallData;
import com.actuate.tool.development.installer.model.InstallType;
import com.actuate.tool.development.installer.model.Module;
import com.actuate.tool.development.installer.model.Modules;
import com.actuate.tool.development.installer.task.CloneWorkspaceSettings;
import com.actuate.tool.development.installer.task.InstallBRDPro;
import com.actuate.tool.development.installer.util.LogUtil;
import com.actuate.tool.development.installer.util.UIUtil;

public class InstallWizard extends Wizard
{

	private static final String MAXIMIZED = "Maximized";

	private static final String HEIGHT = "Height";

	private static final String WIDTH = "Width";

	private static final String POS_Y = "PosY";

	private static final String POS_X = "PosX";

	public static final String PROJECT = "Project";

	public static final String CURRENT_BRDPRO_PROJECT = "CurrentBRDProProject";

	public static final String CURRENT_IV_PROJECT = "CurrentIVProject";

	public static final String DIRECTORY = "Directory";

	public static final String MODULES = "Modules";

	public static final String CLEARDIRECTORY = "ClearDirectory";

	public static final String CLOSEBRDPRO = "CloseBRDPro";

	public static final String CREATESHORTCUT = "CreateShortcut";

	public static final String SHORTCUTARGUMENTS = "ShortcutArguments";

	static final String DIALOG_SETTING_FILE = new File( System.getProperties( )
			.getProperty( "user.home" )
			+ File.separator
			+ "\\.brdpro_installer\\userInfo.xml" ).getAbsolutePath( );

	// the model object.
	InstallData data = new InstallData( );

	public InstallWizard( )
	{
		setWindowTitle( "Actuate BRDPro Development Environment Installer" );
		setNeedsProgressMonitor( true );

		final DialogSettings dialogSettings = new DialogSettings( "userInfo" );
		try
		{
			if ( new File( DIALOG_SETTING_FILE ).exists( ) )
				dialogSettings.load( DIALOG_SETTING_FILE );

		}
		catch ( IOException e )
		{
			LogUtil.recordErrorMsg( e, false );
		}

		setDialogSettings( dialogSettings );

		IDialogSettings[] settings = getDialogSettings( ).getSections( );
		if ( settings != null )
		{
			for ( IDialogSettings setting : settings )
			{
				InstallBRDProData installData = new InstallBRDProData( );
				data.addInstallBRDProData( installData );
				installData.setProject( setting.getName( ) );
				if ( setting.get( DIRECTORY ) != null )
				{
					installData.setDirectory( setting.get( DIRECTORY ) );
				}
				if ( setting.get( MODULES ) != null
						&& setting.get( MODULES ).trim( ).length( ) > 0 )
				{
					String[] moduleNames = setting.get( MODULES ).split( ";" );
					Module[] modules = new Module[moduleNames.length];
					for ( int i = 0; i < modules.length; i++ )
					{
						modules[i] = Modules.getInstance( )
								.valueOf( moduleNames[i] );
					}
					installData.setModules( modules );
				}
				if ( setting.get( CLEARDIRECTORY ) != null )
				{
					installData.setNotClearDirectory( setting.getBoolean( CLEARDIRECTORY ) );
				}
				if ( setting.get( CLOSEBRDPRO ) != null )
				{
					installData.setNotCloseBRDPro( setting.getBoolean( CLOSEBRDPRO ) );
				}
				if ( setting.get( SHORTCUTARGUMENTS ) != null )
				{
					installData.setShortcutArguments( setting.get( SHORTCUTARGUMENTS ) );
				}
				if ( setting.get( CREATESHORTCUT ) != null )
				{
					installData.setNotCreateShortcut( setting.getBoolean( CREATESHORTCUT ) );
				}

			}
		}

		if ( getDialogSettings( ).get( CURRENT_BRDPRO_PROJECT ) != null )
		{
			data.setCurrentBRDProProject( getDialogSettings( ).get( CURRENT_BRDPRO_PROJECT ) );
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.wizard.IWizard#addPages()
	 */
	public void addPages( )
	{
		initShell( );

		addPage( new InstallTypePage( data ) );
		addPage( new CloneWorkspaceSettingsPage( data ) );
		addPage( new BRDProProjectPage( data ) );
		addPage( new SettingPage( data ) );
		addPage( new ShortcutPage( data ) );
		addPage( new IPortalViewerProjectPage( data ) );
		addPage( new P4ConnectionSettingPage( data ) );
	}

	private void initShell( )
	{
		UIUtil.setShell( this.getShell( ) );
		this.getShell( ).setImages( new Image[]{
				ImageCache.getImage( "/icons/actuate_16.png" ),
				ImageCache.getImage( "/icons/actuate_32.png" ),
				ImageCache.getImage( "/icons/actuate_48.png" )
		} );
		if ( getDialogSettings( ).get( WIDTH ) != null
				&& getDialogSettings( ).get( HEIGHT ) != null )
			this.getShell( ).setSize( getDialogSettings( ).getInt( WIDTH ),
					getDialogSettings( ).getInt( HEIGHT ) );
		else
		{
			this.getShell( ).setSize( 600, 550 );
		}
		if ( getDialogSettings( ).get( POS_X ) != null
				&& getDialogSettings( ).get( POS_Y ) != null )
			this.getShell( ).setLocation( getDialogSettings( ).getInt( POS_X ),
					getDialogSettings( ).getInt( POS_Y ) );
		else
		{
			int width = this.getShell( ).getMonitor( ).getClientArea( ).width;
			int height = this.getShell( ).getMonitor( ).getClientArea( ).height;
			int x = this.getShell( ).getSize( ).x;
			int y = this.getShell( ).getSize( ).y;
			this.getShell( )
					.setLocation( ( width - x ) / 2, ( height - y ) / 2 );
		}
		if ( getDialogSettings( ).get( MAXIMIZED ) != null )
			this.getShell( )
					.setMaximized( getDialogSettings( ).getBoolean( MAXIMIZED ) );

		this.getShell( ).addControlListener( new ControlListener( ) {

			public void controlMoved( ControlEvent e )
			{
				collectShellInfos( );

			}

			public void controlResized( ControlEvent e )
			{
				collectShellInfos( );
			}

			private void collectShellInfos( )
			{
				if ( !InstallWizard.this.getShell( ).getMaximized( )
						&& !InstallWizard.this.getShell( ).getMinimized( )
						&& InstallWizard.this.getShell( ).getVisible( ) )
				{
					Point location = InstallWizard.this.getShell( )
							.getLocation( );
					Point size = InstallWizard.this.getShell( ).getSize( );
					getDialogSettings( ).put( POS_X, location.x );
					getDialogSettings( ).put( POS_Y, location.y );
					getDialogSettings( ).put( WIDTH, size.x );
					getDialogSettings( ).put( HEIGHT, size.y );
				}

				if ( InstallWizard.this.getShell( ).getMaximized( ) )
				{
					getDialogSettings( ).put( MAXIMIZED, true );
				}
				else
				{
					getDialogSettings( ).put( MAXIMIZED, false );
				}
			}

		} );

		this.getShell( ).addDisposeListener( new DisposeListener( ) {

			public void widgetDisposed( DisposeEvent e )
			{
				saveInstallerSettings( );
			}

		} );

		try
		{
			new ProgressMonitorDialog( getShell( ) ).run( true,
					true,
					new IRunnableWithProgress( ) {

						public void run( final IProgressMonitor monitor )
								throws InvocationTargetException,
								InterruptedException
						{
							monitor.beginTask( "Collecting Actuate Build Projects...",
									IProgressMonitor.UNKNOWN );
							final String[][] fileNames = new String[1][];

							// File file = new File( "E:\\zip" );
							File file = new File( "\\\\qaant\\ActuateBuild" );
							fileNames[0] = file.list( new FilenameFilter( ) {

								public boolean accept( File dir, String name )
								{
									File file = new File( dir, name );
									monitor.subTask( "Scanning "
											+ file.getAbsolutePath( ) );
									List<File> brdproFiles = new ArrayList<File>( );
									List<File> iportalViewerFiles = new ArrayList<File>( );

									// if ( !name.equalsIgnoreCase(
									// "A11SP4" )
									// && !name.equalsIgnoreCase(
									// "BRDPRo_Bug_Fix" ) )
									// return false;
									checkActuateBuildFile( file,
											brdproFiles,
											iportalViewerFiles );

									if ( brdproFiles.size( ) > 0 )
									{
										if ( !data.getBrdproMap( )
												.containsKey( name ) )
											data.getBrdproMap( ).put( name,
													new ArrayList<File>( ) );
										data.getBrdproMap( )
												.get( name )
												.addAll( brdproFiles );
									}
									if ( iportalViewerFiles.size( ) > 0 )
									{
										if ( !data.getIportalViewMap( )
												.containsKey( name ) )
											data.getIportalViewMap( )
													.put( name,
															new ArrayList<File>( ) );
										data.getIportalViewMap( )
												.get( name )
												.addAll( iportalViewerFiles );
									}
									if ( brdproFiles.size( ) > 0
											|| iportalViewerFiles.size( ) > 0 )
										return true;
									return false;
								}

							} );

							monitor.done( );
						}
					} );
		}
		catch ( Exception e )
		{
			LogUtil.recordErrorMsg( e, false );
		}
	}

	private void checkActuateBuildFile( File file,
			final List<File> brdproFiles, final List<File> iportalViewerFiles )
	{
		File[] children = file.listFiles( new FileFilter( ) {

			public boolean accept( File file )
			{
				String fileName = file.getName( ).toLowerCase( );
				if ( file.isFile( ) )
				{
					if ( fileName.startsWith( "actuatebirtdesignerprofessional" )
							|| fileName.startsWith( "brdpro" ) )
					{
						brdproFiles.add( file );
					}
					if ( fileName.startsWith( "actuatebirtviewer" ) )
					{
						iportalViewerFiles.add( file );
					}
				}
				else if ( file.getName( ).equalsIgnoreCase( "BRDPro" ) )
				{
					return true;
				}
				else if ( file.getName( )
						.equalsIgnoreCase( "ActuateBirtViewer" ) )
				{
					return true;
				}
				return false;
			}

		} );

		if ( children != null )
		{
			for ( int i = 0; i < children.length; i++ )
			{
				checkActuateBuildFile( children[i],
						brdproFiles,
						iportalViewerFiles );
			}
		}
	}

	public IWizardPage getNextPage( IWizardPage page )
	{
		List<IWizardPage> pages = Arrays.asList( this.getPages( ) );
		int index = pages.indexOf( page );
		if ( index == pages.size( ) - 1 || index == -1 )
		{
			return null;
		}
		if ( index == 0 )
		{
			if ( data.getInstallType( ) == InstallType.installBRDPro )
				return pages.get( 2 );
			else if ( data.getInstallType( ) == InstallType.synciPortalWorkspace )
				return pages.get( 5 );
			else
				return pages.get( 1 );
		}
		else if ( index == 1 )
			return null;
		else if ( index == 4 )
			return null;
		else
			return (IWizardPage) pages.get( index + 1 );
	}

	// public IWizardPage getPreviousPage( IWizardPage page )
	// {
	// int index = pages.indexOf( page );
	// if ( index == 0 || index == -1 )
	// {
	// // first page or page not found
	// return null;
	// }
	// return (IWizardPage) pages.get( index - 1 );
	// }

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.wizard.IWizard#performFinish()
	 */
	public boolean performFinish( )
	{
		if ( getDialogSettings( ) != null )
		{
			saveInstallerSettings( );
		}

		try
		{
			// puts the data into a database ...
			getContainer( ).run( true, true, new IRunnableWithProgress( ) {

				public void run( IProgressMonitor monitor )
						throws InvocationTargetException, InterruptedException
				{

					if ( data != null )
					{
						if ( data.getInstallType( ) == InstallType.installBRDPro )
						{
							new InstallBRDPro( data.getCurrentInstallBRDProData( ) ).execute( monitor );
						}
						else if ( data.getInstallType( ) == InstallType.cloneWorkspaceSettings )
						{
							new CloneWorkspaceSettings( data ).execute( monitor );
						}
					}
					monitor.done( );
				}
			} );
		}
		catch ( Exception e )
		{
			LogUtil.recordErrorMsg( e, true );
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.wizard.IWizard#performCancel()
	 */
	public boolean performCancel( )
	{
		boolean ans = MessageDialog.openConfirm( getShell( ),
				"Confirmation",
				"Are you sure to cancel the task?" );
		if ( ans )
			return true;
		else
			return false;
	}

	private void saveDialogSettings( )
	{
		try
		{
			File file = new File( DIALOG_SETTING_FILE );
			if ( !file.exists( ) )
			{
				if ( !file.getParentFile( ).exists( ) )
					file.getParentFile( ).mkdirs( );
			}
			getDialogSettings( ).save( DIALOG_SETTING_FILE );
		}
		catch ( IOException e1 )
		{
			LogUtil.recordErrorMsg( e1, false );
		}
	}

	private void saveInstallerSettings( )
	{
		if ( data.getCurrentBRDProProject( ) != null )
		{
			getDialogSettings( ).put( CURRENT_BRDPRO_PROJECT,
					data.getCurrentBRDProProject( ) );
			IDialogSettings projectSetting = getDialogSettings( ).getSection( data.getCurrentBRDProProject( ) );
			if ( projectSetting == null )
			{
				projectSetting = getDialogSettings( ).addNewSection( data.getCurrentBRDProProject( ) );
			}

			projectSetting.put( DIRECTORY, data.getCurrentInstallBRDProData( )
					.getDirectory( ) );
			projectSetting.put( CLEARDIRECTORY,
					data.getCurrentInstallBRDProData( ).isNotClearDirectory( ) );
			projectSetting.put( CLOSEBRDPRO, data.getCurrentInstallBRDProData( )
					.isNotCloseBRDPro( ) );
			projectSetting.put( CREATESHORTCUT,
					data.getCurrentInstallBRDProData( ).isNotCreateShortcut( ) );
			projectSetting.put( SHORTCUTARGUMENTS,
					data.getCurrentInstallBRDProData( ).getShortcutArguments( ) );
			if ( data.getCurrentInstallBRDProData( ).getModules( ) != null )
			{
				StringBuffer buffer = new StringBuffer( );
				for ( int i = 0; i < data.getCurrentInstallBRDProData( )
						.getModules( ).length; i++ )
				{
					Module module = data.getCurrentInstallBRDProData( )
							.getModules( )[i];
					if ( module != null )
					{
						buffer.append( module.getName( ) );
						if ( i < data.getCurrentInstallBRDProData( )
								.getModules( ).length - 1 )
							buffer.append( ";" );
					}
				}
				projectSetting.put( MODULES, buffer.toString( ) );
			}
			else
			{
				projectSetting.put( MODULES, (String) null );
			}
		}
		saveDialogSettings( );
	}
}
