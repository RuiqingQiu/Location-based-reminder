package com.cse110team14.placeit;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import com.google.android.gms.maps.model.LatLng;

import android.location.Location;
import android.util.Log;


public class PlaceIt implements Serializable {
	private String title;
	private String description;
	private LatLng location;
	private String postDate;
	private String dateToBeReminded;
	// This date field keep track of what kind of placeit it is
	/* 1 for one time only, 2 for minutely, and 3 for weekly
	 * 4 for two week, 5 for three week, 6 for monthly
	 */
	private int placeitType;
	private String color;

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
	}
	public void setPlaceItType(int type){
		this.placeitType = type;
	}
	public int getPlaceItType(){
		return placeitType;
	}

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}

	public LatLng getLocation() {
		return location;
	}

	public void setDatePosted(){
		postDate = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
	}
	public String getDate() {
		return postDate;
	}
	
	
	public Calendar getDateRemindedToCalendar(){
		String [] splited = dateToBeReminded.split("/");
		int month = Integer.parseInt(splited[0]) - 1;
		int day = Integer.parseInt(splited[1]);
		int year = Integer.parseInt(splited[2]);
		String [] tmp = postDate.split(" ");
		if(tmp[4].compareTo("PM") == 0)
		{
			int colon = tmp[3].indexOf(':');
			int hourOfDay = Integer.parseInt(tmp[3].substring(0,colon)) + 12;
			int minute = Integer.parseInt(tmp[3].substring(colon+1, colon+3));
			Log.e("hello", "hour: " + hourOfDay + "minute: " + minute);
			Calendar c = Calendar.getInstance();
			c.set(year, month, day, hourOfDay, minute);
			return c;
		}
		else{
			int colon = tmp[3].indexOf(':');
			int hourOfDay = Integer.parseInt(tmp[3].substring(0,colon));
			int minute = Integer.parseInt(tmp[3].substring(colon+1, colon+3));
			Log.e("hello", "hour: " + hourOfDay + "minute: " + minute);
			Calendar c = Calendar.getInstance();
			c.set(year, month, day, hourOfDay, minute);
			return c;
		}
	}

	public String getColor() {
		return color;
	}

	public String getDateReminded() {
		return dateToBeReminded;
	}

}
