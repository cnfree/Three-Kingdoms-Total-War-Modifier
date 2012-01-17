
package org.sf.feeling.sanguo.patch.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.TimeZone;
import java.util.zip.Deflater;
import java.util.zip.ZipInputStream;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.sf.feeling.sanguo.patch.Patch;
import org.sf.feeling.sanguo.patch.model.General;
import org.sf.feeling.swt.win32.extension.io.FileSystem;
import org.sf.feeling.swt.win32.extension.util.SortMap;

public class BakUtil
{

	public static final int IO_FINISH = 2 >> 7 - 1;
	public static String bakFolderPath = Patch.GAME_ROOT + "\\patch\\bak";
	public static String bakIncreaseFolderPath = Patch.GAME_ROOT
			+ "\\patch\\bak\\resources";
	public static String patchFolderPath = Patch.GAME_ROOT + "\\patch";

	public static String defalutBakFilePath = Patch.GAME_ROOT
			+ "\\patch\\bak\\default.zip";
	public static File defalutBakFile = new File( defalutBakFilePath );

	public static String defalutResourceBakFilePath = Patch.GAME_ROOT
			+ "\\patch\\bak\\resources\\default.zip";
	public static File defalutResourceBakFile = new File( defalutResourceBakFilePath );

	private static File[] bakDataFiles = new File[]{
			FileConstants.enumVnVsFile,
			FileConstants.unitFile,
			FileConstants.battleFile,
			FileConstants.buildingsFile,
			FileConstants.baowuFile,
			FileConstants.exportUnitFile,
			FileConstants.nameFile,
			FileConstants.descNamesFile,
			FileConstants.vnVsFile,
			FileConstants.stratFile,
			FileConstants.unitEnumsFile,
			FileConstants.characterFile,
			FileConstants.characterTraitFile,
			FileConstants.scriptFile,
			FileConstants.disasterFile,
			FileConstants.projectTileFile,
			FileConstants.desc_MountFile,
			FileConstants.modelStratFile,
	};

	private static FileFilter tgaFilter = new FileFilter( ) {

		public boolean accept( File file )
		{
			if ( file.isDirectory( )
					|| file.getName( ).toLowerCase( ).endsWith( ".tga" ) )
			{
				for ( int i = 0; i < file.getName( ).length( ); i++ )
				{
					char ch = file.getName( ).charAt( i );
					if ( ch >= 255 )
					{
						return false;
					}
				}
				return true;
			}
			return false;
		}
	};

	private static FileFilter ddsFilter = new FileFilter( ) {

		public boolean accept( File file )
		{
			if ( file.isDirectory( )
					|| file.getName( ).toLowerCase( ).endsWith( ".dds" ) )
			{
				for ( int i = 0; i < file.getName( ).length( ); i++ )
				{
					char ch = file.getName( ).charAt( i );
					if ( ch >= 255 )
					{
						return false;
					}
				}
				return true;
			}
			return false;
		}
	};

	public static void bakData( String comment )
	{
		if ( checkBakFolder( ) )
		{
			Calendar cal = Calendar.getInstance( TimeZone.getTimeZone( "Etc/GMT-8" ),
					Locale.CHINA );
			SimpleDateFormat format = new SimpleDateFormat( "yyyyMMddHHmmss" );
			format.setCalendar( cal );
			String currentVersion = format.format( cal.getTime( ) );
			File bakFile = new File( bakFolderPath
					+ "\\"
					+ currentVersion
					+ ".zip" );
			File bakResourceFile = new File( bakIncreaseFolderPath
					+ "\\"
					+ currentVersion
					+ ".zip" );
			try
			{
				ZipOutputStream zos = new ZipOutputStream( bakFile );
				zos.setEncoding( "GBK" );
				zos.setComment( comment );
				zipDataFiles( zos, false );
				zos.close( );
				zos = new ZipOutputStream( bakResourceFile );
				zos.setEncoding( "GBK" );
				zos.setComment( comment );
				zipIncreaseFiles( zos, false );
				zos.close( );
				setCurrentVersion( currentVersion );
			}
			catch ( IOException e )
			{
				e.printStackTrace( );
				bakFile.delete( );
			}
			Patch.getInstance( ).hideInfo( );
		}
	}

