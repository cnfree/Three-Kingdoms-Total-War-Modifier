
package com.actuate.tool.development.installer.wizard;

import java.io.File;
import java.util.List;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.actuate.tool.development.installer.model.InstallData;
import com.actuate.tool.development.installer.model.InstallType;
import com.actuate.tool.development.installer.util.FileSorter;

class IPortalViewerProjectPage extends WizardPage
{

	private Combo comboProjects;
	private Combo comboFiles;

	private InstallData data;
	private Text txtRoot;
	private Text txtView;
	private Button forceButton;

	IPortalViewerProjectPage( InstallData data )
	{
		super( "ProjectPage" );
		setTitle( "Select the iPortal Viewer Project" );
		setDescription( "Select the iPortal Viewer file." );
		this.data = data;
	}

	public void createControl( Composite parent )
	{
		Composite composite = new Composite( parent, SWT.NULL );
		GridLayout gridLayout = new GridLayout( );
		gridLayout.marginWidth = 10;
		//gridLayout.marginHeight = 10;
		composite.setLayout( gridLayout );

		Group fileSelectionGroup = new Group( composite, SWT.NONE );
		fileSelectionGroup.setText( "Select Actuate Web Viewer Build" );
		GridData gd = new GridData( GridData.FILL_HORIZONTAL );
		gd.minimumWidth = 0;
		fileSelectionGroup.setLayoutData( gd );
		fileSelectionGroup.setLayout( new GridLayout( 2, false ) );

		new Label( fileSelectionGroup, SWT.NONE ).setText( "Viewer &Project: " );
		comboProjects = new Combo( fileSelectionGroup, SWT.READ_ONLY
				| SWT.BORDER );

		gd = new GridData( );
		gd.widthHint = 350;
		gd.horizontalAlignment = SWT.FILL;
		comboProjects.setLayoutData( gd );
		comboProjects.addSelectionListener( new SelectionAdapter( ) {

			public void widgetSelected( SelectionEvent e )
			{
				if ( data != null )
				{
					data.setCurrentIVProject( comboProjects.getText( ).trim( ) );
				}
				handleProjectSelection( );
			}

		} );

		new Label( fileSelectionGroup, SWT.NONE ).setText( "&Viewer File: " );
		comboFiles = new Combo( fileSelectionGroup, SWT.READ_ONLY | SWT.BORDER );

		gd = new GridData( );
		gd.widthHint = 350;
		gd.horizontalAlignment = SWT.FILL;
		comboFiles.setLayoutData( gd );
		comboFiles.addSelectionListener( new SelectionAdapter( ) {

			public void widgetSelected( SelectionEvent e )
			{
				handleProjectFileSelection( );
			}

		} );

		if ( !data.getIportalViewMap( ).isEmpty( ) )
			comboProjects.setItems( data.getIportalViewMap( )
					.keySet( )
					.toArray( new String[0] ) );
		comboProjects.getParent( ).layout( );
		if ( data != null && data.getCurrentIVProject( ) != null )
		{
			int index = comboProjects.indexOf( data.getCurrentIVProject( ) );
			if ( index != -1 )
			{
				comboProjects.setText( data.getCurrentIVProject( ) );
				handleProjectSelection( );
			}
		}

		Group p4ConfigGroup = new Group( composite, SWT.NONE );
		p4ConfigGroup.setText( "P4 Workspace Sync Settings" );
		gd = new GridData( GridData.FILL_HORIZONTAL );
		gd.minimumWidth = 0;
		gd.verticalIndent = 10;
		p4ConfigGroup.setLayoutData( gd );

		p4ConfigGroup.setLayout( new GridLayout( 3, false ) );

		new Label( p4ConfigGroup, SWT.NONE ).setText( "P4 &Root: " );
		txtRoot = new Text( p4ConfigGroup, SWT.BORDER );
		gd = new GridData( GridData.FILL_HORIZONTAL );
		txtRoot.setLayoutData( gd );

		txtRoot.addModifyListener( new ModifyListener( ) {

			public void modifyText( ModifyEvent e )
			{
				checkStatus( );
				setPageComplete( isPageComplete( ) );
			}

		} );

		Button rootDirectoryButton = new Button( p4ConfigGroup, SWT.PUSH );
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
				}
			}

		} );

		new Label( p4ConfigGroup, SWT.NONE ).setText( "P4 V&iew: " );
		txtView = new Text( p4ConfigGroup, SWT.BORDER );
		gd = new GridData( GridData.FILL_HORIZONTAL );
		gd.horizontalSpan = 2;
		txtView.setLayoutData( gd );

		txtView.addModifyListener( new ModifyListener( ) {

			public void modifyText( ModifyEvent e )
			{
				checkStatus( );
				setPageComplete( isPageComplete( ) );
			}

		} );

		forceButton = new Button( p4ConfigGroup, SWT.CHECK );
		forceButton.setText( "&Force Operation" );
		gd = new GridData( GridData.FILL_HORIZONTAL );
		gd.horizontalSpan = 3;
		forceButton.setLayoutData( gd );

		setControl( composite );

	}

	public boolean isPageComplete( )
	{
		if ( data != null )
		{
			if ( data.getInstallType( ) != InstallType.synciPortalWorkspace )
				return true;

			checkStatus( );

			return comboProjects != null
					&& comboProjects.getSelectionIndex( ) > -1
					&& comboFiles != null
					&& comboFiles.getSelectionIndex( ) > -1
					&& getErrorMessage( ) == null;
		}
		return false;
	}

	private void handleProjectSelection( )
	{
		comboFiles.removeAll( );
		List<File> files = data.getIportalViewMap( )
				.get( comboProjects.getText( ) );

		FileSorter.sortFiles( files );

		if ( files != null && !files.isEmpty( ) )
		{
			for ( File file : files )
			{
				comboFiles.add( file.getName( ) );
			}
			comboFiles.select( comboFiles.getItemCount( ) - 1 );
			handleProjectFileSelection( );
		}
		setPageComplete( isPageComplete( ) );
	}

	private void handleProjectFileSelection( )
	{
		if ( data != null )
		{
			data.getCurrentIportalViewerData( )
					.setBirtViewerFile( data.getIportalViewMap( )
							.get( comboProjects.getText( ) )
							.get( comboFiles.getSelectionIndex( ) )
							.getAbsolutePath( ) );
		}
		setPageComplete( isPageComplete( ) );
	}

	private void checkStatus( )
	{
		if ( txtRoot.getText( ).trim( ).length( ) == 0 )
		{
			setErrorMessage( "The path of p4 workspace root is empty." );
			return;
		}

		File file = new File( txtRoot.getText( ) );
		if ( file.getParentFile( ) == null )
		{
			setErrorMessage( "The path of p4 workspace root is invalid." );
			return;
		}

		if ( txtView.getText( ).trim( ).length( ) == 0 )
		{
			setErrorMessage( "the path of p4 view is empty." );
			return;
		}

		setErrorMessage( null );
		return;
	}
}