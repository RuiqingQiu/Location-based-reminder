package com.cse110team14.placeit;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class PlaceItViewActivity extends Activity {
	
	private PlaceIt curr;
    private ListView listView;
    private ArrayAdapter adapter;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_activelist);
		
		listView = (ListView) findViewById(R.id.ActiveListView);
		
	    adapter = new ArrayAdapter(this, R.layout.placeit_view);
	    //adapter.add(object)
/*		
		View myContentsView = getLayoutInflater().inflate(R.layout.placeit_view, null);
		
		TextView tvTitle = ((TextView)myContentsView.findViewById(R.id.title));
        tvTitle.setText(curr.getTitle());
        TextView tvDescription = ((TextView)myContentsView.findViewById(R.id.description));
        tvDescription.setText("Description: " + curr.getDescription());
        TextView tvDateRemind = ((TextView)myContentsView.findViewById(R.id.dateToBeReminded));
        tvDateRemind.setText("Date to be Reminded: " + curr.getDateReminded());
        TextView tvDatePost = ((TextView)myContentsView.findViewById(R.id.postDate));
        tvDatePost.setText("Post Date and time: " + curr.getDate());*/	
	}
}
