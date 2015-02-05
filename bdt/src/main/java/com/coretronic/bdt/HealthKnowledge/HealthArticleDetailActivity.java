package com.coretronic.bdt.HealthKnowledge;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.*;
import com.coretronic.bdt.AppConfig;
import com.coretronic.bdt.AppUtils;
import com.coretronic.bdt.DataModule.ArticleDataInfo;
import com.coretronic.bdt.DataModule.ArticleFavorInfo;
import com.coretronic.bdt.NewArticlesListActivity;
import com.coretronic.bdt.Person.NotifyListActivity;
import com.coretronic.bdt.Person.Register.PersonLoginActivity;
import com.coretronic.bdt.R;
import com.coretronic.bdt.Utility.CustomerTwoBtnAlertDialog;
import com.coretronic.bdt.module.HealthSharePopupWindow;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import org.apache.http.Header;
import org.json.JSONException;
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
 * Created by james on 2014/9/12.
 */
public class HealthArticleDetailActivity extends Activity  implements OnClickListener, OnTouchListener, OnCompletionListener, OnBufferingUpdateListener{

    private String TAG = HealthArticleDetailActivity.class.getSimpleName();

    private Button btnBack = null;
    private PopupWindow articleSharePopupWindow;
    private PopupWindow healthAudioPlayPopupWindow;
    private Context mContext;
    private ProgressDialog dialog = null;

    private RelativeLayout navigationBar;
    private Button btnPopMenu = null;
    private ScrollView scrollView;
    private RelativeLayout popupWindowForAudioPlay;

    private RequestParams params = null;
    private AsyncHttpClient client = null;
    private Gson gson = new Gson();
    private ArticleDataInfo articleDataInfo = null;
    private ArticleFavorInfo articleFavorInfo = null;

    private ImageLoader imageLoader;
    private DisplayImageOptions options = null;
    private ImageLoaderConfiguration config = null;

    private ImageView newsPhoto = null;
    private ImageView imaNewStatus = null;
    private TextView newsTitle = null;
    private TextView newsContent = null;
    private TextView newsAuthor = null;
    private TextView newsDate = null;
    private Button favorBtn = null;

    private String getNewsId = "";
    private String getNewsTitle = "";
    private String getFavorArticle = "";
    private String getNewsStatus;
    private SharedPreferences sharedPreferences;
    private String stateFlag;
    private String shortUrl;

    private Button audioPlay;
    private boolean playPause;
    private MediaPlayer mediaPlayer;
    private boolean intialStage = true;
    private Button popupWindowPlay;
    private TextView textViewSpace;
    private ProgressBar progressBarForAudio;
    private SeekBar seekBarProgress;
    private int mediaFileLengthInMilliseconds;
    private final Handler handler = new Handler();
    private Boolean isLogin = false;
    private CustomerTwoBtnAlertDialog askSignInDialog = null;
    private Runnable notification;

