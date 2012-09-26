
package com.actuate.development.tool.wizard;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.actuate.development.tool.model.ToolFeatureData;

class BRDProShortcutPage extends WizardPage implements IPropertyChangeListener
{

	private Text shortcutArgumentText;

	private ToolFeatureData data;

	private Button shortcutButton;

	BRDProShortcutPage( ToolFeatureData data )
	{
		super( "ShortcutPage" );
		setTitle( "Create the Executable Shortcut" );
		setDescription( "Create the BRDPro executable shortcut on Desktop." );
		this.data = data;
		this.data.addChangeListener( this );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets
	 * .Composite)
	 */
	public void createControl( Composite parent )
	{
		Composite composite = new Composite( parent, SWT.NULL );
		GridLayout gridLayout = new GridLayout( 2, false );
		gridLayout.marginWidth = 10;
		gridLayout.marginHeight = 20;
		composite.setLayout( gridLayout );

		shortcutButton = new Button( composite, SWT.CHECK );
		shortcutButton.setText( "Create &shortcut on Desktop" );
		shortcutButton.addSelectionListener( new SelectionAdapter( ) {

			public void widgetSelected( SelectionEvent e )
			{
				if ( data != null )
					data.getCurrentInstallBRDProData( )
							.setNotCreateShortcut( !shortcutButton.getSelection( ) );
				checkTextStatus( shortcutButton );
			}
		} );

		GridData gd = new GridData( );
		gd.horizontalSpan = 2;
		shortcutButton.setLayoutData( gd );

		new Label( composite, SWT.NONE ).setText( "Shortcut &Arguments: " );
		shortcutArgumentText = new Text( composite, SWT.BORDER );
		shortcutArgumentText.setText( "-showlocation -nl en_us -vmargs -server -Xms256m -Xmx512m -XX:PermSize=64M -XX:MaxPermSize=256M" );
		gd = new GridData( GridData.FILL_HORIZONTAL );
		shortcutArgumentText.setLayoutData( gd );

		shortcutArgumentText.addModifyListener( new ModifyListener( ) {

			public void modifyText( ModifyEvent e )
			{
				if ( shortcutArgumentText.getText( ).trim( ).length( ) > 0 )
				{
					data.getCurrentInstallBRDProData( )
							.setShortcutArguments( shortcutArgumentText.getText( )
									.trim( ) );
				}

			}
		} );

		initPage( );

		setControl( composite );

	}

	private void initPage( )
	{
		if ( data != null )
		{
			shortcutButton.setSelection( !data.getCurrentInstallBRDProData( )
					.isNotCreateShortcut( ) );
			checkTextStatus( shortcutButton );
			if ( data.getCurrentInstallBRDProData( ).getShortcutArguments( ) != null
					&& data.getCurrentInstallBRDProData( )
							.getShortcutArguments( )
							.trim( )
							.length( ) > 0 )
			{
				shortcutArgumentText.setText( data.getCurrentInstallBRDProData( )
						.getShortcutArguments( ) );
			}
			else
			{
				shortcutArgumentText.setText( "-showlocation -nl en_us -vmargs -server -Xms256m -Xmx512m -XX:PermSize=64M -XX:MaxPermSize=256M" );
				data.getCurrentInstallBRDProData( )
						.setShortcutArguments( shortcutArgumentText.getText( ) );
			}
		}
	}

	public boolean isPageComplete( )
	{
		if ( data != null )
		{
			return true;
		}
		return false;
	}

	private void checkTextStatus( final Button shortcutButton )
	{
		shortcutArgumentText.setEnabled( shortcutButton.getSelection( ) );
	}

	public void propertyChange( PropertyChangeEvent event )
	{
		if ( ToolkitWizard.CURRENT_BRDPRO_PROJECT.equals( event.getProperty( ) ) )
			initPage( );
	}
}
