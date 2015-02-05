package com.coretronic.bdt;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;


/***
 * Created by Darren on 2014/12/11
 */
public class WidgetProvider extends AppWidgetProvider {
    private static CharSequence temp;
    private static CharSequence location;
    private static CharSequence rain_rate;
    private static CharSequence stepCount;
    private static CharSequence msgCount;
    private String TAG = WidgetProvider.class.getSimpleName();
    private static final DateFormat df = new SimpleDateFormat("hh:mm");
    private SharedPreferences sharedPreferences;
    private String currentTime, startTime;

//    private String location;
//    private String rain_rate;

    //啟動WidgetService服務對應的action
    private final Intent SERVICE_INTENT = new Intent("android.appwidget.action.EXAMPLE_APP_WIDGET_SERVICE");

    //更新widget的廣播對應的action
    public static String CLOCK_WIDGET_UPDATE = "com.coretronic.bdt.8BITCLOCK_WIDGET_UPDATE";

    public static String WAKE_UPLOAD_COUNT = "UPLOAD_STEP_COUNT";

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
//        Bundle bData = intent.getExtras();

        if (CLOCK_WIDGET_UPDATE.equals(intent.getAction())) {
            // Get the widget manager and ids for this widget provider, then call the shared
            // clock update method.
//            readData();
            sharedPreferences = context.getSharedPreferences(AppConfig.SHAREDPREFERENCES_NAME, 0);
            temp = sharedPreferences.getString(AppConfig.PREF_LOCATION_TEMP, "");
            location = sharedPreferences.getString(AppConfig.PREF_LOCATION_COUNTRY, "");
            rain_rate = sharedPreferences.getString(AppConfig.PREF_LOCATION_RAIN, "");
            stepCount = sharedPreferences.getString(AppConfig.PREF_STEP_KEY_WIDGET, "");
            msgCount = sharedPreferences.getString(AppConfig.PREF_MSG_COUNT, "");

//            Bundle bundle = intent.getExtras();
//            temp = bundle.getString("temp");
//            location = bundle.getString("location");
//            rain_rate = bundle.getString("pop");

//            Log.i(TAG, "broadcast receive");
//            Log.i(TAG, "get temp: " + temp);

            ComponentName thisAppWidget = new ComponentName(context.getPackageName(), getClass().getName());
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int ids[] = appWidgetManager.getAppWidgetIds(thisAppWidget);
            //更新廣播
            for (int appWidgetID: ids) {
                updateAppWidget(context, appWidgetManager, appWidgetID);

            }
        }

        if (CLOCK_WIDGET_UPDATE.equals(intent.getAction())) {

        }
    }

    private PendingIntent createClockTickIntent(Context context) {
        Intent intent = new Intent(CLOCK_WIDGET_UPDATE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        return pendingIntent;
    }

    // 第一個widget被建立時啟用
    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        callSystemTime();
        startTime = currentTime;
        //啟用服務
        context.startService(SERVICE_INTENT);

        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.SECOND, 1);
        alarmManager.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(), 1000, createClockTickIntent(context));
    }

    // 最後一個widget被刪除時啟用
    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        //關閉服務
        context.stopService(SERVICE_INTENT);

        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(createClockTickIntent(context));
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int N = appWidgetIds.length;

        // Perform this loop procedure for each App Widget that belongs to this provider
        for (int i = 0; i < N; i++) {
            int appWidgetId = appWidgetIds[i];

            // Create an Intent to launch ExampleActivity
            Intent intent = new Intent(context, SplashScreen.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,	intent, 0);

            // Get the layout for the App Widget and attach an on-click listener
            // to the button
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
            views.setOnClickPendingIntent(R.id.buttonIntoSplash, pendingIntent);

            // Tell the AppWidgetManager to perform an update on the current app
            // widget
            appWidgetManager.updateAppWidget(appWidgetId, views);

            // Update The clock label using a shared method
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    //更新所有的widget
    public static void updateAppWidget(Context context,	AppWidgetManager appWidgetManager, int appWidgetId) {
        Calendar calendar = Calendar.getInstance();
        Lunar lunar = new Lunar(calendar);

        SimpleDateFormat sdf4 = new SimpleDateFormat("MM/dd");
        SimpleDateFormat sdf5 = new SimpleDateFormat("EEEE");
        SimpleDateFormat sdf6 = new SimpleDateFormat("a");
        String date=sdf4.format(new Date());
        String day=sdf5.format(new Date());
        String td=sdf6.format(new Date());
        String currentTime =  df.format(new Date());

        String dateView = date+" ("+day+") "+"農曆"+lunar.toString();
        String timeZone = td;


        RemoteViews updateViews = new RemoteViews(context.getPackageName(),	R.layout.widget);
        updateViews.setTextViewText(R.id.time, currentTime);
        updateViews.setTextViewText(R.id.timeTV, dateView);
        updateViews.setTextViewText(R.id.zone, timeZone);
        updateViews.setTextViewText(R.id.temp, temp);
        updateViews.setTextViewText(R.id.country, location);
        updateViews.setTextViewText(R.id.rain_rate, "降雨機率"+rain_rate+"%");
        updateViews.setTextViewText(R.id.count_step, stepCount);
        updateViews.setTextViewText(R.id.count_msg, msgCount);

        appWidgetManager.updateAppWidget(appWidgetId, updateViews);
    }

    //抓取系統時間
    private void callSystemTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis()); // 獲取當前時間
        currentTime = formatter.format(curDate);
        Log.i(TAG, "==Time==: " + currentTime);
    }

}