    //transactionLog
    private String currentTime;
    private String append;
    private String uuidChose;
    public JsonHttpResponseHandler getNewsDetailHandler = new JsonHttpResponseHandler() {
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

//            setFavorArticleBtnText( articleDataInfo.getFavorArticle() , 0);

                if (articleDataInfo.getResult().getNewsId() != null) {
                    getNewsId = articleDataInfo.getResult().getNewsId();
                }

                loadDoctorPhoto(articleDataInfo.getResult().getNewsPhoto());


                getFavorArticle = articleDataInfo.getResult().getFavorArticle();
                if (getFavorArticle.equals(AppConfig.CANCLE_FAVOR_ARTICLE_ID)) {
                    favorBtn.setText(getString(R.string.favorite_this_article));
                }
                //  取消收藏文章，設置button文字與提示取消收藏成功
                else if (getFavorArticle.equals(AppConfig.FAVOR_ARTICLE_ID)) {

                    favorBtn.setText(getString(R.string.cancel_favorite_article));
                } else {

                    AppUtils.getAlertDialog(mContext, getString(R.string.data_renew_error)).show();
                }

                if (articleDataInfo.getResult().getNewsTitle() != null) {
                    getNewsTitle = AppUtils.ToDBC(articleDataInfo.getResult().getNewsTitle().toString());
                    newsTitle.setText(getNewsTitle);
                }


                if (articleDataInfo.getResult().getNewsContent() != null) {
                    newsContent.setText(AppUtils.ToDBC(articleDataInfo.getResult().getNewsContent().toString()));

                }

                if (articleDataInfo.getResult().getTime() != null) {
                    try {

                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date date = sdf.parse(articleDataInfo.getResult().getTime());

                        sdf = new SimpleDateFormat("yyyy-MM-dd");

                        Log.i(TAG, "year:" + sdf.format(date));
                        newsDate.setText(sdf.format(date));
//                      newsDate.setText(calendar.DAY_OF_YEAR+ calendar.DAY_OF_MONTH+ calendar.DATE);
                    } catch (ParseException e) {
//                        AppUtils.getAlertDialog(mContext, articleDataInfo.getStatus()).show();
//                        dialog.dismiss();
                    }
                }

                if (articleDataInfo.getResult().getAuthor() != null) {

                    newsAuthor.setText(articleDataInfo.getResult().getAuthor());
                }

                if (articleDataInfo.getResult().getShortUrl() != null) {
                    shortUrl = articleDataInfo.getResult().getShortUrl();
                }


                dialog.dismiss();

            } else {
//                AppUtils.getAlertDialog(mContext, articleDataInfo.getStatus()).show();
//                dialog.dismiss();

                //如果回傳是空值
                AppUtils.getAlertDialog(mContext, getString(R.string.data_load_error));
//                Log.e(TAG, divisionInfo.getStatus());
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

        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            super.onFailure(statusCode, headers, responseString, throwable);
            Log.i(TAG, "444");
            Log.i(TAG, "responseString:" + responseString);
            if (dialog != null) {
                dialog.dismiss();
            }
        }

    };

    public JsonHttpResponseHandler getFavorHandler = new JsonHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            Log.d(TAG, "onSuccess = " + response);
            Log.d(TAG, "articleDataInfo.getMsgCode() = " + articleDataInfo.getMsgCode());

            try {
                JSONObject jObject = new JSONObject(String.valueOf(response));
                Log.d(TAG, "articleDataInfo.getResult().getFavorArticle()= " + articleDataInfo.getResult().getFavorArticle());
                if (jObject.getString("msgCode").equals(AppConfig.SUCCESS_CODE)) {

                    // 收藏文章成功，設置button文字與提示收藏成功
                    if (getFavorArticle.equals(AppConfig.CANCLE_FAVOR_ARTICLE_ID)) {

                        AppUtils.getAlertDialog(mContext, getString(R.string.favor_article_ok_alert)).show();
                        getFavorArticle = AppConfig.FAVOR_ARTICLE_ID;
                        articleDataInfo.getResult().setFavorArticle(getFavorArticle);
                        favorBtn.setText(getString(R.string.cancel_favorite_article));
                    }
                    //取消收藏文章，設置button文字與提示取消收藏成功
                    else if (getFavorArticle.equals(AppConfig.FAVOR_ARTICLE_ID)) {

                        getFavorArticle = AppConfig.CANCLE_FAVOR_ARTICLE_ID;
                        articleDataInfo.getResult().setFavorArticle(getFavorArticle);
                        AppUtils.getAlertDialog(mContext, getString(R.string.favor_article_cancel_alert)).show();
                        favorBtn.setText(getString(R.string.favorite_this_article));

                    } else {

                        AppUtils.getAlertDialog(mContext, getString(R.string.data_renew_error)).show();
                    }

                } else {
                    AppUtils.getAlertDialog(mContext, getString(R.string.data_renew_error)).show();
                }
            } catch (JSONException e) {
                AppUtils.getAlertDialog(mContext, getString(R.string.data_renew_error)).show();
            }
            dialog.dismiss();

        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            Log.d(TAG, "onFailure");
            dialog.dismiss();
            AppUtils.getAlertDialog(mContext, getString(R.string.data_load_error)).show();
        }


    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.health_knowledge_content);
        mContext = this;
        articleSharePopupWindow = new HealthSharePopupWindow(mContext, null);
