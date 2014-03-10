package com.cse110team14.placeit.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.cse110team14.placeit.MainActivity;
import com.cse110team14.placeit.R;
import com.cse110team14.placeit.UpdatePlaceItsOnServer;
import com.cse110team14.placeit.R.id;
import com.cse110team14.placeit.R.layout;
import com.cse110team14.placeit.model.CPlaceIts;
import com.cse110team14.placeit.model.PlaceIt;
import com.cse110team14.placeit.util.DownloadTask;
import com.cse110team14.placeit.util.MultiSelectionSpinner;
import com.cse110team14.placeit.view.MapView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapButtonController {
  private MapView mapview;
  private Context context;
  public MapButtonController(final MapView mapview, Context alertcontext){
	  this.mapview = mapview;
	  initializeButton();
	  context = alertcontext;
  }
  
  public void initializeButton(){
	// Setting click event listener for the find button
      mapview.getFindButtion().setOnClickListener(new OnClickListener() {
          @Override
          public void onClick(View v) {
        	  
              // Getting the place entered
              String location = mapview.getEditPlace().getText().toString();

              if(location==null || location.equals("")){
                  Toast.makeText(MainActivity.mainActivity.getApplicationContext(), "No Place is entered", Toast.LENGTH_SHORT).show();
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
      mapview.getRetrackButton().setOnClickListener(new OnClickListener(){
      	 @Override
  		 public void onClick(View v) {
      		 /* Checking errors, if the iterator was never initalized, pop up an dialog
      		  * box and show what the error is
      		  */
      		 if(MainActivity.marker == null){
      			AlertDialog.Builder temp = new AlertDialog.Builder(MainActivity.mainActivity.getApplicationContext());
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
  			 if (MainActivity.marker.hasNext()) {
  				 Marker current = MainActivity.marker.next();
  				 Log.e("test", ""+current.getTitle());
  				 MainActivity.map.animateCamera(CameraUpdateFactory.newLatLng(current.getPosition()), 2000, MainActivity.cancelableCallback);
  				 current.showInfoWindow();
  				 
  			 }
  			 else{
  				 MainActivity.marker = MainActivity.mMarkers.iterator();
  				 Marker current = MainActivity.marker.next();
  				 Log.e("test", ""+current.getTitle());
  				 MainActivity.map.animateCamera(CameraUpdateFactory.newLatLng(current.getPosition()), 2000, MainActivity.cancelableCallback);
  				 current.showInfoWindow();
  			 }
  		 }
      });
      
      
      //Initialize the create button 
      mapview.getCreateButton().setOnClickListener(new OnClickListener()
      {
			@Override
			public void onClick(View v) 

			{
				MainActivity.alert= new AlertDialog.Builder(context);
		        
		        MainActivity.alert.setTitle("Create A PlaceIt");

		        // Get the layout inflater
		        LayoutInflater inflater = MainActivity.mainActivity.getLayoutInflater();
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
		        MainActivity.alert.setView(view);
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
			          Marker m = MainActivity.map.addMarker(new MarkerOptions().title(placeItTitle).position(position));
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
			          MainActivity.mMarkers.add(m);
			          PlaceIt tmp = new PlaceIt(placeItTitle, placeItDescription, markerColor, m.getPosition() ,dateToBeReminded, currentDateTime);
			          tmp.setPlaceItType(placeItType);
			          MainActivity.activeList.add(tmp);
			          //Post data on to the server
			          tmp.setListType("1");
					  UpdatePlaceItsOnServer.postPlaceIts(tmp);
			          MainActivity.marker = MainActivity.mMarkers.iterator();
			          MainActivity.map.moveCamera(CameraUpdateFactory.newLatLngZoom(position,17));
		              MainActivity.map.animateCamera(CameraUpdateFactory.zoomIn());
		        	}
		        });//End of positive button
		        MainActivity.alert.setNeutralButton("Create Category PlaceIts", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						  MainActivity.c_alert= new AlertDialog.Builder(context);
				        
				          MainActivity.c_alert.setTitle("Create A Category PlaceIt");
						  MultiSelectionSpinner spinner;
						  LayoutInflater inflater = MainActivity.mainActivity.getLayoutInflater();
					      final View view = inflater.inflate(R.layout.create_category_placeits, null);
						  String[] array = { "one", "two", "three", "one", "two", "three", "one", "two", "three", "one", "two", "three", "one", "two", "three", "one", "two", "three"  };
						  spinner = (MultiSelectionSpinner)view.findViewById(R.id.category_spinner);
						  MainActivity.c_alert.setView(view);
						  spinner.setItems(array);
						  
						  final EditText title = (EditText)view.findViewById(R.id.title);
					      final EditText description = (EditText)view.findViewById(R.id.description);
					      final DatePicker date = (DatePicker)view.findViewById(R.id.datePicker);
						  MainActivity.c_alert.setPositiveButton("Create the PlaceIt", new DialogInterface.OnClickListener() {
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
					        		String dateToBeReminded = (date.getMonth()+1) + "/" + date.getDayOfMonth() + "/" + date.getYear();	          
					        		String currentDateTime = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
						          
					        		//Check if the date is valid
					        		if (checkDate(dateToBeReminded) == false){
					        			AlertDialog.Builder temp = initializeAlert("Enteted Date is not valid", "Please enter a valid date :)");
					        			temp.show();
					        			return;
					        		}
					        	  String [] categories = new String[]{"1","2","3"};
						          //String [] categories = new String[3];
						          //TODO
						          CPlaceIts tmp = new CPlaceIts(placeItTitle, placeItDescription ,currentDateTime, dateToBeReminded, categories);
						          MainActivity.cActiveList.add(tmp);
						          tmp.setListType("1");
						          UpdatePlaceItsOnServer.postCPlaceIts(tmp);
					        	}
					        });//End of positive button
						  	MainActivity.c_alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
							  public void onClick(DialogInterface dialog, int whichButton) {
							  }
							 
						  	});
						  	 MainActivity.c_alert.show();
					}
				});
		        MainActivity.alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
						  public void onClick(DialogInterface dialog, int whichButton) {
						  }
			    });
			        
		        /* Rather than delete, we will set the marker to be invisible and 
				 * it's never added to the list
				 */
				MainActivity.alert.show();
			}
		});
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
}
