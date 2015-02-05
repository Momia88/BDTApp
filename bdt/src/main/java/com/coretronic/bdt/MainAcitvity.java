package com.coretronic.bdt;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.*;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.*;
import com.coretronic.bdt.DataModule.HomeDataInfo;
import com.coretronic.bdt.Friend.FriendActivity;
import com.coretronic.bdt.HealthKnowledge.HealthArticleDetailActivity;
import com.coretronic.bdt.Person.PersonActivity;
import com.coretronic.bdt.Person.Register.PersonLoginActivity;
import com.coretronic.bdt.Utility.CustomerOneBtnAlertDialog;
import com.coretronic.bdt.Utility.CustomerTwoBtnAlertDialog;
import com.coretronic.bdt.Utility.GeocoderUtility;
import com.coretronic.bdt.module.AnimatedGifImageView;
import com.coretronic.bdt.module.GradePopupWindow;
import com.coretronic.bdt.module.MenuPopupWindow;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//import com.google.android.gms.location.LocationClient;

/**
 * Created by Morris on 2014/8/11.
 */
public class MainAcitvity extends Activity implements
        GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener,
        LocationListener {
    public static final int USER_ID_LENGTH = 20;
    private String TAG = MainAcitvity.class.getSimpleName();
    private Context mContext;
    private SharedPreferences sharedPreferences;
    private AsyncHttpClient client;
    private RequestParams params;
    private PopupWindow menuPopupWindow;
    private PopupWindow gradePopupWindow;

    private String strForecast = "http://api.openweathermap.org/data/2.5/forecast/daily?mode=json&units=metric&cnt=12";

    private Button btnGrade;
    private Button btnPopMenu;

    private TextView countryText;
    private String country = "";

    private String currentTime;
    private String append;

    private RelativeLayout navigationBar;

    private LinearLayout dateLL;
    // bg
    private RelativeLayout mainLayout;
    private BitmapDrawable bgDrawable;
    private BitmapDrawable mascotDrawable;
    private ImageView mascoIV;
    private ProgressDialog dialog = null;
    //    private ImageView newArticle;
//    private ImageView newFriend;
    // ui
    private FrameLayout newMessageFL = null;
    private FrameLayout newArticlesFL = null;
    private FrameLayout newFriendsFL = null;
    private TextView mainDateTV = null;
    private TextView mainLunarTV = null;
    private TextView mainDespTV = null;
    private TextView mainLocateTV = null;
    private TextView temperatureTV = null;
    // 降雨機率textview
    private TextView rainPopTV = null;

    private TextView newMsgTV = null;
    private TextView newArticlesTV = null;
    private TextView newFriendsTV = null;

    private TextView newFriendsCount = null;
    private TextView newArticlesCount = null;
    private TextView newMsgCount = null;


    private ImageView newMessageIV = null;
    private ImageView newArticlesIV = null;
    private ImageView newFriendsIV = null;


    //    private Boolean isMenuShow = false;
    private AnimationDrawable animDrawable;
    private Animation animation;
    private String UUIDChose;
    private int friendCount = 0;
    private int messageCount = 0;
    private int articlesCount = 0;

    private double currentLat = 0.0;
    private double currentLng = 0.0;
    GeocoderUtility geocoderUtility;

    private NotifyTimerReceiver receiver;
    private long currentMills = 0;

    // animation
    private AnimatedGifImageView animatedGifState;
    private AnimatedGifImageView animatedGifIdle;
    private AnimatedGifImageView animatedGifEat;
    private AnimatedGifImageView animatedGifWashing;

    // alert
    private CustomerTwoBtnAlertDialog askSignInDialog = null;
    //    private AlertDialog notGetDataAlert = null;
    private CustomerTwoBtnAlertDialog exitAlert = null;
    private CustomerTwoBtnAlertDialog notGetDataAlert = null;
    private CustomerOneBtnAlertDialog exceptionAlertDialog = null;
    private CustomerOneBtnAlertDialog oneBtnAlertDialog = null;

    // data
    private Bitmap tempBM = null;
    private int screenWidth = 0;
    private int screenHeight = 0;
    private RotateAnimation anim;
    private Boolean isPlayingAnimate = false;
    private Boolean isLogin = false;

    // location
    private String uuidChose = "";
    private String url = "";
    private Boolean isLoadLocationData = false;
//    private LocationClient mLocationClient = null;
    private GoogleApiClient mLocationClient = null;
    private Location mCurrentLocation;
    private LocationRequest mLocationoRequest;
    private AsyncHttpClient asyncHttpClient;
    private Gson gson = new Gson();
    private HomeDataInfo homeDataInfo = null;
    //    private Intent locationServiceIntent = null;
    private SensorManager sm = null;

    // task
    GPSTimerTask gpsTimerTask = null;

    // timer
    Timer eatTimer = null;
    Timer dirtyTimer = null;

    // handler
    Handler gpsHandler = new Handler() {
        int i = 0;

        @Override
        public void handleMessage(Message msg) {

            Log.i(TAG, "gpsHandler msg.what:" + msg.what);
            switch (msg.what) {
                case 0:
//                    Log.i(TAG,"latitude:"+locationService.getLatitude());
                    Log.i(TAG, "gpsHandler");
                    Log.i(TAG, "latitude:" + sharedPreferences.getString(AppConfig.PREF_CURRENT_LATITUDE, "0"));
                    Log.i(TAG, "longitude:" + sharedPreferences.getString(AppConfig.PREF_CURRENT_LONGITUDE, "0"));


                    currentLat = Double.parseDouble(sharedPreferences.getString(AppConfig.PREF_CURRENT_LATITUDE, "0"));
                    currentLng = Double.parseDouble(sharedPreferences.getString(AppConfig.PREF_CURRENT_LONGITUDE, "0"));


                    Log.i(TAG, "country:" + geocoderUtility.getGeocoder(currentLat, currentLng, 1));

                    if (geocoderUtility.getGeocoder(currentLat, currentLng, 1).equals("")) {
                        countryText.setText(AppConfig.DEFAULT_PLACE);


                    } else {
                        countryText.setText(geocoderUtility.getGeocoder(currentLat, currentLng, 1));
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                getWeather(currentLat, currentLng);
                            }
                        }).start();
                    }

                    break;
            }
            super.handleMessage(msg);
        }
    };


    JsonHttpResponseHandler jsonHandler = new JsonHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            Log.d(TAG, "onSuccess = " + response);
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            Log.d(TAG, "onFailure");
        }

    };


    public JsonHttpResponseHandler homeDataJsonHandler = new JsonHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            Log.d(TAG, "onSuccess = " + response);
            try {
                if (response.get("msgCode").equals(AppConfig.SUCCESS_CODE)) {

                    homeDataInfo = gson.fromJson(response.toString(), HomeDataInfo.class);
                    Log.i(TAG, "homeDataInfo.getResult().getArticleCounts():" + homeDataInfo.getResult().getArticleCounts());
                    friendCount = Integer.valueOf(homeDataInfo.getResult().getFriendCounts());
                    articlesCount = Integer.valueOf(homeDataInfo.getResult().getArticleCounts());
                    messageCount = Integer.valueOf(homeDataInfo.getResult().getMessageCounts());
                    if (Integer.valueOf(homeDataInfo.getResult().getArticleCounts()) == 0) {
                        hideNewArticlesCircle();
                    } else {

                        newArticlesCount.setText(homeDataInfo.getResult().getArticleCounts());
                        showNewArticlesCircle();
                    }

                    if (Integer.valueOf(homeDataInfo.getResult().getFriendCounts()) == 0) {
                        if (isLogin == true) {
                            hideNewFriendsCircle();
                        } else {
                            showNewFriendsCircle();
                        }
                        //loginhideNewFriendsCircle();
                    } else {

                        newFriendsCount.setText(homeDataInfo.getResult().getFriendCounts());
                        showNewFriendsCircle();
                    }

                    if (Integer.valueOf(homeDataInfo.getResult().getMessageCounts()) == 0) {

                        hideMessageCircle();
                    } else {
                        newMsgCount.setText(homeDataInfo.getResult().getMessageCounts());
                        showMessageCircle();
                    }

                    if (dialog != null) {
                        dialog.dismiss();
                    }

                } else {


                    notGetDataAlert.show();
//                    notGetDataAlert = AppUtils.getAlertDialog(mContext, getString(R.string.get_data_error),getString(R.string.retry),getString(R.string.sure), erroAlertListener);
//                    notGetDataAlert.show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
//                notGetDataAlert = AppUtils.getAlertDialog(mContext, getString(R.string.get_data_error),getString(R.string.retry),getString(R.string.sure), erroAlertListener);
//                notGetDataAlert.show();
                notGetDataAlert.show();

            } catch (JsonSyntaxException e) {
                e.printStackTrace();
//                notGetDataAlert = AppUtils.getAlertDialog(mContext, getString(R.string.get_data_error),getString(R.string.retry),getString(R.string.sure),erroAlertListener);
//                notGetDataAlert.show();
                notGetDataAlert.show();
            } catch (Exception e) {
                e.printStackTrace();
//                notGetDataAlert = AppUtils.getAlertDialog(mContext, getString(R.string.get_data_error),getString(R.string.retry),getString(R.string.sure), erroAlertListener);
//                notGetDataAlert.show();
                notGetDataAlert.show();
            }
            if (dialog != null) {
                dialog.dismiss();
            }
        }


        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            Log.d(TAG, "onFailure");
            if (dialog != null) {
                dialog.dismiss();
            }

