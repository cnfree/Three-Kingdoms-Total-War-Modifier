
package com.actuate.development.tool.wizard;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;
import org.sf.feeling.swt.win32.extension.Win32;
import org.sf.feeling.swt.win32.extension.shell.Windows;
import org.sf.feeling.swt.win32.internal.extension.util.ImageCache;

import com.actuate.development.tool.model.ToolFeature;
import com.actuate.development.tool.model.ToolFeatureData;
import com.actuate.development.tool.task.CloneWorkspaceSettings;
import com.actuate.development.tool.task.InstallBRDPro;
import com.actuate.development.tool.task.SyncIPortalWorkspace;
import com.actuate.development.tool.util.LogUtil;
import com.actuate.development.tool.util.UIUtil;

public class ToolkitWizard extends Wizard
{

	// the model object.
	private ToolFeatureData data = new ToolFeatureData( );
	private ToolkitWizardHelper helper;

	public ToolkitWizard( )
	{
		setWindowTitle( "Actuate BRDPro Development Toolkit" );
		setNeedsProgressMonitor( true );
		helper = new ToolkitWizardHelper( this, data );
		helper.initWizardDialogSettings( );
		helper.initToolkitConfig( );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.wizard.IWizard#addPages()
	 */
	public void addPages( )
	{
		initShell( );

		addPage( new ToolFeaturePage( data ) );
		addPage( new CloneWorkspaceSettingsPage( data ) );
		addPage( new BRDProProjectPage( data ) );
		addPage( new BRDProSettingPage( data ) );
		addPage( new BRDProShortcutPage( data ) );
		addPage( new IPortalViewerProjectPage( data ) );
	}

	private void initShell( )
	{
		UIUtil.setShell( this.getShell( ) );
		Windows.setWindowStyle( this.getShell( ).handle,
				Windows.getWindowStyle( this.getShell( ).handle )
						| Win32.WS_MINIMIZEBOX );
		this.getShell( ).setImages( new Image[]{
				ImageCache.getImage( "/icons/actuate_16.png" ),
				ImageCache.getImage( "/icons/actuate_32.png" ),
				ImageCache.getImage( "/icons/actuate_48.png" )
		} );
		if ( getDialogSettings( ).get( ToolkitConstants.DIALOG_SETTING_WIDTH ) != null
				&& getDialogSettings( ).get( ToolkitConstants.DIALOG_SETTING_HEIGHT ) != null )
			this.getShell( )
					.setSize( getDialogSettings( ).getInt( ToolkitConstants.DIALOG_SETTING_WIDTH ),
							getDialogSettings( ).getInt( ToolkitConstants.DIALOG_SETTING_HEIGHT ) );
		else
		{
			this.getShell( ).setSize( 700, 600 );
		}
		if ( getDialogSettings( ).get( ToolkitConstants.DIALOG_SETTING_POS_X ) != null
				&& getDialogSettings( ).get( ToolkitConstants.DIALOG_SETTING_POS_Y ) != null )
			this.getShell( )
					.setLocation( getDialogSettings( ).getInt( ToolkitConstants.DIALOG_SETTING_POS_X ),
							getDialogSettings( ).getInt( ToolkitConstants.DIALOG_SETTING_POS_Y ) );
		else
		{
			int width = this.getShell( ).getMonitor( ).getClientArea( ).width;
			int height = this.getShell( ).getMonitor( ).getClientArea( ).height;
			int x = this.getShell( ).getSize( ).x;
			int y = this.getShell( ).getSize( ).y;
			this.getShell( )
					.setLocation( ( width - x ) / 2, ( height - y ) / 2 );
		}
		if ( getDialogSettings( ).get( ToolkitConstants.DIALOG_SETTING_MAXIMIZED ) != null )
			this.getShell( )
					.setMaximized( getDialogSettings( ).getBoolean( ToolkitConstants.DIALOG_SETTING_MAXIMIZED ) );

		this.getShell( ).addControlListener( new ControlListener( ) {

			public void controlMoved( ControlEvent e )
			{
				collectShellInfos( );

			}

			public void controlResized( ControlEvent e )
			{
				collectShellInfos( );
			}

			private void collectShellInfos( )
			{
				if ( !ToolkitWizard.this.getShell( ).getMaximized( )
						&& !ToolkitWizard.this.getShell( ).getMinimized( )
						&& ToolkitWizard.this.getShell( ).getVisible( ) )
				{
					Point location = ToolkitWizard.this.getShell( )
							.getLocation( );
					Point size = ToolkitWizard.this.getShell( ).getSize( );
					getDialogSettings( ).put( ToolkitConstants.DIALOG_SETTING_POS_X,
							location.x );
					getDialogSettings( ).put( ToolkitConstants.DIALOG_SETTING_POS_Y,
							location.y );
					getDialogSettings( ).put( ToolkitConstants.DIALOG_SETTING_WIDTH,
							size.x );
					getDialogSettings( ).put( ToolkitConstants.DIALOG_SETTING_HEIGHT,
							size.y );
				}

				if ( ToolkitWizard.this.getShell( ).getMaximized( ) )
				{
					getDialogSettings( ).put( ToolkitConstants.DIALOG_SETTING_MAXIMIZED,
							true );
				}
				else
				{
					getDialogSettings( ).put( ToolkitConstants.DIALOG_SETTING_MAXIMIZED,
							false );
				}
			}

		} );

		this.getShell( ).addDisposeListener( new DisposeListener( ) {

			public void widgetDisposed( DisposeEvent e )
			{
				helper.saveToolkitSettings( );
			}

		} );

		this.getShell( ).addShellListener( new ShellAdapter( ) {

			public void shellActivated( ShellEvent e )
			{
				Windows.flashWindow( getShell( ).handle, false );
			}

			public void shellDeiconified( ShellEvent e )
			{
				Windows.flashWindow( getShell( ).handle, false );
			}

		} );

		try
		{
			ProgressMonitorDialog dialog = new ProgressMonitorDialog( null ) {

				protected void configureShell( Shell shell )
				{
					super.configureShell( shell );
					shell.setImages( new Image[]{
							ImageCache.getImage( "/icons/actuate_16.png" ),
							ImageCache.getImage( "/icons/actuate_32.png" ),
							ImageCache.getImage( "/icons/actuate_48.png" )
					} );
					shell.forceActive( );
				}

				protected void cancelPressed( )
				{
					System.exit( 0 );
				}
			};

			dialog.run( true, true, new IRunnableWithProgress( ) {

				public void run( final IProgressMonitor monitor )
						throws InvocationTargetException, InterruptedException
				{
					monitor.beginTask( "Collecting Actuate Build Projects...",
							IProgressMonitor.UNKNOWN );
					helper.collectInstallationFiles( monitor );
					monitor.done( );
				}
			} );
		}
		catch ( Exception e )
		{
			LogUtil.recordErrorMsg( e, false );
		}
	}

	public IWizardPage getNextPage( IWizardPage page )
	{
		List<IWizardPage> pages = Arrays.asList( this.getPages( ) );
		int index = pages.indexOf( page );
		if ( index == pages.size( ) - 1 || index == -1 )
		{
			return null;
		}
		if ( index == 0 )
		{
			if ( data.getToolFeature( ) == ToolFeature.installBRDPro )
				return pages.get( 2 );
			else if ( data.getToolFeature( ) == ToolFeature.synciPortalWorkspace )
				return pages.get( 5 );
			else
				return pages.get( 1 );
		}
		else if ( index == 1 )
			return null;
		else if ( index == 4 )
			return null;
		else
			return (IWizardPage) pages.get( index + 1 );
	}

	public boolean performFinish( )
	{
		if ( getDialogSettings( ) != null )
		{
			helper.saveToolkitSettings( );
		}

		try
		{
			// puts the data into a database ...
			getContainer( ).run( true, true, new IRunnableWithProgress( ) {

				public void run( IProgressMonitor monitor )
						throws InvocationTargetException, InterruptedException
				{

					if ( data != null )
					{
						if ( data.getToolFeature( ) == ToolFeature.installBRDPro )
						{
							new InstallBRDPro( data.getCurrentInstallBRDProData( ) ).execute( monitor );
						}
						else if ( data.getToolFeature( ) == ToolFeature.cloneWorkspaceSettings )
						{
							new CloneWorkspaceSettings( data ).execute( monitor );
						}
						else if ( data.getToolFeature( ) == ToolFeature.synciPortalWorkspace )
						{
							new SyncIPortalWorkspace( data.getCurrentIportalViewerData( ) ).execute( monitor );
						}
					}
					monitor.done( );
				}
			} );
		}
		catch ( Exception e )
		{
			LogUtil.recordErrorMsg( e, true );
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.wizard.IWizard#performCancel()
	 */
	public boolean performCancel( )
	{
		boolean ans = MessageDialog.openConfirm( getShell( ),
				"Confirmation",
				"Are you sure to cancel the task?" );
		if ( ans )
			return true;
		else
			return false;
	}

}
