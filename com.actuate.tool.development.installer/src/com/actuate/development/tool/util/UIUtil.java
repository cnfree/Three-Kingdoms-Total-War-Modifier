
package com.actuate.development.tool.util;

import org.eclipse.swt.widgets.Shell;

public class UIUtil
{

	private static Shell shell;

	public static void setShell( Shell shell )
	{
		UIUtil.shell = shell;
	}

	public static Shell getShell( )
	{
		if ( shell == null || shell.isDisposed( ) )
		{
			shell = new Shell( );
		}
		return shell;
	}
}
