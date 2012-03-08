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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.sf.feeling.sanguo.patch.util.ChangeCode;
import org.sf.feeling.sanguo.patch.util.FileUtil;
import org.sf.feeling.sanguo.patch.util.PinyinComparator;
import org.sf.feeling.sanguo.patch.util.UnitUtil;
import org.sf.feeling.sanguo.patch.widget.WidgetUtil;
import org.sf.feeling.swt.win32.extension.util.SortMap;

public class CodePage extends SimpleTabPage
{

	private CCombo generalCombo;
	private CCombo generalCodeCombo;
	private CCombo factionCombo;
	private CCombo cityCombo;
	private CCombo regionCombo;
	private CCombo factionCodeCombo;
	private CCombo cityCodeCombo;
	private CCombo regionCodeCombo;
	private Text generalCodeText;
	private Text generalText;
	private Text factionText;
	private Text cityText;
	private Text factionCodeText;
	private Text cityCodeText;
	private Text regionCodeText;
	private Text regionText;
	private CCombo soldierCombo;
	private Text soldierCodeText;
	private Text soldierText;
	private CCombo soldierCodeCombo;
	private SortMap generalMap;
	private SortMap factionMap;
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
		createLeftFindArea( );
		createRightFindArea( );

		initPage( );
	}

	private void initPage( )
	{
		final SortMap cityProperty = FileUtil.loadProperties( "city" );

		generalMap = UnitUtil.getAvailableGenerals( );
		for ( int i = 0; i < generalMap.getKeyList( ).size( ); i++ )
		{
			generalCombo.add( ChangeCode.toLong( (String) generalMap.get( i ) ) );
		}

		generalCombo.addSelectionListener( new SelectionAdapter( ) {

			public void widgetSelected( SelectionEvent e )
			{
				String code = (String) generalMap.getKeyList( )
						.get( generalCombo.getSelectionIndex( ) );
				if ( code != null )
					generalCodeText.setText( code );
				else
					generalCodeText.setText( "" );
			}

		} );

		String[] generals = (String[]) generalMap.getKeyList( )
				.toArray( new String[0] );
		Arrays.sort( generals );
		generalCodeCombo.setItems( generals );
		generalCodeCombo.addSelectionListener( new SelectionAdapter( ) {

			public void widgetSelected( SelectionEvent e )
			{
				String code = ChangeCode.toLong( (String) generalMap.get( generalCodeCombo.getText( ) ) );
				if ( code != null )
					generalText.setText( code );
				else
					generalText.setText( "" );
			}
		} );

		soldierUnitMap = UnitUtil.getSoldierUnits( );
		for ( int i = 0; i < soldierUnitMap.getKeyList( ).size( ); i++ )
		{
			soldierCombo.add( ChangeCode.toLong( (String) soldierUnitMap.get( i ) ) );
		}

		soldierCombo.addSelectionListener( new SelectionAdapter( ) {

			public void widgetSelected( SelectionEvent e )
			{
				String code = (String) soldierUnitMap.getKeyList( )
						.get( soldierCombo.getSelectionIndex( ) );
				if ( code != null )
					soldierCodeText.setText( code );
				else
					soldierCodeText.setText( "" );
			}

		} );

		String[] soldiers = (String[]) soldierUnitMap.getKeyList( )
				.toArray( new String[0] );
		Arrays.sort( soldiers );
		soldierCodeCombo.setItems( soldiers );
		soldierCodeCombo.addSelectionListener( new SelectionAdapter( ) {

			public void widgetSelected( SelectionEvent e )
			{
				String code = ChangeCode.toLong( (String) soldierUnitMap.get( soldierCodeCombo.getText( ) ) );
				if ( code != null )
					soldierText.setText( code );
				else
					soldierText.setText( "" );
			}
		} );

		factionCombo.addSelectionListener( new SelectionAdapter( ) {

			public void widgetSelected( SelectionEvent e )
			{
				String code = (String) factionMap.getKeyList( )
						.get( factionCombo.getSelectionIndex( ) );
				if ( code != null )
					factionCodeText.setText( code );
				else
					factionCodeText.setText( "" );
			}
		} );

		factionCodeCombo.addSelectionListener( new SelectionAdapter( ) {

			public void widgetSelected( SelectionEvent e )
			{
				String code = ChangeCode.toLong( (String) factionMap.get( factionCodeCombo.getText( ) ) );
				if ( code != null )
					factionText.setText( code );
				else
					factionText.setText( "" );
			}
		} );

		if ( cityProperty != null )
		{
			List cityList = new ArrayList( );
			List regionList = new ArrayList( );
			Iterator iter = cityProperty.keySet( ).iterator( );
			while ( iter.hasNext( ) )
			{
				String name = (String) iter.next( );
				String code = (String) cityProperty.get( name );
				if ( code.indexOf( "-a-" ) != -1
						|| code.indexOf( "-ag-" ) != -1 )
					regionList.add( name );
				else if ( code.indexOf( "-b-" ) != -1
						|| code.indexOf( "-bg-" ) != -1 )
					cityList.add( name );
			}
			
			Collections.sort( cityList, new Comparator( ) {

				public int compare( Object o1, Object o2 )
				{
					return PinyinComparator.compare( o1.toString( ), o2.toString( ) );
				}
			} );
			
			Collections.sort( regionList, new Comparator( ) {

				public int compare( Object o1, Object o2 )
				{
					return PinyinComparator.compare( o1.toString( ), o2.toString( ) );
				}
			} );
			
			cityCombo.setItems( (String[]) cityList.toArray( new String[0] ) );
			regionCombo.setItems( (String[]) regionList.toArray( new String[0] ) );
			cityCombo.addModifyListener( new ModifyListener( ) {

				public void modifyText( ModifyEvent e )
				{
					String code = (String) cityProperty.get( cityCombo.getText( ) );
					if ( code != null )
						cityCodeText.setText( code );
					else
						cityCodeText.setText( "" );
				}
			} );
			regionCombo.addModifyListener( new ModifyListener( ) {

				public void modifyText( ModifyEvent e )
				{
					String code = (String) cityProperty.get( regionCombo.getText( ) );
					if ( code != null )
						regionCodeText.setText( code );
					else
						regionCodeText.setText( "" );
				}
			} );

			cityList.clear( );
			regionList.clear( );
			iter = cityProperty.values( ).iterator( );
			while ( iter.hasNext( ) )
			{
				String code = (String) iter.next( );
				if ( code.indexOf( "-a-" ) != -1
						|| code.indexOf( "-ag-" ) != -1 )
					regionList.add( code );
				else if ( code.indexOf( "-b-" ) != -1
						|| code.indexOf( "-bg-" ) != -1 )
					cityList.add( code );
			}
			Collections.sort( cityList );
			Collections.sort( regionList );
			cityCodeCombo.setItems( (String[]) cityList.toArray( new String[0] ) );
			regionCodeCombo.setItems( (String[]) regionList.toArray( new String[0] ) );
			cityCodeCombo.addModifyListener( new ModifyListener( ) {

				public void modifyText( ModifyEvent e )
				{
					Iterator iter = cityProperty.keySet( ).iterator( );
					while ( iter.hasNext( ) )
					{
						Object obj = iter.next( );
						if ( cityCodeCombo.getText( )
								.equals( cityProperty.get( obj ) ) )
						{
							cityText.setText( obj.toString( ) );
							return;
						}
					}
					cityText.setText( "" );
				}
			} );
			regionCodeCombo.addModifyListener( new ModifyListener( ) {

				public void modifyText( ModifyEvent e )
				{
					Iterator iter = cityProperty.keySet( ).iterator( );
					while ( iter.hasNext( ) )
					{
						Object obj = iter.next( );
						if ( regionCodeCombo.getText( )
								.equals( cityProperty.get( obj ) ) )
						{
							regionText.setText( obj.toString( ) );
							return;
						}
					}
					regionText.setText( "" );
				}
			} );
		}
	}

	private void createLeftFindArea( )
	{
		TableWrapData td;
		Section patchSection = WidgetUtil.getToolkit( )
				.createSection( container.getBody( ), Section.EXPANDED );
		td = new TableWrapData( TableWrapData.FILL );
		patchSection.setLayoutData( td );
		patchSection.setText( "通过名称查询代码：" );
		WidgetUtil.getToolkit( ).createCompositeSeparator( patchSection );
		Composite patchClient = WidgetUtil.getToolkit( )
				.createComposite( patchSection );
		GridLayout layout = new GridLayout( );
		layout.numColumns = 3;
		patchClient.setLayout( layout );

		GridData gd = new GridData( );
		gd.widthHint = 200;

		WidgetUtil.getToolkit( ).createLabel( patchClient, "查询将军代码：" );
		generalCombo = WidgetUtil.getToolkit( ).createCCombo( patchClient,
				SWT.READ_ONLY );
		gd = new GridData( );
		gd.widthHint = 200;
		generalCombo.setLayoutData( gd );
		generalCodeText = WidgetUtil.createText( patchClient, "" );
		gd = new GridData( );
		gd.widthHint = 200;
		generalCodeText.setLayoutData( gd );

		WidgetUtil.getToolkit( ).createLabel( patchClient, "查询势力代码：" );
		factionCombo = WidgetUtil.getToolkit( ).createCCombo( patchClient,
				SWT.READ_ONLY );
		gd = new GridData( );
		gd.widthHint = 200;
		factionCombo.setLayoutData( gd );
		factionCodeText = WidgetUtil.createText( patchClient, "" );
		gd = new GridData( );
		gd.widthHint = 200;
		factionCodeText.setLayoutData( gd );

		WidgetUtil.getToolkit( ).createLabel( patchClient, "查询城市代码：" );
		cityCombo = WidgetUtil.getToolkit( ).createCCombo( patchClient,
				SWT.READ_ONLY );
		gd = new GridData( );
		gd.widthHint = 200;
		cityCombo.setLayoutData( gd );
		cityCodeText = WidgetUtil.createText( patchClient, "" );
		gd = new GridData( );
		gd.widthHint = 200;
		cityCodeText.setLayoutData( gd );

		WidgetUtil.getToolkit( ).createLabel( patchClient, "查询地区代码：" );
		regionCombo = WidgetUtil.getToolkit( ).createCCombo( patchClient,
				SWT.READ_ONLY );
		gd = new GridData( );
		gd.widthHint = 200;
		regionCombo.setLayoutData( gd );
		regionCodeText = WidgetUtil.createText( patchClient, "" );
		gd = new GridData( );
		gd.widthHint = 200;
		regionCodeText.setLayoutData( gd );

		WidgetUtil.getToolkit( ).createLabel( patchClient, "查询兵种代码：" );
		soldierCombo = WidgetUtil.getToolkit( ).createCCombo( patchClient,
				SWT.READ_ONLY );
		gd = new GridData( );
		gd.widthHint = 200;
		soldierCombo.setLayoutData( gd );
		soldierCodeText = WidgetUtil.createText( patchClient, "" );
		gd = new GridData( );
		gd.widthHint = 200;
		soldierCodeText.setLayoutData( gd );

		patchSection.setClient( patchClient );
	}

	private void createRightFindArea( )
	{
		TableWrapData td;
		Section patchSection = WidgetUtil.getToolkit( )
				.createSection( container.getBody( ), Section.EXPANDED );
		td = new TableWrapData( TableWrapData.FILL );
		patchSection.setLayoutData( td );
		patchSection.setText( "通过代码查询名称：" );
		WidgetUtil.getToolkit( ).createCompositeSeparator( patchSection );
		Composite patchClient = WidgetUtil.getToolkit( )
				.createComposite( patchSection );
		GridLayout layout = new GridLayout( );
		layout.numColumns = 3;
		patchClient.setLayout( layout );

		WidgetUtil.getToolkit( ).createLabel( patchClient, "查询将军名称：" );
		generalCodeCombo = WidgetUtil.getToolkit( ).createCCombo( patchClient,
				SWT.READ_ONLY );
		GridData gd = new GridData( );
		gd.widthHint = 200;
		generalCodeCombo.setLayoutData( gd );
		generalText = WidgetUtil.createText( patchClient, "" );
		gd = new GridData( );
		gd.widthHint = 200;
		generalText.setLayoutData( gd );

		WidgetUtil.getToolkit( ).createLabel( patchClient, "查询势力名称：" );
		factionCodeCombo = WidgetUtil.getToolkit( ).createCCombo( patchClient,
				SWT.READ_ONLY );
		gd = new GridData( );
		gd.widthHint = 200;
		factionCodeCombo.setLayoutData( gd );
		factionText = WidgetUtil.createText( patchClient, "" );
		gd = new GridData( );
		gd.widthHint = 200;
		factionText.setLayoutData( gd );

		WidgetUtil.getToolkit( ).createLabel( patchClient, "查询城市名称：" );
		cityCodeCombo = WidgetUtil.getToolkit( ).createCCombo( patchClient,
				SWT.READ_ONLY );
		gd = new GridData( );
		gd.widthHint = 200;
		cityCodeCombo.setLayoutData( gd );
		cityText = WidgetUtil.createText( patchClient, "" );
		gd = new GridData( );
		gd.widthHint = 200;
		cityText.setLayoutData( gd );

		WidgetUtil.getToolkit( ).createLabel( patchClient, "查询地区名称：" );
		regionCodeCombo = WidgetUtil.getToolkit( ).createCCombo( patchClient,
				SWT.READ_ONLY );
		gd = new GridData( );
		gd.widthHint = 200;
		regionCodeCombo.setLayoutData( gd );
		regionText = WidgetUtil.createText( patchClient, "" );
		gd = new GridData( );
		gd.widthHint = 200;
		regionText.setLayoutData( gd );

		WidgetUtil.getToolkit( ).createLabel( patchClient, "查询兵种名称：" );
		soldierCodeCombo = WidgetUtil.getToolkit( ).createCCombo( patchClient,
				SWT.READ_ONLY );
		gd = new GridData( );
		gd.widthHint = 200;
		soldierCodeCombo.setLayoutData( gd );
		soldierText = WidgetUtil.createText( patchClient, "" );
		gd = new GridData( );
		gd.widthHint = 200;
		soldierText.setLayoutData( gd );

		patchSection.setClient( patchClient );
	}

	private void createTitle( )
	{
		WidgetUtil.createFormText( container.getBody( ),
				"本页面用于查询将军、势力、城市、地区、特殊兵种代码及名称。" );
	}

	public String getDisplayName( )
	{
		return "代码查询";
	}

	class CustomComparator implements Comparator
	{

		private Properties properties;

		public CustomComparator( Properties properties )
		{
			this.properties = properties;
		}

		public int compare( Object arg0, Object arg1 )
		{
			String code0 = properties.getProperty( (String) arg0 );
			String code1 = properties.getProperty( (String) arg1 );
			String[] code0s = code0.split( "-" );
			String[] code1s = code1.split( "-" );
			code0 = code0s[code0s.length - 1];
			code1 = code1s[code1s.length - 1];
			return code0.compareToIgnoreCase( code1 );
		}
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

		int index = factionCodeCombo.getSelectionIndex( );

		String[] items = (String[]) factionMap.getKeyList( )
				.toArray( new String[0] );
		Arrays.sort( items );
		factionCodeCombo.removeAll( );
		factionCodeCombo.setItems( items );
		factionCodeCombo.select( index );
	}
}
