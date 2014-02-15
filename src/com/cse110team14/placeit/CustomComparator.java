package com.cse110team14.placeit;

//Custom Comparator which will help to sort the list of active place-its by comparing the calendar dates

import java.util.Comparator;

public class CustomComparator implements Comparator<PlaceIt> {

	@Override
	public int compare(PlaceIt lhs, PlaceIt rhs) {
		return lhs.getDateRemindedToCalendar().compareTo(rhs.getDateRemindedToCalendar());
	}
}
