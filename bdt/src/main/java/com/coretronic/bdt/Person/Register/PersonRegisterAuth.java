package com.coretronic.bdt.Person.Register;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.*;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.coretronic.bdt.AppConfig;
import com.coretronic.bdt.AppUtils;
import com.coretronic.bdt.Person.Module.Person;
import com.coretronic.bdt.Person.Module.RegisterAlertDialog;
import com.coretronic.bdt.Person.Module.SmsInfo;
import com.coretronic.bdt.Person.PersonActivity;
import com.coretronic.bdt.R;
import com.coretronic.bdt.Utility.CustomerTwoBtnAlertDialog;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Morris on 14/11/25.
 */
public class PersonRegisterAuth extends Fragment {
    private static String TAG = PersonRegisterAuth.class.getSimpleName();
    public static final int USER_ID_LENGTH = 20;

    private Button btnBack;
    private TextView barTitle;
    private Context mContext;
    private RelativeLayout navigationBar;
    private SharedPreferences sharedPreferences;
    private ProgressDialog progressDialog = null;
    private String uuidChose;
    private String userId;
    private AsyncHttpClient asyncHttpClient;
    private Gson gson = new Gson();
    private RegisterAlertDialog registerAlertDialog;
    private EditText mPpTxtCodeInput;
    private Button mPpRegisterCodeSumit;
    private Button mPpRegisterCodeResend;
    private BroadcastReceiver smsReceiver;
    private Date curDate;
    private SimpleDateFormat formatter;
    private String phoneNum;
    private String fragmentName;
    private Person personInfo;

