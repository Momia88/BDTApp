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
import android.widget.*;
import com.coretronic.bdt.AppConfig;
import com.coretronic.bdt.AppUtils;
import com.coretronic.bdt.R;
import com.coretronic.bdt.Utility.CustomerOneBtnAlertDialog;
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
import java.util.Arrays;
import java.util.Date;
import java.util.List;


public class WalkWaySearch extends Fragment {
    private String TAG = WalkWaySearch.class.getSimpleName();

    public Button btnNext;

    // spinner
    private Spinner spinner_area;
    private Spinner spinner_distance;
    private Spinner spinner_feature;
    private ArrayAdapter<String> areaAdapter;
    private ArrayAdapter<String> distanceAdapter;
    private ArrayAdapter<String> featureAdapter;
    private List<String> areaList;
    private List<String> distanceList;
    private List<String> featureList;
    private String areaSelected;
    private String distanceSelected;
    private String featureSelected;

    private PopupWindow menuPopupWindow;
    private TextView barTitle;
    //    private Button btnPopMenu;
    private Button btnBack;
    private Context mContext;
    private RelativeLayout navigationBar;
    private SharedPreferences sharedPreferences;
    private ProgressDialog progressDialog = null;
    private String uuidChose;
    private AsyncHttpClient asyncHttpClient;
    private Gson gson = new Gson();
    private WalkWayFeature walkWayFeature;

    //transactionLog
    private String currentTime;
    private String append;
    private CustomerOneBtnAlertDialog customerDialog;

    private String[] distanceArr = {"全部", "1-3公里", "3-5公里", "5公里以上"};
    //    private String [] featureArr = {"全部","海景","季節花景","湖景","綠景","山林"};
    public JsonHttpResponseHandler jsonHandler = new JsonHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
            Log.d(TAG, "onSuccess = " + response);
            try {
                if (response.get("msgCode").equals("A01")) {
                    featureList.clear();
                    featureList.add("全部");
                    walkWayFeature = gson.fromJson(response.toString(), WalkWayFeature.class);
                    featureList.addAll(walkWayFeature.getResult().getWalkwayFeature());
                    featureAdapter.notifyDataSetChanged();
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
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
            Log.d(TAG, "onFailure");
            customerDialog = AppUtils.getAlertDialog(getActivity(), getString(R.string.data_load_error));
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.walkway_search, container, false);
        mContext = v.getContext();
        progressDialog = new ProgressDialog(mContext);
        menuPopupWindow = new MenuPopupWindow(mContext, null);
        initView(v);
        getWalkWayFeature();
        return v;
    }


    @Override
    public void onPause() {
        super.onPause();
        if (menuPopupWindow != null) {
            menuPopupWindow.dismiss();
        }
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        if (customerDialog != null && customerDialog.isShowing()) {
            customerDialog.dismiss();
        }
        if (asyncHttpClient != null) {
            asyncHttpClient.cancelAllRequests(true);
        }
    }

    private void initView(View v) {
        // navigation bar
        navigationBar = (RelativeLayout) v.findViewById(R.id.navigationBar_option);
        btnBack = (Button) v.findViewById(R.id.btnBack);
//        btnPopMenu = (Button) v.findViewById(R.id.btnPopMenu);
        barTitle = (TextView) v.findViewById(R.id.action_bar_title);

        barTitle.setText(getString(R.string.lb_ww_title));
        btnBack.setOnClickListener(btnListener);
//        btnPopMenu.setOnClickListener(btnListener);

        // button
        btnNext = (Button) v.findViewById(R.id.btn_walk_search_next);
        btnNext.setOnClickListener(btnListener);

        // Spinner
        areaList = new ArrayList<String>();
        distanceList = new ArrayList<String>();
        featureList = new ArrayList<String>();
        areaList = Arrays.asList(AppConfig.CITY);
        distanceList = Arrays.asList(distanceArr);
//        featureList = Arrays.asList(featureArr);

        areaAdapter = new ArrayAdapter<String>(mContext, R.layout.spinner_layout_division, areaList);
        spinner_area = (Spinner) v.findViewById(R.id.spinner_walkway_area);
        spinner_area.setAdapter(areaAdapter);

        distanceAdapter = new ArrayAdapter<String>(mContext, R.layout.spinner_layout_division, distanceList);
        spinner_distance = (Spinner) v.findViewById(R.id.spinner_walkway_distance);
        spinner_distance.setAdapter(distanceAdapter);

        featureAdapter = new ArrayAdapter<String>(mContext, R.layout.spinner_layout_division, featureList);
        spinner_feature = (Spinner) v.findViewById(R.id.spinner_walkway_feature);
        spinner_feature.setAdapter(featureAdapter);


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
                            "WalkWaySearch" + "," +
                            "WalkWayMainActivity" + "," +
                            "btnBack" + "\n";
                    transactionLogSave(append);
                    getFragmentManager().popBackStackImmediate();
                    break;
                case R.id.btnPopMenu:
                    Log.d(TAG, "btnPopMenu");
                    menuPopupWindow.showAsDropDown(navigationBar, 0, 0);
                    break;

                case R.id.btn_walk_search_next:
                    callSystemTime();
                    append = currentTime + "," +
                            uuidChose + "," +
                            "WalkWaySearch" + "," +
                            "WalkWayInfoList" + "," +
                            "btnWalkWaySearchNext" + "\n";
                    transactionLogSave(append);
                    areaSelected = spinner_area.getSelectedItem().toString();
                    distanceSelected = spinner_distance.getSelectedItem().toString();
                    featureSelected = spinner_feature.getSelectedItem().toString();

                    fragment = new WalkWayInfoList();
                    Bundle bundle = new Bundle();
                    bundle.putString("walkway_area", areaSelected);
                    bundle.putString("walkway_distance", distanceSelected);
                    bundle.putString("walkway_feature", featureSelected);
                    fragment.setArguments(bundle);

                    if (fragment != null) {
                        getFragmentManager().beginTransaction()
                                .replace(R.id.frame_container, fragment, "WalkWayInfoList")
                                .addToBackStack("WalkWayInfoList")
                                .commit();
                    } else {
                        Log.e(TAG, "Error in creating fragment");
                    }
                    break;
            }
        }
    };


    private void getWalkWayFeature() {
        progressDialog.setMessage(getString(R.string.dialog_download_msg));
        progressDialog.show();
        final String url = AppConfig.DOMAIN_SITE_PATE + AppConfig.REQUEST_WALKWAYS_FEATURE;
        Log.i(TAG, "url:  " + url);
        uuidChose = sharedPreferences.getString(AppConfig.PREF_UNIQUE_ID, "");
        Log.i(TAG, "UUID:  " + uuidChose);
        RequestParams params = new RequestParams();
        params.add("uid", uuidChose);
        params.add("time", AppUtils.getChineseSystemTime());
        Log.i(TAG, "params:  " + params);
        asyncHttpClient.post(url, params, jsonHandler);
    }

    public class WalkWayFeature {
        private String msgCode;
        private String status;
        private ResultObj result;

        public class ResultObj {
            private List<String> walkwayFeature;

            public List<String> getWalkwayFeature() {
                return walkwayFeature;
            }
        }

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

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (progressDialog != null) {
            progressDialog.dismiss();
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
