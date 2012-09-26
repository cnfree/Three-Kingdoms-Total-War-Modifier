
package com.actuate.tool.development.tool.wizard;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
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
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.actuate.tool.development.tool.model.ToolFeatureData;
import com.actuate.tool.development.tool.model.ToolFeature;
import com.actuate.tool.development.tool.model.Module;
import com.actuate.tool.development.tool.model.ModuleType;
import com.actuate.tool.development.tool.provider.ModuleContentProvider;
import com.actuate.tool.development.tool.provider.ModuleLabelProvider;

class BRDProSettingPage extends WizardPage implements IPropertyChangeListener
{

	private Text txtDirectory;

	private ToolFeatureData data;

	private Button closeButton;

	private Button cleanButton;

	private Button browseButton;

	private CheckboxTreeViewer moduleViewer;

	private ModuleContentProvider provider;

	BRDProSettingPage( ToolFeatureData data )
	{
		super( "SettingPage" );
		setTitle( "Config Installation Settings" );
		setDescription( "Config the BRDPro development environment settings." );
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
		GridLayout gridLayout = new GridLayout( 3, false );
		gridLayout.marginWidth = 10;
		composite.setLayout( gridLayout );

		Label moduleLabel = new Label( composite, SWT.NONE );
		moduleLabel.setText( "&Select the modules will be installed" );
		GridData gd = new GridData( GridData.FILL_HORIZONTAL );
		gd.horizontalSpan = 3;
		moduleLabel.setLayoutData( gd );

		moduleViewer = new CheckboxTreeViewer( composite, SWT.BORDER );

		moduleViewer.getTree( )
				.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
		provider = new ModuleContentProvider( );
		moduleViewer.setContentProvider( provider );
		moduleViewer.setLabelProvider( new ModuleLabelProvider( ) );
		moduleViewer.setInput( new Object[0] );

		moduleViewer.expandAll( );

		moduleViewer.addCheckStateListener( new ICheckStateListener( ) {

			public void checkStateChanged( CheckStateChangedEvent event )
			{
				Object selection = event.getElement( );
				if ( selection instanceof Module )
				{
					ModuleType type = ( (Module) selection ).getType( );
					checkCheckStatus( moduleViewer, provider, type );

				}
				else if ( selection instanceof ModuleType )
				{
					moduleViewer.setGrayed( selection, false );
					boolean checked = event.getChecked( );
					for ( Object obj : provider.getChildren( selection ) )
						moduleViewer.setChecked( obj, checked );
				}

				updateModelCheckStatus( moduleViewer );
			}
		} );

		gd = new GridData( GridData.FILL_BOTH );
		gd.horizontalSpan = 3;
		gd.heightHint = 100;
		moduleViewer.getTree( ).setLayoutData( gd );

		new Label( composite, SWT.NONE ).setText( "Installation &Directory: " );
		txtDirectory = new Text( composite, SWT.BORDER );
		gd = new GridData( GridData.FILL_HORIZONTAL );
		txtDirectory.setLayoutData( gd );

		txtDirectory.addModifyListener( new ModifyListener( ) {

			public void modifyText( ModifyEvent e )
			{
				String text = txtDirectory.getText( );
				File file = new File( text );
				if ( file.getParentFile( ) == null )
				{
					setErrorMessage( "The installation directory is invalid." );
				}
				else
				{
					setErrorMessage( null );
					if ( data != null )
						data.getCurrentInstallBRDProData( )
								.setDirectory( txtDirectory.getText( ) );
				}
				setPageComplete( isPageComplete( ) );
			}

		} );

		browseButton = new Button( composite, SWT.PUSH );
		browseButton.setText( "B&rowse..." );
		browseButton.addSelectionListener( new SelectionAdapter( ) {

			public void widgetSelected( SelectionEvent e )
			{
				DirectoryDialog dialog = new DirectoryDialog( getShell( ) );
				dialog.setMessage( "Select Installation Directory" );
				String path = dialog.open( );
				if ( path != null )
				{
					txtDirectory.setText( path );
				}
			}

		} );

		cleanButton = new Button( composite, SWT.CHECK );
		cleanButton.setText( "Clean &the installation directory" );
		cleanButton.setSelection( true );
		gd = new GridData( );
		gd.horizontalSpan = 3;
		cleanButton.setLayoutData( gd );
		cleanButton.addSelectionListener( new SelectionAdapter( ) {

			public void widgetSelected( SelectionEvent e )
			{
				if ( data != null )
					data.getCurrentInstallBRDProData( )
							.setNotClearDirectory( !cleanButton.getSelection( ) );
			}
		} );

		closeButton = new Button( composite, SWT.CHECK );
		closeButton.setText( "Force to close the opened BRDPro and folder processes under the installation directory" );
		closeButton.setSelection( true );
		gd = new GridData( );
		gd.horizontalSpan = 3;
		closeButton.setLayoutData( gd );
		closeButton.addSelectionListener( new SelectionAdapter( ) {

			public void widgetSelected( SelectionEvent e )
			{
				if ( data != null )
					data.getCurrentInstallBRDProData( )
							.setNotCloseBRDPro( !closeButton.getSelection( ) );
			}
		} );

		initPage( );
		setControl( composite );

	}

