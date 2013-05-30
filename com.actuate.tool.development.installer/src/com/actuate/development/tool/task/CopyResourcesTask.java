
package com.actuate.development.tool.task;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.commons.io.FileUtils;

import com.actuate.development.tool.util.ClassPathUpdater;

public class CopyResourcesTask
{

	public CopyResourcesTask( String source, String target )
	{
		try
		{
			FileUtils.copyDirectory( new File( source ),
					new File( target ),
					new FileFilter( ) {

						public boolean accept( File file )
						{
							if ( file.isFile( )
									&& file.getName( )
											.toLowerCase( )
											.trim( )
											.endsWith( ".zip" ) )
								return false;
							return true;
						}
					} );
		}
		catch ( Exception e )
		{
			StringWriter writer = new StringWriter( );
			PrintWriter ps = new PrintWriter( writer );
			e.printStackTrace( ps );
			try
			{
				writer.close( );
			}
			catch ( IOException e1 )
			{
			}
			String errorMessage = writer.toString( );
			System.err.println( errorMessage );
			System.exit( -1 );
		}
	}

	public static void main( String[] args )
	{
		if ( args.length != 2 )
			return;
		ClassPathUpdater.loadClasspath( );
		new CopyResourcesTask( args[0], args[1] );
	}
}
