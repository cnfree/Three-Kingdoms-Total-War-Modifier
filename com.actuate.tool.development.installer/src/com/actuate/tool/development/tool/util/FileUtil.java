/*******************************************************************************
 * Copyright (c) 2011 cnfree.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  cnfree  - initial API and implementation
 *******************************************************************************/

package com.actuate.tool.development.tool.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileUtil
{

	public static void writeToFile( File file, String string )
	{
		try
		{
			PrintWriter out = new PrintWriter( new OutputStreamWriter( new FileOutputStream( file ) ) );
			out.print( string );
			out.close( );
		}
		catch ( IOException e )
		{
			e.printStackTrace( );
		}
	}

	public static void writeToBinarayFile( File file, InputStream source,
			boolean close )
	{
		BufferedInputStream bis = null;
		BufferedOutputStream fouts = null;
		try
		{
			bis = new BufferedInputStream( source );
			if ( !file.exists( ) )
			{
				if ( !file.getParentFile( ).exists( ) )
				{
					file.getParentFile( ).mkdirs( );
				}
				file.createNewFile( );
			}
			fouts = new BufferedOutputStream( new FileOutputStream( file ) );
			byte b[] = new byte[1024];
			int i = 0;
			while ( ( i = bis.read( b ) ) != -1 )
			{
				fouts.write( b, 0, i );
			}
			fouts.flush( );
			fouts.close( );
			if ( close )
				bis.close( );
		}
		catch ( IOException e )
		{
			Logger.getLogger( FileUtil.class.getName( ) ).log( Level.WARNING,
					"Write binaray file failed.", //$NON-NLS-1$
					e );
			try
			{
				if ( fouts != null )
					fouts.close( );
			}
			catch ( IOException f )
			{
				Logger.getLogger( FileUtil.class.getName( ) )
						.log( Level.WARNING, "Close output stream failed.", f ); //$NON-NLS-1$
			}
			if ( close )
			{
				try
				{
					if ( bis != null )
						bis.close( );
				}
				catch ( IOException f )
				{
					Logger.getLogger( FileUtil.class.getName( ) )
							.log( Level.WARNING, "Close input stream failed.", //$NON-NLS-1$
									f );
				}
			}
		}
	}

	public static boolean copyFile( String src, String des )
	{
		FileInputStream fis = null;
		try
		{
			fis = new FileInputStream( src );
			writeToBinarayFile( new File( des ), fis, false );
			fis.close( );
		}
		catch ( Exception e )
		{
			Logger.getLogger( FileUtil.class.getName( ) ).log( Level.WARNING,
					"Copy file failed.", //$NON-NLS-1$
					e );
			try
			{
				fis.close( );
				return true;
			}
			catch ( IOException f )
			{
				Logger.getLogger( FileUtil.class.getName( ) )
						.log( Level.WARNING, "Close input stream failed.", f ); //$NON-NLS-1$
			}

		}
		return false;
	}

	public static boolean copyDirectory( String srcDirectory,
			String desDirectory )
	{
		return copyDirectory( srcDirectory, desDirectory, null );
	}

	public static boolean copyDirectory( String srcDirectory,
			String desDirectory, FileFilter filter )
	{
		try
		{
			File des = new File( desDirectory );
			if ( !des.exists( ) )
			{
				des.mkdirs( );
			}
			File src = new File( srcDirectory );
			File[] allFile = src.listFiles( );
			int totalNum = allFile.length;
			String srcName = ""; //$NON-NLS-1$
			String desName = ""; //$NON-NLS-1$
			int currentFile = 0;
			for ( currentFile = 0; currentFile < totalNum; currentFile++ )
			{
				if ( !allFile[currentFile].isDirectory( ) )
				{
					srcName = allFile[currentFile].toString( );
					desName = desDirectory
							+ File.separator
							+ allFile[currentFile].getName( );
					if ( filter == null || filter.accept( new File( srcName ) ) )
						copyFile( srcName, desName );
				}
				else
				{
					if ( !copyDirectory( allFile[currentFile].getPath( )
							.toString( ),
							desDirectory
									+ File.separator
									+ allFile[currentFile].getName( )
											.toString( ),
							filter ) )
					{
						Logger.getLogger( FileUtil.class.getName( ) )
								.log( Level.WARNING, "Copy sub directory " //$NON-NLS-1$
										+ srcDirectory
										+ "failed." ); //$NON-NLS-1$
					}
				}
			}
			return true;
		}
		catch ( Exception e )
		{
			Logger.getLogger( FileUtil.class.getName( ) ).log( Level.WARNING,
					"Copy directory " + srcDirectory + "failed.", //$NON-NLS-1$ //$NON-NLS-2$
					e );
			return false;
		}
	}

	public static void copyDirectoryToDirectory( File srcDir, File destDir )
			throws IOException
	{
		copyDirectoryToDirectory( srcDir, destDir, null );
	}

	public static void copyDirectoryToDirectory( File srcDir, File destDir,
			FileFilter filter ) throws IOException
	{
		if ( srcDir == null )
		{
			throw new NullPointerException( "Source must not be null" ); //$NON-NLS-1$
		}
		if ( srcDir.exists( ) && srcDir.isDirectory( ) == false )
		{
			throw new IllegalArgumentException( "Source '" //$NON-NLS-1$
					+ destDir
					+ "' is not a directory" ); //$NON-NLS-1$
		}
		if ( destDir == null )
		{
			throw new NullPointerException( "Destination must not be null" ); //$NON-NLS-1$
		}
		if ( destDir.exists( ) && destDir.isDirectory( ) == false )
		{
			throw new IllegalArgumentException( "Destination '" //$NON-NLS-1$
					+ destDir
					+ "' is not a directory" ); //$NON-NLS-1$
		}
		copyDirectory( srcDir.getAbsolutePath( ),
				new File( destDir, srcDir.getName( ) ).getAbsolutePath( ),
				filter );
	}

	public static long sizeOfDirectory( File directory )
	{
		if ( !directory.exists( ) )
		{
			String message = directory + " does not exist"; //$NON-NLS-1$
			throw new IllegalArgumentException( message );
		}
		if ( !directory.isDirectory( ) )
		{
			String message = directory + " is not a directory"; //$NON-NLS-1$
			throw new IllegalArgumentException( message );
		}
		long size = 0;
		File[] files = directory.listFiles( );
		if ( files == null )
		{ // null if security restricted
			return 0L;
		}
		for ( int i = 0; i < files.length; i++ )
		{
			File file = files[i];
			if ( file.isDirectory( ) )
			{
				size += sizeOfDirectory( file );
			}
			else
			{
				size += file.length( );
			}
		}

		return size;

	}

	/**
	 * Recursively delete a directory.
	 * 
	 * @param directory
	 *            directory to delete
	 * @throws IOException
	 *             in case deletion is unsuccessful
	 */
	public static void deleteDirectory( File directory ) throws IOException
	{
		if ( !directory.exists( ) )
		{
			return;
		}

		cleanDirectory( directory );
		if ( !directory.delete( ) )
		{
			String message = "Unable to delete directory " + directory + ".";
			throw new IOException( message );
		}
	}

	public static void deleteFile( File file ) throws IOException
	{
		if ( !file.exists( ) )
		{
			return;
		}
		if ( file.isFile( ) )
			file.delete( );
		else
			deleteDirectory( file );
	}

	/**
	 * Clean a directory without deleting it.
	 * 
	 * @param directory
	 *            directory to clean
	 * @throws IOException
	 *             in case cleaning is unsuccessful
	 */
	public static void cleanDirectory( File directory ) throws IOException
	{
		if ( !directory.exists( ) )
		{
			String message = directory + " does not exist";
			throw new IllegalArgumentException( message );
		}

		if ( !directory.isDirectory( ) )
		{
			String message = directory + " is not a directory";
			throw new IllegalArgumentException( message );
		}

		IOException exception = null;

		File[] files = directory.listFiles( );
		for ( int i = 0; i < files.length; i++ )
		{
			File file = files[i];
			try
			{
				forceDelete( file );
			}
			catch ( IOException ioe )
			{
				exception = ioe;
			}
		}

		if ( null != exception )
		{
			throw exception;
		}
	}

	public static void forceDelete( File file ) throws IOException
	{
		if ( file.isDirectory( ) )
		{
			deleteDirectory( file );
		}
		else
		{
			if ( !file.exists( ) )
			{
				throw new FileNotFoundException( "File does not exist: " + file );
			}
			if ( !file.delete( ) )
			{
				String message = "Unable to delete file: " + file;
				throw new IOException( message );
			}
		}
	}

	public static void replaceFile( File file, String regex, String replace )
	{
		Map<String, String> map = new HashMap<String, String>( );
		map.put( regex, replace );
		replaceFile( file, regex, map );
	}

	public static void replaceFile( File file, String regex,
			Map<String, String> map )
	{
		try
		{
			int sizeL = (int) file.length( );
			int chars_read = 0;
			BufferedReader in = new BufferedReader( new InputStreamReader( new FileInputStream( file ) ) );
			char[] data = new char[sizeL];
			while ( in.ready( ) )
			{
				chars_read += in.read( data, chars_read, sizeL - chars_read );
			}
			in.close( );
			char[] v = new char[chars_read];
			System.arraycopy( data, 0, v, 0, chars_read );
			String temp = new String( v );
			Pattern pattern = Pattern.compile( regex );
			Matcher matcher = pattern.matcher( temp );
			StringBuffer sbr = new StringBuffer( );
			while ( matcher.find( ) )
			{
				Iterator<String> iter = map.keySet( ).iterator( );
				String group = matcher.group( );
				while ( iter.hasNext( ) )
				{
					String key = (String) iter.next( );
					Pattern pattern1 = Pattern.compile( key );
					Matcher matcher1 = pattern1.matcher( group );
					group = matcher1.replaceAll( Matcher.quoteReplacement( (String) map.get( key ) ) );
				}
				matcher.appendReplacement( sbr,
						Matcher.quoteReplacement( group ) );
			}
			matcher.appendTail( sbr );
			PrintWriter out = new PrintWriter( new BufferedWriter( new OutputStreamWriter( new FileOutputStream( file ) ) ),
					false );
			out.print( sbr );
			out.close( );
		}
		catch ( Exception e )
		{
			e.printStackTrace( );
		}
	}

	public static String getContent( File file )
	{
		try
		{
			ByteArrayOutputStream out = new ByteArrayOutputStream( 4096 );
			byte[] tmp = new byte[4096];
			InputStream is = new BufferedInputStream( new FileInputStream( file ) );
			while ( true )
			{
				int r = is.read( tmp );
				if ( r == -1 )
					break;
				out.write( tmp, 0, r );
			}
			byte[] bytes = out.toByteArray( );
			is.close( );
			out.close( );
			String content = new String( bytes );
			return content.trim( );
		}
		catch ( IOException e )
		{
			e.printStackTrace( );
		}
		return null;
	}

}