//            notGetDataAlert = AppUtils.getAlertDialog(mContext, getString(R.string.get_data_error),getString(R.string.retry),getString(R.string.sure), erroAlertListener);
//            notGetDataAlert.show();
            notGetDataAlert.show();
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            super.onFailure(statusCode, headers, responseString, throwable);
            Log.i(TAG, "444");
            Log.i(TAG, "responseString:" + responseString);
            if (dialog != null) {
                dialog.dismiss();
            }

//            notGetDataAlert = AppUtils.getAlertDialog(mContext, getString(R.string.get_data_error),getString(R.string.retry),getString(R.string.sure), erroAlertListener);
//            notGetDataAlert.show();
            notGetDataAlert.show();
        }
    };


    public JsonHttpResponseHandler locationJsonHandler = new JsonHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            Log.d(TAG, "qa onSuccess 2 = " + response);
            isLoadLocationData = false;
            try {
                if (response.get("msgCode").equals(AppConfig.SUCCESS_CODE)) {
//                    JSONArray result = response.getJSONArray("result");
                    JSONObject result = (JSONObject) response.getJSONObject("result");
                    if (!(result.getString("Name").equals(""))) {
                        mainLocateTV.setText(result.getString("Name"));
                    }

                    if (!(result.getString("Temp").equals(""))) {
                        temperatureTV.setText(result.getString("Temp"));
                    }
                    if (!(result.getString("Pop").equals(""))) {
                        rainPopTV.setText(result.getString("Pop"));
                    }

                } else {
//                    alert = AppUtils.getAlertDialog(mContext, getString(R.string.data_result_error), erroAlertListener);
//                    alert.show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                temperatureTV.setText("--");
                rainPopTV.setText("--");
//                alert = AppUtils.getAlertDialog(mContext, getString(R.string.data_result_error), erroAlertListener);
//                alert.show();
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
                temperatureTV.setText("--");
                rainPopTV.setText("--");
//                alert = AppUtils.getAlertDialog(mContext, getString(R.string.data_result_error), erroAlertListener);
//                alert.show();
            } catch (Exception e) {
                e.printStackTrace();
                temperatureTV.setText("--");
                rainPopTV.setText("--");
//                alert = AppUtils.getAlertDialog(mContext, getString(R.string.data_result_error), erroAlertListener);
//                alert.show();

            }

        }


        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            Log.d(TAG, "onFailure");
            isLoadLocationData = false;
            temperatureTV.setText("--");
            rainPopTV.setText("--");
