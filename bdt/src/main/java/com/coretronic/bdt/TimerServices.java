package com.coretronic.bdt;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import java.util.concurrent.TimeUnit;

/**
 * Created by james on 2014/9/29.
 */
public class TimerServices extends Service {
    private String TAG = TimerServices.class.getSimpleName();
    private final static int NOTIFICATION_ID = 0;
    public final static String TIMER_ACTION = "com.example.ServiceTest.TimerServices";


    private SharedPreferences sharedPreferences;


    //    private Timer timer;
//    private int sec;
    private NotificationManager ntfMgr;

    private long startTime = 0;
    private Handler cleanHandler ;
    private Handler hungryHandler ;

    @Override
    // 第一次啟動Service時會呼叫onCreate()
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate");

        sharedPreferences = getSharedPreferences(AppConfig.SHAREDPREFERENCES_NAME, 0);
        ntfMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        hungryHandler = new Handler();
        cleanHandler = new Handler();

        //取得目前時間
        startTime = System.currentTimeMillis();

        //設定Delay的時間
        hungryHandler.postDelayed(hungryTimerRunnable, AppConfig.CHECK_TIME);
        cleanHandler.postDelayed(cleanTimerRunnable, AppConfig.CHECK_TIME);


    }

    @Override
    // 以startService()方式啟動會呼叫onStartCommand()
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.i(TAG, "onStartCommand");


//        // 取得之前使用者輸入的秒數
//        Bundle bundle = intent.getExtras();
//        sec = bundle.getInt("sec");
//
//        // 建立Timer，並將使用者輸入的秒數設定成要延遲啟動的時間；
//        // 時間一到便會執行TimerTask的run()內容— 傳送指定的Broadcast
//        TimerTask task = new TimerTask() {
//            @Override
//            public void run() {
//                sendBroadcast(new Intent(TIMER_ACTION));
//            }
//        };
//        timer = new Timer();
//        timer.schedule(task, sec * 1000);
//
//        // 建立NotificationManager並呼叫showNotification()發送通知訊息
//        ntfMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//        showNotification();

        // 回傳START_STICKY可以保證再次建立新的Service時仍會呼叫onStartCommand()
        return START_STICKY;
    }


    private Runnable cleanTimerRunnable = new Runnable() {
        public void run() {

            /*
            long spentTime = System.currentTimeMillis() - startTime;
//            //計算目前已過分鐘數
//            long hours = (spentTime / 1000) / 60 / 60;
//            //計算目前已過分鐘數
//            long minius = (spentTime / 1000) / 60;
//            //計算目前已過秒數
//            long seconds = (spentTime / 1000) % 60;


            long utSeconds = TimeUnit.MILLISECONDS.toSeconds(spentTime) -
                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(spentTime));
            long utMin = TimeUnit.MILLISECONDS.toMinutes(spentTime) -
                    TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(spentTime));
            long utHours = TimeUnit.MILLISECONDS.toHours(spentTime);
            Log.i(TAG,"spentTime:"+spentTime);
//            Log.i(TAG,"dure time:"+hours+"/" +minius+"/"+ seconds);
            Log.i(TAG,"dure time:"+utHours+"/" +utMin+"/"+ utSeconds);
            String hms = String.format("%02d:%02d:%02d",utHours,utMin,utSeconds);

            Log.i(TAG, "hms:"+ hms);
            */
            cleanHandler.postDelayed( this, AppConfig.CHECK_TIME );


            long reocredEatTimer = sharedPreferences.getLong(AppConfig.CLEAN_TIME_KEY, 0);
            Log.i(TAG,"clean reocredEatTimer:"+reocredEatTimer +"/ status:"+ sharedPreferences.getBoolean(AppConfig.CLEAN_STATUS_KEY,false));
            long spendTime = System.currentTimeMillis() - reocredEatTimer ;
            long spendSeconds = TimeUnit.MILLISECONDS.toSeconds(spendTime) -
                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(spendTime));
            long spendMin = TimeUnit.MILLISECONDS.toMinutes(spendTime) -
                    TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(spendTime));
            long spendHour = TimeUnit.MILLISECONDS.toHours(spendTime);

            Log.i(TAG,"clean spendSecond:"+spendSeconds);


            // debug
//            if( spendSeconds >= 50 )
            if( spendHour >= 2 )
