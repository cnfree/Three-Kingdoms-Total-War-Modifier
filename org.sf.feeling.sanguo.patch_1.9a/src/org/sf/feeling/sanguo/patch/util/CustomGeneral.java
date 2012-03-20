
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
import java.util.Random;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.sf.feeling.sanguo.patch.Patch;
import org.sf.feeling.sanguo.patch.model.Unit;
import org.sf.feeling.swt.win32.extension.graphics.GraphicsUtil;
import org.sf.feeling.swt.win32.extension.graphics.TgaLoader;
import org.sf.feeling.swt.win32.extension.util.SortMap;

public class CustomGeneral
{

	private final static String[] descriptions = {
			"少時好詩書，習弓馬。先后擔任都尉、校尉、偏將軍、中郎將、太守等職。",
			"少有壯節，威勇果敢，被太守推薦為孝廉，后擔任校尉、中郎將等職。其人意在儒雅，雖有軍事，好學不倦。",
			"少年時就很有才干，才名昭顯，為官后清廉守己，為朝中重臣。",
			"少時博學多通，后受重用，先后參與了軍政各項方案的制定。在任期間提出了多項安邊興國的建議，均為朝廷采納。",
			"個性審慎多思慮，有勇有謀又公私分明，治軍紀律嚴明，曾在多次戰斗立下功勞。死后，其子繼承了爵位。",
			"初為郡丞，行太守事，后不斷升遷，所在多有治績。為人嚴肅，少言語，時人對其多有忌憚。為官十數年，多進良言，有功于國家。",
			"其人頗有膽略和武勇，曾鎮守邊關。其熟知地理地勢，在敵軍必經之路設伏，在敵軍中埋伏后，其率眾奮勇殺敵，最后大敗敵軍。",
			"有膽略，暢軍事，為太守，時盜賊盛起，他帶兩千兵很快就將其全部平定。",
			"驍勇而有謀略，年輕時多次隨軍征討，屢立戰功。",
			"少有壯節，威勇果敢，被太守推薦為孝廉，后擔任校尉、中郎將等職。其人意在儒雅，雖有軍事，好學不倦。",
			"初為地方小吏，后被召為尚書郎。諫議君主應該著重治理國家，為君主贊賞，后任將軍鎮守一方。",
			"性寬弘，喜怒不形於色。又忍辱負重。少貧孤，晝勤四體，夜誦經傳。后被召為主記，拜中郎將。手不釋卷，拔能人為賢，為時人所稱頌。"
	};

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

	private String[] chenghaos;

	public void setGeneralChenghaos( String[] chenghaos )
	{
		this.chenghaos = chenghaos;
	}

	private String generalDescription;

	public void setGeneralDescription( String generalDescription )
	{
		this.generalDescription = generalDescription;
	}

	private String soldierType;
	private String soldierDictionary;
	private String general;
	private int posX, posY;

	private String strat_model;

	private String battle_model;

	public void createCustomGeneral( )
	{
		generalCustomSoldier( );
	}

