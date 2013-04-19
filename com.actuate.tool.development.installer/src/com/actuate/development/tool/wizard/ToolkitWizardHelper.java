
package com.actuate.development.tool.wizard;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.DialogSettings;
import org.eclipse.jface.dialogs.IDialogSettings;

import com.actuate.development.tool.model.IPortalViewerData;
import com.actuate.development.tool.model.InstallBRDProData;
import com.actuate.development.tool.model.Module;
import com.actuate.development.tool.model.Modules;
import com.actuate.development.tool.model.ToolFeatureData;
import com.actuate.development.tool.model.config.PathConfig;
import com.actuate.development.tool.util.LogUtil;

public class ToolkitWizardHelper
{

	private ToolkitWizard wizard;
	private ToolFeatureData data;

	public ToolkitWizardHelper( ToolkitWizard wizard, ToolFeatureData data )
	{
		this.wizard = wizard;
		this.data = data;
	}

	public void initWizardDialogSettings( )
	{
		final DialogSettings dialogSettings = new DialogSettings( "userInfo" );
		try
		{
			if ( new File( ToolkitConstants.DIALOG_SETTING_FILE ).exists( ) )
				dialogSettings.load( ToolkitConstants.DIALOG_SETTING_FILE );

		}
		catch ( IOException e )
		{
			LogUtil.recordErrorMsg( e, false );
		}

		wizard.setDialogSettings( dialogSettings );
	}

	public void saveToolkitSettings( )
	{
		if ( data.getCurrentBRDProProject( ) != null )
		{
			wizard.getDialogSettings( )
					.put( ToolkitConstants.CURRENT_BRDPRO_PROJECT,
							data.getCurrentBRDProProject( ) );
			IDialogSettings projectSetting = wizard.getDialogSettings( )
					.getSection( data.getCurrentBRDProProject( ) );
			if ( projectSetting == null )
			{
				projectSetting = wizard.getDialogSettings( )
						.addNewSection( data.getCurrentBRDProProject( ) );
			}

			projectSetting.put( ToolkitConstants.DIRECTORY,
					data.getCurrentInstallBRDProData( ).getDirectory( ) );
			projectSetting.put( ToolkitConstants.CLEARDIRECTORY,
					data.getCurrentInstallBRDProData( ).isNotClearDirectory( ) );
			projectSetting.put( ToolkitConstants.CLOSEBRDPRO,
					data.getCurrentInstallBRDProData( ).isNotCloseBRDPro( ) );
			projectSetting.put( ToolkitConstants.CREATESHORTCUT,
					data.getCurrentInstallBRDProData( ).isNotCreateShortcut( ) );
			projectSetting.put( ToolkitConstants.SHORTCUTARGUMENTS,
					data.getCurrentInstallBRDProData( ).getShortcutArguments( ) );
			if ( data.getCurrentInstallBRDProData( ).getModules( ) != null )
			{
				StringBuffer buffer = new StringBuffer( );
				for ( int i = 0; i < data.getCurrentInstallBRDProData( )
						.getModules( ).length; i++ )
				{
					Module module = data.getCurrentInstallBRDProData( )
							.getModules( )[i];
					if ( module != null )
					{
						buffer.append( module.getName( ) );
						if ( i < data.getCurrentInstallBRDProData( )
								.getModules( ).length - 1 )
							buffer.append( ";" );
					}
				}
				projectSetting.put( ToolkitConstants.MODULES, buffer.toString( ) );
			}
			else
			{
				projectSetting.put( ToolkitConstants.MODULES, (String) null );
			}
		}

		if ( data.getCurrentIVProject( ) != null )
		{
			wizard.getDialogSettings( )
					.put( ToolkitConstants.CURRENT_IV_PROJECT,
							data.getCurrentIVProject( ) );
			IDialogSettings projectSetting = wizard.getDialogSettings( )
					.getSection( data.getCurrentIVProject( ) );
			if ( projectSetting == null )
			{
				projectSetting = wizard.getDialogSettings( )
						.addNewSection( data.getCurrentIVProject( ) );
			}

			projectSetting.put( ToolkitConstants.P4ROOT,
					data.getCurrentIportalViewerData( ).getRoot( ) );
			projectSetting.put( ToolkitConstants.P4VIEW,
					data.getCurrentIportalViewerData( ).getView( ) );
			projectSetting.put( ToolkitConstants.CUSTOMPROJECTNAME,
					data.getCurrentIportalViewerData( ).getCustomProjectName( ) );
			projectSetting.put( ToolkitConstants.FORCEOPERATION,
					data.getCurrentIportalViewerData( ).isForceOperation( ) );
			projectSetting.put( ToolkitConstants.REVERTFILES,
					data.getCurrentIportalViewerData( ).isRevertFiles( ) );
			projectSetting.put( ToolkitConstants.P4SERVER,
					data.getCurrentIportalViewerData( ).getServer( ) );
			projectSetting.put( ToolkitConstants.P4USER,
					data.getCurrentIportalViewerData( ).getUser( ) );
			projectSetting.put( ToolkitConstants.P4PASSWORD,
					data.getCurrentIportalViewerData( ).getPassword( ) );
			projectSetting.put( ToolkitConstants.P4CLIENT,
					data.getCurrentIportalViewerData( ).getClient( ) );
		}

		saveWizardDialogSettings( );
	}

