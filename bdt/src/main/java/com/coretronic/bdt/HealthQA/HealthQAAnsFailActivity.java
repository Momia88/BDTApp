package com.coretronic.bdt.HealthQA;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
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
import com.coretronic.bdt.HealthQA.module.QuestionInfo;
import com.coretronic.bdt.R;
import com.coretronic.bdt.Utility.CustomerOneBtnAlertDialog;
import com.coretronic.bdt.Utility.CustomerTwoBtnAlertDialog;
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

/**
 * Created by james on 14/11/19.
 */
public class HealthQAAnsFailActivity extends Fragment {
    private final String TAG = HealthQAAnsFailActivity.class.getSimpleName();
    private Context mContext;

    // action bar
    private TextView actionBarTitle = null;
    private View rightLine = null;
    private View leftLine = null;
    private Button btnPopMenu = null;
    private Button actBarBackBtn = null;
    private Button ansFailQAStartPlayBtn = null;
    private Button ansFailQAExitPlayBtn = null;
    private ProgressDialog progressDialog = null;
    private CustomerOneBtnAlertDialog alert = null;
    private CustomerTwoBtnAlertDialog exitPlayAlert = null;
    private TextView remainHeartTV = null;

    private ImageView ansFailPlayHeart1 = null;
    private ImageView ansFailPlayHeart2 = null;
    private ImageView ansFailPlayHeart3 = null;
    private ImageView noLifeBestPlayLife1 = null;
    private ImageView noLifeBestPlayLife2 = null;
    private ImageView noLifeBestPlayLife3 = null;
    private BitmapDrawable heartDrawable;
    private BitmapDrawable noHeartDrawable;

    private LinearLayout ansCorrectLL = null;
    private ScrollView ansDetailLL = null;
    private SharedPreferences sharedPreferences;
    private AsyncHttpClient asyncHttpClient;

    // content
    private FragmentManager fragmentManager = null;
    private FragmentTransaction fragmentTransaction = null;
    private Fragment fragment = null;
    private String challengeId = "";
    private String uuidChose = "";
    private String questionId = "";
    private QuestionInfo questionInfo;
    private Gson gson = new Gson();
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

                    setAnsInCorrectData(response.toString());

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
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getActivity().getSharedPreferences(AppConfig.SHAREDPREFERENCES_NAME, 0);
        asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.setTimeout(AppConfig.TIMEOUT);

