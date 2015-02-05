package com.coretronic.bdt.Tutorial;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import com.coretronic.bdt.AppConfig;
import com.coretronic.bdt.MainAcitvity;
import com.coretronic.bdt.R;
import com.coretronic.bdt.Utility.CustomerTwoBtnAlertDialog;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by james on 2014/8/14.
 */
public class TutorFirstPageActivity extends Activity {

    private String TAG = MainAcitvity.class.getSimpleName();
    private Context mContext;
    private Button skipTutorBtn = null;
    private Button oneMinLearnBtn = null;
    private BitmapDrawable bgDrawable;
    private LinearLayout bgLL = null;
    private Intent intent = null;
    private SharedPreferences sharedPreferences;
    private CustomerTwoBtnAlertDialog exitAlert = null;
    private String currentTime;
    private String append;
    private String UUIDChose;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tutor_firstpage_layout);

        mContext = this;
        intent = this.getIntent();

        initView();
        sharedPreferences = getSharedPreferences(AppConfig.SHAREDPREFERENCES_NAME, 0);
        UUIDChose = sharedPreferences.getString(AppConfig.PREF_UNIQUE_ID, "");
        bgDrawable = new BitmapDrawable(getResources(), getResources().openRawResource(R.raw.launcher_page));
        bgLL.setBackgroundDrawable(bgDrawable);

        skipTutorBtn.setOnClickListener(onClickListener);
        oneMinLearnBtn.setOnClickListener(onClickListener);
    }

    private void initView() {
        skipTutorBtn = (Button) findViewById(R.id.skip_tutor);
        oneMinLearnBtn = (Button) findViewById(R.id.one_min_learn);
        bgLL = (LinearLayout) findViewById(R.id.bgLL);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = null;
            switch (v.getId()) {
                case R.id.one_min_learn:
                    callSystemTime();
                    append = currentTime + "," +
                            UUIDChose + "," +
                            "TutorFirstPageActivity" + "," +
                            "TutorSecondPageActivity" + "," +
                            "btnOneMinLearn" + "\n";
                    transactionLogSave(append);
                    intent = new Intent(TutorFirstPageActivity.this, TutorSecondPageActivity.class);
                    break;
                case R.id.skip_tutor:
                    callSystemTime();
                    append = currentTime + "," +
                            UUIDChose + "," +
                            "TutorFirstPageActivity" + "," +
                            "MainActivity" + "," +
                            "btnSkip" + "\n";
                    transactionLogSave(append);
                    intent = new Intent(TutorFirstPageActivity.this, MainAcitvity.class);
                    break;

            }
            startActivity(intent);
            finish();
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Log.d(TAG, "onKeyDown back");
            Bundle bundle = intent.getExtras();
//            if (bundle != null && (bundle.getString("source").equals("PersonMenu") )) {
            if (sharedPreferences.getBoolean(AppConfig.PREF_FIRST_USED, false) == false) {

                finish();
            } else {

                exitAlert = new CustomerTwoBtnAlertDialog(mContext);
                exitAlert.setMsg(getString(R.string.check_exit))
                        .setPositiveBtnText(getString(R.string.yes))
                        .setNegativeBtnText(getString(R.string.no))
                        .setPositiveListener(exitSureAlertListener)
                        .setNegativeListener(exitNoAlertListener)
                        .show();


                // Dialog style
//                TextView tv = new TextView(mContext);
//                tv.setText(getString(R.string.check_exit));
//                tv.setTextSize(30);
//                tv.setPadding(30, 10, 10, 10);
//                ContextThemeWrapper contextThemeWrapper = new ContextThemeWrapper(mContext, R.style.customAlertDialog);
//                new AlertDialog.Builder(contextThemeWrapper)
//                        .setCustomTitle(tv)
//                        .setPositiveButton(getString(R.string.yes),
//                                new DialogInterface.OnClickListener() {
//                                    public void onClick(DialogInterface dialog, int whichButton) {
//                                        finish();
//                                    }
//                                }
//                        )
//                        .setNegativeButton(getString(R.string.no),
//                                new DialogInterface.OnClickListener() {
//                                    public void onClick(DialogInterface dialog, int whichButton) {
//                                        dialog.dismiss();
//                                    }
//                                }
//                        )
//                        .create()
//                        .show();
            }
        }
        return false;
    }


    private String exitEvent() {
        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();

        return bundle.getString("source");
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onDestroy() {

        if (null != bgLL) {
            bgLL.getBackground().setCallback(null);
        }
        if (null != bgDrawable && !bgDrawable.getBitmap().isRecycled()) {
            bgDrawable.getBitmap().recycle();
        }
        super.onDestroy();
    }

    private View.OnClickListener exitSureAlertListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            callSystemTime();
            append = currentTime + "," +
                    UUIDChose + "," +
                    "TutorFirstPageActivity" + "," +
                    "TutorFirstPageActivity" + "," +
                    "btnExitYes" + "\n";
            transactionLogSave(append);
            finish();
        }

    };

    private View.OnClickListener exitNoAlertListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            callSystemTime();
            append = currentTime + "," +
                    UUIDChose + "," +
                    "TutorFirstPageActivity" + "," +
                    "TutorFirstPageActivity" + "," +
                    "btnExitNo" + "\n";
            transactionLogSave(append);
            exitAlert.dismiss();
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