	public static File bakBugData( )
	{
		if ( checkBakFolder( ) )
		{
			File bakFile = new File( bakFolderPath + "\\bug.zip" );
			try
			{
				ZipOutputStream zos = new ZipOutputStream( bakFile );
				zos.setEncoding( "GBK" );
				zipDataFiles( zos, false );
				zos.close( );
			}
			catch ( IOException e )
			{
				e.printStackTrace( );
				bakFile.delete( );
			}
			return bakFile;
		}
		return null;
	}

	public static void bakDataAndResources( String comment, File[] resources )
	{
		if ( checkBakFolder( ) )
		{
			Calendar cal = Calendar.getInstance( TimeZone.getTimeZone( "Etc/GMT-8" ),
					Locale.CHINA );
			SimpleDateFormat format = new SimpleDateFormat( "yyyyMMddHHmmss" );
			format.setCalendar( cal );
			String currentVersion = format.format( cal.getTime( ) );
			File bakFile = new File( bakFolderPath
					+ "\\"
					+ currentVersion
					+ ".zip" );
			File bakResourceFile = new File( bakIncreaseFolderPath
					+ "\\"
					+ currentVersion
					+ ".zip" );
			try
			{
				ZipOutputStream zos = new ZipOutputStream( bakFile );
				zos.setEncoding( "GBK" );
				zos.setComment( comment );
				zipDataFiles( zos, false );
				zos.close( );
				zos = new ZipOutputStream( bakResourceFile );
				zos.setEncoding( "GBK" );
				zos.setComment( comment );
				for ( int i = 0; i < resources.length; i++ )
				{
					zipResourceFile( zos,
							resources[i].getAbsolutePath( ),
							false );
				}
				zipIncreaseFiles( zos, false );
				zos.close( );
				setCurrentVersion( currentVersion );
			}
			catch ( IOException e )
			{
				e.printStackTrace( );
				bakFile.delete( );
			}
			Patch.getInstance( ).hideInfo( );
		}
	}

	private static void zipResourceFile( ZipOutputStream zos,
			String absolutePath, boolean showInfo ) throws IOException
	{
		zipResourceFile( zos, absolutePath, tgaFilter, showInfo );
	}

	private static void zipResourceFile( java.util.zip.ZipOutputStream zos,
			String absolutePath, boolean showInfo ) throws IOException
	{
		zipResourceFile( zos, absolutePath, tgaFilter, showInfo );
	}

	private static void setCurrentVersion( String currentVersion )
	{
		Properties properties = new Properties( );
		properties.setProperty( "currentVersion", currentVersion );
		try
		{
			properties.store( new FileOutputStream( new File( patchFolderPath
					+ "\\bak.properties" ) ), null );
		}
		catch ( IOException e )
		{
			e.printStackTrace( );
		}
	}

	private static void zipIncreaseFiles( ZipOutputStream zos, boolean showInfo )
	{
		String currentVersion = getBakCurrentVersion( );
		if ( currentVersion != null )
		{
			File bakFile = new File( bakIncreaseFolderPath
					+ "\\"
					+ currentVersion
					+ ".zip" );
			if ( !bakFile.exists( ) )
				return;
			try
			{
				List resourceList = new ArrayList( );
				ZipInputStream zis = new ZipInputStream( new FileInputStream( bakFile ) );
				java.util.zip.ZipEntry zipEntry = null;
				while ( ( zipEntry = zis.getNextEntry( ) ) != null )
				{
					if ( zipEntry.getName( ).startsWith( "resources/" ) )
					{
						resourceList.add( zipEntry.getName( ) );
					}
				}
				zis.close( );

				for ( int i = 0; i < resourceList.size( ); i++ )
				{
					String entryPath = (String) resourceList.get( i );
					String filePath = Patch.GAME_ROOT
							+ "\\"
							+ entryPath.replaceAll( "resources/", "" )
									.replaceAll( "/", "\\\\" );
					File resourceFile = new File( filePath );
					if ( resourceFile.exists( )
							&& !zos.getEntries( ).contains( entryPath ) )
					{
						recursiveZip( zos,
								resourceFile,
								entryPath,
								tgaFilter,
								showInfo );
					}
				}
			}
			catch ( IOException e )
			{
				e.printStackTrace( );
			}
		}
	}

