
package org.sf.feeling.sanguo.patch.model;

import java.util.ArrayList;
import java.util.List;

public class Unit
{

	private String animal;
	private List attributes = new ArrayList( );
	private String category;
	private int chargeDist;
	private String[] cost = new String[6];
	private String dictionary;
	private String engine;
	private List factions = new ArrayList( );
	private List formation = new ArrayList( );
	private int[] ground = new int[4];
	private int[] health = new int[2];
	private int heat;
	private String[] mental = new String[3];
	private String mount;
	private List mountEffect = new ArrayList( );
	private List officers = new ArrayList( );
	private String[] primary = new String[11];	
	private String[] primaryArmour = new String[4];
	private List primaryAttr = new ArrayList( );
	private String[] second = new String[11];
	private String[] secondArmour = new String[3];
	private List secondAttr = new ArrayList( );
	private String[] soldier = new String[4];
	private String type;
	private String unitClass;
	public String getAnimal( )
	{
		return animal;
	}
	public List getAttributes( )
	{
		return attributes;
	}

	public String getCategory( )
	{
		return category;
	}

	public int getChargeDist( )
	{
		return chargeDist;
	}

	public String[] getCost( )
	{
		return cost;
	}

	public String getDictionary( )
	{
		return dictionary;
	}

	public String getEngine( )
	{
		return engine;
	}

	public List getFactions( )
	{
		return factions;
	}

	public List getFormation( )
	{
		return formation;
	}

	public int[] getGround( )
	{
		return ground;
	}

	public int[] getHealth( )
	{
		return health;
	}

	public int getHeat( )
	{
		return heat;
	}

	public String[] getMental( )
	{
		return mental;
	}

	public String getMount( )
	{
		return mount;
	}

	public List getMountEffect( )
	{
		return mountEffect;
	}

	public List getOfficers( )
	{
		return officers;
	}

	public String[] getPrimary( )
	{
		return primary;
	}

	public String[] getPrimaryArmour( )
	{
		return primaryArmour;
	}

	public List getPrimaryAttr( )
	{
		return primaryAttr;
	}

	public String[] getSecond( )
	{
		return second;
	}

	public String[] getSecondArmour( )
	{
		return secondArmour;
	}

	public List getSecondAttr( )
	{
		return secondAttr;
	}

	public String[] getSoldier( )
	{
		return soldier;
	}

	public String getType( )
	{
		return type;
	}

	public String getUnitClass( )
	{
		return unitClass;
	}

	public boolean hasPrimaryWeapon( )
	{
		if ( primary != null && primary.length > 5 && !"no".equals( primary[5] ) )
			return true;
		return false;
	}

	public boolean hasSecondWeapon( )
	{
		if ( second != null && second.length > 5 && !"no".equals( second[5] ) )
			return true;
		return false;
	}

	public void setAnimal( String animal )
	{
		this.animal = animal;
	}

	public void setAttributes( List attributes )
	{
		this.attributes = attributes;
	}

	public void setCategory( String category )
	{
		this.category = category;
	}

	public void setChargeDist( int chargeDist )
	{
		this.chargeDist = chargeDist;
	}

	public void setCost( String[] cost )
	{
		this.cost = cost;
	}

	public void setDictionary( String dictionary )
	{
		this.dictionary = dictionary;
	}

	public void setEngine( String engine )
	{
		this.engine = engine;
	}

	public void setFactions( List factions )
	{
		this.factions = factions;
	}

	public void setFormation( List formation )
	{
		this.formation = formation;
	}

	public void setGround( int[] ground )
	{
		this.ground = ground;
	}

	public void setHealth( int[] health )
	{
		this.health = health;
	}

	public void setHeat( int heat )
	{
		this.heat = heat;
	}

	public void setMental( String[] mental )
	{
		this.mental = mental;
	}

	public void setMount( String mount )
	{
		this.mount = mount;
	}

	public void setMountEffect( List mountEffect )
	{
		this.mountEffect = mountEffect;
	}

	public void setOfficers( List officers )
	{
		this.officers = officers;
	}

	public void setPrimary( String[] primary )
	{
		this.primary = primary;
	}

	public void setPrimaryArmour( String[] primaryArmour )
	{
		this.primaryArmour = primaryArmour;
	}

	public void setPrimaryAttr( List primaryAttr )
	{
		this.primaryAttr = primaryAttr;
	}

	public void setSecond( String[] second )
	{
		this.second = second;
	}

	public void setSecondArmour( String[] secondArmour )
	{
		this.secondArmour = secondArmour;
	}

	public void setSecondAttr( List secondAttr )
	{
		this.secondAttr = secondAttr;
	}

	public void setSoldier( String[] soldier )
	{
		this.soldier = soldier;
	}

	public void setType( String type )
	{
		this.type = type;
	}

	public void setUnitClass( String unitClass )
	{
		this.unitClass = unitClass;
	}
}