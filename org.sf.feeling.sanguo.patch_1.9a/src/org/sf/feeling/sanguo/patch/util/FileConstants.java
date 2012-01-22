
package org.sf.feeling.sanguo.patch.util;

import java.io.File;

import org.sf.feeling.sanguo.patch.Patch;

public class FileConstants
{

	public static String dataPath = Patch.GAME_ROOT + "\\alexander\\data";
	public static File dataFile = new File( dataPath );

	public static String menuSymbolsPath = Patch.GAME_ROOT
			+ "\\alexander\\data\\menu\\symbols";
	public static File menuSymbolsFile = new File( menuSymbolsPath );

	public static String descrBannersPath = Patch.GAME_ROOT
			+ "\\alexander\\data\\descr_banners.txt";
	public static File descrBannersFile = new File( descrBannersPath );

	public static String descrFactionsPath = Patch.GAME_ROOT
			+ "\\alexander\\data\\descr_sm_factions.txt";
	public static File descrFactionsFile = new File( descrFactionsPath );

	public static String descrWallsPath = Patch.GAME_ROOT
			+ "\\alexander\\data\\descr_walls.txt";
	public static File descrWallsFile = new File( descrWallsPath );

	public static String captainBannerPath = Patch.GAME_ROOT
			+ "\\alexander\\data\\ui\\captain banners";
	public static File captainBannerFile = new File( captainBannerPath );

	public static String customPortraitPath = Patch.GAME_ROOT
			+ "\\alexander\\data\\ui\\custom_portraits";
	public static File customPortraitFile = new File( customPortraitPath );

	public static String unitFilePath = Patch.GAME_ROOT
			+ "\\alexander\\data\\export_descr_unit.txt";;
	public static File unitFile = new File( unitFilePath );

	public static String modelStratFilePath = Patch.GAME_ROOT
			+ "\\alexander\\data\\descr_model_strat.txt";
	public static File modelStratFile = new File( modelStratFilePath );

	public static String battleFilePath = Patch.GAME_ROOT
			+ "\\alexander\\data\\descr_model_battle.txt";
	public static File battleFile = new File( battleFilePath );

	public static String buildingsFilePath = Patch.GAME_ROOT
			+ "\\alexander\\data\\export_descr_buildings.txt";
	public static File buildingsFile = new File( buildingsFilePath );

	public static String baowuFilePath = Patch.GAME_ROOT
			+ "\\alexander\\data\\export_descr_ancillaries.txt";
	public static File baowuFile = new File( baowuFilePath );

	public final static String EXPORT_UNITS_FILEPATH = Patch.GAME_ROOT
			+ "\\alexander\\data\\text\\export_units.txt";
	public final static File exportUnitFile = new File( EXPORT_UNITS_FILEPATH );

	public final static String UNIT_ENUMS_FILEPATH = Patch.GAME_ROOT
			+ "\\alexander\\data\\export_descr_unit_enums.txt";
	public final static File unitEnumsFile = new File( UNIT_ENUMS_FILEPATH );

	public final static String nameFilePath = Patch.GAME_ROOT
			+ "\\alexander\\data\\text\\names.txt";
	public final static File nameFile = new File( nameFilePath );

	public final static String vnVsFilePath = Patch.GAME_ROOT
			+ "\\alexander\\data\\text\\export_VnVs.txt";
	public final static File vnVsFile = new File( vnVsFilePath );

	public final static String enumVnVsFilePath = Patch.GAME_ROOT
			+ "\\alexander\\data\\export_descr_VnVs_enums.txt";
	public final static File enumVnVsFile = new File( enumVnVsFilePath );

	public final static String stratFilePath = Patch.GAME_ROOT
			+ "\\alexander\\data\\world\\maps\\campaign\\Alexander\\descr_strat.txt";
	public final static File stratFile = new File( stratFilePath );

	public final static String mapGroundPath = Patch.GAME_ROOT
			+ "\\alexander\\data\\world\\maps\\campaign\\Alexander\\map_ground_types.tga";
	public final static File mapGroundFile = new File( mapGroundPath );

	public final static String mapRegionPath = Patch.GAME_ROOT
			+ "\\alexander\\data\\world\\maps\\campaign\\Alexander\\map_regions.tga";
	public final static File mapRegionFile = new File( mapRegionPath );

	public final static String mapFeaturePath = Patch.GAME_ROOT
			+ "\\alexander\\data\\world\\maps\\campaign\\Alexander\\map_features.tga";
	public final static File mapFeatureFile = new File( mapFeaturePath );

	public final static String desc_namesFilePath = Patch.GAME_ROOT
			+ "\\alexander\\data\\descr_names.txt";
	public final static File descNamesFile = new File( desc_namesFilePath );

