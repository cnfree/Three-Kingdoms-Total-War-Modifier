
package org.sf.feeling.sanguo.patch.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.sf.feeling.sanguo.patch.model.Unit;

public class UnitParser
{

	private static final String PATTERN_DICTIONARY = "^\\s*(dictionary)(\\s+)";
	private static final String PATTERN_CATEGORY = "^\\s*(category)(\\s+)";
	private static final String PATTERN_CLASS = "^\\s*(class)(\\s+)";
	private static final String PATTERN_SOLDIER = "^\\s*(soldier)(\\s+)";
	private static final String PATTERN_OFFICER = "^\\s*(officer)(\\s+)";
	private static final String PATTERN_MOUNT_EFFECT = "^\\s*(mount_effect)(\\s+)";
	private static final String PATTERN_ATTRIBUTES = "^\\s*(attributes)(\\s+)";
	private static final String PATTERN_HEALTH = "^\\s*(stat_health)(\\s+)";
	private static final String PATTERN_PRIMARY = "^\\s*(stat_pri)(\\s+)";
	private static final String PATTERN_PRIMARY_ATTR = "^\\s*(stat_pri_attr)(\\s+)";
	private static final String PATTERN_SECOND = "^\\s*(stat_sec)(\\s+)";
	private static final String PATTERN_SECOND_ATTR = "^\\s*(stat_sec_attr)(\\s+)";
	private static final String PATTERN_PRIMARY_ARMOUR = "^\\s*(stat_pri_armour)(\\s+)";
	private static final String PATTERN_SECOND_ARMOUR = "^\\s*(stat_sec_armour)(\\s+)";
	private static final String PATTERN_HEAT = "^\\s*(stat_heat)(\\s+)";
	private static final String PATTERN_GROUND = "^\\s*(stat_ground)(\\s+)";
	private static final String PATTERN_MENTAL = "^\\s*(stat_mental)(\\s+)";
	private static final String PATTERN_CHARGE_DIST = "^\\s*(stat_charge_dist)(\\s+)";
	private static final String PATTERN_COST = "^\\s*(stat_cost)(\\s+)";
	private static final String PATTERN_OWNER = "^\\s*(ownership)(\\s+)";
	private static final String PATTERN_MOUNT = "^\\s*(mount)(\\s+)";
	private static final String PATTERN_FORMATION = "^\\s*(formation)(\\s+)";
	private static final String PATTERN_ENGINE = "^\\s*(engine)(\\s+)";
	private static final String PATTERN_ANIMAL = "^\\s*(animal)(\\s+)";

