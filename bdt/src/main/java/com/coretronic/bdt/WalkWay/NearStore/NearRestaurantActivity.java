package com.coretronic.bdt.WalkWay.NearStore;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.coretronic.bdt.Adapter.NearRestaurantListAdapter;
import com.coretronic.bdt.AppConfig;
import com.coretronic.bdt.AppUtils;
import com.coretronic.bdt.DataModule.RestaurantDataInfo;
import com.coretronic.bdt.R;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by darren on 2014/12/9.
 */
public class NearRestaurantActivity extends Activity {
    private String TAG = NearRestaurantActivity.class.getSimpleName();
    private ListView listView = null;
    private Context mContext;
    private Button btnBack = null;
    private SharedPreferences sharedPreferences;
    private RequestParams params = null;
    private AsyncHttpClient client = null;
    private Gson gson = new Gson();
    private Button btnPopMenu = null;
    private View rightLine = null;
    private RelativeLayout navigationBar;
    private TextView barTitle;
    private NearRestaurantListAdapter listAdapter = null;
    private RestaurantDataInfo restaurantDataInfo = null;
    private String walkWayId;
    //transactionLog
    private String currentTime;
    private String append;
    private String uuidChose;

    public JsonHttpResponseHandler jsonHandler = new JsonHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            Log.i(TAG, "onSuccess = " + response);
            restaurantDataInfo = gson.fromJson(response.toString(), RestaurantDataInfo.class);

            if (restaurantDataInfo.getMsgCode().equals(AppConfig.SUCCESS_CODE)) {

                Log.i(TAG, "restaurantDataInfo.getMsgCode:" + restaurantDataInfo.getMsgCode());
                listView.setAdapter(new NearRestaurantListAdapter(mContext, restaurantDataInfo));
            }

        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            Log.i(TAG, "onFailure = ");
            AppUtils.getAlertDialog(mContext, getString(R.string.data_load_error)).show();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.near_restaurant_list);
        mContext = this;
        initView();
        btnPopMenu.setVisibility(View.GONE);
        rightLine.setVisibility(View.GONE);
        barTitle.setText("附近餐廳");

        sharedPreferences = getSharedPreferences(AppConfig.SHAREDPREFERENCES_NAME, 0);
        walkWayId = sharedPreferences.getString(AppConfig.WALKWAYS_CURRENT_WALKWAYID, walkWayId);
        Log.i(TAG, "walkWayId: " + walkWayId);
        uuidChose = sharedPreferences.getString(AppConfig.PREF_UNIQUE_ID, "");

        final String url = AppConfig.DOMAIN_SITE_PATE + AppConfig.REQUEST_NEAR_RESTAURANTS;
        params = new RequestParams();
        JSONArray jsonArray = new JSONArray();
        params.add("walkwayId", walkWayId);
        //將參數包成一個array
        jsonArray.put(params);
        Log.i(TAG, "PARAMS:  " + params);

        client = new AsyncHttpClient();
        client.setTimeout(AppConfig.TIMEOUT);
        client.post(url, params, jsonHandler);

    }

    private void initView() {
        navigationBar = (RelativeLayout) findViewById(R.id.navigationBar_option);
        barTitle = (TextView) findViewById(R.id.action_bar_title);
        btnPopMenu = (Button) findViewById(R.id.btnPopMenu);
        rightLine = (View) findViewById(R.id.right_line);
        btnBack = (Button) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(btnListener);
        listView = (ListView) findViewById(R.id.listView);

    }

    private View.OnClickListener btnListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btnBack:
                    callSystemTime();
                    append = currentTime + "," +
                            uuidChose + "," +
                            "NearRestaurantActivity" + "," +
                            "WalkWayListDetailInfo" + "," +
                            "btnBack" + "\n";
                    transactionLogSave(append);
                    finish();
                    break;
            }
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
}

