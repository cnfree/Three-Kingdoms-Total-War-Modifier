
package org.sf.feeling.sanguo.patch.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.sf.feeling.swt.win32.extension.util.SortMap;

public class SoldierMapUtil
{

	final static SortMap factionProperty = FileUtil.loadProperties( "faction" );

	static SortMap unitMap; // key-value:unit type - unit name
	// name
	static SortMap soldierUnitMap; // key-value:normal unit type - normal unit
	static SortMap unitTypeToDictionaryMap; // key-value:unit type - unit
	// dictionary
	static SortMap unitDictionaryToTypeMap; // key-value:unit dictionary - unit
	// type
	static List soldierUnitList;
	static List guyongUnitList;
	static SortMap unitFactionMap; // key-value:unit type - faction list
	static SortMap factionGeneralMap;// key-value:faction - general code list

	public static void initMap( )
	{
		initUnitTypeToDictionaryMap( );
		unitMap = initUnitMap( );
		initSoldierUnitMaps( );
	}

	private static SortMap initUnitMap( )
	{
		SortMap unitMap = new SortMap( );
		List unitList = unitTypeToDictionaryMap.getKeyList( );

		if ( FileConstants.exportUnitFile.exists( ) )
		{
			try
			{
				String line = null;
				BufferedReader in = new BufferedReader( new InputStreamReader( new FileInputStream( FileConstants.exportUnitFile ),
						"UTF-16LE" ) );
				while ( ( line = in.readLine( ) ) != null )
				{
					Pattern pattern = Pattern.compile( "^\\s*(\\{\\s*)(.+)(\\s*\\})(\\s*)" );
					Matcher matcher = pattern.matcher( line );
					if ( matcher.find( ) )
					{
						String unitDictionary = matcher.group( )
								.replaceAll( "(\\{)", "" )
								.replaceAll( "(\\})", "" )
								.trim( );
						if ( unitList.contains( unitDictionaryToTypeMap.get( unitDictionary ) ) )
						{
							unitMap.put( unitDictionaryToTypeMap.get( unitDictionary ),
									line.substring( matcher.end( ) )
											.split( ";" )[0].trim( ) );
						}
					}
				}
				in.close( );
			}
			catch ( IOException e )
			{
				e.printStackTrace( );
			}
		}
		return unitMap;
	}

	private static void initUnitTypeToDictionaryMap( )
	{
		unitTypeToDictionaryMap = new SortMap( );
		unitDictionaryToTypeMap = new SortMap( );
		unitFactionMap = new SortMap( );
		soldierUnitList = new ArrayList( );
		guyongUnitList = new ArrayList( );
		String[] categorys = new String[]{
				"infantry", "cavalry", "siege", "handler", "ship"
		};
		List categoryList = Arrays.asList( categorys );
		if ( FileConstants.unitFile.exists( ) )
		{
			try
			{
				String line = null;

				BufferedReader in = new BufferedReader( new InputStreamReader( new FileInputStream( FileConstants.unitFile ),
						"GBK" ) );
				boolean startSoldier = false;
				String type = null;
				String dictionary = null;
				while ( ( line = in.readLine( ) ) != null )
				{
					if ( line.split( ";" ).length == 0 )
					{
						continue;
					}
					if ( !startSoldier )
					{
						Pattern pattern = Pattern.compile( "^\\s*(type)(\\s+)" );
						Matcher matcher = pattern.matcher( line );
						if ( matcher.find( ) )
						{
							type = line.split( ";" )[0].replaceAll( "type", "" )
									.trim( );
							startSoldier = true;
						}
					}
					else
					{
						Pattern pattern = Pattern.compile( "^\\s*(dictionary)(\\s+)" );
						Matcher matcher = pattern.matcher( line );
						if ( matcher.find( ) )
						{
							dictionary = line.split( ";" )[0].replaceAll( "dictionary",
									"" )
									.trim( );
							unitTypeToDictionaryMap.put( type, dictionary );
							unitDictionaryToTypeMap.put( dictionary, type );
							continue;
						}
						pattern = Pattern.compile( "^\\s*(category)(\\s+)" );
						matcher = pattern.matcher( line );
						if ( matcher.find( ) )
						{
							String category = line.split( ";" )[0].replaceAll( "category",
									"" )
									.trim( );
							if ( !categoryList.contains( category ) )
							{
								dictionary = null;
								type = null;
								startSoldier = false;
							}
							continue;
						}
						pattern = Pattern.compile( "^\\s*(ownership)(\\s+)" );
						matcher = pattern.matcher( line );
						if ( matcher.find( ) )
						{
							String[] ownerships = line.split( ";" )[0].replaceAll( "ownership",
									"" )
									.trim( )
									.split( "," );
							List factions = new ArrayList( );
							for ( int i = 0; i < ownerships.length; i++ )
							{
								factions.add( ownerships[i].trim( )
										.toLowerCase( ) );
							}
							unitFactionMap.put( dictionary, factions );
							dictionary = null;
							type = null;
							startSoldier = false;
							continue;
						}
						pattern = Pattern.compile( "^\\s*(attributes)(\\s+)" );
						matcher = pattern.matcher( line );
						if ( matcher.find( ) )
						{
							if ( line.indexOf( "general_unit" ) == -1 )
							{
								soldierUnitList.add( type );
								if ( line.indexOf( "mercenary_unit" ) > -1 )
									guyongUnitList.add( type );
							}
							continue;
						}
					}
				}
				in.close( );
			}
			catch ( IOException e )
			{
				e.printStackTrace( );
			}
		}
	}

	private static void initSoldierUnitMaps( )
	{
		soldierUnitMap = new SortMap( );
		List renameSoldierNameList = new ArrayList( );
		Collection generalUnitNames = MapUtil.generalNameMap.getValueList( );
		for ( int i = 0; i < unitMap.size( ); i++ )
		{
			String unit = (String) unitMap.getKeyList( ).get( i );
			if ( generalUnitNames.contains( unitMap.get( unit ) ) )
			{

			}
			else if ( soldierUnitList.contains( unit ) )
			{
				if ( soldierUnitMap.getValueList( )
						.contains( unitMap.get( unit ) ) )
					renameSoldierNameList.add( unitMap.get( unit ) );
				soldierUnitMap.put( unit, unitMap.get( unit ) );
			}
		}
		for ( int i = 0; i < renameSoldierNameList.size( ); i++ )
		{
			String name = (String) renameSoldierNameList.get( i );
			for ( int j = 0; j < soldierUnitMap.size( ); j++ )
			{
				if ( soldierUnitMap.get( j ).equals( name ) )
				{
					String type = (String) soldierUnitMap.getKeyList( ).get( j );
					if ( guyongUnitList.contains( type ) )
					{
						soldierUnitMap.put( type, name + "（雇佣兵）" );
					}
					else
					{
						List list = (List) unitFactionMap.get( unitTypeToDictionaryMap.get( type ) );
						String factionName = (String) MapUtil.factionMap.get( list.get( 0 )
								.toString( )
								.toUpperCase( ) );
						if ( factionName != null )
						{
							soldierUnitMap.put( type, name
									+ "（"
									+ factionName
									+ "）" );
						}
					}
				}
			}
		}
		Collections.sort( soldierUnitMap.getKeyList( ), new Comparator( ) {

			public int compare( Object o1, Object o2 )
			{
				String name1 = (String) soldierUnitMap.get( o1 );
				String name2 = (String) soldierUnitMap.get( o2 );
				return PinyinComparator.compare( name1, name2 );
			}
		} );
	}
}
