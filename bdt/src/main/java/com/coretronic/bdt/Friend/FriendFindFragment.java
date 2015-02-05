package com.coretronic.bdt.Friend;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.coretronic.bdt.AppConfig;
import com.coretronic.bdt.AppUtils;
import com.coretronic.bdt.DataModule.FriendSearchInfo;
import com.coretronic.bdt.R;
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
 * Created by poter.hsu on 2014/12/15.
 */
public class FriendFindFragment extends Fragment {

    private static final String TAG = FriendListFragment.class.getSimpleName();

    private Context context;
    private RelativeLayout navigationBar;
    private Button btnBack;
    private Button btnPopMenu;
    private TextView barTitle;
    private View rightLine;
    private String userId;
    private String userTel;
    private Button btnFindFriend;
    private AsyncHttpClient httpClient;
    private ProgressDialog waitingDialog;
    private EditText edtFriendTel;
    //transactionLog
    private String currentTime;
    private String append;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.friend_find, container, false);
        this.context = view.getContext();

        Bundle bundle = getArguments();
        userId = bundle.getString(AppConfig.BUNDLE_ARGU_UID);
        userTel = bundle.getString(AppConfig.BUNDLE_ARGU_TEL);

        initView(view);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FriendFindAboutFragment aboutFragment = new FriendFindAboutFragment();
        Bundle bundle = new Bundle();
        bundle.putString(AppConfig.BUNDLE_ARGU_TEL, userTel);
        aboutFragment.setArguments(bundle);

        getFragmentManager().beginTransaction().replace(R.id.friend_find_child_fragment_container, aboutFragment).commit();
    }

    private void initView(View view) {
        // Navigation Bar
        navigationBar = (RelativeLayout) view.findViewById(R.id.navigationBar_option);
        rightLine = (View) view.findViewById(R.id.right_line);
        btnBack = (Button) view.findViewById(R.id.btnBack);
        btnPopMenu = (Button) view.findViewById(R.id.btnPopMenu);
        barTitle = (TextView) view.findViewById(R.id.action_bar_title);

        barTitle.setText(getString(R.string.pp_friend));
        rightLine.setVisibility(View.GONE);
        btnPopMenu.setVisibility(View.GONE);
        btnBack.setOnClickListener(btnListener);

        edtFriendTel = (EditText) view.findViewById(R.id.edt_friend_tel);

        btnFindFriend = (Button) view.findViewById(R.id.btn_find_friend);
        btnFindFriend.setOnClickListener(btnFindFriendListener);
    }

    private View.OnClickListener btnListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btnBack:
                    callSystemTime();
                    append = currentTime + "," +
                            userId + "," +
                            "FriendActivityAddFriend" + "," +
                            "FriendActivity" + "," +
                            "btnBack" + "\n";
                    transactionLogSave(append);
                    getFragmentManager().popBackStackImmediate();
                    break;
            }
        }
    };

    private boolean validatePhoneNumber(String tel) {
        if (tel.length() <= 0) {
            Toast.makeText(context, R.string.friend_toast_please_enter_tel, Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (tel.equals(userTel) == true) {
            Toast.makeText(context, R.string.friend_toast_this_is_self_tel, Toast.LENGTH_SHORT).show();
            return false;
        } else
            return true;
    }

    private View.OnClickListener btnFindFriendListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            callSystemTime();
            append = currentTime + "," +
                    userId + "," +
                    "FriendActivityAddFriend" + "," +
                    "FriendRelation" + "," +
                    "btnFindFriend" + "\n";
            transactionLogSave(append);
            String friendTel = edtFriendTel.getText().toString();
            if (validatePhoneNumber(friendTel) == false)
                return;

            // setup http client
            String requestUrl = AppConfig.DOMAIN_SITE_PATE + AppConfig.API_FRIEND_SEARCH;
            RequestParams params = new RequestParams();
            params.add(AppConfig.BUNDLE_ARGU_UID, userId);
            params.add(AppConfig.BUNDLE_ARGU_TEL, friendTel);

            httpClient = new AsyncHttpClient();
            httpClient.setTimeout(AppConfig.TIMEOUT);
            httpClient.post(requestUrl, params, friendSearchHttpResponseHandler);
            httpClient = null;

            // show dialog
            waitingDialog = AppUtils.customProgressDialog(context, null, getString(R.string.dialog_read_msg));
            waitingDialog.show();
        }
    };

    private void switchToFriendRelationFragment(String fid, String tel, String name, String thumb, String state) {
        FriendRelationFragment relationFragment = new FriendRelationFragment();
        Bundle bundle = new Bundle();
        bundle.putString(AppConfig.BUNDLE_ARGU_UID, userId);
        bundle.putString(AppConfig.BUNDLE_ARGU_FID, fid);
        bundle.putString(AppConfig.BUNDLE_ARGU_TEL, tel);
        bundle.putString(AppConfig.BUNDLE_ARGU_NAME, name);
        bundle.putString(AppConfig.BUNDLE_ARGU_THUMB, thumb);
        bundle.putString(AppConfig.BUNDLE_ARGU_STATE, state);
        relationFragment.setArguments(bundle);

        getFragmentManager().beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .replace(R.id.friend_frame_container, relationFragment)
                .addToBackStack(null)
                .commit();
    }

    private JsonHttpResponseHandler friendSearchHttpResponseHandler = new JsonHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            super.onSuccess(statusCode, headers, response);

            Log.i(TAG, "JsonHttpResponseHandler onSuccess, response.");

            try {
                if (waitingDialog != null)
                    waitingDialog.dismiss();

                String msgCode = (String)response.get("msgCode");
                if (msgCode.equals("A01")) {
                    Gson gson = new Gson();
                    FriendSearchInfo friendSearchInfo = gson.fromJson(response.toString(), FriendSearchInfo.class);

                    String fid = friendSearchInfo.getResult().getUid();
                    String tel = friendSearchInfo.getResult().getTel();
                    String name =  friendSearchInfo.getResult().getName();
                    if (name == null) name = "";
                    String thumb = friendSearchInfo.getResult().getThumb();
                    String state = friendSearchInfo.getResult().getState();

                    switchToFriendRelationFragment(fid, tel, name, thumb, state);
                }
                else if (msgCode.equals("I01")) {
                    FriendFindInexistenceFragment inexistenceFragment = new FriendFindInexistenceFragment();
                    getFragmentManager().beginTransaction().replace(R.id.friend_find_child_fragment_container, inexistenceFragment).commit();
                } else {
                    if (response.get("result") != null) {
                        AppUtils.getAlertDialog(context, response.get("result").toString()).show();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (JsonSyntaxException e) {
                AppUtils.getAlertDialog(context, getString(R.string.data_result_error)).show();
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            super.onFailure(statusCode, headers, throwable, errorResponse);

            Log.i(TAG, "JsonHttpResponseHandler onFailure, errorResponse.");
            AppUtils.getAlertDialog(context, getString(R.string.data_load_error)).show();

            if (waitingDialog != null)
                waitingDialog.dismiss();
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            super.onFailure(statusCode, headers, responseString, throwable);

            Log.i(TAG, "JsonHttpResponseHandler onFailure, responseString: " + responseString);

            if (waitingDialog != null)
                waitingDialog.dismiss();
        }
    };

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
