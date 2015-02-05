package com.coretronic.bdt.Friend;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
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
import com.coretronic.bdt.DataModule.FriendListInfo;
import com.coretronic.bdt.DataModule.FriendThumbInfo;
import com.coretronic.bdt.Friend.Adapter.FriendListAdapter;
import com.coretronic.bdt.Friend.Adapter.FriendListItem;
import com.coretronic.bdt.Friend.Adapter.SeparatedListAdapter;
import com.coretronic.bdt.Friend.Module.FriendActionDialog;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by poter.hsu on 2014/12/12.
 */
public class FriendListFragment extends Fragment {

    private static final String TAG = FriendListFragment.class.getSimpleName();
    private static final String BTN_ADD_FRIEND_TEXT = "新增";
    public static final String SECTION_INVITE_ME_TEXT = "誰邀請我";
    public static final String SECTION_MY_FRIEND_TEXT = "我的好友";
    public static final String SECTION_INVITED_TEXT = "已邀請好友";

    private Context context;
    private Button btnAddFriend;
    private Button btnBack;
    private TextView barTitle;
    private RelativeLayout navigationBar;
    private AsyncHttpClient httpClient;
    private ListView friendListView;
    private List<FriendListItem> inviteMeListItemList;
    private List<FriendListItem> myFriendListItemList;
    private List<FriendListItem> invitedListItemList;
    private ProgressDialog waitingDialog;
    private SeparatedListAdapter separatedListAdapter;
    private String userId;
    private String userTel;
    private SharedPreferences sharedPreferences;
    //transactionLog
    private String currentTime;
    private String append;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.friend_list, container, false);
        this.context = view.getContext();
        initView(view);

        sharedPreferences = getActivity().getSharedPreferences(AppConfig.SHAREDPREFERENCES_NAME, Context.MODE_PRIVATE);

        userId = sharedPreferences.getString(AppConfig.PREF_UNIQUE_ID, "");
        userTel = sharedPreferences.getString(AppConfig.PREF_USER_PHONE, "");

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // setup http client
        String requestUrl = AppConfig.DOMAIN_SITE_PATE + AppConfig.API_FRIEND_LIST;
        RequestParams params = new RequestParams();
        params.add(AppConfig.BUNDLE_ARGU_UID, userId);
        httpClient = new AsyncHttpClient();
        httpClient.setTimeout(AppConfig.TIMEOUT);
        httpClient.post(requestUrl, params, friendListHttpResponseHandler);
        httpClient = null;

        // show dialog
        waitingDialog = AppUtils.customProgressDialog(context, null, getString(R.string.dialog_read_msg));
        waitingDialog.show();
    }

    private void initView(View view) {
        // Navigation Bar
        navigationBar = (RelativeLayout) view.findViewById(R.id.navigationBar_option);
        btnBack = (Button) view.findViewById(R.id.btnBack);
        btnAddFriend = (Button) view.findViewById(R.id.btnPopMenu);
        barTitle = (TextView) view.findViewById(R.id.action_bar_title);

        barTitle.setText(getString(R.string.pp_friend));
        btnAddFriend.setText(BTN_ADD_FRIEND_TEXT);
        btnBack.setOnClickListener(btnListener);
        btnAddFriend.setOnClickListener(btnListener);

        friendListView = (ListView) view.findViewById(R.id.lv_friend_list);
        friendListView.setOnItemClickListener(onFriendItemClickListener);
    }

    private AdapterView.OnItemClickListener onFriendItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            int position = (int)l;
            FriendListItem friendListItem = (FriendListItem)adapterView.getItemAtPosition(position);
            String friendId = friendListItem.getFriendId();
            String friendName = friendListItem.getFriendName();
            if (friendListItem.getType().equals(AppConfig.INVITE_TYPE_INVITE_ME))
                handleClickedFriendInviteMe(friendId, friendName);
            else if (friendListItem.getType().equals(AppConfig.INVITE_TYPE_INVITED))
                handleClickedFriendInvited(friendId, friendName);
        }
    };

    private void doFriendActionRequestHttp(String requestUrl, String friendId) {
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

    private void handleClickedFriendInviteMe(final String friendId, final String friendName) {
        final FriendActionDialog dialog = new FriendActionDialog(context);
        dialog
            .setMessageText(getString(R.string.friend_invite_want_be_friend).replace(":name:", friendName))
            .setImage(R.drawable.friend_invite)
            .setButtonOkText(getString(R.string.friend_btn_no_want))
            .setButtonCancelText(getString(R.string.friend_btn_want))
            .setOnClickButtonOkListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    doFriendActionRequestHttp(AppConfig.DOMAIN_SITE_PATE + AppConfig.API_FRIEND_INVITE_REJECT, friendId);
                }
            })
            .setOnClickButtonCancelListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    doFriendActionRequestHttp(AppConfig.DOMAIN_SITE_PATE + AppConfig.API_FRIEND_INVITE_ACCEPT, friendId);
                }
            })
            .show();
    }

    private void handleClickedFriendInvited(final String friendId, final String friendName) {
        final FriendActionDialog dialog = new FriendActionDialog(context);
        dialog
                .setMessageText(getString(R.string.friend_invite_want_cancel_friend).replace(":name:", friendName))
                .setImage(R.drawable.friend_break_double_confirm)
                .setButtonOkText(getString(R.string.friend_btn_cancel))
                .setButtonCancelText(getString(R.string.friend_btn_no_cancel))
                .setOnClickButtonOkListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        doFriendActionRequestHttp(AppConfig.DOMAIN_SITE_PATE + AppConfig.API_FRIEND_INVITE_CANCEL, friendId);
                    }
                })
                .show();
    }

    private void requestFriendsThumb(List<FriendListInfo.Result.Item> resultItemList) {
        String requestUrl = AppConfig.DOMAIN_SITE_PATE + AppConfig.API_USER_THUMB;

        for (FriendListInfo.Result.Item resultItem : resultItemList) {
            RequestParams params = new RequestParams();
            params.add(AppConfig.BUNDLE_ARGU_UID, resultItem.getFid());

            httpClient = new AsyncHttpClient();
            httpClient.setTimeout(AppConfig.TIMEOUT);
            httpClient.post(requestUrl, params, friendThumbHttpResponseHandler);
            httpClient = null;
        }
    }

    private void onAddFriendClick() {
        FriendFindFragment friendFindFragment = new FriendFindFragment();
        Bundle bundle = new Bundle();
        bundle.putString(AppConfig.BUNDLE_ARGU_UID, userId);
        bundle.putString(AppConfig.BUNDLE_ARGU_TEL, userTel);
        friendFindFragment.setArguments(bundle);

        getFragmentManager().beginTransaction()
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .replace(R.id.friend_frame_container, friendFindFragment)
            .addToBackStack(null)
            .commit();
    }

    private View.OnClickListener btnListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btnPopMenu:
                    callSystemTime();
                    append = currentTime + "," +
                            userId + "," +
                            "FriendActivity" + "," +
                            "FriendActivityAddFriend" + "," +
                            "btnAddFriend" + "\n";
                    transactionLogSave(append);
                    Log.d(TAG, "btnAddFriend");
                    onAddFriendClick();
                    break;
                case R.id.btnBack:
                    callSystemTime();
                    append = currentTime + "," +
                            userId + "," +
                            "FriendActivity" + "," +
                            "Person" + "," +
                            "btnBack" + "\n";
                    transactionLogSave(append);
                    // here will not intent to MainActivity.
