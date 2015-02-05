package com.coretronic.bdt.GoodFoodFinder;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.coretronic.bdt.*;
import com.coretronic.bdt.DataModule.GoodFoodAnswerModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import org.apache.http.Header;
import org.json.JSONObject;
import com.coretronic.bdt.AppUtils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by imying.huang on 2014/11/27.
 */
public class GoodFoodFinderQuestion extends FragmentActivity {

    private String TAG = GoodFoodFinderQuestion.class.getSimpleName();
    private Button btnAnswer01 = null;
    private Button btnAnswer02 = null;
    private Context mContext;
    private Bundle bundle = null;
    private ProgressDialog dialog = null;
    private TextView actionBarTitle;
    private Button actBarBackBtn = null;
    private Button btnPopMenu;
    private Button return_video;
    private BitmapDrawable bgDrawable;
    private LinearLayout linearLayout;
    private Drawable drawable;
    private int page,pagec ;
    private GoodFoodAnswerModel goodFoodAnswerModel;
    private String answerChose , UUIDChose;
    private View left_line, right_line;
    private Gson gsonPost = new Gson();
    private RequestParams params = null;
    private AsyncHttpClient client = null;
    private SharedPreferences sharedPreferences;
    private TextView questionpage = null;
    //transactionLog
    private String currentTime;
    private String append;

    public JsonHttpResponseHandler jsonHandlerPost = new JsonHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            Log.i(TAG, "onSuccess = " + response);
            dialog.dismiss();

            setContentView(R.layout.goodfood_finder_question_finish);
            linearLayout = (LinearLayout) findViewById(R.id.good_food);
            actionBarTitle = (TextView) findViewById(R.id.action_bar_title);
            actionBarTitle.setText("我要找好食");
            btnPopMenu = (Button) findViewById(R.id.btnPopMenu);
            actBarBackBtn = (Button) findViewById(R.id.btnBack);
            actBarBackBtn.setOnClickListener(onClickListener);
            btnPopMenu.setVisibility(View.GONE);
            actBarBackBtn.setVisibility(View.GONE);
            right_line = (View) findViewById(R.id.right_line);
            right_line.setVisibility(View.GONE);
            left_line = (View) findViewById(R.id.left_line);
            left_line.setVisibility(View.GONE);

            bgDrawable = new BitmapDrawable(getResources(), getResources().openRawResource(R.drawable.goodfoodfinder_pagefinish));
            if (android.os.Build.VERSION.SDK_INT < 16) {
                linearLayout.setBackgroundDrawable(bgDrawable);
            } else {
                linearLayout.setBackground(bgDrawable);
            }