        Bundle bundle = this.getArguments();
        questionId = bundle.getString("questionId");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.health_qa_ansfail, container, false);
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(backListener);
        Log.i(TAG, "view:" + view);
        mContext = view.getContext();
        progressDialog = new ProgressDialog(mContext);

        exitPlayAlert = AppUtils.getAlertDialog(mContext,
                getResources().getString(R.string.qa_exit_alert_text),
                getResources().getString(R.string.cancel),
                getResources().getString(R.string.sure_exit),
                exitPlayAlertPositineveListener,
                exitPlayAlertNegativeListener);

        initView(view);

        syncDataWithServer();
        return view;
    }


    private void syncDataWithServer() {
        progressDialog.setMessage(getString(R.string.dialog_download_msg));
        progressDialog.show();

        final String url = AppConfig.DOMAIN_SITE_PATE + AppConfig.REQUEST_INCORRECT;
        Log.i(TAG, "url:  " + url);

        challengeId = sharedPreferences.getString(AppConfig.PREF_CHALLENGE_ID, "");

        uuidChose = sharedPreferences.getString(AppConfig.PREF_UNIQUE_ID, "");
        Log.i(TAG, "UUID:  " + uuidChose + "/questionId:" + questionId);
        RequestParams params = new RequestParams();
        params.add("uid", uuidChose);
//        params.add("uid", "uuid-12345");
        params.add("time", AppUtils.getSystemTime());
        params.add("challengeId", challengeId);
        params.add("questionId", questionId);
        Log.i(TAG, "params:  " + params);

        asyncHttpClient.post(url, params, jsonHandler);
    }


    private void initView(View view) {
        actionBarTitle = (TextView) view.findViewById(R.id.action_bar_title);
        rightLine = (View) view.findViewById(R.id.right_line);
        leftLine = (View) view.findViewById(R.id.left_line);
        btnPopMenu = (Button) view.findViewById(R.id.btnPopMenu);
        actBarBackBtn = (Button) view.findViewById(R.id.btnBack);

        ansFailQAStartPlayBtn = (Button) view.findViewById(R.id.ansFailQAStartPlayBtn);
        ansFailQAExitPlayBtn = (Button) view.findViewById(R.id.ansFailQAExitPlayBtn);

        remainHeartTV = (TextView) view.findViewById(R.id.return_video);
        ansFailPlayHeart1 = (ImageView) view.findViewById(R.id.ansFailPlayLife1);
        ansFailPlayHeart2 = (ImageView) view.findViewById(R.id.ansFailPlayLife2);
        ansFailPlayHeart3 = (ImageView) view.findViewById(R.id.ansFailPlayLife3);


        // action bar ui set
        actionBarTitle.setText(getString(R.string.qa_title));
        rightLine.setVisibility(View.GONE);
        leftLine.setVisibility(View.GONE);
        actBarBackBtn.setVisibility(View.GONE);
        btnPopMenu.setVisibility(View.GONE);

        heartDrawable = new BitmapDrawable(getResources(), getResources().openRawResource(R.drawable.play_life));
        noHeartDrawable = new BitmapDrawable(getResources(), getResources().openRawResource(R.drawable.play_life_out));

        if (android.os.Build.VERSION.SDK_INT < 16) {
            ansFailPlayHeart1.setImageDrawable(noHeartDrawable);
            ansFailPlayHeart2.setImageDrawable(noHeartDrawable);
            ansFailPlayHeart3.setImageDrawable(noHeartDrawable);


        } else {
            ansFailPlayHeart1.setImageDrawable(noHeartDrawable);
            ansFailPlayHeart2.setImageDrawable(noHeartDrawable);
            ansFailPlayHeart3.setImageDrawable(noHeartDrawable);
        }

        ansFailQAStartPlayBtn.setOnClickListener(btnListener);
        ansFailQAExitPlayBtn.setOnClickListener(btnListener);
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
            for (ImageView iv : imageViews)
//                iv.setImageBitmap(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.play_life_out));
            {
//                if (android.os.Build.VERSION.SDK_INT < 16) {
//                    iv.setImageDrawable(noHeartDrawable);
//                } else {
//                    iv.setImageDrawable(noHeartDrawable);
//                }
            }
        } else {
            for (int i = 0; i < count; i++) {
                if (android.os.Build.VERSION.SDK_INT < 16) {
                    imageViews[i].setImageDrawable(heartDrawable);
                } else {
                    imageViews[i].setImageDrawable(heartDrawable);
                }
            }
//            for (int j = count; j <= (imageViews.length - count); j++) {
////                imageViews[j].setImageBitmap(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.play_life_out));
//                if (android.os.Build.VERSION.SDK_INT < 16) {
//                    imageViews[j].setImageDrawable(noHeartDrawable);
//                } else {
//                    imageViews[j].setImageDrawable(noHeartDrawable);
//                }
//            }
        }
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
    }

    private void setAnsInCorrectData(String response) {
        questionInfo = gson.fromJson(response, QuestionInfo.class);
        if( Integer.valueOf( questionInfo.getResult().getHeart() ) != 0 )
        {
            remainHeartTV.setText(getString(R.string.remain_heart) + questionInfo.getResult().getHeart() + getString(R.string.suffix_heart_unit) );

            setHeart(Integer.valueOf(questionInfo.getResult().getHeart()), ansFailPlayHeart1, ansFailPlayHeart2, ansFailPlayHeart3);
//            AppUtils.setMultiHeartBitmap(mContext, Integer.valueOf(questionInfo.getResult().getHeart()),ansFailPlayHeart1,ansFailPlayHeart2,ansFailPlayHeart3);
        }
        else
        {
            redirectToNoHeartPage();
            Log.i(TAG,"no heart!!");
        }

    }

    private View.OnClickListener btnListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btnBack:
