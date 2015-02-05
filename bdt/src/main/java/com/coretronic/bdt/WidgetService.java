package com.coretronic.bdt;

import android.app.Service;
import android.content.*;
import android.location.*;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import com.coretronic.bdt.WalkWay.StepCount.StepListener;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by darren on 2014/12/15.
 */
public class WidgetService extends Service {

    private static final String TAG="WidgetService";
    private final String CLOCK_WIDGET_UPDATE = "com.coretronic.bdt.8BITCLOCK_WIDGET_UPDATE";
    private SharedPreferences sharedPreferences;
    //每15分鐘更新一次
    private static final int UPDATE_TIME = 15*60*1000;
    private UpdateThread mUpdateThread;
    private Context mContext;
    private int count=0;
    private String url = "";
    private AsyncHttpClient asyncHttpClient;
    private AsyncHttpClient client;
    private String temp;
    private String location;
    private String rain_rate;
    private String messageCounts;
    //    private StepListener stepListener;
    private StepListenerForWidget stepListener;
    private Handler serviceHandler = new Handler();
    private String currentTime, currentTime2;
    public static final int SCREEN_OFF_RECEIVER_DELAY = 100;
    private Long startTime;
    private String area, local;
    private String returnAddress;
    private double latitude=0.0;
    private double longitude =0.0;
    private double defaultLatitude = 24.711808;
    private double defaultLongitude = 120.915872;

    public JsonHttpResponseHandler locationJsonHandler = new JsonHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            Log.i(TAG, "onSuccess = " + response);
            try {
                JSONObject result = (JSONObject) response.getJSONObject("result");
                temp = result.getString("Temp");
                location = result.getString("Name");
                rain_rate = result.getString("Pop");
                Log.i(TAG, "temp: " + temp);
                Log.i(TAG, "name: " + location);
                Log.i(TAG, "pop: " + rain_rate);

                sharedPreferences.edit()
                        .putString(AppConfig.PREF_LOCATION_TEMP, temp)
                        .putString(AppConfig.PREF_LOCATION_COUNTRY, location)
                        .putString(AppConfig.PREF_LOCATION_RAIN, rain_rate)
                        .commit();

            } catch (JSONException e){
                e.printStackTrace();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            Log.i(TAG, "onFailure");
//            isLoadLocationData = false;
        }
    };

    public JsonHttpResponseHandler stepCountJsonHandler = new JsonHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            Log.i(TAG, "onSuccess = " + response);
            //上傳成功後將count清為0
            Log.i(TAG, "before upload: " + stepListener.getSteps());
//            stepListenerForWidget.stop();
//            stepListenerForWidget.start();
            stepListener.dailyInit();
            Log.i(TAG, "after upload: " + stepListener.getSteps());
            sharedPreferences.edit()
                    .putString(AppConfig.PREF_STEP_KEY_WIDGET, "")
                    .commit();

            Log.i(TAG, "after upload count: " + sharedPreferences.getString(AppConfig.PREF_STEP_KEY_WIDGET, ""));
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            Log.i(TAG, "onFailure");
            stepListener.dailyInit();
