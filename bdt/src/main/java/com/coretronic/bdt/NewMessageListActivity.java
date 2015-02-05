package com.coretronic.bdt;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.coretronic.bdt.Adapter.NewMessageListBaseAdapter;
import com.coretronic.bdt.DataModule.HomeMessageInfo;
import com.coretronic.bdt.MessageWall.WallMsgDetailActivity;
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
public class NewMessageListActivity extends Activity {
    final private String url = AppConfig.DOMAIN_SITE_PATE + AppConfig.REQUEST_SEARCH_RESULT;
    private TextView barTitle;
    private Button btnPopMenu;
    private Button btnBack;
    private View rightLine = null;
    private Context mContext;
    private RelativeLayout navigationBar;
    private String TAG = NewMessageListActivity.class.getSimpleName();
    private ProgressDialog progressDialog = null;
    private AsyncHttpClient asyncHttpClient;
    private CustomerOneBtnAlertDialog alert = null;

    private ListView listView = null;
    //    private ProgressDialog dialog = null;
    private RequestParams params = null;
    private AsyncHttpClient client = null;
    private List<HomeMessageInfo.Result> homeMessageResultList = null;
    private HomeMessageInfo tempResultList = null;
    private HomeMessageInfo homeMessageInfo = null;
    private NewMessageListBaseAdapter listAdapter = null;
    private Gson gson = new Gson();
    private int visibleLast = 0;
    private View loadMoreView;
    private int visibleCount;
    private TextView loadMoreText;
    private boolean lastItemFlag = true;
    private SharedPreferences sharedPreferences;
    private String clickType = "";
    private String clickMsgId = "";
    private String clickId = "";
    private Boolean isLogin = false;

    public JsonHttpResponseHandler jsonHandler = new JsonHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            Log.d(TAG, "onSuccess = " + response);

