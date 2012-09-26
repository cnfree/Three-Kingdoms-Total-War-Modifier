
package com.actuate.tool.development.tool.model;

public enum ToolFeature {
	installBRDPro("Install BRDPro"), cloneWorkspaceSettings(
			"Clone Workspace Settings"), synciPortalWorkspace("Synchronize iPortal Workspace");

	private String value;

	ToolFeature( String value )
	{
		this.value = value;
	}

	public String getValue( )
	{
		return this.value;
	}
}
