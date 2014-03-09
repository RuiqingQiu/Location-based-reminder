package com.cse110team14.placeit.model;

import java.util.Calendar;


public class SimplePlaceIt {
	protected String title;
	protected String description;
	protected String postDate;
	protected String dateToBeReminded;
	//List type 1 is in active and 2 is in pulleddown
	protected String listType;
	protected int RCType;

	public SimplePlaceIt(String title, String description, String postDate, String dateToBeReminded){
		this.title = title;
		this.description = description;
		this.postDate = postDate;
		this.dateToBeReminded = dateToBeReminded;
		RCType = 0;
	}
	/**
	 * Get method for title
	 * @return
	 */
	public String getTitle(){
		return title;
	}

	/**
	 * Get method for description
	 * @return
	 */
	public String getDescription(){
		return description;
	}
	
	/**
	 * Set method for postDate
	 */
	public void setDatePosted(){
		postDate = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
	}
	/**
	 * Get the postDate
	 * @return the string for postDate
	 */
	public String getDate(){
		return postDate;
	}
	/**
	 * Get method for getting the color for the placeit
	 * @return the string representation of the color
	 */
	
	public void setListType(String type){
		this.listType = type;
	}
	public String getListType(){
		return listType;
	}
	
	/**
	 * Get method for the dateToBeReminded
	 * @return the string representation of the dateToBeReminded
	 */
	public String getDateReminded() {
		return dateToBeReminded;
	}
	public void setDateReminded(String date){
		dateToBeReminded = date;
	}
	public int getRorC(){
		return this.RCType;
	}

	/**
	 * Method that will transform the dateToBeReminded to be a Calendar Object
	 * @return a Calendar object containing time and dates
	 */
	public Calendar getDateRemindedToCalendar(){
		String [] splited = dateToBeReminded.split("/");
		int month = Integer.parseInt(splited[0]) - 1;
		int day = Integer.parseInt(splited[1]);
		int year = Integer.parseInt(splited[2]);
		String [] tmp = postDate.split(" ");
		if(tmp[4].compareTo("PM") == 0)
		{
			Calendar c = constructDate(month, day, year, tmp, true);
			return c;
		}
		else{
			
			Calendar c = constructDate(month,day,year,tmp,false);
			return c;
		}
	}
	

	private Calendar constructDate(int month, int day, int year, String[] tmp, boolean PM) {
		int colon = tmp[3].indexOf(':');
		int hourOfDay = 0;
		if(PM)
			hourOfDay = Integer.parseInt(tmp[3].substring(0,colon)) + 12;
		else
			hourOfDay = Integer.parseInt(tmp[3].substring(0,colon));
		int minute = Integer.parseInt(tmp[3].substring(colon+1, colon+3));
		Calendar c = Calendar.getInstance();
		c.set(year, month, day, hourOfDay, minute);
		return c;
	}
}