	public static String getBakCurrentVersion( )
	{
		try
		{
			File propertiesFile = new File( patchFolderPath
					+ "\\bak.properties" );
			if ( !propertiesFile.exists( ) )
				return null;
			Properties props = new Properties( );
			InputStream in = new FileInputStream( propertiesFile );
			props.load( in );
			in.close( );
			return props.getProperty( "currentVersion" );
		}
		catch ( Exception e )
		{
			SWT.error( SWT.ERROR_IO, e );
		}
		return null;
	}

	public static void bakToDefaultData( final Listener listener )
	{
		if ( checkBakToDefault( ) )
		{
			Patch.getInstance( ).showInfo( "开始准备备份", "" );
			new Thread( ) {

				public void run( )
				{
					try
					{
						if ( checkBakFolder( ) )
						{
							ZipOutputStream apacheZos = new ZipOutputStream( defalutBakFile );
							apacheZos.setComment( "游戏数据全局备份初始档" );
							apacheZos.setEncoding( "GBK" );
							zipDataFiles( apacheZos, true );
							apacheZos.close( );

							java.util.zip.ZipOutputStream zos = new java.util.zip.ZipOutputStream( new BufferedOutputStream( new FileOutputStream( defalutResourceBakFile ) ) );
							zos.setLevel( Deflater.BEST_SPEED );
							zipResourceFile( zos,
									FileConstants.customPortraitPath,
									true );

							List unitList = new ArrayList( );
							unitList.addAll( MapUtil.generalUnitMap.getKeyList( ) );
							unitList.addAll( MapUtil.soldierUnitMap.getKeyList( ) );

							final List unitNames = new ArrayList( );
							final List unitInfoNames = new ArrayList( );
							for ( int i = 0; i < unitList.size( ); i++ )
							{
								String dictionary = (String) MapUtil.unitTypeToDictionaryMap.get( unitList.get( i ) );
								unitNames.add( "#"
										+ dictionary.toLowerCase( )
										+ ".tga" );
								unitInfoNames.add( dictionary.toLowerCase( )
										+ "_info.tga" );
							}

							FileFilter unitFilter = new FileFilter( ) {

								public boolean accept( File file )
								{
									if ( file.isDirectory( ) )
									{
										return true;
									}
									else
									{
										return unitNames.contains( file.getName( )
												.toLowerCase( ) );
									}
								}
							};

							FileFilter unitInfoFilter = new FileFilter( ) {

								public boolean accept( File file )
								{
									if ( file.isDirectory( ) )
									{
										return true;
									}
									else
									{
										return unitInfoNames.contains( file.getName( )
												.toLowerCase( ) );
									}
								}
							};

							zipResourceFile( zos,
									FileConstants.uiUnitsPath,
									unitFilter,
									true );
							zipResourceFile( zos,
									FileConstants.uiUnitInfoPath,
									unitInfoFilter,
									true );
							zipResourceFile( zos,
									FileConstants.uiAncillariesPath,
									true );

							zipResourceFile( zos,
									FileConstants.captainBannerPath,
									true );
							zipResourceFile( zos,
									FileConstants.stratBannerPath,
									ddsFilter,
									true );
							zipResourceFile( zos,
									FileConstants.battleBannerPath,
									ddsFilter,
									true );

							zos.close( );
						}
					}
					catch ( IOException e )
					{
						e.printStackTrace( );
					}
					Display.getDefault( ).syncExec( new Runnable( ) {

						public void run( )
						{
							if ( Patch.getInstance( ).getShell( ).isDisposed( ) )
								return;
							Patch.getInstance( ).hideInfo( );
							Event event = new Event( );
							event.type = IO_FINISH;
							listener.handleEvent( event );
						}
					} );
				}
			}.start( );
		}
		else
		{
			if ( Patch.getInstance( ).getShell( ).isDisposed( ) )
				return;
			Patch.getInstance( ).hideInfo( );
			Event event = new Event( );
			event.type = IO_FINISH;
			listener.handleEvent( event );
		}
	}

