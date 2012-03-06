
package org.sf.feeling.sanguo.patch.dialog;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.sf.feeling.sanguo.patch.model.Unit;
import org.sf.feeling.sanguo.patch.util.BakUtil;
import org.sf.feeling.sanguo.patch.util.ChangeCode;
import org.sf.feeling.sanguo.patch.util.MapUtil;
import org.sf.feeling.sanguo.patch.util.UnitParser;
import org.sf.feeling.sanguo.patch.util.UnitUtil;
import org.sf.feeling.sanguo.patch.widget.WidgetUtil;
import org.sf.feeling.swt.win32.extension.util.SortMap;

public class UnitModify
{

	private Unit currentUnit;
	private SortMap generalUnitMap = UnitUtil.getGeneralUnits( );
	private SortMap officerMap = UnitUtil.getAvailableOfficers( );
	private SortMap horseMap = UnitUtil.getAvailableHorses( );
	private SortMap soldierQiMap = UnitUtil.getCavalryMap( );
	private SortMap soldierBuMap = UnitUtil.getInfantryMap( );
	private SortMap soldierSiegeMap = UnitUtil.getSiegeMap( );
	private SortMap soldierHandlerMap = UnitUtil.getHandlerMap( );
	private SortMap unitMap = UnitUtil.getUnits( );
	private CCombo soldierNumberCombo;
	private CCombo soldierLifeCombo;
	private CCombo heatCombo;
	private CCombo chargeCombo;
	private CCombo officer1Combo;
	private CCombo officer2Combo;
	private CCombo officer3Combo;
	private CCombo soldierModelCombo;
	private CCombo mountCombo;
	private Group attributesGroup;
	private CCombo attackCombo;
	private CCombo powerCombo;
	private CCombo rangeCombo;
	private CCombo amountCombo;
	private CCombo delayCombo;
	private Button apBtn;
	private Button bpBtn;
	private Button areaBtn;
	private Button spearBtn;
	private CCombo bonusCombo;
	private CCombo secAttackCombo;
	private CCombo secPowerCombo;
	private CCombo secRangeCombo;
	private CCombo secAmountCombo;
	private CCombo secDelayCombo;
	private Group statSecAttriGroup;
	private Button secApBtn;
	private Button secBpBtn;
	private Button secAreaBtn;
	private Button secSpearBtn;
	private CCombo secBonusCombo;
	private CCombo armourCombo;
	private CCombo skillCombo;
	private CCombo shieldCombo;
	private CCombo moraleCombo;
	private CCombo disciplineCombo;
	private CCombo trainingCombo;
	private CCombo horseCombo;
	private CCombo camelCombo;
	private CCombo elephantCombo;
	private CCombo chariotCombo;
	private CCombo scrubCombo;
	private CCombo sandCombo;
	private CCombo forestCombo;
	private CCombo snowCombo;
	private CCombo turnsCombo;
	private CCombo costCombo;
	private CCombo maintainCombo;
	private CCombo weaponUpdateCombo;
	private CCombo armourUpdateCombo;
	private CCombo battleCostCombo;
	private Button applyButton;
	private Button restoreButton;

	private void initModels( )
	{
		officers = new String[officerMap.size( )];
		{
			for ( int i = 0; i < officers.length; i++ )
			{
				officers[i] = (String) officerMap.get( i );
			}
		}

		horses = new String[horseMap.size( )];
		{
			for ( int i = 0; i < horses.length; i++ )
			{
				horses[i] = (String) horseMap.get( i );
			}
		}

		soldierQis = new String[soldierQiMap.size( )];
		{
			for ( int i = 0; i < soldierQis.length; i++ )
			{
				soldierQis[i] = ChangeCode.toLong( (String) unitMap.get( soldierQiMap.getKeyList( )
						.get( i ) ) );
				if ( this.generalUnitMap.containsKey( soldierQiMap.getKeyList( )
						.get( i ) ) )
					soldierQis[i] += "卫队";
				else
					soldierQis[i] += "士兵";
			}
		}

		soldierBus = new String[soldierBuMap.size( )];
		{
			for ( int i = 0; i < soldierBus.length; i++ )
			{
				soldierBus[i] = ChangeCode.toLong( (String) unitMap.get( soldierBuMap.getKeyList( )
						.get( i ) ) );
				if ( this.generalUnitMap.containsKey( soldierBuMap.getKeyList( )
						.get( i ) ) )
					soldierBus[i] += "卫队";
				else
					soldierBus[i] += "士兵";
			}
		}

		soldierSieges = new String[soldierSiegeMap.size( )];
		{
			for ( int i = 0; i < soldierSieges.length; i++ )
			{
				soldierSieges[i] = ChangeCode.toLong( (String) unitMap.get( soldierSiegeMap.getKeyList( )
						.get( i ) ) );
				if ( this.generalUnitMap.containsKey( soldierSiegeMap.getKeyList( )
						.get( i ) ) )
					soldierSieges[i] += "卫队";
				else
					soldierSieges[i] += "士兵";
			}
		}

		soldierHandlers = new String[soldierHandlerMap.size( )];
		{
			for ( int i = 0; i < soldierHandlers.length; i++ )
			{
				soldierHandlers[i] = ChangeCode.toLong( (String) unitMap.get( soldierHandlerMap.getKeyList( )
						.get( i ) ) );
				if ( this.generalUnitMap.containsKey( soldierHandlerMap.getKeyList( )
						.get( i ) ) )
					soldierHandlers[i] += "卫队";
				else
					soldierHandlers[i] += "士兵";
			}
		}
	}

	VerifyListener numberVerifyListener = new VerifyListener( ) {

		public void verifyText( VerifyEvent event )
		{
			if ( event.text.length( ) <= 0 )
			{
				return;
			}
			int beginIndex = Math.min( event.start, event.end );
			int endIndex = Math.max( event.start, event.end );
			String inputtedText = ( (CCombo) event.widget ).getText( );
			String newString = inputtedText.substring( 0, beginIndex );

			newString += event.text;
			newString += inputtedText.substring( endIndex );
			try
			{
				if ( newString.length( ) > 0 )
					NumberFormat.getInstance( ).parse( newString );
			}
			catch ( Exception e )
			{
				event.doit = false;
				return;
			}
			event.doit = true;
		}
	};
	private Button hideForestBtn;
	private Button hideTreeBtn;
	private Button hideGrassBtn;
	private Button hideAnyWhereBtn;
	private CCombo soldierCombo;

