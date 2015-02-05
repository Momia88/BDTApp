package com.coretronic.bdt.WalkWay;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.coretronic.bdt.AppConfig;
import com.coretronic.bdt.AppUtils;
import com.coretronic.bdt.Person.Register.PersonLoginActivity;
import com.coretronic.bdt.R;
import com.coretronic.bdt.Utility.CustomerTwoBtnAlertDialog;
import com.coretronic.bdt.module.MenuPopupWindow;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class WalkWayIndex extends Fragment {
    private String TAG = WalkWayIndex.class.getSimpleName();

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

    private Button btnSearch;
    private Button btnNear;
    private Button btnRecord;

    private TextView tonyCopyrightTV;

    //transactionLog
    private String currentTime;
    private String append;
    private boolean isLogin = false;
    private CustomerTwoBtnAlertDialog alertDialog = null;
    private Intent intent = null;
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
        View v = inflater.inflate(R.layout.walkway_index, container, false);
        mContext = v.getContext();
        progressDialog = new ProgressDialog(mContext);
        menuPopupWindow = new MenuPopupWindow(mContext, null);
        initView(v);
        intent = new Intent();
        alertDialog = AppUtils.getAlertDialog(mContext, mContext.getResources().getString(R.string.pp_msg_need_register), "登入", "取消", dialogListener);
        return v;
    }

    private void initView(View v) {
        // navigation bar
        navigationBar = (RelativeLayout) v.findViewById(R.id.navigationBar_option);
        btnBack = (Button) v.findViewById(R.id.btnBack);
//        btnPopMenu = (Button) v.findViewById(R.id.btnPopMenu);
        barTitle = (TextView) v.findViewById(R.id.action_bar_title);

        tonyCopyrightTV = (TextView) v.findViewById(R.id.tonyCopyrightTV);

        barTitle.setText(getString(R.string.lb_ww_title));
        btnBack.setOnClickListener(btnListener);
//        btnPopMenu.setOnClickListener(btnListener);
        // button
        btnSearch = (Button) v.findViewById(R.id.btn_ww_search);
        btnNear = (Button) v.findViewById(R.id.btn_ww_near);
        btnRecord = (Button) v.findViewById(R.id.btn_ww_record);
        btnSearch.setOnClickListener(btnListener);
        btnNear.setOnClickListener(btnListener);
        btnRecord.setOnClickListener(btnListener);
        tonyCopyrightTV.setOnClickListener(btnListener);



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
                            "WalkWayMainActivity" + "," +
                            "MainActivity" + "," +
                            "btnBack" + "\n";
                    transactionLogSave(append);
                    getActivity().finish();
                  break;
                case R.id.btnPopMenu:
                    menuPopupWindow.showAsDropDown(navigationBar, 0, 0);
                    break;
                case R.id.tonyCopyrightTV:
                    Intent intenet = new Intent(Intent.ACTION_VIEW);
                    intenet.setData(Uri.parse(AppConfig.TONY_COPYRIGHT_WEBSITEPATH));
                    startActivity(intenet);

                    break;
                case R.id.btn_ww_search:
                    callSystemTime();
                    append = currentTime + "," +
                            uuidChose + "," +
                            "WalkWayMainActivity" + "," +
                            "WalkWaySearch" + "," +
                            "btnWalkWaySearch" + "\n";
                    transactionLogSave(append);
                    fragment = new WalkWaySearch();
                    if (fragment != null) {
                        getFragmentManager().beginTransaction()
                                .replace(R.id.frame_container, fragment,"WalkWaySearch")
                                .addToBackStack("WalkWaySearch")
                                .commit();
                    } else {
                        Log.e(TAG, "Error in creating fragment");
                    }
                    break;
                case R.id.btn_ww_near:
                    callSystemTime();
                    append = currentTime + "," +
                            uuidChose + "," +
                            "WalkWayMainActivity" + "," +
                            "WalkWayNear" + "," +
                            "btnWalkWayNear" + "\n";
                    transactionLogSave(append);
                    fragment = new WalkWayNear();
                    if (fragment != null) {
                        getFragmentManager().beginTransaction()
                                .replace(R.id.frame_container, fragment,"WalkWayNear")
                                .addToBackStack("WalkWayNear")
                                .commit();
                    } else {
                        Log.e(TAG, "Error in creating fragment");
                    }
                    break;

                case R.id.btn_ww_record:
                    callSystemTime();
                    append = currentTime + "," +
                            uuidChose + "," +
                            "WalkWayMainActivity" + "," +
                            "WalkWayRecordMap" + "," +
                            "btnWalkWayRecord" + "\n";
                    transactionLogSave(append);
                    isLogin = sharedPreferences.getBoolean(AppConfig.PREF_IS_LOGIN, false);
                    Log.i(TAG,"is Login:" + isLogin);
                    if( isLogin == false )
                    {
                        alertDialog.show();
                    }
                    else{
                        fragment = new WalkWayArea();
                        if (fragment != null) {
                            getFragmentManager().beginTransaction()
                                    .replace(R.id.frame_container, fragment,"WalkWayRecordMap")
                                    .addToBackStack("WalkWayRecordMap")
                                    .commit();
                        } else {
                            Log.e(TAG, "Error in creating fragment");
                        }
                    }

                    break;
            }
        }
    };
    // login listener
    private View.OnClickListener dialogListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Log.i(TAG, "click the dialog sure");
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

    //抓取系統時間
    private void callSystemTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
        Date curDate = new Date(System.currentTimeMillis()); // 獲取當前時間
        currentTime = formatter.format(curDate);
        Log.i(TAG, "==Time==: " + currentTime);

    }
}
