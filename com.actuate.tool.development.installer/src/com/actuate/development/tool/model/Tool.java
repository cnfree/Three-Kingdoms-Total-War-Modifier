
package com.actuate.development.tool.model;

import java.io.File;
import java.util.ArrayList;

import java.util.List;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import com.actuate.development.tool.util.LogUtil;

@Root
public class Tool
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

			Tool tool = new Tool( );
			tool.setIportalConfigs( configs );
			tool.setPlugins( plugins );
			serializer.write( tool, new File( "C:\\1.xml" ) );
		}
		catch ( Exception e )
		{
			e.printStackTrace( );
		}
	}

	public static void init( )
	{
		String xmlpath = "\\\\GUI-VISTA\\shared\\plugins\\toolkit.xml";
		Serializer serializer = new Persister( );
		File source = new File( xmlpath );
		try
		{
			Tool tool = serializer.read( Tool.class, source );
			Plugins plugins = tool.getPlugins( );
			List<Plugin> pluginList = plugins.getPlugins( );
			for ( Plugin plugin : pluginList )
			{
				if ( plugin.getIcon( ) != null )
				{
					new Module( plugin.getName( ),
							plugin.getLabel( ),
							"\\\\GUI-VISTA\\shared\\plugins\\"
									+ plugin.getIcon( ),
							plugin.getFile( ).trim( ) );
				}
				else
				{
					new Module( plugin.getName( ),
							plugin.getLabel( ),
							plugin.getFile( ).trim( ) );
				}
			}
			IPortalConfigs configList = tool.getIportalConfigs( );
			for ( IPortalConfig config : configList.getIPortalConfigs( ) )
			{
				Modules.getInstance( ).addIPortalConfig( config.getProject( ),
						config );
			}
		}
		catch ( Exception e )
		{
			LogUtil.recordErrorMsg( e, true );
		}
	}
}
