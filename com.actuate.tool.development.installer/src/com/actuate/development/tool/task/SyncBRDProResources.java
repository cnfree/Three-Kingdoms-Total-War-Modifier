
package com.actuate.development.tool.task;

import org.eclipse.core.runtime.IProgressMonitor;

import com.actuate.development.tool.model.SyncBRDProResourcesData;

public class SyncBRDProResources implements ITaskWithMonitor
{

	SyncBRDProResourcesData data;

	public SyncBRDProResources( SyncBRDProResourcesData data )
	{
		this.data = data;
	}

	public void execute( IProgressMonitor monitor )
	{
		// TODO Auto-generated method stub

	}

}
