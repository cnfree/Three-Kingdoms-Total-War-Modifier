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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.sf.feeling.sanguo.patch.Patch;
import org.sf.feeling.sanguo.patch.model.Unit;
import org.sf.feeling.sanguo.patch.util.BakUtil;
import org.sf.feeling.sanguo.patch.util.ChangeCode;
import org.sf.feeling.sanguo.patch.util.FileConstants;
import org.sf.feeling.sanguo.patch.util.FileUtil;
import org.sf.feeling.sanguo.patch.util.UnitParser;
import org.sf.feeling.sanguo.patch.util.UnitUtil;
import org.sf.feeling.sanguo.patch.widget.WidgetUtil;
import org.sf.feeling.swt.win32.extension.shell.ShellFolder;
import org.sf.feeling.swt.win32.extension.shell.ShellLink;
import org.sf.feeling.swt.win32.extension.util.SortMap;

public class BasicPatchPage extends SimpleTabPage
{

	private static final String[] wallTypes = new String[]{
			"medium_reinforced", "huge_reinforced", "medium_iron", "huge_iron"
	};
	private static final String[] disasterTypes = new String[]{
			"earthquake", "plague", "flood", "storm"
	};
	private static final String[] accuracyTypes = new String[]{
			"accuracy_vs_units", "accuracy_vs_buildings"
	};
	private List comboList = new ArrayList( );
	private String[] projectTileTypes;
	private String[] typeNames;
	
	private static final String re1 = "(stat_cost)"; // Variable Name 1
	private static final String re2 = "(\\s+)"; // White Space 1
	private static final String re3 = "(\\d+)"; // Integer Number 1
	private static final String re4 = "(\\s*)"; // White Space 2
	private static final String re5 = "(,)"; // Any Single Character 1
	private static final String re6 = "(construction)"; // Variable Name 1
	private static final String PATTERN_COST = "^\\s*(stat_cost)(\\s+)";
	private static final String PATTERN_COST_BUILDING = "^\\s*(cost)(\\s+)";
	private static final String PATTERN_CONSTRUCTION = "^\\s*(construction)(\\s+)";
	private SortMap soldierUnitMap;

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
				"<form><p>注意：<br/>1、灾害次数值越小越好。<br/>2、投石误差值越小越精准，但过于精准将无法射中移动目标。<br/>3、三国全面战争1.7a版巨型城墙生命值为250，仅供参考。</p></form>",
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
			final Button zaoBingBtn = WidgetUtil.getToolkit( )
					.createButton( patchClient, "造兵回合修改", SWT.CHECK );

			final CCombo soldierCombo = WidgetUtil.getToolkit( )
					.createCCombo( patchClient, SWT.READ_ONLY );

			soldierUnitMap = UnitUtil.getAllSoldierUnits( );
			if ( soldierUnitMap != null )
			{
				for ( int i = 0; i < soldierUnitMap.getKeyList( ).size( ); i++ )
				{
					soldierCombo.add( ChangeCode.toLong( (String) soldierUnitMap.get( i ) ) );
				}
			}
			soldierCombo.add( "全部兵种", 0 );
			soldierCombo.select( 0 );
			GridData gd = new GridData( GridData.FILL_HORIZONTAL );
			gd.widthHint = 100;
			soldierCombo.setLayoutData( gd );
			soldierCombo.setEnabled( false );

			final CCombo zaoBingCombo = WidgetUtil.getToolkit( )
					.createCCombo( patchClient, SWT.READ_ONLY );
			zaoBingCombo.setEnabled( false );

			gd = new GridData( GridData.FILL_HORIZONTAL );
			gd.widthHint = 100;
			zaoBingCombo.setLayoutData( gd );

			String[] items = new String[100];
			for ( int i = 0; i < 100; i++ )
				items[i] = i + "回合";

			zaoBingCombo.setItems( items );
			zaoBingCombo.select( 0 );

			final Button zaoBingApply = WidgetUtil.getToolkit( )
					.createButton( patchClient, "应用", SWT.PUSH );
			zaoBingApply.setEnabled( false );
			final Button zaoBingRestore = WidgetUtil.getToolkit( )
					.createButton( patchClient, "还原", SWT.PUSH );
			zaoBingApply.addSelectionListener( new SelectionAdapter( ) {

				public void widgetSelected( SelectionEvent e )
				{
					if ( soldierCombo.getSelectionIndex( ) == -1
							|| zaoBingCombo.getSelectionIndex( ) == -1 )
						return;
					zaoBingApply.setEnabled( false );
					if ( FileConstants.unitFile.exists( ) )
					{
						if ( soldierCombo.getSelectionIndex( ) == 0 )
						{
							BakUtil.bakData( "造兵回合修改：全部兵种"
									+ zaoBingCombo.getText( ) );
							FileUtil.replaceFile( FileConstants.unitFile,
									re1 + re2 + re3 + re4 + re5,
									re3,
									"" + zaoBingCombo.getSelectionIndex( ) );
						}
						else
						{
							BakUtil.bakData( "造兵回合修改："
									+ soldierCombo.getText( )
									+ zaoBingCombo.getText( ) );
							String soldierType = (String) soldierUnitMap.getKeyList( )
									.get( soldierCombo.getSelectionIndex( ) - 1 );
							int huihe = zaoBingCombo.getSelectionIndex( );
							Unit soldier = UnitParser.getUnit( soldierType );
							String[] cost = soldier.getCost( );
							cost[0] = "" + huihe;
							soldier.setCost( cost );
							UnitParser.saveSoldier( soldier );
						}
					}
					zaoBingApply.setEnabled( true );
				}
			} );
			zaoBingRestore.addSelectionListener( new RestoreListener( ) );

