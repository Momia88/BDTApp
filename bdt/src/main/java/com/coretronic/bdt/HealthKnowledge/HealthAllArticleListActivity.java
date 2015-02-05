package com.coretronic.bdt.HealthKnowledge;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.coretronic.bdt.Adapter.HealthKnowledgeListBaseAdapter;
import com.coretronic.bdt.AppConfig;
import com.coretronic.bdt.AppUtils;
import com.coretronic.bdt.DataModule.ArticleListDataInfo;
import com.coretronic.bdt.R;
import com.coretronic.bdt.module.HealthKnowledgePopupWindow;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by james on 2014/9/12.
 */
public class HealthAllArticleListActivity extends Activity {

    private String TAG = HealthAllArticleListActivity.class.getSimpleName();

    private Button btnBack = null;
    private PopupWindow articlePopupWindow;
    private Context mContext;
    private ProgressDialog dialog = null;

    private RelativeLayout navigationBar;
    private Button btnPopMenu = null;
    private View loadMoreView;
    private int visibleCount;
    private TextView loadMoreText;
    private TextView noDataTV;
    private RequestParams params = null;
    private AsyncHttpClient client = null;

    private int visibleLast = 0;
    private ListView listView = null;
    private Gson gson = new Gson();
    private List<ArticleListDataInfo.Result> serverTestList = null;
    private List<ArticleListDataInfo.Result> articleResultList = null;
    private List<ArticleListDataInfo.Result> tempArticleResultList = null;
    private ArticleListDataInfo articleListDataInfo = null;
    private HealthKnowledgeListBaseAdapter listAdapter = null;
    private String requestApiName = "";
    private Boolean scrollFlag = true;
    private String UUIDChose;
    private String all, favor, watched, stateChange;
    private String stateFlag = all;

    //transactionLog
    private String currentTime;
    private String append;
    private String uuidChose;
    private SharedPreferences sharedPreferences;


    public JsonHttpResponseHandler renewDataJsonPostHandler = new JsonHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            Log.i(TAG, "onSuccess = " + response);

            articleListDataInfo = gson.fromJson(response.toString(), ArticleListDataInfo.class);
            scrollFlag = true;
            requestData();
            dialog.dismiss();

        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            Log.i(TAG, "onFailure");
            if(dialog != null){
                dialog.dismiss();
            }
            AppUtils.getAlertDialog(mContext, getString(R.string.data_load_error)).show();

        }
        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            super.onFailure(statusCode, headers, responseString, throwable);
            Log.i(TAG, "444");
            Log.i(TAG, "responseString:" + responseString);
            if (dialog !=null) {
                dialog.dismiss();
            }
        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.health_knowldge_list);

        mContext = this;
        articlePopupWindow = new HealthKnowledgePopupWindow(mContext, null);

        changeBarTitle();

        serverTestList = new ArrayList<ArticleListDataInfo.Result>();
        articleResultList = new ArrayList<ArticleListDataInfo.Result>();
        tempArticleResultList = new ArrayList<ArticleListDataInfo.Result>();

        sharedPreferences = getSharedPreferences(AppConfig.SHAREDPREFERENCES_NAME,0);
        uuidChose =sharedPreferences.getString(AppConfig.PREF_UNIQUE_ID,"");

        initView();
        btnPopMenu.setText(R.string.find_article_menu_title);


        dialog = AppUtils.customProgressDialog(mContext, null, getString(R.string.dialog_read_msg));
