
package com.actuate.tool.development.installer.task;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.sf.feeling.swt.win32.extension.shell.Windows;

import com.actuate.tool.development.installer.model.IPortalViewerData;
import com.actuate.tool.development.installer.util.FileUtil;
import com.actuate.tool.development.installer.util.LogUtil;
import com.actuate.tool.development.installer.util.UIUtil;

public class SyncIPortalWorkspace
{

	private IPortalViewerData data;

	public SyncIPortalWorkspace( IPortalViewerData data )
	{
		this.data = data;
	}

	public void execute( final IProgressMonitor monitor )
	{
		if ( data == null )
			return;

		monitor.beginTask( "", IProgressMonitor.UNKNOWN );

		final int[] step = new int[1];
		final String[] stepDetail = new String[1];
		final String[] originRoot = new String[1];
		try
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
						MessageDialog.openError( null, "Error", result );
						Windows.flashWindow( UIUtil.getShell( ).handle, false );
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

			originRoot[0] = updateClientSpecification( getTempFile( "specification",
					".txt" ),
					data.getRoot( ) );

			if ( originRoot[0] == null )
			{
				Display.getDefault( ).syncExec( new Runnable( ) {

					public void run( )
					{
						Windows.flashWindow( UIUtil.getShell( ).handle, false );
					}
				} );
				return;
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
				updateClientSpecification( getTempFile( "specification", ".txt" ),
						originRoot[0] );
			}

		}
		catch ( final Exception e )
		{
			if ( originRoot[0] != null )
			{
				updateClientSpecification( getTempFile( "specification", ".txt" ),
						originRoot[0] );
			}
			Display.getDefault( ).syncExec( new Runnable( ) {

				public void run( )
				{
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
					} );

			Thread errThread = new Thread( ) {

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

			Thread inThread = new Thread( ) {

				public void run( )
				{
					try
					{
						BufferedReader input = new BufferedReader( new InputStreamReader( downloadProcess.getInputStream( ) ) );
						final String[] line = new String[1];
						while ( ( line[0] = input.readLine( ) ) != null )
						{
							monitor.subTask( "[Step " + step[0] + "] Synchronizing: " + line[0] );
						}
						input.close( );
					}
					catch (final Exception e )
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

			downloadProcess.waitFor( );

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
		catch (final Exception e )
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

	private File getTempFile( String config, String suffix )
	{
		String filePath = System.getProperty( "java.io.tmpdir" )
				+ System.currentTimeMillis( )
				+ "\\"
				+ config
				+ suffix;
		File configFile = new File( filePath );
		if ( !configFile.exists( ) )
		{
			if ( !configFile.getParentFile( ).exists( ) )
			{
				configFile.getParentFile( ).mkdirs( );
			}
		}
		return configFile;
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
}
