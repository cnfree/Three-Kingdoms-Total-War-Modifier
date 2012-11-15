
package com.actuate.development.tool.util;

import java.io.File;
import java.io.FileFilter;

import org.eclipse.core.runtime.IProgressMonitor;

public class WorkspaceUtil
{

	public static boolean cloneWorkspaceSettings( IProgressMonitor monitor, String oldWorkspace,
			String newWorkspace )
	{
		if ( oldWorkspace == null
				|| newWorkspace == null
				|| !new File( oldWorkspace ).exists( ) )
			return false;
		FileFilter filter = new FileFilter( ) {

			public boolean accept( File pathname )
			{
				if ( pathname.isFile( ) && pathname.exists( ) )
				{
					String path = pathname.getAbsolutePath( );
					if ( path.toLowerCase( ).endsWith( "workbench.xml" ) )
						return false;
					if ( path.toLowerCase( )
							.endsWith( "launchconfigurationhistory.xml" ) )
						return false;
					String suffix = path.substring( path.lastIndexOf( '.' ) );
					if ( suffix.equalsIgnoreCase( ".xml" )
							|| suffix.equalsIgnoreCase( ".ini" )
							|| suffix.equalsIgnoreCase( ".prefs" )
							|| suffix.equalsIgnoreCase( ".properties" ) )

						return true;
				}
				return false;
			}
		};
		return FileUtil.copyDirectory( monitor, oldWorkspace, newWorkspace, filter );
	}
}
