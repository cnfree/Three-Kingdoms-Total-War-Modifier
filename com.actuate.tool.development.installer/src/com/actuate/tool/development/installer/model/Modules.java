
package com.actuate.tool.development.installer.model;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Modules
{

	private Map<String, Module> sdkMap = new LinkedHashMap<String, Module>( );
	private Map<String, Module> pluginMap = new LinkedHashMap<String, Module>( );
	private Map<String, Module> extensionMap = new HashMap<String, Module>( );

	private static class SingletonContainer
	{

		private final static Modules instance = new Modules( );
		static
		{
			Module.init( );
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

}
