
package org.sf.feeling.sanguo.patch.dialog;

import org.eclipse.jface.dialogs.BaseDialog;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.sf.feeling.sanguo.patch.model.General;
import org.sf.feeling.sanguo.patch.util.ChangeCode;
import org.sf.feeling.sanguo.patch.util.UnitUtil;
import org.sf.feeling.sanguo.patch.widget.FormWidgetFactory;
import org.sf.feeling.sanguo.patch.widget.WidgetUtil;
import org.sf.feeling.swt.win32.extension.util.SortMap;
import org.sf.feeling.swt.win32.extension.widgets.ShellWrapper;

public class PositionDialog extends BaseDialog
{

	SortMap generalMap = UnitUtil.getAvailableGenerals( );
	SortMap factionMap = UnitUtil.getFactionMap( );

	public PositionDialog( String title )
	{
		super( title );
	}

	protected Control createDialogArea( Composite parent )
	{
		Composite composite = new Composite( parent, SWT.NONE );
		composite.setLayoutData( new GridData( GridData.FILL_BOTH ) );

		GridLayout layout = new GridLayout( );
		layout.numColumns = 3;
		layout.marginWidth = layout.marginHeight = 30;
		composite.setLayout( layout );

		WidgetUtil.getToolkit( ).createLabel( composite, "选择相邻武将：" );
		factionCombo = WidgetUtil.getToolkit( ).createCCombo( composite );
		for ( int i = 0; i < factionMap.getKeyList( ).size( ); i++ )
		{
			factionCombo.add( (String) factionMap.get( i ) );
		}
		factionCombo.add( "全部势力", 0 );
		factionCombo.addSelectionListener( new SelectionAdapter( ) {

			public void widgetSelected( SelectionEvent e )
			{
				if ( factionCombo.getSelectionIndex( ) == 0 )
				{
					String text = generalCombo.getText( );
					generalCombo.removeAll( );
					for ( int i = 0; i < generalMap.size( ); i++ )
					{
						String generalName = ChangeCode.toLong( (String) generalMap.get( i ) );
						generalCombo.add( generalName );
						if ( generalName.equals( text ) )
							generalCombo.setText( text );
					}
				}
				else if ( factionCombo.getSelectionIndex( ) > 0 )
				{
					String text = generalCombo.getText( );
					generalCombo.removeAll( );
					String faction = (String) factionMap.getKeyList( )
							.get( factionCombo.getSelectionIndex( ) - 1 );
					SortMap modelMap = UnitUtil.getGeneralModels( );
					for ( int i = 0; i < generalMap.size( ); i++ )
					{
						String general = (String) generalMap.getKeyList( )
								.get( i );
						General model = (General) modelMap.get( general );
						if ( faction.equals( model.getFaction( ).trim( ) ) )
						{
							String generalName = ChangeCode.toLong( (String) generalMap.get( general ) );
							generalCombo.add( generalName );
							if ( generalName.equals( text ) )
								generalCombo.setText( text );
						}
					}
				}
				checkEnableStatus( );
			}
		} );

		GridData gd = new GridData( );
		gd.widthHint = 100;
		factionCombo.setLayoutData( gd );

		generalCombo = WidgetUtil.getToolkit( ).createCCombo( composite );
		for ( int i = 0; i < generalMap.size( ); i++ )
		{
			String generalName = ChangeCode.toLong( (String) generalMap.get( i ) );
			generalCombo.add( generalName );

		}
		generalCombo.addSelectionListener( new SelectionAdapter( ) {

			public void widgetSelected( SelectionEvent arg0 )
			{
				checkEnableStatus( );
			}

		} );
		gd = new GridData( );
		gd.widthHint = 100;
		generalCombo.setLayoutData( gd );

		return composite;
	}

	protected Control createButtonBar( Composite parent )
	{
		Control control = super.createButtonBar( parent );
		checkEnableStatus( );
		return control;
	}

	private void checkEnableStatus( )
	{
		if ( getOkButton( ) != null && !getOkButton( ).isDisposed( ) )
		{
			getOkButton( ).setEnabled( generalCombo.getSelectionIndex( ) != -1 );
		}
	}

	protected Control createContents( Composite parent )
	{
		Control control = super.createContents( parent );
		FormWidgetFactory.getInstance( ).paintFormStyle( (Composite) parent );
		FormWidgetFactory.getInstance( ).adapt( (Composite) parent );
		return control;
	}

	private String general = null;
	private CCombo generalCombo;
	private CCombo factionCombo;

	protected void okPressed( )
	{
		int index = generalMap.getValueList( )
				.indexOf( generalCombo.getText( ) );
		if ( index != -1 )
		{
			general = (String) generalMap.getKeyList( ).get( index );
		}

		super.okPressed( );
	}

	public Object getResult( )
	{
		return general;
	}

	public int open( )
	{
		if ( getShell( ) == null )
		{
			create( );
			getShell( ).setImages( ( (Shell) getShell( ).getParent( ) ).getImages( ) );
			ShellWrapper wrapper = new ShellWrapper( getShell( ) );
			wrapper.installTheme( );
			getShell( ).setSize( 360, 160 );
		}
		if ( initDialog( ) )
		{
			return super.open( );
		}
		return Dialog.CANCEL;
	}
}
