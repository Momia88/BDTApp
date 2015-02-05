package com.coretronic.bdt.HealthKnowledge;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.View;
import android.widget.*;
import com.coretronic.bdt.AppConfig;
import com.coretronic.bdt.AppUtils;
import com.coretronic.bdt.DataModule.ArticleDataInfo;
import com.coretronic.bdt.HealthQA.HealthQuestionActivity;
import com.coretronic.bdt.MainAcitvity;
import com.coretronic.bdt.R;
import com.coretronic.bdt.Utility.AnimationUtility;
import com.coretronic.bdt.module.AnimatedGifImageView;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import org.apache.http.Header;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by changyuanyu on 14/10/3.
 */
public class HealthAnswerActivity extends Activity {
    final private String url = AppConfig.DOMAIN_SITE_PATE + AppConfig.REQUEST_RECOMMEND_QUERY_DOCTOR;
    private  Button button_next;
    private PopupWindow menuPopupWindow;

    private int showAnsAnimatePlayingTime = 2000;

    private TextView barTitle;
    private Button btnPopMenu;
    private Button btnBack;
    private Button nextQuesBtn;
    private Button exitBtn;
    private Button detailNextQuesBtn;
    private View rightLine;
    private View leftLine;
//    private LinearLayout animatLL;
    private LinearLayout answerLL;
    private LinearLayout answerDetailLL;
    private TextView matchData;
    private TextView newsTitle = null;
    private TextView newsContent = null;
    private TextView newsAuthor = null;
    private TextView newsDate = null;
    private String shortUrl = "";
    private ImageView newsPhoto = null;
    private Context mContext;
    private RelativeLayout navigationBar;
    private String TAG = HealthArticleFinder.class.getSimpleName();
    private SharedPreferences sharedPreferences;

    private ProgressDialog dialog = null;
    private RequestParams params = null;
    private AsyncHttpClient client = null;
    private Gson gson = new Gson();
//    private AnimationView aniView = null;
    private AnimationUtility animatioinUtility = null;
    private Handler answerCorrectHandler = new Handler();
    private Intent intent = null;
    private Boolean answerBool = false;
    // get question id from pre page
    private String questionId = "";
    private ArticleDataInfo articleDataInfo = null;
    private ImageLoader imageLoader;
    private DisplayImageOptions options = null;
    private ImageLoaderConfiguration config = null;
    private AlertDialog.Builder alertDialog = null;

    private AnimatedGifImageView answerGif;
    //transactionLog
    private String currentTime;
    private String append;
    private String uuidChose;


    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            answerDetailLL.setVisibility(View.VISIBLE);
            answerLL.setVisibility(View.GONE);

            // action bar
            btnPopMenu.setText(getString(R.string.health_news_share));
            btnPopMenu.setVisibility(View.VISIBLE);
            btnBack.setText(getString(R.string.back_tohome));
            btnBack.setVisibility(View.GONE);
            rightLine.setVisibility(View.VISIBLE);
            leftLine.setVisibility(View.GONE);

            requestDetailContent();

