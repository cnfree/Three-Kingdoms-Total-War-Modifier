
package com.actuate.development.tool.model;

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
					"/icons/p4.png" ), emfsdk = new Module( "emfsdk",
					"EMF SDK",
					ModuleType.plugin,
					"/icons/emf.gif" );

	private String value;
	private ModuleType type;
	private String imagePath;
	private String pluginNamePattern;
	private String name;

	public String getName( )
	{
		return name;
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
