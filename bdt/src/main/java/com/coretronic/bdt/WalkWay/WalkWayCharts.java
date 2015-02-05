package com.coretronic.bdt.WalkWay;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.coretronic.bdt.AppConfig;
import com.coretronic.bdt.AppUtils;
import com.coretronic.bdt.R;
import com.coretronic.bdt.Utility.CustomerOneBtnAlertDialog;
import com.coretronic.bdt.WalkWay.Module.ChartsRankInfo;
import com.coretronic.bdt.WalkWay.Module.WalkWayChartsAdapter;
import com.coretronic.bdt.module.MenuPopupWindow;
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


public class WalkWayCharts extends Fragment {
    private String TAG = WalkWayCharts.class.getSimpleName();

    private PopupWindow menuPopupWindow;
    private TextView barTitle;
    private Button btnCharts;
    private Button btnBack;
    private Context mContext;
    private RelativeLayout navigationBar;
    private SharedPreferences sharedPreferences;
    private ProgressDialog progressDialog = null;
    private String uuidChose;
    private AsyncHttpClient asyncHttpClient;
    private Gson gson = new Gson();

    private TextView mWwTxtFriendNum;
    private TextView mWwTxtAllNum;
    private TextView mWwBtnFriend;
    private TextView mWwBtnAll;
    private LinearLayout mWwLayoutCharts;
    private TextView mWwTxtUserCount;
    private TextView mWwTxtRecordAll;
    private ListView mWwFrientChartsListView;
    private LinearLayout mWwLayoutAllcharts;
    private TextView mWwTxtUserCountAll;
    private TextView mWwTxtRecordFriend;
    private TextView mWwTxtAllUsers;
    private ListView mWwAllchartsListView;
    private ImageView myLogo;
    private ImageView myLogo2;
    private List<ChartsRankInfo> friendChartsList;
    private List<ChartsRankInfo> allUserChartsList;
    private WalkWayChartsAdapter friendChartsAdapter;
    private WalkWayChartsAdapter allUserChartsAdapter;
    private int tagFlag = 0;
    private CustomerOneBtnAlertDialog customerDialog;
    //transactionLog
    private String currentTime;
    private String append;

