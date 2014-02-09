package com.cse110team14.placeit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class PlaceItViewActivity extends Activity {

	private PlaceIt curr;

	private ListView listView;
	private ArrayAdapter adapter;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_activelist);
		// Log.e("C______", "C______");
		Log.e("______", "______");
		curr = (PlaceIt) getIntent().getSerializableExtra("clicked");
		if (curr == null) {
		Log.e("NULL", "NULL");
	}		
		Log.e("^^^^^^^^^^^^^^^^^", curr.getTitle());
		// Log.e("Title", (String)
		// getIntent().getExtras().getSerializable("Title"));
		// Log.e("D______", "D______");
		/*
		 * Toast.makeText( PlaceItViewActivity.this, "Title: " + curr.getTitle()
		 * + "\nDescription: " + curr.getDescription() +
		 * "\nDate to be Reminded: " + curr.getDateReminded() +
		 * "\nDate of Generation: " + curr.getDate() + "\nLocation: (" +
		 * curr.getLocation().longitude + ", " + curr.getLocation().latitude +
		 * ")", Toast.LENGTH_LONG) .show();
		 */
//		Log.e("*****", "*****");

//		curr = new PlaceIt()
//		Log.e("&", getIntent().getStringExtra ("Location"));
//		if (curr == null) {
//			Log.e("NULL", "NULL");
//		}
		// curr.getTitle();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}
