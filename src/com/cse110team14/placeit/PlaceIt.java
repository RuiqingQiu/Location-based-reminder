package com.cse110team14.placeit;

import java.util.Date;

import android.location.Location;

public class PlaceIt {
	private String title;
	private String description;
	private Location location;
	private Date postDate;
	//This date field keep track of what kind of placeit it is
	//1 for active, 2 for pull down list, and 3 for expired
	private int placeitType;
	
	public PlaceIt(String title, String description, Location location, Date postDate){
		this.title = title;
		this.description = description;
		this.location = location;
		this.postDate = postDate;
		this.placeitType = 1;
	}
	
	public String getTitle(){
		return title;
	}
	public String getDescription(){
		return description;
	}
	public Location getLocation(){
		return location;
	}
	public Date getDate(){
		return postDate;
	}
	

}