    public JsonHttpResponseHandler jsonHandler = new JsonHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
            Log.d(TAG, "onSuccess = " + response);
            try {
                if (response.get("msgCode").equals("A01")) {
                    friendChartsList.clear();
                    allUserChartsList.clear();
                    WalkWayFriendsRank walkWayFriendsRank = gson.fromJson(response.toString(), WalkWayFriendsRank.class);


                    if (tagFlag == 0) {
                        mWwTxtUserCount.setText("本人：" + walkWayFriendsRank.getResult().getCount() + " 個");
                        mWwTxtRecordFriend.setText("第 " + walkWayFriendsRank.getResult().getRank() + " 名");
                        mWwTxtFriendNum.setText(walkWayFriendsRank.getResult().getTotalUser() + " 位朋友");
                        friendChartsList.addAll(walkWayFriendsRank.getResult().getFriends());
                        friendChartsAdapter.notifyDataSetChanged();
                    } else {
                        mWwTxtUserCountAll.setText("本人：" + walkWayFriendsRank.getResult().getCount() + " 個");
                        mWwTxtRecordAll.setText("第 " + walkWayFriendsRank.getResult().getRank() + " 名");
                        mWwTxtAllUsers.setText(walkWayFriendsRank.getResult().getTotalUser() + " 位朋友");
                        allUserChartsList.addAll(walkWayFriendsRank.getResult().getUsers());
                        allUserChartsAdapter.notifyDataSetChanged();
                    }
                } else {
                    customerDialog = AppUtils.getAlertDialog(mContext, getString(R.string.data_load_error));
                    customerDialog.show();
                }
            } catch (JSONException e) {
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


    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getActivity().getSharedPreferences(AppConfig.SHAREDPREFERENCES_NAME, 0);
        asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.setTimeout(AppConfig.TIMEOUT);
        uuidChose = sharedPreferences.getString(AppConfig.PREF_UNIQUE_ID, "");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis()); // 獲取當前時間

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.walkway_charts, container, false);
        mContext = v.getContext();
        progressDialog = new ProgressDialog(mContext);
        menuPopupWindow = new MenuPopupWindow(mContext, null);
        initView(v);
        if (tagFlag == 0) {
            getFriendRanks();
        } else {
            getAllUsersRanks();
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
        navigationBar = (RelativeLayout) v.findViewById(R.id.navigationBar_record);
        btnBack = (Button) v.findViewById(R.id.btnBack);
        btnCharts = (Button) v.findViewById(R.id.btnRecordPopMenu);
        barTitle = (TextView) v.findViewById(R.id.action_bar_title);

        barTitle.setText(getString(R.string.lb_ww_charts_title));
        btnBack.setOnClickListener(btnListener);
        btnCharts.setVisibility(View.INVISIBLE);

        //mylogo
        myLogo = (ImageView) v.findViewById(R.id.ww_user_logo);
        myLogo2 = (ImageView) v.findViewById(R.id.ww_user_logo_2);

        String str = sharedPreferences.getString(AppConfig.PREF_USER_THUMB,null);
        if(str != null && str.trim().length() > 0) {
            myLogo.setImageBitmap(AppUtils.base64ToBitmap(str));
            myLogo2.setImageBitmap(AppUtils.base64ToBitmap(str));
        }
        // button
        mWwBtnFriend = (TextView) v.findViewById(R.id.ww_btn_friend);
        mWwBtnAll = (TextView) v.findViewById(R.id.ww_btn_all);
        mWwBtnFriend.setOnClickListener(btnListener);
        mWwBtnAll.setOnClickListener(btnListener);

        // change button
        mWwBtnFriend = (TextView) v.findViewById(R.id.ww_btn_friend);
        mWwBtnAll = (TextView) v.findViewById(R.id.ww_btn_all);

        // friend record
        mWwLayoutCharts = (LinearLayout) v.findViewById(R.id.ww_layout_charts);
        mWwTxtUserCount = (TextView) v.findViewById(R.id.ww_txt_user_count);
        mWwTxtRecordFriend = (TextView) v.findViewById(R.id.ww_txt_record_friend);
        mWwTxtFriendNum =(TextView) v.findViewById(R.id.ww_txt_friend_num);
        mWwFrientChartsListView = (ListView) v.findViewById(R.id.ww_frient_charts_listView);

        // all record
        mWwLayoutAllcharts = (LinearLayout) v.findViewById(R.id.ww_layout_allcharts);
        mWwTxtUserCountAll = (TextView) v.findViewById(R.id.ww_txt_user_count_all);
        mWwTxtRecordAll = (TextView) v.findViewById(R.id.ww_txt_record_all);
        mWwTxtAllUsers = (TextView) v.findViewById(R.id.ww_txt_all_users);
        mWwAllchartsListView = (ListView) v.findViewById(R.id.ww_allcharts_listView);

        friendChartsList = new ArrayList<ChartsRankInfo>();
        allUserChartsList = new ArrayList<ChartsRankInfo>();
        friendChartsAdapter = new WalkWayChartsAdapter(mContext, friendChartsList);
        allUserChartsAdapter = new WalkWayChartsAdapter(mContext, allUserChartsList);
        mWwFrientChartsListView.setAdapter(friendChartsAdapter);
        mWwAllchartsListView.setAdapter(allUserChartsAdapter);
    }


    private View.OnClickListener btnListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Fragment fragment;
            switch (view.getId()) {
                case R.id.btnBack:
                    callSystemTime();
                    append = currentTime + "," +
                            uuidChose + "," +
                            "WalkWayChartsList" + "," +
                            "WalkWayRecordMap" + "," +
                            "btnBack" + "\n";
                    transactionLogSave(append);
                    getFragmentManager().popBackStackImmediate();
                    break;
                case R.id.ww_btn_friend:
                    callSystemTime();
                    append = currentTime + "," +
                            uuidChose + "," +
                            "WalkWayChartsList" + "," +
                            "WalkWayChartsList" + "," +
                            "btnWalkWayChartsListFriend" + "\n";
                    transactionLogSave(append);
                    tagFlag = 0;
                    mWwBtnFriend.setTextColor(getResources().getColor(R.color.white));
                    mWwBtnFriend.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_style_solid_radio));
                    mWwBtnAll.setTextColor(getResources().getColor(R.color.txt_origin));
                    mWwBtnAll.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_style_solid_radio_bg_while));
                    mWwLayoutAllcharts.setVisibility(View.GONE);
                    mWwLayoutCharts.setVisibility(View.VISIBLE);
                    getFriendRanks();

                    break;

                case R.id.ww_btn_all:
                    callSystemTime();
                    append = currentTime + "," +
                            uuidChose + "," +
                            "WalkWayChartsList" + "," +
                            "WalkWayChartsList" + "," +
                            "btnWalkWayChartsListAll" + "\n";
                    transactionLogSave(append);
                    tagFlag = 1;
                    mWwBtnAll.setTextColor(getResources().getColor(R.color.white));
                    mWwBtnAll.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_style_solid_radio));
                    mWwBtnFriend.setTextColor(getResources().getColor(R.color.txt_origin));
                    mWwBtnFriend.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_style_solid_radio_bg_while));
                    mWwLayoutCharts.setVisibility(View.GONE);
                    mWwLayoutAllcharts.setVisibility(View.VISIBLE);
                    getAllUsersRanks();
                    break;
            }
        }
    };


    private void getFriendRanks() {
        progressDialog.setMessage(getString(R.string.dialog_download_msg));
        progressDialog.show();
        asyncHttpClient.cancelRequests(mContext, true);
        final String url = AppConfig.DOMAIN_SITE_PATE + AppConfig.REQUEST_WALKWAYS_FRIEND_RANKS;
        Log.i(TAG, "url:  " + url);

        uuidChose = sharedPreferences.getString(AppConfig.PREF_UNIQUE_ID, "");
        Log.i(TAG, "UUID:  " + uuidChose);
        RequestParams params = new RequestParams();
        params.add("uid", uuidChose);
        params.add("time", AppUtils.getSystemTime());
        Log.i(TAG, "params:  " + params);
        asyncHttpClient.post(url, params, jsonHandler);
    }

    private void getAllUsersRanks() {
        progressDialog.setMessage(getString(R.string.dialog_download_msg));
        progressDialog.show();
        asyncHttpClient.cancelRequests(mContext, true);
        final String url = AppConfig.DOMAIN_SITE_PATE + AppConfig.REQUEST_WALKWAYS_ALL_USERS_RANKS;
        Log.i(TAG, "url:  " + url);

        uuidChose = sharedPreferences.getString(AppConfig.PREF_UNIQUE_ID, "");
        Log.i(TAG, "UUID:  " + uuidChose);
        RequestParams params = new RequestParams();
        params.add("uid", uuidChose);
        params.add("time", AppUtils.getSystemTime());
        Log.i(TAG, "params:  " + params);
        asyncHttpClient.post(url, params, jsonHandler);
    }

    public class WalkWayFriendsRank {
        private String msgCode;
        private String status;

        private ResultObj result;

        public String getMsgCode() {
            return msgCode;
        }

        public String getStatus() {
            return status;
        }

        public ResultObj getResult() {
            return result;
        }

        public class ResultObj {
            private String rank;
            private String count;
            private String userId;
            private String userName;
            private String totalUser;
            private List<ChartsRankInfo> friends;
            private List<ChartsRankInfo> users;

            public String getRank() {
                return rank;
            }

            public String getCount() {
                return count;
            }

            public String getUserId() {
                return userId;
            }

            public String getUserName() {
                return userName;
            }

            public String getTotalUser() {
                return totalUser;
            }

            public List<ChartsRankInfo> getFriends() {
                return friends;
            }

            public List<ChartsRankInfo> getUsers() {
                return users;
            }
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
