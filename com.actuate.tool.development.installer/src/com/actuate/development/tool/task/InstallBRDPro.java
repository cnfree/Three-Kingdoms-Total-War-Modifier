
package com.actuate.development.tool.task;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.sf.feeling.swt.win32.extension.io.FileSystem;
import org.sf.feeling.swt.win32.extension.shell.ShellFolder;
import org.sf.feeling.swt.win32.extension.shell.ShellLink;
import org.sf.feeling.swt.win32.extension.shell.Windows;
import org.sf.feeling.swt.win32.extension.system.Kernel;
import org.sf.feeling.swt.win32.extension.system.ProcessEntry;

import com.actuate.development.tool.Toolkit;
import com.actuate.development.tool.config.LocationConfig;
import com.actuate.development.tool.config.PathConfig;
import com.actuate.development.tool.model.Module;
import com.actuate.development.tool.model.ModuleType;
import com.actuate.development.tool.model.ModuleVersion;
import com.actuate.development.tool.model.feature.InstallBRDProData;
import com.actuate.development.tool.util.FileSorter;
import com.actuate.development.tool.util.FileUtil;
import com.actuate.development.tool.util.LogUtil;
import com.actuate.development.tool.util.UIUtil;

public class InstallBRDPro implements ITaskWithMonitor
{

	private static final String ORG_ECLIPSE_DTP = "org.eclipse.datatools.connectivity.oda.feature_";

	private static final String ORG_ECLIPSE_PLATFORM = "org.eclipse.platform_";

	private static final String ORG_ECLIPSE_EMF = "org.eclipse.emf_";

	private static final String ORG_ECLIPSE_GEF = "org.eclipse.gef_";

	private static final String ORG_ECLIPSE_WTP = "org.eclipse.wst.common_core.feature_";

	private static final String ECLIPSE_FEATURES = "eclipse\\features\\";

	private static final String ECLIPSE_PLUGINS = "eclipse\\plugins\\";

	private static final String ECLIPSE_FEATURE_PATTERN = "(?i)"
			+ Pattern.quote( ECLIPSE_FEATURES );

	private static final String ECLIPSE_VERSION_PATTERN = "(?i)"
			+ Pattern.quote( ECLIPSE_PLUGINS + ORG_ECLIPSE_PLATFORM )
			+ "(\\d+\\.\\d+\\.\\d+)";

	private static final String EMF_VERSION_PATTERN = "(?i)"
			+ Pattern.quote( ECLIPSE_FEATURES + ORG_ECLIPSE_EMF )
			+ "(\\d+\\.\\d+\\.\\d+)";

	private static final String GEF_VERSION_PATTERN = "(?i)"
			+ Pattern.quote( ECLIPSE_FEATURES + ORG_ECLIPSE_GEF )
			+ "(\\d+\\.\\d+\\.\\d+)";

	private static final String WTP_VERSION_PATTERN = "(?i)"
			+ Pattern.quote( ECLIPSE_FEATURES + ORG_ECLIPSE_WTP )
			+ "(\\d+\\.\\d+\\.\\d+)";

	private static final String DTP_VERSION_PATTERN = "(?i)"
			+ Pattern.quote( ECLIPSE_FEATURES + ORG_ECLIPSE_DTP )
			+ "(\\d+\\.\\d+\\.\\d+)";

	private InstallBRDProData data;

	private StringBuffer installBuffer;

	private StringBuffer sourceBuffer;

	private StringBuffer linkBuffer;

	private Module[] current = new Module[1];

	private Thread outputThread;

	private Thread downloadThread;

	private Process antProcess;

	private boolean monitorAntProcess;

	public InstallBRDPro( InstallBRDProData data )
	{
		this.data = data;
	}