			zaoBingBtn.addSelectionListener( new SelectionAdapter( ) {

				public void widgetSelected( SelectionEvent e )
				{
					soldierCombo.setEnabled( zaoBingBtn.getSelection( ) );
					zaoBingCombo.setEnabled( zaoBingBtn.getSelection( ) );
					zaoBingApply.setEnabled( zaoBingBtn.getSelection( ) );
				}

			} );

		}
		{
			final Button unitBtn = WidgetUtil.getToolkit( )
					.createButton( patchClient, "所有兵种", SWT.CHECK );

			final CCombo typeCombo = WidgetUtil.getToolkit( )
					.createCCombo( patchClient, SWT.READ_ONLY );
			typeCombo.add( "生产回合" );
			typeCombo.add( "兵种造价" );
			typeCombo.add( "维护费用" );
			typeCombo.add( "武器升级费用" );
			typeCombo.add( "防具升级费用" );
			typeCombo.add( "对战模式造价" );
			GridData gd = new GridData( GridData.FILL_HORIZONTAL );
			gd.widthHint = 100;
			typeCombo.setLayoutData( gd );
			typeCombo.setEnabled( false );

			final CCombo numberCombo = WidgetUtil.getToolkit( )
					.createCCombo( patchClient, SWT.READ_ONLY );
			numberCombo.add( "减少1/3" );
			numberCombo.add( "减少1/2" );
			numberCombo.add( "减少2/3" );
			gd = new GridData( GridData.FILL_HORIZONTAL );
			gd.widthHint = 100;
			numberCombo.setLayoutData( gd );
			numberCombo.setEnabled( false );

			final Button unitApply = WidgetUtil.getToolkit( )
					.createButton( patchClient, "应用", SWT.PUSH );
			unitApply.setEnabled( false );
			final Button unitRestore = WidgetUtil.getToolkit( )
					.createButton( patchClient, "还原", SWT.PUSH );
			unitApply.addSelectionListener( new SelectionAdapter( ) {

				public void widgetSelected( SelectionEvent e )
				{
					if ( numberCombo.getSelectionIndex( ) == -1
							|| typeCombo.getSelectionIndex( ) == -1 )
						return;
					unitApply.setEnabled( false );
					if ( FileConstants.unitFile.exists( ) )
					{
						BakUtil.bakData( "所有兵种"
								+ typeCombo.getText( )
								+ numberCombo.getText( ) );
						try
						{
							String line = null;
							StringWriter writer = new StringWriter( );
							PrintWriter printer = new PrintWriter( writer );
							BufferedReader in = new BufferedReader( new InputStreamReader( new FileInputStream( FileConstants.unitFile ),
									"GBK" ) );
							while ( ( line = in.readLine( ) ) != null )
							{

								Pattern pattern = Pattern.compile( PATTERN_COST );
								Matcher matcher = pattern.matcher( line );
								if ( matcher.find( ) )
								{
									String[] costs = getSoldierCost( line );
									int number = Integer.parseInt( costs[typeCombo.getSelectionIndex( )].trim( )
											.split( "\\." )[0] );
									if ( numberCombo.getSelectionIndex( ) == 0 )
									{
										number = compute( number, 2, 3 );
									}
									else if ( numberCombo.getSelectionIndex( ) == 1 )
									{
										number = compute( number, 1, 2 );
									}
									else if ( numberCombo.getSelectionIndex( ) == 2 )
									{
										number = compute( number, 1, 3 );
									}
									costs[typeCombo.getSelectionIndex( )] = number
											+ "";
									setSoldierCost( printer, costs, line );
									continue;
								}

								printer.println( line );
							}
							in.close( );

							PrintWriter out = new PrintWriter( new BufferedWriter( new OutputStreamWriter( new FileOutputStream( FileConstants.unitFile ),
									"GBK" ) ),
									false );
							out.print( writer.getBuffer( ) );
							out.close( );
						}
						catch ( IOException e1 )
						{
							e1.printStackTrace( );
						}
					}
					unitApply.setEnabled( true );
				}
			} );
			unitRestore.addSelectionListener( new RestoreListener( ) );
			unitBtn.addSelectionListener( new SelectionAdapter( ) {

				public void widgetSelected( SelectionEvent e )
				{
					typeCombo.setEnabled( unitBtn.getSelection( ) );
					numberCombo.setEnabled( unitBtn.getSelection( ) );
					unitApply.setEnabled( unitBtn.getSelection( ) );
				}
			} );

		}
		{
			final Button zaoChengBtn = WidgetUtil.getToolkit( )
					.createButton( patchClient, "建筑修建回合修改", SWT.CHECK );

			final CCombo zaoChengCombo = WidgetUtil.getToolkit( )
					.createCCombo( patchClient, true );
			String[] items = new String[100];
			for ( int i = 0; i < 100; i++ )
				items[i] = ( i + 1 ) + "回合";
			zaoChengCombo.setItems( items );
			zaoChengCombo.select( 0 );
			zaoChengCombo.setEnabled( false );
			GridData gd = new GridData( GridData.FILL_HORIZONTAL );
			gd.horizontalSpan = 2;
			zaoChengCombo.setLayoutData( gd );

			final Button zaoChengApply = WidgetUtil.getToolkit( )
					.createButton( patchClient, "应用", SWT.FLAT );
			zaoChengApply.setEnabled( false );
			zaoChengApply.addSelectionListener( new SelectionAdapter( ) {

				public void widgetSelected( SelectionEvent e )
				{
					if ( FileConstants.buildingsFile.exists( ) )
					{
						zaoChengApply.setEnabled( false );
						BakUtil.bakData( "建筑修建回合修改：" + zaoChengCombo.getText( ) );
						FileUtil.replaceFile( FileConstants.buildingsFile,
								re6 + re2 + re3,
								re3,
								"" + ( zaoChengCombo.getSelectionIndex( ) + 1 ) );
						zaoChengApply.setEnabled( true );
					}
				}

			} );
			final Button zaoChengRestore = WidgetUtil.getToolkit( )
					.createButton( patchClient, "还原", SWT.FLAT );

			zaoChengRestore.addSelectionListener( new RestoreListener( ) );

			zaoChengBtn.addSelectionListener( new SelectionAdapter( ) {

				public void widgetSelected( SelectionEvent e )
				{
					zaoChengCombo.setEnabled( zaoChengBtn.getSelection( ) );
					zaoChengApply.setEnabled( zaoChengBtn.getSelection( ) );
				}

			} );
		}
		{
			final Button buildingBtn = WidgetUtil.getToolkit( )
					.createButton( patchClient, "所有建筑", SWT.CHECK );

			final CCombo typeCombo = WidgetUtil.getToolkit( )
					.createCCombo( patchClient, SWT.READ_ONLY );
			typeCombo.add( "建造回合" );
			typeCombo.add( "建造费用" );
			GridData gd = new GridData( GridData.FILL_HORIZONTAL );
			gd.widthHint = 100;
			typeCombo.setLayoutData( gd );
			typeCombo.setEnabled( false );

			final CCombo numberCombo = WidgetUtil.getToolkit( )
					.createCCombo( patchClient, SWT.READ_ONLY );
			numberCombo.add( "减少1/3" );
			numberCombo.add( "减少1/2" );
			numberCombo.add( "减少2/3" );
			gd = new GridData( GridData.FILL_HORIZONTAL );
			gd.widthHint = 100;
			numberCombo.setLayoutData( gd );
			numberCombo.setEnabled( false );

			final Button buildingApply = WidgetUtil.getToolkit( )
					.createButton( patchClient, "应用", SWT.PUSH );
			buildingApply.setEnabled( false );
			final Button buildingRestore = WidgetUtil.getToolkit( )
					.createButton( patchClient, "还原", SWT.PUSH );
			buildingApply.addSelectionListener( new SelectionAdapter( ) {

				public void widgetSelected( SelectionEvent e )
				{
					if ( numberCombo.getSelectionIndex( ) == -1
							|| typeCombo.getSelectionIndex( ) == -1 )
						return;
					buildingApply.setEnabled( false );
					if ( FileConstants.buildingsFile.exists( ) )
					{
						BakUtil.bakData( "所有建筑"
								+ typeCombo.getText( )
								+ numberCombo.getText( ) );
						try
						{
							String line = null;
							StringWriter writer = new StringWriter( );
							PrintWriter printer = new PrintWriter( writer );
							BufferedReader in = new BufferedReader( new InputStreamReader( new FileInputStream( FileConstants.buildingsFile ),
									"GBK" ) );
							while ( ( line = in.readLine( ) ) != null )
							{

								Pattern pattern = null;
								if ( typeCombo.getSelectionIndex( ) == 0 )
								{
									pattern = Pattern.compile( PATTERN_CONSTRUCTION );
								}
								else
								{
									pattern = Pattern.compile( PATTERN_COST_BUILDING );
								}
								Matcher matcher = pattern.matcher( line );
								if ( matcher.find( ) )
								{
									String string = null;
									if ( typeCombo.getSelectionIndex( ) == 0 )
									{
										string = line.trim( ).split( ";" )[0].substring( "construction".length( ) );
									}
									else
									{
										string = line.trim( ).split( ";" )[0].substring( "cost".length( ) );
									}

									int number = Integer.parseInt( string.trim( )
											.split( "\\." )[0] );
									if ( numberCombo.getSelectionIndex( ) == 0 )
									{
										number = compute( number, 2, 3 );
									}
									else if ( numberCombo.getSelectionIndex( ) == 1 )
									{
										number = compute( number, 1, 2 );
									}
									else if ( numberCombo.getSelectionIndex( ) == 2 )
									{
										number = compute( number, 1, 3 );
									}
									if ( typeCombo.getSelectionIndex( ) == 0 )
									{
										printer.println( "            construction  "
												+ number );
									}
									else
									{
										printer.println( "            cost  "
												+ number );
									}
									continue;
								}
								printer.println( line );
							}
							in.close( );

							PrintWriter out = new PrintWriter( new BufferedWriter( new OutputStreamWriter( new FileOutputStream( FileConstants.buildingsFile ),
									"GBK" ) ),
									false );
							out.print( writer.getBuffer( ) );
							out.close( );
						}
						catch ( IOException e1 )
						{
							e1.printStackTrace( );
						}
					}
					buildingApply.setEnabled( true );
				}
			} );
			buildingRestore.addSelectionListener( new RestoreListener( ) );
			buildingBtn.addSelectionListener( new SelectionAdapter( ) {

				public void widgetSelected( SelectionEvent e )
				{
					typeCombo.setEnabled( buildingBtn.getSelection( ) );
					numberCombo.setEnabled( buildingBtn.getSelection( ) );
					buildingApply.setEnabled( buildingBtn.getSelection( ) );
				}
			} );

		}
		{
			final String factions[] = new String[]{
					"	romans_senate	;朝廷（1.7张绣）", "	spain		;在野", "	slave		;乱军"
			};
			final Button unlockFactionBtn = WidgetUtil.getToolkit( )
					.createButton( patchClient, "解锁势力", SWT.CHECK );

			final CCombo factionCombo = WidgetUtil.getToolkit( )
					.createCCombo( patchClient, SWT.READ_ONLY );
			factionCombo.add( "朝廷" );
			factionCombo.add( "在野" );
			factionCombo.add( "乱军" );
			GridData gd = new GridData( GridData.FILL_HORIZONTAL );
			gd.horizontalSpan = 2;
			factionCombo.setLayoutData( gd );
			factionCombo.setEnabled( false );

			final Button unlockFactionApply = WidgetUtil.getToolkit( )
					.createButton( patchClient, "应用", SWT.PUSH );
			unlockFactionApply.setEnabled( false );
			final Button unlockFactionRestore = WidgetUtil.getToolkit( )
					.createButton( patchClient, "还原", SWT.PUSH );
			unlockFactionApply.addSelectionListener( new SelectionAdapter( ) {

				public void widgetSelected( SelectionEvent e )
				{
					if ( factionCombo.getSelectionIndex( ) == -1
							|| factionCombo.getSelectionIndex( ) == -1 )
						return;

					String playableFaction = factions[factionCombo.getSelectionIndex( )];

					unlockFactionApply.setEnabled( false );
					if ( FileConstants.buildingsFile.exists( ) )
					{
						BakUtil.bakData( "解锁势力：" + factionCombo.getText( ) );
						try
						{
							String line = null;
							StringWriter writer = new StringWriter( );
							PrintWriter printer = new PrintWriter( writer );
							BufferedReader in = new BufferedReader( new InputStreamReader( new FileInputStream( FileConstants.stratFile ),
									"GBK" ) );
							boolean startPlayable = false;
							boolean startUnlockable = false;
							boolean startNonplayable = false;
							while ( ( line = in.readLine( ) ) != null )
							{
								if ( !startPlayable )
								{
									Pattern pattern = Pattern.compile( "(?i)^\\s*playable" );
									Matcher matcher = pattern.matcher( line );
									if ( matcher.find( ) )
									{
										startPlayable = true;
										printer.println( line );

										while ( ( line = in.readLine( ) ) != null )
										{
											pattern = Pattern.compile( "(?i)^\\s*end" );
											matcher = pattern.matcher( line );
											if ( matcher.find( ) )
											{
												printer.println( "	romans_scipii	;曹操" );
												printer.println( "	macedon		;吕布" );
												printer.println( "	seleucid	;孔融" );
												printer.println( "	greek_cities	;陶谦" );
												printer.println( "	thrace		;袁术" );
												printer.println( "	carthage	;袁绍" );
												printer.println( "	scythia		;公孙瓒" );
												printer.println( "	numidia		;张燕" );
												printer.println( "	egypt		;马腾" );
												printer.println( "	germans		;李傕" );
												printer.println( "	parthia		;刘表" );
												printer.println( "	armenia		;韩玄" );
												printer.println( "	romans_brutii	;孙策" );
												printer.println( "	pontus		;严白虎" );
												printer.println( "	romans_julii	;刘备" );
												printer.println( "	gauls		;刘璋" );
												printer.println( "	britons		;张鲁" );
												printer.println( "	dacia		;孟获" );
												printer.println( playableFaction );
												break;
											}
										}
									}
								}

								if ( !startUnlockable )
								{
									Pattern pattern = Pattern.compile( "(?i)^\\s*unlockable" );
									Matcher matcher = pattern.matcher( line );
									if ( matcher.find( ) )
									{
										startUnlockable = true;
										printer.println( line );

										while ( ( line = in.readLine( ) ) != null )
										{
											pattern = Pattern.compile( "(?i)^\\s*end" );
											matcher = pattern.matcher( line );
											if ( matcher.find( ) )
											{
												break;
											}
										}
									}
								}

								if ( !startNonplayable )
								{
									Pattern pattern = Pattern.compile( "(?i)^\\s*nonplayable" );
									Matcher matcher = pattern.matcher( line );
									if ( matcher.find( ) )
									{
										startNonplayable = true;
										printer.println( line );

										while ( ( line = in.readLine( ) ) != null )
										{
											pattern = Pattern.compile( "(?i)^\\s*end" );
											matcher = pattern.matcher( line );
											if ( matcher.find( ) )
											{
												List list = new ArrayList( );
												list.addAll( Arrays.asList( factions ) );
												list.remove( playableFaction );
												printer.println( list.get( 0 ) );
												printer.println( list.get( 1 ) );
												break;
											}
										}
									}
								}

								printer.println( line );
							}
							in.close( );

							PrintWriter out = new PrintWriter( new BufferedWriter( new OutputStreamWriter( new FileOutputStream( FileConstants.stratFile ),
									"GBK" ) ),
									false );
							out.print( writer.getBuffer( ) );
							out.close( );
						}
						catch ( IOException e1 )
						{
							e1.printStackTrace( );
						}
					}
					unlockFactionApply.setEnabled( true );
				}
			} );
			unlockFactionRestore.addSelectionListener( new RestoreListener( ) );
			unlockFactionBtn.addSelectionListener( new SelectionAdapter( ) {

				public void widgetSelected( SelectionEvent e )
				{
					unlockFactionApply.setEnabled( unlockFactionBtn.getSelection( ) );
					factionCombo.setEnabled( unlockFactionBtn.getSelection( ) );
				}
			} );

		}
		{
			final Button disastersBtn = WidgetUtil.getToolkit( )
					.createButton( patchClient, "自然灾害（次/年）", SWT.CHECK );
			GridData gd = new GridData( );
			disastersBtn.setLayoutData( gd );

			final CCombo disasterTypeCombo = WidgetUtil.getToolkit( )
					.createCCombo( patchClient, SWT.READ_ONLY );
			gd = new GridData( GridData.FILL_HORIZONTAL );
			gd.widthHint = 100;
			disasterTypeCombo.setLayoutData( gd );
			disasterTypeCombo.setEnabled( false );
			disasterTypeCombo.setItems( new String[]{
					"地震", "瘟疫", "洪水", "台风"
			} );
			comboList.add( disasterTypeCombo );
			final CCombo disasterValueCombo = WidgetUtil.getToolkit( )
					.createCCombo( patchClient, SWT.READ_ONLY );
			gd = new GridData( GridData.FILL_HORIZONTAL );
			gd.widthHint = 100;
			disasterValueCombo.setLayoutData( gd );
			disasterValueCombo.setEnabled( false );
			initNumberCombo( disasterValueCombo, 0, 50 );

			disasterTypeCombo.addSelectionListener( new SelectionAdapter( ) {

				public void widgetSelected( SelectionEvent e )
				{
					refreshDisasterCombo( disasterTypeCombo,
							disasterTypes,
							disasterValueCombo );
				}
			} );

			final Button disastersApply = WidgetUtil.getToolkit( )
					.createButton( patchClient, "应用", SWT.PUSH );
			disastersApply.setEnabled( false );
			final Button disastersRestore = WidgetUtil.getToolkit( )
					.createButton( patchClient, "还原", SWT.PUSH );
			disastersBtn.addSelectionListener( new SelectionAdapter( ) {

				public void widgetSelected( SelectionEvent e )
				{
					disastersApply.setEnabled( disastersBtn.getSelection( ) );
					disasterTypeCombo.setEnabled( disastersBtn.getSelection( ) );
					disasterValueCombo.setEnabled( disastersBtn.getSelection( ) );
				}
			} );
			disastersApply.addSelectionListener( new SelectionAdapter( ) {

				public void widgetSelected( SelectionEvent e )
				{
					if ( FileConstants.disasterFile.exists( )
							&& disasterTypeCombo.getSelectionIndex( ) > -1
							&& disasterValueCombo.getSelectionIndex( ) > 0 )
					{
						disastersApply.setEnabled( false );
						BakUtil.bakData( "自然灾害修改："
								+ disasterTypeCombo.getText( )
								+ "每年"
								+ disasterValueCombo.getText( )
								+ "次" );
						try
						{
							String disasterType = disasterTypes[disasterTypeCombo.getSelectionIndex( )];
							String line = null;
							StringWriter writer = new StringWriter( );
							PrintWriter printer = new PrintWriter( writer );
							BufferedReader in = new BufferedReader( new InputStreamReader( new FileInputStream( FileConstants.disasterFile ),
									"GBK" ) );
							boolean startDisaster = false;
							while ( ( line = in.readLine( ) ) != null )
							{
								if ( !startDisaster )
								{
									Pattern pattern = Pattern.compile( "^\\s*(event)(\\s+)("
											+ disasterType
											+ ")(\\s*)(;*)" );
									Matcher matcher = pattern.matcher( line );
									if ( matcher.find( ) )
									{
										startDisaster = true;
									}
								}
								else
								{
									Pattern pattern = Pattern.compile( "^\\s*(frequency)(\\s+)" );
									Matcher matcher = pattern.matcher( line );
									if ( matcher.find( ) )
									{
										printer.println( matcher.group( )
												+ disasterValueCombo.getText( ) );
										continue;
									}
									pattern = Pattern.compile( "^\\s*(event)(\\s*)" );
									matcher = pattern.matcher( line );
									if ( matcher.find( ) )
									{
										startDisaster = false;
									}
								}
								printer.println( line );
							}
							in.close( );

							PrintWriter out = new PrintWriter( new BufferedWriter( new OutputStreamWriter( new FileOutputStream( FileConstants.disasterFile ),
									"GBK" ) ),
									false );
							out.print( writer.getBuffer( ) );
							out.close( );
						}
						catch ( IOException e1 )
						{
							e1.printStackTrace( );
						}
						disastersApply.setEnabled( true );
					}
				}
			} );
			disastersRestore.addSelectionListener( new SelectionAdapter( ) {

				public void widgetSelected( SelectionEvent e )
				{
					disastersRestore.setEnabled( false );
					BakUtil.restoreCurrectVersionBakFile( );
					refreshPage( );
					disastersRestore.setEnabled( true );
				}
			} );
		}
		{
			final String[] accuracyTypeNames = new String[]{
					"部队", "设施"
			};

			for ( int j = 0; j < accuracyTypes.length; j++ )
			{
				final String accuracyType = accuracyTypes[j];
				final String accuracyTypeName = accuracyTypeNames[j];
				final Button accuracyBtn = WidgetUtil.getToolkit( )
						.createButton( patchClient,
								"投石命中" + accuracyTypeNames[j] + "误差",
								SWT.CHECK );
				GridData gd = new GridData( );
				accuracyBtn.setLayoutData( gd );

				final CCombo accuracyTypeCombo = WidgetUtil.getToolkit( )
						.createCCombo( patchClient, SWT.READ_ONLY );
				gd = new GridData( GridData.FILL_HORIZONTAL );
				gd.widthHint = 100;
				accuracyTypeCombo.setLayoutData( gd );
				accuracyTypeCombo.setEnabled( false );
				if ( FileUtil.containMatchString( FileConstants.projectTileFile,
						"big_fiery_boulder" ) )
				{
					projectTileTypes = new String[]{
							"big_boulder", "big_fiery_boulder"
					};
					typeNames = new String[]{
							"投石", "投石（开火）"
					};
				}
				else
				{
					projectTileTypes = new String[]{
						"big_boulder"
					};
					typeNames = new String[]{
						"投石"
					};
				}
				accuracyTypeCombo.setItems( typeNames );
				comboList.add( accuracyTypeCombo );

				final Spinner accuracyValueSpinner = WidgetUtil.getToolkit( )
						.createSpinner( patchClient );
				gd = new GridData( GridData.FILL_HORIZONTAL );
				gd.widthHint = 100;
				accuracyValueSpinner.setLayoutData( gd );
				accuracyValueSpinner.setEnabled( false );
				initSpinner( accuracyValueSpinner, 0, 10000, 2, 5 );

				accuracyTypeCombo.addSelectionListener( new SelectionAdapter( ) {

					public void widgetSelected( SelectionEvent e )
					{
						refreshPrjectTileCombo( accuracyTypeCombo,
								projectTileTypes,
								accuracyValueSpinner,
								accuracyType );
					}
				} );

				final Button accuracyApply = WidgetUtil.getToolkit( )
						.createButton( patchClient, "应用", SWT.PUSH );
				accuracyApply.setEnabled( false );
				final Button accuracyRestore = WidgetUtil.getToolkit( )
						.createButton( patchClient, "还原", SWT.PUSH );
				accuracyBtn.addSelectionListener( new SelectionAdapter( ) {

					public void widgetSelected( SelectionEvent e )
					{
						accuracyApply.setEnabled( accuracyBtn.getSelection( ) );
						accuracyTypeCombo.setEnabled( accuracyBtn.getSelection( ) );
						accuracyValueSpinner.setEnabled( accuracyBtn.getSelection( ) );
					}
				} );
				accuracyApply.addSelectionListener( new SelectionAdapter( ) {

					public void widgetSelected( SelectionEvent e )
					{
						if ( FileConstants.projectTileFile.exists( )
								&& accuracyTypeCombo.getSelectionIndex( ) > -1
								&& accuracyValueSpinner.getText( )
										.trim( )
										.length( ) > 0 )
						{
							accuracyApply.setEnabled( false );
							BakUtil.bakData( accuracyTypeCombo.getText( )
									+ "命中"
									+ accuracyTypeName
									+ "误差："
									+ accuracyValueSpinner.getText( ).trim( ) );
							try
							{
								String projectTileType = projectTileTypes[accuracyTypeCombo.getSelectionIndex( )];
								String line = null;
								StringWriter writer = new StringWriter( );
								PrintWriter printer = new PrintWriter( writer );
								BufferedReader in = new BufferedReader( new InputStreamReader( new FileInputStream( FileConstants.projectTileFile ),
										"GBK" ) );
								boolean startProjectTile = false;
								while ( ( line = in.readLine( ) ) != null )
								{
									if ( !startProjectTile )
									{
										Pattern pattern = Pattern.compile( "^\\s*(projectile)(\\s+)("
												+ projectTileType
												+ ")(\\s*)(;*)" );
										Matcher matcher = pattern.matcher( line );
										if ( matcher.find( ) )
										{
											startProjectTile = true;
										}
									}
									else
									{
										Pattern pattern = Pattern.compile( "^\\s*("
												+ accuracyType
												+ ")(\\s+)" );
										Matcher matcher = pattern.matcher( line );
										if ( matcher.find( ) )
										{
											float accuracy = ( (float) accuracyValueSpinner.getSelection( ) ) / 10000;
											printer.println( matcher.group( )
													+ accuracy );
											continue;
										}
										pattern = Pattern.compile( "^\\s*(projectile)(\\s*)" );
										matcher = pattern.matcher( line );
										if ( matcher.find( ) )
										{
											startProjectTile = false;
										}
									}
									printer.println( line );
								}
								in.close( );

								PrintWriter out = new PrintWriter( new BufferedWriter( new OutputStreamWriter( new FileOutputStream( FileConstants.projectTileFile ),
										"GBK" ) ),
										false );
								out.print( writer.getBuffer( ) );
								out.close( );
							}
							catch ( IOException e1 )
							{
								e1.printStackTrace( );
							}
							accuracyApply.setEnabled( true );
						}
					}
				} );
				accuracyRestore.addSelectionListener( new SelectionAdapter( ) {

					public void widgetSelected( SelectionEvent e )
					{
						accuracyRestore.setEnabled( false );
						BakUtil.restoreCurrectVersionBakFile( );
						refreshPage( );
						accuracyRestore.setEnabled( true );
					}
				} );
			}
		}
		{
			final Button wallsBtn = WidgetUtil.getToolkit( )
					.createButton( patchClient, "城墙生命", SWT.CHECK );
			GridData gd = new GridData( );
			wallsBtn.setLayoutData( gd );

			final CCombo wallTypeCombo = WidgetUtil.getToolkit( )
					.createCCombo( patchClient, SWT.READ_ONLY );
			gd = new GridData( GridData.FILL_HORIZONTAL );
			gd.widthHint = 100;
			wallTypeCombo.setLayoutData( gd );
			wallTypeCombo.setEnabled( false );
			wallTypeCombo.setItems( new String[]{
					"城墙", "中型城墙", "大型城墙", "巨型城墙"
			} );
			comboList.add( wallTypeCombo );
			final CCombo wallValueCombo = WidgetUtil.getToolkit( )
					.createCCombo( patchClient, SWT.READ_ONLY );
			gd = new GridData( GridData.FILL_HORIZONTAL );
			gd.widthHint = 100;
			wallValueCombo.setLayoutData( gd );
			wallValueCombo.setEnabled( false );
			wallValueCombo.setItems( new String[]{
					"50",
					"100",
					"150",
					"200",
					"250",
					"300",
					"400",
					"500",
					"750",
					"1000",
					"1500",
					"2000"
			} );

			wallTypeCombo.addSelectionListener( new SelectionAdapter( ) {

				public void widgetSelected( SelectionEvent e )
				{
					refreshWallCombo( wallTypeCombo, wallTypes, wallValueCombo );
				}
			} );

			final Button wallsApply = WidgetUtil.getToolkit( )
					.createButton( patchClient, "应用", SWT.PUSH );
			wallsApply.setEnabled( false );
			final Button wallsRestore = WidgetUtil.getToolkit( )
					.createButton( patchClient, "还原", SWT.PUSH );
			wallsBtn.addSelectionListener( new SelectionAdapter( ) {

				public void widgetSelected( SelectionEvent e )
				{
					wallsApply.setEnabled( wallsBtn.getSelection( ) );
					wallTypeCombo.setEnabled( wallsBtn.getSelection( ) );
					wallValueCombo.setEnabled( wallsBtn.getSelection( ) );
				}
			} );
			wallsApply.addSelectionListener( new SelectionAdapter( ) {

				public void widgetSelected( SelectionEvent e )
				{
					if ( FileConstants.descrWallsFile.exists( )
							&& wallTypeCombo.getSelectionIndex( ) > -1
							&& wallValueCombo.getSelectionIndex( ) > -1 )
					{
						wallsApply.setEnabled( false );
						BakUtil.bakData( "城墙生命值修改："
								+ wallTypeCombo.getText( )
								+ "："
								+ wallValueCombo.getText( ) );
						try
						{
							String wallType = wallTypes[wallTypeCombo.getSelectionIndex( )];
							String line = null;
							StringWriter writer = new StringWriter( );
							PrintWriter printer = new PrintWriter( writer );
							BufferedReader in = new BufferedReader( new InputStreamReader( new FileInputStream( FileConstants.descrWallsFile ),
									"GBK" ) );
							boolean startWall = false;
							while ( ( line = in.readLine( ) ) != null )
							{
								if ( !startWall )
								{
									Pattern pattern = Pattern.compile( "^\\s*(gate)(\\s+)("
											+ wallType
											+ ")(\\s*)(;*)",
											Pattern.CASE_INSENSITIVE );
									Matcher matcher = pattern.matcher( line );
									if ( matcher.find( ) )
									{
										startWall = true;
									}
								}
								else
								{
									Pattern pattern = Pattern.compile( "^\\s*(full_health)(\\s+)",
											Pattern.CASE_INSENSITIVE );
									Matcher matcher = pattern.matcher( line );
									if ( matcher.find( ) )
									{
										printer.println( matcher.group( )
												+ wallValueCombo.getText( ) );
										startWall = false;
										continue;
									}
								}
								printer.println( line );
							}
							in.close( );

							PrintWriter out = new PrintWriter( new BufferedWriter( new OutputStreamWriter( new FileOutputStream( FileConstants.descrWallsFile ),
									"GBK" ) ),
									false );
							out.print( writer.getBuffer( ) );
							out.close( );
						}
						catch ( IOException e1 )
						{
							e1.printStackTrace( );
						}
						wallsApply.setEnabled( true );
					}
				}
			} );
			wallsRestore.addSelectionListener( new SelectionAdapter( ) {

				public void widgetSelected( SelectionEvent e )
				{
					wallsRestore.setEnabled( false );
					BakUtil.restoreCurrectVersionBakFile( );
					refreshPage( );
					wallsRestore.setEnabled( true );
				}
			} );
		}
		{
			final Button windowBtn = WidgetUtil.getToolkit( )
					.createButton( patchClient,
							"创建游戏窗口化启动快捷方式到桌面（Alt+Tab键切换窗口）",
							SWT.CHECK );
			GridData gd = new GridData( );
			gd.horizontalSpan = 3;
			windowBtn.setLayoutData( gd );
			final Button windowApply = WidgetUtil.getToolkit( )
					.createButton( patchClient, "应用", SWT.PUSH );
			windowApply.setEnabled( false );
			windowApply.addSelectionListener( new SelectionAdapter( ) {

				public void widgetSelected( SelectionEvent e )
				{
					String filePath = ShellFolder.DESKTOP.getAbsolutePath( container.handle )
							+ File.separator
							+ "三国全面战争"
							+ ".lnk";

					ShellLink.createShortCut( Patch.GAME_APPLICATION.getAbsolutePath( ),
							filePath );
					ShellLink.setShortCutArguments( filePath,
							"-ne -nm -show_err" );
					ShellLink.setShortCutDescription( filePath,
							"Created the link file by cnfree2000" );
					ShellLink.setShortCutWorkingDirectory( filePath,
							Patch.GAME_ROOT.getAbsolutePath( ) );

					try
					{
						FileUtil.writeToBinarayFile( new File( Patch.GAME_ROOT.getAbsolutePath( )
								+ "\\三国全面战争.lnk" ),
								new FileInputStream( filePath ) );
					}
					catch ( FileNotFoundException e1 )
					{
						e1.printStackTrace( );
					}
				}
			} );
			windowBtn.addSelectionListener( new SelectionAdapter( ) {

				public void widgetSelected( SelectionEvent e )
				{
					windowApply.setEnabled( windowBtn.getSelection( ) );
				}

			} );
		}
		patchSection.setClient( patchClient );
	}

	protected int compute( int number, int i, int j )
	{
		float result = ( (float) number * i ) / j;
		if ( number % j == 0 )
			return (int) result;
		else
			return (int) result + 1;
	}

	private void createTitle( )
	{
		WidgetUtil.createFormText( container.getBody( ),
				"本页面用于设置游戏的基本数据，配置完毕后重启游戏即可生效。" );
	}

	public String getDisplayName( )
	{
		return "基本修改";
	}

	private String[] getSoldierCost( String line )
	{
		String soldierInfo = line.trim( ).split( ";" )[0].substring( "stat_cost".length( ) )
				.trim( );
		return soldierInfo.split( "," );
	}

	private static void setSoldierCost( PrintWriter printer, String[] costs,
			String line )
	{
		if ( costs != null && costs.length == 6 )
		{
			StringBuffer buffer = new StringBuffer( );
			buffer.append( "stat_cost        " );
			buffer.append( costs[0]
					+ ", "
					+ costs[1]
					+ ", "
					+ costs[2]
					+ ", "
					+ costs[3]
					+ ", "
					+ costs[4]
					+ ", "
					+ costs[5] );
			printer.println( buffer );
		}
		else
			printer.println( line );
	}
	
	public void refresh( )
	{
		super.refresh( );
		refreshPage( );
	}

	private void refreshPage( )
	{
		for ( int i = 0; i < comboList.size( ); i++ )
		{
			CCombo combo = (CCombo) comboList.get( i );
			if ( combo.getSelectionIndex( ) > -1 )
				combo.notifyListeners( SWT.Selection, new Event( ) );
		}
	}
	
	private void refreshDisasterCombo( final CCombo disasterTypeCombo,
			final String[] disasterTypes, final CCombo disasterValueCombo )
	{
		String disasterType = disasterTypes[disasterTypeCombo.getSelectionIndex( )];
		String freq = "";
		if ( FileConstants.disasterFile.exists( ) )
		{
			try
			{
				String line = null;
				BufferedReader in = new BufferedReader( new InputStreamReader( new FileInputStream( FileConstants.disasterFile ),
						"GBK" ) );
				boolean startDisaster = false;
				while ( ( line = in.readLine( ) ) != null )
				{
					if ( !startDisaster )
					{
						Pattern pattern = Pattern.compile( "^\\s*(event)(\\s+)("
								+ disasterType
								+ ")(\\s*)(;*)" );
						Matcher matcher = pattern.matcher( line );
						if ( matcher.find( ) )
						{
							startDisaster = true;
							continue;
						}
					}
					else
					{
						Pattern pattern = Pattern.compile( "^\\s*(frequency)(\\s+)" );
						Matcher matcher = pattern.matcher( line );
						if ( matcher.find( ) )
						{
							freq = line.substring( matcher.end( ) );
							in.close( );
							break;
						}
					}
				}
				in.close( );
			}
			catch ( IOException e1 )
			{
				e1.printStackTrace( );
			}
		}
		if ( freq != null )
			disasterValueCombo.setText( freq );
	}

	private void refreshWallCombo( final CCombo wallTypeCombo,
			final String[] wallTypes, final CCombo wallValueCombo )
	{
		String wallType = wallTypes[wallTypeCombo.getSelectionIndex( )];
		String freq = "";
		if ( FileConstants.descrWallsFile.exists( ) )
		{
			try
			{
				String line = null;
				BufferedReader in = new BufferedReader( new InputStreamReader( new FileInputStream( FileConstants.descrWallsFile ),
						"GBK" ) );
				boolean startWall = false;
				while ( ( line = in.readLine( ) ) != null )
				{
					if ( !startWall )
					{
						Pattern pattern = Pattern.compile( "^\\s*(gate)(\\s+)("
								+ wallType
								+ ")(\\s*)(;*)" );
						Matcher matcher = pattern.matcher( line );
						if ( matcher.find( ) )
						{
							startWall = true;
							continue;
						}
					}
					else
					{
						Pattern pattern = Pattern.compile( "^\\s*(full_health)(\\s+)" );
						Matcher matcher = pattern.matcher( line );
						if ( matcher.find( ) )
						{
							freq = line.substring( matcher.end( ) );
							in.close( );
							break;
						}
					}
				}
				in.close( );
			}
			catch ( IOException e1 )
			{
				e1.printStackTrace( );
			}
		}
		if ( freq != null )
			wallValueCombo.setText( freq );
	}

	private void refreshPrjectTileCombo( final CCombo projectTileTypeCombo,
			final String[] projectTileTypes,
			final Spinner projectTileValueSpinner, String accuracyType )
	{
		String projectTileType = projectTileTypes[projectTileTypeCombo.getSelectionIndex( )];
		String accuracy = "";
		if ( FileConstants.projectTileFile.exists( ) )
		{
			try
			{
				String line = null;
				BufferedReader in = new BufferedReader( new InputStreamReader( new FileInputStream( FileConstants.projectTileFile ),
						"GBK" ) );
				boolean startProjectTile = false;
				while ( ( line = in.readLine( ) ) != null )
				{
					if ( !startProjectTile )
					{
						Pattern pattern = Pattern.compile( "^\\s*(projectile)(\\s+)("
								+ projectTileType
								+ ")(\\s*)(;*)" );
						Matcher matcher = pattern.matcher( line );
						if ( matcher.find( ) )
						{
							startProjectTile = true;
							continue;
						}
					}
					else
					{
						Pattern pattern = Pattern.compile( "^\\s*("
								+ accuracyType
								+ ")(\\s+)" );
						Matcher matcher = pattern.matcher( line );
						if ( matcher.find( ) )
						{
							accuracy = line.substring( matcher.end( ) );
							in.close( );
							break;
						}
					}
				}
				in.close( );
			}
			catch ( IOException e1 )
			{
				e1.printStackTrace( );
			}
		}
		if ( accuracy != null )
			projectTileValueSpinner.setSelection( (int) ( Float.parseFloat( accuracy.trim( ) ) * 10000 ) );
	}
	
	private void initNumberCombo( CCombo combo, int min, int max )
	{
		for ( int i = min; i <= max; i++ )
		{
			combo.add( "" + i );
		}
		combo.add( "", 0 );
	}

	private void initSpinner( Spinner combo, int min, int max, int digit,
			int step )
	{
		combo.setMinimum( min );
		combo.setMaximum( max );
		combo.setDigits( digit );
		combo.setIncrement( step );
	}
}
