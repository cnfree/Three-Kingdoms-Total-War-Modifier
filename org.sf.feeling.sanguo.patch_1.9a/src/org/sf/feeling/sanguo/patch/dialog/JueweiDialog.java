
package org.sf.feeling.sanguo.patch.dialog;

import org.eclipse.jface.dialogs.BaseDialog;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.sf.feeling.sanguo.patch.util.ChangeCode;
import org.sf.feeling.sanguo.patch.widget.FormWidgetFactory;
import org.sf.feeling.sanguo.patch.widget.WidgetUtil;
import org.sf.feeling.swt.win32.extension.widgets.ShellWrapper;

public class JueweiDialog extends BaseDialog
{

	public JueweiDialog( String title )
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

		if ( jueweis != null && jueweis.length > 0 )
		{
			Label[] labels = new Label[jueweis.length];
			texts = new Text[jueweis.length];

			for ( int i = 0; i < jueweis.length; i++ )
			{
				labels[i] = WidgetUtil.getToolkit( ).createLabel( composite,
						"爵位" + ( i + 1 ) + "：" );
				if ( jueweis[i] == null )
					jueweis[i] = "";
				texts[i] = WidgetUtil.getToolkit( ).createText( composite,
						ChangeCode.toLong( jueweis[i] ) );
				GridData gd = new GridData( GridData.FILL_HORIZONTAL );
				gd.widthHint = 200;
				texts[i].setLayoutData( gd );
			}
		}

		return composite;
	}

	protected Control createContents( Composite parent )
	{
		Control control = super.createContents( parent );
		FormWidgetFactory.getInstance( ).paintFormStyle( (Composite) parent );
		FormWidgetFactory.getInstance( ).adapt( (Composite) parent );
		return control;
	}

	private Text[] texts;
	private String[] jueweis;

	protected void okPressed( )
	{
		if ( texts != null )
		{
			jueweis = new String[jueweis.length];
			for ( int i = 0; i < jueweis.length; i++ )
			{
				jueweis[i] = texts[i].getText( ).trim( );
			}
		}

		super.okPressed( );
	}

	public Object getResult( )
	{
		return jueweis;
	}

	public void setGeneralJueweis( String[] jueweis )
	{
		this.jueweis = jueweis;
	}

	public int open( )
	{
		if ( getShell( ) == null )
		{
			create( );
			getShell( ).setImages( ( (Shell) getShell( ).getParent( ) ).getImages( ) );
			ShellWrapper wrapper = new ShellWrapper( getShell( ) );
			wrapper.installTheme( );
			getShell( ).setSize( 400, 250 );
		}
		if ( initDialog( ) )
		{
			return super.open( );
		}
		return Dialog.CANCEL;
	}
}
