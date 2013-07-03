
package com.actuate.development.tool.task;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuDetectEvent;
import org.eclipse.swt.events.MenuDetectListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tray;
import org.eclipse.swt.widgets.TrayItem;
import org.sf.feeling.swt.win32.extension.shell.Windows;
import org.sf.feeling.swt.win32.internal.extension.util.ImageCache;

import com.actuate.development.tool.config.PathConfig;
import com.actuate.development.tool.model.Version;
import com.actuate.development.tool.model.feature.SyncBRDProResourcesData;
import com.actuate.development.tool.util.FileUtil;
import com.actuate.development.tool.util.UIUtil;

public class SyncBRDProResources implements ITaskWithMonitor
{

	private static final String ECLIPSE_SDK = PathConfig.getProperty( PathConfig.ECLIPSE_SDK,
			"(?i)eclipse.+SDK.+win32\\.zip" );
	private static final String EMF_SDK = PathConfig.getProperty( PathConfig.EMF_SDK,
			"(?i)emf.+SDK.+\\.zip" );
	private static final String GEF_SDK = PathConfig.getProperty( PathConfig.GEF_SDK,
			"(?i)GEF.+ALL.+\\.zip" );
	private static final String WTP_SDK = PathConfig.getProperty( PathConfig.WTP_SDK,
			"(?i)wtp.+sdk.+\\.zip" );

	private SyncBRDProResourcesData data;
	private long allLength;
	private long currentLength;
	private boolean monitorAntProcess;
	private Process javaProcess;
	private Tray tray;
	private Shell shell;
	private TrayItem trayItem;

	public SyncBRDProResources( SyncBRDProResourcesData data )
	{
		this.data = data;
	}

	public void execute( IProgressMonitor monitor )
	{
		if ( data == null )
			return;

		if ( data.isMinimizeToolkit( ) )
		{
			Display.getDefault( ).syncExec( new Runnable( ) {

				public void run( )
				{
					createSystemTray( );
				}
			} );
		}

		startMonitorProcess( monitor );

		monitor.beginTask( "Synchronizing files", 10000 );

		Version[] versions = data.getPlatformVersions( );
		List<String> ignoreVersions = new ArrayList<String>( );
		if ( data.getIgnorePlatformVersions( ) != null )
		{
			ignoreVersions.addAll( Arrays.asList( data.getIgnorePlatformVersions( ) ) );
		}

		final List<String> syncVersions = new ArrayList<String>( );
		for ( int i = 0; i < versions.length; i++ )
		{
			if ( !ignoreVersions.contains( versions[i].getValue( ) ) )
				syncVersions.add( versions[i].getValue( ) );
		}

		allLength = 0L;
		File platformDir = new File( "\\\\QA-BUILD\\BIRTOutput\\platform" );
		final Map<String, List<File>> sdkFiles = new HashMap<String, List<File>>( );
		listSDKFiles( syncVersions, platformDir, sdkFiles, monitor );

		if ( monitor.isCanceled( ) )
		{
			cancelTask( monitor );
			return;
		}

		final List<String> pluginVersions = new ArrayList<String>( );
		if ( data.getPluginVersions( ) != null )
		{
			pluginVersions.addAll( Arrays.asList( data.getPluginVersions( ) ) );
		}

		File pluginDir = new File( "\\\\qaant\\QA\\Toolkit\\plugins" );
		final File[] pluginFiles = listToolkitPlugins( pluginVersions,
				pluginDir,
				monitor );

		if ( monitor.isCanceled( ) )
		{
			cancelTask( monitor );
			return;
		}

		File targetDir = new File( data.getTargetDirectory( ) );
		if ( !targetDir.exists( ) )
			targetDir.mkdirs( );
		{
			copySDKFiles( platformDir, sdkFiles, monitor );
			copyToolkitPluginFiles( pluginDir, pluginFiles, monitor );
		}

		if ( data.isMinimizeToolkit( ) )
		{
			shell.getDisplay( ).syncExec( new Runnable( ) {

				public void run( )
				{
					if ( shell.getMinimized( ) || !shell.isVisible( ) )
						toggleDisplay( shell, tray );
				}
			} );
		}

		if ( !monitor.isCanceled( ) )
		{
			finishTask( monitor );
		}
		else
		{
			cancelTask( monitor );
		}
	}

