package com.coretronic.bdt.Person.Register;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.coretronic.bdt.AppConfig;
import com.coretronic.bdt.AppUtils;
import com.coretronic.bdt.R;
import com.coretronic.bdt.Utility.CustomerOneBtnAlertDialog;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Morris on 14/11/25.
 */
public class PersonLogin extends Fragment {
    private static String TAG = PersonLogin.class.getSimpleName();

    private Button btnBack;
    private TextView barTitle;
    private Context mContext;
    private RelativeLayout navigationBar;
    private SharedPreferences sharedPreferences;
    private ProgressDialog progressDialog = null;
    private String uuidChose;
    private AsyncHttpClient asyncHttpClient;
    private Gson gson = new Gson();

    private EditText mPpEditPhone;
    private Button mPpBtnLoginLogin;
    private Button mPpBtnLoginAdd;
    private String phoneNum;
    private CustomerOneBtnAlertDialog dialog;


    public JsonHttpResponseHandler jsonHandler = new JsonHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
            Log.d(TAG, "onSuccess = " + response);
            try {
                if (response.get("msgCode").equals("A01")) {
                    Fragment fragment = new PersonRegisterAuth();
                    Bundle bundle = new Bundle();
                    bundle.putString(AppConfig.PHONENUM,phoneNum);
                    bundle.putString(AppConfig.FRAGMENT_NAME,PersonLogin.class.getSimpleName());
                    fragment.setArguments(bundle);
                    if (fragment != null) {
                        getFragmentManager().beginTransaction()
                                .replace(R.id.person_frame_container, fragment, "PersonRegisterAuth")
                                .addToBackStack("PersonRegisterAuth")
                                .commit();
                    } else {
                        Log.e(TAG, "Error in creating fragment");
                    }
                } else {
                    dialog.setMsg(response.get("result").toString()).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (JsonSyntaxException e) {
                dialog.setMsg(getString(R.string.data_result_error)).show();
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
            dialog.setMsg(getString(R.string.data_load_error)).show();
        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getActivity().getSharedPreferences(AppConfig.SHAREDPREFERENCES_NAME, 0);
        asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.setTimeout(AppConfig.TIMEOUT);
        uuidChose = sharedPreferences.getString(AppConfig.PREF_UNIQUE_ID, "");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis()); // 獲取當前時間
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.person_login, container, false);
        mContext = v.getContext();
        progressDialog = new ProgressDialog(mContext);
        dialog = new CustomerOneBtnAlertDialog(mContext);
        initView(v);
        return v;
    }

    private void initView(View v) {
        // navigation bar
        navigationBar = (RelativeLayout) v.findViewById(R.id.navigationBar_option);
        btnBack = (Button) v.findViewById(R.id.btnBack);
        barTitle = (TextView) v.findViewById(R.id.action_bar_title);

        barTitle.setText(getString(R.string.pp_register_title));
        btnBack.setOnClickListener(btnListener);

        mPpEditPhone = (EditText) v.findViewById(R.id.pp_edit_phone);
        mPpBtnLoginLogin = (Button) v.findViewById(R.id.pp_btn_login_sumit);
        mPpBtnLoginAdd = (Button) v.findViewById(R.id.pp_btn_login_add);
        mPpBtnLoginLogin.setOnClickListener(btnListener);
        mPpBtnLoginAdd.setOnClickListener(btnListener);
    }


    private View.OnClickListener btnListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btnBack:
                    getActivity().finish();
                    break;
                case R.id.pp_btn_login_sumit:
                    if (checkInfo()) {
                        userLogin();
                    } else {
                        dialog.setMsg("請輸入電話").show();
                    }
                    break;
                case R.id.pp_btn_login_add:
                    getFragmentManager().beginTransaction()
                            .replace(R.id.person_frame_container, new PersonRegister())
                            .addToBackStack("PersonRegister")
                            .commit();
                    break;
            }
        }
    };


    private void userLogin() {
        progressDialog.setMessage(getString(R.string.dialog_download_msg));
        progressDialog.show();

        final String url = AppConfig.USER_AUTH_SITE_PATE + AppConfig.USER_AUTH_LOGIN;
        Log.i(TAG, "url:  " + url);

        uuidChose = sharedPreferences.getString(AppConfig.PREF_UNIQUE_ID, "");
        Log.i(TAG, "UUID:  " + uuidChose);
        RequestParams params = new RequestParams();
        params.add("uid", uuidChose);
        params.add("time", AppUtils.getSystemTime());
        params.add("tel", mPpEditPhone.getText().toString().trim());

        Log.i(TAG, "params:  " + params);
        asyncHttpClient.post(url, params, jsonHandler);
    }

    private boolean checkInfo() {
        phoneNum = mPpEditPhone.getText().toString().trim();
        if (phoneNum.length() == 0) {
            return false;
        }
        return true;

    }
}