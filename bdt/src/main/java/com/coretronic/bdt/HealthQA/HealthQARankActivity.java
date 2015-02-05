package com.coretronic.bdt.HealthQA;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.coretronic.bdt.AppConfig;
import com.coretronic.bdt.AppUtils;
import com.coretronic.bdt.HealthQA.adapter.HealthQAListBaseAdapter;
import com.coretronic.bdt.HealthQA.module.MemberRecordInfo;
import com.coretronic.bdt.MainAcitvity;
import com.coretronic.bdt.R;
import com.coretronic.bdt.Utility.CustomerOneBtnAlertDialog;
import com.coretronic.bdt.Utility.SegmentedUIComponent;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by james on 14/11/19.
 */
public class HealthQARankActivity extends Fragment {
    private final String TAG = HealthQARankActivity.class.getSimpleName();
    private Context mContext;

    // action bar
    private TextView actionBarTitle = null;
    private View rightLine = null;
    private Button btnPopMenu = null;
    private Button actBarBackBtn = null;

    // ui
    private RadioGroup sortGroup = null;
    private SegmentedUIComponent friendSort = null;
    private SegmentedUIComponent allSort = null;
    private ListView qaRankListView = null;
    private ImageView personalPhotoIV = null;
    private TextView qaSelfScoreTV = null;
    private TextView qaSelfSortTV = null;
    private TextView qaTotalCountTV = null;
    private TextView noFriendText = null;
    private Button qaStartPlayBtn = null;
    private ProgressDialog progressDialog = null;
    private ImageView heartIV1 = null;
    private ImageView heartIV2 = null;
    private ImageView heartIV3 = null;
    private CustomerOneBtnAlertDialog alert = null;
    private CustomerOneBtnAlertDialog noHeartAlert = null;
    private FragmentManager fragmentManager = null;
    private FragmentTransaction fragmentTransaction = null;
    private Fragment fragment = null;
    private View view = null;
    private BitmapDrawable heartDrawable;
    private BitmapDrawable noHeartDrawable;

    // content
    private HealthQAListBaseAdapter qaRankListAdapter = null;
    private String selfScoreVal = "0";
    private String selfSortVal = "0";
    private String friendsTotalNum = "0";
    private String membersTotalNum = "0";
    private String userLogoForder = null;
    private String userThumb = "";
    private File userLogo = null;
    // 0: friends rank ; 1: all member rank
    private int radioBtnTag = 0;
    private SharedPreferences sharedPreferences;

    // server data
    private String url = "";
    private String uuidChose = "";
    private AsyncHttpClient asyncHttpClient;
    private Gson gson = new Gson();
    private MemberRecordInfo memberRecordInfo;
    private List<MemberRecordInfo.Users> qaListViewData;
    private int heartCount = 0;

    //transactionLog
    private String currentTime;
    private String append;


    public JsonHttpResponseHandler jsonHandler = new JsonHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
            Log.d(TAG, "qa onSuccess = " + response);