	private void createSystemTray( )
	{
		shell = UIUtil.getShell( );
		tray = shell.getDisplay( ).getSystemTray( );
		trayItem = new TrayItem( tray, SWT.NONE );
		trayItem.addSelectionListener( new SelectionAdapter( ) {

			public void widgetSelected( SelectionEvent e )
			{
				toggleDisplay( shell, tray );
			}
		} );

		final Menu trayMenu = new Menu( shell, SWT.POP_UP );
		MenuItem showMenuItem = new MenuItem( trayMenu, SWT.PUSH );
		showMenuItem.setText( "&Show Window" );

		showMenuItem.addSelectionListener( new SelectionAdapter( ) {

			public void widgetSelected( SelectionEvent event )
			{
				toggleDisplay( shell, tray );
			}
		} );

		trayMenu.setDefaultItem( showMenuItem );

		trayItem.addMenuDetectListener( new MenuDetectListener( ) {

			public void menuDetected( MenuDetectEvent e )
			{
				trayMenu.setVisible( true );
			}
		} );

		trayItem.setImage( ImageCache.getImage( "/icons/actuate_16.png" ) );

		shell.addShellListener( new ShellAdapter( ) {

			public void shellIconified( ShellEvent e )
			{
				toggleDisplay( shell, tray );
			}
		} );

		toggleDisplay( shell, tray );
	}

	protected void toggleDisplay( Shell shell, Tray tray )
	{
		shell.setVisible( !shell.isVisible( ) );
		tray.getItem( 0 ).setVisible( !shell.isVisible( ) );
		if ( shell.getVisible( ) )
		{
			shell.setMinimized( false );
			shell.setActive( );
		}
	}

	private void cancelTask( final IProgressMonitor monitor )
	{
		monitorAntProcess = false;
		monitor.subTask( "" );
		monitor.setTaskName( "Canceled synchronizing the BRDPro resource files" );
		Display.getDefault( ).syncExec( new Runnable( ) {

			public void run( )
			{
				MessageDialog.openInformation( null,
						"Information",
						"Canceled synchronizing the BRDPro resource files." );
				Windows.flashWindow( UIUtil.getShell( ).handle, false );
			}
		} );
	}

	private void finishTask( final IProgressMonitor monitor )
	{
		monitorAntProcess = false;
		monitor.subTask( "" );
		monitor.setTaskName( "Finished synchronizing the BRDPro resource files" );

		Display.getDefault( ).syncExec( new Runnable( ) {

			public void run( )
			{
				if ( UIUtil.getShell( ).getMinimized( ) )
					Windows.flashWindow( UIUtil.getShell( ).handle, true );
				StringBuffer buffer = new StringBuffer( );
				buffer.append( "Synchronize the BRDPro resource files sucessfully." );
				MessageDialog.openInformation( null,
						"Information",
						buffer.toString( ) );
				Windows.flashWindow( UIUtil.getShell( ).handle, false );
			}
		} );
	}

