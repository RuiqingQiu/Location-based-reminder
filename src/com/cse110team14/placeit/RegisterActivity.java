package com.cse110team14.placeit;

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

import com.cse110team14.placeit.controller.RegisterController;
import com.cse110team14.placeit.view.RegisterView;



import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class RegisterActivity extends Activity {
	public static RegisterActivity registerActivity;
	public static String User_url = "http://placeitteam14.appspot.com/user";
	private Context context = this;
	/**
	 * Method: onCreate to create content for the register page
	 */
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        registerActivity = this;
        RegisterView rv = new RegisterView();
        RegisterController rc = new RegisterController(rv, context);
       
	}

}
