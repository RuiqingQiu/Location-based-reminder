/*
 * Filename: MainActivity.java
 * Hello world
 */
package com.cse110team14.placeit;
import java.io.BufferedReader;
import java.io.BufferedWriter;
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
import android.app.AlertDialog.Builder;
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
	private GoogleMap map;
	public static List<PlaceIt> PlaceIts = new ArrayList<PlaceIt>();
	private static List<PlaceIt> pullDown = new ArrayList<PlaceIt>();
	
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


        //When the app is opened, read in the file and populate the placeit list
        onCreateReadFile();
        Log.e("hello",""+PlaceIts.size());
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
        create.setOnClickListener(new OnClickListener()
        {
			@Override
			public void onClick(View v) 
			{
				AlertDialog.Builder alert1 = new AlertDialog.Builder(context);
		        
		        alert1.setTitle("Placeit information");

		        // Get the layout inflater
		        LayoutInflater inflater = getLayoutInflater();
		        // Set an EditText view to get user input 
		        
		        // Inflate and set the layout for the dialog
		        final View view = inflater.inflate(R.layout.create_placeits, null);
		        final EditText title = (EditText)view.findViewById(R.id.title);
		        final EditText description = (EditText)view.findViewById(R.id.description);
		        final EditText location = (EditText)view.findViewById(R.id.location);
		        final EditText date = (EditText)view.findViewById(R.id.date);
		        final EditText color = (EditText)view.findViewById(R.id.color);
		        // Pass null as the parent view because its going in the dialog layout
		        alert1.setView(view);
		        alert1.setPositiveButton("Create the PlaceIt", new DialogInterface.OnClickListener() {
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
		        		LatLng position = new LatLng(Double.parseDouble(splited[0]), Double.parseDouble(splited[1]));
		        		
		        		String dateToBeReminded = date.getText().toString();	          
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
			          PlaceIts.add(new PlaceIt(placeItTitle, placeItDescription, markerColor, m.getPosition() ,dateToBeReminded, currentDateTime));
			          marker = mMarkers.iterator();
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

        ShowMarkerWhenAppOpen();
        
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
				//First case for the marker, if it's already a placeit
				if(mMarkers.contains(m))
				{
				
				}
				
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
			          String dateToBeReminded = date.getText().toString();	          
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
			          PlaceIts.add(new PlaceIt(placeItTitle, placeItDescription, markerColor, m.getPosition() ,dateToBeReminded, currentDateTime));
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
    
    public static List<PlaceIt> getActiveList(){
    	return PlaceIts;
    }
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
    	Iterator<PlaceIt> itr = PlaceIts.iterator();
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
    
    /**
     * This method will called when the app is in onCreate, it will read in the placeits
     * saved file and put them into the list
     */
    public void onCreateReadFile(){
    	PlaceIts.clear();
    	try {
        	FileInputStream in = openFileInput("saved_placeits.dat");
        	InputStreamReader isr = new InputStreamReader ( in ) ;
        	BufferedReader reader = new BufferedReader(isr);
        	String readString = reader.readLine () ;
        	while (readString != null){
        	   Log.e("hello", readString);
        	   String []splited = readString.split("###");
        	   /*for (int i = 0; i < splited.length; i++){
        		   Log.e("hello", splited[i]);
        	   }*/
        	   Log.e("hello", "Another one");
        	   putPlaceItsReadFromFileToLists(readString);
        	   readString = reader.readLine();
            }
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
    public void putPlaceItsReadFromFileToLists(String str){
       String []splited = str.split("###");
 	   String placeItTitle = splited[0];
 	   String description = splited[1];
 	   String dateToBeReminded = splited[2];
 	   String postDate = splited[3];
 	   LatLng location = new LatLng(Double.parseDouble(splited[4]), Double.parseDouble(splited[5]));
 	   String color = splited[6];
       PlaceIts.add(new PlaceIt(placeItTitle, description, color, location, dateToBeReminded, postDate));
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
    	try {
    		FileOutputStream out = openFileOutput("saved_placeits.dat", Context.MODE_PRIVATE);
    		//write place its to file
    		Iterator<PlaceIt> placeitsIterator = PlaceIts.iterator();
    		while(placeitsIterator.hasNext()){
    			PlaceIt element = placeitsIterator.next();
    			String str = element.getTitle() + "###" + element.getDescription() + "###" + element.getDateReminded()
    					+"###" + element.getDate() + "###" + element.getLocation().latitude +"###" +
    					element.getLocation().longitude + "###" + element.getColor()+"\n"; 
    			out.write(str.getBytes());
    		}
    		out.close();
    	} catch (FileNotFoundException e) {
    		e.printStackTrace();
    	} catch (IOException e) {
    		e.printStackTrace();
    	}

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
  
}