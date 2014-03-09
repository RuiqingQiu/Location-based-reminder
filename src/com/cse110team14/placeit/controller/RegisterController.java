package com.cse110team14.placeit.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
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

import com.cse110team14.placeit.LoginActivity;
import com.cse110team14.placeit.MainActivity;
import com.cse110team14.placeit.RegisterActivity;
import com.cse110team14.placeit.util.EncryptUtils;
import com.cse110team14.placeit.view.RegisterView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

public class RegisterController {
	private String TAG = "registerControl";
	private RegisterView registerview;
	private Context context;
	private boolean registered = true;
	public RegisterController(RegisterView registerview, Context context){
		this.registerview = registerview;
		this.context = context;
		initializeButton();
	}
	private void initializeButton(){
		 registerview.getRegister().setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View arg0) {
					Log.e("A:B", registerview.getPassword().getText().toString() + ":" +registerview.getRepass().getText().toString());
					if (registerview.getUsername().getText().toString().length() == 0 || 
						registerview.getPassword().getText().toString().length() == 0)
					{
						AlertDialog.Builder alert = initializeAlert("Error", "User and password can't be empty!");
						alert.show();
					}
					else if (!registerview.getPassword().getText().toString().equals(registerview.getRepass().getText().toString())){
						AlertDialog.Builder alert = initializeAlert("Error", "Two password inputs are not consistant!");
						alert.show();
					}else{
						new CheckDuplicateUserTask().execute(RegisterActivity.User_url);
					}
				}
	        	
	        });
	}
	private void postdata() {
		final ProgressDialog dialog = ProgressDialog.show(context,
				"Posting Data...", "Please wait...", false);
		Thread t = new Thread() {

			public void run() {
				HttpClient client = new DefaultHttpClient();
				HttpPost post = new HttpPost(RegisterActivity.User_url);
<<<<<<< HEAD
			    try {
			      List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
			      nameValuePairs.add(new BasicNameValuePair("User",
			    		  registerview.getUsername().getText().toString()));
			      nameValuePairs.add(new BasicNameValuePair("password",
			    		  EncryptUtils.encode(registerview.getPassword().getText().toString())));
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
//				}//@
			    
=======
				//TODO check reg_name exist or not
				
//				if (checkDuplicateUsername(registerview.getUsername().getText().toString())){
//					pass = false;
//				}
				checkDuplicateUsername(registerview.getUsername().getText().toString());
				if(pass){
				    try {
				      List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
				      nameValuePairs.add(new BasicNameValuePair("User",
				    		  registerview.getUsername().getText().toString()));
				      nameValuePairs.add(new BasicNameValuePair("password",
				    		  EncryptUtils.encode(registerview.getPassword().getText().toString())));
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
				}//@
>>>>>>> 17f2678e933ade29bf14a6bc6b912373c5fa2e4a
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
    
	private class CheckDuplicateUserTask extends AsyncTask<String, Void, List<String>> {
		@Override
		protected List<String> doInBackground(String... url) {

			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet(RegisterActivity.User_url);
			List<String> list = new ArrayList<String>();
			try {
				HttpResponse response = client.execute(request);
				HttpEntity entity = response.getEntity();
				String data = EntityUtils.toString(entity);
				Log.d(TAG, data);
				JSONObject myjson;

				try {
					myjson = new JSONObject(data);
					JSONArray array = myjson.getJSONArray("data");
					for (int i = 0; i < array.length(); i++) {
						JSONObject obj = array.getJSONObject(i);
						list.add(obj.get("name").toString());
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

		protected void onPostExecute(List<String> list) {
			String username = registerview.getUsername().getText().toString();
			registered = false;
			for (int i = 0; i < list.size(); i++) {
				if (username.equals(list.get(i))) {
					
					registered = true;
				}
				Log.e("register", username);
				Log.e("register", list.get(i));
			}
			Log.e("registerd=", Boolean.toString(registered));
			if(!registered){
				postdata();
				Intent myIntent = new Intent(RegisterActivity.registerActivity.getApplicationContext(), LoginActivity.class);
				RegisterActivity.registerActivity.startActivity(myIntent);
			}else{
				AlertDialog.Builder alert = initializeAlert("Error", "Username has already registered!");
				alert.show();
				registered = false;
			}
		}

	}
}
