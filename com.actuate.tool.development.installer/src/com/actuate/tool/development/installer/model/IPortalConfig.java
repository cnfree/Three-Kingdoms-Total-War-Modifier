
package com.actuate.tool.development.installer.model;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

@Root
public class IPortalConfig
{

	@Attribute
	private String project;
	@Attribute
	private String defaultView;

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