//        healthAudioPlayPopupWindow = new HealthAudioPlayPopupWindow(mContext, null);
        sharedPreferences = getSharedPreferences(AppConfig.SHAREDPREFERENCES_NAME, 0);
        isLogin  = sharedPreferences.getBoolean(AppConfig.PREF_IS_LOGIN, false);
        askSignInDialog = AppUtils.getAlertDialog(mContext, getString(R.string.pp_msg_need_register), getString(R.string.pp_register_title), "取消", askSignInDialogListener);
        uuidChose = sharedPreferences.getString(AppConfig.PREF_UNIQUE_ID, "");
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        ((TextView) findViewById(R.id.action_bar_title)).setText(R.string.health_knowledge);

        initView();

        btnPopMenu.setText(R.string.health_news_share);

        dialog = AppUtils.customProgressDialog(mContext, null, getString(R.string.dialog_read_msg));
        dialog.show();

        imageLoader = ImageLoader.getInstance();

        options = new DisplayImageOptions.Builder()
                .build();
        config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .defaultDisplayImageOptions(options)
                .build();
        imageLoader.init(config);

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        getNewsId = bundle.getString(AppConfig.NEWS_ID_KEY);
//        getNewsStatus = bundle.getString(AppConfig.NEWS_STATUS);
        stateFlag = bundle.getString("stateChange");

        Log.i(TAG, "stateFlag detail:  " + stateFlag);
        Log.i(TAG, "get news ID:" + bundle.getString(AppConfig.NEWS_ID_KEY));

