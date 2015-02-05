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
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.coretronic.bdt.AppConfig;
import com.coretronic.bdt.AppUtils;
import com.coretronic.bdt.MessageWall.Module.GoodInfo;
import com.coretronic.bdt.MessageWall.Module.Goods;
import com.coretronic.bdt.MessageWall.Module.WallMessageDetail;
import com.coretronic.bdt.NewArticlesListActivity;
import com.coretronic.bdt.NewMessageListActivity;
import com.coretronic.bdt.Person.NotifyListActivity;
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
import java.util.Date;
import java.util.List;

/**
 * Created by Morris on 14/11/12.
 */
public class WallMsgDetail extends Fragment {
    private String TAG = WallMsgDetail.class.getSimpleName();
    private Context mContext;
    private Button btnBack;
    private TextView barTitle;
    private RelativeLayout navigationBar;
    private SharedPreferences sharedPreferences;
    private ProgressDialog progressDialog = null;
    private String uuidChose;
    private AsyncHttpClient asyncHttpClient;
    private Gson gson = new Gson();
    //view
    private ImageView mImgUserPic;
    private TextView mTxtUserName;
    private TextView mTxtMsgTime;
    private TextView mTxtMsgShare;
    private LinearLayout mImgListView;
    private TextView mTxtGoodFriends;
    private ImageView mBtnGoodAdd;
    // get bundle data
    private String refId = "";
    private String articleType = "";
    private String sourceFrom = "";
    private String userName = "";
    private String userThumb = "";
    // result obj
    private WallMessageDetail wallMessageDetail;
    private FrameLayout frameMsgLayout;
    private int isGood = -1;
    private CustomerOneBtnAlertDialog oneBtnAlertDialog;
    //transactionLog
    private String currentTime;
    private String append;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");

        sharedPreferences = getActivity().getSharedPreferences(AppConfig.SHAREDPREFERENCES_NAME, 0);
        asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.setTimeout(AppConfig.TIMEOUT);
        uuidChose = sharedPreferences.getString(AppConfig.PREF_UNIQUE_ID, "");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");
        View v = inflater.inflate(R.layout.wall_detail_msg_layout, container, false);
        mContext = v.getContext();
        progressDialog = new ProgressDialog(mContext);
        initView(v);
        Bundle bundle = getArguments();
        if (bundle != null) {
            Log.i(TAG, "bundle: " + bundle);
            refId = bundle.getString("refId");
            articleType = bundle.getString("articleType");
            sourceFrom = bundle.getString("sourceFrom");
            userName = bundle.getString("userName");
            userThumb = bundle.getString("userThumb");
        }
        getWallMsgDetail(0);
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
        frameMsgLayout.setVisibility(View.VISIBLE);
    }

    private void initView(View v) {
        // navigation bar
        navigationBar = (RelativeLayout) v.findViewById(R.id.navigationBar_option);
        btnBack = (Button) getActivity().findViewById(R.id.btnBack);
        barTitle = (TextView) getActivity().findViewById(R.id.action_bar_title);

        barTitle.setText(getString(R.string.pp_wall));
        btnBack.setOnClickListener(btnListener);

        frameMsgLayout = (FrameLayout) getActivity().findViewById(R.id.frame_msg_container);
        mImgUserPic = (ImageView) v.findViewById(R.id.imgUserPic);
        mTxtUserName = (TextView) v.findViewById(R.id.txtUserName);
        mTxtMsgTime = (TextView) v.findViewById(R.id.txtMsgTime);
        mTxtMsgShare = (TextView) v.findViewById(R.id.txtMsgShare);
        mImgListView = (LinearLayout) v.findViewById(R.id.imgListView);
        mTxtGoodFriends = (TextView) v.findViewById(R.id.txtGoodFriends);
        mBtnGoodAdd = (ImageView) v.findViewById(R.id.btnGoodAdd);

        mBtnGoodAdd.setOnClickListener(btnListener);
        mTxtGoodFriends.setOnClickListener(btnListener);
    }

    private void setArticleInfo() {
        isGood = Integer.valueOf(wallMessageDetail.getResult().getIsGood());
        if (isGood == 0) {
            mBtnGoodAdd.setImageResource(R.drawable.ic_good_article);
        } else if (isGood == 1) {
            mBtnGoodAdd.setImageResource(R.drawable.ic_good_article_press);
        }
        userThumb = wallMessageDetail.getResult().getThumb();
        if (userThumb != null) {
            mImgUserPic.setImageBitmap(AppUtils.base64ToBitmap(userThumb));
        }
        mTxtUserName.setText(wallMessageDetail.getResult().getUserName());
        mTxtMsgTime.setText(wallMessageDetail.getResult().getDate());
        mTxtMsgShare.setText(wallMessageDetail.getResult().getComment());
        String str = getGoodStr(wallMessageDetail.getResult().getTotalGoodNum(), wallMessageDetail.getResult().getGood());
        mTxtGoodFriends.setText(str);
        int imgSize = wallMessageDetail.getResult().getPicUrl().size();
        for (int i = 0; i < imgSize; i++) {
            addImage(wallMessageDetail.getResult().getPicUrl().get(i));
        }
    }

    View.OnClickListener btnListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btnBack:
                    callSystemTime();
                    append = currentTime + "," +
                            uuidChose + "," +
                            "WallMsgDetail" + "," +
                            "WallMsgActivity" + "," +
                            "btnBack" + "\n";
                    transactionLogSave(append);
                    backEvent();
