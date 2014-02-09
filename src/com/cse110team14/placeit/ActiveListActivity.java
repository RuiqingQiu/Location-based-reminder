package com.cse110team14.placeit;


import java.util.ArrayList;
import java.util.Collections;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;


public class ActiveListActivity extends Activity {
	private Iterator<PlaceIt> piIterator;
	private List<PlaceIt> sorted;
//	private PlaceIt curr;
	
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
        
        sorted = new ArrayList<PlaceIt>();
        //piIterator = MainActivity.PlaceIts.iterator();
        
        for(Iterator<PlaceIt> i =  MainActivity.PlaceIts.iterator(); i.hasNext();)
        	sorted.add(i.next());
        
        Collections.sort(sorted, new CustomComparator());
        
		setContentView(R.layout.activity_activelist);

		ListView listView = (ListView) findViewById(R.id.ActiveListView);

		//piIterator = MainActivity.PlaceIts.iterator();

		/*List<HashMap<String, String>> activeList = new ArrayList<HashMap<String, String>>();
		while (piIterator.hasNext()) {
			HashMap<String, String> map = new HashMap<String, String>();
			curr = piIterator.next();
			map.put("ItemTitle", curr.getTitle());
			map.put("ItemText", curr.getDescription());
			activeList.add(map);
		}*/
//		for (PlaceIt pi : sorted)
		List<HashMap<String, String>> activeList = new ArrayList<HashMap<String, String>>();
		for(PlaceIt curr : sorted) {
			HashMap<String, String> map = new HashMap<String, String>();
//			curr = piIterator.next();
			map.put("ItemTitle", curr.getTitle());
			map.put("ItemText", curr.getDescription());
			activeList.add(map);
		}
			
			
		SimpleAdapter mSchedule = new SimpleAdapter(this, activeList,
				R.layout.list_item,new String[] { "ItemTitle", "ItemText" }, 
				new int[] { R.id.ItemTitle, R.id.ItemText });
		listView.setAdapter(mSchedule);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void goToPlaceItView(View v){
		startActivity(new Intent(this, PlaceItViewActivity.class));
	}
}
