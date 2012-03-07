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

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.tools.zip.ZipFileInfo;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.sf.feeling.sanguo.patch.Patch;
import org.sf.feeling.sanguo.patch.util.BakUtil;
import org.sf.feeling.sanguo.patch.widget.WidgetUtil;

public class BakAndRestorePage extends SimpleTabPage implements Listener
{

	private String date;

	private TableViewer bakFileTable;

	private boolean IO_WAIT = false;

	private Button restoreButton;

	private Button applyRestoreButton;

	private Button initBakButton;

	private Button initApplyBakButton;

	private Button restoreBakButton;

	private Button applyRestoreBakButton;

	class TableContentProvider implements IStructuredContentProvider
	{

		public Object[] getElements( Object parent )
		{
			List list = new ArrayList( );

			if ( parent instanceof File )
			{
				File folder = (File) parent;
				if ( folder.exists( ) )
				{
					File[] children = folder.listFiles( );
					if ( children != null && date != null )
					{
						for ( int i = 0; i < children.length; i++ )
						{
							File file = children[i];
							if ( file.getName( ).startsWith( date )
									&& file.getName( ).endsWith( ".zip" ) )
							{
								list.add( file );
							}
						}
					}
				}
			}

			return list.toArray( );
		}

		public void dispose( )
		{
			// TODO Auto-generated method stub

		}

		public void inputChanged( Viewer arg0, Object arg1, Object arg2 )
		{
			// TODO Auto-generated method stub

		}

	}

	class TableLabelProvider implements ITableLabelProvider
	{

		public Image getColumnImage( Object element, int columnIndex )
		{
			return null;
		}

		public String getColumnText( Object element, int columnIndex )
		{
			if ( element instanceof File && ( (File) element ).exists( ) )
			{
				File file = (File) element;
				if ( columnIndex == 0 )
				{
					String dateTime = file.getName( ).replaceAll( "\\.zip", "" );
					SimpleDateFormat format = new SimpleDateFormat( "yyyyMMddHHmmss" );
					try
					{
						Date date = format.parse( dateTime );
						format = new SimpleDateFormat( "HH时mm分" );
						return format.format( date );
					}
					catch ( ParseException e )
					{
					}
				}
				else if ( columnIndex == 1 )
				{
					try
					{
						ZipFileInfo zipFile = new ZipFileInfo( file, "GBK" );
						String comment = zipFile.getComment( );
						zipFile.close( );
						if ( comment != null )
							return comment;
					}
					catch ( IOException e )
					{
						e.printStackTrace( );
					}
				}
			}
			return "";
		}

		public void addListener( ILabelProviderListener listener )
		{
		}

		public void dispose( )
		{
		}

		public boolean isLabelProperty( Object element, String property )
		{
			return false;
		}

		public void removeListener( ILabelProviderListener listener )
		{
		}

	}

	public void buildUI( Composite parent )
	{
		super.buildUI( parent );
		TableWrapLayout layout = new TableWrapLayout( );
		layout.leftMargin = 10;
		layout.rightMargin = 10;
		layout.topMargin = 15;
		layout.verticalSpacing = 20;
		container.getBody( ).setLayout( layout );

		initTime( );

		createTitle( );
		createPatchArea( );

		FormText noteText = WidgetUtil.createFormText( container.getBody( ),
				"注意：如果还原备份文件后进入游戏出现异常，请尝试换一个版本还原。"
						+ "若是使用原始档还原并导致进入游戏失败，请尝试再次使用原始档还原。" );
		TableWrapData data = new TableWrapData( TableWrapData.FILL );
		data.maxWidth = 600;
		noteText.setLayoutData( data );
	}

	private void initTime( )
	{
		Calendar cal = Calendar.getInstance( TimeZone.getTimeZone( "Etc/GMT-8" ),
				Locale.CHINA );
		SimpleDateFormat format = new SimpleDateFormat( "yyyyMMdd" );
		format.setCalendar( cal );
		date = format.format( cal.getTime( ) );
	}

