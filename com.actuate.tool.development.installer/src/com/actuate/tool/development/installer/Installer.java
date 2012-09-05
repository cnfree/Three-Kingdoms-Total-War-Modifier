
package com.actuate.tool.development.installer;

import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.sf.feeling.swt.win32.extension.io.FileSystem;
import org.sf.feeling.swt.win32.extension.jna.win32.Shell32;
import org.sf.feeling.swt.win32.extension.jna.win32.structure.SHELLEXECUTEINFO;
import org.sf.feeling.swt.win32.internal.extension.util.ImageCache;

import com.actuate.tool.development.installer.util.LogUtil;
import com.actuate.tool.development.installer.wizard.InstallWizard;

public class Installer
{

	public static void main( String[] args )
	{
		if ( args != null
				&& args.length > 0
				&& "-uac".equalsIgnoreCase( args[0] ) )
		{
			Shell shell = new Shell( );
			shell.setImages( new Image[]{
					ImageCache.getImage( "/icons/actuate_16.png" ),
					ImageCache.getImage( "/icons/actuate_32.png" ),
					ImageCache.getImage( "/icons/actuate_48.png" )
			} );
			InstallWizard wizard = new InstallWizard( );
			WizardDialog dialog = new WizardDialog( null, wizard );
			dialog.open( );
		}
		else
		{
			try
			{
				SHELLEXECUTEINFO info = new SHELLEXECUTEINFO( );
				info.fMask = SHELLEXECUTEINFO.SEE_MASK_NOCLOSEPROCESS
						| SHELLEXECUTEINFO.SEE_MASK_FLAG_NO_UI;
				info.lpFile = FileSystem.getCurrentDirectory( )
						+ "\\Installer.exe";
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