	private static boolean checkBakFolder( )
	{
		File bakFolder = new File( bakFolderPath );
		if ( !bakFolder.exists( ) )
		{
			bakFolder.mkdirs( );
		}
		else if ( bakFolder.isFile( ) )
		{
			bakFolder.delete( );
			bakFolder.mkdirs( );
		}
		File bakIncreaseFolder = new File( bakIncreaseFolderPath );
		if ( !bakIncreaseFolder.exists( ) )
		{
			bakIncreaseFolder.mkdirs( );
		}
		else if ( bakIncreaseFolder.isFile( ) )
		{
			bakIncreaseFolder.delete( );
			bakIncreaseFolder.mkdirs( );
		}
		return bakFolder.exists( ) && bakFolder.isDirectory( );
	}

	public static void recursiveZip( Object os, File file, final String path,
			FileFilter filter, boolean showInfo ) throws FileNotFoundException,
			IOException
	{
		if ( file.isDirectory( ) )
		{
			// 如果为目录，ZipEntry名称的尾部应该以反斜杠"/"结尾
			// zos.putNextEntry(new ZipEntry(path + "/"));
			File[] files = file.listFiles( filter );
			if ( files != null )
			{
				for ( int i = 0; i < files.length; i++ )
				{
					// 进行递归，同时传递父文件ZipEntry的名称，还有压缩输出流
					recursiveZip( os,
							files[i],
							path + "/" + files[i].getName( ),
							filter,
							showInfo );
				}
			}
		}
		if ( file.isFile( ) )
		{
			if ( showInfo )
			{
				Display.getDefault( ).syncExec( new Runnable( ) {

					public void run( )
					{
						Patch.getInstance( ).showInfo( "正在备份文件：",
								path.substring( path.indexOf( "/" ) + 1 ) );
					};
				} );
			}
			byte[] bt = new byte[512];
			if ( os instanceof ZipOutputStream )
			{
				ZipOutputStream zos = (ZipOutputStream) os;
				ZipEntry ze = new ZipEntry( path );
				// 设置压缩前的文件大小
				ze.setSize( file.length( ) );
				zos.putNextEntry( ze );
				BufferedInputStream fis = new BufferedInputStream( new FileInputStream( file ) );
				int i = 0;
				while ( ( i = fis.read( bt ) ) != -1 )
				{
					zos.write( bt, 0, i );
				}
				fis.close( );
			}
			if ( os instanceof java.util.zip.ZipOutputStream )
			{
				java.util.zip.ZipOutputStream zos = (java.util.zip.ZipOutputStream) os;
				ZipEntry ze = new ZipEntry( path );
				// 设置压缩前的文件大小
				ze.setSize( file.length( ) );
				zos.putNextEntry( ze );
				BufferedInputStream fis = new BufferedInputStream( new FileInputStream( file ) );
				int i = 0;
				while ( ( i = fis.read( bt ) ) != -1 )
				{
					zos.write( bt, 0, i );
				}
				fis.close( );
			}
		}
	}

	private static void zipDataFiles( ZipOutputStream zos, boolean showInfo )
			throws FileNotFoundException, IOException
	{
		for ( int i = 0; i < bakDataFiles.length; i++ )
		{
			zipDataFile( zos, bakDataFiles[i].getAbsolutePath( ), showInfo );
		}
	}

	public static File[] getGeneralsImageFiles( String[] generals )
	{
		List list = new ArrayList( );
		SortMap generalMap = UnitUtil.getGeneralMap( );
		for ( int i = 0; i < generals.length; i++ )
		{
			Iterator iter = generalMap.getKeyList( ).iterator( );
			Object index = null;
			while ( iter.hasNext( ) )
			{
				Object key = iter.next( );
				Object value = generalMap.get( key );
				if ( generals[i] != null && generals[i].equals( value ) )
					index = key;
				if ( index != null )
					break;
			}
			String portrait = ( (General) MapUtil.generalModelMap.get( generals[i] ) ).getPortrait( );
			list.add( new File( FileConstants.customPortraitFile, portrait ) );
		}
		return (File[]) list.toArray( new File[0] );
	}