	public Control createModifyControl( Composite parent, boolean isMemory )
	{
		Composite clientContainer = WidgetUtil.getToolkit( )
				.createComposite( parent );
		GridLayout layout = new GridLayout( );
		layout.numColumns = 4;
		clientContainer.setLayout( layout );

		Label soldierLabel = WidgetUtil.getToolkit( )
				.createLabel( clientContainer, "选择兵种：" );

		soldierCombo = WidgetUtil.getToolkit( ).createCCombo( clientContainer,
				SWT.READ_ONLY );
		GridData gd = new GridData( );
		gd.widthHint = 150;
		soldierCombo.setLayoutData( gd );

		initSoldierCombo( );

		if ( isMemory )
		{
			gd = new GridData( );
			gd.horizontalSpan = 2;

			Label label = WidgetUtil.getToolkit( )
					.createLabel( clientContainer, "(修改数据不会影响原兵种属性)" );
			label.setLayoutData( gd );
		}
		else
		{
			nameLabel = WidgetUtil.getToolkit( ).createLabel( clientContainer,
					"编辑名称：" );

			gd = new GridData( );
			gd.widthHint = 147;
			nameText = WidgetUtil.getToolkit( )
					.createText( clientContainer, "" );
			nameText.setLayoutData( gd );
		}

		final Composite patchClient = WidgetUtil.getToolkit( )
				.createComposite( clientContainer );
		layout = new GridLayout( );
		layout.marginWidth = 1;
		layout.marginHeight = 1;
		layout.numColumns = 4;
		patchClient.setLayout( layout );

		gd = new GridData( GridData.FILL_BOTH );
		gd.horizontalSpan = 4;
		patchClient.setLayoutData( gd );
		patchClient.setEnabled( false );

		WidgetUtil.getToolkit( ).createLabel( patchClient, "士兵数量：" );
		soldierNumberCombo = WidgetUtil.getToolkit( )
				.createCCombo( patchClient, SWT.READ_ONLY );
		gd = new GridData( );
		gd.widthHint = 150;
		soldierNumberCombo.setLayoutData( gd );
		initNumberCombo( soldierNumberCombo, 6, 60 );

		WidgetUtil.getToolkit( ).createLabel( patchClient, "士兵生命：" );
		soldierLifeCombo = WidgetUtil.getToolkit( ).createCCombo( patchClient,
				SWT.READ_ONLY );
		gd = new GridData( );
		gd.widthHint = 150;
		soldierLifeCombo.setLayoutData( gd );
		initNumberCombo( soldierLifeCombo, 1, 15 );

		WidgetUtil.getToolkit( ).createLabel( patchClient, "体力流失：" );
		heatCombo = WidgetUtil.getToolkit( ).createCCombo( patchClient,
				SWT.READ_ONLY );
		gd = new GridData( );
		gd.widthHint = 150;
		heatCombo.setLayoutData( gd );
		initNumberCombo( heatCombo, -4, 4 );

		WidgetUtil.getToolkit( ).createLabel( patchClient, "冲锋距离：" );
		chargeCombo = WidgetUtil.getToolkit( ).createCCombo( patchClient,
				SWT.READ_ONLY );
		gd = new GridData( );
		gd.widthHint = 150;
		chargeCombo.setLayoutData( gd );
		initNumberCombo( chargeCombo, 10, 100, 10 );

		WidgetUtil.getToolkit( ).createLabel( patchClient, "将军1模型：" );
		officer1Combo = WidgetUtil.getToolkit( ).createCCombo( patchClient,
				SWT.READ_ONLY );
		gd = new GridData( );
		gd.widthHint = 150;
		officer1Combo.setLayoutData( gd );
		officer1Combo.setItems( officers );
		officer1Combo.add( "", 0 );

		Label general2Label = WidgetUtil.getToolkit( )
				.createLabel( patchClient, "将军2模型：" );
		officer2Combo = WidgetUtil.getToolkit( ).createCCombo( patchClient,
				SWT.READ_ONLY );
		gd = new GridData( );
		gd.widthHint = 150;
		officer2Combo.setLayoutData( gd );
		officer2Combo.setItems( officers );
		officer2Combo.add( "", 0 );

		Label general3Label = WidgetUtil.getToolkit( )
				.createLabel( patchClient, "将军3模型：" );
		officer3Combo = WidgetUtil.getToolkit( ).createCCombo( patchClient,
				SWT.READ_ONLY );
		gd = new GridData( );
		gd.widthHint = 150;
		officer3Combo.setLayoutData( gd );
		officer3Combo.setItems( officers );
		officer3Combo.add( "", 0 );

		gd = new GridData( );
		gd.widthHint = general3Label.computeSize( SWT.DEFAULT, SWT.DEFAULT ).x + 1;
		soldierLabel.setLayoutData( gd );

		if ( nameLabel != null )
		{
			gd = new GridData( );
			gd.widthHint = general2Label.computeSize( SWT.DEFAULT, SWT.DEFAULT ).x;
			nameLabel.setLayoutData( gd );
		}

		WidgetUtil.getToolkit( ).createLabel( patchClient, "马匹模型：" );
		mountCombo = WidgetUtil.getToolkit( ).createCCombo( patchClient,
				SWT.READ_ONLY );
		gd = new GridData( );
		gd.widthHint = 150;
		mountCombo.setLayoutData( gd );
		mountCombo.setItems( horses );
		mountCombo.add( "", 0 );

		WidgetUtil.getToolkit( ).createLabel( patchClient, "士兵模型：" );
		soldierModelCombo = WidgetUtil.getToolkit( ).createCCombo( patchClient,
				SWT.READ_ONLY );
		gd = new GridData( );
		gd.widthHint = 150;
		soldierModelCombo.setLayoutData( gd );
		soldierModelCombo.add( "", 0 );
		soldierModelCombo.addSelectionListener( new SelectionAdapter( ) {

			public void widgetSelected( SelectionEvent e )
			{
				String unitType = null;

				if ( soldierCombo.getSelectionIndex( ) != -1 )
				{
					patchClient.setEnabled( true );
					String soldierType = null;
					int index = soldierCombo.getSelectionIndex( )
							- ( soldierCombo.getItemCount( ) - soldierUnitMap.size( ) );
					if ( index > -1 )
					{
						soldierType = (String) soldierUnitMap.getKeyList( )
								.get( index );
					}
					else
					{
						soldierType = (String) generalUnitMap.getKeyList( )
								.get( soldierCombo.getSelectionIndex( ) );
					}

					Unit soldier = UnitParser.getUnit( soldierType );

					if ( soldierType != null )
					{
						if ( soldierModelCombo.getSelectionIndex( ) > 0
								&& soldierModelCombo.isEnabled( ) )
						{
							if ( "infantry".equals( soldier.getCategory( ) ) )
							{
								unitType = (String) soldierBuMap.getKeyList( )
										.get( soldierModelCombo.getSelectionIndex( ) - 1 );
							}
							else if ( "cavalry".equals( soldier.getCategory( ) ) )
							{
								unitType = (String) soldierQiMap.getKeyList( )
										.get( soldierModelCombo.getSelectionIndex( ) - 1 );
							}
							else if ( "siege".equals( soldier.getCategory( ) ) )
							{
								unitType = (String) soldierSiegeMap.getKeyList( )
										.get( soldierModelCombo.getSelectionIndex( ) - 1 );
							}
							else if ( "handler".equals( soldier.getCategory( ) ) )
							{
								unitType = (String) soldierHandlerMap.getKeyList( )
										.get( soldierModelCombo.getSelectionIndex( ) - 1 );
							}

							Unit unit = UnitParser.getUnit( unitType );
							if ( ( unit.hasSecondWeapon( ) && !soldier.hasSecondWeapon( ) )
									|| ( unit.hasPrimaryWeapon( ) && !soldier.hasPrimaryWeapon( ) ) )
							{
								MessageDialog.openInformation( Display.getDefault( )
										.getActiveShell( ),
										"错误信息提示",
										"不能选择“"
												+ soldierModelCombo.getText( )
												+ "”作为该兵种士兵兵模。" );
								soldierModelCombo.select( 0 );
								soldierModelCombo.notifyListeners( SWT.Selection,
										new Event( ) );
								return;
							}
						}
					}
				}

			}

		} );

		Label infoLabel = WidgetUtil.getToolkit( )
				.createLabel( patchClient, "" );
		gd = new GridData( GridData.FILL_HORIZONTAL );
		gd.horizontalSpan = 2;
		infoLabel.setLayoutData( gd );
		infoLabel.setText( "（部分兵模可能会导致游戏跳出，修改后请测试）" );
		infoLabel.setForeground( Display.getDefault( )
				.getSystemColor( SWT.COLOR_RED ) );

		gd = new GridData( GridData.FILL_HORIZONTAL );
		gd.horizontalSpan = 4;
		attributesGroup = WidgetUtil.getToolkit( ).createGroup( patchClient,
				"兵种特殊属性（请不要选超过10项）" );
		attributesGroup.setLayoutData( gd );

		layout = new GridLayout( );
		layout.numColumns = 6;
		attributesGroup.setLayout( layout );

		Button frightenFootBtn = WidgetUtil.getToolkit( )
				.createButton( attributesGroup, SWT.CHECK, true );
		frightenFootBtn.setText( "惊吓步兵" );
		frightenFootBtn.setData( "frighten_foot" );

		Button frightenMountBtn = WidgetUtil.getToolkit( )
				.createButton( attributesGroup, SWT.CHECK, true );
		frightenMountBtn.setText( "惊吓骑兵" );
		frightenMountBtn.setData( "frighten_mounted" );

		hideTreeBtn = WidgetUtil.getToolkit( ).createButton( attributesGroup,
				SWT.CHECK,
				true );
		hideTreeBtn.setText( "树林隐藏" );
		hideTreeBtn.setData( "hide_forest" );

		hideForestBtn = WidgetUtil.getToolkit( ).createButton( attributesGroup,
				SWT.CHECK,
				true );
		hideForestBtn.setText( "森林隐藏" );
		hideForestBtn.setData( "hide_improved_forest" );

		hideGrassBtn = WidgetUtil.getToolkit( ).createButton( attributesGroup,
				SWT.CHECK,
				true );
		hideGrassBtn.setText( "草丛隐藏" );
		hideGrassBtn.setData( "hide_long_grass" );

		hideAnyWhereBtn = WidgetUtil.getToolkit( )
				.createButton( attributesGroup, SWT.CHECK, true );
		hideAnyWhereBtn.setText( "到处隐藏" );
		hideAnyWhereBtn.setData( "hide_anywhere" );
		SelectionAdapter hideListener = new SelectionAdapter( ) {

			public void widgetSelected( SelectionEvent e )
			{
				checkHideStatus( );
			}
		};
		hideAnyWhereBtn.addSelectionListener( hideListener );
		hideForestBtn.addSelectionListener( hideListener );
		hideGrassBtn.addSelectionListener( hideListener );
		hideTreeBtn.addSelectionListener( hideListener );

		Button commandBtn = WidgetUtil.getToolkit( )
				.createButton( attributesGroup, SWT.CHECK, true );
		commandBtn.setText( "中军战旗" );
		commandBtn.setToolTipText( "提高附近友军的士气" );
		commandBtn.setData( "command" );

		Button chargeBtn = WidgetUtil.getToolkit( )
				.createButton( attributesGroup, SWT.CHECK, true );
		chargeBtn.setText( "强力冲锋" );
		chargeBtn.setData( "power_charge" );

		Button swimBtn = WidgetUtil.getToolkit( )
				.createButton( attributesGroup, SWT.CHECK, true );
		swimBtn.setText( "会游泳" );
		swimBtn.setData( "can_swim" );

		Button warcryBtn = WidgetUtil.getToolkit( )
				.createButton( attributesGroup, SWT.CHECK, true );
		warcryBtn.setText( "能战吼" );
		warcryBtn.setToolTipText( "降低附近敌人的士气" );
		warcryBtn.setData( "warcry" );

		Button withdrawBtn = WidgetUtil.getToolkit( )
				.createButton( attributesGroup, SWT.CHECK, true );
		withdrawBtn.setText( "自动撤退" );
		withdrawBtn.setToolTipText( "弓兵和弓骑非常有用的技能" );
		withdrawBtn.setData( "can_withdraw" );

		Button womenBtn = WidgetUtil.getToolkit( )
				.createButton( attributesGroup, SWT.CHECK, true );
		womenBtn.setText( "刺耳尖叫" );
		womenBtn.setToolTipText( "惊吓附近敌人" );
		womenBtn.setData( "screeching_women" );

		Button druidBtn = WidgetUtil.getToolkit( )
				.createButton( attributesGroup, SWT.CHECK, true );
		druidBtn.setText( "德鲁伊" );
		druidBtn.setToolTipText( "增加友军士气" );
		druidBtn.setData( "druid" );

		final Button hardyBtn = WidgetUtil.getToolkit( )
				.createButton( attributesGroup, SWT.CHECK, true );
		hardyBtn.setText( "耐力良好" );
		hardyBtn.setData( "hardy" );

		final Button veryHardyBtn = WidgetUtil.getToolkit( )
				.createButton( attributesGroup, SWT.CHECK, true );
		veryHardyBtn.setText( "耐力极佳" );
		veryHardyBtn.setData( "very_hardy" );

		hardyBtn.addSelectionListener( new SelectionAdapter( ) {

			public void widgetSelected( SelectionEvent e )
			{
				if ( hardyBtn.getSelection( ) )
					veryHardyBtn.setSelection( !hardyBtn.getSelection( ) );
			}
		} );
		veryHardyBtn.addSelectionListener( new SelectionAdapter( ) {

			public void widgetSelected( SelectionEvent e )
			{
				if ( veryHardyBtn.getSelection( ) )
					hardyBtn.setSelection( !veryHardyBtn.getSelection( ) );
			}
		} );

		Button seaBtn = WidgetUtil.getToolkit( ).createButton( attributesGroup,
				SWT.CHECK,
				true );
		seaBtn.setText( "可以坐船" );
		seaBtn.setData( "sea_faring" );

		Button sapBtn = WidgetUtil.getToolkit( ).createButton( attributesGroup,
				SWT.CHECK,
				true );
		sapBtn.setText( "可挖地道" );
		sapBtn.setToolTipText( "步兵才有效" );
		sapBtn.setData( "can_sap" );

		Button amokBtn = WidgetUtil.getToolkit( )
				.createButton( attributesGroup, SWT.CHECK, true );
		amokBtn.setText( "动物乱跑" );
		amokBtn.setToolTipText( "让大象狂暴的原因" );
		amokBtn.setData( "can_run_amok" );

		gd = new GridData( GridData.FILL_HORIZONTAL );
		gd.horizontalSpan = 4;
		Group statPriGroup = WidgetUtil.getToolkit( ).createGroup( patchClient,
				"主武器数据" );
		statPriGroup.setLayoutData( gd );

		layout = new GridLayout( );
		layout.numColumns = 6;
		statPriGroup.setLayout( layout );

		WidgetUtil.getToolkit( ).createLabel( statPriGroup, "攻击力：" );

		attackCombo = WidgetUtil.getToolkit( ).createCCombo( statPriGroup,
				SWT.READ_ONLY );
		gd = new GridData( );
		gd.widthHint = 100;
		attackCombo.setLayoutData( gd );
		initNumberCombo( attackCombo, 0, 50 );

		WidgetUtil.getToolkit( ).createLabel( statPriGroup, "冲击力：" );
		powerCombo = WidgetUtil.getToolkit( ).createCCombo( statPriGroup,
				SWT.READ_ONLY );
		gd = new GridData( );
		gd.widthHint = 100;
		powerCombo.setLayoutData( gd );
		initNumberCombo( powerCombo, 0, 20 );

		WidgetUtil.getToolkit( ).createLabel( statPriGroup, "射程：" );
		rangeCombo = WidgetUtil.getToolkit( ).createCCombo( statPriGroup,
				SWT.READ_ONLY );
		gd = new GridData( );
		gd.widthHint = 100;
		rangeCombo.setLayoutData( gd );
		initNumberCombo( rangeCombo, 0, 200, 10 );

		WidgetUtil.getToolkit( ).createLabel( statPriGroup, "弹药：" );
		amountCombo = WidgetUtil.getToolkit( ).createCCombo( statPriGroup,
				SWT.READ_ONLY );
		gd = new GridData( );
		gd.widthHint = 100;
		amountCombo.setLayoutData( gd );
		initNumberCombo( amountCombo, 0, 80, 5 );

		WidgetUtil.getToolkit( ).createLabel( statPriGroup, "攻击间隔：" );
		delayCombo = WidgetUtil.getToolkit( ).createCCombo( statPriGroup );
		gd = new GridData( );
		gd.horizontalAlignment = SWT.FILL;
		delayCombo.setLayoutData( gd );
		initNumberCombo( delayCombo, 1, 200, 5 );

		WidgetUtil.getToolkit( ).createLabel( statPriGroup, "秒杀率：" );
		killCombo = WidgetUtil.getToolkit( ).createCCombo( statPriGroup );
		gd = new GridData( );
		gd.horizontalAlignment = SWT.FILL;
		killCombo.setLayoutData( gd );
		initNumberCombo( killCombo, 1, 100 );

		gd = new GridData( GridData.FILL_HORIZONTAL );
		gd.horizontalSpan = 4;
		Group statPriAttriGroup = WidgetUtil.getToolkit( )
				.createGroup( patchClient, "主武器属性" );
		statPriAttriGroup.setLayoutData( gd );
		layout = new GridLayout( );
		layout.numColumns = 6;
		statPriAttriGroup.setLayout( layout );

		apBtn = WidgetUtil.getToolkit( ).createButton( statPriAttriGroup,
				SWT.CHECK,
				true );
		apBtn.setText( "破甲" );
		bpBtn = WidgetUtil.getToolkit( ).createButton( statPriAttriGroup,
				SWT.CHECK,
				true );
		bpBtn.setText( "穿透" );
		areaBtn = WidgetUtil.getToolkit( ).createButton( statPriAttriGroup,
				SWT.CHECK,
				true );
		areaBtn.setText( "片伤" );

		spearBtn = WidgetUtil.getToolkit( ).createButton( statPriAttriGroup,
				SWT.CHECK,
				true );
		spearBtn.setText( "长枪" );
		spearBtn.addSelectionListener( new SelectionAdapter( ) {

			public void widgetSelected( SelectionEvent e )
			{
				bonusCombo.setEnabled( spearBtn.getSelection( ) );
			}

		} );

		WidgetUtil.getToolkit( ).createLabel( statPriAttriGroup, "对骑兵加成：" );
		bonusCombo = WidgetUtil.getToolkit( ).createCCombo( statPriAttriGroup,
				SWT.READ_ONLY );
		gd = new GridData( );
		gd.widthHint = 100;
		bonusCombo.setLayoutData( gd );
		initNumberCombo( bonusCombo, 2, 12, 2 );

		gd = new GridData( GridData.FILL_HORIZONTAL );
		gd.horizontalSpan = 4;
		Group statSecGroup = WidgetUtil.getToolkit( ).createGroup( patchClient,
				"副武器数据" );
		statSecGroup.setLayoutData( gd );

		layout = new GridLayout( );
		layout.numColumns = 6;
		statSecGroup.setLayout( layout );

		WidgetUtil.getToolkit( ).createLabel( statSecGroup, "攻击力：" );

		secAttackCombo = WidgetUtil.getToolkit( ).createCCombo( statSecGroup,
				SWT.READ_ONLY );
		gd = new GridData( );
		gd.widthHint = 100;
		secAttackCombo.setLayoutData( gd );
		initNumberCombo( secAttackCombo, 0, 50 );

		WidgetUtil.getToolkit( ).createLabel( statSecGroup, "冲击力：" );
		secPowerCombo = WidgetUtil.getToolkit( ).createCCombo( statSecGroup,
				SWT.READ_ONLY );
		gd = new GridData( );
		gd.widthHint = 100;
		secPowerCombo.setLayoutData( gd );
		initNumberCombo( secPowerCombo, 0, 20 );

		WidgetUtil.getToolkit( ).createLabel( statSecGroup, "射程：" );
		secRangeCombo = WidgetUtil.getToolkit( ).createCCombo( statSecGroup,
				SWT.READ_ONLY );
		gd = new GridData( );
		gd.widthHint = 100;
		secRangeCombo.setLayoutData( gd );
		initNumberCombo( secRangeCombo, 0, 200, 10 );

		WidgetUtil.getToolkit( ).createLabel( statSecGroup, "弹药：" );
		secAmountCombo = WidgetUtil.getToolkit( ).createCCombo( statSecGroup,
				SWT.READ_ONLY );
		gd = new GridData( );
		gd.widthHint = 100;
		secAmountCombo.setLayoutData( gd );
		initNumberCombo( secAmountCombo, 0, 80, 5 );

		WidgetUtil.getToolkit( ).createLabel( statSecGroup, "攻击间隔：" );
		secDelayCombo = WidgetUtil.getToolkit( ).createCCombo( statSecGroup );
		gd = new GridData( );
		gd.horizontalAlignment = SWT.FILL;
		secDelayCombo.setLayoutData( gd );
		initNumberCombo( secDelayCombo, 1, 200, 5 );

		WidgetUtil.getToolkit( ).createLabel( statSecGroup, "秒杀率：" );
		secKillCombo = WidgetUtil.getToolkit( ).createCCombo( statSecGroup );
		gd = new GridData( );
		gd.horizontalAlignment = SWT.FILL;
		secKillCombo.setLayoutData( gd );
		initNumberCombo( secKillCombo, 1, 100 );

		gd = new GridData( GridData.FILL_HORIZONTAL );
		gd.horizontalSpan = 4;
		statSecAttriGroup = WidgetUtil.getToolkit( ).createGroup( patchClient,
				"副武器属性" );
		statSecAttriGroup.setLayoutData( gd );
		layout = new GridLayout( );
		layout.numColumns = 6;
		statSecAttriGroup.setLayout( layout );

		secApBtn = WidgetUtil.getToolkit( ).createButton( statSecAttriGroup,
				SWT.CHECK,
				true );
		secApBtn.setText( "破甲" );
		secBpBtn = WidgetUtil.getToolkit( ).createButton( statSecAttriGroup,
				SWT.CHECK,
				true );
		secBpBtn.setText( "穿透" );
		secAreaBtn = WidgetUtil.getToolkit( ).createButton( statSecAttriGroup,
				SWT.CHECK,
				true );
		secAreaBtn.setText( "片伤" );
		secSpearBtn = WidgetUtil.getToolkit( ).createButton( statSecAttriGroup,
				SWT.CHECK,
				true );
		secSpearBtn.setText( "长枪" );
		secSpearBtn.addSelectionListener( new SelectionAdapter( ) {

			public void widgetSelected( SelectionEvent e )
			{
				secBonusCombo.setEnabled( secSpearBtn.getSelection( ) );
			}

		} );

		WidgetUtil.getToolkit( ).createLabel( statSecAttriGroup, "对骑兵加成：" );
		secBonusCombo = WidgetUtil.getToolkit( )
				.createCCombo( statSecAttriGroup, SWT.READ_ONLY );
		gd = new GridData( );
		gd.widthHint = 100;
		secBonusCombo.setLayoutData( gd );
		initNumberCombo( secBonusCombo, 2, 12, 2 );

		gd = new GridData( GridData.FILL_HORIZONTAL );
		gd.horizontalSpan = 4;
		Group armourGroup = WidgetUtil.getToolkit( ).createGroup( patchClient,
				"防御数据" );
		armourGroup.setLayoutData( gd );

		layout = new GridLayout( );
		layout.numColumns = 6;
		armourGroup.setLayout( layout );

		WidgetUtil.getToolkit( ).createLabel( armourGroup, "盔甲防御：" );
		armourCombo = WidgetUtil.getToolkit( ).createCCombo( armourGroup,
				SWT.READ_ONLY );
		gd = new GridData( );
		gd.widthHint = 80;
		armourCombo.setLayoutData( gd );
		initNumberCombo( armourCombo, 0, 50 );

		WidgetUtil.getToolkit( ).createLabel( armourGroup, "防御技巧：" );
		skillCombo = WidgetUtil.getToolkit( ).createCCombo( armourGroup,
				SWT.READ_ONLY );
		gd = new GridData( );
		gd.widthHint = 80;
		skillCombo.setLayoutData( gd );
		initNumberCombo( skillCombo, 0, 20 );

		WidgetUtil.getToolkit( ).createLabel( armourGroup, "盾牌防御：" );
		shieldCombo = WidgetUtil.getToolkit( ).createCCombo( armourGroup,
				SWT.READ_ONLY );
		gd = new GridData( );
		gd.widthHint = 80;
		shieldCombo.setLayoutData( gd );
		initNumberCombo( shieldCombo, 0, 20 );

		gd = new GridData( GridData.FILL_HORIZONTAL );
		gd.horizontalSpan = 4;
		Group moraleGroup = WidgetUtil.getToolkit( ).createGroup( patchClient,
				"战术素养" );
		moraleGroup.setLayoutData( gd );

		layout = new GridLayout( );
		layout.numColumns = 6;
		moraleGroup.setLayout( layout );

		WidgetUtil.getToolkit( ).createLabel( moraleGroup, "基础士气：" );
		moraleCombo = WidgetUtil.getToolkit( ).createCCombo( moraleGroup,
				SWT.READ_ONLY );
		gd = new GridData( );
		gd.widthHint = 80;
		moraleCombo.setLayoutData( gd );
		initNumberCombo( moraleCombo, 1, 30 );

		WidgetUtil.getToolkit( ).createLabel( moraleGroup, "纪律服从：" );
		disciplineCombo = WidgetUtil.getToolkit( ).createCCombo( moraleGroup,
				SWT.READ_ONLY );
		gd = new GridData( );
		gd.widthHint = 80;
		disciplineCombo.setLayoutData( gd );
		disciplineCombo.setItems( new String[]{
				"正常", "低纪律", "有纪律", "狂暴"
		} );
		disciplineCombo.setToolTipText( "狂暴的部队有可能不下令就突击，很NB的说，不过士兵生命值要高点" );

		WidgetUtil.getToolkit( ).createLabel( moraleGroup, "训练程度：" );
		trainingCombo = WidgetUtil.getToolkit( ).createCCombo( moraleGroup,
				SWT.READ_ONLY );
		gd = new GridData( );
		gd.widthHint = 80;
		trainingCombo.setLayoutData( gd );
		trainingCombo.setItems( new String[]{
				"没有训练", "训练良好", "训练极好"
		} );
		trainingCombo.setToolTipText( "训练决定阵型的整齐程度" );

		gd = new GridData( GridData.FILL_HORIZONTAL );
		gd.horizontalSpan = 4;
		horseGroup = WidgetUtil.getToolkit( ).createGroup( patchClient,
				"坐骑效果加成" );
		horseGroup.setLayoutData( gd );

		layout = new GridLayout( );
		layout.numColumns = 8;
		horseGroup.setLayout( layout );

		WidgetUtil.getToolkit( ).createLabel( horseGroup, "战马：" );
		horseCombo = WidgetUtil.getToolkit( ).createCCombo( horseGroup,
				SWT.READ_ONLY );
		gd = new GridData( );
		gd.widthHint = 80;
		horseCombo.setLayoutData( gd );
		initNumberCombo( horseCombo, -10, 10 );

		WidgetUtil.getToolkit( ).createLabel( horseGroup, "骆驼：" );

		camelCombo = WidgetUtil.getToolkit( ).createCCombo( horseGroup,
				SWT.READ_ONLY );
		gd = new GridData( );
		gd.widthHint = 80;
		camelCombo.setLayoutData( gd );
		initNumberCombo( camelCombo, -10, 10 );

		WidgetUtil.getToolkit( ).createLabel( horseGroup, "大象：" );
		elephantCombo = WidgetUtil.getToolkit( ).createCCombo( horseGroup,
				SWT.READ_ONLY );
		gd = new GridData( );
		gd.widthHint = 80;
		elephantCombo.setLayoutData( gd );
		initNumberCombo( elephantCombo, -10, 10 );

		WidgetUtil.getToolkit( ).createLabel( horseGroup, "战车：" );

		chariotCombo = WidgetUtil.getToolkit( ).createCCombo( horseGroup,
				SWT.READ_ONLY );
		gd = new GridData( );
		gd.widthHint = 80;
		chariotCombo.setLayoutData( gd );
		initNumberCombo( chariotCombo, -10, 10 );

		gd = new GridData( GridData.FILL_HORIZONTAL );
		gd.horizontalSpan = 4;
		Group groundGroup = WidgetUtil.getToolkit( ).createGroup( patchClient,
				"地形加成" );
		groundGroup.setLayoutData( gd );

		layout = new GridLayout( );
		layout.numColumns = 8;
		groundGroup.setLayout( layout );

		WidgetUtil.getToolkit( ).createLabel( groundGroup, "灌木：" );
		scrubCombo = WidgetUtil.getToolkit( ).createCCombo( groundGroup,
				SWT.READ_ONLY );
		gd = new GridData( );
		gd.widthHint = 80;
		scrubCombo.setLayoutData( gd );
		initNumberCombo( scrubCombo, -10, 10 );

		WidgetUtil.getToolkit( ).createLabel( groundGroup, "沙漠：" );
		sandCombo = WidgetUtil.getToolkit( ).createCCombo( groundGroup,
				SWT.READ_ONLY );
		gd = new GridData( );
		gd.widthHint = 80;
		sandCombo.setLayoutData( gd );
		initNumberCombo( sandCombo, -10, 10 );

		WidgetUtil.getToolkit( ).createLabel( groundGroup, "森林：" );
		forestCombo = WidgetUtil.getToolkit( ).createCCombo( groundGroup,
				SWT.READ_ONLY );
		gd = new GridData( );
		gd.widthHint = 80;
		forestCombo.setLayoutData( gd );
		initNumberCombo( forestCombo, -10, 10 );

		WidgetUtil.getToolkit( ).createLabel( groundGroup, "雪地：" );
		snowCombo = WidgetUtil.getToolkit( ).createCCombo( groundGroup,
				SWT.READ_ONLY );
		gd = new GridData( );
		gd.widthHint = 80;
		snowCombo.setLayoutData( gd );
		initNumberCombo( snowCombo, -10, 10 );

		gd = new GridData( GridData.FILL_HORIZONTAL );
		gd.horizontalSpan = 4;
		Group costGroup = WidgetUtil.getToolkit( ).createGroup( patchClient,
				"兵种生产" );
		costGroup.setLayoutData( gd );

		layout = new GridLayout( );
		layout.numColumns = 6;
		costGroup.setLayout( layout );

		WidgetUtil.getToolkit( ).createLabel( costGroup, "生产回合：" );
		turnsCombo = WidgetUtil.getToolkit( )
				.createCCombo( costGroup, SWT.NONE );
		gd = new GridData( );
		gd.widthHint = 80;
		turnsCombo.setLayoutData( gd );
		initNumberCombo( turnsCombo, 0, 100 );

		WidgetUtil.getToolkit( ).createLabel( costGroup, "兵种造价：" );
		costCombo = WidgetUtil.getToolkit( ).createCCombo( costGroup, SWT.NONE );
		gd = new GridData( );
		gd.widthHint = 80;
		costCombo.setLayoutData( gd );
		costCombo.addVerifyListener( numberVerifyListener );

		WidgetUtil.getToolkit( ).createLabel( costGroup, "维护成本：" );
		maintainCombo = WidgetUtil.getToolkit( ).createCCombo( costGroup,
				SWT.NONE );
		gd = new GridData( );
		gd.widthHint = 80;
		maintainCombo.setLayoutData( gd );
		maintainCombo.addVerifyListener( numberVerifyListener );

		WidgetUtil.getToolkit( ).createLabel( costGroup, "武器升级：" );
		weaponUpdateCombo = WidgetUtil.getToolkit( ).createCCombo( costGroup,
				SWT.NONE );
		gd = new GridData( );
		gd.widthHint = 80;
		weaponUpdateCombo.setLayoutData( gd );
		weaponUpdateCombo.addVerifyListener( numberVerifyListener );

		WidgetUtil.getToolkit( ).createLabel( costGroup, "防具升级：" );
		armourUpdateCombo = WidgetUtil.getToolkit( ).createCCombo( costGroup,
				SWT.NONE );
		gd = new GridData( );
		gd.widthHint = 80;
		armourUpdateCombo.setLayoutData( gd );
		armourUpdateCombo.addVerifyListener( numberVerifyListener );

		WidgetUtil.getToolkit( ).createLabel( costGroup, "自定义模式造价：" );
		battleCostCombo = WidgetUtil.getToolkit( ).createCCombo( costGroup,
				SWT.NONE );
		gd = new GridData( );
		gd.widthHint = 80;
		battleCostCombo.setLayoutData( gd );
		battleCostCombo.addVerifyListener( numberVerifyListener );

		if ( !isMemory )
		{
			gd = new GridData( GridData.FILL_HORIZONTAL );
			gd.horizontalSpan = 4;
			Composite buttonGroup = WidgetUtil.getToolkit( )
					.createComposite( patchClient );
			buttonGroup.setLayoutData( gd );

			layout = new GridLayout( );
			layout.numColumns = 2;
			buttonGroup.setLayout( layout );

			applyButton = WidgetUtil.getToolkit( ).createButton( buttonGroup,
					"应用",
					SWT.PUSH );
			gd = new GridData( );
			gd.grabExcessHorizontalSpace = true;
			gd.horizontalAlignment = SWT.END;
			applyButton.setLayoutData( gd );
			applyButton.addSelectionListener( new SelectionAdapter( ) {

				public void widgetSelected( SelectionEvent e )
				{
					applyButton.setEnabled( false );
					BakUtil.bakData( "兵种数据修改：" + soldierCombo.getText( ) );
					saveSoldier( false );
					applyButton.setEnabled( true );
				}
			} );

			restoreButton = WidgetUtil.getToolkit( ).createButton( buttonGroup,
					"还原",
					SWT.PUSH );
			gd = new GridData( );
			gd.grabExcessHorizontalSpace = true;
			restoreButton.setLayoutData( gd );
			restoreButton.addSelectionListener( new SelectionAdapter( ) {

				public void widgetSelected( SelectionEvent e )
				{
					restoreButton.setEnabled( false );
					BakUtil.restoreCurrectVersionBakFile( );
					String text = soldierCombo.getText( );
					soldierCombo.setText( "" );
					soldierCombo.setText( text );
					soldierCombo.notifyListeners( SWT.Selection, new Event( ) );
					restoreButton.setEnabled( true );
				}
			} );
		}

		if ( soldier != null )
		{
			if ( generalUnitMap.containsKey( soldier.getType( ) ) )
			{
				soldierCombo.setText( ChangeCode.toLong( (String) generalUnitMap.get( soldier.getType( ) ) ) );
			}
			else if ( soldierUnitMap.containsKey( soldier.getType( ) ) )
			{
				soldierCombo.setText( ChangeCode.toLong( (String) soldierUnitMap.get( soldier.getType( ) ) ) );
			}
			if ( soldierCombo.indexOf( soldierCombo.getText( ) ) != -1 )
			{
				patchClient.setEnabled( true );
				initSoldier( soldier );
				Event event = new Event( );
				event.type = SWT.Verify;
				event.doit = true;
				notifyEvent( event );
			}
		}
		soldierCombo.addSelectionListener( new SelectionAdapter( ) {

			public void widgetSelected( SelectionEvent e )
			{
				if ( soldierCombo.getSelectionIndex( ) != -1 )
				{
					patchClient.setEnabled( true );
					String soldierType = null;
					int index = soldierCombo.getSelectionIndex( )
							- ( soldierCombo.getItemCount( ) - soldierUnitMap.size( ) );
					if ( index > -1 )
					{
						soldierType = (String) soldierUnitMap.getKeyList( )
								.get( index );
					}
					else
					{
						soldierType = (String) generalUnitMap.getKeyList( )
								.get( soldierCombo.getSelectionIndex( ) );
					}
					if ( soldierType != null )
					{
						Unit soldier = UnitParser.getUnit( soldierType );
						initSoldier( soldier );
						Event event = new Event( );
						event.type = SWT.Verify;
						event.doit = true;
						notifyEvent( event );
						return;
					}
				}
				else
					patchClient.setEnabled( false );

				Event event = new Event( );
				event.type = SWT.Verify;
				event.doit = false;
				notifyEvent( event );
				return;
			}
		} );

		return clientContainer;
	}

