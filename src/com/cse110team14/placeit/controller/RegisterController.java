package com.cse110team14.placeit.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import com.cse110team14.placeit.LoginActivity;
import com.cse110team14.placeit.RegisterActivity;
import com.cse110team14.placeit.view.RegisterView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

public class RegisterController {
	private RegisterView registerview;
	private Context context;
	public RegisterController(RegisterView registerview, Context context){
		this.registerview = registerview;
		this.context = context;
		initializeButton();
	}
	private void initializeButton(){
		 registerview.getRegister().setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View arg0) {
					if (registerview.getUsername().getText().toString().length() == 0 || 
						registerview.getPassword().getText().toString().length() == 0)
					{
						AlertDialog.Builder alert = initializeAlert("Error", "User and password can't be empty!");
						alert.show();
					}
					else{
						postdata();
						Intent myIntent = new Intent(RegisterActivity.registerActivity.getApplicationContext(), LoginActivity.class);
						RegisterActivity.registerActivity.startActivity(myIntent);
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

			    try {
			      List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
			      nameValuePairs.add(new BasicNameValuePair("User",
			    		  registerview.getUsername().getText().toString()));
			      nameValuePairs.add(new BasicNameValuePair("password",
			    		  registerview.getPassword().getText().toString()));
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
}