//                    finish();
                    break;
                case R.id.ansFailQAStartPlayBtn:
                    callSystemTime();
                    append = currentTime + "," +
                            uuidChose + "," +
                            "HealthQAAnsFailActivity" + "," +
                            "HealthQuestionActivity" + "," +
                            "btnNext" + "\n";
                    transactionLogSave(append);
                    nextQuestion();
                    break;

                case R.id.ansFailQAExitPlayBtn:
                    callSystemTime();
                    append = currentTime + "," +
                            uuidChose + "," +
                            "HealthQAAnsFailActivity" + "," +
                            "BtnBackAskDialog" + "," +
                            "btnBack" + "\n";
                    transactionLogSave(append);
                    exitPlayAlert.show();
                    break;
            }
        }
    };

    private void redirectToNoHeartPage()
    {
        Bundle bundle = new Bundle();
        bundle.putString("todayScore", questionInfo.getResult().getToday());
        bundle.putString("bestScore", questionInfo.getResult().getAll());
        bundle.putString("heartCount", questionInfo.getResult().getHeart());
        Log.i(TAG, "todayScore:" + questionInfo.getResult().getToday());
        Log.i(TAG, "bestScore:" + questionInfo.getResult().getAll());
        Log.i(TAG, "heartCount:"+questionInfo.getResult().getHeart());

        fragmentManager = null;
        fragmentTransaction = null;

        fragmentManager = getFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();

        fragment = null;
        fragment = new HealthQAAnsNoHeartActivity();
        fragment.setArguments(bundle);
        if (fragment != null) {
            fragmentTransaction
                    .replace(R.id.qa_frame_container, fragment, "HealthQAAnsNoHeartActivity")
                    .addToBackStack("HealthQAAnsNoHeartActivity")
                    .commit();
        } else {
            Log.e(TAG, "Error in creating fragment");
        }
    }


    private View.OnClickListener exitPlayAlertPositineveListener  = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            callSystemTime();
            append = currentTime + "," +
                    uuidChose + "," +
                    "BtnBackAskDialog" + "," +
                    "HealthQAAnsFailActivity" + "," +
                    "btnBackAskDialogCanael"+ "\n";
            transactionLogSave(append);
            if( exitPlayAlert != null )
            {
                exitPlayAlert.dismiss();
            }

        }
    };

    private View.OnClickListener exitPlayAlertNegativeListener =  new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            callSystemTime();
            append = currentTime + "," +
                    uuidChose + "," +
                    "BtnBackAskDialog" + "," +
                    "HealthQAMainActivity" + "," +
                    "btnBackAskDialogBack"+ "\n";
            transactionLogSave(append);
            Log.i(TAG, "not save");

            if( exitPlayAlert != null )
            {
                exitPlayAlert.dismiss();
            }
            qaFiailResetChallengeData();
            redirectToRankBtnEvent();
        }
    };


    private void qaFiailResetChallengeData()
    {
        sharedPreferences.edit()
                .putString(AppConfig.PREF_CHALLENGE_ID, "")
                .commit();
    }

    private void redirectToRankBtnEvent() {
        fragmentManager = getFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();

        fragment = null;
        fragment = new HealthQARankActivity();
        if (fragment != null) {

            sharedPreferences.edit()
                    .putString(AppConfig.PREF_CHALLENGE_ID, "")
                    .commit();


            fragmentTransaction
                    .replace(R.id.qa_frame_container, fragment, "HealthQARankActivity")
                    .addToBackStack("HealthQARankActivity")
                    .commit();
        } else {
            Log.e(TAG, "Error in creating fragment");
        }
    }

    private void nextQuestion()
    {
        fragmentManager = null;
        fragmentTransaction = null;

        fragmentManager = getFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();

        fragment = null;
        fragment = new HealthQuestionActivity();
        if (fragment != null) {
            fragmentTransaction
                    .replace(R.id.qa_frame_container, fragment, "HealthQuestionActivity")
//                    .addToBackStack("HealthQuestionActivity")
                    .commit();
        } else {
            Log.e(TAG, "Error in creating fragment");
        }
    }

    private View.OnKeyListener backListener = new View.OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    exitPlayAlert.show();
                    return true;
                }
            }
            return false;
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    private View.OnClickListener erroAlertListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Log.i(TAG, " on error Litener click");
            alert.dismiss();
            getActivity().finish();
        }
    };
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
