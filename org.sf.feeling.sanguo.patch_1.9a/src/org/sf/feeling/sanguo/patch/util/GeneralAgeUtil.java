
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.sf.feeling.swt.win32.extension.util.SortMap;

public class GeneralAgeUtil
{

	public static int[] computeGeneralAge( String generalCode )
	{
		String faction = UnitUtil.getGeneralFaction( generalCode );
		SortMap generalAgeMap = computeFactionGeneralAges( faction );
		Object[] generalRelative = computeGeneralRelative( generalCode );

		if ( generalRelative != null && generalRelative.length == 3 )
		{
			String parent = (String) generalRelative[0];
			List children = (List) generalRelative[1];
			String[] brothers = (String[]) generalRelative[2];
			int currentAge = Integer.parseInt( (String) generalAgeMap.get( generalCode ) );

			int maxAge = 99;
			if ( parent != null )
			{
				maxAge = Integer.parseInt( (String) generalAgeMap.get( parent ) ) - 12;
			}
			int minAge = 16;
			if ( children != null && !children.isEmpty( ) )
			{
				for ( int i = 0; i < children.size( ); i++ )
				{
					int age = Integer.parseInt( (String) generalAgeMap.get( (String) children.get( i ) ) ) + 12;
					if ( age > minAge )
						minAge = age;
				}
			}

			if ( brothers != null && brothers.length == 2 )
			{
				if ( brothers[0] != null )
				{
					int age = Integer.parseInt( (String) generalAgeMap.get( brothers[0] ) ) - 1;
					if ( maxAge > age )
						maxAge = age;
				}
				if ( brothers[1] != null )
				{
					int age = Integer.parseInt( (String) generalAgeMap.get( brothers[1] ) ) + 1;
					if ( minAge < age )
						minAge = age;
				}
			}

			return new int[]{
					currentAge, minAge, maxAge
			};
		}
		return null;
	}

	private static Object[] computeGeneralRelative( String generalCode )
	{
		if ( FileConstants.stratFile.exists( ) )
		{
			try
			{
				String line = null;
				BufferedReader in = new BufferedReader( new InputStreamReader( new FileInputStream( FileConstants.stratFile ),
						"GBK" ) );
				String parent = null;
				List children = new ArrayList( );
				String[] brothers = new String[2];
				while ( ( line = in.readLine( ) ) != null )
				{
					Pattern pattern = Pattern.compile( "^\\s*(relative)" );
					Matcher matcher = pattern.matcher( line );
					if ( matcher.find( ) )
					{
						String relativeLine = line.split( ";" )[0].trim( );
						if ( relativeLine.indexOf( generalCode ) > -1 )
						{
							String[] splits = relativeLine.split( "," );
							if ( splits[0].indexOf( generalCode ) > -1 )
							{
								if ( splits.length > 2 )
								{
									for ( int i = 2; i < splits.length; i++ )
									{
										if ( "end".equals( splits[i].trim( ) ) )
										{
											break;
										}
										else
										{
											children.add( splits[i].trim( ) );
										}
									}
								}
							}
							else
							{
								parent = splits[0].replaceAll( "relative", "" )
										.trim( );
								int index = -1;
								for ( int i = 2; i < splits.length; i++ )
								{
									if ( "end".equals( splits[i].trim( ) ) )
									{
										break;
									}
									else
									{
										if ( generalCode.equals( splits[i].trim( ) ) )
										{
											index = i;
										}
									}
								}
								if ( index > 2 )
									brothers[0] = splits[index - 1].trim( );
								if ( index < splits.length - 2 )
									brothers[1] = splits[index + 1].trim( );
							}
						}
					}
				}
				in.close( );
				return new Object[]{
						parent, children, brothers
				};
			}
			catch ( IOException e )
			{
				e.printStackTrace( );
			}
		}
		return null;
	}

