package com.coretronic.bdt.WalkWay.StepCount;

/**
 * Created by darren on 2014/11/19.
 */
import android.app.Service;
import android.content.*;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import com.coretronic.bdt.AppConfig;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.content.IntentFilter;
import android.content.BroadcastReceiver;
import android.content.Intent;

public class StepCountBackService extends Service{

    private Handler serviceHandler = new Handler();
    private String TAG = StepCountBackService.class.getSimpleName();
    private StepListener stepListener;
    private String date, dateKey;
    private SharedPreferences sharedPreferences;
    private String currentTime;
    public static final int SCREEN_OFF_RECEIVER_DELAY = 100;
    private Long startTime;

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "onBind");
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate");
        stepListener = new StepListener(this);
        sharedPreferences = getSharedPreferences(AppConfig.SHAREDPREFERENCES_NAME, 0);
        registerReceiver(mReceiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));
        startTime = System.currentTimeMillis();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        Log.i(TAG, "onStart");
        super.onStart(intent, startId);
        stepListener.start();
        serviceHandler.postDelayed(showTime, 1000);
        serviceHandler.postDelayed(UpdateSteps, 1000);
        serviceHandler.postDelayed(updateTimer, 1000);
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy");
        super.onDestroy();
        serviceHandler.removeCallbacks(showTime);
        serviceHandler.removeCallbacks(UpdateSteps);
        serviceHandler.removeCallbacks(updateTimer);
        stepListener.stop();
        unregisterReceiver(mReceiver);
    }

    private Runnable updateTimer = new Runnable() {
        public void run() {
            Log.i(TAG, "run");
            Long spentTime = System.currentTimeMillis() - startTime;
            //計算目前已過時間
            Long hours = (spentTime/1000)/3600;
            Long minius = (spentTime/1000)/60;
            Long seconds = (spentTime/1000) % 60;

            String totalTime = hours+"時"+minius+"分"+seconds+"秒";
            Log.i(TAG, "totalTime" + totalTime);

            sharedPreferences.edit()
                    .putString(AppConfig.PREF_TIME_KEY, totalTime)
                    .commit();
            serviceHandler.postDelayed(this, 1000);
        }
    };

    //處理時間
    private Runnable showTime = new Runnable() {
        public void run() {
            callSystemTime();
            Log.i(TAG, "run");
            //log目前時間
            Log.i(TAG, currentTime);
            sharedPreferences.edit()
                    .putString(AppConfig.PREF_DATE_KEY, currentTime)
                    .commit();
            serviceHandler.postDelayed(this, 1000);
        }
    };

    //處理步數
    private Runnable UpdateSteps = new Runnable() {
        public void run() {
            // TODO Auto-generated method stub
            // 需要背景做的事
            int step = stepListener.getSteps();
            String stepCount = String.valueOf(step);
            Log.i(TAG, "step: " + stepCount);
            sharedPreferences.edit()
                    .putString(AppConfig.PREF_STEP_KEY, stepCount)
                    .commit();
            serviceHandler.postDelayed(this, 100);
        }
    };


    //抓取系統時間
    private void callSystemTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd\nHH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis()); // 獲取當前時間
        currentTime = formatter.format(curDate);
        Log.i(TAG, "==Time==: " + currentTime);

    }

    public BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "onReceive("+intent+")");

            if (!intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                return;
            }

            Runnable runnable = new Runnable() {
                public void run() {
                    Log.i(TAG, "Runnable executing.");
//                    stepListener.stop();
                    stepListener.start();
                }
            };

            serviceHandler.postDelayed(runnable, SCREEN_OFF_RECEIVER_DELAY);
        }
    };

}
