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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
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
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.sf.feeling.sanguo.patch.util.BakUtil;
import org.sf.feeling.sanguo.patch.util.FileConstants;
import org.sf.feeling.sanguo.patch.util.UnitUtil;
import org.sf.feeling.sanguo.patch.widget.WidgetUtil;
import org.sf.feeling.swt.win32.extension.util.SortMap;

public class BuildingBonusPage extends SimpleTabPage
{

	private String[] buildings = new String[]{
			"wooden_pallisade",
			"wooden_wall",
			"stone_wall",
			"large_stone_wall",
			"epic_stone_wall"
	};
	private final String[] bonusTypes = new String[]{
			"weapon_simple(\\s+)bonus",
			"weapon_missile(\\s+)bonus",
			"weapon_bladed(\\s+)bonus",
			"armour(\\s+)bonus",
			"recruits_exp_bonus(\\s+)bonus",
			"recruits_morale_bonus(\\s+)bonus",
			"population_growth_bonus(\\s+)bonus",
			"population_loyalty_bonus(\\s+)bonus",
			"population_health_bonus(\\s+)bonus",
			"happiness_bonus(\\s+)bonus",
			"law_bonus(\\s+)bonus",
			"taxable_income_bonus(\\s+)bonus",
			"trade_base_income_bonus(\\s+)bonus",
			"farming_level(\\s+)bonus",
			"trade_level_bonus(\\s+)bonus",
			"religious_belief(\\s+)christianity",
			"religious_belief(\\s+)pagan",
			"religious_belief(\\s+)zoroastrian",
			"religious_belief(\\s+)zj04xj",
			"religious_belief(\\s+)zj05yizu",
			"religious_belief(\\s+)zj06dao",
			"religious_belief(\\s+)zj07hj"
	};
	private final int[] bonusValues = new int[]{
			3,
			3,
			3,
			3,
			9,
			9,
			20,
			20,
			20,
			20,
			20,
			100,
			20,
			20,
			20,
			20,
			20,
			20,
			20,
			20,
			20,
			20
	};

