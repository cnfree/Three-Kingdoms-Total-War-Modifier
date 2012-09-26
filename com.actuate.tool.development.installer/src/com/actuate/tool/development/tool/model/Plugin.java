
package com.actuate.tool.development.tool.model;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root
public class Plugin
{

	@Attribute
	private String name;
	@Attribute
	private String label;
	@Attribute(required = false)
	private String icon;
	@Element(data = true)
	private String file;

	public String getName( )
	{
		return name;
	}

	public void setName( String name )
	{
		this.name = name;
	}

	public String getLabel( )
	{
		return label;
	}

	public void setLabel( String label )
	{
		this.label = label;
	}

	public String getIcon( )
	{
		return icon;
	}

	public void setIcon( String icon )
	{
		this.icon = icon;
	}

	public String getFile( )
	{
		return file;
	}

	public void setFile( String file )
	{
		this.file = file;
	}
}
