package com.cse110team14.placeit.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import com.google.android.gms.maps.model.LatLng;
import com.cse110team14.placeit.LocationService;
import com.cse110team14.placeit.MainActivity;
import com.cse110team14.placeit.R;
import com.cse110team14.placeit.model.CPlaceIts;
import com.cse110team14.placeit.model.PlaceIt;
import com.cse110team14.placeit.server_side.UpdatePlaceItsOnServer;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

/** A class, to download Google Places */
public class PlacesTask extends AsyncTask<String, Integer, String>{

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
    /** A method to download json data from url */
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
 
            StringBuffer sb  = new StringBuffer();
 
            String line = "";
            while( ( line = br.readLine())  != null){
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

    // Executed after the complete execution of doInBackground() method
    @Override
    protected void onPostExecute(String result){
        PlaceParserTask parserTask = new PlaceParserTask();

        // Start parsing the Google places in JSON format
        // Invokes the "doInBackground()" method of the class ParseTask
        parserTask.execute(result);
    }

}

/** A class to parse the Google Places in JSON format */
class PlaceParserTask extends AsyncTask<String, Integer, List<HashMap<String,String>>>{

    JSONObject jObject;

    // Invoked by execute() method of this object
    @Override
    protected List<HashMap<String,String>> doInBackground(String... jsonData) {

        List<HashMap<String, String>> places = null;
        PlaceJSONParser placeJsonParser = new PlaceJSONParser();

        try{
            jObject = new JSONObject(jsonData[0]);

            /** Getting the parsed data as a List construct */
            places = placeJsonParser.parse(jObject);

        }catch(Exception e){
            Log.d("Exception",e.toString());
        }
        return places;
    }

    // Executed after the complete execution of doInBackground() method
    @Override
    protected void onPostExecute(List<HashMap<String,String>> list){
        Log.e("hello", "Enter placetask");
        double min = 880.0;
        HashMap<String, String> targetPlace = null;
        for(int i=0;i<list.size();i++){
            
            // Getting a place from the places list
            HashMap<String, String> hmPlace = list.get(i);
            
            // Getting latitude of the place
            double lat = Double.parseDouble(hmPlace.get("lat"));
            
            // Getting longitude of the place
            double lng = Double.parseDouble(hmPlace.get("lng"));
            
            Location placeLocation = new Location("");
            placeLocation.setLatitude(lat);
            placeLocation.setLongitude(lng);
            
            // Getting name
            String name = hmPlace.get("place_name");

            // Getting vicinity
            String vicinity = hmPlace.get("vicinity");
            LatLng latLng = new LatLng(lat, lng);
            Log.e("hello", "distance: "+LocationService.myCurrentLocation.distanceTo(placeLocation));
            double distance = LocationService.myCurrentLocation.distanceTo(placeLocation);
            //Check anything within range
            if (distance < 880){
            	if(distance < min){
            		min = distance;
            		targetPlace = hmPlace;
            	}
            }
        }//End of for loop
        //If there's somewhere found
        if(min != 0.0 && targetPlace != null){
        	String closestLocation = targetPlace.get("place_name");
        	String address = targetPlace.get("vicinity");
        	//Remove from the list
        	LocationService.createNotification(null, LocationService.currentCPlaceIt, closestLocation, address);
        	//Move the placeit to pulldown
        	MainActivity.cActiveList.remove(LocationService.currentCPlaceIt);
        	MainActivity.cPullDownList.add(LocationService.currentCPlaceIt);
        	LocationService.currentCPlaceIt.setListType("2");
        	//Update the placeit on server
        	UpdatePlaceItsOnServer.postCPlaceIts(LocationService.currentCPlaceIt);
        }
    }
}
