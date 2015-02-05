package com.coretronic.bdt.WalkWay.Diary;

import android.app.*;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import com.coretronic.bdt.AppConfig;
import com.coretronic.bdt.AppUtils;
import com.coretronic.bdt.R;
import com.coretronic.bdt.Utility.CustomerTwoBtnAlertDialog;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DiaryMainActivity extends Activity {
    private final String TAG = DiaryMainActivity.class.getSimpleName();
    private LinearLayout diaryMainLayout = null;
    private BitmapDrawable bgDrawable = null;
    private Context mContext = null;
    private FragmentManager fragmentManager = null;
    private FragmentTransaction fragmentTransaction = null;
    private Fragment fragment = null;
    private SharedPreferences sharedPreferences;
    private CustomerTwoBtnAlertDialog askDraftContinueAlertDialog = null;
    private Bundle bundle= null;
    private String currentWalkWayName = "八達通步道";
    private String currentWalkWayID = "";
    private int targetFragment = AppConfig.PREF_DIARYSTEP1_STATUS_ID;
    //transactionLog
    private String currentTime;
    private String append;
    private String UUIDChose;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.diary_main_layout);

        mContext = this;

        diaryMainLayout = (LinearLayout) findViewById(R.id.diary_main_layout);
        bgDrawable = new BitmapDrawable(getResources(), getResources().openRawResource(R.raw.bg_main));
        bgDrawable.setAlpha(150);
        if (android.os.Build.VERSION.SDK_INT < 16) {
            diaryMainLayout.setBackgroundDrawable(bgDrawable);
        } else {
            diaryMainLayout.setBackground(bgDrawable);
        }





        // check it will show fragment name
        sharedPreferences = getSharedPreferences(AppConfig.SHAREDPREFERENCES_NAME, 0);
        currentWalkWayName = sharedPreferences.getString(AppConfig.WALKWAYS_CURRENT_WALKWAYNAME, "");
        currentWalkWayID = sharedPreferences.getString(AppConfig.WALKWAYS_CURRENT_WALKWAYID, "");
        Log.i(TAG,"currentWalkWayName:"+currentWalkWayName);
        Log.i(TAG,"currentWalkWayID:"+currentWalkWayID);
        targetFragment = sharedPreferences.getInt(AppConfig.PREF_DIARY_CURRENT_STATUS_KEY, AppConfig.PREF_DIARYSTEP1_STATUS_ID );
//        lastEditWalkWay = sharedPreferences.getString(AppConfig.PREF_DIARY_CURRENT_STATUS_KEY, "" );
        UUIDChose = sharedPreferences.getString(AppConfig.PREF_UNIQUE_ID, "");


        String preWalkWayName = sharedPreferences.getString(AppConfig.PREF_DIARY_PRE_WALKWAY_NAME_KEY, "");
        askDraftContinueAlertDialog = AppUtils.getAlertDialog(mContext,
                getResources().getString(R.string.alert_save_prefix)+preWalkWayName+getResources().getString(R.string.alert_save_suffix),
                getResources().getString(R.string.alert_goto_edit),
                getResources().getString(R.string.alert_suspend_save),
                askDraftContinuePositineveListener,
                askDraftContinueNetiveListener);
        askDraftContinueAlertDialog.setCanceledOnTouchOutside(false);
        askDraftContinueAlertDialog.setCancelable(false);
        Log.i(TAG, "targetFragment:"+targetFragment +"/ fragment name:" + getReplaceTargetName(targetFragment));

        fragmentManager = getFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();

        bundle = new Bundle();

        bundle.putInt(AppConfig.PHOTO_CATEGORY_SOURCE, AppConfig.DIRECT_REQUEST_RECORD);

        if( targetFragment != AppConfig.PREF_DIARYSTEP1_STATUS_ID)
        {
            askDraftContinueAlertDialog.show();
        }
        else
        {


            sharedPreferences.edit()
                    .putString(AppConfig.PREF_DIARY_PRE_WALKWAY_NAME_KEY, currentWalkWayName)
                    .putString(AppConfig.PREF_DIARY_PRE_WALKWAY_ID_KEY, currentWalkWayID)
                    .putString(AppConfig.JPEG_TRANSFILE_NAME_STEP1_KEY, "")
                    .putString(AppConfig.JPEG_TRANSFILE_NAME_STEP2_KEY, "")
                    .putString(AppConfig.JPEG_TRANSFILE_NAME_STEP3_KEY, "")
                    .putString(AppConfig.JPEG_TRANSFILE_NAME_STEP4_KEY, "")
                    .putString(AppConfig.JPEG_TRANSFILE_NAME_STEP5_KEY, "")
                    .putString(AppConfig.PREF_DIARYSTEP1_EDIT_CONTENT_KEY, "")
                    .putString(AppConfig.PREF_DIARYSTEP2_EDIT_CONTENT_KEY, "")
                    .putString(AppConfig.PREF_DIARYSTEP3_EDIT_CONTENT_KEY, "")
                    .putString(AppConfig.PREF_DIARYSTEP4_EDIT_CONTENT_KEY, "")
                    .putString(AppConfig.PREF_DIARYSTEP5_EDIT_CONTENT_KEY, "")
                    .commit();


//            if( targetFragment == AppConfig.PREF_DIARYSTEP1_STATUS_ID)
//            {
                AppUtils.clearFolderData(mContext, AppConfig.APP_PATH_SD_CARD, AppConfig.APP_TRANSPORT_PATH_SD_CARD);
//            }

            fragment = getReplaceTargetFragment(targetFragment);
            fragment.setArguments(bundle);
            if (fragment != null) {
                fragmentTransaction
                        .replace(R.id.diary_frame_container, fragment, getReplaceTargetName(targetFragment))
                        .commit();
            } else {
                Log.e("WWActivity", "Error in creating fragment");
            }

        }


    }


    // replace to remember page
    private View.OnClickListener askDraftContinuePositineveListener  = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.i(TAG,"continue edit");
            callSystemTime();
            append = currentTime + "," +
                    UUIDChose + "," +
                    "DiaryMissionDraftContinue" + "," +
                    "DiaryMissionDraft" + "," +
                    "btnAskDialogContinue" + "\n";
            transactionLogSave(append);
