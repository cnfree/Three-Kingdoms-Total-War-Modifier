
package org.sf.feeling.sanguo.patch.dialog;

import org.eclipse.jface.dialogs.BaseDialog;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.sf.feeling.sanguo.patch.util.ChangeCode;
import org.sf.feeling.sanguo.patch.util.FileUtil;
import org.sf.feeling.sanguo.patch.widget.FormWidgetFactory;
import org.sf.feeling.sanguo.patch.widget.WidgetUtil;
import org.sf.feeling.swt.win32.extension.util.SortMap;
import org.sf.feeling.swt.win32.extension.widgets.ShellWrapper;

public class SkillDialog extends BaseDialog
{

	private SortMap skillMap = FileUtil.loadProperties( "skill" );
	private Button[] buttons = new Button[skillMap.size( )];

	public SkillDialog( String title )
	{
		super( title );
	}

	protected Control createDialogArea( Composite parent )
	{
		Composite composite = new Composite( parent, SWT.NONE );
		composite.setLayoutData( new GridData( GridData.FILL_BOTH ) );
		GridLayout layout = new GridLayout( );
		layout.marginWidth = layout.marginHeight = 20;
		composite.setLayout( layout );

		Group group = WidgetUtil.getToolkit( )
				.createGroup( composite, "选择武将特技" );
		GridData gd = new GridData( GridData.FILL_HORIZONTAL );
		group.setLayoutData( gd );

		layout = new GridLayout( );
		layout.numColumns = 3;
		layout.makeColumnsEqualWidth = true;
		group.setLayout( layout );

		for ( int i = 0; i < skillMap.size( ); i++ )
		{
			buttons[i] = WidgetUtil.getToolkit( ).createButton( group,
					SWT.CHECK,
					true );
			buttons[i].setText( ChangeCode.toLong( (String) skillMap.get( i ) ) );
			buttons[i].setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );

			if ( skills != null
					&& skills.containsKey( skillMap.getKeyList( ).get( i ) ) )
			{
				buttons[i].setSelection( true );
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

	protected void okPressed( )
	{
		for ( int i = 0; i < skillMap.size( ); i++ )
		{
			if ( buttons[i].getSelection( ) )
			{
				if ( skills != null
						&& !skills.containsKey( skillMap.getKeyList( ).get( i ) ) )
				{
					skills.put( skillMap.getKeyList( ).get( i ), "1" );
				}
			}
			else
			{
				if ( skills != null
						&& skills.containsKey( skillMap.getKeyList( ).get( i ) ) )
				{
					skills.remove( skillMap.getKeyList( ).get( i ) );
				}
			}
		}
		super.okPressed( );
	}

	public int open( )
	{
		if ( getShell( ) == null )
		{
			create( );
			getShell( ).setImages( ( (Shell) getShell( ).getParent( ) ).getImages( ) );
			ShellWrapper wrapper = new ShellWrapper( getShell( ) );
			wrapper.installTheme( );
			getShell( ).setSize( 360, 250 );
		}
		if ( initDialog( ) )
		{
			return super.open( );
		}
		return Dialog.CANCEL;
	}

	private SortMap skills;

	public void setGeneralSkills( SortMap skills )
	{
		this.skills = skills;
	}
}
