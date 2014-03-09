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
import org.json.JSONObject;

import com.cse110team14.placeit.R;
import com.cse110team14.placeit.controller.MapButtonController;
import com.cse110team14.placeit.controller.MapOnClickController;
import com.cse110team14.placeit.model.CPlaceIts;
import com.cse110team14.placeit.model.PlaceIt;
import com.cse110team14.placeit.util.DownloadTask;
import com.cse110team14.placeit.util.GeocodeJSONParser;
import com.cse110team14.placeit.view.MapView;
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
import android.os.StrictMode;
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
	public static GoogleMap map;
	public static List<PlaceIt> activeList = new ArrayList<PlaceIt>();
	public static List<PlaceIt> pullDown = new ArrayList<PlaceIt>();
	public static List<CPlaceIts> cActiveList = new ArrayList<CPlaceIts>();
	public static List<CPlaceIts> cPullDownList = new ArrayList<CPlaceIts>();
	//This is for getting the view of the activity
	public static MainActivity mainActivity;
	public static CancelableCallback cancelableCallback;
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
	public static List<Marker> mMarkers = new ArrayList<Marker>();
	public static Iterator<Marker> marker;
	private String loginStatusFile = "savedLoginStatus.dat";
	private String activeListFile = "saved_placeits.dat";
	private String pulldownListFile = "pulldown_placeits.dat";
	
	
	//Variables for all Widgets in MainActivity
	public static AlertDialog.Builder alert;
	final Context context = this;
	
	Button test;
	
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
        
    	//strict mode for internet access
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		
        mainActivity = this;
        cancelableCallback = this;
        setUpMapIfNeeded();
        
        test = (Button)findViewById(R.id.test);
        //TODO: delete
        test.setOnClickListener(new OnClickListener(){
        	
			@Override
			public void onClick(View v) {  	
				try {
					PrintWriter writer = new PrintWriter(activeListFile, "UTF-8");
					writer.print("");
					writer.close();
					PrintWriter writer1 = new PrintWriter(pulldownListFile, "UTF-8");
					writer1.print("");
					writer1.close();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Intent myIntent = new Intent(MainActivity.this, LoginActivity.class);
				LoginActivity.loginActivity.logined = false;
				//TODO save to activeListFile.dat
				saveLoginStatus("");
				startActivity(myIntent);
				
				
				
			}
        	
        });
        //Set up the map view
        MapView mapview = new MapView();
        //Set up the button control
        MapButtonController mapbuttoncontroller = new MapButtonController(mapview, context);
        map.setMyLocationEnabled(true);
        map.setInfoWindowAdapter(new PlaceItsInfoWindow(getLayoutInflater().inflate(R.layout.placeits_info_window, null)));
        
        //When the app is opened, read in the file and populate the placeit list
        //readFileToList(activeListFile, activeList);
        //readFileToList(pulldownListFile, pullDown);
        
        activeList = DownloadUserData.loadRegularDataToActiveList(LoginActivity.username);
        pullDown = DownloadUserData.loadRegularDataToPullList(LoginActivity.username);
        Log.e("hello",""+activeList.size());
        // Getting reference to EditText
       
        
        
        //Start the service for checking location onCreate
        startService(new Intent(this, LocationService.class));
      
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
     * Called when the Activity becomes visible.
     */
    @Override
    protected void onStart() {
        super.onStart();
        mLocationClient.connect();
        MapOnClickController mp = new MapOnClickController(context);
        activeList = DownloadUserData.loadRegularDataToActiveList(LoginActivity.username);
        pullDown = DownloadUserData.loadRegularDataToPullList(LoginActivity.username);
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
			in.close(); //@@@@@
			isr.close(); //
			reader.close(); //
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
    
    
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
    
    
    /*
     * Called when the Activity is no longer visible.
     */
    @Override
    protected void onStop() {
        super.onStop();
        saveList(activeList, activeListFile);
        saveList(pullDown, pulldownListFile);
        saveLoginStatus(LoginActivity.username);
    	
    }
    
    public void saveLoginStatus(String user){
    	try {
    		FileOutputStream outputStatus = openFileOutput(loginStatusFile, Context.MODE_PRIVATE);

    		outputStatus.write((Boolean.toString(LoginActivity.loginActivity.logined) + "###" + user + "\n").getBytes());
	    	outputStatus.close();
	    	
    	} catch (FileNotFoundException e) {
    		e.printStackTrace();
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    }
    
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
