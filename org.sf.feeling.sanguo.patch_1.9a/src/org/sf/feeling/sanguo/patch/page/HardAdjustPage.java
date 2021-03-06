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
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.sf.feeling.sanguo.patch.Patch;
import org.sf.feeling.sanguo.patch.util.BakUtil;
import org.sf.feeling.sanguo.patch.util.ChangeCode;
import org.sf.feeling.sanguo.patch.util.FileConstants;
import org.sf.feeling.sanguo.patch.util.UnitUtil;
import org.sf.feeling.sanguo.patch.widget.WidgetUtil;
import org.sf.feeling.swt.win32.extension.util.SortMap;

public class HardAdjustPage extends SimpleTabPage
{

	private SortMap soldierUnitMap;
	private CCombo factionCombo;
	private SortMap factionMap;
	private CCombo userFactionCombo;

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
				"<form><p>说明：<br/>1、禁用驿站暴兵后仍觉得曹操兵太多的，可以尝试修改曹操兵种的<a>造兵回合</a>，或者降低电脑的经济加成。<br/>2、禁用电脑税收加成并设置电脑太守加成几率为0时，电脑将和玩家一样，需要面对高昂的军队维护费。</p></form>",
				true,
				true );
		TableWrapData data = new TableWrapData( TableWrapData.FILL );
		data.maxWidth = 600;
		noteText.setLayoutData( data );

		noteText.addHyperlinkListener( new HyperlinkAdapter( ) {

			public void linkActivated( HyperlinkEvent e )
			{
				Patch.getInstance( ).select( 0 );
			}

		} );
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
			final Button toushiBtn = WidgetUtil.getToolkit( )
					.createButton( patchClient, "禁用投石车", SWT.CHECK );

			factionCombo = WidgetUtil.getToolkit( ).createCCombo( patchClient,
					SWT.READ_ONLY );

			GridData gd = new GridData( );
			gd.horizontalSpan = 2;
			gd.widthHint = 150;
			factionCombo.setLayoutData( gd );
			factionCombo.setEnabled( false );

			final Button toushiApply = WidgetUtil.getToolkit( )
					.createButton( patchClient, "应用", SWT.PUSH );
			toushiApply.setEnabled( false );
			final Button toushiRestore = WidgetUtil.getToolkit( )
					.createButton( patchClient, "还原", SWT.PUSH );

			toushiApply.addSelectionListener( new SelectionAdapter( ) {

				private void appendToString( StringBuffer buffer, String temp,
						String replacement )
				{
					Pattern pattern = Pattern.compile( "engine(\\s+)(heavy_onager)",
							Pattern.CASE_INSENSITIVE );
					Matcher matcher = pattern.matcher( temp );
					if ( matcher.find( ) )
					{
						int start = matcher.end( );
						buffer.append( temp.substring( 0, start ) );
						String lastString = temp.substring( start );
						Pattern pattern1 = Pattern.compile( "(ownership)(.+)(\\s)",
								Pattern.CASE_INSENSITIVE );
						Matcher matcher1 = pattern1.matcher( lastString );
						if ( matcher1.find( ) )
						{
							int begin = matcher1.start( );
							int end = matcher1.end( );
							buffer.append( lastString.substring( 0, begin ) );
							if ( matcher1.group( ).trim( ).indexOf( "," ) > -1 )
							{
								String[] splits = matcher1.group( )
										.trim( )
										.split( "," );
								if ( replacement.equals( splits[splits.length - 1].trim( ) ) )
								{
									String str = matcher1.group( )
											.substring( 0,
													matcher1.group( )
															.lastIndexOf( replacement ) )
											+ matcher1.group( )
													.substring( matcher1.group( )
															.lastIndexOf( replacement )
															+ replacement.length( ) );
									if ( str.trim( ).lastIndexOf( "," ) == str.trim( )
											.length( ) - 1 )
										str = str.substring( 0,
												str.lastIndexOf( "," ) )
												+ str.substring( str.lastIndexOf( "," ) + 1 );
									buffer.append( str );
								}
								else
								{
									String str = matcher1.group( )
											.replaceAll( replacement
													+ "(\\s*)(,)(\\s{0,1})",
													"" );

									buffer.append( str );
								}
							}
							else
								buffer.append( matcher1.group( ) );
							appendToString( buffer,
									lastString.substring( end ),
									replacement );
						}
						else
						{
							appendToString( buffer, lastString, replacement );
						}
					}
					else
						buffer.append( temp );
				}

				public void widgetSelected( SelectionEvent e )
				{
					if ( factionCombo.getSelectionIndex( ) != -1 )
					{
						toushiApply.setEnabled( false );
						BakUtil.bakData( "禁用投石车:" + factionCombo.getText( ) );
						// FileUtil.bakFile(FileConstants.unitFile);
						String code = (String) factionMap.getKeyList( )
								.get( factionCombo.getSelectionIndex( ) );
						try
						{
							int sizeL = (int) FileConstants.unitFile.length( );
							int chars_read = 0;
							BufferedReader in = new BufferedReader( new InputStreamReader( new FileInputStream( FileConstants.unitFile ),
									"GBK" ) );
							char[] data = new char[sizeL];
							while ( in.ready( ) )
							{
								chars_read += in.read( data, chars_read, sizeL
										- chars_read );
							}
							in.close( );
							char[] v = new char[chars_read];
							System.arraycopy( data, 0, v, 0, chars_read );
							String temp = new String( v );
							StringBuffer sbr = new StringBuffer( );
							appendToString( sbr, temp, code );
							PrintWriter out = new PrintWriter( new BufferedWriter( new OutputStreamWriter( new FileOutputStream( FileConstants.unitFile ),
									"GBK" ) ),
									false );
							out.print( sbr );
							out.close( );
						}
						catch ( IOException e1 )
						{
							e1.printStackTrace( );
						}
						toushiApply.setEnabled( true );
					}
				}
			} );

			toushiRestore.addSelectionListener( new RestoreListener( ) );

			toushiBtn.addSelectionListener( new SelectionAdapter( ) {

				public void widgetSelected( SelectionEvent e )
				{
					toushiApply.setEnabled( toushiBtn.getSelection( ) );
					factionCombo.setEnabled( toushiBtn.getSelection( ) );
				}

			} );
		}

		{
			final Button baoBingBtn = WidgetUtil.getToolkit( )
					.createButton( patchClient, "禁用电脑驿站暴兵", SWT.CHECK );

			final CCombo soldierCombo = WidgetUtil.getToolkit( )
					.createCCombo( patchClient, SWT.READ_ONLY );

			soldierUnitMap = UnitUtil.getSoldierUnits( );
			if ( soldierUnitMap != null )
			{
				for ( int i = 0; i < soldierUnitMap.getKeyList( ).size( ); i++ )
				{
					soldierCombo.add( ChangeCode.toLong( (String) soldierUnitMap.get( i ) ) );
				}
			}

			soldierCombo.add( "全部兵种", 0 );
			GridData gd = new GridData( );
			gd.horizontalSpan = 2;
			gd.widthHint = 150;
			soldierCombo.setLayoutData( gd );
			soldierCombo.setEnabled( false );

			final Button baoBingApply = WidgetUtil.getToolkit( )
					.createButton( patchClient, "应用", SWT.PUSH );
			baoBingApply.setEnabled( false );
			final Button baoBingRestore = WidgetUtil.getToolkit( )
					.createButton( patchClient, "还原", SWT.PUSH );

			baoBingBtn.addSelectionListener( new SelectionAdapter( ) {

				public void widgetSelected( SelectionEvent e )
				{
					soldierCombo.setEnabled( baoBingBtn.getSelection( ) );
					baoBingApply.setEnabled( baoBingBtn.getSelection( ) );
				}

			} );

			baoBingRestore.addSelectionListener( new RestoreListener( ) );
			baoBingApply.addSelectionListener( new SelectionAdapter( ) {

				public void widgetSelected( SelectionEvent e )
				{
					if ( soldierCombo.getSelectionIndex( ) == -1 )
						return;
					baoBingApply.setEnabled( false );
					BakUtil.bakData( "禁用电脑驿站暴兵:" + soldierCombo.getText( ) );

					String line = null;

					StringWriter writer = new StringWriter( );
					PrintWriter printer = new PrintWriter( writer );

					String regex = "^\\s*(recruit)(\\s+)";
					if ( soldierCombo.getSelectionIndex( ) > 0 )
					{
						regex = "^\\s*(recruit)(\\s+)(\""
								+ soldierUnitMap.getKeyList( )
										.get( soldierCombo.getSelectionIndex( ) - 1 )
								+ "\")";
					}

					try
					{
						BufferedReader in = new BufferedReader( new InputStreamReader( new FileInputStream( FileConstants.buildingsFile ),
								"GBK" ) );
						boolean startTemple = false;
						while ( ( line = in.readLine( ) ) != null )
						{
							if ( !startTemple )
							{
								printer.println( line );
								Pattern pattern = Pattern.compile( "^\\s*(hrr_farms)",
										Pattern.CASE_INSENSITIVE );
								Matcher matcher = pattern.matcher( line );
								if ( matcher.find( ) )
								{
									startTemple = true;
								}
							}
							else
							{

								Pattern pattern = Pattern.compile( regex,
										Pattern.CASE_INSENSITIVE );
								Matcher matcher = pattern.matcher( line );
								if ( matcher.find( ) )
								{
									continue;
								}
								else
								{
									printer.println( line );
									Pattern pattern1 = Pattern.compile( "^\\s*(construction)",
											Pattern.CASE_INSENSITIVE );
									Matcher matcher1 = pattern1.matcher( line );
									if ( matcher1.find( ) )
									{
										startTemple = false;
									}
								}
							}
						}
						in.close( );
						PrintWriter out = new PrintWriter( new BufferedWriter( new OutputStreamWriter( new FileOutputStream( FileConstants.buildingsFile ),
								"GBK" ) ),
								false );
						out.print( writer.getBuffer( ) );
						out.close( );
						printer.close( );
					}
					catch ( IOException e1 )
					{
						e1.printStackTrace( );
					}
					baoBingApply.setEnabled( true );
				}
			} );
		}

		{
			final Button bounsBtn = WidgetUtil.getToolkit( )
					.createButton( patchClient, "禁用电脑驿站加成", SWT.CHECK );

			final CCombo bonusCombo = WidgetUtil.getToolkit( )
					.createCCombo( patchClient, SWT.READ_ONLY );

			bonusCombo.add( "全部加成", 0 );
			bonusCombo.add( "法律加成", 1 );
			bonusCombo.add( "快乐加成", 2 );
			bonusCombo.add( "税收加成", 3 );
			bonusCombo.add( "健康加成", 4 );
			bonusCombo.add( "部队装备加成", 5 );
			bonusCombo.add( "部队士气加成", 6 );
			bonusCombo.add( "部队经验加成", 7 );
			GridData gd = new GridData( );
			gd.horizontalSpan = 2;
			gd.widthHint = 150;
			bonusCombo.setLayoutData( gd );
			bonusCombo.setEnabled( false );

			final Button bonusApply = WidgetUtil.getToolkit( )
					.createButton( patchClient, "应用", SWT.PUSH );
			bonusApply.setEnabled( false );
			final Button bonusRestore = WidgetUtil.getToolkit( )
					.createButton( patchClient, "还原", SWT.PUSH );

			bounsBtn.addSelectionListener( new SelectionAdapter( ) {

				public void widgetSelected( SelectionEvent e )
				{
					bonusCombo.setEnabled( bounsBtn.getSelection( ) );
					bonusApply.setEnabled( bounsBtn.getSelection( ) );
				}

			} );

			bonusRestore.addSelectionListener( new RestoreListener( ) );
			bonusApply.addSelectionListener( new SelectionAdapter( ) {

				public void widgetSelected( SelectionEvent e )
				{
					if ( bonusCombo.getSelectionIndex( ) == -1 )
						return;
					bonusApply.setEnabled( false );
					BakUtil.bakData( "禁用电脑驿站加成:" + bonusCombo.getText( ) );

					String line = null;

					StringWriter writer = new StringWriter( );
					PrintWriter printer = new PrintWriter( writer );

					String regex = "^\\s*(recruit)(\\s+)";
					if ( bonusCombo.getSelectionIndex( ) == 1 )
					{
						regex = "^\\s*(law_bonus)(\\s+)(bonus)";
					}
					else if ( bonusCombo.getSelectionIndex( ) == 2 )
					{
						regex = "^\\s*(happiness_bonus)(\\s+)(bonus)";
					}
					else if ( bonusCombo.getSelectionIndex( ) == 3 )
					{
						regex = "^\\s*(taxable_income_bonus)(\\s+)(bonus)";
					}
					else if ( bonusCombo.getSelectionIndex( ) == 4 )
					{
						regex = "^\\s*(population_health_bonus)(\\s+)(bonus)";
					}
					else if ( bonusCombo.getSelectionIndex( ) == 5 )
					{
						regex = "^\\s*((weapon_simple)|(weapon_bladed)|(weapon_missile)|(armour))(\\s+)(bonus)";
					}
					else if ( bonusCombo.getSelectionIndex( ) == 6 )
					{
						regex = "^\\s*(recruits_morale_bonus)(\\s+)(bonus)";
					}
					else if ( bonusCombo.getSelectionIndex( ) == 7 )
					{
						regex = "^\\s*(recruits_exp_bonus)(\\s+)(bonus)";
					}
					else if ( bonusCombo.getSelectionIndex( ) == 0 )
					{
						regex = "(bonus)";
					}

					try
					{
						BufferedReader in = new BufferedReader( new InputStreamReader( new FileInputStream( FileConstants.buildingsFile ),
								"GBK" ) );
						boolean startTemple = false;
						while ( ( line = in.readLine( ) ) != null )
						{
							if ( !startTemple )
							{
								printer.println( line );
								Pattern pattern = Pattern.compile( "^\\s*(hrr_farms)",
										Pattern.CASE_INSENSITIVE );
								Matcher matcher = pattern.matcher( line );
								if ( matcher.find( ) )
								{
									startTemple = true;
								}
							}
							else
							{

								Pattern pattern = Pattern.compile( regex,
										Pattern.CASE_INSENSITIVE );
								Matcher matcher = pattern.matcher( line );
								if ( matcher.find( ) )
								{
									if ( line.matches( "(?i).+AA12\\-GuanAi.*" ) )
										printer.println( line );
									continue;
								}
								else
								{
									printer.println( line );
									Pattern pattern1 = Pattern.compile( "^\\s*(construction)",
											Pattern.CASE_INSENSITIVE );
									Matcher matcher1 = pattern1.matcher( line );
									if ( matcher1.find( ) )
									{
										startTemple = false;
									}
								}
							}
						}
						in.close( );
						PrintWriter out = new PrintWriter( new BufferedWriter( new OutputStreamWriter( new FileOutputStream( FileConstants.buildingsFile ),
								"GBK" ) ),
								false );
						out.print( writer.getBuffer( ) );
						out.close( );
						printer.close( );
					}
					catch ( IOException e1 )
					{
						e1.printStackTrace( );
					}
					bonusApply.setEnabled( true );
				}
			} );
		}

		{
			final Button cikeBtn = WidgetUtil.getToolkit( )
					.createButton( patchClient, "禁止电脑生产刺客", SWT.CHECK );

			userFactionCombo = WidgetUtil.getToolkit( )
					.createCCombo( patchClient, SWT.READ_ONLY );

			GridData gd = new GridData( );
			gd.horizontalSpan = 2;
			gd.widthHint = 150;
			userFactionCombo.setLayoutData( gd );
			userFactionCombo.setEnabled( false );

			final Button cikeApply = WidgetUtil.getToolkit( )
					.createButton( patchClient, "应用", SWT.PUSH );
			cikeApply.setEnabled( false );
			final Button cikeRestore = WidgetUtil.getToolkit( )
					.createButton( patchClient, "还原", SWT.PUSH );

			cikeBtn.addSelectionListener( new SelectionAdapter( ) {

				public void widgetSelected( SelectionEvent e )
				{
					userFactionCombo.setEnabled( cikeBtn.getSelection( ) );
					cikeApply.setEnabled( cikeBtn.getSelection( ) );
				}

			} );

			cikeRestore.addSelectionListener( new RestoreListener( ) );
			cikeApply.addSelectionListener( new SelectionAdapter( ) {

				public void widgetSelected( SelectionEvent e )
				{
					if ( userFactionCombo.getSelectionIndex( ) == -1 )
						return;
					cikeApply.setEnabled( false );
					BakUtil.bakData( "禁止电脑生产刺客" );

					String line = null;

					StringWriter writer = new StringWriter( );
					PrintWriter printer = new PrintWriter( writer );
					String regex = "^\\s*(agent)(\\s+)assassin";

					String[] buildings = new String[]{
							"governors_palace",
							"proconsuls_palace",
							"imperial_palace"
					};

					StringBuffer buildingStr = new StringBuffer( );
					buildingStr.append( "(" );
					for ( int i = 0; i < buildings.length; i++ )
					{
						buildingStr.append( "(" + buildings[i] + ")" );
						if ( i + 1 < buildings.length )
							buildingStr.append( "|" );
					}
					buildingStr.append( ")" );

					String buildingRegex = buildingStr.toString( )
							+ "(\\s+)(requires)(\\s+)(factions)";

					try
					{
						BufferedReader in = new BufferedReader( new InputStreamReader( new FileInputStream( FileConstants.buildingsFile ),
								"GBK" ) );
						boolean startTemple = false;
						boolean addCikeToUserFaction = false;
						while ( ( line = in.readLine( ) ) != null )
						{
							if ( !startTemple )
							{
								printer.println( line );
								Pattern pattern = Pattern.compile( buildingRegex,
										Pattern.CASE_INSENSITIVE );
								Matcher matcher = pattern.matcher( line );
								if ( matcher.find( ) )
								{
									startTemple = true;
									addCikeToUserFaction = false;
								}
							}
							else
							{
								Pattern pattern = Pattern.compile( regex,
										Pattern.CASE_INSENSITIVE );
								Matcher matcher = pattern.matcher( line );
								if ( matcher.find( ) )
								{
									if ( !addCikeToUserFaction )
									{
										addCikeToUserFaction = true;
										if ( userFactionCombo.getSelectionIndex( ) > 0 )
										{
											String faction = (String) factionMap.getKeyList( )
													.get( userFactionCombo.getSelectionIndex( ) - 1 );
											printer.println( "                agent assassin  0  requires factions { "
													+ faction
													+ ", } and hidden_resource swg" );
										}
									}
									continue;
								}
								else
								{
									printer.println( line );
									Pattern pattern1 = Pattern.compile( "^\\s*(construction)",
											Pattern.CASE_INSENSITIVE );
									Matcher matcher1 = pattern1.matcher( line );
									if ( matcher1.find( ) )
									{
										startTemple = false;
									}
								}
							}
						}
						in.close( );
						PrintWriter out = new PrintWriter( new BufferedWriter( new OutputStreamWriter( new FileOutputStream( FileConstants.buildingsFile ),
								"GBK" ) ),
								false );
						out.print( writer.getBuffer( ) );
						out.close( );
						printer.close( );
					}
					catch ( IOException e1 )
					{
						e1.printStackTrace( );
					}
					cikeApply.setEnabled( true );
				}
			} );
		}

		{
			final Button cikeBtn = WidgetUtil.getToolkit( )
					.createButton( patchClient, "电脑太守加成几率", SWT.CHECK );

			final CCombo chanceCombo = WidgetUtil.getToolkit( )
					.createCCombo( patchClient, SWT.READ_ONLY );

			GridData gd = new GridData( );
			gd.horizontalSpan = 2;
			gd.widthHint = 150;
			chanceCombo.setLayoutData( gd );
			chanceCombo.setEnabled( false );

			for ( int i = 0; i <= 100; i++ )
			{
				chanceCombo.add( "" + i );
			}

			final Button chanceApply = WidgetUtil.getToolkit( )
					.createButton( patchClient, "应用", SWT.PUSH );
			chanceApply.setEnabled( false );
			final Button chanceRestore = WidgetUtil.getToolkit( )
					.createButton( patchClient, "还原", SWT.PUSH );

			cikeBtn.addSelectionListener( new SelectionAdapter( ) {

				public void widgetSelected( SelectionEvent e )
				{
					chanceCombo.setEnabled( cikeBtn.getSelection( ) );
					chanceApply.setEnabled( cikeBtn.getSelection( ) );
				}

			} );

			chanceRestore.addSelectionListener( new RestoreListener( ) );
			chanceApply.addSelectionListener( new SelectionAdapter( ) {

				public void widgetSelected( SelectionEvent e )
				{
					if ( chanceCombo.getSelectionIndex( ) == -1 )
						return;
					chanceApply.setEnabled( false );
					BakUtil.bakData( "调整电脑太守加成几率：" + chanceCombo.getText( ) );

					String line = null;

					StringWriter writer = new StringWriter( );
					PrintWriter printer = new PrintWriter( writer );

					try
					{
						BufferedReader in = new BufferedReader( new InputStreamReader( new FileInputStream( FileConstants.characterTraitFile ),
								"GBK" ) );
						while ( ( line = in.readLine( ) ) != null )
						{
							{
								String regex = "^\\s*(Affects)(\\s+)(GiveMoneyAndSoldier1000)";
								Pattern pattern = Pattern.compile( regex,
										Pattern.CASE_INSENSITIVE );
								Matcher matcher = pattern.matcher( line );
								if ( matcher.find( ) )
								{
									printer.println( "    Affects GiveMoneyAndSoldier1000  10  Chance  "
											+ chanceCombo.getText( ) );
									continue;
								}
							}
							{
								String regex = "^\\s*(Affects)(\\s+)(GiveMoneyNotSoldier1000)";
								Pattern pattern = Pattern.compile( regex,
										Pattern.CASE_INSENSITIVE );
								Matcher matcher = pattern.matcher( line );
								if ( matcher.find( ) )
								{
									printer.println( "    Affects GiveMoneyNotSoldier1000  10  Chance  "
											+ chanceCombo.getText( ) );
									continue;
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
						printer.close( );
					}
					catch ( IOException e1 )
					{
						e1.printStackTrace( );
					}
					chanceApply.setEnabled( true );
				}
			} );
		}

		patchSection.setClient( patchClient );
	}

	private void createTitle( )
	{
		WidgetUtil.createFormText( container.getBody( ),
				"本页面可以通过修改某些脚本，比如限制兵种，驿站加成等来改变游戏的难易程度，配置完毕后重启游戏即可生效。" );
	}

	public String getDisplayName( )
	{
		return "难度调整";
	}

	public void refresh( )
	{
		super.refresh( );
		refreshPage( );
	}

	private void refreshPage( )
	{
		factionMap = UnitUtil.getFactionMap( );
		String faction = factionCombo.getText( );

		factionCombo.removeAll( );
		for ( int i = 0; i < factionMap.getKeyList( ).size( ); i++ )
		{
			factionCombo.add( (String) factionMap.get( i ) );
		}

		if ( factionMap.containsValue( faction ) )
			factionCombo.setText( faction );

		String userFaction = userFactionCombo.getText( );
		userFactionCombo.removeAll( );
		userFactionCombo.add( "--选择玩家势力--", 0 );
		for ( int i = 0; i < factionMap.getKeyList( ).size( ); i++ )
		{
			userFactionCombo.add( (String) factionMap.get( i ) );
		}
		if ( factionMap.containsValue( userFaction ) )
			userFactionCombo.setText( userFaction );
		else
			userFactionCombo.select( 0 );
	}
}
