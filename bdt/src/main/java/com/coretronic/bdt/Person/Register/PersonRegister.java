package com.coretronic.bdt.Person.Register;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
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
import com.loopj.android.http.AsyncHttpClient;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Morris on 14/11/25.
 */
public class PersonRegister extends Fragment {
    private static String TAG = PersonRegister.class.getSimpleName();

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
    private TextView mPpRegisterPhone;
    private Button mPpBtnLoginLogin;
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
    private SimpleDateFormat formatter;

    private Person personInfo;
    private CustomerTwoBtnWithEditorDialog inputDialog;
    private CustomerTwoBtnAlertDialog twoBtnAlertDialog;
    private CustomerOneBtnAlertDialog oneBtnAlertDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        sharedPreferences = getActivity().getSharedPreferences(AppConfig.SHAREDPREFERENCES_NAME, 0);
        asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.setTimeout(AppConfig.TIMEOUT);
        uuidChose = sharedPreferences.getString(AppConfig.PREF_UNIQUE_ID, "");
        personInfo = new Person();
        formatter = new SimpleDateFormat("yyyy-MM-dd");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View v = inflater.inflate(R.layout.person_register, container, false);
        mContext = v.getContext();
        progressDialog = new ProgressDialog(mContext);
        inputDialog = new CustomerTwoBtnWithEditorDialog(mContext);
        twoBtnAlertDialog = new CustomerTwoBtnAlertDialog(mContext);
        chineseDatePickerDialog = new ChineseDatePickerDialog(mContext, dialogOnClickListener);
        initView(v);

        return v;
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
                    if (data != null) {
                        startPhotoZoom(data.getData(), 150);
                    }
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

        mMyPhoto = (ImageView) v.findViewById(R.id.pp_img_user_photo);
        mPpRegisterUserName = (TextView) v.findViewById(R.id.pp_register_user_name);
        mPpRegisterBirthday = (TextView) v.findViewById(R.id.pp_register_birthday);
        mPpRegisterSex = (TextView) v.findViewById(R.id.pp_register_sex);
        mPpRegisterAddress = (TextView) v.findViewById(R.id.pp_register_address);
        mPpRegisterPhone = (TextView) v.findViewById(R.id.pp_register_phone);
        mPpBtnLoginLogin = (Button) v.findViewById(R.id.pp_btn_login_sumit);

        mPpRegisterUserName.setOnClickListener(btnListener);
        mPpRegisterBirthday.setOnClickListener(btnListener);
        mPpRegisterSex.setOnClickListener(btnListener);
        mPpRegisterAddress.setOnClickListener(btnListener);
        mPpRegisterPhone.setOnClickListener(btnListener);
        mPpBtnLoginLogin.setOnClickListener(btnListener);
        mMyPhoto.setOnClickListener(btnListener);

        // photo temp forder
        userLogoForder = mContext.getExternalCacheDir() + AppConfig.JPEG_USER_LOGO_FOLDER;
        File dir = new File(userLogoForder);
        Log.i(TAG, "userLogoForder:" + userLogoForder);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        userLogo = new File(userLogoForder, uuidChose + ".png");

        setDefaultUserInfo();
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

