
package com.actuate.development.tool.model;

public enum ToolFeature {
	installBRDPro("Install BRDPro"), cloneWorkspaceSettings(
			"Clone Workspace Settings"), synciPortalWorkspace(
			"Synchronize iPortal Workspace"), syncBRDProResources(
			"Synchronize BRDPro Resources");

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
