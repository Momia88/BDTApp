package com.coretronic.bdt.WalkWay.Diary;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.coretronic.bdt.AppConfig;
import com.coretronic.bdt.AppUtils;
import com.coretronic.bdt.R;
import com.coretronic.bdt.Utility.CustomerTwoBtnAlertDialog;
import com.coretronic.bdt.Utility.PickPhotoUtility;
import com.coretronic.bdt.WalkWay.Module.ToolsPopupWindow;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by james on 14/11/7.
 */
public class DiaryMissionStep4 extends Fragment {
    private final String TAG = DiaryMissionStep4.class.getSimpleName();

    // constant
    private final static int CAMERA_REQUEST = 66;
    private final static int PHOTO_FROM_ALBUM = 99;

    private Context mContext = null;
    // action bar
    private RelativeLayout navigationBar = null;
    private PopupWindow menuPopupWindow;
    private Button btnBack = null;
    private Button btnPopMenu = null;
    private TextView barTitle = null;

    // content
    private SharedPreferences sharedPreferences;
    private Button pickFromAlbumbtn = null;
    private Button takeCameraBtn = null;
    private Button step4SkipBtn = null;
    public Uri takenPhotoUri;
    private int reqPhotoHeight = 0;
    private int reqPhotoWidth = 0;
    private Bitmap tempBitmap = null;
    private String UUIDChose = "";

    private View currentBarPlaceVW = null;

    private Button step4BackBtn = null;
    private ImageView mission3Photo = null;

    private DisplayMetrics mPhone;
    public String photoFileName = "bdt.jpg";
    private Fragment fragment = null;
    private CustomerTwoBtnAlertDialog askSaveAlertDialog = null;
    //transactionLog
    private String currentTime;
    private String append;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getActivity().getSharedPreferences(AppConfig.SHAREDPREFERENCES_NAME,0);
        sharedPreferences.edit()
                .putInt(AppConfig.PREF_DIARY_CURRENT_STATUS_KEY, AppConfig.PREF_DIARYSTEP4_STATUS_ID)
                .commit();

