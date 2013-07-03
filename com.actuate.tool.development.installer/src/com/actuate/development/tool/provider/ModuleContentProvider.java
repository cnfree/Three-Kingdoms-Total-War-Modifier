
package com.actuate.development.tool.provider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.actuate.development.tool.config.LocationConfig;
import com.actuate.development.tool.model.Module;
import com.actuate.development.tool.model.ModuleType;
import com.actuate.development.tool.model.Modules;

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
		list.addAll( Arrays.asList( ModuleType.values( ) ) );
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
				case source :
					List<Module> modules = new ArrayList<Module>( );
					modules.addAll( Arrays.asList( Modules.getInstance( )
							.getSources( ) ) );
					if ( LocationConfig.HEADQUARTER.equals( LocationConfig.getLocation( ) ) )
					{
						modules.remove( Module.dtp );
					}
					return modules.toArray( );
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
