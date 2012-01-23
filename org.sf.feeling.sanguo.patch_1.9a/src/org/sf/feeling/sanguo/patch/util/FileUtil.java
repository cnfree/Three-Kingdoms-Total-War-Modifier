/*******************************************************************************
 * Copyright (c) 2007 cnfree.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  cnfree  - initial API and implementation
 *******************************************************************************/

package org.sf.feeling.sanguo.patch.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.swt.SWT;

public class FileUtil
{

	public static File bakFile( File file )
	{
		return bakFile( file, ".patch.bak" );
	}

	public static File bakFile( File file, String string )
	{
		return bakFile( file, ".patch.bak", "GBK" );
	}

	public static File bakFile( File file, String extension, String encode )
	{
		String filePath = file.getAbsolutePath( );
		String bakFilePath = filePath + extension;
		File bakFile = new File( bakFilePath );
		if ( !bakFile.exists( ) )
		{
			try
			{
				int sizeL = (int) file.length( );
				int chars_read = 0;
				BufferedReader in = new BufferedReader( new InputStreamReader( new FileInputStream( file ),
						encode ) );
				char[] data = new char[sizeL];
				while ( in.ready( ) )
				{
					chars_read += in.read( data, chars_read, sizeL - chars_read );
				}
				in.close( );
				char[] v = new char[chars_read];
				System.arraycopy( data, 0, v, 0, chars_read );
				String temp = new String( v );
				PrintWriter out = new PrintWriter( new BufferedWriter( new OutputStreamWriter( new FileOutputStream( bakFile ),
						encode ) ),
						false );
				out.print( temp );
				out.close( );
			}
			catch ( Exception e )
			{
				e.printStackTrace( );
			}
		}
		return bakFile;
	}

	public static void restoreFile( File file )
	{
		restoreFile( file, ".patch.bak" );
	}

	public static void restoreFile( File file, String string )
	{
		restoreFile( file, ".patch.bak", "GBK" );
	}

	public static void restoreFile( File file, String extension, String encode )
	{
		String filePath = file.getAbsolutePath( );
		String bakFilePath = filePath + extension;
		File bakFile = new File( bakFilePath );
		if ( bakFile.exists( ) )
		{
			try
			{
				int sizeL = (int) bakFile.length( );
				int chars_read = 0;
				BufferedReader in = new BufferedReader( new InputStreamReader( new FileInputStream( bakFile ),
						encode ) );
				char[] data = new char[sizeL];
				while ( in.ready( ) )
				{
					chars_read += in.read( data, chars_read, sizeL - chars_read );
				}
				in.close( );
				char[] v = new char[chars_read];
				System.arraycopy( data, 0, v, 0, chars_read );
				String temp = new String( v );
				PrintWriter out = new PrintWriter( new BufferedWriter( new OutputStreamWriter( new FileOutputStream( file ),
						encode ) ),
						false );
				out.print( temp );
				out.close( );
				bakFile.delete( );
			}
			catch ( Exception e )
			{
				e.printStackTrace( );
			}
		}
	}

	public static void clearRestoreFile( File file )
	{
		String filePath = file.getAbsolutePath( );
		String bakFilePath = filePath + ".patch.bak";
		File bakFile = new File( bakFilePath );
		if ( bakFile.exists( ) )
		{
			bakFile.delete( );
		}
	}

	public static void replaceFile( File file, String regex1, String regex2,
			String replacement )
	{
		Map map = new HashMap( );
		map.put( regex2, replacement );
		replaceFile( file, regex1, map );
	}

	public static void replaceFile( File file, String regex1, String regex2,
			String replacement, String encoding )
	{
		Map map = new HashMap( );
		map.put( regex2, replacement );
		replaceFile( file, regex1, map, encoding );
	}

	public static void replaceFile( File file, String regex, Map map )
	{
		replaceFile( file, regex, map, "GBK" );
	}

