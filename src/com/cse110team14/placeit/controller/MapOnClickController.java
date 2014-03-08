package com.cse110team14.placeit.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.cse110team14.placeit.LoginActivity;
import com.cse110team14.placeit.MainActivity;
import com.cse110team14.placeit.R;
import com.cse110team14.placeit.RegisterActivity;
import com.cse110team14.placeit.R.id;
import com.cse110team14.placeit.R.layout;
import com.cse110team14.placeit.model.PlaceIt;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapOnClickController {
	public static final String PLACEITS_URL = "http://placeitteam14.appspot.com/placeits";
	private Context context;
	public MapOnClickController(Context context){
		this.context = context;
		initializeMapControl();
	}
	
	public void initializeMapControl(){
	    /*
	     * Function for user press on map for a long time, it will create a Marker
	     * at where the user pressed on
	     */
	    MainActivity.map.setOnMapLongClickListener(new OnMapLongClickListener()
	    {
	        @Override
	        public void onMapLongClick(LatLng point)
	        {
	        	MarkerOptions position = new MarkerOptions()
	                      .position(point)
	                      .title("Click to add a Title")
	                      .snippet("latitude: " + point.latitude +"###" + "\n longtitude: " + point.longitude + 
	                    		  "###" + "\nClick on info window to enter your reminder");
	        	MainActivity.map.addMarker(position);
	        }
	    });
	    /*
	     * Method for user clicked on the info window, it will ask for entering informations
	     * Only appear if it's not a PlaceIt yet
	     */
	    MainActivity.map.setOnInfoWindowClickListener(new OnInfoWindowClickListener(){
	
			@Override
			public void onInfoWindowClick(final Marker m) 
			{		
				//First case for the marker, if it's already a placeit
				if(MainActivity.mMarkers.contains(m))
				{
					return;
				}
				
				MainActivity.alert = new AlertDialog.Builder(context);
		        
		        MainActivity.alert.setTitle("Placeit information");
	
		        // Get the layout inflater
		        LayoutInflater inflater = MainActivity.mainActivity.getLayoutInflater();
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
		        MainActivity.alert.setView(v);
		        MainActivity.alert.setPositiveButton("Create the PlaceIt", new DialogInterface.OnClickListener() {
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
			          
			          MainActivity.mMarkers.add(m);
			          PlaceIt tmp = new PlaceIt(placeItTitle, placeItDescription, markerColor, m.getPosition() ,dateToBeReminded, currentDateTime);
			          //Set the type of the placeit
			          tmp.setPlaceItType(placeItType);
			          //Set list to be type 1 to be in active list
			          tmp.setListType("1");
			          Log.e("hello", "the type is" + tmp.getPlaceItType());
			          MainActivity.activeList.add(tmp);
			          MainActivity.marker = MainActivity.mMarkers.iterator();
			          //Put the data into the database
			          postPlaceIts(tmp);
			          
		          }
		        });
		        //If the marker is stored as a placeit
		        if(MainActivity.mMarkers.contains(m)){
		        	//The cancel button will leave it as it is
			        MainActivity.alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			          public void onClick(DialogInterface dialog, int whichButton) {
			          }
			        });
		        }
		        else{
		        	//The cancel button will hide the window
		        	MainActivity.alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				      public void onClick(DialogInterface dialog, int whichButton) {
				    	  m.setVisible(false);
				      }
				    });
		        }
		        /* Rather than delete, we will set the marker to be invisible and 
		         * it's never added to the list
		         */
		        MainActivity.alert.show();
		        m.showInfoWindow();
			}
	    	
	    });
	}

	private void postPlaceIts(PlaceIt p) {
		final ProgressDialog dialog = ProgressDialog.show(context,
				"Posting Data...", "Please wait...", false);
		final PlaceIt tmp = p;
		Thread t = new Thread() {

			public void run() {
				HttpClient client = new DefaultHttpClient();
				HttpPost post = new HttpPost(PLACEITS_URL);

			    try {
			      List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
			      nameValuePairs.add(new BasicNameValuePair("PlaceIts",
			    		  tmp.getTitle()));
			      nameValuePairs.add(new BasicNameValuePair("description",
			    		  tmp.getDescription()));
			      nameValuePairs.add(new BasicNameValuePair("postDate",
			    		  tmp.getDate()));
			      nameValuePairs.add(new BasicNameValuePair("dateToBeReminded",
			    		  tmp.getDateReminded()));
			      nameValuePairs.add(new BasicNameValuePair("color",
			    		  tmp.getColor()));
			      //This is always a regular placeit
			      nameValuePairs.add(new BasicNameValuePair("type",
			    		  "1"));
			      nameValuePairs.add(new BasicNameValuePair("location",
			    		  tmp.getLocation().toString()));
			      nameValuePairs.add(new BasicNameValuePair("placeitType",
			    		  Integer.toString(tmp.getPlaceItType())));
			      nameValuePairs.add(new BasicNameValuePair("sneezeType",
			    		  Integer.toString(tmp.getSneezeType())));
			      nameValuePairs.add(new BasicNameValuePair("user",
			    		  LoginActivity.loginActivity.username));
			      nameValuePairs.add(new BasicNameValuePair("listType",
			    		  tmp.getListType()));
			      nameValuePairs.add(new BasicNameValuePair("action",
				          "put"));
			      post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			 
			      HttpResponse response = client.execute(post);
			      BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			      String line = "";
			      while ((line = rd.readLine()) != null) {
			        Log.d("hello", line);
			      }

			    } catch (IOException e) {
			    	Log.d("hello", "IOException while trying to conect to GAE");
			    }
				dialog.dismiss();
			}
		};

		t.start();
		dialog.show();
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
	  
}
