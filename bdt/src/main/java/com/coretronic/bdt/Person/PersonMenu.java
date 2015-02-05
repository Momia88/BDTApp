package com.coretronic.bdt.Person;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.coretronic.bdt.AppConfig;
import com.coretronic.bdt.AppUtils;
import com.coretronic.bdt.Friend.FriendActivity;
import com.coretronic.bdt.Friend.FriendSettingAutoActivity;
import com.coretronic.bdt.MessageWall.WallActivity;
import com.coretronic.bdt.Person.Module.Person;
import com.coretronic.bdt.Person.Register.PersonLoginActivity;
import com.coretronic.bdt.R;
import com.coretronic.bdt.Tutorial.TutorFirstPageActivity;
import com.coretronic.bdt.Utility.CustomerOneBtnAlertDialog;
import com.coretronic.bdt.Utility.CustomerTwoBtnAlertDialog;
import com.coretronic.bdt.WalkWay.StepCount.PersonalStepRecord;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Morris on 14/11/21.
 */
public class PersonMenu extends Fragment {
    private static String TAG = PersonMenu.class.getSimpleName();

    private Button btnBack;
    private TextView barTitle;
    private Context mContext;
    private ProgressDialog progressDialog = null;
    private SharedPreferences sharedPreferences;
    private CustomerTwoBtnAlertDialog alertDialog = null;
    private String uuidChose;
    private AsyncHttpClient asyncHttpClient;
    private Gson gson = new Gson();

    private LinearLayout mAboutMeForLogin;

    private ImageView myLogo;
    private TextView mPpTxtUserName;
    private LinearLayout mPpBtnLogin;
    private ImageView mPpMenuImgLogin;
    private LinearLayout mPpBtnAboutMe;
    private ImageView mPpMenuImgAboutMe;
    private LinearLayout mPpBtnWall;
    private ImageView mPpMenuImgWall;
    private TextView mPpMenuTxtWall;
    private LinearLayout mPpBtnCount;
    private ImageView mPpMenuImgCount;
    private TextView mPpMenuTxtCount;
    private LinearLayout mPpBtnFriends;
    private ImageView mPpMenuImgFriend;
    private TextView mPpMenuTxtFriend;
    private LinearLayout mPpBtnAutoFriends;
    private ImageView mPpMenuImgAutoFriend;
    private TextView mPpMenuTxtAutoFriend;
    private LinearLayout mPpBtnMsg;
    private ImageView mPpMenuImgMsg;
    private TextView mPpMenuTxtMsg;
    private LinearLayout mPpBtnTeach;
    private ImageView mPpMenuImgTeach;
    private LinearLayout mPpBtnAboutBdt;
    private ImageView mPpMenuImgAboutBdt;
    //    private LinearLayout mPpBtnLogout;
//    private ImageView mPpMenuLogout;
//    private TextView mPpMenuTxtLogout;
    private Intent intent;
    private String currentTime;
    private String append;

    private boolean isLogin = false;
    private CustomerOneBtnAlertDialog oneBtnAlertDialog;

