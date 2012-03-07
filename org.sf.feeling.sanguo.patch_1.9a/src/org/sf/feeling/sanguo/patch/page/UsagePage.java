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

package org.sf.feeling.sanguo.patch.page;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;

public class UsagePage extends TabPage
{

	private Browser browser;

	public void buildUI( Composite parent )
	{
		browser = new Browser( parent, SWT.NONE );

		try
		{
			URL url = new URL( "http://feeling.sourceforge.net/patch/1.9a/usage.txt" );
			HttpURLConnection conn = (HttpURLConnection) url.openConnection( );
			conn.setUseCaches( false );
			BufferedReader reader = new BufferedReader( new InputStreamReader( conn.getInputStream( ) ) );
			String website = reader.readLine( );
			conn.disconnect( );
			browser.setUrl( website );
		}
		catch ( IOException e )
		{
			browser.setText( "无法获取修改器使用手册网络地址，请恢复网络后重试。" );
		}
	}

	public Composite getControl( )
	{
		return browser;
	}

	public String getDisplayName( )
	{
		return "修改器使用手册（感谢吧友王妃婧婧友情提供）";
	}

}