//            stepListenerForWidget.stop();
//            stepListenerForWidget.start();
            sharedPreferences.edit()
                    .putString(AppConfig.PREF_STEP_KEY_WIDGET, "")
                    .commit();
        }
    };

    public JsonHttpResponseHandler msgCountJsonHandler = new JsonHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            Log.i(TAG, "onSuccess = " + response);
            try {
                JSONObject result = (JSONObject) response.getJSONObject("result");
                messageCounts = result.getString("MessageCounts");
//                temp = result.getString("Temp");
//                location = result.getString("Name");
//                rain_rate = result.getString("Pop");
//                Log.i(TAG, "temp: " + temp);
//                Log.i(TAG, "name: " + location);
//                Log.i(TAG, "pop: " + rain_rate);
                Log.i(TAG, "MessageCounts: " + messageCounts);
//                sharedPreferences.edit()
//                        .putString(AppConfig.PREF_LOCATION_TEMP, temp)
//                        .putString(AppConfig.PREF_LOCATION_COUNTRY, location)
//                        .putString(AppConfig.PREF_LOCATION_RAIN, rain_rate)
//                        .putString(AppConfig.PREF_MSG_COUNT, messageCounts)
//                        .commit();

                sharedPreferences.edit()
                        .putString(AppConfig.PREF_MSG_COUNT, messageCounts)
                        .commit();

            } catch (JSONException e){
                e.printStackTrace();
            }

        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            Log.i(TAG, "onFailure");
        }
    };

    public JsonHttpResponseHandler addressJsonHandler = new JsonHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            Log.i(TAG, "address onSuccess = " + response);
            try {
                Log.i(TAG, "address in....");
                JSONArray resultsAry = null;
                resultsAry = response.getJSONArray("results");

//                if (response.getJSONArray("status").get(0).equals("ZERO_RESULTS")){
//                    Log.i(TAG, "resultsAry == null");
//
//                }else{
//
//                    Log.i(TAG, "resultsAry != null");
//                }

                JSONObject addresObj = resultsAry.getJSONObject(1);
                JSONArray addCompAry = addresObj.getJSONArray("address_components");
                JSONObject areaObj = (JSONObject) addCompAry.get(1);
                String areaStr = areaObj.getString("long_name");
                JSONObject locateObj = (JSONObject) addCompAry.get(2);
                String locateStr = locateObj.getString("long_name");
                //areaStr : 鎮 locateStr:縣
                Log.i(TAG, "areaStr :" + areaStr + "/locateStr:" + locateStr);
                requestWeatherData(latitude, longitude, areaStr, locateStr);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            Log.d(TAG, "onFailure");
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            super.onFailure(statusCode, headers, responseString, throwable);
            Log.i(TAG, "444");
            Log.i(TAG, "responseString:" + responseString);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate...");
        stepListener = new StepListenerForWidget(this);
        sharedPreferences = getSharedPreferences(AppConfig.SHAREDPREFERENCES_NAME, 0);
        registerReceiver(mReceiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));
        startTime = System.currentTimeMillis();

        Log.i(TAG, "UUID:  " + sharedPreferences.getString(AppConfig.PREF_UNIQUE_ID, ""));

//        // 要求GPS定位經緯度
//        requestGPSInfo();
//        Log.i(TAG, "latitude0:  " + latitude);
//        Log.i(TAG, "longitude0:  " + longitude);
//        // 判斷是否抓到經緯度, 沒有就吐預設位置
//        judgeLatLon(latitude, longitude);
//        Log.i(TAG, "latitude1:  " + latitude);
//        Log.i(TAG, "longitude1:  " + longitude);
//        // 要求首頁資訊
//        requestMsgInfo();
//        // 要求鄉鎮位置
//        requestAddressfromGoogle(latitude, longitude);

