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
								if(typeCombo.getSelectionIndex( ) == 0){
									pattern = Pattern.compile( PATTERN_CONSTRUCTION );
								}
								else{
									pattern = Pattern.compile( PATTERN_COST_BUILDING );
								}
								Matcher matcher = pattern.matcher( line );
								if ( matcher.find( ) )
								{
									String string = null;
									if(typeCombo.getSelectionIndex( ) == 0){
										string = line.trim( ).split( ";" )[0].substring( "construction".length( ) ); 
									}
									else{
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
									if(typeCombo.getSelectionIndex( ) == 0){
										printer.println( "            construction  "+number );
									}
									else{
										printer.println( "            cost  "+number );
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
}