//            alert = AppUtils.getAlertDialog(mContext, getString(R.string.data_load_error), erroAlertListener);
//            alert.show();

        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            super.onFailure(statusCode, headers, responseString, throwable);
            Log.i(TAG, "444");
            Log.i(TAG, "responseString:" + responseString);
            temperatureTV.setText("--");
            rainPopTV.setText("--");
        }
    };

    /*
    private ServiceConnection serviceCon = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
//            LocationServices.LocationBinder binder = (LocationServices.LocationBinder) service;
//            LocationServices locationServices = binder.getService();
//            locationService = ((LocationServices.LocationBinder) service).getService();

            LocationBindService.LocationBinder binder = (LocationBindService.LocationBinder) service;
            LocationBindService locationBindService = binder.getService();
            if (locationBindService.isLocationAvailable == true) {
                Log.i(TAG, "latitude/longitude:" + locationBindService.getLatitude() + "/" + locationBindService.getLongitude());
            }
//            locationBindService.checkGetGPS();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

*/
    private void getWeather(double lat, double lng) {
        String str = strForecast + "&lat=" + lat + "&lon=" + lng;
        Log.i(TAG, "weather url:" + str);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        try {
            setContentView(R.layout.main_layout);
            mContext = this;

            // user info
            sharedPreferences = getSharedPreferences(AppConfig.SHAREDPREFERENCES_NAME, 0);
            screenWidth = sharedPreferences.getInt(AppConfig.PREF_SCREEN_WIDTH, 0);
            screenHeight = sharedPreferences.getInt(AppConfig.PREF_SCREEN_HEIGHT, 0);
            Log.i(TAG, "screenWidth:" + screenWidth);
            Log.i(TAG, "screenHeight:" + screenHeight);

            sharedPreferences.edit()
                    .putBoolean(AppConfig.PREF_FIRST_USED, false)
                    .commit();

            dialog = new ProgressDialog(mContext);

            geocoderUtility = new GeocoderUtility(MainAcitvity.this);
            sm = (SensorManager) getSystemService(SENSOR_SERVICE);

//        askSignInDialog = AppUtils.getAlertDialog(mContext, getString(R.string.pp_msg_need_register), getString(R.string.pp_register_title), "取消", askSignInDialogListener);

            //init view
            initView();

            askSignInDialog = new CustomerTwoBtnAlertDialog(mContext);
            notGetDataAlert = new CustomerTwoBtnAlertDialog(mContext);
            notGetDataAlert.setMsg(getString(R.string.get_data_error))
                    .setPositiveBtnText(getString(R.string.retry))
                    .setNegativeBtnText(getString(R.string.sure))
                    .setPositiveListener(erroAlertListener)
                    .setNegativeListener(alertDismissListener);

            askSignInDialog.setMsg(getString(R.string.pp_msg_need_register))
                    .setPositiveBtnText(getString(R.string.pp_register_title))
                    .setNegativeBtnText(getString(R.string.cancel))
                    .setPositiveListener(askSignInDialogListener)
                    .setNegativeListener(alertDismissListener);


            exceptionAlertDialog = AppUtils.getAlertDialog(mContext, getString(R.string.get_data_error), erroAlertListener);

            sendRegIdToService();

            menuPopupWindow = new MenuPopupWindow(mContext, null);
            gradePopupWindow = new GradePopupWindow(mContext, null);
            // TODO TEST ID
            UUIDChose = sharedPreferences.getString(AppConfig.PREF_UNIQUE_ID, "");
//        UUIDChose = "uuid-1234";
//        sharedPreferences.edit().putString(AppConfig.PREF_UNIQUE_ID,UUIDChose).commit();

            Log.i(TAG, "UUID:  " + UUIDChose);

            // Get Json from Service
            final String url = AppConfig.DOMAIN_SITE_PATE + AppConfig.REQUEST_MEMBER_INFO;
            params = new RequestParams();
            params.add("uid", UUIDChose);
            client = new AsyncHttpClient();
            client.setTimeout(AppConfig.TIMEOUT);
            client.post(url, params, jsonHandler);


            // animation
//        anim = new RotateAnimation(0, 12f, 0f, 0f);
            anim = new RotateAnimation(0, 12f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                    0.5f);
            anim.setInterpolator(new LinearInterpolator());
            anim.setRepeatCount(Animation.INFINITE);
            anim.setDuration(4000);
            anim.setRepeatMode(Animation.REVERSE);
//        locationServiceIntent = new Intent(this, LocationBindService.class);
//        bindService(locationServiceIntent, serviceCon, Context.BIND_AUTO_CREATE);


            registerTimerReceiver();
            Log.i(TAG, "1111  createGPSTimer");
            createGPSTimer();

            if (this.isRedirectedByUrlScheme()) {
                this.handleRedirection(this.getArticleIdFromRedirectedUrl());
            }

            checkEnterAppMethod();


            // location
            asyncHttpClient = new AsyncHttpClient();
            asyncHttpClient.setTimeout(AppConfig.TIMEOUT);
//            mLocationClient = new LocationClient(this, this, this);
            mLocationClient = new GoogleApiClient.Builder(getApplicationContext())
                    .addApi(LocationServices.API)
                    .build();
            mLocationoRequest = LocationRequest.create();
            mLocationoRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
            mLocationoRequest.setInterval(AppConfig.LOCATION_INTERVAL);
            mLocationoRequest.setFastestInterval(AppConfig.LOCATION_FASTINTERVAL);

            // set default location
            requestWeatherData(AppConfig.LOCATION_DEFAULT_LAT, AppConfig.LOCATION_DEFAULT_LNG, AppConfig.LOCATION_DEFAULT_LOCATE, AppConfig.LOCATION_DEFAULT_AREA);
//            requestWeatherData(24.874420, 121.236095, "龍潭鄉", "桃園縣");

//            requestCorrdinateAndLocate();
        } catch (Exception e) {
//            exceptionAlertDialog.show();
        }

        // if user is log in and setting of auto invite is enabled, do auto invite
        if ((sharedPreferences.getBoolean(AppConfig.PREF_IS_LOGIN, false)) &&
                (sharedPreferences.getBoolean(AppConfig.PREF_IS_ENABLED_AUTO_INVITE, false))) {
            String userId = sharedPreferences.getString(AppConfig.PREF_UNIQUE_ID, "");
            if (userId.length() != USER_ID_LENGTH) {
                Toast.makeText(mContext, getString(R.string.friend_auto_invite_failed_due_to_no_uid), Toast.LENGTH_SHORT).show();
                return;
            }
            doAutoInvite(userId);
        }
    }


    private void requestCorrdinateAndLocate() {
        sharedPreferences.edit()
                .putString(AppConfig.MAIN_LAT_KEY, String.valueOf(mCurrentLocation.getLatitude()))
                .putString(AppConfig.MAIN_LNG_KEY, String.valueOf(mCurrentLocation.getLongitude()))
                .commit();
        if ((!(sharedPreferences.getString(AppConfig.MAIN_LAT_KEY, "").equals(""))) &&
                (!(sharedPreferences.getString(AppConfig.MAIN_LNG_KEY, "").equals("")))) {
            requestWeatherData(Double.valueOf(sharedPreferences.getString(AppConfig.MAIN_LAT_KEY, "")),
                    Double.valueOf(sharedPreferences.getString(AppConfig.MAIN_LNG_KEY, "")),
                    "", "");
        }


    }

    private void requestHomeData() {
        if (notGetDataAlert != null) {
            if (notGetDataAlert.isShowing())
                notGetDataAlert.dismiss();
        }

        dialog.setMessage(getString(R.string.dialog_download_msg));
        dialog.show();
        Log.i(TAG, "requestHomeData");
        url = AppConfig.DOMAIN_SITE_PATE + AppConfig.REQUEST_HOME;
        Log.i(TAG, "url:  " + url);
        uuidChose = sharedPreferences.getString(AppConfig.PREF_UNIQUE_ID, "");
        Log.i(TAG, "UUID:  " + uuidChose);
        RequestParams params = new RequestParams();
        params.add("uid", uuidChose);
//        params.add("uid", "U0000000000000000020");
//        params.add("uid", "uuid-1234");
        params.add("time", AppUtils.getChineseSystemTime());
        params.add("lat", sharedPreferences.getString(AppConfig.PREF_CURRENT_LATITUDE, ""));
        params.add("lng", sharedPreferences.getString(AppConfig.PREF_CURRENT_LONGITUDE, ""));
        Log.i(TAG, "params:  " + params);

        asyncHttpClient.post(url, params, homeDataJsonHandler);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);

        try {
            Log.i(TAG, "onNewIntent");
            if (geocoderUtility == null) {
                geocoderUtility = new GeocoderUtility(MainAcitvity.this);
            }

            if (this.isRedirectedByUrlScheme()) {
                this.handleRedirection(this.getArticleIdFromRedirectedUrl());
            }

            checkEnterAppMethod();

            requestCorrdinateAndLocate();
        } catch (Exception e) {
//            exceptionAlertDialog.show();
        }
    }


    /*
     * check if app through the push notification enter,
     *it will transfer to Health Detail Page.
     */
    private void checkEnterAppMethod() {
        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        // check push notification enter
        if ((bundle != null)) {
            if (bundle.getString(AppConfig.ENTERAPP_SOURCES) != null) {
                Log.i(TAG, "bundle.getString(AppConfig.ENTERAPP_SOURCES): " + bundle.getString(AppConfig.ENTERAPP_SOURCES));
                if (bundle.getString(AppConfig.ENTERAPP_SOURCES).equals(AppConfig.PUSHNOTIFY)) {
                    this.handleRedirection(bundle.getString(AppConfig.NEWS_ID_KEY));
                }
            }
        }
    }


    // login listener
    private View.OnClickListener askSignInDialogListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Log.i(TAG, "click the dialog sure");
            Intent intent = new Intent();
            intent.setClass(mContext, PersonLoginActivity.class);
            mContext.startActivity(intent);
            askSignInDialog.dismiss();
        }
    };


    private void initView() {
        // navigation bar
        navigationBar = (RelativeLayout) findViewById(R.id.navigationBar);
        mainLayout = (RelativeLayout) findViewById(R.id.relative_bg);
//        dateLL = (LinearLayout) findViewById(R.id.dateLL);
        btnPopMenu = (Button) findViewById(R.id.btnPopMenu);
        btnPopMenu.setOnClickListener(btnListener);

        btnGrade = (Button) findViewById(R.id.btnGrade);
        btnGrade.setOnClickListener(btnListener);


        mainDateTV = (TextView) findViewById(R.id.mainDateTV);
        mainLunarTV = (TextView) findViewById(R.id.mainLunarTV);
//        mainDespTV = (TextView) findViewById(R.id.tempDespTV);
        mainLocateTV = (TextView) findViewById(R.id.mainLocate);
        temperatureTV = (TextView) findViewById(R.id.temperatureTV);
        rainPopTV = (TextView) findViewById(R.id.rainTV);

        newMsgTV = (TextView) findViewById(R.id.newMsgTV);
        newArticlesTV = (TextView) findViewById(R.id.newArticlesTV);
        newFriendsTV = (TextView) findViewById(R.id.newFriendsTV);

        newFriendsCount = (TextView) findViewById(R.id.newFriendsCount);
        newArticlesCount = (TextView) findViewById(R.id.newArticlesCount);
        newMsgCount = (TextView) findViewById(R.id.newMsgCount);


        newMessageIV = (ImageView) findViewById(R.id.newMessage);
        newArticlesIV = (ImageView) findViewById(R.id.newArticles);
        newFriendsIV = (ImageView) findViewById(R.id.newFriends);

        newMessageFL = (FrameLayout) findViewById(R.id.newMessageFL);
        newArticlesFL = (FrameLayout) findViewById(R.id.newArticlesFL);
        newFriendsFL = (FrameLayout) findViewById(R.id.newFriendsFL);
        newMessageFL.setOnClickListener(btnListener);
        newArticlesFL.setOnClickListener(btnListener);
        newFriendsFL.setOnClickListener(btnListener);

        // set bg
        bgDrawable = new BitmapDrawable(getResources(), getResources().openRawResource(R.raw.bg_main));
        mascoIV = (ImageView) findViewById(R.id.masco);


//        mascotDrawable = new BitmapDrawable(getResources(), getResources().openRawResource(R.drawable.bg_main));
        tempBM = AppUtils.decodeBitmapFromResource(getResources(), R.drawable.tutor_turtle, (int) (screenWidth / 2.2), (int) (screenHeight / 2.2));
        mascoIV.setImageBitmap(tempBM);


//        Animation.AnimationListener onRotation = new Animation.AnimationListener() {
//
//            public void onAnimationStart(Animation animation) {
//                // TODO Auto-generated method stub
//
//            }
//
//            public void onAnimationRepeat(Animation animation) {
//                // TODO Auto-generated method stub
//
//            }
//
//            public void onAnimationEnd(Animation animation) {
//                x = 0 ;
//                y = 180;
//                mascoIV.clearAnimation();
//            }
//        };

        showMessageCircle();
        showNewArticlesCircle();
        showNewFriendsCircle();
//        RelativeLayout.LayoutParams layoutParams =
//                new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT);
//        layoutParams.setMargins(0,((screenHeight / 2) + (int)(screenHeight/2.03) / 2),10,0);
//        newFriendsIV.setLayoutParams(layoutParams);


        if (tempBM != null) {
//            tempBM.recycle();
            tempBM = null;
        }

        if (android.os.Build.VERSION.SDK_INT < 16) {
            mainLayout.setBackgroundDrawable(bgDrawable);
        } else {
            mainLayout.setBackground(bgDrawable);
        }
    }

    //
    private void setMainDate() {
//        Log.i(TAG,"setMainDate");
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd");

        Calendar calendar = Calendar.getInstance();
        Lunar lunar = new Lunar(calendar);

        String currentDate = sdf.format(calendar.getTime());
        String dayLongName = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());
        mainDateTV.setText(currentDate.toString());