	public void execute( final IProgressMonitor monitor )
	{
		if ( data == null )
			return;

		startMonitorProcess( monitor );

		int baseStep = 5;
		File downloadFile = new File( data.getBrdproFile( ) );
		if ( downloadFile.isFile( ) )
		{
			baseStep++;
		}
		monitor.beginTask( "Total "
				+ ( baseStep + ( data.getModules( ) == null ? 0
						: data.getModules( ).length ) ) + " steps",
				IProgressMonitor.UNKNOWN );

		closeRelationalDirectory( );

		Project p = new Project( );
		p.setBasedir( FileSystem.getCurrentDirectory( ) );
		p.fireBuildStarted( );
		p.init( );

		ProjectHelper helper = ProjectHelper.getProjectHelper( );

		final DefaultLogger consoleLogger = new DefaultLogger( );
		consoleLogger.setErrorPrintStream( System.err );
		consoleLogger.setOutputPrintStream( System.out );
		consoleLogger.setMessageOutputLevel( Project.MSG_INFO );
		p.addBuildListener( consoleLogger );

		boolean[] downloadFlag = new boolean[]{
			false
		};
		boolean[] unzipFlag = new boolean[]{
			false
		};
		String[] subtaskName = new String[1];

		installBuffer = new StringBuffer( );
		sourceBuffer = new StringBuffer( );
		linkBuffer = new StringBuffer( );

		final int[] step = new int[1];
		final String[] stepDetail = new String[1];

		try
		{
			if ( !monitor.isCanceled( ) )
			{
				StringBuffer buffer = new StringBuffer( );
				int result = initTask( monitor, step, stepDetail, buffer );
				if ( result == -1 )
				{
					throw new Exception( buffer.toString( ) );
				}
			}

			if ( !monitor.isCanceled( ) )
			{
				StringBuffer buffer = new StringBuffer( );
				int result = downloadBRDPro( monitor,
						downloadFlag,
						step,
						stepDetail,
						buffer );

				downloadFlag[0] = true;

				if ( result == -1 )
				{
					throw new Exception( buffer.toString( ) );
				}

				if ( !monitor.isCanceled( ) )
				{
					checkBRDProVersion( monitor );
				}
			}

			if ( !monitor.isCanceled( ) )
			{
				File installFile = new File( data.getBrdproFile( ) );
				if ( installFile.isFile( ) )
				{
					extractBRDPro( monitor,
							p,
							helper,
							consoleLogger,
							unzipFlag,
							step,
							stepDetail );
				}
			}
			unzipFlag[0] = true;

			if ( !monitor.isCanceled( ) )
			{
				unzipFlag[0] = false;
				StringBuffer buffer = new StringBuffer( );
				int result = installBRDPro( monitor,
						p,
						helper,
						consoleLogger,
						unzipFlag,
						step,
						stepDetail,
						buffer );
				if ( result == -1 )
				{
					throw new Exception( buffer.toString( ) );
				}
			}
			unzipFlag[0] = true;

			final List<Module> failedList = new ArrayList<Module>( );

			if ( data.getModules( ) != null && data.getModuleVersion( ) != null )
			{
				String eclipseVersion = data.getModuleVersion( ).eclipse;
				String eclipseBigVersion = data.getModuleVersion( ).eclipse.substring( 0,
						data.getModuleVersion( ).eclipse.lastIndexOf( '.' ) );

				String eclipseOutputDir = PathConfig.getProperty( PathConfig.PLATFORM,
						"\\\\qa-build\\BIRTOutput\\platform\\{0}_Release_platform" )
						.replace( "{0}", eclipseVersion );
				if ( LocationConfig.HEADQUARTER.equals( LocationConfig.getLocation( ) ) )
				{
					eclipseOutputDir = new File( PathConfig.getProperty( PathConfig.PLUGINS ) ).getParentFile( )
							.getAbsolutePath( )
							+ "\\platform\\"
							+ eclipseOutputDir;
				}
				String pluginOutputDir = Toolkit.HOST
						+ "\\"
						+ eclipseBigVersion
						+ "\\"
						+ eclipseVersion;

				for ( Module module : data.getModules( ) )
				{
					unzipFlag = new boolean[]{
						false
					};
					downloadFlag = new boolean[]{
						false
					};

					if ( monitor.isCanceled( ) )
					{
						break;
					}

					current[0] = module;
					subtaskName[0] = "[Step "
							+ ++step[0]
							+ "] Downloading and extracting the "
							+ module.getValue( )
							+ " "
							+ module.getType( ).getValue( )
							+ "...";
					monitor.subTask( subtaskName[0] );

					stepDetail[0] = "Download and extract the "
							+ module.getValue( )
							+ " "
							+ module.getType( ).getValue( );

					if ( module == Module.eclipse )
					{
						String pattern = PathConfig.getProperty( PathConfig.ECLIPSE_SDK,
								"(?i)eclipse.+SDK.+win32\\.zip" );
						File file = getSDKFile( eclipseOutputDir, pattern );

						// file = new File(
						// "E:\\zip\\3.7\\eclipse-SDK-M20120208-0800-win32.zip"
						// );
						if ( file != null && file.exists( ) )
						{
							monitorDownload( monitor,
									downloadFlag,
									subtaskName[0],
									new File( data.getTempDir( )
											+ "\\"
											+ module.getName( )
											+ "_sdk", file.getName( ) ),
									file.length( ) );
							initSubtask( monitor,
									step,
									consoleLogger,
									subtaskName,
									unzipFlag,
									file );
							handleEclipseSDK( p, helper, file );
							continue;
						}
					}
					else if ( module == Module.gef )
					{
						if ( Arrays.asList( data.getModules( ) )
								.contains( Module.gefsdk ) )
							continue;
						String pattern = PathConfig.getProperty( PathConfig.GEF_SDK,
								"(?i)GEF.+ALL.+\\.zip" );
						File file = getSDKFile( eclipseOutputDir, pattern );
						// file = new File(
						// "E:\\zip\\3.7\\GEF-ALL-3.7.2.zip" );
						if ( data.getModuleVersion( ).gef != null
								&& data.getModuleVersion( ).gef.indexOf( '.' ) != -1 )
						{
							if ( file != null && file.exists( ) )
							{
								monitorDownload( monitor,
										downloadFlag,
										subtaskName[0],
										new File( data.getTempDir( )
												+ "\\"
												+ module.getName( )
												+ "_sdk", file.getName( ) ),
										file.length( ) );
								initSubtask( monitor,
										step,
										consoleLogger,
										subtaskName,
										unzipFlag,
										file );
								handleGEFSDK( p, helper, file );
								continue;
							}
						}
					}
					else if ( module == Module.emf )
					{
						if ( Arrays.asList( data.getModules( ) )
								.contains( Module.emfsdk ) )
							continue;
						String pattern = PathConfig.getProperty( PathConfig.EMF_SDK,
								"(?i)emf.+SDK.+\\.zip" );
						File file = getSDKFile( eclipseOutputDir, pattern );
						// file = new File(
						// "E:\\zip\\3.7\\emf-xsd-SDK-M201201231045.zip" );
						if ( data.getModuleVersion( ).emf != null
								&& data.getModuleVersion( ).emf.indexOf( '.' ) != -1 )
						{
							if ( file != null && file.exists( ) )
							{
								monitorDownload( monitor,
										downloadFlag,
										subtaskName[0],
										new File( data.getTempDir( )
												+ "\\"
												+ module.getName( )
												+ "_sdk", file.getName( ) ),
										file.length( ) );
								initSubtask( monitor,
										step,
										consoleLogger,
										subtaskName,
										unzipFlag,
										file );
								handleEMFSDK( p, helper, file );
								continue;
							}
						}
					}
					else if ( module == Module.wtp )
					{
						if ( Arrays.asList( data.getModules( ) )
								.contains( Module.wtpsdk ) )
							continue;
						String pattern = PathConfig.getProperty( PathConfig.WTP_SDK,
								"(?i)wtp.+sdk.+\\.zip" );
						File file = getSDKFile( eclipseOutputDir, pattern );
						// file = new File(
						// "E:\\zip\\3.7\\wtp-sdk-M-3.3.2-20120210195245.zip"
						// );
						if ( data.getModuleVersion( ).wtp != null
								&& data.getModuleVersion( ).wtp.indexOf( '.' ) != -1 )
						{
							if ( file != null && file.exists( ) )
							{
								monitorDownload( monitor,
										downloadFlag,
										subtaskName[0],
										new File( data.getTempDir( )
												+ "\\"
												+ module.getName( )
												+ "_sdk", file.getName( ) ),
										file.length( ) );
								initSubtask( monitor,
										step,
										consoleLogger,
										subtaskName,
										unzipFlag,
										file );
								handleWTPSDK( p, helper, file );
								continue;
							}
						}
					}
					else if ( module == Module.dtp )
					{
						String pattern = PathConfig.getProperty( PathConfig.DTP_SDK,
								"(?i)dtp-sdk.+\\.zip" );
						File file = getDTPFile( pattern );
						// file = new File(
						// "E:\\zip\\3.7\\dtp-sdk-1.10.1RC1-201207130500.zip"
						// );
						if ( data.getModuleVersion( ).dtp != null
								&& data.getModuleVersion( ).dtp.indexOf( '.' ) != -1 )
						{
							if ( file != null && file.exists( ) )
							{
								monitorDownload( monitor,
										downloadFlag,
										subtaskName[0],
										new File( data.getTempDir( )
												+ "\\"
												+ module.getName( )
												+ "_sdk", file.getName( ) ),
										file.length( ) );
								initSubtask( monitor,
										step,
										consoleLogger,
										subtaskName,
										unzipFlag,
										file );
								handleDTPSDK( p, helper, file );
								continue;
							}
						}
					}
					else if ( module == Module.perforce )
					{
						String pattern = "(?i)p4.+\\.zip";
						File file = getPluginFile( pluginOutputDir, pattern );
						// file = new File(
						// "E:\\zip\\3.7\\dtp-sdk-1.10.1RC1-201207130500.zip"
						// );
						if ( file != null && file.exists( ) )
						{
							monitorDownload( monitor,
									downloadFlag,
									subtaskName[0],
									new File( data.getTempDir( )
											+ "\\"
											+ module.getName( ), file.getName( ) ),
									file.length( ) );
							initSubtask( monitor,
									step,
									consoleLogger,
									subtaskName,
									unzipFlag,
									file );
							handlePlugin( p, helper, file, Module.perforce );
							continue;
						}
					}
					else if ( module == Module.git )
					{
						String pattern = "(?i)egit.+\\.zip";
						File file = getPluginFile( pluginOutputDir, pattern );
						// file = new File(
						// "E:\\zip\\3.7\\dtp-sdk-1.10.1RC1-201207130500.zip"
						// );
						if ( file != null && file.exists( ) )
						{
							monitorDownload( monitor,
									downloadFlag,
									subtaskName[0],
									new File( data.getTempDir( )
											+ "\\"
											+ module.getName( ), file.getName( ) ),
									file.length( ) );
							initSubtask( monitor,
									step,
									consoleLogger,
									subtaskName,
									unzipFlag,
									file );
							handlePlugin( p, helper, file, Module.git );
							continue;
						}
					}
					else if ( module == Module.emfsdk )
					{
						String pattern = "(?i)emf.+SDK.+\\.zip";
						File file = getSDKFile( eclipseOutputDir, pattern );
						// file = new File(
						// "E:\\zip\\3.7\\emf-xsd-SDK-M201201231045.zip" );
						if ( data.getModuleVersion( ).emf != null
								&& data.getModuleVersion( ).emf.indexOf( '.' ) != -1 )
						{
							if ( file != null && file.exists( ) )
							{
								monitorDownload( monitor,
										downloadFlag,
										subtaskName[0],
										new File( data.getTempDir( )
												+ "\\"
												+ module.getName( ),
												file.getName( ) ),
										file.length( ) );
								initSubtask( monitor,
										step,
										consoleLogger,
										subtaskName,
										unzipFlag,
										file );
								handlePlugin( p, helper, file, Module.emfsdk );
								continue;
							}
						}
					}
					else if ( module == Module.gefsdk )
					{
						String pattern = "(?i)GEF.+ALL.+\\.zip";
						File file = getSDKFile( eclipseOutputDir, pattern );
						// file = new File(
						// "E:\\zip\\3.7\\emf-xsd-SDK-M201201231045.zip" );
						if ( data.getModuleVersion( ).emf != null
								&& data.getModuleVersion( ).emf.indexOf( '.' ) != -1 )
						{
							if ( file != null && file.exists( ) )
							{
								monitorDownload( monitor,
										downloadFlag,
										subtaskName[0],
										new File( data.getTempDir( )
												+ "\\"
												+ module.getName( ),
												file.getName( ) ),
										file.length( ) );
								initSubtask( monitor,
										step,
										consoleLogger,
										subtaskName,
										unzipFlag,
										file );
								handlePlugin( p, helper, file, Module.gefsdk );
								continue;
							}
						}
					}
					else if ( module == Module.wtpsdk )
					{
						String pattern = "(?i)wtp.+sdk.+\\.zip";
						File file = getSDKFile( eclipseOutputDir, pattern );
						// file = new File(
						// "E:\\zip\\3.7\\emf-xsd-SDK-M201201231045.zip" );
						if ( data.getModuleVersion( ).emf != null
								&& data.getModuleVersion( ).emf.indexOf( '.' ) != -1 )
						{
							if ( file != null && file.exists( ) )
							{
								monitorDownload( monitor,
										downloadFlag,
										subtaskName[0],
										new File( data.getTempDir( )
												+ "\\"
												+ module.getName( ),
												file.getName( ) ),
										file.length( ) );
								initSubtask( monitor,
										step,
										consoleLogger,
										subtaskName,
										unzipFlag,
										file );
								handlePlugin( p, helper, file, Module.wtpsdk );
								continue;
							}
						}
					}
					else if ( module.getType( ) == ModuleType.extension )
					{
						String pattern = module.getPluginNamePattern( );
						File file = getPluginFile( pluginOutputDir, pattern );
						if ( file != null && file.exists( ) )
						{
							monitorDownload( monitor,
									downloadFlag,
									subtaskName[0],
									new File( data.getTempDir( )
											+ "\\"
											+ module.getName( ), file.getName( ) ),
									file.length( ) );
							initSubtask( monitor,
									step,
									consoleLogger,
									subtaskName,
									unzipFlag,
									file );
							handlePlugin( p, helper, file, module );
							continue;
						}
					}
					failedList.add( module );
				}
			}

			unzipFlag[0] = true;
			downloadFlag[0] = true;

			if ( !monitor.isCanceled( ) )
			{
				StringBuffer buffer = new StringBuffer( );
				int result = executeUserTask( monitor, step, stepDetail, buffer );
				if ( result == -1 )
				{
					throw new Exception( buffer.toString( ) );
				}
			}

			if ( !monitor.isCanceled( ) )
			{
				StringBuffer buffer = new StringBuffer( );
				int result = cleanTempFiles( monitor, step, stepDetail, buffer );
				if ( result == -1 )
				{
					throw new Exception( buffer.toString( ) );
				}
			}

			p.fireBuildFinished( null );

			if ( !monitor.isCanceled( ) )
			{
				createShortcut( );
				finishTask( monitor, failedList );
			}
			else
			{
				cancelTask( );
			}
		}
		catch ( final Exception e )
		{
			p.fireBuildFinished( e );
			if ( monitor.isCanceled( ) )
			{
				cancelTask( );
			}
			else
			{
				handleTaskError( step, stepDetail, e );
			}
		}
	}