//                    getActivity().finish();
                    break;

                case R.id.btnGoodAdd:
                    callSystemTime();
                    append = currentTime + "," +
                            uuidChose + "," +
                            "WallMsgDetail" + "," +
                            "WallMsgDetailGoodText" + "," +
                            "btnGoodText" + "\n";
                    transactionLogSave(append);
                    Log.d(TAG, "isGood:" + isGood);
                    if (isGood == -1) {
                        return;
                    }
                    if (isGood == 0) {
                        setGoodAdd();
                        mBtnGoodAdd.setImageResource(R.drawable.ic_good_article_press);
                    } else if (isGood == 1) {
                        setGoodRemove();
                        mBtnGoodAdd.setImageResource(R.drawable.ic_good_article);
                    }
                    break;
                case R.id.txtGoodFriends:
                    callSystemTime();
                    append = currentTime + "," +
                            uuidChose + "," +
                            "WallMsgDetail" + "," +
                            "WallMsgDetailTxtGoodFriends" + "," +
                            "btnTxtGoodFriends" + "\n";
                    transactionLogSave(append);
                    // invisibility message list
                    Bundle bundle = new Bundle();
                    bundle.putString("refId", refId);
                    bundle.putString("articleType", articleType);
                    Intent i = new Intent();
                    i.putExtras(bundle);
                    i.setClass(getActivity(), WallGoodListActivity.class);
                    startActivity(i);
                    break;
            }
        }
    };


    private void backEvent() {
        if (!(sourceFrom.equals(""))) {
            Intent intent = new Intent();
            // back to main New message list activity
            if (sourceFrom.equals("NewArticlesListActivity")) {
                intent.setClass(getActivity(), NewArticlesListActivity.class);
            } else if (sourceFrom.equals("NewMessageListActivity")) {
                intent.setClass(getActivity(), NewMessageListActivity.class);
            } else if (sourceFrom.equals(WallActivity.class.getSimpleName())) {
                intent.setClass(getActivity(), WallActivity.class);
            } else if (sourceFrom.equals(NotifyListActivity.class.getSimpleName())) {
                intent.setClass(getActivity(), NotifyListActivity.class);
            } else {
                getActivity().finish();
                return;
            }
            startActivity(intent);
            getActivity().finish();
        }
    }

    private void getWallMsgDetail(int start) {
        progressDialog.setMessage(getString(R.string.dialog_download_msg));
        progressDialog.show();

        final String url = AppConfig.DOMAIN_SITE_PATE + AppConfig.REQUEST_WALL_COMMUNITY_DETAIL;
        Log.i(TAG, "url:  " + url);

        uuidChose = sharedPreferences.getString(AppConfig.PREF_UNIQUE_ID, "");
        RequestParams params = new RequestParams();
        params.add("uid", uuidChose);
        params.add("time", AppUtils.getSystemTime());
        params.add("refId", refId);
        params.add("articleType", articleType);
        Log.i(TAG, "params:" + params.toString());

        asyncHttpClient.post(url, params, jsonHandler);
    }

    private void setGoodAdd() {
        progressDialog.setMessage(getString(R.string.dialog_download_msg));
        progressDialog.show();

        final String url = AppConfig.DOMAIN_SITE_PATE + AppConfig.INSERT_GOOD;
        Log.i(TAG, "url:  " + url);
        uuidChose = sharedPreferences.getString(AppConfig.PREF_UNIQUE_ID, "");
        RequestParams params = new RequestParams();
        params.add("uid", uuidChose);
        params.add("time", AppUtils.getSystemTime());
        params.add("refId", refId);
        Log.i(TAG, "params:" + params.toString());

        asyncHttpClient.post(url, params, addGoodHandler);
    }

    private void setGoodRemove() {
        progressDialog.setMessage(getString(R.string.dialog_download_msg));
        progressDialog.show();

        final String url = AppConfig.DOMAIN_SITE_PATE + AppConfig.REMOVE_GOOD;
        Log.i(TAG, "url:  " + url);
        uuidChose = sharedPreferences.getString(AppConfig.PREF_UNIQUE_ID, "");
        RequestParams params = new RequestParams();
        params.add("uid", uuidChose);
        params.add("time", AppUtils.getSystemTime());
        params.add("refId", refId);
        Log.i(TAG, "params:" + params.toString());
        asyncHttpClient.post(url, params, removeGoodHandler);
    }


    private String getGoodStr(int totleGood, List<Goods> goodList) {
        if (goodList == null) {
            return null;
        }
        int size = goodList.size();
        String str;
        if (size > 1) {
            str = goodList.get(0).getName() + "、";
            str += goodList.get(1).getName();
            str += "等 " + totleGood + " 個人覺得這是一篇好文章";
        } else if (size > 0) {
            str = goodList.get(0).getName();
            str += "覺得這是一篇好文章";
        } else {
            str = "您覺得這篇文章讚嗎？";
        }
        return str;
    }

    private void addImage(String imgPath) {
        ImageView imageView = new ImageView(mContext);
        imageView.setPadding(10, 10, 10, 10);
        imageView.setImageResource(R.drawable.qanews_no_image);
        AppUtils.loadPhoto(mContext, imageView, imgPath);
        mImgListView.addView(imageView);
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

    public JsonHttpResponseHandler jsonHandler = new JsonHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
            Log.d(TAG, "onSuccess = " + response);
            try {
                if (response.get("msgCode").equals("A01")) {
                    wallMessageDetail = gson.fromJson(response.toString(), WallMessageDetail.class);
                    if (wallMessageDetail != null && wallMessageDetail.getResult() != null) {
                        setArticleInfo();
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

        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            super.onFailure(statusCode, headers, responseString, throwable);
            Log.i(TAG, "444");
            Log.i(TAG, "responseString:" + responseString);
            oneBtnAlertDialog = new CustomerOneBtnAlertDialog(mContext);
            oneBtnAlertDialog.setMsg(getString(R.string.data_load_error))
                    .setPositiveBtnText(getString(R.string.sure))
                    .show();
        }
    };

    public JsonHttpResponseHandler addGoodHandler = new JsonHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
            Log.d(TAG, "onSuccess = " + response);
            try {
                if (response.get("msgCode").equals("A01")) {
                    GoodInfo goodInfo = gson.fromJson(String.valueOf(response), GoodInfo.class);
                    isGood = 1;
                    if (isGood == 0) {
                        mBtnGoodAdd.setImageResource(R.drawable.ic_good_article);
                    } else if (isGood == 1) {
                        mBtnGoodAdd.setImageResource(R.drawable.ic_good_article_press);
                    }
                    if (goodInfo.getResult() != null) {
                        String str = getGoodStr(goodInfo.getResult().getTotalGoodNum(), goodInfo.getResult().getGood());
                        mTxtGoodFriends.setText(str);
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

    public JsonHttpResponseHandler removeGoodHandler = new JsonHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
            Log.d(TAG, "onSuccess = " + response);
            try {
                if (response.get("msgCode").equals("A01")) {
                    GoodInfo goodInfo = gson.fromJson(String.valueOf(response), GoodInfo.class);
                    isGood = 0;
                    if (isGood == 0) {
                        mBtnGoodAdd.setImageResource(R.drawable.ic_good_article);
                    } else if (isGood == 1) {
                        mBtnGoodAdd.setImageResource(R.drawable.ic_good_article_press);
                    }
                    if (goodInfo.getResult() != null) {
                        String str = getGoodStr(goodInfo.getResult().getTotalGoodNum(), goodInfo.getResult().getGood());
                        mTxtGoodFriends.setText(str);
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