	private final String[] bonusDescs = new String[]{
			"轻武器",
			"弓矢武器",
			"重武器",
			"盔甲",
			"新兵经验",
			"新兵士气",
			"人口增长",
			"民心",
			"公共健康",
			"快乐",
			"法律",
			"税收收入",
			"贸易收入",
			"农业",
			"商业",
			"复兴汉室",
			"改朝换代",
			"保境安民",
			"雄踞一方",
			"异族文化",
			"道教文化",
			"黄天当立"
	};
	private CCombo factionCombo;
	private SortMap factionMap;

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
				"注意：加成效果描述可以打开城墙说明页面进行查看。" );
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
		layout.numColumns = 4;

		patchClient.setLayout( layout );

		WidgetUtil.getToolkit( ).createLabel( patchClient, "选择势力：" );

		factionCombo = WidgetUtil.getToolkit( ).createCCombo( patchClient,
				SWT.READ_ONLY );
		GridData gd = new GridData( );
		gd.widthHint = 150;
		gd.horizontalSpan = 3;
		factionCombo.setLayoutData( gd );

		final List comboList = new ArrayList( );
		for ( int j = 0; j < bonusTypes.length; j++ )
		{
			WidgetUtil.getToolkit( ).createLabel( patchClient,
					bonusDescs[j] + ":",
					SWT.NONE );
			final CCombo bonusCombo = WidgetUtil.getToolkit( )
					.createCCombo( patchClient, SWT.READ_ONLY );
			bonusCombo.setEnabled( false );
			comboList.add( bonusCombo );
			gd = new GridData( GridData.FILL_HORIZONTAL );
			gd.widthHint = 150;
			bonusCombo.setLayoutData( gd );
			initNumberCombo( bonusCombo, 0, bonusValues[j] );
		}

		gd = new GridData( GridData.FILL_HORIZONTAL );
		gd.horizontalSpan = 4;
		Composite buttonGroup = WidgetUtil.getToolkit( )
				.createComposite( patchClient );
		buttonGroup.setLayoutData( gd );

		layout = new GridLayout( );
		layout.numColumns = 2;
		buttonGroup.setLayout( layout );

		final Button bonusApply = WidgetUtil.getToolkit( )
				.createButton( buttonGroup, "应用", SWT.PUSH );
		gd = new GridData( );
		gd.grabExcessHorizontalSpace = true;
		gd.horizontalAlignment = SWT.END;
		bonusApply.setLayoutData( gd );
		bonusApply.setEnabled( false );

		final Button bonusRestoreApply = WidgetUtil.getToolkit( )
				.createButton( buttonGroup, "还原", SWT.PUSH );
		gd = new GridData( );
		gd.grabExcessHorizontalSpace = true;
		bonusRestoreApply.setLayoutData( gd );
		bonusRestoreApply.addSelectionListener( new SelectionAdapter( ) {

			public void widgetSelected( SelectionEvent e )
			{
				bonusRestoreApply.setEnabled( false );
				BakUtil.restoreCurrectVersionBakFile( );
				refreshPage( );
				bonusRestoreApply.setEnabled( true );
			}
		} );

		bonusApply.addSelectionListener( new SelectionAdapter( ) {

			public void widgetSelected( SelectionEvent e )
			{
				bonusApply.setEnabled( false );
				String faction = (String) factionMap.getKeyList( )
						.get( factionCombo.getSelectionIndex( ) );
				BakUtil.bakData( "建筑加成修改：" + factionCombo.getText( ) );

				for ( int i = 0; i < comboList.size( ); i++ )
				{
					CCombo bonusCombo = (CCombo) comboList.get( i );
					if ( bonusCombo.getSelectionIndex( ) > -1 )
					{
						String bonus = bonusCombo.getText( );
						UnitUtil.addBonusToBuildings( bonusTypes[i],
								bonus,
								faction,
								buildings );
					}
				}
				bonusApply.setEnabled( true );
			}
		} );

		factionCombo.addSelectionListener( new SelectionAdapter( ) {

			public void widgetSelected( SelectionEvent e )
			{
				bonusApply.setEnabled( factionCombo.getSelectionIndex( ) > -1 );
				for ( int i = 0; i < comboList.size( ); i++ )
				{
					CCombo bonusCombo = (CCombo) comboList.get( i );
					bonusCombo.setEnabled( factionCombo.getSelectionIndex( ) > -1 );
				}
				if ( factionCombo.getSelectionIndex( ) > -1 )
				{
					SortMap sortMap = loadFactionBonus( (String) factionMap.getKeyList( )
							.get( factionCombo.getSelectionIndex( ) ) );
					for ( int i = 0; i < comboList.size( ); i++ )
					{
						CCombo bonusCombo = (CCombo) comboList.get( i );
						if ( sortMap.containsKey( bonusTypes[i] ) )
						{
							bonusCombo.setText( ( (String) sortMap.get( bonusTypes[i] ) ).trim( ) );
						}
						else
						{
							bonusCombo.setText( "" );
							bonusCombo.clearSelection( );
						}
					}
				}
			}
		} );
		patchSection.setClient( patchClient );
	}

	private void createTitle( )
	{
		WidgetUtil.createFormText( container.getBody( ),
				"本页面用于设置势力建筑特殊加成，配置完毕后重启游戏即可生效。" );
	}

	public String getDisplayName( )
	{
		return "建筑加成修改";
	}

	private void initNumberCombo( CCombo combo, int min, int max )
	{
		for ( int i = min; i <= max; i++ )
		{
			combo.add( "" + i );
		}
	}

	private SortMap loadFactionBonus( String faction )
	{
		SortMap sortMap = new SortMap( );
		if ( FileConstants.buildingsFile.exists( ) )
		{
			try
			{
				BufferedReader in = new BufferedReader( new InputStreamReader( new FileInputStream( FileConstants.buildingsFile ),
						"GBK" ) );
				String line = null;

				boolean building = false;
				boolean startBuilding = false;
				boolean startBuildingLeft = false;

				while ( ( line = in.readLine( ) ) != null )
				{
					if ( !building )
					{
						StringBuffer buildingStr = new StringBuffer( );
						buildingStr.append( "(" );
						buildingStr.append( buildings[0] );
						buildingStr.append( ")" );
						Pattern pattern = Pattern.compile( buildingStr.append( "(\\s+)(requires)(\\s+)(factions)" )
								.toString( ) );
						Matcher matcher = pattern.matcher( line );
						if ( matcher.find( ) )
						{
							building = true;
							continue;
						}
					}
					else
					{
						if ( !startBuilding
								&& line.indexOf( "capability" ) != -1 )
						{
							startBuilding = true;
							if ( line.indexOf( "{" ) > line.indexOf( "capability" ) )
							{
								startBuildingLeft = true;
							}
							continue;
						}
						if ( startBuilding && !startBuildingLeft )
						{
							if ( line.indexOf( "{" ) != -1 )
							{
								startBuildingLeft = true;
								continue;
							}
						}
						if ( startBuilding && startBuildingLeft )
						{
							if ( "}".equals( line.trim( ) ) )
							{
								break;
							}
							else
							{
								for ( int i = 0; i < bonusTypes.length; i++ )
								{
									Pattern pattern = Pattern.compile( "^\\s*("
											+ bonusTypes[i]
											+ ")(\\s+)(\\d+)(\\s+)(requires)(\\s+)(factions)(\\s*)(\\{)(\\s*)((?i)"
											+ faction
											+ ")(\\s*)(,*)(\\s*)(\\})" );
									Matcher matcher = pattern.matcher( line );
									if ( matcher.find( ) )
									{
										Pattern pattern1 = Pattern.compile( "\\d+" );
										Matcher matcher1 = pattern1.matcher( line.replaceAll( bonusTypes[i],
												"" ) );
										if ( matcher1.find( ) )
										{
											sortMap.put( bonusTypes[i],
													matcher1.group( ) );
										}
										break;
									}
								}
							}
						}
					}
				}
				in.close( );
			}
			catch ( IOException e )
			{
				e.printStackTrace( );
			}
		}
		return sortMap;
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
	}
}
