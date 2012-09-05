
package com.actuate.tool.development.installer.wizard;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.actuate.tool.development.installer.model.InstallData;
import com.actuate.tool.development.installer.model.InstallType;

class P4ConnectionSettingPage extends WizardPage implements
		IPropertyChangeListener
{

	private Text txtServer;

	private InstallData data;

	private Text txtUser;

	private Text txtPassword;

	private Text txtClient;

	P4ConnectionSettingPage( InstallData data )
	{
		super( "P4 Connection SettingPage" );
		setTitle( "Config P4 Connection Settings" );
		setDescription( "Config the perforce connectoin settings." );
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
		Composite composite = new Composite( parent, SWT.NONE );
		GridLayout gridLayout = new GridLayout( 2, false );
		gridLayout.marginWidth = 10;
		composite.setLayout( gridLayout );

		new Label( composite, SWT.NONE ).setText( "&Server: " );
		txtServer = new Text( composite, SWT.BORDER );
		txtServer.setText( "p4:1666" );
		GridData gd = new GridData( GridData.FILL_HORIZONTAL );
		txtServer.setLayoutData( gd );

		txtServer.addModifyListener( new ModifyListener( ) {

			public void modifyText( ModifyEvent e )
			{
				setPageComplete( isPageComplete( ) );
			}

		} );

		new Label( composite, SWT.NONE ).setText( "&User: " );
		txtUser = new Text( composite, SWT.BORDER );
		gd = new GridData( GridData.FILL_HORIZONTAL );
		txtUser.setLayoutData( gd );

		txtUser.addModifyListener( new ModifyListener( ) {

			public void modifyText( ModifyEvent e )
			{
				setPageComplete( isPageComplete( ) );
			}

		} );

		new Label( composite, SWT.NONE ).setText( "&Password: " );
		txtPassword = new Text( composite, SWT.BORDER );
		gd = new GridData( GridData.FILL_HORIZONTAL );
		txtPassword.setLayoutData( gd );

		txtPassword.addModifyListener( new ModifyListener( ) {

			public void modifyText( ModifyEvent e )
			{
				setPageComplete( isPageComplete( ) );
			}

		} );

		new Label( composite, SWT.NONE ).setText( "&Client: " );
		txtClient = new Text( composite, SWT.BORDER );
		gd = new GridData( GridData.FILL_HORIZONTAL );
		txtClient.setLayoutData( gd );

		txtClient.addModifyListener( new ModifyListener( ) {

			public void modifyText( ModifyEvent e )
			{
				setPageComplete( isPageComplete( ) );
			}

		} );

		initPage( );
		setControl( composite );

	}

	private void initPage( )
	{

	}

	public boolean isPageComplete( )
	{
		if ( data != null )
		{
			if ( data.getInstallType( ) != InstallType.synciPortalWorkspace )
				return true;
			return txtServer != null
					&& txtServer.getText( ).trim( ).length( ) > 0
					&& getErrorMessage( ) == null;
		}
		return false;
	}

	public void propertyChange( PropertyChangeEvent event )
	{
		if ( InstallWizard.CURRENT_IV_PROJECT.equals( event.getProperty( ) ) )
			initPage( );
	}

}