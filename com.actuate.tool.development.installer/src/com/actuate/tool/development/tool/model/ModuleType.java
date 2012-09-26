
package com.actuate.tool.development.tool.model;

public enum ModuleType {
	sdk("Source", "/icons/resource_obj.gif"), plugin("Plugin",
			"/icons/plugin_obj.gif"), extension("Extension",
			"/icons/extension_obj.gif");

	private String value;
	private String imagePath;

	ModuleType( String value, String imagePath )
	{
		this.value = value;
		this.imagePath = imagePath;
	}

	public String getImagePath( )
	{
		return imagePath;
	}

	public String getValue( )
	{
		return this.value;
	}
}