	public static void restoreBakFile( final File bakFile )
	{
		restoreBakFile( bakFile, null );
	}

	public static void restoreBakFile( final File bakFile,
			final Listener listener )
	{
		new Thread( ) {

			public void run( )
			{
				if ( bakFile.exists( ) )
				{
					if ( bakFile.getAbsolutePath( ).equals( defalutBakFilePath ) )
					{
						try
						{
							restoreFile( defalutBakFile, true );
							File file = new File( patchFolderPath
									+ "\\bak.properties" );
							if ( file.exists( ) )
								file.delete( );
						}
						catch ( IOException e )
						{
							e.printStackTrace( );
						}
					}
					else
					{
						String fileDate = bakFile.getName( ).substring( 0,
								bakFile.getName( ).indexOf( "." ) );
						String currentVersion = getBakCurrentVersion( );

						// 增量文件释放
						try
						{
							if ( currentVersion != null )
							{
								long fileNumber = Long.parseLong( fileDate );
								long currectVersionNumber = Long.parseLong( currentVersion );
								File[] increaseFiles = computeIncreaseFiles( fileNumber,
										currectVersionNumber );
								for ( int i = 0; i < increaseFiles.length; i++ )
								{
									File file = increaseFiles[i];
									restoreIncraseFile( file, true );
								}
							}
							restoreFile( bakFile, true );
							setCurrentVersion( fileDate );
						}
						catch ( IOException e )
						{
							e.printStackTrace( );
						}
					}
					MapUtil.initMap( );
				}
				Display.getDefault( ).syncExec( new Runnable( ) {

					public void run( )
					{
						if ( Patch.getInstance( ).getShell( ).isDisposed( ) )
							return;
						Patch.getInstance( ).hideInfo( );
						if ( listener != null )
						{
							Event event = new Event( );
							event.type = IO_FINISH;
							listener.handleEvent( event );
						}
					}
				} );
			}
		}.start( );
	}

	private static void restoreFile( File bakFile, boolean showInfo )
			throws IOException
	{
		File increaseFile = new File( bakIncreaseFolderPath
				+ "\\"
				+ bakFile.getName( ) );

		if ( bakFile.exists( ) )
		{
			restoreDataFile( bakFile, showInfo );
		}
		if ( increaseFile.exists( ) )
		{
			restoreIncraseFile( increaseFile, showInfo );
		}
	}

	private static void restoreResourceFile( ZipInputStream zis,
			final java.util.zip.ZipEntry entry, boolean showInfo )
	{
		if ( showInfo )
		{
			Display.getDefault( ).syncExec( new Runnable( ) {

				public void run( )
				{
					Patch.getInstance( ).showInfo( "正在还原文件：",
							entry.getName( ).substring( entry.getName( )
									.indexOf( "/" ) + 1 ) );
				};
			} );
		}
		String filePath = Patch.GAME_ROOT
				+ "\\"
				+ entry.getName( )
						.replaceAll( "resources/", "" )
						.replaceAll( "/", "\\\\" );
		File resourceFile = new File( filePath );
		if ( resourceFile.exists( ) )
		{
			FileUtil.writeToBinarayFile( resourceFile, zis, false );
		}
	}

	private static void restoreDataFile( ZipInputStream zis,
			final java.util.zip.ZipEntry entry, boolean showInfo )
	{
		if ( showInfo )
		{
			Display.getDefault( ).syncExec( new Runnable( ) {

				public void run( )
				{
					Patch.getInstance( ).showInfo( "正在还原文件：",
							entry.getName( ).substring( entry.getName( )
									.indexOf( "/" ) + 1 ) );
				};
			} );
		}
		String filePath = Patch.GAME_ROOT
				+ "\\"
				+ entry.getName( )
						.replaceAll( "datas/", "" )
						.replaceAll( "/", "\\\\" );
		File resourceFile = new File( filePath );
		if ( resourceFile.exists( ) )
		{
			FileUtil.writeToBinarayFile( resourceFile, zis, false );
		}
	}

