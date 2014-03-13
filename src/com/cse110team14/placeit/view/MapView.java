package com.cse110team14.placeit.view;

import com.cse110team14.placeit.MainActivity;
import com.cse110team14.placeit.R;
import com.cse110team14.placeit.R.id;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MapView {

	private View view;
	Button mBtnFind;
	Button retrackBtn;
	Button active;
	Button pulled;
	Button create;
	Button test;
	Button refresh;
	EditText etPlace;
	
	/*
	 * constructor of map view with buttons, initialize components with view find in xml
	 */
	public MapView(){
		View view = MainActivity.mainActivity.getWindow().getDecorView().getRootView();
        // Getting reference to the find button
        mBtnFind = (Button)view.findViewById(R.id.btn_show);
        retrackBtn = (Button)view.findViewById(R.id.retrack);
        active = (Button)view.findViewById(R.id.active);
        pulled = (Button)view.findViewById(R.id.pulled);
        create = (Button)view.findViewById(R.id.create);
        test = (Button)view.findViewById(R.id.test);
        refresh = (Button)view.findViewById(R.id.refresh);
        etPlace = (EditText) view.findViewById(R.id.et_place);
        
	}
	
	/*
	 * return the button component for start a search
	 */
	public Button getFindButtion(){
		return mBtnFind;
	}
	
	/*
	 * return the button component for retrack placeits
	 */
	public Button getRetrackButton(){
		return retrackBtn;
	}
	
	/*
	 * return the button component for active list display
	 */
	public Button getActiveButton(){
		return active;
	}
	
	/*
	 * return the button component for pulled-down list display
	 */
	public Button getPulledButton(){
		return pulled;
	}
	
	/*
	 * return the button component for creating a placeit
	 */
	public Button getCreateButton(){
		return create;
	}
	
	/*
	 * return the button component for refresh user data and display
	 */
	public Button getRefreshButton(){
		return refresh;
	}
	
	/*
	 * return the text field for search text input
	 */
	public EditText getEditPlace(){
		return etPlace;
	}
}
