package com.cse110team14.placeit;

import com.cse110team14.placeit.view.LoginView;

import android.app.Activity;
import android.os.Bundle;

public class LoginActivity extends Activity{
	public static LoginActivity loginActivity;
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        LoginView lv = new LoginView();
        LoginController lc = new LoginController(lv);
	}
	

}
