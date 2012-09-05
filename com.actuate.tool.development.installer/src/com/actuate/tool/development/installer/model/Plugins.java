
package com.actuate.tool.development.installer.model;

import java.util.List;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root
public class Plugins
{

	@ElementList(inline = true)
	private List<Plugin> plugins;

	public List<Plugin> getPlugins( )
	{
		return plugins;
	}

	public void setPlugins( List<Plugin> plugins )
	{
		this.plugins = plugins;
	}

	
}
