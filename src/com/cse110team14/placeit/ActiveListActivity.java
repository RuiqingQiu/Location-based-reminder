package com.cse110team14.placeit;


import java.util.Iterator;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

public class ActiveListActivity extends Activity{
	private Iterator<PlaceIt> piIterator;
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activelist);
        piIterator = MainActivity.PlaceIts.iterator();
        
        
        
	}
	  @Override
	    public boolean onCreateOptionsMenu(Menu menu) {
	        // Inflate the menu; this adds items to the action bar if it is present.
	        getMenuInflater().inflate(R.menu.main, menu);
	        return true;
	    }
}