	private void copyToolkitPluginFiles( File pluginDir,
			final File[] pluginFiles, IProgressMonitor monitor )
	{
		if ( monitor.isCanceled( ) )
			return;
		File targetPlugins = new File( data.getTargetDirectory( ), "plugins" );

		List<File> movePluginFiles = new ArrayList<File>( );
		for ( int i = 0; i < pluginFiles.length; i++ )
		{
			if ( pluginFiles[i].isDirectory( ) )
				movePluginFiles.addAll( FileUtils.listFiles( pluginFiles[i],
						null,
						true ) );
			else
				movePluginFiles.add( pluginFiles[i] );
		}

		String pluginsPath = pluginDir.getAbsolutePath( );
		for ( int i = 0; i < movePluginFiles.size( ); i++ )
		{
			if ( monitor.isCanceled( ) )
				break;
			File sourceFile = movePluginFiles.get( i );
			if ( sourceFile.isDirectory( ) )
				continue;
			if ( !sourceFile.getName( )
					.toLowerCase( )
					.trim( )
					.endsWith( ".zip" ) )
				continue;
			String path = sourceFile.getParentFile( )
					.getAbsolutePath( )
					.substring( pluginsPath.length( ) );
			File parent = new File( targetPlugins, path );
			if ( !parent.exists( ) )
				parent.mkdirs( );
			File targetFile = new File( parent, sourceFile.getName( ) );
			if ( !targetFile.exists( )
					|| targetFile.length( ) != sourceFile.length( ) )
			{
				targetFile.delete( );

				final boolean[] flag = new boolean[]{
					false
				};
				try
				{
					monitorCopy( monitor,
							flag,
							sourceFile.getAbsolutePath( )
									.substring( sourceFile.getParentFile( )
											.getParentFile( )
											.getAbsolutePath( )
											.length( ) )
									.replace( '\\', '/' ),
							targetFile,
							sourceFile.length( ) );

					javaProcess = Runtime.getRuntime( ).exec( new String[]{
							System.getProperty( "java.home" ) + "/bin/java",
							"-cp",
							System.getProperty( "java.class.path" ),
							CopyZipFileTask.class.getName( ),
							"\"" + sourceFile.getAbsolutePath( ) + "\"",
							"\"" + targetFile.getAbsolutePath( ) + "\""
					} );

					StringBuffer errorMessage = new StringBuffer( );
					interruptJavaProcessErrorMessage( javaProcess, errorMessage );
					int result = javaProcess.waitFor( );
					if ( result == -1 )
					{
						Logger.getLogger( SyncBRDProResources.class.getName( ) )
								.log( Level.WARNING,
										"Copy file " + sourceFile.getAbsolutePath( ) + " failed.", //$NON-NLS-1$
										errorMessage );
					}
					javaProcess = null;

				}
				catch ( Exception e )
				{
					Logger.getLogger( SyncBRDProResources.class.getName( ) )
							.log( Level.WARNING,
									"Copy file " + sourceFile.getAbsolutePath( ) + " failed.", //$NON-NLS-1$
									e );
				}
				flag[0] = true;
				monitor.subTask( "" );
			}
			else
			{
				currentLength += targetFile.length( );
				monitor.worked( (int) ( targetFile.length( ) * 10000 / allLength ) );
				updateTooltip( );
			}
		}
		if ( !monitor.isCanceled( ) )
		{
			final boolean[] flag = new boolean[]{
				false
			};
			try
			{
				javaProcess = Runtime.getRuntime( ).exec( new String[]{
						System.getProperty( "java.home" ) + "/bin/java",
						"-cp",
						System.getProperty( "java.class.path" ),
						CopyResourcesTask.class.getName( ),
						"\"" + pluginsPath + "\"",
						"\"" + targetPlugins.getAbsolutePath( ) + "\""
				} );

				StringBuffer errorMessage = new StringBuffer( );
				interruptJavaProcessErrorMessage( javaProcess, errorMessage );
				int result = javaProcess.waitFor( );
				if ( result == -1 )
				{
					Logger.getLogger( SyncBRDProResources.class.getName( ) )
							.log( Level.WARNING,
									"Copy resource files " + pluginsPath + " failed.", //$NON-NLS-1$
									errorMessage );
				}
				javaProcess = null;

			}
			catch ( Exception e )
			{
				Logger.getLogger( SyncBRDProResources.class.getName( ) )
						.log( Level.WARNING,
								"Copy resource files " + pluginsPath + " failed.", //$NON-NLS-1$
								e );
			}
			flag[0] = true;
			monitor.subTask( "" );
			monitor.worked( 10000 );
		}
	}

