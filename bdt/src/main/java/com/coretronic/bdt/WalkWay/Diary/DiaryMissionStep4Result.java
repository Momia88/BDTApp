package com.coretronic.bdt.WalkWay.Diary;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.coretronic.bdt.AppConfig;
import com.coretronic.bdt.AppUtils;
import com.coretronic.bdt.R;
import com.coretronic.bdt.Utility.CustomerTwoBtnAlertDialog;
import com.coretronic.bdt.WalkWay.Module.ToolsPopupWindow;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by james on 14/11/7.
 */
public class DiaryMissionStep4Result extends Fragment {
    // constant
    private final String TAG = DiaryMissionStep4Result.class.getSimpleName();

    // action bar
    private RelativeLayout navigationBar = null;
    private PopupWindow menuPopupWindow;
    private Button btnBack = null;
    private Button btnPopMenu = null;
    private TextView barTitle = null;

    // ui
    private LinearLayout missionStep4ResBg = null;
    private Button mission4ResNextBtn = null;
    private Button mission4ResBackBtn = null;
    private Button sentenceBtn = null;
    private ImageView mission4ResPhoto = null;
    private ScrollView scrollView = null;
    private EditText mission4ResET = null;
    private View currentBarPlaceVW = null;

    // data
    private Context mContext;
    private SharedPreferences sharedPreferences;
    private int reqPhotoHeight = 0;
    private int reqPhotoWidth = 0;
    private int screenWidth = 0;
    private int screenHeight = 0;
    private Bitmap tempBitmap = null;
    private int photoCategory = 0;
    private String fileName="";
    private CustomerTwoBtnAlertDialog askSaveAlertDialog = null;
    private CustomerTwoBtnAlertDialog preStepSaveAlertDialog = null;
    //transactionLog
    private String currentTime;
    private String append;
    private String UUIDChose;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getActivity().getSharedPreferences(AppConfig.SHAREDPREFERENCES_NAME, 0);
        screenHeight = sharedPreferences.getInt(AppConfig.PREF_SCREEN_HEIGHT, 0);
        screenWidth = sharedPreferences.getInt(AppConfig.PREF_SCREEN_WIDTH, 0);
        fileName = sharedPreferences.getString(AppConfig.JPEG_TRANSFILE_NAME_STEP4_KEY,"");
        sharedPreferences.edit()
                .putInt(AppConfig.PREF_DIARY_CURRENT_STATUS_KEY, AppConfig.PREF_DIARYSTEP4EDIT_STATUS_ID)
                .commit();
        UUIDChose = sharedPreferences.getString(AppConfig.PREF_UNIQUE_ID, "");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.diary_mission_step4_result, container, false);
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(backListener);
        mContext = view.getContext();
        menuPopupWindow = new ToolsPopupWindow(mContext, null);
        initView(view);

        sharedPreferences.edit()
                .putBoolean(AppConfig.PREF_DIARYSTEP4_EDIT_KEY,true)
                .commit();

        ((View) view.findViewById(R.id.right_line)).setVisibility(View.GONE);
        ((Button) view.findViewById(R.id.btnToolsPopMenu)).setVisibility(View.GONE);

        setDefaultContent();

        askSaveAlertDialog = AppUtils.getAlertDialog(mContext,
                getResources().getString(R.string.diary_save_alert),
                getResources().getString(R.string.save),
                getResources().getString(R.string.not_save),
                askSaveAlertPositineveListener,
                askSaveAlertNegativeListener);


        preStepSaveAlertDialog = AppUtils.getAlertDialog(mContext,
                getResources().getString(R.string.click_back_alert),
                getResources().getString(R.string.diary_back),
                getResources().getString(R.string.cancel),
                preStepSaveAlertPositineveListener,
                preStepSaveAlertNegativeListener);


        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT );
        lp.width = (AppUtils.getScreenWidth(mContext) / 5) * 4;
        currentBarPlaceVW.setLayoutParams(lp);
        lp = null;

        Bundle bundle = getArguments();
        if (bundle != null) {
            String path = bundle.getString(AppConfig.SELECT_PIC_SELECT_PATH_KEY);
            photoCategory = bundle.getInt(AppConfig.PHOTO_CATEGORY_SOURCE,3);
            Log.d(TAG, "image path: " + path);
            Log.d(TAG, "photoCategory: " + photoCategory);
            if( (photoCategory ==  AppConfig.CAMERA_REQUEST) || (photoCategory ==  AppConfig.ALBUMS_REQUEST) )
            {
                addPhoto(path);

            }
            else if ( photoCategory == AppConfig.DIRECT_REQUEST_RECORD)
            {
                String fullPath = mContext.getExternalCacheDir()+  AppConfig.APP_PATH_SD_CARD + AppConfig.APP_TRANSPORT_PATH_SD_CARD +"/"+fileName+".png";
                Log.i(TAG, "direct request photo fullPath:"+fullPath);
                addPhoto(fullPath);
            }
            else
            {
                setNoChoosePhoto();
            }

        } else {
            setNoChoosePhoto();
        }

        Log.i(TAG,"sharedPreferences.getString(AppConfig.PREF_DIARY_PRE_WALKWAY_ID_KEY):"+sharedPreferences.getString(AppConfig.PREF_DIARY_PRE_WALKWAY_ID_KEY, ""));
        return view;
    }

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


    private void setDefaultContent() {
        String defaultTxt = sharedPreferences.getString(AppConfig.PREF_DIARYSTEP4_EDIT_CONTENT_KEY, "");
        if (!defaultTxt.equals("")) {
            mission4ResET.setText(defaultTxt);
        }
    }


    private void initView(View view) {

        // navigation bar
        navigationBar = (RelativeLayout) view.findViewById(R.id.navigationBar_tools);
        btnBack = (Button) view.findViewById(R.id.btnBack);
        btnPopMenu = (Button) view.findViewById(R.id.btnToolsPopMenu);
        barTitle = (TextView) view.findViewById(R.id.action_bar_title);

        // ui
        missionStep4ResBg = (LinearLayout) view.findViewById(R.id.mission_step4_res_bg);
        mission4ResPhoto = (ImageView) view.findViewById(R.id.mission4_res_photo);
        mission4ResNextBtn = (Button) view.findViewById(R.id.mission4_res_next_btn);
        mission4ResBackBtn = (Button) view.findViewById(R.id.mission4_res_back_btn);
        sentenceBtn = (Button) view.findViewById(R.id.sentenceBtn);
        scrollView = (ScrollView) view.findViewById(R.id.scrollView);
        mission4ResET = (EditText) view.findViewById(R.id.mission4_res_et);
        currentBarPlaceVW = (View) view.findViewById(R.id.bar_cnt);

        btnBack.setText(R.string.exit);

        // listener
        btnBack.setOnClickListener(btnListener);
        btnPopMenu.setOnClickListener(btnListener);
        sentenceBtn.setOnClickListener(btnListener);
        mission4ResNextBtn.setOnClickListener(btnListener);
        mission4ResBackBtn.setOnClickListener(btnListener);

        recordEditTextPosition();
    }

    /**
     * record edittext text position
     */
    private void recordEditTextPosition() {
        mission4ResET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                sharedPreferences.edit()
                        .putString(AppConfig.PREF_DIARYSTEP4_EDIT_CONTENT_KEY, s.toString())
                        .commit();
                Log.i(TAG, "on changed:" + s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.i(TAG, "s.toString():" + s.toString());
            }
        });
    }


    private View.OnClickListener btnListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Fragment fragment;
            switch (view.getId()) {
                case R.id.btnBack:
//                    getFragmentManager().popBackStackImmediate();
                    callSystemTime();
                    append = currentTime + "," +
                            UUIDChose + "," +
                            "DiaryMissionStep4Result" + "," +
                            "BtnBackAskDialog" + "," +
                            "btnBack" + "\n";
                    transactionLogSave(append);
                    exitBtnEvent();
                    break;

                case R.id.btnToolsPopMenu:
                    menuPopupWindow.showAsDropDown(navigationBar, 0, 0);
                    break;

                case R.id.mission4_res_back_btn:
                    callSystemTime();
                    append = currentTime + "," +
                            UUIDChose + "," +
                            "DiaryMissionStep4Result" + "," +
                            "ResBackAskDialog" + "," +
                            "btnMission4ResBack" + "\n";
                    transactionLogSave(append);
                    popupBackCheckDialog();
                    break;

                case R.id.mission4_res_next_btn:
                    callSystemTime();
                    append = currentTime + "," +
                            UUIDChose + "," +
                            "DiaryMissionStep4Result" + "," +
                            "DiaryMissionStep5" + "," +
                            "btnMission4ResNext" + "\n";
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
                case R.id.sentenceBtn:
                    callSystemTime();
                    append = currentTime + "," +
                            UUIDChose + "," +
                            "DiaryMissionStep4Result" + "," +
                            "DiaryMissionStep4ResultSentence" + "," +
                            "btnSentence" + "\n";
                    transactionLogSave(append);
                    popupSentenceList();
                    break;

            }
        }
    };


    private void setNoChoosePhoto() {

        reqPhotoHeight = screenHeight;
        reqPhotoWidth = screenWidth;
         tempBitmap = AppUtils.decodeBitmapFromResource(mContext.getResources(),R.drawable.ic_launcher,(reqPhotoWidth / 5) * 4,(reqPhotoHeight / 5) * 4);
        if (tempBitmap != null) {
            mission4ResPhoto.setVisibility(View.VISIBLE);
            mission4ResPhoto.setImageBitmap(tempBitmap);

        }
    }


    /**
     * add photo to imageview
     *
     * @param path pass the photo path
     */
    private void addPhoto(String path) {

        reqPhotoHeight = screenHeight;
        reqPhotoWidth = screenWidth;

        Boolean isOrientation = false;

        if ((ExifInterface.ORIENTATION_ROTATE_90 == AppUtils.checkImageOrientation(path)) ||
                ((ExifInterface.ORIENTATION_ROTATE_180 == AppUtils.checkImageOrientation(path))))
        {
            isOrientation = true;
        }


        // 手機以直立拍攝的情況
        if (isOrientation == true) {

            reqPhotoWidth = (int) ((reqPhotoHeight / 5) * 4);
            reqPhotoHeight = (int) ((reqPhotoWidth / 5) * 4);

        } else {
            reqPhotoWidth = (int) ((reqPhotoWidth / 5) * 4);
            reqPhotoHeight = (int) ((reqPhotoHeight / 5) * 4);

        }

        Log.i(TAG, " after operation reqPhotoWidth:" + reqPhotoWidth + "/reqPhotoHeight:" + reqPhotoHeight);

        tempBitmap = AppUtils.rotateAndResizeBitmap(path, reqPhotoWidth, reqPhotoHeight);


        if (tempBitmap != null) {
            mission4ResPhoto.setVisibility(View.VISIBLE);
            mission4ResPhoto.setImageBitmap(tempBitmap);
            Log.i(TAG, "bitmap.getWidth():" + tempBitmap.getWidth());
            Log.i(TAG, "bitmap.getHeight():" + tempBitmap.getHeight());
        } else {
            AppUtils.getAlertDialog(mContext,getString(R.string.load_photo_error)).show();
            preStepEvent();
            mission4ResPhoto.setVisibility(View.GONE);
        }

    }


    /**
     * popup the sentence list
     */
    private void popupSentenceList() {

        // custom alert builder dialog
        ContextThemeWrapper alertBuilderThemeCT =
                new ContextThemeWrapper(mContext, R.style.AlertDialogTheme);
        AlertDialog.Builder sentenceBuilder =
                new AlertDialog.Builder(alertBuilderThemeCT);

        sentenceBuilder.setItems(AppConfig.STEP4_SENTENCE_ARY, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {
                Log.i(TAG, "pick sentence item:" + AppConfig.STEP4_SENTENCE_ARY[item]);
                mission4ResET.getText().insert(mission4ResET.getSelectionStart(), AppConfig.STEP4_SENTENCE_ARY[item]);
            }
        });

        sentenceBuilder.setPositiveButton("取消", null);
        AlertDialog sentenceAlert = sentenceBuilder.create();
        // custom dialog button drawable
        sentenceAlert.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button sentenceBuilderPosBtn = ((AlertDialog) dialog)
                        .getButton(DialogInterface.BUTTON_POSITIVE);
                sentenceBuilderPosBtn.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.btn_shape_selector));
            }
        });

        // custom dialog title font size
        final TextView sentenceAlertTitleView = new TextView(mContext.getApplicationContext());
        sentenceAlertTitleView.setText("請選擇你想要的字句");
        sentenceAlertTitleView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 32);
        sentenceAlert.setCustomTitle(sentenceAlertTitleView);
        sentenceAlert.show();

    }

    public void popupBackCheckDialog() {
//        AppUtils.showSaveAlertDialog(mContext, null, getString(R.string.click_back_alert),
//                new AppUtils.DialogClickListener() {
//                    @Override
//                    public void onDone(String stringMetadata, boolean isPositive, EditText editText) {
//                        if (!isPositive) {
//                            Log.i(TAG, "save");
//                            sharedPreferences.edit()
//                                    .putInt(AppConfig.PREF_DIARY_CURRENT_STATUS_KEY, AppConfig.PREF_DIARYSTEP4EDIT_STATUS_ID)
//                                    .commit();
//                        } else {
//                            Log.i(TAG, "don't save");
//                            preStepEvent();
//                        }
//                    }
//                }, getString(R.string.diary_back), getString(R.string.cancel)).show();
        preStepSaveAlertDialog.show();
    }


    private View.OnClickListener preStepSaveAlertPositineveListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            callSystemTime();
            append = currentTime + "," +
                    UUIDChose + "," +
                    "ResBackAskDialog" + "," +
                    "DiaryMissionStep4" + "," +
                    "btnResBackAskDialogBack"+ "\n";
            transactionLogSave(append);
            preStepEvent();
            if (preStepSaveAlertDialog != null) {
                preStepSaveAlertDialog.dismiss();
            }


        }
    };

    private View.OnClickListener preStepSaveAlertNegativeListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.i(TAG, "not save");
            callSystemTime();
            append = currentTime + "," +
                    UUIDChose + "," +
                    "ResBackAskDialog" + "," +
                    "DiaryMissionStep4Result" + "," +
                    "btnResBackAskDialogCanael"+ "\n";
            transactionLogSave(append);
            sharedPreferences.edit()
                    .putInt(AppConfig.PREF_DIARY_CURRENT_STATUS_KEY, AppConfig.PREF_DIARYSTEP4EDIT_STATUS_ID)
                    .commit();

            if (preStepSaveAlertDialog != null) {
                preStepSaveAlertDialog.dismiss();
            }
        }
    };


    private void preStepEvent()
    {
        clearCurrentData();
        // replace fragment page

        Fragment fragment = new DiaryMissionStep4();
        if (fragment != null) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.diary_frame_container, fragment, "DiaryMissionStep4")