	public void initToolkitConfig( )
	{
		IDialogSettings[] settings = wizard.getDialogSettings( ).getSections( );
		if ( settings != null )
		{
			for ( IDialogSettings setting : settings )
			{
				InstallBRDProData installData = new InstallBRDProData( );
				data.addInstallBRDProData( installData );
				installData.setProject( setting.getName( ) );
				if ( setting.get( ToolkitConstants.DIRECTORY ) != null )
				{
					installData.setDirectory( setting.get( ToolkitConstants.DIRECTORY ) );
				}
				if ( setting.get( ToolkitConstants.MODULES ) != null
						&& setting.get( ToolkitConstants.MODULES )
								.trim( )
								.length( ) > 0 )
				{
					String[] moduleNames = setting.get( ToolkitConstants.MODULES )
							.split( ";" );
					Module[] modules = new Module[moduleNames.length];
					for ( int i = 0; i < modules.length; i++ )
					{
						modules[i] = Modules.getInstance( )
								.valueOf( moduleNames[i] );
					}
					installData.setModules( modules );
				}
				if ( setting.get( ToolkitConstants.CLEARDIRECTORY ) != null )
				{
					installData.setNotClearDirectory( setting.getBoolean( ToolkitConstants.CLEARDIRECTORY ) );
				}
				if ( setting.get( ToolkitConstants.CLOSEBRDPRO ) != null )
				{
					installData.setNotCloseBRDPro( setting.getBoolean( ToolkitConstants.CLOSEBRDPRO ) );
				}
				if ( setting.get( ToolkitConstants.SHORTCUTARGUMENTS ) != null )
				{
					installData.setShortcutArguments( setting.get( ToolkitConstants.SHORTCUTARGUMENTS ) );
				}
				if ( setting.get( ToolkitConstants.CREATESHORTCUT ) != null )
				{
					installData.setNotCreateShortcut( setting.getBoolean( ToolkitConstants.CREATESHORTCUT ) );
				}

				IPortalViewerData ivData = new IPortalViewerData( );
				ivData.setProject( setting.getName( ) );
				if ( setting.get( ToolkitConstants.P4ROOT ) != null )
				{
					ivData.setRoot( setting.get( ToolkitConstants.P4ROOT ) );
				}
				if ( setting.get( ToolkitConstants.P4VIEW ) != null )
				{
					ivData.setView( setting.get( ToolkitConstants.P4VIEW ) );
				}
				if ( setting.get( ToolkitConstants.CUSTOMPROJECTNAME ) != null )
				{
					ivData.setCustomProjectName( setting.get( ToolkitConstants.CUSTOMPROJECTNAME ) );
				}
				if ( setting.get( ToolkitConstants.FORCEOPERATION ) != null )
				{
					ivData.setForceOperation( setting.getBoolean( ToolkitConstants.FORCEOPERATION ) );
				}
				if ( setting.get( ToolkitConstants.REVERTFILES ) != null )
				{
					ivData.setRevertFiles( setting.getBoolean( ToolkitConstants.REVERTFILES ) );
				}
				if ( setting.get( ToolkitConstants.P4SERVER ) != null )
				{
					ivData.setServer( setting.get( ToolkitConstants.P4SERVER ) );
				}
				if ( setting.get( ToolkitConstants.P4USER ) != null )
				{
					ivData.setUser( setting.get( ToolkitConstants.P4USER ) );
				}
				if ( setting.get( ToolkitConstants.P4PASSWORD ) != null )
				{
					ivData.setPassword( setting.get( ToolkitConstants.P4PASSWORD ) );
				}
				if ( setting.get( ToolkitConstants.P4CLIENT ) != null )
				{
					ivData.setClient( setting.get( ToolkitConstants.P4CLIENT ) );
				}
				data.addIPortalViewerData( ivData );
			}
		}

		if ( wizard.getDialogSettings( )
				.get( ToolkitConstants.CURRENT_BRDPRO_PROJECT ) != null )
		{
			data.setCurrentBRDProProject( wizard.getDialogSettings( )
					.get( ToolkitConstants.CURRENT_BRDPRO_PROJECT ) );
		}

		if ( wizard.getDialogSettings( )
				.get( ToolkitConstants.CURRENT_IV_PROJECT ) != null )
		{
			data.setCurrentIVProject( wizard.getDialogSettings( )
					.get( ToolkitConstants.CURRENT_IV_PROJECT ) );
		}
	}

