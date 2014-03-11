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
	public Button getFindButtion(){
		return mBtnFind;
	}
	public Button getRetrackButton(){
		return retrackBtn;
	}
	public Button getActiveButton(){
		return active;
	}
	public Button getPulledButton(){
		return pulled;
	}
	public Button getCreateButton(){
		return create;
	}
	public Button getRefreshButton(){
		return refresh;
	}
	public EditText getEditPlace(){
		return etPlace;
	}
}
