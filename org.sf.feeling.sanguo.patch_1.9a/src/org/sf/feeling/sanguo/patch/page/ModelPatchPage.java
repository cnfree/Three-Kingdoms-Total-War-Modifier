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
import org.sf.feeling.sanguo.patch.Patch;
import org.sf.feeling.sanguo.patch.model.Unit;
import org.sf.feeling.sanguo.patch.util.BakUtil;
import org.sf.feeling.sanguo.patch.util.BattleUtil;
import org.sf.feeling.sanguo.patch.util.FileConstants;
import org.sf.feeling.sanguo.patch.util.FileUtil;
import org.sf.feeling.sanguo.patch.util.MapUtil;
import org.sf.feeling.sanguo.patch.util.UnitParser;
import org.sf.feeling.sanguo.patch.util.UnitUtil;
import org.sf.feeling.sanguo.patch.widget.WidgetUtil;
import org.sf.feeling.swt.win32.extension.util.SortMap;

public class ModelPatchPage extends SimpleTabPage
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
				"<form><p>注意：如已导入过其他兵模，可能会因为兵模数量超过上限而无法正常进入游戏，请释放兵模后再进行导入模型操作。</p></form>",
				true,
				false );
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
		layout.numColumns = 3;
		patchClient.setLayout( layout );

		{
			GridData gd = new GridData( );
			gd.widthHint = 200;

			final Button freeBtn = WidgetUtil.getToolkit( )
					.createButton( patchClient,
							"替换所有普通兵种将军模型为中年高级将军，释放模型位置用来导入其他模型",
							SWT.CHECK );

			final Button freeApply = WidgetUtil.getToolkit( )
					.createButton( patchClient, "应用", SWT.PUSH );
			freeApply.setEnabled( false );
			final Button freeRestore = WidgetUtil.getToolkit( )
					.createButton( patchClient, "还原", SWT.PUSH );

			freeApply.addSelectionListener( new SelectionAdapter( ) {

				public void widgetSelected( SelectionEvent e )
				{
					freeApply.setEnabled( false );
					BakUtil.bakData( "替换所有普通兵种将军模型为中年高级将军" );
					updateAvailableModels( );
					MapUtil.initMap( );
					freeApply.setEnabled( true );
				}
			} );

			freeRestore.addSelectionListener( new RestoreListener( ) );

			freeBtn.addSelectionListener( new SelectionAdapter( ) {

				public void widgetSelected( SelectionEvent e )
				{
					freeApply.setEnabled( freeBtn.getSelection( ) );
				}

			} );

		}
		{
			GridData gd = new GridData( );
			gd.widthHint = 200;

			final Button xianzhenBtn = WidgetUtil.getToolkit( )
					.createButton( patchClient, "导入弩兵陷阵营", SWT.CHECK );

			final Button xianzhenApply = WidgetUtil.getToolkit( )
					.createButton( patchClient, "应用", SWT.PUSH );
			xianzhenApply.setEnabled( false );
			final Button xianzhenRestore = WidgetUtil.getToolkit( )
					.createButton( patchClient, "还原", SWT.PUSH );

			xianzhenApply.addSelectionListener( new SelectionAdapter( ) {

				public void widgetSelected( SelectionEvent e )
				{
					xianzhenApply.setEnabled( false );
					String[] xianzhenFactions = UnitUtil.getFactionsByUnitType( "Xianzhenyin Lvbu" );
					BakUtil.bakData( "弩兵陷阵营导入" );
					if ( FileConstants.unitFile.exists( ) )
					{
						Unit soldier = UnitParser.getUnit( "Xianzhenyin Lvbu" );
						if ( soldier != null )
						{
							soldier.getSoldier( )[0] = "Xianzhenyin_Lvbu_Crossbow";
							soldier.getSoldier( )[1] = "30";
							soldier.setUnitClass( "missile" );
							List attributes = soldier.getAttributes( );
							if ( !attributes.contains( "frighten_foot" ) )
								attributes.add( "frighten_foot" );
							if ( !attributes.contains( "can_withdraw" ) )
								attributes.add( "can_withdraw" );
							if ( !attributes.contains( "can_swim" ) )
								attributes.add( "can_swim" );
							soldier.setPrimary( new String[]{
									"12",
									"4",
									"crossbow",
									"170",
									"30",
									"missile",
									"archery",
									"piercing",
									"none",
									"25",
									"1"
							} );
							soldier.setPrimaryAttr( Arrays.asList( new String[]{
									"ap", "launching"
							} ) );
							soldier.setSecond( new String[]{
									"16",
									"4",
									"no",
									"0",
									"0",
									"melee",
									"blade",
									"piercing",
									"spear",
									"25",
									"1"
							} );
							soldier.setSecondAttr( Arrays.asList( new String[]{
									"spear", "spear_bonus_8"
							} ) );
							soldier.setPrimaryArmour( new String[]{
									"9", "6", "4", "metal"
							} );
							soldier.setFormation( Arrays.asList( new String[]{
									"1",
									"2",
									"2",
									"3",
									"4",
									"square",
									"shield_wall"
							} ) );
							UnitParser.saveSoldier( soldier );
						}

						if ( FileConstants.battleFile.exists( )
								&& !FileUtil.containMatchString( FileConstants.battleFile,
										"(Xianzhenyin_Lvbu_Crossbow)" ) )
						{
							updateAvailableModels( );
							FileUtil.appendToFile( FileConstants.battleFile,
									ModelPatchPage.class.getResourceAsStream( "/org/sf/feeling/sanguo/patch/models/xianzhenying_battle.txt" ) );
							try
							{
								for ( int i = 0; i < xianzhenFactions.length; i++ )
								{
									UnitUtil.modifyBattleFile( xianzhenFactions[i],
											"Xianzhenyin_Lvbu_Crossbow" );
								}
							}
							catch ( IOException e1 )
							{
								e1.printStackTrace( );
							}
						}
						File modelRootFile = new File( Patch.GAME_ROOT
								+ "\\patch\\bi\\data\\models_unit\\sanguo" );
						File textureRootFile = new File( Patch.GAME_ROOT
								+ "\\patch\\bi\\data\\models_unit\\sanguo\\textures" );
						if ( modelRootFile.exists( ) && modelRootFile.isFile( ) )
						{
							modelRootFile.delete( );
						}
						if ( !modelRootFile.exists( ) )
						{
							modelRootFile.mkdirs( );
						}
						if ( !textureRootFile.exists( ) )
						{
							textureRootFile.mkdirs( );
						}
						if ( modelRootFile.exists( )
								&& modelRootFile.isDirectory( ) )
						{
							FileUtil.writeToBinarayFile( new File( modelRootFile.getAbsolutePath( )
									+ "\\Xianzhenyin_Lvbu.cas" ),
									ModelPatchPage.class.getResourceAsStream( "/org/sf/feeling/sanguo/patch/models/Xianzhenyin_Lvbu.cas" ) );
							FileUtil.writeToBinarayFile( new File( modelRootFile.getAbsolutePath( )
									+ "\\Xianzhenyin_Lvbu1.cas" ),
									ModelPatchPage.class.getResourceAsStream( "/org/sf/feeling/sanguo/patch/models/Xianzhenyin_Lvbu1.cas" ) );
							FileUtil.writeToBinarayFile( new File( modelRootFile.getAbsolutePath( )
									+ "\\Xianzhenyin_Lvbu2.cas" ),
									ModelPatchPage.class.getResourceAsStream( "/org/sf/feeling/sanguo/patch/models/Xianzhenyin_Lvbu2.cas" ) );
							FileUtil.writeToBinarayFile( new File( modelRootFile.getAbsolutePath( )
									+ "\\Xianzhenyin_Lvbu3.cas" ),
									ModelPatchPage.class.getResourceAsStream( "/org/sf/feeling/sanguo/patch/models/Xianzhenyin_Lvbu3.cas" ) );
						}
						if ( textureRootFile.exists( )
								&& textureRootFile.isDirectory( ) )
						{
							FileUtil.writeToBinarayFile( new File( textureRootFile.getAbsolutePath( )
									+ "\\Xianzhenyin_Lvbu.cas" ),
									ModelPatchPage.class.getResourceAsStream( "/org/sf/feeling/sanguo/patch/models/Xianzhenyin_Lvbu.tga.dds" ) );
						}
						MapUtil.initMap( );
						xianzhenApply.setEnabled( true );
					}
				}
			} );

			xianzhenRestore.addSelectionListener( new RestoreListener( ) );

			xianzhenBtn.addSelectionListener( new SelectionAdapter( ) {

				public void widgetSelected( SelectionEvent e )
				{
					xianzhenApply.setEnabled( xianzhenBtn.getSelection( ) );
				}

			} );

		}
		{
			GridData gd = new GridData( );
			gd.widthHint = 200;

			final Button gaoshunBtn = WidgetUtil.getToolkit( )
					.createButton( patchClient, "导入弩骑高顺卫队", SWT.CHECK );

			final Button gaoshunApply = WidgetUtil.getToolkit( )
					.createButton( patchClient, "应用", SWT.PUSH );
			gaoshunApply.setEnabled( false );
			final Button gaoshunRestore = WidgetUtil.getToolkit( )
					.createButton( patchClient, "还原", SWT.PUSH );

			gaoshunApply.addSelectionListener( new SelectionAdapter( ) {

				public void widgetSelected( SelectionEvent e )
				{
					gaoshunApply.setEnabled( false );
					String[] gaoShunFactions = UnitUtil.getFactionsByUnitType( "JiangJun_ZhongYuan Aa_0402-GaoShun" );
					BakUtil.bakData( "弩骑高顺卫队导入" );
					if ( FileConstants.unitFile.exists( ) )
					{
						Unit soldier = UnitParser.getUnit( "JiangJun_ZhongYuan Aa_0402-GaoShun" );
						if ( soldier != null )
						{
							soldier.getOfficers( ).remove( 0 );
							soldier.getOfficers( ).add( 0,
									"Xianzhenyin_Lvbu_Horse" );
							soldier.setSoldier( new String[]{
									"Xianzhenyin_Lvbu_Horse", "6", "0", "1"
							} );
							soldier.setCategory( "cavalry" );
							soldier.setUnitClass( "missile" );
							List attributes = soldier.getAttributes( );
							attributes.remove( "can_sap" );
							if ( !attributes.contains( "frighten_foot" ) )
								attributes.add( "frighten_foot" );
							if ( !attributes.contains( "can_withdraw" ) )
								attributes.add( "can_withdraw" );
							if ( !attributes.contains( "can_swim" ) )
								attributes.add( "can_swim" );
							if ( !attributes.contains( "power_charge" ) )
								attributes.add( "power_charge" );
							if ( !attributes.contains( "very_hardy" ) )
								attributes.add( "very_hardy" );
							soldier.setPrimary( new String[]{
									"12",
									"4",
									"crossbow",
									"190",
									"30",
									"missile",
									"archery",
									"piercing",
									"none",
									"25",
									"1"
							} );
							soldier.setPrimaryAttr( Arrays.asList( new String[]{
								"ap"
							} ) );
							soldier.setSecond( new String[]{
									"6",
									"14",
									"no",
									"0",
									"0",
									"melee",
									"blade",
									"piercing",
									"spear",
									"25",
									"1"
							} );
							soldier.setSecondAttr( Arrays.asList( new String[]{
								"ap"
							} ) );
							soldier.setPrimaryArmour( new String[]{
									"12", "10", "4", "metal"
							} );
							soldier.setFormation( Arrays.asList( new String[]{
									"1.5",
									"4",
									"3",
									"6",
									"4",
									"square",
									"wedge"
							} ) );
							soldier.setMount( "sanguo horse jjhm9" );
							soldier.setMountEffect( Arrays.asList( new String[]{
									"elephant -4", "camel -4", "horse +2"
							} ) );
							soldier.setChargeDist( 70 );
							UnitParser.saveSoldier( soldier );
						}
					}
					if ( FileConstants.battleFile.exists( )
							&& !FileUtil.containMatchString( FileConstants.battleFile,
									"(Xianzhenyin_Lvbu_Horse)" ) )
					{
						updateAvailableModels( );
						FileUtil.appendToFile( FileConstants.battleFile,
								ModelPatchPage.class.getResourceAsStream( "/org/sf/feeling/sanguo/patch/models/gaoshun_battle.txt" ) );
						try
						{
							for ( int i = 0; i < gaoShunFactions.length; i++ )
							{
								UnitUtil.modifyBattleFile( gaoShunFactions[i],
										"Xianzhenyin_Lvbu_Horse" );
								UnitUtil.modifyBattleFile( gaoShunFactions[i],
										(String) UnitUtil.getMountTypeToModelMap( )
												.get( "sanguo horse jjhm9" ) );
							}
						}
						catch ( IOException e1 )
						{
							e1.printStackTrace( );
						}
					}
					File modelRootFile = new File( Patch.GAME_ROOT
							+ "\\patch\\bi\\data\\models_unit\\sanguo" );
					File textureRootFile = new File( Patch.GAME_ROOT
							+ "\\patch\\bi\\data\\models_unit\\sanguo\\textures" );
					if ( modelRootFile.exists( ) && modelRootFile.isFile( ) )
					{
						modelRootFile.delete( );
					}
					if ( !modelRootFile.exists( ) )
					{
						modelRootFile.mkdirs( );
					}
					if ( !textureRootFile.exists( ) )
					{
						textureRootFile.mkdirs( );
					}
					if ( modelRootFile.exists( ) && modelRootFile.isDirectory( ) )
					{
						FileUtil.writeToBinarayFile( new File( modelRootFile.getAbsolutePath( )
								+ "\\Xianzhenyin_Lvbu.cas" ),
								ModelPatchPage.class.getResourceAsStream( "/org/sf/feeling/sanguo/patch/models/Xianzhenyin_Lvbu.cas" ) );
						FileUtil.writeToBinarayFile( new File( modelRootFile.getAbsolutePath( )
								+ "\\Xianzhenyin_Lvbu1.cas" ),
								ModelPatchPage.class.getResourceAsStream( "/org/sf/feeling/sanguo/patch/models/Xianzhenyin_Lvbu1.cas" ) );
						FileUtil.writeToBinarayFile( new File( modelRootFile.getAbsolutePath( )
								+ "\\Xianzhenyin_Lvbu2.cas" ),
								ModelPatchPage.class.getResourceAsStream( "/org/sf/feeling/sanguo/patch/models/Xianzhenyin_Lvbu2.cas" ) );
						FileUtil.writeToBinarayFile( new File( modelRootFile.getAbsolutePath( )
								+ "\\Xianzhenyin_Lvbu3.cas" ),
								ModelPatchPage.class.getResourceAsStream( "/org/sf/feeling/sanguo/patch/models/Xianzhenyin_Lvbu3.cas" ) );
					}
					if ( textureRootFile.exists( )
							&& textureRootFile.isDirectory( ) )
					{
						FileUtil.writeToBinarayFile( new File( textureRootFile.getAbsolutePath( )
								+ "\\Xianzhenyin_Lvbu.cas" ),
								ModelPatchPage.class.getResourceAsStream( "/org/sf/feeling/sanguo/patch/models/Xianzhenyin_Lvbu.tga.dds" ) );
					}
					MapUtil.initMap( );
					gaoshunApply.setEnabled( true );
				}
			} );

			gaoshunRestore.addSelectionListener( new RestoreListener( ) );

			gaoshunBtn.addSelectionListener( new SelectionAdapter( ) {

				public void widgetSelected( SelectionEvent e )
				{
					gaoshunApply.setEnabled( gaoshunBtn.getSelection( ) );
				}

			} );

		}
		patchSection.setClient( patchClient );
	}

	private void createTitle( )
	{
		WidgetUtil.createFormText( container.getBody( ),
				"本页面用于导入部分模型到游戏，配置完毕后重启游戏即可生效。" );
	}

	public String getDisplayName( )
	{
		return "模型导入";
	}

	private void updateAvailableModels( )
	{
		final SortMap unitModelMap = new SortMap( );
		SortMap generalModelMap = new SortMap( );
		List soldierList = new ArrayList( );
		if ( FileConstants.unitFile.exists( ) )
		{
			try
			{
				String line = null;

				BufferedReader in = new BufferedReader( new InputStreamReader( new FileInputStream( FileConstants.unitFile ),
						"GBK" ) );
				boolean startSoldier = false;
				List models = new ArrayList( );
				SortMap modelMap = generalModelMap;
				while ( ( line = in.readLine( ) ) != null )
				{
					if ( line.split( ";" ).length == 0 )
					{
						continue;
					}
					if ( !startSoldier )
					{
						Pattern pattern = Pattern.compile( "^\\s*(type)(\\s+)" );
						Matcher matcher = pattern.matcher( line );
						if ( matcher.find( ) )
						{
							modelMap = generalModelMap;
							startSoldier = true;
						}
					}
					else
					{
						Pattern pattern = Pattern.compile( "^\\s*(soldier)(\\s+)" );
						Matcher matcher = pattern.matcher( line );
						if ( matcher.find( ) )
						{
							soldierList.add( line.split( ";" )[0].replaceFirst( "soldier",
									"" )
									.trim( )
									.split( "," )[0].trim( ) );
							continue;
						}

						pattern = Pattern.compile( "^\\s*(officer)(\\s+)" );
						matcher = pattern.matcher( line );
						if ( matcher.find( ) )
						{
							models.add( line.split( ";" )[0].replaceFirst( "officer",
									"" )
									.trim( ) );
							continue;
						}

						pattern = Pattern.compile( "^\\s*(ownership)(\\s+)" );
						matcher = pattern.matcher( line );
						if ( matcher.find( ) )
						{
							for ( int i = 0; i < models.size( ); i++ )
							{
								Object solider = models.get( i );
								if ( modelMap.containsKey( solider ) )
								{
									int value = ( Integer.parseInt( (String) modelMap.get( solider ) ) + 1 );
									modelMap.put( solider, "" + value );
								}
								else
									modelMap.put( solider, "1" );
							}
							models.clear( );
							startSoldier = false;
							continue;
						}
						pattern = Pattern.compile( "^\\s*(attributes)(\\s+)" );
						matcher = pattern.matcher( line );
						if ( matcher.find( ) )
						{
							if ( line.indexOf( "general_unit" ) == -1 )
							{
								modelMap = unitModelMap;
							}
							continue;
						}
					}
				}
				in.close( );
			}
			catch ( IOException e )
			{
				e.printStackTrace( );
			}

			for ( int i = 0; i < unitModelMap.getKeyList( ).size( ); i++ )
			{
				String key = (String) unitModelMap.getKeyList( ).get( i );
				if ( soldierList.contains( key )
						|| generalModelMap.getKeyList( ).contains( key ) )
				{
					unitModelMap.remove( key );
					i--;
				}
			}

			unitModelMap.remove( "ZhongNian_general" );
			if ( unitModelMap.size( ) > 0 )
			{
				try
				{
					StringWriter writer = new StringWriter( );
					PrintWriter printer = new PrintWriter( writer );
					String line = null;
					BufferedReader in = new BufferedReader( new InputStreamReader( new FileInputStream( FileConstants.unitFile ),
							"GBK" ) );
					while ( ( line = in.readLine( ) ) != null )
					{
						if ( line.split( ";" ).length == 0 )
						{
							printer.println( line );
							continue;
						}
						Pattern pattern = Pattern.compile( "^\\s*(officer)(\\s+)" );
						Matcher matcher = pattern.matcher( line );
						if ( matcher.find( ) )
						{
							String officer = line.split( ";" )[0].replaceFirst( "officer",
									"" )
									.trim( );
							if ( unitModelMap.getKeyList( ).contains( officer ) )
							{
								printer.println( line.replaceAll( officer,
										"ZhongNian_general" ) );
								continue;
							}
						}
						printer.println( line );
					}
					in.close( );
					PrintWriter out = new PrintWriter( new BufferedWriter( new OutputStreamWriter( new FileOutputStream( FileConstants.unitFile ),
							"GBK" ) ),
							false );
					out.print( writer.getBuffer( ) );
					out.close( );
					printer.close( );
				}
				catch ( IOException e )
				{
					e.printStackTrace( );
				}
				BattleUtil.removeModelTypes( unitModelMap.getKeyList( ) );
			}
		}
	}
}
