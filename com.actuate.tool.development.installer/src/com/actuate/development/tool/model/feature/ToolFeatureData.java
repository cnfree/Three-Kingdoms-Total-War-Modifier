
package com.actuate.development.tool.model.feature;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;

import com.actuate.development.tool.config.UserDataConfig;
import com.actuate.development.tool.model.Version;
import com.actuate.development.tool.wizard.ToolkitConstants;

public class ToolFeatureData
{

	private ToolFeature toolFeature;
	private List<InstallBRDProData> installBRDProDatas = new ArrayList<InstallBRDProData>( );
	private List<IPortalViewerData> iPortalViewerDatas = new ArrayList<IPortalViewerData>( );
	private SyncBRDProResourcesData syncBRDProResourcesData;
	private CloneWorkspaceData cloneWorkspaceData;
	private String currentBRDProProject = UserDataConfig.getProperty( UserDataConfig.DEFAULT_BRDPRO_PROJECT,
			"AugustaR1" );
	private String currentIVProject = UserDataConfig.getProperty( UserDataConfig.DEFAULT_IPORTAL_PROJECT,
			"AugustaR1" );
	private Version[] platformVersions;

	public String getCurrentIVProject( )
	{
		return currentIVProject;
	}

	public void setCurrentIVProject( String currentIVProject )
	{
		String oldProject = this.currentIVProject;
		this.currentIVProject = currentIVProject;
		if ( oldProject != null
				&& currentIVProject != null
				&& !currentIVProject.equals( oldProject ) )
		{
			PropertyChangeEvent event = new PropertyChangeEvent( this,
					ToolkitConstants.CURRENT_IV_PROJECT,
					oldProject,
					currentIVProject );
			for ( IPropertyChangeListener listener : listeners )
			{
				listener.propertyChange( event );
			}
		}
	}

	private Map<String, List<File>> brdproMap = new LinkedHashMap<String, List<File>>( );
	private Map<String, List<File>> iportalViewMap = new LinkedHashMap<String, List<File>>( );

	public Map<String, List<File>> getBrdproMap( )
	{
		return brdproMap;
	}

	public Map<String, List<File>> getIportalViewMap( )
	{
		return iportalViewMap;
	}

	public String getCurrentBRDProProject( )
	{
		return currentBRDProProject;
	}

	public void setCurrentBRDProProject( String currentProject )
	{
		String oldProject = this.currentBRDProProject;
		this.currentBRDProProject = currentProject;
		if ( oldProject != null
				&& currentProject != null
				&& !currentProject.equals( oldProject ) )
		{
			PropertyChangeEvent event = new PropertyChangeEvent( this,
					ToolkitConstants.CURRENT_BRDPRO_PROJECT,
					oldProject,
					currentProject );
			for ( IPropertyChangeListener listener : listeners )
			{
				listener.propertyChange( event );
			}
		}
	}

	public InstallBRDProData getCurrentInstallBRDProData( )
	{
		InstallBRDProData data = getInstallBRDProData( currentBRDProProject );
		if ( data == null && currentBRDProProject != null )
		{
			data = new InstallBRDProData( );
			data.setProject( currentBRDProProject );
			installBRDProDatas.add( data );
		}
		return data;
	}

	public IPortalViewerData getCurrentIportalViewerData( )
	{
		IPortalViewerData data = getIPortalViewerData( currentIVProject );
		if ( data == null && currentIVProject != null )
		{
			data = new IPortalViewerData( );
			data.setProject( currentIVProject );
			iPortalViewerDatas.add( data );
		}
		return data;
	}

	public ToolFeature getToolFeature( )
	{
		return toolFeature;
	}

	public void setToolFeature( ToolFeature toolFeature )
	{
		this.toolFeature = toolFeature;
	}

	public InstallBRDProData[] getInstallBRDProDatas( )
	{
		return installBRDProDatas.toArray( new InstallBRDProData[0] );
	}

	public IPortalViewerData[] getIPortalViewerDatas( )
	{
		return iPortalViewerDatas.toArray( new IPortalViewerData[0] );
	}

	public InstallBRDProData getInstallBRDProData( String project )
	{
		for ( int i = 0; i < installBRDProDatas.size( ); i++ )
		{
			InstallBRDProData data = installBRDProDatas.get( i );
			if ( project.equalsIgnoreCase( data.getProject( ) ) )
			{
				return data;
			}
		}
		return null;
	}

	public IPortalViewerData getIPortalViewerData( String project )
	{
		for ( int i = 0; i < iPortalViewerDatas.size( ); i++ )
		{
			IPortalViewerData data = iPortalViewerDatas.get( i );
			if ( project.equalsIgnoreCase( data.getProject( ) ) )
			{
				return data;
			}
		}
		return null;
	}

	public void addInstallBRDProData( InstallBRDProData module )
	{
		if ( !installBRDProDatas.contains( module ) )
			installBRDProDatas.add( module );
	}

	public void removeInstallBRDProData( InstallBRDProData module )
	{
		installBRDProDatas.remove( module );
	}

	public void addIPortalViewerData( IPortalViewerData module )
	{
		if ( !installBRDProDatas.contains( module ) )
			iPortalViewerDatas.add( module );
	}

	public void removeIPortalViewerData( IPortalViewerData module )
	{
		iPortalViewerDatas.remove( module );
	}

	public CloneWorkspaceData getCloneWorkspaceData( )
	{
		if ( cloneWorkspaceData == null )
		{
			cloneWorkspaceData = new CloneWorkspaceData( );
		}
		return cloneWorkspaceData;
	}
	
	public SyncBRDProResourcesData getSyncBRDProResourcesData( )
	{
		if ( syncBRDProResourcesData == null )
		{
			syncBRDProResourcesData = new SyncBRDProResourcesData( );
		}
		return syncBRDProResourcesData;
	}

	
	public void setSyncBRDProResourcesData(
			SyncBRDProResourcesData syncBRDProResourcesData )
	{
		this.syncBRDProResourcesData = syncBRDProResourcesData;
	}

	private List<IPropertyChangeListener> listeners = new ArrayList<IPropertyChangeListener>( );

	public void addChangeListener( IPropertyChangeListener listener )
	{
		listeners.add( listener );
	}

	public void removeChangeListener( IPropertyChangeListener listener )
	{
		listeners.remove( listener );
	}

	public void setPlatformVersions( Version[] platformVersions )
	{
		this.platformVersions = platformVersions;
	}

	public Version[] getPlatformVersions( )
	{
		return platformVersions;
	}
}
