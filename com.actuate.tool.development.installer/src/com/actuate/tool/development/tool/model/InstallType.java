
package com.actuate.tool.development.tool.model;

public enum InstallType {
	installBRDPro("Install BRDPro"), cloneWorkspaceSettings(
			"Clone Workspace Settings"), synciPortalWorkspace("Synchronize iPortal Workspace");

	private String value;

	InstallType( String value )
	{
		this.value = value;
	}

	public String getValue( )
	{
		return this.value;
	}
}