	public static void replaceFile( File file, String regex, Map map,
			String encoding )
	{
		// bakFile(file);
		try
		{
			int sizeL = (int) file.length( );
			int chars_read = 0;
			BufferedReader in = new BufferedReader( new InputStreamReader( new FileInputStream( file ),
					encoding ) );
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
				Iterator iter = map.keySet( ).iterator( );
				String group = matcher.group( );
				while ( iter.hasNext( ) )
				{
					String key = (String) iter.next( );
					Pattern pattern1 = Pattern.compile( key );
					Matcher matcher1 = pattern1.matcher( group );
					group = matcher1.replaceAll( (String) map.get( key ) );
				}
				matcher.appendReplacement( sbr, group );
			}
			matcher.appendTail( sbr );
			PrintWriter out = new PrintWriter( new BufferedWriter( new OutputStreamWriter( new FileOutputStream( file ),
					encoding ) ),
					false );
			out.print( sbr );
			out.close( );
		}
		catch ( Exception e )
		{
			e.printStackTrace( );
		}
	}

	public static String findMatchString( File file, String regex1,
			String regex2 )
	{
		try
		{
			int sizeL = (int) file.length( );
			int chars_read = 0;
			BufferedReader in = new BufferedReader( new InputStreamReader( new FileInputStream( file ),
					"GBK" ) );
			char[] data = new char[sizeL];
			while ( in.ready( ) )
			{
				chars_read += in.read( data, chars_read, sizeL - chars_read );
			}
			in.close( );
			char[] v = new char[chars_read];
			System.arraycopy( data, 0, v, 0, chars_read );
			String temp = new String( v );
			Pattern pattern = Pattern.compile( regex1 );
			Matcher matcher = pattern.matcher( temp );
			while ( matcher.find( ) )
			{
				Pattern pattern1 = Pattern.compile( regex2 );
				Matcher matcher1 = pattern1.matcher( matcher.group( ) );
				while ( matcher1.find( ) )
				{
					return matcher1.group( );
				}
			}
		}
		catch ( Exception e )
		{
			e.printStackTrace( );
		}
		return "";
	}

	public static boolean containMatchString( File file, String regex )
	{
		try
		{
			int sizeL = (int) file.length( );
			int chars_read = 0;
			BufferedReader in = new BufferedReader( new InputStreamReader( new FileInputStream( file ),
					"GBK" ) );
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
			while ( matcher.find( ) )
			{
				return true;
			}
		}
		catch ( Exception e )
		{
			e.printStackTrace( );
		}
		return false;
	}

	public static void appendToFile( File file, String string )
	{
		try
		{
			PrintWriter out = new PrintWriter( new OutputStreamWriter( new FileOutputStream( file,
					true ),
					"GBK" ) );
			out.println( );
			out.println( string );
			out.close( );
		}
		catch ( IOException e )
		{
			e.printStackTrace( );
		}
	}

	public static void appendToFile( File destFile, InputStream source )
	{
		try
		{
			int chars_read = 0;
			BufferedReader in = new BufferedReader( new InputStreamReader( source,
					"GBK" ) );
			List datas = new ArrayList( );
			while ( in.ready( ) )
			{
				char[] data = new char[1024];
				chars_read += in.read( data, 0, 1024 );
				datas.add( data );
			}
			in.close( );
			source.close( );
			char[] v = new char[chars_read];
			char[] data = new char[datas.size( ) * 1024];
			for ( int i = 0; i < datas.size( ); i++ )
			{
				System.arraycopy( (char[]) datas.get( i ),
						0,
						data,
						i * 1024,
						1024 );
			}
			System.arraycopy( data, 0, v, 0, chars_read );
			String temp = new String( v );
			PrintWriter out = new PrintWriter( new OutputStreamWriter( new FileOutputStream( destFile,
					true ),
					"GBK" ) );
			out.println( );
			out.println( temp );
			out.close( );
		}
		catch ( IOException e )
		{
			e.printStackTrace( );
		}
	}

	public static void writeToBinarayFile( File file, InputStream source )
	{
		writeToBinarayFile( file, source, true );
	}

	public static void writeToBinarayFile( File file, InputStream source,
			boolean close )
	{
		try
		{
			BufferedInputStream bis = new BufferedInputStream( source );
			BufferedOutputStream fouts = new BufferedOutputStream( new FileOutputStream( file ) );
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
			e.printStackTrace( );
		}
	}

	public static Properties loadProperties( String property )
	{
		try
		{
			Properties props = new Properties( );
			InputStream in = null;
			if ( "faction".equals( property ) )
			{
				in = new FileInputStream( FileConstants.factionPropertiesFile );
			}
			else
			{
				in = FileUtil.class.getResourceAsStream( "/org/sf/feeling/sanguo/patch/code/"
						+ property
						+ ".properties" );
			}
			props.load( in );
			in.close( );

			Set set = props.entrySet( );
			Entry[] entries = (Entry[]) set.toArray( new Entry[0] );
			for ( int i = 0; i < entries.length; i++ )
			{
				Entry entry = entries[i];
				String key = (String) entry.getKey( );
				String value = (String) entry.getValue( );
				props.remove( entry.getKey( ) );
				props.put( convert( key ), convert( value ) );
			}
			return props;
		}
		catch ( Exception e )
		{
			SWT.error( SWT.ERROR_IO, e );
		}
		return null;
	}

	private static Object convert( String string )
	{
		try
		{
			return new String( string.getBytes( "ISO-8859-1" ), "utf-8" );
		}
		catch ( UnsupportedEncodingException e )
		{
			return string;
		}
	}

	public static Properties loadProperties( File file )
	{
		try
		{
			Properties props = new Properties( );
			InputStream in = new FileInputStream( file );
			props.load( in );
			in.close( );
			return props;
		}
		catch ( Exception e )
		{
			SWT.error( SWT.ERROR_IO, e );
		}
		return null;
	}

	public static void checkAndSaveFile( File file )
	{
		try
		{
			BufferedInputStream bis = new BufferedInputStream( new FileInputStream( file ) );
			ByteArrayOutputStream bos = new ByteArrayOutputStream( );
			BufferedOutputStream fouts = new BufferedOutputStream( bos );
			byte b[] = new byte[1024];
			int i = 0;
			while ( ( i = bis.read( b ) ) != -1 )
			{
				fouts.write( b, 0, i );
			}
			fouts.flush( );
			bis.close( );
			String content = bos.toString( "gbk" );
			fouts.close( );
			StringBuffer buffer = new StringBuffer( );
			for ( int j = 0; j < content.length( ); j++ )
			{
				char ch = content.charAt( j );
				if ( ch == '\r' )
				{
					if ( j < content.length( ) - 1
							&& content.charAt( j + 1 ) != '\n' )
					{
						// ingore '\n'
					}
					else
					{
						buffer.append( ch );
					}
				}
				else if ( ch == '\n' )
				{
					if ( j > 0 && content.charAt( j - 1 ) != '\r' )
					{
						// ingore '\r'
					}
					else
					{
						buffer.append( ch );
					}
				}
				else
					buffer.append( ch );
			}
			PrintWriter out = new PrintWriter( new BufferedWriter( new OutputStreamWriter( new FileOutputStream( FileConstants.unitFile ),
					"GBK" ) ),
					false );
			out.print( buffer );
			out.close( );
		}
		catch ( IOException e )
		{
			e.printStackTrace( );
		}
	}

	public static void deleteFile( File file )
	{
		if ( file.exists( ) )
		{
			if ( file.isFile( ) )
			{
				file.delete( );
			}
			else if ( file.isDirectory( ) )
			{
				File files[] = file.listFiles( );
				for ( int i = 0; i < files.length; i++ )
				{
					deleteFile( files[i] );
				}
			}
			file.delete( );
		}
	}

	public static String getMD5Str( File file )
	{
		if ( file == null || !file.exists( ) || !file.isFile( ) )
			return null;
		try
		{
			ByteArrayOutputStream out = new ByteArrayOutputStream( 4096 );
			byte[] tmp = new byte[4096];
			InputStream is = new FileInputStream( file );
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

			MessageDigest messageDigest = null;

			messageDigest = MessageDigest.getInstance( "MD5" );
			messageDigest.reset( );
			messageDigest.update( bytes );

			byte[] byteArray = messageDigest.digest( );

			StringBuffer md5StrBuff = new StringBuffer( );

			for ( int i = 0; i < byteArray.length; i++ )
			{
				if ( Integer.toHexString( 0xFF & byteArray[i] ).length( ) == 1 )
					md5StrBuff.append( "0" )
							.append( Integer.toHexString( 0xFF & byteArray[i] ) );
				else
					md5StrBuff.append( Integer.toHexString( 0xFF & byteArray[i] ) );
			}

			return md5StrBuff.toString( );
		}
		catch ( Exception e )
		{
			e.printStackTrace( );
		}
		return null;
	}
}
