package com.coretronic.bdt.MessageWall;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.coretronic.bdt.AppConfig;
import com.coretronic.bdt.AppUtils;
import com.coretronic.bdt.MessageWall.Module.FriendMessage;
import com.coretronic.bdt.MessageWall.Module.WallMessageDetail;
import com.coretronic.bdt.MessageWall.Module.WallMessageList;
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

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Morris on 2014/10/17.
 */
public class WallFriendMsg extends Fragment {
    private String TAG = WallFriendMsg.class.getSimpleName();
    private Context mContext;
    private SharedPreferences sharedPreferences;
    private ProgressDialog progressDialog = null;
    private String uuidChose;
    private AsyncHttpClient asyncHttpClient;
    private Gson gson = new Gson();
    private SimpleDateFormat formatter;
    private Date curDate;

    //view item
    private LinearLayout mFriendMsgListLayout;
    private ImageView mMsgPicAdd;
    private EditText mMsgPicAddComand;
    private Button mMsgPicAddSumit;
    private TextView mTvNoMsg;
    private TextView mGetMore;

    // List View
    private List<FriendMessage> messageseList;
    // get bundle data
    private String refId = "";
    private String articleType = "";
    private String sourceFrom = "";

    // customer view
    private LayoutInflater inflater;
    private CustomerOneBtnAlertDialog oneBtnAlertDialog;
    //transactionLog
    private String currentTime;
    private String append;

    public JsonHttpResponseHandler jsonHandler = new JsonHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            Log.d(TAG, "onSuccess = " + response);
            try {
                if (response.get("msgCode").equals("A01")) {
                    WallMessageDetail wallMessageDetail = gson.fromJson(response.toString(), WallMessageDetail.class);
                    List<FriendMessage> temp = wallMessageDetail.getResult().getMessages();
                    if (temp != null && temp.size() != 0) {
                        messageseList.addAll(temp);
                        if (messageseList != null && messageseList.size() > 0) {
                            mFriendMsgListLayout.removeAllViews();
                            mTvNoMsg.setVisibility(View.GONE);
                            mGetMore.setVisibility(View.VISIBLE);
                            for (FriendMessage f : messageseList) {
                                View v = getView(f);
                                if (v != null) {
                                    mFriendMsgListLayout.addView(v);
                                }
                            }
                        } else {
                            mGetMore.setVisibility(View.GONE);
                            mTvNoMsg.setVisibility(View.VISIBLE);
                        }
                    } else {
                        if (messageseList.size() == 0) {
                            mTvNoMsg.setVisibility(View.VISIBLE);
                        } else {
                            Toast.makeText(mContext, "沒有留言嘍！", 1).show();
                        }
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

            if (progressDialog != null) {
                progressDialog.dismiss();
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

    public JsonHttpResponseHandler msgAddHandler = new JsonHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

            Log.d(TAG, "onSuccess = " + response);
            try {
                if (response.get("msgCode").equals("A01")) {
                    AppUtils.hideSoftKeyborad(getActivity());
                    messageseList.clear();
                    getWallMsgDetail(0);
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
            if (progressDialog != null) {
                progressDialog.dismiss();
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
        formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        curDate = new Date(System.currentTimeMillis()); // 獲取當前時間
        inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        messageseList = new ArrayList<FriendMessage>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.wall_detail_friend_msg_list, container, false);
        mContext = v.getContext();
        progressDialog = new ProgressDialog(mContext);
        initView(v);
        Bundle bundle = getArguments();
        if (bundle != null) {
            refId = bundle.getString("refId");
            articleType = bundle.getString("articleType");
            sourceFrom = bundle.getString("sourceFrom");
        }
        getWallMsgDetail(0);
        return v;
    }

    private void initView(View v) {
        mFriendMsgListLayout = (LinearLayout) v.findViewById(R.id.friend_msg_list);
        mMsgPicAdd = (ImageView) v.findViewById(R.id.msgPicAdd);
        mMsgPicAddComand = (EditText) v.findViewById(R.id.msgPicAddComand);
        mMsgPicAddSumit = (Button) v.findViewById(R.id.msgPicAddSumit);
        mTvNoMsg = (TextView) v.findViewById(R.id.tvNoMsg);
        mGetMore = (TextView) v.findViewById(R.id.btnGetMore);
        mGetMore.setOnClickListener(btnListener);
        mMsgPicAddSumit.setOnClickListener(btnListener);
    }


    // Location Listener
    AdapterView.OnItemClickListener listItemListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
            WallMessageList item = (WallMessageList) adapterView.getItemAtPosition(position);
        }
    };

    View.OnClickListener btnListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.msgPicAddSumit:
                    callSystemTime();
                    append = currentTime + "," +
                            uuidChose + "," +
                            "WallMsgDetail" + "," +
                            "WallMsgDetailAddSumit" + "," +
                            "btnAddSumit" + "\n";
                    transactionLogSave(append);
                    String msg = mMsgPicAddComand.getText().toString();
                    if (msg != null && msg.length() > 0) {
                        sendWallMsg(msg);
                        mMsgPicAddComand.setText("");
                    }
                    break;
                case R.id.btnGetMore:
                    callSystemTime();
                    append = currentTime + "," +
                            uuidChose + "," +
                            "WallMsgDetail" + "," +
                            "WallMsgDetailGetMore" + "," +
                            "btnGetMore" + "\n";
                    transactionLogSave(append);
                    int lastIndex = messageseList.size();
                    getWallMsgDetail(lastIndex);
                    break;
            }
        }
    };


