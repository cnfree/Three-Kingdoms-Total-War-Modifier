
package com.actuate.development.tool.model;

import com.actuate.development.tool.config.UserDataConfig;

public class InstallBRDProData
{

	private String project;
	private String directory;
	private Module[] modules;

	public Module[] getModules( )
	{
		return modules;
	}

	public void setModules( Module[] modules )
	{
		this.modules = modules;
	}

	private boolean notClearDirectory = false;
	private boolean notCloseBRDPro = false;
	private boolean notCreateShortcut = false;
	private String shortcutArguments = UserDataConfig.getProperty( UserDataConfig.BRDPRO_SHORTCUT_ARGS,
			"-showlocation -nl en_us -vmargs -server -Xms256m -Xmx1024m -XX:PermSize=64M -XX:MaxPermSize=256M" );
	private String brdproFile;
	private boolean isInstallShield;
	private ModuleVersion moduleVersion;
	private String tempDir;

	public String getProject( )
	{
		return project;
	}

	public void setProject( String project )
	{
		this.project = project;
	}

	public String getDirectory( )
	{
		return directory;
	}

	public void setDirectory( String directory )
	{
		this.directory = directory;
	}

	public boolean isNotClearDirectory( )
	{
		return notClearDirectory;
	}

	public void setNotClearDirectory( boolean notClearDirectory )
	{
		this.notClearDirectory = notClearDirectory;
	}

	public boolean isNotCloseBRDPro( )
	{
		return notCloseBRDPro;
	}

	public void setNotCloseBRDPro( boolean notCloseBRDPro )
	{
		this.notCloseBRDPro = notCloseBRDPro;
	}

	public boolean isNotCreateShortcut( )
	{
		return notCreateShortcut;
	}

	public void setNotCreateShortcut( boolean notCreateShortcut )
	{
		this.notCreateShortcut = notCreateShortcut;
	}

	public String getShortcutArguments( )
	{
		return shortcutArguments;
	}

	public void setShortcutArguments( String shortcutArguments )
	{
		this.shortcutArguments = shortcutArguments;
	}

	public String getBrdproFile( )
	{
		return brdproFile;
	}

	public void setBrdproFile( String brdproFile )
	{
		this.brdproFile = brdproFile;
	}

	public boolean isInstallShield( )
	{
		return isInstallShield;
	}

	public void setInstallShield( boolean isInstallShield )
	{
		this.isInstallShield = isInstallShield;
	}

	public ModuleVersion getModuleVersion( )
	{
		return moduleVersion;
	}

	public void setModuleVersion( ModuleVersion moduleVersion )
	{
		this.moduleVersion = moduleVersion;
	}

	public String getTempDir( )
	{
		return tempDir;
	}

	public void setTempDir( String tempDir )
	{
		this.tempDir = tempDir;
	}

}
