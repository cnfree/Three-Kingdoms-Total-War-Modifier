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

package com.actuate.tool.development.tool.dialog;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.sf.feeling.swt.win32.internal.extension.util.ColorCache;
import org.sf.feeling.swt.win32.internal.extension.util.ImageCache;

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
		shell.setText( "Error Log" );
		shell.setImages( new Image[]{
				ImageCache.getImage( "/icons/actuate_16.png" ),
				ImageCache.getImage( "/icons/actuate_32.png" ),
				ImageCache.getImage( "/icons/actuate_48.png" )
		} ); //$NON-NLS-1$
	}

	protected void okPressed( )
	{
		super.okPressed( );
	}

	public boolean close( )
	{
		font.dispose( );
		return super.close( );
	}

	protected Control createButtonBar( Composite parent )
	{
		Control control = super.createButtonBar( parent );
		if ( getButton( IDialogConstants.OK_ID ) != null )
		{
			getButton( IDialogConstants.OK_ID ).setText( "OK" );
		}
		if ( getButton( IDialogConstants.CANCEL_ID ) != null )
		{
			getButton( IDialogConstants.CANCEL_ID ).setText( "Cancel" );
		}
		return control;
	}
}
