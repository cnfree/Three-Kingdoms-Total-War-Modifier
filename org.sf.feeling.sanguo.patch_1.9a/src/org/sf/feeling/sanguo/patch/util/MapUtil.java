
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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.sf.feeling.sanguo.patch.model.FactionDescription;
import org.sf.feeling.sanguo.patch.model.FactionTexture;
import org.sf.feeling.sanguo.patch.model.General;
import org.sf.feeling.swt.win32.extension.graphics.TgaLoader;
import org.sf.feeling.swt.win32.extension.util.SortMap;

public class MapUtil
{

	static
	{
		if ( !FileConstants.factionPropertiesFile.exists( ) )
		{
			FileUtil.writeToBinarayFile( FileConstants.factionPropertiesFile,
					MapUtil.class.getResourceAsStream( "/org/sf/feeling/sanguo/patch/code/faction.properties" ) );
		}
	}

	public static final String HANDLER = "handler";

	public static final String SIEGE = "siege";

	public static final String INFANTRY = "infantry";

	public static final String CAVALRY = "cavalry";

	static SortMap unitMap; // key-value:unit type - unit name
	static SortMap generalNameMap; // key-value:general code - general name
	static SortMap availabelGeneralNameMap; // key-value:general code - general
	// name (have image index)
	static SortMap generalUnitMap; // key-value:general unit type - general unit
	// name
	static SortMap availableGeneralUnitMap; // key-value:general unit type -
	// general unit
	// name
	static SortMap soldierUnitMap; // key-value:normal unit type - normal unit
	// name
	static SortMap generalUnitTypeMap; // key-value:general code - unit type
	static SortMap unitTypeToDictionaryMap; // key-value:unit type - unit
	// dictionary
	static SortMap unitDictionaryToTypeMap; // key-value:unit dictionary - unit
	// type
	static List soldierUnitList;
	static List guyongUnitList;
	static List generalUnitList;
	static SortMap generalOrderMap; // key-value:index - general code
	static SortMap factionMap; // key-value:faction - faction name
	static SortMap unitFactionMap; // key-value:unit type - faction list
	static SortMap factionGeneralMap;// key-value:faction - general code list
	static SortMap factionLeaderMap;// key-value:faction - general code list
	static SortMap factionHeirMap;// key-value:faction - general code list
	static SortMap horseMap;// key-value:faction - general code list
	static SortMap officerMap;// key-value:faction - general code list
	static SortMap mountTypeToModelMap;
	static SortMap mountModelToTypeMap;
	static SortMap generalModelMap;
	static List nonRelativeGeneralList;
	static SortMap categoryMap;
	static SortMap factionTextureMap;
	static SortMap factionDescriptionMap;
	static HashSet unAvailableGeneralPoints = new HashSet( );
	static SortMap factionProperty;

	static
	{
		try
		{
			ImageData mapRegion = TgaLoader.loadImage( new FileInputStream( FileConstants.mapRegionFile ) );
			int x, y;
			RGB black = new RGB( 0, 0, 0 );
			RGB white = new RGB( 255, 255, 255 );
			for ( x = 0; x < 180; x++ )
			{
				for ( y = 0; y < 180; y++ )
				{
					if ( black.equals( mapRegion.palette.getRGB( mapRegion.getPixel( x,
							y ) ) )
							|| white.equals( mapRegion.palette.getRGB( mapRegion.getPixel( x,
									y ) ) ) )
					{
						unAvailableGeneralPoints.add( new Point( x, 179 - y ) );
					}
				}
			}

			ImageData mapFeature = TgaLoader.loadImage( new FileInputStream( FileConstants.mapFeatureFile ) );
			for ( x = 0; x < 180; x++ )
			{
				for ( y = 0; y < 180; y++ )
				{
					RGB rgb = mapFeature.palette.getRGB( mapFeature.getPixel( x,
							y ) );
					if ( rgb.blue == 255 )
					{
						unAvailableGeneralPoints.add( new Point( x, 179 - y ) );
					}
				}
			}

			ImageData mapGround = TgaLoader.loadImage( new FileInputStream( FileConstants.mapGroundFile ) );
			List exclueds = Arrays.asList( new RGB[]{
					new RGB( 196, 128, 128 ),
					new RGB( 98, 65, 65 ),
					new RGB( 64, 0, 0 ),
					new RGB( 196, 0, 0 ),
					new RGB( 128, 0, 0 ),
					new RGB( 0, 64, 0 )
			} );

			for ( x = 1; x < 381; x++ )
			{
				for ( y = 1; y < 361; y++ )
				{
					RGB rgb = mapGround.palette.getRGB( mapGround.getPixel( x,
							y ) );
					if ( exclueds.contains( rgb ) )
					{
						int posX = ( x - 1 ) / 2;
						int posY = 179 - y / 2;
						unAvailableGeneralPoints.add( new Point( posX, posY ) );
					}
				}
			}

			for ( x = 1; x < 381; x++ )
			{
				for ( y = 1; y < 361; y++ )
				{
					RGB rgb = mapGround.palette.getRGB( mapGround.getPixel( x,
							y ) );
					if ( !exclueds.contains( rgb ) )
					{
						int posX = ( x - 1 ) / 2;
						int posY = 179 - y / 2;
						unAvailableGeneralPoints.remove( new Point( posX, posY ) );
					}
				}
			}
		}
		catch ( IOException e )
		{
			e.printStackTrace( );
		}
	}