//        //熱們選項
//        if (getNewsStatus.equals("normal")){
//
//            imaNewStatus.setVisibility(View.GONE);
//        }

        requestNewsDetailData();
    }

    // login listener
    private View.OnClickListener askSignInDialogListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Log.i(TAG, "click the dialog sure");
            Intent intent = new Intent();
            intent.setClass(mContext, PersonLoginActivity.class);
            mContext.startActivity(intent);
            askSignInDialog.dismiss();
        }
    };

    private void requestNewsDetailData() {

        final String url = AppConfig.DOMAIN_SITE_PATE + AppConfig.REQUEST_NEW_DETAIL;
        params = new RequestParams();
        params.add("uid", sharedPreferences.getString(AppConfig.PREF_UNIQUE_ID, ""));
        params.add("news_id", getNewsId);
        params.add("time", AppUtils.getChineseSystemTime());
        client = new AsyncHttpClient();
        client.setTimeout(AppConfig.TIMEOUT);
        Log.i(TAG, "POST:" + params);
        client.post(url, params, getNewsDetailHandler);
        params = null;
        client = null;


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

    private void initView() {
        navigationBar = (RelativeLayout) findViewById(R.id.navigationBar_option);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        btnPopMenu = (Button) findViewById(R.id.btnPopMenu);
        btnBack = (Button) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(navBarBtnListener);
        btnPopMenu.setOnClickListener(navBarBtnListener);
        audioPlay = (Button) findViewById(R.id.audio_play);
        audioPlay.setOnClickListener(this);
//        audioPlay.setOnClickListener(pausePlay);
        popupWindowForAudioPlay = (RelativeLayout) findViewById(R.id.relativeLayout_option);
        popupWindowPlay = (Button) findViewById(R.id.audio_pop_play);
        popupWindowPlay.setOnClickListener(this);
        textViewSpace = (TextView) findViewById(R.id.textView_space);
        seekBarProgress = (SeekBar) findViewById(R.id.seekBar_audio);
        seekBarProgress.setMax(99); // It means 100% .0-99
        seekBarProgress.setOnTouchListener(this);
        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.setOnCompletionListener(this);

        newsPhoto = (ImageView) findViewById(R.id.news_photo);
        newsTitle = (TextView) findViewById(R.id.news_title);
        newsContent = (TextView) findViewById(R.id.news_content);
        newsAuthor = (TextView) findViewById(R.id.news_author);
        newsDate = (TextView) findViewById(R.id.news_date);
        favorBtn = (Button) findViewById(R.id.favor_btn);
        favorBtn.setOnClickListener(favorBtnListener);
//        imaNewStatus = (ImageView) findViewById(R.id.news_hot);
    }

    /** Method which updates the SeekBar primary progress by current song playing position*/
    private void primarySeekBarProgressUpdater() {
        // This math construction give a percentage of "was playing"/"song length"
        seekBarProgress.setProgress((int) (((float) mediaPlayer.getCurrentPosition() / mediaFileLengthInMilliseconds) * 100));
        if (mediaPlayer.isPlaying()) {
            notification = new Runnable() {
                public void run() {
                    primarySeekBarProgressUpdater();
                }
            };

            handler.postDelayed(notification,1000);
        }
    }

    @Override
    public void onClick(View v) {
//        if(v.getId() == R.id.audio_play && popupWindowPlay){
            /** Button onClick event handler. Method which start/pause mediaplayer playing */
        callSystemTime();
        append = currentTime + "," +
                uuidChose + "," +
                "HealthArticleDetailActivity" + "," +
                "HealthArticleDetailActivity" + "," +
                "btnAudioPlay" + "\n";
        transactionLogSave(append);
            try {
//                mediaPlayer.setDataSource("http://www.virginmegastore.me/Library/Music/CD_001214/Tracks/Track1.mp3");
//                mediaPlayer.setDataSource("http://mediaserver.coretronic.com/ampache/mp3/news1_voice.mp3");
                mediaPlayer.setDataSource(articleDataInfo.getResult().getRtspurl());
                Log.i(TAG, "mp3 url: " + articleDataInfo.getResult().getRtspurl());

                mediaPlayer.prepare();
            } catch (Exception e) {
                e.printStackTrace();
            }

            mediaFileLengthInMilliseconds = mediaPlayer.getDuration(); // gets the song length in milliseconds from URL
            Log.i(TAG, "song length: " + mediaFileLengthInMilliseconds);

            if(!mediaPlayer.isPlaying()){
                popupWindowForAudioPlay.setVisibility(View.VISIBLE);
                Animation animation = AnimationUtils.loadAnimation(HealthArticleDetailActivity.this, R.anim.dialog_enter);
                popupWindowForAudioPlay.startAnimation(animation);
                textViewSpace.setVisibility(View.VISIBLE);
                mediaPlayer.start();
                audioPlay.setBackgroundResource(R.drawable.btn_h_article_audio_playing);
                popupWindowPlay.setBackgroundResource(R.drawable.btn_h_audio_pause);
            }else {
                mediaPlayer.pause();
                audioPlay.setBackgroundResource(R.drawable.btn_h_article_audio_play);
                popupWindowPlay.setBackgroundResource(R.drawable.btn_h_audio_continue);
            }

            primarySeekBarProgressUpdater();
//        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(v.getId() == R.id.seekBar_audio){
            /** Seekbar onTouch event handler. Method which seeks MediaPlayer to seekBar primary progress position*/
            if(mediaPlayer.isPlaying()){
                SeekBar sb = (SeekBar)v;
                int playPositionInMillisecconds = (mediaFileLengthInMilliseconds / 100) * sb.getProgress();
                mediaPlayer.seekTo(playPositionInMillisecconds);
            }
        }
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        /** MediaPlayer onCompletion event handler. Method which calls then song playing is complete*/
        audioPlay.setBackgroundResource(R.drawable.btn_h_article_audio_play);
        mediaPlayer.stop();
        mediaPlayer.reset();
        textViewSpace.setVisibility(View.GONE);
        popupWindowForAudioPlay.setVisibility(View.GONE);
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        /** Method which updates the SeekBar secondary progress by current song loading from URL position*/
        seekBarProgress.setSecondaryProgress(percent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            keyBackReload();
            return false;
        }
        return false;
    }


//    private View.OnClickListener pausePlay = new View.OnClickListener() {
//
//        @Override
//        public void onClick(View v) {
//            // TODO Auto-generated method stub
//            // TODO Auto-generated method stub
//
////            healthAudioPlayPopupWindow.showAsDropDown(navigationBar, 0, 0);
////            healthAudioPlayPopupWindow.showAtLocation(navigationBar, Gravity.BOTTOM| Gravity.CENTER_HORIZONTAL, 0, 0);
//            popupWindowForAudioPlay.setVisibility(View.VISIBLE);
//            Animation animation = AnimationUtils.loadAnimation(HealthArticleDetailActivity.this, R.anim.dialog_enter);
//            popupWindowForAudioPlay.startAnimation(animation);
//            textViewSpace.setVisibility(View.VISIBLE);
//
////            Log.i(TAG, "total time: " + mediaPlayer.getDuration());
////            Log.i(TAG, "Current time: " + mediaPlayer.getCurrentPosition());
//
//
//            if (!playPause) {
//                audioPlay.setBackgroundResource(R.drawable.btn_h_article_audio_playing);
//                popupWindowPlay.setBackgroundResource(R.drawable.btn_h_audio_pause);
//                if (intialStage)
//                    new Player()
//                            .execute("http://www.virginmegastore.me/Library/Music/CD_001214/Tracks/Track1.mp3");
//                else {
//                    if (!mediaPlayer.isPlaying())
//
//                    mediaPlayer.start();
//
//                }
//                playPause = true;
//            } else {
//                audioPlay.setBackgroundResource(R.drawable.btn_h_article_audio_play);
//                popupWindowPlay.setBackgroundResource(R.drawable.btn_h_audio_continue);
//                if (mediaPlayer.isPlaying())
//                    mediaPlayer.pause();
//                playPause = false;
//            }
//        }
//    };

