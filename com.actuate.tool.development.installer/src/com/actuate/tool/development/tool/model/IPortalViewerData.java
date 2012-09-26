
package com.actuate.tool.development.tool.model;

public class IPortalViewerData
{

	private String project;

	private String birtViewerFile;

	private String root;

	private String view;

	private boolean forceOperation;

	private String server;

	private String user;

	private String password;

	public String getRoot( )
	{
		return root;
	}

	public void setRoot( String root )
	{
		this.root = root;
	}

	public String getServer( )
	{
		return server;
	}

	public void setServer( String server )
	{
		this.server = server;
	}

	public String getUser( )
	{
		return user;
	}

	public void setUser( String user )
	{
		this.user = user;
	}

	public String getPassword( )
	{
		return password;
	}

	public void setPassword( String password )
	{
		this.password = password;
	}

	private String client;

	public String getClient( )
	{
		return client;
	}

	public void setClient( String client )
	{
		this.client = client;
	}

	public String getBirtViewerFile( )
	{
		return birtViewerFile;
	}

	public String getProject( )
	{
		return project;
	}

	public String getView( )
	{
		return view;
	}

	public boolean isForceOperation( )
	{
		return forceOperation;
	}

	public void setBirtViewerFile( String birtViewerFile )
	{
		this.birtViewerFile = birtViewerFile;
	}

	public void setForceOperation( boolean forceOperation )
	{
		this.forceOperation = forceOperation;
	}

	public void setProject( String project )
	{
		this.project = project;
	}

	public void setView( String view )
	{
		this.view = view;
	}

}
