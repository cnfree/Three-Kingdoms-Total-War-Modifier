
package com.actuate.development.tool.dialog;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableColumn;
import org.sf.feeling.swt.win32.internal.extension.util.ImageCache;

import com.actuate.development.tool.model.Changelist;
import com.actuate.development.tool.util.AutoResizeTableLayout;

public class ChangelistDialog extends TitleAreaDialog
{

	private IStructuredContentProvider contentProvider = new IStructuredContentProvider( ) {

		public void dispose( )
		{
		}

		public void inputChanged( Viewer viewer, Object oldInput,
				Object newInput )
		{
		}

		public Object[] getElements( Object input )
		{
			if ( input instanceof List )
			{
				return ( (List) input ).toArray( );
			}
			else if ( input instanceof Object[] )
			{
				return (Object[]) input;
			}
			return new Object[0];
		}
	};

	private ITableLabelProvider labelProvider = new ITableLabelProvider( ) {

		public Image getColumnImage( Object element, int columnIndex )
		{
			return null;
		}

		public String getColumnText( Object element, int columnIndex )
		{
			Changelist info = (Changelist) element;
			if ( columnIndex == 0 )
			{
				return info.getId( );
			}
			else if ( columnIndex == 1 )
			{
				return info.getSubmitTime( );
			}
			else if ( columnIndex == 2 )
			{
				return info.getSubmitBy( );
			}
			else if ( columnIndex == 3 )
			{
				return info.getDescription( );
			}
			return ""; //$NON-NLS-1$
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

	};

	private String result;
	private TableViewer viewer;
	private List<Changelist> changelists;

	public ChangelistDialog( Shell parentShell )
	{
		super( parentShell );
		setShellStyle( 0x10C70 | getDefaultOrientation( ) );
	}

	public void setInput( String content )
	{
		if ( content != null )
		{
			this.changelists = parseChangelist( content );
		}
	}

	private List<Changelist> parseChangelist( String content )
	{
		List<Changelist> changelists = new ArrayList<Changelist>( );
		Pattern pattern = Pattern.compile( "Change\\s+\\d+\\s+on.+?by\\s+.+?@.+",
				Pattern.CASE_INSENSITIVE );
		Matcher matcher = pattern.matcher( content );

		while ( matcher.find( ) )
		{
			String info = matcher.group( );
			Changelist list = new Changelist( );
			list.setId( info.replaceFirst( "Change\\s+", "" )
					.replaceFirst( "\\s+on.+?by\\s+.+?@.+", "" )
					.trim( ) );
			list.setSubmitTime( info.replaceFirst( "Change\\s+\\d+\\s+on", "" )
					.replaceFirst( "by\\s+.+?@.+", "" )
					.trim( ) );
			list.setSubmitBy( info.replaceFirst( "Change\\s+\\d+\\s+on.+?by\\s+",
					"" )
					.replaceFirst( "@.+", "" ) );
			changelists.add( list );
		}

		String[] descriptions = content.split( "Change\\s+\\d+\\s+on.+?by\\s+.+?@.+" );
		for ( int i = 1; i <= changelists.size( ); i++ )
		{
			changelists.get( i - 1 ).setDescription( descriptions[i].trim( ) );
		}
		return changelists;
	}

	protected void configureShell( Shell shell )
	{
		super.configureShell( shell );
		shell.setText( "Changelist Selection" );
		shell.setImages( new Image[]{
				ImageCache.getImage( "/icons/actuate_16.png" ),
				ImageCache.getImage( "/icons/actuate_32.png" ),
				ImageCache.getImage( "/icons/actuate_48.png" )
		} );
	}

	protected Control createDialogArea( Composite parent )
	{

		this.setTitle( "Changelist Selection" );
		this.setMessage( "Select a changelist." );
		Composite contents = (Composite) super.createDialogArea( parent );

		viewer = new TableViewer( contents, SWT.BORDER
				| SWT.FULL_SELECTION
				| SWT.SINGLE
				| SWT.VIRTUAL );
		viewer.getTable( ).setHeaderVisible( true );
		String[] columns = new String[]{
				"Changelist", "Date Submitted", "Submitted By", "Description"
		};

		ColumnViewerToolTipSupport.enableFor( viewer );

		TableColumn[] tableColumns = new TableColumn[4];
		for ( int i = 0; i < columns.length; i++ )
		{
			final int index = i;
			TableViewerColumn viewerColumn = new TableViewerColumn( viewer,
					SWT.NONE );
			tableColumns[i] = viewerColumn.getColumn( );
			tableColumns[i].setResizable( true );
			tableColumns[i].setMoveable( true );
			if ( columns[i] != null )
			{
				tableColumns[i].setText( columns[i] );
			}

			viewerColumn.setLabelProvider( new CellLabelProvider( ) {

				public String getToolTipText( Object element )
				{
					Changelist list = (Changelist) element;
					if ( index == 3 )
					{
						return list.getDescription( );
					}
					return null;
				}

				@Override
				public Point getToolTipShift( Object object )
				{
					return new Point( 20, 0 );
				}

				@Override
				public int getToolTipDisplayDelayTime( Object object )
				{
					return 100; // msec
				}

				@Override
				public int getToolTipTimeDisplayed( Object object )
				{
					return 5000; // msec
				}

				@Override
				public void update( ViewerCell cell )
				{
					Changelist list = (Changelist) cell.getElement( );
					if ( index == 0 )
					{
						cell.setText( list.getId( ) );
					}
					if ( index == 1 )
					{
						cell.setText( list.getSubmitTime( ) );
					}
					if ( index == 2 )
					{
						cell.setText( list.getSubmitBy( ) );
					}
					if ( index == 3 )
					{
						cell.setText( list.getDescription( ) );
					}
				}

			} );
		}

		TableLayout layout = new AutoResizeTableLayout( viewer.getTable( ) );
		layout.addColumnData( new ColumnWeightData( 15, true ) );
		layout.addColumnData( new ColumnWeightData( 20, true ) );
		layout.addColumnData( new ColumnWeightData( 15, true ) );
		layout.addColumnData( new ColumnWeightData( 50, true ) );

		viewer.getTable( ).setLayout( layout );

		viewer.setContentProvider( contentProvider );
		// viewer.setLabelProvider( labelProvider );
		GridData gd = new GridData( GridData.FILL_BOTH );
		gd.widthHint = 700;
		gd.heightHint = 300;
		viewer.getTable( ).setLayoutData( gd );
		viewer.addSelectionChangedListener( new ISelectionChangedListener( ) {

			public void selectionChanged( SelectionChangedEvent arg0 )
			{
				checkStatus( );
			}
		} );

		viewer.addDoubleClickListener( new IDoubleClickListener( ) {

			public void doubleClick( DoubleClickEvent paramDoubleClickEvent )
			{
				checkStatus( );
				ChangelistDialog.this.okPressed( );
			}
		} );

		if ( changelists != null )
		{
			viewer.setInput( changelists );
		}

		checkStatus( );

		return contents;
	}

	protected void createButtonsForButtonBar( Composite parent )
	{
		super.createButtonsForButtonBar( parent );
		checkStatus( );
	}

	protected void checkStatus( )
	{
		if ( viewer.getSelection( ) == null )
		{
			setErrorMessage( "Must select a changelist." );
			setOkButtonStatus( false );
			result = null;
			return;
		}
		if ( viewer.getSelection( ) instanceof StructuredSelection )
		{
			if ( ( (StructuredSelection) viewer.getSelection( ) ).isEmpty( ) )
			{
				setErrorMessage( "Must select a changelist." );
				setOkButtonStatus( false );
				result = null;
				return;
			}
			else
			{
				result = ( (Changelist) ( (StructuredSelection) viewer.getSelection( ) ).getFirstElement( ) ).getId( );
			}
		}
		setErrorMessage( null );
		setOkButtonStatus( true );
	}

	private void setOkButtonStatus( boolean enabled )
	{
		if ( getOKButton( ) != null )
		{
			getOKButton( ).setEnabled( enabled );
		}
	}

	public String getResult( )
	{
		return result;
	}

	protected void okPressed( )
	{
		if ( getErrorMessage( ) != null )
			return;
		super.okPressed( );
	}
}
