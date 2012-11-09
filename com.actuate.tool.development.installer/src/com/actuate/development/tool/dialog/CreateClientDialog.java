
package com.actuate.development.tool.dialog;

import java.io.File;
import java.util.List;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.sf.feeling.swt.win32.internal.extension.util.ImageCache;

public class CreateClientDialog extends TitleAreaDialog
{

	private Text txtRoot;
	private Text txtName;
	private List<String> clients;
	private String[] result;

	public CreateClientDialog( Shell parentShell )
	{
		super( parentShell );
		setShellStyle( 0x10C70 | getDefaultOrientation( ) );
	}

	public void setExistClients( List<String> clients )
	{
		this.clients = clients;
	}

	protected void configureShell( Shell shell )
	{
		super.configureShell( shell );
		shell.setText( "Create Perforce Client" );
		shell.setImages( new Image[]{
				ImageCache.getImage( "/icons/actuate_16.png" ),
				ImageCache.getImage( "/icons/actuate_32.png" ),
				ImageCache.getImage( "/icons/actuate_48.png" )
		} );
	}

	protected Control createDialogArea( Composite parent )
	{

		this.setTitle( "Create Perforce Client" );
		this.setMessage( "Create a new perforce client." );
		Control contents = super.createDialogArea( parent );
		Composite composite = new Composite( (Composite) contents, SWT.NONE );
		GridLayout layout = new GridLayout( 3, false );
		layout.marginWidth = 10;
		composite.setLayout( layout );
		composite.setLayoutData( new GridData( GridData.FILL_BOTH ) );

		new Label( composite, SWT.NONE ).setText( "&Name: " );
		txtName = new Text( composite, SWT.BORDER );
		GridData gd = new GridData( GridData.FILL_HORIZONTAL );
		gd.horizontalSpan = 2;
		txtName.setLayoutData( gd );
		txtName.addModifyListener( new ModifyListener( ) {

			public void modifyText( ModifyEvent arg0 )
			{
				checkStatus( );
			}
		} );

		new Label( composite, SWT.NONE ).setText( "&Workspace: " );
		txtRoot = new Text( composite, SWT.BORDER );
		gd = new GridData( GridData.FILL_HORIZONTAL );
		txtRoot.setLayoutData( gd );
		txtRoot.addModifyListener( new ModifyListener( ) {

			public void modifyText( ModifyEvent arg0 )
			{
				checkStatus( );
			}
		} );

		Button rootDirectoryButton = new Button( composite, SWT.PUSH );
		rootDirectoryButton.setText( "Br&owse..." );
		rootDirectoryButton.addSelectionListener( new SelectionAdapter( ) {

			public void widgetSelected( SelectionEvent e )
			{
				DirectoryDialog dialog = new DirectoryDialog( getShell( ) );
				dialog.setMessage( "Select Directory" );
				String path = dialog.open( );
				if ( path != null )
				{
					txtRoot.setText( path );
					checkStatus( );
				}
			}
		} );

		composite.setLayout( layout );

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
		if ( txtName != null && txtName.getText( ).trim( ).length( ) == 0 )
		{
			this.setErrorMessage( "Client name is empty" );
			setOkButtonStatus( false );
			return;
		}
		else if ( txtName != null && clients != null )
		{
			for ( int i = 0; i < clients.size( ); i++ )
			{
				if ( clients.get( i ).equalsIgnoreCase( txtName.getText( )
						.trim( ) ) )
				{
					this.setErrorMessage( "Client "
							+ clients.get( i )
							+ " exists" );
					setOkButtonStatus( false );
					return;
				}
			}
		}

		if ( txtRoot != null && txtRoot.getText( ).trim( ).length( ) == 0 )
		{
			this.setErrorMessage( "Workspace path is empty" );
			setOkButtonStatus( false );
			return;
		}
		else if ( txtRoot != null )
		{
			File file = new File( txtRoot.getText( ) );
			if ( file.getParentFile( ) == null )
			{
				setErrorMessage( "The path of P4 workspace is invalid." );
				setOkButtonStatus( false );
				return;
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

	public String[] getResult( )
	{
		return result;
	}

	protected void okPressed( )
	{
		result = new String[2];
		result[0] = txtName.getText( ).trim( );
		result[1] = txtRoot.getText( ).trim( );
		super.okPressed( );
	}
}
