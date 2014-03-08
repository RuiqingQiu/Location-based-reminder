package com.cse110team14.placeit.view;

import com.cse110team14.placeit.LoginActivity;
import com.cse110team14.placeit.R;
import com.cse110team14.placeit.RegisterActivity;
import com.cse110team14.placeit.R.id;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class RegisterView {
	private View view;
	private Button register;
	private EditText reg_username;
	private EditText reg_password;
	private EditText reg_repass;
	
	public RegisterView(){
		view = RegisterActivity.registerActivity.getWindow().getDecorView().getRootView();
		reg_username = (EditText)view.findViewById(R.id.reg_name);
	    reg_password = (EditText)view.findViewById(R.id.reg_pass);
	    reg_repass = (EditText)view.findViewById(R.id.reg_repass);
	    register = (Button) view.findViewById(R.id.reg);
	}
	public EditText getUsername(){
		return reg_username;
	}
	public EditText getPassword(){
		return reg_password;
	}
	public EditText getRepass(){
		return reg_repass;
	}
	public Button getRegister(){
		return register;
	}
}
