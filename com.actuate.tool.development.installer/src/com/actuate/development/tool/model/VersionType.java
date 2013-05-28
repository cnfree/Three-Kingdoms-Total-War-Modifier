
package com.actuate.development.tool.model;

public enum VersionType{
	platform("Platform Version", "/icons/version_obj.gif");

	private String value;
	private String imagePath;

	VersionType( String value, String imagePath )
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