	private void copySDKFiles( File platformDir,
			final Map<String, List<File>> sdkFiles, IProgressMonitor monitor )
	{
		File targetPlatform = new File( data.getTargetDirectory( ), "platform" );

		String platformPath = platformDir.getAbsolutePath( );
		Iterator<String> iter = sdkFiles.keySet( ).iterator( );
		while ( iter.hasNext( ) )
		{
			if ( monitor.isCanceled( ) )
				break;
			String version = iter.next( );
			File platformVersion = new File( targetPlatform, version );
			long targetLength = computeSDKFilesLength( platformVersion );

			List<File> sourceFiles = sdkFiles.get( version );
			long sourceLength = 0;
			Iterator<File> fileIter = sourceFiles.iterator( );
			while ( fileIter.hasNext( ) )
			{
				sourceLength += fileIter.next( ).length( );
			}

			if ( sourceLength != targetLength )
			{
				if ( platformVersion.exists( ) )
				{
					if ( platformVersion.isDirectory( ) )
					{
						try
						{
							FileUtils.deleteDirectory( platformVersion );
						}
						catch ( IOException e )
						{
							Logger.getLogger( SyncBRDProResources.class.getName( ) )
									.log( Level.WARNING,
											"Delete directory " + platformVersion.getAbsolutePath( ) + " failed.", //$NON-NLS-1$
											e );
						}
					}
					else
						platformVersion.delete( );
				}
				for ( int i = 0; i < sourceFiles.size( ); i++ )
				{
					if ( monitor.isCanceled( ) )
						break;
					File sourceFile = sourceFiles.get( i );
					String path = sourceFile.getParentFile( )
							.getAbsolutePath( )
							.substring( platformPath.length( ) );
					File parent = new File( targetPlatform, path );
					if ( !parent.exists( ) )
						parent.mkdirs( );
					File targetFile = new File( parent, sourceFile.getName( ) );
					if ( !targetFile.exists( )
							|| targetFile.length( ) != sourceFile.length( ) )
					{
						targetFile.delete( );
						final boolean[] flag = new boolean[]{
							false
						};
						try
						{
							monitorCopy( monitor,
									flag,
									sourceFile.getAbsolutePath( )
											.substring( sourceFile.getParentFile( )
													.getParentFile( )
													.getAbsolutePath( )
													.length( ) )
											.replace( '\\', '/' ),
									targetFile,
									sourceFile.length( ) );

							javaProcess = Runtime.getRuntime( )
									.exec( new String[]{
											System.getProperty( "java.home" )
													+ "/bin/java",
											"-cp",
											System.getProperty( "java.class.path" ),
											CopyZipFileTask.class.getName( ),
											"\""
													+ sourceFile.getAbsolutePath( )
													+ "\"",
											"\""
													+ targetFile.getAbsolutePath( )
													+ "\""
									} );

							StringBuffer errorMessage = new StringBuffer( );
							interruptJavaProcessErrorMessage( javaProcess,
									errorMessage );
							int result = javaProcess.waitFor( );
							if ( result == -1 )
							{
								Logger.getLogger( SyncBRDProResources.class.getName( ) )
										.log( Level.WARNING,
												"Copy file " + sourceFile.getAbsolutePath( ) + " failed.", //$NON-NLS-1$
												errorMessage );
							}
							javaProcess = null;

						}
						catch ( Exception e )
						{
							Logger.getLogger( SyncBRDProResources.class.getName( ) )
									.log( Level.WARNING,
											"Copy file " + sourceFile.getAbsolutePath( ) + " failed.", //$NON-NLS-1$
											e );
						}
						flag[0] = true;
						monitor.subTask( "" );
					}
					else
					{
						currentLength += targetFile.length( );
						monitor.worked( (int) ( targetFile.length( ) * 10000 / allLength ) );
						updateTooltip( );
					}
				}
			}
			else
			{
				currentLength += targetLength;
				monitor.worked( (int) ( targetLength * 10000 / allLength ) );
				updateTooltip( );
			}
		}
	}

	private void startMonitorProcess( final IProgressMonitor monitor )
	{
		monitorAntProcess = true;
		Thread thread = new Thread( "Monitor Process" ) {

			public void run( )
			{
				while ( monitorAntProcess )
				{
					if ( monitor.isCanceled( ) )
					{
						if ( javaProcess != null )
						{
							javaProcess.destroy( );
						}
					}
					try
					{
						Thread.sleep( 100 );
					}
					catch ( InterruptedException e )
					{
					}
				}
			}
		};
		thread.setDaemon( true );
		thread.start( );
	}

	private void interruptJavaProcessErrorMessage( final Process process,
			final StringBuffer buffer )
	{
		Thread errThread = new Thread( "Monitor Java Process" ) {

			public void run( )
			{
				try
				{
					BufferedReader input = new BufferedReader( new InputStreamReader( process.getErrorStream( ) ) );
					String line;
					while ( ( line = input.readLine( ) ) != null )
					{
						if ( buffer != null )
							buffer.append( line ).append( "\r\n" );
					}
					input.close( );
				}
				catch ( Exception e )
				{
				}
			}
		};
		errThread.setDaemon( true );
		errThread.start( );
	}

