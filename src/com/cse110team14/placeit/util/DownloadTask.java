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

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;

import com.cse110team14.placeit.MainActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/** A class, to download Places from Geocoding webservice */
@SuppressLint("NewApi")
public class DownloadTask extends AsyncTask<String, Integer, String>{

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
            Marker tmp = MainActivity.map.addMarker(markerOptions);
            /*mMarkers.add(tmp);
            marker=mMarkers.iterator();*/
            tmp.showInfoWindow();
            
            MainActivity.map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,17));
            MainActivity.map.animateCamera(CameraUpdateFactory.zoomIn());
            
            // Locate the first location
            if(i==0)
                MainActivity.map.animateCamera(CameraUpdateFactory.newLatLng(latLng));

        }
    }
}//End of class
/* END OF HELPER FUNCTIONS AND CLASSES FOR GETTING LOCATION DATA FROM GOOGLE */
