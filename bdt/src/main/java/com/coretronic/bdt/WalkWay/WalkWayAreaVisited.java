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
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.coretronic.bdt.AppConfig;
import com.coretronic.bdt.AppUtils;
import com.coretronic.bdt.R;
import com.coretronic.bdt.Utility.CustomerOneBtnAlertDialog;
import com.coretronic.bdt.WalkWay.Module.WalkWayAreaAdapter;
import com.coretronic.bdt.WalkWay.Module.WalkWayAreaInfo;
import com.coretronic.bdt.WalkWay.Module.WalkWayAreaPartInfo;
import com.coretronic.bdt.WalkWay.Module.WalkWayExListAdapter;
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


public class WalkWayAreaVisited extends Fragment {
    private String TAG = WalkWayAreaVisited.class.getSimpleName();

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

    private ImageView mWwImgTwMap;
    private ImageView mWwPercent;
    private TextView mWwLbTwArea;
    private TextView mWwLbWalkwayNum;
    private ListView mWwMeVisitedListView;
    private ListView mWwMeGoListView;
    private ImageView mWwImgGood;

    private String walkWayArea;
    private List<WalkWayAreaPartInfo> wwUserVisited;
    private List<WalkWayAreaPartInfo> wwUserWillGo;
    private WalkWayAreaAdapter userVisitedAdapter;
    private WalkWayAreaAdapter userWillGoAdapter;

    private ExpandableListView expandableListView;
    private WalkWayExListAdapter walkWayExListAdapter;
    private List<String> groups;
    private List<List<WalkWayAreaPartInfo>> childs;
    private WalkWayAreaInfo walkWayAreaInfo;
    private int mWalkWayTotal = 0;
    private int mWalkWayVisited = 0;
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

                    WalkWayAreaVisit walkWayAreaVisit = gson.fromJson(response.toString(), WalkWayAreaVisit.class);
                    groups.clear();
                    childs.clear();
                    groups.add("我到訪過的步道");
                    childs.add(walkWayAreaVisit.getResult().getVisited());
//                    expandableListView.expandGroup(0);
//                    expandableListView.collapseGroup(0);

                    groups.add("等你到此一遊");
                    childs.add(walkWayAreaVisit.getResult().getWillGo());
//                    expandableListView.expandGroup(1);
//                    expandableListView.collapseGroup(1);
                    walkWayExListAdapter.notifyDataSetChanged();