//        mainLunarTV.setText(lunar.toString());
        // weekly day
        //dayLongName

        sdf = null;
        calendar = null;
        lunar = null;

    }


    // POST GCM Register ID to server
    private void sendRegIdToService() {
        final List<NameValuePair> params = new ArrayList<NameValuePair>();
        final String url = AppConfig.DOMAIN_SITE_PATE + AppConfig.UPDATE_REG_ID;
        Log.i(TAG, "regId:" + sharedPreferences.getString(AppConfig.REG_ID, ""));
        Log.i(TAG, "uid:" + sharedPreferences.getString(AppConfig.PREF_UNIQUE_ID, ""));
        Log.i(TAG, "time:" + AppUtils.getChineseSystemTime());
        params.add(new BasicNameValuePair("regId", sharedPreferences.getString(AppConfig.REG_ID, "")));
        params.add(new BasicNameValuePair("uid", sharedPreferences.getString(AppConfig.PREF_UNIQUE_ID, "")));
        params.add(new BasicNameValuePair("time", AppUtils.getChineseSystemTime()));
        new Thread(new Runnable() {
            @Override
            public void run() {
                String json = AppUtils.postResponse(url, params);
                Log.d(TAG, "json= " + json);
            }
        }).start();
    }


    // 檢查GPS Location的Timer
    private void createGPSTimer() {
        gpsTimerTask = new GPSTimerTask();
        Log.i(TAG, "createGPSTimer");
    }


    // === Clean timer event ===

    private void registerTimerReceiver() {
        // 設定只攔截會發送指定字串的 Broadcast
        IntentFilter filter = new IntentFilter(TimerServices.TIMER_ACTION);
        receiver = new NotifyTimerReceiver();
        // 註冊BroadcastReceiver，當欲攔截的Broadcast發送過來時，
        // 會呼叫對應的onReceive()
        registerReceiver(receiver, filter);
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (mLocationClient != null) {
//            mCurrentLocation = mLocationClient.getLastLocation();
            try {
//                Toast.makeText(this, "mCurrentLocation latit/lng:" + mCurrentLocation.getLatitude()+"/"+mCurrentLocation.getLongitude(), Toast.LENGTH_SHORT).show();
                Log.i(TAG, "Location  latit/lng:" + mCurrentLocation.getLatitude() + "/" + mCurrentLocation.getLongitude());
                Log.i(TAG, "isLoadLocationData:" + isLoadLocationData);
                sharedPreferences.edit()
                        .putString(AppConfig.MAIN_LAT_KEY, String.valueOf(mCurrentLocation.getLatitude()))
                        .putString(AppConfig.MAIN_LNG_KEY, String.valueOf(mCurrentLocation.getLongitude()))
                        .commit();


//                GetAddressPositionTask getAddressTask = new GetAddressPositionTask(mContext,3);
//                getAddressTask.execute();

//                getLocationInfo(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());

//                String geoCoderAddress = geocoderUtility.getGeocoder(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude(), 3);
//                Log.i(TAG, "location geoCoderAddress:" + geoCoderAddress);
//                //(鎮)
//                String locate = (geoCoderAddress.split(";"))[0];
//                //(縣)
//                String area = (geoCoderAddress.split(";"))[1];
//                Log.i(TAG, "location locate:" + locate);
//                Log.i(TAG, "location area:" + area);
//                if (isLoadLocationData == false) {
                    Log.i(TAG, "1111");
                    requestAddressfromGoogle(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
//                    getCurrentLocationViaJSON(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
//                    requestWeatherData(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude(), locate, area);
//                }
            } catch (NullPointerException npe) {
//                Toast.makeText(this, "Connection Failed:"+npe.getMessage(), Toast.LENGTH_SHORT).show();
            } catch (Exception e) {

            }

        }
    }

    @Override
    public void onDisconnected() {
//        Toast.makeText(this, "Disconnected.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLocationChanged(Location location) {
        try {
//            mCurrentLocation = mLocationClient.getLastLocation();
//        Toast.makeText(this, "Location changed latit/lng:" + mCurrentLocation.getLatitude()+"/"+mCurrentLocation.getLongitude(), Toast.LENGTH_SHORT).show();
            Log.i(TAG, "Location changed latit/lng:" + mCurrentLocation.getLatitude() + "/" + mCurrentLocation.getLongitude());

            sharedPreferences.edit()
                    .putString(AppConfig.MAIN_LAT_KEY, String.valueOf(mCurrentLocation.getLatitude()))
                    .putString(AppConfig.MAIN_LNG_KEY, String.valueOf(mCurrentLocation.getLongitude()))
                    .commit();

//            String geoCoderAddress = geocoderUtility.getGeocoder(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude(), 3);
//            //(鎮)
//            String locate = (geoCoderAddress.split(";"))[0];
//            //(縣)
//            String area = (geoCoderAddress.split(";"))[1];
//            Log.i(TAG, "location locate:" + locate);
//            Log.i(TAG, "location area:" + area);
//            if (isLoadLocationData == false) {
                requestAddressfromGoogle(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
//                requestWeatherData(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude(), locate, area);
//            }
        } catch (NullPointerException npe) {
//                Toast.makeText(this, "Connection Failed:"+npe.getMessage(), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {

        }

    }

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
    public void onConnectionFailed(ConnectionResult connectionResult) {
//        Toast.makeText(this, "Connection Failed", Toast.LENGTH_SHORT).show();
    }


    // locate 鎮   area 縣
    private void requestWeatherData(double lat, double lng, String locate, String area) {
//        progressDialog.setMessage(getString(R.string.dialog_download_msg));
//        progressDialog.show();
        isLoadLocationData = true;
        Log.i(TAG, "2222222");
//        url = AppConfig.DOMAIN_SITE_PATE + AppConfig.REQUEST_CURRENT_TEMPAUREATE;
        url = AppConfig.DOMAIN_SITE_PATE + AppConfig.REQUEST_CURRENT_TEMPAUREATE_LATWITHLOCATE;
        Log.i(TAG, "url:  " + url);
        uuidChose = sharedPreferences.getString(AppConfig.PREF_UNIQUE_ID, "");
        Log.i(TAG, "UUID:  " + uuidChose);
        RequestParams params = new RequestParams();
//        params.add("uid", uuidChose);
        params.add("lat", String.valueOf(lat));
        params.add("lng", String.valueOf(lng));
        params.add("locate", area);
        params.add("area", locate);
        Log.i(TAG, "params:  " + params);

        asyncHttpClient.post(url, params, locationJsonHandler);

    }

    /**
     * 取得手機內聯絡人電話列表，若筆數不為 0 且筆數有變化，則送出 HTTP 請求，並記錄此次聯絡人電話數。
     */
    private void doAutoInvite(String userId) {
        String requestUrl = AppConfig.DOMAIN_SITE_PATE + AppConfig.API_FRIEND_INVITE_AUTO;

        JSONArray jsonArray = new JSONArray(getLocalMobilePhoneListInTaiwanFormat(mContext));
        int phoneListCount = jsonArray.length();
        Log.d(TAG, "Phone List Count: " + phoneListCount);

        sharedPreferences.edit().putInt(AppConfig.PREF_LAST_LOCAL_PHONE_COUNT, phoneListCount).commit();

        if (phoneListCount == 0) {
            Toast.makeText(mContext, getString(R.string.friend_auto_invite_no_contact_message), Toast.LENGTH_LONG).show();
            return;
        }

        if (isPhoneListCountChanged(phoneListCount)) {
            String phoneListInJsonString = jsonArray.toString();
            Log.d(TAG, phoneListInJsonString);

            RequestParams params = new RequestParams();
            params.add(AppConfig.BUNDLE_ARGU_UID, userId);
            params.add(AppConfig.BUNDLE_ARGU_TEL_LIST, phoneListInJsonString);

            AsyncHttpClient requestAutoInviteHttpClient = new AsyncHttpClient();
            requestAutoInviteHttpClient.setTimeout(AppConfig.TIMEOUT);
            requestAutoInviteHttpClient.post(requestUrl, params, autoInviteHttpResponseHandler);
        } else {
            Toast.makeText(mContext, getString(R.string.friend_no_need_auto_invite_message), Toast.LENGTH_LONG).show();
        }
    }

    private boolean isPhoneListCountChanged(int phoneListCount) {
        return (phoneListCount == sharedPreferences.getInt(AppConfig.PREF_LAST_LOCAL_PHONE_COUNT, 0));
    }

    /**
     * 取得手機內聯絡人電話列表，去除所有非數字符號，並過濾以台灣地區手機號碼格式：10碼、09開頭。
     *
     * @param context
     * @return 手機號碼列表
     */
    private List<String> getLocalMobilePhoneListInTaiwanFormat(Context context) {
        ArrayList<String> phoneList = new ArrayList<String>();
        ContentResolver cr = context.getContentResolver();
        Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                if (Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String contactNumber = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        if (contactNumber == null) continue;
                        Pattern pattern = Pattern.compile("[^0-9]");
                        Matcher matcher = pattern.matcher(contactNumber);
                        contactNumber = matcher.replaceAll("").trim();
                        if ((contactNumber.length() == 10) || (contactNumber.startsWith("09")))
                            phoneList.add(contactNumber);
                        break;
                    }
                    pCur.close();
                }

            } while (cursor.moveToNext());
        }
        return phoneList;
    }


    private class NotifyTimerReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

