package com.cse110team14.placeit;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import com.cse110team14.placeit.controller.LoginController;
import com.cse110team14.placeit.view.LoginView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class LoginActivity extends Activity {
	public static LoginActivity loginActivity;
	private Context context = this;
	public static boolean logined = false;
	public static String username;
	private String loginStatusFile = "savedLoginStatus.dat";

	/**
	 * Method onCreate will set the login content and if user is already logined in
	 * then go to MainActivity that will display the map
	 */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		loginActivity = this;
		setContentView(R.layout.activity_login);
		LoginView lv = new LoginView();
		LoginController lc = new LoginController(lv, context);
		logined = readLoginStatus();
		
		if(logined == true){
			Intent myIntent = new Intent(
					LoginActivity.this,
					MainActivity.class);
			startActivity(myIntent);
		}
	}
	/**
	 * Read local login status for the user to indicate if the user logined in 
	 * last time
	 * @return boolean to indicate if the user is logined in
	 */
	public boolean readLoginStatus() {
		String loginstatus = Boolean.toString(false);
		String username = "";
		String str = "";
		try {
			FileInputStream in = openFileInput(loginStatusFile);
			InputStreamReader isr = new InputStreamReader(in);
			BufferedReader reader = new BufferedReader(isr);
			str = reader.readLine();
			String []splited = str.split("###");
		 	loginstatus = splited[0];
		 	if(splited.length > 1){
		 	username = splited[1];
		 	}
			in.close();
			isr.close();
			reader.close();
			Log.e("SOS", loginstatus);
			Log.e("SOS", "user name is " + username);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.username = username;
		return Boolean.parseBoolean(loginstatus);
	}
	
}