            return_video = (Button) findViewById(R.id.return_video);
            return_video.setOnClickListener(btn_return_video);
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            Log.i(TAG, "onFailure");
            if(dialog != null){
                dialog.dismiss();
            }
            AppUtils.getAlertDialog(mContext, getString(R.string.data_load_error)).show();
        }
    };

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.goodfood_finder_question);
        mContext = this;
        changeFragment(GoodFoodFinderQuestionFragment.newInstance(page));
        TextView text=(TextView)findViewById(R.id.textquestion);
        text.setVisibility(View.GONE);
        goodFoodAnswerModel = new GoodFoodAnswerModel();
        goodFoodAnswerModel.setAnswerListContainer(new ArrayList<GoodFoodAnswerModel.AnswerList>());
        sharedPreferences = getSharedPreferences(AppConfig.SHAREDPREFERENCES_NAME,0);
        UUIDChose =sharedPreferences.getString(AppConfig.PREF_UNIQUE_ID,"");
        Log.i(TAG, "UUID:  " + UUIDChose);
        initView();
    }


    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.btnAnswer01:
                    answerChose = "A01";
                    setAnswer(page);
                    if(page<4){
                        changeFragment(GoodFoodFinderQuestionFragment.newInstance(++page));
                    }
                    else{
                        return_survice();//將值回傳survice
                    }
                    break;
                case R.id.btnAnswer02:
                    answerChose = "A02";
                    setAnswer(page);
                    if(page<4){
                        changeFragment(GoodFoodFinderQuestionFragment.newInstance(++page));
                    }
                    else{
                        return_survice();//將值回傳survice
                    }
                    break;
                case R.id.btnBack:
                    if(page!=0){
                        changeFragment(GoodFoodFinderQuestionFragment.newInstance(--page));
                        removeAboveAnswer(page);
                    }

                    else{
                        callSystemTime();
                        append = currentTime + "," +
                                UUIDChose + "," +
                                "GoodFoodFinderQuestion" + "," +
                                "GoodFoodFinder" + "," +
                                "btnBack" + "\n";
                        transactionLogSave(append);
                        Intent intent = new Intent(GoodFoodFinderQuestion.this,GoodFoodFinder.class);
                        startActivity(intent);
                        finish();
                    }
            }
            if (page<5){
                btnAnswer01.setText(AppConfig.GoodFoodFinderAnswer01[page]);
                btnAnswer02.setText(AppConfig.GoodFoodFinderAnswer02[page]);
            }
            else{
                btnAnswer01.setText("");
                btnAnswer02.setText("");}

            GlobalVariable globalVariable = (GlobalVariable)getApplicationContext();
            globalVariable.goodFoodAnswerModel = goodFoodAnswerModel;
            questionpage.setText("共5題" + "(" + (page+1) + "/" + "5" + ")");
        }
    };

    private void initView() {
        btnAnswer01 = (Button) findViewById(R.id.btnAnswer01);
        btnAnswer02 = (Button) findViewById(R.id.btnAnswer02);
        linearLayout = (LinearLayout) findViewById(R.id.good_food);
        btnAnswer01.setOnClickListener(onClickListener);
        btnAnswer02.setOnClickListener(onClickListener);

        actionBarTitle = (TextView) findViewById(R.id.action_bar_title);
        btnPopMenu = (Button) findViewById(R.id.btnPopMenu);
        actBarBackBtn = (Button) findViewById(R.id.btnBack);
        actionBarTitle.setText("我要找好食");
        actBarBackBtn.setOnClickListener(onClickListener);
        btnPopMenu.setVisibility(View.GONE);
        bgDrawable = new BitmapDrawable(getResources(), getResources().openRawResource(R.drawable.goodfoodfinder));
        if (android.os.Build.VERSION.SDK_INT < 16) {
            linearLayout.setBackgroundDrawable(bgDrawable);
        } else {
            linearLayout.setBackground(bgDrawable);
        }
        right_line = findViewById(R.id.right_line);
        right_line.setVisibility(View.GONE);

        questionpage = (TextView) findViewById(R.id.page);
    }


    private void changeFragment(Fragment f) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.textlayout, f);
        transaction.commitAllowingStateLoss();
    }
    private void setAnswer(int page){

        List<GoodFoodAnswerModel.AnswerList> answerListContainer = goodFoodAnswerModel.getAnswerListContainer();
        GoodFoodAnswerModel.AnswerList answerList = new GoodFoodAnswerModel.AnswerList();
        Log.i(TAG, "answerListContainer: " + answerListContainer.size());
        String questionId = "Q0" + (page+1);

        GoodFoodAnswerModel.Answer answer = new GoodFoodAnswerModel.Answer();
        answer.setQuestionID(questionId);
        answer.setAnswerID(answerChose);

        answerList.setAnswer(answer);
        answerListContainer.add(answerList);
        Log.i(TAG, "answerListContainer: " + answerListContainer.size());
        Log.i(TAG, "getAnswerID: " + goodFoodAnswerModel.getAnswerListContainer().get(page).getAnswer().getAnswerID());
        Log.i(TAG, "getQuestionID: " + goodFoodAnswerModel.getAnswerListContainer().get(page).getAnswer().getQuestionID());
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
    }
    private void removeAboveAnswer(int page) {
        List list = goodFoodAnswerModel.getAnswerListContainer();
        list.remove(page);

    }
    //回傳survice
    private void return_survice() {
        dialog = AppUtils.customProgressDialog(mContext, null, getString(R.string.dialog_read_msg));
        dialog.show();
        goodFoodAnswerModel.setUid(UUIDChose);
        goodFoodAnswerModel.setTime(AppUtils.getChineseSystemTime());

        gsonPost = new GsonBuilder().setPrettyPrinting().create();
        String answerResult = gsonPost.toJson(goodFoodAnswerModel);
        Log.i(TAG, "AnswerResult POST: " + answerResult);


        final String url = AppConfig.DOMAIN_SITE_PATE + AppConfig.INSERT_RESTAURANT_QUESTION;
        params = new RequestParams();
        params.add("result", answerResult);
        Log.i(TAG, "para: " +params);
        client = new AsyncHttpClient();
        client.setTimeout(AppConfig.TIMEOUT);
        client.post(url, params, jsonHandlerPost);


    }

    private View.OnClickListener btn_return_video = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(GoodFoodFinderQuestion.this,GoodFoodFinder.class);
            startActivity(intent);
            finish();
        }
    };

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


