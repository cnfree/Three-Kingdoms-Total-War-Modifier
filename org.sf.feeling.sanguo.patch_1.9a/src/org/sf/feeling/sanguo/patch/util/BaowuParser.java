
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BaowuParser
{

	public static void saveBaowu( String baowuType, HashMap baowuAttributes )
	{
		if ( FileConstants.baowuFile.exists( ) )
		{
			try
			{
				BufferedReader in = new BufferedReader( new InputStreamReader( new FileInputStream( FileConstants.baowuFile ),
						"GBK" ) );
				String line = null;
				StringWriter writer = new StringWriter( );
				PrintWriter printer = new PrintWriter( writer );
				boolean startBaowu = false;
				boolean startEffect = false;
				while ( ( line = in.readLine( ) ) != null )
				{
					if ( !startBaowu )
					{
						Pattern pattern = Pattern.compile( "^\\s*(Ancillary)(\\s+)("
								+ baowuType
								+ ")(\\s*)" );
						Matcher matcher = pattern.matcher( line );
						if ( matcher.find( ) )
						{
							startBaowu = true;
						}
					}
					else
					{

						Pattern pattern = Pattern.compile( "^\\s*(EffectsDescription)(\\s+)" );
						Matcher matcher = pattern.matcher( line );
						if ( matcher.find( ) )
						{
							printer.println( line );
							if ( !startEffect )
							{
								startEffect = true;
								Iterator iter = baowuAttributes.keySet( )
										.iterator( );
								while ( iter.hasNext( ) )
								{
									String key = (String) iter.next( );
									String value = (String) baowuAttributes.get( key );
									if ( !"0".equals( value ) )
									{
										printer.println( "    Effect "
												+ key
												+ "  "
												+ value );
									}
								}
							}
							continue;
						}

						Pattern pattern1 = Pattern.compile( "^\\s*(Ancillary)(\\s+)" );
						Matcher matcher1 = pattern1.matcher( line );
						if ( matcher1.find( ) )
						{
							startBaowu = false;
							startEffect = false;
						}

						Pattern pattern2 = Pattern.compile( "^\\s*(Effect)(\\s+)" );
						Matcher matcher2 = pattern2.matcher( line );
						if ( matcher2.find( ) )
						{
							if ( !startEffect )
							{
								startEffect = true;
								Iterator iter = baowuAttributes.keySet( )
										.iterator( );
								while ( iter.hasNext( ) )
								{
									String key = (String) iter.next( );
									String value = (String) baowuAttributes.get( key );
									if ( !"0".equals( value ) )
									{
										printer.println( matcher.group( )
												+ key
												+ "  "
												+ value );
									}
								}
							}
							continue;
						}
					}
					printer.println( line );
				}
				in.close( );
				PrintWriter out = new PrintWriter( new BufferedWriter( new OutputStreamWriter( new FileOutputStream( FileConstants.baowuFile ),
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

	public static HashMap getBaowuEffects( String baowuType )
	{
		HashMap effectMap = new HashMap( );
		if ( FileConstants.baowuFile.exists( ) )
		{
			try
			{
				String line = null;
				BufferedReader in = new BufferedReader( new InputStreamReader( new FileInputStream( FileConstants.baowuFile ),
						"GBK" ) );
				boolean startBaowu = false;
				while ( ( line = in.readLine( ) ) != null )
				{
					if ( !startBaowu )
					{
						Pattern pattern = Pattern.compile( "^\\s*(Ancillary)(\\s+)("
								+ baowuType
								+ ")(\\s*)" );
						Matcher matcher = pattern.matcher( line );
						if ( matcher.find( ) )
						{
							startBaowu = true;
						}
					}
					else
					{
						Pattern pattern = Pattern.compile( "^\\s*(Effect)(\\s+)" );
						Matcher matcher = pattern.matcher( line );
						if ( matcher.find( ) )
						{
							String[] effect = line.replaceAll( "Effect", "" )
									.split( ";" )[0].trim( )
									.replaceAll( "\\s+", "," )
									.split( "," );
							if ( effect.length == 2 )
							{
								effectMap.put( effect[0], effect[1] );
							}
						}

						Pattern pattern1 = Pattern.compile( "^\\s*(Ancillary)(\\s+)" );
						Matcher matcher1 = pattern1.matcher( line );
						if ( matcher1.find( ) )
						{
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
		return effectMap;
	}

	public static List getBaowuExcludes( String baowuType )
	{
		List excludes = new ArrayList( );
		if ( FileConstants.baowuFile.exists( ) )
		{
			try
			{
				String line = null;
				BufferedReader in = new BufferedReader( new InputStreamReader( new FileInputStream( FileConstants.baowuFile ),
						"GBK" ) );
				boolean startBaowu = false;
				while ( ( line = in.readLine( ) ) != null )
				{
					if ( !startBaowu )
					{
						Pattern pattern = Pattern.compile( "^\\s*(Ancillary)(\\s+)("
								+ baowuType
								+ ")(\\s*)" );
						Matcher matcher = pattern.matcher( line );
						if ( matcher.find( ) )
						{
							startBaowu = true;
						}
					}
					else
					{
						Pattern pattern = Pattern.compile( "^\\s*(ExcludedAncillaries)(\\s+)" );
						Matcher matcher = pattern.matcher( line );
						if ( matcher.find( ) )
						{
							String[] effect = line.replaceAll( "ExcludedAncillaries",
									"" )
									.split( ";" )[0].trim( ).split( "," );
							excludes.addAll( Arrays.asList( effect ) );
							break;
						}
						pattern = Pattern.compile( "^\\s*(Description)(\\s*)" );
						matcher = pattern.matcher( line );
						if ( matcher.find( ) )
						{
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
		return excludes;
	}

	public static Map getBaowuInfos( )
	{
		Map map = new HashMap( );
		if ( FileConstants.stratFile.exists( ) )
		{
			try
			{
				String line = null;
				BufferedReader in = new BufferedReader( new InputStreamReader( new FileInputStream( FileConstants.stratFile ),
						"GBK" ) );
				boolean startGerenal = false;
				String general = null;
				while ( ( line = in.readLine( ) ) != null )
				{
					if ( !startGerenal )
					{
						Pattern pattern = Pattern.compile( "^\\s*(character)(.+)(named)(\\s+)(character)" );
						Matcher matcher = pattern.matcher( line );
						if ( matcher.find( ) )
						{
							startGerenal = true;
							Pattern pattern1 = Pattern.compile( "(named)(\\s+)(character)(\\s*)(,)" );
							Matcher matcher1 = pattern1.matcher( line );
							if ( matcher1.find( ) )
							{
								String[] value = matcher.group( ).split( "," );
								general = value[value.length - 2].replaceAll( "character",
										"" )
										.trim( );
							}
						}
					}
					else
					{
						Pattern pattern = Pattern.compile( "^\\s*(army)(\\s*)" );
						Matcher matcher = pattern.matcher( line );
						if ( matcher.find( ) )
						{
							startGerenal = false;
							general = null;
							continue;
						}
						Pattern pattern1 = Pattern.compile( "^\\s*(ancillaries)(\\s+)" );
						Matcher matcher1 = pattern1.matcher( line );
						if ( matcher1.find( ) )
						{
							String[] baowus = line.replaceAll( "ancillaries",
									"" ).split( ";" )[0].trim( ).split( "," );
							for ( int i = 0; i < baowus.length; i++ )
							{
								map.put( baowus[i].trim( ), general );
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
		return map;
	}

	public static String[] getGeneralBaowus( String general )
	{
		String[] baowus = new String[0];
		if ( FileConstants.stratFile.exists( ) )
		{
			try
			{
				String line = null;
				BufferedReader in = new BufferedReader( new InputStreamReader( new FileInputStream( FileConstants.stratFile ),
						"GBK" ) );
				boolean startGerenal = false;
				while ( ( line = in.readLine( ) ) != null )
				{
					if ( !startGerenal )
					{
						Pattern pattern = Pattern.compile( "^\\s*(character)(.+)(named)(\\s+)(character)" );
						Matcher matcher = pattern.matcher( line );
						if ( matcher.find( )
								&& line.split( "(?i)(character.+)("
										+ general
										+ ")(\\s*)(,)" ).length == 2 )
						{
							startGerenal = true;
						}
					}
					else
					{
						Pattern pattern1 = Pattern.compile( "^\\s*(ancillaries)(\\s+)" );
						Matcher matcher1 = pattern1.matcher( line );
						if ( matcher1.find( ) )
						{
							baowus = line.replaceAll( "ancillaries", "" )
									.split( ";" )[0].trim( ).split( "," );
							break;
						}
						Pattern pattern = Pattern.compile( "^\\s*(army)(\\s*)" );
						Matcher matcher = pattern.matcher( line );
						if ( matcher.find( ) )
						{
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
		if ( baowus.length > 0 )
		{
			for ( int i = 0; i < baowus.length; i++ )
			{
				baowus[i] = baowus[i].trim( );
			}
		}
		return baowus;
	}

	public static void zhaoyunQiaoduoModify( )
	{
		if ( FileConstants.baowuFile.exists( ) )
		{
			try
			{
				BufferedReader in = new BufferedReader( new InputStreamReader( new FileInputStream( FileConstants.baowuFile ),
						"GBK" ) );
				String line = null;
				StringWriter writer = new StringWriter( );
				PrintWriter printer = new PrintWriter( writer );
				while ( ( line = in.readLine( ) ) != null )
				{
					Pattern pattern = Pattern.compile( "^\\s*(and)(\\s+)(Trait)(\\s+)(ZhanDou1000)(\\s*)(=)(\\s*)(0)" );
					Matcher matcher = pattern.matcher( line );
					if ( matcher.find( ) )
					{
						printer.println( line.replaceAll( "=", ">" ) );
						printer.println( line.substring( 0,
								line.indexOf( "and" ) )
								+ "and Trait Lz-0610-0 >= 1" );
						continue;
					}
					printer.println( line );
				}
				in.close( );
				PrintWriter out = new PrintWriter( new BufferedWriter( new OutputStreamWriter( new FileOutputStream( FileConstants.baowuFile ),
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
