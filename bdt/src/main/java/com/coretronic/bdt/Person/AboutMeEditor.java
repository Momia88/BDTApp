package com.coretronic.bdt.Person;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.coretronic.bdt.AppConfig;
import com.coretronic.bdt.AppUtils;
import com.coretronic.bdt.Person.Module.Person;
import com.coretronic.bdt.Person.Module.RegisterAlertDialog;
import com.coretronic.bdt.R;
import com.coretronic.bdt.Utility.CustomerOneBtnAlertDialog;
import com.coretronic.bdt.Utility.CustomerTwoBtnAlertDialog;
import com.coretronic.bdt.Utility.CustomerTwoBtnWithEditorDialog;
import com.coretronic.bdt.module.ChineseDatePickerDialog;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Morris on 14/11/25.
 */
public class AboutMeEditor extends Fragment {
    private static String TAG = AboutMeEditor.class.getSimpleName();
    private Button btnBack;
    private TextView barTitle;
    private Context mContext;
    private RelativeLayout navigationBar;
    private SharedPreferences sharedPreferences;
    private ProgressDialog progressDialog = null;
    private String uuidChose;
    private AsyncHttpClient asyncHttpClient;
    private Gson gson = new Gson();
    private ChineseDatePickerDialog chineseDatePickerDialog;

    private ImageView mMyPhoto;
    private TextView mPpRegisterUserName;
    private TextView mPpRegisterBirthday;
    private TextView mPpRegisterSex;
    private TextView mPpRegisterAddress;
    private Button mPpBtnSumit;
    private CharSequence[] items = {"男", "女"};
    private AlertDialog.Builder builder;
    private RegisterAlertDialog registerAlertDialog;

    private final int PHOTO_REQUEST_TAKEPHOTO = 1;
    private final int PHOTO_REQUEST_GALLERY = 2;
    private final int PHOTO_REQUEST_CUT = 3;
    private File userLogo = null;
    private String cameraFileName = null;
    private String fileName = null;
    private String userLogoForder = null;
    private Uri extraOutputFilePathUri = null;
    private Person personInfo;
    private CustomerTwoBtnWithEditorDialog inputDialog;
    private CustomerTwoBtnAlertDialog twoBtnAlertDialog;
    private CustomerOneBtnAlertDialog oneBtnAlertDialog;
    //transactionLog
    private String currentTime;
    private String append;

