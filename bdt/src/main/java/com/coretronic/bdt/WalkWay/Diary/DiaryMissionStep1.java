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
public class DiaryMissionStep1 extends Fragment {
    private final String TAG = DiaryMissionStep1.class.getSimpleName();


    private Context mContext = null;
    // action bar
    private RelativeLayout navigationBar = null;
    private PopupWindow menuPopupWindow;
    private Button btnBack = null;
    private Button btnPopMenu = null;
    private TextView barTitle = null;

    // content
    private SharedPreferences sharedPreferences;
    private View currentBarPlaceVW = null;
    private Button pickFromAlbumbtn = null;
    private Button takeCameraBtn = null;
    private Button step1SkipBtn = null;
    public Uri takenPhotoUri;
    private int screenWidth = 0;
    private int screenHeight = 0;
    private int reqPhotoHeight = 0;
    private int reqPhotoWidth = 0;
    private String UUIDChose = "";

    private Uri extraOutputFilePathUri;
    private ImageView mission1Photo = null;
    private Bitmap tempBitmap = null;

    private DisplayMetrics mPhone;
    public String photoFileName = "bdt.jpg";
    private Fragment fragment = null;

    private View totalView = null;

    //transactionLog
    private String currentTime;
    private String append;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        sharedPreferences = getActivity().getSharedPreferences(AppConfig.SHAREDPREFERENCES_NAME, 0);
        sharedPreferences.edit()
                .putInt(AppConfig.PREF_DIARY_CURRENT_STATUS_KEY, AppConfig.PREF_DIARYSTEP1_STATUS_ID)
                .commit();

        UUIDChose = sharedPreferences.getString(AppConfig.PREF_UNIQUE_ID, "");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.diary_mission_step1, container, false);
        totalView = view;
        totalView.setFocusableInTouchMode(true);
        totalView.requestFocus();
        totalView.setOnKeyListener(backListener);
        mContext = view.getContext();
        menuPopupWindow = new ToolsPopupWindow(mContext, null);

        ((View) view.findViewById(R.id.right_line)).setVisibility(View.GONE);
        ((Button) view.findViewById(R.id.btnToolsPopMenu)).setVisibility(View.GONE);


        initView(view);

        screenHeight = AppUtils.getScreenHeight(mContext);
        screenWidth = AppUtils.getScreenWidth(mContext);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        lp.width = (AppUtils.getScreenWidth(mContext) / 5);
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
        currentBarPlaceVW = (View) view.findViewById(R.id.bar_cnt);

        pickFromAlbumbtn = (Button) view.findViewById(R.id.step1_photo_select_btn);
        takeCameraBtn = (Button) view.findViewById(R.id.step1_take_camera_btn);
        step1SkipBtn = (Button) view.findViewById(R.id.step1_skip_btn);

        btnBack.setText(R.string.exit);

        btnBack.setOnClickListener(btnListener);
        btnPopMenu.setOnClickListener(btnListener);
        pickFromAlbumbtn.setOnClickListener(btnListener);
        takeCameraBtn.setOnClickListener(btnListener);
        step1SkipBtn.setOnClickListener(btnListener);