	private void initPage( )
	{
		moduleViewer.setGrayedElements( new Object[0] );
		moduleViewer.setCheckedElements( new Object[0] );

		if ( data != null
				&& data.getCurrentInstallBRDProData( ).getModules( ) != null
				&& data.getCurrentInstallBRDProData( ).getModules( ).length > 0 )
		{
			for ( Module module : data.getCurrentInstallBRDProData( )
					.getModules( ) )
			{
				if ( module != null )
					moduleViewer.setChecked( module, true );
			}
			checkCheckStatus( moduleViewer, provider, ModuleType.plugin );
			checkCheckStatus( moduleViewer, provider, ModuleType.sdk );
			checkCheckStatus( moduleViewer, provider, ModuleType.extension );
		}
		else
		{
			moduleViewer.setCheckedElements( new Object[]{
					ModuleType.plugin, Module.git, Module.perforce
			} );
			updateModelCheckStatus( moduleViewer );
		}

		if ( data != null
				&& data.getCurrentInstallBRDProData( ).getDirectory( ) != null
				&& data.getCurrentInstallBRDProData( )
						.getDirectory( )
						.trim( )
						.length( ) > 0 )
		{
			txtDirectory.setText( data.getCurrentInstallBRDProData( )
					.getDirectory( )
					.trim( ) );
		}
		else
		{
			txtDirectory.setText( "" );
		}

		if ( data != null )
		{
			cleanButton.setSelection( !data.getCurrentInstallBRDProData( )
					.isNotClearDirectory( ) );
			closeButton.setSelection( !data.getCurrentInstallBRDProData( )
					.isNotCloseBRDPro( ) );
		}
	}

	public boolean isPageComplete( )
	{
		if ( data != null )
		{
			if ( data.getToolFeature( ) != ToolFeature.installBRDPro )
				return true;
			return txtDirectory != null
					&& txtDirectory.getText( ).trim( ).length( ) > 0
					&& getErrorMessage( ) == null;
		}
		return false;
	}

	private void checkCheckStatus( final CheckboxTreeViewer moduleViewer,
			final ModuleContentProvider provider, ModuleType type )
	{
		int count = 0;
		for ( Object obj : provider.getChildren( type ) )
		{
			if ( moduleViewer.getChecked( obj ) )
				count++;
		}
		moduleViewer.setGrayed( type, false );
		if ( count == 0 )
			moduleViewer.setChecked( type, false );
		else if ( count == provider.getChildren( type ).length )
			moduleViewer.setChecked( type, true );
		else
			moduleViewer.setGrayChecked( type, true );
	}

	private void updateModelCheckStatus( final CheckboxTreeViewer moduleViewer )
	{
		List<Module> list = new ArrayList<Module>( );
		Object[] selections = moduleViewer.getCheckedElements( );
		for ( int i = 0; i < selections.length; i++ )
		{
			if ( selections[i] instanceof Module )
			{
				list.add( (Module) selections[i] );
			}
		}
		data.getCurrentInstallBRDProData( )
				.setModules( list.toArray( new Module[0] ) );
	}

	public void propertyChange( PropertyChangeEvent event )
	{
		if ( InstallWizard.CURRENT_BRDPRO_PROJECT.equals( event.getProperty( ) ) )
			initPage( );
	}

}
