
package org.sf.feeling.sanguo.patch.dialog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.forms.widgets.FormText;
import org.sf.feeling.sanguo.patch.util.BakUtil;
import org.sf.feeling.sanguo.patch.util.BaowuParser;
import org.sf.feeling.sanguo.patch.util.ChangeCode;
import org.sf.feeling.sanguo.patch.util.CustomComparator;
import org.sf.feeling.sanguo.patch.util.FileUtil;
import org.sf.feeling.sanguo.patch.util.GeneralParser;
import org.sf.feeling.sanguo.patch.util.PinyinComparator;
import org.sf.feeling.sanguo.patch.util.UnitUtil;
import org.sf.feeling.sanguo.patch.widget.WidgetUtil;
import org.sf.feeling.swt.win32.extension.util.SortMap;

public class BaowuHolderModify
{

	private SortMap baowuProperty = FileUtil.loadProperties( "baowu" );
	private CCombo generalCombo;
	private Button applyButton;
	private Button restoreButton;
	private CheckboxTableViewer baowuTable;
	private int sortDir = SWT.UP;
	private Composite patchClient;

	private List availableGeneralList;
	private SortMap generalMap;

	class TableContentProvider implements IStructuredContentProvider
	{

		public Object[] getElements( Object parent )
		{
			List list = new ArrayList( );
			if ( parent instanceof Map )
			{
				list.addAll( ( (Map) parent ).entrySet( ) );
				Object[] entrys = (Object[]) baowuTable.getCheckedElements( );
				if ( entrys != null )
				{
					for ( int i = 0; i < entrys.length; i++ )
					{
						List excludes = BaowuParser.getBaowuExcludes( (String) baowuProperty.get( (String) ( (Entry) entrys[i] ).getKey( ) ) );
						for ( int j = 0; j < excludes.size( ); j++ )
						{
							for ( int z = 0; z < list.size( ); z++ )
							{
								Entry entry = (Entry) list.get( z );
								if ( baowuProperty.get( (String) entry.getKey( ) )
										.equals( excludes.get( j )
												.toString( )
												.trim( ) ) )
								{
									list.remove( z );
									break;
								}
							}
						}
					}
				}
				Collections.sort( list, new CustomComparator( baowuProperty ) {

					public int compare( Object arg0, Object arg1 )
					{
						Entry value0 = (Entry) arg0;
						Entry value1 = (Entry) arg1;
						String code0 = null;
						String code1 = null;
						if ( properties != null )
						{
							code0 = (String) properties.get( ( value0.getKey( ).toString( ) ) );
							code1 = (String) properties.get( ( value1.getKey( ).toString( ) ) );
						}
						else
						{
							code0 = (String) arg0;
							code1 = (String) arg1;
						}
						return code0.compareToIgnoreCase( code1 );
					}
				} );
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
			Entry value = (Entry) element;
			switch ( columnIndex )
			{
				case 1 :
					return (String) value.getKey( );
				case 2 :
					if ( value.getValue( ) != null )
						return ChangeCode.toLong( generalMap.get( value.getValue( ) )
								.toString( ) );
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

	public class TableSorter extends ViewerSorter
	{

		private int sortDir;

		private TableSorter( int sortDir )
		{
			this.sortDir = sortDir;
		}

		public int compare( Viewer viewer, Object e1, Object e2 )
		{
			Entry value1 = (Entry) e1;
			Entry value2 = (Entry) e2;
			String general1 = value1.getValue( ) == null ? ""
					: (String) value1.getValue( );
			String general2 = value2.getValue( ) == null ? ""
					: (String) value2.getValue( );
			if ( sortDir == SWT.UP )
			{
				return general1.compareTo( general2 );
			}
			else if ( sortDir == SWT.DOWN )
			{
				return general2.compareTo( general1 );
			}
			return 0;
		}
	}

	private boolean isMemory;

	public BaowuHolderModify( boolean isMemory )
	{
		this.isMemory = isMemory;

		generalMap = UnitUtil.getGenerals( );
		availableGeneralList = new ArrayList( );
		availableGeneralList.addAll( generalMap.getKeyList( ) );
		UnitUtil.getAvailableGeneralCodes( availableGeneralList );
		Collections.sort( availableGeneralList, new Comparator( ) {

			public int compare( Object o1, Object o2 )
			{
				String name1 = (String) generalMap.get( o1 );
				String name2 = (String) generalMap.get( o2 );
				return PinyinComparator.compare( name1, name2 );
			}
		} );
	}

	public Composite createModifyControl( Composite parent )
	{
		Composite clientContainer = WidgetUtil.getToolkit( )
				.createComposite( parent );
		GridLayout layout = new GridLayout( );
		layout.numColumns = 4;
		clientContainer.setLayout( layout );

		if ( !isMemory )
		{
			WidgetUtil.getToolkit( ).createLabel( clientContainer, "选择武将：" );

			generalCombo = WidgetUtil.getToolkit( )
					.createCCombo( clientContainer, SWT.READ_ONLY );
			GridData gd = new GridData( );
			gd.widthHint = 150;
			generalCombo.setLayoutData( gd );

			for ( int i = 0; i < availableGeneralList.size( ); i++ )
			{
				String generalName = ChangeCode.toLong( (String) generalMap.get( availableGeneralList.get( i ) ) );
				generalCombo.add( generalName );
			}

			gd = new GridData( );
			gd.horizontalSpan = 2;
			WidgetUtil.getToolkit( )
					.createLabel( clientContainer, "" )
					.setLayoutData( gd );

		}
		patchClient = WidgetUtil.getToolkit( )
				.createComposite( clientContainer );
		layout = new GridLayout( );
		layout.marginWidth = 1;
		layout.marginHeight = 1;
		layout.numColumns = 1;
		patchClient.setLayout( layout );

		GridData gd = new GridData( GridData.FILL_BOTH );
		gd.horizontalSpan = 4;
		patchClient.setLayoutData( gd );
		if ( !isMemory )
			patchClient.setEnabled( false );

		final Table table = WidgetUtil.getToolkit( ).createTable( patchClient,
				SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION | SWT.CHECK );
		baowuTable = new CheckboxTableViewer( table );
		table.setLinesVisible( false );
		table.setHeaderVisible( true );
		baowuTable.setContentProvider( new TableContentProvider( ) );
		baowuTable.setLabelProvider( new TableLabelProvider( ) );

		String[] columns = new String[]{
				"", "名称", "持有武将"
		};

		int[] widths = new int[]{
				30, 100, 140
		};

		for ( int i = 0; i < columns.length; i++ )
		{
			TableColumn column = new TableColumn( baowuTable.getTable( ),
					SWT.LEFT );
			column.setResizable( columns[i] != null );
			if ( columns[i] != null )
			{
				column.setText( columns[i] );
			}
			column.setWidth( widths[i] );
		}

		baowuTable.setColumnProperties( columns );
		gd = new GridData( GridData.FILL_BOTH );
		gd.heightHint = 200;
		table.setLayoutData( gd );

		table.getColumn( 2 ).addSelectionListener( new SelectionAdapter( ) {

			public void widgetSelected( SelectionEvent e )
			{
				sortDir = sortDir == SWT.UP ? SWT.DOWN : SWT.UP;
				table.setSortDirection( sortDir );
				baowuTable.setSorter( new TableSorter( sortDir ) );
				table.setSelection( table.getSelectionIndices( ) );
			}
		} );

		baowuTable.addCheckStateListener( new ICheckStateListener( ) {

			public void checkStateChanged( CheckStateChangedEvent event )
			{
				baowuTable.refresh( );
				baowuTable.setSelection( new StructuredSelection( event.getElement( ) ) );
			}
		} );

		initBaowuTable( );

		if ( !isMemory )
		{
			Composite buttonGroup = WidgetUtil.getToolkit( )
					.createComposite( patchClient );
			gd = new GridData( GridData.FILL_HORIZONTAL );
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
					if ( generalCombo.getSelectionIndex( ) != -1 )
					{
						applyButton.setEnabled( false );
						BakUtil.bakData( "武将持有宝物修改：" + generalCombo.getText( ) );
						String general = null;
						if ( generalCombo.getSelectionIndex( ) != -1 )
						{
							general = (String) availableGeneralList.get( generalCombo.getSelectionIndex( ) );
						}
						if ( general != null )
						{
							saveBaowu( general );
							refresh( );
						}
						applyButton.setEnabled( true );
					}
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
					refresh( );
					restoreButton.setEnabled( true );
				}
			} );
			generalCombo.addSelectionListener( new SelectionAdapter( ) {

				public void widgetSelected( SelectionEvent e )
				{
					refresh( );
				}
			} );
			refresh( );
		}
		else
		{
			refresh( );
		}

		FormText noteText = WidgetUtil.createFormText( clientContainer,
				"注意：武将最多只能拥有8个宝物。" );
		gd = new GridData( GridData.FILL_HORIZONTAL );
		gd.horizontalSpan = 4;
		noteText.setLayoutData( gd );
		return clientContainer;
	}

