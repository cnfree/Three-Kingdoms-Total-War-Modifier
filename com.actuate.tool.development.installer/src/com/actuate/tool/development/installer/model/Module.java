
package com.actuate.tool.development.installer.model;

import java.io.File;
import java.util.List;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import com.actuate.tool.development.installer.util.LogUtil;

public class Module
{

	public static Module eclipse = new Module( "eclipse",
			"Eclipse",
			ModuleType.sdk ),
			emf = new Module( "emf", "EMF", ModuleType.sdk ),
			gef = new Module( "gef", "GEF", ModuleType.sdk ),
			wtp = new Module( "wtp", "WTP", ModuleType.sdk ),
			dtp = new Module( "dtp", "DTP", ModuleType.sdk ),
			git = new Module( "git", "Git", ModuleType.plugin, "/icons/git.gif" ),
			perforce = new Module( "perforce",
					"Perforce",
					ModuleType.plugin,
					"/icons/p4.png" );

	private String value;
	private ModuleType type;
	private String imagePath;
	private String pluginNamePattern;
	private String name;

	public String getName( )
	{
		return name;
	}

	public static void init( )
	{
		String xmlpath = "\\\\GUI-VISTA\\shared\\plugins\\plugins.xml";
		Serializer serializer = new Persister( );
		File source = new File( xmlpath );
		try
		{
			Plugins plugins = serializer.read( Plugins.class, source );
			List<Plugin> pluginList = plugins.getPlugins( );
			for ( Plugin plugin : pluginList )
			{
				if ( plugin.getIcon( ) != null )
				{
					new Module( plugin.getName( ),
							plugin.getLabel( ),
							"\\\\GUI-VISTA\\shared\\plugins\\"
									+ plugin.getIcon( ),
							plugin.getFile( ) );
				}
				else
				{
					new Module( plugin.getName( ),
							plugin.getLabel( ),
							plugin.getFile( ) );
				}
			}
		}
		catch ( Exception e )
		{
			LogUtil.recordErrorMsg( e, true );
		}
	}

	public String getPluginNamePattern( )
	{
		return pluginNamePattern;
	}

	private Module( String name, String value, ModuleType type, String imagePath )
	{
		this.name = name;
		this.value = value;
		this.type = type;
		this.imagePath = imagePath;
		Modules.getInstance( ).register( this );
	}

	private Module( String name, String value, ModuleType type )
	{
		this.name = name;
		this.value = value;
		this.type = type;
		this.imagePath = "/icons/library_obj.gif";
		Modules.getInstance( ).register( this );
	}

	public Module( String name, String value, String imagePath,
			String pluginNamePattern )
	{
		this.name = name;
		this.value = value;
		this.type = ModuleType.extension;
		this.imagePath = imagePath;
		this.pluginNamePattern = pluginNamePattern;
		Modules.getInstance( ).register( this );
	}

	public Module( String name, String value, String pluginNamePattern )
	{
		this.name = name;
		this.value = value;
		this.type = ModuleType.extension;
		this.pluginNamePattern = pluginNamePattern;
		Modules.getInstance( ).register( this );
	}

	public String getImagePath( )
	{
		return imagePath;
	}

	public String getValue( )
	{
		return this.value;
	}

	public ModuleType getType( )
	{
		return this.type;
	}
}
