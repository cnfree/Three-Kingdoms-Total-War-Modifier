package com.actuate.development.tool.model;


public class Changelist
{

	private String id;
	private String submitTime;
	private String submitBy;
	private String description;
	
	public String getId( )
	{
		return id;
	}
	
	public void setId( String id )
	{
		this.id = id;
	}
	
	public String getSubmitTime( )
	{
		return submitTime;
	}
	
	public void setSubmitTime( String submitTime )
	{
		this.submitTime = submitTime;
	}
	
	public String getSubmitBy( )
	{
		return submitBy;
	}
	
	public void setSubmitBy( String submitBy )
	{
		this.submitBy = submitBy;
	}
	
	public String getDescription( )
	{
		return description;
	}
	
	public void setDescription( String description )
	{
		this.description = description;
	}
}