//        requestGeoInfo();

        // 建立並開啓排程UpdateThread
        mUpdateThread = new UpdateThread();
        Log.i(TAG, "updateThread...");
        mUpdateThread.start();

        mContext = this.getApplicationContext();
    }

    @Override
    public void onDestroy(){
        //中斷排程
        if (mUpdateThread != null) {
            mUpdateThread.interrupt();
        }
        serviceHandler.removeCallbacks(UpdateSteps);
        serviceHandler.removeCallbacks(DailyCountUpload);
        stepListener.stop();
        unregisterReceiver(mReceiver);

        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    //服務開始，啟用startService()時，onStartCommand()被執行
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);
        stepListener.start();
        serviceHandler.postDelayed(UpdateSteps, 1000);
        serviceHandler.postDelayed(DailyCountUpload, 1000);

        return START_STICKY;
    }

    //GPS經緯度資訊
    private void requestGPSInfo() {

        LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(location != null){
                Log.i(TAG, "1111");
                latitude = location.getLatitude();
                longitude = location.getLongitude();
            }
        }else{
            LocationListener locationListener = new LocationListener() {
                //定位狀態改變
                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                }
                //當GPS或網路定位功能開啟
                @Override
                public void onProviderEnabled(String provider) {
                }
                //當GPS或網路定位功能關閉時
                @Override
                public void onProviderDisabled(String provider) {
                }
                //當地點改變時
                @Override
                public void onLocationChanged(Location location) {
                    if (location != null) {
                        Log.i(TAG, "2222");
                        Log.i("Map", "Location changed : Lat: "
                                + location.getLatitude() + " Lng: "
                                + location.getLongitude());
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                    }
                }
            };
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,1000, 0,locationListener);
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if(location != null){
                Log.i(TAG, "3333");
                latitude = location.getLatitude();
                longitude = location.getLongitude();
            }
        }
    }

    private void judgeLatLon(double lat, double lng){
        if (lat == 0.0 && lng == 0.0){
            Log.i(TAG, "4444");
            latitude = defaultLatitude;
            longitude = defaultLongitude;
            sharedPreferences.edit()
                    .putString(AppConfig.PREF_WIDGET_LAT, String.valueOf(latitude))
                    .putString(AppConfig.PREF_WIDGET_LNG, String.valueOf(longitude))
                    .commit();
            Log.i(TAG, "LAT-1: " + sharedPreferences.getString(AppConfig.PREF_WIDGET_LAT, ""));
            Log.i(TAG, "LNG-1: " + sharedPreferences.getString(AppConfig.PREF_WIDGET_LNG, ""));
        }else{
            sharedPreferences.edit()
                    .putString(AppConfig.PREF_WIDGET_LAT, String.valueOf(latitude))
                    .putString(AppConfig.PREF_WIDGET_LNG, String.valueOf(longitude))
                    .commit();
            Log.i(TAG, "LAT-2: " + sharedPreferences.getString(AppConfig.PREF_WIDGET_LAT, ""));
            Log.i(TAG, "LNG-2: " + sharedPreferences.getString(AppConfig.PREF_WIDGET_LNG, ""));
        }
    }


    // get current location
    private void requestAddressfromGoogle(double lat, double lng) {
        Log.i(TAG, "lat:" + lat + "/lng:" + lng);
        final String url = "http://maps.googleapis.com/maps/api/geocode/json?latlng=" + lat + "," + lng + "&sensor=true";
        RequestParams params = new RequestParams();
//        params.add("uid", UUIDChose);
        client = new AsyncHttpClient();
        client.setTimeout(AppConfig.TIMEOUT);
        client.post(url, params, addressJsonHandler);
    }

