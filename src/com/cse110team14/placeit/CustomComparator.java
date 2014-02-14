package com.cse110team14.placeit;

import java.util.Comparator;

public class CustomComparator implements Comparator<PlaceIt> {

	@Override
	public int compare(PlaceIt lhs, PlaceIt rhs) {
		return lhs.getDateRemindedToCalendar().compareTo(rhs.getDateRemindedToCalendar());
	}
}
