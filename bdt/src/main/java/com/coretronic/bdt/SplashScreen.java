package com.coretronic.bdt;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.*;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.RelativeLayout;
import com.coretronic.bdt.gcm.CommonUtilities;
import com.google.android.gcm.GCMRegistrar;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.coretronic.bdt.gcm.CommonUtilities.DISPLAY_MESSAGE_ACTION;

public class SplashScreen extends Activity {


    private String TAG = SplashScreen.class.getSimpleName();
    // Splash screen timer
    private SharedPreferences sharedPreferences;
    private ProgressDialog dialog = null;
    private Context mContext;
    private AsyncHttpClient client;
    private RequestParams params;
    private DisplayMetrics displayMetrics = null;
    private Boolean isCreated = false;

    // gcm
    // 內部程式取得register ID方式
    // Log.i(TAG,"GET register ID:"+sharedPreferences.getString(AppConfig.REG_ID,""));
    private GoogleCloudMessaging gcm;
    private GcmRegTask gcmRegTask;
    private String regId = "";
    private RelativeLayout splashLayout;
    private BitmapDrawable bgDrawable;

    //upload
    String upLoadServerUri = null;
    String uploadFilePath = "/sdcard/";
    String uploadFileName = "outputLog.txt";
    private String append = null;
    private String uniqueID;
    private String currentTime;

    private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bd_GCM = intent.getExtras();
            if (bd_GCM != null) {
//				String sGCM_Data = "name:" + bd_GCM.get("name").toString()
//						+ "\n";
//				sGCM_Data += "age:" + bd_GCM.get("age").toString() + "\n";
//
//				mTextView.setText(sGCM_Data);
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");
        setContentView(R.layout.activity_splash);
        mContext = this;
        sharedPreferences = getSharedPreferences(AppConfig.SHAREDPREFERENCES_NAME, 0);
        //get version name
        getVersionName();
        splashLayout = (RelativeLayout) findViewById(R.id.splashLayout);
        bgDrawable = new BitmapDrawable(getResources(), getResources().openRawResource(R.raw.launcher_page));
        splashLayout.setBackgroundDrawable(bgDrawable);
        getDisplayInfoSetToPref();
        isCreated = sharedPreferences.getBoolean(AppConfig.PREF_IS_CREATED, false);
//        startNextActivity();
//        upLoadServerUri = "http://houspital.coretronic.com/transactionlog/upload_file.php";
        upLoadServerUri = "http://mongodb.coretronic.com/8daton";
//        upLoadServerUri = "http://192.168.202.7/8daton";
        if (sharedPreferences.getBoolean(AppConfig.PREF_SHORTCUT, false) == false) {
            // add app shorcut on home screen
            AppUtils.addShortcut(mContext);
            sharedPreferences.edit()
                    .putBoolean(AppConfig.PREF_SHORTCUT, true)
                    .apply();
        }
        registerReceiver(mHandleMessageReceiver, new IntentFilter(
                DISPLAY_MESSAGE_ACTION));
    }


    private void getDisplayInfoSetToPref() {
        displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        sharedPreferences.edit()
                .putInt(AppConfig.PREF_SCREEN_HEIGHT, displayMetrics.heightPixels)
                .putInt(AppConfig.PREF_SCREEN_WIDTH, displayMetrics.widthPixels)
                .apply();
        Log.i(TAG, "Screen height:" + displayMetrics.heightPixels);
        Log.i(TAG, "Screen width:" + displayMetrics.widthPixels);
    }