//    class Player extends AsyncTask<String, Void, Boolean> {
//        private ProgressDialog progress;
//
//        @Override
//        protected Boolean doInBackground(String... params) {
//            // TODO Auto-generated method stub
//            Boolean prepared;
//            try {
//
//                mediaPlayer.setDataSource(params[0]);
//
//                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//
//                    @Override
//                    public void onCompletion(MediaPlayer mp) {
//                        // TODO Auto-generated method stub
//                        intialStage = true;
//                        playPause = false;
//                        audioPlay.setBackgroundResource(R.drawable.btn_h_article_audio_play);
//                        mediaPlayer.stop();
//                        mediaPlayer.reset();
//                        textViewSpace.setVisibility(View.GONE);
//                        popupWindowForAudioPlay.setVisibility(View.GONE);
//                    }
//                });
//                mediaPlayer.prepare();
//                prepared = true;
//            } catch (IllegalArgumentException e) {
//                // TODO Auto-generated catch block
//                Log.d("IllegarArgument", e.getMessage());
//                prepared = false;
//                e.printStackTrace();
//            } catch (SecurityException e) {
//                // TODO Auto-generated catch block
//                prepared = false;
//                e.printStackTrace();
//            } catch (IllegalStateException e) {
//                // TODO Auto-generated catch block
//                prepared = false;
//                e.printStackTrace();
//            } catch (IOException e) {
//                // TODO Auto-generated catch block
//                prepared = false;
//                e.printStackTrace();
//            }
//            return prepared;
//        }
//
//        @Override
//        protected void onPostExecute(Boolean result) {
//            // TODO Auto-generated method stub
//            super.onPostExecute(result);
//            if (progress.isShowing()) {
//                progress.cancel();
//            }
//            Log.d("Prepared", "//" + result);
//            mediaPlayer.start();
//
//            intialStage = false;
//        }
//
//        public Player() {
//            progress = new ProgressDialog(HealthArticleDetailActivity.this);
//        }
//
//        @Override
//        protected void onPreExecute() {
//            // TODO Auto-generated method stub
//            super.onPreExecute();
//            this.progress.setMessage("Buffering...");
//            this.progress.show();
//
//        }
//    }



    private View.OnClickListener navBarBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btnBack:
                    callSystemTime();
                    append = currentTime + "," +
                            uuidChose + "," +
                            "HealthArticleDetailActivity" + "," +
                            "HealthAllArticleListActivity" + "," +
                            "btnBack" + "\n";
                    transactionLogSave(append);

                    if(stateFlag.equals(NotifyListActivity.class.getSimpleName())){
                        finish();
                    }else {
                        keyBackReload();
                    }
                    break;
                case R.id.btnPopMenu:
                    callSystemTime();
                    append = currentTime + "," +
                            uuidChose + "," +
                            "HealthArticleDetailActivity" + "," +
                            "PopMenuShareContent" + "," +
                            "btnShareContent" + "\n";
                    transactionLogSave(append);
                    Log.d(TAG, "btnPopMenu");
                    shareContentToOtherApp();
                    break;
            }
        }
    };

    private void keyBackReload() {
        handler.removeCallbacks(notification);

//        Bundle bundle = new Bundle();
//        Intent intent = new Intent(HealthArticleDetailActivity.this, HealthAllArticleListActivity.class);
//        if (stateFlag == null) {
//            Log.i(TAG, "stateFlag == null");
//            stateFlag = "all";
//        }
//        ;
//        if (stateFlag.equals("all")) {
//            Log.i(TAG, "==all==");
//            bundle.putString(AppConfig.ARTICLE_BUNDLE_KEY, AppConfig.REQUEST_NEWS);
//        }
//        if (stateFlag.equals("favor")) {
//            Log.i(TAG, "==favor==");
//            bundle.putString(AppConfig.ARTICLE_BUNDLE_KEY, AppConfig.REQUEST_FAVORITES_ARTICLES);
//        }
//        if (stateFlag.equals("watched")) {
//            Log.i(TAG, "==watched==");
//            bundle.putString(AppConfig.ARTICLE_BUNDLE_KEY, AppConfig.REQUEST_WATCHED_ARTICLES);
//        }
//        if( stateFlag.equals("NewArticlesListActivity"))
//        {
//            intent.setClass(this,NewArticlesListActivity.class);
//            startActivity(intent);
//            HealthArticleDetailActivity.this.finish();
//            return;
//        }
//        bundle.putString("stateChange", stateFlag);
//
//        intent.putExtras(bundle);
//        mContext.startActivity(intent);
        HealthArticleDetailActivity.this.finish();
    }


    private View.OnClickListener favorBtnListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (isLogin == false){
                askSignInDialog.show();
            } else {
                dialog = AppUtils.customProgressDialog(mContext, null, getString(R.string.dialog_read_msg));
                dialog.show();
                setFavorArticleData();
            }
        }
    };


    private void shareContentToOtherApp() {

        String redirectedUrl = this.shortUrl;
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
                    targeted.putExtra(Intent.EXTRA_TEXT, (getNewsTitle + getString(R.string.data_source) + "【" + getString(R.string.app_name) + "】" + getString(R.string.android_download) + redirectedUrl));
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


    private void setFavorArticleData() {
        Log.i(TAG, "getNewsId:" + getNewsId);
        if (!getNewsId.equals("")) {

            Log.i(TAG, "set favor article ID:" + getNewsId);
            Log.i(TAG, "getFavorArticle:" + getFavorArticle);

            String setFavParams = "";
            if (getFavorArticle.equals(AppConfig.CANCLE_FAVOR_ARTICLE_ID)) {
                setFavParams = AppConfig.FAVOR_ARTICLE_ID;
                callSystemTime();
                append = currentTime + "," +
                        uuidChose + "," +
                        "HealthArticleDetailActivity" + "," +
                        "HealthArticleDetailActivity" + "," +
                        "btnFavor" + "\n";
                transactionLogSave(append);
            } else if (getFavorArticle.equals(AppConfig.FAVOR_ARTICLE_ID)) {
                setFavParams = AppConfig.CANCLE_FAVOR_ARTICLE_ID;
                callSystemTime();
                append = currentTime + "," +
                        uuidChose + "," +
                        "HealthArticleDetailActivity" + "," +
                        "HealthArticleDetailActivity" + "," +
                        "btnFavorCancel" + "\n";
                transactionLogSave(append);
            }

            final String url = AppConfig.DOMAIN_SITE_PATE + AppConfig.UPDATE_FAVOR_ARTICLE;
            params = new RequestParams();
            params.add("uid", sharedPreferences.getString(AppConfig.PREF_UNIQUE_ID, ""));
//            params.add("uid", "U0000000000000000020");
            params.add("news_id", getNewsId);
            params.add("favor_article", setFavParams);
            params.add("time", AppUtils.getChineseSystemTime());
            client = new AsyncHttpClient();
            client.setTimeout(AppConfig.TIMEOUT);
            Log.i(TAG, "params:" + params);
            client.post(url, params, getFavorHandler);
            params = null;
            client = null;


        } else {
            AppUtils.getAlertDialog(mContext, getString(R.string.favor_fail_alert)).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mediaPlayer == null){
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer != null) {
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
            textViewSpace.setVisibility(View.GONE);
            popupWindowForAudioPlay.setVisibility(View.GONE);
        }

        if (notification != null) {
            handler.removeCallbacks(notification);
        }

        if (articleSharePopupWindow != null) {
            articleSharePopupWindow.dismiss();
        }

        if (dialog != null) {
            dialog.dismiss();
        }
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
