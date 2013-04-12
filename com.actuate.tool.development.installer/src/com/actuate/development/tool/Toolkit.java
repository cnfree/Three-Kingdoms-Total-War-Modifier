
package com.actuate.development.tool;

import java.io.File;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.sf.feeling.swt.win32.extension.io.FileSystem;
import org.sf.feeling.swt.win32.extension.jna.win32.Shell32;
import org.sf.feeling.swt.win32.extension.jna.win32.structure.SHELLEXECUTEINFO;
import org.sf.feeling.swt.win32.internal.extension.util.ImageCache;

import com.actuate.development.tool.model.PathConfig;
import com.actuate.development.tool.util.ClassPathUpdater;
import com.actuate.development.tool.util.LogUtil;
import com.actuate.development.tool.wizard.ToolkitWizard;

public class Toolkit
{

	public static String HOST;

	public Toolkit( )
	{
		Display display = new Display( );

		Thread qaantThread = new Thread( ) {

			public void run( )
			{
				String plugins = PathConfig.getProperty( PathConfig.PLUGINS,
						"\\\\qaant\\qa\\Toolkit\\plugins" );
				if ( new File( plugins ).exists( ) )
				{
					HOST = plugins;
					synchronized ( Toolkit.this )
					{
						Toolkit.this.notify( );
					}
				}
			}
		};
		qaantThread.start( );

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
			String server = PathConfig.getProperty( PathConfig.SERVER, "Qaant" );
			MessageDialog dialog = new MessageDialog( null,
					"Error",
					null,
					"Can't connect to server "
							+ server
							+ ". Please try it later or contact with administrator.",
					SWT.ICON_ERROR,
					new String[]{
						IDialogConstants.OK_LABEL
					},
					0 ) {

				protected void configureShell( Shell shell )
				{
					super.configureShell( shell );
					shell.setImages( new Image[]{
							ImageCache.getImage( "/icons/actuate_16.png" ),
							ImageCache.getImage( "/icons/actuate_32.png" ),
							ImageCache.getImage( "/icons/actuate_48.png" )
					} );
					shell.forceActive( );
				}
			};
			dialog.open( );
			display.dispose( );
			return;
		}

		ToolkitWizard wizard = new ToolkitWizard( );
		WizardDialog dialog = new WizardDialog( null, wizard );
		dialog.open( );
		display.dispose( );
	}

	public static void main( String[] args )
	{
		if ( args != null
				&& args.length > 0
				&& "-uac".equalsIgnoreCase( args[0] ) )
		{
			ClassPathUpdater.loadClasspath( );
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
