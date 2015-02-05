package com.coretronic.bdt.WalkWay.Diary;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.coretronic.bdt.AppConfig;
import com.coretronic.bdt.AppUtils;
import com.coretronic.bdt.MessageWall.WallActivity;
import com.coretronic.bdt.R;
import com.coretronic.bdt.Utility.CustomerOneBtnAlertDialog;
import com.coretronic.bdt.Utility.CustomerTwoBtnAlertDialog;
import com.coretronic.bdt.WalkWay.Module.ToolsPopupWindow;
import com.google.gson.JsonSyntaxException;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by james on 14/11/19.
 */
public class DiaryMissionLast extends Fragment {
    private final String TAG = DiaryMissionStep5.class.getSimpleName();


    // constant
    private Context mContext = null;
    // action bar
    private RelativeLayout navigationBar = null;
    private PopupWindow menuPopupWindow;
    private Button btnBack = null;
    private Button btnPopMenu = null;
    private TextView barTitle = null;

    // ui
    private Button saveDraftBtn = null;
    private Button shareContentBtn = null;
    private View currentBarPlaceVW = null;
    private SharedPreferences sharedPreferences;
    private CustomerTwoBtnAlertDialog askSaveAlertDialog = null;
    private CustomerTwoBtnAlertDialog uploadFailAlertDialog = null;
    private CustomerOneBtnAlertDialog getAlertDialog = null;

    private String fileName = "";

    // server variable
    private ProgressDialog progressDialog = null;
    private String uuidChose = "";
    private AsyncHttpClient asyncHttpClient;
    private CustomerOneBtnAlertDialog alert = null;

    private Fragment fragment = null;
    private String mission1Photo = "";
    private String mission2Photo = "";
    private String mission3Photo = "";
    private String mission4Photo = "";
    private String mission5Photo = "";

    //transactionLog
    private String currentTime;
    private String append;


    public JsonHttpResponseHandler jsonHandler = new JsonHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

            Log.d(TAG, "qa onSuccess = " + response);

