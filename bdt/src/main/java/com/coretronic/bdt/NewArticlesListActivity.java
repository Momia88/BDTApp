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
import com.coretronic.bdt.Adapter.NewArticlesListBaseAdapter;
import com.coretronic.bdt.DataModule.ArticleListDataInfo;
import com.coretronic.bdt.DataModule.HomeArticlesInfo;
import com.coretronic.bdt.HealthKnowledge.HealthArticleDetailActivity;
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
public class NewArticlesListActivity extends Activity {
    final private String url = AppConfig.DOMAIN_SITE_PATE + AppConfig.REQUEST_SEARCH_RESULT;
    private TextView barTitle;
    private Button btnPopMenu;
    private Button btnBack;
    private View rightLine = null;
    private Context mContext;
    private RelativeLayout navigationBar;
    private String TAG = NewArticlesListActivity.class.getSimpleName();
    private CustomerOneBtnAlertDialog netWorkAlert = null;
    private CustomerOneBtnAlertDialog alert = null;

    private AsyncHttpClient asyncHttpClient;
    private ListView listView = null;
    private ProgressDialog progressDialog = null;
    private RequestParams params = null;
    private AsyncHttpClient client = null;
    private List<ArticleListDataInfo.Result> serverTestList = null;
    private List<HomeArticlesInfo.Result> homeArticleResultList = null;
    private HomeArticlesInfo tempResultList = null;
    private HomeArticlesInfo homeArticlesInfo = null;
    private NewArticlesListBaseAdapter listAdapter = null;
    private Gson gson = new Gson();
    private int visibleLast = 0;
    private View loadMoreView;
    private int visibleCount;
    private TextView loadMoreText;
    private boolean lastItemFlag = true;
    private SharedPreferences sharedPreferences;
    private String clickMsgId = "";
    private String refId = "";
    private String articleType = "";

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


                    homeArticleResultList.clear();
                    doUpdate(0);
                    listAdapter.notifyDataSetChanged();