	public static void initMap( )
	{
		factionProperty = FileUtil.loadProperties( "faction" );
		initUnitTypeToDictionaryMap( );
		initMountTypeToModelMap( );
		unitMap = initUnitMap( );
		generalOrderMap = initGeneralOrderMap( );
		generalNameMap = initGeneralNameMap( );
		factionMap = initFactionMap( );
		availabelGeneralNameMap = initAvailableGeneralNameMap( );
		initGeneralUnitMaps( );
		initAvailableOfficerAndHorses( );
		SoldierMapUtil.initMap( );
		initNonRelativeGeneralList( );
		factionTextureMap = initFactionTextureMap( );
		factionDescriptionMap = initFactionDescriptionMap( );
	}

	private static SortMap initFactionTextureMap( )
	{
		SortMap factionTextureMap = new SortMap( );
		if ( FileConstants.descrBannersFile.exists( ) )
		{
			try
			{
				String line = null;
				BufferedReader in = new BufferedReader( new InputStreamReader( new FileInputStream( FileConstants.descrBannersFile ),
						"GBK" ) );
				boolean startFaction = false;
				String faction = null;
				while ( ( line = in.readLine( ) ) != null )
				{

					{
						Pattern pattern = Pattern.compile( "^\\s*(faction)(\\s+)",
								Pattern.CASE_INSENSITIVE );
						Matcher matcher = pattern.matcher( line );
						if ( matcher.find( ) )
						{
							startFaction = false;
							faction = line.replaceAll( "(?i)faction", "" )
									.trim( )
									.toUpperCase( );
							if ( factionProperty.containsValue( faction.toUpperCase( ) ) )
							{
								factionTextureMap.put( faction,
										new FactionTexture( ) );
								startFaction = true;
								continue;
							}
						}
					}
					if ( startFaction )
					{
						Pattern pattern = Pattern.compile( "^\\s*(standard_texture)(\\s+)",
								Pattern.CASE_INSENSITIVE );
						Matcher matcher = pattern.matcher( line );
						if ( matcher.find( ) )
						{
							String standard_texture = line.replaceAll( "(?i)(standard_texture)",
									"" )
									.trim( );
							( (FactionTexture) factionTextureMap.get( faction ) ).setStandard_texture( standard_texture );
							continue;
						}
					}
					{
						Pattern pattern = Pattern.compile( "^\\s*(rebels_texture)(\\s+)",
								Pattern.CASE_INSENSITIVE );
						Matcher matcher = pattern.matcher( line );
						if ( matcher.find( ) )
						{
							String rebels_texture = line.replaceAll( "(?i)rebels_texture",
									"" )
									.trim( );
							( (FactionTexture) factionTextureMap.get( faction ) ).setRebels_texture( rebels_texture );
							continue;
						}
					}
					{
						Pattern pattern = Pattern.compile( "^\\s*(routing_texture)(\\s+)",
								Pattern.CASE_INSENSITIVE );
						Matcher matcher = pattern.matcher( line );
						if ( matcher.find( ) )
						{
							String routing_texture = line.replaceAll( "(?i)routing_texture",
									"" )
									.trim( );
							( (FactionTexture) factionTextureMap.get( faction ) ).setRouting_texture( routing_texture );
							continue;
						}
					}
					{
						Pattern pattern = Pattern.compile( "^\\s*(ally_texture)(\\s+)",
								Pattern.CASE_INSENSITIVE );
						Matcher matcher = pattern.matcher( line );
						if ( matcher.find( ) )
						{
							String ally_texture = line.replaceAll( "(?i)ally_texture",
									"" )
									.trim( );
							( (FactionTexture) factionTextureMap.get( faction ) ).setAlly_texture( ally_texture );
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

		return factionTextureMap;
	}

	private static SortMap initFactionDescriptionMap( )
	{
		SortMap factionDescriptionMap = new SortMap( );
		if ( FileConstants.descrFactionsFile.exists( ) )
		{
			try
			{
				String line = null;
				BufferedReader in = new BufferedReader( new InputStreamReader( new FileInputStream( FileConstants.descrFactionsFile ),
						"GBK" ) );
				boolean startFaction = false;
				String faction = null;
				while ( ( line = in.readLine( ) ) != null )
				{

					{
						Pattern pattern = Pattern.compile( "^\\s*(faction)(\\s+)",
								Pattern.CASE_INSENSITIVE );
						Matcher matcher = pattern.matcher( line );
						if ( matcher.find( ) )
						{
							startFaction = false;
							faction = line.replaceAll( "(?i)faction", "" )
									.trim( )
									.toUpperCase( );
							if ( factionProperty.containsValue( faction.toUpperCase( ) ) )
							{
								factionDescriptionMap.put( faction,
										new FactionDescription( ) );
								startFaction = true;
								continue;
							}
						}
					}
					if ( startFaction )
					{
						Pattern pattern = Pattern.compile( "^\\s*(culture)(\\s+)",
								Pattern.CASE_INSENSITIVE );
						Matcher matcher = pattern.matcher( line );
						if ( matcher.find( ) )
						{
							String culture = line.replaceAll( "(?i)(culture)",
									"" ).trim( );
							( (FactionDescription) factionDescriptionMap.get( faction ) ).setCulture( culture );
							continue;
						}
					}
					{
						Pattern pattern = Pattern.compile( "^\\s*(symbol)(\\s+)",
								Pattern.CASE_INSENSITIVE );
						Matcher matcher = pattern.matcher( line );
						if ( matcher.find( ) )
						{
							String symbol = line.replaceFirst( "(?i)symbol", "" )
									.trim( );
							( (FactionDescription) factionDescriptionMap.get( faction ) ).setSymbol( symbol );
							continue;
						}
					}
					{
						Pattern pattern = Pattern.compile( "^\\s*(primary_colour)(\\s+)",
								Pattern.CASE_INSENSITIVE );
						Matcher matcher = pattern.matcher( line );
						if ( matcher.find( ) )
						{
							String primary_colour = line.replaceAll( "(?i)primary_colour",
									"" )
									.trim( );
							( (FactionDescription) factionDescriptionMap.get( faction ) ).setPrimary_colour( primary_colour );
							continue;
						}
					}
					{
						Pattern pattern = Pattern.compile( "^\\s*(secondary_colour)(\\s+)",
								Pattern.CASE_INSENSITIVE );
						Matcher matcher = pattern.matcher( line );
						if ( matcher.find( ) )
						{
							String secondary_colour = line.replaceAll( "(?i)secondary_colour",
									"" )
									.trim( );
							( (FactionDescription) factionDescriptionMap.get( faction ) ).setSecondary_colour( secondary_colour );
							continue;
						}
					}
					{
						Pattern pattern = Pattern.compile( "^\\s*(loading_logo)(\\s+)",
								Pattern.CASE_INSENSITIVE );
						Matcher matcher = pattern.matcher( line );
						if ( matcher.find( ) )
						{
							String loading_logo = line.replaceAll( "(?i)loading_logo",
									"" )
									.trim( );
							( (FactionDescription) factionDescriptionMap.get( faction ) ).setLoading_logo( loading_logo );
							continue;
						}
					}
					{
						Pattern pattern = Pattern.compile( "^\\s*(standard_index)(\\s+)",
								Pattern.CASE_INSENSITIVE );
						Matcher matcher = pattern.matcher( line );
						if ( matcher.find( ) )
						{
							String standard_index = line.replaceAll( "(?i)standard_index",
									"" )
									.trim( );
							( (FactionDescription) factionDescriptionMap.get( faction ) ).setStandard_index( standard_index );
							continue;
						}
					}
					{
						Pattern pattern = Pattern.compile( "^\\s*(custom_battle_availability)(\\s+)",
								Pattern.CASE_INSENSITIVE );
						Matcher matcher = pattern.matcher( line );
						if ( matcher.find( ) )
						{
							String custom_battle_availability = line.replaceAll( "(?i)custom_battle_availability",
									"" )
									.trim( );
							( (FactionDescription) factionDescriptionMap.get( faction ) ).setCustom_battle_availability( custom_battle_availability );
							continue;
						}
					}
					{
						Pattern pattern = Pattern.compile( "^\\s*(prefers_naval_invasions)(\\s+)",
								Pattern.CASE_INSENSITIVE );
						Matcher matcher = pattern.matcher( line );
						if ( matcher.find( ) )
						{
							String prefers_naval_invasions = line.replaceAll( "(?i)prefers_naval_invasions",
									"" )
									.trim( );
							( (FactionDescription) factionDescriptionMap.get( faction ) ).setPrefers_naval_invasions( prefers_naval_invasions );
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

		return factionDescriptionMap;
	}

	private static void initNonRelativeGeneralList( )
	{
		nonRelativeGeneralList = new ArrayList( );

		for ( int i = 0; i < generalModelMap.getKeyList( ).size( ); i++ )
		{
			String general = (String) generalModelMap.getKeyList( ).get( i );
			General model = ( (General) generalModelMap.get( general ) );
			if ( model.isLeader( ) )
				continue;
			else if ( model.isHeir( ) )
				continue;
			if ( availabelGeneralNameMap.get( general ) == null )
				continue;
			nonRelativeGeneralList.add( general );
		}

		try
		{
			BufferedReader in = new BufferedReader( new InputStreamReader( new FileInputStream( FileConstants.stratFile ),
					"GBK" ) );
			String line = null;
			while ( ( line = in.readLine( ) ) != null )
			{
				Pattern pattern = Pattern.compile( "^\\s*(relative)(\\s+)([([a-zA-Z0-9_\\-]+)]+)(\\s*)",
						Pattern.CASE_INSENSITIVE );
				Matcher matcher = pattern.matcher( line );
				if ( matcher.find( ) )
				{
					String general = matcher.group( )
							.replaceAll( "(?i)relative", "" )
							.trim( );
					nonRelativeGeneralList.remove( general );
				}
			}
		}
		catch ( IOException e )
		{
			e.printStackTrace( );
		}

		Collections.sort( nonRelativeGeneralList, new Comparator( ) {

			public int compare( Object o1, Object o2 )
			{
				String name1 = (String) availabelGeneralNameMap.get( o1 );
				String name2 = (String) availabelGeneralNameMap.get( o2 );
				return PinyinComparator.compare( name1, name2 );
			}
		} );
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

						String type = (String) unitDictionaryToTypeMap.get( unitDictionary );
						if ( type == null )
							continue;
						String[] types = type.split( "," );
						for ( int i = 0; i < types.length; i++ )
						{
							if ( unitList.contains( types[i] ) )
							{

								unitMap.put( types[i],
										line.substring( matcher.end( ) )
												.split( ";" )[0].trim( ) );
							}
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
		generalUnitList = new ArrayList( );
		categoryMap = new SortMap( );
		categoryMap.put( INFANTRY, new SortMap( ) );
		categoryMap.put( CAVALRY, new SortMap( ) );
		categoryMap.put( SIEGE, new SortMap( ) );
		categoryMap.put( HANDLER, new SortMap( ) );

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
				String category = null;
				String soldier = null;

				while ( ( line = in.readLine( ) ) != null )
				{
					if ( line.split( ";" ).length == 0 )
					{
						continue;
					}
					if ( !startSoldier )
					{
						Pattern pattern = Pattern.compile( "^\\s*(type)(\\s+)",
								Pattern.CASE_INSENSITIVE );
						Matcher matcher = pattern.matcher( line );
						if ( matcher.find( ) )
						{
							type = line.split( ";" )[0].replaceAll( "(?i)type",
									"" ).trim( );
							startSoldier = true;
						}
					}
					else
					{
						Pattern pattern = Pattern.compile( "^\\s*(dictionary)(\\s+)",
								Pattern.CASE_INSENSITIVE );
						Matcher matcher = pattern.matcher( line );
						if ( matcher.find( ) )
						{
							dictionary = line.split( ";" )[0].replaceAll( "(?i)dictionary",
									"" )
									.trim( );
							unitTypeToDictionaryMap.put( type, dictionary );
							if ( unitDictionaryToTypeMap.containsKey( dictionary ) )
							{
								unitDictionaryToTypeMap.put( dictionary,
										unitDictionaryToTypeMap.get( dictionary )
												+ ","
												+ type );
							}
							else
								unitDictionaryToTypeMap.put( dictionary, type );
							continue;
						}
						pattern = Pattern.compile( "^\\s*(category)(\\s+)",
								Pattern.CASE_INSENSITIVE );
						matcher = pattern.matcher( line );
						if ( matcher.find( ) )
						{
							category = line.split( ";" )[0].replaceAll( "(?i)category",
									"" )
									.trim( );
							if ( !categoryMap.keySet( ).contains( category ) )
							{
								dictionary = null;
								type = null;
								startSoldier = false;
							}
							continue;
						}
						pattern = Pattern.compile( "^\\s*(soldier)(\\s+)(\\S+\\s*,)",
								Pattern.CASE_INSENSITIVE );
						matcher = pattern.matcher( line );
						if ( matcher.find( ) )
						{
							soldier = matcher.group( )
									.trim( )
									.replaceAll( "(?i)soldier", "" )
									.replaceAll( ",", "" )
									.trim( );

							continue;
						}

						pattern = Pattern.compile( "^\\s*(ownership)(\\s+)" );
						matcher = pattern.matcher( line );
						if ( matcher.find( ) )
						{

							( (SortMap) categoryMap.get( category ) ).put( type,
									soldier );

							String[] ownerships = line.split( ";" )[0].replaceAll( "ownership",
									"" )
									.trim( )
									.split( "," );
							List factions = new ArrayList( );
							for ( int i = 0; i < ownerships.length; i++ )
							{
								factions.add( ownerships[i].trim( )
										.toUpperCase( ) );
							}
							unitFactionMap.put( dictionary, factions );
							dictionary = null;
							type = null;
							startSoldier = false;
							continue;
						}
						pattern = Pattern.compile( "^\\s*(attributes)(\\s+)",
								Pattern.CASE_INSENSITIVE );
						matcher = pattern.matcher( line );
						if ( matcher.find( ) )
						{
							if ( line.toLowerCase( ).indexOf( "general_unit" ) == -1 )
							{
								soldierUnitList.add( type );
								if ( line.toLowerCase( )
										.indexOf( "mercenary_unit" ) > -1 )
									guyongUnitList.add( type );
							}
							else
								generalUnitList.add( type );
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

	private static SortMap initGeneralNameMap( )
	{
		List generalList = generalOrderMap.getValueList( );
		SortMap generalMap = new SortMap( );
		if ( FileConstants.nameFile.exists( ) )
		{
			try
			{
				BufferedReader in = new BufferedReader( new InputStreamReader( new FileInputStream( FileConstants.nameFile ),
						"UTF-16LE" ) );
				String line = null;
				while ( ( line = in.readLine( ) ) != null )
				{
					Pattern pattern = Pattern.compile( "^\\s*(\\{\\s*)(.+)(\\s*\\})(\\s*)" );
					Matcher matcher = pattern.matcher( line );
					if ( matcher.find( ) )
					{
						String general = matcher.group( )
								.replaceAll( "(\\{)", "" )
								.replaceAll( "(\\})", "" )
								.trim( );
						if ( generalList.contains( general ) )
						{
							generalMap.put( general,
									line.substring( matcher.end( ) )
											.split( ";" )[0] );
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
		return generalMap;
	}

	private static SortMap initGeneralOrderMap( )
	{
		factionGeneralMap = new SortMap( );
		factionLeaderMap = new SortMap( );
		factionHeirMap = new SortMap( );
		generalUnitTypeMap = new SortMap( );
		generalModelMap = new SortMap( );
		if ( FileConstants.stratFile.exists( ) )
		{
			try
			{
				String line = null;
				BufferedReader in = new BufferedReader( new InputStreamReader( new FileInputStream( FileConstants.stratFile ),
						"GBK" ) );
				String faction = null;
				String general = null;
				while ( ( line = in.readLine( ) ) != null )
				{
					if ( faction == null )
					{
						Pattern pattern = Pattern.compile( "^\\s*(faction)(\\s+)(\\S+)(\\s*)(,)" );
						Matcher matcher = pattern.matcher( line );
						if ( matcher.find( ) )
						{
							faction = matcher.group( )
									.replaceAll( "faction", "" )
									.replaceAll( ",", "" )
									.trim( )
									.toUpperCase( );
							factionGeneralMap.put( faction, new ArrayList( ) );
						}
					}
					else
					{
						Pattern pattern = Pattern.compile( "^\\s*(relative)(\\s+)(\\S+)(\\s*)(,)" );
						Matcher matcher = pattern.matcher( line );
						if ( matcher.find( ) )
						{
							faction = null;
						}
						else
						{
							Pattern pattern2 = Pattern.compile( "^\\s*(character)(.+)(named)(\\s+)(character)" );
							Matcher matcher2 = pattern2.matcher( line );
							if ( matcher2.find( ) )
							{
								Pattern pattern1 = Pattern.compile( "(named)(\\s+)(character)(\\s*)(,)" );
								Matcher matcher1 = pattern1.matcher( line );
								if ( matcher1.find( ) )
								{
									if ( matcher2.group( )
											.indexOf( "sub_faction" ) > -1 )
									{
										general = matcher2.group( )
												.replaceAll( "character", "" )
												.split( "," )[1].trim( ).trim( );
									}
									else
									{
										general = matcher2.group( )
												.replaceAll( "character", "" )
												.split( "," )[0].trim( );
									}
									( (List) factionGeneralMap.get( faction ) ).add( general );

									General model = new General( );
									model.setFaction( faction );

									String[] splits = line.split( ",\\s*" );
									for ( int i = 0; i < splits.length; i++ )
									{
										String[] args = splits[i].split( "\\s+" );
										if ( args[0].equalsIgnoreCase( "leader" ) )
										{
											model.setLeader( true );
											factionLeaderMap.put( faction,
													general );
										}
										else if ( args[0].equalsIgnoreCase( "heir" ) )
										{
											model.setHeir( true );
											factionHeirMap.put( faction,
													general );
										}
										else if ( args.length == 2 )
										{
											if ( args[0].equalsIgnoreCase( "portrait" ) )
												model.setPortrait( args[1] );
											if ( args[0].equalsIgnoreCase( "strat_model" ) )
												model.setStrat_model( args[1] );
											if ( args[0].equalsIgnoreCase( "battle_model" ) )
												model.setBattle_model( args[1] );
											if ( args[0].equalsIgnoreCase( "x" ) )
												model.setPosX( args[1] );
											if ( args[0].equalsIgnoreCase( "y" ) )
												model.setPosY( args[1] );
											if ( args[0].equalsIgnoreCase( "age" ) )
												model.setAge( args[1] );
										}
									}

									generalModelMap.put( general, model );
									continue;
								}
							}

							if ( general != null )
							{
								pattern = Pattern.compile( "^\\s*(unit)(.+)(exp)" );
								matcher = pattern.matcher( line );
								if ( matcher.find( ) )
								{
									generalUnitTypeMap.put( general,
											matcher.group( )
													.replaceAll( "unit", "" )
													.replaceAll( "exp", "" )
													.trim( ) );
									general = null;
									continue;
								}
							}
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

		SortMap map = new SortMap( );
		int number = 0;
		for ( int i = 0; i < factionGeneralMap.getValueList( ).size( ); i++ )
		{
			List list = (List) factionGeneralMap.getValueList( ).get( i );
			if ( list != null )
			{
				for ( int j = 0; j < list.size( ); j++ )
				{
					number++;
					map.put( Integer.valueOf( "" + number ), list.get( j ) );
				}
			}
		}
		return map;
	}

	private static SortMap initAvailableGeneralNameMap( )
	{
		List availableGeneralList = new ArrayList( );
		availableGeneralList.addAll( generalNameMap.getKeyList( ) );
		UnitUtil.getAvailableGeneralCodes( availableGeneralList );
		Collections.sort( availableGeneralList, new Comparator( ) {

			public int compare( Object o1, Object o2 )
			{
				String name1 = (String) generalNameMap.get( o1 );
				String name2 = (String) generalNameMap.get( o2 );
				return PinyinComparator.compare( name1, name2 );
			}
		} );

		SortMap availableGeneralMap = new SortMap( );
		for ( int i = 0; i < availableGeneralList.size( ); i++ )
		{
			Object key = availableGeneralList.get( i );
			String generalName = ChangeCode.toLong( (String) generalNameMap.get( key ) );
			if ( availableGeneralMap.containsValue( generalName ) )
			{
				for ( int j = 0; j < availableGeneralMap.getKeyList( ).size( ); j++ )
				{
					String general = (String) availableGeneralMap.getKeyList( )
							.get( j );
					if ( availableGeneralMap.get( j ).equals( generalName ) )
					{
						String faction = UnitUtil.getGeneralFaction( general );
						availableGeneralMap.put( general,
								ChangeCode.toLong( generalName
										+ "（"
										+ factionMap.get( faction.toUpperCase( ) )
										+ "）" ) );
					}
				}
				String faction = UnitUtil.getGeneralFaction( (String) key );
				availableGeneralMap.put( key,
						ChangeCode.toLong( generalName
								+ "（"
								+ factionMap.get( faction.toUpperCase( ) )
								+ "）" ) );
			}
			else
			{
				availableGeneralMap.put( key,
						ChangeCode.toLong( (String) generalNameMap.get( key ) ) );
			}
		}
		return availableGeneralMap;
	}

	private static SortMap initFactionMap( )
	{
		final SortMap factionMap = new SortMap( );
		Iterator iter = factionProperty.keySet( ).iterator( );
		while ( iter.hasNext( ) )
		{
			Object key = iter.next( );
			factionMap.put( factionProperty.get( key ), key );
		}
		Collections.sort( factionMap.getKeyList( ), new Comparator( ) {

			public int compare( Object o1, Object o2 )
			{
				String name1 = (String) factionMap.get( o1 );
				String name2 = (String) factionMap.get( o2 );
				return PinyinComparator.compare( name1, name2 );
			}
		} );
		return factionMap;
	}

	private static void initGeneralUnitMaps( )
	{
		generalUnitMap = new SortMap( );
		soldierUnitMap = new SortMap( );
		availableGeneralUnitMap = new SortMap( );
		List renameSoldierNameList = new ArrayList( );
		Collection generalUnitNames = generalNameMap.getValueList( );
		for ( int i = 0; i < unitMap.size( ); i++ )
		{
			String unit = (String) unitMap.getKeyList( ).get( i );
			if ( generalUnitNames.contains( unitMap.get( unit ) ) )
			{
				availableGeneralUnitMap.put( unit, unitMap.get( unit ) );
			}
			if ( generalUnitList.contains( unit ) )
			{
				generalUnitMap.put( unit, unitMap.get( unit ) );
			}
			else if ( soldierUnitList.contains( unit ) )
			{
				if ( soldierUnitMap.getValueList( )
						.contains( unitMap.get( unit ) ) )
					renameSoldierNameList.add( unitMap.get( unit ) );
				soldierUnitMap.put( unit, unitMap.get( unit ) );
			}
		}
		Collections.sort( generalUnitMap.getKeyList( ), new Comparator( ) {

			public int compare( Object o1, Object o2 )
			{
				String name1 = (String) generalUnitMap.get( o1 );
				String name2 = (String) generalUnitMap.get( o2 );

				if ( isNormalGeneral( name1 ) && !isNormalGeneral( name2 ) )
					return 1;
				if ( isNormalGeneral( name2 ) && !isNormalGeneral( name1 ) )
					return -1;
				return PinyinComparator.compare( name1, name2 );
			}
		} );
		Collections.sort( availableGeneralUnitMap.getKeyList( ),
				new Comparator( ) {

					public int compare( Object o1, Object o2 )
					{
						String name1 = (String) generalUnitMap.get( o1 );
						String name2 = (String) generalUnitMap.get( o2 );

						if ( isNormalGeneral( name1 )
								&& !isNormalGeneral( name2 ) )
							return 1;
						if ( isNormalGeneral( name2 )
								&& !isNormalGeneral( name1 ) )
							return -1;
						return PinyinComparator.compare( name1, name2 );
					}
				} );
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
						String factionName = (String) factionMap.get( list.get( 0 ) );
						if ( list.get( 0 ).toString( ).equalsIgnoreCase( "all" ) )
							soldierUnitMap.put( type, name );
						else
							soldierUnitMap.put( type, name
									+ "（"
									+ factionName
									+ "）" );
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

	public static void initAvailableOfficerAndHorses( )
	{
		List modelList = new ArrayList( );
		if ( FileConstants.battleFile.exists( ) )
		{
			try
			{
				String line = null;
				BufferedReader in = new BufferedReader( new InputStreamReader( new FileInputStream( FileConstants.battleFile ),
						"GBK" ) );
				String officer = null;
				while ( ( line = in.readLine( ) ) != null )
				{
					Pattern pattern = Pattern.compile( "^\\s*(type)(\\s+)" );
					Matcher matcher = pattern.matcher( line );
					if ( matcher.find( ) )
					{
						officer = line.split( ";" )[0].replaceAll( "type", "" )
								.trim( );
						modelList.add( officer );
						continue;
					}
				}
				in.close( );
			}
			catch ( IOException e )
			{
				e.printStackTrace( );
			}
		}

		officerMap = new SortMap( );
		SortMap properties = FileUtil.loadProperties( "officer" );
		String[] keys = (String[]) properties.keySet( ).toArray( new String[0] );
		for ( int i = 0; i < keys.length; i++ )
		{
			if ( modelList.contains( keys[i] ) )
			{
				officerMap.put( keys[i], properties.get( keys[i] ) );
			}
		}
		Collections.sort( officerMap.getKeyList( ), new Comparator( ) {

			public int compare( Object o1, Object o2 )
			{
				String name1 = (String) officerMap.get( o1 );
				String name2 = (String) officerMap.get( o2 );
				return PinyinComparator.compare( name1, name2 );
			}
		} );

		horseMap = new SortMap( );
		properties = FileUtil.loadProperties( "horse" );
		keys = (String[]) properties.keySet( ).toArray( new String[0] );
		for ( int i = 0; i < keys.length; i++ )
		{
			if ( modelList.contains( keys[i] ) )
			{
				horseMap.put( keys[i], properties.get( keys[i] ) );
			}
		}
		Collections.sort( horseMap.getKeyList( ), new Comparator( ) {

			public int compare( Object o1, Object o2 )
			{
				String name1 = (String) horseMap.get( o1 );
				String name2 = (String) horseMap.get( o2 );
				return PinyinComparator.compare( name1, name2 );
			}
		} );
	}

	private static void initMountTypeToModelMap( )
	{
		mountTypeToModelMap = new SortMap( );
		mountModelToTypeMap = new SortMap( );
		if ( FileConstants.desc_MountFile.exists( ) )
		{
			try
			{
				String line = null;

				BufferedReader in = new BufferedReader( new InputStreamReader( new FileInputStream( FileConstants.desc_MountFile ),
						"GBK" ) );
				boolean startMount = false;
				String type = null;
				String model = null;
				while ( ( line = in.readLine( ) ) != null )
				{
					if ( line.split( ";" ).length == 0 )
					{
						continue;
					}
					if ( !startMount )
					{
						Pattern pattern = Pattern.compile( "^\\s*(type)(\\s+)" );
						Matcher matcher = pattern.matcher( line );
						if ( matcher.find( ) )
						{
							type = line.split( ";" )[0].replaceAll( "type", "" )
									.trim( );
							startMount = true;
						}
					}
					else
					{
						Pattern pattern = Pattern.compile( "^\\s*(model)(\\s+)" );
						Matcher matcher = pattern.matcher( line );
						if ( matcher.find( ) )
						{
							model = line.split( ";" )[0].replaceAll( "model",
									"" ).trim( );
							if ( !mountModelToTypeMap.containsKey( model ) )
							{
								mountTypeToModelMap.put( type, model );
								mountModelToTypeMap.put( model, type );
							}
							else
							{
								String type1 = type.trim( );
								String type2 = mountModelToTypeMap.get( model )
										.toString( )
										.trim( );
								if ( type1.length( ) > type2.length( )
										|| type1.toLowerCase( )
												.compareTo( type2.toLowerCase( ) ) > 0 )
								{
									mountModelToTypeMap.put( model, type );
								}
								mountTypeToModelMap.put( type, model );
							}
							startMount = false;
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

	public static boolean isNormalGeneral( String name1 )
	{
		return name1.indexOf( "重步" ) != -1
				|| name1.indexOf( "步弓" ) != -1
				|| name1.indexOf( "重騎" ) != -1
				|| name1.indexOf( "弓騎" ) != -1
				|| name1.indexOf( "將軍" ) != -1
				|| name1.indexOf( "精銳" ) != -1;
	}

}
