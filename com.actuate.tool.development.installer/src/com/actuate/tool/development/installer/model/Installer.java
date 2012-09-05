
package com.actuate.tool.development.installer.model;

import java.io.File;
import java.util.ArrayList;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

@Root
public class Installer
{

	@Element
	private Plugins plugins;

	@Element
	private IPortalConfigs iportalConfigs;

	public Plugins getPlugins( )
	{
		return plugins;
	}

	public void setPlugins( Plugins plugins )
	{
		this.plugins = plugins;
	}

	public IPortalConfigs getIportalConfigs( )
	{
		return iportalConfigs;
	}

	public void setIportalConfigs( IPortalConfigs iportalConfigs )
	{
		this.iportalConfigs = iportalConfigs;
	}

	public static void main( String[] args )
	{
		try
		{
			Serializer serializer = new Persister( );
			Plugins plugins = new Plugins( );
			plugins.setPlugins( new ArrayList<Plugin>( ) );

			Plugin plugin = new Plugin( );
			plugin.setName( "jad" );
			plugin.setLabel( "Jad" );
			plugin.setIcon( "icons\\jad.gif" );
			plugin.setFile( "(?i)jadclipse.*\\.zip" );
			plugins.getPlugins( ).add( plugin );

			IPortalConfigs configs = new IPortalConfigs( );
			configs.setIPortalConfigs( new ArrayList<IPortalConfig>( ) );

			IPortalConfig config = new IPortalConfig( );
			config.setProject( "11SP4" );
			config.setDefaultView( "Actuate/11SP4/iPortalApp" );
			configs.getIPortalConfigs( ).add( config );

			Installer installer = new Installer( );
			installer.setIportalConfigs( configs );
			installer.setPlugins( plugins );
			serializer.write( installer, new File( "C:\\1.xml" ) );
		}
		catch ( Exception e )
		{
			e.printStackTrace( );
		}
	}
}
