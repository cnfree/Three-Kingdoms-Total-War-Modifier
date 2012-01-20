
package org.sf.feeling.sanguo.patch.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Enumeration;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.sf.feeling.swt.win32.extension.io.FileSystem;

public class UpdateUtil
{

	public static final String PATCH_FILE = FileSystem.getCurrentDirectory( )
			+ "\\update\\patch.zip";
	public static final String PATCH_DIR = FileSystem.getCurrentDirectory( )
			+ "\\update\\patch";
	public static final String UPDATE_EXE = FileSystem.getCurrentDirectory( )
			+ "\\update\\update.exe";
	public static final String UPDATE_DIR = FileSystem.getCurrentDirectory( )
			+ "\\update";
	public static final String BAK_DIR = FileSystem.getCurrentDirectory( )
			+ "\\~bak";

	public static boolean needUpdate = false;

	public static void update( final String version )
	{
		Thread thread = new Thread( ) {

			public void run( )
			{

				try
				{
					URL url = new URL( "http://feeling.sourceforge.net/patch/1.9a/update.info" );
					HttpURLConnection conn = (HttpURLConnection) url.openConnection( );
					BufferedReader reader = new BufferedReader( new InputStreamReader( conn.getInputStream( ) ) );
					String updateVersion = reader.readLine( ).trim( );
					String updateURL = reader.readLine( );
					String updateMD5 = reader.readLine( );
					String zipURL = reader.readLine( );
					String zipMD5 = reader.readLine( );
					reader.close( );
					conn.disconnect( );

					if ( updateVersion != null
							&& version != null
							&& updateVersion.trim( )
									.compareToIgnoreCase( version.trim( ) ) > 0 )
					{
						if ( zipURL != null && updateURL != null )
						{
							download( updateURL );
							download( zipURL );

							File updateZipFile = new File( PATCH_FILE );
							String updateZipMD5 = FileUtil.getMD5Str( updateZipFile );
							File updateExeFile = new File( UPDATE_EXE );
							String updateExeMD5 = FileUtil.getMD5Str( updateExeFile );

							if ( updateExeMD5 != null
									&& updateMD5 != null
									&& updateExeMD5.trim( )
											.toLowerCase( )
											.equals( updateMD5.trim( )
													.toLowerCase( ) )
									&& zipMD5 != null
									&& updateZipMD5 != null
									&& zipMD5.trim( )
											.toLowerCase( )
											.equals( updateZipMD5.trim( )
													.toLowerCase( ) ) )
							{
								needUpdate = true;
								decompressUpdateFile( updateZipFile );
							}
						}
					}
					else
					{
						deleteUpdateFiles( );
					}
				}
				catch ( IOException e )
				{
					e.printStackTrace( );
				}
			}
		};
		thread.setDaemon( true );
		thread.start( );
	}

	public static void main( String[] args )
	{
		System.out.println( FileUtil.getMD5Str( new File( "C:\\Users\\cchen\\Desktop\\patch\\output\\patch_1.9a_2.5.zip" ) ) );
	}

	protected static void download( String downloadURL ) throws IOException
	{
		int nStartPos = 0;
		URL url = new URL( downloadURL );
		HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection( );
		long nEndPos = getFileSize( downloadURL );
		File updateFile = new File( PATCH_FILE );
		if ( !updateFile.exists( ) )
		{
			if ( !updateFile.getParentFile( ).exists( ) )
				updateFile.getParentFile( ).mkdirs( );
			updateFile.createNewFile( );
		}
		RandomAccessFile oSavedFile = new RandomAccessFile( PATCH_FILE, "rw" );
		httpConnection.setRequestProperty( "User-Agent", "Internet Explorer" );
		String sProperty = "bytes=" + nStartPos + "-";
		httpConnection.setRequestProperty( "RANGE", sProperty );
		InputStream input = httpConnection.getInputStream( );
		byte[] b = new byte[1024];
		int nRead = 0;
		while ( ( nRead = input.read( b, 0, 1024 ) ) > 0 && nStartPos < nEndPos )
		{
			oSavedFile.write( b, 0, nRead );
			nStartPos += nRead;
		}
		httpConnection.disconnect( );
	}

	protected static long getFileSize( String sURL )
	{
		int nFileLength = -1;
		try
		{
			URL url = new URL( sURL );
			HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection( );
			httpConnection.setRequestProperty( "User-Agent",
					"Internet Explorer" );
			int responseCode = httpConnection.getResponseCode( );
			if ( responseCode >= 400 )
			{
				System.err.println( "Error Code : " + responseCode );
				return -2; // -2 represent access is error
			}
			String sHeader;
			for ( int i = 1;; i++ )
			{
				sHeader = httpConnection.getHeaderFieldKey( i );
				if ( sHeader != null )
				{
					if ( sHeader.equals( "Content-Length" ) )
					{
						nFileLength = Integer.parseInt( httpConnection.getHeaderField( sHeader ) );
						break;
					}
				}
				else
					break;
			}
		}
		catch ( Exception e )
		{
			e.printStackTrace( );
		}
		return nFileLength;
	}

	private static void deleteUpdateFiles( )
	{
		File updateDir = new File( UPDATE_DIR );
		if ( updateDir.exists( ) )
		{
			FileUtil.deleteFile( updateDir );
		}
		File bakDir = new File( BAK_DIR );
		if ( bakDir.exists( ) )
		{
			FileUtil.deleteFile( bakDir );
		}
	}

	public static void decompressUpdateFile( File updateZipFile )
			throws IOException
	{
		ZipFile zipFile = null;
		try
		{
			File patchDir = new File( PATCH_DIR );
			if ( !patchDir.exists( ) )
				patchDir.mkdirs( );

			zipFile = new ZipFile( updateZipFile, "GBK" );
			Enumeration<ZipEntry> enumeration = zipFile.getEntries( );
			ZipEntry zipEntry = null;
			while ( enumeration.hasMoreElements( ) )
			{
				zipEntry = enumeration.nextElement( );
				if ( zipEntry.isDirectory( ) )
					continue;
				String filePath = PATCH_DIR
						+ "\\"
						+ zipEntry.getName( ).replaceAll( "/", "\\\\" );
				File patchFile = new File( filePath );
				if ( !patchFile.exists( ) )
				{
					if ( !patchFile.getParentFile( ).exists( ) )
					{
						patchFile.getParentFile( ).mkdirs( );
					}
					patchFile.createNewFile( );
				}
				FileUtil.writeToBinarayFile( patchFile,
						zipFile.getInputStream( zipEntry ),
						true );
			}
			zipFile.close( );
		}
		catch ( Exception e )
		{
			e.printStackTrace( );
			if ( zipFile != null )
			{
				zipFile.close( );
			}
			deleteUpdateFiles( );
		}
	}
}
