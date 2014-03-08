package com.cse110team14.placeit.controller;

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
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.cse110team14.placeit.LoginActivity;
import com.cse110team14.placeit.MainActivity;
import com.cse110team14.placeit.RegisterActivity;
import com.cse110team14.placeit.util.EncryptUtils;
import com.cse110team14.placeit.view.LoginView;

public class LoginController {
	private LoginView loginview;

	private String TAG = "loginControl";
	private Context context;
	private String input_username;
	private String input_password;

	public LoginController(LoginView loginview, Context context) {
		this.loginview = loginview;
		this.context = context;
		initializeButton();
	}

	public void getUserInput() {
		input_username = loginview.getUsername().getText().toString();
		input_password = loginview.getPassword().getText().toString();
		Log.e("hello", "1" + input_username);
		Log.e("hello", "2" + input_password);
	}

	public void initializeButton() {
		loginview.getSignup().setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent myIntent = new Intent(LoginActivity.loginActivity
						.getApplicationContext(), RegisterActivity.class);
				LoginActivity.loginActivity.startActivity(myIntent);
			}

		});

		loginview.getLogin().setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				getUserInput();
				Log.e("hello", "11" + input_username.isEmpty());
				Log.e("hello", "22" + input_password.isEmpty());
				if (input_username.isEmpty() || input_password.isEmpty()) {
					AlertDialog.Builder alert = initializeAlert(
							"Invalid input",
							"Please enter your username and password");
					alert.show();
				} else {
					new CheckUserTask().execute(RegisterActivity.User_url);

				}

			}

		});
	}

	private class CheckUserTask extends AsyncTask<String, Void, List<String>> {
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
						list.add(obj.get("password").toString());
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
			boolean logined = false;
			
				for (int i = 0; i < list.size(); i += 2) {
					if (input_username.equals(list.get(i))
							&& EncryptUtils.encode(input_password).equals(list.get(i + 1))) {
						logined = true;
						LoginActivity.loginActivity.logined = true;
						LoginActivity.loginActivity.username = input_username;
						Intent myIntent = new Intent(
								LoginActivity.loginActivity
										.getApplicationContext(),
								MainActivity.class);
						LoginActivity.loginActivity.startActivity(myIntent);
					}
				}
				if (!logined) {
					AlertDialog.Builder alert = initializeAlert("Error",
							"User name or Password is incorrect");
					alert.show();
				}
		}

	}

	/**
	 * Method to build a alert dialog for error condition, mainly used in user
	 * entries for Placeit information
	 * 
	 * @param title
	 * @param message
	 * @return The dialog box that contains the title and message
	 */
	public AlertDialog.Builder initializeAlert(String title, String message) {
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