	private void createPatchArea( )
	{
		TableWrapData td;
		Section bakSection = WidgetUtil.getToolkit( )
				.createSection( container.getBody( ), Section.EXPANDED );
		td = new TableWrapData( TableWrapData.FILL );
		bakSection.setLayoutData( td );
		bakSection.setText( "备份游戏数据" );
		WidgetUtil.getToolkit( ).createCompositeSeparator( bakSection );

		Composite bakComposite = WidgetUtil.getToolkit( )
				.createComposite( bakSection, SWT.NONE );
		GridLayout layout = new GridLayout( );
		layout.numColumns = 3;
		bakComposite.setLayout( layout );
		bakSection.setClient( bakComposite );

		initBakButton = WidgetUtil.getToolkit( ).createButton( bakComposite,
				"设置当前游戏数据作为原始备份档（请务必保证当前游戏数据无异常状态）",
				SWT.CHECK );
		GridData gd = new GridData( GridData.FILL_HORIZONTAL );
		gd.horizontalSpan = 2;
		initBakButton.setLayoutData( gd );

		initApplyBakButton = WidgetUtil.getToolkit( )
				.createButton( bakComposite, SWT.PUSH, true );
		initApplyBakButton.setText( "设置" );
		initApplyBakButton.setEnabled( false );

		initBakButton.addSelectionListener( new SelectionAdapter( ) {

			public void widgetSelected( SelectionEvent e )
			{
				checkButtonStatus( );
			}
		} );

		initApplyBakButton.addSelectionListener( new SelectionAdapter( ) {

			public void widgetSelected( SelectionEvent e )
			{
				IO_WAIT = true;
				initApplyBakButton.setEnabled( false );
				BakUtil.bakToDefaultData( BakAndRestorePage.this );
			}
		} );
		final Button bakButton = WidgetUtil.getToolkit( )
				.createButton( bakComposite, "备份当前游戏数据", SWT.CHECK );

		final Text description = WidgetUtil.getToolkit( )
				.createText( bakComposite, "请输入备份描述", SWT.NONE );
		gd = new GridData( GridData.FILL_HORIZONTAL );
		gd.widthHint = 200;
		description.setLayoutData( gd );
		description.setEnabled( false );

		final Button applyBakButton = WidgetUtil.getToolkit( )
				.createButton( bakComposite, SWT.PUSH, true );
		applyBakButton.setText( "备份" );
		applyBakButton.setEnabled( false );
		applyBakButton.addSelectionListener( new SelectionAdapter( ) {

			public void widgetSelected( SelectionEvent e )
			{
				BakUtil.bakData( description.getText( ).trim( ) );
				bakFileTable.refresh( );
			}
		} );

		bakButton.addSelectionListener( new SelectionAdapter( ) {

			public void widgetSelected( SelectionEvent e )
			{
				description.setEnabled( bakButton.getSelection( ) );
				applyBakButton.setEnabled( bakButton.getSelection( ) );
			}
		} );

		final Button clearBakButton = WidgetUtil.getToolkit( )
				.createButton( bakComposite, "删除全部历史备份记录（不包含原始档）", SWT.CHECK );
		gd = new GridData( GridData.FILL_HORIZONTAL );
		gd.widthHint = 200;
		gd.horizontalSpan = 2;
		clearBakButton.setLayoutData( gd );

		final Button applyclearBakButton = WidgetUtil.getToolkit( )
				.createButton( bakComposite, SWT.PUSH, true );
		applyclearBakButton.setText( "删除" );
		applyclearBakButton.setEnabled( false );

		clearBakButton.addSelectionListener( new SelectionAdapter( ) {

			public void widgetSelected( SelectionEvent e )
			{
				applyclearBakButton.setEnabled( clearBakButton.getSelection( ) );
			}
		} );
		applyclearBakButton.addSelectionListener( new SelectionAdapter( ) {

			public void widgetSelected( SelectionEvent e )
			{
				File folder = new File( BakUtil.bakFolderPath );
				if ( folder.exists( ) && folder.isDirectory( ) )
				{
					File[] children = folder.listFiles( );
					if ( children != null )
					{
						for ( int i = 0; i < children.length; i++ )
						{
							File file = children[i];
							if ( file.isFile( )
									&& !file.getAbsolutePath( )
											.equals( BakUtil.defalutBakFilePath ) )
							{
								file.delete( );
							}
						}
					}
				}

				folder = new File( BakUtil.bakIncreaseFolderPath );
				if ( folder.exists( ) && folder.isDirectory( ) )
				{
					File[] children = folder.listFiles( );
					if ( children != null )
					{
						for ( int i = 0; i < children.length; i++ )
						{
							File file = children[i];
							if ( file.isFile( )
									&& !file.getAbsolutePath( )
											.equals( BakUtil.defalutResourceBakFilePath ) )
							{
								file.delete( );
							}
						}
					}
				}

				File file = new File( BakUtil.patchFolderPath
						+ "\\bak.properties" );
				if ( file.exists( ) )
					file.delete( );

				refreshTable( );
			}
		} );

		restoreButton = WidgetUtil.getToolkit( ).createButton( bakComposite,
				"还原游戏数据至原始档状态（视机器性能不同，可能需要等待几分钟）",
				SWT.CHECK );
		gd = new GridData( GridData.FILL_HORIZONTAL );
		gd.widthHint = 200;
		gd.horizontalSpan = 2;
		restoreButton.setLayoutData( gd );

		applyRestoreButton = WidgetUtil.getToolkit( )
				.createButton( bakComposite, SWT.PUSH, true );
		applyRestoreButton.setText( "还原" );
		applyRestoreButton.setEnabled( false );

		restoreButton.addSelectionListener( new SelectionAdapter( ) {

			public void widgetSelected( SelectionEvent e )
			{
				checkButtonStatus( );
			}
		} );

		applyRestoreButton.addSelectionListener( new SelectionAdapter( ) {

			public void widgetSelected( SelectionEvent e )
			{
				IO_WAIT = true;
				applyRestoreButton.setEnabled( false );
				BakUtil.restoreDefaultBakFile( BakAndRestorePage.this );
			}
		} );

		Section restoreSection = WidgetUtil.getToolkit( )
				.createSection( container.getBody( ), Section.EXPANDED );
		td = new TableWrapData( TableWrapData.FILL );
		restoreSection.setLayoutData( td );
		restoreSection.setText( "还原备份数据" );
		WidgetUtil.getToolkit( ).createCompositeSeparator( restoreSection );

		Composite restoreComposite = WidgetUtil.getToolkit( )
				.createComposite( restoreSection, SWT.NONE );
		layout = new GridLayout( );
		layout.numColumns = 2;
		restoreComposite.setLayout( layout );

		final DateTime calendar = new DateTime( restoreComposite, SWT.CALENDAR );
		calendar.addSelectionListener( new SelectionAdapter( ) {

			public void widgetSelected( SelectionEvent e )
			{
				date = new String( );
				date += calendar.getYear( );
				if ( calendar.getMonth( ) < 9 )
				{
					date += ( "0" + ( calendar.getMonth( ) + 1 ) );
				}
				else
					date += ( calendar.getMonth( ) + 1 );
				if ( calendar.getDay( ) < 10 )
				{
					date += ( "0" + calendar.getDay( ) );
				}
				else
					date += calendar.getDay( );
				refreshTable( );
			}

		} );

		final Table table = WidgetUtil.getToolkit( )
				.createTable( restoreComposite,
						SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION );
		bakFileTable = new TableViewer( table );
		table.setLinesVisible( false );
		table.setHeaderVisible( true );
		bakFileTable.setContentProvider( new TableContentProvider( ) );
		bakFileTable.setLabelProvider( new TableLabelProvider( ) );
		String[] columns = new String[]{
				"备份时间", "描述"
		};

		int[] widths = new int[]{
				90, 200
		};

		for ( int i = 0; i < columns.length; i++ )
		{
			TableColumn column = new TableColumn( bakFileTable.getTable( ),
					SWT.LEFT );
			column.setResizable( columns[i] != null );
			if ( columns[i] != null )
			{
				column.setText( columns[i] );
			}
			column.setWidth( widths[i] );
		}
		bakFileTable.setColumnProperties( columns );
		gd = new GridData( GridData.FILL_BOTH );
		gd.heightHint = 100;
		table.setLayoutData( gd );

		bakFileTable.setInput( new File( BakUtil.bakFolderPath ) );

		Composite buttonGroup = WidgetUtil.getToolkit( )
				.createComposite( restoreComposite );
		gd = new GridData( GridData.FILL_HORIZONTAL );
		gd.horizontalSpan = 2;
		buttonGroup.setLayoutData( gd );

		layout = new GridLayout( );
		layout.numColumns = 2;
		layout.marginWidth = layout.marginHeight = 0;
		buttonGroup.setLayout( layout );

		restoreBakButton = WidgetUtil.getToolkit( ).createButton( buttonGroup,
				"使用选中备份文件还原",
				SWT.CHECK );
		gd = new GridData( );
		gd.widthHint = 300;
		restoreBakButton.setLayoutData( gd );

		applyRestoreBakButton = WidgetUtil.getToolkit( )
				.createButton( buttonGroup, SWT.PUSH, true );
		applyRestoreBakButton.setText( "还原" );
		applyRestoreBakButton.setEnabled( false );
		applyRestoreBakButton.addSelectionListener( new SelectionAdapter( ) {

			public void widgetSelected( SelectionEvent e )
			{
				if ( bakFileTable.getSelection( ) instanceof StructuredSelection )
				{
					StructuredSelection selection = ( (StructuredSelection) bakFileTable.getSelection( ) );
					if ( !selection.isEmpty( )
							&& selection.getFirstElement( ) instanceof File )
					{
						File file = (File) selection.getFirstElement( );
						if ( file.exists( ) )
						{
							IO_WAIT = true;
							applyRestoreBakButton.setEnabled( false );
							BakUtil.restoreBakFile( file,
									BakAndRestorePage.this );
						}
					}
				}
			}
		} );
		restoreBakButton.addSelectionListener( new SelectionAdapter( ) {

			public void widgetSelected( SelectionEvent e )
			{
				applyRestoreBakButton.setEnabled( restoreBakButton.getSelection( ) );
			}
		} );

		final Button deleteBakButton = WidgetUtil.getToolkit( )
				.createButton( buttonGroup, "删除选中备份文件", SWT.CHECK );
		gd = new GridData( );
		gd.widthHint = 300;
		deleteBakButton.setLayoutData( gd );

		final Button applyDeleteBakButton = WidgetUtil.getToolkit( )
				.createButton( buttonGroup, SWT.PUSH, true );
		applyDeleteBakButton.setText( "删除" );
		applyDeleteBakButton.setEnabled( false );

		deleteBakButton.addSelectionListener( new SelectionAdapter( ) {

			public void widgetSelected( SelectionEvent e )
			{
				applyDeleteBakButton.setEnabled( deleteBakButton.getSelection( ) );
			}
		} );

		applyDeleteBakButton.addSelectionListener( new SelectionAdapter( ) {

			public void widgetSelected( SelectionEvent e )
			{
				if ( bakFileTable.getSelection( ) instanceof StructuredSelection )
				{
					StructuredSelection selection = ( (StructuredSelection) bakFileTable.getSelection( ) );
					if ( !selection.isEmpty( )
							&& selection.getFirstElement( ) instanceof File )
					{
						int index = bakFileTable.getTable( )
								.getSelectionIndex( );
						File file = (File) selection.getFirstElement( );
						if ( file.exists( ) )
							file.delete( );
						bakFileTable.refresh( );
						if ( index < bakFileTable.getTable( ).getItemCount( ) )
						{
							bakFileTable.getTable( ).select( index );
						}
						else
						{
							if ( bakFileTable.getTable( ).getItemCount( ) > 0 )
								bakFileTable.getTable( )
										.select( bakFileTable.getTable( )
												.getItemCount( ) - 1 );
						}
					}
				}
			}
		} );

		restoreSection.setClient( restoreComposite );

	}

