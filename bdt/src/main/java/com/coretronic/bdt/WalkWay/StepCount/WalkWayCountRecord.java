package com.coretronic.bdt.WalkWay.StepCount;

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
import com.coretronic.bdt.Adapter.PersonalStepRecordBaseAdapter;
import com.coretronic.bdt.Adapter.WalkWayCountBaseAdapter;
import com.coretronic.bdt.AppConfig;
import com.coretronic.bdt.AppUtils;
import com.coretronic.bdt.DataModule.DailyStepRecord;
import com.coretronic.bdt.DataModule.RestaurantDataInfo;
import com.coretronic.bdt.R;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by darren on 2014/12/10.
 */
public class WalkWayCountRecord extends Activity{
    private String TAG = WalkWayCountRecord.class.getSimpleName();
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
    private WalkWayCountBaseAdapter listAdapter = null;
    private DailyStepRecord dailyStepRecord = null;
    private String walkWayId;
    private String UUIDChose;
    private TextView noDataTV;


    public JsonHttpResponseHandler jsonHandler = new JsonHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            Log.i(TAG, "onSuccess = " + response);
            dailyStepRecord = gson.fromJson(response.toString(), DailyStepRecord.class);

            if (dailyStepRecord.getMsgCode().equals(AppConfig.SUCCESS_CODE)) {

                Log.i(TAG, "dailyStepRecord.getMsgCode:" + dailyStepRecord.getMsgCode());
                listView.setAdapter(new WalkWayCountBaseAdapter(mContext, dailyStepRecord));

                if (dailyStepRecord.getResult().size() == 0) {
                    noDataTV.setText("尚無資料");
                    noDataTV.setVisibility(View.VISIBLE);
                    listView.setVisibility(View.GONE);
                }
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
        setContentView(R.layout.person_step_record_list);
        mContext = this;
        initView();
        btnPopMenu.setVisibility(View.GONE);
        rightLine.setVisibility(View.GONE);
        barTitle.setText("我的記錄");
        sharedPreferences = getSharedPreferences(AppConfig.SHAREDPREFERENCES_NAME, 0);
        walkWayId = sharedPreferences.getString(AppConfig.WALKWAYS_CURRENT_WALKWAYID, walkWayId);
        UUIDChose =sharedPreferences.getString(AppConfig.PREF_UNIQUE_ID,"");

        final String url = AppConfig.DOMAIN_SITE_PATE + AppConfig.REQUEST_WAY_WALK_COUNT;
        params = new RequestParams();
        JSONArray jsonArray = new JSONArray();
        params.add("uid", UUIDChose);
        params.add("walkway_id", walkWayId);
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
        noDataTV = (TextView) findViewById(R.id.no_datatv);
    }

    private View.OnClickListener btnListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btnBack:
                    finish();
                    break;
            }
        }
    };
}
