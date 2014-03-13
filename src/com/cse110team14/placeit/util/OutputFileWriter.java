package com.cse110team14.placeit.util;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import android.content.Context;

import com.cse110team14.placeit.LoginActivity;
import com.cse110team14.placeit.MainActivity;
import com.cse110team14.placeit.model.CPlaceIts;
import com.cse110team14.placeit.model.PlaceIt;

public class OutputFileWriter {
	 
	 /**
     * Save specific list of placeits to corresponding file, called in onStop
     *  method
     */
    public static void saveRegularPlaceItsList(List<PlaceIt> list, String file){
    	try {
    		FileOutputStream out = MainActivity.mainActivity.openFileOutput(file, Context.MODE_PRIVATE);
    		//write place its to file
    		Iterator<PlaceIt> itr = list.iterator();
    		while(itr.hasNext()){
    			PlaceIt element = itr.next();
    			String str = element.getTitle() + "###" + element.getDescription() + "###" + element.getDateReminded()
    					+"###" + element.getDate() + "###" + element.getLocation().latitude +"###" +
    					element.getLocation().longitude + "###" + element.getColor()+ "###" + element.getPlaceItType() + 
    					"###" + element.getSneezeType() + "\n"; 
    			out.write(str.getBytes());
    		}
    		out.close();
    	} catch (FileNotFoundException e) {
    		e.printStackTrace();
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    }
    public static void saveCategoryPlaceItsList(List<CPlaceIts> list, String file){
    	try {
    		FileOutputStream out = MainActivity.mainActivity.openFileOutput(file, Context.MODE_PRIVATE);
    		//write place its to file
    		Iterator<CPlaceIts> itr = list.iterator();
    		while(itr.hasNext()){
    			CPlaceIts element = itr.next();
    			String str = element.getTitle() + "###" + element.getDescription() + "###" + element.getDateReminded()
    					+"###" + element.getDate() + "###" + element.getCategoriesToString() + "\n"; 
    			out.write(str.getBytes());
    		}
    		out.close();
    	} catch (FileNotFoundException e) {
    		e.printStackTrace();
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    }
    
    public static void saveLoginStatus(String user){
    	try {
    		FileOutputStream outputStatus = MainActivity.mainActivity.openFileOutput(MainActivity.loginStatusFile, Context.MODE_PRIVATE);

    		outputStatus.write((Boolean.toString(LoginActivity.loginActivity.logined) + "###" + user + "\n").getBytes());
	    	outputStatus.close();
	    	
    	} catch (FileNotFoundException e) {
    		e.printStackTrace();
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    }
}
