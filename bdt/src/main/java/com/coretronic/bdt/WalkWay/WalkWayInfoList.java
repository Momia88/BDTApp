package com.coretronic.bdt.WalkWay;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.coretronic.bdt.AppConfig;
import com.coretronic.bdt.AppUtils;
import com.coretronic.bdt.R;
import com.coretronic.bdt.Utility.CustomerOneBtnAlertDialog;
import com.coretronic.bdt.WalkWay.Module.WalkWayListAdapter;
import com.coretronic.bdt.WalkWay.Module.WalkwayInfo;
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

public class WalkWayInfoList extends Fragment {
    private static String TAG = WalkWayInfoList.class.getSimpleName();
    private PopupWindow menuPopupWindow;
    //    private Button btnPopMenu;
    private Button btnBack;
    private TextView barTitle;
    private Context mContext;
    private RelativeLayout navigationBar;
    private SharedPreferences sharedPreferences;
    private ProgressDialog progressDialog = null;
    private String uuidChose;
    private AsyncHttpClient asyncHttpClient;
    private Gson gson = new Gson();

    private View loadMoreView;
    private TextView loadMoreText;
    private ListView wwListView;
    private List<WalkwayInfo> wwInfos;
    private WalkWayListAdapter wwListAdapter;
    private CustomerOneBtnAlertDialog customerDialog;
    //transactionLog
    private String currentTime;
    private String append;

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
                    WalkWayLists walkWayLists = gson.fromJson(response.toString(), WalkWayLists.class);
                    List<WalkwayInfo> lists = walkWayLists.getResult();
                    wwInfos.addAll(lists);
                    wwListAdapter.notifyDataSetChanged();
                } else {
                    if (response.get("status").equals("result_null") && wwInfos.size() == 0) {
                        customerDialog = AppUtils.getAlertDialog(mContext, "目前尚無資料");
                        customerDialog.show();
                    } else {
                        Toast.makeText(mContext, "最後一筆嘍！", 1).show();
                    }
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
    private String walkway_area;
    private String walkway_distance;
    private String walkway_feature;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        sharedPreferences = getActivity().getSharedPreferences(AppConfig.SHAREDPREFERENCES_NAME, 0);
        asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.setTimeout(AppConfig.TIMEOUT);
        uuidChose = sharedPreferences.getString(AppConfig.PREF_UNIQUE_ID, "");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis()); // 獲取當前時間
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");

        View v = inflater.inflate(R.layout.walkway_list, container, false);
        mContext = v.getContext();
        progressDialog = new ProgressDialog(mContext);
        menuPopupWindow = new MenuPopupWindow(mContext, null);
        initView(v);
        try {
            Bundle bundle = getArguments();
            if (bundle != null) {
                walkway_area = bundle.getString("walkway_area", null);
                walkway_distance = bundle.getString("walkway_distance", null);
                walkway_feature = bundle.getString("walkway_feature", null);
                getWalkWayList(0);
            } else {
                customerDialog.setMsg(getString(R.string.get_data_error))
                        .setPositiveListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                customerDialog.dismiss();
                                getFragmentManager().popBackStackImmediate();
                            }
                        }).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");

        if (customerDialog != null && customerDialog.isShowing()) {
            customerDialog.dismiss();
        }
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        if (asyncHttpClient != null) {
            asyncHttpClient.cancelAllRequests(true);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
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

        // list content
        wwListView = (ListView) v.findViewById(R.id.walkway_listView);
        wwInfos = new ArrayList<WalkwayInfo>();
        wwListAdapter = new WalkWayListAdapter(mContext, wwInfos);
        wwListView.setAdapter(wwListAdapter);

        // load button
        loadMoreView = getActivity().getLayoutInflater().inflate(R.layout.list_more, null);
        loadMoreText = (TextView) loadMoreView.findViewById(R.id.load_text);
//        loadMoreText.setText(getString(R.string.doupdata));
        wwListView.addFooterView(loadMoreView);
        wwListView.setOnScrollListener(scrollListener);
    }

    private OnClickListener btnListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            Fragment fragment;
            switch (view.getId()) {
                case R.id.btnBack:
                    callSystemTime();
                    append = currentTime + "," +
                            uuidChose + "," +
                            "WalkWayInfoList" + "," +
                            "WalkWaySearch" + "," +
                            "btnBack" + "\n";
                    transactionLogSave(append);
                    getFragmentManager().popBackStackImmediate();
                    break;
                case R.id.btnPopMenu:
                    menuPopupWindow.showAsDropDown(navigationBar, 0, 0);
                    break;
            }
        }
    };


    private void getWalkWayList(int start) {
        progressDialog.setMessage(getString(R.string.dialog_download_msg));
        progressDialog.show();

        final String url = AppConfig.DOMAIN_SITE_PATE + AppConfig.REQUEST_WALKWAYS_LIST;
        Log.i(TAG, "url:  " + url);

        uuidChose = sharedPreferences.getString(AppConfig.PREF_UNIQUE_ID, "");
        Log.i(TAG, "UUID:  " + uuidChose);
        RequestParams params = new RequestParams();
        params.add("uid", uuidChose);
        params.add("time", AppUtils.getChineseSystemTime());
        params.add("fullyDistance", walkway_distance);
        params.add("walkway_feature", walkway_feature);
        params.add("walkway_area", walkway_area);
        params.add("start", String.valueOf(start));
        params.add("num", String.valueOf(AppConfig.SHOW_CONTENT_NUMBER));

        Log.i(TAG, "params:  " + params);
        asyncHttpClient.post(url, params, jsonHandler);
    }


    private int visibleCount;
    private int visibleLast = 0;
    AbsListView.OnScrollListener scrollListener = new AbsListView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView absListView, int scrollState) {
            Log.d(TAG, "onScrollStateChanged");
            int itemsLastIndex = wwListAdapter.getCount() - 1;
            int lastIndex = itemsLastIndex + 1;
            if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && visibleLast == lastIndex) {
                getWalkWayList(lastIndex);
            }
            Log.d(TAG, "lastIndex:" + lastIndex);
            Log.d(TAG, "visibleLast:" + visibleLast);
        }

        @Override
        public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            visibleCount = visibleItemCount;
            visibleLast = firstVisibleItem + visibleItemCount - 1;
        }
    };

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


    public class WalkWayLists {
        private String msgCode;
        private String status;
        private List<WalkwayInfo> result;


        public String getMsgCode() {
            return msgCode;
        }

        public String getStatus() {
            return status;
        }

        public List<WalkwayInfo> getResult() {
            return result;
        }
    }
}
