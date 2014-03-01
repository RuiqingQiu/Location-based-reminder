/*
 * Filename: MainActivity.java
 * Hello world
 */
package com.cse110team14.placeit;

import com.cse110team14.placeit.PulledListActivity;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import com.cse110team14.placeit.GeocodeJSONParser;
import org.json.JSONObject;

import com.cse110team14.placeit.R;
import com.cse110team14.placeit.model.PlaceIt;
import com.cse110team14.placeit.view.PlaceItsInfoWindow;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.GoogleMap.CancelableCallback;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.FragmentActivity;

public class MainActivity extends Activity implements
CancelableCallback,
LocationListener,
GooglePlayServicesClient.ConnectionCallbacks,
GooglePlayServicesClient.OnConnectionFailedListener 
{
	
	// Global constants
    /*
     * Define a request code to send to Google Play services
     * This code is returned in Activity.onActivityResult
     */
	private GoogleMap map;
	public static List<PlaceIt> activeList = new ArrayList<PlaceIt>();
	public static List<PlaceIt> pullDown = new ArrayList<PlaceIt>();

	public static Boolean notificationSent = false;
	
	//For location update in mainActivity
	private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000; 
	// Milliseconds per second 
	private static final int MILLISECONDS_PER_SECOND = 1000; 
	// Update frequency in seconds 
	public static final int UPDATE_INTERVAL_IN_SECONDS = 30; 
	// Update frequency in milliseconds 
	private static final long UPDATE_INTERVAL = MILLISECONDS_PER_SECOND * UPDATE_INTERVAL_IN_SECONDS; 
	// The fastest update frequency, in seconds 
	private static final int FASTEST_INTERVAL_IN_SECONDS = 30; 
	// A fast frequency ceiling in milliseconds 
	private static final long FASTEST_INTERVAL = MILLISECONDS_PER_SECOND * FASTEST_INTERVAL_IN_SECONDS; 
	
	// Define an object that holds accuracy and frequency parameters 
	LocationRequest mLocationRequest; 
	boolean mUpdatesRequested; 
	private SharedPreferences.Editor mEditor; 
	private SharedPreferences mPrefs; 
	private LocationClient mLocationClient; 
	 
	//Initilize the mMarkers list
	private List<Marker> mMarkers = new ArrayList<Marker>();
	private Iterator<Marker> marker;
	private String activeListFile = "saved_placeits.dat";
	private String pulldownListFile = "pulldown_placeits.dat";
	
	
	//Variables for all Widgets in MainActivity
	AlertDialog.Builder alert;
	Button mBtnFind;
	Button retrackBtn;
	Button active;
	Button pulled;
	Button create;
	Button test;
	EditText etPlace;
	final Context context = this;
	
	
	//Call during the activity is created
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	if (notificationSent == true){
    		notificationSent = false;
    		Intent intent = new Intent(MainActivity.this, PulledListActivity.class);
    		startActivity(intent);
    	}
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpMapIfNeeded();
        
        // Getting reference to the find button
        mBtnFind = (Button) findViewById(R.id.btn_show);
        retrackBtn = (Button)findViewById(R.id.retrack);
        active = (Button)findViewById(R.id.active);
        pulled = (Button)findViewById(R.id.pulled);
        create = (Button)findViewById(R.id.create);
        test = (Button)findViewById(R.id.test);
        map.setMyLocationEnabled(true);
        map.setInfoWindowAdapter(new PlaceItsInfoWindow(getLayoutInflater().inflate(R.layout.placeits_info_window, null)));
        
        //When the app is opened, read in the file and populate the placeit list
//        onCreateReadFile();
        readFileToList(activeListFile, activeList);
        readFileToList(pulldownListFile, pullDown);
        Log.e("hello",""+activeList.size());
        // Getting reference to EditText
        etPlace = (EditText) findViewById(R.id.et_place);
        
        
        //Start the service for checking location onCreate
        startService(new Intent(this, LocationService.class));
 
        /* This is comment out due to testing propuse 
        test.setOnClickListener(new OnClickListener(){
        	public void onClick(View v){
        		PlaceIts.clear();
        		PlaceIts.add(new PlaceIt("1", "1","red",new LatLng(18,9),"11/12/2013","Feb 9, 2014 4:57:52 PM"));
        		PlaceIts.add(new PlaceIt("2", "2","blue",new LatLng(18,10),"11/13/2013","Feb 10, 2014 4:57:52 PM"));
        		PlaceIts.add(new PlaceIt("3", "3","blue",new LatLng(18,11),"11/14/2013","Feb 11, 2014 4:57:52 PM"));
        		PlaceIts.add(new PlaceIt("4", "4","green",new LatLng(18,12),"11/15/2013","Feb 12, 2014 4:57:52 PM"));
        		PlaceIts.add(new PlaceIt("5", "5","green",new LatLng(19,9),"11/16/2013","Feb 13, 2014 4:57:52 PM"));
        		PlaceIts.add(new PlaceIt("6", "6","orange",new LatLng(19,10),"11/17/2013","Feb 14, 2014 4:57:52 PM"));
        		PlaceIts.add(new PlaceIt("7", "7","orance",new LatLng(19,11),"11/18/2013","Feb 15, 2014 4:57:52 PM"));
        		createNotification(null, PlaceIts.get(0));
        		Log.e("hello", PlaceIts.get(0).getDateRemindedToCalendar().toString());
        	}
        });*/
        
        // Setting click event listener for the find button
        mBtnFind.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // Getting the place entered
                String location = etPlace.getText().toString();
 
                if(location==null || location.equals("")){
                    Toast.makeText(getBaseContext(), "No Place is entered", Toast.LENGTH_SHORT).show();
                    return;
                }
 
                String url = "https://maps.googleapis.com/maps/api/geocode/json?";
 
                try {
                    // encoding special characters like space in the user input place
                    location = URLEncoder.encode(location, "utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
 
                String address = "address=" + location;
 
                String sensor = "sensor=false";
 
                // url , from where the geocoding data is fetched
                url = url + address + "&" + sensor;
 
                // Instantiating DownloadTask to get places from Google Geocoding service
                // in a non-ui thread
                DownloadTask downloadTask = new DownloadTask();
 
                // Start downloading the geocoding places
                downloadTask.execute(url);
                
            }
        });
        
        //Set up the retrack button for keeping track of all retrack button
        retrackBtn.setOnClickListener(new OnClickListener(){
        	 @Override
    		 public void onClick(View v) {
        		 /* Checking errors, if the iterator was never initalized, pop up an dialog
        		  * box and show what the error is
        		  */
        		 if(marker == null){
        			AlertDialog.Builder temp = new AlertDialog.Builder(context);
     		        temp.setTitle("PlaceIts not found");
     		        temp.setMessage("There's no PlaceIts to be retracked. ");
     		        temp.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
     			        public void onClick(DialogInterface dialog, int whichButton) {
     				         return;
     			          }
     			        });
     		        temp.show();
     		        return;
        		 }
    			 if (marker.hasNext()) {
    				 Marker current = marker.next();
    				 Log.e("test", ""+current.getTitle());
    				 map.animateCamera(CameraUpdateFactory.newLatLng(current.getPosition()), 2000, MainActivity.this);
    				 current.showInfoWindow();
    				 
    			 }
    			 else{
    				 marker = mMarkers.iterator();
    				 Marker current = marker.next();
    				 Log.e("test", ""+current.getTitle());
    				 map.animateCamera(CameraUpdateFactory.newLatLng(current.getPosition()), 2000, MainActivity.this);
    				 current.showInfoWindow();
    			 }
    		 }
        });
        
        //Initialize the create button 
        create.setOnClickListener(new OnClickListener()
        {
			@Override
			public void onClick(View v) 
			{
				AlertDialog.Builder alert1 = new AlertDialog.Builder(context);
		        
		        alert1.setTitle("Create A PlaceIt");

		        // Get the layout inflater
		        LayoutInflater inflater = getLayoutInflater();
		        // Set an EditText view to get user input 
		        
		        // Inflate and set the layout for the dialog
		        final View view = inflater.inflate(R.layout.create_placeits, null);
		        final EditText title = (EditText)view.findViewById(R.id.title);
		        final EditText description = (EditText)view.findViewById(R.id.description);
		        final EditText location = (EditText)view.findViewById(R.id.location);
		        final EditText color = (EditText)view.findViewById(R.id.color);
		        final RadioGroup rg = (RadioGroup)view.findViewById(R.id.radioGroup);
		        final DatePicker date = (DatePicker)view.findViewById(R.id.datePicker);
		        // Pass null as the parent view because its going in the dialog layout
		        alert1.setView(view);
		        alert1.setPositiveButton("Create the PlaceIt", new DialogInterface.OnClickListener() {
		        	@SuppressLint("DefaultLocale")
					public void onClick(DialogInterface dialog, int whichButton) {
		        		
		        		String placeItTitle = title.getText().toString();
		        		if(placeItTitle.isEmpty()){
			        	  AlertDialog.Builder temp = initializeAlert("No Title Entered", "Please enter a title :)");
		     		      temp.show();
		     		      return;
		        		}
		        		String placeItDescription = description.getText().toString();
		        		if(placeItDescription.isEmpty()){
			        	  AlertDialog.Builder temp = initializeAlert("No Description Entered", "Please enter a description :)");
		     		      temp.show();
		     		      return;
		        		}
		        		String [] splited = location.getText().toString().split("\\s*,\\s*");
		        		if (location.toString().isEmpty() ||
		        				splited.length != 2 ||
		        				Double.parseDouble(splited[0]) > 90.0 || 
		        				Double.parseDouble(splited[0]) < -90.0 || 
		        				Double.parseDouble(splited[1]) > 180 || 
		        				Double.parseDouble(splited[1]) < -180){
		        			AlertDialog.Builder temp = initializeAlert("Not a valid location", "Please enter a valid location :)");
			     		    temp.show();
			     		    return;
		        		}
		        		LatLng position = new LatLng(Double.parseDouble(splited[0]), Double.parseDouble(splited[1]));
		        		int placeItType = 1;
		        		/* Get the selected radio button id */
		        		switch (rg.getCheckedRadioButtonId()){
		        			case R.id.minutely:
		        				placeItType = 2;
		        				break;
		        			case R.id.weekly:
		        				placeItType = 3;
		        				break;
		        			case R.id.twoweekly:
		        				placeItType = 4;
		        				break;
		        			case R.id.threeweekly:
		        				placeItType = 5;
		        				break;
		        			case R.id.monthly:
		        				placeItType = 6;
		        				break;
		        		}
		        		
		        		
		        		String dateToBeReminded = (date.getMonth()+1) + "/" + date.getDayOfMonth() + "/" + date.getYear();	          
		        		String currentDateTime = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
			          
		        		//Check if the date is valid
		        		if (checkDate(dateToBeReminded) == false){
		        			AlertDialog.Builder temp = initializeAlert("Enteted Date is not valid", "Please enter a valid date :)");
		        			temp.show();
		        			return;
		        		}
			          
		        		String markerColor = color.getText().toString();
		        		if( markerColor.toLowerCase().equals("red") 
		        			|| markerColor.toLowerCase().equals("blue")
		        			|| markerColor.toLowerCase().equals("azure")
		        			|| markerColor.toLowerCase().equals("cyan")
					        || markerColor.toLowerCase().equals("green")
					        || markerColor.toLowerCase().equals("megenta")
					        || markerColor.toLowerCase().equals("orange")
					        || markerColor.toLowerCase().equals("violet")
					        || markerColor.toLowerCase().equals("rose")
					        || markerColor.toLowerCase().equals("yellow")
					   )
			           {}  
			           else{
			        	  AlertDialog.Builder temp = initializeAlert("Entered Color is not valid", "Please enter a valid color");
		     		      temp.show();
		     		      return;
			          }
			          Marker m = map.addMarker(new MarkerOptions().title(placeItTitle).position(position));
			          //All information entries are valid
			          m.setSnippet(placeItDescription + "###"
			        		  + dateToBeReminded +"###" + currentDateTime);
			          
			          if(markerColor.toLowerCase().equals("red"))
			        	  m.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
			          else if(markerColor.toLowerCase().equals("blue"))
			        	  m.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
		        	  else if(markerColor.toLowerCase().equals("azure"))
		        		  m.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
		        	  else if(markerColor.toLowerCase().equals("cyan"))
			              m.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
			          else if(markerColor.toLowerCase().equals("green"))
			              m.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
			          else if(markerColor.toLowerCase().equals("magenta"))
			              m.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
			          else if(markerColor.toLowerCase().equals("orange"))
			              m.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
			          else if(markerColor.toLowerCase().equals("violet"))
			              m.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
			          else if(markerColor.toLowerCase().equals("rose"))
			              m.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
			          else if(markerColor.toLowerCase().equals("yellow"))
			              m.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
			          mMarkers.add(m);
			          PlaceIt tmp = new PlaceIt(placeItTitle, placeItDescription, markerColor, m.getPosition() ,dateToBeReminded, currentDateTime);
			          tmp.setPlaceItType(placeItType);
			          activeList.add(tmp);
			          marker = mMarkers.iterator();
			          map.moveCamera(CameraUpdateFactory.newLatLngZoom(position,17));
		              map.animateCamera(CameraUpdateFactory.zoomIn());
		        	}
		        });//End of positive button
		        alert1.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
						  public void onClick(DialogInterface dialog, int whichButton) {
						  }
			    });
			        
		        /* Rather than delete, we will set the marker to be invisible and 
				 * it's never added to the list
				 */
				alert1.show();
			}
			
				
        });//End of on create

        //Show markers in the placeit list when app is open
        ShowMarkerWhenAppOpen();
        
        // Create the LocationRequest object 
     	mLocationRequest = LocationRequest.create(); 
     	// Use high accuracy
     	mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY); 
     	// Set the update interval to 5 seconds 
     	mLocationRequest.setInterval(UPDATE_INTERVAL); 
     	// Set the fastest update interval to 1 second 
     	mLocationRequest.setFastestInterval(FASTEST_INTERVAL); 
     				 
     	// Open the shared preferences 
     	mPrefs = getSharedPreferences("SharedPreferences",Context.MODE_PRIVATE); 
     	// Get a SharedPreferences editor 
     	mEditor = mPrefs.edit(); 
     				 
     	// Start with updates turned on 
     	mUpdatesRequested = true; 
     				 
     	/* 
     	 * Create a new location client, using the enclosing class to 
     	 * handle callbacks. 
     	 */ 
     	 mLocationClient = new LocationClient(this, this, this); 
     	LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            Toast.makeText(this, "GPS is Enabled in your devide", Toast.LENGTH_SHORT).show();
        }else{
            showGPSDisabledAlertToUser();
        }
   
    }
    
    /**
     * A Private helper method that is called when the gps is disabled
     */
    private void showGPSDisabledAlertToUser(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("GPS is disabled in your device. Would you like to enable it?")
        .setCancelable(false)
        .setPositiveButton("Goto Settings Page To Enable GPS",
                new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int id){
                Intent callGPSSettingIntent = new Intent(
                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(callGPSSettingIntent);
            }
        });
        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int id){
                dialog.cancel();
            }
        });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }
   
    /*
    @SuppressLint("NewApi")
	public void createNotification(View view, PlaceIt p) {
        // Prepare intent which is triggered if the
        // notification is selected

    	Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);

        // Build notification
        // Actions are just fake
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Notification noti = new Notification.Builder(this)
            .setContentTitle("PlaceIt Notificaiton: " + p.getTitle())
            .setContentText("Description: " + p.getDescription())
            .setSmallIcon(R.drawable.ic_launcher)
            .setContentIntent(pIntent)
            .setSound(alarmSound)
            .build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // hide the notification after its selected
        noti.flags |= Notification.FLAG_AUTO_CANCEL;

        notificationManager.notify(0, noti);

      }*/
    
    /*
     * Called when the Activity becomes visible.
     */
    @Override
    protected void onStart() {
        super.onStart();
        mLocationClient.connect();
        /*
         * Function for user press on map for a long time, it will create a Marker
         * at where the user pressed on
         */
        map.setOnMapLongClickListener(new OnMapLongClickListener()
        {
            @Override
            public void onMapLongClick(LatLng point)
	        {
            	MarkerOptions position = new MarkerOptions()
	                      .position(point)
	                      .title("Click to add a Title")
	                      .snippet("latitude: " + point.latitude +"###" + "\n longtitude: " + point.longitude + 
	                    		  "###" + "\nClick on info window to enter your reminder");
            	map.addMarker(position);
	        }
        });
        /*
         * Method for user clicked on the info window, it will ask for entering informations
         * Only appear if it's not a PlaceIt yet
         */
        map.setOnInfoWindowClickListener(new OnInfoWindowClickListener(){

			@Override
			public void onInfoWindowClick(final Marker m) 
			{		
				//First case for the marker, if it's already a placeit
				if(mMarkers.contains(m))
				{
					return;
				}
				
				alert = new AlertDialog.Builder(context);
		        
		        alert.setTitle("Placeit information");

		        // Get the layout inflater
		        LayoutInflater inflater = getLayoutInflater();
		        // Set an EditText view to get user input 
		        
		        // Inflate and set the layout for the dialog
		        final View v = inflater.inflate(R.layout.create_placeits, null);
		        final EditText title = (EditText)v.findViewById(R.id.title);
		        final EditText description = (EditText)v.findViewById(R.id.description);
		        final EditText location = (EditText)v.findViewById(R.id.location);
		        location.setText("" + m.getPosition().latitude + ", " + m.getPosition().latitude, TextView.BufferType.EDITABLE);
		        //final EditText date = (EditText)v.findViewById(R.id.date);
		        final DatePicker date = (DatePicker)v.findViewById(R.id.datePicker);
		        final RadioGroup rg = (RadioGroup)v.findViewById(R.id.radioGroup);
		        final EditText color = (EditText)v.findViewById(R.id.color);
		        // Pass null as the parent view because its going in the dialog layout
		        alert.setView(v);
		        alert.setPositiveButton("Create the PlaceIt", new DialogInterface.OnClickListener() {
		        @SuppressLint("DefaultLocale")
				public void onClick(DialogInterface dialog, int whichButton) {
			          String placeItTitle = title.getText().toString();
			          if(placeItTitle.isEmpty()){
			        	  AlertDialog.Builder temp = initializeAlert("No Title Entered", "Please enter a title :)");
		     		      temp.show();
		     		      return;
			          }
			          String placeItDescription = description.getText().toString();
			          if(placeItDescription.isEmpty()){
			        	  AlertDialog.Builder temp = initializeAlert("No Description Entered", "Please enter a description :)");
		     		      temp.show();
		     		      return;
			          }
			          
			          /* Getting the placeit type */
			          int placeItType = 1;
		        		/* Get the selected radio button id */
		        		switch (rg.getCheckedRadioButtonId()){
		        			case R.id.minutely:
		        				placeItType = 2;
		        				break;
		        			case R.id.weekly:
		        				placeItType = 3;
		        				break;
		        			case R.id.twoweekly:
		        				placeItType = 4;
		        				break;
		        			case R.id.threeweekly:
		        				placeItType = 5;
		        				break;
		        			case R.id.monthly:
		        				placeItType = 6;
		        				break;
		        		}
			          
			          String dateToBeReminded = (date.getMonth() + 1) + "/" + date.getDayOfMonth() + "/" + date.getYear();         
			          String currentDateTime = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
			          
			          //Check if the date is valid
			          if (checkDate(dateToBeReminded) == false){
			        	  AlertDialog.Builder temp = initializeAlert("Entered Date is not valid", "Please enter a valid date :)");
		     		      temp.show();
		     		      return;
			          }
			          
			          String markerColor = color.getText().toString();
			          if( markerColor.toLowerCase().equals("red") 
			           || markerColor.toLowerCase().equals("blue")
			           || markerColor.toLowerCase().equals("azure")
		        	   || markerColor.toLowerCase().equals("cyan")
			           || markerColor.toLowerCase().equals("green")
			           || markerColor.toLowerCase().equals("megenta")
			           || markerColor.toLowerCase().equals("orange")
			           || markerColor.toLowerCase().equals("violet")
			           || markerColor.toLowerCase().equals("rose")
			           || markerColor.toLowerCase().equals("yellow")
			          )
			          {}  
			          else{
			        	  AlertDialog.Builder temp = initializeAlert("Entered Color is not valid", "Please enter a valid color");
		     		      temp.show();
		     		      return;
			          }
			          //All information entries are valid
			          m.setTitle(placeItTitle);
			          m.setSnippet(placeItDescription + "###"
			        		  + dateToBeReminded +"###" + currentDateTime);
			          
			          if(markerColor.toLowerCase().equals("red"))
			        	  m.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
			          else if(markerColor.toLowerCase().equals("blue"))
			        	  m.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
		        	  else if(markerColor.toLowerCase().equals("azure"))
		        		  m.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
        			  else if(markerColor.toLowerCase().equals("cyan"))
			              m.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
			          else if(markerColor.toLowerCase().equals("green"))
			              m.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
			          else if(markerColor.toLowerCase().equals("magenta"))
			              m.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
			          else if(markerColor.toLowerCase().equals("orange"))
			              m.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
			          else if(markerColor.toLowerCase().equals("violet"))
			              m.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
			          else if(markerColor.toLowerCase().equals("rose"))
			              m.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
			          else if(markerColor.toLowerCase().equals("yellow"))
			              m.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
			          
			          mMarkers.add(m);
			          PlaceIt tmp = new PlaceIt(placeItTitle, placeItDescription, markerColor, m.getPosition() ,dateToBeReminded, currentDateTime);
			          //Set the type of the placeit
			          tmp.setPlaceItType(placeItType);
			          Log.e("hello", "the type is" + tmp.getPlaceItType());
			          activeList.add(tmp);
			          marker = mMarkers.iterator();
			          
		          }
		        });
		        //If the marker is stored as a placeit
		        if(mMarkers.contains(m)){
		        	//The cancel button will leave it as it is
			        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			          public void onClick(DialogInterface dialog, int whichButton) {
			          }
			        });
		        }
		        else{
		        	//The cancel button will hide the window
		        	alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				      public void onClick(DialogInterface dialog, int whichButton) {
				    	  m.setVisible(false);
				      }
				    });
		        }
		        /* Rather than delete, we will set the marker to be invisible and 
		         * it's never added to the list
		         */
		        alert.show();
		        m.showInfoWindow();
			}
        	
        });
    }
    
    //Return the ActiveList
    public static List<PlaceIt> getActiveList(){
    	return activeList;
    }
    //Return the pullDown List
    public static List<PlaceIt> getPullDownList(){
    	return pullDown;
    }
    
    /**
     * Method use to check if user entered a correct date and format
     * @param date
     * @return
     */
    public boolean checkDate(String date){
    	DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
    	formatter.setLenient(false);
    	try{
    		formatter.parse(date);
    	}
    	catch(ParseException e){
    		return false;
    	}
    	return true;
    }
    
    /**
     * This method is called everytime the app is reopened, it will use the list and
     * repost all the markers on the map
     */
    public void ShowMarkerWhenAppOpen(){
    	Iterator<PlaceIt> itr = activeList.iterator();
    	map.clear();
    	mMarkers.clear();
    	while(itr.hasNext()){
        	   PlaceIt tmp = itr.next();
        	   Marker m = map.addMarker(new MarkerOptions()
        	   .position(tmp.getLocation())
        	   .title(tmp.getTitle())
        	   .snippet(tmp.getDescription() + "###"
		        		  + tmp.getDateReminded() +"###" + tmp.getDate()));
        	   String color = tmp.getColor().toLowerCase();
        	   Log.e("test",tmp.getTitle()+tmp.getColor());
        	   if(color.equals("red"))
		        	  m.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
		       else if(color.equals("blue"))
		        	  m.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
	           else if(color.equals("azure"))
	        		  m.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
	           else if(color.equals("cyan"))
		              m.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
		       else if(color.equals("green"))
		              m.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
		       else if(color.equals("magenta"))
		              m.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
		       else if(color.equals("orange"))
		              m.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
		       else if(color.equals("violet"))
		              m.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
		       else if(color.equals("rose"))
		              m.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
		       else if(color.equals("yellow"))
		              m.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
        	   mMarkers.add(m);
    	}	   
    	marker = mMarkers.iterator();
    }

	public void readFileToList(String file, List<PlaceIt> list) {
		list.clear();
		try {
			FileInputStream in = openFileInput(file);
			InputStreamReader isr = new InputStreamReader(in);
			BufferedReader reader = new BufferedReader(isr);
			String readString = reader.readLine();
			while (readString != null) {
				// putPullDownReadFromFileToLists(readString);
				readStringToList(readString, list);
				readString = reader.readLine();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
    
//    /**
//     * This method will called when the app is in onCreate, it will read in the placeits
//     * saved file and put them into the list
//     */
//    public void onCreateReadFile(){
//    	PlaceIts.clear();
//    	try {
//        	FileInputStream in = openFileInput(activeListFile);
//        	InputStreamReader isr = new InputStreamReader ( in ) ;
//        	BufferedReader reader = new BufferedReader(isr);
//        	String readString = reader.readLine () ;
//        	while (readString != null){
////        	   putPlaceItsReadFromFileToLists(readString);
//        	   readStringToList(readString, PlaceIts);
//        	   readString = reader.readLine();
//            }
//        } catch (FileNotFoundException e) {
//        	e.printStackTrace();
//        } catch (IOException e) {
//        	e.printStackTrace();
//        }
//    	
//    	pullDown.clear();
//    	try {
//        	FileInputStream in = openFileInput(pulldownListFile);
//        	InputStreamReader isr = new InputStreamReader ( in ) ;
//        	BufferedReader reader = new BufferedReader(isr);
//        	String readString = reader.readLine () ;
//        	while (readString != null){
//        	   Log.e("pulldown", readString);
//        	   Log.e("pulldown", "Another one");
////        	   putPullDownReadFromFileToLists(readString);
//        	   readStringToList(readString, pullDown);
//        	   readString = reader.readLine();
//            }
//        	
//        } catch (FileNotFoundException e) {
//        	e.printStackTrace();
//        } catch (IOException e) {
//        	e.printStackTrace();
//        }
//    }
    
//    /**
//     * A helper function for onCreateReadFile, it will get a string that encodes a placeit
//     * and put it into the list
//     * @param str
//     */
//    public void putPlaceItsReadFromFileToLists(String str){
//       String []splited = str.split("###");
// 	   String placeItTitle = splited[0];
// 	   String description = splited[1];
// 	   String dateToBeReminded = splited[2];
// 	   String postDate = splited[3];
// 	   LatLng location = new LatLng(Double.parseDouble(splited[4]), Double.parseDouble(splited[5]));
// 	   String color = splited[6];
// 	   int type = Integer.parseInt(splited[7]);
// 	   int sneezeType = Integer.parseInt(splited[8]);
//	   PlaceIt tmp = new PlaceIt(placeItTitle, description, color, location, dateToBeReminded, postDate);
//	   tmp.setPlaceItType(type);
//	   tmp.setSneezeType(sneezeType);
//       PlaceIts.add(tmp);
//    }
//    
//    /**
//     * A helper function for onCreateReadFile, it will get a string that encodes a placeit
//     * and put it into the list
//     * @param str
//     */
//    public void putPullDownReadFromFileToLists(String str){
//       String []splited = str.split("###");
// 	   String placeItTitle = splited[0];
// 	   String description = splited[1];
// 	   String dateToBeReminded = splited[2];
// 	   String postDate = splited[3];
// 	   LatLng location = new LatLng(Double.parseDouble(splited[4]), Double.parseDouble(splited[5]));
// 	   String color = splited[6];
// 	   int type = Integer.parseInt(splited[7]);
// 	   int sneezeType = Integer.parseInt(splited[8]);
// 	   PlaceIt tmp = new PlaceIt(placeItTitle, description, color, location, dateToBeReminded, postDate);
// 	   tmp.setPlaceItType(type);
// 	   tmp.setSneezeType(sneezeType);
//       pullDown.add(tmp);
//    }
    
    /**
     * A helper function for onCreateReadFile, it will get a string that encodes a placeit
     * and put it into the list
     * @param str
     */
    public void readStringToList(String str, List<PlaceIt> list){
       String []splited = str.split("###");
 	   String placeItTitle = splited[0];
 	   String description = splited[1];
 	   String dateToBeReminded = splited[2];
 	   String postDate = splited[3];
 	   LatLng location = new LatLng(Double.parseDouble(splited[4]), Double.parseDouble(splited[5]));
 	   String color = splited[6];
 	   int type = Integer.parseInt(splited[7]);
 	   int sneezeType = Integer.parseInt(splited[8]);
 	   PlaceIt tmp = new PlaceIt(placeItTitle, description, color, location, dateToBeReminded, postDate);
 	   tmp.setPlaceItType(type);
 	   tmp.setSneezeType(sneezeType);
       list.add(tmp);
    }
    
    
    /**
     * Method to build a alert dialog for error condition, mainly used in user entries for
     * Placeit information
     * @param title
     * @param message
     * @return The dialog box that contains the title and message
     */
    public AlertDialog.Builder initializeAlert(String title, String message){
    	AlertDialog.Builder tmp = new AlertDialog.Builder(context);
    	tmp.setTitle(title);
	    tmp.setMessage(message);
	    tmp.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int whichButton) {
		          }
		        });
    	return tmp;
    }
    /*
     * Called when the Activity is no longer visible.
     */
    @Override
    protected void onStop() {
        super.onStop();
//    	saveActiveListPlaceIt();
//    	savePulldownListPlaceIt();
        saveList(activeList, activeListFile);
        saveList(pullDown, pulldownListFile);
    	
    }