	private void handleTaskError( final int[] step, final String[] stepDetail,
			final Exception e )
	{
		monitorAntProcess = false;
		Display.getDefault( ).syncExec( new Runnable( ) {

			public void run( )
			{
				if ( UIUtil.getShell( ).getMinimized( ) )
					Windows.flashWindow( UIUtil.getShell( ).handle, true );
				LogUtil.recordErrorMsg( "Step "
						+ step[0]
						+ ": "
						+ stepDetail[0]
						+ " failed.", e, true );
				Windows.flashWindow( UIUtil.getShell( ).handle, false );
			}
		} );
	}

	private void cancelTask( )
	{
		monitorAntProcess = false;
		Display.getDefault( ).syncExec( new Runnable( ) {

			public void run( )
			{
				MessageDialog.openInformation( null,
						"Information",
						"Canceled installing the BRDPro Development Environment." );
				Windows.flashWindow( UIUtil.getShell( ).handle, false );
			}
		} );
	}

	private void finishTask( final IProgressMonitor monitor,
			final List<Module> failedList )
	{
		monitorAntProcess = false;
		monitor.subTask( "" );
		monitor.setTaskName( "Finished installing the BRDPro Development Environment" );

		Display.getDefault( ).syncExec( new Runnable( ) {

			public void run( )
			{
				if ( UIUtil.getShell( ).getMinimized( ) )
					Windows.flashWindow( UIUtil.getShell( ).handle, true );
				StringBuffer buffer = new StringBuffer( );
				buffer.append( "Install the Actuate BRDPro Development Environment sucessfully." );

				if ( failedList.size( ) > 0 )
				{
					buffer.append( "\n\nDetails:\n" );
					for ( int i = 0; i < failedList.size( ); i++ )
					{
						buffer.append( ( i + 1 )
								+ ". Install the "
								+ failedList.get( i ).getValue( )
								+ " "
								+ failedList.get( i ).getType( ).getValue( )
								+ " failed.\n" );
					}
				}

				MessageDialog.openInformation( null,
						"Information",
						buffer.toString( ) );
				Windows.flashWindow( UIUtil.getShell( ).handle, false );
				StringBuffer uninstallBuffer = new StringBuffer( );
				if ( installBuffer != null && installBuffer.length( ) > 0 )
				{
					uninstallBuffer.append( "[BRDPro]\n" );
					uninstallBuffer.append( installBuffer );

				}
				if ( sourceBuffer != null && sourceBuffer.length( ) > 0 )
				{
					uninstallBuffer.append( "[Source]\n" );
					uninstallBuffer.append( sourceBuffer );
				}
				if ( linkBuffer != null && linkBuffer.length( ) > 0 )
				{
					uninstallBuffer.append( "[Link]\n" );
					uninstallBuffer.append( linkBuffer );
				}

				FileUtil.writeToFile( new File( data.getDirectory( ),
						"uninstall.data" ), uninstallBuffer.toString( ).trim( ) );
			}
		} );
	}

	private void createShortcut( )
	{
		Display.getDefault( ).syncExec( new Runnable( ) {

			public void run( )
			{
				File eclipseFile = new File( data.getDirectory( ),
						"\\eclipse\\eclipse.exe" );
				String filePath = new File( eclipseFile.getParentFile( ),
						"eclipse.lnk" ).getAbsolutePath( );
				if ( eclipseFile.exists( ) )
				{
					ShellLink.createShortCut( eclipseFile.getAbsolutePath( ),
							filePath );
					ShellLink.setShortCutArguments( filePath,
							data.getShortcutArguments( ) );
					ShellLink.setShortCutDescription( filePath,
							"Created by Tooklit" );
					ShellLink.setShortCutWorkingDirectory( filePath,
							eclipseFile.getParentFile( ).getAbsolutePath( ) );

					if ( !data.isNotCreateShortcut( ) )
					{
						try
						{
							File file = new File( ShellFolder.DESKTOP.getAbsolutePath( 0 )
									+ File.separator
									+ new File( data.getDirectory( ) ).getName( )
									+ ".lnk" );
							FileUtil.writeToBinarayFile( file,
									new FileInputStream( filePath ),
									true );
						}
						catch ( FileNotFoundException e )
						{
							LogUtil.recordErrorMsg( e, false );
						}
					}
				}
			}
		} );
	}

