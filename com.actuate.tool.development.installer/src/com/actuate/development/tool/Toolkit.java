
package com.actuate.development.tool;

import java.io.File;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.sf.feeling.swt.win32.extension.io.FileSystem;
import org.sf.feeling.swt.win32.extension.io.Network;
import org.sf.feeling.swt.win32.extension.jna.win32.Shell32;
import org.sf.feeling.swt.win32.extension.jna.win32.structure.SHELLEXECUTEINFO;
import org.sf.feeling.swt.win32.internal.extension.util.ImageCache;

import com.actuate.development.tool.util.LogUtil;
import com.actuate.development.tool.wizard.ToolkitWizard;

public class Toolkit
{

	public static String HOST;

	public Toolkit( )
	{
		Shell shell = new Shell( );
		shell.setImages( new Image[]{
				ImageCache.getImage( "/icons/actuate_16.png" ),
				ImageCache.getImage( "/icons/actuate_32.png" ),
				ImageCache.getImage( "/icons/actuate_48.png" )
		} );

		Thread qaantThread = new Thread( ) {

			public void run( )
			{
				if ( Network.ping( "Qaant", 32 ) != -1 )
				{
					if ( HOST == null
							&& new File( "\\\\Qaant\\qa\\Toolkit\\plugins\\" ).exists( ) )
					{
						HOST = "\\\\Qaant\\qa\\Toolkit\\plugins\\";
						synchronized ( Toolkit.this )
						{
							Toolkit.this.notify( );
						}
					}
				}
			}
		};
		qaantThread.start( );

		Thread guiThread = new Thread( ) {

			public void run( )
			{
				if ( Network.ping( "GUI-VISTA", 32 ) != -1 )
				{
					if ( HOST == null
							&& new File( "\\\\GUI-VISTA\\shared\\plugins\\" ).exists( ) )
					{
						HOST = "\\\\GUI-VISTA\\shared\\plugins\\";
						synchronized ( Toolkit.this )
						{
							Toolkit.this.notify( );
						}
					}
				}
			}
		};
		guiThread.start( );

		synchronized ( this )
		{
			try
			{
				if ( HOST == null )
					this.wait( 3000 );
			}
			catch ( InterruptedException e )
			{
			}
		}

		if ( HOST == null )
		{
			MessageDialog.openError( shell,
					"Error",
					"Can't connect to server Qaant or GUI-Vista on network. Please try it later or contact with cchen@actuate.com." );
			shell.dispose( );
			System.exit( 1 );
		}

		ToolkitWizard wizard = new ToolkitWizard( );
		WizardDialog dialog = new WizardDialog( null, wizard );
		dialog.open( );
	}

	public static void main( String[] args )
	{
		if ( args != null
				&& args.length > 0
				&& "-uac".equalsIgnoreCase( args[0] ) )
		{
			new Toolkit( );
		}
		else
		{
			try
			{
				SHELLEXECUTEINFO info = new SHELLEXECUTEINFO( );
				info.fMask = SHELLEXECUTEINFO.SEE_MASK_NOCLOSEPROCESS
						| SHELLEXECUTEINFO.SEE_MASK_FLAG_NO_UI;
				info.lpFile = FileSystem.getCurrentDirectory( )
						+ "\\Toolkit.exe";
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