//        dialog.setCancelable(false);
        dialog.show();

        loadMoreView = getLayoutInflater().inflate(R.layout.list_more, null);
        loadMoreText = (TextView) loadMoreView.findViewById(R.id.load_text);
        listView.addFooterView(loadMoreView);

        listAdapter = new HealthKnowledgeListBaseAdapter(mContext, articleResultList);
        listView.setAdapter(listAdapter);

        doUpdate(0);
        // scroll listener
        listView.setOnScrollListener(scrollListener);
    }


    private void requestData() {
        Log.i(TAG, "===1===");
//        articleListDataInfo = gson.fromJson(AppTestResponse.healthArticleResponse, ArticleListDataInfo.class);

        // Log.i(TAG, "aaa" + articleListDataInfo.getResult().size());

        if (articleListDataInfo.getResult()!=null){

            articleResultList.addAll(articleListDataInfo.getResult());
            Log.i(TAG, "articleResultList.size():" + articleResultList.size());
            loadMoreText.setVisibility(View.VISIBLE);
            loadMoreText.setText("");

        }else{
            Log.i(TAG, "-----last chose-----");
            loadMoreText.setVisibility(View.GONE);
            scrollFlag = false;
        }

        if (articleListDataInfo.getResult().size() < AppConfig.SHOW_CONTENT_NUMBER) {
            Log.i(TAG, "-----last chose-----");
            loadMoreText.setVisibility(View.GONE);
            scrollFlag = false;
        }



        if (articleListDataInfo.getMsgCode().equals(AppConfig.SUCCESS_CODE)) {



//            if (articleListDataInfo.getResult()!=null){
//                if (articleListDataInfo.getResult().size() < AppConfig.SHOW_CONTENT_NUMBER){
//                    Log.i(TAG, "-----last chose-----");
//                    loadMoreText.setVisibility(View.GONE);
//                    scrollFlag = false;
//                }
//            }else{
//                loadMoreText.setVisibility(View.GONE);
//                scrollFlag = false;
//            }



//            Log.i(TAG, "size:  " + articleListDataInfo.getResult().size());
//            tempArticleResultList.clear();
//
//            //list load無資料
//            if (articleListDataInfo.getResult().size() == 0) {
//                scrollFlag = false;
//                loadMoreText.setText("");
//                Log.i(TAG, "====list load無資料====");
//                return;
//            }
//
//            int count = listAdapter.getCount();
//            for (int i = count; i < count + AppConfig.SHOW_CONTENT_NUMBER; i++) {
////                adapter.addItem(String.valueOf(i));
//                if (i >= articleListDataInfo.getResult().size()) {
//                    loadMoreText.setText("");
//                    Log.i(TAG, "==i==: " + i);
//                    break;
//                }
//                Log.i(TAG, "==Count==: " + count);
//                tempArticleResultList.add(articleResultList.get(i));
////            listAdapter.addItem(serverTestList.get(i));
//            }
//
//
//            Log.i(TAG, "count:" + count);
//            articleResultList.addAll(tempArticleResultList);


//            listView.setSelection(visibleLast - visibleCount + 1);

            listAdapter.notifyDataSetChanged();

//            //設置定位點
//            if ((visibleLast - visibleCount) < 0) {
//                listView.setSelection(0);
//            } else {
//                listView.setSelection(visibleLast - visibleCount+1);
//            }

//            Log.i(TAG,"visibleLast - visibleCount:"+(visibleLast - visibleCount));
//            if ((visibleLast - visibleCount) < 0) {
//                listView.setSelection(0);
//
//            } else {
//                listView.setSelection(visibleLast - visibleCount+1);
//            }

//            if(articleResultList.size() < visibleLast )
//            {
//                listView.removeFooterView(loadMoreView);
//            }
//            else
//            {
//                listView.addFooterView(loadMoreView);
//            }

            loadMoreText.setText("");
//            listAdapter.notifyDataSetChanged();

            // 設定沒有資料狀況下文字
            if ((articleResultList.size() == 0) && (requestApiName.equals(AppConfig.REQUEST_FAVORITES_ARTICLES))) {
                scrollFlag = false;
                Log.i(TAG,"requestApiName:"+requestApiName+ " /AppConfig.REQUEST_FAVOR_ARTICLE:"+AppConfig.REQUEST_FAVORITES_ARTICLES);
                noDataTV.setText(R.string.no_favor);

                noDataTV.setVisibility(View.VISIBLE);
                listView.setVisibility(View.GONE);

                return;
            } else if ((articleResultList.size() == 0 )&& (requestApiName.equals(AppConfig.REQUEST_WATCHED_ARTICLES))) {
                scrollFlag = false;
                Log.i(TAG,"requestApiName:"+requestApiName+ " /AppConfig.REQUEST_FAVOR_ARTICLE:"+AppConfig.REQUEST_WATCHED_ARTICLES);
                noDataTV.setText(R.string.no_reviewed);

                noDataTV.setVisibility(View.VISIBLE);
                listView.setVisibility(View.GONE);

                return;
            }
            noDataTV.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);



            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    view.setSelected(true);
                    Log.i(TAG, "click positioin:" + position);

                    callSystemTime();
                    append = currentTime + "," +
                            uuidChose + "," +
                            "HealthAllArticleListActivity" + "," +
                            articleResultList.get(position).getNewsId() + "," +
                            "btnNewsItem" + "\n";
                    transactionLogSave(append);

//                    if (position < visibleLast) {
                    Log.i(TAG, "articleResultList.get(position).getNewsId():" + articleResultList.get(position).getNewsId());
                    Bundle bundle = new Bundle();
                    bundle.putString(AppConfig.NEWS_ID_KEY, articleResultList.get(position).getNewsId());
                    bundle.putString(AppConfig.NEWS_STATUS, articleResultList.get(position).getPopular());
                    bundle.putString("stateChange", stateFlag);
                    Intent intent = new Intent(HealthAllArticleListActivity.this, HealthArticleDetailActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
//                    }
                }
            });

            dialog.dismiss();

        } else {
//                AppUtils.getAlertDialog(mContext, divisionInfo.getStatus());
            dialog.dismiss();

//                AppUtils.getAlertDialog(mContext,getString(R.string.data_load_error));
//                Log.e(TAG, divisionInfo.getStatus());
        }
    }


    // doupdate new data , post to server
    private void doUpdate(int start) {
        final String url = AppConfig.DOMAIN_SITE_PATE + requestApiName;
        params = new RequestParams();
        JSONArray jsonArray = new JSONArray();

        UUIDChose =sharedPreferences.getString(AppConfig.PREF_UNIQUE_ID,"");

//        params.add("uid", "ae841e15-6f41-4945-9f6e-f0bfd0771664");
        params.add("uid", UUIDChose);
        params.add("request", "health_news");
        params.add("time", AppUtils.getChineseSystemTime());
        params.add("start", start + "");
        params.add("num", AppConfig.SHOW_CONTENT_NUMBER + "");
        //將參數包成一個array
        jsonArray.put(params);
        Log.i(TAG, "PARAMS:  " + params);
        loadMoreText.setText(getString(R.string.doupdata));
        scrollFlag = false;
        client = new AsyncHttpClient();
        client.setTimeout(AppConfig.TIMEOUT);
        client.post(url, params, renewDataJsonPostHandler);
        params = null;
        client = null;

    }


    private View.OnClickListener navBarBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btnBack:
                    callSystemTime();
                    append = currentTime + "," +
                            uuidChose + "," +
                            "HealthAllArticleListActivity" + "," +
                            "MainActivity" + "," +
                            "btnBack" + "\n";
                    transactionLogSave(append);
                    finish();
                    break;
                case R.id.btnPopMenu:
                    Log.d(TAG, "btnPopMenu");
                    callSystemTime();
                    append = currentTime + "," +
                            uuidChose + "," +
                            "HealthAllArticleListActivity" + "," +
                            "HealthKnowledgePopupWindow" + "," +
                            "btnPopMenu" + "\n";
                    transactionLogSave(append);
                    articlePopupWindow.showAsDropDown(navigationBar, 0, 0);
                    break;
            }
        }
    };

    AbsListView.OnScrollListener scrollListener = new AbsListView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            Log.i(TAG, "onScrollStateChanged");
            Log.i(TAG, "scrollFlag:"+scrollFlag);
            if( scrollFlag == false) {
                return;
            }

            int itemsLastIndex = listAdapter.getCount() - 1;
            int lastIndex = itemsLastIndex + 1;
            Log.i(TAG, "itemsLastIndex:" + itemsLastIndex);
            Log.i(TAG, "lastIndex:" + lastIndex);
            if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && visibleLast == lastIndex) {
                doUpdate(lastIndex);
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            Log.i(TAG, "onScroll");

            visibleCount = visibleItemCount;
            visibleLast = firstVisibleItem + visibleItemCount - 1;
            Log.i(TAG, "visibleCount:" + visibleCount);
            Log.i(TAG, "visibleLast:" + visibleLast);

        }
    };


    private void initView() {
        navigationBar = (RelativeLayout) findViewById(R.id.navigationBar_option);
        btnPopMenu = (Button) findViewById(R.id.btnPopMenu);
        btnBack = (Button) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(navBarBtnListener);
        btnPopMenu.setOnClickListener(navBarBtnListener);
        listView = (ListView) findViewById(R.id.listView);
        noDataTV = (TextView) findViewById(R.id.no_datatv);
    }


    @Override
    protected void onNewIntent(Intent intent) {
        Log.i(TAG, "999===onNewIntent===");
        super.onNewIntent(intent);
        setIntent(intent);

        // clear List
        clearData();

        changeBarTitle();
        doUpdate(0);

    }

    private void changeBarTitle() {

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        requestApiName = bundle.getString(AppConfig.ARTICLE_BUNDLE_KEY);
        stateFlag = bundle.getString("stateChange");

        Log.i(TAG, "change requestApiName:" + requestApiName);
        Log.i(TAG, "stateFlag:  " + stateFlag);
        if(requestApiName == null){
            ((TextView) findViewById(R.id.action_bar_title)).setText(getString(R.string.health_knowledge));
            return;
        }
        if (requestApiName.equals(AppConfig.REQUEST_NEWS)) {
            ((TextView) findViewById(R.id.action_bar_title)).setText(getString(R.string.health_knowledge));
        }

        if (requestApiName.equals(AppConfig.REQUEST_FAVORITES_ARTICLES)) {
            ((TextView) findViewById(R.id.action_bar_title)).setText(getString(R.string.favor_article));
        }

        if (requestApiName.equals(AppConfig.REQUEST_WATCHED_ARTICLES)) {
            ((TextView) findViewById(R.id.action_bar_title)).setText(getString(R.string.reviewed));
        }
    }

    private void clearData() {
//        int size = articleResultList.size();
//        Log.i(TAG, "===remove===");
//        if (size > 0) {
//            articleResultList.removeAll(articleResultList);
//            listAdapter.notifyDataSetChanged();
//        }
//
//        if (articleListDataInfo != null) {
//            articleListDataInfo = null;
//        }

        if (articleResultList != null) {
            articleResultList.clear();
            Log.i(TAG, "after clear articleResultList.size(): " + articleResultList.size());
            listAdapter.notifyDataSetChanged();
        }

//
//        if (tempArticleResultList != null) {
//            tempArticleResultList.clear();
//        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (articlePopupWindow != null) {
            articlePopupWindow.dismiss();
        }

        if (dialog != null) {
            dialog.dismiss();
        }
    }

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