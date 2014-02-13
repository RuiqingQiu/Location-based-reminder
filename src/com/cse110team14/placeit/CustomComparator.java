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
		String temp1 = date.substring(4);
		String temp2 = date.substring(0,4);
		date = temp1+temp2;
		return Integer.parseInt(date);
	}
}
