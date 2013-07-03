
package com.actuate.development.tool.model;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

@Root(name="iportalConfig")
public class IPortalConfig
{

	@Attribute
	private String project;
	@Attribute
	private String defaultView;
	@Attribute
	private String replaceFile;

	public String getReplaceFile( )
	{
		return replaceFile;
	}

	public void setReplaceFile( String replaceFile )
	{
		this.replaceFile = replaceFile;
	}

	public String getProject( )
	{
		return project;
	}

	public void setProject( String project )
	{
		this.project = project;
	}

	public String getDefaultView( )
	{
		return defaultView;
	}

	public void setDefaultView( String defaultView )
	{
		this.defaultView = defaultView;
	}
}