    public JsonHttpResponseHandler verifyHandler = new JsonHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
            Log.d(TAG, "onSuccess = " + response);
            try {
                if (response.get("msgCode").equals("A01")) {
                    if (response.get("result") != null) {
                        JSONObject jsonObject = (JSONObject) response.get("result");
                        userId = (String) jsonObject.get("uid");
                        Log.d(TAG, "userId = " + userId);
                        Toast.makeText(mContext, "Login success ! \n" + userId, 1).show();
                        sharedPreferences.edit()
                                .putString(AppConfig.PREF_UNIQUE_ID, userId)
                                .putString(AppConfig.PREF_USER_PHONE, phoneNum)
                                .putBoolean(AppConfig.PREF_IS_LOGIN, true)
                                .apply();

                        askForEnableAutoInvite();
                    }

                } else {
                    if (response.get("result") != null) {
                        AppUtils.getAlertDialog(mContext, response.get("result").toString()).show();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (JsonSyntaxException e) {
                AppUtils.getAlertDialog(mContext, getString(R.string.data_result_error)).show();
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
            AppUtils.getAlertDialog(mContext, getString(R.string.data_load_error)).show();
        }
    };

    public JsonHttpResponseHandler smsHandler = new JsonHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
            Log.d(TAG, "onSuccess = " + response);
            try {
                if (response.get("msgCode").equals("A01")) {
                    if (response.get("result") != null) {
                        Toast.makeText(mContext, response.get("result").toString(), 1).show();
                    }
                } else {
                    if (response.get("result") != null) {
                        AppUtils.getAlertDialog(mContext, response.get("result").toString()).show();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (JsonSyntaxException e) {
                AppUtils.getAlertDialog(mContext, getString(R.string.data_result_error)).show();
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
            AppUtils.getAlertDialog(mContext, getString(R.string.data_load_error)).show();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getActivity().getSharedPreferences(AppConfig.SHAREDPREFERENCES_NAME, 0);
        asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.setTimeout(AppConfig.TIMEOUT);
        uuidChose = sharedPreferences.getString(AppConfig.PREF_UNIQUE_ID, "");
//        formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        formatter = new SimpleDateFormat("yyyy-MM-dd");
        curDate = new Date(System.currentTimeMillis()); // 獲取當前時間
        personInfo = new Person();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.person_register_auth, container, false);
        mContext = v.getContext();
        progressDialog = new ProgressDialog(mContext);
        initView(v);
        Bundle bundle = getArguments();
        if (bundle != null) {
            phoneNum = bundle.getString(AppConfig.PHONENUM);
            fragmentName = bundle.getString(AppConfig.FRAGMENT_NAME);
            Log.d(TAG, "phoneNum:" + phoneNum);
            Log.d(TAG, "fragmentName:" + fragmentName);
            if (fragmentName.equals(PersonRegister.class.getSimpleName())) {
                String json = bundle.getString(AppConfig.PERSON_INFO);
                personInfo = gson.fromJson(json, Person.class);
                sendRegisterRequest(phoneNum, AppConfig.USER_AUTH_REGISTER);
            }
        }
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter(AppConfig.SMS_INTENT_FILTER);
        smsReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String json = intent.getStringExtra(AppConfig.SMS_PROF);
                SmsInfo smsInfo = gson.fromJson(json, SmsInfo.class);
                if (smsInfo != null && smsInfo.getMsgBody() != null) {
                    if (smsInfo.getMsgBody().trim().length() > 4) {
                        String code = smsInfo.getMsgBody().trim().substring(0, 4);
                        mPpTxtCodeInput.setText(code);
                    }
                }
            }
        };
        mContext.registerReceiver(this.smsReceiver, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        mContext.unregisterReceiver(this.smsReceiver);

    }

    private void initView(View v) {
        // navigation bar
        navigationBar = (RelativeLayout) v.findViewById(R.id.navigationBar_option);
        btnBack = (Button) v.findViewById(R.id.btnBack);
        barTitle = (TextView) v.findViewById(R.id.action_bar_title);

        barTitle.setText(getString(R.string.pp_register_title));
        btnBack.setOnClickListener(btnListener);

        mPpTxtCodeInput = (EditText) v.findViewById(R.id.pp_txt_code_input);
        mPpRegisterCodeSumit = (Button) v.findViewById(R.id.pp_register_code_sumit);
        mPpRegisterCodeResend = (Button) v.findViewById(R.id.pp_register_code_resend);
        mPpRegisterCodeSumit.setOnClickListener(btnListener);
        mPpRegisterCodeResend.setOnClickListener(btnListener);
    }


    private View.OnClickListener btnListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btnBack:
                    registerAlertDialog = new RegisterAlertDialog(mContext);
                    registerAlertDialog.setMsg("若您按返回，系統將利用簡訊傳送全的認證碼。\n一天只會有三次的認證機會喔！");
                    registerAlertDialog.setTextViewVisibility(false);
                    registerAlertDialog.setPositiveListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            registerAlertDialog.dismiss();
                            getFragmentManager().popBackStackImmediate();
                        }
                    });

