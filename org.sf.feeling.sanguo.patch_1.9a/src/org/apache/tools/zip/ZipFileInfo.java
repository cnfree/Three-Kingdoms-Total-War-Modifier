/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package org.apache.tools.zip;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.util.zip.ZipException;

/**
 * Replacement for <code>java.util.ZipFile</code>.
 * 
 * <p>
 * This class adds support for file name encodings other than UTF-8 (which is
 * required to work on ZIP files created by native zip tools and is able to skip
 * a preamble like the one found in self extracting archives. Furthermore it
 * returns instances of <code>org.apache.tools.zip.ZipEntry</code> instead of
 * <code>java.util.zip.ZipEntry</code>.
 * </p>
 * 
 * <p>
 * It doesn't extend <code>java.util.zip.ZipFile</code> as it would have to
 * reimplement all methods anyway. Like <code>java.util.ZipFile</code>, it uses
 * RandomAccessFile under the covers and supports compressed and uncompressed
 * entries.
 * </p>
 * 
 * <p>
 * The method signatures mimic the ones of <code>java.util.zip.ZipFile</code>,
 * with a couple of exceptions:
 * 
 * <ul>
 * <li>There is no getName method.</li>
 * <li>entries has been renamed to getEntries.</li>
 * <li>getEntries and getEntry return <code>org.apache.tools.zip.ZipEntry</code>
 * instances.</li>
 * <li>close is allowed to throw IOException.</li>
 * </ul>
 * 
 */
public class ZipFileInfo
{

	/**
	 * The encoding to use for filenames and the file comment.
	 * 
	 * <p>
	 * For a list of possible values see <a
	 * href="http://java.sun.com/j2se/1.5.0/docs/guide/intl/encoding.doc.html"
	 * >http://java.sun.com/j2se/1.5.0/docs/guide/intl/encoding.doc.html</a>.
	 * Defaults to the platform's default character encoding.
	 * </p>
	 */
	private String encoding = null;

	private String comment = null;

	public String getComment( )
	{
		return comment;
	}

	/**
	 * The actual data source.
	 */
	private RandomAccessFile archive;

	/**
	 * Opens the given file for reading, assuming the platform's native encoding
	 * for file names.
	 * 
	 * @param f
	 *            the archive.
	 * 
	 * @throws IOException
	 *             if an error occurs while reading the file.
	 */
	public ZipFileInfo( File f ) throws IOException
	{
		this( f, null );
	}

	/**
	 * Opens the given file for reading, assuming the platform's native encoding
	 * for file names.
	 * 
	 * @param name
	 *            name of the archive.
	 * 
	 * @throws IOException
	 *             if an error occurs while reading the file.
	 */
	public ZipFileInfo( String name ) throws IOException
	{
		this( new File( name ), null );
	}

	/**
	 * Opens the given file for reading, assuming the specified encoding for
	 * file names.
	 * 
	 * @param name
	 *            name of the archive.
	 * @param encoding
	 *            the encoding to use for file names
	 * 
	 * @throws IOException
	 *             if an error occurs while reading the file.
	 */
	public ZipFileInfo( String name, String encoding ) throws IOException
	{
		this( new File( name ), encoding );
	}

	/**
	 * Opens the given file for reading, assuming the specified encoding for
	 * file names.
	 * 
	 * @param f
	 *            the archive.
	 * @param encoding
	 *            the encoding to use for file names
	 * 
	 * @throws IOException
	 *             if an error occurs while reading the file.
	 */
	public ZipFileInfo( File f, String encoding ) throws IOException
	{
		this.encoding = encoding;
		archive = new RandomAccessFile( f, "r" );
		try
		{
			populateFromCentralDirectory( );
		}
		catch ( IOException e )
		{
			try
			{
				archive.close( );
			}
			catch ( IOException e2 )
			{
				// swallow, throw the original exception instead
			}
			throw e;
		}
	}

	/**
	 * The encoding to use for filenames and the file comment.
	 * 
	 * @return null if using the platform's default character encoding.
	 */
	public String getEncoding( )
	{
		return encoding;
	}

	/**
	 * Closes the archive.
	 * 
	 * @throws IOException
	 *             if an error occurs closing the archive.
	 */
	public void close( ) throws IOException
	{
		archive.close( );
	}

	/**
	 * Reads the central directory of the given archive and populates the
	 * internal tables with ZipEntry instances.
	 * 
	 * <p>
	 * The ZipEntrys will know all data that can be obtained from the central
	 * directory alone, but not the data that requires the local file header or
	 * additional data to be read.
	 * </p>
	 */
	private void populateFromCentralDirectory( ) throws IOException
	{
		positionAtCentralDirectory( );
	}

	private static final int MIN_EOCD_SIZE =
	/* end of central dir signature */4
	/* number of this disk */+ 2
	/* number of the disk with the */
	/* start of the central directory */+ 2
	/* total number of entries in */
	/* the central dir on this disk */+ 2
	/* total number of entries in */
	/* the central dir */+ 2
	/* size of the central directory */+ 4
	/* offset of start of central */
	/* directory with respect to */
	/* the starting disk number */+ 4
	/* zipfile comment length */+ 2;

	/**
	 * Searches for the &quot;End of central dir record&quot;, parses it and
	 * positions the stream at the first central directory record.
	 */
	private void positionAtCentralDirectory( ) throws IOException
	{
		boolean found = false;
		long off = archive.length( ) - MIN_EOCD_SIZE;
		if ( off >= 0 )
		{
			archive.seek( off );
			byte[] sig = ZipOutputStream.EOCD_SIG;
			int curr = archive.read( );
			while ( curr != -1 )
			{
				if ( curr == sig[0] )
				{
					curr = archive.read( );
					if ( curr == sig[1] )
					{
						curr = archive.read( );
						if ( curr == sig[2] )
						{
							curr = archive.read( );
							if ( curr == sig[3] )
							{
								found = true;
								break;
							}
						}
					}
				}
				archive.seek( --off );
				curr = archive.read( );
			}
		}
		if ( !found )
		{
			throw new ZipException( "archive is not a ZIP archive" );
		}

		archive.seek( off + MIN_EOCD_SIZE - 2 );
		byte[] commentOffset = new byte[2];
		archive.readFully( commentOffset );

		int commentLength = ( commentOffset[1] << 8 ) & 0xFF00;
		commentLength += ( commentOffset[0] & 0xFF );
		byte[] commentBytes = new byte[commentLength];
		archive.readFully( commentBytes );
		comment = getString( commentBytes );
	}

	/**
	 * Retrieve a String from the given bytes using the encoding set for this
	 * ZipFile.
	 * 
	 * @param bytes
	 *            the byte array to transform
	 * @return String obtained by using the given encoding
	 * @throws ZipException
	 *             if the encoding cannot be recognized.
	 */
	protected String getString( byte[] bytes ) throws ZipException
	{
		if ( encoding == null )
		{
			return new String( bytes );
		}
		else
		{
			try
			{
				return new String( bytes, encoding );
			}
			catch ( UnsupportedEncodingException uee )
			{
				throw new ZipException( uee.getMessage( ) );
			}
		}
	}
}