        UUIDChose = sharedPreferences.getString(AppConfig.PREF_UNIQUE_ID, "");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.diary_mission_step4, container, false);
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(backListener);
        mContext = view.getContext();
        menuPopupWindow = new ToolsPopupWindow(mContext, null);

        initView(view);

        ((View) view.findViewById(R.id.right_line)).setVisibility(View.GONE);
        ((Button) view.findViewById(R.id.btnToolsPopMenu)).setVisibility(View.GONE);

        askSaveAlertDialog = AppUtils.getAlertDialog(mContext,
                getResources().getString(R.string.diary_save_alert),
                getResources().getString(R.string.save),
                getResources().getString(R.string.not_save),
                askSaveAlertPositineveListener,
                askSaveAlertNegativeListener);


        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT );
        lp.width = (AppUtils.getScreenWidth(mContext) / 5) * 4;
        currentBarPlaceVW.setLayoutParams(lp);
        lp = null;
        Log.i(TAG,"sharedPreferences.getString(AppConfig.PREF_DIARY_PRE_WALKWAY_ID_KEY):"+sharedPreferences.getString(AppConfig.PREF_DIARY_PRE_WALKWAY_ID_KEY, ""));

        return view;
    }


    private void initView(View view) {
        // navigation bar
        navigationBar = (RelativeLayout) view.findViewById(R.id.navigationBar_tools);
        btnBack = (Button) view.findViewById(R.id.btnBack);
        btnPopMenu = (Button) view.findViewById(R.id.btnToolsPopMenu);
        barTitle = (TextView) view.findViewById(R.id.action_bar_title);

        pickFromAlbumbtn = (Button) view.findViewById(R.id.step4_photo_select_btn);
        takeCameraBtn = (Button) view.findViewById(R.id.step4_take_camera_btn);
        step4SkipBtn = (Button) view.findViewById(R.id.step4_skip_btn);
        step4BackBtn = (Button) view.findViewById(R.id.step4_back_btn);

        btnBack.setText(R.string.exit);

        btnBack.setOnClickListener(btnListener);
        btnPopMenu.setOnClickListener(btnListener);
        pickFromAlbumbtn.setOnClickListener(btnListener);
        takeCameraBtn.setOnClickListener(btnListener);
        step4SkipBtn.setOnClickListener(btnListener);
        step4BackBtn.setOnClickListener(btnListener);
        currentBarPlaceVW = (View) view.findViewById(R.id.bar_cnt);

//        mission1Photo = (ImageView) view.findViewById(R.id.mission1_photo);

    }


    private View.OnClickListener btnListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Fragment fragment;
            switch (view.getId()) {
                case R.id.btnBack:
//                    getActivity().finish();
                    callSystemTime();
                    append = currentTime + "," +
                            UUIDChose + "," +
                            "DiaryMissionStep4" + "," +
                            "BtnBackAskDialog" + "," +
                            "btnBack" + "\n";
                    transactionLogSave(append);
                    exitBtnEvent();
                    break;
                case R.id.btnToolsPopMenu:
                    menuPopupWindow.showAsDropDown(navigationBar, 0, 0);
                    break;
                case R.id.step4_photo_select_btn:
                    callSystemTime();
                    append = currentTime + "," +
                            UUIDChose + "," +
                            "DiaryMissionStep4" + "," +
                            "selectPhotoFromAlbum" + "," +
                            "btnSelectPhotoFromAlbum" + "\n";
                    transactionLogSave(append);
                    selectPhotoFromAlbum();
                    break;
                case R.id.step4_take_camera_btn:
                    callSystemTime();
                    append = currentTime + "," +
                            UUIDChose + "," +
                            "DiaryMissionStep4" + "," +
                            "TakeCamera" + "," +
                            "btnTakeCamera" + "\n";
                    transactionLogSave(append);
                    photoFileName = AppUtils.getCameraSystemTimeFileName(AppConfig.JPEG_FILE_PREFIX) + AppConfig.JPEG_FILE_SUFFIX;
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, AppUtils.getPhotoUri(photoFileName));
                    if (cameraIntent.resolveActivity(mContext.getPackageManager()) != null) {
                        startActivityForResult(cameraIntent, CAMERA_REQUEST);
                    } else {
                        AppUtils.getAlertDialog(mContext, "照相app").show();
                    }

                    break;
                case R.id.step4_skip_btn:
                    callSystemTime();
                    append = currentTime + "," +
                            UUIDChose + "," +
                            "DiaryMissionStep4" + "," +
                            "DiaryMissionStep5" + "," +
                            "btnStep4Skip" + "\n";
                    transactionLogSave(append);
                    fragment = new DiaryMissionStep5();
                    if (fragment != null) {
                        getFragmentManager().beginTransaction()
                                .replace(R.id.diary_frame_container, fragment, "DiaryMissionStep5")
                                .addToBackStack("DiaryMissionStep5")
                                .commit();
                    } else {
                        Log.e(TAG, "Error in creating fragment");
                    }
                    break;
                case R.id.step4_back_btn:
                    callSystemTime();
                    append = currentTime + "," +
                            UUIDChose + "," +
                            "DiaryMissionStep4" + "," +
                            "DiaryMissionStep3" + "," +
                            "btnStep4Back" + "\n";
                    transactionLogSave(append);
                    Log.i(TAG,"step4_back_btn");
//                    if(  getFragmentManager().popBackStackImmediate("DiaryMainActivity",0) == false )
//                    {
//                        getFragmentManager().popBackStack();
                    backToPreMissioinPage();