    public JsonHttpResponseHandler jsonHandler = new JsonHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
            Log.d(TAG, "onSuccess = " + response);
            try {
                if (response.get("msgCode").equals("A01")) {
                    Toast.makeText(mContext, response.get("result").toString(), 1).show();
                    Fragment fragment = new PersonMenu();
                    if (fragment != null) {
                        getFragmentManager().beginTransaction()
                                .replace(R.id.person_frame_container, fragment, "PersionMenu")
                                .commit();
                    } else {
                        Log.e(TAG, "Error in creating fragment");
                    }
                } else {
                    oneBtnAlertDialog = AppUtils.getAlertDialog(mContext, response.get("result").toString());
                    oneBtnAlertDialog.show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (JsonSyntaxException e) {
                oneBtnAlertDialog = AppUtils.getAlertDialog(mContext, getString(R.string.data_result_error));
                oneBtnAlertDialog.show();
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            Log.d(TAG, "onFailure");
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
            oneBtnAlertDialog = AppUtils.getAlertDialog(mContext, getString(R.string.data_load_error));
            oneBtnAlertDialog.show();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getActivity().getSharedPreferences(AppConfig.SHAREDPREFERENCES_NAME, 0);
        asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.setTimeout(AppConfig.TIMEOUT);
        uuidChose = sharedPreferences.getString(AppConfig.PREF_UNIQUE_ID, "");
        personInfo = new Person();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");
        View v = inflater.inflate(R.layout.person_about_me_edit, container, false);
        mContext = v.getContext();
        progressDialog = new ProgressDialog(mContext);
        inputDialog = new CustomerTwoBtnWithEditorDialog(mContext);
        twoBtnAlertDialog = new CustomerTwoBtnAlertDialog(mContext);
        chineseDatePickerDialog = new ChineseDatePickerDialog(mContext, dialogOnClickListener);
        initView(v);
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    private void initView(View v) {
        // navigation bar
        navigationBar = (RelativeLayout) v.findViewById(R.id.navigationBar_option);
        btnBack = (Button) v.findViewById(R.id.btnBack);
        barTitle = (TextView) v.findViewById(R.id.action_bar_title);

        barTitle.setText(getString(R.string.pp_register_title));
        btnBack.setOnClickListener(btnListener);

        mPpRegisterUserName = (TextView) v.findViewById(R.id.pp_register_user_name);
        mPpRegisterBirthday = (TextView) v.findViewById(R.id.pp_register_birthday);
        mPpRegisterSex = (TextView) v.findViewById(R.id.pp_register_sex);
        mPpRegisterAddress = (TextView) v.findViewById(R.id.pp_register_address);
        mPpBtnSumit = (Button) v.findViewById(R.id.pp_btn_login_sumit);
        mMyPhoto = (ImageView) v.findViewById(R.id.pp_img_user_photo);

        mPpRegisterUserName.setOnClickListener(btnListener);
        mPpRegisterBirthday.setOnClickListener(btnListener);
        mPpRegisterSex.setOnClickListener(btnListener);
        mPpRegisterAddress.setOnClickListener(btnListener);
        mPpBtnSumit.setOnClickListener(btnListener);
        mMyPhoto.setOnClickListener(btnListener);

        userLogoForder = mContext.getExternalCacheDir() + AppConfig.JPEG_USER_LOGO_FOLDER;
        File dir = new File(userLogoForder);
        Log.i(TAG, "userLogoForder:" + userLogoForder);

        if (!dir.exists()) {
            Log.i(TAG, "userLogoForder:" + userLogoForder);
            dir.mkdirs();
        }
        userLogo = new File(userLogoForder, uuidChose + ".png");
        setDefaultUserInfo();

    }


    private View.OnClickListener btnListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            final EditText input = new EditText(mContext);
            Dialog dialog = null;
            switch (view.getId()) {
                case R.id.btnBack:
                    callSystemTime();
                    append = currentTime + "," +
                            uuidChose + "," +
                            "AboutMeEditor" + "," +
                            "AboutMe" + "," +
                            "btnBack" + "\n";
                    transactionLogSave(append);
                    getFragmentManager().popBackStackImmediate();
                    break;
                case R.id.pp_register_birthday:
                    callSystemTime();
                    append = currentTime + "," +
                            uuidChose + "," +
                            "AboutMeEditor" + "," +
                            "RegisterBirthdayDialog" + "," +
                            "btnRegisterBirthday" + "\n";
                    transactionLogSave(append);
                    chineseDatePickerDialog.show();
                    break;
                case R.id.pp_register_user_name:
                    callSystemTime();
                    append = currentTime + "," +
                            uuidChose + "," +
                            "AboutMeEditor" + "," +
                            "RegisterUserNameDialog" + "," +
                            "btnRegisterUserName" + "\n";
                    transactionLogSave(append);
                    inputDialog.setTitle("請輸入姓名")
                            .setValue(personInfo.getName())
                            .setPositiveBtnText("確定")
                            .setPositiveListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    inputDialog.dismiss();
                                    if (inputDialog.getInputMsg() != null) {
                                        callSystemTime();
                                        append = currentTime + "," +
                                                uuidChose + "," +
                                                "RegisterUserNameDialog" + "," +
                                                "AboutMeEditor" + "," +
                                                "btnDetermine" + "\n";
                                        transactionLogSave(append);
                                        personInfo.setName(inputDialog.getInputMsg().toString());
                                        mPpRegisterUserName.setText(personInfo.getName());
                                    }
                                }
                            }).show();
                    break;
                case R.id.pp_register_sex:
                    callSystemTime();
                    append = currentTime + "," +
                            uuidChose + "," +
                            "AboutMeEditor" + "," +
                            "RegisterSexDialog" + "," +
                            "btnRegisterSex" + "\n";
                    transactionLogSave(append);
                    int sex = -1;
                    if (mPpRegisterSex.getText().equals(items[0])) {
                        sex = 0;
                    } else if (mPpRegisterSex.getText().equals(items[1])) {
                        sex = 1;
                    }
                    builder = new AlertDialog.Builder(mContext);
                    builder.setTitle("請選擇性別");
                    builder.setSingleChoiceItems(items, sex, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {
                            dialog.dismiss();
                            if (items[i] != null) {
                                append = currentTime + "," +
                                        uuidChose + "," +
                                        "RegisterSexDialog" + "," +
                                        "AboutMeEditor" + "," +
                                        "btn" +items[i].toString()+ "\n";
                                transactionLogSave(append);
                                personInfo.setSex(items[i].toString());
                                mPpRegisterSex.setText(personInfo.getSex());
                            }
                        }
                    });
                    dialog = builder.create();
                    dialog.show();
                    break;

