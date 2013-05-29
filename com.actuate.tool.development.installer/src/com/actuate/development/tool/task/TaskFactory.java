
package com.actuate.development.tool.task;

import com.actuate.development.tool.model.ToolFeature;
import com.actuate.development.tool.model.ToolFeatureData;

public class TaskFactory
{

	public static ITaskWithMonitor createTask( ToolFeatureData data )
	{
		if ( data != null )
		{
			if ( data.getToolFeature( ) == ToolFeature.installBRDPro )
			{
				return new InstallBRDPro( data.getCurrentInstallBRDProData( ) );
			}
			else if ( data.getToolFeature( ) == ToolFeature.cloneWorkspaceSettings )
			{
				return new CloneWorkspaceSettings( data.getCloneWorkspaceData( ) );
			}
			else if ( data.getToolFeature( ) == ToolFeature.synciPortalWorkspace )
			{
				return new SyncIPortalWorkspace( data.getCurrentIportalViewerData( ) );
			}
			else if ( data.getToolFeature( ) == ToolFeature.syncBRDProResources )
			{
				return new SyncBRDProResources( data.getSyncBRDProResourcesData( ) );
			}
		}

		return null;
	}
}