	private int cleanTempFiles( final IProgressMonitor monitor,
			final int[] step, final String[] stepDetail,
			StringBuffer errorMessage ) throws IOException,
			InterruptedException
	{
		monitor.subTask( "[Step "
				+ ++step[0]
				+ "] Cleaning the temporary files..." );
		stepDetail[0] = "Clean the temporary files";
		File cleanFile = getAntFile( "/templates/Clean.xml", true );

		antProcess = Runtime.getRuntime( ).exec( new String[]{
				System.getProperty( "java.home" ) + "/bin/java",
				"-cp",
				System.getProperty( "java.class.path" ),
				AntTask.class.getName( ),
				"\"" + cleanFile.getAbsolutePath( ) + "\"",
				"clean"
		} );

		interruptAntTaskErrorMessage( antProcess, errorMessage );

		int result = antProcess.waitFor( );
		antProcess = null;
		return result;
	}

	private int executeUserTask( final IProgressMonitor monitor,
			final int[] step, final String[] stepDetail,
			StringBuffer errorMessage ) throws IOException,
			InterruptedException
	{
		monitor.subTask( "[Step "
				+ ++step[0]
				+ "] Executing the user custom task..." );
		stepDetail[0] = "Execute the user custom task";

		File antFile = new File( FileSystem.getCurrentDirectory( )
				+ "\\custom\\BRDPro_Task.xml" );
		if ( !antFile.exists( ) )
		{
			FileUtil.writeToBinarayFile( antFile,
					InstallBRDPro.class.getResourceAsStream( "/custom/BRDPro_Task.xml" ),
					true );
		}

		File customFile = getAntFile( antFile.getAbsolutePath( ), false );

		antProcess = Runtime.getRuntime( ).exec( new String[]{
				System.getProperty( "java.home" ) + "/bin/java",
				"-cp",
				System.getProperty( "java.class.path" ),
				AntTask.class.getName( ),
				"\"" + customFile.getAbsolutePath( ) + "\"",
				"custom"
		} );

		interruptAntTaskErrorMessage( antProcess, errorMessage );

		int result = antProcess.waitFor( );
		antProcess = null;
		return result;
	}

	private void extractBRDPro( final IProgressMonitor monitor, Project p,
			ProjectHelper helper, final DefaultLogger consoleLogger,
			boolean[] flag, final int[] step, final String[] stepDetail )
	{
		String[] subtaskName = new String[]{
			"[Step " + ++step[0] + "] Extracting the BRDPro archive file..."
		};
		monitor.subTask( subtaskName[0] );
		stepDetail[0] = "Extract the BRDPro archive file";
		File extractFile = getAntFile( "/templates/Extract.xml", false );
		helper.parse( p, extractFile );
		interruptOutput( monitor, step, consoleLogger, flag, subtaskName );
		p.executeTarget( "unzip_brdpro" );
	}

	private int installBRDPro( final IProgressMonitor monitor, Project p,
			ProjectHelper helper, final DefaultLogger consoleLogger,
			boolean[] flag, final int[] step, final String[] stepDetail,
			StringBuffer errorMessage ) throws IOException,
			InterruptedException
	{

		String[] subtaskName = new String[]{
			"[Step " + ++step[0] + "] Installing the BRDPro..."
		};
		monitor.subTask( subtaskName[0] );
		stepDetail[0] = "Install the BRDPro";
		File installFile = getConfigFile( "/templates/brdpro.ini",
				"/templates/Install.xml",
				"/links/comOda.link" );

		if ( data.isInstallShield( ) )
		{
			Thread.sleep( 300 );
			helper.parse( p, installFile );
			interruptOutput( monitor, step, consoleLogger, flag, subtaskName );
			p.executeTarget( "extract_brdpro" );
			return 0;
		}
		else
		{
			antProcess = Runtime.getRuntime( ).exec( new String[]{
					System.getProperty( "java.home" ) + "/bin/java",
					"-cp",
					System.getProperty( "java.class.path" ),
					AntTask.class.getName( ),
					"\"" + installFile.getAbsolutePath( ) + "\"",
					"extract_brdpro"
			} );
			interruptAntTaskErrorMessage( antProcess, errorMessage );

			int result = antProcess.waitFor( );
			antProcess = null;
			return result;
		}
	}

	private int downloadBRDPro( final IProgressMonitor monitor,
			boolean[] downloadFlag, final int[] step,
			final String[] stepDetail, StringBuffer errorMessage )
			throws IOException, InterruptedException
	{
		File brdproFile = new File( data.getBrdproFile( ) );
		long fileLength = brdproFile.length( );
		String defaultTaskName = "[Step "
				+ ++step[0]
				+ "] Downloading the BRDPro archive file...";

		File downloadFile = null;

		if ( brdproFile.isFile( ) )
		{
			monitorDownload( monitor,
					downloadFlag,
					defaultTaskName,
					new File( data.getTempDir( ) + "\\brdpro\\zip",
							brdproFile.getName( ) ),
					fileLength );

			monitor.subTask( defaultTaskName
					+ "\t[ Size: "
					+ FileUtils.byteCountToDisplaySize( fileLength )
					+ " ] " );
			stepDetail[0] = "Download the BRDPro archive file";
			downloadFile = getAntFile( "/templates/Download.xml", true );
		}
		else
		{
			defaultTaskName = "[Step "
					+ ++step[0]
					+ "] Downloading the BRDPro installation files...";

			Collection<File> targetFiles = FileUtils.listFiles( brdproFile,
					new String[]{
						"cab"
					},
					true );

			Iterator<File> fileIter = targetFiles.iterator( );
			while ( fileIter.hasNext( ) )
			{
				fileLength += fileIter.next( ).length( );
			}

			monitorDownload( monitor,
					downloadFlag,
					defaultTaskName,
					new File( data.getTempDir( ) + "\\brdpro\\install" ),
					fileLength );

			monitor.subTask( defaultTaskName
					+ "\t[ Size: "
					+ FileUtils.byteCountToDisplaySize( fileLength )
					+ " ] " );
			stepDetail[0] = "Download the BRDPro installation files";
			downloadFile = getAntFile( "/templates/Download_II.xml", true );
		}

		antProcess = Runtime.getRuntime( ).exec( new String[]{
				System.getProperty( "java.home" ) + "/bin/java",
				"-cp",
				System.getProperty( "java.class.path" ),
				AntTask.class.getName( ),
				"\"" + downloadFile.getAbsolutePath( ) + "\"",
				"download"
		} );
		interruptAntTaskErrorMessage( antProcess, errorMessage );

		int result = antProcess.waitFor( );
		antProcess = null;

		return result;
	}

	private int initTask( final IProgressMonitor monitor, final int[] step,
			final String[] stepDetail, StringBuffer errorMessage )
			throws IOException, InterruptedException
	{
		data.setTempDir( data.getDirectory( )
				+ "\\temp"
				+ System.currentTimeMillis( ) );
		// data.clearDirectory = true;
		monitor.subTask( "[Step "
				+ ++step[0]
				+ "] Initializing the installation task..." );
		stepDetail[0] = "Initialize the installation task";

		File logFile = new File( data.getDirectory( ), "uninstall.data" );
		boolean useAntClean = !logFile.exists( );
		if ( !useAntClean )
		{
			cleanInstallFile( monitor );
		}
		File initFile = getAntFile( "/templates/Init.xml", useAntClean );

		antProcess = Runtime.getRuntime( ).exec( new String[]{
				System.getProperty( "java.home" ) + "/bin/java",
				"-cp",
				System.getProperty( "java.class.path" ),
				AntTask.class.getName( ),
				"\"" + initFile.getAbsolutePath( ) + "\"",
				"init"
		} );
		interruptAntTaskErrorMessage( antProcess, errorMessage );

		int result = antProcess.waitFor( );
		antProcess = null;
		return result;
	}

