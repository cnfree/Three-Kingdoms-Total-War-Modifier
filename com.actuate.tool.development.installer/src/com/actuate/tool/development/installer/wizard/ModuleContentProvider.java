
package com.actuate.tool.development.installer.wizard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.actuate.tool.development.installer.model.Module;
import com.actuate.tool.development.installer.model.ModuleType;
import com.actuate.tool.development.installer.model.Modules;

public class ModuleContentProvider implements ITreeContentProvider
{

	public void dispose( )
	{

	}

	public void inputChanged( Viewer viewer, Object oldInput, Object newInput )
	{

	}

	public Object[] getElements( Object inputElement )
	{
		List<ModuleType> list = new ArrayList<ModuleType>( );
		list.addAll( Arrays.asList( ModuleType.values( ) ));
		if ( Modules.getInstance( ).getExtensions( ).length == 0 )
			list.remove( ModuleType.extension );
		return list.toArray( );
	}

	public Object[] getChildren( Object parentElement )
	{
		if ( parentElement instanceof ModuleType )
		{
			ModuleType type = (ModuleType) parentElement;
			switch ( type )
			{
				case sdk :
					return Modules.getInstance( ).getSDKs( );
				case plugin :
					return Modules.getInstance( ).getPlugins( );
				case extension :
					return Modules.getInstance( ).getExtensions( );
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