	private void initSoldierCombo( )
	{
		List soldierList = new ArrayList( );
		unitMap = UnitUtil.getUnits( );
		generalUnitMap = UnitUtil.getGeneralUnits( );
		officerMap = UnitUtil.getAvailableOfficers( );
		horseMap = UnitUtil.getAvailableHorses( );
		soldierQiMap = UnitUtil.getCavalryMap( );
		soldierBuMap = UnitUtil.getInfantryMap( );
		soldierSiegeMap = UnitUtil.getSiegeMap( );
		soldierHandlerMap = UnitUtil.getHandlerMap( );
		
		initModels();
		
		for ( int i = 0; i < generalUnitMap.getKeyList( ).size( ); i++ )
		{
			soldierList.add( ChangeCode.toLong( (String) generalUnitMap.get( i ) ) );
		}
		soldierUnitMap = UnitUtil.getSoldierUnits( );
		for ( int i = 0; i < soldierUnitMap.getKeyList( ).size( ); i++ )
		{
			soldierList.add( ChangeCode.toLong( (String) soldierUnitMap.get( i ) ) );
		}
		soldierCombo.setItems( (String[]) soldierList.toArray( new String[0] ) );
	}

	public Unit saveSoldier( boolean isMemory )
	{
		if ( soldierCombo.getSelectionIndex( ) != -1 )
		{
			String soldierType = null;
			int generalIndex = soldierCombo.getSelectionIndex( )
					- ( soldierCombo.getItemCount( ) - soldierUnitMap.size( ) );
			if ( generalIndex >= 0 )
			{
				soldierType = (String) soldierUnitMap.getKeyList( )
						.get( generalIndex );
			}
			else
			{
				soldierType = (String) generalUnitMap.getKeyList( )
						.get( soldierCombo.getSelectionIndex( ) );
			}
			if ( soldierType != null )
			{
				Unit soldier = UnitParser.getUnit( soldierType );

				String[] soldierSoldier = soldier.getSoldier( );
				if ( soldierSoldier != null && soldierSoldier.length == 4 )
				{
					if ( soldierNumberCombo.indexOf( soldierNumberCombo.getText( ) ) > 0 )
					{
						soldierSoldier[1] = soldierNumberCombo.getItem( soldierNumberCombo.indexOf( soldierNumberCombo.getText( ) ) );
					}
				}
				int[] soldierHealth = soldier.getHealth( );
				if ( soldierHealth != null
						&& soldierHealth.length == 2
						&& soldierLifeCombo.indexOf( soldierLifeCombo.getText( ) ) > 0 )
				{
					soldierHealth[0] = Integer.parseInt( soldierLifeCombo.getItem( soldierLifeCombo.indexOf( soldierLifeCombo.getText( ) ) ) );
				}
				if ( heatCombo.indexOf( heatCombo.getText( ) ) > 0 )
				{
					soldier.setHeat( Integer.parseInt( heatCombo.getItem( heatCombo.indexOf( heatCombo.getText( ) ) ) ) );
				}
				if ( chargeCombo.indexOf( chargeCombo.getText( ) ) > 0 )
				{
					soldier.setChargeDist( Integer.parseInt( chargeCombo.getItem( chargeCombo.indexOf( chargeCombo.getText( ) ) ) ) );
				}

				if ( currentUnit != null )
				{
					soldier.setPrimary( currentUnit.getPrimary( ) );
				}
				String[] primary = soldier.getPrimary( );
				if ( primary != null && primary.length == 11 )
				{
					if ( attackCombo.indexOf( attackCombo.getText( ) ) > 0 )
					{
						primary[0] = attackCombo.getItem( attackCombo.indexOf( attackCombo.getText( ) ) );
					}
					if ( powerCombo.indexOf( powerCombo.getText( ) ) > 0 )
					{
						primary[1] = powerCombo.getItem( powerCombo.indexOf( powerCombo.getText( ) ) );
					}
					if ( rangeCombo.indexOf( rangeCombo.getText( ) ) > 0 )
					{
						primary[3] = rangeCombo.getItem( rangeCombo.indexOf( rangeCombo.getText( ) ) );
					}
					if ( amountCombo.indexOf( amountCombo.getText( ) ) > 0 )
					{
						primary[4] = amountCombo.getItem( amountCombo.indexOf( amountCombo.getText( ) ) );
					}
					if ( delayCombo.indexOf( delayCombo.getText( ) ) > 0 )
					{
						primary[9] = delayCombo.getItem( delayCombo.indexOf( delayCombo.getText( ) ) );
					}
					if ( killCombo.indexOf( killCombo.getText( ) ) > 0 )
					{
						primary[10] = killCombo.getItem( killCombo.indexOf( killCombo.getText( ) ) );
					}
				}

				if ( currentUnit != null )
				{
					soldier.setSecond( currentUnit.getSecond( ) );
				}
				String[] second = soldier.getSecond( );
				if ( second != null && second.length == 11 )
				{
					if ( secAttackCombo.indexOf( secAttackCombo.getText( ) ) > 0 )
					{
						second[0] = secAttackCombo.getItem( secAttackCombo.indexOf( secAttackCombo.getText( ) ) );
					}
					if ( secPowerCombo.indexOf( secPowerCombo.getText( ) ) > 0 )
					{
						second[1] = secPowerCombo.getItem( secPowerCombo.indexOf( secPowerCombo.getText( ) ) );
					}
					if ( secRangeCombo.indexOf( secRangeCombo.getText( ) ) > 0 )
					{
						second[3] = secRangeCombo.getItem( secRangeCombo.indexOf( secRangeCombo.getText( ) ) );
					}
					if ( secAmountCombo.indexOf( secAmountCombo.getText( ) ) > 0 )
					{
						second[4] = secAmountCombo.getItem( secAmountCombo.indexOf( secAmountCombo.getText( ) ) );
					}
					if ( secDelayCombo.indexOf( secDelayCombo.getText( ) ) > 0 )
					{
						second[9] = secDelayCombo.getItem( secDelayCombo.indexOf( secDelayCombo.getText( ) ) );
					}
					if ( secKillCombo.indexOf( secKillCombo.getText( ) ) > 0 )
					{
						second[10] = secKillCombo.getItem( secKillCombo.indexOf( secKillCombo.getText( ) ) );
					}
				}

				String[] armour = soldier.getPrimaryArmour( );
				if ( armour != null && armour.length == 4 )
				{
					if ( armourCombo.indexOf( armourCombo.getText( ) ) > 0 )
					{
						armour[0] = armourCombo.getItem( armourCombo.indexOf( armourCombo.getText( ) ) );
					}
					if ( skillCombo.indexOf( skillCombo.getText( ) ) > 0 )
					{
						armour[1] = skillCombo.getItem( skillCombo.indexOf( skillCombo.getText( ) ) );
					}
					if ( shieldCombo.indexOf( shieldCombo.getText( ) ) > 0 )
					{
						armour[2] = shieldCombo.getItem( shieldCombo.indexOf( shieldCombo.getText( ) ) );
					}
				}
				String[] mental = soldier.getMental( );
				if ( mental != null && mental.length == 3 )
				{
					if ( moraleCombo.indexOf( moraleCombo.getText( ) ) > 0 )
					{
						mental[0] = moraleCombo.getItem( moraleCombo.indexOf( moraleCombo.getText( ) ) );
					}
					int index = disciplineCombo.indexOf( disciplineCombo.getText( ) );
					if ( index != -1 )
					{
						if ( index == 0 )
							mental[1] = "normal";
						else if ( index == 1 )
							mental[1] = "low";
						else if ( index == 2 )
							mental[1] = "disciplined";
						else if ( index == 3 )
							mental[1] = "berserker";
					}
					index = trainingCombo.indexOf( trainingCombo.getText( ) );
					if ( index != -1 )
					{
						if ( index == 0 )
							mental[2] = "untrained";
						else if ( index == 1 )
							mental[2] = "trained";
						else if ( index == 2 )
							mental[2] = "highly_trained";
					}
				}
				List effects = soldier.getMountEffect( );
				if ( effects != null )
				{
					if ( elephantCombo.indexOf( elephantCombo.getText( ) ) > 0 )
					{
						int elephant = Integer.parseInt( elephantCombo.getItem( elephantCombo.indexOf( elephantCombo.getText( ) ) ) );
						for ( int i = 0; i < effects.size( ); i++ )
						{
							String effect = (String) effects.get( i );
							if ( effect.indexOf( "elephant" ) != -1 )
								effects.remove( effect );
						}
						if ( elephant < 0 )
							effects.add( "elephant " + elephant );
						else if ( elephant > 0 )
							effects.add( "elephant +" + elephant );
					}
					if ( horseCombo.indexOf( horseCombo.getText( ) ) > 0 )
					{
						int horse = Integer.parseInt( horseCombo.getItem( horseCombo.indexOf( horseCombo.getText( ) ) ) );
						for ( int i = 0; i < effects.size( ); i++ )
						{
							String effect = (String) effects.get( i );
							if ( effect.indexOf( "horse" ) != -1 )
								effects.remove( effect );
						}
						if ( horse < 0 )
							effects.add( "horse " + horse );
						else if ( horse > 0 )
							effects.add( "horse +" + horse );
					}
					if ( chariotCombo.indexOf( chariotCombo.getText( ) ) > 0 )
					{
						int chariot = Integer.parseInt( chariotCombo.getItem( chariotCombo.indexOf( chariotCombo.getText( ) ) ) );
						for ( int i = 0; i < effects.size( ); i++ )
						{
							String effect = (String) effects.get( i );
							if ( effect.indexOf( "chariot" ) != -1 )
								effects.remove( effect );
						}
						if ( chariot < 0 )
							effects.add( "chariot " + chariot );
						else if ( chariot > 0 )
							effects.add( "chariot +" + chariot );
					}
					if ( camelCombo.indexOf( camelCombo.getText( ) ) > 0 )
					{
						int camel = Integer.parseInt( camelCombo.getItem( camelCombo.indexOf( camelCombo.getText( ) ) ) );
						for ( int i = 0; i < effects.size( ); i++ )
						{
							String effect = (String) effects.get( i );
							if ( effect.indexOf( "camel" ) != -1 )
								effects.remove( effect );
						}
						if ( camel < 0 )
							effects.add( "camel " + camel );
						else if ( camel > 0 )
							effects.add( "camel +" + camel );
					}
				}

				int[] grounds = soldier.getGround( );
				if ( grounds != null && grounds.length == 4 )
				{
					if ( scrubCombo.indexOf( scrubCombo.getText( ) ) > 0 )
					{
						grounds[0] = Integer.parseInt( scrubCombo.getItem( scrubCombo.indexOf( scrubCombo.getText( ) ) ) );
					}
					if ( sandCombo.indexOf( sandCombo.getText( ) ) > 0 )
					{
						grounds[1] = Integer.parseInt( sandCombo.getItem( sandCombo.indexOf( sandCombo.getText( ) ) ) );
					}
					if ( forestCombo.indexOf( forestCombo.getText( ) ) > 0 )
					{
						grounds[2] = Integer.parseInt( forestCombo.getItem( forestCombo.indexOf( forestCombo.getText( ) ) ) );
					}
					if ( snowCombo.indexOf( snowCombo.getText( ) ) > 0 )
					{
						grounds[3] = Integer.parseInt( snowCombo.getItem( snowCombo.indexOf( snowCombo.getText( ) ) ) );
					}
				}

				List attributes = soldier.getAttributes( );
				Control[] children = attributesGroup.getChildren( );
				if ( children != null && attributes != null )
				{
					for ( int i = 0; i < children.length; i++ )
					{
						if ( children[i] instanceof Button
								&& children[i].getData( ) != null )
						{
							Button button = ( (Button) children[i] );
							Object data = button.getData( );
							if ( button.getSelection( )
									&& !attributes.contains( data ) )
								attributes.add( data );
							else if ( !button.getSelection( )
									&& attributes.contains( data ) )
								attributes.remove( data );
						}
					}
				}

				if ( currentUnit != null )
				{
					soldier.setPrimaryAttr( currentUnit.getPrimaryAttr( ) );
				}
				List primaryAttrs = soldier.getPrimaryAttr( );
				if ( primaryAttrs != null )
				{
					if ( apBtn.getSelection( ) && !primaryAttrs.contains( "ap" ) )
						primaryAttrs.add( "ap" );
					else if ( !apBtn.getSelection( )
							&& primaryAttrs.contains( "ap" ) )
						primaryAttrs.remove( "ap" );
					if ( bpBtn.getSelection( ) && !primaryAttrs.contains( "bp" ) )
						primaryAttrs.add( "bp" );
					else if ( !bpBtn.getSelection( )
							&& primaryAttrs.contains( "bp" ) )
						primaryAttrs.remove( "bp" );
					if ( areaBtn.getSelection( )
							&& !primaryAttrs.contains( "area" ) )
						primaryAttrs.add( "area" );
					else if ( !areaBtn.getSelection( )
							&& primaryAttrs.contains( "area" ) )
						primaryAttrs.remove( "area" );
					if ( spearBtn.getSelection( )
							&& !primaryAttrs.contains( "spear" ) )
					{
						primaryAttrs.add( "spear" );
					}
					else if ( !spearBtn.getSelection( )
							&& primaryAttrs.contains( "spear" ) )
					{
						primaryAttrs.remove( "spear" );
					}
					for ( int i = 0; i < primaryAttrs.size( ); i++ )
					{
						String attribute = (String) primaryAttrs.get( i );
						if ( attribute.indexOf( "spear_bonus_" ) != -1 )
						{
							primaryAttrs.remove( attribute );
						}
					}
					if ( spearBtn.getSelection( ) )
					{
						if ( bonusCombo.indexOf( bonusCombo.getText( ) ) > 0 )
						{
							String bonus = bonusCombo.getItem( bonusCombo.indexOf( bonusCombo.getText( ) ) );
							primaryAttrs.add( "spear_bonus_" + bonus );
						}
					}
				}

				if ( currentUnit != null )
				{
					soldier.setSecondAttr( currentUnit.getSecondAttr( ) );
				}
				List secondAttrs = soldier.getSecondAttr( );
				if ( secondAttrs != null )
				{
					if ( secApBtn.getSelection( )
							&& !secondAttrs.contains( "ap" ) )
						secondAttrs.add( "ap" );
					else if ( !secApBtn.getSelection( )
							&& secondAttrs.contains( "ap" ) )
						secondAttrs.remove( "ap" );
					if ( secBpBtn.getSelection( )
							&& !secondAttrs.contains( "bp" ) )
						secondAttrs.add( "bp" );
					else if ( !secBpBtn.getSelection( )
							&& secondAttrs.contains( "bp" ) )
						secondAttrs.remove( "bp" );
					if ( secAreaBtn.getSelection( )
							&& !secondAttrs.contains( "area" ) )
						secondAttrs.add( "area" );
					else if ( !secAreaBtn.getSelection( )
							&& secondAttrs.contains( "area" ) )
						secondAttrs.remove( "area" );
					if ( secSpearBtn.getSelection( )
							&& !secondAttrs.contains( "spear" ) )
					{
						secondAttrs.add( "spear" );
					}
					else if ( !secSpearBtn.getSelection( )
							&& secondAttrs.contains( "spear" ) )
					{
						secondAttrs.remove( "spear" );
					}
					for ( int i = 0; i < secondAttrs.size( ); i++ )
					{
						String attribute = (String) secondAttrs.get( i );
						if ( attribute.indexOf( "spear_bonus_" ) != -1 )
						{
							secondAttrs.remove( attribute );
						}
					}
					if ( secSpearBtn.getSelection( ) )
					{
						if ( secBonusCombo.indexOf( secBonusCombo.getText( ) ) > 0 )
						{
							String bonus = secBonusCombo.getItem( secBonusCombo.indexOf( secBonusCombo.getText( ) ) );
							secondAttrs.add( "spear_bonus_" + bonus );
						}
					}
				}

				String[] costs = soldier.getCost( );
				if ( costs != null && costs.length == 6 )
				{
					try
					{
						if ( turnsCombo.getText( ).trim( ).length( ) > 0 )
						{
							costs[0] = ""
									+ NumberFormat.getInstance( )
											.parse( ( turnsCombo.getText( ).trim( ) ) )
											.floatValue( );
						}
						if ( costCombo.getText( ).trim( ).length( ) > 0 )
						{
							costs[1] = ""
									+ NumberFormat.getInstance( )
											.parse( ( costCombo.getText( ).trim( ) ) )
											.floatValue( );
						}
						if ( maintainCombo.getText( ).trim( ).length( ) > 0 )
						{
							costs[2] = ""
									+ NumberFormat.getInstance( )
											.parse( ( maintainCombo.getText( ).trim( ) ) )
											.floatValue( );
						}
						if ( weaponUpdateCombo.getText( ).trim( ).length( ) > 0 )
						{
							costs[3] = ""
									+ NumberFormat.getInstance( )
											.parse( ( weaponUpdateCombo.getText( ).trim( ) ) )
											.floatValue( );
						}
						if ( armourUpdateCombo.getText( ).trim( ).length( ) > 0 )
						{
							costs[4] = ""
									+ NumberFormat.getInstance( )
											.parse( ( armourUpdateCombo.getText( ).trim( ) ) )
											.floatValue( );
						}
						if ( battleCostCombo.getText( ).trim( ).length( ) > 0 )
						{
							costs[5] = ""
									+ NumberFormat.getInstance( )
											.parse( ( battleCostCombo.getText( ).trim( ) ) )
											.floatValue( );
						}
					}
					catch ( ParseException e )
					{
						e.printStackTrace( );
					}
				}

				List officers = soldier.getOfficers( );
				if ( officers != null )
				{
					if ( officer1Combo.getSelectionIndex( ) > 0
							&& officer1Combo.isEnabled( ) )
					{
						Object officerCode = officerMap.getKeyList( )
								.get( officer1Combo.getSelectionIndex( ) - 1 );
						if ( officers.size( ) > 0 )
						{
							officers.remove( 0 );
						}
						officers.add( 0, officerCode );
					}
					if ( officer2Combo.getSelectionIndex( ) > 0
							&& officer2Combo.isEnabled( ) )
					{
						Object officerCode = officerMap.getKeyList( )
								.get( officer2Combo.getSelectionIndex( ) - 1 );
						if ( officers.size( ) > 1 )
						{
							officers.remove( 1 );
						}
						if ( officers.size( ) > 0 )
						{
							officers.add( 1, officerCode );
						}
					}
					if ( officer3Combo.getSelectionIndex( ) > 0
							&& officer3Combo.isEnabled( ) )
					{
						Object officerCode = officerMap.getKeyList( )
								.get( officer3Combo.getSelectionIndex( ) - 1 );
						if ( officers.size( ) > 2 )
						{
							officers.remove( 2 );
						}
						if ( officers.size( ) > 1 )
						{
							officers.add( 2, officerCode );
						}
					}
				}
				if ( soldierModelCombo.getSelectionIndex( ) > 0
						&& soldierModelCombo.isEnabled( ) )
				{
					if ( "infantry".equals( soldier.getCategory( ) ) )
					{
						soldier.getSoldier( )[0] = (String) soldierBuMap.get( soldierModelCombo.getSelectionIndex( ) - 1 );
					}
					else if ( "cavalry".equals( soldier.getCategory( ) ) )
					{
						soldier.getSoldier( )[0] = (String) soldierQiMap.get( soldierModelCombo.getSelectionIndex( ) - 1 );
					}
					else if ( "siege".equals( soldier.getCategory( ) ) )
					{
						soldier.getSoldier( )[0] = (String) soldierSiegeMap.get( soldierModelCombo.getSelectionIndex( ) - 1 );
					}
					else if ( "handler".equals( soldier.getCategory( ) ) )
					{
						soldier.getSoldier( )[0] = (String) soldierHandlerMap.get( soldierModelCombo.getSelectionIndex( ) - 1 );
					}
				}
				if ( mountCombo.getSelectionIndex( ) > 0
						&& mountCombo.isEnabled( ) )
				{
					String mountModel = (String) horseMap.getKeyList( )
							.get( mountCombo.getSelectionIndex( ) - 1 );
					soldier.setMount( (String) UnitUtil.getMountModelToTypeMap( )
							.get( mountModel ) );
				}
				if ( !isMemory )
				{
					// FileUtil.bakFile(FileConstants.unitFile,
					// ".unit.patch.bak")
					// .deleteOnExit();
					UnitParser.saveSoldier( soldier );
					if ( nameText != null
							&& nameText.getText( ).trim( ).length( ) > 0 )
					{
						UnitUtil.setUnitName( soldier.getType( ),
								nameText.getText( ).trim( ) );
						MapUtil.initMap( );
						refresh( );
						if ( soldierType != null )
						{
							String name = (String) UnitUtil.getGeneralUnits( )
									.get( soldierType );
							if ( name == null )
								name = (String) UnitUtil.getSoldierUnits( )
										.get( soldierType );
							if ( name != null )
							{
								String newName = ChangeCode.toLong( name );
								soldierCombo.setText( newName );
							}
							else
							{
								soldierCombo.clearSelection( );
							}
						}
					}
				}
				return soldier;
			}
		}
		return null;
	}

