
package com.actuate.development.tool.model.feature;

public class IPortalViewerData
{

	private String project;

	private String birtViewerFile;

	private String root;

	private String view;

	private String customProjectName;

	private boolean forceOperation;

	private boolean revertFiles;

	private boolean skipSync;

	private String server;

	private String user;

	private String password;

	private String client;

	private String revisionArg;

	private boolean isLatestRevision = true;

	public boolean isLatestRevision( )
	{
		return isLatestRevision;
	}

	public String getRevisionArg( )
	{
		return revisionArg;
	}

	public String getBirtViewerFile( )
	{
		return birtViewerFile;
	}

	public String getClient( )
	{
		return client;
	}

	public String getCustomProjectName( )
	{
		return customProjectName;
	}

	public String getPassword( )
	{
		return password;
	}

	public String getProject( )
	{
		return project;
	}

	public String getRoot( )
	{
		return root;
	}

	public String getServer( )
	{
		return server;
	}

	public String getUser( )
	{
		return user;
	}

	public String getView( )
	{
		return view;
	}

	public boolean isForceOperation( )
	{
		return forceOperation;
	}

	public boolean isRevertFiles( )
	{
		return revertFiles;
	}

	public boolean isSkipSync( )
	{
		return skipSync;
	}

	public void setBirtViewerFile( String birtViewerFile )
	{
		this.birtViewerFile = birtViewerFile;
	}

	public void setClient( String client )
	{
		this.client = client;
	}

	public void setCustomProjectName( String customProjectName )
	{
		this.customProjectName = customProjectName;
	}

	public void setForceOperation( boolean forceOperation )
	{
		this.forceOperation = forceOperation;
	}

	public void setPassword( String password )
	{
		this.password = password;
	}

	public void setProject( String project )
	{
		this.project = project;
	}

	public void setRevertFiles( boolean revertFiles )
	{
		this.revertFiles = revertFiles;
	}

	public void setRoot( String root )
	{
		this.root = root;
	}

	public void setServer( String server )
	{
		this.server = server;
	}

	public void setSkipSync( boolean skipSync )
	{
		this.skipSync = skipSync;
	}

	public void setUser( String user )
	{
		this.user = user;
	}

	public void setView( String view )
	{
		this.view = view;
	}

	public void setRevisionArg( String revisionArg )
	{
		this.revisionArg = revisionArg;
	}

	public void setLatestRevision( boolean isLatestRevision )
	{
		this.isLatestRevision = isLatestRevision;
	}

}
