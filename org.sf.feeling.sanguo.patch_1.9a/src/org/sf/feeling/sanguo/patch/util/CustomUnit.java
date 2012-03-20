
package org.sf.feeling.sanguo.patch.util;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.List;

import org.eclipse.swt.graphics.ImageData;
import org.sf.feeling.sanguo.patch.Patch;
import org.sf.feeling.sanguo.patch.model.Unit;
import org.sf.feeling.swt.win32.extension.graphics.GraphicsUtil;
import org.sf.feeling.swt.win32.extension.graphics.TgaLoader;

public class CustomUnit
{

	private String description = null;

	private String displayName;

	private String unitFaction;

	private String name;

	private Unit soldier;

	private String soldierDictionary;

	private ImageData soldierImage;

	private ImageData soldierCardImage;

	private String soldierType;

	private boolean isGeneralUnit;

	public boolean isGeneralUnit( )
	{
		return isGeneralUnit;
	}

	public void setGeneralUnit( boolean isGeneralUnit )
	{
		this.isGeneralUnit = isGeneralUnit;
	}

	public void createCustomUtil( )
	{
		generalCustomSoldier( );
	}

	private void generalCustomSoldier( )
	{
		soldierType = "Custom " + name;
		soldierDictionary = soldierType.replaceAll( "(\\s+)", "_" );

		String soldierModel = soldier.getSoldier( )[0];
		List officerTypes = soldier.getOfficers( );
		try
		{
			UnitUtil.modifyBattleFile( unitFaction, soldierModel );
			if ( officerTypes != null && officerTypes.size( ) > 0 )
			{
				for ( int i = 0; i < officerTypes.size( ); i++ )
				{
					UnitUtil.modifyBattleFile( unitFaction,
							(String) officerTypes.get( i ) );
				}
			}
		}
		catch ( IOException e1 )
		{
			e1.printStackTrace( );
		}

		String horse = soldier.getMount( );
		if ( horse != null )
		{
			horse = (String) UnitUtil.getMountTypeToModelMap( ).get( horse );
			if ( horse != null )
			{
				try
				{
					UnitUtil.modifyBattleFile( unitFaction, horse );
				}
				catch ( IOException e )
				{
					e.printStackTrace( );
				}
			}
		}

		if ( isGeneralUnit )
		{
			if ( !soldier.getAttributes( ).contains( "general_unit" ) )
				soldier.getAttributes( ).add( "general_unit" );
			if ( !soldier.getAttributes( ).contains( "no_custom" ) )
				soldier.getAttributes( ).add( "no_custom" );
		}
		else
		{
			soldier.getAttributes( ).remove( "general_unit" );
			soldier.getAttributes( ).remove( "no_custom" );
		}

		UnitParser.createSoldier( soldier,
				this.soldierType,
				soldierDictionary,
				unitFaction );
		setSoldierDescription( soldierDictionary, displayName, description );
		setSoldierImage( );
	}

	public String getDescription( )
	{
		return description;
	}

	public String getFaction( )
	{
		return unitFaction;
	}

	public void setDescription( String description )
	{
		this.description = description;
	}

	public void setDisplayName( String displayName )
	{
		this.displayName = ChangeCode.Change( displayName, true );
	}

	public void setFaction( String faction )
	{
		this.unitFaction = faction;
	}

	public void setSoldier( Unit soldier )
	{
		this.soldier = soldier;
	}

	public void setSoldierImage( ImageData soldierImage )
	{
		this.soldierImage = soldierImage;
	}

	public void setName( String name )
	{
		this.name = name;
	}

	private void setSoldierDescription( String soldierDictionary,
			String displayName, String description )
	{

		try
		{
			PrintWriter out = new PrintWriter( new BufferedWriter( new OutputStreamWriter( new FileOutputStream( FileConstants.exportUnitFile,
					true ),
					"UTF-16LE" ) ),
					false );
			out.println( );

			String short_description = isGeneralUnit ? displayName + "麾下的精銳親兵。"
					: displayName;
			String long_description = displayName + "麾下的精銳親兵。\\n\\n";
			long_description += ( displayName + "的部下隨" + displayName + "征戰四方。\\n" );

			if ( !isGeneralUnit )
				long_description = displayName + "。\\n";

			if ( description != null && description.length( ) > 0 )
				long_description = ChangeCode.toShort( description );
			out.println( "{" + soldierDictionary + "}" + displayName );
			out.println( "{" + soldierDictionary + "_descr}" + long_description );
			out.println( "{"
					+ soldierDictionary
					+ "_descr_short}"
					+ short_description );

			out.close( );
		}
		catch ( IOException e )
		{
			e.printStackTrace( );
		}

		try
		{
			PrintWriter out = new PrintWriter( new BufferedWriter( new OutputStreamWriter( new FileOutputStream( FileConstants.unitEnumsFile,
					true ),
					"GBK" ) ),
					false );

			out.println( );
			out.println( soldierDictionary );
			out.println( soldierDictionary + "_descr" );
			out.println( soldierDictionary + "_descr_short" );

			out.close( );
		}
		catch ( IOException e )
		{
			e.printStackTrace( );
		}
	}

	private void setSoldierImage( )
	{
		String bigFilePath = Patch.GAME_ROOT
				+ "\\alexander\\data\\ui\\unit_info\\"
				+ unitFaction
				+ "\\"
				+ soldierDictionary
				+ "_info.tga";
		String smallFilePath = Patch.GAME_ROOT
				+ "\\alexander\\data\\ui\\units\\"
				+ unitFaction
				+ "\\#"
				+ soldierDictionary
				+ ".tga";
		if ( soldierImage != null )
		{
			try
			{
				TgaLoader.saveImage( new FileOutputStream( bigFilePath ),
						GraphicsUtil.resizeImage( soldierImage, 160, 210, true ) );

				if ( !isGeneralUnit )
				{
					if ( soldierCardImage != null )
					{
						TgaLoader.saveImage( new FileOutputStream( smallFilePath ),
								GraphicsUtil.resizeImage( soldierCardImage,
										48,
										64,
										true ) );
					}
					else
					{
						TgaLoader.saveImage( new FileOutputStream( smallFilePath ),
								GraphicsUtil.resizeImage( soldierImage,
										48,
										64,
										true ) );
					}
				}
			}
			catch ( IOException e )
			{
				e.printStackTrace( );
			}
		}
	}

	public ImageData getSoldierCardImage( )
	{
		return soldierCardImage;
	}

	public void setSoldierCardImage( ImageData soldierCardImage )
	{
		this.soldierCardImage = soldierCardImage;
	}
}
