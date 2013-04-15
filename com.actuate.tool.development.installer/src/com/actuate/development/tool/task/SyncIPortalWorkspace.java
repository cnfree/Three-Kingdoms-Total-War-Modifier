
package com.actuate.development.tool.task;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.sf.feeling.swt.win32.extension.io.FileSystem;
import org.sf.feeling.swt.win32.extension.shell.Windows;
import org.sf.feeling.swt.win32.extension.system.Kernel;
import org.sf.feeling.swt.win32.extension.system.ProcessEntry;

import com.actuate.development.tool.model.IPortalViewerData;
import com.actuate.development.tool.model.Modules;
import com.actuate.development.tool.util.FileUtil;
import com.actuate.development.tool.util.LogUtil;
import com.actuate.development.tool.util.UIUtil;

public class SyncIPortalWorkspace
{

	private IPortalViewerData data;
	private Thread downloadThread;
	private Process antProcess;
	private boolean monitorAntProcess;

	public SyncIPortalWorkspace( IPortalViewerData data )
	{
		this.data = data;
	}

	public void execute( final IProgressMonitor monitor )
	{
		if ( data == null )
			return;

		startMonitorProcess( monitor );

		int total = 11;
		if ( data.isSkipSync( ) )
		{
			total -= 4;
		}
		else
		{
			if ( data.isRevertFiles( ) )
				total++;
		}
		monitor.beginTask( "Total " + total + " steps",
				IProgressMonitor.UNKNOWN );

		final int[] step = new int[1];
		final String[] stepDetail = new String[1];
		final String[] originRoot = new String[1];

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

		final boolean[] unzipFlag = new boolean[]{
			false
		};

		boolean[] downloadFlag = new boolean[]{
			false
		};

		try
		{
			if ( !data.isSkipSync( ) )
			{
				monitor.subTask( "[Step "
						+ ++step[0]
						+ "] Checking the perforce connection settings..." );
				stepDetail[0] = "Check the perforce connection settings";

				final String result = checkP4ConnectionSettings( );

				if ( result != null )
				{
					Display.getDefault( ).syncExec( new Runnable( ) {

						public void run( )
						{
							if ( UIUtil.getShell( ).getMinimized( ) )
								Windows.flashWindow( UIUtil.getShell( ).handle,
										true );
							MessageDialog.openError( null, "Error", result );
							Windows.flashWindow( UIUtil.getShell( ).handle,
									false );
						}
					} );
					return;
				}

				monitor.subTask( "[Step "
						+ ++step[0]
						+ "] Updating the perforce client workspace specification from "
						+ data.getServer( ) );
				stepDetail[0] = "Update the perforce client workspace specification from "
						+ data.getServer( );

				originRoot[0] = updateClientSpecification( FileUtil.getTempFile( "specification.txt" ),
						data.getRoot( ) );

				if ( originRoot[0] == null )
				{
					return;
				}

				if ( data.isRevertFiles( ) )
				{
					monitor.subTask( "[Step "
							+ ++step[0]
							+ "] Reverting the iPortal Viewer workspace..." );
					stepDetail[0] = "Revert the iPortal Viewer workspace";

					if ( originRoot[0] != null )
					{
						revertiPortal( monitor, step );
					}
				}

				monitor.subTask( "[Step "
						+ ++step[0]
						+ "] Synchronizing the iPortal Viewer workspace..." );
				stepDetail[0] = "Synchronize the iPortal Viewer workspace";

				if ( originRoot[0] != null )
				{
					synciPortal( monitor, step );
				}

				monitor.subTask( "[Step "
						+ ++step[0]
						+ "] Resetting the perforce client workspace specification from "
						+ data.getServer( ) );
				stepDetail[0] = "Reset the perforce client workspace specification from "
						+ data.getServer( );

				if ( originRoot[0] != null )
				{
					updateClientSpecification( FileUtil.getTempFile( "specification.txt",
							".txt" ),
							originRoot[0] );

					originRoot[0] = null;
				}
			}

			if ( !monitor.isCanceled( ) )
			{
				initTask( monitor, step, stepDetail );
			}

			if ( !monitor.isCanceled( ) )
			{
				downloadIPortal( monitor, step, stepDetail, downloadFlag );
			}

			if ( !monitor.isCanceled( ) )
			{
				unzipWebViewer( monitor,
						step,
						stepDetail,
						p,
						helper,
						consoleLogger,
						unzipFlag );
			}

			unzipFlag[0] = true;

			if ( !monitor.isCanceled( ) )
			{
				Thread.sleep( 300 );
				replaceIProtal( monitor, step, stepDetail );
			}

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
				updateClassPath( monitor, step, stepDetail );
			}

			if ( !monitor.isCanceled( ) )
			{
				cleanTempFiles( monitor, step, stepDetail );
			}

			p.fireBuildFinished( null );

			if ( !monitor.isCanceled( ) )
			{
				finishTask( monitor );
			}
			else
			{
				cancelTask( );
			}
		}
		catch ( final Exception e )
		{
			p.fireBuildFinished( e );

			if ( originRoot[0] != null )
			{
				updateClientSpecification( FileUtil.getTempFile( "specification.txt",
						".txt" ),
						originRoot[0] );
			}
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

	private void updateClassPath( final IProgressMonitor monitor,
			final int[] step, final String[] stepDetail ) throws IOException
	{
		monitor.subTask( "[Step "
				+ ++step[0]
				+ "] Updating the iPortal Viewer workspace class path..." );
		stepDetail[0] = "Update the iPortal Viewer workspace class path";
		File classPathFile = new File( data.getRoot( )
				+ File.separatorChar
				+ data.getView( ), ".classpath" );
		if ( classPathFile.exists( ) )
		{
			updateClassPath( classPathFile );
		}

		if ( data.getCustomProjectName( ) != null
				&& data.getCustomProjectName( ).trim( ).length( ) > 0 )
		{
			File projectFile = new File( data.getRoot( )
					+ File.separatorChar
					+ data.getView( ), ".project" );
			if ( projectFile.exists( ) )
			{
				updateProjetName( projectFile, data.getCustomProjectName( )
						.trim( ) );
			}
		}
	}

	private void replaceIProtal( final IProgressMonitor monitor,
			final int[] step, final String[] stepDetail ) throws IOException,
			InterruptedException
	{
		monitor.subTask( "[Step "
				+ ++step[0]
				+ "] Replacing the iPortal Viewer workspace files..." );
		stepDetail[0] = "Replace the iPortal Viewer workspace files";
		File reaplceFile = getAntFile( "/templates/Replace_IV.xml" );

		antProcess = Runtime.getRuntime( ).exec( new String[]{
				System.getProperty( "java.home" ) + "/bin/java",
				"-cp",
				System.getProperty( "java.class.path" ),
				AntTask.class.getName( ),
				"\"" + reaplceFile.getAbsolutePath( ) + "\"",
				"replace"
		} );
		antProcess.waitFor( );
		antProcess = null;
	}

	private void unzipWebViewer( final IProgressMonitor monitor,
			final int[] step, final String[] stepDetail, Project p,
			ProjectHelper helper, final DefaultLogger consoleLogger,
			final boolean[] flag )
	{
		String[] subtaskName = new String[]{
			"[Step "
					+ ++step[0]
					+ "] Extracting the iPortal Viewer archive file..."
		};
		monitor.subTask( subtaskName[0] );
		stepDetail[0] = "Extract the iPortal Viewer archive file";
		String extractXML = "/templates/Extract_IV.xml";
		File warFile = new File( data.getBirtViewerFile( ) );
		if ( !warFile.getName( ).toLowerCase( ).endsWith( ".zip" ) )
			extractXML = "/templates/Extract_IV_II.xml";
		File extractFile = getAntFile( extractXML );
		helper.parse( p, extractFile );

		interruptOutput( monitor, step, consoleLogger, flag, subtaskName );

		p.executeTarget( "unzip_webviewer" );
	}

	private void downloadIPortal( final IProgressMonitor monitor,
			final int[] step, final String[] stepDetail, boolean[] downloadFlag )
			throws IOException, InterruptedException
	{
		String defaultTaskName = "[Step "
				+ ++step[0]
				+ "] Downloading the iPortal Viewer archive file...";

		File warFile = new File( data.getBirtViewerFile( ) );
		long fileLength = warFile.length( );
		downloadMonitor( monitor,
				downloadFlag,
				defaultTaskName,
				new File( data.getRoot( ) + "\\temp", warFile.getName( ) ),
				fileLength,
				step );

		monitor.subTask( defaultTaskName
				+ "\t[ Size: "
				+ FileUtils.byteCountToDisplaySize( fileLength )
				+ " ] " );
		stepDetail[0] = "Download the iPortal Viewer archive file";
		File downloadFile = getAntFile( "/templates/Download_IV.xml" );
		antProcess = Runtime.getRuntime( ).exec( new String[]{
				System.getProperty( "java.home" ) + "/bin/java",
				"-cp",
				System.getProperty( "java.class.path" ),
				AntTask.class.getName( ),
				"\"" + downloadFile.getAbsolutePath( ) + "\"",
				"download"
		} );
		antProcess.waitFor( );
		antProcess = null;

		downloadFlag[0] = true;
	}

	private void initTask( final IProgressMonitor monitor, final int[] step,
			final String[] stepDetail ) throws IOException,
			InterruptedException
	{
		monitor.subTask( "[Step "
				+ ++step[0]
				+ "] Initializing the iPortal Viewer workspace replacement task..." );
		stepDetail[0] = "Initialize the iPortal Viewer workspace replacement task";

		File initFile = getAntFile( "/templates/Init_IV.xml" );

		antProcess = Runtime.getRuntime( ).exec( new String[]{
				System.getProperty( "java.home" ) + "/bin/java",
				"-cp",
				System.getProperty( "java.class.path" ),
				AntTask.class.getName( ),
				"\"" + initFile.getAbsolutePath( ) + "\"",
				"init"
		} );
		antProcess.waitFor( );
		antProcess = null;
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
						"Canceled synchronizing the iPortal Viewer workspace." );
				Windows.flashWindow( UIUtil.getShell( ).handle, false );
			}
		} );
	}

	private void finishTask( final IProgressMonitor monitor )
	{
		monitor.subTask( "" );
		monitor.setTaskName( "Finished synchronizing the iPortal Viewer workspace" );

		Display.getDefault( ).syncExec( new Runnable( ) {

			public void run( )
			{
				if ( UIUtil.getShell( ).getMinimized( ) )
					Windows.flashWindow( UIUtil.getShell( ).handle, true );
				StringBuffer buffer = new StringBuffer( );
				buffer.append( "Synchronize the iPortal Viewer workspace sucessfully." );
				MessageDialog.openInformation( null,
						"Information",
						buffer.toString( ) );
				Windows.flashWindow( UIUtil.getShell( ).handle, false );
			}
		} );
	}

	private void cleanTempFiles( final IProgressMonitor monitor,
			final int[] step, final String[] stepDetail ) throws IOException,
			InterruptedException
	{
		monitor.subTask( "[Step "
				+ ++step[0]
				+ "] Cleaning the temporary files..." );
		stepDetail[0] = "Clean the temporary files";
		File cleanFile = getAntFile( "/templates/Clean_IV.xml" );
		antProcess = Runtime.getRuntime( ).exec( new String[]{
				System.getProperty( "java.home" ) + "/bin/java",
				"-cp",
				System.getProperty( "java.class.path" ),
				AntTask.class.getName( ),
				"\"" + cleanFile.getAbsolutePath( ) + "\"",
				"clean"
		} );
		antProcess.waitFor( );
		antProcess = null;
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
				+ "\\custom\\IV_Task.xml" );
		if ( !antFile.exists( ) )
		{
			FileUtil.writeToBinarayFile( antFile,
					InstallBRDPro.class.getResourceAsStream( "/custom/IV_Task.xml" ),
					true );
		}

		File customFile = getAntFile( antFile.getAbsolutePath( ) );

		antProcess = Runtime.getRuntime( ).exec( new String[]{
				System.getProperty( "java.home" ) + "/bin/java",
				"-cp",
				System.getProperty( "java.class.path" ),
				AntTask.class.getName( ),
				"\"" + customFile.getAbsolutePath( ) + "\"",
				"custom"
		} );

		interruptCustomTaskErrorMessage( antProcess, errorMessage );

		int result = antProcess.waitFor( );
		antProcess = null;
		return result;
	}

	private void interruptCustomTaskErrorMessage( final Process process,
			final StringBuffer buffer )
	{
		Thread errThread = new Thread( "Monitor Custom Task" ) {

			public void run( )
			{
				try
				{
					BufferedReader input = new BufferedReader( new InputStreamReader( process.getErrorStream( ) ) );
					String line;
					while ( ( line = input.readLine( ) ) != null )
					{
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
											.startsWith( "7z" ) )
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

	private void updateProjetName( File projectFile, String projectName )
	{
		Map<String, String> map = new HashMap<String, String>( );
		map.put( "<name>.+?</name>", "<name>" + projectName + "</name>" );
		FileUtil.replaceFile( projectFile,
				"<projectDescription>.+?</name>",
				map );

	}

	private void updateClassPath( File classPathFile ) throws IOException
	{
		int sizeL = (int) classPathFile.length( );
		int chars_read = 0;
		BufferedReader in = new BufferedReader( new InputStreamReader( new FileInputStream( classPathFile ) ) );
		char[] data = new char[sizeL];
		while ( in.ready( ) )
		{
			chars_read += in.read( data, chars_read, sizeL - chars_read );
		}
		in.close( );
		char[] v = new char[chars_read];
		System.arraycopy( data, 0, v, 0, chars_read );
		String temp = new String( v );
		String[] entries = temp.replaceAll( "\r\n", "\n" ).split( "\n" );
		List<String> list = new ArrayList<String>( );
		Pattern pattern = Pattern.compile( "\\{.*?\\}" );
		for ( int i = 0; i < entries.length; i++ )
		{
			Matcher matcher = pattern.matcher( entries[i] );
			if ( matcher.find( ) )
			{
				String group = matcher.group( );
				String path = group.substring( 1, group.length( ) - 1 );
				File root = new File( this.data.getRoot( )
						+ File.separatorChar
						+ this.data.getView( ) );
				File file = new File( root, path );
				final String filePattern = "(?i)" + file.getName( );
				File parent = file.getParentFile( );
				File[] children = null;
				if ( parent.exists( ) )
				{
					children = parent.listFiles( new FileFilter( ) {

						public boolean accept( File file )
						{
							String fileName = file.getName( );
							if ( fileName.matches( filePattern ) )
							{
								return true;
							}
							return false;
						}
					} );
				}
				if ( children != null && children.length > 0 )
				{
					for ( int j = 0; j < children.length; j++ )
					{
						File availableFile = new File( parent,
								children[j].getName( ) );
						String filePath = availableFile.getCanonicalPath( )
								.substring( root.getCanonicalPath( ).length( ) + 1 )
								.replace( '\\', '/' );
						String classPath = entries[i].replace( group, filePath );
						if ( !list.contains( classPath ) )
							list.add( classPath );
						else
						{
							System.out.println( "Duplicate Class Path:"
									+ classPath );
						}
					}
				}
			}
			else
			{
				if ( !list.contains( entries[i] ) )
					list.add( entries[i] );
				else
				{
					System.out.println( "Duplicate Class Path:" + entries[i] );
				}
			}
		}
		StringBuffer buffer = new StringBuffer( );
		for ( int i = 0; i < list.size( ); i++ )
		{
			buffer.append( list.get( i ) ).append( "\r\n" );
		}

		PrintWriter out = new PrintWriter( new BufferedWriter( new OutputStreamWriter( new FileOutputStream( classPathFile ) ) ),
				false );
		out.print( buffer.toString( ) );
		out.close( );
	}

	private void interruptOutput( final IProgressMonitor monitor,
			final int[] step, final DefaultLogger consoleLogger,
			final boolean[] flag, final String[] defaultTaskName )
	{

		Thread outputThread = new Thread( "Monitor Output" ) {

			public void run( )
			{
				try
				{
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
						if ( !flag[0] )
						{
							int index = line[0].indexOf( extactingStr );
							if ( index != -1 )
							{
								String file = line[0].substring( index + length );
								monitor.subTask( "[Step "
										+ step[0]
										+ "]"
										+ file );
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

	private File getAntFile( String fileName )
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
		setVolocitryContext( context );
		Template template = velocityEngine.getTemplate( templateFile.getName( ) );
		StringWriter sw = new StringWriter( );
		template.merge( context, sw );
		File tempFile = FileUtil.getTempFile( fileName );
		FileUtil.writeToFile( tempFile, sw.toString( ).trim( ) );

		return tempFile;
	}

	private void setVolocitryContext( VelocityContext context )
	{
		context.put( "p4Root", data.getRoot( ) );
		context.put( "p4View", data.getView( ) );
		File file = new File( data.getBirtViewerFile( ) );
		context.put( "buildPath", file.getParentFile( ).getAbsolutePath( ) );
		context.put( "buildFile", file.getName( ) );
		if ( file.getName( ).toLowerCase( ).endsWith( ".zip" ) )
			context.put( "warFile", "WL_TOMCAT_ActuateBIRTJavaComponent.war" );
		else
			context.put( "warFile", file.getName( ) );
		file = Modules.getInstance( )
				.getIPortalRepalceFile( data.getProject( ) );
		context.put( "replacePath", file.getParentFile( ).getAbsolutePath( ) );
		context.put( "replaceFile", file.getName( ) );
		context.put( "runtime", FileSystem.getCurrentDirectory( ) );
	}

	private void synciPortal( final IProgressMonitor monitor, final int[] step )
	{

		final boolean[] error = new boolean[1];
		final String[] errorMessage = new String[1];
		try
		{
			final Process downloadProcess = Runtime.getRuntime( )
					.exec( new String[]{
					"cmd",
							"/c",
							"p4 -p "
									+ data.getServer( )
									+ " -u "
									+ data.getUser( )
									+ " -P "
									+ data.getPassword( )
									+ " -c "
									+ data.getClient( )
									+ " sync "
									+ ( data.isForceOperation( ) ? "-f" : "" )
									+ " //"
									+ data.getView( )
									+ "/..."
									+ ( data.isLatestRevision( ) ? ""
											: ( data.getRevisionArg( ) != null ? data.getRevisionArg( )
													: "" ) )
					} );

			Thread errThread = new Thread( "Monitor Error Stream" ) {

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
					catch ( final Exception e )
					{
						Display.getDefault( ).syncExec( new Runnable( ) {

							public void run( )
							{
								Logger.getLogger( SyncIPortalWorkspace.class.getName( ) )
										.log( Level.WARNING,
												"Get error stream failed.", e ); //$NON-NLS-1$
							}
						} );
					}
				}
			};
			errThread.setDaemon( true );
			errThread.start( );

			Thread inThread = new Thread( "Monitor Input Stream" ) {

				public void run( )
				{
					try
					{
						BufferedReader input = new BufferedReader( new InputStreamReader( downloadProcess.getInputStream( ) ) );
						final String[] line = new String[1];
						while ( ( line[0] = input.readLine( ) ) != null )
						{
							monitor.subTask( "[Step "
									+ step[0]
									+ "] Synchronizing: "
									+ line[0] );
						}
						input.close( );
					}
					catch ( final Exception e )
					{
						Display.getDefault( ).syncExec( new Runnable( ) {

							public void run( )
							{
								Logger.getLogger( SyncIPortalWorkspace.class.getName( ) )
										.log( Level.WARNING,
												"Get error stream failed.", e ); //$NON-NLS-1$
							}
						} );
					}
				}
			};
			inThread.setDaemon( true );
			inThread.start( );

			final boolean[] processFinish = new boolean[]{
				false
			};

			Thread cancelThread = new Thread( "Monitor P4 Process" ) {

				public void run( )
				{
					while ( !processFinish[0] )
					{
						if ( monitor.isCanceled( ) )
						{
							downloadProcess.destroy( );

							ProcessEntry[] entrys = Kernel.getSystemProcessesSnap( );
							if ( entrys != null )
							{
								for ( int i = 0; i < entrys.length; i++ )
								{
									ProcessEntry entry = entrys[i];
									if ( entry.getProcessName( )
											.equalsIgnoreCase( "p4.exe" ) )
									{
										Kernel.killProcess( entry.getProcessId( ) );
									}
								}
							}
						}
						else
						{
							try
							{
								Thread.sleep( 100 );
							}
							catch ( InterruptedException e )
							{
							}
						}
					}
				}
			};
			cancelThread.setDaemon( true );
			cancelThread.start( );

			downloadProcess.waitFor( );

			processFinish[0] = true;

			Thread.sleep( 100 );

			if ( errorMessage[0] != null )
			{
				Display.getDefault( ).syncExec( new Runnable( ) {

					public void run( )
					{
						MessageDialog.openError( null, "Error", errorMessage[0] );
					}
				} );
			}

		}
		catch ( final Exception e )
		{
			Display.getDefault( ).syncExec( new Runnable( ) {

				public void run( )
				{
					MessageDialog.openError( null, "Error", e.getMessage( ) );
				}
			} );
		}

	}

	private void revertiPortal( final IProgressMonitor monitor, final int[] step )
	{

		final boolean[] error = new boolean[1];
		final String[] errorMessage = new String[1];
		try
		{
			final Process revertProcess = Runtime.getRuntime( )
					.exec( new String[]{
							"cmd",
							"/c",
							"p4 -p "
									+ data.getServer( )
									+ " -u "
									+ data.getUser( )
									+ " -P "
									+ data.getPassword( )
									+ " -c "
									+ data.getClient( )
									+ " revert //"
									+ data.getView( )
									+ "/..."
					} );

			Thread errThread = new Thread( "Monitor Error Stream" ) {

				public void run( )
				{
					try
					{
						BufferedReader input = new BufferedReader( new InputStreamReader( revertProcess.getErrorStream( ) ) );
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
					catch ( final Exception e )
					{
						Display.getDefault( ).syncExec( new Runnable( ) {

							public void run( )
							{
								Logger.getLogger( SyncIPortalWorkspace.class.getName( ) )
										.log( Level.WARNING,
												"Get error stream failed.", e ); //$NON-NLS-1$
							}
						} );
					}
				}
			};
			errThread.setDaemon( true );
			errThread.start( );

			Thread inThread = new Thread( "Monitor Input Stream" ) {

				public void run( )
				{
					try
					{
						BufferedReader input = new BufferedReader( new InputStreamReader( revertProcess.getInputStream( ) ) );
						final String[] line = new String[1];
						while ( ( line[0] = input.readLine( ) ) != null )
						{
							monitor.subTask( "[Step "
									+ step[0]
									+ "] Reverting: "
									+ line[0] );
						}
						input.close( );
					}
					catch ( final Exception e )
					{
						Display.getDefault( ).syncExec( new Runnable( ) {

							public void run( )
							{
								Logger.getLogger( SyncIPortalWorkspace.class.getName( ) )
										.log( Level.WARNING,
												"Get error stream failed.", e ); //$NON-NLS-1$
							}
						} );
					}
				}
			};
			inThread.setDaemon( true );
			inThread.start( );

			revertProcess.waitFor( );

			Thread.sleep( 100 );

			if ( errorMessage[0] != null )
			{
				Display.getDefault( ).syncExec( new Runnable( ) {

					public void run( )
					{
						MessageDialog dialog = new MessageDialog( null,
								"Error",
								null,
								errorMessage[0],
								1,
								new String[]{
									IDialogConstants.OK_LABEL
								},
								0 );
						dialog.setBlockOnOpen( false );
						dialog.open( );
					}
				} );
			}

		}
		catch ( final Exception e )
		{
			Display.getDefault( ).syncExec( new Runnable( ) {

				public void run( )
				{
					MessageDialog.openError( null, "Error", e.getMessage( ) );
				}
			} );
		}
	}

	private String updateClientSpecification( final File specFile,
			final String root )
	{
		final String[] originRoot = new String[1];

		Display.getDefault( ).syncExec( new Runnable( ) {

			public void run( )
			{
				final boolean[] error = new boolean[1];
				final String[] errorMessage = new String[1];
				try
				{
					final Process downloadProcess = Runtime.getRuntime( )
							.exec( new String[]{
									"cmd",
									"/c",
									"p4 -p "
											+ data.getServer( )
											+ " -u "
											+ data.getUser( )
											+ " -P "
											+ data.getPassword( )
											+ " client -o "
											+ data.getClient( )
											+ ">"
											+ "\""
											+ specFile.getAbsolutePath( )
											+ "\""
							} );

					Thread thread = new Thread( "Monitor Error Stream" ) {

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
								originRoot[0] = matcher.group( )
										.trim( )
										.split( "\\s+" )[1];
							}

							if ( originRoot[0] != null )
							{
								FileUtil.replaceFile( specFile,
										"(?i)Root:\\s+\\S+",
										"Root:	" + root );

								final Process uploadProcess = Runtime.getRuntime( )
										.exec( new String[]{
												"cmd",
												"/c",
												"p4 -p "
														+ data.getServer( )
														+ " -u "
														+ data.getUser( )
														+ " -P "
														+ data.getPassword( )
														+ " client -i "
														+ "<"
														+ "\""
														+ specFile.getAbsolutePath( )
														+ "\""
										} );

								thread = new Thread( "Monitor Error Stream" ) {

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
			}
		} );

		return originRoot[0];
	}

	private String checkP4ConnectionSettings( )
	{

		final boolean[] error = new boolean[1];
		final String[] errorMessage = new String[1];
		try
		{
			final Process p4Process = Runtime.getRuntime( ).exec( "p4 -p "
					+ data.getServer( )
					+ " -u "
					+ data.getUser( )
					+ " -P "
					+ data.getPassword( )
					+ " clients -u "
					+ data.getUser( ) );

			Thread thread = new Thread( "Monitor Error Stream" ) {

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
				String client = matcher.group( ).replaceAll( "(?i)Client\\s+",
						"" );
				if ( client.equalsIgnoreCase( data.getClient( ).trim( ) ) )
				{
					exist = true;
					break;
				}
			}
			if ( !error[0] )
			{
				if ( !exist )
				{
					errorMessage[0] = "The client "
							+ data.getClient( ).trim( )
							+ " is unavailable.";
				}
			}
		}
		catch ( Exception e )
		{
			errorMessage[0] = e.getMessage( );
		}
		return errorMessage[0];
	}

	private void downloadMonitor( final IProgressMonitor monitor,
			final boolean[] flag, final String defaultTaskName,
			final File file, final long size, final int[] step )
	{
		downloadThread = new Thread( "Monitor Download" ) {

			public void run( )
			{
				int currentStep = step[0];
				long donwloadSize = file.length( );
				long time = System.currentTimeMillis( );
				while ( !flag[0] )
				{
					if ( currentStep != step[0] )
						break;
					if ( file.exists( ) )
					{
						if ( file.length( ) != donwloadSize )
						{
							if ( currentStep != step[0] )
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
						Thread.sleep( 1000 );
					}
					catch ( InterruptedException e )
					{
					}
				}
			}
		};
		downloadThread.start( );

	}
}
