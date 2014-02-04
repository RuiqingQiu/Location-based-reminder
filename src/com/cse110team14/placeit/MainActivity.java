/*
 * Filename: MainActivity.java
 */
package com.cse110team14.placeit;
import com.cse110team14.placeit.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;
import android.support.v4.app.FragmentActivity;

public class MainActivity extends Activity implements
GooglePlayServicesClient.ConnectionCallbacks,
GooglePlayServicesClient.OnConnectionFailedListener {
	
	// Global constants
    /*
     * Define a request code to send to Google Play services
     * This code is returned in Activity.onActivityResult
     */
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
	static final LatLng HAMBURG = new LatLng(53.558, 9.927);
	private GoogleMap map;
	static final LatLng MELBOURNE = new LatLng(-37.81319, 144.96298);
	
	PolylineOptions rectOptions = new PolylineOptions()
    .add(new LatLng(37.35, -122.0))
    .add(new LatLng(37.45, -122.0))  // North of the previous point, but at the same longitude
    .add(new LatLng(37.45, -122.2))  // Same latitude, and 30km to the west
    .add(new LatLng(37.35, -122.2))  // Same longitude, and 16km to the south
    .add(new LatLng(37.35, -122.0)); // Closes the polyline.
	
	
	AlertDialog.Builder alert;
	Location myCurrentLocation;
	LocationClient myLocationClient;
	final Context context = this;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpMapIfNeeded();
      
        //An marker example
        Marker hamburg = map.addMarker(new MarkerOptions().position(HAMBURG).title("Hamburg").snippet("Hello"));
        myLocationClient =  new LocationClient(this, this, this);
        map.setMyLocationEnabled(true);
        //Marker with window info
        Marker melbourne = map.addMarker(new MarkerOptions()
        .position(MELBOURNE)
        .title("Melbourne")
        .snippet("Population: 4,137,400"));
        
        // Get back the mutable Polyline
        //Adding polylines around SF
    	Polyline polyline = map.addPolyline(rectOptions);
        //myCurrentLocation = myLocationClient.getLastLocation();
        //LatLng ME = new LatLng(myCurrentLocation.getLatitude(), myCurrentLocation.getLongitude());
        //Marker me = map.addMarker(new MarkerOptions().position(ME).title("My Position"));
    	
    	
    	
    }
    
    /*
     * Called when the Activity becomes visible.
     */
    @Override
    protected void onStart() {
        super.onStart();

        
        map.setOnMapLongClickListener(new OnMapLongClickListener()
        {
            @Override
            public void onMapLongClick(LatLng point)
	        {
            	MarkerOptions position = new MarkerOptions().anchor(0.5f, 0.5f)
	                      .position(point)
	                      .title("Marker")
	                      .snippet("latitude: " + point.latitude + " longtitude: " + point.longitude);
            	Marker options = map.addMarker(position);
            	
	        }
        });
        map.setOnInfoWindowClickListener(new OnInfoWindowClickListener(){

			@Override
			public void onInfoWindowClick(final Marker m) 
			{		
				alert = new AlertDialog.Builder(context);
		        
		        alert.setTitle("Placeit information");
	            alert.setMessage("Title: ");

		        // Set an EditText view to get user input 
		        final EditText input = new EditText(context);
		        String title;
		        alert.setView(input);
		        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int whichButton) {
			          String value = input.getText().toString();
			          m.setTitle(value);
		          }
		        });

		        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		          public void onClick(DialogInterface dialog, int whichButton) {
		            // Canceled.
		          }
		        });
		        alert.show();
		        m.showInfoWindow();
			}
        	
        });
        // Connect the client.
        myLocationClient.connect();
        

    }
    
    public void initializeAlert(){
    	
    }
    /*
     * Called when the Activity is no longer visible.
     */
    @Override
    protected void onStop() {
        // Disconnecting the client invalidates it.
        myLocationClient.disconnect();
        super.onStop();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded() {
        if (map != null) {
            return;
        }
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        if (map == null) {
            return;
        }
        // Initialize map options. For example:
        map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
    }

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
		
	}
    
   
}