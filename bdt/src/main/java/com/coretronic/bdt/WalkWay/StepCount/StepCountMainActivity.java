package com.coretronic.bdt.WalkWay.StepCount;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.coretronic.bdt.AppConfig;
import com.coretronic.bdt.AppUtils;
import com.coretronic.bdt.Person.Register.PersonLoginActivity;
import com.coretronic.bdt.R;
import com.coretronic.bdt.Utility.CustomerTwoBtnAlertDialog;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import org.apache.http.Header;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by darren on 2014/11/19.
 */
public class StepCountMainActivity extends Activity {
    private Button startButton;
    private Button stopButton;
    private Button stepRecord;
    private static int steps;
    private TextView textCurrentSteps;
    private TextView textCurrentTime;
    private TextView textView_1;
    private String times, date, newDate, stepCount, newStep, totalTime, newTime;
    private Integer step;
    private SharedPreferences sharedPreferences;
    private Handler updateHandler = new Handler();
    private String TAG = StepCountMainActivity.class.getSimpleName();
    private String walkWayId;
    private String walkWayName;
    private ProgressDialog dialog = null;
    private Context mContext;
    private RequestParams params= null;
    private AsyncHttpClient client = null;
    private String UUIDChose;
    private String startTime, endTime;
    private Gson gsonPost = new Gson();
    private String jsonData;
    private String currentTime;
    private Button btnPopMenu = null;
    private RelativeLayout navigationBar;
    private TextView barTitle;
    private View rightLine = null;
    private Button btnBack = null;
    private boolean isLogin = false;
    private CustomerTwoBtnAlertDialog alertDialog = null;
    private Intent intent = null;

    //transactionLog
    private String append;

    public JsonHttpResponseHandler jsonHandler = new JsonHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            Log.i(TAG, "onSuccess = " + response);

        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            Log.i(TAG, "onFailure");
//            if(dialog != null){
//                dialog.dismiss();
//            }
//            AppUtils.getAlertDialog(mContext, getString(R.string.data_load_error)).show();

        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");
        setContentView(R.layout.step_count_main);
        mContext = this;

        sharedPreferences = getSharedPreferences(AppConfig.SHAREDPREFERENCES_NAME, 0);
        walkWayId = sharedPreferences.getString(AppConfig.WALKWAYS_CURRENT_WALKWAYID, walkWayId);
        walkWayName = sharedPreferences.getString(AppConfig.WALKWAYS_CURRENT_WALKWAYNAME, walkWayName);
        UUIDChose =sharedPreferences.getString(AppConfig.PREF_UNIQUE_ID,"");
        Log.i(TAG, "walkWayId: " + walkWayId);
        Log.i(TAG, "walkWayName: " + walkWayName);
        Log.i(TAG, "UUID:  " + UUIDChose);

        dialog  = new ProgressDialog(mContext);
        alertDialog = AppUtils.getAlertDialog(mContext, mContext.getResources().getString(R.string.pp_msg_need_register), "登入", "取消", dialogListener);
        initView();

