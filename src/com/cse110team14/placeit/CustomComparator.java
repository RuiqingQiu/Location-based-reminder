package com.cse110team14.placeit;

//Custom Comparator which will help to sort the list of active place-its by comparing the calendar dates

import java.util.Comparator;

public class CustomComparator implements Comparator<PlaceIt> {

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
	public int compare(PlaceIt lhs, PlaceIt rhs) {
		return lhs.getDateRemindedToCalendar().compareTo(
				rhs.getDateRemindedToCalendar());
	}
}
