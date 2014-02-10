package com.cse110team14.placeit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

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

	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		sorted = new ArrayList<PlaceIt>();
		List<PlaceIt> pulled = MainActivity.getPullDownList();
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
			map.put("ItemDatePosted", "Post Date and time: " + curr.getDate());
			activeList.add(map);
		}

		SimpleAdapter adapter = new SimpleAdapter(this, activeList,
				R.layout.list_item, new String[] { "ItemTitle", "ItemText",
						"ItemDatePosted" }, new int[] { R.id.ItemTitle,
						R.id.ItemText, R.id.ItemDatePosted });
		listView.setAdapter(adapter);

		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int arg2,
					long id) {
				// ListView listView = (ListView) arg0;

				PulledListActivity.this.id = (int) id;
				clicked = sorted.get((int) id);
				Dialog dialog = new AlertDialog.Builder(PulledListActivity.this)
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
						.setPositiveButton("Move To Active",
								new OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {

										MainActivity.PlaceIts.add(clicked);
										sorted.remove(clicked);
										MainActivity.pullDown.remove(clicked);

										Toast.makeText(
												PulledListActivity.this,
												"Item \""
														+ clicked.getTitle()
														+ "\" is now moved to Active list.",
												Toast.LENGTH_LONG).show();
										finish();
										startActivity(getIntent());
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

				dialog.show();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
