package com.cse110team14.placeit.model;

import java.util.Calendar;

public class CPlaceIts extends SimplePlaceIt{
	//Can hold at most 3 categories
	private String[] categories = new String[3];
	public CPlaceIts(String title, String description, String postDate,
			String dateToBeReminded, String[] categories) {
		super(title, description, postDate, dateToBeReminded, 2);
		this.categories = categories;
		//Indicate category placeit
		this.RCType = 2;
	}
	public void setCategories(String[] categories){
		this.categories = categories;
	}
	public String[] getCategories(){
		return this.categories;
	}
	public String getCategoriesToString(){
		String tmp = categories[0];
		for(int i = 1; i < categories.length; i++){
			tmp = tmp + "|" + categories[i];
		}
		return tmp;
	}

}