	private void monitorCopy( final IProgressMonitor monitor,
			final boolean[] flag, final String defaultTaskName,
			final File file, final long size )
	{
		Thread downloadThread = new Thread( "Monitor Synchronization" ) {

			public void run( )
			{
				long donwloadSize = file.length( );
				long time = System.currentTimeMillis( );
				while ( !flag[0] )
				{
					if ( file.exists( ) )
					{
						if ( file.length( ) != donwloadSize )
						{
							long increasement = file.length( ) - donwloadSize;
							String speed = FileUtil.format( ( (float) increasement )
									/ ( ( (float) ( System.currentTimeMillis( ) - time ) ) / 1000 ) )
									+ "/s";
							donwloadSize = file.length( );
							time = System.currentTimeMillis( );
							monitor.subTask( defaultTaskName
									+ "\t[ "
									+ ( donwloadSize * 100 / size )
									+ "% , Speed: "
									+ speed
									+ " , Size: "
									+ FileUtils.byteCountToDisplaySize( size )
									+ " ]" );

							currentLength += increasement;
							monitor.worked( (int) ( increasement * 10000 / allLength ) );
							updateTooltip( );
						}
					}
					try
					{
						Thread.sleep( 500 );
					}
					catch ( InterruptedException e )
					{
					}
				}
			}
		};
		downloadThread.setDaemon( true );
		downloadThread.start( );
	}

	private long computeSDKFilesLength( File platformVersion )
	{
		long targetLength = 0L;
		if ( platformVersion.exists( ) && platformVersion.isDirectory( ) )
		{
			Collection<File> targetFiles = FileUtils.listFiles( platformVersion,
					new String[]{
						"zip"
					},
					false );

			Iterator<File> fileIter = targetFiles.iterator( );
			while ( fileIter.hasNext( ) )
			{
				targetLength += fileIter.next( ).length( );
			}
		}
		return targetLength;
	}

	private File[] listToolkitPlugins( final List<String> pluginVersions,
			File pluginDir, final IProgressMonitor monitor )
	{
		return pluginDir.listFiles( new FileFilter( ) {

			public boolean accept( File file )
			{
				monitor.subTask( "Scanning Toolkit plugin files: "
						+ file.getAbsolutePath( ) );
				if ( file.isDirectory( )
						&& file.getName( ).matches( "\\d+\\.\\d+" ) )
				{
					if ( pluginVersions.contains( file.getName( ) ) )
					{
						allLength += FileUtils.sizeOfDirectory( file );
						return true;
					}
					return false;
				}
				else
				{
					if ( file.isDirectory( ) )
					{
						allLength += FileUtils.sizeOfDirectory( file );
					}
					else
					{
						allLength += file.length( );
					}
					return true;
				}
			}
		} );
	}

	private void listSDKFiles( final List<String> syncVersions,
			File platformDir, final Map<String, List<File>> sdkFiles,
			final IProgressMonitor monitor )
	{
		Version[] versions = data.getPlatformVersions( );
		for ( int i = 0; i < syncVersions.size( ); i++ )
		{
			String version = syncVersions.get( i );
			for ( int j = 0; j < versions.length; j++ )
			{
				if ( versions[j].getValue( ).equals( version ) )
				{
					monitor.subTask( "Scanning platform: "
							+ versions[j].getVersionFile( ).getAbsolutePath( ) );

					final String parentFileName = versions[j].getVersionFile( )
							.getName( );
					versions[j].getVersionFile( ).listFiles( new FileFilter( ) {

						public boolean accept( File file )
						{
							if ( file.isFile( ) )
							{
								String fileName = file.getName( );
								if ( fileName.matches( ECLIPSE_SDK )
										|| fileName.matches( EMF_SDK )
										|| fileName.matches( GEF_SDK )
										|| fileName.matches( WTP_SDK ) )
								{
									if ( !sdkFiles.containsKey( parentFileName ) )
									{
										sdkFiles.put( parentFileName,
												new ArrayList<File>( ) );
									}
									sdkFiles.get( parentFileName ).add( file );
									allLength += file.length( );
								}
							}
							return false;
						}
					} );
					break;
				}

			}
		}
	}

	private void updateTooltip( )
	{
		if ( data.isMinimizeToolkit( ) )
		{
			Display.getDefault( ).syncExec( new Runnable( ) {

				public void run( )
				{
					trayItem.setToolTipText( "Sync Progress: "
							+ currentLength
							* 100
							/ allLength
							+ "%" );
				}
			} );
		}
	}
}