//            Toast.makeText(MainAcitvity.this, "status and check",
//                    Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        try {
//        imgTortoiseEgg.startAnimation(animation);
            Log.i(TAG, "is Login:" + sharedPreferences.getBoolean(AppConfig.PREF_IS_LOGIN, false));
            isLogin = sharedPreferences.getBoolean(AppConfig.PREF_IS_LOGIN, false);
            // set bg
            bgDrawable = new BitmapDrawable(getResources(), getResources().openRawResource(R.raw.bg_main));
            mainLayout.setBackgroundDrawable(bgDrawable);

            mascoIV.startAnimation(anim);
            requestHomeData();

            setMainDate();
        } catch (Exception e) {
//            exceptionAlertDialog.show();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
//        imgTortoiseEgg.removeCallbacks(animRunable);
//        imgTortoiseEgg.clearAnimation();
        if (menuPopupWindow.isShowing()) {
            menuPopupWindow.dismiss();
        }

        if (dialog != null) {
            dialog.dismiss();
        }

        if (exitAlert != null) {
            exitAlert.dismiss();
        }

        if (exceptionAlertDialog != null) {
            exceptionAlertDialog.dismiss();
        }

        mascoIV.clearAnimation();
        isLoadLocationData = false;
        Log.i(TAG, "=====onPause=====");
//        clearAllAnimation();

//        mainLayout.getBackground().setCallback(null);
//        if (null != bgDrawable && !bgDrawable.getBitmap().isRecycled()) {
//            bgDrawable.getBitmap().recycle();
//        }

        if(oneBtnAlertDialog != null){
            oneBtnAlertDialog.dismiss();
        }

    }

    @Override
    protected void onStop() {
        // unregister listener
        super.onStop();
        mLocationClient.disconnect();
    }

    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "=====onDestroy=====");
