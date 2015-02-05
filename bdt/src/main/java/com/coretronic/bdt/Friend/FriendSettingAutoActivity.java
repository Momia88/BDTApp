package com.coretronic.bdt.Friend;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.coretronic.bdt.AppConfig;
import com.coretronic.bdt.R;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by poter.hsu on 2014/12/18.
 */
public class FriendSettingAutoActivity extends FragmentActivity {

    private static final String TAG = FriendSettingAutoActivity.class.getSimpleName();
    private SharedPreferences sharedPreferences;
    private RelativeLayout navigationBar;
    private Button btnBack;
    private TextView barTitle;
    private RadioGroup rgAutoInvite;
    private View rightLine;
    private Button btnPopMenu;
    //transactionLog
    private String currentTime;
    private String append;
    private String uuidChose;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.friend_setting_auto);

        sharedPreferences = getSharedPreferences(AppConfig.SHAREDPREFERENCES_NAME, 0);
        uuidChose = sharedPreferences.getString(AppConfig.PREF_UNIQUE_ID, "");
        initView();
    }

    private void initView() {
        // Navigation Bar
        navigationBar = (RelativeLayout) findViewById(R.id.navigationBar_option);
        rightLine = (View) findViewById(R.id.right_line);
        btnPopMenu = (Button) findViewById(R.id.btnPopMenu);
        btnBack = (Button) findViewById(R.id.btnBack);
        barTitle = (TextView) findViewById(R.id.action_bar_title);

        barTitle.setText(getString(R.string.pp_friend));
        rightLine.setVisibility(View.GONE);
        btnPopMenu.setVisibility(View.GONE);
        btnBack.setText(R.string.back);
        btnBack.setOnClickListener(btnListener);

        rgAutoInvite = (RadioGroup)findViewById(R.id.rg_auto_invite);
        if (sharedPreferences.getBoolean(AppConfig.PREF_IS_ENABLED_AUTO_INVITE, false))
            rgAutoInvite.check(R.id.rb_enable_auto_invite);
        else
            rgAutoInvite.check(R.id.rb_disable_auto_invite);
        rgAutoInvite.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_enable_auto_invite:
                        callSystemTime();
                        append = currentTime + "," +
                                uuidChose + "," +
                                "FriendSettingAutoActivity" + "," +
                                "EnableAutoInvite" + "," +
                                "btnEnableAutoInvite" + "\n";
                        transactionLogSave(append);
                        sharedPreferences.edit()
                                .putBoolean(AppConfig.PREF_IS_ENABLED_AUTO_INVITE, true)
                                .putInt(AppConfig.PREF_LAST_LOCAL_PHONE_COUNT, 0)
                                .commit();
                        Log.d(TAG, "auto invite enabled");
                        break;
                    case R.id.rb_disable_auto_invite:
                        callSystemTime();
                        append = currentTime + "," +
                                uuidChose + "," +
                                "FriendSettingAutoActivity" + "," +
                                "DisableAutoInvite" + "," +
                                "btnDisableAutoInvite" + "\n";
                        transactionLogSave(append);
                        sharedPreferences.edit()
                                .putBoolean(AppConfig.PREF_IS_ENABLED_AUTO_INVITE, false)
                                .putInt(AppConfig.PREF_LAST_LOCAL_PHONE_COUNT, 0)
                                .commit();
                        Log.d(TAG, "auto invite disabled");
                        break;
                }
            }
        });
    }

    private View.OnClickListener btnListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btnBack:
                    callSystemTime();
                    append = currentTime + "," +
                            uuidChose + "," +
                            "FriendSettingAutoActivity" + "," +
                            "Person" + "," +
                            "btnBack" + "\n";
                    transactionLogSave(append);
                    finish();
                    break;
            }
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