	private void initSoldier( Unit soldier )
	{
		if ( nameText != null )
		{
			nameText.setText( "" );
		}
		String[] soldierSoldier = soldier.getSoldier( );
		soldierNumberCombo.setText( "" );
		if ( soldierSoldier != null && soldierSoldier.length == 4 )
		{
			if ( soldierSoldier[1] != null )
				soldierNumberCombo.setText( soldierSoldier[1] );
		}
		int[] soldierHealth = soldier.getHealth( );
		soldierLifeCombo.setText( "" );
		if ( soldierHealth != null
				&& soldierHealth.length == 2
				&& soldierHealth[0] > 0 )
			soldierLifeCombo.setText( "" + soldierHealth[0] );

		heatCombo.setText( "" + soldier.getHeat( ) );
		chargeCombo.setText( "" + soldier.getChargeDist( ) );

		initSoliderPriAndSec( soldier );

		String[] armour = soldier.getPrimaryArmour( );
		armourCombo.setText( "" );
		skillCombo.setText( "" );
		shieldCombo.setText( "" );
		if ( armour != null && armour.length == 4 )
		{
			if ( armour[0] != null )
				armourCombo.setText( armour[0] );
			if ( armour[1] != null )
				skillCombo.setText( armour[1] );
			if ( armour[2] != null )
				shieldCombo.setText( armour[2] );
		}
		String[] mental = soldier.getMental( );
		moraleCombo.setText( "" );
		disciplineCombo.select( 0 );
		trainingCombo.select( 0 );
		if ( mental != null && mental.length == 3 )
		{
			if ( mental[0] != null )
			{
				moraleCombo.setText( mental[0] );
			}
			if ( mental[1] != null )
			{
				if ( "normal".equals( mental[1] ) )
					disciplineCombo.select( 0 );
				else if ( "low".equals( mental[1] ) )
					disciplineCombo.select( 1 );
				else if ( "disciplined".equals( mental[1] ) )
					disciplineCombo.select( 2 );
				else if ( "berserker".equals( mental[1] ) )
					disciplineCombo.select( 3 );
			}
			if ( mental[2] != null )
			{
				if ( "untrained".equals( mental[2] ) )
					trainingCombo.select( 0 );
				else if ( "trained".equals( mental[2] ) )
					trainingCombo.select( 1 );
				else if ( "highly_trained".equals( mental[2] ) )
					trainingCombo.select( 2 );
			}
		}
		List effects = soldier.getMountEffect( );
		elephantCombo.setText( "" );
		horseCombo.setText( "" );
		chariotCombo.setText( "" );
		camelCombo.setText( "" );
		if ( effects != null && !effects.isEmpty( ) )
		{
			for ( int i = 0; i < effects.size( ); i++ )
			{
				String effect = (String) effects.get( i );
				if ( effect.indexOf( "elephant" ) != -1 )
				{
					elephantCombo.setText( effect.replaceAll( "elephant", "" )
							.replaceAll( "\\+", "" )
							.trim( ) );
				}
				else if ( effect.indexOf( "horse" ) != -1 )
				{
					horseCombo.setText( effect.replaceAll( "horse", "" )
							.replaceAll( "\\+", "" )
							.trim( ) );
				}
				else if ( effect.indexOf( "chariot" ) != -1 )
				{
					chariotCombo.setText( effect.replaceAll( "chariot", "" )
							.replaceAll( "\\+", "" )
							.trim( ) );
				}
				else if ( effect.indexOf( "camel" ) != -1 )
				{
					camelCombo.setText( effect.replaceAll( "camel", "" )
							.replaceAll( "\\+", "" )
							.trim( ) );
				}
			}
		}

		int[] grounds = soldier.getGround( );
		scrubCombo.setText( "" );
		sandCombo.setText( "" );
		forestCombo.setText( "" );
		snowCombo.setText( "" );
		if ( grounds != null && grounds.length == 4 )
		{
			scrubCombo.setText( "" + grounds[0] );
			sandCombo.setText( "" + grounds[1] );
			forestCombo.setText( "" + grounds[2] );
			snowCombo.setText( "" + grounds[3] );
		}

		List attributes = soldier.getAttributes( );
		Control[] children = attributesGroup.getChildren( );
		if ( children != null )
		{
			for ( int i = 0; i < children.length; i++ )
			{
				if ( children[i] instanceof Button )
					( (Button) children[i] ).setSelection( false );
			}
			if ( attributes != null && !attributes.isEmpty( ) )
			{
				for ( int i = 0; i < children.length; i++ )
				{
					Object data = children[i].getData( );
					if ( attributes.contains( data ) )
					{
						if ( children[i] instanceof Button )
							( (Button) children[i] ).setSelection( true );
					}
				}
			}
		}

		checkHideStatus( );

		String[] costs = soldier.getCost( );
		turnsCombo.setText( "" );
		costCombo.setText( "" );
		maintainCombo.setText( "" );
		weaponUpdateCombo.setText( "" );
		armourUpdateCombo.setText( "" );
		battleCostCombo.setText( "" );
		if ( costs != null && costs.length == 6 )
		{
			turnsCombo.setText( "" + costs[0] );
			costCombo.setText( "" + costs[1] );
			maintainCombo.setText( "" + costs[2] );
			weaponUpdateCombo.setText( "" + costs[3] );
			armourUpdateCombo.setText( "" + costs[4] );
			battleCostCombo.setText( "" + costs[5] );
		}

		List officers = soldier.getOfficers( );
		officer1Combo.setText( "" );
		officer2Combo.setText( "" );
		officer3Combo.setText( "" );
		if ( officers != null && !officers.isEmpty( ) )
		{
			if ( officers.size( ) > 0 )
			{
				String officer = (String) officers.get( 0 );
				if ( officerMap.containsKey( officer ) )
					officer1Combo.setText( (String) officerMap.get( officer ) );
			}
			if ( officers.size( ) > 1 )
			{
				String officer = (String) officers.get( 1 );
				if ( officerMap.containsKey( officer ) )
					officer2Combo.setText( (String) officerMap.get( officer ) );
			}
			if ( officers.size( ) > 2 )
			{
				String officer = (String) officers.get( 2 );
				if ( officerMap.containsKey( officer ) )
					officer3Combo.setText( (String) officerMap.get( officer ) );
			}
		}

		mountCombo.setText( "" );
		String mount = soldier.getMount( );
		if ( mount != null )
		{
			String mountModel = (String) UnitUtil.getMountTypeToModelMap( )
					.get( mount );
			if ( mountModel != null && horseMap.containsKey( mountModel ) )
			{
				mountCombo.setText( (String) horseMap.get( mountModel ) );
			}
		}

		if ( mount != null
				&& mount.toLowerCase( ).indexOf( "horse" ) == -1
				&& mount.toLowerCase( ).indexOf( "ma" ) == -1 )
		{
			officer1Combo.setEnabled( false );
			officer2Combo.setEnabled( false );
			officer3Combo.setEnabled( false );
			mountCombo.setEnabled( false );
		}
		else
		{
			officer1Combo.setEnabled( true );
			officer2Combo.setEnabled( true );
			officer3Combo.setEnabled( true );
			mountCombo.setEnabled( mount != null );
		}

		if ( ( "infantry".equals( soldier.getCategory( ) ) ) )
		{
			soldierModelCombo.removeAll( );
			soldierModelCombo.setItems( this.soldierBus );
			soldierModelCombo.add( "", 0 );

			int index = soldierBuMap.getKeyList( ).indexOf( soldier.getType( ) );
			if ( index != -1 )
				soldierModelCombo.select( index + 1 );
			else
				soldierModelCombo.deselectAll( );

			soldierModelCombo.setEnabled( true );
		}
		else if ( ( "cavalry".equals( soldier.getCategory( ) ) )
				&& mountCombo.isEnabled( ) )
		{
			soldierModelCombo.removeAll( );
			soldierModelCombo.setItems( this.soldierQis );
			soldierModelCombo.add( "", 0 );

			int index = soldierQiMap.getKeyList( ).indexOf( soldier.getType( ) );
			if ( index != -1 )
				soldierModelCombo.select( index + 1 );
			else
				soldierModelCombo.deselectAll( );

			soldierModelCombo.setEnabled( true );
		}
		else if ( ( "siege".equals( soldier.getCategory( ) ) ) )
		{
			soldierModelCombo.removeAll( );
			soldierModelCombo.setItems( this.soldierSieges );
			soldierModelCombo.add( "", 0 );

			int index = soldierSiegeMap.getKeyList( )
					.indexOf( soldier.getType( ) );
			if ( index != -1 )
				soldierModelCombo.select( index + 1 );
			else
				soldierModelCombo.deselectAll( );

			soldierModelCombo.setEnabled( true );
		}
		else if ( ( "handler".equals( soldier.getCategory( ) ) ) )
		{
			soldierModelCombo.removeAll( );
			soldierModelCombo.setItems( this.soldierHandlers );
			soldierModelCombo.add( "", 0 );

			int index = soldierHandlerMap.getKeyList( )
					.indexOf( soldier.getType( ) );
			if ( index != -1 )
				soldierModelCombo.select( index + 1 );
			else
				soldierModelCombo.deselectAll( );

			soldierModelCombo.setEnabled( true );
		}
		else
		{
			soldierModelCombo.removeAll( );
			soldierModelCombo.setEnabled( false );
		}

		Control[] horseChildren = horseGroup.getChildren( );
		for ( int i = 0; i < horseChildren.length; i++ )
		{
			horseChildren[i].setEnabled( "cavalry".equals( soldier.getCategory( ) ) );
		}

	}

