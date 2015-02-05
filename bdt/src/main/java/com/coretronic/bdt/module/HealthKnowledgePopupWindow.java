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
import com.coretronic.bdt.HealthKnowledge.HealthAllArticleListActivity;
import com.coretronic.bdt.HealthKnowledge.HealthArticleFinder;
import com.coretronic.bdt.Person.Register.PersonLoginActivity;
import com.coretronic.bdt.R;
import com.coretronic.bdt.Utility.CustomerTwoBtnAlertDialog;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Morris on 2014/8/13.
 */
public class HealthKnowledgePopupWindow extends PopupWindow {
    private final String TAG = HealthKnowledgePopupWindow.class.getSimpleName();
    private Button allArticleBtn;
    private Button myFavoriteBtn;
    private Button reviewedBtn;
    private Button findArticleBtn;
    private Context mContext;
    private Bundle bundle = null;
    private String stateChange;
    private String all, favor, watched;
    private CustomerTwoBtnAlertDialog alertDialog = null;
    private Intent intent = null;
    private SharedPreferences sharedPreferences;
    private boolean isLogin = false;

    //transactionLog
    private String currentTime;
    private String append;
    private String uuidChose;

    public HealthKnowledgePopupWindow(Context context, OnDismissListener dismissListener) {
        super(context);
        this.mContext = context;

        bundle = new Bundle();
        sharedPreferences = mContext.getSharedPreferences(AppConfig.SHAREDPREFERENCES_NAME, 0);
        uuidChose = sharedPreferences.getString(AppConfig.PREF_UNIQUE_ID, "");
        alertDialog = AppUtils.getAlertDialog(mContext, context.getResources().getString(R.string.pp_msg_need_register), "登入", "取消", dialogListener);
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.menu_health_knowledge_popwindow_layout, null);

        setContentView(view);
        setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        // click
        setFocusable(true);
        // outside to hide
        setOutsideTouchable(true);
        // back to dismiss
        setBackgroundDrawable(new BitmapDrawable(context.getResources(), (Bitmap) null));
        setOnDismissListener(dismissListener);

        allArticleBtn = (Button) view.findViewById(R.id.allArticle);
        myFavoriteBtn = (Button) view.findViewById(R.id.myFavorite);
        reviewedBtn = (Button) view.findViewById(R.id.reviewed);
        findArticleBtn = (Button) view.findViewById(R.id.findArticle);

        allArticleBtn.setOnClickListener(btnListener);
        myFavoriteBtn.setOnClickListener(btnListener);
        reviewedBtn.setOnClickListener(btnListener);
        findArticleBtn.setOnClickListener(btnListener);
    }

    private View.OnClickListener btnListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent();
            switch (view.getId()) {

                // 全部文章
                case R.id.allArticle:
                    callSystemTime();
                    append = currentTime + "," +
                            uuidChose + "," +
                            "HealthKnowledgePopupWindow" + "," +
                            "HealthAllArticleListActivity" + "," +
                            "btnAllArticle" + "\n";
                    transactionLogSave(append);

                    intent.setClass(mContext, HealthAllArticleListActivity.class);
                    bundle.putString(AppConfig.ARTICLE_BUNDLE_KEY, AppConfig.REQUEST_NEWS);
                    bundle.putString("stateChange", "all");
                    intent.putExtras(bundle);
                    mContext.startActivity(intent);
                    break;

                // 我的收藏
                case R.id.myFavorite:
                    callSystemTime();
                    append = currentTime + "," +
                            uuidChose + "," +
                            "HealthKnowledgePopupWindow" + "," +
                            "HealthAllArticleListActivity" + "," +
                            "btnMyFavorite" + "\n";
                    transactionLogSave(append);

//                    Log.i(TAG, "login state: " + sharedPreferences.getBoolean(AppConfig.PREF_IS_LOGIN, false));
                    isLogin = sharedPreferences.getBoolean(AppConfig.PREF_IS_LOGIN, false);
                    Log.i(TAG, "is Login:" + isLogin);
                    if( isLogin == false ) {
                        alertDialog.show();
                    } else {
                        intent.setClass(mContext, HealthAllArticleListActivity.class);
                        bundle.putString(AppConfig.ARTICLE_BUNDLE_KEY, AppConfig.REQUEST_FAVORITES_ARTICLES);
                        bundle.putString("stateChange", "favor");
                        intent.putExtras(bundle);
                        mContext.startActivity(intent);
                    }
                    break;

                // 看過的文章
                case R.id.reviewed:
                    callSystemTime();
                    append = currentTime + "," +
                            uuidChose + "," +
                            "HealthKnowledgePopupWindow" + "," +
                            "HealthAllArticleListActivity" + "," +
                            "btnReviewed" + "\n";
                    transactionLogSave(append);

                    intent.setClass(mContext, HealthAllArticleListActivity.class);
                    bundle.putString(AppConfig.ARTICLE_BUNDLE_KEY, AppConfig.REQUEST_WATCHED_ARTICLES);
                    bundle.putString("stateChange", "watched");
                    intent.putExtras(bundle);
                    mContext.startActivity(intent);
                    break;

                // 找文章
                case R.id.findArticle:
                    callSystemTime();
                    append = currentTime + "," +
                            uuidChose + "," +
                            "HealthKnowledgePopupWindow" + "," +
                            "HealthAllArticleListActivity" + "," +
                            "btnFindArticle" + "\n";
                    transactionLogSave(append);

                    intent.setClass(mContext, HealthArticleFinder.class);
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
