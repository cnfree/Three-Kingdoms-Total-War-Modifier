/*******************************************************************************
 * Copyright (c) 2011 cnfree.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  cnfree  - initial API and implementation
 *******************************************************************************/

package org.sf.feeling.sanguo.patch.dialog;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.sf.feeling.sanguo.patch.Patch;
import org.sf.feeling.swt.win32.extension.widgets.ShellWrapper;
import org.sf.feeling.swt.win32.internal.extension.util.ColorCache;

/**
 * 
 */

public class ErrorLogDialog extends Dialog
{

	private StyledText viewer;

	public StyledText getViewer( )
	{
		return viewer;
	}

	private Font font;

	public ErrorLogDialog( Shell parentShell )
	{
		super( parentShell );
		setShellStyle( getShellStyle( ) | SWT.RESIZE | SWT.MAX | SWT.RESIZE );
	}

	protected Control createDialogArea( Composite parent )
	{
		Composite composite = (Composite) super.createDialogArea( parent );
		Label label = new Label( composite, SWT.NONE );
		if ( message != null )
			label.setText( message );
		viewer = new StyledText( composite, SWT.MULTI
				| SWT.H_SCROLL
				| SWT.V_SCROLL
				| SWT.BORDER
				| SWT.FULL_SELECTION );
		viewer.addKeyListener( new KeyAdapter( ) {

			public void keyPressed( KeyEvent e )
			{
				if ( e.stateMask == SWT.CTRL && e.keyCode == 97 )
				{
					viewer.selectAll( );
				}
			}
		} );
		viewer.setForeground( ColorCache.getInstance( ).getColor( 255, 0, 0 ) );
		FontData fontData = new FontData( "Courier New", 10, SWT.NORMAL ); //$NON-NLS-1$
		fontData.setHeight( 10 );
		font = new Font( viewer.getDisplay( ), fontData );
		viewer.setFont( font );
		viewer.setLayoutData( new GridData( GridData.FILL_BOTH ) );
		if ( text != null )
			viewer.setText( text );
		return composite;
	}

	private String message;

	public String getMessage( )
	{
		return message;
	}

	public void setMessage( String message )
	{
		this.message = message;
	}

	private String text;

	public void setText( String text )
	{
		this.text = text;
		if ( viewer != null )
		{
			viewer.setText( text );
		}
	}

	public String getText( )
	{
		if ( viewer != null )
			return viewer.getText( );
		return this.text;
	}

	protected Point getInitialSize( )
	{
		return new Point( 500, 325 );
	}

	protected void configureShell( Shell shell )
	{
		super.configureShell( shell );
		shell.setText( "错误日志" ); //$NON-NLS-1$
		shell.setImage( ImageDescriptor.createFromFile( getClass( ),
				"/patch.png" ).createImage( ) ); //$NON-NLS-1$
	}

	public void create( )
	{
		super.create( );
		if ( Patch.getInstance( ) != null
				&& Patch.getInstance( ).wrapper != null
				&& Patch.getInstance( ).wrapper.isThemeInstalled( ) )
		{
			ShellWrapper wrapper = new ShellWrapper( getShell( ) );
			wrapper.installTheme( Patch.getInstance( ).wrapper.getTheme( ) );
		}
	}

	protected Control createButtonBar( Composite parent )
	{
		Control control = super.createButtonBar( parent );
		if ( getButton( IDialogConstants.OK_ID ) != null )
		{
			getButton( IDialogConstants.OK_ID ).setText( "确定" ); //$NON-NLS-1$
		}
		if ( getButton( IDialogConstants.CANCEL_ID ) != null )
		{
			getButton( IDialogConstants.CANCEL_ID ).setText( "取消" ); //$NON-NLS-1$
		}
		return control;
	}
	public boolean close( )
	{
		font.dispose( );
		return super.close( );
	}
}
