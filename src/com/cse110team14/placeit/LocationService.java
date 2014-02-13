package com.cse110team14.placeit;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Geocoder;
import android.location.Location;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.model.LatLng;

public class LocationService extends Service implements LocationListener,
  GooglePlayServicesClient.ConnectionCallbacks,
  GooglePlayServicesClient.OnConnectionFailedListener {

 private LocationRequest mLocationRequest;  
 private LocationClient mLocationClient;  

 public LocationService() {

 }

 /*
  Called befor service  onStart method is called.All Initialization part goes here
 */ 
 @Override
 public void onCreate() {
  mLocationRequest = LocationRequest.create();
  mLocationRequest.setInterval(5);
  //mLocationRequest.setInterval(CommonUtils.UPDATE_INTERVAL_IN_MILLISECONDS);
  mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
  mLocationRequest.setFastestInterval(5);
  //mLocationRequest.setFastestInterval(CommonUtils.FAST_INTERVAL_CEILING_IN_MILLISECONDS);
  mLocationClient = new LocationClient(getApplicationContext(), this,this);
  mLocationClient.connect();
 }

 /*
  You need to write the code to be executed on service start. Sometime due to memory congestion DVM kill the 
  running service but it can be restarted when the memory is enough to run the service again.
 */
  
 @Override
 public void onStart(Intent intent, int startId) {
  int start = Service.START_STICKY;
 }
 @Override
 public IBinder onBind(Intent intent) {
  return null;
 }
 
 /*
  Overriden method of the interface GooglePlayServicesClient.OnConnectionFailedListener .
  called when connection to the Google Play Service are not able to connect 
 */
 
 @Override
 public void onConnectionFailed(ConnectionResult connectionResult) {
  /*
   * Google Play services can resolve some errors it detects. If the error
   * has a resolution, try sending an Intent to start a Google Play
   * services activity that can resolve error.
   */
  if (connectionResult.hasResolution()) {
   /*try {
    // Start an Activity that tries to resolve the error
    connectionResult.startResolutionForResult(ApplicationClass.getCurrentActivity(),
      CommonUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST);*/
    /*
     * Thrown if Google Play services canceled the original
     * PendingIntent
     */
   /*} catch (IntentSender.SendIntentException e) {
    // Log the error
    e.printStackTrace();
   }*/
  } else {
   // If no resolution is available, display a dialog to the user with
   // the error.
   Log.i("info", "No resolution is available");
  }
 }
 
 /*
  This is overriden method of interface GooglePlayServicesClient.ConnectionCallbacks which is called
  when locationClient is connecte to google service.
  You can receive GPS reading only when this method is called.So request for location updates from this
  method rather than onStart() 
 
 */
 @Override
 public void onConnected(Bundle arg0) {
  Log.i("info", "Location Client is Connected");
  mLocationClient.requestLocationUpdates(mLocationRequest, this);
  Log.i("info", "Service Connect status :: " + isServicesConnected());
 }

 @Override
 public void onDisconnected() {
  Log.i("info", "Location Client is Disconnected");
 }
 /*
  Overrriden method of interface LocationListener called when location of gps device is changed.
  Location Object is received as a parameter.
  This method is called when location of GPS device is changed
 */
 @Override
 public void onLocationChanged(Location location) {
  double latitude = location.getLatitude();
  double longitude = location.getLongitude();
  Location myCurrentLocation = new Location("");
  myCurrentLocation.setLatitude(latitude);
  myCurrentLocation.setLongitude(longitude);
  for (PlaceIt pi : MainActivity.PlaceIts){
		//change place it type to pulled down
		Location placeItLocation = new Location("");
		placeItLocation.setLatitude(pi.getLocation().latitude);
		placeItLocation.setLongitude(pi.getLocation().longitude);
		if (myCurrentLocation.distanceTo(placeItLocation) < 100){
			//send notification to user here
			Log.e(null,"" + myCurrentLocation.distanceTo(placeItLocation));
			Log.e(null, "YAY IT WORKS!");
			//Create the notification and move the placeit to pulldown
			createNotification(null,pi);
			MainActivity.PlaceIts.remove(pi);
			MainActivity.pullDown.add(pi);
		}
  }

  Log.i("info", "Latitude :: " + latitude);
  Log.i("info", "Longitude :: " + longitude);
 }

 /**
  * Verify that Google Play services is available before making a request.
  * @return true if Google Play services is available, otherwise false
  */
 private boolean isServicesConnected() {
  // Check that Google Play services is available
  int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(LocationService.this);

  // If Google Play services is available
  if (ConnectionResult.SUCCESS == resultCode) {
   return true;
  } else {
   return false;
  }
 }
 /*
  Called when Sevice running in backgroung is stopped.
  Remove location upadate to stop receiving gps reading
 */
 @Override
 public void onDestroy() {
  // TODO Auto-generated method stub
  Log.i("info", "Service is destroyed");
  mLocationClient.removeLocationUpdates(this);
  super.onDestroy();
 }
 
 @SuppressLint("NewApi")
	public void createNotification(View view, PlaceIt p) {
        // Prepare intent which is triggered if the
        // notification is selected
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setClass(getApplicationContext(), PulledListActivity.class);
        
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);

        long []vibrate = {500,500,500,500,500,500,500,500,500};
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        // Build notification
        // Actions are just fake
        Notification noti = new Notification.Builder(this)
            .setContentTitle("PlaceIt Notificaiton: " + p.getTitle())
            .setContentText("Description: " + p.getDescription())
            .setSound(alarmSound)
            .setVibrate(vibrate)
            .setSmallIcon(R.drawable.ic_launcher)
            .setContentIntent(pIntent).build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // hide the notification after its selected
        noti.flags |= Notification.FLAG_AUTO_CANCEL;

        notificationManager.notify(0, noti);

      }
}