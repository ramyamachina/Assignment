package com.example.shinobipiechart;

/* 
 * Description: General class containing the constant declarations.
 * 
 */

public class SetterGetter {
	 
	private String name = "";
	private String week = "";
	private String count = "";
	private String engValue = "";
	private String userValue = "";

	/* Name of the comapny*/
	public void setName(String name) 
	{
		this.name = name;
	}
	
	public String getName() 
	{
		return name;
	}
	
	/* Last activity observed time*/
	public void setWeek(String aWeek) 
	{
		this.week = aWeek;
	}
	
	public String getWeek() 
	{
		return week;
	}
	/* % of engagement*/
	public void setEngValue(String aValue) 
	{
		this.engValue = aValue;
	}
	
	public String getEngValue() 
	{
		return engValue;
	}
	
	/* Dollar spent*/ 
	public void setCount(String aCount) 
	{
		this.count = aCount;
	}
	
	public String getCount() 
	{
		return count;
	}
	 
	/* Active user count*/
	public void setuserValue(String aUser) 
	{
		this.userValue = aUser;
	}
	
	 public String getuserValue() 
	{
		 return userValue;
	}

}