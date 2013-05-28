
package com.actuate.development.tool.model;

public class Version
{

	private String value;
	private VersionType type;
	private String imagePath;
	private String name;

	public String getName( )
	{
		return name;
	}

	public Version( String name, String value, VersionType type,
			String imagePath )
	{
		this.name = name;
		this.value = value;
		this.type = type;
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

	public VersionType getType( )
	{
		return this.type;
	}
}