//            sharedPreferences.edit()
//                    .putString(AppConfig.PREF_DIARY_PRE_WALKWAY_NAME_KEY, "")
//                    .putString(AppConfig.PREF_DIARY_PRE_WALKWAY_ID_KEY, "")
//                    .commit();
            fragment = getReplaceTargetFragment(targetFragment);
            fragment.setArguments(bundle);
            askDraftContinueAlertDialog.dismiss();
            if (fragment != null) {
                fragmentTransaction
                        .replace(R.id.diary_frame_container, fragment, getReplaceTargetName(targetFragment))
                        .addToBackStack(getReplaceTargetName(targetFragment))
                        .commit();
            } else {
                Log.e(TAG, "Error in creating fragment");
            }
        }
    };

    // replace to init page
    private  View.OnClickListener  askDraftContinueNetiveListener =  new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.i(TAG,"init edit");

            callSystemTime();
            append = currentTime + "," +
                    UUIDChose + "," +
                    "DiaryMissionDraftContinue" + "," +
                    "DiaryMissionStep1" + "," +
                    "btnAskDialogNotCancel" + "\n";
            transactionLogSave(append);
            AppUtils.clearFolderData(mContext, AppConfig.APP_PATH_SD_CARD, AppConfig.APP_TRANSPORT_PATH_SD_CARD);
            sharedPreferences.edit()
                    .putString(AppConfig.PREF_DIARY_PRE_WALKWAY_NAME_KEY, currentWalkWayName)
                    .putString(AppConfig.PREF_DIARY_PRE_WALKWAY_ID_KEY, currentWalkWayID)
                    .putString(AppConfig.PREF_DIARYSTEP1_EDIT_CONTENT_KEY, "")
                    .putString(AppConfig.JPEG_TRANSFILE_NAME_STEP1_KEY, "")
                    .putString(AppConfig.JPEG_TRANSFILE_NAME_STEP2_KEY, "")
                    .putString(AppConfig.JPEG_TRANSFILE_NAME_STEP3_KEY, "")
                    .putString(AppConfig.JPEG_TRANSFILE_NAME_STEP4_KEY, "")
                    .putString(AppConfig.JPEG_TRANSFILE_NAME_STEP5_KEY, "")
                    .putString(AppConfig.PREF_DIARYSTEP1_EDIT_CONTENT_KEY, "")
                    .putString(AppConfig.PREF_DIARYSTEP2_EDIT_CONTENT_KEY, "")
                    .putString(AppConfig.PREF_DIARYSTEP3_EDIT_CONTENT_KEY, "")
                    .putString(AppConfig.PREF_DIARYSTEP4_EDIT_CONTENT_KEY, "")
                    .putString(AppConfig.PREF_DIARYSTEP5_EDIT_CONTENT_KEY, "")
                    .commit();
            targetFragment = AppConfig.PREF_DIARYSTEP1_STATUS_ID;
            fragment = getReplaceTargetFragment(targetFragment);
            fragment.setArguments(bundle);
            if (fragment != null) {
                fragmentTransaction
                        .replace(R.id.diary_frame_container, fragment, getReplaceTargetName(targetFragment))
                        .addToBackStack(getReplaceTargetName(targetFragment))
                        .commit();
            } else {
                Log.e(TAG, "Error in creating fragment");
            }
            askDraftContinueAlertDialog.dismiss();
        }
    };



    private Fragment getReplaceTargetFragment(int target)
    {
        Fragment fragment = null;
        switch ( target )
        {
            case AppConfig.PREF_DIARYSTEP1_STATUS_ID:
                fragment = new DiaryMissionStep1();
                break;
            case AppConfig.PREF_DIARYSTEP1EDIT_STATUS_ID:
                fragment = new DiaryMissionStep1Result();
                break;
            case AppConfig.PREF_DIARYSTEP2_STATUS_ID:
                fragment = new DiaryMissionStep2();
                break;
            case AppConfig.PREF_DIARYSTEP2EDIT_STATUS_ID:
                fragment = new DiaryMissionStep2Result();
                break;
            case AppConfig.PREF_DIARYSTEP3_STATUS_ID:
                fragment = new DiaryMissionStep3();
                break;
            case AppConfig.PREF_DIARYSTEP3EDIT_STATUS_ID:
                fragment = new DiaryMissionStep3Result();
                break;
            case AppConfig.PREF_DIARYSTEP4_STATUS_ID:
                fragment = new DiaryMissionStep4();
                break;
            case AppConfig.PREF_DIARYSTEP4EDIT_STATUS_ID:
                fragment = new DiaryMissionStep4Result();
                break;
            case AppConfig.PREF_DIARYSTEP5_STATUS_ID:
                fragment = new DiaryMissionStep5();
                break;
            case AppConfig.PREF_DIARYSTEP5EDIT_STATUS_ID:
                fragment = new DiaryMissionStep5Result();
                break;
            case AppConfig.PREF_DIARYSTEP_LAST_ID:
                fragment = new DiaryMissionLast();
                break;
        }
        return fragment;
    }

    private String getReplaceTargetName(int target)
    {
        String value = "DiaryMissionStep1";
        switch ( target )
        {
            case AppConfig.PREF_DIARYSTEP1_STATUS_ID:
                value = "DiaryMissionStep1";
                break;
            case AppConfig.PREF_DIARYSTEP1EDIT_STATUS_ID:
                value = "DiaryMissionStep1Result";
                break;
            case AppConfig.PREF_DIARYSTEP2_STATUS_ID:
                value = "DiaryMissionStep2";
                break;
            case AppConfig.PREF_DIARYSTEP2EDIT_STATUS_ID:
                value = "DiaryMissionStep2Result";
                break;
            case AppConfig.PREF_DIARYSTEP3_STATUS_ID:
                value = "DiaryMissionStep3";
                break;
            case AppConfig.PREF_DIARYSTEP3EDIT_STATUS_ID:
                value = "DiaryMissionStep3Result";
                break;
            case AppConfig.PREF_DIARYSTEP4_STATUS_ID:
                value = "DiaryMissionStep4";
                break;
            case AppConfig.PREF_DIARYSTEP4EDIT_STATUS_ID:
                value = "DiaryMissionStep4Result";
                break;
            case AppConfig.PREF_DIARYSTEP5_STATUS_ID:
                value = "DiaryMissionStep5";
                break;
            case AppConfig.PREF_DIARYSTEP5EDIT_STATUS_ID:
                value = "DiaryMissionStep5Result";
                break;
            case AppConfig.PREF_DIARYSTEP_LAST_ID:
                value = "DiaryMissionLast";
                break;
        }

        return value;
    }





    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if( askDraftContinueAlertDialog != null )
        {
            askDraftContinueAlertDialog.dismiss();
            askDraftContinueAlertDialog = null;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if( askDraftContinueAlertDialog != null )
        {
            askDraftContinueAlertDialog.dismiss();
            askDraftContinueAlertDialog = null;
        }

        //recycle bg
        if (null != diaryMainLayout.getBackground()) {
            diaryMainLayout.getBackground().setCallback(null);
        }
        if (null != bgDrawable && !bgDrawable.getBitmap().isRecycled()) {
            bgDrawable.getBitmap().recycle();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        View v = getCurrentFocus();

        if (v instanceof EditText) {
            View view = getCurrentFocus();
            int scrcoords[] = new int[2];
            view.getLocationOnScreen(scrcoords);
            float x = event.getRawX() + view.getLeft() - scrcoords[0];
            float y = event.getRawY() + view.getTop() - scrcoords[1];

            Log.d("Activity", "Touch event " +
                    event.getRawX() + "," + event.getRawY() + " " +
                    x + "," + y + " rect " + view.getLeft() + "," +
                    view.getTop() + "," + view.getRight() + "," +
                    view.getBottom() + " coords " + scrcoords[0] +
                    "," + scrcoords[1]);

            if (event.getAction() == MotionEvent.ACTION_UP &&
                    (x < view.getLeft() || x >= view.getRight() || y < view.getTop() || y > view.getBottom())) {

                InputMethodManager imm =
                        (InputMethodManager) getSystemService(mContext.INPUT_METHOD_SERVICE);

                imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
            }
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Log.d(TAG, "onKeyDown back");

//            if (fragmentManager.getBackStackEntryCount() == 1) {
//                // Dialog style
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
//
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
//
//            } else {
//                getFragmentManager().popBackStack();
//            }


        }
        return false;
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