	public static Unit getUnit( String soldierType )
	{
		Unit soldier = null;
		if ( FileConstants.unitFile.exists( ) )
		{
			try
			{
				String line = null;
				BufferedReader in = new BufferedReader( new InputStreamReader( new FileInputStream( FileConstants.unitFile ),
						"GBK" ) );
				while ( ( line = in.readLine( ) ) != null )
				{
					if ( line.trim( ).startsWith( ";" ) )
					{
						continue;
					}
					if ( soldier == null )
					{
						Pattern pattern = Pattern.compile( "^\\s*(type)(\\s+)("
								+ soldierType
								+ ")(\\s*)$" );
						if ( line.trim( ).startsWith( ";" ) )
						{
							continue;
						}
						Matcher matcher = pattern.matcher( line.split( ";" )[0].trim( ) );
						if ( matcher.find( ) )
						{
							if ( line.split( ";" )[0].replaceAll( "type", "" )
									.trim( )
									.equals( soldierType ) )
							{
								soldier = new Unit( );
								soldier.setType( soldierType );
								continue;
							}
						}
					}
					else
					{
						Pattern pattern = Pattern.compile( PATTERN_DICTIONARY );
						Matcher matcher = pattern.matcher( line );
						if ( matcher.find( ) )
						{
							getSoldierDictionary( soldier, line );
							continue;
						}

						pattern = Pattern.compile( PATTERN_CATEGORY );
						matcher = pattern.matcher( line );
						if ( matcher.find( ) )
						{
							getSoldierCategory( soldier, line );
							continue;
						}

						pattern = Pattern.compile( PATTERN_CLASS );
						matcher = pattern.matcher( line );
						if ( matcher.find( ) )
						{
							getSoldierClass( soldier, line );
							continue;
						}

						pattern = Pattern.compile( PATTERN_SOLDIER );
						matcher = pattern.matcher( line );
						if ( matcher.find( ) )
						{
							getSoldierSoldier( soldier, line );
							continue;
						}

						pattern = Pattern.compile( PATTERN_MOUNT );
						matcher = pattern.matcher( line );
						if ( matcher.find( ) )
						{
							getSoldierMount( soldier, line );
							continue;
						}

						pattern = Pattern.compile( PATTERN_ANIMAL );
						matcher = pattern.matcher( line );
						if ( matcher.find( ) )
						{
							getSoldierAnimal( soldier, line );
							continue;
						}

						pattern = Pattern.compile( PATTERN_ENGINE );
						matcher = pattern.matcher( line );
						if ( matcher.find( ) )
						{
							getSoldierEngine( soldier, line );
							continue;
						}

						pattern = Pattern.compile( PATTERN_OFFICER );
						matcher = pattern.matcher( line );
						if ( matcher.find( ) )
						{
							getSoldierOfficer( soldier, line );
							continue;
						}

						pattern = Pattern.compile( PATTERN_MOUNT_EFFECT );
						matcher = pattern.matcher( line );
						if ( matcher.find( ) )
						{
							getSoldierMountEffect( soldier, line );
							continue;
						}

						pattern = Pattern.compile( PATTERN_ATTRIBUTES );
						matcher = pattern.matcher( line );
						if ( matcher.find( ) )
						{
							getSoldierAttributes( soldier, line );
							continue;
						}

						pattern = Pattern.compile( PATTERN_FORMATION );
						matcher = pattern.matcher( line );
						if ( matcher.find( ) )
						{
							getSoldierFormation( soldier, line );
							continue;
						}

						pattern = Pattern.compile( PATTERN_HEALTH );
						matcher = pattern.matcher( line );
						if ( matcher.find( ) )
						{
							getSoldierHealth( soldier, line );
							continue;
						}

						pattern = Pattern.compile( PATTERN_PRIMARY );
						matcher = pattern.matcher( line );
						if ( matcher.find( ) )
						{
							getSoldierPrimary( soldier, line );
							continue;
						}

						pattern = Pattern.compile( PATTERN_PRIMARY_ATTR );
						matcher = pattern.matcher( line );
						if ( matcher.find( ) )
						{
							getSoldierPrimaryAttr( soldier, line );
							continue;
						}

						pattern = Pattern.compile( PATTERN_SECOND );
						matcher = pattern.matcher( line );
						if ( matcher.find( ) )
						{
							getSoldierSecond( soldier, line );
							continue;
						}

						pattern = Pattern.compile( PATTERN_SECOND_ATTR );
						matcher = pattern.matcher( line );
						if ( matcher.find( ) )
						{
							getSoldierSecondAttr( soldier, line );
							continue;
						}

						pattern = Pattern.compile( PATTERN_PRIMARY_ARMOUR );
						matcher = pattern.matcher( line );
						if ( matcher.find( ) )
						{
							getSoldierPrimaryArmour( soldier, line );
							continue;
						}

						pattern = Pattern.compile( PATTERN_SECOND_ARMOUR );
						matcher = pattern.matcher( line );
						if ( matcher.find( ) )
						{
							getSoldierSecondArmour( soldier, line );
							continue;
						}

						pattern = Pattern.compile( PATTERN_HEAT );
						matcher = pattern.matcher( line );
						if ( matcher.find( ) )
						{
							getSoldierHeat( soldier, line );
							continue;
						}

						pattern = Pattern.compile( PATTERN_GROUND );
						matcher = pattern.matcher( line );
						if ( matcher.find( ) )
						{
							getSoldierGround( soldier, line );
							continue;
						}

						pattern = Pattern.compile( PATTERN_MENTAL );
						matcher = pattern.matcher( line );
						if ( matcher.find( ) )
						{
							getSoldierMental( soldier, line );
							continue;
						}

						pattern = Pattern.compile( PATTERN_CHARGE_DIST );
						matcher = pattern.matcher( line );
						if ( matcher.find( ) )
						{
							getSoldierChargeDist( soldier, line );
							continue;
						}

						pattern = Pattern.compile( PATTERN_COST );
						matcher = pattern.matcher( line );
						if ( matcher.find( ) )
						{
							getSoldierCost( soldier, line );
							continue;
						}

						pattern = Pattern.compile( PATTERN_OWNER );
						matcher = pattern.matcher( line );
						if ( matcher.find( ) )
						{
							getSoldierFactions( soldier, line );
							break;
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
		return soldier;
	}

	private static void getSoldierFactions( Unit soldier, String line )
	{
		String factionGroup = line.trim( ).split( ";" )[0].substring( "ownership".length( ) )
				.trim( );
		String[] factions = factionGroup.split( "," );
		for ( int i = 0; i < factions.length; i++ )
		{
			soldier.getFactions( ).add( factions[i].trim( ).toLowerCase( ) );
		}
	}

	private static void getSoldierOfficer( Unit soldier, String line )
	{
		String officer = line.trim( ).split( ";" )[0].substring( "officer".length( ) )
				.trim( );
		soldier.getOfficers( ).add( officer );
	}

	private static void getSoldierCost( Unit soldier, String line )
	{
		String soldierInfo = line.trim( ).split( ";" )[0].substring( "stat_cost".length( ) )
				.trim( );
		String[] infos = soldierInfo.split( "," );
		for ( int i = 0; i < 6; i++ )
		{
			if ( infos.length > i )
				soldier.getCost( )[i] = infos[i].trim( );
		}
	}

	private static void getSoldierChargeDist( Unit soldier, String line )
	{
		soldier.setChargeDist( Integer.parseInt( line.trim( ).split( ";" )[0].substring( "stat_charge_dist".length( ) )
				.trim( ) ) );
	}

	private static void getSoldierMental( Unit soldier, String line )
	{
		String soldierInfo = line.trim( ).split( ";" )[0].substring( "stat_mental".length( ) )
				.trim( );
		String[] infos = soldierInfo.split( "," );
		for ( int i = 0; i < 3; i++ )
		{
			if ( infos.length > i )
				soldier.getMental( )[i] = infos[i].trim( );
		}
	}

	private static void getSoldierGround( Unit soldier, String line )
	{
		String soldierInfo = line.trim( ).split( ";" )[0].substring( "stat_ground".length( ) )
				.trim( );
		String[] infos = soldierInfo.split( "," );
		for ( int i = 0; i < 4; i++ )
		{
			if ( infos.length > i )
				soldier.getGround( )[i] = Integer.parseInt( infos[i].trim( ) );
		}
	}

	private static void getSoldierHeat( Unit soldier, String line )
	{
		soldier.setHeat( Integer.parseInt( line.trim( ).split( ";" )[0].substring( "stat_heat".length( ) )
				.trim( ) ) );
	}

	private static void getSoldierPrimaryArmour( Unit soldier, String line )
	{
		String soldierInfo = line.trim( ).split( ";" )[0].substring( "stat_pri_armour".length( ) )
				.trim( );
		String[] infos = soldierInfo.split( "," );
		for ( int i = 0; i < 4; i++ )
		{
			if ( infos.length > i )
				soldier.getPrimaryArmour( )[i] = infos[i].trim( );
		}
	}

	private static void getSoldierSecondArmour( Unit soldier, String line )
	{
		String soldierInfo = line.trim( ).split( ";" )[0].substring( "stat_sec_armour".length( ) )
				.trim( );
		String[] infos = soldierInfo.split( "," );
		for ( int i = 0; i < 3; i++ )
		{
			if ( infos.length > i )
				soldier.getSecondArmour( )[i] = infos[i].trim( );
		}
	}

	private static void getSoldierSecondAttr( Unit soldier, String line )
	{
		String soldierInfo = line.trim( ).split( ";" )[0].substring( "stat_sec_attr".length( ) )
				.trim( );
		String[] infos = soldierInfo.split( "," );
		if ( infos.length == 1 && "no".equals( infos[0].trim( ) ) )
			return;
		for ( int i = 0; i < infos.length; i++ )
		{
			soldier.getSecondAttr( ).add( infos[i].trim( ) );
		}
	}

	private static void getSoldierSecond( Unit soldier, String line )
	{
		String soldierInfo = line.trim( ).split( ";" )[0].substring( "stat_sec".length( ) )
				.trim( );
		String[] infos = soldierInfo.split( "," );
		for ( int i = 0; i < 11; i++ )
		{
			if ( infos.length > i )
				soldier.getSecond( )[i] = infos[i].trim( );
		}
	}

	private static void getSoldierFormation( Unit soldier, String line )
	{
		String soldierInfo = line.trim( ).split( ";" )[0].substring( "formation".length( ) )
				.trim( );
		String[] formation = soldierInfo.split( "," );
		for ( int i = 0; i < formation.length; i++ )
		{
			soldier.getFormation( ).add( formation[i].trim( ) );
		}
	}

	private static void getSoldierPrimaryAttr( Unit soldier, String line )
	{
		String soldierInfo = line.trim( ).split( ";" )[0].substring( "stat_pri_attr".length( ) )
				.trim( );
		String[] infos = soldierInfo.split( "," );
		if ( infos.length == 1 && "no".equals( infos[0].trim( ) ) )
			return;
		for ( int i = 0; i < infos.length; i++ )
		{
			soldier.getPrimaryAttr( ).add( infos[i].trim( ) );
		}
	}

	private static void getSoldierPrimary( Unit soldier, String line )
	{
		String soldierInfo = line.trim( ).split( ";" )[0].substring( "stat_pri".length( ) )
				.trim( );
		String[] infos = soldierInfo.split( "," );
		for ( int i = 0; i < 11; i++ )
		{
			if ( infos.length > i )
				soldier.getPrimary( )[i] = infos[i].trim( );
		}
	}

	private static void getSoldierHealth( Unit soldier, String line )
	{
		String soldierInfo = line.trim( ).split( ";" )[0].substring( "stat_health".length( ) )
				.trim( );
		String[] infos = soldierInfo.split( "," );
		for ( int i = 0; i < 2; i++ )
		{
			if ( infos.length > i )
				soldier.getHealth( )[i] = Integer.parseInt( infos[i].trim( ) );
		}
	}

	private static void getSoldierAttributes( Unit soldier, String line )
	{
		String soldierInfo = line.trim( ).split( ";" )[0].substring( "attributes".length( ) )
				.trim( );
		String[] infos = soldierInfo.split( "," );
		for ( int i = 0; i < infos.length; i++ )
		{
			soldier.getAttributes( ).add( infos[i].trim( ) );
		}
	}

	private static void getSoldierMountEffect( Unit soldier, String line )
	{
		String soldierInfo = line.trim( ).split( ";" )[0].substring( "mount_effect".length( ) )
				.trim( );
		String[] infos = soldierInfo.split( "," );
		for ( int i = 0; i < infos.length; i++ )
		{
			soldier.getMountEffect( ).add( infos[i].trim( ) );
		}
	}

	private static void getSoldierSoldier( Unit soldier, String line )
	{
		String soldierInfo = line.trim( ).split( ";" )[0].substring( "soldier".length( ) )
				.trim( );
		String[] infos = soldierInfo.split( "," );
		for ( int i = 0; i < 4; i++ )
		{
			if ( infos.length > i )
				soldier.getSoldier( )[i] = infos[i].trim( );
		}
	}

	private static void getSoldierMount( Unit soldier, String line )
	{
		soldier.setMount( line.trim( ).split( ";" )[0].substring( "mount".length( ) )
				.trim( ) );
	}

	private static void getSoldierAnimal( Unit soldier, String line )
	{
		soldier.setAnimal( line.trim( ).split( ";" )[0].substring( "animal".length( ) )
				.trim( ) );
	}

	private static void getSoldierEngine( Unit soldier, String line )
	{
		soldier.setEngine( line.trim( ).split( ";" )[0].substring( "engine".length( ) )
				.trim( ) );
	}

	private static void getSoldierClass( Unit soldier, String line )
	{
		soldier.setUnitClass( line.trim( ).split( ";" )[0].substring( "class".length( ) )
				.trim( ) );
	}

	private static void getSoldierCategory( Unit soldier, String line )
	{
		soldier.setCategory( line.trim( ).split( ";" )[0].substring( "category".length( ) )
				.trim( ) );
	}

	private static void getSoldierDictionary( Unit soldier, String line )
	{
		soldier.setDictionary( line.trim( ).split( ";" )[0].substring( "dictionary".length( ) )
				.trim( ) );
	}

	public static void saveSoldier( Unit soldier )
	{
		if ( FileConstants.unitFile.exists( ) )
		{
			try
			{
				String[] officers = UnitUtil.getUnitOfficerTypes( soldier.getType( ) );
				StringWriter writer = new StringWriter( );
				PrintWriter printer = new PrintWriter( writer );
				String line = null;
				BufferedReader in = new BufferedReader( new InputStreamReader( new FileInputStream( FileConstants.unitFile ),
						"GBK" ) );
				boolean startSoldier = false;
				boolean startOfficer = false;
				boolean startMount = false;
				boolean startMountEffect = false;
				boolean startAnimal = false;
				boolean startEngine = false;
				while ( ( line = in.readLine( ) ) != null )
				{
					if ( line.trim( ).startsWith( ";" ) )
					{
						printer.println( line );
						continue;
					}
					if ( !startSoldier )
					{
						Pattern pattern = Pattern.compile( "^\\s*(type)(\\s+)("
								+ soldier.getType( )
								+ ")(\\s*)$" );
						Matcher matcher = pattern.matcher( line.split( ";" )[0].trim( ) );
						if ( matcher.find( ) )
						{
							startSoldier = true;
						}
						printer.println( line );
					}
					else
					{
						Pattern pattern = Pattern.compile( PATTERN_DICTIONARY );
						Matcher matcher = pattern.matcher( line );
						if ( matcher.find( ) )
						{
							setSoldierDictionary( printer, soldier, line );
							continue;
						}

						pattern = Pattern.compile( PATTERN_CATEGORY );
						matcher = pattern.matcher( line );
						if ( matcher.find( ) )
						{
							setSoldierCategory( printer, soldier, line );
							continue;
						}

						pattern = Pattern.compile( PATTERN_CLASS );
						matcher = pattern.matcher( line );
						if ( matcher.find( ) )
						{
							setSoldierClass( printer, soldier, line );
							continue;
						}

						pattern = Pattern.compile( PATTERN_SOLDIER );
						matcher = pattern.matcher( line );
						if ( matcher.find( ) )
						{
							setSoldierSoldier( printer, soldier, line );
							if ( !startOfficer )
							{
								setSoldierOfficer( printer, soldier, officers );
								startOfficer = true;
							}
							continue;
						}

						pattern = Pattern.compile( PATTERN_OFFICER );
						matcher = pattern.matcher( line );
						if ( matcher.find( ) )
						{
							if ( !startOfficer )
							{
								setSoldierOfficer( printer, soldier, officers );
								startOfficer = true;
							}
							continue;
						}

						pattern = Pattern.compile( PATTERN_MOUNT );
						matcher = pattern.matcher( line );
						if ( matcher.find( ) )
						{
							if ( !startMount )
							{
								setSoldierMount( printer, soldier, line );
								startMount = true;
							}
							continue;
						}

						pattern = Pattern.compile( PATTERN_ENGINE );
						matcher = pattern.matcher( line );
						if ( matcher.find( ) )
						{
							if ( !startEngine )
							{
								setSoldierEngine( printer, soldier, line );
								startEngine = true;
							}
							continue;
						}

						pattern = Pattern.compile( PATTERN_ANIMAL );
						matcher = pattern.matcher( line );
						if ( matcher.find( ) )
						{
							if ( !startAnimal )
							{
								setSoldierAnimal( printer, soldier, line );
								startAnimal = true;
							}
							continue;
						}

						pattern = Pattern.compile( PATTERN_MOUNT_EFFECT );
						matcher = pattern.matcher( line );
						if ( matcher.find( ) )
						{
							if ( !startMountEffect )
							{
								setSoldierMountEffect( printer, soldier, line );
								startMountEffect = true;
							}
							continue;
						}

						pattern = Pattern.compile( PATTERN_ATTRIBUTES );
						matcher = pattern.matcher( line );
						if ( matcher.find( ) )
						{
							if ( "cavalry".equals( soldier.getCategory( ) ) )
							{
								if ( !startMount )
								{
									setSoldierMount( printer, soldier );
									startMount = true;
								}
								if ( !startMountEffect )
								{
									setSoldierMountEffect( printer, soldier );
									startMountEffect = true;
								}
							}
							else if ( "siege".equals( soldier.getCategory( ) ) )
							{
								if ( !startEngine )
								{
									setSoldierEngine( printer, soldier );
									startEngine = true;
								}
							}
							else if ( "handler".equals( soldier.getCategory( ) ) )
							{
								if ( !startAnimal )
								{
									setSoldierAnimal( printer, soldier );
									startAnimal = true;
								}
							}
							setSoldierAttributes( printer, soldier, line );
							continue;
						}

						pattern = Pattern.compile( PATTERN_FORMATION );
						matcher = pattern.matcher( line );
						if ( matcher.find( ) )
						{
							setSoldierFormation( printer, soldier, line );
							continue;
						}

						pattern = Pattern.compile( PATTERN_HEALTH );
						matcher = pattern.matcher( line );
						if ( matcher.find( ) )
						{
							setSoldierHealth( printer, soldier, line );
							continue;
						}

						pattern = Pattern.compile( PATTERN_PRIMARY );
						matcher = pattern.matcher( line );
						if ( matcher.find( ) )
						{
							setSoldierPrimary( printer, soldier, line );
							continue;
						}

						pattern = Pattern.compile( PATTERN_PRIMARY_ATTR );
						matcher = pattern.matcher( line );
						if ( matcher.find( ) )
						{
							setSoldierPrimaryAttr( printer, soldier, line );
							continue;
						}

						pattern = Pattern.compile( PATTERN_SECOND );
						matcher = pattern.matcher( line );
						if ( matcher.find( ) )
						{
							setSoldierSecond( printer, soldier, line );
							continue;
						}

						pattern = Pattern.compile( PATTERN_SECOND_ATTR );
						matcher = pattern.matcher( line );
						if ( matcher.find( ) )
						{
							setSoldierSecondAttr( printer, soldier, line );
							continue;
						}

						pattern = Pattern.compile( PATTERN_PRIMARY_ARMOUR );
						matcher = pattern.matcher( line );
						if ( matcher.find( ) )
						{
							setSoldierPrimaryArmour( printer, soldier, line );
							continue;
						}

						pattern = Pattern.compile( PATTERN_SECOND_ARMOUR );
						matcher = pattern.matcher( line );
						if ( matcher.find( ) )
						{
							setSoldierSecondArmour( printer, soldier, line );
							continue;
						}

						pattern = Pattern.compile( PATTERN_HEAT );
						matcher = pattern.matcher( line );
						if ( matcher.find( ) )
						{
							setSoldierHeat( printer, soldier );
							continue;
						}

						pattern = Pattern.compile( PATTERN_GROUND );
						matcher = pattern.matcher( line );
						if ( matcher.find( ) )
						{
							setSoldierGround( printer, soldier, line );
							continue;
						}

						pattern = Pattern.compile( PATTERN_MENTAL );
						matcher = pattern.matcher( line );
						if ( matcher.find( ) )
						{
							setSoldierMental( printer, soldier, line );
							continue;
						}

						pattern = Pattern.compile( PATTERN_CHARGE_DIST );
						matcher = pattern.matcher( line );
						if ( matcher.find( ) )
						{
							setSoldierChargeDist( printer, soldier );
							continue;
						}

						pattern = Pattern.compile( PATTERN_COST );
						matcher = pattern.matcher( line );
						if ( matcher.find( ) )
						{
							setSoldierCost( printer, soldier, line );
							continue;
						}

						pattern = Pattern.compile( PATTERN_OWNER );
						matcher = pattern.matcher( line );
						if ( matcher.find( ) )
						{
							setSoldierFaction( printer, soldier, line );
							startSoldier = false;
							startOfficer = false;
							continue;
						}
						printer.println( line );
					}
				}
				in.close( );
				PrintWriter out = new PrintWriter( new BufferedWriter( new OutputStreamWriter( new FileOutputStream( FileConstants.unitFile ),
						"GBK" ) ),
						false );
				out.print( writer.getBuffer( ) );
				out.close( );
				printer.close( );
			}
			catch ( IOException e )
			{
				e.printStackTrace( );
			}
		}

		List factions = soldier.getFactions( );
		for ( int i = 0; i < factions.size( ); i++ )
		{
			String faction = (String) factions.get( i );
			List officers = soldier.getOfficers( );
			if ( officers != null && officers.size( ) > 0 )
			{
				for ( int j = 0; j < officers.size( ); j++ )
				{
					String officer = (String) officers.get( j );
					try
					{
						UnitUtil.modifyBattleFile( faction, officer );
					}
					catch ( IOException e )
					{
						e.printStackTrace( );
					}
				}
			}
			String soldierModel = soldier.getSoldier( )[0];
			if ( soldierModel != null )
			{
				try
				{
					if ( "infantry".equals( soldier.getCategory( ) ) )
					{
						UnitUtil.modifyBattleFile( faction, soldierModel );
					}
					else if ( "cavalry".equals( soldier.getCategory( ) ) )
					{
						UnitUtil.modifyBattleFile( faction, soldierModel );
					}
				}
				catch ( IOException e )
				{
					e.printStackTrace( );
				}
			}
			String horse = soldier.getMount( );
			if ( horse != null )
			{
				horse = (String) UnitUtil.getMountTypeToModelMap( ).get( horse );
				if ( horse != null )
				{
					try
					{
						UnitUtil.modifyBattleFile( faction, horse );
					}
					catch ( IOException e )
					{
						e.printStackTrace( );
					}
				}
			}
		}
	}

	private static void setSoldierFaction( PrintWriter printer, Unit soldier,
			String line )
	{
		List factions = soldier.getFactions( );
		if ( factions != null && !factions.isEmpty( ) )
		{
			StringBuffer buffer = new StringBuffer( );
			buffer.append( "ownership        " );
			for ( int i = 0; i < factions.size( ); i++ )
			{
				buffer.append( (String) factions.get( i ) );
				if ( i != factions.size( ) - 1 )
					buffer.append( ", " );
			}
			printer.println( buffer );
		}
		else
			printer.println( line );
	}

	private static void setSoldierMount( PrintWriter printer, Unit soldier,
			String line )
	{
		if ( !"cavalry".equals( soldier.getCategory( ) ) )
			return;
		if ( soldier.getMount( ) != null )
			printer.println( "mount            " + soldier.getMount( ) );
		else
			printer.println( line );
	}

	private static void setSoldierAnimal( PrintWriter printer, Unit soldier,
			String line )
	{
		if ( !"handler".equals( soldier.getCategory( ) ) )
			return;
		if ( soldier.getAnimal( ) != null )
			printer.println( "animal           " + soldier.getAnimal( ) );
		else
			printer.println( line );
	}

	private static void setSoldierEngine( PrintWriter printer, Unit soldier,
			String line )
	{
		if ( !"siege".equals( soldier.getCategory( ) ) )
			return;
		if ( soldier.getEngine( ) != null )
			printer.println( "engine           " + soldier.getEngine( ) );
		else
			printer.println( line );
	}

	private static void setSoldierMountEffect( PrintWriter printer, Unit soldier )
	{
		if ( !"cavalry".equals( soldier.getCategory( ) ) )
			return;
		List effects = soldier.getMountEffect( );
		if ( effects != null )
		{
			if ( !effects.isEmpty( ) )
			{
				StringBuffer buffer = new StringBuffer( );
				buffer.append( "mount_effect     " );
				for ( int i = 0; i < effects.size( ); i++ )
				{
					buffer.append( (String) effects.get( i ) );
					if ( i != effects.size( ) - 1 )
						buffer.append( ", " );
				}
				printer.println( buffer );
			}
		}
	}

	private static void setSoldierMount( PrintWriter printer, Unit soldier )
	{
		String mount = soldier.getMount( );
		if ( mount != null && mount.trim( ).length( ) > 0 )
		{
			printer.println( "mount            " + mount );
		}
	}

	private static void setSoldierEngine( PrintWriter printer, Unit soldier )
	{
		String engine = soldier.getEngine( );
		if ( engine != null && engine.trim( ).length( ) > 0 )
		{
			printer.println( "engine           " + engine );
		}
	}

	private static void setSoldierAnimal( PrintWriter printer, Unit soldier )
	{
		String animal = soldier.getAnimal( );
		if ( animal != null && animal.trim( ).length( ) > 0 )
		{
			printer.println( "animal           " + animal );
		}
	}

	private static void setSoldierFormation( PrintWriter printer, Unit soldier,
			String line )
	{
		List formations = soldier.getFormation( );
		if ( formations != null && formations.size( ) > 5 )
		{
			StringBuffer buffer = new StringBuffer( );
			buffer.append( "formation        " );
			for ( int i = 0; i < formations.size( ); i++ )
			{
				buffer.append( (String) formations.get( i ) );
				if ( i != formations.size( ) - 1 )
					buffer.append( ", " );
			}
			printer.println( buffer );
		}
		else
			printer.println( line );
	}

	private static void setSoldierCost( PrintWriter printer, Unit soldier,
			String line )
	{
		String[] costs = soldier.getCost( );
		if ( costs != null && costs.length == 6 )
		{
			StringBuffer buffer = new StringBuffer( );
			buffer.append( "stat_cost        " );
			buffer.append( costs[0]
					+ ", "
					+ costs[1]
					+ ", "
					+ costs[2]
					+ ", "
					+ costs[3]
					+ ", "
					+ costs[4]
					+ ", "
					+ costs[5] );
			printer.println( buffer );
		}
		else
			printer.println( line );
	}

	private static void setSoldierChargeDist( PrintWriter printer, Unit soldier )
	{
		printer.println( "stat_charge_dist " + soldier.getChargeDist( ) );
	}

	private static void setSoldierMental( PrintWriter printer, Unit soldier,
			String line )
	{
		String[] mentals = soldier.getMental( );
		if ( mentals != null && mentals.length == 3 )
		{
			StringBuffer buffer = new StringBuffer( );
			buffer.append( "stat_mental      " );
			buffer.append( mentals[0] + ", " + mentals[1] + ", " + mentals[2] );
			printer.println( buffer );
		}
		else
			printer.println( line );
	}

	private static void setSoldierGround( PrintWriter printer, Unit soldier,
			String line )
	{
		int[] grounds = soldier.getGround( );
		if ( grounds != null && grounds.length == 4 )
		{
			StringBuffer buffer = new StringBuffer( );
			buffer.append( "stat_ground      " );
			buffer.append( grounds[0]
					+ ", "
					+ grounds[1]
					+ ", "
					+ grounds[2]
					+ ", "
					+ grounds[3] );
			printer.println( buffer );
		}
		else
			printer.println( line );
	}

	private static void setSoldierHeat( PrintWriter printer, Unit soldier )
	{
		printer.println( "stat_heat        " + soldier.getHeat( ) );
	}

	private static void setSoldierPrimaryArmour( PrintWriter printer,
			Unit soldier, String line )
	{
		String[] armours = soldier.getPrimaryArmour( );
		if ( armours != null && armours.length == 4 )
		{
			StringBuffer buffer = new StringBuffer( );
			buffer.append( "stat_pri_armour  " );
			buffer.append( armours[0]
					+ ", "
					+ armours[1]
					+ ", "
					+ armours[2]
					+ ", "
					+ armours[3] );
			printer.println( buffer );
		}
		else
			printer.println( line );
	}

	private static void setSoldierSecondArmour( PrintWriter printer,
			Unit soldier, String line )
	{
		String[] armours = soldier.getSecondArmour( );
		if ( armours != null && armours.length == 3 )
		{
			StringBuffer buffer = new StringBuffer( );
			buffer.append( "stat_sec_armour  " );
			buffer.append( armours[0] + ", " + armours[1] + ", " + armours[2] );
			printer.println( buffer );
		}
		else
			printer.println( line );
	}

	private static void setSoldierSecondAttr( PrintWriter printer,
			Unit soldier, String line )
	{
		List secondAttrs = soldier.getSecondAttr( );
		if ( secondAttrs != null )
		{
			StringBuffer buffer = new StringBuffer( );
			buffer.append( "stat_sec_attr    " );
			if ( secondAttrs.isEmpty( ) )
				buffer.append( "no" );
			else
			{
				for ( int i = 0; i < secondAttrs.size( ); i++ )
				{
					buffer.append( (String) secondAttrs.get( i ) );
					if ( i != secondAttrs.size( ) - 1 )
						buffer.append( ", " );
				}
			}
			printer.println( buffer );
		}
		else
			printer.println( line );
	}

	private static void setSoldierSecond( PrintWriter printer, Unit soldier,
			String line )
	{
		String[] seconds = soldier.getSecond( );
		if ( seconds != null && seconds.length == 11 )
		{
			StringBuffer buffer = new StringBuffer( );
			buffer.append( "stat_sec         " );
			List secondList = Arrays.asList( seconds );
			for ( int i = 0; i < secondList.size( ); i++ )
			{
				buffer.append( (String) secondList.get( i ) );
				if ( i != secondList.size( ) - 1 )
					buffer.append( ", " );
			}
			printer.println( buffer );
		}
		else
			printer.println( line );
	}

	private static void setSoldierPrimaryAttr( PrintWriter printer,
			Unit soldier, String line )
	{
		List primaryAttrs = soldier.getPrimaryAttr( );
		if ( primaryAttrs != null )
		{
			StringBuffer buffer = new StringBuffer( );
			buffer.append( "stat_pri_attr    " );
			if ( primaryAttrs.isEmpty( ) )
				buffer.append( "no" );
			else
			{
				for ( int i = 0; i < primaryAttrs.size( ); i++ )
				{
					buffer.append( (String) primaryAttrs.get( i ) );
					if ( i != primaryAttrs.size( ) - 1 )
						buffer.append( ", " );
				}
			}
			printer.println( buffer );
		}
		else
			printer.println( line );
	}

	private static void setSoldierPrimary( PrintWriter printer, Unit soldier,
			String line )
	{
		String[] primarys = soldier.getPrimary( );
		if ( primarys != null && primarys.length == 11 )
		{
			StringBuffer buffer = new StringBuffer( );
			buffer.append( "stat_pri         " );
			List primaryList = Arrays.asList( primarys );
			for ( int i = 0; i < primaryList.size( ); i++ )
			{
				buffer.append( (String) primaryList.get( i ) );
				if ( i != primaryList.size( ) - 1 )
					buffer.append( ", " );
			}
			printer.println( buffer );
		}
		else
			printer.println( line );
	}

	private static void setSoldierHealth( PrintWriter printer, Unit soldier,
			String line )
	{
		int[] healths = soldier.getHealth( );
		if ( healths != null && healths.length == 2 )
		{
			StringBuffer buffer = new StringBuffer( );
			buffer.append( "stat_health      " );
			buffer.append( healths[0] + ", " + healths[1] );
			printer.println( buffer );
		}
		else
			printer.println( line );
	}

	private static void setSoldierAttributes( PrintWriter printer,
			Unit soldier, String line )
	{
		List attributes = soldier.getAttributes( );
		if ( attributes != null && !attributes.isEmpty( ) )
		{
			StringBuffer buffer = new StringBuffer( );
			buffer.append( "attributes       " );
			for ( int i = 0; i < attributes.size( ); i++ )
			{
				buffer.append( (String) attributes.get( i ) );
				if ( i != attributes.size( ) - 1 )
					buffer.append( ", " );
			}
			printer.println( buffer );
		}
		else
			printer.println( line );
	}

	private static void setSoldierMountEffect( PrintWriter printer,
			Unit soldier, String line )
	{
		if ( !"cavalry".equals( soldier.getCategory( ) ) )
			return;
		List effects = soldier.getMountEffect( );
		if ( effects != null )
		{
			if ( !effects.isEmpty( ) )
			{
				StringBuffer buffer = new StringBuffer( );
				buffer.append( "mount_effect     " );
				for ( int i = 0; i < effects.size( ); i++ )
				{
					buffer.append( (String) effects.get( i ) );
					if ( i != effects.size( ) - 1 )
						buffer.append( ", " );
				}
				printer.println( buffer );
			}
		}
		else
			printer.println( line );
	}

	private static void setSoldierOfficer( PrintWriter printer, Unit soldier,
			String[] officers )
	{
		List officerList = soldier.getOfficers( );
		if ( officerList.size( ) >= officers.length )
		{
			for ( int i = 0; i < officerList.size( ); i++ )
			{
				printer.println( "officer          "
						+ (String) officerList.get( i ) );
			}
		}
		else
		{
			for ( int i = 0; i < officers.length; i++ )
			{
				if ( officerList.size( ) > i )
				{
					printer.println( "officer          "
							+ (String) officerList.get( i ) );
				}
				else
				{
					printer.println( "officer          " + officers[i] );
				}
			}
		}

	}

	private static void setSoldierSoldier( PrintWriter printer, Unit soldier,
			String line )
	{
		String[] soldiers = soldier.getSoldier( );
		if ( soldiers != null && soldiers.length == 4 )
		{
			StringBuffer buffer = new StringBuffer( );
			buffer.append( "soldier          " );
			List soldierList = Arrays.asList( soldiers );
			for ( int i = 0; i < soldierList.size( ); i++ )
			{
				buffer.append( (String) soldierList.get( i ) );
				if ( i != soldierList.size( ) - 1 )
					buffer.append( ", " );
			}
			printer.println( buffer );
		}
		else
			printer.println( line );
	}

	private static void setSoldierClass( PrintWriter printer, Unit soldier,
			String line )
	{
		if ( soldier.getUnitClass( ) != null )
			printer.println( "class            " + soldier.getUnitClass( ) );
		else
			printer.println( line );
	}

	private static void setSoldierCategory( PrintWriter printer, Unit soldier,
			String line )
	{
		if ( soldier.getCategory( ) != null )
			printer.println( "category         " + soldier.getCategory( ) );
		else
			printer.println( line );
	}

	private static void setSoldierDictionary( PrintWriter printer,
			Unit soldier, String line )
	{
		if ( soldier.getDictionary( ) != null )
			printer.println( "dictionary       " + soldier.getDictionary( ) );
		else
			printer.println( line );

	}

	public static void createSoldier( Unit soldier, String soldierType,
			String soldierDictionary, String generalFaction )
	{
		if ( FileConstants.unitFile.exists( ) )
		{
			try
			{
				String[] officers = UnitUtil.getUnitOfficerTypes( soldier.getType( ) );
				StringWriter writer = new StringWriter( );
				PrintWriter printer = new PrintWriter( writer );
				String line = null;
				BufferedReader in = new BufferedReader( new InputStreamReader( new FileInputStream( FileConstants.unitFile ),
						"GBK" ) );
				boolean startSoldier = false;
				boolean startOfficer = false;
				boolean startMountEffect = false;
				while ( ( line = in.readLine( ) ) != null )
				{
					if ( line.trim( ).startsWith( ";" ) )
					{
						continue;
					}
					if ( !startSoldier )
					{
						Pattern pattern = Pattern.compile( "^\\s*(type)(\\s+)("
								+ soldier.getType( )
								+ ")(\\s*)$" );
						Matcher matcher = pattern.matcher( line.split( ";" )[0].trim( ) );
						if ( matcher.find( ) )
						{
							String lineType = line.split( ";" )[0].replaceAll( "type",
									"" )
									.trim( );
							if ( lineType.equals( soldier.getType( ) ) )
							{
								startSoldier = true;
								printer.println( line.replaceAll( soldier.getType( ),
										soldierType ) );
							}
						}
					}
					else
					{
						Pattern pattern = Pattern.compile( PATTERN_DICTIONARY );
						Matcher matcher = pattern.matcher( line );
						if ( matcher.find( ) )
						{
							setSoldierDictionary( printer,
									soldierDictionary,
									line );
							continue;
						}

						pattern = Pattern.compile( PATTERN_CATEGORY );
						matcher = pattern.matcher( line );
						if ( matcher.find( ) )
						{
							setSoldierCategory( printer, soldier, line );
							continue;
						}

						pattern = Pattern.compile( PATTERN_CLASS );
						matcher = pattern.matcher( line );
						if ( matcher.find( ) )
						{
							setSoldierClass( printer, soldier, line );
							continue;
						}

						pattern = Pattern.compile( PATTERN_SOLDIER );
						matcher = pattern.matcher( line );
						if ( matcher.find( ) )
						{
							setSoldierSoldier( printer, soldier, line );
							if ( !startOfficer )
							{
								setSoldierOfficer( printer, soldier, officers );
								startOfficer = true;
							}
							continue;
						}

						pattern = Pattern.compile( PATTERN_OFFICER );
						matcher = pattern.matcher( line );
						if ( matcher.find( ) )
						{
							if ( !startOfficer )
							{
								setSoldierOfficer( printer, soldier, officers );
								startOfficer = true;
							}
							continue;
						}

						pattern = Pattern.compile( PATTERN_MOUNT_EFFECT );
						matcher = pattern.matcher( line );
						if ( matcher.find( ) )
						{
							if ( !startMountEffect )
							{
								setSoldierMountEffect( printer, soldier, line );
								startMountEffect = true;
							}
							continue;
						}

						pattern = Pattern.compile( PATTERN_ATTRIBUTES );
						matcher = pattern.matcher( line );
						if ( matcher.find( ) )
						{
							if ( !startMountEffect )
							{
								setSoldierMountEffect( printer, soldier );
								startMountEffect = true;
							}
							setSoldierAttributes( printer, soldier, line );
							continue;
						}

						pattern = Pattern.compile( PATTERN_HEALTH );
						matcher = pattern.matcher( line );
						if ( matcher.find( ) )
						{
							setSoldierHealth( printer, soldier, line );
							continue;
						}

						pattern = Pattern.compile( PATTERN_PRIMARY );
						matcher = pattern.matcher( line );
						if ( matcher.find( ) )
						{
							setSoldierPrimary( printer, soldier, line );
							continue;
						}

						pattern = Pattern.compile( PATTERN_PRIMARY_ATTR );
						matcher = pattern.matcher( line );
						if ( matcher.find( ) )
						{
							setSoldierPrimaryAttr( printer, soldier, line );
							continue;
						}

						pattern = Pattern.compile( PATTERN_SECOND );
						matcher = pattern.matcher( line );
						if ( matcher.find( ) )
						{
							setSoldierSecond( printer, soldier, line );
							continue;
						}

						pattern = Pattern.compile( PATTERN_SECOND_ATTR );
						matcher = pattern.matcher( line );
						if ( matcher.find( ) )
						{
							setSoldierSecondAttr( printer, soldier, line );
							continue;
						}

						pattern = Pattern.compile( PATTERN_PRIMARY_ARMOUR );
						matcher = pattern.matcher( line );
						if ( matcher.find( ) )
						{
							setSoldierPrimaryArmour( printer, soldier, line );
							continue;
						}

						pattern = Pattern.compile( PATTERN_HEAT );
						matcher = pattern.matcher( line );
						if ( matcher.find( ) )
						{
							setSoldierHeat( printer, soldier );
							continue;
						}

						pattern = Pattern.compile( PATTERN_GROUND );
						matcher = pattern.matcher( line );
						if ( matcher.find( ) )
						{
							setSoldierGround( printer, soldier, line );
							continue;
						}

						pattern = Pattern.compile( PATTERN_MENTAL );
						matcher = pattern.matcher( line );
						if ( matcher.find( ) )
						{
							setSoldierMental( printer, soldier, line );
							continue;
						}

						pattern = Pattern.compile( PATTERN_CHARGE_DIST );
						matcher = pattern.matcher( line );
						if ( matcher.find( ) )
						{
							setSoldierChargeDist( printer, soldier );
							continue;
						}

						pattern = Pattern.compile( PATTERN_COST );
						matcher = pattern.matcher( line );
						if ( matcher.find( ) )
						{
							setSoldierCost( printer, soldier, line );
							continue;
						}

						pattern = Pattern.compile( PATTERN_OWNER );
						matcher = pattern.matcher( line );
						if ( matcher.find( ) )
						{
							startSoldier = false;
							startOfficer = false;
							printer.println( "ownership        "
									+ generalFaction );
							break;
						}
						printer.println( line );
					}
				}
				in.close( );
				PrintWriter out = new PrintWriter( new BufferedWriter( new OutputStreamWriter( new FileOutputStream( FileConstants.unitFile,
						true ),
						"GBK" ) ),
						false );
				out.println( );
				out.print( writer.getBuffer( ) );
				out.close( );
				printer.close( );
			}
			catch ( IOException e )
			{
				e.printStackTrace( );
			}
		}

	}

	private static void setSoldierDictionary( PrintWriter printer,
			String soldierDictionary, String line )
	{
		if ( soldierDictionary != null )
			printer.println( "dictionary       " + soldierDictionary );
		else
			printer.println( line );
	}
}