                    if (articleType.equals("daily")) {
                        bundle.putString("refId", refId);
                        bundle.putString("articleType", "daily");
                        bundle.putString("sourceFrom", TAG);
                        intent.setClass(NewArticlesListActivity.this, WallMsgDetailActivity.class);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        finish();
                    } else if (articleType.equals("comment")) {
                        bundle.putString("refId", refId);
                        bundle.putString("articleType", "comment");
                        bundle.putString("sourceFrom", TAG);
                        intent.setClass(NewArticlesListActivity.this, WallMsgDetailActivity.class);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        finish();
                    } else if (articleType.equals("news")) {
                        bundle.putString("stateChange", TAG);
                        bundle.putString(AppConfig.NEWS_ID_KEY, refId);
                        intent.setClass(NewArticlesListActivity.this, HealthArticleDetailActivity.class);
                        intent.putExtras(bundle);
                        mContext.startActivity(intent);
                        finish();

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

    public JsonHttpResponseHandler jsonHandler = new JsonHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            Log.d(TAG, "onSuccess = " + response);

            try {
                if (response.get("msgCode").equals(AppConfig.SUCCESS_CODE)) {

                    homeArticlesInfo = gson.fromJson(response.toString(), HomeArticlesInfo.class);
                    Log.d(TAG, "cityInfo.getMsgCode() = " + homeArticlesInfo.getMsgCode());

                    lastItemFlag = true;
                    requestData(response);
//            dialog.dismiss();
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
//            if(dialog != null){
//                dialog.dismiss();
//            }
            netWorkAlert = AppUtils.getAlertDialog(mContext, getString(R.string.data_result_error), erroAlertListener);
            netWorkAlert.show();
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            super.onFailure(statusCode, headers, responseString, throwable);
            Log.i(TAG, "444");
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
        barTitle.setText(R.string.new_artilces_title);
        btnPopMenu.setVisibility(View.GONE);
        rightLine.setVisibility(View.GONE);

        try {
            serverTestList = new ArrayList<ArticleListDataInfo.Result>();
            homeArticleResultList = new ArrayList<HomeArticlesInfo.Result>();

            alert = new CustomerOneBtnAlertDialog(mContext);
            loadMoreView = getLayoutInflater().inflate(R.layout.list_more, null);
            loadMoreText = (TextView) loadMoreView.findViewById(R.id.load_text);
            listView.addFooterView(loadMoreView);

            Log.i(TAG, "listView.size:" + listView.getCount());

            Log.i(TAG, "articleResultList:" + homeArticleResultList.toString());
            listAdapter = new NewArticlesListBaseAdapter(mContext, homeArticleResultList);
            Log.i(TAG, "on create listAdapter.size:" + listAdapter.getCount());
            listView.setAdapter(listAdapter);
            Log.i(TAG, "----- listAdapter.getCount():" + listAdapter.getCount());

            doUpdate(0);

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

    private void updateMessageRead() {

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
        Log.i(TAG, "===1===");
        Log.i(TAG, "requestdata");

        try {
            homeArticlesInfo = gson.fromJson(response.toString(), HomeArticlesInfo.class);
            Log.d(TAG, "cityInfo.getMsgCode() = " + homeArticlesInfo.getMsgCode());

            if (homeArticlesInfo.getMsgCode().equals(AppConfig.SUCCESS_CODE)) {

                Log.i(TAG, "homeArticlesInfo.getResult():" + homeArticlesInfo.getResult().size());
                // 因有自行新增”自行新增醫師" model，故load無資料狀態size為1
                if (homeArticlesInfo.getResult().size() != 0) {
                    listView.setVisibility(View.VISIBLE);

                    int count = listAdapter.getCount();
                    for (int i = 0; i < homeArticlesInfo.getResult().size(); i++) {
                        homeArticleResultList.add(homeArticlesInfo.getResult().get(i));
                    }

                    Log.i(TAG, "articleResultList Size:  " + homeArticleResultList.size());
                    listView.setSelection(visibleLast - visibleCount + 1);
                    listAdapter.notifyDataSetChanged();


                    //最後一筆判斷鎖住
//                if (homeArticleResultList.size() == homeArticlesInfo.getTotalCount()){
//                    Log.i(TAG, "-----last chose");
//                    loadMoreText.setVisibility(View.GONE);
//                    lastItemFlag = false;
//                    return;
//
//                }

                    if (homeArticlesInfo.getResult().size() < AppConfig.SHOW_CONTENT_NUMBER) {
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

//              loadMoreText.setText("");
//              listAdapter.notifyDataSetChanged();


                } else {
                    lastItemFlag = false;
                    loadMoreText.setText("");
                    loadMoreText.setVisibility(View.GONE);

//                listView.setVisibility(View.GONE);
//                dialog.dismiss();
//                AppUtils.getAlertDialog(mContext, getString(R.string.no_data_info)).show();
                }

            } else if (homeArticlesInfo.getMsgCode().substring(0, 1).equals(AppConfig.ERROR_CODE)) {
                CustomerOneBtnAlertDialog dialog = new CustomerOneBtnAlertDialog(mContext);
                dialog.setMsg(homeArticlesInfo.getStatus()).show();
                Log.e(TAG, homeArticlesInfo.getStatus());
            }
        }
        catch (Exception e)
        {
            netWorkAlert.setMsg(getString(R.string.get_data_error))
                    .setPositiveBtnText(getString(R.string.sure))
                    .setPositiveListener(erroAlertListener)
                    .show();
        }
    }


    // doupdate new data , post to server
    private void doUpdate(int start) {

        loadMoreText.setVisibility(View.VISIBLE);


        final String url = AppConfig.DOMAIN_SITE_PATE + AppConfig.REQUEST_HOMENEWS;
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


    AdapterView.OnItemClickListener listViewItemListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            if (homeArticleResultList != null) {
//                        Intent intent = new Intent(NewArticlesListActivity.this, HealthArticleDetailForFinder.class);
//                        intent.putExtras(bundle);
//                        startActivity(intent);
                try {
//            Log.i(TAG, "article type:" + homeArticleResultList.get(position).getArticletype());
                    clickMsgId = homeArticleResultList.get(position).getMessageId();
                    refId = homeArticleResultList.get(position).getId();
                    articleType = homeArticleResultList.get(position).getArticletype();

                    Log.i(TAG, "cliclkMsgId:"+clickMsgId +"/refId:"+refId+"/articleType:"+articleType);
                    updateMessageRead();
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

    private View.OnClickListener notFinishErroAlertListener = new View.OnClickListener() {
        public void onClick(View v) {
            Log.i(TAG, " on error Litener click");
            if (netWorkAlert != null) {
                netWorkAlert.dismiss();
            }

        }

    };

    // 清除listview內容
    private void clearListData() {
        if (homeArticlesInfo != null) {
            homeArticlesInfo = null;
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

        if (netWorkAlert != null) {
            netWorkAlert.dismiss();
        }

//        homeArticleResultList.clear();

//        if (dialog != null) {
//            dialog.dismiss();
//        }

    }
}
