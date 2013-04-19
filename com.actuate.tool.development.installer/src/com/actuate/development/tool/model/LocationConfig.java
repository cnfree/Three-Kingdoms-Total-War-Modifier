
package com.actuate.development.tool.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.sf.feeling.swt.win32.extension.io.FileSystem;
import org.sf.feeling.swt.win32.extension.io.Network;

public class LocationConfig
{

	private static final String MACHINE_QAANT = "qaant";
	private static final String MACHINE_FS1_LNX = "fs1-lnx";
	private static final String CONF_LOCATION_INI = "/conf/location.ini";
	public final static String LOCATION = "location";
	public final static String SHANGHAI = "sh";
	public final static String HEADQUARTER = "hq";

	private static Properties props = new Properties( );

	static
	{
		File config = new File( FileSystem.getCurrentDirectory( )
				+ CONF_LOCATION_INI );
		if ( !config.exists( ) )
		{
			try
			{
				File dir = config.getParentFile( );
				if ( !dir.exists( ) )
					dir.mkdirs( );
				config.createNewFile( );
			}
			catch ( IOException e )
			{
			}
		}
		try
		{
			if ( config.exists( ) )
			{
				InputStream in = new FileInputStream( config );
				props.load( in );
				in.close( );
			}
			if ( !props.containsKey( LOCATION ) )
			{
				int usTime = Network.ping( MACHINE_FS1_LNX, 32 );
				usTime = usTime == -1 ? 10000 : usTime;
				int cnTime = Network.ping( MACHINE_QAANT, 32 );
				cnTime = cnTime == -1 ? 10000 : cnTime;
				if ( cnTime >= usTime )
				{
					props.put( LOCATION, HEADQUARTER );
				}
				else
				{
					props.put( LOCATION, SHANGHAI );
				}
				props.store( new FileOutputStream( config ), null );
			}
		}
		catch ( Exception e )
		{
			Logger.getLogger( LocationConfig.class.getName( ) )
					.log( Level.WARNING, "Load properties failed.", e );
		}
	}

	public static String getProperty( String property, String defaultValue )
	{
		return props.getProperty( property, defaultValue );
	}

	public static String getLocation( )
	{
		return props.getProperty( LOCATION );
	}

	public static void setLocation( String location )
	{
		try
		{
			props.setProperty( LOCATION, location );
			File config = new File( FileSystem.getCurrentDirectory( )
					+ CONF_LOCATION_INI );
			if ( !config.exists( ) )
			{

				File dir = config.getParentFile( );
				if ( !dir.exists( ) )
					dir.mkdirs( );
				config.createNewFile( );

			}
			props.store( new FileOutputStream( config ), null );
		}
		catch ( IOException e )
		{
			Logger.getLogger( LocationConfig.class.getName( ) )
					.log( Level.WARNING, "Save properties failed.", e );
		}
	}
}
