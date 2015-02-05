package com.coretronic.bdt.MessageWall;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.coretronic.bdt.AppConfig;
import com.coretronic.bdt.AppUtils;
import com.coretronic.bdt.MessageWall.Adapter.CustomerListAdapter;
import com.coretronic.bdt.MessageWall.Module.GoodInfo;
import com.coretronic.bdt.MessageWall.Module.WallMessageList;
import com.coretronic.bdt.R;
import com.coretronic.bdt.Utility.CustomerOneBtnAlertDialog;
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
 * Created by Morris on 2014/10/17.
 */
public class WallGoodListActivity extends Activity {
    private String TAG = WallGoodListActivity.class.getSimpleName();
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
    private ListView wallGoodList;
    private List<String> nameList;
    private String refId = "";
    private String articleType = "";
    // result obj
    private GoodInfo goodInfo;
    private CustomerListAdapter arrayAdapter;
    private View loadMoreView;
    private TextView loadMoreText;
    private CustomerOneBtnAlertDialog oneBtnAlertDialog;
    //transactionLog
    private String currentTime;
    private String append;

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

        initView();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            Log.i(TAG, "bundle: " + bundle);
            refId = bundle.getString("refId");
            articleType = bundle.getString("articleType");
        }
        getWallMsgDetail(0);

    }


    private void initView() {
        // navigation bar
        btnBack = (Button) findViewById(R.id.btnBack);
        barTitle = (TextView) findViewById(R.id.action_bar_title);
        barTitle.setText(getString(R.string.wall_msg_title));
        btnBack.setOnClickListener(btnListener);

        wallGoodList = (ListView) findViewById(R.id.wallGoodList);
        nameList = new ArrayList<String>();
        arrayAdapter = new CustomerListAdapter(mContext, nameList);
        wallGoodList.setAdapter(arrayAdapter);

        loadMoreView = getLayoutInflater().inflate(R.layout.list_more, null);
        loadMoreText = (TextView) loadMoreView.findViewById(R.id.load_text);
//        loadMoreText.setText(getString(R.string.doupdata));
        wallGoodList.addFooterView(loadMoreView);

        wallGoodList.setOnScrollListener(scrollListener);
        wallGoodList.setOnItemClickListener(listItemListener);
    }

    // Location Listener
    AdapterView.OnItemClickListener listItemListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
            WallMessageList item = (WallMessageList) adapterView.getItemAtPosition(position);
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
                    callSystemTime();
                    append = currentTime + "," +
                            uuidChose + "," +
                            "WallMsgDetailTxtGoodFriends" + "," +
                            "WallMsgDetail" + "," +
                            "btnBack" + "\n";
                    transactionLogSave(append);
                    finish();
                    break;
            }
        }
    };

    private void getWallMsgDetail(int start) {
        progressDialog.setMessage(getString(R.string.dialog_download_msg));
        progressDialog.show();

        final String url = AppConfig.DOMAIN_SITE_PATE + AppConfig.REQUEST_ARTICLE_GOODS;
        Log.i(TAG, "url:  " + url);

        uuidChose = sharedPreferences.getString(AppConfig.PREF_UNIQUE_ID, "");
        RequestParams params = new RequestParams();
        params.add("uid", uuidChose);
        params.add("time", AppUtils.getSystemTime());
        params.add("refId", refId);
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
                    goodInfo = gson.fromJson(response.toString(), GoodInfo.class);
                    if (goodInfo != null && goodInfo.getResult() != null) {
                        if (goodInfo.getResult().getGood() != null) {
                            for (int i = 0; i < goodInfo.getResult().getGood().size(); i++) {
                                Log.d(TAG, "name = " + goodInfo.getResult().getGood().get(i).getName());
                                nameList.add(goodInfo.getResult().getGood().get(i).getName());
                            }
                            arrayAdapter.notifyDataSetChanged();
                            wallGoodList.setSelection(visibleLast - visibleCount);
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
}