//                    if (sourceFrom != null) {
//                        Intent intent = new Intent(getActivity(), MainAcitvity.class);
//                        startActivity(intent);
//                    }
                    getActivity().finish();
                    break;
            }
        }
    };

    private JsonHttpResponseHandler friendListHttpResponseHandler = new JsonHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            super.onSuccess(statusCode, headers, response);

            Log.i(TAG, "JsonHttpResponseHandler onSuccess, response.");

            try {
                if (response.get("msgCode").equals("A01")) {
                    Gson gson = new Gson();
                    FriendListInfo friendListInfo = gson.fromJson(response.toString(), FriendListInfo.class);

                    // fill invite me list into inviteMeListItemList
                    inviteMeListItemList = new ArrayList<FriendListItem>();
                    List<FriendListInfo.Result.Item> resultItemOfInviteMeList = friendListInfo.getResult().getInvite_me();
                    for (FriendListInfo.Result.Item resultItem : resultItemOfInviteMeList) {
                        FriendListItem friendListItem = new FriendListItem();
                        friendListItem.setFriendId(resultItem.getFid());
                        friendListItem.setFriendName(resultItem.getName());
                        friendListItem.setType(AppConfig.INVITE_TYPE_INVITE_ME);
                        inviteMeListItemList.add(friendListItem);
                    }

                    // fill friend list into myFriendListItemList
                    myFriendListItemList = new ArrayList<FriendListItem>();
                    List<FriendListInfo.Result.Item> resultItemOfFriendList = friendListInfo.getResult().getFd_list();
                    for (FriendListInfo.Result.Item resultItem : resultItemOfFriendList) {
                        FriendListItem friendListItem = new FriendListItem();
                        friendListItem.setFriendId(resultItem.getFid());
                        friendListItem.setFriendName(resultItem.getName());
                        friendListItem.setType(AppConfig.INVITE_TYPE_MY_FRIEND);
                        myFriendListItemList.add(friendListItem);
                    }

                    // fill invite list into invitedListItemList
                    invitedListItemList = new ArrayList<FriendListItem>();
                    List<FriendListInfo.Result.Item> resultItemOfInvitedList = friendListInfo.getResult().getInvite();
                    for (FriendListInfo.Result.Item resultItem : resultItemOfInvitedList) {
                        FriendListItem friendListItem = new FriendListItem();
                        friendListItem.setFriendId(resultItem.getFid());
                        friendListItem.setFriendName(resultItem.getName());
                        friendListItem.setType(AppConfig.INVITE_TYPE_INVITED);
                        invitedListItemList.add(friendListItem);
                    }

                    // setup friend list view adapter
                    separatedListAdapter = new SeparatedListAdapter(context);
                    separatedListAdapter.addSection(SECTION_INVITE_ME_TEXT + " (" + inviteMeListItemList.size() + ")",
                            new FriendListAdapter(context, inviteMeListItemList));
                    separatedListAdapter.addSection(SECTION_MY_FRIEND_TEXT + " (" + myFriendListItemList.size() + ")",
                            new FriendListAdapter(context, myFriendListItemList));
                    separatedListAdapter.addSection(SECTION_INVITED_TEXT + " (" + invitedListItemList.size() + ")",
                            new FriendListAdapter(context, invitedListItemList));
                    friendListView.setAdapter(separatedListAdapter);

                    requestFriendsThumb(resultItemOfInviteMeList);
                    requestFriendsThumb(resultItemOfInvitedList);
                    requestFriendsThumb(resultItemOfFriendList);

                    if (waitingDialog != null)
                        waitingDialog.dismiss();
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

    private JsonHttpResponseHandler friendThumbHttpResponseHandler = new JsonHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            super.onSuccess(statusCode, headers, response);

            Log.i(TAG, "JsonHttpResponseHandler onSuccess, response.");

            try {
                if (response.get("msgCode").equals("A01")) {

                    Gson gson = new Gson();
                    FriendThumbInfo friendThumbInfo = gson.fromJson(response.toString(), FriendThumbInfo.class);

                    String thumb = friendThumbInfo.getResult().getThumb();
                    if ((thumb == null) || (thumb.length() == 0))
                        return;

                    // 比對誰邀請我列表
                    for (FriendListItem friendListItem : inviteMeListItemList) {
                        String friendId = friendThumbInfo.getResult().getUid();
                        if (friendListItem.getFriendId().equals(friendId)) {
                            friendListItem.setFriendThumb(thumb);
                            separatedListAdapter.notifyDataSetChanged();
                            break;
                        }
                    }

                    // 比對已邀請好友列表
                    for (FriendListItem friendListItem : invitedListItemList) {
                        String friendId = friendThumbInfo.getResult().getUid();
                        if (friendListItem.getFriendId().equals(friendId)) {
                            friendListItem.setFriendThumb(thumb);
                            separatedListAdapter.notifyDataSetChanged();
                            break;
                        }
                    }

                    // 比對我的好友列表
                    for (FriendListItem friendListItem : myFriendListItemList) {
                        String friendId = friendThumbInfo.getResult().getUid();
                        if (friendListItem.getFriendId().equals(friendId)) {
                            friendListItem.setFriendThumb(thumb);
                            separatedListAdapter.notifyDataSetChanged();
                            break;
                        }
                    }
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
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            super.onFailure(statusCode, headers, responseString, throwable);

            Log.i(TAG, "JsonHttpResponseHandler onFailure, responseString: " + responseString);
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

                    FriendListFragment friendListFragment = new FriendListFragment();
                    getFragmentManager().beginTransaction().replace(R.id.friend_frame_container, friendListFragment).commit();
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