	private void saveWizardDialogSettings( )
	{
		try
		{
			File file = new File( ToolkitConstants.DIALOG_SETTING_FILE );
			if ( !file.exists( ) )
			{
				if ( !file.getParentFile( ).exists( ) )
					file.getParentFile( ).mkdirs( );
			}
			wizard.getDialogSettings( )
					.save( ToolkitConstants.DIALOG_SETTING_FILE );
		}
		catch ( IOException e1 )
		{
			LogUtil.recordErrorMsg( e1, false );
		}
	}

	public void checkActuateBuildFile( File file, final List<File> brdproFiles,
			final List<File> iportalViewerFiles )
	{
		final String[] subDirRegexs = PathConfig.getProperty( PathConfig.ACTUATE_BUILD_SUB_DIR,
				"(?i)ActuateBirtViewer;(?i)BRDPro" )
				.split( ";" );
		final String[] brdProRegexs = PathConfig.getProperty( PathConfig.BRDPRO,
				"(?i)actuatebirtdesignerprofessional.*\\.zip;(?i)brdpro.*\\.zip" )
				.split( ";" );
		final String[] iPortalRegexs = PathConfig.getProperty( PathConfig.IPORTAL,
				"(?i)actuatebirtviewer.*\\.zip;(?i)wl_tomcat_actuatebirtjavacomponent.*\\.war" )
				.split( ";" );

		File[] children = file.listFiles( new FileFilter( ) {

			public boolean accept( File file )
			{
				String fileName = file.getName( ).toLowerCase( );
				if ( file.isFile( ) )
				{
					for ( int i = 0; i < brdProRegexs.length; i++ )
					{
						if ( fileName.matches( brdProRegexs[i] ) )
						{
							brdproFiles.add( file );
							return false;
						}
					}

					for ( int i = 0; i < iPortalRegexs.length; i++ )
					{
						if ( fileName.matches( iPortalRegexs[i] ) )
						{
							iportalViewerFiles.add( file );
							return false;
						}
					}
				}
				else
				{
					for ( int i = 0; i < subDirRegexs.length; i++ )
					{
						if ( fileName.matches( subDirRegexs[i] ) )
							return true;
					}
				}
				return false;
			}

		} );

		if ( children != null )
		{
			for ( int i = 0; i < children.length; i++ )
			{
				checkActuateBuildFile( children[i],
						brdproFiles,
						iportalViewerFiles );
			}
		}
	}

	public void collectInstallationFiles( final IProgressMonitor monitor )
	{
		final String[][] fileNames = new String[1][];

		String buildDir = PathConfig.getProperty( PathConfig.ACTUATE_BUILD_DIR,
				"\\\\qaant\\ActuateBuild" );
		File file = new File( buildDir );
		fileNames[0] = file.list( new FilenameFilter( ) {

			public boolean accept( File dir, String name )
			{
				File file = new File( dir, name );
				monitor.subTask( "Scanning " + file.getAbsolutePath( ) );
				List<File> brdproFiles = new ArrayList<File>( );
				List<File> iportalViewerFiles = new ArrayList<File>( );

				checkActuateBuildFile( file, brdproFiles, iportalViewerFiles );

				if ( brdproFiles.size( ) > 0 )
				{
					if ( !data.getBrdproMap( ).containsKey( name ) )
						data.getBrdproMap( ).put( name, new ArrayList<File>( ) );
					data.getBrdproMap( ).get( name ).addAll( brdproFiles );
				}
				if ( iportalViewerFiles.size( ) > 0
						&& Modules.getInstance( )
								.getIPortalProjects( )
								.contains( name ) )
				{
					if ( !data.getIportalViewMap( ).containsKey( name ) )
						data.getIportalViewMap( ).put( name,
								new ArrayList<File>( ) );
					data.getIportalViewMap( )
							.get( name )
							.addAll( iportalViewerFiles );
				}
				if ( brdproFiles.size( ) > 0 || iportalViewerFiles.size( ) > 0 )
					return true;
				return false;
			}

		} );
	}
}