//        mission1Photo = (ImageView) view.findViewById(R.id.mission1_photo);

    }


    private View.OnClickListener btnListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Fragment fragment;
            switch (view.getId()) {
                case R.id.btnBack:
                    callSystemTime();
                    append = currentTime + "," +
                            UUIDChose + "," +
                            "DiaryMissionStep1" + "," +
                            "WalkWayListDetailInfo" + "," +
                            "btnBack" + "\n";
                    transactionLogSave(append);
//                    getActivity().finish();
//                    popUpQuickSaveAlert();
//                    AlertDialog dialog = AppUtils.saveAlertDialog(getActivity());
//                    dialog.setCo
                    exitBtnEvent();
                    break;
                case R.id.btnToolsPopMenu:
                    menuPopupWindow.showAsDropDown(navigationBar, 0, 0);
                    break;
                case R.id.step1_photo_select_btn:
                    callSystemTime();
                    append = currentTime + "," +
                            UUIDChose + "," +
                            "DiaryMissionStep1" + "," +
                            "selectPhotoFromAlbum" + "," +
                            "btnSelectPhotoFromAlbum" + "\n";
                    transactionLogSave(append);
                    selectPhotoFromAlbum();
                    break;
                case R.id.step1_take_camera_btn:
                    callSystemTime();
                    append = currentTime + "," +
                            UUIDChose + "," +
                            "DiaryMissionStep1" + "," +
                            "TakeCamera" + "," +
                            "btnTakeCamera" + "\n";
                    transactionLogSave(append);
                    photoFileName = AppUtils.getCameraSystemTimeFileName(AppConfig.JPEG_FILE_PREFIX) + AppConfig.JPEG_FILE_SUFFIX;
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    extraOutputFilePathUri = AppUtils.getPhotoUri(photoFileName);
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, extraOutputFilePathUri);
                    if (cameraIntent.resolveActivity(mContext.getPackageManager()) != null) {
                        startActivityForResult(cameraIntent, AppConfig.CAMERA_REQUEST);
                    } else {
                        AppUtils.getAlertDialog(mContext, "照相app").show();
                    }

                    break;
                case R.id.step1_skip_btn:
                    callSystemTime();
                    append = currentTime + "," +
                            UUIDChose + "," +
                            "DiaryMissionStep1" + "," +
                            "DiaryMissionStep2" + "," +
                            "btnStep1Skip" + "\n";
                    transactionLogSave(append);
                    fragment = new DiaryMissionStep2();
                    if (fragment != null) {

                        sharedPreferences.edit()
                                .putBoolean(AppConfig.PREF_DIARYSTEP1_EDIT_KEY, false)
                                .commit();

                        getFragmentManager().beginTransaction()
                                .replace(R.id.diary_frame_container, fragment, "DiaryMissionStep2")
                                .addToBackStack("DiaryMissionStep2")
                                .commit();
                    } else {
                        Log.e(TAG, "Error in creating fragment");
                    }
                    break;

            }
        }
    };

    /**
     * select photo from album
     */
    private void selectPhotoFromAlbum() {
        Intent intent = new Intent();
//        intent.setType("image/*");
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        startActivityForResult(intent, AppConfig.ALBUMS_REQUEST);

        if (Build.VERSION.SDK_INT < 19) {
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
        Fragment fragment = new DiaryMissionStep1Result();
        Log.i(TAG, "requestCode:" + requestCode);
        Log.i(TAG, "resultCode:" + resultCode);
        Log.i(TAG, "data:" + data);

        Bundle bundle = new Bundle();
        //藉由requestCode判斷是否為開啟相機或開啟相簿而呼叫的，且data不為null
        if ((requestCode == AppConfig.CAMERA_REQUEST) && (resultCode == Activity.RESULT_OK)) {

            Log.i(TAG, "deal CAMERA_REQUEST");
//            Bundle bundle = new Bundle();
//            takenPhotoUri = AppUtils.getPhotoUri(photoFileName);
            takenPhotoUri = extraOutputFilePathUri;
            bundle.putString(AppConfig.SELECT_PIC_SELECT_PATH_KEY, takenPhotoUri.getPath());
            bundle.putInt(AppConfig.PHOTO_CATEGORY_SOURCE, AppConfig.CAMERA_REQUEST);
//            saveShowImageAndTransactionSizePhoto(takenPhotoUri.getPath());

            Log.i(TAG, "takenPhotoUri.getPath():" + takenPhotoUri.getPath());
            Log.i(TAG, "takenPhotoUri:" + takenPhotoUri);

            saveTransportPhoto(takenPhotoUri.getPath());


        } else if ((requestCode == AppConfig.ALBUMS_REQUEST) && (resultCode == Activity.RESULT_OK)) {
            Log.i(TAG, "deal ALBUMS_REQUEST");
            takenPhotoUri = Uri.parse(PickPhotoUtility.getPath(mContext, data.getData()));
            bundle.putString(AppConfig.SELECT_PIC_SELECT_PATH_KEY, takenPhotoUri.toString());
            bundle.putInt(AppConfig.PHOTO_CATEGORY_SOURCE, AppConfig.ALBUMS_REQUEST);
            saveTransportPhoto(takenPhotoUri.toString());
        }

        fragment.setArguments(bundle);

        if ((fragment != null) && (resultCode == Activity.RESULT_OK)) {

            sharedPreferences.edit()
                    .putBoolean(AppConfig.PREF_DIARYSTEP1_EDIT_KEY, true)
                    .commit();
            fragmentTransaction
                    .replace(R.id.diary_frame_container, fragment, "DiaryMissionStep1Result")
                    .addToBackStack("DiaryMissionStep1Result")
                    .commit();
        } else {
//            AppUtils.getAlertDialog(mContext, getString(R.string.nochoose_photo)).show();
            Log.e(TAG, "Error in creating fragment");
        }

    }

    private void exitBtnEvent() {

        setToSharePreferences(AppConfig.PREF_DIARY_PRE_WALKWAY_NAME_KEY, "");
        setToSharePreferences(AppConfig.PREF_DIARY_PRE_WALKWAY_ID_KEY, "");
        getActivity().finish();
    }


    private void saveTransportPhoto(String path) {
        reqPhotoWidth = reqPhotoHeight = AppConfig.PHOTO_SIZE;
//        tempBitmap = AppUtils.rotateAndResizeBitmap(Uri.parse(path), reqPhotoWidth, reqPhotoHeight,mContext);
        tempBitmap = AppUtils.rotateAndResizeBitmap(path, reqPhotoWidth, reqPhotoHeight);

        Log.i(TAG, "System.currentTimeMillis():" + System.currentTimeMillis());
        Log.i(TAG, "file name 1:" + UUIDChose + "1" + System.currentTimeMillis());
        Log.i(TAG, "date:" + AppUtils.getPhotoDate(path));
        Log.i(TAG, "tempBitmap:" + tempBitmap);
        String fileName = UUIDChose + "1" + System.currentTimeMillis();


        if (sharedPreferences.getString(AppConfig.WALKWAY_CAMERA_DATE, "").equals("")) {
            setToSharePreferences(AppConfig.WALKWAY_CAMERA_DATE, AppUtils.getPhotoDate(path));
        }

        setToSharePreferences(AppConfig.JPEG_TRANSFILE_NAME_STEP1_KEY, fileName);
        AppUtils.savePhotoToSD(mContext, AppConfig.APP_PATH_SD_CARD, AppConfig.APP_TRANSPORT_PATH_SD_CARD, fileName, tempBitmap);
    }


    private void setToSharePreferences(String key, String val) {
        sharedPreferences.edit()
                .putString(key, val)
                .commit();
    }

    private View.OnKeyListener backListener = new View.OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    Log.i(TAG, TAG + " click back key");
                    callSystemTime();
                    append = currentTime + "," +
                            UUIDChose + "," +
                            "DiaryMissionStep1" + "," +
                            "WalkWayListDetailInfo" + "," +
                            "btnKeyCodeBack" + "\n";
                    transactionLogSave(append);
//                    TextView tv = new TextView(mContext);
//                    tv.setText(getString(R.string.check_exit));
//                    tv.setTextSize(30);
//                    tv.setPadding(30, 10, 10, 10);
//                    ContextThemeWrapper contextThemeWrapper = new ContextThemeWrapper(mContext, R.style.customAlertDialog);
//                    new AlertDialog.Builder(contextThemeWrapper)
//                            .setCustomTitle(tv)
//                            .setPositiveButton(getString(R.string.yes),
//                                    new DialogInterface.OnClickListener() {
//                                        public void onClick(DialogInterface dialog, int whichButton) {
//                                            setToSharePreferences(AppConfig.PREF_DIARY_PRE_WALKWAY_NAME_KEY, "");
//                                            setToSharePreferences(AppConfig.PREF_DIARY_PRE_WALKWAY_ID_KEY, "");
//                                            getActivity().finish();
//                                        }
//                                    }
//                            )
//                            .setNegativeButton(getString(R.string.no),
//                                    new DialogInterface.OnClickListener() {
//                                        public void onClick(DialogInterface dialog, int whichButton) {
//                                            dialog.dismiss();
//                                        }
//                                    }
//                            )
//                            .create()
//                            .show();
                    exitBtnEvent();
                    return true;
                }
            }
            return false;
        }
    };


    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();


        if (tempBitmap != null) {
            tempBitmap.recycle();
            tempBitmap = null;
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
