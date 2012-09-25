
package com.actuate.tool.development.installer.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Modules
{

	private Map<String, Module> sdkMap = new LinkedHashMap<String, Module>( );
	private Map<String, Module> pluginMap = new LinkedHashMap<String, Module>( );
	private Map<String, Module> extensionMap = new HashMap<String, Module>( );

	private Map<String, IPortalConfig> iPortalConfigMap = new HashMap<String, IPortalConfig>( );

	private static class SingletonContainer
	{

		private final static Modules instance = new Modules( );
		static
		{
			Installer.init( );
		}

	}

	public final static Modules getInstance( )
	{
		return SingletonContainer.instance;
	}

	public void register( Module module )
	{
		switch ( module.getType( ) )
		{
			case sdk :
				sdkMap.put( module.getName( ), module );
				break;
			case plugin :
				pluginMap.put( module.getName( ), module );
				break;
			case extension :
				extensionMap.put( module.getName( ), module );
				break;
		}
	}

	public Module getSDK( String sdkName )
	{
		return sdkMap.get( sdkName );
	}

	public Module[] getSDKs( )
	{
		Module[] modules = getModules( sdkMap, false );
		return modules;
	}

	public Module[] getPlugins( )
	{
		Module[] modules = getModules( pluginMap, false );
		return modules;
	}

	public Module[] getExtensions( )
	{
		Module[] modules = getModules( extensionMap, true );
		return modules;
	}

	private Module[] getModules( Map<String, Module> map, boolean sort )
	{
		String[] names = map.keySet( ).toArray( new String[0] );
		if ( sort )
			Arrays.sort( names );
		Module[] modules = new Module[names.length];
		for ( int i = 0; i < names.length; i++ )
		{
			modules[i] = map.get( names[i] );
		}
		return modules;
	}

	public Module getPlugin( String pluginName )
	{
		return pluginMap.get( pluginName );
	}

	public Module getExtension( String extensionName )
	{
		return pluginMap.get( extensionName );
	}

	public Module valueOf( String moduleName )
	{
		Module module = sdkMap.get( moduleName );
		if ( module == null )
			module = pluginMap.get( moduleName );
		if ( module == null )
			module = extensionMap.get( moduleName );
		return module;
	}

	public void addIPortalConfig( String project, IPortalConfig config )
	{
		iPortalConfigMap.put( project, config );
	}

	public void removeIPortalConfig( String project )
	{
		iPortalConfigMap.remove( project );
	}

	public boolean containsIPortalConfig( String project )
	{
		return iPortalConfigMap.containsKey( project );
	}

	public String[] getIPortalViews( )
	{
		List<String> views = new ArrayList<String>( );
		IPortalConfig[] configs = iPortalConfigMap.values( )
				.toArray( new IPortalConfig[0] );
		for ( int i = 0; i < configs.length; i++ )
		{
			views.add( configs[i].getDefaultView( ) );
		}
		return views.toArray( new String[0] );
	}

	public String getIPortalView( String project )
	{
		IPortalConfig config = iPortalConfigMap.get( project );
		if ( config != null )
			return config.getDefaultView( );
		return null;
	}

	public File getIPortalRepalceFile( String project )
	{
		IPortalConfig config = iPortalConfigMap.get( project );
		if ( config != null )
		{
			File file = new File( "\\\\GUI-VISTA\\shared\\plugins\\"
					+ config.getReplaceFile( ) );
			if ( file.exists( ) )
				return file;
		}
		return null;
	}

}
