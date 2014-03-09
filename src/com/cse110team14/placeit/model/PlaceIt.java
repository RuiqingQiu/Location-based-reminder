package com.cse110team14.placeit.model;

import java.util.Calendar;

import com.google.android.gms.maps.model.LatLng;
import android.util.Log;


public class PlaceIt extends SimplePlaceIt {
	//Regular placeit will remind people based on location
	private LatLng location;
	// This date field keep track of what kind of placeit it is
	/* 1 for one time only, 2 for minutely, and 3 for weekly
	 * 4 for two week, 5 for three week, 6 for monthly
	 */
	private int placeitType;
	private String color;
	/*
	 * Sneeze type: 1 for none (default)
	 * 				2 for 10 seconds later
	 * 				3 for 45 minutes later
	 */
	private int sneezeType;

	public PlaceIt(String title, String description, String color,
			LatLng location, String dateToBeReminded, String postDate) {
		this.title = title;
		this.description = description;
		this.location = location;
		this.postDate = postDate;
		this.dateToBeReminded = dateToBeReminded;
		//Default to be 1, one time only placeit
		this.placeitType = 1;
		this.color = color;
		//Default to be 1, none sneeze
		this.sneezeType = 1;
	}
	
	/**
	 * Get method for title
	 * @return
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Get method for description
	 * @return
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Get method for location
	 * @return
	 */
	public LatLng getLocation() {
		return location;
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
	public String getDate() {
		return postDate;
	}

	/**
	 * Get method for getting the color for the placeit
	 * @return the string representation of the color
	 */
	public String getColor(){
		return color;
	}
	
	
	public void setSneezeType(int type){
		sneezeType = type;
	}
	
	public int getSneezeType(){
		return this.sneezeType;
	}
	
	/**
	 * Set method for placeit type
	 * @param type
	 */
	public void setPlaceItType(int type){
		this.placeitType = type;
	}
	
	/**
	 * Get method for placeit type
	 * @return
	 */
	public int getPlaceItType(){
		return placeitType;
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

	@Override
	public void setListType(String type) {
		listType = type;
	}

	@Override
	public String getListType() {
		return listType;
	}
}
