
package com.actuate.tool.development.tool.provider;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.sf.feeling.swt.win32.internal.extension.util.ImageCache;

import com.actuate.tool.development.tool.model.Module;
import com.actuate.tool.development.tool.model.ModuleType;

public class ModuleLabelProvider extends LabelProvider
{

	public Image getImage( Object element )
	{
		if ( element instanceof Module )
		{
			Module module = (Module) element;
			if ( module.getType( ) != ModuleType.extension )
				return ImageCache.getImage( module.getImagePath( ) );
			else
			{
				if ( module.getImagePath( ) != null )
				{
					ImageData[] imageDatas = new ImageLoader( ).load( module.getImagePath( ) );
					if ( imageDatas.length > 0 )
					{
						return ImageCache.createImage( imageDatas[0] );
					}
				}
				return ImageCache.getImage( "/icons/link_obj.gif" );
			}
		}
		if ( element instanceof ModuleType )
			return ImageCache.getImage( ( (ModuleType) element ).getImagePath( ) );
		return null;
	}

	public String getText( Object element )
	{
		if ( element instanceof Module )
			return ( (Module) element ).getValue( );
		if ( element instanceof ModuleType )
			return ( (ModuleType) element ).getValue( );
		return null;
	}

}
