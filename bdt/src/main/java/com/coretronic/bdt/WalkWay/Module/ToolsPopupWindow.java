package com.coretronic.bdt.WalkWay.Module;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;

import com.coretronic.bdt.AppConfig;
import com.coretronic.bdt.AppUtils;
import com.coretronic.bdt.Person.Register.PersonLoginActivity;
import com.coretronic.bdt.R;
import com.coretronic.bdt.Utility.CustomerTwoBtnAlertDialog;
import com.coretronic.bdt.WalkWay.Diary.DiaryMainActivity;
import com.coretronic.bdt.WalkWay.NearStore.NearRestaurantActivity;
import com.coretronic.bdt.WalkWay.NearStore.NearStoreActivity;
import com.coretronic.bdt.WalkWay.StepCount.StepCountMainActivity;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by Morris on 2014/8/13.
 */
public class ToolsPopupWindow extends PopupWindow {
    private String TAG = ToolsPopupWindow.class.getSimpleName();
    private Button btnPedometer;
    private Button btnDaily;
    private Button btnPhoto;
    private Button btnVisit;
    private Button btnRestaurage;
    private Button btnStore;
    private Context mContext;
    private Bundle bundle = null;
    private String mWalkId = null;
    private CustomerTwoBtnAlertDialog alertDialog = null;
    private OnDismissListener dismissListener = null;
    private boolean isLogin = false;
    private SharedPreferences sharedPreferences;

    //transactionLog
    private String currentTime;
    private String append;
    private String uuidChose;

    public ToolsPopupWindow(Context context, OnDismissListener dismissListener) {
        super(context);
        this.mContext = context;
        this.dismissListener = dismissListener;
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.menu_ww_tools_popwindow_layout, null);

        setContentView(view);
        setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        setHeight(WindowManager.LayoutParams.WRAP_CONTENT);

        alertDialog = AppUtils.getAlertDialog(mContext, context.getResources().getString(R.string.pp_msg_need_register), "登入", "取消", dialogListener);

        bundle = new Bundle();
        sharedPreferences = mContext.getSharedPreferences(AppConfig.SHAREDPREFERENCES_NAME, 0);
        uuidChose = sharedPreferences.getString(AppConfig.PREF_UNIQUE_ID, "");
        // click
        setFocusable(true);
        // outside to hide
        setOutsideTouchable(true);
        // back to dismiss
        setBackgroundDrawable(new BitmapDrawable(context.getResources(), (Bitmap) null));
        setOnDismissListener(dismissListener);

        btnPedometer = (Button) view.findViewById(R.id.btn_1);
        btnDaily = (Button) view.findViewById(R.id.btn_2);
        btnRestaurage = (Button) view.findViewById(R.id.btn_3);
        btnStore = (Button) view.findViewById(R.id.btn_4);

        btnPedometer.setText("計步器");
        btnDaily.setText("寫日記");
        btnRestaurage.setText("附近\n餐廰");
        btnStore.setText("附近\n商店");

        btnPedometer.setOnClickListener(btnListener);
        btnDaily.setOnClickListener(btnListener);
        btnRestaurage.setOnClickListener(btnListener);
        btnStore.setOnClickListener(btnListener);

    }

//    public ToolsPopupWindow(Context context, OnDismissListener dismissListener, String walkId) {
//        super(context);
//        this.mContext = context;
//        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View view = layoutInflater.inflate(R.layout.menu_ww_tools_popwindow_layout, null);
//
//        setContentView(view);
//        setWidth(WindowManager.LayoutParams.MATCH_PARENT);
//        setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
//
//        bundle = new Bundle();
//
//
//        // click
//        setFocusable(true);
//        // outside to hide
//        setOutsideTouchable(true);
//        // back to dismiss
//        setBackgroundDrawable(new BitmapDrawable(context.getResources(), (Bitmap) null));
//        setOnDismissListener(dismissListener);
//
//        btnPedometer = (Button) view.findViewById(R.id.btn_1);
//        btnDaily = (Button) view.findViewById(R.id.btn_2);
//        btnRestaurage = (Button) view.findViewById(R.id.btn_3);
//        btnStore = (Button) view.findViewById(R.id.btn_4);
//
//        btnPedometer.setText("計步器");
//        btnDaily.setText("寫日記");
//        btnRestaurage.setText("附近\n餐廰");
//        btnStore.setText("附近\n商店");
//
//        btnPedometer.setOnClickListener(btnListener);
//        btnDaily.setOnClickListener(btnListener);
//        btnRestaurage.setOnClickListener(btnListener);
//        btnStore.setOnClickListener(btnListener);
//        mWalkId = walkId;
//
//    }

    private View.OnClickListener btnListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent();
            switch (view.getId()) {
                case R.id.btn_1:
                    callSystemTime();
                    append = currentTime + "," +
                            uuidChose + "," +
                            "WalkWayListDetailInfo" + "," +
                            "StepCountMainActivity" + "," +
                            "btnStepCountMainActivity" + "\n";
                    transactionLogSave(append);
                    intent.setClass(mContext, StepCountMainActivity.class);
                    mContext.startActivity(intent);
                    break;
                case R.id.btn_2:
                    isLogin = sharedPreferences.getBoolean(AppConfig.PREF_IS_LOGIN, false);
                    Log.i(TAG,"is Login:" + isLogin);
                    isLogin = true;
                    if (isLogin == true) {
                        callSystemTime();
                        append = currentTime + "," +
                                uuidChose + "," +
                                "WalkWayListDetailInfo" + "," +
                                "DiaryMainActivity" + "," +
                                "btnDiaryMainActivity" + "\n";
                        transactionLogSave(append);
                        intent.setClass(mContext, DiaryMainActivity.class);
                        mContext.startActivity(intent);
                    } else {

                        setOnDismissListener(dismissListener);
                        alertDialog.show();
                    }
                    break;
                case R.id.btn_3:
                    callSystemTime();
                    append = currentTime + "," +
                            uuidChose + "," +
                            "WalkWayListDetailInfo" + "," +
                            "NearRestaurantActivity" + "," +
                            "btnNearRestaurantActivity" + "\n";
                    transactionLogSave(append);
                    intent.setClass(mContext, NearRestaurantActivity.class);
                    mContext.startActivity(intent);
                    break;
                case R.id.btn_4:
                    callSystemTime();
                    append = currentTime + "," +
                            uuidChose + "," +
                            "WalkWayListDetailInfo" + "," +
                            "NearStoreActivity" + "," +
                            "btnNearStoreActivity" + "\n";
                    transactionLogSave(append);
                    intent.setClass(mContext, NearStoreActivity.class);
                    mContext.startActivity(intent);
                    break;
            }
            dismiss();
        }
    };

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

    //抓取系統時間
    private void callSystemTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
        Date curDate = new Date(System.currentTimeMillis()); // 獲取當前時間
        currentTime = formatter.format(curDate);
        Log.i(TAG, "==Time==: " + currentTime);

    }

}