                    registerAlertDialog.show();
                    break;
                // send register code to server
                case R.id.pp_register_code_sumit:
                    try {
                        String verityCode = mPpTxtCodeInput.getText().toString().trim();
                        if (verityCode.length() > 0) {
                            if (fragmentName.equals(PersonLogin.class.getSimpleName())) {
                                sendVerifyLogin(phoneNum, verityCode);
                            } else {
                                sendVerifyRegister(phoneNum, verityCode);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(mContext, e.getMessage(), 1).show();
                    }
                    break;
                // resend register code
                case R.id.pp_register_code_resend:
                    sendRegisterRequest(phoneNum, AppConfig.USER_AUTH_SMS);
                    break;
            }
        }
    };

    private void sendVerifyLogin(String phone, String verifyCode) {
        progressDialog.setMessage(getString(R.string.dialog_download_msg));
        progressDialog.show();

        final String url = AppConfig.USER_AUTH_SITE_PATE + AppConfig.USER_AUTH_VERIFY_LOGIN;
        Log.i(TAG, "url:  " + url);

        uuidChose = sharedPreferences.getString(AppConfig.PREF_UNIQUE_ID, "");
        Log.i(TAG, "UID:  " + uuidChose);
        RequestParams params = new RequestParams();
        params.add("uid", uuidChose);
        params.add("time", AppUtils.getSystemTime());
        params.add("tel", phone);
        params.add("code", verifyCode);

        Log.i(TAG, "params:  " + params);
        asyncHttpClient.post(url, params, verifyHandler);
    }

    private void sendVerifyRegister(String phone, String verifyCode) {
        progressDialog.setMessage(getString(R.string.dialog_download_msg));
        progressDialog.show();

        final String url = AppConfig.USER_AUTH_SITE_PATE + AppConfig.USER_AUTH_VERIFY_REGISTER;
        Log.i(TAG, "url:  " + url);

        uuidChose = sharedPreferences.getString(AppConfig.PREF_UNIQUE_ID, "");
        Log.i(TAG, "UID:  " + uuidChose);
        RequestParams params = new RequestParams();
        params.add("uid", uuidChose);
        params.add("time", AppUtils.getSystemTime());
        params.add("code", verifyCode);
        params.add("tel", personInfo.getPhoneNum());
        params.add("name", personInfo.getName());
        params.add("thumb", personInfo.getThumb());
        params.add("sex", personInfo.getSex());
        params.add("birthday", personInfo.getBirthday());
        params.add("address", personInfo.getAddress());

        Log.i(TAG, "params:  " + params);
        asyncHttpClient.post(url, params, verifyHandler);

    }

    private void sendRegisterRequest(String phone, String methodName) {
        progressDialog.setMessage(getString(R.string.dialog_download_msg));
        progressDialog.show();

        final String url = AppConfig.USER_AUTH_SITE_PATE + methodName;
        Log.i(TAG, "url:  " + url);
        uuidChose = sharedPreferences.getString(AppConfig.PREF_UNIQUE_ID, "");
        Log.i(TAG, "UID:  " + uuidChose);
        RequestParams params = new RequestParams();
        params.add("uid", uuidChose);
        params.add("time", AppUtils.getSystemTime());
        params.add("tel", phone);

        Log.i(TAG, "params:  " + params);
        asyncHttpClient.post(url, params, smsHandler);
    }

    private void askForEnableAutoInvite() {
        final CustomerTwoBtnAlertDialog dialog = new CustomerTwoBtnAlertDialog(mContext);
        dialog.setMsg(getString(R.string.friend_after_auth_auto_invite_tip));
        dialog.setPositiveBtnText(getString(R.string.friend_btn_after_auth_auto_invite_yes));
        dialog.setNegativeBtnText(getString(R.string.friend_btn_after_auth_auto_invite_no));
        dialog.setPositiveListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userId = sharedPreferences.getString(AppConfig.PREF_UNIQUE_ID, "");
                if (userId.length() != USER_ID_LENGTH) {
                    Toast.makeText(mContext, getString(R.string.friend_auto_invite_failed_due_to_no_uid), Toast.LENGTH_SHORT).show();
                    return;
                }
                doAutoInvite(userId);

                sharedPreferences.edit()
                        .putBoolean(AppConfig.PREF_IS_ENABLED_AUTO_INVITE, true)
                        .putInt(AppConfig.PREF_LAST_LOCAL_PHONE_COUNT, 0)
                        .commit();
                Log.d(TAG, "auto invite enabled");

                dialog.dismiss();
            }
        });
        dialog.setNegativeListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharedPreferences.edit()
                        .putBoolean(AppConfig.PREF_IS_ENABLED_AUTO_INVITE, false)
                        .putInt(AppConfig.PREF_LAST_LOCAL_PHONE_COUNT, 0)
                        .commit();
                Log.d(TAG, "auto invite disabled");

                dialog.dismiss();
            }
        });
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                Intent intent = new Intent(getActivity(), PersonActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });
        dialog.show();
    }

    /**
     * 取得手機內聯絡人電話列表，若筆數不為 0 且筆數有變化，則送出 HTTP 請求，並記錄此次聯絡人電話數。
     */
    private void doAutoInvite(String userId) {
        String requestUrl = AppConfig.DOMAIN_SITE_PATE + AppConfig.API_FRIEND_INVITE_AUTO;

        JSONArray jsonArray = new JSONArray(getLocalMobilePhoneListInTaiwanFormat(mContext));
        int phoneListCount = jsonArray.length();
        Log.d(TAG, "Phone List Count: " + phoneListCount);

        sharedPreferences.edit().putInt(AppConfig.PREF_LAST_LOCAL_PHONE_COUNT, phoneListCount).commit();

        if (phoneListCount == 0) {
            Toast.makeText(mContext, getString(R.string.friend_auto_invite_no_contact_message), Toast.LENGTH_LONG).show();
            return;
        }

        if (isPhoneListCountChanged(phoneListCount)) {
            String phoneListInJsonString = jsonArray.toString();
            Log.d(TAG, phoneListInJsonString);

            RequestParams params = new RequestParams();
            params.add(AppConfig.BUNDLE_ARGU_UID, userId);
            params.add(AppConfig.BUNDLE_ARGU_TEL_LIST, phoneListInJsonString);

            AsyncHttpClient requestAutoInviteHttpClient = new AsyncHttpClient();
            requestAutoInviteHttpClient.setTimeout(AppConfig.TIMEOUT);
            requestAutoInviteHttpClient.post(requestUrl, params, autoInviteHttpResponseHandler);
        } else {
            Toast.makeText(mContext, getString(R.string.friend_no_need_auto_invite_message), Toast.LENGTH_LONG).show();
        }
    }

    private boolean isPhoneListCountChanged(int phoneListCount) {
        return (phoneListCount == sharedPreferences.getInt(AppConfig.PREF_LAST_LOCAL_PHONE_COUNT, 0));
    }

    /**
     * 取得手機內聯絡人電話列表，去除所有非數字符號，並過濾以台灣地區手機號碼格式：10碼、09開頭。
     *
     * @param context
     * @return 手機號碼列表
     */
    private List<String> getLocalMobilePhoneListInTaiwanFormat(Context context) {
        ArrayList<String> phoneList = new ArrayList<String>();
        ContentResolver cr = context.getContentResolver();
        Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                if (Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String contactNumber = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        if (contactNumber == null) continue;
                        Pattern pattern = Pattern.compile("[^0-9]");
                        Matcher matcher = pattern.matcher(contactNumber);
                        contactNumber = matcher.replaceAll("").trim();
                        if ((contactNumber.length() == 10) || (contactNumber.startsWith("09")))
                            phoneList.add(contactNumber);
                        break;
                    }
                    pCur.close();
                }

            } while (cursor.moveToNext());
        }
        return phoneList;
    }

    private JsonHttpResponseHandler autoInviteHttpResponseHandler = new JsonHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            super.onSuccess(statusCode, headers, response);

            Log.i(TAG, "JsonHttpResponseHandler onSuccess, response.");

            try {
                if (response.get("msgCode").equals("A01")) {
                    int invitedCount = response.getJSONObject("result").getInt("count");
                    String invitedMessage = getString(R.string.friend_auto_invited_message);
                    invitedMessage = invitedMessage.replace(":count:", String.valueOf(invitedCount));
                    Toast.makeText(mContext, invitedMessage, Toast.LENGTH_LONG).show();
                } else {
                    if (response.get("result") != null) {
                        AppUtils.getAlertDialog(mContext, response.get("result").toString()).show();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (JsonSyntaxException e) {
                AppUtils.getAlertDialog(mContext, getString(R.string.data_result_error)).show();
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            super.onFailure(statusCode, headers, throwable, errorResponse);

            Log.i(TAG, "JsonHttpResponseHandler onFailure, errorResponse.");
            AppUtils.getAlertDialog(mContext, getString(R.string.data_load_error)).show();
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            super.onFailure(statusCode, headers, responseString, throwable);

            Log.i(TAG, "JsonHttpResponseHandler onFailure, responseString: " + responseString);
        }

    };
}