    private View.OnClickListener btnListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            final EditText input = new EditText(mContext);
            Dialog dialog = null;
            switch (view.getId()) {
                case R.id.btnBack:
                    getFragmentManager().popBackStackImmediate();
                    break;
                case R.id.pp_register_birthday:
                    String birth = personInfo.getBirthday();
                    try {
                        if (birth != null && birth.length() > 0) {
                            Date date = formatter.parse(personInfo.getBirthday());
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(date);
                            chineseDatePickerDialog.setDefaultDay(calendar.get(Calendar.YEAR) - 1911, calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));
                        }
                        chineseDatePickerDialog.show();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    break;
                case R.id.pp_register_user_name:
                    inputDialog.setTitle("請輸入姓名")
                            .setValue(personInfo.getName())
                            .setPositiveBtnText("確定")
                            .setPositiveListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    inputDialog.dismiss();
                                    if (inputDialog.getInputMsg() != null) {
                                        personInfo.setName(inputDialog.getInputMsg().toString());
                                        mPpRegisterUserName.setText(personInfo.getName());
                                    }
                                }
                            }).show();
                    break;
                case R.id.pp_register_sex:
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
                                personInfo.setSex(items[i].toString());
                                mPpRegisterSex.setText(personInfo.getSex());
                            }
                        }
                    });
                    dialog = builder.create();
                    dialog.show();
                    break;
                case R.id.pp_register_phone:
                    inputDialog.setTitle("請輸入電話")
                            .setValue(personInfo.getPhoneNum())
                            .setInputType(InputType.TYPE_CLASS_PHONE)
                            .setPositiveBtnText("確定")
                            .setPositiveListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    inputDialog.dismiss();
                                    if (inputDialog.getInputMsg() != null) {
                                        personInfo.setPhoneNum(inputDialog.getInputMsg().toString());
                                        mPpRegisterPhone.setText(personInfo.getPhoneNum());
                                    }
                                }
                            }).show();
                    break;

                case R.id.pp_register_address:
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
                                personInfo.setAddress(citys[i].toString());
                                mPpRegisterAddress.setText(personInfo.getAddress());
                            }
                        }
                    });
                    dialog = builder.create();
                    dialog.show();
                    break;

                case R.id.pp_btn_login_sumit:
                    String str = checkInfo();
                    if (str == null) {
                        registerAlertDialog = new RegisterAlertDialog(mContext);
                        registerAlertDialog.setMsg("認證碼將以簡訊傳送至此手機。如需更換手機號碼，請點取消鍵！");
                        registerAlertDialog.setPhoneNum(mPpRegisterPhone.getText().toString());
                        registerAlertDialog.setPositiveListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Fragment fragment = new PersonRegisterAuth();
                                if (fragment != null) {
                                    registerAlertDialog.dismiss();
                                    Bundle bundle = new Bundle();
                                    bundle.putString(AppConfig.PHONENUM, personInfo.getPhoneNum());
                                    bundle.putString(AppConfig.FRAGMENT_NAME, PersonRegister.class.getSimpleName());
                                    bundle.putString(AppConfig.PERSON_INFO, gson.toJson(personInfo));
                                    fragment.setArguments(bundle);
                                    getFragmentManager().beginTransaction()
                                            .replace(R.id.person_frame_container, fragment, "PersonRegisterAuth")
                                            .addToBackStack("PersonRegisterAuth")
                                            .commit();
                                } else {
                                    Log.e(TAG, "Error in creating fragment");
                                }
                            }
                        });
                        registerAlertDialog.show();
                    } else {
                        oneBtnAlertDialog = AppUtils.getAlertDialog(mContext, "請輸入" + str);
                        oneBtnAlertDialog.show();
                    }
                    break;
                case R.id.pp_img_user_photo:
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
            String chineseBirth = "民國 " + year + " 年 " + month + " 月 " + day + " 日";
            mPpRegisterBirthday.setText(chineseBirth);
            String str = (year + 1911) + "-" + month + "-" + day;
            personInfo.setBirthday(str);
        }
    };

    //提示對話框方法
    private void changeUserPicDialog() {
        twoBtnAlertDialog.setMsg("頭像設置")
                .setPositiveBtnText("拍照")
                .setPositiveListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        twoBtnAlertDialog.dismiss();
                        // 調用系統的拍照功能
                        cameraFileName = AppUtils.getCameraSystemTimeFileName(AppConfig.JPEG_FILE_PREFIX) + AppConfig.JPEG_FILE_SUFFIX;
                        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        extraOutputFilePathUri = AppUtils.getPhotoUri(cameraFileName);
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, extraOutputFilePathUri);
                        if (cameraIntent.resolveActivity(mContext.getPackageManager()) != null) {
                            startActivityForResult(cameraIntent, PHOTO_REQUEST_TAKEPHOTO);
                        } else {
                            Toast.makeText(mContext, "無照相app", 1).show();
                        }
                    }
                })
                .setNegativeBtnText("相冊")
                .setNegativeListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
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

    private String checkInfo() {
        if (mPpRegisterUserName.getText().toString().trim().length() == 0) {
            return "姓名";
        } else if (mPpRegisterBirthday.getText().toString().trim().length() == 0) {
            return "生日";
        } else if (mPpRegisterSex.getText().toString().trim().length() == 0) {
            return "性別";
        } else if (mPpRegisterAddress.getText().toString().trim().length() == 0) {
            return "地址";
        } else if (mPpRegisterPhone.getText().toString().trim().length() == 0) {
            return "電話";
        }
        return null;
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
        if (chineseDatePickerDialog != null) {
            chineseDatePickerDialog.dismiss();
        }
        if (inputDialog != null) {
            inputDialog.dismiss();
        }
        if (oneBtnAlertDialog != null) {
            oneBtnAlertDialog.dismiss();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (chineseDatePickerDialog.isShowing()) {
            chineseDatePickerDialog.dismiss();
        }
        if (inputDialog.isShowing()) {
            inputDialog.dismiss();
        }
    }
}