        stepRecord.setText("在"+walkWayName+"的記錄");
        textView_1.setText("於"+walkWayName+"走了");
        btnPopMenu.setVisibility(View.GONE);
        rightLine.setVisibility(View.GONE);
        barTitle.setText("計步器");



    }

    private void initView() {

        textCurrentSteps = (TextView) findViewById(R.id.step);
        textCurrentTime = (TextView) findViewById(R.id.time);
        textView_1 = (TextView) findViewById(R.id.text_view_1);
        startButton = (Button) findViewById(R.id.startButton);
        stopButton = (Button) findViewById(R.id.stopButton);
        stepRecord = (Button) findViewById(R.id.stepRecord);
        startButton.setOnClickListener(startClickListener);
        stopButton.setOnClickListener(stopClickListener);
        stepRecord.setOnClickListener(btnListener);
        navigationBar = (RelativeLayout) findViewById(R.id.navigationBar_option);
        barTitle = (TextView) findViewById(R.id.action_bar_title);
        btnPopMenu = (Button) findViewById(R.id.btnPopMenu);
        rightLine = (View) findViewById(R.id.right_line);
        btnBack = (Button) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(btnListener);
    }

    private View.OnClickListener btnListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = null;
            switch (view.getId()) {
                case R.id.btnBack:
                    finish();
                    callSystemTime();
                    append = currentTime + "," +
                            UUIDChose + "," +
                            "StepCountMainActivity" + "," +
                            "WalkWayListDetailInfo" + "," +
                            "btnBack" + "\n";
                    transactionLogSave(append);
                    break;
                case R.id.stepRecord:
                    callSystemTime();
                    append = currentTime + "," +
                            UUIDChose + "," +
                            "StepCountMainActivity" + "," +
                            "WalkWayCountRecord" + "," +
                            "btnWalkWayCountRecord" + "\n";
                    transactionLogSave(append);
                    isLogin = sharedPreferences.getBoolean(AppConfig.PREF_IS_LOGIN, false);
                    Log.i(TAG,"is Login:" + isLogin);
                    if (isLogin == true) {
                        intent = new Intent(StepCountMainActivity.this, WalkWayCountRecord.class);
                        startActivity(intent);
                    } else {
                        alertDialog.show();
                    }

                    break;
            }
        }
    };

    //處理時間
    private Runnable updateTimes = new Runnable() {
        public void run() {
//            newDate = sharedPreferences.getString(AppConfig.PREF_DATE_KEY, date);
//            updateHandler.postDelayed(this, 1000);
//            textCurrentTime.setText(newDate);
            newTime = sharedPreferences.getString(AppConfig.PREF_TIME_KEY, totalTime);
            updateHandler.postDelayed(this, 1000);
            textCurrentTime.setText(newTime);

        }
    };

    //處理步數
    private Runnable updateSteps = new Runnable() {
        public void run() {
            newStep = sharedPreferences.getString(AppConfig.PREF_STEP_KEY, stepCount);
            updateHandler.postDelayed(this, 100);
            textCurrentSteps.setText(newStep+" 步");
        }
    };

    //抓取系統時間
    private void callSystemTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
        Date curDate = new Date(System.currentTimeMillis()); // 獲取當前時間
        currentTime = formatter.format(curDate);
        Log.i(TAG, "==Time==: " + currentTime);

    }

    private Button.OnClickListener startClickListener = new Button.OnClickListener() {
        public void onClick(View arg0) {
            Log.i(TAG, "啟動服務");
            callSystemTime();
            append = currentTime + "," +
                    UUIDChose + "," +
                    "StepCountMainActivity" + "," +
                    "StepCountMainActivity" + "," +
                    "btnstartStepCount" + "\n";
            transactionLogSave(append);
            newStep = "0 步";
            newTime = "0時:0分:0秒";
            startTime = currentTime;
            //啟動服務
            Intent intent = new Intent(StepCountMainActivity.this, StepCountBackService.class);
            startService(intent);
            updateHandler.post(updateTimes);
            updateHandler.post(updateSteps);
        }
    };

    private Button.OnClickListener stopClickListener = new Button.OnClickListener() {
        public void onClick(View arg0) {
            Log.i(TAG, "停止服務");
            callSystemTime();
            append = currentTime + "," +
                    UUIDChose + "," +
                    "StepCountMainActivity" + "," +
                    "StepCountMainActivity" + "," +
                    "btnstopStepCount" + "\n";
            transactionLogSave(append);
            endTime = currentTime;
            //停止服務
            Intent intent = new Intent(StepCountMainActivity.this, StepCountBackService.class);
            stopService(intent);
            steps = 0;
            stepCountPost();
        }
    };

    private void stepCountPost(){

        final String url = AppConfig.DOMAIN_SITE_PATE + AppConfig.INSERT_WAY_WALK_COUNT;
        params = new RequestParams();
        params.add("start", startTime);
        params.add("end", endTime);
        params.add("walkway_id", walkWayId);
        params.add("uid", UUIDChose);
        params.add("count", newStep);
        Log.i(TAG, "params: " + params);

//        params.add("result", params);

        client = new AsyncHttpClient();
        client.setTimeout(AppConfig.TIMEOUT);
        client.post(url, params, jsonHandler);
    }

    // login listener
    private View.OnClickListener dialogListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Log.i(TAG, "click the dialog sure");
            Intent intent = new Intent();
            intent.setClass(mContext, PersonLoginActivity.class);
            mContext.startActivity(intent);
            alertDialog.dismiss();
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
}