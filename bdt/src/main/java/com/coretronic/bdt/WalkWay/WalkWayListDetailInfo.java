package com.coretronic.bdt.WalkWay;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.coretronic.bdt.AppConfig;
import com.coretronic.bdt.AppUtils;
import com.coretronic.bdt.R;
import com.coretronic.bdt.Utility.CustomerOneBtnAlertDialog;
import com.coretronic.bdt.WalkWay.Module.ToolsPopupWindow;
import com.coretronic.bdt.WalkWay.Module.WalkwayInfo;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Morris on 14/10/30.
 */
public class WalkWayListDetailInfo extends Fragment {
    private String TAG = WalkWayListDetailInfo.class.getSimpleName();
    private PopupWindow toolsPopupWindow;
    private Button btnPopMenu;
    private Button btnBack;
    private TextView barTitle;
    private Context mContext;
    private RelativeLayout navigationBar;
    private SharedPreferences sharedPreferences;
    private ProgressDialog progressDialog = null;
    private String uuidChose;
    private AsyncHttpClient asyncHttpClient;
    private SimpleDateFormat formatter;
    private Date curDate;

    // Walk way info
    private Gson gson = new Gson();

    // Text
    private LinearLayout mFriendVisitedLayout;
    private TextView mTxtTitle;
    private TextView mTxtSubTitle;
    private TextView mTxtFeature;
    private TextView mTxtWalkwayDetailDistance;
    private TextView mTxtWalkwayAddress;
    private TextView mTxtParkAvailable;
    private TextView mTxtDistance;
    private TextView mTxtFriendVisited;
    private ImageView mImgVisitedIcon;


    // weather
    private ImageView mWeather_1;
    private ImageView mWeather_2;
    private ImageView mWeather_3;
    private TextView mWeatherDate_1;
    private TextView mWeatherDate_2;
    private TextView mWeatherDate_3;
    private List<TextView> dateTextArr;
    private List<ImageView> weatherImgArr;
    private TextView mVisitedTv;

    private ImageView imageView;
    private BitmapDrawable bitmapDrawable;
    private TableLayout weatherTableLayout;
    private WalkwayInfo walkwayInfo;
    private String walkWayId;
    private String walkWayName;
    private String datePatten = "yyyy-MM-dd HH:mm:ss";

    //check login
    private boolean isLogin = false;
    private boolean isVisited = false;

    //transactionLog
    private String currentTime;
    private String append;
    private CustomerOneBtnAlertDialog customerDialog;

