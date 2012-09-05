
package com.actuate.tool.development.installer.util;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
		return first.compareTo( second );
	}
}