
package com.actuate.development.tool.wizard;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;

import com.actuate.development.tool.config.LocationConfig;
import com.actuate.development.tool.config.PathConfig;
import com.actuate.development.tool.model.feature.ToolFeature;
import com.actuate.development.tool.model.feature.ToolFeatureData;
import com.actuate.development.tool.util.FileSorter;

public class BRDProProjectPage extends WizardPage implements
		IPropertyChangeListener
{

	private Combo comboProjects;
	private Combo comboFiles;

	private ToolFeatureData data;

	public BRDProProjectPage( ToolFeatureData data )
	{
		super( "ProjectPage" );
		this.data = data;
		this.data.addChangeListener( this );
		setTitle( "Select the BRDPro Project" );
		setDescription( "Select the BRDPro installation file." );
	}

	public void createControl( Composite parent )
	{
		Composite composite = new Composite( parent, SWT.NULL );
		GridLayout gridLayout = new GridLayout( 3, false );
		gridLayout.marginWidth = 10;
		gridLayout.marginHeight = 20;
		composite.setLayout( gridLayout );

		new Label( composite, SWT.NONE ).setText( "BRDPro &Project: " );
		comboProjects = new Combo( composite, SWT.READ_ONLY | SWT.BORDER );

		GridData gd = new GridData( GridData.FILL_HORIZONTAL );
		gd.widthHint = 350;
		gd.horizontalSpan = 2;
		comboProjects.setLayoutData( gd );
		comboProjects.addSelectionListener( new SelectionAdapter( ) {

			public void widgetSelected( SelectionEvent e )
			{
				if ( data != null )
				{
					data.setCurrentBRDProProject( comboProjects.getText( )
							.trim( ) );
				}
				handleProjectSelection( );
			}

		} );

		new Label( composite, SWT.NONE ).setText( "&Installation File: " );
		comboFiles = new Combo( composite, SWT.BORDER );

		gd = new GridData( GridData.FILL_HORIZONTAL );
		gd.widthHint = 350;
		comboFiles.setLayoutData( gd );
		comboFiles.addModifyListener( new ModifyListener( ) {

			public void modifyText( ModifyEvent e )
			{
				BusyIndicator.showWhile( Display.getDefault( ),
						new Runnable( ) {

							public void run( )
							{
								handleProjectFileSelection( );
							}
						} );
			}

		} );

		final Button browseButton = new Button( composite, SWT.PUSH );
		browseButton.setText( "Bro&wse..." );
		gd = new GridData( );
		browseButton.setLayoutData( gd );
		browseButton.addSelectionListener( new SelectionAdapter( ) {

			public void widgetSelected( SelectionEvent e )
			{
				FileDialog dialog = new FileDialog( browseButton.getShell( ),
						SWT.OPEN | SWT.SINGLE );
				dialog.setFilterExtensions( new String[]{
					"*.jar;*.war;*.zip"
				} );
				dialog.setFilterNames( new String[]{
					"Archive File (*.jar;*.war;*.zip)"
				} );
				String file = dialog.open( );
				if ( file != null )
				{
					comboFiles.setText( file );
				}
			}
		} );

		initProjects( );

		setControl( composite );

	}

	private void initProjects( )
	{
		if ( !data.getBrdproMap( ).isEmpty( ) )
		{
			String[] projects = data.getBrdproMap( )
					.keySet( )
					.toArray( new String[0] );
			Arrays.sort( projects );
			comboProjects.setItems( projects );
		}
		else
			comboProjects.removeAll( );
		comboProjects.getParent( ).layout( );
		if ( data != null && data.getCurrentBRDProProject( ) != null )
		{
			int index = comboProjects.indexOf( data.getCurrentBRDProProject( ) );
			if ( index != -1 )
			{
				comboProjects.setText( data.getCurrentBRDProProject( ) );
				handleProjectSelection( );
			}
			else
			{
				comboFiles.removeAll( );
			}
		}
	}

	private void checkStatus( )
	{
		if ( comboFiles.indexOf( comboFiles.getText( ) ) == -1 )
		{
			if ( !new File( comboFiles.getText( ) ).exists( ) )
			{
				setErrorMessage( "The path of BRDPro installation file is invalid." );
				return;
			}
		}

		setErrorMessage( null );
		return;
	}

	public boolean isPageComplete( )
	{
		if ( data != null )
		{
			if ( data.getToolFeature( ) != ToolFeature.installBRDPro )
				return true;

			checkStatus( );

			return comboProjects != null
					&& comboProjects.getSelectionIndex( ) > -1
					&& getErrorMessage( ) == null;
		}
		return false;
	}

	private void handleProjectSelection( )
	{
		comboFiles.removeAll( );
		List<File> files = data.getBrdproMap( ).get( comboProjects.getText( ) );

		if ( files != null && !files.isEmpty( ) )
		{
			FileSorter.sortFiles( files );
			for ( File file : files )
			{
				if ( ( PathConfig.getProperty( PathConfig.HQ_PROJECT_ACTUATE_BUILD_DIR ) != null && file.getAbsolutePath( )
						.startsWith( PathConfig.getProperty( PathConfig.HQ_PROJECT_ACTUATE_BUILD_DIR ) ) )
						|| ( PathConfig.getProperty( PathConfig.HQ_RELEASE_ACTUATE_BUILD_DIR ) != null && file.getAbsolutePath( )
								.startsWith( PathConfig.getProperty( PathConfig.HQ_RELEASE_ACTUATE_BUILD_DIR ) ) ) )
				{
					String path = file.getAbsolutePath( );
					String[] tokens = path.split( "\\\\" );
					if ( file.isFile( ) )
					{
						if ( tokens.length > 3 )
							comboFiles.add( tokens[tokens.length - 3] );
					}
					else
					{
						if ( tokens.length > 2 )
							comboFiles.add( tokens[tokens.length - 2] );
					}
				}
				else
				{
					comboFiles.add( file.getName( ) );
				}
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
			if ( comboFiles.indexOf( comboFiles.getText( ) ) == -1 )
			{
				data.getCurrentInstallBRDProData( )
						.setBrdproFile( comboFiles.getText( ) );
			}
			else
				data.getCurrentInstallBRDProData( )
						.setBrdproFile( data.getBrdproMap( )
								.get( comboProjects.getText( ) )
								.get( comboFiles.indexOf( comboFiles.getText( ) ) )
								.getAbsolutePath( ) );
		}
		setPageComplete( isPageComplete( ) );
	}

	public void propertyChange( PropertyChangeEvent event )
	{
		if ( LocationConfig.LOCATION.equals( event.getProperty( ) ) )
			initProjects( );
	}

	public void dispose( )
	{
		this.data.removeChangeListener( this );
		super.dispose( );
	}
}