    private void getVersionName(){
        PackageInfo pinfo = null;
        try {
            pinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            int versionNumber = pinfo.versionCode;
            String versionName = pinfo.versionName;
            sharedPreferences.edit()
                    .putString(AppConfig.PREF_APP_VERSION,versionName)
                    .apply();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void runGCM() {
        // gcm
        GCMRegistrar.checkDevice(this);
        GCMRegistrar.checkManifest(this);
        String android_id = Settings.Secure.getString(this.getBaseContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        regId = GCMRegistrar.getRegistrationId(this);

        Log.i("info", "android_id:" + android_id);
        if (gcmRegTask != null) {
            gcmRegTask.cancel(true);
            gcmRegTask = null;
        }
        gcmRegTask = new GcmRegTask();
        gcmRegTask.execute(null, null, null);
    }

    private boolean isLogCreated() {
        return sharedPreferences.getBoolean(AppConfig.PREF_IS_LOG_CREATED, false);
    }

    private void startNextActivity() {
        Intent i;
        if(isLogCreated()) {
            callSystemTime();
            append = currentTime + "," + sharedPreferences.getString(AppConfig.PREF_UNIQUE_ID, null) + ",end,end,end";
            transactionLogSave(append);
            //改檔名變成UUID
            changeName();
            upload();
        } else {

            //初始化txt檔
            append = "time, uid, currentActivity, nextActivity, button"+ "\n";
            transactionLogSave(append);
            sharedPreferences.edit()
                    .putBoolean(AppConfig.PREF_IS_LOG_CREATED, true)
                    .apply();
        }


        if (isCreated){
            i = new Intent(SplashScreen.this, MainAcitvity.class);
        } else {

            i = new Intent(SplashScreen.this, LoginActivity.class);
        }
        // TODO start to main
        i = new Intent(SplashScreen.this, MainAcitvity.class);

        startActivity(i);
        finish();
    }

    // gcm asyncTask
    private class GcmRegTask extends AsyncTask<Void, String, String> {

        @Override
        protected String doInBackground(Void... params) {
            String msg = "";
            try {

                if (gcm == null) {
                    gcm = GoogleCloudMessaging.getInstance(mContext);
                }
                if (regId.equals("")) {
                    regId = gcm.register(CommonUtilities.SENDER_ID);
                    //GCMRegistrar.register(this, CommonUtilities.SENDER_ID);
                    Log.i("info", "reg ID:" + regId);
                } else {
                    Log.i("info", "已經註冊過了 reg ID:" + regId);
                    Log.i("info", "已經註冊過了");
                }
                sharedPreferences.edit()
                        .putString(AppConfig.REG_ID, regId)
                        .apply();

            } catch (IOException e) {
                msg = e.getMessage();
            }
            return msg;
        }

        // 當後台計算結束時，調用 UI thread。
        @Override
        protected void onPostExecute(String msg) {
//            sendRegIdToService();
            Log.i(TAG, " onPostExecute msg:" + msg);
        }

//        private void sendRegIdToService() {
//            final List<NameValuePair> params = new ArrayList<NameValuePair>();
//            final String url = AppConfig.DOMAIN_SITE_PATE + AppConfig.UPDATE_REG_ID;
//            Log.i(TAG,"regId" + sharedPreferences.getString(AppConfig.REG_ID,"") );
//            Log.i(TAG,"uid" + sharedPreferences.getString(AppConfig.PREF_UNIQUE_ID, "") );
//            params.add(new BasicNameValuePair("regId:", sharedPreferences.getString(AppConfig.REG_ID,"")));
//            params.add(new BasicNameValuePair("uid:", sharedPreferences.getString(AppConfig.PREF_UNIQUE_ID, "")));
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    String json = AppUtils.postResponse(url, params);
//                    Log.d(TAG, "json= " + json);
//                }
//            }).start();
//        }

    }

    //寫入記錄到txt
    private void transactionLogSave(String append) {
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

    private void changeName() {
        uniqueID = sharedPreferences.getString(AppConfig.PREF_UNIQUE_ID, null);
        Log.i(TAG, "uniqueID: " + uniqueID);
        File sdcard = Environment.getExternalStorageDirectory();
        File from = new File(sdcard, "outputLog.txt");
        File to = new File(sdcard, uniqueID + ".txt");
        from.renameTo(to);

    }

    private void upload() {
        //Upload uri
        new Thread(new Runnable() {
            public void run() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        Log.i(TAG, "uploading");
                    }
                });
                Log.i("uploadFile Path: ", uploadFilePath + "" + uniqueID + ".txt");
                UpLoadFile(upLoadServerUri, uploadFilePath + uniqueID + ".txt");

            }
        }).start();

    }

