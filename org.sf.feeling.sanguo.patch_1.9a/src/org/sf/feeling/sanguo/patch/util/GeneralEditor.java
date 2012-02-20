
package org.sf.feeling.sanguo.patch.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.sf.feeling.sanguo.patch.Patch;
import org.sf.feeling.sanguo.patch.model.General;
import org.sf.feeling.sanguo.patch.model.Unit;
import org.sf.feeling.swt.win32.extension.graphics.TgaLoader;
import org.sf.feeling.swt.win32.extension.util.SortMap;

public class GeneralEditor
{

	private String posX;
	private String posY;

	public void setPosX( String posX )
	{
		this.posX = posX;
	}

	public void setPosY( String posY )
	{
		this.posY = posY;
	}

	private String name;

	public void setName( String name )
	{
		this.name = name;
	}

	private String displayName;

	public void setDisplayName( String displayName )
	{
		this.displayName = ChangeCode.Change( displayName, true );
	}

	private String general;

	public void setGeneral( String general )
	{
		this.general = general;
	}

	private ImageData soldierImage;

	public void setGeneralSoldierImage( ImageData soldierImage )
	{
		this.soldierImage = soldierImage;
	}

	private ImageData[] generalImages;

	public void setGeneralImages( ImageData[] generalImages )
	{
		this.generalImages = generalImages;
	}

	private Unit soldier;

	public void setGeneralSoldier( Unit soldier )
	{
		this.soldier = soldier;
	}

	private SortMap skills;

	public void setGeneralSkills( SortMap skills )
	{
		this.skills = skills;
	}

	private String generalDescription;

	public void setGeneralDescription( String generalDescription )
	{
		this.generalDescription = generalDescription;
	}

	private String soldierType;
	private String soldierDictionary;
	private String generalFaction;