            if( runnable != null ) {
                answerCorrectHandler.removeCallbacks(runnable);
            }
        }
    };

    public JsonHttpResponseHandler jsonHandler = new JsonHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            Log.d(TAG, "onSuccess = " + response);

            articleDataInfo = gson.fromJson(response.toString(), ArticleDataInfo.class);
            Log.d(TAG, "articleDataInfo = " + articleDataInfo);

            if (articleDataInfo.getMsgCode().equals(AppConfig.SUCCESS_CODE)) {

                Log.i(TAG, "title:" + articleDataInfo.getResult());
                Log.i(TAG, "title:" + articleDataInfo.getResult().getNewsTitle());
                Log.i(TAG, "newsContent:" + articleDataInfo.getResult().getNewsContent());
                Log.i(TAG, "newsAuthor:" + articleDataInfo.getResult().getAuthor());
                Log.i(TAG, "newsData:" + articleDataInfo.getResult().getTime());
                Log.i(TAG, "getFavorArticle:" + articleDataInfo.getResult().getFavorArticle());
                Log.i(TAG, "shortUrl:" + articleDataInfo.getResult().getShortUrl());


                loadDoctorPhoto(articleDataInfo.getResult().getNewsPhoto());

                if (articleDataInfo.getResult().getShortUrl() != null) {
                    shortUrl = articleDataInfo.getResult().getShortUrl();
                }

                if (articleDataInfo.getResult().getNewsTitle() != null) {
                    newsTitle.setText(AppUtils.ToDBC(articleDataInfo.getResult().getNewsTitle().toString()));
                }


                if (articleDataInfo.getResult().getNewsContent() != null) {
                    newsContent.setText(AppUtils.ToDBC(articleDataInfo.getResult().getNewsContent().toString()));
                }

                if (articleDataInfo.getResult().getNewsContent() != null) {
                    newsAuthor.setText(articleDataInfo.getResult().getAuthor().toString());
                }

                if (articleDataInfo.getResult().getTime() != null) {
                    try {

                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date date = sdf.parse(articleDataInfo.getResult().getTime());

                        sdf = new SimpleDateFormat("yyyy-MM-dd");

                        newsDate.setText(sdf.format(date));
                    } catch (ParseException e) {
                        AppUtils.getAlertDialog(mContext, getString(R.string.data_load_error)).show();
                        dialog.dismiss();
                    }
                }

                if (dialog != null) {
                    dialog.dismiss();
                }

            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            Log.d(TAG, "onFailure");
            if (dialog != null) {
                dialog.dismiss();
            }
            AppUtils.getAlertDialog(mContext, getString(R.string.data_load_error)).show();
        }


    };

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.health_answer);

        Log.i(TAG, "onCreate");
        mContext = this;
        sharedPreferences = getSharedPreferences(AppConfig.SHAREDPREFERENCES_NAME, 0);
        uuidChose =sharedPreferences.getString(AppConfig.PREF_UNIQUE_ID,"");

        initView();
        barTitle.setText(R.string.health_question);

        nextQuesBtn.setVisibility(View.GONE);
        btnPopMenu.setVisibility(View.GONE);
        btnBack.setVisibility(View.GONE);
        rightLine.setVisibility(View.GONE);
        leftLine.setVisibility(View.GONE);


        Bundle bundle = getIntent().getExtras();
        answerBool = bundle.getBoolean("answerTF");
        questionId = bundle.getString("questionId");
        Log.i(TAG, "oncreate answerBool:"+answerBool);
        imageLoader = ImageLoader.getInstance();

        options = new DisplayImageOptions.Builder()
                .build();
        config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .defaultDisplayImageOptions(options)
                .build();
        imageLoader.init(config);


        // answer correct
        if (answerBool == true) {

            Log.i(TAG, "answer correct");

            answerDetailLL.setVisibility(View.GONE);
            answerLL.setVisibility(View.VISIBLE);

//            animatioinUtility = new AnimationUtility(mContext, animatLL, R.drawable.spritsheet_ansright,300,2,3, true);
            answerGif.setAnimatedGif(R.drawable.health_qaanswer_yes, AnimatedGifImageView.TYPE.AS_IS);
//            aniView = new AnimationView(mContext, R.drawable.spritsheet_ansright, 2, 3, false, 300);
//            animatLL.addView(aniView);

            answerCorrectHandler.postDelayed(runnable, showAnsAnimatePlayingTime);
//            aniView = null;
        }
        // answer not correct
        else {
            Log.i(TAG, "answer not correct");
//            btnPopMenu.setVisibility(View.VISIBLE);
//            btnBack.setVisibility(View.VISIBLE);
//            rightLine.setVisibility(View.VISIBLE);
//            leftLine.setVisibility(View.VISIBLE);

//            answerDetailLL.setVisibility(View.VISIBLE);
            nextQuesBtn.setVisibility(View.VISIBLE);
//            answerLL.setVisibility(View.GONE);


//            animatioinUtility = new AnimationUtility(mContext, animatLL, R.drawable.spritsheet_ansfail,300,2,3, true);
            answerGif.setAnimatedGif(R.drawable.health_qaanswer_fail, AnimatedGifImageView.TYPE.AS_IS);
        }
    }


    private void initView() {
        // navigation bar
        navigationBar = (RelativeLayout) findViewById(R.id.navigationBar_option);
        btnBack = (Button) findViewById(R.id.btnBack);
        btnPopMenu = (Button) findViewById(R.id.btnPopMenu);
        btnPopMenu.setOnClickListener(btnListener);
        nextQuesBtn = (Button) findViewById(R.id.health_answeer_next_question_btn);
        nextQuesBtn.setOnClickListener(btnListener);
        exitBtn = (Button) findViewById(R.id.exitBtn);
        exitBtn.setOnClickListener(btnListener);
        detailNextQuesBtn = (Button) findViewById(R.id.health_answer_detail_next_ques_btn);
        detailNextQuesBtn.setOnClickListener(btnListener);
        btnBack.setOnClickListener(btnListener);
        btnPopMenu.setOnClickListener(btnListener);
        barTitle = (TextView) findViewById(R.id.action_bar_title);
        rightLine = (View) findViewById(R.id.right_line);
        leftLine = (View) findViewById(R.id.left_line);
//        animatLL = (LinearLayout) findViewById(R.id.animateLL);
        answerLL = (LinearLayout) findViewById(R.id.answerLL);
        answerDetailLL = (LinearLayout) findViewById(R.id.answerDetailLL);
        newsTitle = (TextView) findViewById(R.id.news_title);
        newsContent = (TextView) findViewById(R.id.news_content);
        newsAuthor = (TextView) findViewById(R.id.news_author);
        newsDate = (TextView) findViewById(R.id.news_data);
        newsPhoto = (ImageView) findViewById(R.id.news_photo);
        answerGif = ((AnimatedGifImageView)findViewById(R.id.ans_animate));

    }


    private void loadDoctorPhoto(String photoPath) {
        Log.i(TAG, "photoPath:" + photoPath);

        if (photoPath != null) {

            Log.i(TAG, "newsPhoto not null");
            newsPhoto.setVisibility(View.VISIBLE);
            imageLoader.displayImage(photoPath, newsPhoto);
            newsPhoto.setVisibility(View.VISIBLE);
        } else {
            Log.i(TAG, "newsPhoto is null");
//            newsPhoto.setImageResource(R.drawable.no_dr_photo);
            newsPhoto.setVisibility(View.GONE);
        }
    }


    private void requestDetailContent() {
            dialog = AppUtils.customProgressDialog(mContext, null, getString(R.string.dialog_read_msg));
            dialog.show();
            final String url = AppConfig.DOMAIN_SITE_PATE + AppConfig.REQUEST_QUESTION_REF;
            params = new RequestParams();
            params.add("uid", sharedPreferences.getString(AppConfig.PREF_UNIQUE_ID, ""));
            params.add("question_id", questionId);
            params.add("time", AppUtils.getChineseSystemTime());
            Log.i(TAG, "params:" + params);
            client = new AsyncHttpClient();
            client.setTimeout(AppConfig.TIMEOUT);
            client.post(url, params, jsonHandler);
    }

    private View.OnClickListener btnListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btnBack:
                    append = currentTime + "," +
                            uuidChose + "," +
                            "HealthAnswerActivity" + "," +
                            "MainActivity" + "," +
                            "btnBack" + "\n";
                    transactionLogSave(append);
                    intent = new Intent(HealthAnswerActivity.this, MainAcitvity.class);
                    startActivity(intent);
                    finish();
                    break;
                case R.id.btnPopMenu:
                    Log.d(TAG, "btnPopMenu");
                    callSystemTime();
                    append = currentTime + "," +
                            uuidChose + "," +
                            "HealthAnswerActivity" + "," +
                            "HealthSharePopupWindow" + "," +
                            "btnShareContent" + "\n";
                    transactionLogSave(append);
                    shareContentToOtherApp();
                    break;
                case R.id.exitBtn:
                    Log.d(TAG, "exitBtn");
                    callSystemTime();
                    append = currentTime + "," +
                            uuidChose + "," +
                            "HealthAnswerActivity" + "," +
                            "HealthAnswerActivity" + "," +
                            "btnExit" + "\n";
                    transactionLogSave(append);
                    showCustomExitAlert();
                    break;
                case R.id.health_answeer_next_question_btn:
                    Log.d(TAG, "btn next question btn");
                    callSystemTime();
                    append = currentTime + "," +
                            uuidChose + "," +
                            "HealthAnswerActivity" + "," +
                            "HealthAnswerActivity" + "," +
                            "btn_next_question" + "\n";
                    transactionLogSave(append);
