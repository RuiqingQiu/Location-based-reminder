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
		super(title, description, postDate, dateToBeReminded, 1);
		this.location = location;
		//Default to be 1, one time only placeit
		this.placeitType = 1;
		this.color = color;
		//Default to be 1, none sneeze
		this.sneezeType = 1;
		//To indicate regular placeit
		this.RCType = 1;
	}
	
	/**
	 * Get method for location
	 * @return
	 */
	public LatLng getLocation() {
		return location;
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
}
