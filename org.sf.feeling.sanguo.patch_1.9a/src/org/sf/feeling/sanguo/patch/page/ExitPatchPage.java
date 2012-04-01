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
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.sf.feeling.sanguo.patch.util.BakUtil;
import org.sf.feeling.sanguo.patch.util.FileConstants;
import org.sf.feeling.sanguo.patch.util.FileUtil;
import org.sf.feeling.sanguo.patch.widget.WidgetUtil;

public class ExitPatchPage extends SimpleTabPage
{

	public void buildUI( Composite parent )
	{
		super.buildUI( parent );
		TableWrapLayout layout = new TableWrapLayout( );
		layout.leftMargin = 10;
		layout.rightMargin = 10;
		layout.topMargin = 15;
		layout.verticalSpacing = 20;
		container.getBody( ).setLayout( layout );

		createTitle( );
		createPatchArea( );

		FormText noteText = WidgetUtil.createFormText( container.getBody( ),
				"<form><p>注意：只有部分势力遇到东州兵兵模和骆驼兵兵模会频繁跳出，不是所有势力都会跳。</p></form>",
				true,
				true );
		TableWrapData data = new TableWrapData( TableWrapData.FILL );
		data.maxWidth = 600;
		noteText.setLayoutData( data );
	}

	private void createPatchArea( )
	{
		TableWrapData td;
		Section patchSection = WidgetUtil.getToolkit( )
				.createSection( container.getBody( ), Section.EXPANDED );
		td = new TableWrapData( TableWrapData.FILL );
		patchSection.setLayoutData( td );
		patchSection.setText( "功能列表：" );
		WidgetUtil.getToolkit( ).createCompositeSeparator( patchSection );
		Composite patchClient = WidgetUtil.getToolkit( )
				.createComposite( patchSection );
		GridLayout layout = new GridLayout( );
		layout.numColumns = 5;
		patchClient.setLayout( layout );

		
		{
			final Button lightSpearBtn = WidgetUtil.getToolkit( )
					.createButton( patchClient, "替换刘璋东州兵兵模为山岳步兵兵模", SWT.CHECK );
			GridData gd = new GridData( );

			gd.horizontalSpan = 3;
			lightSpearBtn.setLayoutData( gd );
			final Button lightSpearApply = WidgetUtil.getToolkit( )
					.createButton( patchClient, "应用", SWT.PUSH );
			lightSpearApply.setEnabled( false );
			final Button lightSpearRestore = WidgetUtil.getToolkit( )
					.createButton( patchClient, "还原", SWT.PUSH );
			lightSpearBtn.addSelectionListener( new SelectionAdapter( ) {

				public void widgetSelected( SelectionEvent e )
				{
					lightSpearApply.setEnabled( lightSpearBtn.getSelection( ) );
				}

			} );
			lightSpearApply.addSelectionListener( new SelectionAdapter( ) {

				public void widgetSelected( SelectionEvent e )
				{
					if ( FileConstants.unitFile.exists( ) )
					{
						lightSpearApply.setEnabled( false );
						BakUtil.bakData( "替换刘璋东州兵兵模为山岳步兵兵模" );
						FileUtil.replaceFile( FileConstants.unitFile, "(?i)soldier\\s+light_spearmen", "(?i)light_spearmen", "Shanyue_Liuzhang" );
						FileUtil.replaceFile( FileConstants.unitFile, "(?i)officer\\s+light_spearmen", "(?i)light_spearmen", "Shanyue_Liuzhang" );
						lightSpearApply.setEnabled( true );
					}
				}
			} );
			lightSpearRestore.addSelectionListener( new SelectionAdapter( ) {

				public void widgetSelected( SelectionEvent e )
				{
					lightSpearRestore.setEnabled( false );
					BakUtil.restoreCurrectVersionBakFile( );
					lightSpearRestore.setEnabled( true );
				}
			} );
		}
		{
			final Button camelBtn = WidgetUtil.getToolkit( )
					.createButton( patchClient, "替换骆驼兵模为铁甲马兵模", SWT.CHECK );
			GridData gd = new GridData( );

			gd.horizontalSpan = 3;
			camelBtn.setLayoutData( gd );
			final Button camelApply = WidgetUtil.getToolkit( )
					.createButton( patchClient, "应用", SWT.PUSH );
			camelApply.setEnabled( false );
			final Button camelRestore = WidgetUtil.getToolkit( )
					.createButton( patchClient, "还原", SWT.PUSH );
			camelBtn.addSelectionListener( new SelectionAdapter( ) {

				public void widgetSelected( SelectionEvent e )
				{
					camelApply.setEnabled( camelBtn.getSelection( ) );
				}
			} );
			camelApply.addSelectionListener( new SelectionAdapter( ) {

				public void widgetSelected( SelectionEvent e )
				{

					if ( FileConstants.unitFile.exists( ) )
					{
						camelApply.setEnabled( false );
						BakUtil.bakData( "替换骆驼兵模为铁甲马兵模" );
						FileUtil.replaceFile( FileConstants.unitFile, "(?i)mount\\s+LLH chincamel", "(?i)LLH chincamel", "LLH tijiama" );
						FileUtil.replaceFile( FileConstants.unitFile, "(?i)mount\\s+camel cataphract", "(?i)camel cataphract", "LLH tijiama" );
						camelApply.setEnabled( true );
					}
				}
			} );
			camelRestore.addSelectionListener( new SelectionAdapter( ) {

				public void widgetSelected( SelectionEvent e )
				{
					camelRestore.setEnabled( false );
					BakUtil.restoreCurrectVersionBakFile( );
					camelRestore.setEnabled( true );
				}
			} );
		}

		patchSection.setClient( patchClient );
	}

	private void createTitle( )
	{
		WidgetUtil.createFormText( container.getBody( ),
				"通过本页面修改部分兵种模型可以减少游戏战斗跳出，配置完毕后重启游戏即可生效。" );
	}

	public String getDisplayName( )
	{
		return "防跳补丁";
	}


}