	private void initBaowuTable( )
	{
		Map baowuMap = BaowuParser.getBaowuInfos( );
		List baowuList = new ArrayList( );
		Iterator iter = baowuProperty.keySet( ).iterator( );
		while ( iter.hasNext( ) )
		{
			baowuList.add( iter.next( ) );
		}
		Collections.sort( baowuList, new CustomComparator( baowuProperty ) {

			public int compare( Object arg0, Object arg1 )
			{
				String code0 = null;
				String code1 = null;
				if ( properties != null )
				{
					code0 = (String) properties.get( (String) arg0 );
					code1 = (String) properties.get( (String) arg1 );
				}
				else
				{
					code0 = (String) arg0;
					code1 = (String) arg1;
				}
				return code0.compareToIgnoreCase( code1 );
			}
		} );

		HashMap map = new HashMap( );
		for ( int i = 0; i < baowuList.size( ); i++ )
		{
			String key = (String) baowuProperty.get( (String) baowuList.get( i ) );
			if ( baowuMap.containsKey( key ) )
				map.put( (String) baowuList.get( i ), baowuMap.get( key ) );
			else
				map.put( (String) baowuList.get( i ), null );
		}
		baowuTable.setInput( map );
	}

	public void saveBaowu( String general )
	{
		if ( general != null )
		{
			// FileUtil.bakFile(FileConstants.stratFile, ".unit.patch.bak")
			// .deleteOnExit();
			Object[] elements = baowuTable.getCheckedElements( );
			Map baowuMap = new HashMap( );
			String[] baowus = new String[elements.length];
			for ( int i = 0; i < elements.length; i++ )
			{
				Entry entry = (Entry) elements[i];
				baowus[i] = (String) baowuProperty.get( (String) entry.getKey( ) );
				if ( entry.getValue( ) != null
						&& !general.equals( entry.getValue( ) ) )
				{
					if ( !baowuMap.containsKey( entry.getValue( ) ) )
					{
						baowuMap.put( entry.getValue( ), new ArrayList( ) );
					}
					( (List) baowuMap.get( entry.getValue( ) ) ).add( baowus[i] );
				}
			}
			GeneralParser.removeBaowu( baowuMap );
			GeneralParser.setGeneralBaowus( general, baowus );
		}
	}

