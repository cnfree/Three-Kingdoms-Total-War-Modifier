
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
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.sf.feeling.swt.win32.extension.util.SortMap;

public class BattleUtil
{

	public static List getModelFactions( String modelType )
	{
		List modelFactions = new ArrayList( );

		try
		{
			String line = null;
			BufferedReader in = new BufferedReader( new InputStreamReader( new FileInputStream( FileConstants.battleFile ),
					"GBK" ) );
			boolean startModel = false;
			while ( ( line = in.readLine( ) ) != null )
			{
				if ( line.trim( ).startsWith( ";" ) )
				{
					continue;
				}
				if ( !startModel )
				{
					Pattern pattern = Pattern.compile( "^\\s*(type)(\\s+)" );
					Matcher matcher = pattern.matcher( line );
					if ( matcher.find( ) )
					{
						String type = line.split( ";" )[0].replaceAll( "type",
								"" ).trim( );
						if ( modelType.equals( type ) )
						{
							startModel = true;
							continue;
						}
					}
				}
				else
				{
					Pattern pattern = Pattern.compile( "^\\s*(model_tri)(\\s+)" );
					Matcher matcher = pattern.matcher( line );
					if ( matcher.find( ) )
					{
						break;
					}

					pattern = Pattern.compile( "^\\s*(texture)(\\.+)(,)(\\s*)" );
					matcher = pattern.matcher( line );
					if ( matcher.find( ) )
					{
						String[] splits = line.split( ";" )[0].split( "," )[0].trim( )
								.split( "\\s+" );
						if ( splits.length == 2 )
							modelFactions.add( splits[1].trim( ) );
					}
					continue;
				}
			}
			in.close( );
		}
		catch ( IOException e )
		{
			e.printStackTrace( );
		}
		return modelFactions;
	}

	public static void removeModelTypes( List modelTypes )
	{
		try
		{
			StringWriter writer = new StringWriter( );
			PrintWriter printer = new PrintWriter( writer );
			String line = null;
			BufferedReader in = new BufferedReader( new InputStreamReader( new FileInputStream( FileConstants.battleFile ),
					"GBK" ) );
			boolean startModel = false;
			while ( ( line = in.readLine( ) ) != null )
			{
				if ( line.trim( ).startsWith( ";" ) )
				{
					printer.println( line );
					continue;
				}
				if ( !startModel )
				{
					Pattern pattern = Pattern.compile( "^\\s*(type)(\\s+)" );
					Matcher matcher = pattern.matcher( line );
					if ( matcher.find( ) )
					{
						String type = line.split( ";" )[0].replaceAll( "type",
								"" ).trim( );
						if ( modelTypes.contains( type ) )
						{
							startModel = true;
							continue;
						}
					}
				}
				else
				{
					Pattern pattern = Pattern.compile( "^\\s*(model_tri)(\\s+)" );
					Matcher matcher = pattern.matcher( line );
					if ( matcher.find( ) )
					{
						startModel = false;
						continue;
					}
					continue;
				}
				printer.println( line );
			}
			in.close( );
			PrintWriter out = new PrintWriter( new BufferedWriter( new OutputStreamWriter( new FileOutputStream( FileConstants.battleFile ),
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

	public static SortMap getFactionTextureMap( )
	{
		return MapUtil.factionTextureMap;
	}

	public static SortMap getFactionDescriptionMap( )
	{
		return MapUtil.factionDescriptionMap;
	}
}
