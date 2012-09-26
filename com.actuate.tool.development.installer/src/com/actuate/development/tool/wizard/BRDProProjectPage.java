
package com.actuate.development.tool.wizard;

import java.io.File;
import java.util.List;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.actuate.development.tool.model.ToolFeature;
import com.actuate.development.tool.model.ToolFeatureData;
import com.actuate.development.tool.util.FileSorter;

class BRDProProjectPage extends WizardPage
{

	private Combo comboProjects;
	private Combo comboFiles;

	private ToolFeatureData data;

	BRDProProjectPage( ToolFeatureData data )
	{
		super( "ProjectPage" );
		setTitle( "Select the BRDPro Project" );
		setDescription( "Select the BRDPro installation file." );
		this.data = data;
	}

	public void createControl( Composite parent )
	{
		Composite composite = new Composite( parent, SWT.NULL );
		GridLayout gridLayout = new GridLayout( 2, false );
		gridLayout.marginWidth = 10;
		gridLayout.marginHeight = 20;
		composite.setLayout( gridLayout );

		new Label( composite, SWT.NONE ).setText( "BRDPro &Project: " );
		comboProjects = new Combo( composite, SWT.READ_ONLY | SWT.BORDER );

		GridData gd = new GridData( );
		gd.widthHint = 300;
		gd.horizontalAlignment = SWT.FILL;
		comboProjects.setLayoutData( gd );
		comboProjects.addSelectionListener( new SelectionAdapter( ) {

			public void widgetSelected( SelectionEvent e )
			{
				if ( data != null )
				{
					data.setCurrentBRDProProject( comboProjects.getText( ).trim( ) );
				}
				handleProjectSelection( );
			}

		} );

		new Label( composite, SWT.NONE ).setText( "&Installation File: " );
		comboFiles = new Combo( composite, SWT.READ_ONLY | SWT.BORDER );

		gd = new GridData( );
		gd.widthHint = 300;
		gd.horizontalAlignment = SWT.FILL;
		comboFiles.setLayoutData( gd );
		comboFiles.addSelectionListener( new SelectionAdapter( ) {

			public void widgetSelected( SelectionEvent e )
			{
				handleProjectFileSelection( );
			}

		} );

		if ( !data.getBrdproMap( ).isEmpty( ) )
			comboProjects.setItems( data.getBrdproMap( )
					.keySet( )
					.toArray( new String[0] ) );
		comboProjects.getParent( ).layout( );
		if ( data != null && data.getCurrentBRDProProject( ) != null )
		{
			int index = comboProjects.indexOf( data.getCurrentBRDProProject( ) );
			if ( index != -1 )
			{
				comboProjects.setText( data.getCurrentBRDProProject( ) );
				handleProjectSelection( );
			}
		}

		setControl( composite );

	}

	public boolean isPageComplete( )
	{
		if ( data != null )
		{
			if ( data.getToolFeature( ) != ToolFeature.installBRDPro )
				return true;
			return comboProjects != null
					&& comboProjects.getSelectionIndex( ) > -1
					&& comboFiles != null
					&& comboFiles.getSelectionIndex( ) > -1;
		}
		return false;
	}

	private void handleProjectSelection( )
	{
		comboFiles.removeAll( );
		List<File> files = data.getBrdproMap( ).get( comboProjects.getText( ) );

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
			data.getCurrentInstallBRDProData( )
					.setBrdproFile( data.getBrdproMap( )
							.get( comboProjects.getText( ) )
							.get( comboFiles.getSelectionIndex( ) )
							.getAbsolutePath( ) );
		}
		setPageComplete( isPageComplete( ) );
	}
}
