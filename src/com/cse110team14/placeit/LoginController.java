package com.cse110team14.placeit;

import android.view.View;
import android.view.View.OnClickListener;

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
				// TODO Auto-generated method stub
				
			}
    		
    	});
    	
    	loginview.getLogin().setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
    		
    	});
    }
}