	private static void restoreIncraseFile( File file, boolean showInfo )
			throws IOException
	{
		ZipInputStream zis = new ZipInputStream( new BufferedInputStream( new FileInputStream( file ) ) );
		java.util.zip.ZipEntry entry;
		while ( ( entry = zis.getNextEntry( ) ) != null )
		{
			if ( entry.getName( ).startsWith( "resources/" ) )
			{
				restoreResourceFile( zis, entry, showInfo );
			}
		}
		zis.close( );
	}

	private static void restoreDataFile( File file, boolean showInfo )
			throws IOException
	{
		ZipInputStream zis = new ZipInputStream( new BufferedInputStream( new FileInputStream( file ) ) );
		java.util.zip.ZipEntry entry;
		while ( ( entry = zis.getNextEntry( ) ) != null )
		{
			if ( entry.getName( ).startsWith( "datas/" ) )
			{
				restoreDataFile( zis, entry, showInfo );
			}
		}
		zis.close( );
	}

	private static File[] computeIncreaseFiles( long destVersion,
			long sourceVersion )
	{
		if ( destVersion == sourceVersion )
			return new File[0];
		File folder = new File( bakIncreaseFolderPath );
		if ( !folder.exists( ) )
			return new File[0];
		String[] children = folder.list( );
		if ( children == null || children.length == 0 )
			return new File[0];
		List versions = new ArrayList( );
		for ( int i = 0; i < children.length; i++ )
		{
			try
			{
				String version = children[i].substring( 0,
						children[i].indexOf( "." ) );
				Long.parseLong( version );
				versions.add( version );
			}
			catch ( NumberFormatException e )
			{
			}
		}
		List availableVersions = new ArrayList( );
		if ( destVersion > sourceVersion )
		{
			for ( int i = 0; i < versions.size( ); i++ )
			{
				String version = (String) versions.get( i );
				long versionNumber = Long.parseLong( version );
				if ( versionNumber >= sourceVersion
						&& versionNumber < destVersion )
				{
					availableVersions.add( version );
				}
			}
			Collections.sort( availableVersions, new Comparator( ) {

				public int compare( Object o1, Object o2 )
				{
					long result = Long.parseLong( (String) o1 )
							- Long.parseLong( (String) o2 );
					if ( result > 0 )
						return 1;
					else if ( result == 0 )
						return 0;
					else
						return -1;
				}
			} );
		}
		else
		{
			for ( int i = 0; i < versions.size( ); i++ )
			{
				String version = (String) versions.get( i );
				long versionNumber = Long.parseLong( version );
				if ( versionNumber <= sourceVersion
						&& versionNumber > destVersion )
				{
					availableVersions.add( version );
				}
			}
			Collections.sort( availableVersions, new Comparator( ) {

				public int compare( Object o1, Object o2 )
				{
					long result = Long.parseLong( (String) o2 )
							- Long.parseLong( (String) o1 );
					if ( result > 0 )
						return 1;
					else if ( result == 0 )
						return 0;
					else
						return -1;
				}
			} );
		}

		List availableFiles = new ArrayList( );
		for ( int i = 0; i < availableVersions.size( ); i++ )
		{
			String version = (String) availableVersions.get( i );
			File bakFile = new File( bakIncreaseFolderPath
					+ "\\"
					+ version
					+ ".zip" );
			if ( bakFile.exists( ) )
			{
				availableFiles.add( bakFile );
			}
		}
		return (File[]) availableFiles.toArray( new File[0] );
	}

	public static void restoreDefaultBakFile( final Listener listener )
	{
		restoreBakFile( defalutBakFile, listener );
	}

	public static File getCurrentVersionBakFile( )
	{
		return new File( bakFolderPath
				+ "\\"
				+ getBakCurrentVersion( )
				+ ".zip" );
	}

	public static void restoreCurrectVersionBakFile( )
	{
		File bakFile = new File( bakFolderPath
				+ "\\"
				+ getBakCurrentVersion( )
				+ ".zip" );
		try
		{
			if ( bakFile.exists( ) )
				restoreFile( bakFile, false );
		}
		catch ( IOException e )
		{
			e.printStackTrace( );
		}
		MapUtil.initMap( );
	}