                    mWalkWayVisited = walkWayAreaVisit.getResult().getVisited().size();
                    mWwLbWalkwayNum.setText(mWalkWayVisited + "/" + mWalkWayTotal);
                    float percent = (float) mWalkWayVisited / (float) mWalkWayTotal * 100;
                    setMedalIcon(percent);
                    setPercentIcon(percent);

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
        groups = new ArrayList<String>();
        childs = new ArrayList<List<WalkWayAreaPartInfo>>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.walkway_visited, container, false);
        mContext = v.getContext();
        progressDialog = new ProgressDialog(mContext);
        menuPopupWindow = new MenuPopupWindow(mContext, null);
        initView(v);
        Bundle bundle = getArguments();
        if (bundle != null) {
            String json = bundle.getString("WalkWayMapArea");
            walkWayAreaInfo = gson.fromJson(json, WalkWayAreaInfo.class);
            mWwLbTwArea.setText(walkWayAreaInfo.getPartArea());
            mWwImgTwMap.setImageResource(walkWayAreaInfo.getMapIconRid());
            mWwPercent.setImageResource(walkWayAreaInfo.getPercentRid());
            mWalkWayTotal = walkWayAreaInfo.getWalkways();
            mWalkWayVisited = walkWayAreaInfo.getVisiteds();
            mWwLbWalkwayNum.setText(mWalkWayVisited + "/" + mWalkWayTotal);
            float percent = (float) mWalkWayVisited / (float) mWalkWayTotal * 100;
            setMedalIcon(percent);
            getWalkWayAreaVisited(walkWayAreaInfo.getPartArea());
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

        barTitle.setText(getString(R.string.lb_ww_title));
        btnBack.setOnClickListener(btnListener);
        btnCharts.setOnClickListener(btnListener);

        // button
        mWwImgTwMap = (ImageView) v.findViewById(R.id.ww_img_tw_map);
        mWwPercent = (ImageView) v.findViewById(R.id.ww_img_percent);
        mWwLbTwArea = (TextView) v.findViewById(R.id.ww_lb_tw_area);
        mWwLbWalkwayNum = (TextView) v.findViewById(R.id.ww_lb_walkway_num);
        mWwImgGood = (ImageView) v.findViewById(R.id.ww_img_good);


        expandableListView = (ExpandableListView) v.findViewById(R.id.expandableListView);
        walkWayExListAdapter = new WalkWayExListAdapter(mContext, groups, childs);
        expandableListView.setAdapter(walkWayExListAdapter);
        expandableListView.setOnChildClickListener(itemClickListener);

    }

    private void setMedalIcon(float percent) {
        try {
            if (percent < 10) {
                mWwImgGood.setImageBitmap(null);
                return;
            }
            int res = 0;
            if (percent < 30) {
                res = R.drawable.ic_ww_medal_20;
            } else if (percent < 50) {
                res = R.drawable.ic_ww_medal_40;
            } else if (percent < 70) {
                res = R.drawable.ic_ww_medal_60;
            } else if (percent < 90) {
                res = R.drawable.ic_ww_medal_80;
            } else if (percent >= 90) {
                res = R.drawable.ic_ww_medal_100;
            }
            mWwImgGood.setImageResource(res);
        } catch (Exception e) {
            e.printStackTrace();
        }

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
                            "WalkWayVisitedList" + "," +
                            "WalkWayRecordMapDetial" + "," +
                            "btnBack" + "\n";
                    transactionLogSave(append);
                    getFragmentManager().popBackStackImmediate();
                    break;
                case R.id.btnRecordPopMenu:
                    callSystemTime();
                    append = currentTime + "," +
                            uuidChose + "," +
                            "WalkWayVisitedList" + "," +
                            "WalkWayChartsList" + "," +
                            "btnWalkWayChartsList" + "\n";
                    transactionLogSave(append);
                    fragment = new WalkWayCharts();
                    if (fragment != null) {
                        getFragmentManager().beginTransaction()
                                .replace(R.id.frame_container, fragment, "WalkWayChartsList")
                                .addToBackStack("WalkWayChartsList")
                                .commit();
                    } else {
                        Log.e(TAG, "Error in creating fragment");
                    }
                    break;

                case R.id.ww_btn_record_next:
                    callSystemTime();
                    append = currentTime + "," +
                            uuidChose + "," +
                            "WalkWayVisitedList" + "," +
                            "WalkWayVisitedList" + "," +
                            "btnWalkWayVisitedList" + "\n";
                    transactionLogSave(append);
                    fragment = new WalkWayAreaVisited();
                    if (fragment != null) {
                        getFragmentManager().beginTransaction()
                                .replace(R.id.frame_container, fragment, "WalkWayVisitedList")
                                .addToBackStack("WalkWayVisitedList")
                                .commit();
                    } else {
                        Log.e(TAG, "Error in creating fragment");
                    }
                    break;
            }
        }
    };

    private void getWalkWayAreaVisited(String area) {
        progressDialog.setMessage(getString(R.string.dialog_download_msg));
        progressDialog.show();

        final String url = AppConfig.DOMAIN_SITE_PATE + AppConfig.REQUEST_WALKWAYS_AREA_USER_VISITED;
        Log.i(TAG, "url:  " + url);

        uuidChose = sharedPreferences.getString(AppConfig.PREF_UNIQUE_ID, "");
        Log.i(TAG, "UUID:  " + uuidChose);
        RequestParams params = new RequestParams();
        params.add("uid", uuidChose);
        params.add("time", AppUtils.getSystemTime());
        params.add("walkwayPartArea", area);
        Log.i(TAG, "params:  " + params);
        asyncHttpClient.post(url, params, jsonHandler);
    }


    private ExpandableListView.OnChildClickListener itemClickListener = new ExpandableListView.OnChildClickListener() {
        @Override
        public boolean onChildClick(ExpandableListView parent, View view, int groupPosition, int childPosition, long id) {
            Fragment fragment = new WalkWayListDetailInfo();
            if (fragment != null) {
                Bundle bundle = new Bundle();
                bundle.putString("WalkWayId", childs.get(groupPosition).get(childPosition).getWalkwayId());
                fragment.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frame_container, fragment, "WalkWayDetailInfo")
                        .addToBackStack("WalkWayDetailInfo")
                        .commit();
            } else {
                Log.e(TAG, "Error in creating fragment");
            }
            return false;
        }
    };

    public class WalkWayAreaVisit {
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

    }

    private void setPercentIcon(float percent) {
        int p = (int)(percent / 10);
        try {
            int res = 0;
            switch (p) {
                case 0:
                    res = R.drawable.ic_percent_light_0;
                    break;
                case 1:
                    res = R.drawable.ic_percent_light_10;
                    break;
                case 2:
                    res = R.drawable.ic_percent_light_20;
                    break;
                case 3:
                    res = R.drawable.ic_percent_light_30;
                    break;
                case 4:
                    res = R.drawable.ic_percent_light_40;
                    break;
                case 5:
                    res = R.drawable.ic_percent_light_50;
                    break;
                case 6:
                    res = R.drawable.ic_percent_light_60;
                    break;
                case 7:
                    res = R.drawable.ic_percent_light_70;
                    break;
                case 8:
                    res = R.drawable.ic_percent_light_80;
                    break;
                case 9:
                    res = R.drawable.ic_percent_light_90;
                    break;

            }
            mWwPercent.setImageResource(res);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class ResultObj {
        private String walkways;
        private String visitedWalkways;
        private List<WalkWayAreaPartInfo> visited;
        private List<WalkWayAreaPartInfo> willGo;

        public String getWalkways() {
            return walkways;
        }

        public String getVisitedWalkways() {
            return visitedWalkways;
        }

        public List<WalkWayAreaPartInfo> getVisited() {
            return visited;
        }

        public List<WalkWayAreaPartInfo> getWillGo() {
            return willGo;
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