	private void initSoliderPriAndSec( Unit soldier )
	{
		String[] primary = soldier.getPrimary( );
		attackCombo.setText( "" );
		powerCombo.setText( "" );
		rangeCombo.setText( "" );
		amountCombo.setText( "" );
		delayCombo.setText( "" );
		killCombo.setText( "" );
		if ( primary != null && primary.length == 11 )
		{
			if ( primary[0] != null )
				attackCombo.setText( primary[0] );
			if ( primary[1] != null )
				powerCombo.setText( primary[1] );
			if ( primary[3] != null )
				rangeCombo.setText( primary[3] );
			if ( primary[4] != null )
				amountCombo.setText( primary[4] );
			if ( primary[9] != null )
				delayCombo.setText( primary[9] );
			if ( primary[10] != null )
				killCombo.setText( primary[10] );

			if ( "no".equals( primary[5] ) )
			{
				rangeCombo.setEnabled( false );
				amountCombo.setEnabled( false );
				attackCombo.setEnabled( false );
				powerCombo.setEnabled( false );
				delayCombo.setEnabled( false );
				killCombo.setEnabled( false );

				apBtn.setEnabled( false );
				bpBtn.setEnabled( false );
				areaBtn.setEnabled( false );
				spearBtn.setEnabled( false );
				bonusCombo.setEnabled( false );
				spearBtn.setEnabled( false );
			}
			else
			{
				rangeCombo.setEnabled( true );
				amountCombo.setEnabled( true );
				attackCombo.setEnabled( true );
				powerCombo.setEnabled( true );
				delayCombo.setEnabled( true );
				killCombo.setEnabled( true );

				if ( "melee".equals( primary[5] ) )
				{
					rangeCombo.setEnabled( false );
					amountCombo.setEnabled( false );
				}

				apBtn.setEnabled( true );
				bpBtn.setEnabled( true );
				areaBtn.setEnabled( true );
				spearBtn.setEnabled( true );
				bonusCombo.setEnabled( true );
				spearBtn.setEnabled( true );
			}
		}

		String[] second = soldier.getSecond( );
		secAttackCombo.setText( "" );
		secPowerCombo.setText( "" );
		secRangeCombo.setText( "" );
		secAmountCombo.setText( "" );
		secDelayCombo.setText( "" );

		if ( "siege".equals( soldier.getCategory( ) ) )
		{
			secRangeCombo.removeAll( );
			initNumberCombo( secRangeCombo, 0, 400, 10 );
			secAttackCombo.removeAll( );
			initNumberCombo( secAttackCombo, 0, 100 );
		}
		else
		{
			secRangeCombo.removeAll( );
			initNumberCombo( secRangeCombo, 0, 200, 10 );
			secAttackCombo.removeAll( );
			initNumberCombo( secAttackCombo, 0, 50 );
		}

		if ( second != null && second.length == 11 )
		{
			if ( second[0] != null )
				secAttackCombo.setText( second[0] );
			if ( second[1] != null )
				secPowerCombo.setText( second[1] );
			if ( second[3] != null )
				secRangeCombo.setText( second[3] );
			if ( second[4] != null )
				secAmountCombo.setText( second[4] );
			if ( second[9] != null )
				secDelayCombo.setText( second[9] );
			if ( second[10] != null )
				secKillCombo.setText( second[10] );

			if ( "no".equals( second[5] ) )
			{
				secAttackCombo.setEnabled( false );
				secPowerCombo.setEnabled( false );
				secRangeCombo.setEnabled( false );
				secAmountCombo.setEnabled( false );
				secDelayCombo.setEnabled( false );
				secKillCombo.setEnabled( false );

				secApBtn.setEnabled( false );
				secBpBtn.setEnabled( false );
				secAreaBtn.setEnabled( false );
				secSpearBtn.setEnabled( false );
				secBonusCombo.setEnabled( false );
				secSpearBtn.setEnabled( false );
			}
			else
			{
				secAttackCombo.setEnabled( true );
				secPowerCombo.setEnabled( true );
				secRangeCombo.setEnabled( true );
				secAmountCombo.setEnabled( true );
				secDelayCombo.setEnabled( true );
				secKillCombo.setEnabled( true );

				if ( "melee".equals( second[5] ) )
				{
					secRangeCombo.setEnabled( false );
					secAmountCombo.setEnabled( false );
				}

				secApBtn.setEnabled( true );
				secBpBtn.setEnabled( true );
				secAreaBtn.setEnabled( true );
				secSpearBtn.setEnabled( true );
				secBonusCombo.setEnabled( true );
				secSpearBtn.setEnabled( true );
			}
		}

		List primaryAttrs = soldier.getPrimaryAttr( );
		apBtn.setSelection( false );
		bpBtn.setSelection( false );
		areaBtn.setSelection( false );
		spearBtn.setSelection( false );
		bonusCombo.setText( "" );
		bonusCombo.setEnabled( false );
		spearBtn.setEnabled( spearBtn.getEnabled( )
				&& "infantry".equals( soldier.getCategory( ) ) );

		if ( primaryAttrs != null && !primaryAttrs.isEmpty( ) )
		{
			apBtn.setSelection( primaryAttrs.contains( "ap" ) );
			bpBtn.setSelection( primaryAttrs.contains( "bp" ) );
			areaBtn.setSelection( primaryAttrs.contains( "area" ) );
			spearBtn.setSelection( primaryAttrs.contains( "spear" ) );
			if ( spearBtn.getSelection( ) )
			{
				for ( int i = 0; i < primaryAttrs.size( ); i++ )
				{
					String attribute = (String) primaryAttrs.get( i );
					if ( attribute.indexOf( "spear_bonus_" ) != -1 )
					{
						bonusCombo.setText( attribute.replaceAll( "spear_bonus_",
								"" )
								.trim( ) );
					}
				}
			}
			bonusCombo.setEnabled( spearBtn.getSelection( )
					&& spearBtn.getEnabled( ) );
		}

		List secondAttrs = soldier.getSecondAttr( );
		secApBtn.setSelection( false );
		secBpBtn.setSelection( false );
		secAreaBtn.setSelection( false );
		secSpearBtn.setSelection( false );
		secBonusCombo.setText( "" );
		secBonusCombo.setEnabled( false );
		secSpearBtn.setEnabled( secSpearBtn.getEnabled( )
				&& "infantry".equals( soldier.getCategory( ) ) );
		if ( secondAttrs != null && !secondAttrs.isEmpty( ) )
		{
			secApBtn.setSelection( secondAttrs.contains( "ap" ) );
			secBpBtn.setSelection( secondAttrs.contains( "bp" ) );
			secAreaBtn.setSelection( secondAttrs.contains( "area" ) );
			secSpearBtn.setSelection( secondAttrs.contains( "spear" ) );
			if ( secSpearBtn.getSelection( ) )
			{
				for ( int i = 0; i < secondAttrs.size( ); i++ )
				{
					String attribute = (String) secondAttrs.get( i );
					if ( attribute.indexOf( "spear_bonus_" ) != -1 )
					{
						secBonusCombo.setText( attribute.replaceAll( "spear_bonus_",
								"" )
								.trim( ) );
					}
				}
			}
			secBonusCombo.setEnabled( secSpearBtn.getSelection( )
					&& secSpearBtn.getEnabled( ) );
		}

	}