	public static void checkDefaultBak( )
	{
		if ( Patch.getInstance( ).getShell( ).isDisposed( ) )
			return;
		if ( !defalutBakFile.exists( ) )
		{
			if ( MessageDialog.openConfirm( Patch.getInstance( ).getShell( ),
					"提示",
					"修改器检测到您尚未设置游戏数据备份原始档，您是否愿意现在开始设置？" ) )
			{
				Patch.getInstance( ).select( Patch.getInstance( )
						.getPageCount( ) - 5 );
			}
		}
		else if ( !defalutResourceBakFile.exists( ) )
		{
			if ( MessageDialog.openConfirm( Patch.getInstance( ).getShell( ),
					"提示",
					"修改器检测到您的游戏数据备份原始档与当前版本不兼容，需要重新设置。您是否愿意现在开始设置？" ) )
			{
				deleteDictionary( new File( bakFolderPath ) );
				File propertiesFile = new File( patchFolderPath
						+ "\\bak.properties" );
				if ( propertiesFile.exists( ) )
					propertiesFile.delete( );
				Patch.getInstance( ).select( Patch.getInstance( )
						.getPageCount( ) - 5 );
			}
		}
		else
		{
			new Thread( ) {

				public void run( )
				{
					try
					{
						final List loseFileList = new ArrayList( );
						loseFileList.addAll( Arrays.asList( bakDataFiles ) );
						ZipInputStream zis = new ZipInputStream( new FileInputStream( defalutBakFile ) );
						java.util.zip.ZipEntry zipEntry = null;
						while ( ( zipEntry = zis.getNextEntry( ) ) != null )
						{
							for ( int i = 0; i < bakDataFiles.length; i++ )
							{
								checkZipFile( zipEntry,
										bakDataFiles[i],
										loseFileList );
							}
						}
						zis.close( );
						Display.getDefault( ).syncExec( new Runnable( ) {

							public void run( )
							{
								if ( loseFileList.size( ) > 0 )
								{
									syncDefaultBakFile( loseFileList );
								}
							}
						} );

					}
					catch ( IOException e )
					{
						e.printStackTrace( );
					}
				}
			}.start( );
		}
	}

	protected static void checkZipFile( java.util.zip.ZipEntry zipEntry,
			File file, List loseFileList )
	{
		if ( file.getAbsolutePath( )
				.substring( Patch.GAME_ROOT.getAbsolutePath( ).length( ) )
				.replaceAll( "\\\\", "/" )
				.equalsIgnoreCase( zipEntry.getName( ).replaceAll( "datas", "" ) ) )
		{
			loseFileList.remove( file );
		}
	}

	public static boolean checkBakToDefault( )
	{
		if ( defalutBakFile.exists( ) )
		{
			if ( MessageDialog.openConfirm( Patch.getInstance( ).getShell( ),
					"提示",
					"修改器检查到您已经设置过游戏数据备份原始档！您是否要继续操作，设置当前游戏数据作为备份原始档？" ) )
			{
				return true;
			}
			else
				return false;
		}
		else
			return true;
	}

