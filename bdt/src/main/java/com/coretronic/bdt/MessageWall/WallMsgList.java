package com.coretronic.bdt.MessageWall;

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
import android.widget.*;
import com.coretronic.bdt.AppConfig;
import com.coretronic.bdt.AppUtils;
import com.coretronic.bdt.MessageWall.Adapter.MessageListAdapter;
import com.coretronic.bdt.MessageWall.Module.WallMessageInfo;
import com.coretronic.bdt.MessageWall.Module.WallMessageList;
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
 * Created by Morris on 2014/10/17.
 */
public class WallMsgList extends Fragment {
    private String TAG = WallMsgList.class.getSimpleName();
    private Context mContext;
    private Button btnBack;
    private TextView barTitle;
    private RelativeLayout navigationBar;
    private SharedPreferences sharedPreferences;
    private ProgressDialog progressDialog = null;
    private String uuidChose;
    private AsyncHttpClient asyncHttpClient;
    private Gson gson = new Gson();
    // UI setting
    private Button btnMsgAdd;
    private ListView msgList;
    private ImageView mUserLogo;
    private TextView mUserName;

    // List View
    private MessageListAdapter messageListAdapter;
    private List<WallMessageInfo> wallMessageInfos;
//    private TextView loadmore_btn;

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
                    WallMessageList wallMessageList = gson.fromJson(response.toString(), WallMessageList.class);
                    if (wallMessageList.getResult() != null && wallMessageList.getResult().size() != 0) {
                        wallMessageInfos.addAll(wallMessageList.getResult());
                        messageListAdapter.notifyDataSetChanged();
                        msgList.setSelection(visibleLast - visibleCount);

                    } else {
                        if (wallMessageInfos.size() != 0) {
                            Toast.makeText(mContext, "沒有留言嘍！", 1).show();
                        }
                    }
                } else {
                    AppUtils.getAlertDialog(mContext, response.get("result").toString()).show();
                }
            } catch (JSONException e) {
                AppUtils.getAlertDialog(mContext, getString(R.string.data_result_error)).show();
                e.printStackTrace();
            } catch (JsonSyntaxException e) {
                AppUtils.getAlertDialog(mContext, getString(R.string.data_result_error)).show();
                e.printStackTrace();
            } catch (Exception e) {
                AppUtils.getAlertDialog(mContext, getString(R.string.data_result_error)).show();
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

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.wall_msg_layout, container, false);
        mContext = v.getContext();
        progressDialog = new ProgressDialog(mContext);
        initView(v);
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        wallMessageInfos.clear();
        getWallMsgList(0);
    }

    private void initView(View v) {
        // navigation bar
        navigationBar = (RelativeLayout) v.findViewById(R.id.navigationBar_option);
        btnBack = (Button) v.findViewById(R.id.btnBack);
        barTitle = (TextView) v.findViewById(R.id.action_bar_title);

        barTitle.setText(getString(R.string.pp_wall));
        btnBack.setOnClickListener(btnListener);

        // button
        mUserLogo = (ImageView) v.findViewById(R.id.img_msg_user_photo);
        mUserName = (TextView) v.findViewById(R.id.txt_msg_user_name);
        String str = sharedPreferences.getString(AppConfig.PREF_USER_THUMB, "");
        if (str != null) {
            mUserLogo.setImageBitmap(AppUtils.base64ToBitmap(str));
        }
        mUserName.setText(sharedPreferences.getString(AppConfig.PREF_USER_NANE, ""));

        btnMsgAdd = (Button) v.findViewById(R.id.btnMsgAdd);
        btnMsgAdd.setOnClickListener(btnListener);
        msgList = (ListView) v.findViewById(R.id.msgList);

        wallMessageInfos = new ArrayList<WallMessageInfo>();
        messageListAdapter = new MessageListAdapter(mContext, wallMessageInfos);
        msgList.setAdapter(messageListAdapter);
        msgList.setOnItemClickListener(listItemListener);
        msgList.setOnScrollListener(scrollListener);

//        loadmore_btn = new TextView(mContext);
//        loadmore_btn.setText("Load more...");
//        msgList.addFooterView(loadmore_btn);
    }

    // Location Listener
    AdapterView.OnItemClickListener listItemListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
            WallMessageInfo item = (WallMessageInfo) adapterView.getItemAtPosition(position);
            Log.i(TAG, "positon: " + position);
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putString("refId", item.getRefId());
            bundle.putString("articleType", item.getArticleType());
            bundle.putString("sourceFrom", WallActivity.class.getSimpleName());
            bundle.putString("userName", item.getUserName());
            bundle.putString("userThumb", item.getThumb());
            Log.i(TAG, "bundle: " + bundle);
            callSystemTime();
            append = currentTime + "," +
                    uuidChose + "," +
                    "WallMsgActivity" + "," +
                    item.getRefId() + "," +
                    "btnListItem" + "\n";
            transactionLogSave(append);
            Log.i(TAG,append);
            intent.putExtras(bundle);
            intent.setClass(mContext, WallMsgDetailActivity.class);
            startActivity(intent);

        }
    };

    View.OnClickListener btnListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btnBack:
                    callSystemTime();
                    append = currentTime + "," +
                            uuidChose + "," +
                            "WallMsgActivity" + "," +
                            "Person" + "," +
                            "btnBack" + "\n";
                    transactionLogSave(append);
                    getActivity().finish();
                    break;
                case R.id.btnMsgAdd:
                    callSystemTime();
                    append = currentTime + "," +
                            uuidChose + "," +
                            "WallMsgActivity" + "," +
                            "MsgAdd" + "," +
                            "btnMsgAdd" + "\n";
                    transactionLogSave(append);
                    Fragment fragment = new WallMsgAdd();
                    if (fragment != null) {
                        getActivity().getFragmentManager().beginTransaction()
                                .replace(R.id.frame_container, fragment, "WallMsgAdd")
                                .addToBackStack("WallMsgAdd")
                                .commit();
                    } else {
                        Log.e(TAG, "Error in creating fragment");
                    }
                    break;

            }

        }
    };

    private void getWallMsgList(int start) {
        progressDialog.setMessage(getString(R.string.dialog_download_msg));
        progressDialog.show();

        final String url = AppConfig.DOMAIN_SITE_PATE + AppConfig.REQUEST_WALL_MESSAGE_LIST;
        Log.i(TAG, "url:  " + url);

        uuidChose = sharedPreferences.getString(AppConfig.PREF_UNIQUE_ID, "");
        RequestParams params = new RequestParams();
        params.add("uid", uuidChose);
        params.add("time", AppUtils.getSystemTime());
        params.add("start", String.valueOf(start));
        params.add("num", "10");
        Log.i(TAG, "params:" + params.toString());

        asyncHttpClient.post(url, params, jsonHandler);
    }

    private int visibleCount;
    private int visibleLast = 0;
    AbsListView.OnScrollListener scrollListener = new AbsListView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView absListView, int scrollState) {
            Log.d(TAG, "onScrollStateChanged");
            int lastIndex = messageListAdapter.getCount();
            if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && visibleLast == lastIndex) {
                getWallMsgList(lastIndex);
            }
            Log.d(TAG, "lastIndex:" + lastIndex);
            Log.d(TAG, "visibleLast:" + visibleLast);

        }

        @Override
        public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            visibleCount = visibleItemCount;
            visibleLast = firstVisibleItem + visibleItemCount;
        }
    };

    @Override
    public void onPause() {
        super.onPause();
        if (progressDialog != null) {
            progressDialog.dismiss();
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