	private void initNumberCombo( CCombo combo, int min, int max )
	{
		for ( int i = min; i <= max; i++ )
		{
			combo.add( "" + i );
		}
		combo.add( "", 0 );
		combo.addVerifyListener( numberVerifyListener );
	}

	private void initNumberCombo( CCombo combo, int min, int max, int step )
	{
		for ( int i = min; i <= max; i += step )
		{
			combo.add( "" + i );
		}
		combo.add( "", 0 );
		combo.addVerifyListener( numberVerifyListener );
	}

	private void checkHideStatus( )
	{
		if ( hideForestBtn.getSelection( )
				&& hideTreeBtn.getSelection( )
				&& hideGrassBtn.getSelection( ) )
		{
			hideAnyWhereBtn.setSelection( true );
		}
		if ( hideAnyWhereBtn.getSelection( ) )
		{
			hideForestBtn.setSelection( false );
			hideGrassBtn.setSelection( false );
			hideTreeBtn.setSelection( false );
		}
		hideForestBtn.setEnabled( !hideAnyWhereBtn.getSelection( ) );
		hideGrassBtn.setEnabled( !hideAnyWhereBtn.getSelection( ) );
		hideTreeBtn.setEnabled( !hideAnyWhereBtn.getSelection( ) );
	}

	List listeners = new ArrayList( );

