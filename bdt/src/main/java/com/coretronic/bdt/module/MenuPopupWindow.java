package com.coretronic.bdt.module;

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
import com.coretronic.bdt.GoodFoodFinder.GoodFoodFinder;
import com.coretronic.bdt.HealthKnowledge.HealthAllArticleListActivity;
import com.coretronic.bdt.HealthQA.HealthQAMainActivity;
import com.coretronic.bdt.Person.Register.PersonLoginActivity;
import com.coretronic.bdt.R;
import com.coretronic.bdt.Utility.CustomerTwoBtnAlertDialog;
import com.coretronic.bdt.WalkWay.WalkWayActivity;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Morris on 2014/8/13.
 */
public class MenuPopupWindow extends PopupWindow {
    private final String TAG = MenuPopupWindow.class.getSimpleName();
    private Button btnHealthNews;
    private Button btnHealthQA;
    private Button btnFindWalk;
    private Button btnFindFood;
    private Context mContext;
    private Bundle bundle = null;
    private CustomerTwoBtnAlertDialog alertDialog = null;
    private Intent intent = null;
    private boolean isLogin = false;
    private SharedPreferences sharedPreferences;

    //transactionLog
    private String currentTime;
    private String append;
    private String uuidChose;

    public MenuPopupWindow(Context context, OnDismissListener dismissListener) {
        super(context);
        this.mContext = context;
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.menu_popwindow_layout, null);

        sharedPreferences = mContext.getSharedPreferences(AppConfig.SHAREDPREFERENCES_NAME, 0);
        uuidChose = sharedPreferences.getString(AppConfig.PREF_UNIQUE_ID, "");

        setContentView(view);
        setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        setHeight(WindowManager.LayoutParams.WRAP_CONTENT);

        intent = new Intent();
        alertDialog = AppUtils.getAlertDialog(mContext, context.getResources().getString(R.string.pp_msg_need_register), "登入", "取消", dialogListener);

        bundle = new Bundle();


        // click
        setFocusable(true);
        // outside to hide
        setOutsideTouchable(true);
        // back to dismiss
        setBackgroundDrawable(new BitmapDrawable(context.getResources(), (Bitmap) null));
        setOnDismissListener(dismissListener);

        btnHealthNews = (Button) view.findViewById(R.id.btn_1);
        btnHealthQA = (Button) view.findViewById(R.id.btn_2);
        btnFindWalk = (Button) view.findViewById(R.id.btn_3);
        btnFindFood = (Button) view.findViewById(R.id.btn_4);

        btnHealthNews.setText("健康\n心知");
        btnHealthQA.setText("生活\n金頭腦");
        btnFindWalk.setText("我要\n找步道");
        btnFindFood.setText("我要\n找好食");

        btnHealthNews.setOnClickListener(btnListener);
        btnHealthQA.setOnClickListener(btnListener);
        btnFindWalk.setOnClickListener(btnListener);
        btnFindFood.setOnClickListener(btnListener);
    }

    private View.OnClickListener btnListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            switch (view.getId()) {
                case R.id.btn_1:
                    callSystemTime();
                    append = currentTime + "," +
                            uuidChose + "," +
                            "MenuPopupWindow" + "," +
                            "HealthAllArticleListActivity" + "," +
                            "btnHealthNews" + "\n";
                    transactionLogSave(append);

                    intent.setClass(mContext, HealthAllArticleListActivity.class);
                    bundle.putString(AppConfig.ARTICLE_BUNDLE_KEY, AppConfig.REQUEST_NEWS);
                    bundle.putString("stateChange", "all");
                    intent.putExtras(bundle);
                    mContext.startActivity(intent);
                    break;
                case R.id.btn_2:
                    callSystemTime();
                    append = currentTime + "," +
                            uuidChose + "," +
                            "MenuPopupWindow" + "," +
                            "HealthQAMainActivity" + "," +
                            "btnHealthQA" + "\n";
                    transactionLogSave(append);

                    isLogin = sharedPreferences.getBoolean(AppConfig.PREF_IS_LOGIN, false);
                    Log.i(TAG,"is Login:" + isLogin);
                    if( isLogin == false )
                    {
                        alertDialog.show();
                    }
                    else
                    {
                        intent.setClass(mContext, HealthQAMainActivity.class);
                        mContext.startActivity(intent);
                    }

                    break;
                case R.id.btn_3:
                    callSystemTime();
                    append = currentTime + "," +
                            uuidChose + "," +
                            "MenuPopupWindow" + "," +
                            "WalkWayActivity" + "," +
                            "btnFindWalk" + "\n";
                    transactionLogSave(append);

                    intent.setClass(mContext, WalkWayActivity.class);
                    mContext.startActivity(intent);
                    break;
                case R.id.btn_4:
                    callSystemTime();
                    append = currentTime + "," +
                            uuidChose + "," +
                            "MenuPopupWindow" + "," +
                            "GoodFoodFinder" + "," +
                            "btnFindFood" + "\n";
                    transactionLogSave(append);

//                    intent.setClass(mContext, WallActivity.class);
//                    mContext.startActivity(intent);
                    intent.setClass(mContext, GoodFoodFinder.class);
//                    intent.setClass(mContext, WallDiaryDetailActivity.class);
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
