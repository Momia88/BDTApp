package com.coretronic.bdt.Person;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.coretronic.bdt.AppConfig;
import com.coretronic.bdt.AppUtils;
import com.coretronic.bdt.Friend.FriendActivity;
import com.coretronic.bdt.HealthKnowledge.HealthArticleDetailActivity;
import com.coretronic.bdt.MessageWall.WallMsgDetailActivity;
import com.coretronic.bdt.Person.Module.NotifyInfo;
import com.coretronic.bdt.Person.Module.NotifyListAdapter;
import com.coretronic.bdt.R;
import com.coretronic.bdt.Utility.CustomerOneBtnAlertDialog;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by changyuanyu on 14/12/13.
 */
public class NotifyListActivity extends Activity {
    private String TAG = NotifyListActivity.class.getSimpleName();
    final private String url = AppConfig.DOMAIN_SITE_PATE + AppConfig.REQUEST_IN_APP_NOTIFICATION;
    private TextView barTitle;
    private Button btnPopMenu;
    private Button btnBack;
    private View rightLine = null;
    private Context mContext;
    private RelativeLayout navigationBar;
    private CustomerOneBtnAlertDialog netWorkAlert = null;
    private CustomerOneBtnAlertDialog alert = null;

    private AsyncHttpClient asyncHttpClient;
    private ListView listView = null;
    private ProgressDialog progressDialog = null;
    private RequestParams params = null;
    private List<NotifyInfo.Result> notifyList = null;
    private NotifyInfo notifyInfo = null;
    private NotifyListAdapter listAdapter = null;
    private Gson gson = new Gson();
    private int visibleLast = 0;
    private View loadMoreView;
    private int visibleCount;
    private TextView loadMoreText;
    private SharedPreferences sharedPreferences;
    private String messageType = "";
    private String referenceId = "";
    private String refArticleType = "";

    public JsonHttpResponseHandler updateReadJsonHandler = new JsonHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
            Log.d(TAG, "updateReadJsonHandler onSuccess = " + response);