//        clearAllAnimation();
        // 解除BroadcastReceiver的註冊
        unregisterReceiver(receiver);


        //recycle bg
//        mainLayout.getBackground().setCallback(null);
//        if (null != bgDrawable && !bgDrawable.getBitmap().isRecycled()) {
//            bgDrawable.getBitmap().recycle();
//        }
//        if( locationServiceIntent != null ) {
//        stopService(locationServiceIntent);
//        }
//        unbindService(serviceCon);

        //recycle bg
        mainLayout.getBackground().setCallback(null);
        if (null != bgDrawable && !bgDrawable.getBitmap().isRecycled()) {
            bgDrawable.getBitmap().recycle();
            bgDrawable = null;
        }
    }

    private boolean isCheckServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    private class GPSTimerTask extends TimerTask {
        @Override
        public void run() {
            Log.i(TAG, "GPSTimerTask run:");
            gpsHandler.sendEmptyMessage(0);
        }
    }


    // --------  Listener
    private View.OnClickListener btnListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent();
            switch (view.getId()) {
                case R.id.btnGrade:
                    Log.d(TAG, "btnGrade");
                    callSystemTime();
                    append = currentTime + "," +
                            UUIDChose + "," +
                            "MainActivity" + "," +
                            "Person" + "," +
                            "btnPerson" + "\n";
                    transactionLogSave(append);
                    intent.setClass(mContext, PersonActivity.class);
                    startActivity(intent);
                    intent = null;
//                    gradePopupWindow.showAsDropDown(navigationBar, 0, 0);

                    break;
                case R.id.btnPopMenu:
                    Log.d(TAG, "btnPopMenu");
                    callSystemTime();
                    append = currentTime + "," +
                            UUIDChose + "," +
                            "MainActivity" + "," +
                            "MenuPopupWindow" + "," +
                            "btnPopMenu" + "\n";
                    transactionLogSave(append);
                    menuPopupWindow.showAsDropDown(navigationBar, 0, 0);
                    break;

                case R.id.newMessageFL:
                    if (messageCount != 0) {
                        callSystemTime();
                        append = currentTime + "," +
                                UUIDChose + "," +
                                "MainActivity" + "," +
                                "NewMessageListActivity" + "," +
                                "btnNewMsg" + "\n";
                        transactionLogSave(append);
                        intent.setClass(MainAcitvity.this, NewMessageListActivity.class);
                        startActivity(intent);
                        intent = null;
//                        hideMessageCircle();
                    }

                    break;

                case R.id.newArticlesFL:
                    if (articlesCount != 0) {
                        callSystemTime();
                        append = currentTime + "," +
                                UUIDChose + "," +
                                "MainActivity" + "," +
                                "NewArticlesListActivity" + "," +
                                "btnNewArticle" + "\n";
                        transactionLogSave(append);
                        intent.setClass(MainAcitvity.this, NewArticlesListActivity.class);
                        startActivity(intent);
                        intent = null;
                    } else {

                    }

                    break;

                case R.id.newFriendsFL:
                    if (isLogin != true) {
                        askSignInDialog.show();
                    } else {
                        if (friendCount == 0) {
                            hideNewFriendsCircle();
                        } else {
                            callSystemTime();
                            append = currentTime + "," +
                                    UUIDChose + "," +
                                    "MainActivity" + "," +
                                    "FriendActivity" + "," +
                                    "btnFriend" + "\n";
                            transactionLogSave(append);
                            // went to friends list
                            intent.setClass(MainAcitvity.this, FriendActivity.class);
                            startActivity(intent);
//                            intent = null;
//                            Toast.makeText(mContext, "go to new friends page", 1).show();
                        }
                    }
                    break;

            }
        }
    };


    private View.OnClickListener erroAlertListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.i(TAG, " on error Litener click");
            if (notGetDataAlert != null) {
                notGetDataAlert.dismiss();
            }

            requestHomeData();
        }


    };


    private View.OnClickListener alertDismissListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.i(TAG, " on error Litener click");
            if (notGetDataAlert != null) {
                notGetDataAlert.dismiss();
            }

            if (askSignInDialog != null) {
                askSignInDialog.dismiss();
            }
        }


    };


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Log.d(TAG, "onKeyDown back");
            callSystemTime();
            append = currentTime + "," +
                    UUIDChose + "," +
                    "MainActivity" + "," +
                    "MainActivity" + "," +
                    "btnBack" + "\n";
            transactionLogSave(append);
            exitAlert = new CustomerTwoBtnAlertDialog(mContext);
            exitAlert.setMsg(getString(R.string.check_exit))
                    .setPositiveBtnText(getString(R.string.yes))
                    .setNegativeBtnText(getString(R.string.no))
                    .setPositiveListener(exitSureAlertListener)
                    .setNegativeListener(exitNoAlertListener)
                    .show();
