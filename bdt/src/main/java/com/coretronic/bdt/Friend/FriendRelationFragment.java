package com.coretronic.bdt.Friend;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.coretronic.bdt.AppConfig;
import com.coretronic.bdt.AppUtils;
import com.coretronic.bdt.Friend.Module.FriendActionDialog;
import com.coretronic.bdt.R;
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
 * Created by poter.hsu on 2014/12/16.
 */
public class FriendRelationFragment extends Fragment {

    private static final String TAG = FriendListFragment.class.getSimpleName();
    public static final String FRIEND_STATE_CAN_BE_INVITE = "可以邀請";
    public static final String FRIEND_STATE_ALREADY_INVITE = "已經邀請";
    public static final String FRIEND_STATE_ALREADY_FRIEND = "已經是好友";

    private Context context;
    private RelativeLayout navigationBar;
    private Button btnBack;
    private Button btnPopMenu;
    private TextView barTitle;
    private View rightLine;
    private String userId;
    private String userTel;
    private Button btnRelationAction;
    private AsyncHttpClient httpClient;
    private ProgressDialog waitingDialog;
    private EditText edtFriendTel;
    private String friendId;
    private String friendTel;
    private String friendThumb;
    private String friendState;
    private ImageView ivFriendThumb;
    private TextView tvFriendName;
    private ImageView ivFriendState;
    private String friendName;
    private TextView tvFriendTel;
    private TextView tvFriendStateTip;
    private FriendActionDialog confirmDialog;
    private FriendActionDialog doubleConfirmDialog;
    //transactionLog
    private String currentTime;
    private String append;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.friend_relation, container, false);
        this.context = view.getContext();

        Bundle bundle = getArguments();
        userId = bundle.getString(AppConfig.BUNDLE_ARGU_UID);
        friendId = bundle.getString(AppConfig.BUNDLE_ARGU_FID);
        friendTel = bundle.getString(AppConfig.BUNDLE_ARGU_TEL);
        friendName = bundle.getString(AppConfig.BUNDLE_ARGU_NAME);
        friendThumb = bundle.getString(AppConfig.BUNDLE_ARGU_THUMB);
        friendState = bundle.getString(AppConfig.BUNDLE_ARGU_STATE);

        initView(view);

        return view;
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

        tvFriendTel = (TextView) view.findViewById(R.id.tv_friend_tel);
        tvFriendTel.setText(friendTel);

        ivFriendThumb = (ImageView) view.findViewById(R.id.iv_friend_thumb);
        if ( (friendThumb != null) && (friendThumb.length() > 0) )
            ivFriendThumb.setImageBitmap(AppUtils.base64ToBitmap(friendThumb));

        tvFriendName = (TextView) view.findViewById(R.id.tv_friend_name);
        tvFriendName.setText(friendName);

        ivFriendState = (ImageView) view.findViewById(R.id.iv_friend_state);
        tvFriendStateTip = (TextView) view.findViewById(R.id.tv_friend_state_tip);
        btnRelationAction = (Button) view.findViewById(R.id.btn_relation_action);

        if (friendState.equals(FRIEND_STATE_CAN_BE_INVITE)) {
            ivFriendState.setImageResource(R.drawable.friend_invite_or_already_friend);
            tvFriendStateTip.setText("");
            tvFriendStateTip.setVisibility(View.GONE);
            btnRelationAction.setText(R.string.firend_btn_invite);
            btnRelationAction.setBackgroundResource(R.drawable.btn_style_solid_brown_1);
        }
        else if (friendState.equals(FRIEND_STATE_ALREADY_INVITE)) {
            ivFriendState.setImageResource(R.drawable.friend_already_invite);
            tvFriendStateTip.setText(R.string.friend_state_tip_already_invite);
            tvFriendStateTip.setVisibility(View.VISIBLE);
            btnRelationAction.setText(R.string.friend_btn_invite_again);
            btnRelationAction.setBackgroundResource(R.drawable.btn_style_solid_orange);
        }
        else if (friendState.equals(FRIEND_STATE_ALREADY_FRIEND)) {
            ivFriendState.setImageResource(R.drawable.friend_invite_or_already_friend);
            tvFriendStateTip.setText(R.string.friend_state_tip_already_friend);
            tvFriendStateTip.setVisibility(View.VISIBLE);
            btnRelationAction.setText(R.string.friend_btn_invite_break);
            btnRelationAction.setBackgroundResource(R.drawable.btn_style_solid_orange);
        } else {
            String message = "Something error due to unknown friend state: " + friendState;
            AppUtils.getAlertDialog(context, message).show();
        }
        btnRelationAction.setOnClickListener(btnRelationActionListener);
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

    private void doubleConfirmBreakRelation() {
        String message = getString(R.string.friend_invite_break_double_check_message);
        doubleConfirmDialog = new FriendActionDialog(context);
        doubleConfirmDialog
                .setMessageText(message)
                .setImage(R.drawable.friend_break_double_confirm)
                .setButtonOkText(getString(R.string.friend_btn_sure_exit))
                .setButtonCancelText(getString(R.string.friend_btn_sure_no_exit))
                .setOnClickButtonOkListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        doubleConfirmDialog.dismiss();
                        doFriendActionRequestHttp(AppConfig.DOMAIN_SITE_PATE + AppConfig.API_FRIEND_BREAK);
                    }
                })
                .show();
    }

    private void confirmBreakRelation() {
        String message = getString(R.string.friend_invite_break_check_message).replace(":name:", friendName);
        confirmDialog = new FriendActionDialog(context);
        confirmDialog
            .setMessageText(message)
            .setImage(R.drawable.friend_break_confirm)
            .setButtonOkText(getString(R.string.friend_btn_exit))
            .setButtonCancelText(getString(R.string.friend_btn_no_exit))
            .setOnClickButtonOkListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    callSystemTime();
                    append = currentTime + "," +
                            userId + "," +
                            "FinishFriendRelationDialog" + "," +
                            "FriendRelation" + "," +
                            "btnFinishFriendRelation" + "\n";
                    transactionLogSave(append);
                    confirmDialog.dismiss();
                    doubleConfirmBreakRelation();
                }
            })
            .show();
    }

    private void doFriendActionRequestHttp(String requestUrl) {
        // setup http client
        RequestParams params = new RequestParams();
        params.add(AppConfig.BUNDLE_ARGU_UID, userId);
        params.add(AppConfig.BUNDLE_ARGU_FID, friendId);

        httpClient = new AsyncHttpClient();
        httpClient.setTimeout(AppConfig.TIMEOUT);
        httpClient.post(requestUrl, params, friendActionHttpResponseHandler);
        httpClient = null;

        // show dialog
        waitingDialog = AppUtils.customProgressDialog(context, null, getString(R.string.dialog_read_msg));
        waitingDialog.show();
    }

    private View.OnClickListener btnRelationActionListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (friendState.equals(FRIEND_STATE_CAN_BE_INVITE)) { // ready to invite
                callSystemTime();
                append = currentTime + "," +
                        userId + "," +
                        "FriendRelation" + "," +
                        "FriendActivityAddFriend" + "," +
                        "btnFriendInvite" + "\n";
                transactionLogSave(append);
                doFriendActionRequestHttp(AppConfig.DOMAIN_SITE_PATE + AppConfig.API_FRIEND_INVITE_SEND);
            }
            else if (friendState.equals(FRIEND_STATE_ALREADY_INVITE)) { // ready to invite again
                callSystemTime();
                append = currentTime + "," +
                        userId + "," +
                        "FriendRelation" + "," +
                        "FriendActivityAddFriend" + "," +
                        "btnFriendInviteAgain" + "\n";
                transactionLogSave(append);
                doFriendActionRequestHttp(AppConfig.DOMAIN_SITE_PATE + AppConfig.API_FRIEND_INVITE_SEND);
            }
            else if (friendState.equals(FRIEND_STATE_ALREADY_FRIEND)) { // ready to break
                callSystemTime();
                append = currentTime + "," +
                        userId + "," +
                        "FriendRelation" + "," +
                        "FinishFriendRelationDialog" + "," +
                        "btnFinishFriendRelation" + "\n";
                transactionLogSave(append);
                confirmBreakRelation();
                return;
            } else
                return;
        }
    };

    private JsonHttpResponseHandler friendActionHttpResponseHandler = new JsonHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            super.onSuccess(statusCode, headers, response);

            Log.i(TAG, "JsonHttpResponseHandler onSuccess, response.");

            try {
                if (waitingDialog != null)
                    waitingDialog.dismiss();

                String msgCode = (String)response.get("msgCode");
                if (msgCode.equals("A01")) {
                    String result = (String) response.get("result");
                    Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
                    getFragmentManager().popBackStackImmediate();
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
