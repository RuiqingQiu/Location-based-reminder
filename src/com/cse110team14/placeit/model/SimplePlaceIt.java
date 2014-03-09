package com.cse110team14.placeit.model;


public abstract class SimplePlaceIt {
	protected String title;
	protected String description;
	protected String postDate;
	protected String dateToBeReminded;
	protected String color;
	//List type 1 is in active and 2 is in pulleddown
	protected String listType;

	/**
	 * Get method for title
	 * @return
	 */
	public abstract String getTitle();

	/**
	 * Get method for description
	 * @return
	 */
	public abstract String getDescription();
	
	/**
	 * Set method for postDate
	 */
	public abstract void setDatePosted();
	/**
	 * Get the postDate
	 * @return the string for postDate
	 */
	public abstract String getDate();
	/**
	 * Get method for getting the color for the placeit
	 * @return the string representation of the color
	 */
	public abstract String getColor();
	
	public abstract void setListType(String type);
	public abstract String getListType();
}
