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

import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.sf.feeling.sanguo.patch.dialog.CustomGeneralUnitDialog;
import org.sf.feeling.sanguo.patch.util.BakUtil;
import org.sf.feeling.sanguo.patch.util.ChangeCode;
import org.sf.feeling.sanguo.patch.util.FileConstants;
import org.sf.feeling.sanguo.patch.util.FileUtil;
import org.sf.feeling.sanguo.patch.util.MapUtil;
import org.sf.feeling.sanguo.patch.util.UnitUtil;
import org.sf.feeling.sanguo.patch.widget.WidgetUtil;
import org.sf.feeling.swt.win32.extension.util.SortMap;

public class GeneralUnitPage extends SimpleTabPage
{

	private CCombo generalUnitCombo;
	private CCombo factionCombo;
	private SortMap generalUnitMap;
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
		container.addDisposeListener( new DisposeListener( ) {

			public void widgetDisposed( DisposeEvent e )
			{
				FileUtil.restoreFile( FileConstants.unitFile, "patch.tmp" );
			}

		} );
		createTitle( );
		createPatchArea( );

		FormText noteText = WidgetUtil.createFormText( container.getBody( ),
				"<form><p>注意：收买在野名将，可以在收买前先自定义将军卫队，无需重新开局。推荐为王双，李严，郝昭等创建卫队。</p></form>",
				true,
				true );
		TableWrapData data = new TableWrapData( TableWrapData.FILL );
		data.maxWidth = 390;
		noteText.setLayoutData( data );
	}

	private void createPatchArea( )
	{
		TableWrapData td;
		Section patchSection = WidgetUtil.getToolkit( )
				.createSection( container.getBody( ), Section.EXPANDED );
		td = new TableWrapData( TableWrapData.FILL );
		patchSection.setLayoutData( td );
		patchSection.setText( "请按提示步骤收买武将：" );
		WidgetUtil.getToolkit( ).createCompositeSeparator( patchSection );
		Composite patchClient = WidgetUtil.getToolkit( )
				.createComposite( patchSection );
		GridLayout layout = new GridLayout( );
		layout.numColumns = 3;
		patchClient.setLayout( layout );

		final Label firstStep = WidgetUtil.getToolkit( )
				.createLabel( patchClient, "1.收买前请确保能够收买成功，可以先存档，然后尝试收买。" );
		GridData gd = new GridData( GridData.FILL_HORIZONTAL );
		gd.horizontalSpan = 3;
		firstStep.setLayoutData( gd );

		WidgetUtil.getToolkit( ).createLabel( patchClient, "2.选择收买方势力：" );

		factionCombo = WidgetUtil.getToolkit( ).createCCombo( patchClient,
				SWT.READ_ONLY );
		gd = new GridData( GridData.FILL_HORIZONTAL );
		gd.widthHint = 200;
		gd.horizontalSpan = 2;
		factionCombo.setLayoutData( gd );

		WidgetUtil.getToolkit( ).createLabel( patchClient, "3.选择收买武将之将军卫队：" );
		generalUnitCombo = WidgetUtil.getToolkit( ).createCCombo( patchClient,
				SWT.READ_ONLY );
		gd = new GridData( GridData.FILL_HORIZONTAL );
		generalUnitCombo.setLayoutData( gd );

		Button unitButton = WidgetUtil.getToolkit( ).createButton( patchClient,
				"自定义",
				SWT.PUSH );
		unitButton.addSelectionListener( new SelectionAdapter( ) {

			public void widgetSelected( SelectionEvent e )
			{
				CustomGeneralUnitDialog dialog = new CustomGeneralUnitDialog( "创建将军卫队" );
				if ( Window.OK == dialog.open( ) )
				{
					MapUtil.initMap( );
					refresh( );
					if ( dialog.getResult( ) != null )
						generalUnitCombo.setText( dialog.getResult( )
								.toString( ) );
				}
			}
		} );
		WidgetUtil.getToolkit( ).createLabel( patchClient, "4.收买前的准备工作：" );

		final Button readyApply = WidgetUtil.getToolkit( )
				.createButton( patchClient, "应用(收买前请勿切换到修改器其它页面)", SWT.PUSH );
		gd = new GridData( GridData.FILL_HORIZONTAL );
		gd.horizontalSpan = 2;
		readyApply.setLayoutData( gd );

		readyApply.addSelectionListener( new SelectionAdapter( ) {

			public void widgetSelected( SelectionEvent e )
			{
				if ( generalUnitCombo.getSelectionIndex( ) != -1
						&& factionCombo.getSelectionIndex( ) != -1 )
				{
					readyApply.setEnabled( false );

					FileUtil.restoreFile( FileConstants.unitFile, "patch.tmp" );

					BakUtil.bakData( "收买武将将军卫队："
							+ factionCombo.getText( )
							+ "<--"
							+ generalUnitCombo.getText( ) );

					String generalUnit = (String) generalUnitMap.getKeyList( )
							.get( generalUnitCombo.getSelectionIndex( ) );
					String faction = UnitUtil.getFactionsByUnitType( generalUnit )[0];
					UnitUtil.addGeneralUnitToFaction( generalUnit,
							faction,
							(String) factionMap.getKeyList( )
									.get( factionCombo.getSelectionIndex( ) ) );

					FileUtil.bakFile( FileConstants.unitFile, "patch.tmp" );

					try
					{
						StringWriter writer = new StringWriter( );
						PrintWriter printer = new PrintWriter( writer );

						StringWriter writer1 = new StringWriter( );
						PrintWriter printer1 = new PrintWriter( writer1 );

						BufferedReader in = new BufferedReader( new InputStreamReader( new FileInputStream( FileConstants.unitFile ),
								"GBK" ) );
						String line = null;
						boolean startType = false;
						while ( ( line = in.readLine( ) ) != null )
						{
							if ( line.trim( ).startsWith( ";" ) )
							{
								printer.println( line );
								continue;
							}
							if ( !startType )
							{
								Pattern pattern = Pattern.compile( "^\\s*(type)(\\s+)("
										+ generalUnit
										+ ")(\\s*)$" );
								Matcher matcher = pattern.matcher( line.split( ";" )[0].trim( ) );
								if ( matcher.find( ) )
								{
									startType = true;
									printer1.println( line );
									printer.println( ";" + line );

								}
								else
								{
									printer.println( line );
								}
							}
							else
							{
								Pattern pattern = Pattern.compile( "^\\s*(ownership)(\\s+)" );
								Matcher matcher = pattern.matcher( line );
								if ( matcher.find( ) )
								{
									startType = false;
								}
								printer1.println( line );
								printer.println( ";" + line );
							}
						}
						in.close( );

						PrintWriter out = new PrintWriter( new BufferedWriter( new OutputStreamWriter( new FileOutputStream( FileConstants.unitFile ),
								"GBK" ) ),
								false );
						out.print( writer1.getBuffer( )
								.append( writer.getBuffer( ) ) );
						out.close( );

						printer.close( );
						printer1.close( );
					}
					catch ( IOException e1 )
					{
						e1.printStackTrace( );
					}
					readyApply.setEnabled( true );
				}
			}

		} );

		final Label fifthStep = WidgetUtil.getToolkit( )
				.createLabel( patchClient,
						"5.存档，退出游戏并重启，然后读档进行收买。若连续收买多名武将，需重复1-5步操作。",
						SWT.WRAP );
		gd = new GridData( GridData.FILL_HORIZONTAL );
		gd.horizontalSpan = 3;
		gd.widthHint = 390;
		fifthStep.setLayoutData( gd );

		WidgetUtil.getToolkit( ).createLabel( patchClient, "6.收买完毕后的收尾工作：" );
		final Button endApply = WidgetUtil.getToolkit( )
				.createButton( patchClient, "应用", SWT.PUSH );
		gd = new GridData( GridData.FILL_HORIZONTAL );
		gd.horizontalSpan = 2;
		endApply.setLayoutData( gd );
		endApply.addSelectionListener( new SelectionAdapter( ) {

			public void widgetSelected( SelectionEvent e )
			{
				endApply.setEnabled( false );
				FileUtil.restoreFile( FileConstants.unitFile, "patch.tmp" );
				endApply.setEnabled( true );
			}
		} );

		final Label seventhStep = WidgetUtil.getToolkit( )
				.createLabel( patchClient, "7.收买操作结束，存档，退出游戏并重启。" );
		gd = new GridData( GridData.FILL_HORIZONTAL );
		gd.horizontalSpan = 3;
		seventhStep.setLayoutData( gd );

		WidgetUtil.getToolkit( ).createLabel( patchClient, "8.重启游戏如有问题：" );
		final Button restoreApply = WidgetUtil.getToolkit( )
				.createButton( patchClient, "还原", SWT.PUSH );
		gd = new GridData( GridData.FILL_HORIZONTAL );
		gd.horizontalSpan = 2;
		restoreApply.setLayoutData( gd );
		restoreApply.addSelectionListener( new RestoreListener( ) {

			public void widgetSelected( SelectionEvent e )
			{
				super.widgetSelected( e );
				refreshPage( );
			}
		} );
		patchSection.setClient( patchClient );
	}

	private void createTitle( )
	{
		WidgetUtil.createFormText( container.getBody( ),
				"本页面用于收买含有特殊将军卫队之武将，并保留其将军卫队。" );
	}

	public String getDisplayName( )
	{
		return "收买武将";
	}

	public void refresh( )
	{
		super.refresh( );
		refreshPage( );
	}

	private void refreshPage( )
	{
		String general = "";
		int index = generalUnitCombo.getSelectionIndex( );
		if ( index > -1 )
			general = (String) generalUnitMap.getKeyList( ).get( index );
		generalUnitMap = UnitUtil.getGeneralUnits( );
		generalUnitCombo.setItems( new String[0] );
		for ( int i = 0; i < generalUnitMap.size( ); i++ )
		{
			String generalName = ChangeCode.toLong( (String) generalUnitMap.get( i ) );
			generalUnitCombo.add( generalName );
		}
		int newIndex = generalUnitMap.getIndexOf( general );
		if ( newIndex != -1 )
		{
			generalUnitCombo.select( newIndex );
			generalUnitCombo.notifyListeners( SWT.Selection, new Event( ) );
		}

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

	public void deActivate( )
	{
		FileUtil.restoreFile( FileConstants.unitFile, "patch.tmp" );
	}

}