//            exitAlert = AppUtils.getAlertDialog(mContext, getString(R.string.check_exit),getString(R.string.yes),getString(R.string.no), exitSureAlertListener, exitNoAlertListener);
//            exitAlert.show();

            // Dialog style
//            TextView tv = new TextView(mContext);
//            tv.setText(getString(R.string.check_exit));
//            tv.setTextSize(30);
//            tv.setPadding(30, 10, 10, 10);
//            ContextThemeWrapper contextThemeWrapper = new ContextThemeWrapper(mContext, R.style.customAlertDialog);
//            new AlertDialog.Builder(contextThemeWrapper)
//                    .setCustomTitle(tv)
//                    .setPositiveButton(getString(R.string.yes),
//                            new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int whichButton) {
//                                    callSystemTime();
//                                    append = currentTime + "," +
//                                            UUIDChose + "," +
//                                            "MainActivity" + "," +
//                                            "MainActivity" + "," +
//                                            "btnExitYes" + "\n";
//                                    transactionLogSave(append);
//                                    finish();
//                                }
//                            }
//                    )
//                    .setNegativeButton(getString(R.string.no),
//                            new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int whichButton) {
//                                    callSystemTime();
//                                    append = currentTime + "," +
//                                            UUIDChose + "," +
//                                            "MainActivity" + "," +
//                                            "MainActivity" + "," +
//                                            "btnExitNo" + "\n";
//                                    transactionLogSave(append);
//                                    dialog.dismiss();
//                                }
//                            }
//                    )
//                    .create()
//                    .show();
        } else if (keyCode == KeyEvent.KEYCODE_MENU) {
            callSystemTime();
            append = currentTime + "," +
                    UUIDChose + "," +
                    "MainActivity" + "," +
                    "MenuPopupWindow" + "," +
                    "btnPopMenu" + "\n";
            transactionLogSave(append);
            menuPopupWindow.showAsDropDown(navigationBar, 0, 0);
        }

        return false;
    }

    private View.OnClickListener exitSureAlertListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            callSystemTime();
            append = currentTime + "," +
                    UUIDChose + "," +
                    "MainActivity" + "," +
                    "MainActivity" + "," +
                    "btnExitYes" + "\n";
            transactionLogSave(append);
            finish();
        }

    };

    private View.OnClickListener exitNoAlertListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            callSystemTime();
            append = currentTime + "," +
                    UUIDChose + "," +
                    "MainActivity" + "," +
                    "MainActivity" + "," +
                    "btnExitNo" + "\n";
            transactionLogSave(append);
            exitAlert.dismiss();
        }
    };


    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart");
        mLocationClient.connect();
    }

    /**
     * 將文章 ID 傳到文章頁面，並進行導頁。
     */
    private void handleRedirection(String newsId) {
        Log.i(TAG, "handleRedirection newsID:" + newsId);
        Bundle bundle = new Bundle();
        bundle.putString(AppConfig.NEWS_ID_KEY, newsId);
        Intent intent = new Intent(MainAcitvity.this, HealthArticleDetailActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    /**
     * 取得導向 Url 中參數 aid 的值。
     *
     * @return 文章 ID，失敗回傳 null。
     */
    private String getArticleIdFromRedirectedUrl() {
        if ((getIntent() == null) || (getIntent().getData() == null))
            return null;
        Uri redirectUrl = getIntent().getData();
        String articleId = redirectUrl.getQueryParameter("aid");
//        getIntent().setData(null);
        return articleId;
    }

    /**
     * 檢查 Intent 中是否有資料，若有資料再檢查是否帶有 aid 參數，
     * 若通過檢查，代表此頁是經由 Url Scheme 導向而來。
     *
     * @return 是否經由導向而來。
     */
    private Boolean isRedirectedByUrlScheme() {
        if ((getIntent() == null) || (getIntent().getData() == null))
            return Boolean.FALSE;
        String redirectValue = this.getArticleIdFromRedirectedUrl();
        if (redirectValue == null)
            return Boolean.FALSE;
        else
            return Boolean.TRUE;
    }

    //寫入記錄到txt
    public void transactionLogSave(String append) {
        try {
            FileOutputStream outStream = new FileOutputStream("/sdcard/outputLog.txt", true);
            outStream.write(append.getBytes());
            outStream.close();
        } catch (FileNotFoundException e) {
            return;
        } catch (IOException e) {
            return;
        }
    }

    //抓取系統時間
    private void callSystemTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
        Date curDate = new Date(System.currentTimeMillis()); // 獲取當前時間
        currentTime = formatter.format(curDate);
        Log.i(TAG, "==Time==: " + currentTime);

    }


    /**
     * set circle showing or hiding
     */
    private void showNewFriendsCircle() {
        tempBM = AppUtils.decodeBitmapFromResource(getResources(), R.drawable.main_newfriends, (int) (screenWidth / 2.2), (int) (screenHeight / 2.2));
        newFriendsIV.setImageBitmap(tempBM);
        newFriendsTV.setVisibility(View.VISIBLE);
        newFriendsCount.setVisibility(View.VISIBLE);
        newFriendsFL.setVisibility(View.VISIBLE);

    }

    private void showNewArticlesCircle() {
        tempBM = AppUtils.decodeBitmapFromResource(getResources(), R.drawable.main_newarticles, (int) (screenWidth / 2.1), (int) (screenHeight / 2.1));
        newArticlesIV.setImageBitmap(tempBM);
        newArticlesTV.setVisibility(View.VISIBLE);
        newArticlesCount.setVisibility(View.VISIBLE);
        newArticlesFL.setVisibility(View.VISIBLE);

    }

    private void showMessageCircle() {
        tempBM = AppUtils.decodeBitmapFromResource(getResources(), R.drawable.main_newmessage, (int) (screenWidth / 2.32), (int) (screenHeight / 2.32));
        newMessageIV.setImageBitmap(tempBM);
        newMsgTV.setVisibility(View.VISIBLE);
        newMsgCount.setVisibility(View.VISIBLE);
        newMessageFL.setVisibility(View.VISIBLE);
    }

    private void hideNewFriendsCircle() {
        tempBM = AppUtils.decodeBitmapFromResource(getResources(), R.drawable.main_newfriends_bubble, (int) (screenWidth / 2.4), (int) (screenHeight / 2.4));
        newFriendsIV.setImageBitmap(tempBM);
        newFriendsTV.setVisibility(View.INVISIBLE);
        newFriendsCount.setVisibility(View.INVISIBLE);
//        newFriendsFL.setVisibility(View.INVISIBLE);
    }

    private void hideNewArticlesCircle() {
        tempBM = AppUtils.decodeBitmapFromResource(getResources(), R.drawable.main_newarticles_bubble, (int) (screenWidth / 2.3), (int) (screenHeight / 2.3));
        newArticlesIV.setImageBitmap(tempBM);
        newArticlesTV.setVisibility(View.INVISIBLE);
        newArticlesCount.setVisibility(View.INVISIBLE);
//        newArticlesFL.setVisibility(View.INVISIBLE);
    }

    private void hideMessageCircle() {
        tempBM = AppUtils.decodeBitmapFromResource(getResources(), R.drawable.main_newmessage_bubble, (int) (screenWidth / 2.5), (int) (screenHeight / 2.5));
        newMessageIV.setImageBitmap(tempBM);
        newMsgTV.setVisibility(View.INVISIBLE);
        newMsgCount.setVisibility(View.INVISIBLE);
//        newMessageFL.setVisibility(View.INVISIBLE);
    }

    private JsonHttpResponseHandler autoInviteHttpResponseHandler = new JsonHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            super.onSuccess(statusCode, headers, response);

            Log.i(TAG, "JsonHttpResponseHandler onSuccess, response.");

            try {
                if (response.get("msgCode").equals("A01")) {
                    int invitedCount = response.getJSONObject("result").getInt("count");
                    String invitedMessage = getString(R.string.friend_auto_invited_message);
                    invitedMessage = invitedMessage.replace(":count:", String.valueOf(invitedCount));
                    Toast.makeText(mContext, invitedMessage, Toast.LENGTH_LONG).show();
                } else {
                    if (response.get("result") != null) {
                        oneBtnAlertDialog = AppUtils.getAlertDialog(mContext, response.get("result").toString());
                        oneBtnAlertDialog.show();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (JsonSyntaxException e) {
                oneBtnAlertDialog = AppUtils.getAlertDialog(mContext, getString(R.string.data_result_error));
                oneBtnAlertDialog.show();
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            super.onFailure(statusCode, headers, throwable, errorResponse);

            Log.i(TAG, "JsonHttpResponseHandler onFailure, errorResponse.");
            oneBtnAlertDialog = AppUtils.getAlertDialog(mContext, getString(R.string.data_load_error));
            oneBtnAlertDialog.show();
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            super.onFailure(statusCode, headers, responseString, throwable);

            Log.i(TAG, "JsonHttpResponseHandler onFailure, responseString: " + responseString);
        }

    };


    // get current location
    private void requestAddressfromGoogle(double lat, double lng) {
        Log.i(TAG, "lat:" + lat + "/lng:" + lng);
        final String url = "http://maps.googleapis.com/maps/api/geocode/json?latlng=" + lat + "," + lng + "&sensor=true";
        params = new RequestParams();
//        params.add("uid", UUIDChose);
        client = new AsyncHttpClient();
        client.setTimeout(AppConfig.TIMEOUT);
        client.post(url, params, addressJsonHandler);
    }

    public JsonHttpResponseHandler addressJsonHandler = new JsonHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            Log.d(TAG, "address onSuccess = " + response);
            try {

                JSONArray resultsAry = null;
                resultsAry = response.getJSONArray("results");

                JSONObject addresObj = resultsAry.getJSONObject(1);
                JSONArray addCompAry = addresObj.getJSONArray("address_components");
                JSONObject areaObj = (JSONObject) addCompAry.get(1);
                String areaStr = areaObj.getString("long_name");
                JSONObject locateObj = (JSONObject) addCompAry.get(2);
                String locateStr = locateObj.getString("long_name");
                //areaStr : 鎮 locateStr:縣
                Log.i(TAG, "areaStr :" + areaStr + "/locateStr:" + locateStr);
                requestWeatherData(Double.valueOf(sharedPreferences.getString(AppConfig.MAIN_LAT_KEY, "")),
                        Double.valueOf(sharedPreferences.getString(AppConfig.MAIN_LNG_KEY, "")),
                        areaStr, locateStr);
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
}