/*
 * Filename: MainActivity.java
 * Hello world
 */
package com.cse110team14.placeit;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import com.cse110team14.placeit.GeocodeJSONParser;
import org.json.JSONObject;

import com.cse110team14.placeit.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
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
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.FragmentActivity;

public class MainActivity extends Activity implements
CancelableCallback
{
	
	// Global constants
    /*
     * Define a request code to send to Google Play services
     * This code is returned in Activity.onActivityResult
     */
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
	static final LatLng HAMBURG = new LatLng(53.558, 9.927);
	private GoogleMap map;
	static final LatLng MELBOURNE = new LatLng(-37.81319, 144.96298);
	public static List<PlaceIt> PlaceIts = new ArrayList<PlaceIt>();
	
	private List<Marker> mMarkers = new ArrayList<Marker>();
	private Iterator<Marker> marker;
	
	AlertDialog.Builder alert;
	Location myCurrentLocation;
	LocationClient myLocationClient;
	Button mBtnFind;
	Button retrackBtn;
	Button active;
	Button pulled;
	Button create;
	EditText etPlace;
	final Context context = this;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpMapIfNeeded();
        
        //Initilize the mMarkers list
        mMarkers = new ArrayList<Marker>();
        
        // Getting reference to the find button
        mBtnFind = (Button) findViewById(R.id.btn_show);
        retrackBtn = (Button)findViewById(R.id.retrack);
        active = (Button)findViewById(R.id.active);
        pulled = (Button)findViewById(R.id.pulled);
        create = (Button)findViewById(R.id.create);
      
        map.setMyLocationEnabled(true);
        map.setInfoWindowAdapter(new PlaceItsInfoWindow());


        // Getting reference to EditText
        etPlace = (EditText) findViewById(R.id.et_place);
 
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
        
    }

    /*
     * Called when the Activity becomes visible.
     */
    @Override
    protected void onStart() {
        super.onStart();
        
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
        map.setOnInfoWindowClickListener(new OnInfoWindowClickListener(){

			@Override
			public void onInfoWindowClick(final Marker m) 
			{		
				alert = new AlertDialog.Builder(context);
		        
		        alert.setTitle("Placeit information");

		        // Get the layout inflater
		        LayoutInflater inflater = getLayoutInflater();
		        // Set an EditText view to get user input 
		        
		        // Inflate and set the layout for the dialog
		        final View v = inflater.inflate(R.layout.placeitsinfo, null);
		        final EditText title = (EditText)v.findViewById(R.id.title);
		        final EditText description = (EditText)v.findViewById(R.id.description);
		        final EditText date = (EditText)v.findViewById(R.id.date);
		        final EditText color = (EditText)v.findViewById(R.id.color);
		        // Pass null as the parent view because its going in the dialog layout
		        alert.setView(v);
		        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int whichButton) {
			          String placeItTitle = title.getText().toString();
			          m.setTitle(placeItTitle);
			          String placeItDescription = description.getText().toString();
			          String dateToBeReminded = date.getText().toString();
			          String currentDateTime = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
			          m.setSnippet(placeItDescription + "###"
			        		  + dateToBeReminded +"###" + currentDateTime);
			          String markerColor = color.getText().toString();
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
			          else if(markerColor.toLowerCase().equals("megenta"))
			              m.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
			          else if(markerColor.toLowerCase().equals("orange"))
			              m.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
			          else if(markerColor.toLowerCase().equals("violet"))
			              m.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
			          else if(markerColor.toLowerCase().equals("rose"))
			              m.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
			          else if(markerColor.toLowerCase().equals("yellow"))
			              m.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
			          else{
			        	  AlertDialog.Builder temp = new AlertDialog.Builder(context);
		     		      temp.setTitle("Entered Color is not valid");
		     		      temp.setMessage("Please enter a valid color :)");
		     		      temp.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
		     			        public void onClick(DialogInterface dialog, int whichButton) {
		     			          }
		     			        });
		     		      temp.show();
		     		      return;
			          }
			          mMarkers.add(m);
			          PlaceIts.add(new PlaceIt(placeItTitle, placeItDescription, markerColor, m.getPosition() ,dateToBeReminded, currentDateTime));
			          marker = mMarkers.iterator();
			          
		          }
		        });

		        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		          public void onClick(DialogInterface dialog, int whichButton) {
		        	m.setVisible(false);
		          }
		        });
		        /* Rather than delete, we will set the marker to be invisible and 
		         * it's never added to the list
		         */
		        alert.show();
		        m.showInfoWindow();
			}
        	
        });
    }
    
    public void initializeAlert(){
    	
    }
    /*
     * Called when the Activity is no longer visible.
     */
    @Override
    protected void onStop() {
        super.onStop();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
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

    	//Log.e("test", "finish");
   	 	/*if (marker.hasNext()) {
	   		 Marker current = marker.next();
	   		 map.animateCamera(CameraUpdateFactory.newLatLng(current.getPosition()), 2000, this);
	   		 current.showInfoWindow();
	   	 }*/
	}

	public void goToActiveList(View v){
		startActivity(new Intent(MainActivity.this, ActiveListActivity.class));
	}
	
	public void goToPulledList(View v){
		startActivity(new Intent(MainActivity.this, PulledListActivity.class));
	}
	
	public void goToCreate(View v){
		startActivity(new Intent(MainActivity.this, CreatePlaceIts.class));
	}
	

	@Override
	public void onCancel() {
		// TODO Auto-generated method stub
		
	}
	
	/* A CLASS FOR CUSTOM PLACEITS INFO WINDOW ON THE MAP */
	class PlaceItsInfoWindow implements InfoWindowAdapter{

		private final View myContentsView;
		  
		PlaceItsInfoWindow(){
		   myContentsView = getLayoutInflater().inflate(R.layout.placeits_info_window, null);
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
                markerOptions.title(name);
                // Placing a marker on the touched position
                Marker tmp = map.addMarker(markerOptions);
                mMarkers.add(tmp);
                marker=mMarkers.iterator();
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
  
}