//                                            .addToBackStack("DiaryMissionStep2")
                    .commit();
        } else {
            Log.e(TAG, "Error in creating fragment");
        }
    }


    private void clearAllData() {

        // clear photo
        AppUtils.deletePhotoFromSD( mContext, AppConfig.APP_PATH_SD_CARD, AppConfig.APP_TRANSPORT_PATH_SD_CARD , fileName);

        // clear share preferences
        sharedPreferences.edit()
                .putString(AppConfig.PREF_DIARY_PRE_WALKWAY_NAME_KEY, "")
                .putString(AppConfig.PREF_DIARY_PRE_WALKWAY_ID_KEY, "")
                .putString(AppConfig.JPEG_TRANSFILE_NAME_STEP4_KEY, "")
                .putString(AppConfig.PREF_DIARYSTEP1_EDIT_CONTENT_KEY, "")
                .putString(AppConfig.PREF_DIARYSTEP2_EDIT_CONTENT_KEY, "")
                .putString(AppConfig.PREF_DIARYSTEP3_EDIT_CONTENT_KEY, "")
                .putString(AppConfig.PREF_DIARYSTEP4_EDIT_CONTENT_KEY, "")
                .putString(AppConfig.PREF_DIARYSTEP5_EDIT_CONTENT_KEY, "")
                .putInt(AppConfig.PREF_DIARY_CURRENT_STATUS_KEY, AppConfig.PREF_DIARYSTEP1_STATUS_ID)
                .putBoolean(AppConfig.PREF_DIARYSTEP4_EDIT_KEY, false)
                .commit();

    }

    private void clearCurrentData() {

        // clear photo
        AppUtils.deletePhotoFromSD( mContext, AppConfig.APP_PATH_SD_CARD, AppConfig.APP_TRANSPORT_PATH_SD_CARD , fileName);
        // clear share preferences
        sharedPreferences.edit()
                .putString(AppConfig.JPEG_TRANSFILE_NAME_STEP4_KEY, "")
                .putString(AppConfig.PREF_DIARYSTEP4_EDIT_CONTENT_KEY, "")
                .putString(AppConfig.PREF_DIARYSTEP5_EDIT_CONTENT_KEY, "")
                .putBoolean(AppConfig.PREF_DIARYSTEP4_EDIT_KEY, false)
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
                            "DiaryMissionStep4Result" + "," +
                            "ResBackAskDialog" + "," +
                            "btnKeyCodeBack" + "\n";
                    transactionLogSave(append);
                    popupBackCheckDialog();
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
            clearAllData();
            askSaveAlertDialog.dismiss();
            getActivity().finish();
        }
    };


    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
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