//                    intent = new Intent(HealthAnswerActivity.this, HealthQuestionActivity.class );
//                    startActivity(intent);
//                    intent = null;
                    finish();
                    break;
                case R.id.health_answer_detail_next_ques_btn:
                    Log.d(TAG, "btn next question btn");
                    callSystemTime();
                    append = currentTime + "," +
                            uuidChose + "," +
                            "HealthAnswerActivity" + "," +
                            "HealthQuestionActivity" + "," +
                            "btn_detail_next_ques" + "\n";
                    transactionLogSave(append);
                    intent = new Intent(HealthAnswerActivity.this, HealthQuestionActivity.class);
                    startActivity(intent);
                    intent = null;
                    break;
            }
        }
    };



    private void shareContentToOtherApp() {

        //        String fileFullPath = Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator + fname;
//        指定開Line App
//        intent = mContext.getPackageManager().getLaunchIntentForPackage(AppConfig.LINE_PACKAGE_NAME);
//        intent.setType("text/plain");
//        intent.putExtra(Intent.EXTRA_TEXT, (getNewsTitle + getString(R.string.data_source) + "【" + getString(R.string.app_name) + "】" + getString(R.string.android_download) + redirectedUrl));



        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        List<ResolveInfo> resInfo =
                getPackageManager().queryIntentActivities(intent, 0);
        if (!resInfo.isEmpty()) {

            List<Intent> targetedShareIntents = new ArrayList<Intent>();

            for (ResolveInfo info : resInfo) {
                Intent targeted = new Intent(Intent.ACTION_SEND);
                targeted.setType("text/plain");
                ActivityInfo activityInfo = info.activityInfo;
                if (activityInfo.packageName.contains("facebook") ||
                        activityInfo.name.contains("facebook")) {
                    continue;
                } else {
                    targeted.putExtra(Intent.EXTRA_TEXT, (newsTitle.getText().toString() + getString(R.string.data_source) + "【" + getString(R.string.app_name) + "】" + getString(R.string.android_download) + shortUrl));
                }
                targeted.setPackage(activityInfo.packageName);
                targetedShareIntents.add(targeted);
            }

            Intent chooserIntent = Intent.createChooser(targetedShareIntents.remove(0), getString(R.string.share));
            if (chooserIntent == null) {
                return;
            }
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetedShareIntents.toArray(
                    new Parcelable[]{}
            ));

            try {
                startActivity(chooserIntent);
            } catch (android.content.ActivityNotFoundException ex) {
//                Toast.makeText(this, getString(R.string.cannt_not_find_app), Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    protected void onDestroy()
    {
        super.onDestroy();
//        animatioinUtility.stopAnimation();
    }


    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause");
        if (menuPopupWindow != null) {
            menuPopupWindow.dismiss();
        }

        if (dialog != null) {
            dialog.dismiss();
        }

        if(runnable != null ) {
            answerCorrectHandler.removeCallbacks(runnable);
        }

//        clearListData();

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            Log.d(TAG, "onKeyDown back");
            Log.d(TAG,"answerBool:"+answerBool);
            // Dialog style
            if (answerBool == false)
            {
                finish();
            } else if (answerBool == true)
            {
                showCustomExitAlert();
            }

            return false;
        }
        return false;
    }


    private void showCustomExitAlert() {
        TextView tv = new TextView(mContext);
        tv.setText(getString(R.string.qacheck_exit));
        tv.setTextSize(30);
        tv.setPadding(30, 10, 10, 10);
        ContextThemeWrapper contextThemeWrapper = new ContextThemeWrapper(mContext, R.style.customAlertDialog);
        alertDialog = new AlertDialog.Builder(contextThemeWrapper);
        alertDialog.setCustomTitle(tv);
        alertDialog.setPositiveButton(getString(R.string.sure_exit),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        intent = new Intent(HealthAnswerActivity.this, MainAcitvity.class);
                        startActivity(intent);
                        finish();
                        intent = null;
                    }
                }
        );
        alertDialog.setNegativeButton(getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                }
        );
        alertDialog.create();
        alertDialog.show();
    }

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