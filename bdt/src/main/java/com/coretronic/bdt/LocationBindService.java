package com.coretronic.bdt;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.app.Service;

/**
 * Created by james on 14/10/28.
 */
public class LocationBindService extends Service  implements LocationListener {
    private static final String TAG = LocationBindService.class.getSimpleName();


    private SharedPreferences sharedPreferences;
    private Context context = null;

    // 1 hour
//    private int TIME = 3600000;
    private int TIME = 1000;
    // meters
    private int DISTANCE = 20000;
    private boolean isGPSEnabled = false;
    private boolean isNetworkEnabled = false;
    public boolean isLocationAvailable = false;

    private Location mLocation;
    private double mLatitude;
    private double mLongitude;
    protected LocationManager mLocationManager;


    // 第一次啟動Service時會呼叫onCreate()
    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "LocationServices onCreate");
        context = this;
        // user info
        sharedPreferences = getSharedPreferences(AppConfig.SHAREDPREFERENCES_NAME, 0);

        mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        getLocation();

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return myBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {

        Log.i(TAG,"onUnbind");
        mLocationManager.removeUpdates(this);
        mLocation = null;
        mLocationManager = null;

        return super.onUnbind(intent);
    }

    public class LocationBinder extends Binder {

        public LocationBindService getService() {
            return LocationBindService.this;
        }
    }

    private LocationBinder myBinder = new LocationBinder();


    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i(TAG, "onLocationChanged location:" + location);
        mLatitude = location.getLatitude();
        mLongitude = location.getLongitude();
//        getAddress();
        sharedPreferences.edit()
                .putString(AppConfig.PREF_CURRENT_LATITUDE, String.valueOf(mLatitude))
                .putString(AppConfig.PREF_CURRENT_LONGITUDE, String.valueOf(mLongitude))
                .commit();

    }


    public Location getLocation() {
        try {
            // gps enabled
            isGPSEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            Log.i(TAG, "getLocation isGPSEnabled:" + isGPSEnabled);
            if (isGPSEnabled) {
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, TIME, DISTANCE, this);

                if (mLocationManager != null) {
                    mLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (mLocation != null) {
                        Log.i(TAG, "isGPSEnabled mLocation != null");
                        mLatitude = mLocation.getLatitude();
                        mLongitude = mLocation.getLongitude();
                        isLocationAvailable = true; // setting a flag that location is available
                        return mLocation;
                    }
                }
            }


            // if gps not enabled , use network fetch location
            isNetworkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if (isNetworkEnabled) {
                mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, TIME, DISTANCE, this);
                if (mLocationManager != null) {
                    mLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (mLocation != null) {
                        Log.i(TAG, "isNetworkEnabled mLocation != null");
                        Log.i(TAG, "isNetworkEnabled mLocation != null:" + mLocation);
                        mLatitude = mLocation.getLatitude();
                        mLongitude = mLocation.getLongitude();
                        isLocationAvailable = true; // setting a flag that location is available
                        return mLocation;
                    }
                }
            }

            // gps and network not get location
//            if (isGPSEnabled == false) {
//                turnOnGPSAlert();
//            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        isLocationAvailable = false;
        return null;
    }

    public double getLatitude() {
        Log.i(TAG, "getLatitude mLocation:" + mLocation);
        if (mLocation != null) {
            mLatitude = mLocation.getLatitude();
        }
        return mLatitude;
    }

    public double getLongitude() {
        if (mLocation != null) {
            mLongitude = mLocation.getLongitude();
        }
        return mLongitude;
    }

    public void checkGetGPS() {
        Log.i(TAG,"isLocationAvailable:"+isLocationAvailable);
        if (isLocationAvailable == false) {
            turnOnGPSAlert();
        }
    }

    private void turnOnGPSAlert() {
        Log.i(TAG, "turnOnGPSAlert");
        AlertDialog.Builder mAlertDialog = new AlertDialog.Builder(context);

        // Setting Dialog Title
        mAlertDialog
                .setMessage(getString(R.string.gps_off_alert))
                .setPositiveButton(getString(R.string.goto_turnon_gps), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        context.startActivity(intent);
                    }
                })
                .setNegativeButton(getString(R.string.dont_goto_turnon_gps), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).show();
    }

}
