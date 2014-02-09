package com.cse110team14.placeit;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

public class ActiveListActivity extends Activity{
	//private Iterator<PlaceIt> piIterator;
	private List<PlaceIt> sorted;
	
	public void onCreate(Bundle savedInstanceState){
		
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activelist);
        
        sorted = new ArrayList<PlaceIt>();
        //piIterator = MainActivity.PlaceIts.iterator();
        for(Iterator<PlaceIt> i =  MainActivity.PlaceIts.iterator(); i.hasNext();)
        	sorted.add(i.next());
        Collections.sort(sorted, new CustomComparator());
        
	}
	  @Override
	    public boolean onCreateOptionsMenu(Menu menu) {
	        // Inflate the menu; this adds items to the action bar if it is present.
	        getMenuInflater().inflate(R.menu.main, menu);
	        return true;
	  }
}
