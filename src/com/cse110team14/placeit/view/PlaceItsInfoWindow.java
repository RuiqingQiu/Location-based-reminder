package com.cse110team14.placeit.view;

import android.view.View;
import android.widget.TextView;

import com.cse110team14.placeit.R;
import com.cse110team14.placeit.R.id;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.model.Marker;

/* A CLASS FOR CUSTOM PLACEITS INFO WINDOW ON THE MAP */
public class PlaceItsInfoWindow implements InfoWindowAdapter{

	private final View myContentsView;
	  
	public PlaceItsInfoWindow(View view){
	   myContentsView = view;
	}
	@Override
	public View getInfoContents(Marker marker) {
		String snippet = marker.getSnippet();
		if(snippet == null)
			return null;
		//Every string that added will concat with ###, and now we split them to get parts
		String [] splited = snippet.split("###");
		if(splited[0].startsWith("latitude"))
		{
			TextView tvTitle = ((TextView)myContentsView.findViewById(R.id.title));
            tvTitle.setText(marker.getTitle());
            TextView tvDescription = ((TextView)myContentsView.findViewById(R.id.description));
            tvDescription.setText("Description: " +splited[0] + "\n" + splited[1] + "\n" +splited[2]);
		}
		else{
			TextView tvTitle = ((TextView)myContentsView.findViewById(R.id.title));
            tvTitle.setText(marker.getTitle());
            TextView tvDescription = ((TextView)myContentsView.findViewById(R.id.description));
            tvDescription.setText("Description: " +splited[0]);
            TextView tvDateRemind = ((TextView)myContentsView.findViewById(R.id.dateToBeReminded));
            tvDateRemind.setText("Date to be Reminded: " + splited[1]);
            TextView tvDatePost = ((TextView)myContentsView.findViewById(R.id.postDate));
            tvDatePost.setText("Post Date and time: " + splited[2]);
		}
        return myContentsView;
	}

	@Override
	public View getInfoWindow(Marker marker) {
		// TODO Auto-generated method stub
		return null;
	}
	
}