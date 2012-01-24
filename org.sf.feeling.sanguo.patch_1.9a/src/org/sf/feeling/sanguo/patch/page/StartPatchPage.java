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
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.sf.feeling.sanguo.patch.dialog.PositionDialog;
import org.sf.feeling.sanguo.patch.model.General;
import org.sf.feeling.sanguo.patch.util.BakUtil;
import org.sf.feeling.sanguo.patch.util.ChangeCode;
import org.sf.feeling.sanguo.patch.util.FileConstants;
import org.sf.feeling.sanguo.patch.util.FileUtil;
import org.sf.feeling.sanguo.patch.util.GeneralAgeUtil;
import org.sf.feeling.sanguo.patch.util.MapUtil;
import org.sf.feeling.sanguo.patch.util.UnitUtil;
import org.sf.feeling.sanguo.patch.widget.WidgetUtil;
import org.sf.feeling.swt.win32.extension.util.SortMap;

public class StartPatchPage extends SimpleTabPage
{

	private static final SortMap factionProperty = FileUtil.loadProperties( "faction" );
	private static final String re1 = "(denari)"; // Variable Name 1
	private static final String re2 = "(\\s+)"; // White Space 1
	private static final String re3 = "(\\d+)"; // Integer Number 1
	private static final String re7 = "(starting_action_points)"; // Variable
	private CCombo generalInCombo;
	private CCombo generalOutCombo;
	private SortMap generalMap;
	private List nonRelativeGeneralList;
	private CCombo generalCombo;
	private CCombo generalAgeCombo;
	private CCombo generalChangeCombo;
	private Button generalChangeBtn;
	private Button posButton;
	private Spinner posXSpinner;
	private Spinner posYSpinner;
	private CCombo moneyFactionCombo;
	private CCombo generalChangeFactionCombo;
	private SortMap factionMap;
	private CCombo ageFactionCombo;
	private CCombo generalChangeOutFactionCombo;
	private CCombo generalSwitchInFactionCombo;
	private CCombo generalSwitchOutFactionCombo;
	private CCombo generalIdentityFactionCombo;
	private CCombo generalIdentityChangeCombo;
	private Button switchGeneralApply;
	private Button changeGeneralFactionApply;
	private Button changeIdentityRestoreApply;
	private Button changeIdentityApply;
	private CCombo identityCombo;
	private Button zouTianXiaApply;

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
				"<form><p>注意：<br/>1、武将交换功能将交换2个武将所属势力，身份，年龄，后代及大地图所在位置。"
						+ "比如马腾和马超交换，马超将成为马腾势力的君主，而马腾则会成为马超的儿子。<br/>"
						+ "2、更换所属势力的武将不能有儿子，并且不能是势力君主或者继承人。<br/>"
						+ "3、“武将年龄设置”能减小的岁数有限，想最大化减小武将年龄，需使用“最大化减小势力武将年龄”功能。<br/>"
						+ "4、收买武将对势力武将有年龄要求，设置势力部分垃圾武将年龄大于40岁更有利于武将的收买。<br/>"
						+ "5、修改器会在应用前自动计算武将可用坐标，以保证武将初始位置为合法坐标。</p></form>",
				true,
				true );
		TableWrapData data = new TableWrapData( TableWrapData.FILL );
		data.maxWidth = 600;
		noteText.setLayoutData( data );;
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
		layout.numColumns = 8;
		patchClient.setLayout( layout );
		{
			final Button addMoneyBtn = WidgetUtil.getToolkit( )
					.createButton( patchClient, "势力初始金钱修改", SWT.CHECK );

			moneyFactionCombo = WidgetUtil.getToolkit( )
					.createCCombo( patchClient, SWT.READ_ONLY );

			moneyFactionCombo.add( "全部势力", 0 );
			moneyFactionCombo.select( 0 );
			GridData gd = new GridData( GridData.FILL_HORIZONTAL );
			gd.horizontalSpan = 2;
			gd.widthHint = 150;
			moneyFactionCombo.setLayoutData( gd );
			moneyFactionCombo.setEnabled( false );

			final Text addMoneyText = WidgetUtil.getToolkit( )
					.createText( patchClient, "50000" );
			addMoneyText.setEnabled( false );
			gd = new GridData( GridData.FILL_HORIZONTAL );
			gd.horizontalSpan = 2;
			gd.widthHint = 150;
			addMoneyText.setLayoutData( gd );

			final Button addMoneyApply = WidgetUtil.getToolkit( )
					.createButton( patchClient, "应用", SWT.PUSH );
			addMoneyApply.setEnabled( false );
			final Button addMoneyRestore = WidgetUtil.getToolkit( )
					.createButton( patchClient, "还原", SWT.PUSH );

			gd = new GridData( );
			gd.horizontalSpan = 2;
			addMoneyRestore.setLayoutData( gd );

			addMoneyApply.addSelectionListener( new SelectionAdapter( ) {

				public void widgetSelected( SelectionEvent e )
				{
					if ( moneyFactionCombo.getSelectionIndex( ) == -1
							|| addMoneyText.getText( ).trim( ).length( ) == 0 )
						return;
					long money = 0;
					try
					{
						money = Math.abs( Long.parseLong( addMoneyText.getText( ) ) );
					}
					catch ( NumberFormatException e1 )
					{
						e1.printStackTrace( );
						return;
					}
					addMoneyApply.setEnabled( false );
					if ( FileConstants.stratFile.exists( ) )
					{
						if ( moneyFactionCombo.getSelectionIndex( ) == 0 )
						{
							BakUtil.bakData( "势力金钱修改：全部势力"
									+ addMoneyText.getText( ) );
							FileUtil.replaceFile( FileConstants.stratFile, re1
									+ re2
									+ re3, re3, "" + money );
						}
						else
						{
							BakUtil.bakData( "势力金钱修改："
									+ moneyFactionCombo.getText( )
									+ addMoneyText.getText( ) );
							String faction = (String) factionProperty.get( moneyFactionCombo.getText( ) );
							String line = null;
							StringWriter writer = new StringWriter( );
							PrintWriter printer = new PrintWriter( writer );
							try
							{
								BufferedReader in = new BufferedReader( new InputStreamReader( new FileInputStream( FileConstants.stratFile ),
										"GBK" ) );
								boolean startDenari = false;
								while ( ( line = in.readLine( ) ) != null )
								{
									if ( !startDenari )
									{
										printer.println( line );
										Pattern pattern = Pattern.compile( "^\\s*(faction)(\\s+)((?i)"
												+ faction
												+ ")(\\s*)(,)" );
										Matcher matcher = pattern.matcher( line );
										if ( matcher.find( ) )
										{
											startDenari = true;
										}
									}
									else
									{
										Pattern pattern = Pattern.compile( "^\\s*(denari)(\\s+)(\\d+)(\\s*)" );
										Matcher matcher = pattern.matcher( line );
										if ( matcher.find( ) )
										{
											printer.print( matcher.group( )
													.replaceAll( "\\d+",
															"" + money ) );
											printer.println( line.substring( matcher.end( ) ) );
											startDenari = false;
										}
										else
										{
											printer.println( line );
										}
									}
								}
								in.close( );
								PrintWriter out = new PrintWriter( new BufferedWriter( new OutputStreamWriter( new FileOutputStream( FileConstants.stratFile ),
										"GBK" ) ),
										false );
								out.print( writer.getBuffer( ) );
								out.close( );
								printer.close( );
							}
							catch ( Exception e1 )
							{
								e1.printStackTrace( );
							}
						}
					}
					addMoneyApply.setEnabled( true );
				}
			} );

			addMoneyRestore.addSelectionListener( new SelectionAdapter( ) {

				public void widgetSelected( SelectionEvent e )
				{
					addMoneyRestore.setEnabled( false );
					BakUtil.restoreCurrectVersionBakFile( );
					refreshPage( );
					addMoneyRestore.setEnabled( true );
				}
			} );

			addMoneyBtn.addSelectionListener( new SelectionAdapter( ) {

				public void widgetSelected( SelectionEvent e )
				{
					moneyFactionCombo.setEnabled( addMoneyBtn.getSelection( ) );
					addMoneyText.setEnabled( addMoneyBtn.getSelection( ) );
					addMoneyApply.setEnabled( addMoneyBtn.getSelection( ) );
				}

			} );
		}
		{
			final Button zouTianXiaBtn = WidgetUtil.getToolkit( )
					.createButton( patchClient, "回合行动力修改", SWT.CHECK );

			final Text zouTianXiaText = WidgetUtil.getToolkit( )
					.createText( patchClient,
							FileUtil.findMatchString( FileConstants.characterFile,
									re7 + re2 + re3,
									re3 ) );
			zouTianXiaText.addModifyListener( new ModifyListener( ) {

				public void modifyText( ModifyEvent arg0 )
				{
					zouTianXiaApply.setEnabled( zouTianXiaBtn.getSelection( )
							&& zouTianXiaText.getText( ).trim( ).length( ) > 0 );
				}
			} );
			zouTianXiaText.setEnabled( false );
			GridData gd = new GridData( GridData.FILL_HORIZONTAL );
			gd.horizontalSpan = 4;
			zouTianXiaText.setLayoutData( gd );
			zouTianXiaApply = WidgetUtil.getToolkit( )
					.createButton( patchClient, "应用", SWT.PUSH );
			zouTianXiaApply.setEnabled( false );
			final Button zouTianXiaRestore = WidgetUtil.getToolkit( )
					.createButton( patchClient, "还原", SWT.PUSH );
			gd = new GridData( );
			gd.horizontalSpan = 2;
			zouTianXiaRestore.setLayoutData( gd );

			zouTianXiaApply.addSelectionListener( new SelectionAdapter( ) {

				public void widgetSelected( SelectionEvent e )
				{

					if ( FileConstants.characterFile.exists( ) )
					{
						zouTianXiaApply.setEnabled( false );
						try
						{
							int point = Math.abs( Integer.parseInt( zouTianXiaText.getText( ) ) );
							BakUtil.bakData( "回合行动力修改："
									+ zouTianXiaText.getText( ) );
							FileUtil.replaceFile( FileConstants.characterFile,
									re7 + re2 + re3,
									re3,
									"" + point );
						}
						catch ( NumberFormatException e1 )
						{
							e1.printStackTrace( );
						}
						zouTianXiaApply.setEnabled( zouTianXiaBtn.getSelection( )
								&& zouTianXiaText.getText( ).trim( ).length( ) > 0 );
					}
				}
			} );

			zouTianXiaRestore.addSelectionListener( new SelectionAdapter( ) {

				public void widgetSelected( SelectionEvent e )
				{
					if ( FileConstants.characterFile.exists( ) )
					{
						zouTianXiaRestore.setEnabled( false );
						BakUtil.restoreCurrectVersionBakFile( );
						refreshPage( );
						zouTianXiaRestore.setEnabled( true );
						zouTianXiaText.setText( FileUtil.findMatchString( FileConstants.characterFile,
								re7 + re2 + re3,
								re3 ) );
						zouTianXiaApply.setEnabled( zouTianXiaBtn.getSelection( )
								&& zouTianXiaText.getText( ).trim( ).length( ) > 0 );
					}
				}
			} );

			zouTianXiaBtn.addSelectionListener( new SelectionAdapter( ) {

				public void widgetSelected( SelectionEvent e )
				{
					zouTianXiaText.setEnabled( zouTianXiaBtn.getSelection( ) );
					zouTianXiaApply.setEnabled( zouTianXiaBtn.getSelection( )
							&& zouTianXiaText.getText( ).trim( ).length( ) > 0 );
				}

			} );
		}
		{
			final Button generalChangeBtn = WidgetUtil.getToolkit( )
					.createButton( patchClient, "武将交换", SWT.CHECK );

			generalSwitchInFactionCombo = WidgetUtil.getToolkit( )
					.createCCombo( patchClient, SWT.READ_ONLY );
			GridData gd = new GridData( GridData.FILL_HORIZONTAL );
			generalSwitchInFactionCombo.setLayoutData( gd );
			generalSwitchInFactionCombo.setEnabled( false );
			generalSwitchInFactionCombo.addSelectionListener( new SelectionAdapter( ) {

				public void widgetSelected( SelectionEvent e )
				{
					if ( generalSwitchInFactionCombo.getSelectionIndex( ) == 0 )
					{
						String text = generalInCombo.getText( );
						generalInCombo.removeAll( );
						for ( int i = 0; i < generalMap.size( ); i++ )
						{
							String generalName = ChangeCode.toLong( (String) generalMap.get( i ) );
							generalInCombo.add( generalName );
							if ( generalName.equals( text ) )
								generalInCombo.setText( text );
						}
					}
					else if ( generalSwitchInFactionCombo.getSelectionIndex( ) > 0 )
					{
						String text = generalInCombo.getText( );
						generalInCombo.removeAll( );
						String faction = (String) factionMap.getKeyList( )
								.get( generalSwitchInFactionCombo.getSelectionIndex( ) - 1 );
						SortMap modelMap = UnitUtil.getGeneralModels( );
						for ( int i = 0; i < generalMap.size( ); i++ )
						{
							String general = (String) generalMap.getKeyList( )
									.get( i );
							General model = (General) modelMap.get( general );
							if ( faction.equals( model.getFaction( ).trim( ) ) )
							{
								String generalName = ChangeCode.toLong( (String) generalMap.get( general ) );
								generalInCombo.add( generalName );
								if ( generalName.equals( text ) )
									generalInCombo.setText( text );
							}
						}
					}
					switchGeneralApply.setEnabled( generalChangeBtn.getSelection( )
							&& generalInCombo.getSelectionIndex( ) != -1
							&& generalOutCombo.getSelectionIndex( ) != -1 );
				}
			} );

			generalInCombo = WidgetUtil.getToolkit( )
					.createCCombo( patchClient, SWT.READ_ONLY );
			gd = new GridData( GridData.FILL_HORIZONTAL );
			generalInCombo.setLayoutData( gd );
			generalInCombo.setEnabled( false );

			generalSwitchOutFactionCombo = WidgetUtil.getToolkit( )
					.createCCombo( patchClient, SWT.READ_ONLY );
			gd = new GridData( GridData.FILL_HORIZONTAL );
			generalSwitchOutFactionCombo.setLayoutData( gd );
			generalSwitchOutFactionCombo.setEnabled( false );
			generalSwitchOutFactionCombo.addSelectionListener( new SelectionAdapter( ) {

				public void widgetSelected( SelectionEvent e )
				{
					if ( generalSwitchOutFactionCombo.getSelectionIndex( ) == 0 )
					{
						String text = generalOutCombo.getText( );
						generalOutCombo.removeAll( );
						for ( int i = 0; i < generalMap.size( ); i++ )
						{
							String generalName = ChangeCode.toLong( (String) generalMap.get( i ) );
							generalOutCombo.add( generalName );
							if ( generalName.equals( text ) )
								generalOutCombo.setText( text );
						}
					}
					else if ( generalSwitchOutFactionCombo.getSelectionIndex( ) > 0 )
					{
						String text = generalOutCombo.getText( );
						generalOutCombo.removeAll( );
						String faction = (String) factionMap.getKeyList( )
								.get( generalSwitchOutFactionCombo.getSelectionIndex( ) - 1 );
						SortMap modelMap = UnitUtil.getGeneralModels( );
						for ( int i = 0; i < generalMap.size( ); i++ )
						{
							String general = (String) generalMap.getKeyList( )
									.get( i );
							General model = (General) modelMap.get( general );
							if ( faction.equals( model.getFaction( ).trim( ) ) )
							{
								String generalName = ChangeCode.toLong( (String) generalMap.get( general ) );
								generalOutCombo.add( generalName );
								if ( generalName.equals( text ) )
									generalOutCombo.setText( text );
							}
						}
					}
					switchGeneralApply.setEnabled( generalChangeBtn.getSelection( )
							&& generalInCombo.getSelectionIndex( ) != -1
							&& generalOutCombo.getSelectionIndex( ) != -1 );
				}
			} );

			generalOutCombo = WidgetUtil.getToolkit( )
					.createCCombo( patchClient, SWT.READ_ONLY );
			gd = new GridData( GridData.FILL_HORIZONTAL );
			generalOutCombo.setLayoutData( gd );
			generalOutCombo.setEnabled( false );

			switchGeneralApply = WidgetUtil.getToolkit( )
					.createButton( patchClient, "应用", SWT.PUSH );
			switchGeneralApply.setEnabled( false );

			final Button changeUnitRestoreApply = WidgetUtil.getToolkit( )
					.createButton( patchClient, "还原", SWT.PUSH );
			gd = new GridData( );
			gd.horizontalSpan = 2;
			changeUnitRestoreApply.setLayoutData( gd );

			changeUnitRestoreApply.addSelectionListener( new SelectionAdapter( ) {

				public void widgetSelected( SelectionEvent e )
				{
					changeUnitRestoreApply.setEnabled( false );
					BakUtil.restoreCurrectVersionBakFile( );
					refreshPage( );
					changeUnitRestoreApply.setEnabled( true );
					switchGeneralApply.setEnabled( generalChangeBtn.getSelection( )
							&& generalInCombo.getSelectionIndex( ) != -1
							&& generalOutCombo.getSelectionIndex( ) != -1 );
				}
			} );

			switchGeneralApply.addSelectionListener( new SelectionAdapter( ) {

				public void widgetSelected( SelectionEvent e )
				{
					switchGeneralApply.setEnabled( false );

					String generalInCode = (String) generalMap.getKeyList( )
							.get( generalMap.getValueList( )
									.indexOf( generalInCombo.getText( ) ) );
					String generalOutCode = (String) generalMap.getKeyList( )
							.get( generalMap.getValueList( )
									.indexOf( generalOutCombo.getText( ) ) );

					BakUtil.bakData( "武将交换："
							+ generalInCombo.getText( )
							+ "<-->"
							+ generalOutCombo.getText( ) );

					UnitUtil.switchGeneral( generalInCode, generalOutCode );
					MapUtil.initMap( );
					refreshPage( );
					switchGeneralApply.setEnabled( generalChangeBtn.getSelection( )
							&& generalInCombo.getSelectionIndex( ) != -1
							&& generalOutCombo.getSelectionIndex( ) != -1 );
				}
			} );

			generalChangeBtn.addSelectionListener( new SelectionAdapter( ) {

				public void widgetSelected( SelectionEvent e )
				{
					generalSwitchInFactionCombo.setEnabled( generalChangeBtn.getSelection( ) );
					generalSwitchOutFactionCombo.setEnabled( generalChangeBtn.getSelection( ) );
					generalInCombo.setEnabled( generalChangeBtn.getSelection( ) );
					generalOutCombo.setEnabled( generalChangeBtn.getSelection( ) );
				}
			} );

			SelectionAdapter listener = new SelectionAdapter( ) {

				public void widgetSelected( SelectionEvent e )
				{
					switchGeneralApply.setEnabled( generalChangeBtn.getSelection( )
							&& generalInCombo.getSelectionIndex( ) != -1
							&& generalOutCombo.getSelectionIndex( ) != -1 );
				}
			};
			generalOutCombo.addSelectionListener( listener );
			generalInCombo.addSelectionListener( listener );
		}
		{
			generalChangeBtn = WidgetUtil.getToolkit( )
					.createButton( patchClient, "", SWT.CHECK );
			int width = generalChangeBtn.computeSize( SWT.DEFAULT, SWT.DEFAULT ).x;
			generalChangeBtn.setText( "更换武将势力" );

			generalChangeOutFactionCombo = WidgetUtil.getToolkit( )
					.createCCombo( patchClient, SWT.READ_ONLY );
			GridData gd = new GridData( GridData.FILL_HORIZONTAL );
			generalChangeOutFactionCombo.setLayoutData( gd );
			generalChangeOutFactionCombo.setEnabled( false );
			generalChangeOutFactionCombo.addSelectionListener( new SelectionAdapter( ) {

				public void widgetSelected( SelectionEvent e )
				{
					if ( generalChangeOutFactionCombo.getSelectionIndex( ) == 0 )
					{
						String text = generalChangeCombo.getText( );
						generalChangeCombo.removeAll( );
						nonRelativeGeneralList = UnitUtil.getNonRelativeGenerals( );
						generalChangeCombo.removeAll( );

						for ( int i = 0; i < generalMap.size( ); i++ )
						{
							String generalName = ChangeCode.toLong( (String) generalMap.get( i ) );
							generalChangeCombo.add( generalName );
							if ( generalName.equals( text ) )
								generalChangeCombo.setText( text );
						}
					}
					else if ( generalChangeOutFactionCombo.getSelectionIndex( ) > 0 )
					{
						String text = generalChangeCombo.getText( );
						generalChangeCombo.removeAll( );
						nonRelativeGeneralList = UnitUtil.getNonRelativeGenerals( );
						String faction = (String) factionMap.getKeyList( )
								.get( generalChangeOutFactionCombo.getSelectionIndex( ) - 1 );
						SortMap modelMap = UnitUtil.getGeneralModels( );
						for ( int i = 0; i < nonRelativeGeneralList.size( ); i++ )
						{
							String general = (String) nonRelativeGeneralList.get( i );
							General model = (General) modelMap.get( general );
							if ( faction.equals( model.getFaction( ).trim( ) ) )
							{
								String generalName = ChangeCode.toLong( (String) generalMap.get( nonRelativeGeneralList.get( i ) ) );
								generalChangeCombo.add( generalName );
								if ( generalName.equals( text ) )
									generalChangeCombo.setText( text );
							}
						}
					}
					changeGeneralFactionApply.setEnabled( generalChangeBtn.getSelection( )
							&& generalChangeCombo.getSelectionIndex( ) != -1
							&& generalChangeFactionCombo.getSelectionIndex( ) != -1 );
				}
			} );

			generalChangeCombo = WidgetUtil.getToolkit( )
					.createCCombo( patchClient, SWT.READ_ONLY );
			gd = new GridData( GridData.FILL_HORIZONTAL );
			generalChangeCombo.setLayoutData( gd );
			generalChangeCombo.setEnabled( false );
			generalChangeCombo.addSelectionListener( new SelectionAdapter( ) {

				public void widgetSelected( SelectionEvent e )
				{
					if ( generalChangeCombo.isEnabled( )
							&& generalChangeCombo.getSelectionIndex( ) != -1 )
					{
						posXSpinner.setEnabled( true );
						posYSpinner.setEnabled( true );
						posButton.setEnabled( true );
					}
					else
					{
						posXSpinner.setEnabled( false );
						posYSpinner.setEnabled( false );
						posButton.setEnabled( false );
					}
				};
			} );

			generalChangeFactionCombo = WidgetUtil.getToolkit( )
					.createCCombo( patchClient, SWT.READ_ONLY );
			gd = new GridData( GridData.FILL_HORIZONTAL );
			gd.widthHint = 150;
			gd.horizontalSpan = 2;
			generalChangeFactionCombo.setLayoutData( gd );
			generalChangeFactionCombo.setEnabled( false );

			changeGeneralFactionApply = WidgetUtil.getToolkit( )
					.createButton( patchClient, "应用", SWT.PUSH );
			changeGeneralFactionApply.setEnabled( false );

			final Button changeUnitRestoreApply = WidgetUtil.getToolkit( )
					.createButton( patchClient, "还原", SWT.PUSH );
			gd = new GridData( );
			gd.horizontalSpan = 2;
			changeUnitRestoreApply.setLayoutData( gd );

			CLabel positionLabel = WidgetUtil.getToolkit( )
					.createCLabel( patchClient, "大地图坐标（X， Y）" );
			gd = new GridData( );
			gd.horizontalIndent = width;
			positionLabel.setLayoutData( gd );

			posXSpinner = WidgetUtil.getToolkit( ).createSpinner( patchClient );
			gd = new GridData( GridData.FILL_HORIZONTAL );
			gd.horizontalSpan = 2;
			gd.widthHint = 150;
			posXSpinner.setLayoutData( gd );
			posXSpinner.setEnabled( false );
			initSpinner( posXSpinner, 0, 189, 0, 1 );

			posYSpinner = WidgetUtil.getToolkit( ).createSpinner( patchClient );
			gd = new GridData( GridData.FILL_HORIZONTAL );
			gd.widthHint = 150;
			gd.horizontalSpan = 2;
			posYSpinner.setLayoutData( gd );
			posYSpinner.setEnabled( false );
			initSpinner( posYSpinner, 0, 179, 0, 1 );

			posButton = WidgetUtil.getToolkit( ).createButton( patchClient,
					SWT.PUSH,
					true );
			posButton.setEnabled( false );
			posButton.setText( "选择" );
			posButton.addSelectionListener( new SelectionAdapter( ) {

				public void widgetSelected( SelectionEvent e )
				{
					PositionDialog dialog = new PositionDialog( "选择相邻武将" );
					if ( dialog.open( ) == Dialog.OK )
					{
						String general = (String) dialog.getResult( );
						if ( general != null )
						{
							General model = (General) UnitUtil.getGeneralModels( )
									.get( general );
							try
							{
								Point point = computeGeneralPosition( new Point( Integer.parseInt( model.getPosX( ) ),
										Integer.parseInt( model.getPosY( ) ) ),
										true,
										true );
								posXSpinner.setSelection( point.x );
								posYSpinner.setSelection( point.y );
							}
							catch ( NumberFormatException e1 )
							{
								e1.printStackTrace( );
							}
						}
					}
				}
			} );

			CLabel span = WidgetUtil.getToolkit( ).createCLabel( patchClient,
					"" );
			gd = new GridData( );
			gd.horizontalSpan = 2;
			span.setLayoutData( gd );

			changeUnitRestoreApply.addSelectionListener( new SelectionAdapter( ) {

				public void widgetSelected( SelectionEvent e )
				{
					changeUnitRestoreApply.setEnabled( false );
					BakUtil.restoreCurrectVersionBakFile( );
					refreshPage( );
					changeUnitRestoreApply.setEnabled( true );
					changeGeneralFactionApply.setEnabled( generalChangeBtn.getSelection( )
							&& generalChangeCombo.getSelectionIndex( ) != -1
							&& generalChangeFactionCombo.getSelectionIndex( ) != -1 );
				}
			} );

			changeGeneralFactionApply.addSelectionListener( new SelectionAdapter( ) {

				public void widgetSelected( SelectionEvent e )
				{
					changeGeneralFactionApply.setEnabled( false );

					Point point = computeGeneralPosition( new Point( posXSpinner.getSelection( ),
							posYSpinner.getSelection( ) ),
							true,
							true );

					String generalCode = (String) generalMap.getKeyList( )
							.get( generalMap.getValueList( )
									.indexOf( generalChangeCombo.getText( ) ) );
					String factionCode = (String) factionMap.getKeyList( )
							.get( generalChangeFactionCombo.getSelectionIndex( ) );

					BakUtil.bakData( "更换武将势力："
							+ generalChangeCombo.getText( )
							+ "-->"
							+ generalChangeFactionCombo.getText( )
							+ "势力，坐标（"
							+ point.x
							+ "，"
							+ point.y
							+ "）" );

					UnitUtil.changeGeneral( generalCode, factionCode, ""
							+ point.x, "" + point.y );
					MapUtil.initMap( );
					refreshPage( );
					changeGeneralFactionApply.setEnabled( generalChangeBtn.getSelection( )
							&& generalChangeCombo.getSelectionIndex( ) != -1
							&& generalChangeFactionCombo.getSelectionIndex( ) != -1 );
				}
			} );

			generalChangeBtn.addSelectionListener( new SelectionAdapter( ) {

				public void widgetSelected( SelectionEvent e )
				{
					generalChangeOutFactionCombo.setEnabled( generalChangeBtn.getSelection( ) );
					generalChangeCombo.setEnabled( generalChangeBtn.getSelection( ) );
					generalChangeFactionCombo.setEnabled( generalChangeBtn.getSelection( ) );

					if ( generalChangeCombo.isEnabled( )
							&& generalChangeCombo.getSelectionIndex( ) != -1 )
					{
						posXSpinner.setEnabled( true );
						posYSpinner.setEnabled( true );
						posButton.setEnabled( true );
					}
					else
					{
						posXSpinner.setEnabled( false );
						posYSpinner.setEnabled( false );
						posButton.setEnabled( false );
					}
				}
			} );

			SelectionAdapter listener = new SelectionAdapter( ) {

				public void widgetSelected( SelectionEvent e )
				{
					changeGeneralFactionApply.setEnabled( generalChangeBtn.getSelection( )
							&& generalChangeCombo.getSelectionIndex( ) != -1
							&& generalChangeFactionCombo.getSelectionIndex( ) != -1 );
				}
			};
			generalChangeCombo.addSelectionListener( listener );
			generalChangeFactionCombo.addSelectionListener( listener );

			generalChangeCombo.addSelectionListener( new SelectionAdapter( ) {

				public void widgetSelected( SelectionEvent e )
				{
					if ( generalChangeCombo.getSelectionIndex( ) != -1 )
					{
						String general = (String) generalMap.getKeyList( )
								.get( generalMap.getValueList( )
										.indexOf( generalChangeCombo.getText( ) ) );
						General model = (General) UnitUtil.getGeneralModels( )
								.get( general );
						try
						{
							posXSpinner.setSelection( Integer.parseInt( model.getPosX( ) ) );
							posYSpinner.setSelection( Integer.parseInt( model.getPosY( ) ) );
						}
						catch ( NumberFormatException e1 )
						{
							e1.printStackTrace( );
						}
					}
				}
			} );
		}
		{
			final Button generalIdentityBtn = WidgetUtil.getToolkit( )
					.createButton( patchClient, "", SWT.CHECK );
			generalIdentityBtn.setText( "设置武将身份" );

			generalIdentityFactionCombo = WidgetUtil.getToolkit( )
					.createCCombo( patchClient, SWT.READ_ONLY );
			GridData gd = new GridData( GridData.FILL_HORIZONTAL );
			generalIdentityFactionCombo.setLayoutData( gd );
			generalIdentityFactionCombo.setEnabled( false );
			generalIdentityFactionCombo.addSelectionListener( new SelectionAdapter( ) {

				public void widgetSelected( SelectionEvent e )
				{
					if ( generalIdentityFactionCombo.getSelectionIndex( ) == 0 )
					{
						String text = generalIdentityChangeCombo.getText( );
						generalIdentityChangeCombo.removeAll( );
						nonRelativeGeneralList = UnitUtil.getNonRelativeGenerals( );
						generalIdentityChangeCombo.removeAll( );

						for ( int i = 0; i < generalMap.size( ); i++ )
						{
							String generalName = ChangeCode.toLong( (String) generalMap.get( i ) );
							generalIdentityChangeCombo.add( generalName );
							if ( generalName.equals( text ) )
								generalIdentityChangeCombo.setText( text );
						}
					}
					else if ( generalIdentityFactionCombo.getSelectionIndex( ) > 0 )
					{
						String text = generalIdentityChangeCombo.getText( );
						generalIdentityChangeCombo.removeAll( );
						String faction = (String) factionMap.getKeyList( )
								.get( generalIdentityFactionCombo.getSelectionIndex( ) - 1 );
						SortMap modelMap = UnitUtil.getGeneralModels( );
						for ( int i = 0; i < generalMap.size( ); i++ )
						{
							String general = (String) generalMap.getKeyList( )
									.get( i );
							General model = (General) modelMap.get( general );
							if ( faction.equals( model.getFaction( ).trim( ) ) )
							{
								String generalName = ChangeCode.toLong( (String) generalMap.get( i ) );
								generalIdentityChangeCombo.add( generalName );
								if ( generalName.equals( text ) )
									generalIdentityChangeCombo.setText( text );
							}
						}
					}
					changeIdentityApply.setEnabled( generalIdentityBtn.getSelection( )
							&& generalIdentityChangeCombo.getSelectionIndex( ) != -1
							&& identityCombo.getSelectionIndex( ) != -1 );
				}
			} );

			generalIdentityChangeCombo = WidgetUtil.getToolkit( )
					.createCCombo( patchClient, SWT.READ_ONLY );
			gd = new GridData( GridData.FILL_HORIZONTAL );
			generalIdentityChangeCombo.setLayoutData( gd );
			generalIdentityChangeCombo.setEnabled( false );

			identityCombo = WidgetUtil.getToolkit( ).createCCombo( patchClient,
					SWT.READ_ONLY );
			gd = new GridData( GridData.FILL_HORIZONTAL );
			gd.widthHint = 150;
			gd.horizontalSpan = 2;
			identityCombo.setLayoutData( gd );
			identityCombo.setEnabled( false );
			identityCombo.setItems( new String[]{
					"势力君主", "势力继承人"
			} );

			changeIdentityApply = WidgetUtil.getToolkit( )
					.createButton( patchClient, "应用", SWT.PUSH );
			changeIdentityApply.setEnabled( false );
			changeIdentityApply.addSelectionListener( new SelectionAdapter( ) {

				public void widgetSelected( SelectionEvent e )
				{
					changeIdentityApply.setEnabled( false );
					BakUtil.bakData( "设置武将身份："
							+ generalIdentityChangeCombo.getText( )
							+ "-->"
							+ identityCombo.getText( ) );

					String general = (String) generalMap.getKeyList( )
							.get( generalMap.getValueList( )
									.indexOf( generalIdentityChangeCombo.getText( ) ) );
					General model = (General) UnitUtil.getGeneralModels( )
							.get( general );
					String faction = model.getFaction( );
					if ( identityCombo.getSelectionIndex( ) == 0 )
					{
						String leader = (String) UnitUtil.getFactionLeaderMap( )
								.get( faction );
						UnitUtil.switchGeneral( general, leader );
					}
					else if ( identityCombo.getSelectionIndex( ) == 1 )
					{
						String heir = (String) UnitUtil.getFactionHeirMap( )
								.get( faction );
						UnitUtil.switchGeneral( general, heir );
					}
					MapUtil.initMap( );
					refreshPage( );
					changeIdentityApply.setEnabled( generalIdentityBtn.getSelection( )
							&& generalIdentityChangeCombo.getSelectionIndex( ) != -1
							&& identityCombo.getSelectionIndex( ) != -1 );
				}
			} );

			changeIdentityRestoreApply = WidgetUtil.getToolkit( )
					.createButton( patchClient, "还原", SWT.PUSH );
			gd = new GridData( );
			gd.horizontalSpan = 2;
			changeIdentityRestoreApply.setLayoutData( gd );

			changeIdentityRestoreApply.addSelectionListener( new SelectionAdapter( ) {

				public void widgetSelected( SelectionEvent e )
				{
					changeIdentityRestoreApply.setEnabled( false );
					BakUtil.restoreCurrectVersionBakFile( );
					refreshPage( );
					changeIdentityRestoreApply.setEnabled( true );
					changeIdentityApply.setEnabled( generalIdentityBtn.getSelection( )
							&& generalIdentityChangeCombo.getSelectionIndex( ) != -1
							&& identityCombo.getSelectionIndex( ) != -1 );
				}
			} );

			generalIdentityBtn.addSelectionListener( new SelectionAdapter( ) {

				public void widgetSelected( SelectionEvent e )
				{
					generalIdentityFactionCombo.setEnabled( generalIdentityBtn.getSelection( ) );
					generalIdentityChangeCombo.setEnabled( generalIdentityBtn.getSelection( ) );
					identityCombo.setEnabled( generalIdentityBtn.getSelection( ) );
					changeIdentityApply.setEnabled( generalIdentityBtn.getSelection( )
							&& generalIdentityChangeCombo.getSelectionIndex( ) != -1
							&& identityCombo.getSelectionIndex( ) != -1 );
				}
			} );

			SelectionAdapter listener = new SelectionAdapter( ) {

				public void widgetSelected( SelectionEvent e )
				{
					changeIdentityApply.setEnabled( generalIdentityBtn.getSelection( )
							&& generalIdentityChangeCombo.getSelectionIndex( ) != -1
							&& identityCombo.getSelectionIndex( ) != -1 );
				}
			};

			generalIdentityChangeCombo.addSelectionListener( listener );
			identityCombo.addSelectionListener( listener );
		}
		{
			final Button generalAgeBtn = WidgetUtil.getToolkit( )
					.createButton( patchClient, "武将年龄设置", SWT.CHECK );
			generalCombo = WidgetUtil.getToolkit( ).createCCombo( patchClient,
					SWT.READ_ONLY );
			GridData gd = new GridData( GridData.FILL_HORIZONTAL );
			gd.horizontalSpan = 2;
			gd.widthHint = 150;
			generalCombo.setLayoutData( gd );
			generalCombo.setEnabled( false );
			generalCombo.addSelectionListener( new SelectionAdapter( ) {

				public void widgetSelected( SelectionEvent e )
				{
					if ( generalCombo.getSelectionIndex( ) > -1 )
					{
						String generalCode = (String) generalMap.getKeyList( )
								.get( generalCombo.getSelectionIndex( ) );
						int[] age = computeGeneralAge( generalCode );
						initAgeCombo( generalAgeCombo, age );
					}
				}
			} );
			generalAgeCombo = WidgetUtil.getToolkit( )
					.createCCombo( patchClient, SWT.READ_ONLY );
			gd = new GridData( GridData.FILL_HORIZONTAL );
			gd.widthHint = 150;
			gd.horizontalSpan = 2;
			generalAgeCombo.setLayoutData( gd );
			generalAgeCombo.setEnabled( false );

			final Button generalAgeApply = WidgetUtil.getToolkit( )
					.createButton( patchClient, "应用", SWT.PUSH );
			generalAgeApply.setEnabled( false );

			final Button generalAgeRestoreApply = WidgetUtil.getToolkit( )
					.createButton( patchClient, "还原", SWT.PUSH );
			gd = new GridData( );
			gd.horizontalSpan = 2;
			generalAgeRestoreApply.setLayoutData( gd );

			generalAgeRestoreApply.addSelectionListener( new SelectionAdapter( ) {

				public void widgetSelected( SelectionEvent e )
				{
					generalAgeRestoreApply.setEnabled( false );
					BakUtil.restoreCurrectVersionBakFile( );
					refreshPage( );
					generalAgeRestoreApply.setEnabled( true );
					generalAgeApply.setEnabled( generalAgeBtn.getSelection( )
							&& generalCombo.getSelectionIndex( ) != -1
							&& generalAgeCombo.getSelectionIndex( ) != -1 );
				}
			} );

			generalAgeApply.addSelectionListener( new SelectionAdapter( ) {

				public void widgetSelected( SelectionEvent e )
				{
					generalAgeApply.setEnabled( false );
					if ( generalCombo.getSelectionIndex( ) > -1
							&& generalAgeCombo.getSelectionIndex( ) > -1 )
					{
						BakUtil.bakData( "武将年龄设置："
								+ generalCombo.getText( )
								+ generalAgeCombo.getData( )
								+ "岁"
								+ "-->"
								+ generalAgeCombo.getText( )
								+ "岁" );
						String generalCode = (String) generalMap.getKeyList( )
								.get( generalCombo.getSelectionIndex( ) );
						GeneralAgeUtil.saveGeneralAge( generalCode,
								generalAgeCombo.getText( ) );
					}
					refreshPage( );
					generalAgeApply.setEnabled( generalAgeBtn.getSelection( )
							&& generalCombo.getSelectionIndex( ) != -1
							&& generalAgeCombo.getSelectionIndex( ) != -1 );
				}
			} );

			generalAgeBtn.addSelectionListener( new SelectionAdapter( ) {

				public void widgetSelected( SelectionEvent e )
				{
					generalCombo.setEnabled( generalAgeBtn.getSelection( ) );
					generalAgeCombo.setEnabled( generalAgeBtn.getSelection( ) );
					generalAgeApply.setEnabled( generalAgeBtn.getSelection( )
							&& generalCombo.getSelectionIndex( ) != -1
							&& generalAgeCombo.getSelectionIndex( ) != -1 );
				}
			} );

			SelectionAdapter listener = new SelectionAdapter( ) {

				public void widgetSelected( SelectionEvent e )
				{
					generalAgeApply.setEnabled( generalAgeBtn.getSelection( )
							&& generalCombo.getSelectionIndex( ) != -1
							&& generalAgeCombo.getSelectionIndex( ) != -1 );
				}
			};
			generalCombo.addSelectionListener( listener );
			generalAgeCombo.addSelectionListener( listener );
		}
		{
			final Button factionAgeBtn = WidgetUtil.getToolkit( )
					.createButton( patchClient, "最大化减小势力武将年龄", SWT.CHECK );
			ageFactionCombo = WidgetUtil.getToolkit( )
					.createCCombo( patchClient, SWT.READ_ONLY );

			GridData gd = new GridData( GridData.FILL_HORIZONTAL );
			gd.horizontalSpan = 2;
			gd.widthHint = 150;
			ageFactionCombo.setLayoutData( gd );
			ageFactionCombo.setEnabled( false );

			final Button factionAgeApply = WidgetUtil.getToolkit( )
					.createButton( patchClient, "应用", SWT.PUSH );
			factionAgeApply.setEnabled( false );
			gd = new GridData( );
			gd.horizontalSpan = 3;
			gd.horizontalAlignment = SWT.END;
			factionAgeApply.setLayoutData( gd );

			final Button factionAgeRestoreApply = WidgetUtil.getToolkit( )
					.createButton( patchClient, "还原", SWT.PUSH );
			gd = new GridData( );
			gd.horizontalSpan = 2;
			factionAgeRestoreApply.setLayoutData( gd );

			factionAgeRestoreApply.addSelectionListener( new SelectionAdapter( ) {

				public void widgetSelected( SelectionEvent e )
				{
					factionAgeRestoreApply.setEnabled( false );
					BakUtil.restoreCurrectVersionBakFile( );
					refreshPage( );
					factionAgeRestoreApply.setEnabled( true );
					factionAgeApply.setEnabled( factionAgeBtn.getSelection( )
							&& ageFactionCombo.getSelectionIndex( ) != -1 );
				}
			} );

			factionAgeApply.addSelectionListener( new SelectionAdapter( ) {

				public void widgetSelected( SelectionEvent e )
				{
					factionAgeApply.setEnabled( false );
					BakUtil.bakData( "最大化减小势力武将年龄：" + ageFactionCombo.getText( ) );
					if ( ageFactionCombo.getSelectionIndex( ) > 0 )
					{
						String faction = (String) factionProperty.get( ageFactionCombo.getText( ) );
						GeneralAgeUtil.convertFactionAges( new String[]{
							faction
						} );
					}
					else if ( ageFactionCombo.getSelectionIndex( ) == 0 )
					{
						List factions = new ArrayList( );
						factions.addAll( factionProperty.values( ) );
						GeneralAgeUtil.convertFactionAges( (String[]) factions.toArray( new String[0] ) );
					}
					refreshPage( );
					factionAgeApply.setEnabled( factionAgeBtn.getSelection( )
							&& ageFactionCombo.getSelectionIndex( ) != -1 );
				}
			} );

			factionAgeBtn.addSelectionListener( new SelectionAdapter( ) {

				public void widgetSelected( SelectionEvent e )
				{
					ageFactionCombo.setEnabled( factionAgeBtn.getSelection( ) );
					factionAgeApply.setEnabled( factionAgeBtn.getSelection( )
							&& ageFactionCombo.getSelectionIndex( ) != -1 );
				}
			} );

			SelectionAdapter listener = new SelectionAdapter( ) {

				public void widgetSelected( SelectionEvent e )
				{
					factionAgeApply.setEnabled( factionAgeBtn.getSelection( )
							&& ageFactionCombo.getSelectionIndex( ) != -1 );
				}
			};
			ageFactionCombo.addSelectionListener( listener );
		}
		patchSection.setClient( patchClient );
	}

	protected void initAgeCombo( CCombo generalAgeCombo, int[] age )
	{
		if ( age != null && age.length == 3 )
		{
			generalAgeCombo.removeAll( );
			for ( int i = age[1]; i <= age[2]; i++ )
			{
				generalAgeCombo.add( "" + i );
			}
			generalAgeCombo.setText( "" + age[0] );
			generalAgeCombo.setData( "" + age[0] );
		}
	}

	protected int[] computeGeneralAge( String generalCode )
	{
		return GeneralAgeUtil.computeGeneralAge( generalCode );
	}

	private void createTitle( )
	{
		WidgetUtil.createFormText( container.getBody( ),
				"本页面用于设置游戏的开局初始数据，配置完毕后需重新开局方可生效。" );
	}

	public String getDisplayName( )
	{
		return "开局初始化修改";
	}

	public void refresh( )
	{
		super.refresh( );
		refreshPage( );
	}

	private void refreshPage( )
	{
		String generalIn = generalInCombo.getText( );
		String generalOut = generalOutCombo.getText( );
		String general = generalCombo.getText( );
		String generalChange = generalChangeCombo.getText( );
		String generalIdentity = generalIdentityChangeCombo.getText( );

		removeComboItems( generalInCombo );
		removeComboItems( generalOutCombo );
		removeComboItems( generalCombo );
		removeComboItems( generalChangeCombo );
		removeComboItems( generalIdentityChangeCombo );

		factionMap = UnitUtil.getFactionMap( );
		generalMap = UnitUtil.getAvailableGenerals( );
		nonRelativeGeneralList = UnitUtil.getNonRelativeGenerals( );

		String faction = moneyFactionCombo.getText( );

		moneyFactionCombo.removeAll( );
		for ( int i = 0; i < factionMap.getKeyList( ).size( ); i++ )
		{
			moneyFactionCombo.add( (String) factionMap.get( i ) );
		}

		if ( factionMap.containsValue( faction ) )
			moneyFactionCombo.setText( faction );

		faction = generalChangeFactionCombo.getText( );
		generalChangeFactionCombo.removeAll( );
		for ( int i = 0; i < factionMap.getKeyList( ).size( ); i++ )
		{
			generalChangeFactionCombo.add( (String) factionMap.get( i ) );
		}
		if ( factionMap.containsValue( faction ) )
		{
			generalChangeFactionCombo.setText( faction );
			generalChangeFactionCombo.notifyListeners( SWT.Selection,
					new Event( ) );
		}

		int index = generalChangeOutFactionCombo.getSelectionIndex( );
		faction = generalChangeOutFactionCombo.getText( );
		generalChangeOutFactionCombo.removeAll( );
		for ( int i = 0; i < factionMap.getKeyList( ).size( ); i++ )
		{
			generalChangeOutFactionCombo.add( (String) factionMap.get( i ) );
		}
		generalChangeOutFactionCombo.add( "全部势力", 0 );
		if ( factionMap.containsValue( faction ) )
		{
			generalChangeOutFactionCombo.setText( faction );
			generalChangeOutFactionCombo.notifyListeners( SWT.Selection,
					new Event( ) );
		}
		else if ( index == 0 )
		{
			generalChangeOutFactionCombo.select( 0 );
			generalChangeOutFactionCombo.notifyListeners( SWT.Selection,
					new Event( ) );
		}

		index = generalSwitchInFactionCombo.getSelectionIndex( );
		faction = generalSwitchInFactionCombo.getText( );
		generalSwitchInFactionCombo.removeAll( );
		for ( int i = 0; i < factionMap.getKeyList( ).size( ); i++ )
		{
			generalSwitchInFactionCombo.add( (String) factionMap.get( i ) );
		}
		generalSwitchInFactionCombo.add( "全部势力", 0 );
		if ( factionMap.containsValue( faction ) )
		{
			generalSwitchInFactionCombo.setText( faction );
			generalSwitchInFactionCombo.notifyListeners( SWT.Selection,
					new Event( ) );
		}
		else if ( index == 0 )
		{
			generalSwitchInFactionCombo.select( 0 );
			generalSwitchInFactionCombo.notifyListeners( SWT.Selection,
					new Event( ) );
		}

		index = generalSwitchOutFactionCombo.getSelectionIndex( );
		faction = generalSwitchOutFactionCombo.getText( );
		generalSwitchOutFactionCombo.removeAll( );
		for ( int i = 0; i < factionMap.getKeyList( ).size( ); i++ )
		{
			generalSwitchOutFactionCombo.add( (String) factionMap.get( i ) );
		}
		generalSwitchOutFactionCombo.add( "全部势力", 0 );
		if ( factionMap.containsValue( faction ) )
		{
			generalSwitchOutFactionCombo.setText( faction );
			generalSwitchOutFactionCombo.notifyListeners( SWT.Selection,
					new Event( ) );
		}
		else if ( index == 0 )
		{
			generalSwitchOutFactionCombo.select( 0 );
			generalSwitchOutFactionCombo.notifyListeners( SWT.Selection,
					new Event( ) );
		}

		index = ageFactionCombo.getSelectionIndex( );
		faction = ageFactionCombo.getText( );
		ageFactionCombo.removeAll( );
		for ( int i = 0; i < factionMap.getKeyList( ).size( ); i++ )
		{
			ageFactionCombo.add( (String) factionMap.get( i ) );
		}
		ageFactionCombo.add( "全部势力", 0 );
		if ( factionMap.containsValue( faction ) )
		{
			ageFactionCombo.setText( faction );
			ageFactionCombo.notifyListeners( SWT.Selection, new Event( ) );
		}
		else if ( index == 0 )
		{
			ageFactionCombo.select( 0 );
			ageFactionCombo.notifyListeners( SWT.Selection, new Event( ) );
		}

		index = generalIdentityFactionCombo.getSelectionIndex( );
		faction = generalIdentityFactionCombo.getText( );
		generalIdentityFactionCombo.removeAll( );
		for ( int i = 0; i < factionMap.getKeyList( ).size( ); i++ )
		{
			generalIdentityFactionCombo.add( (String) factionMap.get( i ) );
		}
		generalIdentityFactionCombo.add( "全部势力", 0 );
		if ( factionMap.containsValue( faction ) )
		{
			generalIdentityFactionCombo.setText( faction );
			generalIdentityFactionCombo.notifyListeners( SWT.Selection,
					new Event( ) );
		}
		else if ( index == 0 )
		{
			generalIdentityFactionCombo.select( 0 );
			generalIdentityFactionCombo.notifyListeners( SWT.Selection,
					new Event( ) );
		}

		if ( generalIn != null && generalInCombo.indexOf( generalIn ) != -1 )
		{
			generalInCombo.setText( generalIn );
			generalInCombo.notifyListeners( SWT.Selection, new Event( ) );
		}

		if ( generalOut != null && generalOutCombo.indexOf( generalOut ) != -1 )
		{
			generalOutCombo.setText( generalOut );
			generalOutCombo.notifyListeners( SWT.Selection, new Event( ) );
		}

		for ( int i = 0; i < generalMap.size( ); i++ )
		{
			generalCombo.add( (String) generalMap.get( i ) );
		}
		if ( general != null && generalCombo.indexOf( general ) != -1 )
		{
			generalCombo.setText( general );
			generalCombo.notifyListeners( SWT.Selection, new Event( ) );
		}

		if ( generalChange != null
				&& generalChangeCombo.indexOf( generalChange ) != -1 )
		{
			generalChangeCombo.setText( generalChange );
			generalChangeCombo.notifyListeners( SWT.Selection, new Event( ) );
		}

		if ( generalIdentity != null
				&& generalIdentityChangeCombo.indexOf( generalIdentity ) != -1 )
		{
			generalIdentityChangeCombo.setText( generalChange );
			generalIdentityChangeCombo.notifyListeners( SWT.Selection,
					new Event( ) );
		}
	}

	private void removeComboItems( CCombo combo )
	{
		combo.removeAll( );
	}

	private void initSpinner( Spinner combo, int min, int max, int digit,
			int step )
	{
		combo.setMinimum( min );
		combo.setMaximum( max );
		combo.setDigits( digit );
		combo.setIncrement( step );
	}

	private Point computeGeneralPosition( Point point, boolean x, boolean y )
	{
		String general = (String) nonRelativeGeneralList.get( generalChangeCombo.getSelectionIndex( ) );
		General model = (General) UnitUtil.getGeneralModels( ).get( general );

		Iterator iter = UnitUtil.getGeneralModels( ).values( ).iterator( );

		while ( iter.hasNext( ) )
		{
			General temp = (General) iter.next( );
			if ( temp == model )
				continue;

			if ( ( temp.getPosX( ).equals( Integer.toString( point.x ) ) && temp.getPosY( )
					.equals( Integer.toString( point.y ) ) )
					|| UnitUtil.getUnAvailableGeneralPoints( ).contains( point ) )
			{
				if ( point.x == 189 )
				{
					x = false;
				}
				if ( point.y == 179 )
				{
					y = false;
				}
				if ( x )
				{
					point.x = point.x + 1;
				}
				else
				{
					point.x = point.x - 1;
				}
				if ( y )
				{
					point.y = point.y + 1;
				}
				else
				{
					point.y = point.y - 1;
				}
				return computeGeneralPosition( point, x, y );
			}
		}

		return point;
	}
}
