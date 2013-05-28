
package com.actuate.development.tool.provider;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.sf.feeling.swt.win32.internal.extension.util.ImageCache;

import com.actuate.development.tool.model.Version;
import com.actuate.development.tool.model.VersionType;

public class SyncResourcesLabelProvider extends LabelProvider
{

	public Image getImage( Object element )
	{
		if ( element instanceof VersionType )
			return ImageCache.getImage( ( (VersionType) element ).getImagePath( ) );
		if ( element instanceof Version )
			return ImageCache.getImage( ( (Version) element ).getImagePath( ) );
		return null;
	}

	public String getText( Object element )
	{
		if ( element instanceof VersionType )
			return ( (VersionType) element ).getValue( );
		if ( element instanceof Version )
			return ( (Version) element ).getValue( );
		return null;
	}

}
