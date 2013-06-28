
package com.actuate.development.tool.model.feature;

import com.actuate.development.tool.model.Version;

public class SyncBRDProResourcesData
{

	private String[] ignorePlatformVersions;
	private String[] pluginVersions;
	private String targetDirectory;
	private boolean minimizeToolkit;
	private Version[] platformVersions;

	public Version[] getPlatformVersions( )
	{
		if ( platformVersions == null )
			return new Version[0];
		return platformVersions;
	}

	public void setPlatformVersions( Version[] platformVersions )
	{
		this.platformVersions = platformVersions;
	}

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
