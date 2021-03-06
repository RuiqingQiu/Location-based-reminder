package com.cse110team14.placeit.util;

//Custom Comparator which will help to sort the list of active place-its by comparing the calendar dates

import java.util.Comparator;

import com.cse110team14.placeit.model.PlaceIt;
import com.cse110team14.placeit.model.SimplePlaceIt;

public class CustomComparator implements Comparator<SimplePlaceIt> {

	/**
	 * compare 2 place-its by time to remind
	 * 
	 * @param lhs - the place-it to compare
	 *        rhs - the place-it compared to
	 * 
	 * @return 0 if the times of the two Calendars are equal, -1 if the time of
	 *         this Calendar is before the other one, 1 if the time of this
	 *         Calendar is after the other one.
	 */
	@Override
	public int compare(SimplePlaceIt lhs, SimplePlaceIt rhs) {
		return lhs.getDateRemindedToCalendar().compareTo(
				rhs.getDateRemindedToCalendar());
	}
}
