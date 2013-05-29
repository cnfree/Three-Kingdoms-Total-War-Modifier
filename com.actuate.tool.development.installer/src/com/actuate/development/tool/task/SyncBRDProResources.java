
package com.actuate.development.tool.task;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
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

import com.actuate.development.tool.config.PathConfig;
import com.actuate.development.tool.model.Version;
import com.actuate.development.tool.model.feature.SyncBRDProResourcesData;

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

	public SyncBRDProResources( SyncBRDProResourcesData data )
	{
		this.data = data;
	}

	public void execute( IProgressMonitor monitor )
	{
		if ( data == null )
			return;

		monitor.beginTask( "Synchronizing the BRDPro resource files", 100000 );

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

		final long[] allLength = new long[]{
			0L
		};
		File platformDir = new File( "\\\\QA-BUILD\\BIRTOutput\\platform" );
		final Map<String, List<File>> sdkFiles = new HashMap<String, List<File>>( );
		listSDKFiles( syncVersions, allLength, platformDir, sdkFiles );

		final List<String> pluginVersions = new ArrayList<String>( );
		if ( data.getPluginVersions( ) != null )
		{
			pluginVersions.addAll( Arrays.asList( data.getPluginVersions( ) ) );
		}

		File pluginDir = new File( "\\\\qaant\\QA\\Toolkit\\plugins" );
		final File[] pluginFiles = listToolkitPlugins( allLength,
				pluginVersions,
				pluginDir );

		File targetDir = new File( data.getTargetDirectory( ) );
		if ( !targetDir.exists( ) )
			targetDir.mkdirs( );
		{
			copySDKFiles( platformDir, sdkFiles );
			copyToolkitPluginFiles( pluginDir, pluginFiles );
		}
	}

	private void copyToolkitPluginFiles( File pluginDir,
			final File[] pluginFiles )
	{
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
			File sourceFile = movePluginFiles.get( i );
			if ( sourceFile.isDirectory( ) )
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
				try
				{
					FileUtils.copyFile( sourceFile, targetFile );
				}
				catch ( IOException e )
				{
					Logger.getLogger( SyncBRDProResources.class.getName( ) )
							.log( Level.WARNING,
									"Copy file " + sourceFile.getAbsolutePath( ) + " failed.", //$NON-NLS-1$
									e );
				}
			}
		}
	}

	private void copySDKFiles( File platformDir,
			final Map<String, List<File>> sdkFiles )
	{
		File targetPlatform = new File( data.getTargetDirectory( ), "platform" );

		String platformPath = platformDir.getAbsolutePath( );
		Iterator<String> iter = sdkFiles.keySet( ).iterator( );
		while ( iter.hasNext( ) )
		{
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
						try
						{
							FileUtils.copyFile( sourceFile, targetFile );
						}
						catch ( IOException e )
						{
							Logger.getLogger( SyncBRDProResources.class.getName( ) )
									.log( Level.WARNING,
											"Copy file " + sourceFile.getAbsolutePath( ) + " failed.", //$NON-NLS-1$
											e );
						}
					}
				}
			}
		}
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

	private File[] listToolkitPlugins( final long[] allLength,
			final List<String> pluginVersions, File pluginDir )
	{
		return pluginDir.listFiles( new FileFilter( ) {

			public boolean accept( File file )
			{
				if ( file.isDirectory( )
						&& file.getName( ).matches( "\\d+\\.\\d+" ) )
				{
					if ( pluginVersions.contains( file.getName( ) ) )
					{
						allLength[0] += FileUtils.sizeOfDirectory( file );
						return true;
					}
					return false;
				}
				else
				{
					if ( file.isDirectory( ) )
					{
						allLength[0] += FileUtils.sizeOfDirectory( file );
					}
					else
					{
						allLength[0] += file.length( );
					}
					return true;
				}

			}
		} );
	}

	private void listSDKFiles( final List<String> syncVersions,
			final long[] allLength, File platformDir,
			final Map<String, List<File>> sdkFiles )
	{
		platformDir.listFiles( new FileFilter( ) {

			public boolean accept( File file )
			{
				if ( file.isDirectory( )
						&& file.getName( ).matches( "(?i).+?_platform" ) )
				{
					String version = file.getName( ).split( "_" )[0];
					if ( syncVersions.contains( version ) )
					{
						file.listFiles( new FileFilter( ) {

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
										String parentFileName = file.getParentFile( )
												.getName( );
										if ( !sdkFiles.containsKey( parentFileName ) )
										{
											sdkFiles.put( parentFileName,
													new ArrayList<File>( ) );
										}
										sdkFiles.get( parentFileName )
												.add( file );
										allLength[0] += file.length( );
									}
								}
								return false;
							}
						} );
					}
				}
				return false;
			}
		} );
	}
}
