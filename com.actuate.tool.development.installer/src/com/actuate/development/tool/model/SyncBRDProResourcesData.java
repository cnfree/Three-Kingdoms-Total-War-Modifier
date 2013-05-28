
package com.actuate.development.tool.model;

public class SyncBRDProResourcesData
{

	private String[] ignorePlatformVersions;
	private String[] pluginVersions;
	private String targetDirectory;
	private boolean minimizeToolkit;

	
	public boolean isMinimizeToolkit( )
	{
		return minimizeToolkit;
	}

	public String[] getIgnorePlatformVersions( )
	{
		return ignorePlatformVersions;
	}

	public void setIgnorePlatformVersions( String[] ignorePlatformVersions )
	{
		this.ignorePlatformVersions = ignorePlatformVersions;
	}

	public String[] getPluginVersions( )
	{
		return pluginVersions;
	}

	public void setPluginVersions( String[] pluginVersions )
	{
		this.pluginVersions = pluginVersions;
	}

	public String getTargetDirectory( )
	{
		return targetDirectory;
	}

	public void setTargetDirectory( String targetDirectory )
	{
		this.targetDirectory = targetDirectory;
	}

	public void setMinimizeToolkit( boolean minimizeToolkit )
	{
		this.minimizeToolkit = minimizeToolkit;
	}
}
