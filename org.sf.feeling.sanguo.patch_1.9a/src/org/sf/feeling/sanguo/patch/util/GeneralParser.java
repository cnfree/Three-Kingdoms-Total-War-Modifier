
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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.sf.feeling.sanguo.patch.model.General;
import org.sf.feeling.swt.win32.extension.util.SortMap;

public class GeneralParser
{

	public static void setGeneralSkills( String general, SortMap generalSkills )
	{
		sortSkills( generalSkills );
		if ( FileConstants.stratFile.exists( ) )
		{
			try
			{
				BufferedReader in = new BufferedReader( new InputStreamReader( new FileInputStream( FileConstants.stratFile ),
						"GBK" ) );
				String line = null;
				StringWriter writer = new StringWriter( );
				PrintWriter printer = new PrintWriter( writer );
				boolean startGeneral = false;
				while ( ( line = in.readLine( ) ) != null )
				{
					if ( !startGeneral )
					{
						Pattern pattern = Pattern.compile( "^\\s*(character)(.+)(named)(\\s+)(character)" );
						Matcher matcher = pattern.matcher( line );
						if ( matcher.find( ) && line.indexOf( general ) > -1 )
						{
							startGeneral = true;
						}
					}
					else
					{
						Pattern pattern = Pattern.compile( "^\\s*(traits)(\\s+)" );
						Matcher matcher = pattern.matcher( line );
						if ( matcher.find( ) )
						{
							printer.print( matcher.group( ) );
							Iterator iter = generalSkills.getKeyList( )
									.iterator( );
							while ( iter.hasNext( ) )
							{
								String key = (String) iter.next( );
								String value = (String) generalSkills.get( key );
								printer.print( key + " " + value );
								if ( iter.hasNext( ) )
								{
									printer.print( " , " );
								}
								else
									printer.println( );
							}
							startGeneral = false;
							continue;
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
				printer.close( );
			}
			catch ( IOException e )
			{
				e.printStackTrace( );
			}
		}
	}

	private static void sortSkills( SortMap skills )
	{
		List keyList = skills.getKeyList( );
		for ( int i = 0; i < keyList.size( ); i++ )
		{
			String skill = (String) keyList.get( i );
			if ( skill.indexOf( "Jnxg" ) > -1 && keyList.size( ) > 15 )
			{
				keyList.remove( skill );
				keyList.add( 15, skill );
			}
			else
			{
				for ( int j = 3; j < 8; j++ )
				{
					if ( skill.indexOf( "Jn" + j ) > -1 && keyList.size( ) > j )
					{
						keyList.remove( skill );
						keyList.add( j, skill );
					}
				}
			}
		}
	}

	public static void setGeneralName( String general, String name )
	{
		if ( FileConstants.nameFile.exists( ) )
		{
			try
			{
				BufferedReader in = new BufferedReader( new InputStreamReader( new FileInputStream( FileConstants.nameFile ),
						"UTF-16LE" ) );
				String line = null;
				StringWriter writer = new StringWriter( );
				PrintWriter printer = new PrintWriter( writer );
				while ( ( line = in.readLine( ) ) != null )
				{
					Pattern pattern = Pattern.compile( "^\\s*(\\{\\s*)("
							+ general
							+ ")(\\s*\\})(\\s*)" );
					Matcher matcher = pattern.matcher( line );
					if ( matcher.find( ) )
					{
						printer.println( matcher.group( ) + name );
					}
					else
						printer.println( line );
				}
				in.close( );
				PrintWriter out = new PrintWriter( new BufferedWriter( new OutputStreamWriter( new FileOutputStream( FileConstants.nameFile ),
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

	public static SortMap getGeneralSkills( String general )
	{
		SortMap skillMap = new SortMap( );
		if ( FileConstants.stratFile.exists( ) )
		{
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
						Pattern pattern = Pattern.compile( "^\\s*(character)(.+)(named)(\\s+)(character)" );
						Matcher matcher = pattern.matcher( line );
						if ( matcher.find( ) && line.indexOf( general ) > -1 )
						{
							startGeneral = true;
						}
					}
					else
					{
						Pattern pattern = Pattern.compile( "^\\s*(traits)(\\s+)" );
						Matcher matcher = pattern.matcher( line );
						if ( matcher.find( ) )
						{
							String[] skills = line.replaceAll( "traits", "" )
									.split( ";" )[0].trim( ).split( "," );
							for ( int i = 0; i < skills.length; i++ )
							{
								String[] skill = skills[i].trim( )
										.replaceAll( "\\s+", "," )
										.split( "," );
								if ( skill.length == 2 )
								{
									skillMap.put( skill[0], skill[1] );
								}
							}
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
		return skillMap;
	}

	public static void removeBaowu( Map baowuMap )
	{
		if ( FileConstants.stratFile.exists( ) )
		{
			try
			{
				BufferedReader in = new BufferedReader( new InputStreamReader( new FileInputStream( FileConstants.stratFile ),
						"GBK" ) );
				String line = null;
				StringWriter writer = new StringWriter( );
				PrintWriter printer = new PrintWriter( writer );
				boolean startGeneral = false;
				String general = null;
				while ( ( line = in.readLine( ) ) != null )
				{
					if ( !startGeneral )
					{
						Pattern pattern = Pattern.compile( "^\\s*(character)(.+)(named)(\\s+)(character)" );
						Matcher matcher = pattern.matcher( line );
						if ( matcher.find( ) )
						{
							Iterator iter = baowuMap.keySet( ).iterator( );
							while ( iter.hasNext( ) )
							{
								String key = (String) iter.next( );
								if ( line.indexOf( key ) > -1 )
								{
									startGeneral = true;
									general = key;
									continue;
								}
							}
						}
					}
					else
					{
						Pattern pattern = Pattern.compile( "^\\s*(ancillaries)(\\s+)" );
						Matcher matcher = pattern.matcher( line );
						List generalBaowus = (List) baowuMap.get( general );
						if ( matcher.find( ) )
						{
							String[] baowus = line.replaceAll( "ancillaries",
									"" )
									.trim( )
									.split( "," );
							List baowuList = new ArrayList( );
							for ( int i = 0; i < baowus.length; i++ )
							{
								if ( !generalBaowus.contains( baowus[i].trim( ) ) )
									baowuList.add( baowus[i].trim( ) );
							}
							if ( baowuList.size( ) > 0 )
							{
								printer.print( matcher.group( ) );
								for ( int i = 0; i < baowuList.size( ); i++ )
								{
									printer.print( baowuList.get( i ) );
									if ( i < baowuList.size( ) - 1 )
										printer.print( ", " );
								}
								printer.println( );
							}
							startGeneral = false;
							general = null;
							continue;
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
				printer.close( );
			}
			catch ( IOException e )
			{
				e.printStackTrace( );
			}
		}
	}

	public static void setGeneralBaowus( String general, String[] baowus )
	{
		if ( FileConstants.stratFile.exists( ) )
		{
			try
			{
				BufferedReader in = new BufferedReader( new InputStreamReader( new FileInputStream( FileConstants.stratFile ),
						"GBK" ) );
				String line = null;
				StringWriter writer = new StringWriter( );
				PrintWriter printer = new PrintWriter( writer );
				boolean startGeneral = false;
				while ( ( line = in.readLine( ) ) != null )
				{
					if ( startGeneral == false )
					{
						Pattern pattern = Pattern.compile( "^\\s*(character)(.+)(named)(\\s+)(character)" );
						Matcher matcher = pattern.matcher( line );
						if ( matcher.find( ) && line.indexOf( general ) > -1 )
						{
							startGeneral = true;
						}
					}
					else
					{
						Pattern pattern = Pattern.compile( "^\\s*(ancillaries)(\\s+)" );
						Matcher matcher = pattern.matcher( line );
						if ( matcher.find( ) )
						{
							if ( baowus.length > 0 )
							{
								printer.print( matcher.group( ) );
								for ( int i = 0; i < baowus.length; i++ )
								{
									printer.print( baowus[i] );
									if ( i < baowus.length - 1 )
										printer.print( ", " );
								}
								printer.println( );
							}
							startGeneral = false;
							continue;
						}
						pattern = Pattern.compile( "^\\s*(army)" );
						matcher = pattern.matcher( line );
						if ( matcher.find( ) )
						{
							if ( baowus.length > 0 )
							{
								printer.print( "ancillaries " );
								for ( int i = 0; i < baowus.length; i++ )
								{
									printer.print( baowus[i] );
									if ( i < baowus.length - 1 )
										printer.print( ", " );
								}
								printer.println( );
							}
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
				printer.close( );
			}
			catch ( IOException e )
			{
				e.printStackTrace( );
			}
		}
	}

	public static String[] getJueweiLevels( String juewei )
	{
		List jueweiList = new ArrayList( );
		if ( FileConstants.characterTraitFile.exists( ) )
		{
			try
			{
				BufferedReader in = new BufferedReader( new InputStreamReader( new FileInputStream( FileConstants.characterTraitFile ),
						"GBK" ) );
				String line = null;
				boolean startTrait = false;

				while ( ( line = in.readLine( ) ) != null )
				{
					if ( !startTrait )
					{
						Pattern pattern = Pattern.compile( "^\\s*(Trait)(\\s+)"
								+ juewei
								+ "(\\s*)$" );
						Matcher matcher = pattern.matcher( line );
						if ( matcher.find( ) )
						{
							startTrait = true;
							continue;
						}
					}
					else
					{
						Pattern pattern = Pattern.compile( "^\\s*(Level)(\\s+)" );
						Matcher matcher = pattern.matcher( line );
						if ( matcher.find( ) )
						{
							jueweiList.add( line.substring( matcher.end( ) )
									.split( ";" )[0].trim( ) );
							continue;
						}

						pattern = Pattern.compile( "^\\s*(Trait)(\\s+)" );
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
		return (String[]) jueweiList.toArray( new String[0] );
	}

	public static void setGeneralJueweis( String juewei, String[] jueweis,
			String generalDescription )
	{
		String[] jueweiLevels = getJueweiLevels( juewei );
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
					if ( line.indexOf( juewei ) > -1 )
					{
						int index = jueweis.length - jueweiLevels.length;
						for ( int i = 0; i < jueweiLevels.length
								&& i + index < jueweis.length; i++ )
						{
							if ( line.indexOf( "{" + jueweiLevels[i] + "}" ) > -1 )
							{
								printer.println( "{"
										+ jueweiLevels[i]
										+ "}【人物列傳】" );
							}
							else if ( line.indexOf( "{"
									+ jueweiLevels[i]
									+ "_desc}" ) > -1 )
							{
								if ( generalDescription == null
										|| generalDescription.trim( ).length( ) == 0 )
									printer.println( line );
								else
								{
									String desc = ChangeCode.toShort( generalDescription );
									StringBuffer buffer = new StringBuffer( );
									for ( int j = 0; j < desc.length( ); j++ )
									{
										buffer.append( desc.charAt( j ) );
										if ( j > 0 && j % 26 == 0 )
										{
											buffer.append( "\\n" );
										}
									}
									printer.println( "{"
											+ jueweiLevels[i]
											+ "_desc}"
											+ buffer.toString( ) );
								}
							}
							else if ( line.indexOf( "{"
									+ jueweiLevels[i]
									+ "_effects_desc}" ) > -1 )
							{
								printer.println( line );

							}
							else if ( line.indexOf( "{"
									+ jueweiLevels[i]
									+ "_gain_desc}" ) > -1 )
							{
								printer.println( line );

							}
							else if ( line.indexOf( "{"
									+ jueweiLevels[i]
									+ "_epithet_desc}" ) > -1 )
							{
								if ( jueweis != null
										&& jueweis[i + index] != null )
								{
									if ( "△".equals( jueweis[i + index] ) )
									{
										printer.println( "{"
												+ jueweiLevels[i]
												+ "_epithet_desc}"
												+ jueweis[i + index] );
									}
									else
									{
										String jueweiTemp = ChangeCode.toShort( jueweis[i
												+ index] );
										jueweiTemp = ( "·" + jueweiTemp ).replaceAll( "··",
												"·" );
										printer.println( "{"
												+ jueweiLevels[i]
												+ "_epithet_desc}"
												+ jueweiTemp );
									}
								}
							}
						}
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

	public static String[] getGeneralJueweis( String general )
	{
		String[] jueweiLevels = getJueweiLevels( general );
		String[] jueweis = new String[jueweiLevels.length];
		if ( FileConstants.vnVsFile.exists( ) )
		{
			try
			{
				BufferedReader in = new BufferedReader( new InputStreamReader( new FileInputStream( FileConstants.vnVsFile ),
						"UTF-16LE" ) );
				String line = null;
				while ( ( line = in.readLine( ) ) != null )
				{
					if ( line.indexOf( general ) > -1 )
					{
						for ( int i = 0; i < jueweiLevels.length; i++ )
						{
							if ( line.indexOf( "{"
									+ jueweiLevels[i]
									+ "_epithet_desc}" ) > -1 )
							{
								jueweis[i] = line.replaceAll( "\\{"
										+ jueweiLevels[i]
										+ "_epithet_desc\\}",
										"" ).trim( );
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
		return jueweis;
	}

	public static void setGeneralModels( String general, String generalModel,
			String battleModel )
	{
		if ( FileConstants.stratFile.exists( ) )
		{
			try
			{
				BufferedReader in = new BufferedReader( new InputStreamReader( new FileInputStream( FileConstants.stratFile ),
						"GBK" ) );
				String line = null;
				StringWriter writer = new StringWriter( );
				PrintWriter printer = new PrintWriter( writer );
				while ( ( line = in.readLine( ) ) != null )
				{
					Pattern pattern = Pattern.compile( "^\\s*(character)(.+)(named)(\\s+)(character)" );
					Matcher matcher = pattern.matcher( line );
					if ( matcher.find( )
							&& line.split( "(?i)(\\s+)("
									+ general
									+ ")(\\s*)(,)" ).length == 2 )
					{
						General outModel = (General) MapUtil.generalModelMap.get( general );
						if ( outModel.getStrat_model( ) != null
								&& outModel.getBattle_model( ) != null )
						{
							if ( generalModel != null )
							{
								line = line.replaceAll( "(?i)(strat_model)(\\s+)([a-zA-Z0-9_\\-]+)",
										"strat_model " + generalModel );
							}
							if ( battleModel != null )
							{
								line = line.replaceAll( "(?i)(battle_model)(\\s+)([a-zA-Z0-9_\\-]+)",
										"battle_model " + battleModel );
							}

						}
						else
						{
							if ( generalModel != null )
							{
								line += ", ";
								line += ( "strat_model " + generalModel );
							}
							if ( battleModel != null )
							{
								line += ", ";
								line += ( "battle_model " + battleModel );
							}
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
				printer.close( );
			}
			catch ( IOException e )
			{
				e.printStackTrace( );
			}
		}

	}

	public static void setGeneralPosition( String general, String posX,
			String posY )
	{
		if ( FileConstants.stratFile.exists( ) )
		{
			try
			{
				BufferedReader in = new BufferedReader( new InputStreamReader( new FileInputStream( FileConstants.stratFile ),
						"GBK" ) );
				String line = null;
				StringWriter writer = new StringWriter( );
				PrintWriter printer = new PrintWriter( writer );
				while ( ( line = in.readLine( ) ) != null )
				{
					Pattern pattern = Pattern.compile( "^\\s*(character)(.+)(named)(\\s+)(character)" );
					Matcher matcher = pattern.matcher( line );
					if ( matcher.find( )
							&& line.split( "(?i)(\\s+)("
									+ general
									+ ")(\\s*)(,)" ).length == 2 )
					{
						if ( posX != null )
						{
							line = line.replaceAll( "(?i)(,)(\\s*)(x)(\\s+)(\\d+)(\\s*)(,)",
									", x " + posX + "," );
						}
						if ( posY != null )
						{
							line = line.replaceAll( "(?i)(,)(\\s*)(y)(\\s+)(\\d+)(\\s*)(,)",
									", y " + posY + "," );
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
				printer.close( );
			}
			catch ( IOException e )
			{
				e.printStackTrace( );
			}
		}

	}

	public static void addGeneralName( String general, String displayName )
	{
		if ( FileConstants.nameFile.exists( ) )
		{
			try
			{
				PrintWriter out = new PrintWriter( new BufferedWriter( new OutputStreamWriter( new FileOutputStream( FileConstants.nameFile,
						true ),
						"UTF-16LE" ) ),
						false );
				out.println( );
				out.println( "{"
						+ general
						+ "}	"
						+ ChangeCode.toShort( displayName ) );
				out.close( );
			}
			catch ( IOException e )
			{
				e.printStackTrace( );
			}
		}
	}

	public static void createGeneralJueweis( String general, String[] jueweis,
			String generalDescription )
	{
		if ( FileConstants.vnVsFile.exists( ) )
		{
			try
			{

				PrintWriter out = new PrintWriter( new BufferedWriter( new OutputStreamWriter( new FileOutputStream( FileConstants.vnVsFile,
						true ),
						"UTF-16LE" ) ),
						false );
				out.println( );
				for ( int i = 0; i < jueweis.length; i++ )
				{
					String level = general + "-" + ( i + 1 );
					out.println( "{" + level + "}【人物列傳】" );
					out.println( "{" + level + "_desc}" + generalDescription );
					if ( i == 0 )
					{
						out.println( "{" + level + "_effects_desc}▲" );

					}
					else
					{
						out.println( "{"
								+ level
								+ "_effects_desc}晉升爵位威望影響+"
								+ i );
					}
					out.println( "{" + level + "_gain_desc}這個將軍的爵位提高了。" );
					out.println( "{"
							+ level
							+ "_epithet_desc}"
							+ ChangeCode.toShort( jueweis[i] ) );
				}
				out.close( );
			}
			catch ( IOException e )
			{
				e.printStackTrace( );
			}
		}

		if ( FileConstants.characterTraitFile.exists( ) )
		{
			try
			{
				BufferedReader in = new BufferedReader( new InputStreamReader( GeneralParser.class.getResourceAsStream( "/org/sf/feeling/sanguo/patch/script/levelscript.txt" ),
						"UTF-8" ) );
				String line = null;
				StringWriter writer = new StringWriter( );
				PrintWriter printer = new PrintWriter( writer );
				while ( ( line = in.readLine( ) ) != null )
				{
					printer.println( line );
				}
				in.close( );
				printer.close( );
				String levelscript = writer.getBuffer( )
						.toString( )
						.replaceAll( "Custom_General_Name", general );

				in = new BufferedReader( new InputStreamReader( GeneralParser.class.getResourceAsStream( "/org/sf/feeling/sanguo/patch/script/triggerscript.txt" ),
						"UTF-8" ) );
				writer = new StringWriter( );
				printer = new PrintWriter( writer );
				while ( ( line = in.readLine( ) ) != null )
				{
					printer.println( line );
				}
				in.close( );
				printer.close( );
				String triggerscript = writer.getBuffer( )
						.toString( )
						.replaceAll( "Custom_General_Name", general );

				writer = new StringWriter( );
				printer = new PrintWriter( writer );
				in = new BufferedReader( new InputStreamReader( new FileInputStream( FileConstants.characterTraitFile ),
						"GBK" ) );
				line = null;
				while ( ( line = in.readLine( ) ) != null )
				{
					printer.println( line );
				}
				in.close( );

				PrintWriter out = new PrintWriter( new BufferedWriter( new OutputStreamWriter( new FileOutputStream( FileConstants.characterTraitFile ),
						"GBK" ) ),
						false );
				out.println( levelscript );
				out.print( writer.getBuffer( ) );
				out.println( ";------------------------------------------");
				out.println( triggerscript );
				out.close( );
				printer.close( );
			}
			catch ( IOException e )
			{
				e.printStackTrace( );
			}
		}
		if ( FileConstants.enumVnVsFile.exists( ) )
		{

			StringWriter writer = new StringWriter( );
			PrintWriter printer = new PrintWriter( writer );
			printer.println( );
			for ( int i = 0; i < jueweis.length; i++ )
			{
				String level = general + "-" + ( i + 1 );
				printer.println( level );
				printer.println( level + "_effects_desc" );
				printer.println( level + "_gain_desc" );
				printer.println( level + "_gain_desc" );
				printer.println( level + "_epithet_desc" );
			}
			printer.close( );
			FileUtil.appendToFile( FileConstants.enumVnVsFile,
					writer.getBuffer( ).toString( ) );

		}
	}
}
