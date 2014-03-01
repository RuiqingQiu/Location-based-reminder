package com.cse110team14.placeit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.cse110team14.placeit.model.PlaceIt;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class PulledListActivity extends Activity {

	private Iterator<PlaceIt> piIterator;
	private List<PlaceIt> sorted;

	private PlaceIt clicked;
	private int id;

	/**
	 * Called when the activity is first created. This method create the list
	 * view, and show the list view items and bind it to the click listener.
	 * 
	 * @param savedInstanceState
	 *            - If the activity is being re-initialized after previously
	 *            being shut down then this Bundle contains the data it most
	 *            recently supplied in onSaveInstanceState(Bundle).
	 * 
	 * @return void
	 */
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		sorted = new ArrayList<PlaceIt>();
		List<PlaceIt> pulled = MainActivity.pullDown;
		for (Iterator<PlaceIt> i = pulled.iterator(); i.hasNext();)
			sorted.add(i.next());

		Collections.sort(sorted, new CustomComparator());

		setContentView(R.layout.activity_pulledlist);

		ListView listView = (ListView) findViewById(R.id.PulledListView);

		List<HashMap<String, String>> activeList = new ArrayList<HashMap<String, String>>();
		for (PlaceIt curr : sorted) {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("ItemTitle", "Title: " + curr.getTitle());
			map.put("ItemText", "Description: " + curr.getDescription());
			map.put("ItemDateToRemind", "Date and time to Remind: " + curr.getDateReminded());
			map.put("ItemPostTime", "Post Time: " + curr.getDate());
			activeList.add(map);
		}

		SimpleAdapter adapter = new SimpleAdapter(this, activeList,
				R.layout.list_item, new String[] { "ItemTitle", "ItemText",
						"ItemDateToRemind","ItemPostTime" }, new int[] { R.id.ItemTitle,
						R.id.ItemText, R.id.ItemDateToRemind, R.id.ItemPostTime });
		listView.setAdapter(adapter);

		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int arg2,
					long id) {
				// ListView listView = (ListView) arg0;

				PulledListActivity.this.id = (int) id;
				clicked = sorted.get((int) id);
				Dialog detailDetailsdialog = createDetailsDialog();
				detailDetailsdialog.show();
			}
		});
	}

	
	/**
	 * This method create the dialog showing the details of a place-it, and the 
	 * related buttons for repost and other options.
	 * 
	 * @param none
	 * 
	 * @return the dialog showing actions and details of a place-it
	 */
	private Dialog createDetailsDialog(){
		Dialog dia = new AlertDialog.Builder(PulledListActivity.this)
		.setTitle("Title: " + clicked.getTitle())
		.setItems(
				new String[] {
						"Description: "
								+ clicked.getDescription(),
						"Date to be Reminded: "
								+ clicked.getDateReminded(),
						"Post Date and time: "
								+ clicked.getDate(),
						"Location: ("
								+ clicked.getLocation().latitude
								+ ", "
								+ clicked.getLocation().longitude
								+ ")" }, null)
		.setPositiveButton("Repost",
				new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog,
							int which) {
						Dialog repostDialog = createRepostDialog();
						repostDialog.show();
						//finish();
						//startActivity(getIntent());
						
						
					}
				})
		.setNegativeButton("Discard",
				new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog,
							int which) {

						sorted.remove(clicked);
						MainActivity.pullDown.remove(clicked);

						Toast.makeText(
								PulledListActivity.this,
								"Item \""
										+ clicked.getTitle()
										+ "\" is now discarded.",
								Toast.LENGTH_LONG).show();
						finish();
						startActivity(getIntent());
					}
				})
		.setNeutralButton("OK", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog,
					int which) {
				Toast.makeText(
						PulledListActivity.this,
						"Pulled-Down item \"" + clicked.getTitle()
								+ "\" has been shown.",
						Toast.LENGTH_LONG).show();
			}
		}).create();
	return dia;
	}
	
	/**
	 * This method create the dialog showing the repost time options
	 * 
	 * @param none
	 * 
	 * @return the dialog showing actions and details of a place-it
	 */
	private Dialog createRepostDialog(){
		Dialog dia = new AlertDialog.Builder(PulledListActivity.this)
		.setTitle("When do you want to repost it?")
		.setPositiveButton("Now",
				new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog,
							int which) {
						
						sorted.remove(clicked);
						MainActivity.pullDown.remove(clicked);
						clicked.setDatePosted();
						MainActivity.activeList.add(clicked);

						Toast.makeText(
								PulledListActivity.this,
								"Item is now active",
								Toast.LENGTH_LONG).show();
						finish();
						startActivity(getIntent());
					}
				})
		.setNegativeButton("45 min later",
				new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog,
							int which) {

						//activate 45 minutes later
						sorted.remove(clicked);
						MainActivity.pullDown.remove(clicked);
						clicked.setSneezeType(3);
						clicked.setDatePosted();
						MainActivity.activeList.add(clicked);

						Toast.makeText(
								PulledListActivity.this,
								"Item will be active 45 minutes later.",
								Toast.LENGTH_LONG).show();
						finish();
						startActivity(getIntent());
					}
				})
		.setNeutralButton("10s later", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog,
					int which) {
				
				//activate 10 seconds later
				sorted.remove(clicked);
				MainActivity.pullDown.remove(clicked);
				clicked.setSneezeType(2);
				clicked.setDatePosted();
				MainActivity.activeList.add(clicked);
							
				Toast.makeText(
						PulledListActivity.this,
						"Item will be active 10 seconds later.",
						Toast.LENGTH_LONG).show();
			}
		}).create();
		return dia;
	}
	
	/**
	 * Initialize the contents of the Activity's standard options menu
	 * 
	 * @param The options menu in which you place your items.
	 * 
	 * @return Initialize the contents of the Activity's standard options menu
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
