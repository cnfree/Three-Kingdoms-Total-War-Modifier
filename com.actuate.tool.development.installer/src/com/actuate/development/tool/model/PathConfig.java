
package com.actuate.development.tool.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.sf.feeling.swt.win32.extension.io.FileSystem;

import com.actuate.development.tool.util.FileUtil;

public class PathConfig
{

	// Toolkit server
	public final static String SERVER = "server";

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

	private static Properties props = new Properties( );
	static
	{
		File config = new File( FileSystem.getCurrentDirectory( )
				+ "/conf/config.ini" );
		if ( !config.exists( ) )
		{
			FileUtil.writeToBinarayFile( config,
					PathConfig.class.getResourceAsStream( "/conf/config.ini" ),
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
				InputStream in = PathConfig.class.getResourceAsStream( "/conf/config.ini" );
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
				props.put( convert( key ), convert( value ) );
			}
		}
		catch ( Exception e )
		{
			SWT.error( SWT.ERROR_IO, e );
		}
	}

	public static String getProperty( String property, String defaultValue )
	{
		return props.getProperty( property, defaultValue );
	}

	private static Object convert( String string )
	{
		try
		{
			return new String( string.getBytes( "ISO-8859-1" ), "utf-8" );
		}
		catch ( UnsupportedEncodingException e )
		{
			return string;
		}
	}

	public static Properties loadProperties( File file )
	{
		Properties props = new Properties( );
		try
		{
			InputStream in = new FileInputStream( file );
			props.load( in );
			in.close( );
		}
		catch ( Exception e )
		{
			SWT.error( SWT.ERROR_IO, e );
		}
		return props;
	}

}