	private void closeRelationalDirectory( )
	{
		if ( !data.isNotCloseBRDPro( ) && data.getDirectory( ) != null )
		{
			try
			{
				String path = new File( data.getDirectory( ) ).getCanonicalPath( );
				ProcessEntry[] entrys = Kernel.getSystemProcessesSnap( );
				if ( entrys != null )
				{
					for ( int i = 0; i < entrys.length; i++ )
					{
						ProcessEntry entry = entrys[i];
						String entryPath = new File( entry.getExePath( ) ).getCanonicalPath( );
						if ( entryPath.startsWith( path ) )
						{
							Kernel.killProcess( entry.getProcessId( ) );
						}
					}
				}

				int[] handles = Windows.enumWindows( );
				for ( int i = 0; i < handles.length; i++ )
				{
					String name = Windows.getWindowText( handles[i] );
					if ( name.startsWith( path ) )
					{
						Windows.forceCloseWindow( handles[i] );
					}
				}
			}
			catch ( IOException e1 )
			{
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
						if ( antProcess != null )
						{
							antProcess.destroy( );
						}

						try
						{
							String path = new File( FileSystem.getCurrentDirectory( ) ).getCanonicalPath( );
							ProcessEntry[] entrys = Kernel.getSystemProcessesSnap( );
							if ( entrys != null )
							{
								for ( int i = 0; i < entrys.length; i++ )
								{
									ProcessEntry entry = entrys[i];
									if ( entry.getProcessName( )
											.toLowerCase( )
											.startsWith( "7z" )
											|| entry.getProcessName( )
													.toLowerCase( )
													.startsWith( "iscab" ) )
									{
										String entryPath = new File( entry.getExePath( ) ).getCanonicalPath( );
										if ( entryPath.startsWith( path ) )
										{
											Kernel.killProcess( entry.getProcessId( ) );
										}
									}
								}
							}
						}
						catch ( IOException e )
						{
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

	private void cleanInstallFile( IProgressMonitor monitor )
			throws IOException
	{
		FileUtil.deleteFile( monitor, new File( data.getDirectory( ),
				"bodtools" ) );
		FileUtil.deleteFile( monitor, new File( data.getDirectory( ), "oda" ) );
		FileUtil.deleteFile( monitor, new File( data.getDirectory( ),
				"resources" ) );
		FileUtil.deleteFile( monitor, new File( data.getDirectory( ),
				"MyClasses" ) );
		FileUtil.deleteFile( monitor,
				new File( data.getDirectory( ), "License" ) );
		FileUtil.deleteFile( monitor, new File( data.getDirectory( ), "setup" ) );
		FileUtil.deleteFile( monitor, new File( data.getDirectory( ), "ico" ) );
		FileUtil.deleteFile( monitor, new File( data.getDirectory( ),
				"3rdparty_products.txt" ) );
		FileUtil.deleteFile( monitor, new File( data.getDirectory( ),
				"startbrdpro.bat" ) );
		FileUtil.deleteFile( monitor, new File( data.getDirectory( ),
				"eclipse\\p2" ) );
		FileUtil.deleteFile( monitor, new File( data.getDirectory( ),
				"startbrdpro.bat" ) );
		FileUtil.deleteFile( monitor, new File( data.getDirectory( ),
				"eclipse\\eclipse.lnk" ) );

		File configs = new File( data.getDirectory( ), "eclipse\\configuration" );
		File[] files = configs.listFiles( );
		if ( files != null )
		{
			for ( int i = 0; i < files.length; i++ )
			{
				if ( monitor.isCanceled( ) )
				{
					return;
				}
				if ( files[i].getName( ).equalsIgnoreCase( ".settings" ) )
					continue;
				FileUtil.deleteFile( monitor, files[i] );
			}
		}

		File uninstallFile = new File( data.getDirectory( ), "uninstall.data" );
		boolean startBRDPro = false;
		boolean startSource = false;
		boolean startLink = false;
		if ( uninstallFile.exists( ) )
		{
			BufferedReader in = new BufferedReader( new InputStreamReader( new FileInputStream( uninstallFile ) ) );
			String line;
			while ( ( line = in.readLine( ) ) != null )
			{
				if ( monitor.isCanceled( ) )
				{
					in.close( );
					return;
				}

				line = line.trim( );
				if ( line.length( ) == 0 )
					continue;
				if ( line.equalsIgnoreCase( "[BRDPro]" ) )
				{
					startBRDPro = true;
					startSource = false;
					startLink = false;
				}
				else if ( line.equalsIgnoreCase( "[Source]" ) )
				{
					startBRDPro = false;
					startSource = true;
					startLink = false;
				}
				else if ( line.equalsIgnoreCase( "[Link]" ) )
				{
					startBRDPro = false;
					startSource = false;
					startLink = true;
				}
				else
				{
					if ( startBRDPro )
					{
						File file = new File( data.getDirectory( ), line );
						if ( file.exists( ) )
						{
							if ( file.isFile( ) )
							{
								if ( line.indexOf( "configuration\\.settings" ) != -1 )
									continue;
								file.delete( );
							}
							else
							{
								deleteDirectory( file );
							}
						}
					}
					else if ( startSource )
					{
						File file = new File( data.getDirectory( ), line );
						if ( file.exists( ) )
						{
							if ( file.isFile( ) )
								file.delete( );
						}
					}
					else if ( startLink )
					{
						FileUtil.deleteFile( monitor,
								new File( data.getDirectory( ),
										"eclipse\\links\\" + line + ".link" ) );
						FileUtil.deleteFile( monitor,
								new File( data.getDirectory( ), "links\\"
										+ line ) );
					}
				}

			}
			in.close( );
		}

		if ( !monitor.isCanceled( ) )
		{
			deleteDirectory( new File( data.getDirectory( ), "eclipse\\links" ) );
			deleteDirectory( new File( data.getDirectory( ), "links" ) );
		}
		else
			return;

		File[] tempDirs = new File( data.getDirectory( ) ).listFiles( new FileFilter( ) {

			public boolean accept( File file )
			{
				if ( file.getName( ).startsWith( "temp" )
						&& file.getName( ).length( ) == new File( data.getTempDir( ) ).getName( )
								.length( )
						&& file.isDirectory( ) )
				{
					return true;
				}
				return false;
			}

		} );

		if ( tempDirs != null )
		{
			for ( int i = 0; i < tempDirs.length; i++ )
			{
				if ( monitor.isCanceled( ) )
					return;
				FileUtil.deleteDirectory( monitor, tempDirs[i] );
			}
		}
		// FileUtil.deleteFile( uninstallFile );
	}

	private void deleteDirectory( File file )
	{
		if ( !file.isDirectory( ) )
			return;
		String[] children = file.list( );
		if ( children == null || children.length == 0 )
		{
			file.delete( );
		}
		else if ( children.length == 1
				&& children[0].equalsIgnoreCase( ".settings" ) )
		{
			file.delete( );
		}
	}

	private void initSubtask( final IProgressMonitor monitor, final int[] step,
			final DefaultLogger consoleLogger, String subtaskName[],
			final boolean[] flag, File file )
	{
		subtaskName[0] += "\t[ Size: "
				+ FileUtils.byteCountToDisplaySize( file.length( ) )
				+ " ] ";
		interruptOutput( monitor, step, consoleLogger, flag, subtaskName );
		monitor.subTask( subtaskName[0] );
	}

	private void interruptAntTaskErrorMessage( final Process process,
			final StringBuffer buffer )
	{
		Thread errThread = new Thread( "Monitor Ant Task" ) {

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

	private void interruptOutput( final IProgressMonitor monitor,
			final int[] currentStep, final DefaultLogger consoleLogger,
			final boolean[] flag, final String[] defaultTaskName )
	{

		String threadName = "Monitor Output";
		if ( current[0] != null && current[0].getName( ) != null )
		{
			linkBuffer.append( current[0].getName( ) ).append( "\n" );
			threadName += ( ": " + current[0].getName( ) );
		}

		outputThread = new Thread( threadName ) {

			public void run( )
			{
				try
				{
					final Module module = current[0];
					final int step = currentStep[0];
					PipedInputStream pipedIS = new PipedInputStream( );
					PipedOutputStream pipedOS = new PipedOutputStream( );
					pipedOS.connect( pipedIS );
					BufferedReader input = new BufferedReader( new InputStreamReader( pipedIS ) );
					PrintStream ps = new PrintStream( pipedOS );
					consoleLogger.setOutputPrintStream( ps );
					final String[] line = new String[1];
					String extactingStr = "[exec] Extracting";
					int length = "[exec]".length( );
					while ( ( line[0] = input.readLine( ) ) != null )
					{
						if ( module != current[0] )
							break;
						if ( !flag[0] )
						{
							int index = line[0].indexOf( extactingStr );
							if ( index != -1 )
							{
								String file = line[0].substring( index + length );
								monitor.subTask( "[Step " + step + "]" + file );
								if ( module == null )
								{
									if ( data.isInstallShield( ) )
									{

										file = file.trim( )
												.replaceAll( "Extracting\\s+",
														"" );
										if ( file.toLowerCase( )
												.indexOf( "eclipse" ) > -1 )
										{
											file = ( "\\" + file );
											installBuffer.append( file + "\n" );
										}
									}
									else
									{
										file = file.trim( )
												.replaceAll( "Extracting\\s+BRDPro",
														"" );
										installBuffer.insert( 0, file + "\n" );
									}

								}
								else if ( module.getType( ) == ModuleType.source
										&& file.indexOf( "eclipse\\plugins" ) > -1
										&& file.indexOf( "source" ) > -1 )
								{
									String prefix = "\\eclipse\\dropins";
									file = ( prefix + "\\" + file.trim( )
											.replaceAll( "Extracting\\s+", "" ) );
									sourceBuffer.append( file ).append( "\n" );
								}
							}
							else
							{
								monitor.subTask( defaultTaskName[0] );
							}
							System.out.println( line[0] );
						}
					}
					input.close( );
					pipedIS.close( );
					consoleLogger.setOutputPrintStream( System.out );
				}
				catch ( IOException e )
				{

				}
			}
		};
		outputThread.start( );

	}

	private void monitorDownload( final IProgressMonitor monitor,
			final boolean[] flag, final String defaultTaskName,
			final File file, final long size )
	{
		downloadThread = new Thread( "Monitor Download" ) {

			public void run( )
			{
				long donwloadSize = file.length( );
				if ( file.isDirectory( ) )
				{
					Collection<File> targetFiles = FileUtils.listFiles( file,
							new String[]{
								"cab"
							},
							true );
					Iterator<File> fileIter = targetFiles.iterator( );
					while ( fileIter.hasNext( ) )
					{
						donwloadSize += fileIter.next( ).length( );
					}
				}
				long time = System.currentTimeMillis( );
				final Module module = current[0];
				while ( !flag[0] )
				{
					if ( module != current[0] )
						break;
					if ( file.exists( ) )
					{
						if ( file.length( ) != donwloadSize )
						{
							if ( module != current[0] )
								break;
							String speed = FileUtil.format( ( (float) ( file.length( ) - donwloadSize ) )
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
		downloadThread.setDaemon( true );
		downloadThread.start( );
	}

	private File getPluginFile( String pluginOutputDir, final String pattern )
	{
		File pluginDir = new File( pluginOutputDir );
		if ( pluginDir.exists( ) )
		{
			File plugin = getPluginFile( pattern, pluginDir );
			if ( plugin != null )
				return plugin;
		}

		pluginDir = pluginDir.getParentFile( );
		if ( pluginDir != null && pluginDir.exists( ) )
		{
			File plugin = getPluginFile( pattern, pluginDir );
			if ( plugin != null )
				return plugin;
		}

		if ( pluginDir == null )
			return null;

		pluginDir = pluginDir.getParentFile( );
		if ( pluginDir != null && pluginDir.exists( ) )
		{

			File plugin = getPluginFile( pattern, pluginDir );
			if ( plugin != null )
				return plugin;
		}

		return null;
	}

	private File getPluginFile( final String pattern, File file )
	{
		File[] sdks = file.listFiles( new FileFilter( ) {

			public boolean accept( File file )
			{
				String fileName = file.getName( );
				if ( fileName.matches( pattern ) )
				{
					return true;
				}
				return false;
			}
		} );
		if ( sdks != null && sdks.length > 0 )
		{
			FileSorter.sortFiles( sdks );
			return sdks[sdks.length - 1];
		}
		return null;
	}

	private void handleEclipseSDK( Project p, ProjectHelper helper, File file )
	{
		Module module = Module.eclipse;
		final String validateFile = ORG_ECLIPSE_PLATFORM
				+ data.getModuleVersion( ).eclipse;
		handleSDK( p, helper, file, module, validateFile );
	}

	private void handleGEFSDK( Project p, ProjectHelper helper, File file )
	{
		Module module = Module.gef;
		final String validateFile = ORG_ECLIPSE_GEF
				+ data.getModuleVersion( ).gef;
		handleSDK( p, helper, file, module, validateFile );

	}

	private void handleEMFSDK( Project p, ProjectHelper helper, File file )
	{
		Module module = Module.emf;
		final String validateFile = ORG_ECLIPSE_EMF
				+ data.getModuleVersion( ).emf;
		handleSDK( p, helper, file, module, validateFile );

	}

	private void handleDTPSDK( Project p, ProjectHelper helper, File file )
	{
		Module module = Module.dtp;
		final String validateFile = ORG_ECLIPSE_DTP
				+ data.getModuleVersion( ).dtp;
		handleSDK( p, helper, file, module, validateFile );

	}

	private void handleWTPSDK( Project p, ProjectHelper helper, File file )
	{
		Module module = Module.wtp;
		final String validateFile = ORG_ECLIPSE_WTP
				+ data.getModuleVersion( ).wtp;
		handleSDK( p, helper, file, module, validateFile );

	}

	private void handleSDK( Project p, ProjectHelper helper, File file,
			Module module, final String validateFile )
	{
		String fileName = "/templates/Source.xml";
		File templateFile = FileUtil.getTempFile( fileName );
		FileUtil.writeToBinarayFile( templateFile, this.getClass( )
				.getResourceAsStream( fileName ), true );

		VelocityEngine velocityEngine = new VelocityEngine( );
		velocityEngine.setProperty( VelocityEngine.FILE_RESOURCE_LOADER_PATH,
				templateFile.getAbsoluteFile( )
						.getParentFile( )
						.getAbsolutePath( ) );
		velocityEngine.init( );

		VelocityContext context = new VelocityContext( );
		context.put( "tempDir", data.getTempDir( ) );
		context.put( "brdproFile", data.getBrdproFile( ) );
		context.put( "installPath", data.getDirectory( ) );
		context.put( "source", module.getName( ) );
		context.put( "sourceFile", file.getAbsolutePath( ) );
		context.put( "runtime", FileSystem.getCurrentDirectory( ) );

		Template template = velocityEngine.getTemplate( templateFile.getName( ) );
		StringWriter sw = new StringWriter( );
		template.merge( context, sw );

		File tempFile = FileUtil.getTempFile( fileName );
		FileUtil.writeToFile( tempFile, sw.toString( ).trim( ) );

		helper.parse( p, tempFile );
		p.executeTarget( module.getName( ) + "_download" );

		File sdkDir = new File( p.getProperty( "build.temp" )
				+ "\\"
				+ module.getName( )
				+ "_sdk\\"
				+ ECLIPSE_FEATURES );
		if ( sdkDir.exists( ) )
		{
			File[] children = sdkDir.listFiles( new FileFilter( ) {

				public boolean accept( File file )
				{
					if ( file.getName( ).startsWith( validateFile ) )
						return true;
					return false;
				}
			} );
			if ( children != null )
			{
				p.executeTarget( module.getName( ) + "_install" );
			}
		}
	}

	private void handlePlugin( Project p, ProjectHelper helper, File file,
			Module module )
	{
		String fileName = "/templates/Plugin.xml";
		File templateFile = FileUtil.getTempFile( fileName );
		FileUtil.writeToBinarayFile( templateFile, this.getClass( )
				.getResourceAsStream( fileName ), true );

		VelocityEngine velocityEngine = new VelocityEngine( );
		velocityEngine.setProperty( VelocityEngine.FILE_RESOURCE_LOADER_PATH,
				templateFile.getAbsoluteFile( )
						.getParentFile( )
						.getAbsolutePath( ) );
		velocityEngine.init( );

		VelocityContext context = new VelocityContext( );
		context.put( "tempDir", data.getTempDir( ) );
		context.put( "brdproFile", data.getBrdproFile( ) );
		context.put( "installPath", data.getDirectory( ) );
		context.put( "plugin", module.getName( ) );
		context.put( "pluginFile", file.getAbsolutePath( ) );
		context.put( "runtime", FileSystem.getCurrentDirectory( ) );

		Template template = velocityEngine.getTemplate( templateFile.getName( ) );
		StringWriter sw = new StringWriter( );
		template.merge( context, sw );

		File tempFile = FileUtil.getTempFile( fileName );
		FileUtil.writeToFile( tempFile, sw.toString( ).trim( ) );

		helper.parse( p, tempFile );
		p.executeTarget( module.getName( ) + "_install" );

		File linkTemplateFile = FileUtil.getTempFile( "/templates/extension.link" );
		FileUtil.writeToBinarayFile( linkTemplateFile, this.getClass( )
				.getResourceAsStream( "/templates/extension.link" ), true );

		velocityEngine = new VelocityEngine( );
		velocityEngine.setProperty( VelocityEngine.FILE_RESOURCE_LOADER_PATH,
				linkTemplateFile.getAbsoluteFile( )
						.getParentFile( )
						.getAbsolutePath( ) );

		context = new VelocityContext( );
		context.put( "extension", module.getName( ) );

		template = velocityEngine.getTemplate( linkTemplateFile.getName( ) );
		sw = new StringWriter( );
		template.merge( context, sw );

		File linkDir = new File( data.getDirectory( ), "eclipse\\links" );
		if ( !linkDir.exists( ) )
			linkDir.mkdirs( );
		File linkFile = new File( linkDir, module.getName( ) + ".link" );
		FileUtil.writeToFile( linkFile, sw.toString( ).trim( ) );
	}

	private File getSDKFile( String eclipseOutputDir, final String pattern )
	{
		File sdkDir = new File( eclipseOutputDir );
		if ( sdkDir.exists( ) )
		{
			File[] sdks = sdkDir.listFiles( new FileFilter( ) {

				public boolean accept( File file )
				{
					String fileName = file.getName( );
					if ( fileName.matches( pattern ) )
					{
						return true;
					}
					return false;
				}
			} );
			FileSorter.sortFiles( sdks );
			if ( sdks != null && sdks.length > 0 )
			{
				return sdks[sdks.length - 1];
			}
		}
		return null;
	}

	private File getDTPFile( final String pattern )
	{
		File sdkDir = new File( PathConfig.getProperty( PathConfig.DTP_OUTPUT,
				"\\\\qa-build\\BIRTOutput\\dtp.output" ),
				data.getModuleVersion( ).dtp );
		if ( sdkDir.exists( ) )
		{
			List<File> files = new ArrayList<File>( );
			checkDTPFile( sdkDir, pattern, files );
			if ( files.size( ) > 0 )
			{
				FileSorter.sortFiles( files );
				return files.get( files.size( ) - 1 );
			}
		}
		return null;
	}

	private void checkDTPFile( File file, final String pattern,
			final List<File> files )
	{
		File[] children = file.listFiles( new FileFilter( ) {

			public boolean accept( File file )
			{
				if ( file.isFile( ) )
				{
					String fileName = file.getName( );
					if ( fileName.matches( pattern ) )
					{
						files.add( file );
						return false;
					}
				}
				else
				{
					return true;
				}
				return false;
			}

		} );

		if ( children != null && files.isEmpty( ) )
		{
			FileSorter.sortFiles( children );
			for ( int i = children.length - 1; i >= 0; i-- )
			{
				if ( !files.isEmpty( ) )
					break;
				checkDTPFile( children[i], pattern, files );
			}
		}
	}

	private File getAntFile( String fileName, boolean useAntClean )
	{
		File templateFile = null;
		File xmlFile = new File( fileName );
		if ( xmlFile.exists( ) )
		{
			try
			{
				templateFile = FileUtil.getTempFile( xmlFile.getName( ) );
				FileInputStream fis = new FileInputStream( fileName );
				FileUtil.writeToBinarayFile( templateFile, fis, true );
				fis.close( );
				fileName = xmlFile.getName( );
			}
			catch ( IOException e )
			{
				Logger.getLogger( this.getClass( ).getName( ) )
						.log( Level.WARNING, "Read file failed.", e );
			}
		}
		else
		{
			templateFile = FileUtil.getTempFile( fileName );
			FileUtil.writeToBinarayFile( templateFile, this.getClass( )
					.getResourceAsStream( fileName ), true );
		}
		VelocityEngine velocityEngine = new VelocityEngine( );
		velocityEngine.setProperty( VelocityEngine.FILE_RESOURCE_LOADER_PATH,
				templateFile.getAbsoluteFile( )
						.getParentFile( )
						.getAbsolutePath( ) );
		velocityEngine.init( );

		VelocityContext context = new VelocityContext( );
		context.put( "runtime", FileSystem.getCurrentDirectory( ) );
		context.put( "tempDir", data.getTempDir( ) );
		context.put( "brdproFile", data.getBrdproFile( ) );
		context.put( "installPath", data.getDirectory( ) );
		context.put( "clean", !data.isNotClearDirectory( ) );
		context.put( "existInstallPath",
				new File( data.getDirectory( ) ).exists( ) && useAntClean );

		Template template = velocityEngine.getTemplate( templateFile.getName( ) );
		StringWriter sw = new StringWriter( );
		template.merge( context, sw );

		File tempFile = FileUtil.getTempFile( fileName );
		FileUtil.writeToFile( tempFile, sw.toString( ).trim( ) );

		return tempFile;
	}

	private File getConfigFile( String config, String ant, String link )
	{
		File configTemplateFile = FileUtil.getTempFile( config );
		FileUtil.writeToBinarayFile( configTemplateFile, this.getClass( )
				.getResourceAsStream( config ), true );

		VelocityEngine velocityEngine = new VelocityEngine( );
		velocityEngine.setProperty( VelocityEngine.FILE_RESOURCE_LOADER_PATH,
				configTemplateFile.getAbsoluteFile( )
						.getParentFile( )
						.getAbsolutePath( ) );

		VelocityContext context = new VelocityContext( );
		context.put( "tempDir", data.getTempDir( ) );
		context.put( "brdproFile", data.getBrdproFile( ) );
		context.put( "installPath", data.getDirectory( ) );

		Template template = velocityEngine.getTemplate( configTemplateFile.getName( ) );
		StringWriter sw = new StringWriter( );
		template.merge( context, sw );

		File configFile = FileUtil.getTempFile( config );
		FileUtil.writeToFile( configFile, sw.toString( ).trim( ) );

		File linkFile = FileUtil.getTempFile( link, ".link" );
		FileUtil.writeToBinarayFile( linkFile, this.getClass( )
				.getResourceAsStream( link ), true );

		File antTemplateFile = FileUtil.getTempFile( ant );
		FileUtil.writeToBinarayFile( antTemplateFile, this.getClass( )
				.getResourceAsStream( ant ), true );

		velocityEngine = new VelocityEngine( );
		velocityEngine.setProperty( VelocityEngine.FILE_RESOURCE_LOADER_PATH,
				antTemplateFile.getAbsoluteFile( )
						.getParentFile( )
						.getAbsolutePath( ) );

		context = new VelocityContext( );
		context.put( "config", configFile.getAbsolutePath( ) );
		context.put( "isInstallShield", data.isInstallShield( ) );
		context.put( "odaLinkPath", linkFile.getAbsolutePath( ) );
		context.put( "runtime", FileSystem.getCurrentDirectory( ) );
		context.put( "tempDir", data.getTempDir( ) );
		context.put( "brdproFile", data.getBrdproFile( ) );
		context.put( "installPath", data.getDirectory( ) );

		template = velocityEngine.getTemplate( antTemplateFile.getName( ) );
		sw = new StringWriter( );
		template.merge( context, sw );

		File antFile = FileUtil.getTempFile( ant );
		FileUtil.writeToFile( antFile, sw.toString( ).trim( ) );

		return antFile;
	}

	private void checkBRDProVersion( IProgressMonitor monitor )
	{
		File downloadFile = new File( data.getBrdproFile( ) );
		if ( downloadFile.isFile( ) )
		{
			String brdproFile = new File( data.getTempDir( ), "brdpro\\zip\\"
					+ data.getBrdproFile( ).substring( data.getBrdproFile( )
							.lastIndexOf( '\\' ) ) ).getAbsolutePath( );
			try
			{
				ZipFile zipFile = new ZipFile( brdproFile );
				Enumeration enumeration = zipFile.getEntries( );
				ZipEntry zipEntry = (ZipEntry) enumeration.nextElement( );
				data.setInstallShield( true );
				if ( zipEntry.getName( ).toLowerCase( ).startsWith( "brdpro" ) )
				{
					data.setInstallShield( false );
				}
				if ( data.isInstallShield( ) )
				{
					while ( enumeration.hasMoreElements( ) )
					{
						zipEntry = (ZipEntry) enumeration.nextElement( );
						if ( zipEntry.getName( )
								.matches( "(?i)Report Files/.+txt" ) )
						{
							String filePath = System.getProperty( "java.io.tmpdir" )
									+ "\\log"
									+ System.currentTimeMillis( )
									+ ".txt";
							File tempFile = new File( filePath );
							if ( !tempFile.exists( ) )
							{
								if ( !tempFile.getParentFile( ).exists( ) )
								{
									tempFile.getParentFile( ).mkdirs( );
								}
								tempFile.createNewFile( );
							}
							FileUtil.writeToBinarayFile( tempFile,
									zipFile.getInputStream( zipEntry ),
									true );
							data.setModuleVersion( checkModuleVersion( tempFile ) );
							break;
						}
					}
				}
				else
				{
					data.setModuleVersion( new ModuleVersion( ) );
					while ( enumeration.hasMoreElements( ) )
					{
						zipEntry = (ZipEntry) enumeration.nextElement( );
						String entryName = zipEntry.getName( ).replace( '/',
								'\\' );
						checkVersion( entryName, data.getModuleVersion( ) );
					}
				}
				zipFile.close( );
			}
			catch ( IOException e )
			{
				e.printStackTrace( );
			}
		}
		else
		{
			try
			{
				data.setInstallShield( true );
				File reportFile = new File( data.getTempDir( ),
						"brdpro\\install\\Report Files" );
				File tempFile = reportFile.listFiles( )[0];
				data.setModuleVersion( checkModuleVersion( tempFile ) );
			}
			catch ( IOException e )
			{
				e.printStackTrace( );
			}
		}
	}

	private ModuleVersion checkModuleVersion( File tempFile )
			throws IOException
	{

		if ( !tempFile.exists( ) )
			return null;

		String line = null;
		BufferedReader in = new BufferedReader( new InputStreamReader( new FileInputStream( tempFile ) ) );

		ModuleVersion version = new ModuleVersion( );
		while ( ( line = in.readLine( ) ) != null )
		{
			checkVersion( line, version );
		}
		in.close( );
		return version;
	}

	private void checkVersion( String line, ModuleVersion version )
	{
		boolean isFeature = false;
		{
			Pattern pattern = Pattern.compile( ECLIPSE_FEATURE_PATTERN );
			Matcher matcher = pattern.matcher( line );
			if ( matcher.find( ) )
			{
				isFeature = true;
			}
		}

		if ( isFeature )
		{
			if ( version.emf == null )
			{
				Pattern pattern = Pattern.compile( EMF_VERSION_PATTERN );
				Matcher matcher = pattern.matcher( line );
				if ( matcher.find( ) )
				{
					version.emf = matcher.group( )
							.toLowerCase( )
							.trim( )
							.replace( ECLIPSE_FEATURES, "" )
							.replace( ORG_ECLIPSE_EMF, "" );
					return;
				}
			}
			if ( version.gef == null )
			{
				Pattern pattern = Pattern.compile( GEF_VERSION_PATTERN );
				Matcher matcher = pattern.matcher( line );
				if ( matcher.find( ) )
				{
					version.gef = matcher.group( )
							.toLowerCase( )
							.trim( )
							.replace( ECLIPSE_FEATURES, "" )
							.replace( ORG_ECLIPSE_GEF, "" );
					return;
				}
			}
			if ( version.dtp == null )
			{
				Pattern pattern = Pattern.compile( DTP_VERSION_PATTERN );
				Matcher matcher = pattern.matcher( line );
				if ( matcher.find( ) )
				{
					version.dtp = matcher.group( )
							.toLowerCase( )
							.trim( )
							.replace( ECLIPSE_FEATURES, "" )
							.replace( ORG_ECLIPSE_DTP, "" );
					return;
				}
			}
			if ( version.wtp == null )
			{
				Pattern pattern = Pattern.compile( WTP_VERSION_PATTERN );
				Matcher matcher = pattern.matcher( line );
				if ( matcher.find( ) )
				{
					version.wtp = matcher.group( )
							.toLowerCase( )
							.trim( )
							.replace( ECLIPSE_FEATURES, "" )
							.replace( ORG_ECLIPSE_WTP, "" );
					return;
				}
			}
		}
		else
		{
			if ( version.eclipse == null )
			{
				Pattern pattern = Pattern.compile( ECLIPSE_VERSION_PATTERN );
				Matcher matcher = pattern.matcher( line );
				if ( matcher.find( ) )
				{
					version.eclipse = matcher.group( )
							.toLowerCase( )
							.trim( )
							.replace( ECLIPSE_PLUGINS, "" )
							.replace( ORG_ECLIPSE_PLATFORM, "" );
					return;
				}
			}
		}
	}

}
