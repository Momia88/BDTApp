package com.coretronic.bdt.WalkWay;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.coretronic.bdt.AppConfig;
import com.coretronic.bdt.AppUtils;
import com.coretronic.bdt.MessageWall.Adapter.CustomerListAdapter;
import com.coretronic.bdt.MessageWall.Module.WallMessageList;
import com.coretronic.bdt.R;
import com.coretronic.bdt.Utility.CustomerOneBtnAlertDialog;
import com.coretronic.bdt.WalkWay.Module.WalkWayVisitedInfo;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Morris on 2014/10/17.
 */
public class WalkWayFriendsListActivity extends Activity {
    private String TAG = WalkWayFriendsListActivity.class.getSimpleName();
    private Context mContext;
    private Button btnBack;
    private TextView barTitle;
    private SharedPreferences sharedPreferences;
    private ProgressDialog progressDialog = null;
    private String uuidChose;
    private AsyncHttpClient asyncHttpClient;
    private Gson gson = new Gson();
    private SimpleDateFormat formatter;
    private Date curDate;
    private ListView walkWayFriendsList;
    private List<String> nameList;
    private String walkWayId = "";
    private String articleType = "";
    // result obj
    private WalkWayVisitedInfo walkWayVisitedInfo;
    private CustomerListAdapter arrayAdapter;
    private View loadMoreView;
    private TextView loadMoreText;
    private CustomerOneBtnAlertDialog oneBtnAlertDialog;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wall_good_list);
        mContext = this;
        sharedPreferences = getSharedPreferences(AppConfig.SHAREDPREFERENCES_NAME, 0);
        asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.setTimeout(AppConfig.TIMEOUT);
        uuidChose = sharedPreferences.getString(AppConfig.PREF_UNIQUE_ID, "");
        progressDialog = new ProgressDialog(mContext);
        oneBtnAlertDialog = new CustomerOneBtnAlertDialog(mContext);

        initView();
        try {
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                Log.i(TAG, "bundle: " + bundle);
                walkWayId = bundle.getString("walkwayid", null);
            }
            if (walkWayId != null) {
                getWallMsgDetail(0);
            } else {
                oneBtnAlertDialog
                        .setMsg(getString(R.string.get_data_error))
                        .setPositiveListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                oneBtnAlertDialog.dismiss();
                                finish();
                            }
                        }).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void initView() {
        // navigation bar
        btnBack = (Button) findViewById(R.id.btnBack);
        barTitle = (TextView) findViewById(R.id.action_bar_title);
        barTitle.setText(getString(R.string.ww_friend_list_title));
        btnBack.setOnClickListener(btnListener);

        walkWayFriendsList = (ListView) findViewById(R.id.wallGoodList);
        nameList = new ArrayList<String>();
        arrayAdapter = new CustomerListAdapter(mContext, nameList);
        walkWayFriendsList.setAdapter(arrayAdapter);
        walkWayFriendsList.setOnItemClickListener(listItemListener);

        loadMoreView = getLayoutInflater().inflate(R.layout.list_more, null);
        loadMoreText = (TextView) loadMoreView.findViewById(R.id.load_text);
//        loadMoreText.setText(getString(R.string.doupdata));
        walkWayFriendsList.addFooterView(loadMoreView);
        walkWayFriendsList.setOnScrollListener(scrollListener);
    }

    // Location Listener
    AdapterView.OnItemClickListener listItemListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
//            WallMessageList item = (WallMessageList) adapterView.getItemAtPosition(position);
        }
    };

    private int visibleCount;
    private int visibleLast = 0;
    AbsListView.OnScrollListener scrollListener = new AbsListView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView absListView, int scrollState) {
            Log.d(TAG, "onScrollStateChanged");
            int itemsLastIndex = arrayAdapter.getCount() - 1;
            int lastIndex = itemsLastIndex + 1;
            if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && visibleLast == lastIndex) {
                getWallMsgDetail(lastIndex);
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

    View.OnClickListener btnListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btnBack:
                    finish();
                    break;
            }
        }
    };

    private void getWallMsgDetail(int start) {
        progressDialog.setMessage(getString(R.string.dialog_download_msg));
        progressDialog.show();

        final String url = AppConfig.DOMAIN_SITE_PATE + AppConfig.REQUEST_WALKWAYS_VISITS;
        Log.i(TAG, "url:  " + url);

        uuidChose = sharedPreferences.getString(AppConfig.PREF_UNIQUE_ID, "");
        RequestParams params = new RequestParams();
        params.add("uid", uuidChose);
        params.add("time", AppUtils.getSystemTime());
        params.add("walkway_id", walkWayId);
        params.add("start", String.valueOf(start));
        params.add("num", String.valueOf(AppConfig.SHOW_CONTENT_NUMBER));
        Log.i(TAG, "params:" + params.toString());

        asyncHttpClient.post(url, params, jsonHandler);
    }

    public JsonHttpResponseHandler jsonHandler = new JsonHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
            Log.d(TAG, "onSuccess = " + response);
            try {
                if (response.get("msgCode").equals("A01")) {
                    walkWayVisitedInfo = gson.fromJson(response.toString(), WalkWayVisitedInfo.class);
                    if (walkWayVisitedInfo != null && walkWayVisitedInfo.getResult() != null) {
                        if (walkWayVisitedInfo.getResult().getFriends() != null) {
                            for (int i = 0; i < walkWayVisitedInfo.getResult().getFriends().size(); i++) {
                                Log.d(TAG, "name = " + walkWayVisitedInfo.getResult().getFriends().get(i).getName());
                                nameList.add(walkWayVisitedInfo.getResult().getFriends().get(i).getName());
                            }
                            arrayAdapter.notifyDataSetChanged();
                            walkWayFriendsList.setSelection(visibleLast - visibleCount);
                        }
                    }

                } else {
                    oneBtnAlertDialog = AppUtils.getAlertDialog(mContext, response.get("result").toString());
                    oneBtnAlertDialog.show();
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
            Log.d(TAG, "onFailure");
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
            oneBtnAlertDialog = AppUtils.getAlertDialog(mContext, getString(R.string.data_load_error));
            oneBtnAlertDialog.show();
        }
    };

    @Override
    public void onPause() {
        super.onPause();
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        if (oneBtnAlertDialog != null) {
            oneBtnAlertDialog.dismiss();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

}

