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
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.coretronic.bdt.AppConfig;
import com.coretronic.bdt.AppUtils;
import com.coretronic.bdt.R;
import com.coretronic.bdt.Utility.CustomerOneBtnAlertDialog;
import com.coretronic.bdt.WalkWay.Module.WalkWayAreaInfo;
import com.coretronic.bdt.WalkWay.Module.WalkWayRecordMapAdapter;
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


public class WalkWayArea extends Fragment {
    private String TAG = WalkWayArea.class.getSimpleName();

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

    private ListView mWwRecordMapListView;
    private List<WalkWayAreaInfo> recordMapInfos;
    private WalkWayRecordMapAdapter recordMapAdapter;
    //transactionLog
    private String currentTime;
    private String append;
    private CustomerOneBtnAlertDialog oneBtnAlertDialog;

    public JsonHttpResponseHandler jsonHandler = new JsonHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
            Log.d(TAG, "onSuccess = " + response);
            try {
                if (response.get("msgCode").equals("A01")) {
                    recordMapInfos.clear();
                    WalkWayAreaInfos walkWayAreaInfos = gson.fromJson(response.toString(), WalkWayAreaInfos.class);
                    List<WalkWayAreaInfo> lists = walkWayAreaInfos.getResult();
                    recordMapInfos.addAll(lists);
                    recordMapAdapter.notifyDataSetChanged();
                } else {
                    oneBtnAlertDialog = AppUtils.getAlertDialog(mContext, getString(R.string.data_load_error));
                    oneBtnAlertDialog.show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (JsonSyntaxException e) {
                oneBtnAlertDialog= AppUtils.getAlertDialog(mContext, getString(R.string.data_result_error));
                oneBtnAlertDialog.show();
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
            oneBtnAlertDialog = AppUtils.getAlertDialog(mContext, getString(R.string.data_load_error));
            oneBtnAlertDialog.show();
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
        View v = inflater.inflate(R.layout.walkway_record_map, container, false);
        mContext = v.getContext();
        progressDialog = new ProgressDialog(mContext);
        menuPopupWindow = new MenuPopupWindow(mContext, null);
        initView(v);
        return v;
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
        mWwRecordMapListView = (ListView) v.findViewById(R.id.ww_record_map_listView);
        recordMapInfos = new ArrayList<WalkWayAreaInfo>();
        recordMapAdapter = new WalkWayRecordMapAdapter(mContext, recordMapInfos);
        mWwRecordMapListView.setAdapter(recordMapAdapter);

        getWalkWayAreaList();
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
                            "WalkWayRecordMap" + "," +
                            "WalkWayMainActivity" + "," +
                            "btnBack" + "\n";
                    transactionLogSave(append);
                    getFragmentManager().popBackStackImmediate();
                    break;
                case R.id.btnRecordPopMenu:
                    callSystemTime();
                    append = currentTime + "," +
                            uuidChose + "," +
                            "WalkWayRecordMap" + "," +
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

            }
        }
    };


    private void getWalkWayAreaList() {
        progressDialog.setMessage(getString(R.string.dialog_download_msg));
        progressDialog.show();

        final String url = AppConfig.DOMAIN_SITE_PATE + AppConfig.REQUEST_WALKWAYS_AREA_LIST;
        Log.i(TAG, "url:  " + url);

        uuidChose = sharedPreferences.getString(AppConfig.PREF_UNIQUE_ID, "");
        Log.i(TAG, "UUID:  " + uuidChose);
        RequestParams params = new RequestParams();
        params.add("uid", uuidChose);
        params.add("time", AppUtils.getSystemTime());

        Log.i(TAG, "params:  " + params);
        asyncHttpClient.post(url, params, jsonHandler);
    }

    public class WalkWayAreaInfos {
        private String msgCode;
        private String status;
        private List<WalkWayAreaInfo> result;

        public String getMsgCode() {
            return msgCode;
        }

        public String getStatus() {
            return status;
        }

        public List<WalkWayAreaInfo> getResult() {
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

    @Override
    public void onPause() {
        super.onPause();
        if (oneBtnAlertDialog != null) {
            oneBtnAlertDialog.dismiss();
        }
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        if (asyncHttpClient != null) {
            asyncHttpClient.cancelAllRequests(true);
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