//                    }
//                    else
//                    {
//                        getFragmentManager().popBackStackImmediate("DiaryMainActivity",0);
//
//                    }
//                    Log.i(TAG,"getFragmentManager().popBackStackImmediate(\"DiaryMainActivity\",0):"+getFragmentManager().popBackStackImmediate("DiaryMainActivity",0));
//                    getFragmentManager().popBackStack();
//                    Log.i(TAG,"getFragmentManager().getBackStackEntryCount():"+getFragmentManager().getBackStackEntryCount());
                    break;

            }
        }
    };

    private void backToPreMissioinPage() {
        Fragment fragment = null;
        Bundle bundle = new Bundle();
        if (sharedPreferences.getBoolean(AppConfig.PREF_DIARYSTEP3_EDIT_KEY, false) == true) {
            bundle.putInt(AppConfig.PHOTO_CATEGORY_SOURCE, AppConfig.DIRECT_REQUEST_RECORD);
            fragment = new DiaryMissionStep3Result();
            fragment.setArguments(bundle);
            getFragmentManager().beginTransaction()
                    .replace(R.id.diary_frame_container, fragment, "DiaryMissionStep3Result")
                    .addToBackStack("DiaryMissionStep3Result")
                    .commit();
        } else {
            fragment = new DiaryMissionStep3();
            getFragmentManager().beginTransaction()
                    .replace(R.id.diary_frame_container, fragment, "DiaryMissionStep3")
                    .addToBackStack("DiaryMissionStep3")
                    .commit();
        }
    }

    /**
     * select photo from album
     */
    private void selectPhotoFromAlbum() {
        Intent intent = new Intent();

        if (Build.VERSION.SDK_INT <19){
            intent = new Intent();
            intent.setType("image/jpeg");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, AppConfig.ALBUMS_REQUEST);
        } else {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/jpeg");
            startActivityForResult(intent, AppConfig.ALBUMS_REQUEST);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment fragment = new DiaryMissionStep4Result();
        Log.i(TAG, "requestCode:" + requestCode);
        Log.i(TAG, "resultCode:" + resultCode);
        Log.i(TAG, "data:" + data);


        Bundle bundle = new Bundle();
        //藉由requestCode判斷是否為開啟相機或開啟相簿而呼叫的，且data不為null
        if ((requestCode == AppConfig.CAMERA_REQUEST) && (resultCode == Activity.RESULT_OK)) {

//            Bundle bundle = new Bundle();
            takenPhotoUri = AppUtils.getPhotoUri(photoFileName);
            bundle.putString(AppConfig.SELECT_PIC_SELECT_PATH_KEY, takenPhotoUri.getPath());
            bundle.putInt(AppConfig.PHOTO_CATEGORY_SOURCE, AppConfig.CAMERA_REQUEST);

            Log.i(TAG, "takenPhotoUri.getPath():" + takenPhotoUri.getPath());
            Log.i(TAG, "takenPhotoUri:" + takenPhotoUri);

            saveTransportPhoto(takenPhotoUri.getPath());



        } else if ((requestCode == AppConfig.ALBUMS_REQUEST) && (resultCode == Activity.RESULT_OK)) {

            takenPhotoUri = Uri.parse(PickPhotoUtility.getPath(mContext, data.getData()));
            bundle.putString(AppConfig.SELECT_PIC_SELECT_PATH_KEY, takenPhotoUri.toString());
            bundle.putInt(AppConfig.PHOTO_CATEGORY_SOURCE, AppConfig.ALBUMS_REQUEST);

            saveTransportPhoto(takenPhotoUri.toString());

        }

        fragment.setArguments(bundle);

        if ((fragment != null) && (resultCode == Activity.RESULT_OK) ) {
            fragmentTransaction
                    .replace(R.id.diary_frame_container, fragment, "DiaryMissionStep4Result")
                    .addToBackStack("DiaryMissionStep4Result")
                    .commit();
        } else {
//            AppUtils.getAlertDialog(mContext, getString(R.string.nochoose_photo)).show();
            Log.e(TAG, "Error in creating fragment");
        }

    }

    private void saveTransportPhoto(String path)
    {
        reqPhotoWidth = reqPhotoHeight = AppConfig.PHOTO_SIZE;
        tempBitmap = AppUtils.rotateAndResizeBitmap(path, reqPhotoWidth, reqPhotoHeight);
        Log.i(TAG,"System.currentTimeMillis():"+System.currentTimeMillis());
        Log.i(TAG,"file name 4:"+ UUIDChose+"4"+System.currentTimeMillis());
        Log.i(TAG, "date:" + AppUtils.getPhotoDate(path));
        Log.i(TAG, "tempBitmap:" + tempBitmap);
        String fileName = UUIDChose+"4"+System.currentTimeMillis();


        if(sharedPreferences.getString(AppConfig.WALKWAY_CAMERA_DATE, "").equals(""))
        {
            setToSharePreferences(AppConfig.WALKWAY_CAMERA_DATE, AppUtils.getPhotoDate(path));
        }

        setToSharePreferences(AppConfig.JPEG_TRANSFILE_NAME_STEP4_KEY, fileName);
        AppUtils.savePhotoToSD(mContext, AppConfig.APP_PATH_SD_CARD, AppConfig.APP_TRANSPORT_PATH_SD_CARD, fileName, tempBitmap);
    }

    private void setToSharePreferences(String key, String val)
    {
        sharedPreferences.edit()
                .putString(key, val)
                .commit();
    }

    private View.OnKeyListener backListener = new View.OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    Log.i(TAG, TAG+" click back key");
                    callSystemTime();
                    append = currentTime + "," +
                            UUIDChose + "," +
                            "DiaryMissionStep4" + "," +
                            "DiaryMissionStep3" + "," +
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

    private View.OnClickListener askSaveAlertPositineveListener  = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            callSystemTime();
            append = currentTime + "," +
                    UUIDChose + "," +
                    "BtnBackAskDialog" + "," +
                    "WalkWayListDetailInfo" + "," +
                    "btnBackAskDialogSave" + "\n";
            transactionLogSave(append);
            askSaveAlertDialog.dismiss();
            getActivity().finish();
        }
    };

    private View.OnClickListener askSaveAlertNegativeListener =  new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.i(TAG,"not save");
            callSystemTime();
            append = currentTime + "," +
                    UUIDChose + "," +
                    "BtnBackAskDialog" + "," +
                    "WalkWayListDetailInfo" + "," +
                    "btnBackAskDialogNotSave" + "\n";
            transactionLogSave(append);
            sharedPreferences.edit()
                    .putString(AppConfig.PREF_DIARY_PRE_WALKWAY_NAME_KEY, "")
                    .putString(AppConfig.PREF_DIARY_PRE_WALKWAY_ID_KEY, "")
                    .putString(AppConfig.PREF_DIARYSTEP1_EDIT_CONTENT_KEY, "")
                    .putString(AppConfig.PREF_DIARYSTEP2_EDIT_CONTENT_KEY, "")
                    .putString(AppConfig.PREF_DIARYSTEP3_EDIT_CONTENT_KEY, "")
                    .putString(AppConfig.PREF_DIARYSTEP4_EDIT_CONTENT_KEY, "")
                    .putString(AppConfig.PREF_DIARYSTEP5_EDIT_CONTENT_KEY, "")
                    .putInt(AppConfig.PREF_DIARY_CURRENT_STATUS_KEY, AppConfig.PREF_DIARYSTEP1_STATUS_ID)
                    .commit();

            askSaveAlertDialog.dismiss();
            getActivity().finish();
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if( askSaveAlertDialog != null )
        {
            askSaveAlertDialog.dismiss();
            askSaveAlertDialog = null;
        }

        if( tempBitmap != null )
        {
            tempBitmap.recycle();
            tempBitmap = null;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if( askSaveAlertDialog != null )
        {
            askSaveAlertDialog.dismiss();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if( askSaveAlertDialog != null )
        {
            askSaveAlertDialog.dismiss();
            askSaveAlertDialog = null;
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
