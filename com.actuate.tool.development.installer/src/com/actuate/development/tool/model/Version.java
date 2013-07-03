
package com.actuate.development.tool.model;

import java.io.File;

public class Version
{

	private String value;
	private VersionType type;
	private String imagePath;
	private String name;
	private File versionFile;

	public File getVersionFile( )
	{
		return versionFile;
	}

	public void setVersionFile( File versionFile )
	{
		this.versionFile = versionFile;
	}

	public String getName( )
	{
		return name;
	}

	public Version( String name, String value, File versionFile,
			VersionType type, String imagePath )
	{
		this.name = name;
		this.value = value;
		this.type = type;
		this.imagePath = imagePath;
		this.versionFile = versionFile;
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
