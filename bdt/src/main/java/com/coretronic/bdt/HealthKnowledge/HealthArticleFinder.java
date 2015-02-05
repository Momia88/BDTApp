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


public class HealthArticleFinder extends Activity {
    final private String url = AppConfig.DOMAIN_SITE_PATE + AppConfig.REQUEST_SEARCH_RESULT;
    public Button button_next;
    private PopupWindow menuPopupWindow;
    private EditText keywordET = null;
    private LinearLayout contentLL = null;
    private TextView barTitle;
    private Button btnPopMenu;
    private Button btnBack;
    private TextView matchData;
    private Context mContext;
    private RelativeLayout navigationBar;
    private String TAG = HealthArticleFinder.class.getSimpleName();

    private ListView listView = null;
    private ProgressDialog dialog = null;
    private RequestParams params = null;
    private AsyncHttpClient client = null;
    private List<ArticleListDataInfo.Result> serverTestList = null;
    private List<ArticleListDataInfo.Result> articleResultList = null;
    private List<ArticleListDataInfo.Result> tempArticleResultList = null;
    private ArticleListDataInfo articleListDataInfo = null;
    private HealthKnowledgeListBaseAdapter listAdapter = null;
    private Gson gson = new Gson();
    private int visibleLast = 0;
    private View loadMoreView;
    private int visibleCount;
    private TextView loadMoreText;
    private String num;
    private boolean lastItemFlag = true;

    //transactionLog
    private String currentTime;
    private String append;
    private String uuidChose;
    private SharedPreferences sharedPreferences;


//    SimpleDateFormat sdf = new SimpleDateFormat("mm:ss:MM");
//    Date dt1 = null;
//    Date dt2 = null;

    public JsonHttpResponseHandler jsonHandler = new JsonHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            Log.d(TAG, "onSuccess = " + response);

//            dt2 = new Date();
//            Log.i(TAG, "time:" + sdf.format(dt2));
//            Long ut1 = dt1.getTime();
//            Long ut2 = dt2.getTime();
//            Long timeP = ut2 - ut1;
//            Log.i(TAG, "timeP:" + timeP);

            articleListDataInfo = gson.fromJson(response.toString(), ArticleListDataInfo.class);
            Log.d(TAG, "cityInfo.getMsgCode() = " + articleListDataInfo.getMsgCode());

            lastItemFlag = true;
            requestData(response);
            dialog.dismiss();
//            setArticleList(response);

//            if (articleListDataInfo.getMsgCode().equals(AppConfig.SUCCESS_CODE)) {
//                dialog.dismiss();
//
//            } else if (articleListDataInfo.getMsgCode().substring(0, 1).equals(AppConfig.ERROR_CODE)) {
////                AppUtils.getAlertDialog(mContext, articleListDataInfo.getStatus()).show();
//                dialog.dismiss();
//
//                AppUtils.getAlertDialog(mContext,getString(R.string.data_load_error)).show();
//                Log.e(TAG, articleListDataInfo.getStatus());
//            }

        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            Log.d(TAG, "onFailure");
            if(dialog != null){
                dialog.dismiss();
            }
            AppUtils.getAlertDialog(mContext, getString(R.string.data_load_error)).show();
        }


    };

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.health_knowledge_article_finder);

        mContext = this;
        sharedPreferences = getSharedPreferences(AppConfig.SHAREDPREFERENCES_NAME, 0);
        uuidChose =sharedPreferences.getString(AppConfig.PREF_UNIQUE_ID,"");

        initView();
        barTitle.setText(R.string.find_article_keyWord);
        btnPopMenu.setText(R.string.find_article_menu_title);
        menuPopupWindow = new HealthKnowledgePopupWindow(mContext, null);


        serverTestList = new ArrayList<ArticleListDataInfo.Result>();
        articleResultList = new ArrayList<ArticleListDataInfo.Result>();
        tempArticleResultList = new ArrayList<ArticleListDataInfo.Result>();


        loadMoreView = getLayoutInflater().inflate(R.layout.list_more, null);
        loadMoreText = (TextView) loadMoreView.findViewById(R.id.load_text);
        listView.addFooterView(loadMoreView);

        Log.i(TAG,"listView.size:"+listView.getCount());

        Log.i(TAG,"articleResultList:"+articleResultList.toString());
        listAdapter = new HealthKnowledgeListBaseAdapter(mContext, articleResultList);
        Log.i(TAG,"on create listAdapter.size:"+listAdapter.getCount());
        listView.setAdapter(listAdapter);
        Log.i(TAG, "----- listAdapter.getCount():" + listAdapter.getCount());