	public void editGeneral( )
	{
		SortMap generaUnitMap = UnitUtil.getAvailableGeneralUnits( );
		SortMap generalMap = UnitUtil.getGenerals( );
		String generalUnit = UnitUtil.getGeneralUnitType( general );
		generalFaction = UnitUtil.getGeneralFaction( general );

		if ( !generaUnitMap.containsKey( generalUnit ) && soldier != null )
		{
			soldierType = "Custom " + name;
			soldierDictionary = soldierType.replaceAll( "(\\s+)", "_" );
			String soldierModel = soldier.getSoldier( )[0];
			List officerTypes = soldier.getOfficers( );
			try
			{
				UnitUtil.modifyBattleFile( generalFaction, soldierModel );
				if ( officerTypes != null && officerTypes.size( ) > 0 )
				{
					for ( int i = 0; i < officerTypes.size( ); i++ )
					{
						UnitUtil.modifyBattleFile( generalFaction,
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
						UnitUtil.modifyBattleFile( generalFaction, horse );
					}
					catch ( IOException e )
					{
						e.printStackTrace( );
					}
				}
			}

			if ( !soldier.getAttributes( ).contains( "general_unit" ) )
				soldier.getAttributes( ).add( "general_unit" );
			if ( !soldier.getAttributes( ).contains( "no_custom" ) )
				soldier.getAttributes( ).add( "no_custom" );
			UnitParser.createSoldier( soldier,
					this.soldierType,
					soldierDictionary,
					generalFaction );
			setSoldierDescription( soldierDictionary, displayName );
			UnitUtil.changeGeneralSoldier( general, soldierType );
			setSoldierImage( );
		}
		else if ( soldier != null )
		{
			String soldierModel = soldier.getSoldier( )[0];
			List officerTypes = soldier.getOfficers( );
			try
			{
				UnitUtil.modifyBattleFile( generalFaction, soldierModel );
				if ( officerTypes != null && officerTypes.size( ) > 0 )
				{
					for ( int i = 0; i < officerTypes.size( ); i++ )
					{
						UnitUtil.modifyBattleFile( generalFaction,
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
						UnitUtil.modifyBattleFile( generalFaction, horse );
					}
					catch ( IOException e )
					{
						e.printStackTrace( );
					}
				}
			}

			if ( !soldier.getAttributes( ).contains( "general_unit" ) )
				soldier.getAttributes( ).add( "general_unit" );
			if ( !soldier.getAttributes( ).contains( "no_custom" ) )
				soldier.getAttributes( ).add( "no_custom" );

			Unit oldSoldier = UnitParser.getUnit( generalUnit );
			soldierType = oldSoldier.getType( );
			soldier.setType( oldSoldier.getType( ) );
			soldier.setDictionary( oldSoldier.getDictionary( ) );
			soldierDictionary = oldSoldier.getDictionary( );
			soldier.setFactions( oldSoldier.getFactions( ) );
			UnitParser.saveSoldier( soldier );

			if ( soldierImage != null )
				setSoldierImage( );
		}

		if ( generalImages != null )
			changeGeneralImages( );
		changeGeneralDescription( );

		String generalName = (String) generalMap.get( general );
		if ( generalName != null && generalName.indexOf( "（" ) != -1 )
			generalName = generalName.substring( 0, generalName.indexOf( "（" ) );

		if ( !ChangeCode.toLong( displayName )
				.equals( ChangeCode.toLong( generalName ) ) )
		{
			changeGeneralName( );
		}

		changeGeneralModels( );
		changeGeneralSkills( );
		changeGeneralBaowus( );
		changeGeneralPositions( );
	}

	private void changeGeneralModels( )
	{
		if ( generalModel == null && battleModel == null )
			return;
		GeneralParser.setGeneralModels( general, generalModel, battleModel );
		if ( generalModel != null )
		{
			try
			{
				String casFile = UnitUtil.modifyBattleFile( generalFaction,
						generalModel );
				if ( casFile != null )
				{
					UnitUtil.modifyStratFile( generalModel, casFile );
				}
				if ( !generalModel.equals( battleModel ) )
				{
					if ( battleModel != null )
					{
						UnitUtil.modifyBattleFile( generalFaction, battleModel );
					}
				}
			}
			catch ( IOException e )
			{
				e.printStackTrace( );
			}
		}
	}

	private void changeGeneralPositions( )
	{
		if ( posX == null || posY == null )
			return;
		GeneralParser.setGeneralPosition( general, posX, posY );
	}

	private void changeGeneralBaowus( )
	{
		if ( general != null && baowus != null )
		{
			Map baowuMap = new HashMap( );
			Object[] allBaowus = BaowuParser.getBaowuInfos( )
					.entrySet( )
					.toArray( );
			List availableBaowus = new ArrayList( );
			List baowuList = Arrays.asList( baowus );
			for ( int i = 0; i < allBaowus.length; i++ )
			{
				Entry entry = (Entry) allBaowus[i];
				if ( baowuList.contains( (String) entry.getKey( ) ) )
				{
					availableBaowus.add( entry );
				}
			}
			for ( int i = 0; i < availableBaowus.size( ); i++ )
			{
				Entry entry = (Entry) availableBaowus.get( i );
				if ( entry.getValue( ) != null
						&& !general.equals( entry.getValue( ) ) )
				{
					if ( !baowuMap.containsKey( entry.getValue( ) ) )
					{
						baowuMap.put( entry.getValue( ), new ArrayList( ) );
					}
					( (List) baowuMap.get( entry.getValue( ) ) ).add( entry.getKey( ) );
				}
			}
			GeneralParser.removeBaowu( baowuMap );
			GeneralParser.setGeneralBaowus( general, baowus );
		}
	}

	private void changeGeneralName( )
	{
		if ( soldierType == null )
		{
			String unitType = UnitUtil.getGeneralUnitType( general );
			if ( UnitUtil.getGeneralUnits( ).containsKey( unitType ) )
			{
				String unitName = (String) UnitUtil.getGeneralUnits( )
						.get( unitType );
				if ( !MapUtil.isNormalGeneral( unitName ) )
				{
					UnitUtil.setUnitName( unitType, displayName );
				}
			}
		}
		UnitUtil.setGeneralDescriptionName( general, displayName );
		GeneralParser.setGeneralName( general, displayName );
	}

	private void changeGeneralSkills( )
	{
		GeneralParser.setGeneralSkills( general, skills );
	}

	private void changeGeneralDescription( )
	{
		String[] oldJueweis = GeneralParser.getGeneralJueweis( (String) GeneralParser.getGeneralSkills( general )
				.getKeyList( )
				.get( 2 ) );
		if ( jueweis == null || oldJueweis.length > jueweis.length )
		{
			jueweis = oldJueweis;
		}
		else
		{
			for ( int i = 0; i < jueweis.length; i++ )
			{
				if ( jueweis[i] == null )
				{
					jueweis = oldJueweis;
					break;
				}
			}
		}
		if ( generalDescription == null
				|| generalDescription.trim( ).length( ) == 0 )
			generalDescription = null;
		GeneralParser.setGeneralJueweis( (String) GeneralParser.getGeneralSkills( general )
				.getKeyList( )
				.get( 2 ),
				jueweis,
				generalDescription );
	}

	private void changeGeneralImages( )
	{
		String portrait = ( (General) UnitUtil.getGeneralModels( )
				.get( general ) ).getPortrait( );
		ImageData smallImageData = null;
		ImageData bigImageData = null;
		if ( generalImages[0] != null )
		{
			bigImageData = generalImages[0].scaledTo( 69, 96 );
		}
		if ( generalImages[1] != null )
		{
			smallImageData = generalImages[1].scaledTo( 44, 63 );
		}
		try
		{
			{
				Image image = new Image( null, smallImageData );
				Image grayImage = new Image( null, image, SWT.IMAGE_GRAY );
				ImageData grayImageData = grayImage.getImageData( );
				image.dispose( );
				grayImage.dispose( );
				TgaLoader.saveImage( new FileOutputStream( new File( FileConstants.customPortraitPath
						+ "\\"
						+ portrait
						+ "\\card_dead.tga" ) ),
						grayImageData );
				TgaLoader.saveImage( new FileOutputStream( new File( FileConstants.customPortraitPath
						+ "\\"
						+ portrait
						+ "\\card_young.tga" ) ),
						smallImageData );
				TgaLoader.saveImage( new FileOutputStream( new File( FileConstants.customPortraitPath
						+ "\\"
						+ portrait
						+ "\\card_old.tga" ) ),
						smallImageData );
			}
			{
				Image image = new Image( null, bigImageData );
				Image grayImage = new Image( null, image, SWT.IMAGE_GRAY );
				ImageData grayImageData = grayImage.getImageData( );
				image.dispose( );
				grayImage.dispose( );
				TgaLoader.saveImage( new FileOutputStream( new File( FileConstants.customPortraitPath
						+ "\\"
						+ portrait
						+ "\\portrait_dead.tga" ) ),
						grayImageData );
				TgaLoader.saveImage( new FileOutputStream( new File( FileConstants.customPortraitPath
						+ "\\"
						+ portrait
						+ "\\portrait_young.tga" ) ),
						bigImageData );
				TgaLoader.saveImage( new FileOutputStream( new File( FileConstants.customPortraitPath
						+ "\\"
						+ portrait
						+ "\\portrait_old.tga" ) ),
						bigImageData );
			}
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
				+ generalFaction
				+ "\\"
				+ soldierDictionary
				+ "_info.tga";
		String smallFilePath = Patch.GAME_ROOT
				+ "\\alexander\\data\\ui\\units\\"
				+ generalFaction
				+ "\\#"
				+ soldierDictionary
				+ ".tga";
		if ( soldierImage != null )
		{
			try
			{
				TgaLoader.saveImage( new FileOutputStream( bigFilePath ),
						soldierImage.scaledTo( 160, 210 ) );
				TgaLoader.saveImage( new FileOutputStream( smallFilePath ),
						soldierImage.scaledTo( 48, 64 ) );
			}
			catch ( IOException e )
			{
				e.printStackTrace( );
			}
		}
	}

	private void setSoldierDescription( String soldierDictionary,
			String displayName )
	{

		try
		{
			PrintWriter out = new PrintWriter( new BufferedWriter( new OutputStreamWriter( new FileOutputStream( FileConstants.exportUnitFile,
					true ),
					"UTF-16LE" ) ),
					false );
			out.println( );

			String short_description = displayName + "麾下的精銳親兵。";

			String long_description = displayName + "麾下的精銳親兵。\\n\\n";

			List spec = new ArrayList( );
			if ( skills.containsKey( "Jn1001" )
					&& Integer.parseInt( (String) skills.get( "Jn1001" ) ) > 2 )
			{
				spec.add( "驍勇異常，勇貫三軍" );
			}

			if ( skills.containsKey( "Jn3001" )
					|| Integer.parseInt( (String) skills.get( "Jn3000" ) ) > 6 )
			{
				spec.add( "知兵善戰，治軍嚴整" );
			}

			if ( spec.size( ) > 0 )
				long_description += ( displayName + "，" );
			for ( int i = 0; i < spec.size( ); i++ )
			{
				long_description += (String) spec.get( i );
				if ( i < spec.size( ) - 1 )
					long_description += "，";
				else
					long_description += "。";
			}

			if ( long_description.indexOf( "。" ) > -1 )
				long_description += "\\n";
			long_description += ( displayName + "的部下隨" + displayName + "征戰四方。\\n" );

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

	private String[] baowus;

	public void setGeneralBaowus( String[] baowus )
	{
		this.baowus = baowus;
	}

	private String[] jueweis;

	public void setGeneralJueweis( String[] jueweis )
	{
		this.jueweis = jueweis;
	}

	private String generalModel;
	private String battleModel;

	public void setGeneralModel( String generalModel, String battleModel )
	{
		this.generalModel = generalModel;
		this.battleModel = battleModel;
	}

}
