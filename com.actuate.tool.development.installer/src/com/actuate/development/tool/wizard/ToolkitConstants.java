
package com.actuate.development.tool.wizard;

import java.io.File;

public class ToolkitConstants
{

	public static final String DIALOG_SETTING_MAXIMIZED = "Maximized";

	public static final String DIALOG_SETTING_HEIGHT = "Height";

	public static final String DIALOG_SETTING_WIDTH = "Width";

	public static final String DIALOG_SETTING_POS_Y = "PosY";

	public static final String DIALOG_SETTING_POS_X = "PosX";

	public static final String PROJECT = "Project";

	public static final String DIALOG_SETTING_FILE = new File( System.getProperties( )
			.getProperty( "user.home" )
			+ "\\.brdpro_toolkit\\userInfo.xml" ).getAbsolutePath( );

	public static final String P4ROOT = "P4Root";

	public static final String P4VIEW = "P4View";

	public static final String CUSTOMPROJECTNAME = "CustomProjectName";

	public static final String FORCEOPERATION = "ForceOperation";

	public static final String REVERTFILES = "RevertFiles";

	public static final String P4SERVER = "P4Server";

	public static final String P4USER = "P4User";

	public static final String P4PASSWORD = "P4Password";

	public static final String P4CLIENT = "P4Client";

	public static final String CURRENT_BRDPRO_PROJECT = "CurrentBRDProProject";

	public static final String CURRENT_IV_PROJECT = "CurrentIVProject";

	public static final String DIRECTORY = "Directory";

	public static final String MODULES = "Modules";

	public static final String CLEARDIRECTORY = "ClearDirectory";

	public static final String CLOSEBRDPRO = "CloseBRDPro";

	public static final String CREATESHORTCUT = "CreateShortcut";

	public static final String SHORTCUTARGUMENTS = "ShortcutArguments";
	
	public static final String BRDPROSYNCIGNOREDVERSIONS = "BRDProSyncIgnoredVersions";
	
	public static final String BRDPROSYNCPLUGINVERSIONS = "BRDProSyncPluginVersions";
	
	public static final String BRDPROSYNCTARGETDIRECTORY = "BRDProSyncTargetDirectory";
}
