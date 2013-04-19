
package com.actuate.development.tool.model.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.sf.feeling.swt.win32.extension.io.FileSystem;

import com.actuate.development.tool.util.FileUtil;

public class UserDataConfig
{

	public final static String BRDPRO_SHORTCUT_ARGS = "brdpro_shortcut_args";

	public final static String DEFAULT_BRDPRO_PROJECT = "default_brdpro_project";

	public final static String DEFAULT_IPORTAL_PROJECT = "default_iportal_project";

	private static Properties props = new Properties( );

	static
	{
		File config = new File( FileSystem.getCurrentDirectory( )
				+ "/conf/data.ini" );
		if ( !config.exists( ) )
		{
			FileUtil.writeToBinarayFile( config,
					UserDataConfig.class.getResourceAsStream( "/conf/data.ini" ),
					true );
		}
		try
		{

			if ( config.exists( ) )
			{
				InputStream in = new FileInputStream( config );
				props.load( in );
				in.close( );
			}
			else
			{
				InputStream in = UserDataConfig.class.getResourceAsStream( "/conf/data.ini" );
				props.load( in );
				in.close( );
			}

			Set set = props.entrySet( );
			Entry[] entries = (Entry[]) set.toArray( new Entry[0] );
			for ( int i = 0; i < entries.length; i++ )
			{
				Entry entry = entries[i];
				String key = (String) entry.getKey( );
				String value = (String) entry.getValue( );
				props.remove( entry.getKey( ) );
				props.put( FileUtil.convert( key ), FileUtil.convert( value ) );
			}
		}
		catch ( Exception e )
		{
			Logger.getLogger( UserDataConfig.class.getName( ) )
					.log( Level.WARNING, "Load properties failed.", e );
		}
	}

	public static String getProperty( String property, String defaultValue )
	{
		return props.getProperty( property, defaultValue );
	}

}
