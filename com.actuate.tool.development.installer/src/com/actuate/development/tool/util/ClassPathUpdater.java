
package com.actuate.development.tool.util;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.sf.feeling.swt.win32.extension.io.FileSystem;

public final class ClassPathUpdater
{

	private static Method addURL = initAddMethod( );

	private static URLClassLoader classloader = (URLClassLoader) ClassLoader.getSystemClassLoader( );

	private static Method initAddMethod( )
	{
		try
		{
			Method add = URLClassLoader.class.getDeclaredMethod( "addURL",
					new Class[]{
						URL.class
					} );
			add.setAccessible( true );
			return add;
		}
		catch ( Exception e )
		{
			throw new RuntimeException( e );
		}
	}

	public static void loadClasspath( )
	{
		List<String> files = getJarFiles( );
		for ( String f : files )
		{
			loadClasspath( f );
		}

		List<String> resFiles = getResFiles( );

		for ( String r : resFiles )
		{
			loadResourceDir( r );
		}
	}

	private static void loadClasspath( String filepath )
	{
		File file = new File( filepath );
		loopFiles( file );
	}

	private static void loadResourceDir( String filepath )
	{
		File file = new File( filepath );
		loopDirs( file );
	}

	private static void loopDirs( File file )
	{
		if ( file.isDirectory( ) )
		{
			addURL( file );
			File[] tmps = file.listFiles( );
			for ( File tmp : tmps )
			{
				loopDirs( tmp );
			}
		}
	}

	private static void loopFiles( File file )
	{
		if ( file.isDirectory( ) )
		{
			File[] tmps = file.listFiles( );
			for ( File tmp : tmps )
			{
				loopFiles( tmp );
			}
		}
		else
		{
			if ( file.getAbsolutePath( ).toLowerCase( ).endsWith( ".jar" )
					|| file.getAbsolutePath( ).toLowerCase( ).endsWith( ".zip" ) )
			{
				addURL( file );
			}
		}
	}

	private static void addURL( File file )
	{
		try
		{
			addURL.invoke( classloader, new Object[]{
				file.toURI( ).toURL( )
			} );
		}
		catch ( Exception e )
		{
			LogUtil.recordErrorMsg( e, true );
		}
	}

	private static List<String> getJarFiles( )
	{
		List<String> jars = new ArrayList<String>( );
		File ext = new File( FileSystem.getCurrentDirectory( ) + "/ext" );
		if ( ext.exists( ) )
		{
			File[] files = ext.listFiles( );
			if ( files != null && files.length > 0 )
			{
				for ( int i = 0; i < files.length; i++ )
				{
					if ( files[i].isFile( ) )
					{
						jars.add( files[i].getAbsolutePath( ) );
					}
				}
			}
		}
		return jars;
	}

	private static List<String> getResFiles( )
	{
		List<String> resources = new ArrayList<String>( );
		File ext = new File( FileSystem.getCurrentDirectory( )
				+ "/ext/resources" );
		if ( ext.exists( ) )
		{
			resources.add( ext.getAbsolutePath( ) );
		}
		return resources;
	}
}