            try {
                if (response.get("msgCode").equals(AppConfig.SUCCESS_CODE)) {

                    if (progressDialog != null) {
                        progressDialog.dismiss();
                    }
                    Bundle bundle = new Bundle();
                    Intent intent = new Intent();
                    if (messageType.equals("article") || messageType.equals("message")) {
                        bundle.putString("refId", referenceId);
                        bundle.putString("articleType", refArticleType);
                        bundle.putString("sourceFrom", TAG);
                        intent.setClass(NotifyListActivity.this, WallMsgDetailActivity.class);
                        intent.putExtras(bundle);
                        mContext.startActivity(intent);
                    } else if (messageType.equals("health")) {
                        bundle.putString("stateChange", TAG);
                        bundle.putString(AppConfig.NEWS_ID_KEY, referenceId);
                        intent.setClass(NotifyListActivity.this, HealthArticleDetailActivity.class);
                        intent.putExtras(bundle);
                        mContext.startActivity(intent);
                    }else if (messageType.equals("invite")) {
                        intent = new Intent(NotifyListActivity.this, FriendActivity.class);
                        mContext.startActivity(intent);
                    }
                } else {
                    alert.setMsg(getString(R.string.data_load_error))
                            .setPositiveBtnText(getString(R.string.sure))
                            .setPositiveListener(erroAlertListener)
                            .show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                alert.setMsg(getString(R.string.data_load_error))
                        .setPositiveBtnText(getString(R.string.sure))
                        .setPositiveListener(erroAlertListener)
                        .show();
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
                alert.setMsg(getString(R.string.data_load_error))
                        .setPositiveBtnText(getString(R.string.sure))
                        .setPositiveListener(erroAlertListener)
                        .show();

            } catch (Exception e) {
                e.printStackTrace();
                alert.setMsg(getString(R.string.data_load_error))
                        .setPositiveBtnText(getString(R.string.sure))
                        .setPositiveListener(erroAlertListener)
                        .show();
            }

        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            Log.d(TAG, "onFailure");
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
            alert.setMsg(getString(R.string.data_load_error))
                    .setPositiveBtnText(getString(R.string.sure))
                    .setPositiveListener(erroAlertListener)
                    .show();
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            super.onFailure(statusCode, headers, responseString, throwable);
            Log.i(TAG, "444");
            Log.i(TAG, "responseString:" + responseString);

            alert.setMsg(getString(R.string.data_load_error))
                    .setPositiveBtnText(getString(R.string.sure))
                    .setPositiveListener(erroAlertListener)
                    .show();

        }
    };

    public JsonHttpResponseHandler jsonHandler = new JsonHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            Log.d(TAG, "onSuccess = " + response);

            try {
                if (response.get("msgCode").equals(AppConfig.SUCCESS_CODE)) {
                    notifyInfo = gson.fromJson(response.toString(), NotifyInfo.class);
                    requestData(response);
                } else {
                    netWorkAlert = AppUtils.getAlertDialog(mContext, getString(R.string.data_result_error), erroAlertListener);
                    netWorkAlert.show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                netWorkAlert = AppUtils.getAlertDialog(mContext, getString(R.string.data_result_error), erroAlertListener);
                netWorkAlert.show();
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
                netWorkAlert = AppUtils.getAlertDialog(mContext, getString(R.string.data_result_error), erroAlertListener);
                netWorkAlert.show();
            } catch (Exception e) {
                e.printStackTrace();
                netWorkAlert = AppUtils.getAlertDialog(mContext, getString(R.string.data_result_error), erroAlertListener);
                netWorkAlert.show();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            Log.d(TAG, "onFailure");
            netWorkAlert = AppUtils.getAlertDialog(mContext, getString(R.string.data_result_error), erroAlertListener);
            netWorkAlert.show();
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            super.onFailure(statusCode, headers, responseString, throwable);
            Log.i(TAG, "responseString:" + responseString);
            netWorkAlert.setMsg(getString(R.string.data_result_error))
                    .setPositiveBtnText(getString(R.string.sure))
                    .setPositiveListener(erroAlertListener)
                    .show();
        }
    };

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_newarticles);
        mContext = this;
        sharedPreferences = getSharedPreferences(AppConfig.SHAREDPREFERENCES_NAME, 0);
        progressDialog = new ProgressDialog(mContext);
        initView();
        barTitle.setText(R.string.notify_title);
        btnPopMenu.setVisibility(View.GONE);
        rightLine.setVisibility(View.GONE);
        alert = new CustomerOneBtnAlertDialog(mContext);
        try {
            asyncHttpClient = new AsyncHttpClient();
            asyncHttpClient.setTimeout(AppConfig.TIMEOUT);
            notifyList = new ArrayList<NotifyInfo.Result>();
            loadMoreView = getLayoutInflater().inflate(R.layout.list_more, null);
            loadMoreText = (TextView) loadMoreView.findViewById(R.id.load_text);
            listView.addFooterView(loadMoreView);
            listAdapter = new NotifyListAdapter(mContext, notifyList);
            listView.setAdapter(listAdapter);
            // scroll listener
            listView.setOnScrollListener(scrollListener);
            listView.setOnItemClickListener(listViewItemListener);
        } catch (Exception e) {
            netWorkAlert.setMsg(getString(R.string.get_data_error))
                    .setPositiveBtnText(getString(R.string.sure))
                    .setPositiveListener(erroAlertListener)
                    .show();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        notifyList.clear();
        doUpdate(0);

    }

    private void initView() {
        // navigation bar
        navigationBar = (RelativeLayout) findViewById(R.id.navigationBar_option);
        btnBack = (Button) findViewById(R.id.btnBack);
        btnPopMenu = (Button) findViewById(R.id.btnPopMenu);
        rightLine = (View) findViewById(R.id.right_line);
        btnBack.setOnClickListener(btnListener);
        btnPopMenu.setOnClickListener(btnListener);
        barTitle = (TextView) findViewById(R.id.action_bar_title);
        listView = (ListView) findViewById(R.id.mainArticlesListView);
    }

    private void requestData(JSONObject response) {
        try {
            notifyInfo = gson.fromJson(response.toString(), NotifyInfo.class);
            Log.d(TAG, "getMsgCode() = " + notifyInfo.getMsgCode());

            if (notifyInfo.getResult() == null) {
                netWorkAlert.setMsg(getString(R.string.get_data_error))
                        .setPositiveBtnText(getString(R.string.sure))
                        .setPositiveListener(erroAlertListener)
                        .show();
                return;
            }
            listView.setVisibility(View.VISIBLE);

            int count = listAdapter.getCount();
            for (int i = 0; i < notifyInfo.getResult().size(); i++) {
                notifyList.add(notifyInfo.getResult().get(i));
            }

            Log.i(TAG, "articleResultList Size:  " + notifyList.size());
            listView.setSelection(visibleLast - visibleCount + 1);
            listAdapter.notifyDataSetChanged();


            if (notifyInfo.getResult().size() < AppConfig.SHOW_CONTENT_NUMBER) {
                Log.i(TAG, "-----last chose < 10-----");
                loadMoreText.setVisibility(View.GONE);
                return;
            }

            if ((visibleLast - visibleCount) < 0) {
                listView.setSelection(0);
            } else {
                listView.setSelection(visibleLast - visibleCount);
            }
            loadMoreText.setText("");
            listView.setVisibility(View.VISIBLE);

        } catch (Exception e) {
            netWorkAlert.setMsg(getString(R.string.get_data_error))
                    .setPositiveBtnText(getString(R.string.sure))
                    .setPositiveListener(erroAlertListener)
                    .show();
        }
    }


    // Call API
    // doupdate new data , post to server
    private void doUpdate(int start) {
        loadMoreText.setVisibility(View.VISIBLE);
        final String url = AppConfig.DOMAIN_SITE_PATE + AppConfig.REQUEST_IN_APP_NOTIFICATION;
        params = new RequestParams();
        JSONArray jsonArray = new JSONArray();

        params.add("uid", sharedPreferences.getString(AppConfig.PREF_UNIQUE_ID, ""));
//        params.add("uid", "U0000000000000000051");
        params.add("time", AppUtils.getSystemTime());
        params.add("start", String.valueOf(start));
        params.add("num", AppConfig.SHOW_CONTENT_NUMBER + "");

        //將參數包成一個array
        jsonArray.put(params);
        Log.i(TAG, "PARAMS:  " + params);
        loadMoreText.setText(getString(R.string.doupdata));
        asyncHttpClient.post(url, params, jsonHandler);
    }

    private void updateMessageRead(String notifyId) {

        progressDialog.setMessage(getString(R.string.dialog_download_msg));
        progressDialog.show();

        final String url = AppConfig.DOMAIN_SITE_PATE + AppConfig.UPDATE_IN_APP_NOTIFICATION_STATE;
        Log.i(TAG, "url:  " + url);

        RequestParams params = new RequestParams();
        params.add("uid", sharedPreferences.getString(AppConfig.PREF_UNIQUE_ID, ""));
//        params.add("uid", "U0000000000000000051");
        params.add("time", AppUtils.getSystemTime());
        params.add("notificationId", notifyId);
        Log.i(TAG, "params:  " + params);
        asyncHttpClient.post(url, params, updateReadJsonHandler);
    }


    AbsListView.OnScrollListener scrollListener = new AbsListView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            Log.i(TAG, "onScrollStateChanged");
            int itemsLastIndex = listAdapter.getCount() - 1;
            int lastIndex = itemsLastIndex + 1;
            if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && visibleLast == lastIndex) {
                doUpdate(lastIndex);
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            Log.i(TAG, "onScroll");
            visibleCount = visibleItemCount;
            visibleLast = firstVisibleItem + visibleItemCount - 1;
        }
    };


    AdapterView.OnItemClickListener listViewItemListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            if (notifyList != null) {
                try {
                    messageType = notifyList.get(position).getMessageType();
                    referenceId = notifyList.get(position).getReferenceId();
                    refArticleType = notifyList.get(position).getRefArticleType();
                    String str = notifyList.get(position).getNotificationId();
                    updateMessageRead(str);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };


    private View.OnClickListener btnListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btnBack:
                    finish();
                    break;
            }
        }
    };


    private View.OnClickListener erroAlertListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Log.i(TAG, " on error Litener click");
            if (netWorkAlert != null) {
                netWorkAlert.dismiss();
            }
            finish();
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause");

        if (netWorkAlert != null) {
            netWorkAlert.dismiss();
        }

        notifyList.clear();

    }
}