            try {
                if (response.get("msgCode").equals(AppConfig.SUCCESS_CODE)) {

                    // recored data to sharepreferences
                    if (radioBtnTag == 0) {
                        sharedPreferences.edit()
                                .putString(AppConfig.PREF_FRIENDS_RANK_DATA_KEY, response.toString())
                                .putString(AppConfig.PREF_FRIENDS_RANK_DATA_KEY, response.toString())
                                .commit();
                        setFriendRankData();
                    } else if (radioBtnTag == 1) {
                        sharedPreferences.edit()
                                .putString(AppConfig.PREF_ALLMEMBERS_RANK_DATA_KEY, response.toString())
                                .commit();
                        setAllMembersRankData();
                    }


                } else {
                    alert = AppUtils.getAlertDialog(mContext, getString(R.string.data_result_error), erroAlertListener);
                    alert.show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                alert = AppUtils.getAlertDialog(mContext, getString(R.string.data_result_error), erroAlertListener);
                alert.show();
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
                alert = AppUtils.getAlertDialog(mContext, getString(R.string.data_result_error), erroAlertListener);
                alert.show();
            } catch (Exception e) {
                e.printStackTrace();
                alert = AppUtils.getAlertDialog(mContext, getString(R.string.data_result_error), erroAlertListener);
                alert.show();
            }

        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            Log.d(TAG, "onFailure");
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
            alert = AppUtils.getAlertDialog(mContext, getString(R.string.data_load_error), erroAlertListener);
            alert.show();
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            super.onFailure(statusCode, headers, responseString, throwable);
            Log.i(TAG, "444");
            Log.i(TAG, "responseString:" + responseString);
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
            alert = AppUtils.getAlertDialog(mContext, getString(R.string.returndata_error), erroAlertListener);
            alert.show();

//            startNextActivity();
        }

    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getActivity().getSharedPreferences(AppConfig.SHAREDPREFERENCES_NAME, 0);
        asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.setTimeout(AppConfig.TIMEOUT);
        uuidChose = sharedPreferences.getString(AppConfig.PREF_UNIQUE_ID, "");
        userThumb = sharedPreferences.getString(AppConfig.PREF_USER_THUMB, "");
        qaListViewData = new ArrayList<MemberRecordInfo.Users>();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.health_qa_rank, container, false);
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(backListener);
        Log.i(TAG, "view:" + view);
        mContext = view.getContext();


        initView(view);
        loadUserPhoto();
        qaRankListAdapter = new HealthQAListBaseAdapter(mContext, qaListViewData);
        qaRankListView.setAdapter(qaRankListAdapter);

        Log.i(TAG, "PREF_FRIENDS_RANK_DATA_KEY:" + sharedPreferences.getString(AppConfig.PREF_FRIENDS_RANK_DATA_KEY, ""));


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(mContext);
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
        }

        if (radioBtnTag == 0) {
            if ((sharedPreferences.getString(AppConfig.PREF_FRIENDS_RANK_DATA_KEY, "")).equals("")) {
                getDataFromServer();
            } else {
                setFriendRankData();
            }
        } else {
            if ((sharedPreferences.getString(AppConfig.PREF_ALLMEMBERS_RANK_DATA_KEY, "")).equals("")) {
                getDataFromServer();
            } else {
                setAllMembersRankData();
            }
        }
    }


    private void initView(View view) {
        actionBarTitle = (TextView) view.findViewById(R.id.action_bar_title);
        rightLine = (View) view.findViewById(R.id.right_line);
        btnPopMenu = (Button) view.findViewById(R.id.btnPopMenu);
        actBarBackBtn = (Button) view.findViewById(R.id.btnBack);

        // action bar ui set
        actionBarTitle.setText(getString(R.string.qa_title));
        rightLine.setVisibility(View.GONE);
        btnPopMenu.setVisibility(View.GONE);

        // content ui
        sortGroup = (RadioGroup) view.findViewById(R.id.qa_sort_group);
        friendSort = (SegmentedUIComponent) view.findViewById(R.id.friendsort);
        allSort = (SegmentedUIComponent) view.findViewById(R.id.allSort);
        qaRankListView = (ListView) view.findViewById(R.id.health_qa_listView);
        personalPhotoIV = (ImageView) view.findViewById(R.id.qa_self_photo);
        qaSelfScoreTV = (TextView) view.findViewById(R.id.qa_self_score);
        qaSelfSortTV = (TextView) view.findViewById(R.id.qa_self_sort);
        qaTotalCountTV = (TextView) view.findViewById(R.id.qa_total_count);
        qaStartPlayBtn = (Button) view.findViewById(R.id.qa_start_play);
        noFriendText = (TextView) view.findViewById((R.id.no_data_text));
        heartIV1 = (ImageView) view.findViewById(R.id.play_life1);
        heartIV2 = (ImageView) view.findViewById(R.id.play_life2);
        heartIV3 = (ImageView) view.findViewById(R.id.play_life3);


        heartDrawable = new BitmapDrawable(getResources(), getResources().openRawResource(R.drawable.play_life));
        noHeartDrawable = new BitmapDrawable(getResources(), getResources().openRawResource(R.drawable.play_life_out));

        if (android.os.Build.VERSION.SDK_INT < 16) {
            heartIV1.setImageDrawable(noHeartDrawable);
            heartIV2.setImageDrawable(noHeartDrawable);
            heartIV3.setImageDrawable(noHeartDrawable);


        } else {
            heartIV1.setImageDrawable(noHeartDrawable);
            heartIV2.setImageDrawable(noHeartDrawable);
            heartIV3.setImageDrawable(noHeartDrawable);


        }

        sortGroup.setOnCheckedChangeListener(groupListener);
        btnPopMenu.setOnClickListener(btnListener);
        actBarBackBtn.setOnClickListener(btnListener);
        qaStartPlayBtn.setOnClickListener(btnListener);

    }


    private void getDataFromServer() {
        progressDialog.setMessage(getString(R.string.dialog_download_msg));
        progressDialog.show();

        if (radioBtnTag == 0) {
            url = AppConfig.DOMAIN_SITE_PATE + AppConfig.REQUEST_GAME_FRIENDS_RANKS;

        } else if (radioBtnTag == 1) {
            url = AppConfig.DOMAIN_SITE_PATE + AppConfig.REQUEST_GAME_ALLUSERS_RANKS;
        }

        Log.i(TAG, "url:  " + url);
        uuidChose = sharedPreferences.getString(AppConfig.PREF_UNIQUE_ID, "");
        Log.i(TAG, "UUID:  " + uuidChose);
        RequestParams params = new RequestParams();
        params.add("uid", uuidChose);
//        params.add("uid", "uuid-12345");
//        params.add("uid", "uuid-1234");
        params.add("time", AppUtils.getSystemTime());
        Log.i(TAG, "params:  " + params);

        asyncHttpClient.post(url, params, jsonHandler);
        params = null;

    }


    private RadioGroup.OnCheckedChangeListener groupListener = new RadioGroup.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {

            Log.i(TAG, "checkedId:" + checkedId);

            switch (checkedId) {
                case R.id.friendsort:
                    Log.i(TAG, "click friend sort button");
                    callSystemTime();
                    append = currentTime + "," +
                            uuidChose + "," +
                            "HealthQAMainActivity" + "," +
                            "HealthQAFriendsort" + "," +
                            "btnFriendsort" + "\n";
                    transactionLogSave(append);
                    radioBtnTag = 0;
                    if ((sharedPreferences.getString(AppConfig.PREF_FRIENDS_RANK_DATA_KEY, "")).equals("")) {
                        getDataFromServer();
                    } else {
                        setFriendRankData();
                    }

                    break;
                case R.id.allSort:
                    Log.i(TAG, "click all sort button");
                    callSystemTime();
                    append = currentTime + "," +
                            uuidChose + "," +
                            "HealthQAMainActivity" + "," +
                            "HealthQAAllSort" + "," +
                            "btnAllSort" + "\n";
                    transactionLogSave(append);
                    radioBtnTag = 1;
                    if ((sharedPreferences.getString(AppConfig.PREF_ALLMEMBERS_RANK_DATA_KEY, "")).equals("")) {
                        getDataFromServer();
                    } else {
                        setAllMembersRankData();
                    }

                    break;

            }
        }
    };


    private void setFriendRankData() {

        memberRecordInfo = gson.fromJson(sharedPreferences.getString(AppConfig.PREF_FRIENDS_RANK_DATA_KEY, ""), MemberRecordInfo.class);
        qaListViewData.clear();
        qaListViewData.addAll(memberRecordInfo.getResult().getUsersList());
        qaRankListAdapter.notifyDataSetChanged();

        // if no member, the listview show textview
        if (memberRecordInfo.getResult().getUsersList().size() != 0) {
            qaRankListView.setVisibility(View.VISIBLE);
            noFriendText.setVisibility(View.GONE);
        } else {
            qaRankListView.setVisibility(View.GONE);
            noFriendText.setText(R.string.no_friends_text);
            noFriendText.setVisibility(View.VISIBLE);
        }

        selfScoreVal = memberRecordInfo.getResult().getCount();
        friendsTotalNum = memberRecordInfo.getResult().getTotalUser();
        selfSortVal = memberRecordInfo.getResult().getRank();
        heartCount = Integer.valueOf(memberRecordInfo.getResult().getHeart());

        if (sharedPreferences.getString(AppConfig.PREF_CHALLENGE_ID, "").equals("")) {
            sharedPreferences.edit()
                    .putString(AppConfig.PREF_CHALLENGE_ID, memberRecordInfo.getResult().getChallengeId())
                    .commit();
        }
        Log.i(TAG, "current challengeID:" + sharedPreferences.getString(AppConfig.PREF_CHALLENGE_ID, ""));

        // ===== set ui value =====
        // set personalPhotoIV photo
//        personalPhotoIV.setImageBitmap(decodeBase64(memberRecordInfo.getResult().getThumb()));
//        Log.i(TAG,"memberRecordInfo.getResult().getThumb():\n"+memberRecordInfo.getResult().getThumb());
//        personalPhotoIV.setImageBitmap(AppUtils.decodeBase64(memberRecordInfo.getResult().getThumb()));
        if (userLogo.exists()) {
//            personalPhotoIV.setImageURI(Uri.fromFile(userLogo));
        }
        if (userThumb.equals("")) {
            personalPhotoIV.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.qa_self_photo));
        } else {
            personalPhotoIV.setImageBitmap(AppUtils.base64ToBitmap(userThumb));
        }

        // set self score
        qaSelfScoreTV.setText(getString(R.string.self_txt) + AppUtils.numberFormat(selfScoreVal) + getString(R.string.qascore_unit));
        // set self score rank number
        qaSelfSortTV.setText(getString(R.string.prefix_unit) + " " + AppUtils.numberFormat((selfSortVal)) + " " + getString(R.string.score_suffix_unit));
        // set self total friends
        qaTotalCountTV.setText(AppUtils.numberFormat(friendsTotalNum) + getString(R.string.suffix_friends));
        setHeartCountPhoto();
    }


    private void setAllMembersRankData() {

        memberRecordInfo = gson.fromJson(sharedPreferences.getString(AppConfig.PREF_ALLMEMBERS_RANK_DATA_KEY, ""), MemberRecordInfo.class);
        qaListViewData.clear();
        qaListViewData.addAll(memberRecordInfo.getResult().getUsersList());
        qaRankListAdapter.notifyDataSetChanged();

        // if no member, the listview show textview
        if (memberRecordInfo.getResult().getUsersList().size() != 0) {
            qaRankListView.setVisibility(View.VISIBLE);
            noFriendText.setVisibility(View.GONE);
        } else {
            qaRankListView.setVisibility(View.GONE);
            noFriendText.setText(R.string.no_members_text);
            noFriendText.setVisibility(View.VISIBLE);
        }

        selfScoreVal = memberRecordInfo.getResult().getCount();
        membersTotalNum = memberRecordInfo.getResult().getTotalUser();
        selfSortVal = memberRecordInfo.getResult().getRank();
        heartCount = Integer.valueOf(memberRecordInfo.getResult().getHeart());


        // ===== set ui value =====
        // set personalPhotoIV photo
//        personalPhotoIV.setImageBitmap(AppUtils.decodeBase64(memberRecordInfo.getResult().getThumb()));
        if (userLogo.exists()) {
//            personalPhotoIV.setImageURI(Uri.fromFile(userLogo));
        }
        if (userThumb.equals("")) {
            personalPhotoIV.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.qa_self_photo));
        } else {
            personalPhotoIV.setImageBitmap(AppUtils.base64ToBitmap(userThumb));
        }
        // set self score
        qaSelfScoreTV.setText(getString(R.string.self_txt) + AppUtils.numberFormat(selfScoreVal) + getString(R.string.qascore_unit));
        // set self score sort number
        qaSelfSortTV.setText(getString(R.string.prefix_unit) + " " + AppUtils.numberFormat((Integer.valueOf(selfSortVal))) + " " + getString(R.string.score_suffix_unit));
        // set all members
        qaTotalCountTV.setText(getString(R.string.prefix_all) + AppUtils.numberFormat(membersTotalNum) + getString(R.string.suffix_members));
        // set heart count
        setHeartCountPhoto();

    }

    private void loadUserPhoto() {
        userLogoForder = mContext.getExternalCacheDir() + AppConfig.JPEG_USER_LOGO_FOLDER;
        File dir = new File(userLogoForder);
        Log.i(TAG, "userLogoForder:" + userLogoForder);

        if (!dir.exists()) {
            Log.i(TAG, "userLogoForder:" + userLogoForder);
            dir.mkdirs();
        }
        dir = null;
        userLogo = new File(userLogoForder, uuidChose + ".png");
    }

    private View.OnClickListener btnListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btnBack:
                    redirectToMainPageEvent();
                    callSystemTime();
                    append = currentTime + "," +
                            uuidChose + "," +
                            "HealthQAMainActivity" + "," +
                            "MainActivity" + "," +
                            "btnBack" + "\n";
                    transactionLogSave(append);
                    break;

                // qa start play button listener
                case R.id.qa_start_play:
//                    Intent intent = new Intent(HealthQARankActivity.this, HealthQuestionActivity.class);
//                    startActivity(intent);
//                    finish();
                    callSystemTime();
                    append = currentTime + "," +
                            uuidChose + "," +
                            "HealthQAMainActivity" + "," +
                            "HealthQuestionActivity" + "," +
                            "btnStarPlayHealthQuestion" + "\n";
                    transactionLogSave(append);
                    if (heartCount == 0) {
                        noHeartAlert = AppUtils.getAlertDialog(mContext, getString(R.string.notheart_alert_text), getString(R.string.close));
                        noHeartAlert.show();
                        return;
                    }

                    clearListViewPreferenceData();
                    fragmentManager = getFragmentManager();
                    fragmentTransaction = fragmentManager.beginTransaction();
                    fragment = new HealthQuestionActivity();
                    if (fragment != null) {
                        fragmentTransaction
                                .replace(R.id.qa_frame_container, fragment, "HealthQuestionActivity")
                                .addToBackStack("HealthQuestionActivity")
                                .commit();
                    } else {
                        Log.e(TAG, "Error in creating fragment");
                    }

                    break;
            }
        }
    };


    private View.OnKeyListener backListener = new View.OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    callSystemTime();
                    append = currentTime + "," +
                            uuidChose + "," +
                            "HealthQAMainActivity" + "," +
                            "MainActivity" + "," +
                            "btnKeyCodeBack" + "\n";
                    transactionLogSave(append);
                    redirectToMainPageEvent();
                    return true;
                }
            }
            return false;
        }
    };


    private void redirectToMainPageEvent() {
        clearListViewPreferenceData();
        Intent intent = new Intent();
        intent.setClass(mContext, MainAcitvity.class);
        startActivity(intent);
        getActivity().finish();
    }


    private void setHeartCountPhoto() {
//        AppUtils.setMultiHeartBitmap(mContext, heartCount, heartIV1, heartIV2, heartIV3);
        setHeart( heartCount, heartIV1, heartIV2, heartIV3);

    }


    private void setHeart(int count, ImageView... imageViews)
    {
        Log.i(TAG, "count:"+count);
        if (count < 0) {
            for (ImageView iv : imageViews) {
//                iv.setImageBitmap(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.play_life_out));
                if (android.os.Build.VERSION.SDK_INT < 16) {
                    iv.setImageDrawable(noHeartDrawable);
                } else {
                    iv.setImageDrawable(noHeartDrawable);
                }
            }
        }
        if (count == 0) {
        } else {
            for (int i = 0; i < count; i++) {
                if (android.os.Build.VERSION.SDK_INT < 16) {
                    imageViews[i].setImageDrawable(heartDrawable);
                } else {
                    imageViews[i].setImageDrawable(heartDrawable);
                }
            }
        }
    }


    private View.OnClickListener erroAlertListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Log.i(TAG, " on error Litener click");
            clearListViewPreferenceData();
            alert.dismiss();
            getActivity().finish();
        }
    };


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (null != noHeartDrawable && !noHeartDrawable.getBitmap().isRecycled()) {
            Log.i(TAG,"====correctIVDrawable===");
            noHeartDrawable.getBitmap().recycle();
            noHeartDrawable = null;
        }

        if (null != heartDrawable && !heartDrawable.getBitmap().isRecycled()) {
            Log.i(TAG,"====correctIVDrawable===");
            heartDrawable.getBitmap().recycle();
            heartDrawable = null;
        }

        if( userLogo != null )
        {
            userLogo = null;
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        clearListViewPreferenceData();

        if (memberRecordInfo != null) {
            memberRecordInfo = null;
        }
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }

        if (noHeartAlert != null) {
            noHeartAlert.dismiss();
            noHeartAlert = null;
        }

    }

    private void clearListViewPreferenceData() {
        sharedPreferences.edit()
                .putString(AppConfig.PREF_FRIENDS_RANK_DATA_KEY, "")
                .putString(AppConfig.PREF_ALLMEMBERS_RANK_DATA_KEY, "")
                .commit();
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
