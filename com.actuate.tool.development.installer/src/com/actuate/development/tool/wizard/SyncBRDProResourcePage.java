
package com.actuate.development.tool.wizard;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
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
import com.actuate.development.tool.model.ToolFeature;
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
		platformLabel.setText( "&Select the versions of eclipse platform that will be synchironized" );
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
				setPageComplete( isPageComplete( ) );
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

		new Label( composite, SWT.NONE ).setText( "Target &Directory: " );
		txtDirectory = new Text( composite, SWT.BORDER );
		gd = new GridData( GridData.FILL_HORIZONTAL );
		txtDirectory.setLayoutData( gd );

		txtDirectory.addModifyListener( new ModifyListener( ) {

			public void modifyText( ModifyEvent e )
			{
				setPageComplete( isPageComplete( ) );
			}

		} );

		Button browseButton = new Button( composite, SWT.PUSH );
		browseButton.setText( "B&rowse..." );
		browseButton.addSelectionListener( new SelectionAdapter( ) {

			public void widgetSelected( SelectionEvent e )
			{
				DirectoryDialog dialog = new DirectoryDialog( getShell( ) );
				dialog.setMessage( "Select Target Directory" );
				String path = dialog.open( );
				if ( path != null )
				{
					txtDirectory.setText( path );
				}
			}

		} );

		txtDirectory.setText( "\\\\qaant\\QA\\Toolkit\\platform" );

		Point size = composite.computeSize( SWT.DEFAULT, SWT.DEFAULT );
		composite.setSize( size );

		scrollContent.addControlListener( new ControlAdapter( ) {

			public void controlResized( ControlEvent e )
			{
				computeSize( );
			}
		} );

		final Button bgButton = new Button( composite, SWT.CHECK );
		bgButton.setText( "&Minimize Toolkit to the system tray area when synchironizing the resources" );
		gd = new GridData( );
		gd.horizontalSpan = 3;
		gd.horizontalSpan = 3;
		bgButton.setLayoutData( gd );
		bgButton.addSelectionListener( new SelectionAdapter( ) {

			public void widgetSelected( SelectionEvent e )
			{
				if ( data != null )
				{
					data.getSyncBRDProResourcesData( )
							.setMinimizeToolkit( bgButton.getSelection( ) );
				}
			}
		} );

		scrollContent.setContent( composite );

		setControl( scrollContent );

		if ( data != null && data.getSyncBRDProResourcesData( ) != null )
		{
			if ( data.getSyncBRDProResourcesData( ).getTargetDirectory( ) != null )
			{
				txtDirectory.setText( data.getSyncBRDProResourcesData( )
						.getTargetDirectory( ) );
			}

			if ( data.getSyncBRDProResourcesData( ).getIgnorePlatformVersions( ) != null
					&& data.getSyncBRDProResourcesData( )
							.getIgnorePlatformVersions( ).length > 0 )
			{
				List<String> versions = Arrays.asList( data.getSyncBRDProResourcesData( )
						.getIgnorePlatformVersions( ) );
				for ( Object obj : provider.getChildren( VersionType.platform ) )
				{
					Version version = (Version) obj;
					if ( versions.contains( version.getValue( ) ) )
						platformViewer.setChecked( obj, false );
				}
				checkCheckStatus( platformViewer,
						provider,
						VersionType.platform );
			}

			bgButton.setSelection( data.getSyncBRDProResourcesData( )
					.isMinimizeToolkit( ) );

			setPageComplete( isPageComplete( ) );
		}
	}

	private void computeSize( )
	{
		scrollContent.setMinSize( composite.computeSize( SWT.DEFAULT,
				SWT.DEFAULT ) );
		composite.layout( );
	}

	public boolean isPageComplete( )
	{
		if ( data != null )
		{
			if ( data.getToolFeature( ) != ToolFeature.syncBRDProResources )
				return true;
			checkStatus( );
			return getErrorMessage( ) == null;

		}
		return false;
	}

	private void checkStatus( )
	{
		if ( data != null )
		{
			data.getSyncBRDProResourcesData( )
					.setTargetDirectory( txtDirectory.getText( ) );

			SyncResourcesContentProvider provider = (SyncResourcesContentProvider) platformViewer.getContentProvider( );
			Object[] children = provider.getChildren( VersionType.platform );
			List<String> uncheckList = new ArrayList<String>( );
			List<String> versionList = new ArrayList<String>( );
			for ( int i = 0; i < children.length; i++ )
			{
				if ( !platformViewer.getChecked( children[i] ) )
				{
					uncheckList.add( ( (Version) children[i] ).getValue( ) );
				}
				else
				{
					String versionToken = ( (Version) children[i] ).getValue( );
					versionToken = versionToken.substring( 0,
							versionToken.lastIndexOf( '.' ) );
					if ( !versionList.contains( versionToken ) )
						versionList.add( versionToken );
				}
			}
			data.getSyncBRDProResourcesData( )
					.setIgnorePlatformVersions( uncheckList.toArray( new String[0] ) );
			data.getSyncBRDProResourcesData( )
					.setPluginVersions( versionList.toArray( new String[0] ) );
		}

		if ( platformViewer.getCheckedElements( ) == null
				|| platformViewer.getCheckedElements( ).length == 0 )
		{
			setErrorMessage( "Please select at least one version of eclipse platform." );
			return;
		}
		String text = txtDirectory.getText( );
		File file = new File( text );
		if ( file.getParentFile( ) == null )
		{
			setErrorMessage( "The target directory is invalid." );
			return;
		}
		setErrorMessage( null );
		return;
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