	public void addListener( Listener listener )
	{
		if ( !listeners.contains( listener ) )
		{
			listeners.add( listener );
		}
	}

	public void removeListener( VerifyListener listener )
	{
		if ( listeners.contains( listener ) )
		{
			listeners.remove( listener );
		}
	}

	private void notifyEvent( Event event )
	{
		for ( int i = 0; i < listeners.size( ); i++ )
		{
			( (Listener) listeners.get( i ) ).handleEvent( event );
		}
	}

	private Unit soldier = null;
	private CCombo killCombo;
	private CCombo secKillCombo;

	private SortMap soldierUnitMap;
	private Group horseGroup;
	private Text nameText;
	private Label nameLabel;
	private String[] officers;
	private String[] horses;
	private String[] soldierQis;
	private String[] soldierBus;
	private String[] soldierSieges;
	private String[] soldierHandlers;

	public void setSoldier( Unit soldier )
	{
		this.soldier = soldier;
	}

	public void refresh( )
	{
		if ( soldierCombo != null )
		{
			String soldierType = null;
			int selectIndex = soldierCombo.getSelectionIndex( );
			int generalIndex = soldierCombo.getSelectionIndex( )
					- ( soldierCombo.getItemCount( ) - soldierUnitMap.size( ) );
			if ( generalIndex >= 0 )
			{
				soldierType = (String) soldierUnitMap.getKeyList( )
						.get( generalIndex );
			}
			else if ( selectIndex >= 0 )
			{
				soldierType = (String) generalUnitMap.getKeyList( )
						.get( selectIndex );
			}

			generalUnitMap = UnitUtil.getGeneralUnits( );
			soldierUnitMap = UnitUtil.getSoldierUnits( );
			officerMap = UnitUtil.getAvailableOfficers( );
			horseMap = UnitUtil.getAvailableHorses( );

			initSoldierCombo( );

			officer1Combo.removeAll( );
			officer2Combo.removeAll( );
			officer3Combo.removeAll( );
			horseCombo.removeAll( );
			soldierModelCombo.removeAll( );

			officers = new String[officerMap.size( )];
			for ( int i = 0; i < officers.length; i++ )
			{
				officers[i] = (String) officerMap.get( i );
			}

			horses = new String[horseMap.size( )];
			for ( int i = 0; i < horses.length; i++ )
			{
				horses[i] = (String) horseMap.get( i );
			}

			officer1Combo.setItems( officers );
			officer1Combo.add( "", 0 );
			officer2Combo.setItems( officers );
			officer2Combo.add( "", 0 );
			officer3Combo.setItems( officers );
			officer3Combo.add( "", 0 );

			mountCombo.setItems( horses );
			mountCombo.add( "", 0 );

			if ( soldierType != null )
			{
				if ( generalUnitMap.containsKey( soldierType ) )
				{
					soldierCombo.setText( ChangeCode.toLong( (String) generalUnitMap.get( soldierType ) ) );
				}
				else if ( soldierUnitMap.containsKey( soldierType ) )
				{
					soldierCombo.setText( ChangeCode.toLong( (String) soldierUnitMap.get( soldierType ) ) );
				}
				soldierCombo.notifyListeners( SWT.Selection, new Event( ) );
			}
		}
	}
}
