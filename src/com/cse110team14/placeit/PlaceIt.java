package com.cse110team14.placeit;

import java.util.Date;

import android.location.Location;

public class PlaceIt {
	private String title;
	private String description;
	private Location location;
	private Date postDate;
	
	public PlaceIt(String title, String description, Location location, Date postDate){
		this.title = title;
		this.description = description;
		this.location = location;
		this.postDate = postDate;
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
