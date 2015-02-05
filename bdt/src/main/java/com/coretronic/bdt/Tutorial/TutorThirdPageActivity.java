package com.coretronic.bdt.Tutorial;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.coretronic.bdt.AppConfig;
import com.coretronic.bdt.AppUtils;
import com.coretronic.bdt.MainAcitvity;
import com.coretronic.bdt.R;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by james on 2014/8/14.
 */
public class TutorThirdPageActivity extends Activity {
    private LinearLayout tutorThirdLL = null;
    private BitmapDrawable bgDrawable = null;
    private Button nextStepBtn = null;
    private String TAG = TutorThirdPageActivity.class.getSimpleName();
    private ImageView tutorTurtle = null;


    // content
    private Context mContext = null;
    private int screenWidth = 0;
    private int screenHeight = 0;

    private SharedPreferences sharedPreferences;
    private String currentTime;
    private String append;
    private String UUIDChose;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tutor_thirdpage_layout);
        mContext = this;

        screenWidth = AppUtils.getScreenWidth(mContext);
        screenHeight = AppUtils.getScreenHeight(mContext);

        initView();
        sharedPreferences = getSharedPreferences(AppConfig.SHAREDPREFERENCES_NAME, 0);
        UUIDChose = sharedPreferences.getString(AppConfig.PREF_UNIQUE_ID, "");

        bgDrawable = new BitmapDrawable(getResources(), getResources().openRawResource(R.drawable.tutor_third_page));
        tutorThirdLL.setBackgroundDrawable(bgDrawable);

        Bitmap tempBM = AppUtils.decodeBitmapFromResource(getResources(), R.drawable.tutor_turtle, (int) (screenWidth / 2), (int) (screenHeight / 2));
        tutorTurtle.setImageBitmap(tempBM);

        nextStepBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callSystemTime();
                append = currentTime + "," +
                        UUIDChose + "," +
                        "TutorThirdPageActivity" + "," +
                        "MainActivity" + "," +
                        "btnNextStep" + "\n";
                transactionLogSave(append);
                Intent intent = new Intent(TutorThirdPageActivity.this, MainAcitvity.class);
                startActivity(intent);
                finish();
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");

        if (null != tutorThirdLL) {
            tutorThirdLL.getBackground().setCallback(null);
        }
        if (null != bgDrawable && !bgDrawable.getBitmap().isRecycled()) {
            bgDrawable.getBitmap().recycle();
        }

    }



    private void initView()
    {
        nextStepBtn = (Button)findViewById(R.id.nextstep);
        tutorThirdLL = (LinearLayout) findViewById(R.id.tutorThirdLL);
        tutorTurtle = (ImageView) findViewById(R.id.tutor_turtle);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Log.d(TAG, "onKeyDown back");
            Intent intent = new Intent(TutorThirdPageActivity.this, TutorSecondPageActivity.class);
            startActivity(intent);
            finish();

        }
        return false;
    }

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
