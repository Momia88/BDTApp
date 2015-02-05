package com.coretronic.bdt;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.coretronic.bdt.Tutorial.TutorFirstPageActivity;
import com.coretronic.bdt.Utility.CustomerOneBtnAlertDialog;
import com.coretronic.bdt.Utility.CustomerTwoBtnAlertDialog;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import org.apache.http.Header;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class LoginActivity extends Activity {

    private String TAG = LoginActivity.class.getSimpleName();
    private Context mContext;
    private RadioGroup radioGroup;
    private RadioButton radioMan, radioFemale;
    private Button btnStart;
    private Spinner ageSpinner;
    private String userSex = "男";
    private String[] ageStr = {
            "44歲以下",
            "45歲-54歲",
            "55歲-64歲",
            "65歲-70歲",
            "71歲以上"
    };
    //    private String twBirthday;
//    private ChineseDatePickerDialog dialog;
    private SharedPreferences sharedPreferences;
    private ProgressDialog progressDialog = null;
    private AsyncHttpClient asyncHttpClient;
    private CustomerTwoBtnAlertDialog customerAlertDialog;

    public JsonHttpResponseHandler jsonHandler = new JsonHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
            Log.d(TAG, "onSuccess = " + response);
            try {
                if (response.get("msgCode").equals("A01")) {
                    sharedPreferences.edit()
                            .putBoolean(AppConfig.PREF_IS_CREATED, true)
                            .putBoolean(AppConfig.PREF_FIRST_USED, true)
                            .commit();
                    Intent i = new Intent(LoginActivity.this, TutorFirstPageActivity.class);
                    startActivity(i);
                    finish();
                } else {
                    customerAlertDialog.setMsg(getString(R.string.data_result_error));
                    customerAlertDialog.show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                customerAlertDialog.setMsg(getString(R.string.data_result_error));
                customerAlertDialog.show();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            Log.d(TAG, "onFailure");
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
            customerAlertDialog.setMsg(getString(R.string.data_load_error));
            customerAlertDialog.show();
        }
    };

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        mContext = this;
        asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.setTimeout(AppConfig.TIMEOUT);
        progressDialog = new ProgressDialog(mContext);
        sharedPreferences = getSharedPreferences(AppConfig.SHAREDPREFERENCES_NAME, 0);
        customerAlertDialog = new CustomerTwoBtnAlertDialog(mContext);
        customerAlertDialog.setMsg(getString(R.string.data_result_error));
        customerAlertDialog.setPositiveBtnText("重試");
        customerAlertDialog.setPositiveListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customerAlertDialog.dismiss();
                sendFristUseTime();
            }
        });
        customerAlertDialog.setNegativeBtnText("離開");
        customerAlertDialog.setNegativeListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customerAlertDialog.dismiss();
                finish();
            }
        });
        // initial view items
        initView();

    }

    private void initView() {
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        radioMan = (RadioButton) findViewById(R.id.radioMan);
        radioFemale = (RadioButton) findViewById(R.id.radioFemale);
        ageSpinner = (Spinner) findViewById(R.id.userBirthday);
        btnStart = (Button) findViewById(R.id.btnStart);
        //set Listener
        btnStart.setOnClickListener(btnStartListener);
        radioGroup.setOnCheckedChangeListener(checkedChangeListener);
        ageSpinner.setAdapter(new ArrayAdapter<String>(mContext, R.layout.spinner_layout_division, ageStr));
    }

    RadioGroup.OnCheckedChangeListener checkedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
            switch (checkedId) {
                case R.id.radioMan:
                    userSex = radioMan.getText().toString();
                    break;
                case R.id.radioFemale:
                    userSex = radioFemale.getText().toString();
                    break;
            }
        }
    };

    View.OnClickListener btnStartListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            CustomerOneBtnAlertDialog dialog = new CustomerOneBtnAlertDialog(mContext);

            if (ageSpinner.getSelectedItem().toString().trim().length() == 0) {
                dialog.setMsg(getString(R.string.dialog_birthday_alert)).show();
            } else {
                if (AppUtils.isOnline(mContext) == false) {
                    dialog.setMsg(getString(R.string.network_off_alert)).show();
                    return;
                }
                sharedPreferences.edit()
                        .putString(AppConfig.PREF_USER_AGE, ageSpinner.getSelectedItem().toString())
                        .putString(AppConfig.PREF_USER_SEX, userSex)
                        .apply();
                sendFristUseTime();
            }
        }
    };

    private void sendFristUseTime() {
        final String url = AppConfig.DOMAIN_SITE_PATE + AppConfig.CREATE_FIRST_TIME_USE;

        Log.i(TAG, "url:  " + url);
        RequestParams params = new RequestParams();
        params.add("uid", getUUID());
        params.add("sex", userSex);
        params.add("age_level", ageSpinner.getSelectedItem().toString());
        params.add("first_use_time", getFirstUseTime());

        progressDialog.setMessage(getString(R.string.dialog_download_msg));
        progressDialog.show();
        Log.i(TAG, "params:  " + params);
        asyncHttpClient.post(url, params, jsonHandler);

    }

    private static String uniqueID = null;
    private static String firstUseTime = null;

    private String getUUID() {
        if (uniqueID == null) {
            uniqueID = sharedPreferences.getString(AppConfig.PREF_UNIQUE_ID, null);
            if (uniqueID == null) {
                uniqueID = UUID.randomUUID().toString();
                sharedPreferences.edit()
                        .putString(AppConfig.PREF_UNIQUE_ID, uniqueID)
                        .putString(AppConfig.PREF_DEVICE_ID, uniqueID)
                        .apply();
            }
        }
        Log.d(TAG, "uniqueID = " + uniqueID);
        return uniqueID;
    }

    private String getFirstUseTime() {
        if (firstUseTime == null) {
            firstUseTime = sharedPreferences.getString(AppConfig.PREF_FIRST_TIME_USE, null);
            if (firstUseTime == null) {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm");
                Date curDate = new Date(System.currentTimeMillis()); // 獲取當前時間
                firstUseTime = formatter.format(curDate);
                sharedPreferences.edit()
                        .putString(AppConfig.PREF_UNIQUE_ID, uniqueID)
                        .apply();
            }
        }
        Log.d(TAG, "firstUseTime = " + firstUseTime);
        return firstUseTime;
    }

    private void getUserInfo() {
        progressDialog.setMessage(getString(R.string.dialog_download_msg));
        progressDialog.show();

        final String url = AppConfig.USER_AUTH_SITE_PATE + AppConfig.USER_INFO;
        Log.i(TAG, "url:  " + url);

        RequestParams params = new RequestParams();
        params.add("uid", uniqueID);
        params.add("time", AppUtils.getSystemTime());

        Log.i(TAG, "params:  " + params);
        asyncHttpClient.post(url, params, jsonHandler);
    }
}
