
package com.actuate.development.tool.wizard;

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
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.actuate.development.tool.model.Module;
import com.actuate.development.tool.model.ToolFeatureData;
import com.actuate.development.tool.model.Version;
import com.actuate.development.tool.model.VersionType;
import com.actuate.development.tool.provider.SyncResourcesContentProvider;
import com.actuate.development.tool.provider.SyncResourcesLabelProvider;

public class SyncBRDProResourcePage extends WizardPage implements
		IPropertyChangeListener
{

	private ToolFeatureData data;
	private CheckboxTreeViewer platformViewer;
	private Text txtDirectory;
	private ScrolledComposite scrollContent;
	private Composite composite;

	public SyncBRDProResourcePage( ToolFeatureData data )
	{
		super( "SyncBRDProResourcePage" );
		this.data = data;
		setTitle( "Synchronize the BRDPro Resources" );
		setDescription( "Synchronize the BRDPro resource files, such as toolkit plugins, eclipse framework SDKs." );
	}

	public void createControl( Composite parent )
	{
		scrollContent = new ScrolledComposite( parent, SWT.H_SCROLL
				| SWT.V_SCROLL );
		scrollContent.setAlwaysShowScrollBars( false );
		scrollContent.setExpandHorizontal( true );
		scrollContent.setExpandVertical( true );
		scrollContent.setMinWidth( 550 );
		scrollContent.setLayout( new FillLayout( ) );
		scrollContent.setLayoutData( new GridData( GridData.FILL_BOTH ) );

		composite = new Composite( scrollContent, SWT.NULL );
		GridLayout gridLayout = new GridLayout( 3, false );
		gridLayout.marginWidth = 10;
		composite.setLayout( gridLayout );

		Label platformLabel = new Label( composite, SWT.NONE );
		platformLabel.setText( "&Select the eclipse platform versions will be synchironized" );
		GridData gd = new GridData( GridData.FILL_HORIZONTAL );
		gd.horizontalSpan = 3;
		platformLabel.setLayoutData( gd );

		platformViewer = new CheckboxTreeViewer( composite, SWT.BORDER );
		platformViewer.getTree( )
				.setLayoutData( new GridData( GridData.FILL_BOTH ) );
		final SyncResourcesContentProvider provider = new SyncResourcesContentProvider( data );
		platformViewer.setContentProvider( provider );
		platformViewer.setLabelProvider( new SyncResourcesLabelProvider( ) );
		platformViewer.setInput( new Object[0] );

		platformViewer.addCheckStateListener( new ICheckStateListener( ) {

			public void checkStateChanged( CheckStateChangedEvent event )
			{
				Object selection = event.getElement( );
				if ( selection instanceof Version )
				{
					VersionType type = ( (Version) selection ).getType( );
					checkCheckStatus( platformViewer, provider, type );

				}
				else if ( selection instanceof VersionType )
				{
					platformViewer.setGrayed( selection, false );
					boolean checked = event.getChecked( );
					for ( Object obj : provider.getChildren( selection ) )
						platformViewer.setChecked( obj, checked );
				}

				updateModelCheckStatus( platformViewer );
			}
		} );

		platformViewer.expandToLevel( VersionType.platform, 1 );
		platformViewer.setChecked( VersionType.platform, true );
		for ( Object obj : provider.getChildren( VersionType.platform ) )
			platformViewer.setChecked( obj, true );
		
		gd = new GridData( GridData.FILL_BOTH );
		gd.horizontalSpan = 3;
		gd.heightHint = 100;
		platformViewer.getTree( ).setLayoutData( gd );

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

		Button browseButton = new Button( composite, SWT.PUSH );
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

		Point size = composite.computeSize( SWT.DEFAULT, SWT.DEFAULT );
		composite.setSize( size );

		scrollContent.addControlListener( new ControlAdapter( ) {

			public void controlResized( ControlEvent e )
			{
				computeSize( );
			}
		} );

		scrollContent.setContent( composite );

		setControl( scrollContent );

		// if ( data != null && data.getCurrentIVProject( ) != null )
		// {
		// int index = comboProjects.indexOf( data.getCurrentIVProject( ) );
		// if ( index != -1 )
		// {
		// comboProjects.setText( data.getCurrentIVProject( ) );
		// handleProjectSelection( );
		// }
		// }
		//
		// initPage( );
	}

	private void computeSize( )
	{
		scrollContent.setMinSize( composite.computeSize( SWT.DEFAULT,
				SWT.DEFAULT ) );
		composite.layout( );
	}

	private void checkCheckStatus( final CheckboxTreeViewer moduleViewer,
			final SyncResourcesContentProvider provider, VersionType type )
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
		// TODO Auto-generated method stub

	}

}
