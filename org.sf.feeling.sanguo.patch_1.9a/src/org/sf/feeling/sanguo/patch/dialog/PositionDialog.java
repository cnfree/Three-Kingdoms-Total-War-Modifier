
package org.sf.feeling.sanguo.patch.dialog;

import org.eclipse.jface.dialogs.BaseDialog;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.sf.feeling.sanguo.patch.util.ChangeCode;
import org.sf.feeling.sanguo.patch.util.UnitUtil;
import org.sf.feeling.sanguo.patch.widget.FormWidgetFactory;
import org.sf.feeling.sanguo.patch.widget.WidgetUtil;
import org.sf.feeling.swt.win32.extension.util.SortMap;
import org.sf.feeling.swt.win32.extension.widgets.ShellWrapper;

public class PositionDialog extends BaseDialog
{

	SortMap generalMap = UnitUtil.getAvailableGenerals( );

	public PositionDialog( String title )
	{
		super( title );
	}

	protected Control createDialogArea( Composite parent )
	{
		Composite composite = new Composite( parent, SWT.NONE );
		composite.setLayoutData( new GridData( GridData.FILL_BOTH ) );

		GridLayout layout = new GridLayout( );
		layout.numColumns = 2;
		layout.marginWidth = layout.marginHeight = 30;
		composite.setLayout( layout );

		WidgetUtil.getToolkit( ).createLabel( composite, "选择相邻武将：" );
		generalCombo = WidgetUtil.getToolkit( ).createCCombo( composite );
		for ( int i = 0; i < generalMap.size( ); i++ )
		{
			String generalName = ChangeCode.toLong( (String) generalMap.get( i ) );
			generalCombo.add( generalName );
		}
		GridData gd = new GridData( );
		gd.widthHint = 200;
		generalCombo.setLayoutData( gd );

		return composite;
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

	protected void okPressed( )
	{
		if ( generalCombo.getSelectionIndex( ) != -1 )
			general = (String) generalMap.getKeyList( )
					.get( generalCombo.getSelectionIndex( ) );

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