	public String[] saveBaowu( )
	{
		Object[] elements = baowuTable.getCheckedElements( );
		String[] baowus = new String[elements.length];
		for ( int i = 0; i < elements.length; i++ )
		{
			Entry entry = (Entry) elements[i];
			baowus[i] = (String) baowuProperty.get( (String) entry.getKey( ) );
		}
		return baowus;
	}

	public void refresh( )
	{
		if ( generalCombo != null )
		{
			String selectGeneral = "";
			int index = generalCombo.getSelectionIndex( );
			if ( index > -1 )
				selectGeneral = (String) generalMap.getKeyList( ).get( index );
			generalMap = UnitUtil.getAvailableGenerals( );
			generalCombo.setItems( new String[0] );
			for ( int i = 0; i < generalMap.size( ); i++ )
			{
				String generalName = ChangeCode.toLong( (String) generalMap.get( i ) );
				generalCombo.add( generalName );
			}
			int newIndex = generalMap.getIndexOf( selectGeneral );
			if ( newIndex != -1 )
			{
				generalCombo.select( newIndex );
			}
		}
		if ( !isMemory )
		{
			if ( generalCombo.getSelectionIndex( ) != -1 )
			{
				patchClient.setEnabled( true );
				String general = null;
				if ( generalCombo.getSelectionIndex( ) != -1 )
				{
					general = (String) availableGeneralList.get( generalCombo.getSelectionIndex( ) );
				}
				if ( general != null )
				{
					initBaowuTable( );
					baowuTable.setAllChecked( false );
					Map baowuMap = (Map) baowuTable.getInput( );
					Iterator iter = baowuMap.entrySet( ).iterator( );
					while ( iter.hasNext( ) )
					{
						Entry baowu = (Entry) iter.next( );
						if ( general.equals( baowu.getValue( ) ) )
						{
							baowuTable.setChecked( baowu, true );
						}
					}
					baowuTable.refresh( );
					return;
				}
			}
			else
			{
				baowuTable.setAllChecked( false );
				patchClient.setEnabled( false );
			}
		}
		else
		{
			initBaowuTable( );
			baowuTable.setAllChecked( false );
			Map baowuMap = (Map) baowuTable.getInput( );
			if ( baowus != null && baowus.length > 0 )
			{
				for ( int i = 0; i < baowus.length; i++ )
				{
					Iterator iter = baowuMap.entrySet( ).iterator( );
					while ( iter.hasNext( ) )
					{
						Entry baowu = (Entry) iter.next( );
						if ( baowus[i].equals( baowuProperty.get( baowu.getKey( ) ) ) )
						{
							baowuTable.setChecked( baowu, true );
							break;
						}
					}
				}
			}
			baowuTable.refresh( );
			return;
		}
	}

	private String[] baowus;

	public void setBaowus( String[] baowus )
	{
		this.baowus = baowus;
		if ( baowuTable != null
				&& baowuTable.getTable( ) != null
				&& !baowuTable.getTable( ).isDisposed( ) )
		{
			refresh( );
		}
	}
}
