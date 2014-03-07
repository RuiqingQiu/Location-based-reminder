package com.cse110team14.placeit.view;

import com.cse110team14.placeit.LoginActivity;
import com.cse110team14.placeit.R;
import com.cse110team14.placeit.R.id;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginView {
	private View view;
	EditText username;
	EditText password;
	Button login;
	Button signup;
	public LoginView(){
		view = LoginActivity.loginActivity.getWindow().getDecorView().getRootView();
		username = (EditText)view.findViewById(R.id.user_name);
		password = (EditText)view.findViewById(R.id.user_password);
		login = (Button)view.findViewById(R.id.login);
		signup = (Button)view.findViewById(R.id.register);
	}
	public EditText getUsername(){
		return username;
	}
	public EditText getPassword(){
		return password;
	}
	public Button getLogin(){
		return login;
	}
	public Button getSignup(){
		return signup;
	}
}