    public JsonHttpResponseHandler jsonHandler = new JsonHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
            Log.d(TAG, "onSuccess = " + response);
            try {
                if (response.get("msgCode").equals("A01")) {
                    setUserInfo(response.get("result").toString());
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
        sharedPreferences = getActivity().getSharedPreferences(AppConfig.SHAREDPREFERENCES_NAME, Context.MODE_PRIVATE);
        asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.setTimeout(AppConfig.TIMEOUT);
        uuidChose = sharedPreferences.getString(AppConfig.PREF_UNIQUE_ID, "");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis()); // 獲取當前時間
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.person_menu, container, false);
        mContext = v.getContext();
        progressDialog = new ProgressDialog(mContext);
        alertDialog = AppUtils.getAlertDialog(mContext, getResources().getString(R.string.pp_msg_need_register), "登入", "取消", dialogListener);
        initView(v);
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        isLogin = sharedPreferences.getBoolean(AppConfig.PREF_IS_LOGIN, false);
        setLayout();
        Log.d(TAG, "isLogin:" + isLogin);
        getUserInfo();
    }

    private void initView(View v) {
        // navigation bar
        btnBack = (Button) v.findViewById(R.id.btnBack);
        barTitle = (TextView) v.findViewById(R.id.action_bar_title);

        barTitle.setText(getString(R.string.pp_person_list));
        btnBack.setOnClickListener(btnListener);

        myLogo = (ImageView) v.findViewById(R.id.pp_img_user_photo);
        mAboutMeForLogin = (LinearLayout) v.findViewById(R.id.pp_menu_login_about_me);
        mPpTxtUserName = (TextView) v.findViewById(R.id.pp_txt_user_name);
        mAboutMeForLogin.setOnClickListener(btnListener);
        mPpBtnLogin = (LinearLayout) v.findViewById(R.id.pp_btn_login);
        mPpMenuImgLogin = (ImageView) v.findViewById(R.id.pp_menu_img_login);
        mPpBtnLogin.setOnClickListener(btnListener);

        mPpBtnAboutMe = (LinearLayout) v.findViewById(R.id.pp_btn_about_me);
        mPpMenuImgAboutMe = (ImageView) v.findViewById(R.id.pp_menu_img_about_me);
        mPpBtnAboutMe.setOnClickListener(btnListener);

        mPpBtnWall = (LinearLayout) v.findViewById(R.id.pp_btn_wall);
        mPpMenuImgWall = (ImageView) v.findViewById(R.id.pp_menu_img_wall);
        mPpMenuTxtWall = (TextView) v.findViewById(R.id.pp_menu_txt_wall);
        mPpBtnWall.setOnClickListener(btnListener);

        mPpBtnCount = (LinearLayout) v.findViewById(R.id.pp_btn_count);
        mPpMenuImgCount = (ImageView) v.findViewById(R.id.pp_menu_img_count);
        mPpMenuTxtCount = (TextView) v.findViewById(R.id.pp_menu_txt_count);
        mPpBtnCount.setOnClickListener(btnListener);

        mPpBtnFriends = (LinearLayout) v.findViewById(R.id.pp_btn_friends);
        mPpMenuImgFriend = (ImageView) v.findViewById(R.id.pp_menu_img_friend);
        mPpMenuTxtFriend = (TextView) v.findViewById(R.id.pp_menu_txt_friend);
        mPpBtnFriends.setOnClickListener(btnListener);

        mPpBtnAutoFriends = (LinearLayout) v.findViewById(R.id.pp_btn_auto_friends);
        mPpMenuImgAutoFriend = (ImageView) v.findViewById(R.id.pp_menu_img_auto_friend);
        mPpMenuTxtAutoFriend = (TextView) v.findViewById(R.id.pp_menu_txt_auto_friend);
        mPpBtnAutoFriends.setOnClickListener(btnListener);

        mPpBtnMsg = (LinearLayout) v.findViewById(R.id.pp_btn_msg);
        mPpMenuImgMsg = (ImageView) v.findViewById(R.id.pp_menu_img_msg);
        mPpMenuTxtMsg = (TextView) v.findViewById(R.id.pp_menu_txt_msg);
        mPpBtnMsg.setOnClickListener(btnListener);

        mPpBtnTeach = (LinearLayout) v.findViewById(R.id.pp_btn_teach);
        mPpMenuImgTeach = (ImageView) v.findViewById(R.id.pp_menu_img_teach);
        mPpBtnTeach.setOnClickListener(btnListener);

        mPpBtnAboutBdt = (LinearLayout) v.findViewById(R.id.pp_btn_about_bdt);
        mPpMenuImgAboutBdt = (ImageView) v.findViewById(R.id.pp_menu_img_about_bdt);
        mPpBtnAboutBdt.setOnClickListener(btnListener);

//        mPpBtnLogout = (LinearLayout) v.findViewById(R.id.pp_btn_logout);
//        mPpMenuLogout = (ImageView) v.findViewById(R.id.pp_menu_logout);
//        mPpMenuTxtLogout = (TextView) v.findViewById(R.id.pp_menu_txt_logout);
//        mPpBtnLogout.setOnClickListener(btnListener);
    }

    private View.OnClickListener dialogListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            alertDialog.dismiss();
            intent = new Intent(getActivity(), PersonLoginActivity.class);
            startActivity(intent);
        }
    };

    private View.OnClickListener btnListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Fragment fragment;
            switch (view.getId()) {
                case R.id.btnBack:
                    callSystemTime();
                    append = currentTime + "," +
                            uuidChose + "," +
                            "Person" + "," +
                            "MainActivity" + "," +
                            "btnBack" + "\n";
                    transactionLogSave(append);
                    Log.i(TAG, "is Login:" + sharedPreferences.getBoolean(AppConfig.PREF_IS_LOGIN, false));
                    getActivity().finish();
                    break;
                case R.id.pp_btn_about_me:
                    if (!isLogin) {
                        alertDialog.show();
                    }
                    break;

                case R.id.pp_menu_login_about_me:
                    callSystemTime();
                    append = currentTime + "," +
                            uuidChose + "," +
                            "Person" + "," +
                            "AboutMe" + "," +
                            "btn_login_about_me" + "\n";
                    transactionLogSave(append);
                    fragment = new AboutMe();
                    if (fragment != null) {
                        getFragmentManager().beginTransaction()
                                .replace(R.id.person_frame_container, fragment, "AboutMe")
                                .addToBackStack("AboutMe")
                                .commit();
                    } else {
                        Log.e(TAG, "Error in creating fragment");
                    }

                    break;
                case R.id.pp_btn_wall:
                    callSystemTime();
                    append = currentTime + "," +
                            uuidChose + "," +
                            "Person" + "," +
                            "WallMsgActivity" + "," +
                            "btn_wall" + "\n";
                    transactionLogSave(append);
                    if (!isLogin) {
                        alertDialog.show();
                    } else {
                        intent = new Intent(getActivity(), WallActivity.class);
                        startActivity(intent);
                    }
                    break;
                case R.id.pp_btn_friends:
                    callSystemTime();
                    append = currentTime + "," +
                            uuidChose + "," +
                            "Person" + "," +
                            "FriendActivity" + "," +
                            "btn_friends" + "\n";
                    transactionLogSave(append);
                    if (!isLogin) {
                        alertDialog.show();
                    } else {
                        intent = new Intent(getActivity(), FriendActivity.class);
                        startActivity(intent);
                    }
                    break;
                case R.id.pp_btn_auto_friends:
                    callSystemTime();
                    append = currentTime + "," +
                            uuidChose + "," +
                            "Person" + "," +
                            "FriendSettingAutoActivity" + "," +
                            "btn_auto_friends" + "\n";
                    transactionLogSave(append);
                    if (!isLogin) {
                        alertDialog.show();
                    } else {
                        intent = new Intent(getActivity(), FriendSettingAutoActivity.class);
                        startActivity(intent);
                    }
                    break;
                case R.id.pp_btn_msg:
                    callSystemTime();
                    append = currentTime + "," +
                            uuidChose + "," +
                            "Person" + "," +
                            "Message" + "," +
                            "btn_msg" + "\n";
                    transactionLogSave(append);
                    if (!isLogin) {
                        alertDialog.show();
                    } else {
                        intent = new Intent(getActivity(), NotifyListActivity.class);
                        startActivity(intent);
                    }
                    break;
                case R.id.pp_btn_count:
                    callSystemTime();
                    append = currentTime + "," +
                            uuidChose + "," +
                            "Person" + "," +
                            "PersonalStepRecord" + "," +
                            "btn_count" + "\n";
                    transactionLogSave(append);
                    if (!isLogin) {
                        alertDialog.show();
                    } else {
                        intent = new Intent(getActivity(), PersonalStepRecord.class);
                        startActivity(intent);
                    }
                    break;
                case R.id.pp_btn_teach:
                    callSystemTime();
                    append = currentTime + "," +
                            uuidChose + "," +
                            "Person" + "," +
                            "TutorFirstPageActivity" + "," +
                            "btn_teach" + "\n";
                    transactionLogSave(append);
                    Bundle bundle = new Bundle();
                    bundle.putString("source", TAG);
                    intent = new Intent(getActivity(), TutorFirstPageActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    break;
                case R.id.pp_btn_about_bdt:
                    callSystemTime();
                    append = currentTime + "," +
                            uuidChose + "," +
                            "Person" + "," +
                            "AboutBDT" + "," +
                            "about_bdt" + "\n";
                    transactionLogSave(append);
                    fragment = new AboutBDT();
                    if (fragment != null) {
                        getFragmentManager().beginTransaction()
                                .replace(R.id.person_frame_container, fragment, "AboutBDT")
                                .addToBackStack("AboutBDT")
                                .commit();
                    } else {
                        Log.e(TAG, "Error in creating fragment");
                    }
                    break;
                case R.id.pp_btn_login:
                    callSystemTime();
                    append = currentTime + "," +
                            uuidChose + "," +
                            "Person" + "," +
                            "PersonLoginActivity" + "," +
                            "btn_login" + "\n";
                    transactionLogSave(append);
                    intent = new Intent(getActivity(), PersonLoginActivity.class);
                    startActivity(intent);
                    break;
//                case R.id.pp_btn_logout:
//                    callSystemTime();
//                    append = currentTime + "," +
//                            uuidChose + "," +
//                            "Person" + "," +
//                            "Logout" + "," +
//                            "btn_logout" + "\n";
//                    transactionLogSave(append);
//                    isLogin = false;
//                    String deviceId = sharedPreferences.getString(AppConfig.PREF_DEVICE_ID, null);
//                    sharedPreferences.edit()
//                            .putBoolean(AppConfig.PREF_IS_LOGIN, isLogin)
//                            .putString(AppConfig.PREF_UNIQUE_ID, deviceId)
//                            .apply();
//                    Intent i = new Intent();
//                    i.setClass(getActivity(), LoginActivity.class);
//                    startActivity(i);
//                    getActivity().finish();
//                    break;
            }
        }
    };

    private void setLayout() {

        if (isLogin) {
            mAboutMeForLogin.setVisibility(View.VISIBLE);
            mPpBtnLogin.setVisibility(View.GONE);
            mPpBtnAboutMe.setVisibility(View.GONE);
            mPpMenuImgWall.setImageResource(R.drawable.pp_ic_wall);
            mPpMenuTxtWall.setTextColor(getResources().getColor(R.color.pp_text_color_black));
            mPpMenuImgCount.setImageResource(R.drawable.pp_ic_count);
            mPpMenuTxtCount.setTextColor(getResources().getColor(R.color.pp_text_color_black));
            mPpMenuImgFriend.setImageResource(R.drawable.pp_ic_friend);
            mPpMenuTxtFriend.setTextColor(getResources().getColor(R.color.pp_text_color_black));
            mPpMenuImgAutoFriend.setImageResource(R.drawable.pp_ic_friend_auto_invite);
            mPpMenuTxtAutoFriend.setTextColor(getResources().getColor(R.color.pp_text_color_black));
            mPpMenuImgMsg.setImageResource(R.drawable.pp_ic_msg);
            mPpMenuTxtMsg.setTextColor(getResources().getColor(R.color.pp_text_color_black));
//            mPpMenuLogout.setImageResource(R.drawable.pp_ic_logout);
//            mPpMenuTxtLogout.setTextColor(getResources().getColor(R.color.pp_text_color_black));
//            mPpBtnLogout.setVisibility(View.VISIBLE);

        } else {
            mAboutMeForLogin.setVisibility(View.GONE);
            mPpBtnLogin.setVisibility(View.VISIBLE);
            mPpBtnAboutMe.setVisibility(View.VISIBLE);
            mPpMenuImgWall.setImageResource(R.drawable.pp_ic_wall_gray);
            mPpMenuTxtWall.setTextColor(getResources().getColor(R.color.pp_text_color_brown));
            mPpMenuImgCount.setImageResource(R.drawable.pp_ic_count_gray);
            mPpMenuTxtCount.setTextColor(getResources().getColor(R.color.pp_text_color_brown));
            mPpMenuImgFriend.setImageResource(R.drawable.pp_ic_friend_gray);
            mPpMenuTxtFriend.setTextColor(getResources().getColor(R.color.pp_text_color_brown));
            mPpMenuImgAutoFriend.setImageResource(R.drawable.pp_ic_friend_auto_invite_gray);
            mPpMenuTxtAutoFriend.setTextColor(getResources().getColor(R.color.pp_text_color_brown));
            mPpMenuImgMsg.setImageResource(R.drawable.pp_ic_message_gray);
            mPpMenuTxtMsg.setTextColor(getResources().getColor(R.color.pp_text_color_brown));
//            mPpBtnLogout.setVisibility(View.GONE);
        }

    }

    private void getUserInfo() {

        final String url = AppConfig.USER_AUTH_SITE_PATE + AppConfig.USER_INFO;
        Log.i(TAG, "url:  " + url);
        uuidChose = sharedPreferences.getString(AppConfig.PREF_UNIQUE_ID, "");
        Log.i(TAG, "UUID:  " + uuidChose);

        if (uuidChose.length() == 20) {
            progressDialog.setMessage(getString(R.string.dialog_download_msg));
            progressDialog.show();
            RequestParams params = new RequestParams();
            params.add("uid", uuidChose);
            params.add("time", AppUtils.getSystemTime());

            Log.i(TAG, "params:  " + params);
            asyncHttpClient.post(url, params, jsonHandler);
        }
    }

    private void setUserInfo(String json) {
        Person person = gson.fromJson(json, Person.class);
        if (person != null) {
            if (person.getThumb() != null && person.getThumb().length() > 0) {
                myLogo.setImageBitmap(AppUtils.base64ToBitmap(person.getThumb()));
            }
            mPpTxtUserName.setText(person.getName());

            sharedPreferences.edit()
                    .putString(AppConfig.PREF_USER_NANE, person.getName())
                    .putString(AppConfig.PREF_USER_THUMB, person.getThumb())
                    .putString(AppConfig.PREF_USER_BIRTHDAY, person.getBirthday())
                    .putString(AppConfig.PREF_USER_ADDRESS, person.getAddress())
                    .putString(AppConfig.PREF_USER_SEX, person.getSex())
                    .putString(AppConfig.PREF_USER_PHONE, person.getPhoneNum())
                    .apply();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (alertDialog.isShowing()) {
            alertDialog.dismiss();
        }
        if (oneBtnAlertDialog != null) {
            oneBtnAlertDialog.dismiss();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (alertDialog.isShowing()) {
            alertDialog.dismiss();
        }
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