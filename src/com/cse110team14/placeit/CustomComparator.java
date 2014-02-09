package com.cse110team14.placeit;

import java.util.Comparator;

public class CustomComparator implements Comparator<PlaceIt> {

	@Override
	public int compare(PlaceIt lhs, PlaceIt rhs) {
		return Integer.valueOf(extractBackslashes(lhs)).compareTo(extractBackslashes(rhs));
	}
	public int extractBackslashes(PlaceIt aplaceit){
		String date = aplaceit.getDateReminded();
		date = date.replace("/", "");
		return Integer.parseInt(date);
	}
}
