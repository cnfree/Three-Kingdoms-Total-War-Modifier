
package com.actuate.development.tool.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.sf.feeling.swt.win32.extension.io.FileSystem;

import com.actuate.development.tool.util.FileUtil;
import com.actuate.development.tool.util.LogUtil;

public class PathConfig
{

	private static final String PATH_CONF = "/conf/";

	private static final String PATH_SH_INI = "path_sh.ini";

	private static final String PATH_HQ_INI = "path_hq.ini";

	// Toolkit plugins directory
	public final static String PLUGINS = "plugins";

	// Actuate build directory
	public final static String ACTUATE_BUILD_DIR = "actuate_build_dir";
	// Sub directory of Actuate build directory name regex, ';' is the separator
	// char
	public final static String ACTUATE_BUILD_SUB_DIR = "actuate_build_sub_dir";

	// BRDPro file name regex, ';' is the separator char
	public final static String BRDPRO = "brdpro";
	// iPortal Viewer file name regex, ';' is the separator char
	public final static String IPORTAL = "iportal";

	// Eclipse platform directory, the path must contain eclipse version like
	// "4.2.1"
	public final static String PLATFORM = "platform";
	// DTP SDK daily build output directory
	public final static String DTP_OUTPUT = "dtp_output";

	// Eclipse SDK file name regex in the platform directory
	public final static String ECLIPSE_SDK = "eclipse_sdk";
	// GEF SDK file name regex in the platform directory
	public final static String GEF_SDK = "gef_sdk";
	// EMF SDK file name regex in the platform directory
	public final static String EMF_SDK = "emf_sdk";
	// WTP SDK file name regex in the platform directory
	public final static String WTP_SDK = "wtp_sdk";
	// DTP SDK file name regex in the dtp output directory
	public final static String DTP_SDK = "dtp_sdk";

	public static final String HQ_PROJECT_ACTUATE_BUILD_DIR = "hq_project_actuate_build_dir";

	public static final String HQ_RELEASE_ACTUATE_BUILD_DIR = "hq_release_actuate_build_dir";

	public static final String PLATFORM_VERSION_DIR = "platform_version_dir";

	public static final String HQ_PROJECT_VIEWER_WAR_DIR = "hq_project_viewer_war_dir";

	private static Properties props = new Properties( );

	static
	{
		load( );
	}

	public static void load( )
	{
		String location = LocationConfig.getLocation( );
		String path;
		if ( LocationConfig.HEADQUARTER.equals( location ) )
		{
			path = PATH_HQ_INI;
		}
		else
		{
			path = PATH_SH_INI;
		}
		File config = new File( FileSystem.getCurrentDirectory( )
				+ PATH_CONF
				+ path );
		if ( !config.exists( ) )
		{
			FileUtil.writeToBinarayFile( config,
					PathConfig.class.getResourceAsStream( PATH_CONF + path ),
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
				InputStream in = PathConfig.class.getResourceAsStream( PATH_CONF
						+ path );
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
			Logger.getLogger( PathConfig.class.getName( ) ).log( Level.WARNING,
					"Load properties failed.",
					e );
		}
	}

	public static String getProperty( String property, String defaultValue )
	{
		if ( !props.containsKey( property ) )
		{
			props.put( property, defaultValue );
		}
		return props.getProperty( property );
	}

	public static String getProperty( String property )
	{
		return props.getProperty( property );
	}

	public static void setProperty( String property, String value )
	{
		props.setProperty( property, value );
		String location = LocationConfig.getLocation( );
		if ( LocationConfig.HEADQUARTER.equals( location ) )
		{
			String path = PATH_HQ_INI;
			File config = new File( FileSystem.getCurrentDirectory( )
					+ PATH_CONF
					+ path );
			try
			{
				FileOutputStream fos = new FileOutputStream( config );
				props.store( fos, null );
				fos.close( );
			}
			catch ( IOException e )
			{
				LogUtil.recordErrorMsg( e, true );
			}
		}

	}
}
