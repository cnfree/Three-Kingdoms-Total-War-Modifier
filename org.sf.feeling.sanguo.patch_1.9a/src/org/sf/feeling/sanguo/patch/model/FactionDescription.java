
package org.sf.feeling.sanguo.patch.model;

import org.eclipse.swt.graphics.RGB;

public class FactionDescription
{

	private String culture;
	private String primary_colour;
	private String secondary_colour;
	private String loading_logo;
	private String standard_index;
	private String custom_battle_availability;
	private String prefers_naval_invasions;
	private String symbol;

	public String getSymbol( )
	{
		return symbol;
	}

	public void setSymbol( String symbol )
	{
		this.symbol = symbol;
	}

	public String getCulture( )
	{
		return culture;
	}

	public void setCulture( String culture )
	{
		this.culture = culture;
	}

	public String getPrimary_colour( )
	{
		return primary_colour;
	}

	public void setPrimary_colour( String primary_colour )
	{
		this.primary_colour = primary_colour;
	}

	public String getSecondary_colour( )
	{
		return secondary_colour;
	}

	public void setSecondary_colour( String secondary_colour )
	{
		this.secondary_colour = secondary_colour;
	}

	public String getLoading_logo( )
	{
		return loading_logo;
	}

	public void setLoading_logo( String loading_logo )
	{
		this.loading_logo = loading_logo;
	}

	public String getStandard_index( )
	{
		return standard_index;
	}

	public void setStandard_index( String standard_index )
	{
		this.standard_index = standard_index;
	}

	public String getCustom_battle_availability( )
	{
		return custom_battle_availability;
	}

	public void setCustom_battle_availability( String custom_battle_availability )
	{
		this.custom_battle_availability = custom_battle_availability;
	}

	public String getPrefers_naval_invasions( )
	{
		return prefers_naval_invasions;
	}

	public void setPrefers_naval_invasions( String prefers_naval_invasions )
	{
		this.prefers_naval_invasions = prefers_naval_invasions;
	}

	public RGB parseRGB( String primary_colour )
	{
		String[] color = primary_colour.trim( )
				.split( "(\\s*,*\\s+)|(\\s*,+\\s*)" );
		return new RGB( Integer.parseInt( color[1].trim( ) ),
				Integer.parseInt( color[3].trim( ) ),
				Integer.parseInt( color[5].trim( ) ) );
	}
}
