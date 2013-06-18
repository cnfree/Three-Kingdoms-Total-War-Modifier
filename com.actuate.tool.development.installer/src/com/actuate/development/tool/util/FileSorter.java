
package com.actuate.development.tool.util;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileSorter implements Comparator<File>
{

	public static void sortFiles( File[] files )
	{
		Arrays.sort( files, new FileSorter( ) );
	}

	public static void sortFiles( List<File> files )
	{
		Collections.sort( files, new FileSorter( ) );
	}

	public int compare( File file0, File file1 )
	{
		String first = file0.getName( );
		String second = file1.getName( );

		Pattern pattern = Pattern.compile( "\\d{6,10}",
				Pattern.CASE_INSENSITIVE | Pattern.DOTALL );
		String number1 = null;
		Matcher matcher = pattern.matcher( first );
		if ( matcher.find( ) )
		{
			number1 = matcher.group( );
		}

		String number2 = null;
		matcher = pattern.matcher( second );
		if ( matcher.find( ) )
		{
			number2 = matcher.group( );
		}

		if ( number1 == null && number2 == null )
			return first.compareTo( second );
		else if ( number1 != null && number2 == null )
			return 1;
		else if ( number2 != null && number1 == null )
			return -1;
		else if ( number1.equals( number2 ) )
			return first.compareTo( second );
		else
			return number1.compareTo( number2 );
	}
}