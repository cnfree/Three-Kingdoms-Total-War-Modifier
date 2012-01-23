
package org.sf.feeling.sanguo.patch.model;

public class General
{

	private String portrait;
	private String strat_model;
	private String battle_model;
	private String posX;
	private String posY;
	private boolean isLeader = false;
	private boolean isHeir = false;
	private String faction;
	
	public boolean isHeir( )
	{
		return isHeir;
	}

	
	public void setHeir( boolean isHeir )
	{
		this.isHeir = isHeir;
	}

	private String age;

	public String getAge( )
	{
		return age;
	}

	public void setAge( String age )
	{
		this.age = age;
	}

	public boolean isLeader( )
	{
		return isLeader;
	}

	public void setLeader( boolean isLeader )
	{
		this.isLeader = isLeader;
	}

	public String getPosX( )
	{
		return posX;
	}

	public void setPosX( String posX )
	{
		this.posX = posX;
	}

	public String getPosY( )
	{
		return posY;
	}

	public void setPosY( String posY )
	{
		this.posY = posY;
	}

	public String getPortrait( )
	{
		return portrait;
	}

	public void setPortrait( String portrait )
	{
		this.portrait = portrait;
	}

	public String getStrat_model( )
	{
		return strat_model;
	}

	public void setStrat_model( String strat_model )
	{
		this.strat_model = strat_model;
	}

	public String getBattle_model( )
	{
		return battle_model;
	}

	public void setBattle_model( String battle_model )
	{
		this.battle_model = battle_model;
	}
	
	public String getFaction( )
	{
		return faction;
	}

	public void setFaction( String faction )
	{
		this.faction = faction;
	}
}
