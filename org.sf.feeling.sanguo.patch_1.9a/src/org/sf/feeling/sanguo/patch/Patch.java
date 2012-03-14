/*******************************************************************************
 * Copyright (c) 2007 cnfree.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  cnfree  - initial API and implementation
 *******************************************************************************/

package org.sf.feeling.sanguo.patch;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.sf.feeling.sanguo.patch.dialog.ErrorLogDialog;
import org.sf.feeling.sanguo.patch.page.BasePage;
import org.sf.feeling.sanguo.patch.provider.CategoryProviderFactory;
import org.sf.feeling.sanguo.patch.provider.ICategoryProvider;
import org.sf.feeling.sanguo.patch.util.BakUtil;
import org.sf.feeling.sanguo.patch.util.FileConstants;
import org.sf.feeling.sanguo.patch.util.FileUtil;
import org.sf.feeling.sanguo.patch.util.MapUtil;
import org.sf.feeling.sanguo.patch.util.Properties;
import org.sf.feeling.sanguo.patch.util.UpdateUtil;
import org.sf.feeling.swt.win32.extension.io.FileSystem;
import org.sf.feeling.swt.win32.extension.shell.ShellFolder;
import org.sf.feeling.swt.win32.extension.shell.Windows;
import org.sf.feeling.swt.win32.extension.system.FileVersionInfo;
import org.sf.feeling.swt.win32.extension.widgets.ShellWrapper;
import org.sf.feeling.swt.win32.internal.extension.util.ImageCache;

public class Patch
{

	public static File GAME_ROOT = null;
	public static File GAME_APPLICATION = null;

	private static String version = null;

	private ICategoryProvider provider;
	private BasePage page;

	public Patch( String[] args )
	{
		if ( args != null && args.length > 0 )
		{
			String file = args[0];
			if ( file != null && new File( file ).exists( ) )
			{
				FileVersionInfo info = new FileVersionInfo( );
				info.loadVersionInfo( file );
				version = info.getProductVersion( );
				UpdateUtil.update( version );
			}
		}
	}

	private void initial( )
	{
		final Display display = new Display( );
		shell = new Shell( display );

		FileDialog dialog = new FileDialog( shell, SWT.OPEN );
		dialog.setText( "请选择三国全面战争运行文件" );
		dialog.setFilterExtensions( new String[]{
			"*.exe"
		} ); // Windows
		File file = new File( System.getProperties( ).getProperty( "user.home" ) //$NON-NLS-1$
				+ File.separator
				+ "\\.sanguo_patch\\1.9a\\patch.ini" );
		if ( file.exists( ) )
		{
			Properties properties = FileUtil.loadProperties( file );
			if ( properties != null )
			{
				String path = properties.getProperty( "path" );
				if ( path != null && new File( path ).exists( ) )
					dialog.setFilterPath( "\"" + path + "\"" );
			}
		}
		String path = dialog.open( );
		if ( path == null || path.length( ) == 0 || !new File( path ).exists( ) )
		{
			shell.dispose( );
		}
		else
		{
			GAME_APPLICATION = new File( path );
			GAME_ROOT = new File( path ).getParentFile( );

			if ( !new File( GAME_ROOT, "alexander" ).exists( )
					|| !FileConstants.testFile( ) )
			{
				MessageDialog.openError( shell, "错误", "“"
						+ GAME_ROOT
						+ "”不是三国全面战争1.9a安装文件夹或者游戏安装文件不完整" );
				shell.dispose( );
				return;
			}

			Properties properties = new Properties( );
			properties.setProperty( "path", GAME_ROOT.getAbsolutePath( ) );
			try
			{
				if ( !file.exists( ) )
				{
					File appdata = new File( ShellFolder.LOCAL_APPDATA.getAbsolutePath( shell.handle ) );
					if ( appdata.exists( ) && !file.getParentFile( ).exists( ) )
						file.getParentFile( ).mkdirs( );
					file.createNewFile( );
				}
				properties.store( new FileOutputStream( file ), null );
			}
			catch ( IOException e )
			{
				e.printStackTrace( );
			}

			shell.setSize( 850, 650 );
			shell.setImages( new Image[]{
				ImageCache.getImage( "/patch.png" ),
			} );

			GridLayout layout = new GridLayout( );
			layout.marginWidth = layout.marginHeight = layout.verticalSpacing = 0;
			shell.setLayout( layout );
			shell.setText( "三国全面战争1.9a修改器"
					+ ( version != null ? version + "版" : "" )
					+ " - "
					+ GAME_ROOT.getAbsolutePath( ) );

			Composite container = new Composite( shell, SWT.NONE );
			container.setLayout( new FillLayout( ) );
			page = new BasePage( );
			page.buildUI( container );
			container.setLayoutData( new GridData( GridData.FILL_BOTH ) );
			provider = CategoryProviderFactory.getInstance( )
					.getCategoryProvider( );
			page.setCategoryProvider( provider );
			select( provider.getCategories( ).length - 1 );

			shell.addDisposeListener( new DisposeListener( ) {

				public void widgetDisposed( DisposeEvent arg0 )
				{

					if ( Windows.isWindowTransparent( shell.handle ) )
					{
						Windows.setWindowTransparent( shell.handle, false );
					}
					Windows.hideWindowBlend( shell.handle, 1000 );
				}

			} );
			shell.layout( );
			wrapper = new ShellWrapper( shell );
			wrapper.open( );

			showInfo( "", "正在初始化修改器..." );
			Thread thread = new Thread( ) {

				public void run( )
				{
					MapUtil.initMap( );
					Display.getDefault( ).syncExec( new Runnable( ) {

						public void run( )
						{
							hideInfo( );
							BakUtil.checkDefaultBak( );
						}
					} );
				}
			};
			thread.setDaemon( true );
			thread.start( );

		}

		while ( !shell.isDisposed( ) )
		{
			if ( !display.readAndDispatch( ) )
				display.sleep( );
		}
		display.dispose( );

		if ( UpdateUtil.needUpdate )
		{
			File updateFile = new File( UpdateUtil.UPDATE_EXE );
			if ( updateFile.exists( ) )
			{
				File lockFile = new File( FileSystem.getCurrentDirectory( ),
						".lock" );
				lockFile.deleteOnExit( );
				Program.launch( UpdateUtil.UPDATE_EXE );
			}
		}
	}