//    //向Google API要求鄉鎮位置
//    private void requestGeoInfo(){
//        Log.i(TAG, "latitude1:  " + latitude);
//        Log.i(TAG, "longitude1:  " + longitude);
//
//        Geocoder gc = new Geocoder(this, Locale.TRADITIONAL_CHINESE);
//        List<Address> lstAddress = null;
//        try {
//            lstAddress = gc.getFromLocation(latitude, longitude, 1);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        returnAddress=lstAddress.get(0).getAddressLine(0);
//        local = lstAddress.get(0).getAdminArea();
//        area = lstAddress.get(0).getLocality();
//        Log.i(TAG, "returnAddress: " + returnAddress);
//        Log.i(TAG, "Area: " + area); //城市
//        Log.i(TAG, "Local: " + local); //鄉鎮
//    }

    private void requestWeatherData(double lat, double lng, String locate, String area) {
        url = AppConfig.DOMAIN_SITE_PATE + AppConfig.REQUEST_CURRENT_TEMPAUREATE_LATWITHLOCATE;
        RequestParams params = new RequestParams();
        params.add("lat", String.valueOf(lat));
        params.add("lng", String.valueOf(lng));
        params.add("locate", area);
        params.add("area", locate);
        Log.i(TAG, "params:  " + params);

        client = new AsyncHttpClient();
        client.setTimeout(AppConfig.TIMEOUT);
        client.post(url, params, locationJsonHandler);
    }

    private void requestMsgInfo(){
        url = AppConfig.DOMAIN_SITE_PATE + AppConfig.REQUEST_HOME;
        RequestParams params = new RequestParams();
        params.add("lat", "");
        params.add("lng", "");
        params.add("uid", sharedPreferences.getString(AppConfig.PREF_UNIQUE_ID, ""));
        params.add("time", AppUtils.getChineseSystemTime());
        Log.i(TAG, "params:  " + params);

        client = new AsyncHttpClient();
        client.setTimeout(AppConfig.TIMEOUT);
        client.post(url, params, msgCountJsonHandler);

    }

    //每五分鐘傳送每日計步數量
    private void insertDailyCount(){
        callSystemTime();
        url = AppConfig.DOMAIN_SITE_PATE + AppConfig.INSERT_DAILY_WALK_COUNT;
        RequestParams params = new RequestParams();
        Log.i(TAG, "sharedPreferences uuid: " + sharedPreferences.getString(AppConfig.PREF_UNIQUE_ID, ""));
        params.add("uid", sharedPreferences.getString(AppConfig.PREF_UNIQUE_ID, ""));
//        params.add("uid", "uuid-1234");
        params.add("start", "");
        params.add("end", currentTime);
        params.add("count", sharedPreferences.getString(AppConfig.PREF_STEP_KEY_WIDGET, ""));
        Log.i(TAG, "params:  " + params);

        client = new AsyncHttpClient();
        client.setTimeout(AppConfig.TIMEOUT);
        client.post(url, params, stepCountJsonHandler);
    }

    //處理步數
    private Runnable UpdateSteps = new Runnable() {
        public void run() {
            // TODO Auto-generated method stub
            // 需要背景做的事
            int step = stepListener.getSteps();
            String stepCount = String.valueOf(step);
            Log.i(TAG, "step: " + stepCount);
            sharedPreferences.edit()
                    .putString(AppConfig.PREF_STEP_KEY_WIDGET, stepCount)
                    .commit();

            Intent updateIntent = new Intent(CLOCK_WIDGET_UPDATE);
            mContext.sendBroadcast(updateIntent);
            Log.i(TAG, "update broadcast");
            serviceHandler.postDelayed(this, 3*1000);
        }
    };

    private Runnable DailyCountUpload = new Runnable() {
        public void run() {
            callSystemTime2();
            Log.i(TAG, "time for count upload: " + currentTime2);
            if (currentTime2.equals("23:55")){
                insertDailyCount();
            }

            Intent updateIntent = new Intent(CLOCK_WIDGET_UPDATE);
            mContext.sendBroadcast(updateIntent);
            Log.i(TAG, "update broadcast");
            serviceHandler.postDelayed(this, 28*1000);
        }
    };

    private class UpdateThread extends Thread {

        @Override
        public void run() {
            super.run();
            try {
                while (true) {
                    Log.i(TAG, "run thread for 4 steps...");
                    //每15分鐘更新以下4個步驟

                    // 要求GPS定位經緯度
                    requestGPSInfo();
                    Log.i(TAG, "latitude0:  " + latitude);
                    Log.i(TAG, "longitude0:  " + longitude);
                    sleep(2000);
                    // 判斷是否抓到經緯度, 沒有就吐預設位置
                    judgeLatLon(latitude, longitude);
                    Log.i(TAG, "latitude1:  " + latitude);
                    Log.i(TAG, "longitude1:  " + longitude);
                    sleep(2000);
                    // 要求首頁資訊
                    requestMsgInfo();
                    sleep(2000);
                    // 要求鄉鎮位置
                    requestAddressfromGoogle(latitude, longitude);

//                    requestGPSInfo();
//                    sleep(3000);
//                    requestGeoInfo();
//                    sleep(3000);
//                    requestWeatherData();
//                    sleep(3000);
//                    requestMsgInfo();

//                    Bundle bundle = new Bundle();
//                    bundle.putString("temp", temp);
//                    bundle.putString("location", location);
//                    bundle.putString("pop", rain_rate);
                    Intent updateIntent = new Intent(CLOCK_WIDGET_UPDATE);
//                    updateIntent.putExtras(bundle);
                    Log.i(TAG, "update broadcast");

                    mContext.sendBroadcast(updateIntent);

                    Thread.sleep(UPDATE_TIME);
                }
            } catch (InterruptedException e) {
                //異常時，终止排程
                e.printStackTrace();
            }
        }
    }

    public BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "onReceive("+intent+")");

            if (!intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                return;
            }

            Runnable runnable = new Runnable() {
                public void run() {
                    Log.i(TAG, "Runnable executing.");
//                    stepListener.stop();
                    stepListener.start();
                }
            };

            serviceHandler.postDelayed(runnable, SCREEN_OFF_RECEIVER_DELAY);
        }
    };

    //抓取系統時間
    private void callSystemTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis()); // 獲取當前時間
        currentTime = formatter.format(curDate);
        Log.i(TAG, "==Time==: " + currentTime);
    }

    private void callSystemTime2() {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        Date curDate = new Date(System.currentTimeMillis()); // 獲取當前時間
        currentTime2 = formatter.format(curDate);
        Log.i(TAG, "==Time==: " + currentTime2);
    }

}