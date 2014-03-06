package com.cse110team14.placeit.controller;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;

import com.cse110team14.placeit.LoginActivity;
import com.cse110team14.placeit.RegisterActivity;
import com.cse110team14.placeit.view.LoginView;


public class LoginController {
	private LoginView loginview;
	public LoginController(LoginView loginview){
		this.loginview = loginview;
		initializeButton();
	}
    public void initializeButton(){
    	loginview.getSignup().setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				Intent myIntent = new Intent(LoginActivity.loginActivity.getApplicationContext(), RegisterActivity.class);
				LoginActivity.loginActivity.startActivity(myIntent);
			}
    		
    	});
    	
    	loginview.getLogin().setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				
			}
    		
    	});
    }
}
