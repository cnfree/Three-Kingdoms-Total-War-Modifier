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

public class FactionChangePage extends SimpleTabPage
{

	private CCombo factionChangeCombo;
	private CCombo currentFactionCombo;
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
	}

	private void createPatchArea( )
	{
		TableWrapData td;
		Section patchSection = WidgetUtil.getToolkit( )
				.createSection( container.getBody( ), Section.EXPANDED );
		td = new TableWrapData( TableWrapData.FILL );
		patchSection.setLayoutData( td );
		patchSection.setText( "请按提示步骤切换游戏势力：" );
		WidgetUtil.getToolkit( ).createCompositeSeparator( patchSection );
		Composite patchClient = WidgetUtil.getToolkit( )
				.createComposite( patchSection );
		GridLayout layout = new GridLayout( );
		layout.numColumns = 3;
		patchClient.setLayout( layout );

		WidgetUtil.getToolkit( ).createLabel( patchClient, "1.选择当前游玩势力：" );

		currentFactionCombo = WidgetUtil.getToolkit( )
				.createCCombo( patchClient, SWT.READ_ONLY );
		GridData gd = new GridData( GridData.FILL_HORIZONTAL );
		gd.widthHint = 200;
		gd.horizontalSpan = 2;
		currentFactionCombo.setLayoutData( gd );

		WidgetUtil.getToolkit( ).createLabel( patchClient, "2.选择切换游戏势力：" );
		factionChangeCombo = WidgetUtil.getToolkit( )
				.createCCombo( patchClient, SWT.READ_ONLY );
		gd = new GridData( GridData.FILL_HORIZONTAL );
		gd.widthHint = 200;
		gd.horizontalSpan = 2;
		factionChangeCombo.setLayoutData( gd );

		WidgetUtil.getToolkit( ).createLabel( patchClient, "3.生成切换势力脚本：" );
		final Button startApply = WidgetUtil.getToolkit( )
				.createButton( patchClient, "应用", SWT.PUSH );
		gd = new GridData( GridData.FILL_HORIZONTAL );
		gd.horizontalSpan = 2;
		startApply.setLayoutData( gd );
		startApply.addSelectionListener( new SelectionAdapter( ) {

			public void widgetSelected( SelectionEvent e )
			{
				startApply.setEnabled( false );
				if ( currentFactionCombo.getSelectionIndex( ) > -1
						&& factionChangeCombo.getSelectionIndex( ) > -1 )
				{
					FileUtil.restoreFile( FileConstants.scriptFile, "patch.tmp" );

					BakUtil.bakData( "切换游戏势力："
							+ currentFactionCombo.getText( )
							+ "-->"
							+ factionChangeCombo.getSelectionIndex( ) );

					FileUtil.bakFile( FileConstants.scriptFile, "patch.tmp" );

					String currentFaction = (String) factionMap.getKeyList( )
							.get( currentFactionCombo.getSelectionIndex( ) );
					String changeFaction = (String) factionMap.getKeyList( )
							.get( factionChangeCombo.getSelectionIndex( ) );

					try
					{
						StringWriter writer = new StringWriter( );
						PrintWriter printer = new PrintWriter( writer );

						BufferedReader in = new BufferedReader( new InputStreamReader( new FileInputStream( FileConstants.scriptFile ),
								"GBK" ) );
						String line = null;
						boolean startScript = false;
						while ( ( line = in.readLine( ) ) != null )
						{
							if ( !startScript )
							{
								Pattern pattern = Pattern.compile( "(?i)^\\s*script" );
								Matcher matcher = pattern.matcher( line.split( ";" )[0].trim( ) );
								if ( matcher.find( ) )
								{
									startScript = true;
									printer.println( line );
									printer.println( );
									printer.println( "if I_LocalFaction "
											+ currentFaction );
									printer.println( "	console_command control "
											+ changeFaction );
									printer.println( "end_if" );
									printer.println( );
									continue;
								}
							}
							printer.println( line );
						}
						in.close( );
						PrintWriter out = new PrintWriter( new BufferedWriter( new OutputStreamWriter( new FileOutputStream( FileConstants.scriptFile ),
								"GBK" ) ),
								false );
						out.print( writer.getBuffer( )
								.append( writer.getBuffer( ) ) );
						out.close( );
						printer.close( );
					}
					catch ( IOException e1 )
					{
						e1.printStackTrace( );
					}
				}
				startApply.setEnabled( true );
			}
		} );

		Label infoLabel = WidgetUtil.getToolkit( ).createLabel( patchClient,
				"4.重启游戏，读取存档，开启12回合，切换势力即时生效。" );
		gd = new GridData( GridData.FILL_HORIZONTAL );
		gd.widthHint = 200;
		gd.horizontalSpan = 3;
		infoLabel.setLayoutData( gd );

		WidgetUtil.getToolkit( ).createLabel( patchClient, "5.移除切换势力脚本：" );
		final Button endApply = WidgetUtil.getToolkit( )
				.createButton( patchClient, "应用", SWT.PUSH );
		gd = new GridData( GridData.FILL_HORIZONTAL );
		gd.horizontalSpan = 2;
		endApply.setLayoutData( gd );
		endApply.addSelectionListener( new SelectionAdapter( ) {

			public void widgetSelected( SelectionEvent e )
			{
				endApply.setEnabled( false );
				FileUtil.restoreFile( FileConstants.scriptFile, "patch.tmp" );
				endApply.setEnabled( true );
			}
		} );

		WidgetUtil.getToolkit( ).createLabel( patchClient, "5.切换游戏势力如有问题：" );
		final Button restoreApply = WidgetUtil.getToolkit( )
				.createButton( patchClient, "还原", SWT.PUSH );
		gd = new GridData( GridData.FILL_HORIZONTAL );
		gd.horizontalSpan = 2;
		restoreApply.setLayoutData( gd );
		restoreApply.addSelectionListener( new RestoreListener( ) {

			public void widgetSelected( SelectionEvent e )
			{
				restoreApply.setEnabled( false );
				BakUtil.restoreCurrectVersionBakFile( );
				refreshPage( );
				restoreApply.setEnabled( true );
			}
		} );
		patchSection.setClient( patchClient );
	}

	private void createTitle( )
	{
		WidgetUtil.createFormText( container.getBody( ),
				"本页面可以使玩家在游戏过程中不重新开档就能切换游玩势力。" );
	}

	public String getDisplayName( )
	{
		return "切换游戏势力";
	}

	public void refresh( )
	{
		super.refresh( );
		refreshPage( );
	}

	private void refreshPage( )
	{
		factionMap = UnitUtil.getFactionMap( );
		String currentFaction = currentFactionCombo.getText( );

		currentFactionCombo.removeAll( );
		for ( int i = 0; i < factionMap.getKeyList( ).size( ); i++ )
		{
			currentFactionCombo.add( (String) factionMap.get( i ) );
		}

		if ( factionMap.containsValue( currentFaction ) )
			currentFactionCombo.setText( currentFaction );

		String changeFaction = factionChangeCombo.getText( );

		factionChangeCombo.removeAll( );
		for ( int i = 0; i < factionMap.getKeyList( ).size( ); i++ )
		{
			factionChangeCombo.add( (String) factionMap.get( i ) );
		}

		if ( factionMap.containsValue( changeFaction ) )
			factionChangeCombo.setText( changeFaction );
	}

}
