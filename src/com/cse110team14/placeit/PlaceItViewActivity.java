package com.cse110team14.placeit;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class PlaceItViewActivity extends Activity {
	
	private PlaceIt curr;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.placeit_view);
		
		
		View myContentsView = getLayoutInflater().inflate(R.layout.placeit_view, null);
		
		TextView tvTitle = ((TextView)myContentsView.findViewById(R.id.title));
        tvTitle.setText(curr.getTitle());
        TextView tvDescription = ((TextView)myContentsView.findViewById(R.id.description));
        tvDescription.setText("Description: " + curr.getDescription());
        TextView tvDateRemind = ((TextView)myContentsView.findViewById(R.id.dateToBeReminded));
        tvDateRemind.setText("Date to be Reminded: " + curr.getDateReminded());
        TextView tvDatePost = ((TextView)myContentsView.findViewById(R.id.postDate));
        tvDatePost.setText("Post Date and time: " + curr.getDate());
		
	}
}
