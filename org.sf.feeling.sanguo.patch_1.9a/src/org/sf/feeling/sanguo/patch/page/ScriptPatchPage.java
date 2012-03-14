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

public class ScriptPatchPage extends SimpleTabPage
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
				"<form><p>注意：<br/>1、心灰意冷或心怀不满状态的武将无法被收买，解除该状态后武将负面效果依然存在，并可被收买。" +
				"<br/>2、只有部分武将特殊技能能够全势力爆发。</p></form>",
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

		// final Button bugFixBtn = WidgetUtil.getToolkit().createButton(
		// patchClient, "修正高顺“陷阵营督统”称号无法激活bug", SWT.CHECK);
		// GridData gd = new GridData();
		//
		// gd.horizontalSpan = 3;
		// bugFixBtn.setLayoutData(gd);
		//
		// final Button bugFixApply = WidgetUtil.getToolkit().createButton(
		// patchClient, "应用", SWT.PUSH);
		// bugFixApply.setEnabled(false);
		// final Button bugFixRestore = WidgetUtil.getToolkit().createButton(
		// patchClient, "还原", SWT.PUSH);
		//
		// bugFixApply.addSelectionListener(new SelectionAdapter() {
		//
		// public void widgetSelected(SelectionEvent e) {
		//
		// if (FileConstants.characterTraitFile.exists()) {
		// bugFixApply.setEnabled(false);
		// BakUtil.bakData("高顺陷阵营督统修改");
		// // FileUtil.bakFile(FileConstants.characterTraitFile);
		// if (!(FileUtil.containMatchString(
		// FileConstants.characterTraitFile,
		// "(Trigger J-0402-GaoShun-1100-6)")
		// || FileUtil.containMatchString(
		// FileConstants.characterTraitFile,
		// "(Trigger J-0402-GaoShun-1100-7)") || FileUtil
		// .containMatchString(
		// FileConstants.characterTraitFile,
		// "(Trigger J-0402-GaoShun-1100-8)"))
		// && FileUtil.containMatchString(
		// FileConstants.characterTraitFile,
		// "GaoShun1100")) {
		// FileUtil
		// .appendToFile(
		// FileConstants.characterTraitFile,
		// ScriptPatchPage.class
		// .getResourceAsStream("/org/sf/feeling/sanguo/patch/script/gaoshun.txt"));
		// }
		// bugFixApply.setEnabled(true);
		// }
		// }
		// });
		//
		// bugFixRestore.addSelectionListener(new SelectionAdapter() {
		//
		// public void widgetSelected(SelectionEvent e) {
		// bugFixRestore.setEnabled(false);
		// BakUtil.restoreCurrectVersionBakFile();
		// refreshPage();
		// bugFixRestore.setEnabled(true);
		// }
		// });
		//
		// bugFixBtn.addSelectionListener(new SelectionAdapter() {
		//
		// public void widgetSelected(SelectionEvent e) {
		// bugFixApply.setEnabled(bugFixBtn.getSelection());
		// }
		//
		// });
		// {
		// final Button qiaoduoBtn = WidgetUtil.getToolkit().createButton(
		// patchClient, "激活赵云巧夺技能（此技能仅对赵云有效）", SWT.CHECK);
		// gd = new GridData();
		//
		// gd.horizontalSpan = 3;
		// qiaoduoBtn.setLayoutData(gd);
		// final Button qiaoduoApply = WidgetUtil.getToolkit().createButton(
		// patchClient, "应用", SWT.PUSH);
		// qiaoduoApply.setEnabled(false);
		// final Button qiaoduoRestore = WidgetUtil.getToolkit().createButton(
		// patchClient, "还原", SWT.PUSH);
		// qiaoduoBtn.addSelectionListener(new SelectionAdapter() {
		//
		// public void widgetSelected(SelectionEvent e) {
		// qiaoduoApply.setEnabled(qiaoduoBtn.getSelection());
		// }
		//
		// });
		// qiaoduoApply.addSelectionListener(new SelectionAdapter() {
		//
		// public void widgetSelected(SelectionEvent e) {
		//
		// if (FileConstants.baowuFile.exists()) {
		// qiaoduoApply.setEnabled(false);
		// BakUtil.bakData("赵云巧夺技能修改");
		// // FileUtil.bakFile(FileConstants.baowuFile);
		// BaowuParser.zhaoyunQiaoduoModify();
		// qiaoduoApply.setEnabled(true);
		// }
		// }
		// });
		// qiaoduoRestore.addSelectionListener(new SelectionAdapter() {
		//
		// public void widgetSelected(SelectionEvent e) {
		// qiaoduoRestore.setEnabled(false);
		// BakUtil.restoreCurrectVersionBakFile();
		// refreshPage();
		// qiaoduoRestore.setEnabled(true);
		// }
		// });
		// }
		{
			final Button nengliBtn = WidgetUtil.getToolkit( )
					.createButton( patchClient, "解除部分武将能力深藏状态", SWT.CHECK );
			GridData gd = new GridData( );

			gd.horizontalSpan = 3;
			nengliBtn.setLayoutData( gd );
			final Button nengliApply = WidgetUtil.getToolkit( )
					.createButton( patchClient, "应用", SWT.PUSH );
			nengliApply.setEnabled( false );
			final Button nengliRestore = WidgetUtil.getToolkit( )
					.createButton( patchClient, "还原", SWT.PUSH );
			nengliBtn.addSelectionListener( new SelectionAdapter( ) {

				public void widgetSelected( SelectionEvent e )
				{
					nengliApply.setEnabled( nengliBtn.getSelection( ) );
				}

			} );
			nengliApply.addSelectionListener( new SelectionAdapter( ) {

				public void widgetSelected( SelectionEvent e )
				{

					if ( FileConstants.characterTraitFile.exists( ) )
					{
						nengliApply.setEnabled( false );
						BakUtil.bakData( "解除部分武将能力深藏状态" );
						// FileUtil.bakFile(FileConstants.characterTraitFile);
						if ( !( FileUtil.containMatchString( FileConstants.characterTraitFile,
								"(?i)(Trigger\\s+NengLiShenCang1100-Patch)" ) )
								&& FileUtil.containMatchString( FileConstants.characterTraitFile,
										"(?i)NengLiShenCang1200" ) )
						{
							FileUtil.appendToFile( FileConstants.characterTraitFile,
									ScriptPatchPage.class.getResourceAsStream( "/org/sf/feeling/sanguo/patch/script/nenglishengcang.txt" ) );
						}
						nengliApply.setEnabled( true );
					}
				}
			} );
			nengliRestore.addSelectionListener( new SelectionAdapter( ) {

				public void widgetSelected( SelectionEvent e )
				{
					nengliRestore.setEnabled( false );
					BakUtil.restoreCurrectVersionBakFile( );
					nengliRestore.setEnabled( true );
				}
			} );
		}
		{
			final Button mingyunBtn = WidgetUtil.getToolkit( )
					.createButton( patchClient, "解除部分武将命运危机状态", SWT.CHECK );
			GridData gd = new GridData( );

			gd.horizontalSpan = 3;
			mingyunBtn.setLayoutData( gd );
			final Button mingyunApply = WidgetUtil.getToolkit( )
					.createButton( patchClient, "应用", SWT.PUSH );
			mingyunApply.setEnabled( false );
			final Button mingyunRestore = WidgetUtil.getToolkit( )
					.createButton( patchClient, "还原", SWT.PUSH );
			mingyunBtn.addSelectionListener( new SelectionAdapter( ) {

				public void widgetSelected( SelectionEvent e )
				{
					mingyunApply.setEnabled( mingyunBtn.getSelection( ) );
				}
			} );
			mingyunApply.addSelectionListener( new SelectionAdapter( ) {

				public void widgetSelected( SelectionEvent e )
				{

					if ( FileConstants.characterTraitFile.exists( ) )
					{
						mingyunApply.setEnabled( false );
						BakUtil.bakData( "解除部分武将命运危机状态" );
						// FileUtil.bakFile(FileConstants.characterTraitFile);
						if ( !( FileUtil.containMatchString( FileConstants.characterTraitFile,
								"(?i)(Trigger\\s+MingYun1100-Patch)" ) )
								&& FileUtil.containMatchString( FileConstants.characterTraitFile,
										"(?i)MingYun1100" ) )
						{
							FileUtil.appendToFile( FileConstants.characterTraitFile,
									ScriptPatchPage.class.getResourceAsStream( "/org/sf/feeling/sanguo/patch/script/mingyunweiji.txt" ) );
						}
						mingyunApply.setEnabled( true );
					}
				}
			} );
			mingyunRestore.addSelectionListener( new SelectionAdapter( ) {

				public void widgetSelected( SelectionEvent e )
				{
					mingyunRestore.setEnabled( false );
					BakUtil.restoreCurrectVersionBakFile( );
					mingyunRestore.setEnabled( true );
				}
			} );
		}
		{
			final Button weiyanBtn = WidgetUtil.getToolkit( )
					.createButton( patchClient, "解除魏延持功骄狂负面影响", SWT.CHECK );
			GridData gd = new GridData( );

			gd.horizontalSpan = 3;
			weiyanBtn.setLayoutData( gd );
			final Button weiyanApply = WidgetUtil.getToolkit( )
					.createButton( patchClient, "应用", SWT.PUSH );
			weiyanApply.setEnabled( false );
			final Button weiyanRestore = WidgetUtil.getToolkit( )
					.createButton( patchClient, "还原", SWT.PUSH );
			weiyanBtn.addSelectionListener( new SelectionAdapter( ) {

				public void widgetSelected( SelectionEvent e )
				{
					weiyanApply.setEnabled( weiyanBtn.getSelection( ) );
				}
			} );
			weiyanApply.addSelectionListener( new SelectionAdapter( ) {

				public void widgetSelected( SelectionEvent e )
				{

					if ( FileConstants.characterTraitFile.exists( ) )
					{
						weiyanApply.setEnabled( false );
						BakUtil.bakData( "解除魏延持功骄狂负面影响" );
						// FileUtil.bakFile(FileConstants.characterTraitFile);
						try
						{
							String line = null;
							StringWriter writer = new StringWriter( );
							PrintWriter printer = new PrintWriter( writer );
							BufferedReader in = new BufferedReader( new InputStreamReader( new FileInputStream( FileConstants.characterTraitFile ),
									"GBK" ) );
							boolean startWeiyan = false;
							while ( ( line = in.readLine( ) ) != null )
							{
								if ( !startWeiyan )
								{
									Pattern pattern = Pattern.compile( "(?i)^\\s*(Trait)(\\s+)(WeiYan1200)(\\s*)" );
									Matcher matcher = pattern.matcher( line );
									if ( matcher.find( ) )
									{
										startWeiyan = true;
									}
								}
								else
								{
									Pattern pattern = Pattern.compile( "(?i)^\\s*(Effect)(\\s+)(.+)(;*)" );
									Matcher matcher = pattern.matcher( line );
									if ( matcher.find( ) )
									{
										if ( matcher.group( ).indexOf( "Law" ) > -1 )
											continue;
										if ( matcher.group( )
												.indexOf( "Unrest" ) > -1 )
											continue;
										if ( matcher.group( )
												.indexOf( "Influence" ) > -1 )
											continue;
									}
									pattern = Pattern.compile( "(?i)^\\s*(Trait)(\\s+)" );
									matcher = pattern.matcher( line );
									if ( matcher.find( ) )
									{
										startWeiyan = false;
									}
								}
								printer.println( line );
							}
							in.close( );

							PrintWriter out = new PrintWriter( new BufferedWriter( new OutputStreamWriter( new FileOutputStream( FileConstants.characterTraitFile ),
									"GBK" ) ),
									false );
							out.print( writer.getBuffer( ) );
							out.close( );
						}
						catch ( IOException e1 )
						{
							e1.printStackTrace( );
						}
						weiyanApply.setEnabled( true );
					}
				}
			} );
			weiyanRestore.addSelectionListener( new SelectionAdapter( ) {

				public void widgetSelected( SelectionEvent e )
				{
					weiyanRestore.setEnabled( false );
					BakUtil.restoreCurrectVersionBakFile( );
					weiyanRestore.setEnabled( true );
				}
			} );
		}
		{
			final Button caocaoBtn = WidgetUtil.getToolkit( )
					.createButton( patchClient, "解除曹操头痛隐疾负面影响", SWT.CHECK );
			GridData gd = new GridData( );

			gd.horizontalSpan = 3;
			caocaoBtn.setLayoutData( gd );
			final Button caocaoApply = WidgetUtil.getToolkit( )
					.createButton( patchClient, "应用", SWT.PUSH );
			caocaoApply.setEnabled( false );
			final Button caocaoRestore = WidgetUtil.getToolkit( )
					.createButton( patchClient, "还原", SWT.PUSH );
			caocaoBtn.addSelectionListener( new SelectionAdapter( ) {

				public void widgetSelected( SelectionEvent e )
				{
					caocaoApply.setEnabled( caocaoBtn.getSelection( ) );
				}
			} );
			caocaoApply.addSelectionListener( new SelectionAdapter( ) {

				public void widgetSelected( SelectionEvent e )
				{

					if ( FileConstants.characterTraitFile.exists( ) )
					{
						caocaoApply.setEnabled( false );
						BakUtil.bakData( "解除曹操头痛隐疾负面影响" );
						// FileUtil.bakFile(FileConstants.characterTraitFile);
						try
						{
							String line = null;
							StringWriter writer = new StringWriter( );
							PrintWriter printer = new PrintWriter( writer );
							BufferedReader in = new BufferedReader( new InputStreamReader( new FileInputStream( FileConstants.characterTraitFile ),
									"GBK" ) );
							boolean startCaocao = false;
							while ( ( line = in.readLine( ) ) != null )
							{
								if ( !startCaocao )
								{
									Pattern pattern = Pattern.compile( "(?i)^\\s*(Trait)(\\s+)(Caocao2020)(\\s*)" );
									Matcher matcher = pattern.matcher( line );
									if ( matcher.find( ) )
									{
										startCaocao = true;
									}
								}
								else
								{
									Pattern pattern = Pattern.compile( "(?i)^\\s*(Effect)(\\s+)(.+)(;*)" );
									Matcher matcher = pattern.matcher( line );
									if ( matcher.find( ) )
									{
										continue;
									}
									pattern = Pattern.compile( "(?i)^\\s*(Trait)(\\s+)" );
									matcher = pattern.matcher( line );
									if ( matcher.find( ) )
									{
										startCaocao = false;
									}
								}
								printer.println( line );
							}
							in.close( );

							PrintWriter out = new PrintWriter( new BufferedWriter( new OutputStreamWriter( new FileOutputStream( FileConstants.characterTraitFile ),
									"GBK" ) ),
									false );
							out.print( writer.getBuffer( ) );
							out.close( );
						}
						catch ( IOException e1 )
						{
							e1.printStackTrace( );
						}
						caocaoApply.setEnabled( true );
					}
				}
			} );
			caocaoRestore.addSelectionListener( new SelectionAdapter( ) {

				public void widgetSelected( SelectionEvent e )
				{
					caocaoRestore.setEnabled( false );
					BakUtil.restoreCurrectVersionBakFile( );
					caocaoRestore.setEnabled( true );
				}
			} );
		}
		{
			final Button xinhuiyilengBtn = WidgetUtil.getToolkit( )
					.createButton( patchClient, "解除部分武将心灰意冷或心怀不满状态", SWT.CHECK );
			GridData gd = new GridData( );

			gd.horizontalSpan = 3;
			xinhuiyilengBtn.setLayoutData( gd );
			final Button xinhuiyilengApply = WidgetUtil.getToolkit( )
					.createButton( patchClient, "应用", SWT.PUSH );
			xinhuiyilengApply.setEnabled( false );
			final Button xinhuiyilengRestore = WidgetUtil.getToolkit( )
					.createButton( patchClient, "还原", SWT.PUSH );
			xinhuiyilengBtn.addSelectionListener( new SelectionAdapter( ) {

				public void widgetSelected( SelectionEvent e )
				{
					xinhuiyilengApply.setEnabled( xinhuiyilengBtn.getSelection( ) );
				}

			} );
			xinhuiyilengApply.addSelectionListener( new SelectionAdapter( ) {

				public void widgetSelected( SelectionEvent e )
				{

					if ( FileConstants.characterTraitFile.exists( ) )
					{
						xinhuiyilengApply.setEnabled( false );
						BakUtil.bakData( "解除部分武将心灰意冷或心怀不满状态" );
						// FileUtil.bakFile(FileConstants.characterTraitFile);
						if ( !( FileUtil.containMatchString( FileConstants.characterTraitFile,
								"(?i)(Trigger\\s+ZhongCheng3100-Patch)" ) ) )
						{
							FileUtil.appendToFile( FileConstants.characterTraitFile,
									ScriptPatchPage.class.getResourceAsStream( "/org/sf/feeling/sanguo/patch/script/xinhuiyileng.txt" ) );
						}
						if ( !( FileUtil.containMatchString( FileConstants.characterTraitFile,
								"(?i)(Trigger\\s+ZhongCheng2100-Patch)" ) ) )
						{
							FileUtil.appendToFile( FileConstants.characterTraitFile,
									ScriptPatchPage.class.getResourceAsStream( "/org/sf/feeling/sanguo/patch/script/xinhuaibuman.txt" ) );
						}
						xinhuiyilengApply.setEnabled( true );
					}
				}
			} );
			xinhuiyilengRestore.addSelectionListener( new SelectionAdapter( ) {

				public void widgetSelected( SelectionEvent e )
				{
					xinhuiyilengRestore.setEnabled( false );
					BakUtil.restoreCurrectVersionBakFile( );
					xinhuiyilengRestore.setEnabled( true );
				}
			} );
		}
		{
			final Button xinhuiyilengBtn = WidgetUtil.getToolkit( )
					.createButton( patchClient, "解除诸葛亮外出云游状态", SWT.CHECK );
			GridData gd = new GridData( );

			gd.horizontalSpan = 3;
			xinhuiyilengBtn.setLayoutData( gd );
			final Button xinhuiyilengApply = WidgetUtil.getToolkit( )
					.createButton( patchClient, "应用", SWT.PUSH );
			xinhuiyilengApply.setEnabled( false );
			final Button xinhuiyilengRestore = WidgetUtil.getToolkit( )
					.createButton( patchClient, "还原", SWT.PUSH );
			xinhuiyilengBtn.addSelectionListener( new SelectionAdapter( ) {

				public void widgetSelected( SelectionEvent e )
				{
					xinhuiyilengApply.setEnabled( xinhuiyilengBtn.getSelection( ) );
				}

			} );
			xinhuiyilengApply.addSelectionListener( new SelectionAdapter( ) {

				public void widgetSelected( SelectionEvent e )
				{

					if ( FileConstants.characterTraitFile.exists( ) )
					{
						xinhuiyilengApply.setEnabled( false );
						BakUtil.bakData( "解除诸葛亮外出云游状态" );
						// FileUtil.bakFile(FileConstants.characterTraitFile);
						if ( !( FileUtil.containMatchString( FileConstants.characterTraitFile,
								"(?i)(Trigger\\s+ZhuGeLiang3100-Patch)" ) ) )
						{
							FileUtil.appendToFile( FileConstants.characterTraitFile,
									ScriptPatchPage.class.getResourceAsStream( "/org/sf/feeling/sanguo/patch/script/waichuyunyou.txt" ) );
						}
						xinhuiyilengApply.setEnabled( true );
					}
				}
			} );
			xinhuiyilengRestore.addSelectionListener( new SelectionAdapter( ) {

				public void widgetSelected( SelectionEvent e )
				{
					xinhuiyilengRestore.setEnabled( false );
					BakUtil.restoreCurrectVersionBakFile( );
					xinhuiyilengRestore.setEnabled( true );
				}
			} );
		}
		{
			final Button zhongchengBtn = WidgetUtil.getToolkit( )
					.createButton( patchClient, "降低电脑势力所有武将忠诚度便于收买", SWT.CHECK );
			GridData gd = new GridData( );
			gd.horizontalSpan = 3;
			zhongchengBtn.setLayoutData( gd );
			final Button zhongchengApply = WidgetUtil.getToolkit( )
					.createButton( patchClient, "应用", SWT.PUSH );
			zhongchengApply.setEnabled( false );
			final Button zhongchengRestore = WidgetUtil.getToolkit( )
					.createButton( patchClient, "还原", SWT.PUSH );
			zhongchengBtn.addSelectionListener( new SelectionAdapter( ) {

				public void widgetSelected( SelectionEvent e )
				{
					zhongchengApply.setEnabled( zhongchengBtn.getSelection( ) );
				}

			} );
			zhongchengApply.addSelectionListener( new SelectionAdapter( ) {

				public void widgetSelected( SelectionEvent e )
				{

					if ( FileConstants.characterTraitFile.exists( ) )
					{
						zhongchengApply.setEnabled( false );
						BakUtil.bakData( "降低电脑势力所有武将忠诚度" );
						// FileUtil.bakFile(FileConstants.characterTraitFile);
						if ( !( FileUtil.containMatchString( FileConstants.characterTraitFile,
								"(?i)(Trigger\\s+ZhongCheng-Patch)" ) ) )
						{
							FileUtil.appendToFile( FileConstants.characterTraitFile,
									ScriptPatchPage.class.getResourceAsStream( "/org/sf/feeling/sanguo/patch/script/zhongcheng.txt" ) );
						}
						zhongchengApply.setEnabled( true );
					}
				}
			} );
			zhongchengRestore.addSelectionListener( new SelectionAdapter( ) {

				public void widgetSelected( SelectionEvent e )
				{
					zhongchengRestore.setEnabled( false );
					BakUtil.restoreCurrectVersionBakFile( );
					zhongchengRestore.setEnabled( true );
				}
			} );
		}
		{
			final Button guanjueBtn = WidgetUtil.getToolkit( )
					.createButton( patchClient, "武将全势力官爵晋级", SWT.CHECK );
			GridData gd = new GridData( );
			gd.horizontalSpan = 3;
			guanjueBtn.setLayoutData( gd );
			final Button guanjueApply = WidgetUtil.getToolkit( )
					.createButton( patchClient, "应用", SWT.PUSH );
			guanjueApply.setEnabled( false );
			final Button guanjueRestore = WidgetUtil.getToolkit( )
					.createButton( patchClient, "还原", SWT.PUSH );
			guanjueBtn.addSelectionListener( new SelectionAdapter( ) {

				public void widgetSelected( SelectionEvent e )
				{
					guanjueApply.setEnabled( guanjueBtn.getSelection( ) );
				}

			} );
			guanjueApply.addSelectionListener( new SelectionAdapter( ) {

				public void widgetSelected( SelectionEvent e )
				{

					if ( FileConstants.characterTraitFile.exists( ) )
					{
						guanjueApply.setEnabled( false );
						BakUtil.bakData( "武将全势力官爵晋级" );
						try
						{
							String line = null;
							StringWriter writer = new StringWriter( );
							PrintWriter printer = new PrintWriter( writer );
							BufferedReader in = new BufferedReader( new InputStreamReader( new FileInputStream( FileConstants.characterTraitFile ),
									"GBK" ) );
							boolean startGuanJue = false;
							while ( ( line = in.readLine( ) ) != null )
							{
								if ( !startGuanJue )
								{
									Pattern pattern = Pattern.compile( "(?i)^\\s*Trigger\\s+GJ\\-" );
									Matcher matcher = pattern.matcher( line );
									if ( matcher.find( ) )
									{
										startGuanJue = true;
										printer.println( line );

										while ( ( line = in.readLine( ) ) != null )
										{
											pattern = Pattern.compile( "(?i)^\\s*Affects" );
											matcher = pattern.matcher( line );
											if ( matcher.find( ) )
											{
												startGuanJue = false;
												break;
											}

											pattern = Pattern.compile( "(?i)^\\s*and\\s+FactionType" );
											matcher = pattern.matcher( line );
											if ( matcher.find( ) )
											{
												continue;
											}
											else
											{
												printer.println( line );
											}
										}
									}
								}
								printer.println( line );
							}
							in.close( );

							PrintWriter out = new PrintWriter( new BufferedWriter( new OutputStreamWriter( new FileOutputStream( FileConstants.characterTraitFile ),
									"GBK" ) ),
									false );
							out.print( writer.getBuffer( ) );
							out.close( );
						}
						catch ( IOException e1 )
						{
							e1.printStackTrace( );
						}
						guanjueApply.setEnabled( true );
					}
				}
			} );
			guanjueRestore.addSelectionListener( new SelectionAdapter( ) {

				public void widgetSelected( SelectionEvent e )
				{
					guanjueRestore.setEnabled( false );
					BakUtil.restoreCurrectVersionBakFile( );
					guanjueRestore.setEnabled( true );
				}
			} );
		}
		{
			final Button guanjueBtn = WidgetUtil.getToolkit( )
					.createButton( patchClient, "武将部分特殊技能全势力触发", SWT.CHECK );
			GridData gd = new GridData( );
			gd.horizontalSpan = 3;
			guanjueBtn.setLayoutData( gd );
			final Button guanjueApply = WidgetUtil.getToolkit( )
					.createButton( patchClient, "应用", SWT.PUSH );
			guanjueApply.setEnabled( false );
			final Button guanjueRestore = WidgetUtil.getToolkit( )
					.createButton( patchClient, "还原", SWT.PUSH );
			guanjueBtn.addSelectionListener( new SelectionAdapter( ) {

				public void widgetSelected( SelectionEvent e )
				{
					guanjueApply.setEnabled( guanjueBtn.getSelection( ) );
				}

			} );
			guanjueApply.addSelectionListener( new SelectionAdapter( ) {

				public void widgetSelected( SelectionEvent e )
				{

					if ( FileConstants.characterTraitFile.exists( ) )
					{
						guanjueApply.setEnabled( false );
						BakUtil.bakData( "武将部分特殊技能全势力触发" );
						try
						{
							String line = null;
							StringWriter writer = new StringWriter( );
							PrintWriter printer = new PrintWriter( writer );
							BufferedReader in = new BufferedReader( new InputStreamReader( new FileInputStream( FileConstants.characterTraitFile ),
									"GBK" ) );
							boolean startGuanJue = false;
							while ( ( line = in.readLine( ) ) != null )
							{
								if ( !startGuanJue )
								{
									Pattern pattern = Pattern.compile( "(?i)^\\s*Trigger\\s+J\\-" );
									Matcher matcher = pattern.matcher( line );
									if ( matcher.find( ) )
									{
										startGuanJue = true;
										printer.println( line );

										while ( ( line = in.readLine( ) ) != null )
										{
											pattern = Pattern.compile( "(?i)^\\s*Affects" );
											matcher = pattern.matcher( line );
											if ( matcher.find( ) )
											{
												startGuanJue = false;
												break;
											}

											pattern = Pattern.compile( "(?i)^\\s*and\\s+FactionType" );
											matcher = pattern.matcher( line );
											if ( matcher.find( ) )
											{
												continue;
											}
											
											pattern = Pattern.compile( "(?i)^\\s*and\\s+FactionLeaderTrait" );
											matcher = pattern.matcher( line );
											if ( matcher.find( ) )
											{
												continue;
											}
											
											pattern = Pattern.compile( "(?i)^\\s*and\\s+I_SettlementOwner" );
											matcher = pattern.matcher( line );
											if ( matcher.find( ) )
											{
												continue;
											}
											
											pattern = Pattern.compile( "(?i)^\\s*and\\s+I_NumberOfSettlements" );
											matcher = pattern.matcher( line );
											if ( matcher.find( ) )
											{
												continue;
											}

											printer.println( line );

										}
									}
								}
								printer.println( line );
							}
							in.close( );

							PrintWriter out = new PrintWriter( new BufferedWriter( new OutputStreamWriter( new FileOutputStream( FileConstants.characterTraitFile ),
									"GBK" ) ),
									false );
							out.print( writer.getBuffer( ) );
							out.close( );
						}
						catch ( IOException e1 )
						{
							e1.printStackTrace( );
						}
						guanjueApply.setEnabled( true );
					}
				}
			} );
			guanjueRestore.addSelectionListener( new SelectionAdapter( ) {

				public void widgetSelected( SelectionEvent e )
				{
					guanjueRestore.setEnabled( false );
					BakUtil.restoreCurrectVersionBakFile( );
					guanjueRestore.setEnabled( true );
				}
			} );
		}

		patchSection.setClient( patchClient );
	}

	private void createTitle( )
	{
		WidgetUtil.createFormText( container.getBody( ),
				"本页面用于修改脚本来改变武将状态和游戏事件，配置完毕后重启游戏即可生效。" );
	}

	public String getDisplayName( )
	{
		return "脚本修改";
	}


}
