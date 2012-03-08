
package org.sf.feeling.sanguo.patch.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.sf.feeling.sanguo.patch.Patch;
import org.sf.feeling.sanguo.patch.model.General;
import org.sf.feeling.sanguo.patch.model.Unit;
import org.sf.feeling.swt.win32.extension.util.SortMap;

public class UnitUtil
{

	private static final class CategoryComparator implements Comparator
	{

		public int compare( Object o1, Object o2 )
		{
			boolean o1IsGeneral = MapUtil.generalUnitList.contains( o1 );
			boolean o2IsGeneral = MapUtil.generalUnitList.contains( o2 );
			if ( o1IsGeneral && !o2IsGeneral )
				return -1;
			else if ( !o1IsGeneral && o2IsGeneral )
			{
				return 1;
			}
			String name1 = (String) MapUtil.unitMap.get( o1 );
			String name2 = (String) MapUtil.unitMap.get( o2 );
			if ( o1IsGeneral && o2IsGeneral )
			{
				if ( MapUtil.isNormalGeneral( name1 )
						&& !MapUtil.isNormalGeneral( name2 ) )
					return 1;
				else if ( MapUtil.isNormalGeneral( name2 )
						&& !MapUtil.isNormalGeneral( name1 ) )
					return -1;
			}
			return PinyinComparator.compare( name1, name2 );
		}
	}

