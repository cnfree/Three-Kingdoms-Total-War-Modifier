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

package org.sf.feeling.sanguo.patch.widget;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.internal.forms.widgets.FormUtil;

public class WidgetUtil
{

	public static int computeStringLength( Control control, String s )
	{
		GC gc = new GC( control );
		char[] chars = s.toCharArray( );
		int length = 0;
		for ( int i = 0; i < chars.length; i++ )
		{
			length += gc.getAdvanceWidth( chars[i] );
		}
		gc.dispose( );
		return length;
	}

	public static Label createLabel( Composite parent )
	{
		return createLabel( parent, "" );
	}

	protected static Label createLabel( Composite parent, int span )
	{
		Label label = getToolkit( ).createLabel( parent, "" );
		GridData gd = new GridData( GridData.FILL_HORIZONTAL );
		gd.horizontalSpan = 4;
		label.setLayoutData( gd );
		return label;
	}

	public static FormText createFormText( Composite parent, boolean trackFocus )
	{
		FormText engine = new FormText( parent, SWT.WRAP
				| getToolkit( ).getOrientation( ) ) {

			public boolean isEnabled( )
			{
				return getEnabled( );
			}
		};
		engine.marginWidth = 1;
		engine.marginHeight = 0;
		engine.setHyperlinkSettings( getToolkit( ).getHyperlinkGroup( ) );
		getToolkit( ).adapt( engine, trackFocus, true );
		engine.setMenu( parent.getMenu( ) );
		return engine;
	}

	public static FormText createFormText( Composite parent, String text,
			boolean parseTags, boolean expandURLs )
	{
		FormText rtext = createFormText( parent, false );
		TableWrapData td = new TableWrapData( TableWrapData.FILL );
		rtext.setLayoutData( td );
		rtext.setText( text, parseTags, expandURLs );
		return rtext;
	}

	public static FormText createFormText( Composite parent, String text )
	{
		return createFormText( parent, text, false, false );
	}

	public static FormText createFormText( Composite parent )
	{
		return createFormText( parent, "", false, false );
	}

	public static Text createText( Composite parent, String text )
	{
		Text rtext = getToolkit( ).createText( parent, text, SWT.NONE );
		return rtext;
	}

	public static FormWidgetFactory getToolkit( )
	{
		return FormWidgetFactory.getInstance( );
	}

	public static Label createBoldLabel( Composite parent )
	{
		return createBoldLabel( parent, "" );
	}

	public static Label createBoldLabel( Composite parent, String text )
	{
		Label label = getToolkit( ).createLabel( parent, text );
		label.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
		label.setFont( FormUtil.createBoldFont( null, label.getFont( ) ) );
		return label;
	}

	public static Label createLabel( Composite parent, String text )
	{
		Label label = getToolkit( ).createLabel( parent, text );
		return label;
	}

	public static Button createButton( Composite parent, String text )
	{
		Button button = getToolkit( ).createButton( parent, text, SWT.PUSH );
		return button;
	}
}