                case R.id.pp_register_address:
                    callSystemTime();
                    append = currentTime + "," +
                            uuidChose + "," +
                            "AboutMeEditor" + "," +
                            "RegisterAddressDialog" + "," +
                            "btnRegisterAddress" + "\n";
                    transactionLogSave(append);
                    final String[] citys = AppConfig.CITY;
                    builder = new AlertDialog.Builder(mContext);
                    builder.setTitle("請選擇居住地");
                    int index = -1;
                    for (int i = 0; i < citys.length; i++) {
                        if (mPpRegisterAddress.getText().equals(citys[i])) {
                            index = i;
                            continue;
                        }
                    }
                    builder.setSingleChoiceItems(citys, index, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {
                            dialog.dismiss();
                            if (citys[i] != null) {
                                append = currentTime + "," +
                                        uuidChose + "," +
                                        "RegisterAddressDialog" + "," +
                                        "AboutMeEditor" + "," +
                                        "btn" +citys[i].toString()+ "\n";
                                transactionLogSave(append);
                                personInfo.setAddress(citys[i].toString());
                                mPpRegisterAddress.setText(personInfo.getAddress());
                            }
                        }
                    });
                    dialog = builder.create();
                    dialog.show();
                    break;

                case R.id.pp_btn_login_sumit:
                    callSystemTime();
                    append = currentTime + "," +
                            uuidChose + "," +
                            "AboutMeEditor" + "," +
                            "Person" + "," +
                            "btnRegisterComplete" + "\n";
                    transactionLogSave(append);
                    sendUserUpdateRequest();
                    break;
                case R.id.pp_img_user_photo:
                    callSystemTime();
                    append = currentTime + "," +
                            uuidChose + "," +
                            "AboutMeEditor" + "," +
                            "RegisterUserPhotoDialog" + "," +
                            "btnRegisterUserPhoto" + "\n";
                    transactionLogSave(append);
                    changeUserPicDialog();
                break;
            }
        }
    };

    View.OnClickListener dialogOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            chineseDatePickerDialog.dismiss();
            int year = chineseDatePickerDialog.getYearNow();
            int month = chineseDatePickerDialog.getMonthNow();
            int day = chineseDatePickerDialog.getDayNow();
            String birth = "民國 " + year + " 年 " + month + " 月 " + day + " 日";
            mPpRegisterBirthday.setText(birth);
            String str = (year + 1911) + "-" + month + "-" + day;
            personInfo.setBirthday(str);
        }
    };

    private String checkInfo() {
        if (mPpRegisterUserName.getText().toString().trim().length() == 0) {
            return "姓名";
        } else if (mPpRegisterBirthday.getText().toString().trim().length() == 0) {
            return "生日";
        } else if (mPpRegisterSex.getText().toString().trim().length() == 0) {
            return "性別";
        } else if (mPpRegisterAddress.getText().toString().trim().length() == 0) {
            return "地址";
        }
        return null;
    }


    private void setDefaultUserInfo() {
        try {
            String pName = sharedPreferences.getString(AppConfig.PREF_USER_NANE, "");
            String pBirth = sharedPreferences.getString(AppConfig.PREF_USER_BIRTHDAY, "");
            String pSex = sharedPreferences.getString(AppConfig.PREF_USER_SEX, "");
            String pAddress = sharedPreferences.getString(AppConfig.PREF_USER_ADDRESS, "");
            String pThumb = sharedPreferences.getString(AppConfig.PREF_USER_THUMB, "");
            personInfo.setName(pName);
            personInfo.setBirthday(pBirth);
            personInfo.setSex(pSex);
            personInfo.setAddress(pAddress);
            personInfo.setThumb(pThumb);

            mPpRegisterUserName.setText(pName);
            if (pBirth.trim().length() > 0) {
                mPpRegisterBirthday.setText(AppUtils.dateToChineseDate(pBirth));
            }
            mPpRegisterSex.setText(pSex);
            mPpRegisterAddress.setText(pAddress);
            Log.d(TAG, pThumb);
            if (pThumb.trim().length() > 0) {
                mMyPhoto.setImageBitmap(AppUtils.base64ToBitmap(pThumb));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setTempUserInfo() {

        try {
            mPpRegisterUserName.setText(personInfo.getName());
            mPpRegisterBirthday.setText(AppUtils.dateToChineseDate(personInfo.getBirthday()));
            mPpRegisterSex.setText(personInfo.getSex());
            mPpRegisterAddress.setText(personInfo.getAddress());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult");
        switch (requestCode) {
            case PHOTO_REQUEST_TAKEPHOTO:
                Log.d(TAG, "PHOTO_REQUEST_TAKEPHOTO");
                try {
//                    File f = saveTransportPhoto(extraOutputFilePathUri.getPath());
//                    if (f != null) {
//                        startPhotoZoom(Uri.fromFile(f), 150);
//                    }
                    startPhotoZoom(extraOutputFilePathUri, 150);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;

            case PHOTO_REQUEST_GALLERY:
                Log.d(TAG, "PHOTO_REQUEST_GALLERY");
                try {
                    if (data != null)
                        startPhotoZoom(data.getData(), 150);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case PHOTO_REQUEST_CUT:
                Log.d(TAG, "PHOTO_REQUEST_CUT");
                try {
                    if (userLogo != null) {
                        mMyPhoto.setImageBitmap(null);
                        mMyPhoto.setImageURI(Uri.fromFile(userLogo));
                        String str = AppUtils.bitmapTobase64(BitmapFactory.decodeFile(userLogo.getAbsolutePath()));
                        personInfo.setThumb(str);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (personInfo.getThumb() != null) {
                        if ("".equals(personInfo.getThumb())) {
                            mMyPhoto.setImageResource(R.drawable.pp_ic_img_default);
                        } else {
                            mMyPhoto.setImageBitmap(AppUtils.base64ToBitmap(personInfo.getThumb()));
                        }
                    }
                }
                setTempUserInfo();
                break;

        }
        super.onActivityResult(requestCode, resultCode, data);

    }

    //提示對話框方法
    private void changeUserPicDialog() {
        twoBtnAlertDialog.setMsg("頭像設置")
                .setPositiveBtnText("拍照")
                .setPositiveListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        twoBtnAlertDialog.dismiss();
                        callSystemTime();
                        append = currentTime + "," +
                                uuidChose + "," +
                                "RegisterUserPhotoDialog" + "," +
                                "TakeCamera" + "," +
                                "btnTakeCamera" + "\n";
                        transactionLogSave(append);
                        inputDialog.dismiss();
                        // 調用系統的拍照功能
                        cameraFileName = AppUtils.getCameraSystemTimeFileName(AppConfig.JPEG_FILE_PREFIX) + AppConfig.JPEG_FILE_SUFFIX;
                        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        extraOutputFilePathUri = AppUtils.getPhotoUri(cameraFileName);
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, extraOutputFilePathUri);
                        if (cameraIntent.resolveActivity(mContext.getPackageManager()) != null) {
                            startActivityForResult(cameraIntent, PHOTO_REQUEST_TAKEPHOTO);
                        } else {
                            Toast.makeText(mContext,"無照相app",1).show();
                        }
                    }
                })
                .setNegativeBtnText("相冊")
                .setNegativeListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        callSystemTime();
                        append = currentTime + "," +
                                uuidChose + "," +
                                "RegisterUserPhotoDialog" + "," +
                                "selectPhotoFromAlbum" + "," +
                                "btnSelectPhotoFromAlbum" + "\n";
                        transactionLogSave(append);
                        twoBtnAlertDialog.dismiss();
                        Intent intent = new Intent(Intent.ACTION_PICK, null);
                        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                        startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
                    }
                })
                .show();
//        new AlertDialog.Builder(getActivity())
//                .setTitle("頭像設置")
//                .setPositiveButton("拍照", new DialogInterface.OnClickListener() {
//
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                        // 調用系統的拍照功能
//                        cameraFileName = AppUtils.getCameraSystemTimeFileName(AppConfig.JPEG_FILE_PREFIX) + AppConfig.JPEG_FILE_SUFFIX;
//                        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                        extraOutputFilePathUri = AppUtils.getPhotoUri(cameraFileName);
//                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, extraOutputFilePathUri);
//                        if (cameraIntent.resolveActivity(mContext.getPackageManager()) != null) {
//                            startActivityForResult(cameraIntent, PHOTO_REQUEST_TAKEPHOTO);
//                        } else {
//                            AppUtils.getAlertDialog(mContext, "無照相app").show();
//                        }
//                    }
//                })
//                .setNegativeButton("相冊", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                        Intent intent = new Intent(Intent.ACTION_PICK, null);
//                        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
//                        startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
//                    }
//                }).show();
    }

    private void startPhotoZoom(Uri uri, int size) {
        Log.d(TAG, "startPhotoZoom");
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // crop為true是設置在開啟的intent中設置顯示的view可以剪裁
        intent.putExtra("crop", "true");

        // aspectX aspectY 是寬高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);

        // outputX,outputY 是剪裁圖片的寬高
        intent.putExtra("outputX", size);
        intent.putExtra("outputY", size);
        intent.putExtra("scale", true);
        intent.putExtra("outputFormat", "PNG");// 圖片格式
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(userLogo));

        startActivityForResult(intent, PHOTO_REQUEST_CUT);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (chineseDatePickerDialog.isShowing()) {
            chineseDatePickerDialog.dismiss();
        }
        if (inputDialog != null) {
            inputDialog.dismiss();
        }
        if (twoBtnAlertDialog != null) {
            twoBtnAlertDialog.dismiss();
        }
        if (oneBtnAlertDialog != null) {
            oneBtnAlertDialog.dismiss();
        }
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (chineseDatePickerDialog.isShowing()) {
            chineseDatePickerDialog.dismiss();
        }
    }

    private File saveTransportPhoto(String path) {
        int reqPhotoWidth = 1024;
        int reqPhotoHeight = 1024;
        Bitmap tempBitmap = AppUtils.rotateAndResizeBitmap(path, reqPhotoWidth, reqPhotoHeight);
        fileName = AppUtils.getCameraSystemTimeFileName(AppConfig.JPEG_FILE_PREFIX) + AppConfig.JPEG_FILE_SUFFIX;
        return AppUtils.savePhotoToCacheDir(mContext, AppConfig.JPEG_USER_LOGO_FOLDER, fileName, tempBitmap);
    }

    private void sendUserUpdateRequest() {
        progressDialog.setMessage(getString(R.string.dialog_download_msg));
        progressDialog.show();

        final String url = AppConfig.USER_AUTH_SITE_PATE + AppConfig.USER_UPDATE;
        Log.i(TAG, "url:  " + url);

        uuidChose = sharedPreferences.getString(AppConfig.PREF_UNIQUE_ID, "");
        Log.i(TAG, "UID:  " + uuidChose);
        RequestParams params = new RequestParams();
        params.add("uid", uuidChose);
        params.add("time", AppUtils.getSystemTime());
        params.add("name", personInfo.getName());
        params.add("thumb", personInfo.getThumb());
        params.add("sex", personInfo.getSex());
        params.add("birthday", personInfo.getBirthday());
        params.add("address", personInfo.getAddress());

        Log.i(TAG, "params:  " + params);
        asyncHttpClient.post(url, params, jsonHandler);
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