	public void select( int index )
	{
		page.setSelection( index );
		page.refresh( );
	}

	private static Patch patch;
	private Shell shell;
	public ShellWrapper wrapper;

	public Shell getShell( )
	{
		return shell;
	}

	public static void main( String[] args )
	{
		try
		{
			patch = new Patch( args );
			patch.initial( );
		}
		catch ( Exception e )
		{
			e.printStackTrace( );
			popupExceptionLog( e );
		}
	}

	private static void popupExceptionLog( Exception e )
	{
		Display display = Display.getCurrent( );
		if ( display == null || display.isDisposed( ) )
			display = new Display( );
		Shell shell = null;
		if ( Patch.getInstance( ) != null )
			shell = Patch.getInstance( ).getShell( );
		ErrorLogDialog dialog = new ErrorLogDialog( shell );
		dialog.setMessage( "修改器发生未知错误，即将退出，您可以将错误日志发送到修改器反馈中心。" );
		StringBuffer buffer = new StringBuffer( );
		readErrorMsg( buffer, e );
		dialog.setText( buffer.toString( ) );
		dialog.setBlockOnOpen( true );
		dialog.open( );
		display.dispose( );
	}

	public static Patch getInstance( )
	{
		return patch;
	}

	public int getPageCount( )
	{
		return provider.getCategories( ).length;
	};

	public void showInfo( String info, String file )
	{
		if ( shell.isDisposed( ) )
			return;
		page.showInfo( info, file );
		wrapper.getClientArea( ).setEnabled( false );
	}

	public void hideInfo( )
	{
		if ( shell.isDisposed( ) )
			return;
		page.hideInfo( );
		wrapper.getClientArea( ).setEnabled( true );
	}

	public static void readErrorMsg( StringBuffer buffer, Exception e )
	{
		StringWriter writer = new StringWriter( );
		PrintWriter ps = new PrintWriter( writer );
		e.printStackTrace( ps );
		try
		{
			writer.close( );
		}
		catch ( IOException e1 )
		{
			e1.printStackTrace( );
		}
		buffer.append( writer.toString( ) + "\r\n" ); //$NON-NLS-1$
	}
}