	private void createTitle( )
	{
		WidgetUtil.createFormText( container.getBody( ),
				"本页面用于备份和还原游戏内重要数据文件，若修改处过多，还原可能需要重新开档。" );
	}

	public String getDisplayName( )
	{
		return "备份与还原";
	}

	private void refreshTable( )
	{
		if ( bakFileTable != null
				&& bakFileTable.getTable( ) != null
				&& !bakFileTable.getTable( ).isDisposed( ) )
			bakFileTable.refresh( );
	}

	public void refresh( )
	{
		super.refresh( );
		refreshTable( );
	}

	public void handleEvent( Event event )
	{
		if ( Patch.getInstance( ).getShell( ).isDisposed( ) )
			return;
		if ( event.type == BakUtil.IO_FINISH )
		{
			IO_WAIT = false;
		}
		checkButtonStatus( );
	}

	private void checkButtonStatus( )
	{
		if ( restoreButton.getSelection( ) && !applyRestoreButton.isEnabled( ) )
			applyRestoreButton.setEnabled( restoreButton.getSelection( )
					&& !IO_WAIT );
		else if ( initBakButton.getSelection( )
				&& !initApplyBakButton.isEnabled( ) )
			initApplyBakButton.setEnabled( initBakButton.getSelection( )
					&& !IO_WAIT );
		else if ( restoreBakButton.getSelection( )
				&& !applyRestoreBakButton.isEnabled( ) )
			applyRestoreBakButton.setEnabled( restoreBakButton.getSelection( )
					&& !IO_WAIT );
	}
}