            try {
                if (response.get("msgCode").equals(AppConfig.SUCCESS_CODE)) {


                    clearAllData();
                    Intent intent = new Intent(mContext, WallActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                    }


                } else {
                    alert = AppUtils.getAlertDialog(mContext, getString(R.string.data_result_error), erroAlertListener);
                    alert.show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                alert = AppUtils.getAlertDialog(mContext, getString(R.string.data_result_error), erroAlertListener);
                alert.show();
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
                alert = AppUtils.getAlertDialog(mContext, getString(R.string.data_result_error), erroAlertListener);
                alert.show();
            } catch (Exception e) {
                e.printStackTrace();
                alert = AppUtils.getAlertDialog(mContext, getString(R.string.data_result_error), erroAlertListener);
                alert.show();
            }
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            Log.d(TAG, "onFailure");
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
            alert = AppUtils.getAlertDialog(mContext, getString(R.string.data_load_error), erroAlertListener);
            alert.show();
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            super.onFailure(statusCode, headers, responseString, throwable);
            Log.i(TAG, "444");
            Log.i(TAG, "responseString:" + responseString);
            alert = AppUtils.getAlertDialog(mContext, getString(R.string.data_load_error), erroAlertListener);
            alert.show();
        }
    };


    public AsyncHttpResponseHandler fileHandler = new AsyncHttpResponseHandler() {
        @Override
        public void onProgress(int bytesWritten, int totalSize) {
            super.onProgress(bytesWritten, totalSize);
//            Log.d(TAG, "bytesWritten:" + bytesWritten);
            Log.d(TAG, "totalSize:" + totalSize);

        }

        @Override
        public void onSuccess(int i, Header[] headers, byte[] bytes) {
            if (progressDialog != null) {
                progressDialog.dismiss();
                postDiaryDataToServer();
            }
            try {
                Log.d(TAG, "onSuccess:" + new String(bytes, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
            if( uploadFailAlertDialog!= null )
            {
                if( !(uploadFailAlertDialog.isShowing()))
                {
                    uploadFailAlertDialog.show();
                }

            }

//            try {
//                Log.d(TAG, "onFailure:" + new String(bytes, "UTF-8"));
//                uploadFailAlertDialog.show();
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
                uploadFailAlertDialog.show();
//            }
        }

        @Override
        public void onFinish() {
            super.onFinish();
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
            Log.d(TAG, "onFinish:");

        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        sharedPreferences = getActivity().getSharedPreferences(AppConfig.SHAREDPREFERENCES_NAME, 0);
        fileName = sharedPreferences.getString(AppConfig.JPEG_TRANSFILE_NAME_STEP5_KEY, "");
        sharedPreferences.edit()
                .putInt(AppConfig.PREF_DIARY_CURRENT_STATUS_KEY, AppConfig.PREF_DIARYSTEP_LAST_ID)
                .commit();
        asyncHttpClient = new AsyncHttpClient();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.diary_mission_finish, container, false);
        mContext = view.getContext();
        menuPopupWindow = new ToolsPopupWindow(mContext, null);

        initView(view);

        progressDialog = new ProgressDialog(mContext);

        askSaveAlertDialog = AppUtils.getAlertDialog(mContext,
                getResources().getString(R.string.diary_save_alert),
                getResources().getString(R.string.save),
                getResources().getString(R.string.not_save),
                askSaveAlertPositineveListener,
                askSaveAlertNegativeListener);

        uploadFailAlertDialog = AppUtils.getAlertDialog(mContext,
                getResources().getString(R.string.diary_upload_fail),
                getResources().getString(R.string.cancel),
                getResources().getString(R.string.retry),
                uploadFailAlertPositineveListener,
                uploadFailAlertNegativeListener);

//        preStepSaveAlertDialog = AppUtils.getAlertDialog(mContext,
//                getResources().getString(R.string.click_back_alert),
//                getResources().getString(R.string.diary_back),
//                getResources().getString(R.string.cancel),
//                preStepSaveAlertPositineveListener,
//                preStepSaveAlertNegativeListener);

        sharedPreferences.edit()
                .putBoolean(AppConfig.PREF_DIARYSTEP_LAST_KEY, true)
                .commit();


        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        lp.width = (AppUtils.getScreenWidth(mContext) / 5) * 5;
        currentBarPlaceVW.setLayoutParams(lp);
        lp = null;

        getMissPhotoName();

        return view;
    }


    private void initView(View view) {
        // navigation bar
        navigationBar = (RelativeLayout) view.findViewById(R.id.navigationBar_tools);
        btnBack = (Button) view.findViewById(R.id.btnBack);
        btnPopMenu = (Button) view.findViewById(R.id.btnToolsPopMenu);
        barTitle = (TextView) view.findViewById(R.id.action_bar_title);

        saveDraftBtn = (Button) view.findViewById(R.id.prestep_btn);
        shareContentBtn = (Button) view.findViewById(R.id.finish_share_btn);

        btnBack.setText(R.string.exit);

        btnBack.setOnClickListener(btnListener);
        btnPopMenu.setOnClickListener(btnListener);
        saveDraftBtn.setOnClickListener(btnListener);
        shareContentBtn.setOnClickListener(btnListener);
        currentBarPlaceVW = (View) view.findViewById(R.id.bar_cnt);


    }

    private View.OnClickListener btnListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btnBack:
                    callSystemTime();
                    append = currentTime + "," +
                            uuidChose + "," +
                            "DiaryMissionLast" + "," +
                            "BtnBackAskDialog" + "," +
                            "btnBack" + "\n";
                    transactionLogSave(append);
                    exitBtnEvent();
                    break;
                case R.id.btnToolsPopMenu:
                    callSystemTime();
                    append = currentTime + "," +
                            uuidChose + "," +
                            "DiaryMissionLast" + "," +
                            "MenuToolsPopupWindow" + "," +
                            "btnToolsPopMenu" + "\n";
                    transactionLogSave(append);
                    menuPopupWindow.showAsDropDown(navigationBar, 0, 0);
                    break;
                case R.id.prestep_btn:
                    callSystemTime();
                    append = currentTime + "," +
                            uuidChose + "," +
                            "DiaryMissionLast" + "," +
                            "DiaryMissionStep5" + "," +
                            "btnResBack" + "\n";
                    transactionLogSave(append);
                    backToPreMissioinPage();
                    break;
                case R.id.finish_share_btn:
                    callSystemTime();
                    append = currentTime + "," +
                            uuidChose + "," +
                            "DiaryMissionLast" + "," +
                            "WallActivity" + "," +
                            "btnFinishShare" + "\n";
                    transactionLogSave(append);
                    shareBtnEvent();
                    break;

            }
        }
    };



    private void shareBtnEvent() {

        if (    mission1Photo.equals("") &&
                mission2Photo.equals("") &&
                mission3Photo.equals("") &&
                mission4Photo.equals("") &&
                mission5Photo.equals(""))
        {
            getAlertDialog = AppUtils.getAlertDialog(mContext, mContext.getString(R.string.dairy_noedit_error));
            getAlertDialog.show();
            return;
        }

        if( AppUtils.isOnline(mContext))
        {
            uploadFiles();
        }
        else
        {
            uploadFailAlertDialog.show();
        }


    }


    private void uploadFiles() {
        if( progressDialog != null ) {
            progressDialog.setMessage(getString(R.string.diary_upload_dialog));
            progressDialog.show();
        }
        final String url = AppConfig.FILE_UPLOAD_PATH;

        try {
            RequestParams params = new RequestParams();

            // image folder full path
            String imgFolderPath = mContext.getExternalCacheDir() + AppConfig.APP_PATH_SD_CARD + AppConfig.APP_TRANSPORT_PATH_SD_CARD;
            Log.i(TAG, "folder fullPath:" + imgFolderPath);

            File fileFolder = new File(imgFolderPath);
            // get all file save to file array
            File files[] = fileFolder.listFiles();
            // get multi file
            Log.i(TAG, "files.length:" + files.length);

            for (int i = 0; i < files.length; i++) {
            File imgFile = new File(imgFolderPath, files[i].getName());
                Log.i(TAG, "files.getName():" + imgFile.getName());
                params.put("file[" + i + "]", imgFile);

            }
            asyncHttpClient.post(url, params, fileHandler);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void postDiaryDataToServer() {
        if( progressDialog!=null) {
            progressDialog.setMessage(getString(R.string.diary_upload_dialog));
            progressDialog.show();
        }

        final String url = AppConfig.DOMAIN_SITE_PATE + AppConfig.INSERT_DAILY_NOTE;
        Log.i(TAG, "url:  " + url);

        uuidChose = sharedPreferences.getString(AppConfig.PREF_UNIQUE_ID, "");
        Log.i(TAG, "UUID:  " + uuidChose);

        asyncHttpClient.setTimeout(AppConfig.TIMEOUT);
        RequestParams params = new RequestParams();
        params.add("uid", uuidChose);
//        params.add("uid", "uuid-12345");
        params.add("time", AppUtils.getSystemTime());
        params.add("wid", sharedPreferences.getString(AppConfig.PREF_DIARY_PRE_WALKWAY_ID_KEY, ""));
        params.add("mission1Photo", mission1Photo);
        params.add("mission2Photo", mission2Photo);
        params.add("mission3Photo", mission3Photo);
        params.add("mission4Photo", mission4Photo);
        params.add("mission5Photo", mission5Photo);
        params.add("mission1Comment", sharedPreferences.getString(AppConfig.PREF_DIARYSTEP1_EDIT_CONTENT_KEY, ""));
        params.add("mission2Comment", sharedPreferences.getString(AppConfig.PREF_DIARYSTEP2_EDIT_CONTENT_KEY, ""));
        params.add("mission3Comment", sharedPreferences.getString(AppConfig.PREF_DIARYSTEP3_EDIT_CONTENT_KEY, ""));
        params.add("mission4Comment", sharedPreferences.getString(AppConfig.PREF_DIARYSTEP4_EDIT_CONTENT_KEY, ""));
        params.add("mission5Comment", sharedPreferences.getString(AppConfig.PREF_DIARYSTEP5_EDIT_CONTENT_KEY, ""));
        params.add("photoDate", sharedPreferences.getString(AppConfig.WALKWAY_CAMERA_DATE, ""));
        Log.i(TAG, "params:  " + params);
        asyncHttpClient.post(url, params, jsonHandler);
    }


    private void getMissPhotoName() {
        mission1Photo = sharedPreferences.getString(AppConfig.JPEG_TRANSFILE_NAME_STEP1_KEY, "");
        if (!mission1Photo.equals("")) {
            mission1Photo = mission1Photo + ".png";
        }
        mission2Photo = sharedPreferences.getString(AppConfig.JPEG_TRANSFILE_NAME_STEP2_KEY, "");
        if (!mission2Photo.equals("")) {
            mission2Photo = mission2Photo + ".png";
        }
        mission3Photo = sharedPreferences.getString(AppConfig.JPEG_TRANSFILE_NAME_STEP3_KEY, "");
        if (!mission3Photo.equals("")) {
            mission3Photo = mission3Photo + ".png";
        }
        mission4Photo = sharedPreferences.getString(AppConfig.JPEG_TRANSFILE_NAME_STEP4_KEY, "");
        if (!mission4Photo.equals("")) {
            mission4Photo = mission4Photo + ".png";
        }
        mission5Photo = sharedPreferences.getString(AppConfig.JPEG_TRANSFILE_NAME_STEP5_KEY, "");
        if (!mission5Photo.equals("")) {
            mission5Photo = mission5Photo + ".png";
        }
    }


    private void clearAllData() {

        // clear photo
        AppUtils.clearFolderData(mContext, AppConfig.APP_PATH_SD_CARD, AppConfig.APP_TRANSPORT_PATH_SD_CARD);
        // clear share preferences
        sharedPreferences.edit()
                .putString(AppConfig.PREF_DIARY_PRE_WALKWAY_NAME_KEY, "")
                .putString(AppConfig.PREF_DIARY_PRE_WALKWAY_ID_KEY, "")
                .putString(AppConfig.JPEG_TRANSFILE_NAME_STEP5_KEY, "")
                .putString(AppConfig.PREF_DIARYSTEP1_EDIT_CONTENT_KEY, "")
                .putString(AppConfig.PREF_DIARYSTEP2_EDIT_CONTENT_KEY, "")
                .putString(AppConfig.PREF_DIARYSTEP3_EDIT_CONTENT_KEY, "")
                .putString(AppConfig.PREF_DIARYSTEP4_EDIT_CONTENT_KEY, "")
                .putString(AppConfig.PREF_DIARYSTEP5_EDIT_CONTENT_KEY, "")
                .putInt(AppConfig.PREF_DIARY_CURRENT_STATUS_KEY, AppConfig.PREF_DIARYSTEP1_STATUS_ID)
                .putBoolean(AppConfig.PREF_DIARYSTEP_LAST_KEY, false)
                .commit();

    }

    private View.OnClickListener erroAlertListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Log.i(TAG, " on error Litener click");
            if (alert != null) {
                alert.dismiss();
            }
        }
    };


    private View.OnKeyListener backListener = new View.OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    Log.i(TAG, TAG+" click back key");
                    callSystemTime();
                    append = currentTime + "," +
                            uuidChose + "," +
                            "DiaryMissionLast" + "," +
                            "ResBackAskDialog" + "," +
                            "btnKeyCodeBack" + "\n";
                    transactionLogSave(append);
                    backToPreMissioinPage();
                    return true;
                }
            }
            return false;
        }
    };

    private void exitBtnEvent() {
        askSaveAlertDialog.show();

    }

    private View.OnClickListener askSaveAlertPositineveListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            callSystemTime();
            append = currentTime + "," +
                    uuidChose + "," +
                    "BtnBackAskDialog" + "," +
                    "WalkWayListDetailInfo" + "," +
                    "btnBackAskDialogSave" + "\n";
            transactionLogSave(append);
            getActivity().finish();
            askSaveAlertDialog.dismiss();

        }
    };

    private View.OnClickListener askSaveAlertNegativeListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            callSystemTime();
            append = currentTime + "," +
                    uuidChose + "," +
                    "BtnBackAskDialog" + "," +
                    "WalkWayListDetailInfo" + "," +
                    "btnBackAskDialogNotSave" + "\n";
            transactionLogSave(append);
            Log.i(TAG, "not save");

            clearData();

            askSaveAlertDialog.dismiss();
            getActivity().finish();
        }
    };


    private View.OnClickListener uploadFailAlertPositineveListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
           if(uploadFailAlertDialog != null )
           {
               uploadFailAlertDialog.dismiss();
           }

        }
    };

    private View.OnClickListener uploadFailAlertNegativeListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            uploadFiles();

        }
    };

    private void clearData() {

        // clear photo
        AppUtils.deletePhotoFromSD(mContext, AppConfig.APP_PATH_SD_CARD, AppConfig.APP_TRANSPORT_PATH_SD_CARD, fileName);

        // clear share preferences
        sharedPreferences.edit()
                .putString(AppConfig.PREF_DIARY_PRE_WALKWAY_NAME_KEY, "")
                .putString(AppConfig.PREF_DIARY_PRE_WALKWAY_ID_KEY, "")
                .putString(AppConfig.JPEG_TRANSFILE_NAME_STEP5_KEY, "")
                .putString(AppConfig.PREF_DIARYSTEP1_EDIT_CONTENT_KEY, "")
                .putString(AppConfig.PREF_DIARYSTEP2_EDIT_CONTENT_KEY, "")
                .putString(AppConfig.PREF_DIARYSTEP3_EDIT_CONTENT_KEY, "")
                .putString(AppConfig.PREF_DIARYSTEP4_EDIT_CONTENT_KEY, "")
                .putString(AppConfig.PREF_DIARYSTEP5_EDIT_CONTENT_KEY, "")
                .putInt(AppConfig.PREF_DIARY_CURRENT_STATUS_KEY, AppConfig.PREF_DIARYSTEP1_STATUS_ID)
                .putBoolean(AppConfig.PREF_DIARYSTEP_LAST_KEY, false)
                .commit();
    }


    private void backToPreMissioinPage() {
        Fragment fragment = null;
        Bundle bundle = new Bundle();

        if (sharedPreferences.getBoolean(AppConfig.PREF_DIARYSTEP5_EDIT_KEY, false) == true) {

            bundle.putInt(AppConfig.PHOTO_CATEGORY_SOURCE, AppConfig.DIRECT_REQUEST_RECORD);
            fragment = new DiaryMissionStep5Result();
            fragment.setArguments(bundle);
            getFragmentManager().beginTransaction()
                    .replace(R.id.diary_frame_container, fragment, "DiaryMissionStep5Result")
                    .addToBackStack("DiaryMissionStep5Result")
                    .commit();
        } else {
            fragment = new DiaryMissionStep5();
            getFragmentManager().beginTransaction()
                    .replace(R.id.diary_frame_container, fragment, "DiaryMissionStep5")
                    .addToBackStack("DiaryMissionStep5")
                    .commit();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (getAlertDialog != null) {
            getAlertDialog.dismiss();
        }

        if( askSaveAlertDialog != null )
        {
            askSaveAlertDialog.dismiss();
        }

        if( progressDialog != null )
        {
            progressDialog.dismiss();
        }

        if( uploadFailAlertDialog != null )
        {
            uploadFailAlertDialog.dismiss();
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
