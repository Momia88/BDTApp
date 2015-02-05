package com.coretronic.bdt.Person;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.coretronic.bdt.AppConfig;
import com.coretronic.bdt.AppUtils;
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Morris on 14/11/25.
 */
public class AboutMe extends Fragment {
    private static String TAG = AboutMe.class.getSimpleName();

    private Button btnBack;
    private TextView barTitle;
    private Context mContext;
    private RelativeLayout navigationBar;
    private SharedPreferences sharedPreferences;
    private ProgressDialog progressDialog = null;
    private String uuidChose;
    private AsyncHttpClient asyncHttpClient;
    private Gson gson = new Gson();

    private ImageView mMyPhoto;
    private TextView mPpRegisterUserName;
    private TextView mPpRegisterBirthday;
    private TextView mPpRegisterSex;
    private TextView mPpRegisterAddress;
    private Button mPpBtnAboutMeLogin;
    private File userPhoto = null;
    private CustomerOneBtnAlertDialog oneBtnAlertDialog;
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
                    setUserInfo();
                } else {
                    oneBtnAlertDialog = AppUtils.getAlertDialog(mContext, getString(R.string.data_load_error));
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
        View v = inflater.inflate(R.layout.person_about_me, container, false);
        mContext = v.getContext();
        progressDialog = new ProgressDialog(mContext);
        userPhoto = new File(mContext.getExternalCacheDir(), uuidChose + ".png");
        initView(v);
        getUserInfo();
        return v;
    }

    @Override
    public void onPause() {
        super.onPause();
        if(oneBtnAlertDialog != null){
            oneBtnAlertDialog.dismiss();
        }
        if(progressDialog != null){
            progressDialog.dismiss();
        }
    }

    private void initView(View v) {
        // navigation bar
        navigationBar = (RelativeLayout) v.findViewById(R.id.navigationBar_option);
        btnBack = (Button) v.findViewById(R.id.btnBack);
        barTitle = (TextView) v.findViewById(R.id.action_bar_title);

        barTitle.setText(getString(R.string.pp_aboutme_title));
        btnBack.setOnClickListener(btnListener);
        mMyPhoto = (ImageView) v.findViewById(R.id.pp_img_user_photo);


        mPpRegisterUserName = (TextView) v.findViewById(R.id.pp_register_user_name);
        mPpRegisterBirthday = (TextView) v.findViewById(R.id.pp_register_birthday);
        mPpRegisterSex = (TextView) v.findViewById(R.id.pp_register_sex);
        mPpRegisterAddress = (TextView) v.findViewById(R.id.pp_register_address);
        mPpBtnAboutMeLogin = (Button) v.findViewById(R.id.pp_btn_about_me_sumit);
        mPpBtnAboutMeLogin.setOnClickListener(btnListener);
    }


    private View.OnClickListener btnListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btnBack:
                    callSystemTime();
                    append = currentTime + "," +
                            uuidChose + "," +
                            "AboutMe" + "," +
                            "Person" + "," +
                            "btnBack" + "\n";
                    transactionLogSave(append);
                    getFragmentManager().popBackStackImmediate();
                    break;
                case R.id.pp_btn_about_me_sumit:
                    callSystemTime();
                    append = currentTime + "," +
                            uuidChose + "," +
                            "AboutMe" + "," +
                            "AboutMeEditor" + "," +
                            "btnAboutMeEditor" + "\n";
                    transactionLogSave(append);
                    Fragment fragment = new AboutMeEditor();
                    if (fragment != null) {
                        getFragmentManager().beginTransaction()
                                .replace(R.id.person_frame_container, fragment, "AboutMeEditor")
                                .addToBackStack("AboutMeEditor")
                                .commit();
                    } else {
                        Log.e(TAG, "Error in creating fragment");
                    }
                    break;
            }
        }
    };

    private void getUserInfo() {
        progressDialog.setMessage(getString(R.string.dialog_download_msg));
        progressDialog.show();

        final String url = AppConfig.USER_AUTH_SITE_PATE + AppConfig.USER_INFO;
        Log.i(TAG, "url:  " + url);

        uuidChose = sharedPreferences.getString(AppConfig.PREF_UNIQUE_ID, "");
        Log.i(TAG, "UUID:  " + uuidChose);
        RequestParams params = new RequestParams();
        params.add("uid", uuidChose);
        params.add("time", AppUtils.getSystemTime());

        Log.i(TAG, "params:  " + params);
        asyncHttpClient.post(url, params, jsonHandler);
    }

    private void setUserInfo() {
        String str = sharedPreferences.getString(AppConfig.PREF_USER_THUMB, null);
        if (str != null && str.length() > 0) {
            mMyPhoto.setImageBitmap(AppUtils.base64ToBitmap(str));
        }
        mPpRegisterUserName.setText(sharedPreferences.getString(AppConfig.PREF_USER_NANE, null));
        mPpRegisterBirthday.setText(AppUtils.dateToChineseDate(sharedPreferences.getString(AppConfig.PREF_USER_BIRTHDAY, null)));
        mPpRegisterSex.setText(sharedPreferences.getString(AppConfig.PREF_USER_SEX, null));
        mPpRegisterAddress.setText(sharedPreferences.getString(AppConfig.PREF_USER_ADDRESS, null));
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