//        //===== server test
//        articleListDataInfo = gson.fromJson(AppTestResponse.healthArticleResponse, ArticleListDataInfo.class);
//
//
//        for (int i = 0; i < 10; i++) {
//            serverTestList.addAll(articleListDataInfo.getResult());
//        }
//        Log.i(TAG, "serverTestList.size:" + serverTestList.size());
//        //=====

        // scroll listener
        listView.setOnScrollListener(scrollListener);


    }


    private void initView() {
        // navigation bar
        navigationBar = (RelativeLayout) findViewById(R.id.navigationBar_option);
        btnBack = (Button) findViewById(R.id.btnBack);
        btnPopMenu = (Button) findViewById(R.id.btnPopMenu);
        btnBack.setOnClickListener(btnListener);
        btnPopMenu.setOnClickListener(btnListener);
        button_next = (Button) findViewById(R.id.button_next);
        button_next.setOnClickListener(queryClickListener);
        keywordET = (EditText) findViewById(R.id.keywordET);
        barTitle = (TextView) findViewById(R.id.action_bar_title);
        listView = (ListView) findViewById(R.id.finder_listView);
        matchData = (TextView) findViewById(R.id.match_data);
        contentLL = (LinearLayout) findViewById(R.id.contentLL);
    }

    //Button監聽
    private View.OnClickListener queryClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            callSystemTime();
            append = currentTime + "," +
                    uuidChose + "," +
                    "HealthArticleFinder" + "," +
                    "NewsListView" + "," +
                    "btnFindNews" + "\n";
            transactionLogSave(append);
            articleResultList.clear();
            doUpdate(0);
        }
    };

    private void requestData(JSONObject response) {
        Log.i(TAG, "===1===");
        Log.i(TAG,"requestdata");
        articleListDataInfo = gson.fromJson(response.toString(), ArticleListDataInfo.class);
        Log.d(TAG, "cityInfo.getMsgCode() = " + articleListDataInfo.getMsgCode());

        if (articleListDataInfo.getMsgCode().equals(AppConfig.SUCCESS_CODE)) {

            Log.i(TAG, "size:" + articleListDataInfo.getResult().size());

            // 因有自行新增”自行新增醫師" model，故load無資料狀態size為1
            if (articleListDataInfo.getResult().size() != 0) {
                listView.setVisibility(View.VISIBLE);
                // 有內容則show list
                showListViewContent();


//                tempArticleResultList.clear();
                int count = listAdapter.getCount();
                Log.i(TAG, "listview.getCount():" + listView.getCount());
                Log.i(TAG, "listAdapter.getCount():" + count);
                for (int i = count; i < count + AppConfig.SHOW_CONTENT_NUMBER; i++) {
//                adapter.addItem(String.valueOf(i));
                    if (i >= articleListDataInfo.getTotalCount()) {
                        loadMoreText.setText("");
                        Log.i(TAG, "==i==: " + i);
                        break;
                    }
                    Log.i(TAG, "==Count==: " + count);
                    articleResultList.add(articleListDataInfo.getResult().get(i-count));
//                  listAdapter.addItem(serverTestList.get(i));
                }

                Log.i(TAG, "articleResultList Size:  " + articleResultList.size());

//                Log.i(TAG, "count:" + count + "/articleResultList:"+articleResultList.toString());
//                articleResultList.addAll(tempArticleResultList);
                listAdapter.notifyDataSetChanged();

//                if (articleListDataInfo.getResult()!=null){
//
//                    articleResultList.addAll(articleListDataInfo.getResult());
//                    Log.i(TAG, "articleResultList.size():" + articleResultList.size());
//
//                }else{
//                    Log.i(TAG, "-----last chose-----");
//                    loadMoreText.setVisibility(View.GONE);
//                    lastItemFlag = false;
//                }

//                if (articleListDataInfo.getResult() == null) {
//                    Log.i(TAG, "-----last chose = 0-----");
//                    loadMoreText.setVisibility(View.GONE);
//                    lastItemFlag = false;
//                    return;
//
//                }

                //最後一筆判斷鎖住
                if (articleResultList.size() == articleListDataInfo.getTotalCount()){
                    Log.i(TAG, "-----last chose");
                    loadMoreText.setVisibility(View.GONE);
                    lastItemFlag = false;
                    return;

                }

                if (articleListDataInfo.getResult().size() < AppConfig.SHOW_CONTENT_NUMBER){
                    Log.i(TAG, "-----last chose < 10-----");
                    loadMoreText.setVisibility(View.GONE);
                    lastItemFlag = false;
                    return;
                }

//                //設置定位點
//                if ((visibleLast - visibleCount) < 0) {
//                    listView.setSelection(0);
//                } else {
//                    listView.setSelection(visibleLast - visibleCount);
//                }
//                Log.i(TAG, "articleResultList size:" + articleResultList.size() + "/articleResultList:"+articleResultList.toString());
                loadMoreText.setText("");
                listView.setVisibility(View.VISIBLE);

//              loadMoreText.setText("");
//              listAdapter.notifyDataSetChanged();

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        view.setSelected(true);

                        callSystemTime();
                        append = currentTime + "," +
                                uuidChose + "," +
                                "HealthArticleFinder" + "," +
                                articleResultList.get(position).getNewsId() + "," +
                                "btnNewsItem" + "\n";
                        transactionLogSave(append);

                        Log.i(TAG, articleResultList.get(position).getNewsId());
                        Bundle bundle = new Bundle();
                        bundle.putString(AppConfig.NEWS_ID_KEY, articleResultList.get(position).getNewsId());
                        bundle.putString(AppConfig.NEWS_STATUS, articleResultList.get(position).getPopular());
                        Intent intent = new Intent(HealthArticleFinder.this, HealthArticleDetailForFinder.class);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                });
//                dialog.dismiss();

            } else {
                lastItemFlag = false;
                contentLL.setVisibility(View.VISIBLE);
                listView.setVisibility(View.GONE);
                matchData.setText(getString(R.string.match_data) + (articleListDataInfo.getTotalCount()) + getString(R.string.match_data2));
//                dialog.dismiss();
                AppUtils.getAlertDialog(mContext, getString(R.string.no_data_info)).show();
            }

        } else if (articleListDataInfo.getMsgCode().substring(0, 1).equals(AppConfig.ERROR_CODE)) {

//            clearListData();
//            dialog.dismiss();
            AppUtils.getAlertDialog(mContext, articleListDataInfo.getStatus()).show();

//                AppUtils.getAlertDialog(mContext,getString(R.string.data_load_error));
            Log.e(TAG, articleListDataInfo.getStatus());
        }
    }


    // doupdate new data , post to server
    private void doUpdate(int start) {
        final String url = AppConfig.DOMAIN_SITE_PATE + AppConfig.REQUEST_SEARCH_RESULT;
        params = new RequestParams();
        JSONArray jsonArray = new JSONArray();
        dialog = AppUtils.customProgressDialog(mContext, null, getString(R.string.dialog_read_msg));
        dialog.show();


        params.add("uid", sharedPreferences.getString(AppConfig.PREF_UNIQUE_ID, ""));
//        params.add("uid", "ae841e15-6f41-4945-9f6e-f0bfd0771664");
        params.add("article_keyword", keywordET.getText()+"");
        params.add("start", start + "");
        params.add("num", AppConfig.SHOW_CONTENT_NUMBER + "");
        params.add("time", AppUtils.getChineseSystemTime());

        //將參數包成一個array
        jsonArray.put(params);
        Log.i(TAG, "PARAMS:  " + params);
        loadMoreText.setText(getString(R.string.doupdata));

        client = new AsyncHttpClient();
        client.setTimeout(AppConfig.TIMEOUT);
        lastItemFlag = false;
        client.post(url, params, jsonHandler);
        params = null;
        client = null;
    }


    AbsListView.OnScrollListener scrollListener = new AbsListView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            Log.i(TAG, "onScrollStateChanged");
            if( lastItemFlag == false) {
                return;
            }
            int itemsLastIndex = listAdapter.getCount() - 1;
            // (fixed) footer index
            int lastIndex = itemsLastIndex + 1;
            Log.i(TAG, "itemsLastIndex:" + itemsLastIndex);
            Log.i(TAG, "lastIndex:" + lastIndex);
            // lastIndex:visible last index(include footer)
            if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE &&
                    lastIndex == visibleLast) {
                Log.i(TAG, "scrollListener doUpdate");

                // doUpdate
                // lastIndex+1 為筆數
                doUpdate(lastIndex);
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            Log.i(TAG, "onScroll");

            visibleCount = visibleItemCount;
            //
            visibleLast = firstVisibleItem + visibleItemCount - 1;
            Log.i(TAG, "visibleCount:" + visibleCount);
            Log.i(TAG, "visibleLast:" + visibleLast);
        }
    };


    private View.OnClickListener btnListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btnBack:
                    callSystemTime();
                    append = currentTime + "," +
                            uuidChose + "," +
                            "HealthArticleFinder" + "," +
                            "HealthAllArticleListActivity" + "," +
                            "btnBack" + "\n";
                    transactionLogSave(append);
                    finish();
                    break;
                case R.id.btnPopMenu:
                    Log.d(TAG, "btnPopMenu");
                    callSystemTime();
                    append = currentTime + "," +
                            uuidChose + "," +
                            "HealthArticleFinder" + "," +
                            "HealthKnowledgePopupWindow" + "," +
                            "btnPopMenu" + "\n";
                    transactionLogSave(append);
                    menuPopupWindow.showAsDropDown(navigationBar, 0, 0);
                    break;
            }
        }
    };


    // 呈現listview內容
    private void showListViewContent() {
        contentLL.setVisibility(View.VISIBLE);

        matchData.setText(getString(R.string.match_data) + (articleListDataInfo.getTotalCount()) + getString(R.string.match_data2));
//        listAdapter.notifyDataSetChanged();
    }


    // 清除listview內容
    private void clearListData() {
        if (articleResultList != null) {
//            articleResultList.clear();
            listAdapter.notifyDataSetChanged();
        }

        // no內容則show list
//        contentLL.setVisibility(View.GONE);
//
//        if (listAdapter != null) {
//            listAdapter = null;
//        }
//        if (params != null) {
//            params = null;
//        }
//
//        if (client != null) {
//            client = null;
//        }
//
//
//        if (loadMoreView != null) {
//            loadMoreView = null;
//        }
//
//        if (loadMoreText != null) {
//            loadMoreText = null;
//        }
//
//        keywordET.setText("");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause");
        if (menuPopupWindow != null) {
            menuPopupWindow.dismiss();
        }

        if (dialog != null) {
            dialog.dismiss();
        }

//        clearListData();

    }

    @Override
    protected void onResume() {
        super.onResume();
        clearListData();
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