	private void generalCustomSoldier( )
	{
		soldierType = "Custom " + name;
		soldierDictionary = soldierType.replaceAll( "(\\s+)", "_" );
		general = soldierDictionary;
		String soldierModel = soldier.getSoldier( )[0];
		List officerTypes = soldier.getOfficers( );
		try
		{
			UnitUtil.modifyBattleFile( faction, soldierModel );
			if ( officerTypes != null && officerTypes.size( ) > 0 )
			{
				for ( int i = 0; i < officerTypes.size( ); i++ )
				{
					UnitUtil.modifyBattleFile( faction,
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
					UnitUtil.modifyBattleFile( faction, horse );
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
				faction );
		setSoldierDescription( soldierDictionary, displayName );
		setSoldierImage( );

		changeGeneralSkills( general );
		changeGeneralBaowus( );

		UnitUtil.createGeneral( general,
				faction,
				posX,
				posY,
				strat_model,
				battle_model,
				skills,
				baowus,
				soldierType );

		addGeneralName( );
		changeGeneralImages( );
		changeGeneralDescription( );
	}

	public void setPosX( int posX )
	{
		this.posX = posX;
	}

	public void setPosY( int posY )
	{
		this.posY = posY;
	}

	public void setStrat_model( String strat_model )
	{
		this.strat_model = strat_model;
	}

	public void setBattle_model( String battle_model )
	{
		this.battle_model = battle_model;
	}

	private void changeGeneralBaowus( )
	{
		if ( baowus != null )
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
				if ( entry.getValue( ) != null )
				{
					if ( !baowuMap.containsKey( entry.getValue( ) ) )
					{
						baowuMap.put( entry.getValue( ), new ArrayList( ) );
					}
					( (List) baowuMap.get( entry.getValue( ) ) ).add( entry.getKey( ) );
				}
			}
			GeneralParser.removeBaowu( baowuMap );
			// GeneralParser.setGeneralBaowus( general, baowus );
		}
	}

	private void addGeneralName( )
	{
		GeneralParser.addGeneralName( general, displayName );
	}

	private void changeGeneralSkills( String general )
	{
		if ( chenghaos != null && chenghaos.length > 0 )
		{
			for ( int i = 0; i < chenghaos.length; i++ )
			{
				String chenghao = chenghaos[i];
				if ( "Ch2001".equals( chenghao ) )
				{
					skills.remove( "Ch2000" );
					skills.put( "Ch2000", "1" );
				}
				if ( "Ch6001".equals( chenghao ) )
				{
					skills.remove( "Ch6000" );
					skills.put( "Ch6000", "1" );
				}
				if ( "Ch4006".equals( chenghao ) )
				{
					skills.remove( "Ch4000" );
					skills.put( "Ch4000", "6" );
				}
				if ( "Ch6005".equals( chenghao ) )
				{
					skills.remove( "Ch6000" );
					skills.put( "Ch6000", "5" );
				}
			}
		}

		skills.putAt( general, "1", 0 );
		skills.putAt( "shouming8000", "1", 0 );
		skills.putAt( "JbAAAA0100", "1", 0 );
		// GeneralParser.setGeneralSkills( general, skills );
	}

	private void changeGeneralDescription( )
	{
		if ( generalDescription == null
				|| generalDescription.trim( ).length( ) == 0 )
		{
			generalDescription = displayName
					+ "，"
					+ descriptions[new Random( ).nextInt( descriptions.length )];
		}
		else
		{
			generalDescription = generalDescription.replaceAll( "\\n", "\\\\n" );
		}
		GeneralParser.createGeneralJueweis( general,
				jueweis,
				generalDescription );
	}

	private void changeGeneralImages( )
	{
		ImageData smallImageData = null;
		ImageData bigImageData = null;
		if ( generalImages[1] == null )
		{
			smallImageData = GraphicsUtil.resizeImage( generalImages[0],
					44,
					63,
					true );
			bigImageData = generalImages[0];
		}
		else
		{
			smallImageData = generalImages[1];
			bigImageData = generalImages[0];
		}

		try
		{
			File portraitFile = new File( FileConstants.customPortraitPath,
					general );
			if ( !portraitFile.exists( ) )
				portraitFile.mkdirs( );
			{
				Image image = new Image( null, smallImageData );
				Image grayImage = new Image( null, image, SWT.IMAGE_GRAY );
				ImageData grayImageData = grayImage.getImageData( );
				image.dispose( );
				grayImage.dispose( );
				TgaLoader.saveImage( new FileOutputStream( new File( FileConstants.customPortraitPath
						+ "\\"
						+ general
						+ "\\card_dead.tga" ) ),
						grayImageData );
				TgaLoader.saveImage( new FileOutputStream( new File( FileConstants.customPortraitPath
						+ "\\"
						+ general
						+ "\\card_young.tga" ) ),
						smallImageData );
				TgaLoader.saveImage( new FileOutputStream( new File( FileConstants.customPortraitPath
						+ "\\"
						+ general
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
						+ general
						+ "\\portrait_dead.tga" ) ),
						grayImageData );
				TgaLoader.saveImage( new FileOutputStream( new File( FileConstants.customPortraitPath
						+ "\\"
						+ general
						+ "\\portrait_young.tga" ) ),
						bigImageData );
				TgaLoader.saveImage( new FileOutputStream( new File( FileConstants.customPortraitPath
						+ "\\"
						+ general
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
				+ faction
				+ "\\"
				+ soldierDictionary
				+ "_info.tga";
		String smallFilePath = Patch.GAME_ROOT
				+ "\\alexander\\data\\ui\\units\\"
				+ faction
				+ "\\#"
				+ soldierDictionary
				+ ".tga";
		if ( soldierImage != null )
		{
			try
			{
				TgaLoader.saveImage( new FileOutputStream( bigFilePath ),
						GraphicsUtil.resizeImage( soldierImage, 160, 210, true ) );
				TgaLoader.saveImage( new FileOutputStream( smallFilePath ),
						GraphicsUtil.resizeImage( soldierImage, 48, 64, true ) );
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

	private String faction;

	public void setFaction( String faction )
	{
		this.faction = faction;
	}
}