	public static String getGeneralFaction( String general )
	{
		String faction = null;
		if ( FileConstants.stratFile.exists( ) )
		{
			try
			{
				String line = null;

				BufferedReader in = new BufferedReader( new InputStreamReader( new FileInputStream( FileConstants.stratFile ),
						"GBK" ) );
				boolean startFaction = false;
				while ( ( line = in.readLine( ) ) != null )
				{
					if ( !startFaction )
					{
						Pattern pattern = Pattern.compile( "^\\s*(faction)(\\s+)(\\S+)(\\s*)(,)",
								Pattern.CASE_INSENSITIVE );
						Matcher matcher = pattern.matcher( line );
						if ( matcher.find( ) )
						{
							startFaction = true;
							faction = matcher.group( )
									.replaceAll( "faction", "" )
									.replaceAll( ",", "" )
									.trim( );
						}
					}
					else
					{
						Pattern pattern = Pattern.compile( "^\\s*(relative)(\\s+)(\\S+)(\\s*)(,)",
								Pattern.CASE_INSENSITIVE );
						Matcher matcher = pattern.matcher( line );
						if ( matcher.find( ) )
						{
							startFaction = false;
						}
						else
						{
							Pattern pattern1 = Pattern.compile( "^\\s*(character)(\\s+)",
									Pattern.CASE_INSENSITIVE );
							Matcher matcher1 = pattern1.matcher( line );
							// 潜在存在bug.
							if ( matcher1.find( ) )
							{
								Pattern pattern2 = Pattern.compile( "\\s*"
										+ general
										+ "\\s*,", Pattern.CASE_INSENSITIVE );
								Matcher matcher2 = pattern2.matcher( line );
								if ( matcher2.find( ) )
								{
									break;
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
		return faction;
	}

	public static String getGeneralUnitType( String general )
	{
		return (String) MapUtil.generalUnitTypeMap.get( general );
	}

	public static SortMap getGenerals( )
	{
		return MapUtil.generalNameMap;
	}

	public static SortMap getGeneralMap( )
	{
		return MapUtil.generalOrderMap;
	}

	public static void saveSmallBingPai( String destFactionCode,
			String sourceFactionCode, String soldierCode ) throws IOException
	{
		String destFilePath = Patch.GAME_ROOT
				+ "\\alexander\\data\\ui\\units\\"
				+ destFactionCode
				+ "\\#"
				+ soldierCode
				+ ".tga";
		File destFile = new File( destFilePath );
		String sourceFilePath = Patch.GAME_ROOT
				+ "\\alexander\\data\\ui\\units\\"
				+ sourceFactionCode
				+ "\\#"
				+ soldierCode
				+ ".tga";
		File sourceFile = new File( sourceFilePath );
		if ( sourceFile.exists( ) && !destFile.exists( ) )
		{
			FileUtil.writeToBinarayFile( destFile,
					new FileInputStream( sourceFile ) );
		}
	}

	public static void saveBigBingPai( String destFactionCode,
			String sourceFactionCode, String soldierCode ) throws IOException
	{

		String destFilePath = Patch.GAME_ROOT
				+ "\\alexander\\data\\ui\\unit_info\\"
				+ destFactionCode
				+ "\\"
				+ soldierCode
				+ "_info.tga";
		File destFile = new File( destFilePath );
		String sourceFilePath = Patch.GAME_ROOT
				+ "\\alexander\\data\\ui\\unit_info\\"
				+ sourceFactionCode
				+ "\\"
				+ soldierCode
				+ "_info.tga";
		File sourceFile = new File( sourceFilePath );
		if ( sourceFile.exists( ) && !destFile.exists( ) )
		{
			FileUtil.writeToBinarayFile( destFile,
					new FileInputStream( sourceFile ) );
		}
	}

	public static String modifyBattleFile( String faction, String unitSoldier )
			throws IOException
	{
		String casFile = null;

		BufferedReader in = new BufferedReader( new InputStreamReader( new FileInputStream( FileConstants.battleFile ),
				"GBK" ) );
		String line = null;

		boolean startType = false;
		boolean startTexture = false;
		boolean endTexture = false;
		boolean startSprite = false;
		boolean endSprite = false;

		String lastLine = null;
		StringWriter writer = new StringWriter( );
		PrintWriter printer = new PrintWriter( writer );
		boolean finish = false;
		while ( ( line = in.readLine( ) ) != null )
		{
			if ( finish )
			{
				printer.println( line );
				continue;
			}
			if ( line.trim( ).startsWith( ";" ) )
			{
				printer.println( line );
				continue;
			}
			if ( !startType )
			{
				Pattern pattern = Pattern.compile( "^\\s*(type)(\\s+)("
						+ unitSoldier
						+ ")(\\s*)$", Pattern.CASE_INSENSITIVE );
				Matcher matcher = pattern.matcher( line.split( ";" )[0].trim( ) );
				if ( matcher.find( ) )
				{
					startType = true;
				}
				printer.println( line );
			}
			else
			{
				Pattern pattern1 = Pattern.compile( "(\\s+\\S+)(\\.cas)(,)",
						Pattern.CASE_INSENSITIVE );
				Matcher matcher1 = pattern1.matcher( line );
				if ( matcher1.find( ) )
				{
					casFile = matcher1.group( )
							.trim( )
							.replaceAll( ",", "" )
							.trim( );
				}

				if ( !startTexture
						&& line.indexOf( "texture" ) != -1
						&& !endTexture )
				{
					startTexture = true;
					printer.println( line );
					lastLine = line;

					Pattern pattern = Pattern.compile( "^\\s*(texture)(\\s+)((?i)"
							+ faction
							+ ")(\\s*)(,)(\\s*)",
							Pattern.CASE_INSENSITIVE );
					Matcher matcher = pattern.matcher( line );
					if ( matcher.find( ) )
					{
						endTexture = true;
						lastLine = null;
					}

					continue;
				}
				if ( startTexture && !endTexture )
				{
					Pattern pattern = Pattern.compile( "^\\s*(texture)(\\s+)((?i)"
							+ faction
							+ ")(\\s*)(,)(\\s*)",
							Pattern.CASE_INSENSITIVE );
					Matcher matcher = pattern.matcher( line );
					if ( matcher.find( ) )
					{
						endTexture = true;
						printer.println( line );
						lastLine = null;
						continue;
					}
					if ( line.indexOf( "texture" ) == -1 )
					{
						endTexture = true;
						if ( lastLine.indexOf( "," ) > -1 )
						{
							printer.println( "texture            "
									+ faction
									+ lastLine.substring( lastLine.indexOf( "," ) ) );
						}
						printer.println( line );
						continue;
					}
					else
					{
						printer.println( line );
						lastLine = line;
						continue;
					}
				}

				if ( !startSprite
						&& line.indexOf( "model_sprite" ) != -1
						&& !endSprite )
				{
					startSprite = true;
					printer.println( line );
					lastLine = line;

					Pattern pattern = Pattern.compile( "^\\s*(model_sprite)(\\s+)((?i)"
							+ faction
							+ ")(\\s*)(,)(\\s*)",
							Pattern.CASE_INSENSITIVE );
					Matcher matcher = pattern.matcher( line );
					if ( matcher.find( ) )
					{
						endSprite = true;
						lastLine = null;
					}

					continue;
				}
				if ( startSprite && !endSprite )
				{
					Pattern pattern = Pattern.compile( "^\\s*(model_sprite)(\\s+)((?i)"
							+ faction
							+ ")(\\s*)(,)(\\s*)",
							Pattern.CASE_INSENSITIVE );
					Matcher matcher = pattern.matcher( line );
					if ( matcher.find( ) )
					{
						endSprite = true;
						printer.println( line );
						lastLine = null;
						continue;
					}
					if ( line.indexOf( "model_sprite" ) == -1 )
					{
						endSprite = true;
						if ( lastLine.split( "," ).length >= 3 )
						{
							printer.println( "model_sprite       "
									+ faction
									+ lastLine.substring( lastLine.indexOf( "," ) ) );
						}
						printer.println( line );
						continue;
					}
					else
					{
						printer.println( line );
						lastLine = line;
						continue;
					}
				}

				if ( startType && endTexture && endSprite )
				{
					startType = false;
					startTexture = false;
					endTexture = false;
					startSprite = false;
					endSprite = false;
					finish = true;
				}
				printer.println( line );
			}
		}
		in.close( );
		PrintWriter out = new PrintWriter( new BufferedWriter( new OutputStreamWriter( new FileOutputStream( FileConstants.battleFile ),
				"GBK" ) ),
				false );
		out.print( writer.getBuffer( ) );
		out.close( );
		printer.close( );
		return casFile;
	}

	public static void modifyUnitFile( String factionCode, String soldierType )
			throws IOException
	{
		Unit unit = UnitParser.getUnit( soldierType );
		List factions = unit.getFactions( );
		if ( !factions.contains( factionCode ) )
			factions.add( factionCode );
		UnitParser.saveSoldier( unit );
	}

	public static String getUnitDictionary( String unitType )
	{
		return (String) MapUtil.unitTypeToDictionaryMap.get( unitType );
	}

	public static String getUnitSoldier( String soldierType )
	{
		Unit unit = UnitParser.getUnit( soldierType );
		if ( unit != null )
			return unit.getSoldier( )[0];
		else
			return null;
	}

	public static String[] getUnitOfficerTypes( String soldierType )
	{
		Unit unit = UnitParser.getUnit( soldierType );
		if ( unit != null )
			return (String[]) unit.getOfficers( ).toArray( new String[0] );
		else
			return new String[0];
	}

	public static String[] getFactionsByUnitType( String soldierType )
	{
		return getFactionsFromSoldierType( soldierType );
	}

	public static String[] getFactionsFromSoldierType( String soldierType )
	{
		List factions = (List) MapUtil.unitFactionMap.get( soldierType );
		if ( factions != null )
			return (String[]) factions.toArray( new String[0] );
		return new String[0];
	}

	public static void switchGeneral( String generalInCode,
			String generalOutCode )
	{
		List generalInInfo = getGeneralInfo( generalInCode );
		List generalOutInfo = getGeneralInfo( generalOutCode );

		String generalInFaction = getGeneralFaction( generalInCode );
		String generalOutFaction = getGeneralFaction( generalOutCode );

		try
		{
			BufferedReader in = new BufferedReader( new InputStreamReader( new FileInputStream( FileConstants.stratFile ),
					"GBK" ) );
			String line = null;

			StringWriter writer = new StringWriter( );
			PrintWriter printer = new PrintWriter( writer );
			boolean startGeneralIn = false;
			boolean startGeneralOut = false;
			boolean startRelative = false;
			while ( ( line = in.readLine( ) ) != null )
			{
				if ( !startGeneralIn )
				{
					Pattern pattern = Pattern.compile( "^\\s*(character)(.+)(named)(\\s+)(character)",
							Pattern.CASE_INSENSITIVE );
					Matcher matcher = pattern.matcher( line );
					if ( matcher.find( )
							&& line.split( "(?i)(character.+)("
									+ generalInCode
									+ ")(\\s*)(,)" ).length == 2 )
					{
						startGeneralIn = true;
						line = line.replaceAll( generalInCode, "General_Out" )
								.replaceAll( generalOutCode, "General_In" );

						General inModel = (General) MapUtil.generalModelMap.get( generalInCode );
						General outModel = (General) MapUtil.generalModelMap.get( generalOutCode );

						line = line.replaceAll( "(?i)(portrait)(\\s+)([a-zA-Z0-9_\\-]+)",
								"portrait " + outModel.getPortrait( ) );

						if ( inModel.getStrat_model( ) != null
								&& inModel.getBattle_model( ) != null )
						{
							if ( outModel.getStrat_model( ) == null
									|| outModel.getBattle_model( ) == null )
							{
								line = line.replaceAll( "(?i)(portrait)(.+)",
										"portrait " + outModel.getPortrait( ) );
							}
							else
							{
								line = line.replaceAll( "(?i)(strat_model)(\\s+)([a-zA-Z0-9_\\-]+)",
										"strat_model "
												+ outModel.getStrat_model( ) );
								line = line.replaceAll( "(?i)(battle_model)(\\s+)([a-zA-Z0-9_\\-]+)",
										"battle_model "
												+ outModel.getBattle_model( ) );
							}
						}
						else
						{
							if ( outModel.getStrat_model( ) != null )
							{
								line += ",";
								line += ( "strat_model " + outModel.getStrat_model( ) );
							}
							if ( outModel.getBattle_model( ) != null )
							{
								line += ",";
								line += ( "battle_model " + outModel.getBattle_model( ) );
							}
						}

						printer.println( line );
						for ( int i = 0; i < generalInInfo.size( ); i++ )
						{
							in.readLine( );
						}
						for ( int i = 0; i < generalOutInfo.size( ); i++ )
						{
							printer.println( generalOutInfo.get( i ) );
						}
						continue;
					}
				}
				else
				{
					if ( line.indexOf( "relative" ) > -1 )
					{
						startRelative = true;
						if ( line.split( "(?i)(\\s+)("
								+ generalInCode
								+ ")(\\s*)(,)" ).length == 2 )
						{
							printer.println( line.replaceAll( generalInCode,
									"General_Out" ).replaceAll( generalOutCode,
									"General_In" ) );
							continue;
						}
						else if ( line.split( "(?i)(,)(\\s*)("
								+ generalInCode
								+ ")(\\s*)(,)" ).length == 2 )
						{
							printer.println( line.replaceAll( generalInCode,
									"General_Out" ).replaceAll( generalOutCode,
									"General_In" ) );
							continue;
						}
					}
					else if ( startRelative )
					{
						startGeneralIn = false;
						startRelative = false;
					}
				}

				if ( !startGeneralOut )
				{
					Pattern pattern = Pattern.compile( "^\\s*(character)(.+)(named)(\\s+)(character)",
							Pattern.CASE_INSENSITIVE );
					Matcher matcher = pattern.matcher( line );
					if ( matcher.find( )
							&& line.split( "(?i)(character.+)("
									+ generalOutCode
									+ ")(\\s*)(,)" ).length == 2 )
					{
						startGeneralOut = true;

						line = line.replaceAll( generalInCode, "General_Out" )
								.replaceAll( generalOutCode, "General_In" );

						General inModel = (General) MapUtil.generalModelMap.get( generalInCode );
						General outModel = (General) MapUtil.generalModelMap.get( generalOutCode );

						line = line.replaceAll( "(?i)(portrait)(\\s+)([a-zA-Z0-9_\\-]+)",
								"portrait " + inModel.getPortrait( ) );

						if ( outModel.getStrat_model( ) != null
								&& outModel.getBattle_model( ) != null )
						{
							if ( inModel.getStrat_model( ) == null
									|| inModel.getBattle_model( ) == null )
							{
								line = line.replaceAll( "(?i)(portrait)(.+)",
										"portrait " + inModel.getPortrait( ) );
							}
							else
							{
								line = line.replaceAll( "(?i)(strat_model)(\\s+)([a-zA-Z0-9_\\-]+)",
										"strat_model "
												+ inModel.getStrat_model( ) );
								line = line.replaceAll( "(?i)(battle_model)(\\s+)([a-zA-Z0-9_\\-]+)",
										"battle_model "
												+ inModel.getBattle_model( ) );
							}
						}
						else
						{
							if ( inModel.getStrat_model( ) != null )
							{
								line += ", ";
								line += ( "strat_model " + inModel.getStrat_model( ) );
							}
							if ( inModel.getBattle_model( ) != null )
							{
								line += ", ";
								line += ( "battle_model " + inModel.getBattle_model( ) );
							}
						}

						printer.println( line );
						for ( int i = 0; i < generalOutInfo.size( ); i++ )
						{
							in.readLine( );
						}
						for ( int i = 0; i < generalInInfo.size( ); i++ )
						{
							printer.println( generalInInfo.get( i ) );
						}
						continue;
					}
				}
				else
				{
					if ( line.indexOf( "relative" ) > -1 )
					{
						startRelative = true;
						if ( line.split( "(?i)(\\s+)("
								+ generalOutCode
								+ ")(\\s*)(,)" ).length == 2 )
						{
							printer.println( line.replaceAll( generalInCode,
									"General_Out" ).replaceAll( generalOutCode,
									"General_In" ) );
							continue;
						}
						else if ( line.split( "(?i)(,)(\\s*)("
								+ generalOutCode
								+ ")(\\s*)(,)" ).length == 2 )
						{
							printer.println( line.replaceAll( generalInCode,
									"General_Out" ).replaceAll( generalOutCode,
									"General_In" ) );
							continue;
						}
					}
					else if ( startRelative )
					{
						startGeneralOut = false;
						startRelative = false;
					}
				}
				printer.println( line );
			}
			in.close( );
			PrintWriter out = new PrintWriter( new BufferedWriter( new OutputStreamWriter( new FileOutputStream( FileConstants.stratFile ),
					"GBK" ) ),
					false );
			out.print( writer.getBuffer( )
					.toString( )
					.replaceAll( "General_Out", generalOutCode )
					.replaceAll( "General_In", generalInCode ) );
			out.close( );
			printer.close( );

			addGeneralUnitToFaction( UnitUtil.getGeneralUnitType( generalInCode ),
					generalInFaction,
					generalOutFaction );
			addGeneralUnitToFaction( UnitUtil.getGeneralUnitType( generalOutCode ),
					generalOutFaction,
					generalInFaction );

			General model = (General) MapUtil.generalModelMap.get( generalInCode );
			if ( model.getStrat_model( ) != null )
			{
				UnitUtil.modifyBattleFile( generalOutFaction,
						model.getStrat_model( ) );
				if ( !model.getStrat_model( ).equals( model.getBattle_model( ) ) )
				{
					if ( model.getBattle_model( ) != null )
					{
						UnitUtil.modifyBattleFile( generalOutFaction,
								model.getBattle_model( ) );
					}
				}
			}

			model = (General) MapUtil.generalModelMap.get( generalOutCode );
			if ( model.getStrat_model( ) != null )
			{
				UnitUtil.modifyBattleFile( generalInFaction,
						model.getStrat_model( ) );
				if ( !model.getStrat_model( ).equals( model.getBattle_model( ) ) )
				{
					if ( model.getBattle_model( ) != null )
					{
						UnitUtil.modifyBattleFile( generalInFaction,
								model.getBattle_model( ) );
					}
				}
			}

			switchGeneralFaction( generalInCode, generalOutCode );

			// changeGeneralImage( generalInCode, generalOutCode );
		}
		catch ( IOException e )
		{
			e.printStackTrace( );
		}

	}

	// private static void changeGeneralImage( String generalInCode,
	// String generalOutCode )
	// {
	// SortMap generalMap = getGeneralMap( );
	// Iterator iter = generalMap.getKeyList( ).iterator( );
	// Object inIndex = null;
	// Object outIndex = null;
	// while ( iter.hasNext( ) )
	// {
	// Object key = iter.next( );
	// Object value = generalMap.get( key );
	// if ( generalInCode.equals( value ) )
	// inIndex = key;
	// if ( generalOutCode.equals( value ) )
	// outIndex = key;
	// if ( inIndex != null && outIndex != null )
	// break;
	// }
	// Properties properties = FileUtil.loadProperties( "origin" );
	//
	// String inImage = properties.getProperty( inIndex.toString( ) );
	// String outImage = properties.getProperty( outIndex.toString( ) );
	//
	// if ( inImage.length( ) == 1 )
	// inImage = "00" + inImage;
	// else if ( inImage.length( ) == 2 )
	// inImage = "0" + inImage;
	//
	// if ( outImage.length( ) == 1 )
	// outImage = "00" + outImage;
	// else if ( outImage.length( ) == 2 )
	// outImage = "0" + outImage;
	//
	// try
	// {
	// FileUtil.writeToBinarayFile( new File( FileConstants.cardDeadPath
	// + "\\"
	// + outImage
	// + ".tga.tmp" ),
	// new FileInputStream( FileConstants.cardDeadPath
	// + "\\"
	// + inImage
	// + ".tga" ) );
	// FileUtil.writeToBinarayFile( new File( FileConstants.cardYoungPath
	// + "\\"
	// + outImage
	// + ".tga.tmp" ),
	// new FileInputStream( FileConstants.cardYoungPath
	// + "\\"
	// + inImage
	// + ".tga" ) );
	// FileUtil.writeToBinarayFile( new File( FileConstants.cardOldPath
	// + "\\"
	// + outImage
	// + ".tga.tmp" ),
	// new FileInputStream( FileConstants.cardOldPath
	// + "\\"
	// + inImage
	// + ".tga" ) );
	// FileUtil.writeToBinarayFile( new File( FileConstants.portraitsDeadPath
	// + "\\"
	// + outImage
	// + ".tga.tmp" ),
	// new FileInputStream( FileConstants.portraitsDeadPath
	// + "\\"
	// + inImage
	// + ".tga" ) );
	// FileUtil.writeToBinarayFile( new File( FileConstants.portraitsYoungPath
	// + "\\"
	// + outImage
	// + ".tga.tmp" ),
	// new FileInputStream( FileConstants.portraitsYoungPath
	// + "\\"
	// + inImage
	// + ".tga" ) );
	// FileUtil.writeToBinarayFile( new File( FileConstants.portraitsOldPath
	// + "\\"
	// + outImage
	// + ".tga.tmp" ),
	// new FileInputStream( FileConstants.portraitsOldPath
	// + "\\"
	// + inImage
	// + ".tga" ) );
	//
	// FileUtil.writeToBinarayFile( new File( FileConstants.cardDeadPath
	// + "\\"
	// + outImage
	// + ".tga.tmp" ),
	// new FileInputStream( FileConstants.cardDeadPath
	// + "\\"
	// + inImage
	// + ".tga" ) );
	// FileUtil.writeToBinarayFile( new File( FileConstants.cardYoungPath
	// + "\\"
	// + outImage
	// + ".tga.tmp" ),
	// new FileInputStream( FileConstants.cardYoungPath
	// + "\\"
	// + inImage
	// + ".tga" ) );
	// FileUtil.writeToBinarayFile( new File( FileConstants.cardOldPath
	// + "\\"
	// + outImage
	// + ".tga.tmp" ),
	// new FileInputStream( FileConstants.cardOldPath
	// + "\\"
	// + inImage
	// + ".tga" ) );
	// FileUtil.writeToBinarayFile( new File( FileConstants.portraitsDeadPath
	// + "\\"
	// + outImage
	// + ".tga.tmp" ),
	// new FileInputStream( FileConstants.portraitsDeadPath
	// + "\\"
	// + inImage
	// + ".tga" ) );
	// FileUtil.writeToBinarayFile( new File( FileConstants.portraitsYoungPath
	// + "\\"
	// + outImage
	// + ".tga.tmp" ),
	// new FileInputStream( FileConstants.portraitsYoungPath
	// + "\\"
	// + inImage
	// + ".tga" ) );
	// FileUtil.writeToBinarayFile( new File( FileConstants.portraitsOldPath
	// + "\\"
	// + outImage
	// + ".tga.tmp" ),
	// new FileInputStream( FileConstants.portraitsOldPath
	// + "\\"
	// + inImage
	// + ".tga" ) );
	//
	// FileUtil.writeToBinarayFile( new File( FileConstants.cardDeadPath
	// + "\\"
	// + inImage
	// + ".tga" ), new FileInputStream( FileConstants.cardDeadPath
	// + "\\"
	// + outImage
	// + ".tga" ) );
	// FileUtil.writeToBinarayFile( new File( FileConstants.cardYoungPath
	// + "\\"
	// + inImage
	// + ".tga" ),
	// new FileInputStream( FileConstants.cardYoungPath
	// + "\\"
	// + outImage
	// + ".tga" ) );
	// FileUtil.writeToBinarayFile( new File( FileConstants.cardOldPath
	// + "\\"
	// + inImage
	// + ".tga" ), new FileInputStream( FileConstants.cardOldPath
	// + "\\"
	// + outImage
	// + ".tga" ) );
	// FileUtil.writeToBinarayFile( new File( FileConstants.portraitsDeadPath
	// + "\\"
	// + inImage
	// + ".tga" ),
	// new FileInputStream( FileConstants.portraitsDeadPath
	// + "\\"
	// + outImage
	// + ".tga" ) );
	// FileUtil.writeToBinarayFile( new File( FileConstants.portraitsYoungPath
	// + "\\"
	// + inImage
	// + ".tga" ),
	// new FileInputStream( FileConstants.portraitsYoungPath
	// + "\\"
	// + outImage
	// + ".tga" ) );
	// FileUtil.writeToBinarayFile( new File( FileConstants.portraitsOldPath
	// + "\\"
	// + inImage
	// + ".tga" ),
	// new FileInputStream( FileConstants.portraitsOldPath
	// + "\\"
	// + outImage
	// + ".tga" ) );
	//
	// new File( FileConstants.cardDeadPath + "\\" + outImage + ".tga" ).delete(
	// );
	// new File( FileConstants.cardDeadPath + "\\" + outImage + ".tga.tmp"
	// ).renameTo( new File( FileConstants.cardDeadPath
	// + "\\"
	// + outImage
	// + ".tga" ) );
	// new File( FileConstants.cardYoungPath + "\\" + outImage + ".tga"
	// ).delete( );
	// new File( FileConstants.cardYoungPath
	// + "\\"
	// + outImage
	// + ".tga.tmp" ).renameTo( new File( FileConstants.cardYoungPath
	// + "\\"
	// + outImage
	// + ".tga" ) );
	// new File( FileConstants.cardOldPath + "\\" + outImage + ".tga" ).delete(
	// );
	// new File( FileConstants.cardOldPath + "\\" + outImage + ".tga.tmp"
	// ).renameTo( new File( FileConstants.cardOldPath
	// + "\\"
	// + outImage
	// + ".tga" ) );
	// new File( FileConstants.portraitsDeadPath
	// + "\\"
	// + outImage
	// + ".tga" ).delete( );
	// new File( FileConstants.portraitsDeadPath
	// + "\\"
	// + outImage
	// + ".tga.tmp" ).renameTo( new File( FileConstants.portraitsDeadPath
	// + "\\"
	// + outImage
	// + ".tga" ) );
	// new File( FileConstants.portraitsYoungPath
	// + "\\"
	// + outImage
	// + ".tga" ).delete( );
	// new File( FileConstants.portraitsYoungPath
	// + "\\"
	// + outImage
	// + ".tga.tmp" ).renameTo( new File( FileConstants.portraitsYoungPath
	// + "\\"
	// + outImage
	// + ".tga" ) );
	// new File( FileConstants.portraitsOldPath + "\\" + outImage + ".tga"
	// ).delete( );
	// new File( FileConstants.portraitsOldPath
	// + "\\"
	// + outImage
	// + ".tga.tmp" ).renameTo( new File( FileConstants.portraitsOldPath
	// + "\\"
	// + outImage
	// + ".tga" ) );
	//
	// }
	// catch ( FileNotFoundException e )
	// {
	// e.printStackTrace( );
	// }
	// }

	private static void switchGeneralFaction( String generalInCode,
			String generalOutCode )
	{
		try
		{
			BufferedReader in = new BufferedReader( new InputStreamReader( new FileInputStream( FileConstants.descNamesFile ),
					"GBK" ) );
			String line = null;

			StringWriter writer = new StringWriter( );
			PrintWriter printer = new PrintWriter( writer );
			while ( ( line = in.readLine( ) ) != null )
			{
				Pattern pattern = Pattern.compile( "^(\\s*)("
						+ generalInCode
						+ ")(\\s*)$", Pattern.CASE_INSENSITIVE );
				Matcher matcher = pattern.matcher( line );
				if ( matcher.find( ) )
				{
					printer.println( line.replaceAll( generalInCode,
							"General_Out" ).replaceAll( generalOutCode,
							"General_In" ) );
					continue;
				}

				Pattern pattern1 = Pattern.compile( "^(\\s*)("
						+ generalOutCode
						+ ")(\\s*)$", Pattern.CASE_INSENSITIVE );
				Matcher matcher1 = pattern1.matcher( line );
				if ( matcher1.find( ) )
				{
					printer.println( line.replaceAll( generalInCode,
							"General_Out" ).replaceAll( generalOutCode,
							"General_In" ) );
					continue;
				}
				printer.println( line );
			}
			in.close( );
			PrintWriter out = new PrintWriter( new BufferedWriter( new OutputStreamWriter( new FileOutputStream( FileConstants.descNamesFile ),
					"GBK" ) ),
					false );
			out.print( writer.getBuffer( )
					.toString( )
					.replaceAll( "General_Out", generalOutCode )
					.replaceAll( "General_In", generalInCode ) );
			out.close( );
			printer.close( );
		}
		catch ( IOException e )
		{
			e.printStackTrace( );
		}
	}

	public static List getGeneralInfo( String generalCode )
	{
		return getGeneralInfo( generalCode, false );
	}

	public static List getGeneralInfo( String generalCode, boolean full )
	{
		List infos = new ArrayList( );
		try
		{
			String line = null;
			BufferedReader in = new BufferedReader( new InputStreamReader( new FileInputStream( FileConstants.stratFile ),
					"GBK" ) );
			boolean startGeneral = false;
			while ( ( line = in.readLine( ) ) != null )
			{
				if ( startGeneral == false )
				{
					Pattern pattern2 = Pattern.compile( "^\\s*(character)(.+)(named)(\\s+)(character)",
							Pattern.CASE_INSENSITIVE );
					Matcher matcher2 = pattern2.matcher( line );
					if ( matcher2.find( )
							&& line.split( "(?i)(character.+)("
									+ generalCode
									+ ")(\\s*)(,)" ).length == 2 )
					{
						if ( full )
							infos.add( line );
						startGeneral = true;
					}
				}
				else
				{
					if ( !full )
					{
						infos.add( line );
						Pattern pattern2 = Pattern.compile( "^\\s*(unit)(\\s+)",
								Pattern.CASE_INSENSITIVE );
						Matcher matcher2 = pattern2.matcher( line );
						if ( matcher2.find( ) )
						{
							break;
						}
					}
					else
					{
						infos.add( line );
						Pattern pattern2 = Pattern.compile( "^\\s*(unit)(\\s+)",
								Pattern.CASE_INSENSITIVE );
						Matcher matcher2 = pattern2.matcher( line );
						if ( matcher2.find( ) )
						{
							while ( true )
							{
								line = in.readLine( );
								matcher2 = pattern2.matcher( line );
								if ( matcher2.find( ) )
									infos.add( line );
								else
									break;
							}
							break;
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
		return infos;
	}

	public static void addGeneralUnitToFaction( String generalUnit,
			String sourceFaction, String destFaction )
	{
		Unit unit = UnitParser.getUnit( generalUnit );
		if ( sourceFaction != null && destFaction != null && unit != null )
		{
			try
			{
				UnitUtil.modifyUnitFile( destFaction, unit.getType( ) );
				UnitUtil.modifyBattleFile( destFaction, unit.getSoldier( )[0] );
				List officers = unit.getFactions( );
				if ( officers != null && officers.size( ) > 0 )
				{
					for ( int i = 0; i < officers.size( ); i++ )
					{
						UnitUtil.modifyBattleFile( destFaction,
								(String) officers.get( i ) );
					}
				}

				String horse = unit.getMount( );
				if ( horse != null )
				{
					horse = (String) UnitUtil.getMountTypeToModelMap( )
							.get( horse );
					if ( horse != null )
					{
						try
						{
							UnitUtil.modifyBattleFile( destFaction, horse );
						}
						catch ( IOException e )
						{
							e.printStackTrace( );
						}
					}
				}

				UnitUtil.saveBigBingPai( destFaction,
						sourceFaction,
						unit.getDictionary( ) );
				UnitUtil.saveSmallBingPai( destFaction,
						sourceFaction,
						unit.getDictionary( ) );
			}
			catch ( IOException e1 )
			{
				e1.printStackTrace( );
			}
		}
	}

	public static List getAvailableGeneralCodes( List generalCodes )
	{
		List codes = new ArrayList( );
		SortMap generalMap = getGeneralMap( );
		Iterator iter = generalMap.getKeyList( ).iterator( );
		while ( iter.hasNext( ) )
		{
			Object key = iter.next( );
			codes.add( generalMap.get( key ) );
		}
		for ( int i = 0; i < generalCodes.size( ); i++ )
		{
			Object code = generalCodes.get( i );
			if ( !codes.contains( code ) )
			{
				generalCodes.remove( i );
				i--;
			}
		}
		return generalCodes;
	}

	public static void changeGeneralSoldier( String generalCode,
			String generalUnitCode )
	{
		String oldGeneralUnit = getGeneralUnitType( generalCode );
		if ( FileConstants.stratFile.exists( ) && oldGeneralUnit != null )
		{
			try
			{
				String line = null;
				StringWriter writer = new StringWriter( );
				PrintWriter printer = new PrintWriter( writer );
				BufferedReader in = new BufferedReader( new InputStreamReader( new FileInputStream( FileConstants.stratFile ),
						"GBK" ) );
				boolean startGeneral = false;
				while ( ( line = in.readLine( ) ) != null )
				{
					if ( !startGeneral )
					{
						Pattern pattern = Pattern.compile( "^\\s*(character)(\\s+)",
								Pattern.CASE_INSENSITIVE );
						Matcher matcher = pattern.matcher( line );
						if ( matcher.find( )
								&& line.split( "(?i)(character.+)("
										+ generalCode
										+ ")(\\s*)(,)" ).length == 2 )
						{
							startGeneral = true;
						}
					}
					else
					{
						Pattern pattern = Pattern.compile( "^\\s*(unit)(.+)(exp)",
								Pattern.CASE_INSENSITIVE );
						Matcher matcher = pattern.matcher( line );
						if ( matcher.find( ) )
						{
							line = line.replaceAll( oldGeneralUnit,
									generalUnitCode );
							startGeneral = false;
						}
					}
					printer.println( line );
				}
				in.close( );

				PrintWriter out = new PrintWriter( new BufferedWriter( new OutputStreamWriter( new FileOutputStream( FileConstants.stratFile ),
						"GBK" ) ),
						false );
				out.print( writer.getBuffer( ) );
				out.close( );
			}
			catch ( IOException e )
			{
				e.printStackTrace( );
			}
			String[] soldierOwners = getFactionsByUnitType( generalUnitCode );
			if ( soldierOwners != null && soldierOwners.length > 0 )
			{
				addGeneralUnitToFaction( generalUnitCode,
						getFactionsByUnitType( generalUnitCode )[0],
						getGeneralFaction( generalCode ) );
			}
		}
	}

	public static SortMap getAvailableOfficers( )
	{
		return MapUtil.officerMap;
	}

	public static SortMap getAvailableHorses( )
	{
		return MapUtil.horseMap;
	}

	public static SortMap getGeneralUnits( )
	{
		return MapUtil.generalUnitMap;
	}

	public static SortMap getAvailableGeneralUnits( )
	{
		return MapUtil.availableGeneralUnitMap;
	}

	public static SortMap getSoldierUnits( )
	{
		return MapUtil.soldierUnitMap;
	}

	public static SortMap getAllSoldierUnits( )
	{
		return SoldierMapUtil.soldierUnitMap;
	}

	public static SortMap getUnits( )
	{
		return MapUtil.unitMap;
	}

	public static SortMap getAvailableGenerals( )
	{
		return MapUtil.availabelGeneralNameMap;
	}

	public static SortMap getGeneralModels( )
	{
		return MapUtil.generalModelMap;
	}

	public static List getNonRelativeGenerals( )
	{
		return MapUtil.nonRelativeGeneralList;
	}

	public static Set getUnAvailableGeneralPoints( )
	{
		return MapUtil.unAvailableGeneralPoints;
	}

	public static SortMap getFactionMap( )
	{
		return MapUtil.factionMap;
	}

	public static SortMap getMountTypeToModelMap( )
	{
		return MapUtil.mountTypeToModelMap;
	}

	public static SortMap getMountModelToTypeMap( )
	{
		return MapUtil.mountModelToTypeMap;
	}

	public static SortMap getGeneralModelProperties( )
	{
		return MapUtil.generalModelProperty;
	}

	public static void setUnitName( String unitType, String name )
	{
		String dictionary = (String) MapUtil.unitTypeToDictionaryMap.get( unitType );
		String oldName = (String) UnitUtil.getGeneralUnits( ).get( unitType );
		if ( oldName == null )
			oldName = (String) UnitUtil.getSoldierUnits( ).get( unitType );
		if ( oldName == null )
			return;
		String newName = ChangeCode.toShort( name );
		if ( FileConstants.exportUnitFile.exists( ) )
		{
			try
			{
				BufferedReader in = new BufferedReader( new InputStreamReader( new FileInputStream( FileConstants.exportUnitFile ),
						"UTF-16LE" ) );
				String line = null;
				StringWriter writer = new StringWriter( );
				PrintWriter printer = new PrintWriter( writer );
				boolean startName = false;
				boolean startNameDescr = false;
				boolean startNameShortDescr = false;

				Pattern namePattern = Pattern.compile( "(?i)\\{\\s*"
						+ Pattern.quote( dictionary )
						+ "\\s*\\}" );
				Pattern nameDesrcPattern = Pattern.compile( "(?i)\\{\\s*"
						+ Pattern.quote( dictionary )
						+ "_descr\\s*\\}" );
				Pattern nameShortDesrcPattern = Pattern.compile( "(?i)\\{\\s*"
						+ Pattern.quote( dictionary )
						+ "_descr_short\\s*\\}" );
				Pattern startPattern = Pattern.compile( "^\\s*\\{" );

				while ( ( line = in.readLine( ) ) != null )
				{
					if ( startPattern.matcher( line ).find( ) )
					{
						if ( line.indexOf( dictionary ) != -1 )
						{

							if ( namePattern.matcher( line ).find( ) )
							{
								startName = true;
							}
							else
							{
								startName = false;
							}
							if ( nameDesrcPattern.matcher( line ).find( ) )
							{
								startNameDescr = true;
							}
							else
							{
								startNameDescr = false;
							}
							if ( nameShortDesrcPattern.matcher( line ).find( ) )
							{
								startNameShortDescr = true;
							}
							else
							{
								startNameShortDescr = false;
							}
						}
						else
						{
							startName = false;
							startNameDescr = false;
							startNameShortDescr = false;
						}
					}
					if ( startName == true
							|| startNameDescr == true
							|| startNameShortDescr == true )
					{
						printer.println( line.replaceAll( oldName, newName ) );
					}
					else
					{
						printer.println( line );
					}
				}
				in.close( );
				PrintWriter out = new PrintWriter( new BufferedWriter( new OutputStreamWriter( new FileOutputStream( FileConstants.exportUnitFile ),
						"UTF-16LE" ) ),
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
	}

	public static void setGeneralDescriptionName( String general, String name )
	{
		String generalCode = (String) GeneralParser.getGeneralSkills( general )
				.getKeyList( )
				.get( 0 );
		String oldName = (String) MapUtil.generalNameMap.get( general );
		String newName = ChangeCode.toShort( name );
		if ( FileConstants.vnVsFile.exists( ) )
		{
			try
			{
				BufferedReader in = new BufferedReader( new InputStreamReader( new FileInputStream( FileConstants.vnVsFile ),
						"UTF-16LE" ) );
				String line = null;
				StringWriter writer = new StringWriter( );
				PrintWriter printer = new PrintWriter( writer );
				while ( ( line = in.readLine( ) ) != null )
				{
					if ( line.split( "(?i)(\\s+)(" + generalCode + ")(\\s*)(,)" ).length == 2 )
					{
						printer.println( line.replaceAll( oldName, newName ) );
					}
					else
						printer.println( line );
				}
				in.close( );
				PrintWriter out = new PrintWriter( new BufferedWriter( new OutputStreamWriter( new FileOutputStream( FileConstants.vnVsFile ),
						"UTF-16LE" ) ),
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
	}

	public static void addUnitToBuildings( String unitType, String faction,
			String[] buildings ) throws IOException
	{
		BufferedReader in = new BufferedReader( new InputStreamReader( new FileInputStream( FileConstants.buildingsFile ),
				"GBK" ) );
		String line = null;

		boolean building = false;
		boolean startBuilding = false;
		boolean startBuildingLeft = false;

		StringWriter writer = new StringWriter( );
		PrintWriter printer = new PrintWriter( writer );
		while ( ( line = in.readLine( ) ) != null )
		{
			if ( !building )
			{
				StringBuffer buildingStr = new StringBuffer( );
				buildingStr.append( "(" );
				for ( int i = 0; i < buildings.length; i++ )
				{
					buildingStr.append( "(" + buildings[i] + ")" );
					if ( i + 1 < buildings.length )
						buildingStr.append( "|" );
				}
				buildingStr.append( ")" );
				Pattern pattern = Pattern.compile( buildingStr.append( "(\\s+)(requires)(\\s+)(factions)" )
						.toString( ),
						Pattern.CASE_INSENSITIVE );
				Matcher matcher = pattern.matcher( line );
				if ( matcher.find( ) )
				{
					building = true;
					printer.println( line );
					continue;
				}
			}
			else
			{
				if ( !startBuilding && line.indexOf( "capability" ) != -1 )
				{
					startBuilding = true;
					if ( line.indexOf( "{" ) > line.indexOf( "capability" ) )
					{
						startBuildingLeft = true;
					}
					printer.println( line );
					continue;
				}
				if ( startBuilding && !startBuildingLeft )
				{
					if ( line.indexOf( "{" ) != -1 )
					{
						startBuildingLeft = true;
						printer.println( line );
						continue;
					}
				}
				if ( startBuilding && startBuildingLeft )
				{
					if ( line.indexOf( "recruit" ) == -1 )
					{
						building = false;
						startBuilding = false;
						startBuildingLeft = false;
						printer.println( "                recruit \""
								+ unitType
								+ "\"  0  requires factions { "
								+ faction
								+ ", } " );
						printer.println( line );
						continue;
					}
					else
					{
						Pattern pattern = Pattern.compile( "^\\s*(recruit)(\\s+)(\""
								+ unitType
								+ "\")(\\s+)(\\d+)(\\s+)(requires)(\\s+)(factions)(\\s*)(\\{)(\\s*)((?i)"
								+ faction
								+ ")(\\s*)(,*)(\\s*)(\\})",
								Pattern.CASE_INSENSITIVE );
						Matcher matcher = pattern.matcher( line );
						if ( matcher.find( ) )
						{
							building = false;
							startBuilding = false;
							startBuildingLeft = false;
							printer.println( line );
							continue;
						}
					}
				}
			}
			printer.println( line );
		}
		in.close( );
		PrintWriter out = new PrintWriter( new BufferedWriter( new OutputStreamWriter( new FileOutputStream( FileConstants.buildingsFile ),
				"GBK" ) ),
				false );
		out.print( writer.getBuffer( ) );
		out.close( );
		printer.close( );
	}

	public static void addBonusToBuildings( String bonusType, String bonus,
			String faction, String[] buildings )
	{
		if ( FileConstants.buildingsFile.exists( ) )
		{
			try
			{
				BufferedReader in = new BufferedReader( new InputStreamReader( new FileInputStream( FileConstants.buildingsFile ),
						"GBK" ) );
				String line = null;

				boolean building = false;
				boolean startBuilding = false;
				boolean startBuildingLeft = false;

				StringBuffer buildingStr = new StringBuffer( );
				buildingStr.append( "(" );
				for ( int i = 0; i < buildings.length; i++ )
				{
					buildingStr.append( "(" + buildings[i] + ")" );
					if ( i + 1 < buildings.length )
						buildingStr.append( "|" );
				}
				buildingStr.append( ")" );

				String buildingRegex = buildingStr.toString( )
						+ "(\\s+)(requires)(\\s+)(factions)";

				StringWriter writer = new StringWriter( );
				PrintWriter printer = new PrintWriter( writer );
				while ( ( line = in.readLine( ) ) != null )
				{
					if ( !building )
					{

						Pattern pattern = Pattern.compile( buildingRegex,
								Pattern.CASE_INSENSITIVE );
						Matcher matcher = pattern.matcher( line );
						if ( matcher.find( ) )
						{
							building = true;
							printer.println( line );
							continue;
						}
					}
					else
					{
						if ( !startBuilding
								&& line.indexOf( "capability" ) != -1 )
						{
							startBuilding = true;
							if ( line.indexOf( "{" ) > line.indexOf( "capability" ) )
							{
								startBuildingLeft = true;
							}
							printer.println( line );
							continue;
						}
						if ( startBuilding && !startBuildingLeft )
						{
							if ( line.indexOf( "{" ) != -1 )
							{
								startBuildingLeft = true;
								printer.println( line );
								continue;
							}
						}
						if ( startBuilding && startBuildingLeft )
						{
							if ( "}".equals( line.trim( ) ) )
							{
								building = false;
								startBuilding = false;
								startBuildingLeft = false;
								printer.println( "                "
										+ bonusType.replaceAll( "\\(\\\\s\\+\\)",
												" " )
										+ " "
										+ bonus
										+ " requires factions { "
										+ faction
										+ ", } " );
								printer.println( line );
								continue;
							}
							else
							{
								Pattern pattern = Pattern.compile( "^\\s*("
										+ bonusType
										+ ")(\\s+)(\\d+)(\\s+)(requires)(\\s+)(factions)(\\s*)(\\{)(\\s*)((?i)"
										+ faction
										+ ")(\\s*)(,*)(\\s*)(\\})",
										Pattern.CASE_INSENSITIVE );
								Matcher matcher = pattern.matcher( line );
								if ( matcher.find( ) )
								{
									building = false;
									startBuilding = false;
									startBuildingLeft = false;
									if ( Integer.parseInt( bonus ) > 0 )
									{
										printer.println( line.replaceAll( "(\\d+)",
												bonus ) );
									}
									continue;
								}
							}
						}
					}
					printer.println( line );
				}
				in.close( );
				PrintWriter out = new PrintWriter( new BufferedWriter( new OutputStreamWriter( new FileOutputStream( FileConstants.buildingsFile ),
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
	}

	public static void changeGeneral( String generalCode, String factionCode,
			String posX, String posY )
	{
		List generalFullInfo = getGeneralInfo( generalCode, true );
		String generalInFaction = getGeneralFaction( generalCode );
		String generalOutFaction = factionCode;
		List generals = (List) MapUtil.factionGeneralMap.get( factionCode );
		String leader = null;
		for ( int i = 0; i < generals.size( ); i++ )
		{
			if ( ( (General) MapUtil.generalModelMap.get( generals.get( i ) ) ).isLeader( ) )
			{
				leader = (String) generals.get( i );
				break;
			}
		}

		if ( leader == null )
			return;

		List leaderFullInfo = getGeneralInfo( leader, true );

		try
		{
			BufferedReader in = new BufferedReader( new InputStreamReader( new FileInputStream( FileConstants.stratFile ),
					"GBK" ) );
			String line = null;

			StringWriter writer = new StringWriter( );
			PrintWriter printer = new PrintWriter( writer );
			boolean startGeneralIn = false;
			boolean startGeneralOut = false;
			boolean startRelative = false;
			while ( ( line = in.readLine( ) ) != null )
			{
				if ( !startGeneralIn )
				{
					Pattern pattern = Pattern.compile( "^\\s*(character)(.+)(named)(\\s+)(character)",
							Pattern.CASE_INSENSITIVE );
					Matcher matcher = pattern.matcher( line );
					if ( matcher.find( )
							&& line.split( "(?i)(character.+)("
									+ generalCode
									+ ")(\\s*)(,)" ).length == 2 )
					{
						startGeneralIn = true;
						for ( int i = 0; i < generalFullInfo.size( ) - 1; i++ )
						{
							in.readLine( );
						}
						continue;
					}
				}
				else
				{
					if ( line.indexOf( "relative" ) > -1 )
					{
						startRelative = true;
						if ( line.split( "(?i)(,)(\\s*)("
								+ generalCode
								+ ")(\\s*)(,)" ).length == 2 )
						{
							printer.println( line.replaceAll( "("
									+ generalCode
									+ ")(\\s*)(,)", "" ) );
							continue;
						}
					}
					else if ( startRelative )
					{
						startGeneralIn = false;
						startRelative = false;
					}
				}

				if ( !startGeneralOut )
				{
					Pattern pattern = Pattern.compile( "^\\s*(character)(.+)(named)(\\s+)(character)",
							Pattern.CASE_INSENSITIVE );
					Matcher matcher = pattern.matcher( line );
					if ( matcher.find( )
							&& line.split( "(?i)(character.+)("
									+ leader
									+ ")(\\s*)(,)" ).length == 2 )
					{
						startGeneralOut = true;
						printer.println( line );
						for ( int i = 0; i < leaderFullInfo.size( ) - 1; i++ )
						{
							printer.println( in.readLine( ) );
						}

						printer.println( );

						for ( int i = 0; i < generalFullInfo.size( ); i++ )
						{
							String infoLine = (String) generalFullInfo.get( i );
							if ( i == 0 )
							{
								infoLine = infoLine.replaceAll( "(?i)(,)(\\s*)(x)(\\s+\\d+\\s*)(,)",
										", x " + posX + "," );
								infoLine = infoLine.replaceAll( "(?i)(,)(\\s*)(y)(\\s+\\d+\\s*)(,)",
										", y " + posY + "," );
								infoLine = infoLine.replaceAll( "sub_faction.+,\\s*"
										+ generalCode,
										generalCode );
								infoLine = infoLine.replaceAll( generalCode,
										"General_Replace" );
							}
							else
							{
								if ( infoLine.toLowerCase( )
										.startsWith( "unit" ) )
								{
									printer.println( infoLine );
									printer.println( in.readLine( ) );
									break;
								}
							}
							printer.println( infoLine );
						}

						continue;
					}
				}
				printer.println( line );
			}
			in.close( );
			PrintWriter out = new PrintWriter( new BufferedWriter( new OutputStreamWriter( new FileOutputStream( FileConstants.stratFile ),
					"GBK" ) ),
					false );
			out.print( writer.getBuffer( )
					.toString( )
					.replaceAll( "General_Replace", generalCode ) );
			out.close( );
			printer.close( );

			addGeneralUnitToFaction( UnitUtil.getGeneralUnitType( generalCode ),
					generalInFaction,
					generalOutFaction );

			General model = (General) MapUtil.generalModelMap.get( generalCode );
			if ( model.getStrat_model( ) != null )
			{
				UnitUtil.modifyBattleFile( factionCode, model.getStrat_model( ) );
				if ( !model.getStrat_model( ).equals( model.getBattle_model( ) ) )
				{
					if ( model.getBattle_model( ) != null )
					{
						UnitUtil.modifyBattleFile( factionCode,
								model.getBattle_model( ) );
					}
				}
			}

			changeGeneralFaction( generalCode, leader );

			// changeGeneralImage( generalInCode, generalOutCode );
		}
		catch ( IOException e )
		{
			e.printStackTrace( );
		}

	}

	private static void changeGeneralFaction( String generalInCode,
			String leader )
	{
		try
		{
			BufferedReader in = new BufferedReader( new InputStreamReader( new FileInputStream( FileConstants.descNamesFile ),
					"GBK" ) );
			String line = null;

			StringWriter writer = new StringWriter( );
			PrintWriter printer = new PrintWriter( writer );
			while ( ( line = in.readLine( ) ) != null )
			{
				Pattern pattern = Pattern.compile( "^(\\s*)("
						+ generalInCode
						+ ")(\\s*)$", Pattern.CASE_INSENSITIVE );
				Matcher matcher = pattern.matcher( line );
				if ( matcher.find( ) )
				{
					continue;
				}

				Pattern pattern1 = Pattern.compile( "^(\\s*)("
						+ leader
						+ ")(\\s*)$", Pattern.CASE_INSENSITIVE );
				Matcher matcher1 = pattern1.matcher( line );
				if ( matcher1.find( ) )
				{
					printer.println( line );
					printer.println( line.replaceAll( leader, generalInCode ) );
					continue;
				}
				printer.println( line );
			}
			in.close( );
			PrintWriter out = new PrintWriter( new BufferedWriter( new OutputStreamWriter( new FileOutputStream( FileConstants.descNamesFile ),
					"GBK" ) ),
					false );
			out.print( writer.getBuffer( ).toString( ) );
			out.close( );
			printer.close( );
		}
		catch ( IOException e )
		{
			e.printStackTrace( );
		}
	}

	public static void modifyStratFile( String generalModel, String casFile )
	{
		try
		{
			BufferedReader in = new BufferedReader( new InputStreamReader( new FileInputStream( FileConstants.modelStratFile ),
					"GBK" ) );
			String line = null;
			boolean flag = false;
			StringWriter writer = new StringWriter( );
			PrintWriter printer = new PrintWriter( writer );
			while ( ( line = in.readLine( ) ) != null )
			{
				Pattern pattern1 = Pattern.compile( "^(\\s*)(type)(\\s+)("
						+ generalModel
						+ ")(\\s*)$", Pattern.CASE_INSENSITIVE );
				Matcher matcher1 = pattern1.matcher( line );
				if ( matcher1.find( ) )
				{
					flag = true;
					break;
				}
				else
					printer.println( line );
			}
			in.close( );
			if ( !flag )
			{
				PrintWriter out = new PrintWriter( new BufferedWriter( new OutputStreamWriter( new FileOutputStream( FileConstants.modelStratFile ),
						"GBK" ) ),
						false );
				out.print( writer.getBuffer( ) );
				out.println( );
				out.println( "type		" + generalModel );
				out.println( "skeleton		strat_named_with_army" );
				out.println( "scale				0.7" );
				out.println( "indiv_range			40" );
				out.println( "model_flexi_m			" + casFile + ", max" );
				out.println( "shadow_model_flexi		data/models_strat/shadow_model_sword.cas, max" );

				out.close( );
			}
			printer.close( );
		}
		catch ( IOException e )
		{
			e.printStackTrace( );
		}

	}

	public static SortMap getCavalryMap( )
	{
		final SortMap map = (SortMap) MapUtil.categoryMap.get( MapUtil.CAVALRY );
		for ( int i = 0; i < map.size( ); i++ )
		{
			if ( MapUtil.unitMap.get( map.getKeyList( ).get( i ) ) == null )
			{
				map.remove( i );
				i--;
			}
		}
		Collections.sort( map.getKeyList( ), new CategoryComparator( ) );
		return map;
	}

	public static SortMap getInfantryMap( )
	{
		final SortMap map = (SortMap) MapUtil.categoryMap.get( MapUtil.INFANTRY );
		for ( int i = 0; i < map.size( ); i++ )
		{
			if ( MapUtil.unitMap.get( map.getKeyList( ).get( i ) ) == null )
			{
				map.remove( i );
				i--;
			}
		}
		Collections.sort( map.getKeyList( ), new CategoryComparator( ) );
		return map;
	}

	public static SortMap getSiegeMap( )
	{
		final SortMap map = (SortMap) MapUtil.categoryMap.get( MapUtil.SIEGE );
		for ( int i = 0; i < map.size( ); i++ )
		{
			if ( MapUtil.unitMap.get( map.getKeyList( ).get( i ) ) == null )
			{
				map.remove( i );
				i--;
			}
		}
		Collections.sort( map.getKeyList( ), new CategoryComparator( ) );
		return map;
	}

	public static SortMap getHandlerMap( )
	{
		final SortMap map = (SortMap) MapUtil.categoryMap.get( MapUtil.HANDLER );
		for ( int i = 0; i < map.size( ); i++ )
		{
			if ( MapUtil.unitMap.get( map.getKeyList( ).get( i ) ) == null )
			{
				map.remove( i );
				i--;
			}
		}
		Collections.sort( map.getKeyList( ), new CategoryComparator( ) );
		return map;
	}

	public static SortMap getFactionLeaderMap( )
	{
		return MapUtil.factionLeaderMap;
	}

	public static SortMap getFactionHeirMap( )
	{
		return MapUtil.factionHeirMap;
	}

	public static void createGeneral( String general, String faction, int posX,
			int posY, String strat_model, String battle_model, SortMap skills,
			String[] baowus, String soldierType )
	{
		List generals = (List) MapUtil.factionGeneralMap.get( faction );
		String leader = null;
		for ( int i = 0; i < generals.size( ); i++ )
		{
			if ( ( (General) MapUtil.generalModelMap.get( generals.get( i ) ) ).isLeader( ) )
			{
				leader = (String) generals.get( i );
				break;
			}
		}

		if ( leader == null )
			return;

		List leaderFullInfo = getGeneralInfo( leader, true );

		try
		{
			BufferedReader in = new BufferedReader( new InputStreamReader( new FileInputStream( FileConstants.stratFile ),
					"GBK" ) );
			String line = null;

			StringWriter writer = new StringWriter( );
			PrintWriter printer = new PrintWriter( writer );
			boolean finish = false;
			String exp = (String) skills.get( "exp" );
			skills.remove( "exp" );
			while ( ( line = in.readLine( ) ) != null )
			{
				if ( !finish )
				{
					Pattern pattern = Pattern.compile( "^\\s*(character)(.+)(named)(\\s+)(character)",
							Pattern.CASE_INSENSITIVE );
					Matcher matcher = pattern.matcher( line );
					if ( matcher.find( )
							&& line.split( "(?i)(character.+)("
									+ leader
									+ ")(\\s*)(,)" ).length == 2 )
					{
						finish = true;
						printer.println( line );
						for ( int i = 0; i < leaderFullInfo.size( ) - 1; i++ )
						{
							printer.println( in.readLine( ) );
						}

						printer.println( );

						StringBuffer infoLine = new StringBuffer( "character	" );
						infoLine.append( general );
						infoLine.append( ", named character,  age 16 , , x " )
								.append( posX );
						infoLine.append( " , y " ).append( posY );
						infoLine.append( ", portrait " ).append( general );
						if ( strat_model != null )
						{
							infoLine.append( ", strat_model " )
									.append( strat_model );
						}
						if ( battle_model != null )
						{
							infoLine.append( ", battle_model " )
									.append( battle_model );
						}
						printer.println( infoLine );

						infoLine = new StringBuffer( "traits 	" );
						for ( int i = 0; i < skills.size( ); i++ )
						{
							String skill = (String) skills.getKeyList( )
									.get( i );
							infoLine.append( skill ).append( " " );
							infoLine.append( (String) skills.get( skill ) );
							if ( i < skills.size( ) - 1 )
							{
								infoLine.append( " , " );
							}
						}
						printer.println( infoLine );

						if ( baowus != null && baowus.length > 0 )
						{
							infoLine = new StringBuffer( "ancillaries " );
							for ( int i = 0; i < baowus.length; i++ )
							{
								infoLine.append( baowus[i] );
								if ( i < baowus.length - 1 )
								{
									infoLine.append( ", " );
								}
							}
							printer.println( infoLine );
						}
						printer.println( "army" );
						printer.println( "unit		"
								+ soldierType
								+ "		exp "
								+ exp
								+ " armour 2 weapon_lvl 1" );
						printer.println( );
					}
					else
					{
						printer.println( line );
					}

					continue;
				}
				printer.println( line );
			}
			in.close( );
			PrintWriter out = new PrintWriter( new BufferedWriter( new OutputStreamWriter( new FileOutputStream( FileConstants.stratFile ),
					"GBK" ) ),
					false );
			out.print( writer.getBuffer( ).toString( ) );
			out.close( );
			printer.close( );

			if ( strat_model != null )
			{
				String casFile = UnitUtil.modifyBattleFile( faction,
						strat_model );
				if ( casFile != null )
				{
					UnitUtil.modifyStratFile( strat_model, casFile );
				}
			}
			if ( battle_model != null && !battle_model.equals( strat_model ) )
			{
				UnitUtil.modifyBattleFile( faction, battle_model );
			}
		}
		catch ( IOException e )
		{
			e.printStackTrace( );
		}

		changeGeneralFaction( general, leader );
	}

	public static List getModelInfo( String modelCode )
	{
		List infos = new ArrayList( );
		try
		{
			String line = null;
			BufferedReader in = new BufferedReader( new InputStreamReader( new FileInputStream( FileConstants.battleFile ),
					"GBK" ) );
			boolean startGeneral = false;
			boolean startTexture = false;
			boolean startSprite = false;
			while ( ( line = in.readLine( ) ) != null )
			{
				if ( startGeneral == false )
				{
					Pattern pattern2 = Pattern.compile( "^\\s*(type)(\\s+)"
							+ modelCode
							+ "\\s*$", Pattern.CASE_INSENSITIVE );
					Matcher matcher2 = pattern2.matcher( line );
					if ( matcher2.find( ) )
					{
						startGeneral = true;
					}
				}
				else
				{
					{
						Pattern pattern2 = Pattern.compile( "^\\s*(skeleton_horse)(\\s+)",
								Pattern.CASE_INSENSITIVE );
						Matcher matcher2 = pattern2.matcher( line );
						if ( matcher2.find( ) )
						{
							infos.add( line.replace( "skeleton_horse",
									"skeleton" ) );
							continue;
						}
					}
					{
						Pattern pattern2 = Pattern.compile( "^\\s*(texture)(\\s+)",
								Pattern.CASE_INSENSITIVE );
						Matcher matcher2 = pattern2.matcher( line );
						if ( matcher2.find( ) )
						{
							if ( !startTexture )
							{
								infos.add( line.replaceAll( "texture.+,",
										"texture		 		" ) );
								startTexture = true;
							}
							continue;
						}
					}
					{
						Pattern pattern2 = Pattern.compile( "^\\s*(model_sprite)(\\s+)",
								Pattern.CASE_INSENSITIVE );
						Matcher matcher2 = pattern2.matcher( line );
						if ( matcher2.find( ) )
						{
							if ( !startSprite )
							{
								infos.add( line.replaceAll( "model_sprite.+\\d+",
										"model_sprite		60.0" ) );
								startSprite = true;
							}
							continue;
						}
					}

					Pattern pattern2 = Pattern.compile( "^\\s*(type)(\\s+)",
							Pattern.CASE_INSENSITIVE );
					Matcher matcher2 = pattern2.matcher( line );
					if ( matcher2.find( ) )
					{
						break;
					}

					if ( line.trim( ).length( ) == 0 )
						continue;
					if ( line.trim( ).startsWith( ";" ) )
						continue;
					if ( line.trim( ).startsWith( "skeleton" ) )
						continue;
					infos.add( line );
				}
			}
			in.close( );
		}
		catch ( IOException e )
		{
			e.printStackTrace( );
		}
		return infos;
	}

	public static List getCustomGeneralModelList( )
	{
		return MapUtil.getCustomGeneralModelList( );
	}
}
