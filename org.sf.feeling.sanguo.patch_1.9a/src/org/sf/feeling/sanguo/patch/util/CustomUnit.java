
package org.sf.feeling.sanguo.patch.util;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.graphics.ImageData;
import org.sf.feeling.sanguo.patch.Patch;
import org.sf.feeling.sanguo.patch.model.Unit;
import org.sf.feeling.swt.win32.extension.graphics.TgaLoader;

public class CustomUnit
{

	private int buildingLevel = -1;

	private String description = null;

	private String displayName;

	private boolean isGeneralUnit = true;

	private boolean isSpecialGeneralUnit = true;

	private String name;

	private Unit soldier;

	private ImageData soldierCardImage;

	private String soldierDictionary;

	private ImageData soldierImage;

	private String soldierType;

	private String unitFaction;

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
		setSoldierBuilding( );
	}

	public int getBuildingLevel( )
	{
		return buildingLevel;
	}

	public String getDescription( )
	{
		return description;
	}

	public String getFaction( )
	{
		return unitFaction;
	}

	public ImageData getSoldierCardImage( )
	{
		return soldierCardImage;
	}

	public boolean isGeneralUnit( )
	{
		return isGeneralUnit;
	}

	public boolean isSpecialGeneralUnit( )
	{
		return isSpecialGeneralUnit;
	}

	public void setBuildingLevel( int buildingLevel )
	{
		this.buildingLevel = buildingLevel;
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

	public void setGeneralUnit( boolean isGeneralUnit )
	{
		this.isGeneralUnit = isGeneralUnit;
	}

	public void setName( String name )
	{
		this.name = name;
	}

	public void setSoldier( Unit soldier )
	{
		this.soldier = soldier;
	}

	private void setSoldierBuilding( )
	{
		String[] buildings = null;
		if ( "missile".equals( soldier.getUnitClass( ) )
				|| "siege".equals( soldier.getCategory( ) ) )
		{
			buildings = new String[]{
					"practice_field",
					"archery_range",
					"catapult_range",
					"siege_engineer"
			};
		}
		else if ( "infantry".equals( soldier.getCategory( ) )
				|| "handler".equals( soldier.getCategory( ) ) )
		{
			buildings = new String[]{
					"muster_field",
					"militia_barracks",
					"city_barracks",
					"army_barracks",
					"royal_barracks"
			};
		}
		else if ( "cavalry".equals( soldier.getCategory( ) ) )
		{
			buildings = new String[]{
					"stables",
					"cavalry_barracks",
					"hippodrome",
					"circus_maximus"
			};
		}
		else if ( "ship".equals( soldier.getCategory( ) ) )
		{
			buildings = new String[]{
					"port", "shipwright", "dockyard"
			};
		}
		else if ( "non_combatant".equals( soldier.getCategory( ) ) )
		{
			buildings = new String[]{
					"governors_house",
					"governors_villa",
					"governors_palace",
					"proconsuls_palace",
					"imperial_palace"
			};
		}
		if ( buildings != null )
		{
			try
			{
				if ( buildingLevel > 0 )
				{
					List list = new ArrayList( );
					for ( int i = buildingLevel; i < buildings.length; i++ )
					{
						list.add( buildings[i] );
					}
					if ( list.size( ) > 0 )
					{
						UnitUtil.addUnitToBuildings( soldierType,
								unitFaction,
								(String[]) list.toArray( new String[0] ) );
					}
				}
				else
				{
					if ( ( !isGeneralUnit && buildingLevel == -1 )
							|| buildingLevel == 0 )
					{
						UnitUtil.addUnitToBuildings( soldierType,
								unitFaction,
								buildings );
					}
				}
			}
			catch ( IOException e )
			{
				e.printStackTrace( );
			}
		}
	}

	public void setSoldierCardImage( ImageData soldierCardImage )
	{
		this.soldierCardImage = soldierCardImage;
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

			String short_description = isSpecialGeneralUnit ? displayName
					+ "麾下的精銳親兵。" : displayName;
			String long_description = displayName + "麾下的精銳親兵。\\n\\n";
			long_description += ( displayName + "的部下隨" + displayName + "征戰四方。\\n" );

			if ( !isSpecialGeneralUnit )
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
						soldierImage.scaledTo( 160, 210 ) );

				if ( !isSpecialGeneralUnit )
				{
					if ( soldierCardImage != null )
					{
						TgaLoader.saveImage( new FileOutputStream( smallFilePath ),
								soldierCardImage.scaledTo( 48, 64 ) );
					}
					else
					{
						TgaLoader.saveImage( new FileOutputStream( smallFilePath ),
								soldierImage.scaledTo( 48, 64 ) );
					}
				}
			}
			catch ( IOException e )
			{
				e.printStackTrace( );
			}
		}
	}

	public void setSoldierImage( ImageData soldierImage )
	{
		this.soldierImage = soldierImage;
	}

	public void setSpecialGeneralUnit( boolean isSpecialGeneralUnit )
	{
		this.isSpecialGeneralUnit = isSpecialGeneralUnit;
	}
}