    private void UpLoadFile(String uploadUrl, String srcPath) {
        String end = "\r\n";
        String twoHyphens = "--";
        String boundary = "------WebKitFormBoundarywVNyhahkf7RyLA1F";
        try {
            File file = new File(srcPath);

            URL url = new URL(uploadUrl);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setUseCaches(false);
            // 使用POST方法
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Connection", "keep-alive");
            httpURLConnection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            httpURLConnection.setConnectTimeout(10 * 60 * 1000);
            String beginbound = "--" + boundary + end
                    + "Content-Disposition: form-data; name=\"file\"; filename=\"" + srcPath.substring(srcPath.lastIndexOf("/") + 1) + "\"" + end
                    + "Content-Type: multipart/form-data" + end
                    + end;

            String endbound = end + "--" + boundary + end
                    + "Content-Disposition: form-data; name=\"submit\"" + end + end
                    + "Submit" + end
                    + "--" + boundary + "--" + end;

            httpURLConnection.setRequestProperty("Content-Length", String.valueOf(file.length() + beginbound.length() + endbound.length()));

            DataOutputStream out = new DataOutputStream(httpURLConnection.getOutputStream());
            out.writeBytes(beginbound);
            FileInputStream in = new FileInputStream(srcPath);
            byte[] buffer = new byte[1024]; // 1k
            int count = 0;
            int readcount = 0;
            int totalcount = in.available();
            // 讀取文件
            while ((count = in.read(buffer)) != -1) {
                Log.i(TAG, "write count:" + readcount + ",total:" + totalcount);

                out.write(buffer, 0, count);
                out.flush();
                Log.i(TAG, "write count ok");
                readcount += count;
            }
            Log.i(TAG, "fis.close");
            out.writeBytes(endbound);
            //dos.flush();
            Log.i(TAG, "flush ok");
            InputStream is = httpURLConnection.getInputStream();
            InputStreamReader isr = new InputStreamReader(is, "utf-8");
            BufferedReader br = new BufferedReader(isr);
            String result = "";
            String t = br.readLine();
            while (t != null) {
                result += t;
                t = br.readLine();
            }

            Log.i(TAG, "UpLoad result: " + result);
            is.close();
            in.close();
            out.close();

            Log.i(TAG, "====444===");
            deleteFile();
            Log.i(TAG, "====555===");
            append = "time, uid, currentActivity, nextActivity, button"+ "\n";
            transactionLogSave(append);
//            append = "";
//            transactionLogSave(append);
            Log.i(TAG, "====666===");
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("UpLoading", e.getMessage());
        }
    }

    private void deleteFile() {
        File sdcard = Environment.getExternalStorageDirectory();
        File file = new File(sdcard, uniqueID + ".txt");
        sharedPreferences.edit()
                .putBoolean(AppConfig.PREF_IS_LOG_CREATED, true)
                .commit();

        boolean deleted = file.delete();

    }

    //讀取outputLog.txt的內容
    private void readTxt() {
        String filePath = uploadFilePath + uploadFileName;
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), "UTF-8")); // 指定讀取文件的編碼格式，以免出現中文亂碼
            String outputContent = null;
            while ((outputContent = reader.readLine()) != null) {
                Log.i(TAG, "outputLog.txt: " + outputContent);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");

        if (AppUtils.isOnline(mContext)) {
            int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(mContext);
            if (resultCode != ConnectionResult.SUCCESS) {
                sharedPreferences.edit()
                        .putBoolean(AppConfig.PREF_GOOGLE_SERVICE, false)
                        .apply();

                Dialog dialog = GooglePlayServicesUtil.getErrorDialog(resultCode, this, AppConfig.RQS_GooglePlayServices);
                dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        Log.i(TAG, "==============click listener=========");
                        //startNextActivity();
                        //SplashScreen.this.finish();
                        // Get Json from Service
//                        getDivitionInfo();
                        startNextActivity();
                    }
                });
                Log.i(TAG, "show dialog");
                dialog.show();

                // next run onResume
            } else {
                sharedPreferences.edit()
                        .putBoolean(AppConfig.PREF_GOOGLE_SERVICE, true)
                        .apply();

                Log.d(TAG, "On Line");
                // Get Json from Service
//                getDivitionInfo();
                startNextActivity();
                // run GCM
                runGCM();
            }
        } else {
            Log.d(TAG, "Off Line");
            new Thread() {
                public void run() {
                    try {
                        // Thread will sleep for 5 seconds
                        sleep(2 * 1000);
                        startNextActivity();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }
    }

    @Override
    protected void onDestroy() {
        unregisterTask();
        if (null != splashLayout) {
            splashLayout.getBackground().setCallback(null);
        }
        if (null != bgDrawable && !bgDrawable.getBitmap().isRecycled()) {
            bgDrawable.getBitmap().recycle();
        }
        super.onDestroy();
    }

    private void unregisterTask() {
        if (gcmRegTask != null) {
            gcmRegTask.cancel(true);
        }
        unregisterReceiver(mHandleMessageReceiver);
    }

    //抓取系統時間
    private void callSystemTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
        Date curDate = new Date(System.currentTimeMillis()); // 獲取當前時間
        currentTime = formatter.format(curDate);
        Log.i(TAG, "==Time==: " + currentTime);
    }
}