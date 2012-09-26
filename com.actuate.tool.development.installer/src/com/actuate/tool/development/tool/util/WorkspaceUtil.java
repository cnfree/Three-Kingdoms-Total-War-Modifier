
package com.actuate.tool.development.tool.util;

import java.io.File;
import java.io.FileFilter;

public class WorkspaceUtil
{

	public static boolean cloneWorkspaceSettings( String oldWorkspace,
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
		return FileUtil.copyDirectory( oldWorkspace, newWorkspace, filter );
	}
}