	private static SortMap computeFactionGeneralAges( String faction )
	{
		SortMap factionGeneralAgeMap = new SortMap( );
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
						Pattern pattern = Pattern.compile( "^\\s*(faction)(\\s+)(\\S+)(\\s*)(,)" );
						Matcher matcher = pattern.matcher( line );
						if ( matcher.find( ) )
						{
							if ( faction.equalsIgnoreCase( matcher.group( )
									.replaceAll( "faction", "" )
									.replaceAll( ",", "" )
									.trim( ) ) )
							{
								startFaction = true;
								continue;
							}
						}
					}
					else
					{
						Pattern pattern = Pattern.compile( "^\\s*(faction)(\\s+)" );
						Matcher matcher = pattern.matcher( line );
						if ( matcher.find( ) )
						{
							break;
						}
						pattern = Pattern.compile( "^\\s*(character)(.+)(named)(\\s+)(character)" );
						matcher = pattern.matcher( line );
						if ( matcher.find( ) )
						{
							Pattern pattern1 = Pattern.compile( "(named)(\\s+)(character)(\\s*)(,)" );
							Matcher matcher1 = pattern1.matcher( line );
							if ( matcher1.find( ) )
							{
								String[] value = matcher.group( ).split( "," );
								String general = value[value.length - 2].replaceAll( "character",
										"" )
										.trim( );

								String[] splits = line.trim( ).split( "," );
								for ( int i = 0; i < splits.length; i++ )
								{
									String split = splits[i];
									if ( split.toLowerCase( ).indexOf( "age" ) > -1 )
									{
										factionGeneralAgeMap.put( general,
												split.toLowerCase( )
														.replaceAll( "age", "" )
														.trim( ) );
										break;
									}
								}
								continue;
							}
						}

						pattern = Pattern.compile( "^\\s*(character_record)" );
						matcher = pattern.matcher( line );
						if ( matcher.find( ) )
						{
							String[] splits = line.split( "," );
							String general = splits[0].trim( ).split( "\\s+" )[1].trim( );
							for ( int i = 0; i < splits.length; i++ )
							{
								String split = splits[i];
								if ( split.toLowerCase( ).indexOf( "age" ) > -1 )
								{
									factionGeneralAgeMap.put( general,
											split.toLowerCase( )
													.replaceAll( "age", "" )
													.trim( ) );
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
		return factionGeneralAgeMap;
	}

	public static void convertFactionAges( String[] factions )
	{
		SortMap factionAgeMap = new SortMap( );
		for ( int j = 0; j < factions.length; j++ )
		{
			String faction = factions[j];
			final SortMap generalAgeMap = computeFactionGeneralAges( faction );
			SortMap generalRelativeMap = computeGeneralRelatives( faction );

			List factionGenerals = (List) MapUtil.factionGeneralMap.get( faction );
			for ( int i = 0; i < factionGenerals.size( ); i++ )
			{
				String general = (String) factionGenerals.get( i );
				convert16YearGeneralAge( factionAgeMap,
						general,
						generalRelativeMap,
						generalAgeMap );
			}

			SortMap relativeGeneralAgeMap = new SortMap( );
			for ( int i = 0; i < factionGenerals.size( ); i++ )
			{
				String general = (String) factionGenerals.get( i );
				if ( !factionAgeMap.containsKey( general ) )
				{
					relativeGeneralAgeMap.put( general,
							generalAgeMap.get( general ) );
				}
			}

			Collections.sort( relativeGeneralAgeMap.getKeyList( ),
					new Comparator( ) {

						public int compare( Object o1, Object o2 )
						{
							return ( (String) generalAgeMap.get( o1 ) ).compareTo( ( (String) generalAgeMap.get( o2 ) ) );
						}
					} );

			List generalList = relativeGeneralAgeMap.getKeyList( );
			for ( int i = 0; i < generalList.size( ); i++ )
			{
				String general = (String) generalList.get( i );
				Object[] generalRelative = computeGeneralRelative( general,
						generalRelativeMap );
				int minAge = 16;
				if ( generalRelative != null && generalRelative.length == 2 )
				{
					String youngBrother = (String) generalRelative[1];
					if ( youngBrother != null )
					{
						if ( factionAgeMap.containsKey( youngBrother ) )
						{
							int brotherAge = Integer.parseInt( (String) factionAgeMap.get( youngBrother ) );
							if ( brotherAge + 1 > minAge )
								minAge = brotherAge + 1;
						}
						else
						{
							int brotherAge = Integer.parseInt( (String) generalAgeMap.get( youngBrother ) );
							if ( brotherAge + 1 > minAge )
								minAge = brotherAge + 1;
						}
					}
					List children = (List) generalRelative[0];
					if ( children != null && children.size( ) > 0 )
					{
						String oldChild = (String) children.get( 0 );
						if ( oldChild != null )
						{
							if ( factionAgeMap.containsKey( oldChild ) )
							{
								int childAge = Integer.parseInt( (String) factionAgeMap.get( oldChild ) );
								if ( childAge + 12 > minAge )
									minAge = childAge + 12;
							}
							else
							{
								int childAge = Integer.parseInt( (String) generalAgeMap.get( oldChild ) );
								if ( childAge + 12 > minAge )
									minAge = childAge + 12;
							}
						}
					}
				}

				int age = Integer.parseInt( (String) generalAgeMap.get( general ) );
				if ( age >= 50 && minAge < 50 )
					minAge = 50;
				factionAgeMap.put( general, "" + minAge );
			}
		}
		saveGeneralAges( factionAgeMap );
	}

	private static void saveGeneralAges( SortMap generalAgeMap )
	{
		if ( FileConstants.stratFile.exists( ) )
		{
			try
			{
				String line = null;
				BufferedReader in = new BufferedReader( new InputStreamReader( new FileInputStream( FileConstants.stratFile ),
						"GBK" ) );
				StringWriter writer = new StringWriter( );
				PrintWriter printer = new PrintWriter( writer );
				while ( ( line = in.readLine( ) ) != null )
				{
					Pattern pattern = Pattern.compile( "^\\s*(character)(.+)(named)(\\s+)(character)" );
					Matcher matcher = pattern.matcher( line );
					if ( matcher.find( ) )
					{
						Pattern pattern1 = Pattern.compile( "(named)(\\s+)(character)(\\s*)(,)" );
						Matcher matcher1 = pattern1.matcher( line );
						if ( matcher1.find( ) )
						{
							String[] value = matcher.group( ).split( "," );
							String general = value[value.length - 2].replaceAll( "character",
									"" )
									.trim( );
							if ( generalAgeMap.containsKey( general ) )
							{
								printer.println( line.replaceAll( "age\\s+\\d+",
										"age " + generalAgeMap.get( general ) ) );
							}
							else
							{
								printer.println( line );
							}
						}
					}
					else
					{
						printer.println( line );
					}
				}
				in.close( );
				PrintWriter out = new PrintWriter( new BufferedWriter( new OutputStreamWriter( new FileOutputStream( FileConstants.stratFile ),
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

	private static Object[] computeGeneralRelative( String general,
			SortMap generalRelativeMap )
	{
		Object children = null;
		Object youngBrother = null;

		if ( generalRelativeMap.containsKey( general ) )
		{
			children = generalRelativeMap.get( general );
		}

		List childrens = generalRelativeMap.getValueList( );
		for ( int i = 0; i < childrens.size( ); i++ )
		{
			List brothers = (List) childrens.get( i );
			if ( brothers.contains( general ) )
			{
				if ( brothers.indexOf( general ) < brothers.size( ) - 1 )
				{
					youngBrother = brothers.get( brothers.indexOf( general ) + 1 );
				}
				break;
			}
		}
		return new Object[]{
				children, youngBrother
		};
	}

	private static void convert16YearGeneralAge( SortMap factionAgeMap,
			String general, SortMap generalRelativeMap, SortMap generalAgeMap )
	{
		if ( !generalRelativeMap.containsKey( general ) )
		{
			List childrens = generalRelativeMap.getValueList( );
			boolean noBrother = true;
			for ( int i = 0; i < childrens.size( ); i++ )
			{
				List children = (List) childrens.get( i );
				if ( children.contains( general ) )
				{
					noBrother = false;
					if ( children.indexOf( general ) == children.size( ) - 1 )
					{
						// youngest brother
						int age = Integer.parseInt( (String) generalAgeMap.get( general ) );
						if ( age < 50 )
						{
							factionAgeMap.put( general, "16" );
						}
						else
							factionAgeMap.put( general, "50" );
					}
					break;
				}
			}
			if ( noBrother )
			{
				int age = Integer.parseInt( (String) generalAgeMap.get( general ) );
				if ( age < 50 )
				{
					factionAgeMap.put( general, "16" );
				}
				else
					factionAgeMap.put( general, "50" );
			}
		}
	}

	private static SortMap computeGeneralRelatives( String faction )
	{
		SortMap generalRelativeMap = new SortMap( );
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
						Pattern pattern = Pattern.compile( "^\\s*(faction)(\\s+)(\\S+)(\\s*)(,)" );
						Matcher matcher = pattern.matcher( line );
						if ( matcher.find( ) )
						{
							if ( faction.equalsIgnoreCase( matcher.group( )
									.replaceAll( "faction", "" )
									.replaceAll( ",", "" )
									.trim( ) ) )
							{
								startFaction = true;
								continue;
							}
						}
					}
					else
					{
						Pattern pattern = Pattern.compile( "^\\s*(faction)(\\s+)" );
						Matcher matcher = pattern.matcher( line );
						if ( matcher.find( ) )
						{
							break;
						}

						pattern = Pattern.compile( "^\\s*(relative)" );
						matcher = pattern.matcher( line );
						if ( matcher.find( ) )
						{
							String relativeLine = line.split( ";" )[0].trim( );
							String[] splits = relativeLine.split( "," );
							List children = new ArrayList( );
							if ( splits.length > 2 )
							{
								for ( int i = 2; i < splits.length; i++ )
								{
									if ( "end".equals( splits[i].trim( ) ) )
									{
										break;
									}
									else
									{
										children.add( splits[i].trim( ) );
									}
								}
							}
							String parent = splits[0].replaceAll( "relative",
									"" ).trim( );
							generalRelativeMap.put( parent, children );
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
		return generalRelativeMap;
	}

	public static void saveGeneralAge( String generalCode, String age )
	{
		if ( FileConstants.stratFile.exists( ) )
		{
			try
			{
				String line = null;
				BufferedReader in = new BufferedReader( new InputStreamReader( new FileInputStream( FileConstants.stratFile ),
						"GBK" ) );
				StringWriter writer = new StringWriter( );
				PrintWriter printer = new PrintWriter( writer );
				while ( ( line = in.readLine( ) ) != null )
				{
					Pattern pattern = Pattern.compile( "^\\s*(character)(.+)(named)(\\s+)(character)" );
					Matcher matcher = pattern.matcher( line );
					if ( matcher.find( ) )
					{
						Pattern pattern1 = Pattern.compile( "(named)(\\s+)(character)(\\s*)(,)" );
						Matcher matcher1 = pattern1.matcher( line );
						if ( matcher1.find( ) )
						{
							String[] value = matcher.group( ).split( "," );
							String general = value[value.length - 2].replaceAll( "character",
									"" )
									.trim( );
							if ( generalCode.equals( general ) )
							{
								printer.println( line.replaceAll( "age\\s+\\d+",
										"age " + age ) );
							}
							else
							{
								printer.println( line );
							}
						}
					}
					else
					{
						printer.println( line );
					}
				}
				in.close( );
				PrintWriter out = new PrintWriter( new BufferedWriter( new OutputStreamWriter( new FileOutputStream( FileConstants.stratFile ),
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
}