	private static void syncDefaultBakFile( final List loseFileList )
	{
		MessageDialog.openWarning( Patch.getInstance( ).getShell( ),
				"提示",
				"修改器检查到您设置的游戏数据备份原始档已经过期，现在开始同步您的备份原始档！" );
		new Thread( ) {

			public void run( )
			{
				try
				{
					File tempFile = new File( FileSystem.getTempPath( )
							+ "\\"
							+ System.currentTimeMillis( ) );
					tempFile.mkdirs( );
					if ( tempFile.exists( ) && tempFile.isDirectory( ) )
					{
						ZipInputStream zis = new ZipInputStream( new BufferedInputStream( new FileInputStream( defalutBakFile ) ) );
						java.util.zip.ZipEntry entry;
						while ( ( entry = zis.getNextEntry( ) ) != null )
						{
							File file = new File( tempFile
									+ "\\"
									+ entry.getName( ) );
							if ( entry.isDirectory( ) )
								file.mkdirs( );
							else
							{
								if ( !file.getParentFile( ).exists( ) )
									file.getParentFile( ).mkdirs( );
								FileUtil.writeToBinarayFile( file, zis, false );
							}
						}
						zis.close( );

						for ( int i = 0; i < loseFileList.size( ); i++ )
						{
							File file = (File) loseFileList.get( i );
							File tempLostFile = new File( tempFile
									+ "\\datas"
									+ file.getAbsolutePath( )
											.substring( Patch.GAME_ROOT.getAbsolutePath( )
													.length( ) ) );
							if ( !tempLostFile.getParentFile( ).exists( ) )
								tempLostFile.getParentFile( ).mkdirs( );
							FileUtil.writeToBinarayFile( tempLostFile,
									new FileInputStream( file ),
									false );
						}
						File tempZipFile = new File( defalutBakFilePath
								+ ".tmp" );
						ZipOutputStream zos = new ZipOutputStream( tempZipFile );
						zos.setComment( "游戏数据全局备份初始档" );
						File[] files = tempFile.listFiles( );
						for ( int i = 0; i < files.length; i++ )
						{
							recursiveZip( zos,
									files[i],
									files[i].getAbsolutePath( )
											.substring( tempFile.getAbsolutePath( )
													.length( ) + 1 )
											.replaceAll( "\\\\", "/" ),
									null,
									true );
						}
						zos.close( );
						defalutBakFile.delete( );
						tempZipFile.renameTo( defalutBakFile );
						deleteDictionary( tempFile );
					}
				}
				catch ( IOException e )
				{
					e.printStackTrace( );
				}
				Display.getDefault( ).syncExec( new Runnable( ) {

					public void run( )
					{
						Patch.getInstance( ).hideInfo( );
					}
				} );
			}
		}.start( );
	}

	private static void deleteDictionary( File file )
	{
		if ( file.exists( ) )
		{
			File[] files = file.listFiles( );
			if ( files.length > 0 )
			{
				for ( int i = 0; i < files.length; i++ )
				{
					File child = files[i];
					if ( child.exists( ) )
					{
						if ( child.isDirectory( ) )
						{
							deleteDictionary( child );
						}
						else
						{
							child.delete( );
						}
					}
				}
			}
			file.delete( );
		}
	}

	private static void zipResourceFile( ZipOutputStream zos, String filePath,
			FileFilter filter, boolean showInfo ) throws FileNotFoundException,
			IOException
	{
		recursiveZip( zos,
				new File( filePath ),
				"resources"
						+ filePath.substring( Patch.GAME_ROOT.getAbsolutePath( )
								.length( ) ).replaceAll( "\\\\", "/" ),
				filter,
				showInfo );
	}

	private static void zipResourceFile( java.util.zip.ZipOutputStream zos,
			String filePath, FileFilter filter, boolean showInfo )
			throws FileNotFoundException, IOException
	{
		recursiveZip( zos,
				new File( filePath ),
				"resources"
						+ filePath.substring( Patch.GAME_ROOT.getAbsolutePath( )
								.length( ) ).replaceAll( "\\\\", "/" ),
				filter,
				showInfo );
	}

	private static void zipDataFile( ZipOutputStream zos, String filePath,
			boolean showInfo ) throws FileNotFoundException, IOException
	{
		recursiveZip( zos,
				new File( filePath ),
				"datas"
						+ filePath.substring( Patch.GAME_ROOT.getAbsolutePath( )
								.length( ) ).replaceAll( "\\\\", "/" ),
				null,
				showInfo );
	}

	public static File[] getUnitImageFiles( String faction, String unitType )
	{
		File[] imageFiles = new File[2];
		String dictionary = (String) MapUtil.unitTypeToDictionaryMap.get( unitType );

		String bigFilePath = Patch.GAME_ROOT
				+ "\\alexander\\data\\ui\\unit_info\\"
				+ faction
				+ "\\"
				+ dictionary
				+ "_info.tga";
		String smallFilePath = Patch.GAME_ROOT
				+ "\\alexander\\data\\ui\\units\\"
				+ faction
				+ "\\#"
				+ dictionary
				+ ".tga";

		imageFiles[0] = new File( bigFilePath );
		imageFiles[1] = new File( smallFilePath );

		return imageFiles;
	}
}
