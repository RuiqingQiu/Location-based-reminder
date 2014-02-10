package com.cse110team14.placeit;

import java.io.Serializable;
import java.util.Date;

import com.google.android.gms.maps.model.LatLng;

import android.location.Location;


public class PlaceIt implements Serializable {
	private String title;
	private String description;
	private LatLng location;
	private String postDate;
	private String dateToBeReminded;
	// This date field keep track of what kind of placeit it is
	// 1 for active, 2 for pull down list, and 3 for expired
	private int placeitType;
	private String color;

	public PlaceIt(String title, String description, String color,
			LatLng location, String dateToBeReminded, String postDate) {
		this.title = title;
		this.description = description;
		this.location = location;
		this.postDate = postDate;
		this.dateToBeReminded = dateToBeReminded;
		this.placeitType = 1;
		this.color = color;
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

	public String getDate() {
		return postDate;
	}

	public String getColor() {
		return color;
	}

	public String getDateReminded() {
		return dateToBeReminded;
	}

}
