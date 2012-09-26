package com.actuate.development.tool.model;

import java.util.List;

import org.simpleframework.xml.ElementList;


public class IPortalConfigs
{
	@ElementList(inline = true)
	private List<IPortalConfig> configs;

	public List<IPortalConfig> getIPortalConfigs( )
	{
		return configs;
	}

	public void setIPortalConfigs( List<IPortalConfig> configs )
	{
		this.configs = configs;
	}
}