//    /**
//     * Save the active list of the placeit, called in onStop method
//     */
//    public void saveActiveListPlaceIt(){
//    	try {
//    		FileOutputStream out = openFileOutput(activeListFile, Context.MODE_PRIVATE);
//    		//write place its to file
//    		Iterator<PlaceIt> placeitsIterator = PlaceIts.iterator();
//    		while(placeitsIterator.hasNext()){
//    			PlaceIt element = placeitsIterator.next();
//    			String str = element.getTitle() + "###" + element.getDescription() + "###" + element.getDateReminded()
//    					+"###" + element.getDate() + "###" + element.getLocation().latitude +"###" +
//    					element.getLocation().longitude + "###" + element.getColor()+ "###" + element.getPlaceItType() + 
//    					"###" + element.getSneezeType() + "\n"; 
//    			out.write(str.getBytes());
//    		}
//    		out.close();
//    	} catch (FileNotFoundException e) {
//    		e.printStackTrace();
//    	} catch (IOException e) {
//    		e.printStackTrace();
//    	}
//    }
//    
//    /**
//     * Save the pulldown list of the placeit, called in onStop method
//     */
//    public void savePulldownListPlaceIt(){
//    	try {
//    		FileOutputStream out = openFileOutput(pulldownListFile, Context.MODE_PRIVATE);
//    		//write place its to file
//    		Iterator<PlaceIt> pulldownIterator = pullDown.iterator();
//    		while(pulldownIterator.hasNext()){
//    			PlaceIt element = pulldownIterator.next();
//    			String str = element.getTitle() + "###" + element.getDescription() + "###" + element.getDateReminded()
//    					+"###" + element.getDate() + "###" + element.getLocation().latitude +"###" +
//    					element.getLocation().longitude + "###" + element.getColor()+ "###" + element.getPlaceItType() + 
//    					"###" + element.getSneezeType() + "\n"; 
//    			out.write(str.getBytes());
//    		}
//    		out.close();
//    	} catch (FileNotFoundException e) {
//    		e.printStackTrace();
//    	} catch (IOException e) {
//    		e.printStackTrace();
//    	}
//    }
    
    /**
     * Save specific list of placeits to corresponding file, called in onStop
     *  method
     */
    public void saveList(List<PlaceIt> list, String file){
    	try {
    		FileOutputStream out = openFileOutput(file, Context.MODE_PRIVATE);
    		//write place its to file
    		Iterator<PlaceIt> itr = list.iterator();
    		while(itr.hasNext()){
    			PlaceIt element = itr.next();
    			String str = element.getTitle() + "###" + element.getDescription() + "###" + element.getDateReminded()
    					+"###" + element.getDate() + "###" + element.getLocation().latitude +"###" +
    					element.getLocation().longitude + "###" + element.getColor()+ "###" + element.getPlaceItType() + 
    					"###" + element.getSneezeType() + "\n"; 
    			out.write(str.getBytes());
    		}
    		out.close();
    	} catch (FileNotFoundException e) {
    		e.printStackTrace();
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    }
    
    
    /**
     * Called when app is on pause, it will start the location service
     */
    protected void onPause(){
    	super.onPause();
		// Save the current setting for updates 
		mEditor.putBoolean("KEY_UPDATES_ON", mUpdatesRequested); 
		mEditor.commit(); 
		//mLocationClient.disconnect();
    	startService(new Intent(this, LocationService.class));
    }
    
    /**
     * Called when app is resumed, it will check if map need to be set up and update all the markers
     */
    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        ShowMarkerWhenAppOpen();
        /* 
		 * Get any previous setting for location updates 
		 * Gets "false" if an error occurs 
		 */ 
		 if (mPrefs.contains("KEY_UPDATES_ON")) { 
			 mUpdatesRequested = 
			 mPrefs.getBoolean("KEY_UPDATES_ON", false); 
			 
		 // Otherwise, turn off location updates 
		 } else { 
			 mEditor.putBoolean("KEY_UPDATES_ON", false); 
			 mEditor.commit(); 
		 }
        
    }
    /**
     * Method name: setUpMapIfNeeded
     * Description: This method will be called when the map start up, it will check if
     * the map has set up, if not create a map based on the fragment in the xml file
     **/
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
	public void onFinish() {
	}

    /**
     * Method for going to ActiveListActivity
     * @param v
     */
	public void goToActiveList(View v){
		startActivity(new Intent(MainActivity.this, ActiveListActivity.class));
	}
	
	/**
	 * Method for going to pulledListActivity
	 * @param v
	 */
	public void goToPulledList(View v){
		startActivity(new Intent(MainActivity.this, PulledListActivity.class));
	}
	

	@Override
	public void onCancel() {
		
	}
	// Define the callback method that receives location updates 
    @Override 
    public void onLocationChanged(Location location) { 
	  // Report to the UI that the location was updated 
	  map.animateCamera(CameraUpdateFactory.newLatLngZoom(new 
			LatLng(location.getLatitude(),location.getLongitude()), 17)); 
    }
	
	
	
	/* BEGIN OF HELPER FUNCTIONS AND CLASSES FOR GETTING LOCATION DATA FROM GOOGLE */
	private String downloadUrl(String strUrl) throws IOException{
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);
            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();
 
            // Connecting to url
            urlConnection.connect();
 
            // Reading data from url
            iStream = urlConnection.getInputStream();
 
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
 
            StringBuffer sb = new StringBuffer();
 
            String line = "";
            while( ( line = br.readLine()) != null){
                sb.append(line);
            }
 
            data = sb.toString();
            br.close();
 
        }catch(Exception e){
            Log.d("Exception while downloading url", e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
 
        return data;
    }
    /** A class, to download Places from Geocoding webservice */
    @SuppressLint("NewApi")
	private class DownloadTask extends AsyncTask<String, Integer, String>{
 
        String data = null;
 
        // Invoked by execute() method of this object
        @Override
        protected String doInBackground(String... url) {
            try{
                data = downloadUrl(url[0]);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;
        }
 
        // Executed after the complete execution of doInBackground() method
        @Override
        protected void onPostExecute(String result){
 
            // Instantiating ParserTask which parses the json data from Geocoding webservice
            // in a non-ui thread
            ParserTask parserTask = new ParserTask();
 
            // Start parsing the places in JSON format
            // Invokes the "doInBackground()" method of the class ParseTask
            parserTask.execute(result);
        }
    }
 
    /** A class to parse the Geocoding Places in non-ui thread */
    @SuppressLint("NewApi")
	class ParserTask extends AsyncTask<String, Integer, List<HashMap<String,String>>>{
 
        JSONObject jObject;
        LatLng location;
 
        // Invoked by execute() method of this object
        @Override
        protected List<HashMap<String,String>> doInBackground(String... jsonData) {
 
            List<HashMap<String, String>> places = null;
            GeocodeJSONParser parser = new GeocodeJSONParser();
 
            try{
                jObject = new JSONObject(jsonData[0]);
 
                /** Getting the parsed data as a an ArrayList */
                places = parser.parse(jObject);
 
            }catch(Exception e){
                Log.d("Exception",e.toString());
            }
            return places;
        }
 
        // Executed after the complete execution of doInBackground() method
        @Override
        protected void onPostExecute(List<HashMap<String,String>> list){
 
            // Clears all the existing markers
            //map.clear();
 
            for(int i=0;i<list.size();i++){
 
                // Creating a marker
                MarkerOptions markerOptions = new MarkerOptions();
 
                // Getting a place from the places list
                HashMap<String, String> hmPlace = list.get(i);
 
                // Getting latitude of the place
                double lat = Double.parseDouble(hmPlace.get("lat"));
 
                // Getting longitude of the place
                double lng = Double.parseDouble(hmPlace.get("lng"));
 
                // Getting name
                String name = hmPlace.get("formatted_address");
 
                LatLng latLng = new LatLng(lat, lng);
 
                // Setting the position for the marker
                markerOptions.position(latLng);
 
                // Setting the title for the marker
                markerOptions.title(name + " (Click to enter Information)");
                //markerOptions.snippet("Click here to Enter Information");
                // Placing a marker on the touched position
                Marker tmp = map.addMarker(markerOptions);
                /*mMarkers.add(tmp);
                marker=mMarkers.iterator();*/
                tmp.showInfoWindow();
                
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,17));
                map.animateCamera(CameraUpdateFactory.zoomIn());
                
                // Locate the first location
                if(i==0)
                    map.animateCamera(CameraUpdateFactory.newLatLng(latLng));
  
            }
        }
    }//End of class
	/* END OF HELPER FUNCTIONS AND CLASSES FOR GETTING LOCATION DATA FROM GOOGLE */

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		 /* 
		 * Google Play services can resolve some errors it detects. 
		 * If the error has a resolution, try sending an Intent to 
		 * start a Google Play services activity that can resolve 
		 * error. 
		 */ 
		 if (connectionResult.hasResolution()) { 
			 try { 
				 // Start an Activity that tries to resolve the error 
				 connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST); 
				 /* 
				 * Thrown if Google Play services canceled the original 
				 * PendingIntent 
				 */ 
			 } catch (IntentSender.SendIntentException e) { 
				 // Log the error 
				 e.printStackTrace(); 
			 } 
		 	} else { 
			 /* 
			 * If no resolution is available, display a dialog to the 
			 * user with the error. 
			 */ 
			 Toast.makeText(this, "FAILURE!", Toast.LENGTH_LONG).show(); 
		 	}
	}
	
	@Override
	public void onConnected(Bundle connectionHint) {
		// Display the connection status 
		Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show(); 
		// If already requested, start periodic updates 
		if (mUpdatesRequested) { 
			mLocationClient.requestLocationUpdates(mLocationRequest, this); 
		}
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
		
	}

}