    public JsonHttpResponseHandler jsonHandler = new JsonHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            Log.i(TAG, "onSuccess");
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
            Log.d(TAG, "onSuccess = " + response);
            try {
                if (response.get("msgCode").equals("A01")) {
                    WalkWayDetail walkWayDetail = gson.fromJson(response.toString(), WalkWayDetail.class);
                    walkwayInfo = walkWayDetail.getResult();
                    setDetailInfo(walkwayInfo);
                    saveToSharePreference();
                } else {
                    customerDialog = AppUtils.getAlertDialog(mContext, getString(R.string.data_load_error));
                    customerDialog.show();
                }
            } catch (JSONException e) {
                customerDialog = AppUtils.getAlertDialog(mContext, getString(R.string.data_result_error));
                customerDialog.show();
                e.printStackTrace();
            } catch (JsonSyntaxException e) {
                customerDialog = AppUtils.getAlertDialog(mContext, getString(R.string.data_result_error));
                customerDialog.show();
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            Log.d(TAG, "onFailure");
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
            customerDialog = AppUtils.getAlertDialog(mContext, getString(R.string.data_load_error));
            customerDialog.show();
        }
    };

    public JsonHttpResponseHandler insertHandler = new JsonHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            Log.i(TAG, "onSuccess");
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
            Log.d(TAG, "onSuccess = " + response);
            try {
                if (response.get("msgCode").equals("A01")) {
                    mImgVisitedIcon.setImageResource(R.drawable.ic_ww_visited);
                    isVisited = true;
                    mImgVisitedIcon.setEnabled(false);
                    mVisitedTv.setText(" 我已經來過");
                }else{
                    customerDialog = AppUtils.getAlertDialog(mContext, getString(R.string.data_load_error));
                    customerDialog.show();
                }
            } catch (JSONException e) {
                customerDialog = AppUtils.getAlertDialog(mContext, getString(R.string.data_result_error));
                customerDialog.show();
                e.printStackTrace();
            } catch (JsonSyntaxException e) {
                customerDialog = AppUtils.getAlertDialog(mContext, getString(R.string.data_result_error));
                customerDialog.show();
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            Log.d(TAG, "onFailure");
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
            customerDialog = AppUtils.getAlertDialog(mContext, getString(R.string.data_load_error));
            customerDialog.show();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getActivity().getSharedPreferences(AppConfig.SHAREDPREFERENCES_NAME, 0);
        asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.setTimeout(AppConfig.TIMEOUT);
        uuidChose = sharedPreferences.getString(AppConfig.PREF_UNIQUE_ID, "");
        formatter = new SimpleDateFormat();
        curDate = new Date(System.currentTimeMillis()); // 獲取當前時間
        isLogin = sharedPreferences.getBoolean(AppConfig.PREF_IS_LOGIN,false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.walkway_detail_info, container, false);
        mContext = v.getContext();
        progressDialog = new ProgressDialog(mContext);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        toolsPopupWindow = new ToolsPopupWindow(mContext, null);
        initView(v);
        Bundle bundle = getArguments();
        if (bundle != null) {
            walkWayId = bundle.getString("WalkWayId");
            getWalkWayInfo(walkWayId);
        }
        return v;
    }

    @Override
    public void onPause() {
        super.onPause();
        if(customerDialog != null && customerDialog.isShowing()){
            customerDialog.dismiss();
        }
        if(progressDialog != null && progressDialog.isShowing()){
            progressDialog.dismiss();
        }
        if(asyncHttpClient != null) {
            asyncHttpClient.cancelAllRequests(true);
        }
    }

    private void initView(View v) {
        // navigation bar
        navigationBar = (RelativeLayout) v.findViewById(R.id.navigationBar_tools);
        btnBack = (Button) v.findViewById(R.id.btnBack);
        btnPopMenu = (Button) v.findViewById(R.id.btnToolsPopMenu);
        barTitle = (TextView) v.findViewById(R.id.action_bar_title);

        barTitle.setText(getString(R.string.lb_ww_title));
        btnBack.setOnClickListener(btnListener);
        btnPopMenu.setOnClickListener(btnListener);

        // View item
        mTxtTitle = (TextView) v.findViewById(R.id.txt_title);
        mTxtSubTitle = (TextView) v.findViewById(R.id.txt_subTitle);
        mTxtFeature = (TextView) v.findViewById(R.id.txt_feature);
        mTxtWalkwayDetailDistance = (TextView) v.findViewById(R.id.txt_walkway_detail_distance);
        mTxtWalkwayAddress = (TextView) v.findViewById(R.id.txt_walkway_address);
        mTxtParkAvailable = (TextView) v.findViewById(R.id.txt_park_available);
        mTxtDistance = (TextView) v.findViewById(R.id.txt_distance);
        imageView = (ImageView) v.findViewById(R.id.img_walkway_photo);
        weatherTableLayout = (TableLayout) v.findViewById(R.id.ww_weathier_tablelayout);
        mVisitedTv = (TextView) v.findViewById(R.id.wwVisitedTv);

        // friend visited
        mFriendVisitedLayout = (LinearLayout) v.findViewById(R.id.wwFriendVisitedLayout);
        mTxtFriendVisited = (TextView) v.findViewById(R.id.wwTvFriendVisited);
        mTxtFriendVisited.setOnClickListener(btnListener);
        mImgVisitedIcon = (ImageView) v.findViewById(R.id.wwImgVisitedIcon);
        if(isLogin){
            mFriendVisitedLayout.setVisibility(View.VISIBLE);
            mImgVisitedIcon.setOnClickListener(btnListener);
        }

        // weather
        mWeather_1 = (ImageView) v.findViewById(R.id.ww_img_weather_1);
        mWeather_2 = (ImageView) v.findViewById(R.id.ww_img_weather_2);
        mWeather_3 = (ImageView) v.findViewById(R.id.ww_img_weather_3);
        weatherImgArr = new ArrayList<ImageView>();
        weatherImgArr.add(mWeather_1);
        weatherImgArr.add(mWeather_2);
        weatherImgArr.add(mWeather_3);

        mWeatherDate_1 = (TextView) v.findViewById(R.id.ww_txt_weather_date_1);
        mWeatherDate_2 = (TextView) v.findViewById(R.id.ww_txt_weather_date_2);
        mWeatherDate_3 = (TextView) v.findViewById(R.id.ww_txt_weather_date_3);
        dateTextArr = new ArrayList<TextView>();
        dateTextArr.add(mWeatherDate_1);
        dateTextArr.add(mWeatherDate_2);
        dateTextArr.add(mWeatherDate_3);

    }

    private void getWalkWayInfo(String walkwayId) {
        progressDialog.setMessage(getString(R.string.dialog_download_msg));
        progressDialog.show();

        final String url = AppConfig.DOMAIN_SITE_PATE + AppConfig.REQUEST_WALKWAY_DETAILS;
        Log.i(TAG, "url:  " + url);
        uuidChose = sharedPreferences.getString(AppConfig.PREF_UNIQUE_ID, "");
        RequestParams params = new RequestParams();
        params.add("uid", uuidChose);
        params.add("time", AppUtils.getSystemTime());
        params.add("walkway_id", walkwayId);
        Log.i(TAG, "params:  " + params);
        asyncHttpClient.post(url, params, jsonHandler);
    }

    private void setWalkWayVisited(String walkwayId) {
        progressDialog.setMessage(getString(R.string.dialog_download_msg));
        progressDialog.show();

        final String url = AppConfig.DOMAIN_SITE_PATE + AppConfig.INSERT_WALKWAYS_RECORD;
        Log.i(TAG, "url:  " + url);
        uuidChose = sharedPreferences.getString(AppConfig.PREF_UNIQUE_ID, "");
        RequestParams params = new RequestParams();
        params.add("uid", uuidChose);
        params.add("time", AppUtils.getSystemTime());
        params.add("walkway_id", walkwayId);

        Log.i(TAG, "params:  " + params);
        asyncHttpClient.post(url, params, insertHandler);
    }

    private void setDetailInfo(WalkwayInfo walkwayInfo) {
        if (walkwayInfo != null) {
            try {
                if (walkwayInfo.getImagePath() != null) {
                    AppUtils.loadPhoto(mContext, imageView, walkwayInfo.getImagePath());
                }
            } catch (Exception e) {
                imageView.setVisibility(View.GONE);
                e.printStackTrace();
            }

            // is visited
            if("0".equals(walkwayInfo.getVisited())){
                isVisited = false;
                mImgVisitedIcon.setImageResource(R.drawable.ic_ww_no_visit);
                mVisitedTv.setText("今天我也到此一遊！");
            }else{
                isVisited = true;
                mImgVisitedIcon.setImageResource(R.drawable.ic_ww_visited);
                mImgVisitedIcon.setEnabled(false);
                mVisitedTv.setText(" 我已經來過");
            }
            List<WalkwayInfo.Friends> friendses = walkwayInfo.getFriends();
            String str = null;
            if(friendses.size() == 1){
                str = friendses.get(0).getName() + "來過此步道";
                mTxtFriendVisited.setText(str);
            }else if(friendses.size() > 1){
                str = friendses.get(0).getName() + "、" + friendses.get(1).getName() + "等"+ friendses.size() +"位也來過";
                mTxtFriendVisited.setText(str);
            }else{
                mTxtFriendVisited.setText("還沒有朋友來過");
            }


            // james add
            walkWayName = walkwayInfo.getWalkwayName();

            mTxtTitle.setText(walkwayInfo.getWalkwayName());
            mTxtSubTitle.setText(walkwayInfo.getWalkwayTitle());
            mTxtFeature.setText(walkwayInfo.getWalkwayFeature());
            mTxtWalkwayDetailDistance.setText(walkwayInfo.getDescription());
            mTxtWalkwayAddress.setText(walkwayInfo.getWalkwayAddress());
            mTxtParkAvailable.setText(walkwayInfo.getParking());
            mTxtDistance.setText(walkwayInfo.getKilometers());
            mTxtWalkwayAddress.setOnClickListener(btnListener);

            //weather
            List<WalkwayInfo.Weather> weatherList = walkwayInfo.getWeather();
            Date date;
            int pop = 0;
            try {
                for (int i = 0; i < weatherImgArr.size(); i++) {
                    // date
                    formatter.applyPattern(datePatten);
                    date = formatter.parse(weatherList.get(i).getDate());
                    formatter.applyPattern("MM/dd");
                    dateTextArr.get(i).setText(formatter.format(date));
                    // weather status
                    pop = Integer.valueOf(weatherList.get(i).getPop());
                    Log.d(TAG, "pop:" + pop);
                    if (pop <= 20) {
                        weatherImgArr.get(i).setImageResource(R.drawable.ic_ww_weather_sun);
                    } else if (pop >= 50) {
                        weatherImgArr.get(i).setImageResource(R.drawable.ic_ww_weather_rain);
                    } else {
                        weatherImgArr.get(i).setImageResource(R.drawable.ic_ww_weather_cloud);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /***
     *
     */
    private void saveToSharePreference() {
        sharedPreferences.edit()
                .putString(AppConfig.WALKWAYS_CURRENT_WALKWAYID, walkWayId)
                .putString(AppConfig.WALKWAYS_CURRENT_WALKWAYNAME, walkWayName)
                .apply();
    }

    private View.OnClickListener btnListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btnBack:
                    callSystemTime();
                    append = currentTime + "," +
                            uuidChose + "," +
                            "WalkWayListDetailInfo" + "," +
                            "WalkWayInfoList" + "," +
                            "btnBack" + "\n";
                    transactionLogSave(append);
                    ((FragmentActivity) mContext).getSupportFragmentManager().popBackStackImmediate();
                    break;
                case R.id.btnToolsPopMenu:
                    callSystemTime();
                    append = currentTime + "," +
                            uuidChose + "," +
                            "WalkWayListDetailInfo" + "," +
                            "MenuToolsPopupWindow" + "," +
                            "btnToolsPopMenu" + "\n";
                    transactionLogSave(append);
                    toolsPopupWindow.showAsDropDown(navigationBar, 0, 0);
                    break;
                case R.id.txt_walkway_address:
                    if (walkwayInfo.getLocation() != null) {
                        startGoogleMap(walkwayInfo.getLocation().getLat(), walkwayInfo.getLocation().getLng());
                    }
                    break;
                case R.id.wwImgVisitedIcon:
                    setWalkWayVisited(walkwayInfo.getWalkwayId());
                    break;
                case R.id.wwTvFriendVisited:
                    Bundle bundle = new Bundle();
                    bundle.putString("walkwayid",walkwayInfo.getWalkwayId());
                    Intent intent = new Intent();
                    intent.setClass(getActivity(),WalkWayFriendsListActivity.class);
                    intent.putExtras(bundle);
                    mContext.startActivity(intent);
                    break;
            }
        }
    };

    private void startGoogleMap(String lat, String lng) {
        Uri uri = Uri.parse(String.format("geo:%s,%s?q=%s,%s", lat, lng, lat, lng));
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        if (isIntentAvailable(intent)) {
            startActivity(intent);
        } else {
            Toast.makeText(mContext, "Oop! google 尚未安裝", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isIntentAvailable(Intent intent) {
        List<ResolveInfo> activities = mContext.getPackageManager().queryIntentActivities(intent,
                PackageManager.COMPONENT_ENABLED_STATE_DEFAULT);
        return activities.size() != 0;
    }


    public class WalkWayDetail {
        private String msgCode;
        private String status;
        private WalkwayInfo result;

        public String getMsgCode() {
            return msgCode;
        }

        public String getStatus() {
            return status;
        }

        public WalkwayInfo getResult() {
            return result;
        }

    }

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
}