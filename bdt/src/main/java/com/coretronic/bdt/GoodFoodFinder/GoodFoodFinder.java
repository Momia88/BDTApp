package com.coretronic.bdt.GoodFoodFinder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.coretronic.bdt.AppConfig;
import com.coretronic.bdt.AppUtils;
import com.coretronic.bdt.DataModule.GoodFoodAnswerModel;
import com.coretronic.bdt.MainAcitvity;
import com.coretronic.bdt.R;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by imying.huang on 2014/12/3.
 */
public class GoodFoodFinder  extends Activity implements
        MediaPlayer.OnBufferingUpdateListener,
        MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnVideoSizeChangedListener,
        MediaPlayer.OnErrorListener,
        MediaPlayer.OnInfoListener,
        MediaPlayer.OnSeekCompleteListener
{

    private Button nextStepBtn = null;
    private String TAG = GoodFoodFinder.class.getSimpleName();
    private Context mContext = null;

    private LinearLayout mainLL = null;
    private BitmapDrawable bgDrawable = null;
    private int currentPosition = 0;

    private int orginalSVWidth = 0;
    private int orginalSVHeight = 0;

    private FrameLayout playerFL = null;
    private FrameLayout.LayoutParams portraitFLParams;


    private int mVideoWidth;
    private int mVideoHeight;
    private TextView loadTV = null;
    private TextView pauseHintTV = null;
    private Boolean isBuffering = true;
    private Boolean isComplete = true;
    private MediaPlayer mMediaPlayer;
    private SurfaceView mPreview;
    private SurfaceHolder holder;
    private String path = AppConfig.GoodFoodFinder_VIDEO_PATH;
    private Bundle extras;
    private static final int LOCAL_AUDIO = 0;
    private static final int STREAM_VIDEO = 1;
    private int mediaType = LOCAL_AUDIO;
    private boolean mIsVideoSizeKnown = false;
    private boolean mIsVideoReadyToBePlayed = false;

    private TextView actionBarTitle;
    private Button actBarBackBtn = null;
    private Button btnPopMenu;
    private LinearLayout linearLayout;
    private Drawable drawable;
    private GoodFoodAnswerModel goodFoodAnswerModel;
    private String answerChose;
    private View left_line, right_line;
    private Button btnplay;

    //transactionLog
    private String currentTime;
    private String append;
    private String uuidChose;
    private SharedPreferences sharedPreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
        Log.i(TAG, "mMediaPlayer/holder:" + mMediaPlayer + "/" + holder);
        setContentView(R.layout.goodfood_finder);
//        Display display = getWindowManager().getDefaultDisplay();
//        int viewwidth = display.getWidth();
//        int viewheight = display.getHeight();
//        if (viewwidth> viewheight){
//            View decorView = getWindow().getDecorView();
//            mPreview = (SurfaceView) findViewById(R.id.surfView);
//            mPreview.setOnClickListener(btnListener);
//            findViewById(R.id.viewlayout).setVisibility(View.GONE);
//            findViewById(R.id.viewLL).setVisibility(View.GONE);
//            hideStatusBar(decorView);
//
//            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
//                    FrameLayout.LayoutParams.MATCH_PARENT,
//                    FrameLayout.LayoutParams.MATCH_PARENT
//            );
//            params.setMargins(0, 0, 0, 0);
//            mPreview.setLayoutParams(params);
//
//        }
//        else{
//
//        }
        btnplay= (Button) findViewById(R.id.btnplay);
        btnplay.setOnClickListener(btnListener);
        sharedPreferences = getSharedPreferences(AppConfig.SHAREDPREFERENCES_NAME, 0);
        uuidChose = sharedPreferences.getString(AppConfig.PREF_UNIQUE_ID, "");

        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnBufferingUpdateListener(this);
        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnVideoSizeChangedListener(this);
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setOnInfoListener(this);
//        mMediaPlayer = MediaPlayer.create(TutorSecondPageActivity.this, Uri.parse(path));
        initView();

        bgDrawable = new BitmapDrawable(getResources(), getResources().openRawResource(R.drawable.goodfoodfinder));
        mainLL.setBackgroundDrawable(bgDrawable);


        holder = mPreview.getHolder();
////        holder = mPreview.destroyDrawingCache();
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        Log.i(TAG, "after setType:holder:" + holder);

    }


    private void initView() {
        Log.i(TAG, "initView");
        mainLL = (LinearLayout ) findViewById(R.id.mainLL);
        nextStepBtn = (Button) findViewById(R.id.nextstep);
        nextStepBtn.setVisibility(View.INVISIBLE);
        nextStepBtn.setOnClickListener(btnListener);

        loadTV = (TextView) findViewById(R.id.loadTV);
        pauseHintTV = (TextView) findViewById(R.id.pauseHintTV);
        mPreview = (SurfaceView) findViewById(R.id.surfView);
        mPreview.setOnClickListener(btnListener);
        portraitFLParams = (FrameLayout.LayoutParams) mPreview.getLayoutParams();

//        mMediaPlayer.setOnBufferingUpdateListener(this);
//        mMediaPlayer.setOnCompletionListener(this);
//        mMediaPlayer.setOnPreparedListener(this);
//        mMediaPlayer.setOnVideoSizeChangedListener(this);
//        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//        mMediaPlayer.setOnInfoListener(this);

        actionBarTitle = (TextView) findViewById(R.id.action_bar_title);
        btnPopMenu = (Button) findViewById(R.id.btnPopMenu);
        actBarBackBtn = (Button) findViewById(R.id.btnBack);
        actionBarTitle.setText("我要找好食");
        actBarBackBtn.setOnClickListener(btnListener);
        btnPopMenu.setVisibility(View.GONE);

        right_line = findViewById(R.id.right_line);
        right_line.setVisibility(View.GONE);

    }


    private View.OnClickListener btnListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.nextstep:
                    Intent intent = new Intent(GoodFoodFinder.this,GoodFoodFinderQuestion.class);
                    startActivity(intent);
                    finish();
                    break;
                case R.id.btnBack:
                    callSystemTime();
                    append = currentTime + "," +
                            uuidChose + "," +
                            "GoodFoodFinder" + "," +
                            "MainActivity" + "," +
                            "btnBack" + "\n";
                    transactionLogSave(append);
                    Intent intentmain = new Intent(GoodFoodFinder.this, MainAcitvity.class);
                    startActivity(intentmain);
                    finish();
                    break;
                case R.id.btnplay:
                    callSystemTime();
                    append = currentTime + "," +
                            uuidChose + "," +
                            "GoodFoodFinder" + "," +
                            "GoodFoodFinder" + "," +
                            "btnGoodFoodFinderVideoPlay" + "\n";
                    transactionLogSave(append);
                    btnplay.setVisibility(View.GONE);
                    holder.addCallback(surfaceHolderCallBack);
                    Log.i(TAG, "after callback");
                    initVideo(mediaType);
                    break;
                case R.id.surfView:

                    Log.i(TAG,"====click===");
                    if( mediaType ==  LOCAL_AUDIO)
                    {
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                        if (mMediaPlayer != null) {
                            if (mMediaPlayer.isPlaying() == true) {
                                currentPosition = mMediaPlayer.getCurrentPosition();
                                mMediaPlayer.pause();
                                pauseHintTV.setText(getString(R.string.tutorial_click_play));
                            } else {
//                            Log.i(TAG, "mMediaPlayer.getDuration():" + mMediaPlayer.getDuration());
//                            Log.i(TAG, "mMediaPlayer.getCurrentPosition():" + mMediaPlayer.getCurrentPosition());
                                mMediaPlayer.start();
                                pauseHintTV.setText("");
                            }
                        }

                    }
                    else if ( mediaType == STREAM_VIDEO) {

                        if (mMediaPlayer != null && isBuffering == false) {
                            if (mMediaPlayer.isPlaying() == true && isBuffering == false) {
                                currentPosition = mMediaPlayer.getCurrentPosition();
                                mMediaPlayer.pause();
                                pauseHintTV.setText(getString(R.string.tutorial_click_play));
                            } else {
//                            Log.i(TAG, "mMediaPlayer.getDuration():" + mMediaPlayer.getDuration());
//                            Log.i(TAG, "mMediaPlayer.getCurrentPosition():" + mMediaPlayer.getCurrentPosition());
                                mMediaPlayer.start();
                                pauseHintTV.setText("");
                            }
                        }

                    }
                    break;
            }
        }

    };


    private void initVideo(int mediaType) {
        doCleanUp();
        try {

            switch (mediaType) {
                case 0:
                    mMediaPlayer = MediaPlayer.create(this, R.raw.findgoodfood);
                    mMediaPlayer.start();
                    mMediaPlayer.setDisplay(holder);
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
//                    mMediaPlayer.setLooping(true);//重複播放
                    mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            Log.i(TAG, "onComplete hit");
                            Intent intent = new Intent(GoodFoodFinder.this,GoodFoodFinderQuestion.class);
                            startActivity(intent);
                            finish();
                        }
                    });
                    break;

                case 1:
//                    mMediaPlayer = MediaPlayer.create(TutorSecondPageActivity.this, Uri.parse(path));
                    mMediaPlayer.setDataSource(mContext, Uri.parse(path));
                    mMediaPlayer.setDisplay(holder);
                    mMediaPlayer.prepareAsync();
//                    mMediaPlayer.setLooping(true);
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "error:" + e.getMessage());
            AppUtils.getAlertDialog(mContext, getString(R.string.tutorial_video_load_error)).show();
        }


    }

//local端影片無法進到onCompletion,rtsp可以
    @Override
    public void onCompletion(MediaPlayer mp) {
//
//        Log.d(TAG, "onCompletion called");
//        loadTV.setText("");
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
////        setOrientationToPortraitThenUnspec();
//        isComplete = true;
//
//        Intent intent = new Intent(GoodFoodFinder.this,GoodFoodFinderQuestion.class);
//        startActivity(intent);
//        finish();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        Log.d(TAG, "onPrepared called");
        mIsVideoReadyToBePlayed = true;
//        if (mIsVideoReadyToBePlayed && mIsVideoSizeKnown) {
        startVideoPlayback();
        isComplete = false;
//        }
    }

    @Override
    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {


        mVideoWidth = mp.getVideoWidth();
        mVideoHeight = mp.getVideoHeight();
        Log.i(TAG, "onVideoSizeChanged mVideoWidth:" + mVideoWidth + "/mVideoHeight:" + mVideoHeight);

        Log.i(TAG, "mIsVideoReadyToBePlayed/mIsVideoSizeKnown:" + mIsVideoReadyToBePlayed + "/" + mIsVideoSizeKnown);
//        if (mIsVideoReadyToBePlayed && mIsVideoSizeKnown) {
//            startVideoPlayback();
//        }
        if (width == 0 || height == 0) {
            Log.e(TAG, "invalid video width(" + width + ") or height(" + height + ")");
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            return;
        } else {
            mIsVideoSizeKnown = true;
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop");
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

    }

    @Override
    protected void onPause() {
        super.onPause();
//        setOrientationToPortraitThenUnspec();


        Log.i(TAG, "onPause");
        if (mMediaPlayer != null) {
            mMediaPlayer.pause();
            currentPosition = mMediaPlayer.getCurrentPosition();
        }

        releaseMediaPlayer();
        doCleanUp();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
        setOrientationToPortraitThenUnspec();
        releaseMediaPlayer();
        doCleanUp();

        if (null != mainLL) {
            mainLL.getBackground().setCallback(null);
        }
        if (null != bgDrawable && !bgDrawable.getBitmap().isRecycled()) {
            bgDrawable.getBitmap().recycle();
        }

    }

    private void releaseMediaPlayer() {
        Log.i(TAG, "releaseMediaPlayer");
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }

        if (holder != null) {
            holder.removeCallback(surfaceHolderCallBack);
            holder = null;
        }
    }

    private void doCleanUp() {
        mVideoWidth = 0;
        mVideoHeight = 0;
        mIsVideoReadyToBePlayed = false;
        mIsVideoSizeKnown = false;
        isBuffering = true;
        if (loadTV != null) {
            loadTV.setText("");
        }

        if (pauseHintTV != null) {
            pauseHintTV.setText("");
        }
    }

    private void startVideoPlayback() {
        Log.v(TAG, "startVideoPlayback");
        Log.v(TAG, "currentPosition:" + currentPosition);
        holder.setFixedSize(mVideoWidth, mVideoHeight);
        mMediaPlayer.start();
        mMediaPlayer.seekTo(currentPosition);

        isComplete = false;
    }


    private void setOrientationToPortraitThenUnspec() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }


    public void hideStatusBar(View view) {

        //Hide the status bar on Android 4.0 and Lower
        if (Build.VERSION.SDK_INT < 16) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);

        } else {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);

        }
    }


    public void showStatusBar(View view) {
        // Show the status bar on Android 4.0 and Lower
        if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);

        }
        //Show the status bar on Android 4.1 and higher
        else {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);

        }

    }




    @Override
    public void onSeekComplete(MediaPlayer mp) {
        Log.i(TAG, "on seek complete");
        isComplete = true;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.i(TAG, "onConfigurationChanged");

        View decorView = getWindow().getDecorView();
        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            findViewById(R.id.viewlayout).setVisibility(View.GONE);
            findViewById(R.id.viewLL).setVisibility(View.GONE);
            hideStatusBar(decorView);

            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
            );
            params.setMargins(0, 0, 0, 0);
            mPreview.setLayoutParams(params);


        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            findViewById(R.id.viewlayout).setVisibility(View.VISIBLE);
            findViewById(R.id.viewLL).setVisibility(View.VISIBLE);
            showStatusBar(decorView);
            mPreview.setLayoutParams(portraitFLParams);
        }
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {

        Log.i(TAG, "on Info what:" + what + "/extra:" + extra);
        if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START) {
            Log.i(TAG, "it is loading");
            loadTV.setText(getString(R.string.video_loading));
            isBuffering = true;
        } else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) {
            Log.i(TAG, "it not loading");
            loadTV.setText("");
        } else if (what == MediaPlayer.MEDIA_INFO_NOT_SEEKABLE) {
            loadTV.setText(getString(R.string.tutorial_video_load_error));
            AppUtils.getAlertDialog(mContext, getString(R.string.video_error_alert)).show();
        }
        isBuffering = false;
        return false;
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.i(TAG, "On Error what:" + what);
        loadTV.setText(getString(R.string.tutorial_video_load_error));
        AppUtils.getAlertDialog(mContext, getString(R.string.video_error_alert)).show();
        return true;
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        Log.d(TAG, "onBufferingUpdate percent:" + percent);

        if (percent == 100) {
            loadTV.setText("");
        } else {
            loadTV.setText(getString(R.string.video_buffering) + percent + "/100)");
            isBuffering = true;
        }
    }


    private SurfaceHolder.Callback surfaceHolderCallBack = new SurfaceHolder.Callback() {

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            Log.i(TAG, "surfaceCreated");
            initVideo(mediaType);
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            Log.d(TAG, "surfaceChanged called");
            Log.i(TAG, " onVideoSizeChanged surfaceChanged mVideoWidth:" + width + "/mVideoHeight:" + height);
            if (orginalSVWidth == 0 && orginalSVHeight == 0) {
                orginalSVWidth = width;
                orginalSVHeight = height;
            }
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {

            Log.d(TAG, "surfaceDestroyed called");
            releaseMediaPlayer();
            doCleanUp();
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
