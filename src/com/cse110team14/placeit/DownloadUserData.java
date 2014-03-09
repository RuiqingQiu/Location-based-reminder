package com.cse110team14.placeit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.cse110team14.placeit.controller.MapOnClickController;
import com.cse110team14.placeit.model.PlaceIt;
import com.cse110team14.placeit.util.EncryptUtils;
import com.google.android.gms.maps.model.LatLng;

public class DownloadUserData {
	private static String TAG = "DownloadUserData";
	public static List<PlaceIt> loadDataToActiveList(String username){
		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet(MapOnClickController.PLACEITS_URL);
		List<PlaceIt> list = new ArrayList<PlaceIt>();
		try {
			HttpResponse response = client.execute(request);
			HttpEntity entity = response.getEntity();
			String data = EntityUtils.toString(entity);
			JSONObject myjson;

			try {
				myjson = new JSONObject(data);
				JSONArray array = myjson.getJSONArray("data");
				for (int i = 0; i < array.length(); i++) {
					JSONObject obj = array.getJSONObject(i);
					String user = obj.get("user").toString();
					String listType = obj.get("listType").toString();
					if(!username.equals(user) || !listType.equals("1") )
						continue;
					String title = obj.get("title").toString();
					String description = obj.get("description").toString();
					String color = obj.get("color").toString();
					//Decoding the location string to LatLng object
					String l = obj.get("location").toString();
					String [] tmp = l.split("\\s+")[1].split(",");
					String second = tmp[1];
					second = second.substring(0, second.length()-1);
					LatLng location = new LatLng(Double.parseDouble(tmp[0].substring(1)), 
							Double.parseDouble(second));
					
					String dateToBeReminded = obj.get("dateToBeReminded").toString();
					String postDate = obj.get("postDate").toString();
					String type = obj.get("type").toString();
				    String placeitType = obj.get("placeitType").toString(); 
				    String sneezeType = obj.get("sneezeType").toString();
				    //If it's type 1, regular placeit
				    if(type.equals("1")){
				    	PlaceIt p = new PlaceIt(title, description, color,location, dateToBeReminded, postDate);
				    	p.setPlaceItType(Integer.parseInt(placeitType));
				    	p.setSneezeType(Integer.parseInt(sneezeType));
				    	p.setListType(listType);
						list.add(p);
						Log.e("userData", p.getTitle());
				    }
				    //If it's type 2, categorical placeit
				    else{
				    }
				}

			} catch (JSONException e) {

				Log.d(TAG, "Error in parsing JSON");
			}

		} catch (ClientProtocolException e) {

			Log.d(TAG,
					"ClientProtocolException while trying to connect to GAE");
		} catch (IOException e) {

			Log.d(TAG, "IOException while trying to connect to GAE");
		}
		return list;
	}
	
	public static List<PlaceIt> loadDataToPullList(String username){
		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet(MapOnClickController.PLACEITS_URL);
		List<PlaceIt> list = new ArrayList<PlaceIt>();
		try {
			HttpResponse response = client.execute(request);
			HttpEntity entity = response.getEntity();
			String data = EntityUtils.toString(entity);
			JSONObject myjson;

			try {
				myjson = new JSONObject(data);
				JSONArray array = myjson.getJSONArray("data");
				for (int i = 0; i < array.length(); i++) {
					JSONObject obj = array.getJSONObject(i);
					String user = obj.get("user").toString();
					String listType = obj.get("listType").toString();
					if(!username.equals(user) || !listType.equals("2")){
						Log.e("userData","pulldown continue");
						continue;
					}
					String title = obj.get("title").toString();
					String description = obj.get("description").toString();
					String color = obj.get("color").toString();
					//Decoding the location string to LatLng object
					String l = obj.get("location").toString();
					String [] tmp = l.split("\\s+")[1].split(",");
					String second = tmp[1];
					second = second.substring(0, second.length()-1);
					LatLng location = new LatLng(Double.parseDouble(tmp[0].substring(1)), 
							Double.parseDouble(second));
					
					String dateToBeReminded = obj.get("dateToBeReminded").toString();
					String postDate = obj.get("postDate").toString();
					String type = obj.get("type").toString();
				    String placeitType = obj.get("placeitType").toString(); 
				    String sneezeType = obj.get("sneezeType").toString();
				    //If it's type 1, regular placeit
				    if(type.equals("1")){
				    	PlaceIt p = new PlaceIt(title, description, color,location, dateToBeReminded, postDate);
				    	p.setPlaceItType(Integer.parseInt(placeitType));
				    	p.setSneezeType(Integer.parseInt(sneezeType));
				    	p.setListType(listType);
						list.add(p);
						Log.e("userData", p.getTitle());
				    }
				    //If it's type 2, categorical placeit
				    else{
				    }
				}

			} catch (JSONException e) {

				Log.d(TAG, "Error in parsing JSON");
			}

		} catch (ClientProtocolException e) {

			Log.d(TAG,
					"ClientProtocolException while trying to connect to GAE");
		} catch (IOException e) {

			Log.d(TAG, "IOException while trying to connect to GAE");
		}
		return list;
	}
}