	public final static String desc_MountFilePath = Patch.GAME_ROOT
			+ "\\alexander\\data\\descr_mount.txt";
	public final static File desc_MountFile = new File( desc_MountFilePath );

	public final static String projectTileFilePath = Patch.GAME_ROOT
			+ "\\alexander\\data\\descr_projectile_new.txt";
	public final static File projectTileFile = new File( projectTileFilePath );

	public final static String characterFilePath = Patch.GAME_ROOT
			+ "\\alexander\\data\\descr_character.txt";
	public final static File characterFile = new File( characterFilePath );

	public final static String disasterFilePath = Patch.GAME_ROOT
			+ "\\alexander\\data\\world\\maps\\base\\descr_disasters.txt";
	public final static File disasterFile = new File( disasterFilePath );

	public final static String characterTraitFilePath = Patch.GAME_ROOT
			+ "\\alexander\\data\\export_descr_character_traits.txt";
	public final static File characterTraitFile = new File( characterTraitFilePath );

	public final static String scriptFilePath = Patch.GAME_ROOT
			+ "\\alexander\\data\\scripts\\show_me\\script_12tpy_dates.txt";
	public final static File scriptFile = new File( scriptFilePath );

	public final static String uiUnitsPath = Patch.GAME_ROOT
			+ "\\alexander\\data\\ui\\units";
	public final static String uiUnitInfoPath = Patch.GAME_ROOT
			+ "\\alexander\\data\\ui\\unit_info";
	public final static String uiAncillariesPath = Patch.GAME_ROOT
			+ "\\alexander\\data\\ui\\ancillaries";
	public final static String stratBannerPath = Patch.GAME_ROOT
			+ "\\Data\\banners";
	public final static String battleBannerPath = Patch.GAME_ROOT
			+ "\\alexander\\data\\models\\textures";
	public final static String factionMapsPath = Patch.GAME_ROOT
			+ "\\alexander\\data\\world\\maps\\campaign\\Alexander";

	public final static String campaignDescriptionPath = Patch.GAME_ROOT
			+ "\\alexander\\data\\text\\campaign_descriptions.txt";
	public final static File campaignDescriptionFile = new File( campaignDescriptionPath );

	public final static String expandedBiPath = Patch.GAME_ROOT
			+ "\\alexander\\data\\text\\expanded_bi.txt";
	public final static File expandedBiFile = new File( expandedBiPath );

	public final static String[] cultures = new String[]{
			"barbarian",
			"carthaginian",
			"eastern",
			"egyptian",
			"greek",
			"roman"
	};

	public final static String uiPath = Patch.GAME_ROOT
			+ "\\alexander\\data\\ui";

	public static boolean testFile( )
	{
		if ( !unitFile.exists( ) )
		{
			return false;
		}
		else if ( !battleFile.exists( ) )
		{
			return false;
		}
		else if ( !buildingsFile.exists( ) )
		{
			return false;
		}
		else if ( !baowuFile.exists( ) )
		{
			return false;
		}
		else if ( !exportUnitFile.exists( ) )
		{
			return false;
		}
		else if ( !unitEnumsFile.exists( ) )
		{
			return false;
		}
		else if ( !nameFile.exists( ) )
		{
			return false;
		}
		else if ( !vnVsFile.exists( ) )
		{
			return false;
		}
		else if ( !stratFile.exists( ) )
		{
			return false;
		}
		else if ( !descNamesFile.exists( ) )
		{
			return false;
		}
		else if ( !desc_MountFile.exists( ) )
		{
			return false;
		}
		else if ( !projectTileFile.exists( ) )
		{
			return false;
		}
		else if ( !characterFile.exists( ) )
		{
			return false;
		}
		else if ( !disasterFile.exists( ) )
		{
			return false;
		}
		else if ( !characterTraitFile.exists( ) )
		{
			return false;
		}
		else if ( !scriptFile.exists( ) )
		{
			return false;
		}
		else if ( !scriptFile.exists( ) )
		{
			return false;
		}
		else if ( !customPortraitFile.exists( ) )
		{
			return false;
		}
		else if ( !modelStratFile.exists( ) )
		{
			return false;
		}
		else if ( !enumVnVsFile.exists( ) )
		{
			return false;
		}
		else if ( !descrBannersFile.exists( ) )
		{
			return false;
		}
		else if ( !descrFactionsFile.exists( ) )
		{
			return false;
		}
		else if ( !descrWallsFile.exists( ) )
		{
			return false;
		}
		else if ( !mapRegionFile.exists( ) )
		{
			return false;
		}
		else if ( !mapGroundFile.exists( ) )
		{
			return false;
		}
		else if ( !mapFeatureFile.exists( ) )
		{
			return false;
		}
		else if ( !campaignDescriptionFile.exists( ) )
		{
			return false;
		}
		else if ( !expandedBiFile.exists( ) )
		{
			return false;
		}
		return true;
	}
}