//            if( spendHour >= AppConfig.CLEAN_TIME )
            {
                Log.i(TAG,"clean utMin>" + AppConfig.CLEAN_TIME);

                // set will clean body_secret paramster
                sharedPreferences.edit()
                        .putBoolean(AppConfig.CLEAN_STATUS_KEY, true)
                        .commit();
                showNotification();
                sendBroadcast(new Intent(TIMER_ACTION));

//               if (cleanHandler != null) {
//                    cleanHandler.removeCallbacks(cleanTimer);
//                    cleanHandler = null;
//                }
//                Log.i(TAG, "===cleanHandler====:"+cleanHandler);
            }

        }
    };


    private Runnable hungryTimerRunnable = new Runnable() {
        public void run() {

            hungryHandler.postDelayed( this, AppConfig.CHECK_TIME );

            long reocredHungryTime = sharedPreferences.getLong(AppConfig.EAT_TIME_KEY, 0);
            Log.i(TAG,"reocredHungryTimer:"+reocredHungryTime +"/ status:"+ sharedPreferences.getBoolean(AppConfig.EAT_STATUS_KEY,false));
            long spendTime = System.currentTimeMillis() - reocredHungryTime ;
            long spendSeconds = TimeUnit.MILLISECONDS.toSeconds(spendTime) -
                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(spendTime));
            long spendMin = TimeUnit.MILLISECONDS.toMinutes(spendTime) -
                    TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(spendTime));
            long spendHour = TimeUnit.MILLISECONDS.toHours(spendTime);

            Log.i(TAG,"spendSecond:"+spendSeconds);


            // debug version
//            if( spendSeconds >= 30 )
            if( spendHour >= 1 )
//            if( spendHour >= AppConfig.HUNGRY_TIME )
            {
                Log.i(TAG,"utMin>" + AppConfig.HUNGRY_TIME);

                // set will clean body_secret paramster
                sharedPreferences.edit()
                        .putBoolean(AppConfig.EAT_STATUS_KEY, true)
                        .commit();
                showNotification();
                sendBroadcast(new Intent(TIMER_ACTION));

            }

        }
    };


//    public void clearHandler()
//    {
//        Log.i(TAG, "remove Handler method/cleanHandler:"+cleanHandler);
//        if( cleanHandler != null ) {
//            Log.i(TAG, "cleanHandler != null");
//            cleanHandler.removeCallbacks(cleanTimerRunnable);
////            cleanTimerRunnable = null;
//            cleanHandler = null;
//        }
//    }


    private void showNotification() {
        Intent intent = new Intent(this, SplashScreen.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        String alertContent = "";
        // 餓、乾淨
        if ( (sharedPreferences.getBoolean(AppConfig.EAT_STATUS_KEY, false) == true) &&
                (sharedPreferences.getBoolean(AppConfig.CLEAN_STATUS_KEY, false) == false) )
        {
            alertContent = getString(R.string.game_hungry_alert_content);
        }
        // 不餓、骯髒
        else if ( (sharedPreferences.getBoolean(AppConfig.EAT_STATUS_KEY, false) == false) &&
                (sharedPreferences.getBoolean(AppConfig.CLEAN_STATUS_KEY, false) == true) )
        {
            alertContent = getString(R.string.game_clean_alert_content);
        }
        // 餓、骯髒
        else if ( (sharedPreferences.getBoolean(AppConfig.EAT_STATUS_KEY, false) == true) &&
                (sharedPreferences.getBoolean(AppConfig.CLEAN_STATUS_KEY, false) == true) )
        {
            alertContent = getString(R.string.game_clean_and_hungry_alert_content);
        }

        Notification notification = new Notification.Builder(this)
                .setTicker(getString(R.string.app_name))
                .setContentTitle(getString(R.string.game_alert_title))
                .setContentText(alertContent)
                .setSmallIcon(android.R.drawable.ic_menu_info_details)
                .setAutoCancel(true).setContentIntent(pendingIntent)
                .getNotification();
        ntfMgr.notify(NOTIFICATION_ID, notification);
    }


    @Override
    // Service準備結束時會呼叫此方法
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
//        // 停止Timer與其指定的工作排程
//        timer.cancel();
//
//        // 取消之前在狀態列上顯示的訊息
        ntfMgr.cancelAll();
//
//        Toast.makeText(this, R.string.msg_serviceOver, Toast.LENGTH_SHORT)
//                .show();
    }

    public void cancleNtf()
    {
        ntfMgr.cancelAll();
    }

    @Override
    // onBind()將於ServiceBindEx範例說明
    public IBinder onBind(Intent intent) {
        return null;
    }

}