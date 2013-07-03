
package com.actuate.development.tool;

import org.sf.feeling.swt.win32.extension.io.FileSystem;
import org.sf.feeling.swt.win32.extension.jna.win32.Shell32;
import org.sf.feeling.swt.win32.extension.jna.win32.structure.SHELLEXECUTEINFO;

import com.actuate.development.tool.util.ClassPathUpdater;
import com.actuate.development.tool.util.LogUtil;

public class Sync
{

	public static void main( String[] args )
	{
		if ( args != null
				&& args.length > 0
				&& "-uac".equalsIgnoreCase( args[0] ) )
		{
			ClassPathUpdater.loadClasspath( );
			new Toolkit( true );
		}
		else
		{
			try
			{
				SHELLEXECUTEINFO info = new SHELLEXECUTEINFO( );
				info.fMask = SHELLEXECUTEINFO.SEE_MASK_NOCLOSEPROCESS
						| SHELLEXECUTEINFO.SEE_MASK_FLAG_NO_UI;
				info.lpFile = FileSystem.getCurrentDirectory( )
						+ "\\Sync.exe";
				info.lpVerb = "runas";
				info.lpDirectory = null;
				info.lpParameters = "-uac";
				Shell32.ShellExecuteEx( info );
			}
			catch ( Exception e )
			{
				LogUtil.recordErrorMsg( e, true );
			}
		}
	}
}