    private View getView(FriendMessage friendMessage) {
        View v = inflater.inflate(R.layout.wall_detail_friend_msg_list_item, null);
        ImageView mImgUserPic = (ImageView) v.findViewById(R.id.imgUserPic);
        TextView mTxtUserName = (TextView) v.findViewById(R.id.txtUserName);
        TextView mTxtMsgTime = (TextView) v.findViewById(R.id.txtMsgTime);
        TextView mTxtMsg = (TextView) v.findViewById(R.id.txtMsg);
        if (friendMessage != null) {
            if (friendMessage.getThumb() != null) {
                mImgUserPic.setImageBitmap(AppUtils.base64ToBitmap(friendMessage.getThumb()));
            }
            mTxtUserName.setText(friendMessage.getRealName());
            mTxtMsgTime.setText(friendMessage.getTime());
            mTxtMsg.setText(friendMessage.getMessage());
        }
        return v;
    }

    private void getWallMsgDetail(int start) {
        progressDialog.setMessage(getString(R.string.dialog_download_msg));
//        progressDialog.show();

        final String url = AppConfig.DOMAIN_SITE_PATE + AppConfig.REQUEST_ARTICLE_MESSAGES;
        Log.i(TAG, "url:  " + url);

        uuidChose = sharedPreferences.getString(AppConfig.PREF_UNIQUE_ID, "");
        RequestParams params = new RequestParams();
        params.add("uid", uuidChose);
        params.add("time", AppUtils.getSystemTime());
        params.add("refId", refId);
        params.add("start", String.valueOf(start));
        params.add("num", "3");
        Log.i(TAG, "params:" + params.toString());

        asyncHttpClient.post(url, params, jsonHandler);
    }

    private void sendWallMsg(String msg) {
        progressDialog.setMessage(getString(R.string.dialog_download_msg));
        progressDialog.show();

        final String url = AppConfig.DOMAIN_SITE_PATE + AppConfig.INSERT_MESSAGE;
        Log.i(TAG, "url:  " + url);

        uuidChose = sharedPreferences.getString(AppConfig.PREF_UNIQUE_ID, "");
        RequestParams params = new RequestParams();
        params.add("uid", uuidChose);
        params.add("time", AppUtils.getSystemTime());
        params.add("refId", refId);
        params.add("articleType", articleType);
        params.add("message", msg);
        Log.i(TAG, "params:" + params.toString());

        asyncHttpClient.post(url, params, msgAddHandler);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        if (oneBtnAlertDialog != null) {
            oneBtnAlertDialog.dismiss();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (progressDialog != null) {
            progressDialog.dismiss();
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

