
package com.actuate.development.tool.provider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.actuate.development.tool.model.Module;
import com.actuate.development.tool.model.ToolFeatureData;
import com.actuate.development.tool.model.VersionType;

public class SyncResourcesContentProvider implements ITreeContentProvider
{

	private ToolFeatureData data;

	public SyncResourcesContentProvider( ToolFeatureData data )
	{
		this.data = data;
	}

	public void dispose( )
	{

	}

	public void inputChanged( Viewer viewer, Object oldInput, Object newInput )
	{

	}

	public Object[] getElements( Object inputElement )
	{
		List<Object> list = new ArrayList<Object>( );
		list.addAll( Arrays.asList( VersionType.values( ) ) );
		return list.toArray( );
	}

	public Object[] getChildren( Object parentElement )
	{
		if ( parentElement instanceof VersionType )
		{
			VersionType type = (VersionType) parentElement;
			switch ( type )
			{
				case platform :
					return data.getPlatformVersions( );
			}
		}
		return new Object[0];

	}

	public Object getParent( Object element )
	{
		if ( element instanceof Module )
			return ( (Module) element ).getType( );
		return null;
	}

	public boolean hasChildren( Object element )
	{
		return getChildren( element ).length > 0;
	}

}