            try {
                homeMessageInfo = gson.fromJson(response.toString(), HomeMessageInfo.class);
                Log.d(TAG, "cityInfo.getMsgCode() = " + homeMessageInfo.getMsgCode());

                lastItemFlag = true;
                requestData(response);
            }
            catch(Exception e)
            {
                alert.setMsg(getString(R.string.get_data_error))
                        .setPositiveBtnText(getString(R.string.sure))
                        .setPositiveListener(erroAlertListener)
                        .show();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            Log.d(TAG, "onFailure");
//            if(dialog != null){
//                dialog.dismiss();
//            }
//            AppUtils.getAlertDialog(mContext, getString(R.string.data_load_error)).show();

            alert.setMsg(getString(R.string.get_data_error))
                    .setPositiveBtnText(getString(R.string.sure))
                    .setPositiveListener(erroAlertListener)
                    .show();
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            super.onFailure(statusCode, headers, responseString, throwable);
            Log.i(TAG, "444");
            Log.i(TAG, "responseString:" + responseString);
            alert.setMsg(getString(R.string.data_result_error))
                    .setPositiveBtnText(getString(R.string.sure))
                    .setPositiveListener(erroAlertListener)
                    .show();
        }
    };



    public JsonHttpResponseHandler updateReadJsonHandler = new JsonHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
            Log.d(TAG, "updateReadJsonHandler onSuccess = " + response);

            try {
                if (response.get("msgCode").equals(AppConfig.SUCCESS_CODE)) {

                    if( progressDialog != null )
                    {
                        progressDialog.dismiss();
                    }

                    Bundle bundle = new Bundle();
                    Intent intent = new Intent();

                    if( isLogin != true)
                    {

                        homeMessageResultList.clear();
                        doUpdate(0);
                        listAdapter.notifyDataSetChanged();
                        return;
                    }

                    if (clickType.equals("daily")) {
                        bundle.putString("refId", clickId);
                        bundle.putString("articleType", "daily");
                        bundle.putString("sourceFrom", TAG);
                            intent.setClass(NewMessageListActivity.this, WallMsgDetailActivity.class);
                            intent.putExtras(bundle);
                            startActivity(intent);
                            finish();
//                        Toast.makeText(mContext, "go to daily page", 1).show();
                    } else if (clickType.equals("comment")) {
                        bundle.putString("sourceFrom", TAG);
                        bundle.putString("articleType", "comment");
//                        bundle.putString("messageId", clickMsgId);
                        bundle.putString("refId", clickId);
                        intent.setClass(NewMessageListActivity.this, WallMsgDetailActivity.class);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        finish();
//                        Toast.makeText(mContext, "go to comment page", 1).show();
                    }

                } else {
//                    alert = AppUtils.getAlertDialog(mContext, getString(R.string.data_result_error),erroAlertListener);
//                    alert.show();
                    alert.setMsg(getString(R.string.data_load_error))
                            .setPositiveBtnText(getString(R.string.sure))
                            .setPositiveListener(erroAlertListener)
                            .show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
//                alert = AppUtils.getAlertDialog(mContext, getString(R.string.data_result_error),erroAlertListener);
//                alert.show();
                alert.setMsg(getString(R.string.data_load_error))
                        .setPositiveBtnText(getString(R.string.sure))
                        .setPositiveListener(erroAlertListener)
                        .show();
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
//                alert = AppUtils.getAlertDialog(mContext, getString(R.string.data_result_error),erroAlertListener);
//                alert.show();
                alert.setMsg(getString(R.string.data_load_error))
                        .setPositiveBtnText(getString(R.string.sure))
                        .setPositiveListener(erroAlertListener)
                        .show();

            } catch (Exception e) {
                e.printStackTrace();
//                alert = AppUtils.getAlertDialog(mContext, getString(R.string.data_result_error),erroAlertListener);
//                alert.show();
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

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_newmessage);

        try {
            mContext = this;
            sharedPreferences = getSharedPreferences(AppConfig.SHAREDPREFERENCES_NAME, 0);

            initView();
            barTitle.setText(R.string.new_msg_title);
            btnPopMenu.setVisibility(View.GONE);
            rightLine.setVisibility(View.GONE);

            alert = new CustomerOneBtnAlertDialog(mContext);

            homeMessageResultList = new ArrayList<HomeMessageInfo.Result>();
            progressDialog = new ProgressDialog(mContext);

            loadMoreView = getLayoutInflater().inflate(R.layout.list_more, null);
            loadMoreText = (TextView) loadMoreView.findViewById(R.id.load_text);
            listView.addFooterView(loadMoreView);

            listAdapter = new NewMessageListBaseAdapter(mContext, homeMessageResultList);
            listView.setAdapter(listAdapter);

            doUpdate(0);

            listView.setOnScrollListener(scrollListener);
            listView.setOnItemClickListener(listViewItemListener);
        }
        catch(Exception e)
        {
            alert.setMsg(getString(R.string.returndata_error))
                    .setPositiveBtnText(getString(R.string.sure))
                    .setPositiveListener(erroAlertListener)
                    .show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        isLogin  = sharedPreferences.getBoolean(AppConfig.PREF_IS_LOGIN, false);

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
        listView = (ListView) findViewById(R.id.mainMessageListView);
    }


    private void updateMessageRead()
    {

        progressDialog.setMessage(getString(R.string.dialog_download_msg));
        progressDialog.show();

        final String url = AppConfig.DOMAIN_SITE_PATE + AppConfig.INSERT_MESSAGE_READ;
        Log.i(TAG, "url:  " + url);

        asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.setTimeout(AppConfig.TIMEOUT);
        RequestParams params = new RequestParams();
        params.add("uid", sharedPreferences.getString(AppConfig.PREF_UNIQUE_ID, ""));
//        params.add("uid", "U0000000000000000020");
//        params.add("time", AppUtils.getSystemTime());
        params.add("messageId", clickMsgId);
        Log.i(TAG, "params:  " + params);
        asyncHttpClient.post(url, params, updateReadJsonHandler);
    }

    private void requestData(JSONObject response) {
        homeMessageInfo = gson.fromJson(response.toString(), HomeMessageInfo.class);
        Log.d(TAG, "cityInfo.getMsgCode() = " + homeMessageInfo.getMsgCode());

        if (homeMessageInfo.getMsgCode().equals(AppConfig.SUCCESS_CODE)) {

            if (homeMessageInfo.getResult().size() != 0) {
                listView.setVisibility(View.VISIBLE);

                int count = listAdapter.getCount();
                for (int i = 0; i < homeMessageInfo.getResult().size(); i++) {
                    homeMessageResultList.add(homeMessageInfo.getResult().get(i));
                }

                Log.i(TAG, "homeMessageResultList Size:  " + homeMessageResultList.size());
                listView.setSelection(visibleLast - visibleCount + 1);
                listAdapter.notifyDataSetChanged();


                if (homeMessageInfo.getResult().size() < AppConfig.SHOW_CONTENT_NUMBER) {
                    Log.i(TAG, "-----last chose < 10-----");
                    loadMoreText.setVisibility(View.GONE);
                    lastItemFlag = false;
                    return;
                }

                if ((visibleLast - visibleCount) < 0) {
                    listView.setSelection(0);
                } else {
                    listView.setSelection(visibleLast - visibleCount);
                }
//                Log.i(TAG, "articleResultList size:" + articleResultList.size() + "/articleResultList:"+articleResultList.toString());
                loadMoreText.setText("");
                listView.setVisibility(View.VISIBLE);

//                dialog.dismiss();

            } else {
                lastItemFlag = false;
                loadMoreText.setText("");
                loadMoreText.setVisibility(View.GONE);
//                listView.setVisibility(View.GONE);
//                dialog.dismiss();
//                AppUtils.getAlertDialog(mContext, getString(R.string.no_data_info)).show();
            }

        } else if (homeMessageInfo.getMsgCode().substring(0, 1).equals(AppConfig.ERROR_CODE)) {

            //AppUtils.getAlertDialog(mContext, homeMessageInfo.getStatus()).show();

            alert.setMsg(homeMessageInfo.getStatus() )
                    .setPositiveBtnText(getString(R.string.sure))
                    .setPositiveListener(erroAlertListener)
                    .show();

            Log.e(TAG, homeMessageInfo.getStatus());
        }
    }



    AdapterView.OnItemClickListener listViewItemListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            view.setSelected(true);

            if( homeMessageResultList!= null) {
                try {
                    Log.i(TAG, "article type:" + homeMessageResultList.get(position).getArticletype());
                    clickType = homeMessageResultList.get(position).getArticletype();
                    clickMsgId = homeMessageResultList.get(position).getMessageId();
                    clickId = homeMessageResultList.get(position).getId();
                    updateMessageRead();
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    };



    // doupdate new data , post to server
    private void doUpdate(int start) {
        final String url = AppConfig.DOMAIN_SITE_PATE + AppConfig.REQUEST_HOME_MSG;
        params = new RequestParams();
        JSONArray jsonArray = new JSONArray();
//        dialog = AppUtils.customProgressDialog(mContext, null, getString(R.string.dialog_read_msg));
//        dialog.show();

        params.add("uid", sharedPreferences.getString(AppConfig.PREF_UNIQUE_ID, ""));
//        params.add("uid", "U0000000000000000020");
        params.add("start", String.valueOf(start));
        params.add("num", AppConfig.SHOW_CONTENT_NUMBER + "");
        params.add("time", AppUtils.getChineseSystemTime());

        //將參數包成一個array
        jsonArray.put(params);
        Log.i(TAG, "PARAMS:  " + params);
        loadMoreText.setText(getString(R.string.doupdata));

        client = new AsyncHttpClient();
//        client.setTimeout(AppConfig.TIMEOUT);
        client.setTimeout(20000);
        lastItemFlag = false;
        client.post(url, params, jsonHandler);
        params = null;
        client = null;
    }


    AbsListView.OnScrollListener scrollListener = new AbsListView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            Log.i(TAG, "onScrollStateChanged");
            if (lastItemFlag == false) {
                return;
            }

            int itemsLastIndex = listAdapter.getCount() - 1;
            int lastIndex = itemsLastIndex + 1;
            // lastIndex+1 ~ n
            if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && visibleLast == lastIndex) {
                doUpdate(lastIndex);
            }

//            int itemsLastIndex = listAdapter.getCount() - 1;
//            // (fixed) footer index
//            int lastIndex = itemsLastIndex + 1;
//            Log.i(TAG, "itemsLastIndex:" + itemsLastIndex);
//            Log.i(TAG, "lastIndex:" + lastIndex);
//            // lastIndex:visible last index(include footer)
//            if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE &&
//                    lastIndex == visibleLast) {
//                Log.i(TAG, "scrollListener doUpdate");
//
//                // doUpdate
//                // lastIndex+1 為筆數
//                doUpdate(lastIndex);
//            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            Log.i(TAG, "onScroll");

            visibleCount = visibleItemCount;
            visibleLast = firstVisibleItem + visibleItemCount - 1;
//            visibleCount = visibleItemCount;
//            //
//            visibleLast = firstVisibleItem + visibleItemCount - 1;
//            // 畫面看到的個數和
//            Log.i(TAG, "visibleCount:" + visibleCount);
//            // 畫面上最後一筆index
//            Log.i(TAG, "visibleLast:" + visibleLast);
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


    private View.OnClickListener erroAlertListener  = new View.OnClickListener() {
        public void onClick(View v) {
            Log.i(TAG, " on error Litener click");
            if( alert != null)
            {
                alert.dismiss();
            }
            finish();

        }

    };
    // 清除listview內容
    private void clearListData() {
        if (homeMessageInfo != null) {
            homeMessageInfo = null;
//            doctorInfo = null;
        }


        if (params != null) {
            params = null;
        }

        if (client != null) {
            client = null;
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause");

//        homeMessageResultList.clear();

        if( progressDialog != null )
        {
            progressDialog.dismiss();
        }

        if (alert != null) {
            alert.dismiss();
        }

    }
}
