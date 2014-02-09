package com.cse110team14.placeit;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.EditText;
import android.widget.RadioButton;

public class CreatePlaceIts extends Activity {
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
        setContentView(R.layout.create_placeits);
  
        EditText title = (EditText)findViewById(R.id.title);
        EditText description = (EditText)findViewById(R.id.description);
        EditText date = (EditText)findViewById(R.id.date);
        EditText color = (EditText)findViewById(R.id.color);
        RadioButton weekly = (RadioButton)findViewById(R.id.weekly);
        RadioButton daily = (RadioButton)findViewById(R.id.daily);
        RadioButton minutely = (RadioButton)findViewById(R.id.minutely);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    // Inflate the menu; this adds items to the action bar if it is present.
	    getMenuInflater().inflate(R.menu.main, menu);
	    return true;
	}
	
	public void create(){
		
	}
	public void cancel(){
		
	}
}
