package com.coretronic.bdt.HealthQA;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
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
public class HealthQAAnsCorrectActivity extends Fragment {
    private final String TAG = HealthQAAnsCorrectActivity.class.getSimpleName();
    private Context mContext;

    private static int TOTAL_DELAYTIME = 2000;
    // action bar
    private TextView actionBarTitle = null;
    private View rightLine = null;
    private View leftLine = null;
    private Button btnPopMenu = null;
    private Button actBarBackBtn = null;
    private ProgressDialog progressDialog = null;
    private TextView qaExplainTitle = null;
    private TextView qaExplainContent = null;
    private TextView qaExplainAnsNum = null;
    private TextView dataSyncTV = null;
    private ImageView qaExplainHeart1 = null;
    private ImageView qaExplainHeart2 = null;
    private ImageView qaExplainHeart3 = null;
    private ImageView correctIV = null;
    private Button qaExitBtn = null;
    private Button qaNextbtn = null;
    private CustomerOneBtnAlertDialog alert = null;
    private CustomerTwoBtnAlertDialog askExitAlertDialog = null;
    private BitmapDrawable heartDrawable;
    private BitmapDrawable noHeartDrawable;

    private SharedPreferences sharedPreferences = null;

    private LinearLayout ansCorrectLL = null;
    private ScrollView ansDetailLL = null;

    // content
    private Gson gson = new Gson();
    private FragmentManager fragmentManager = null;
    private FragmentTransaction fragmentTransaction = null;
    private Fragment fragment = null;
    private String challengeId = "";
    private String uuidChose = "";
    private AsyncHttpClient asyncHttpClient;
    private String questionId = "";
    private QuestionInfo questionInfo = null;
    private View view = null;
    private BitmapDrawable correctIVDrawable;


    // set time
    Date dt1 = null;
    Date dt2 = null;

    //transactionLog
    private String currentTime;
    private String append;


    public JsonHttpResponseHandler jsonHandler = new JsonHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Header[] headers, final JSONObject response) {
            try {


                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
                Log.d(TAG, "qa onSuccess = " + response);
                dt2 = new Date();
                Long ut1 = dt1.getTime();
                Long ut2 = dt2.getTime();
                Long timeP = ut2 - ut1;
                Log.i(TAG, "qa timeP:" + timeP);


                if (timeP < TOTAL_DELAYTIME) {
//                dataSyncTV.setVisibility(View.VISIBLE);
                    long delayTime = TOTAL_DELAYTIME - timeP;
                    Log.i(TAG, "qa delayTime:" + delayTime);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            changeToCorrectDetailPage(response);
                        }
                    }, delayTime);
                    handler = null;
                } else {
//                dataSyncTV.setVisibility(View.VISIBLE);
                    changeToCorrectDetailPage(response);
                    Log.i(TAG, "qa show loading text");
                }
            } catch (Exception e) {
                alert = AppUtils.getAlertDialog(mContext, getString(R.string.returndata_error), erroAlertListener);
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
            alert = AppUtils.getAlertDialog(mContext, getString(R.string.get_data_error), erroAlertListener);
            alert.show();
        }
    };


    private void changeToCorrectDetailPage(JSONObject response) {
        try {
            if (response.get("msgCode").equals(AppConfig.SUCCESS_CODE)) {

                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
                // recored data to sharepreferences

                setContentData(response.toString());
                dataSyncTV.setVisibility(View.INVISIBLE);
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = getActivity().getSharedPreferences(AppConfig.SHAREDPREFERENCES_NAME, 0);
        Bundle bundle = this.getArguments();
        questionId = bundle.getString("questionId", "");
        challengeId = sharedPreferences.getString(AppConfig.PREF_CHALLENGE_ID, "");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.health_qa_explain, container, false);
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(backListener);
        try {


            Log.i(TAG, "view:" + view);
            mContext = view.getContext();
            progressDialog = new ProgressDialog(mContext);
            dt1 = new Date();

            askExitAlertDialog = AppUtils.getAlertDialog(mContext,
                    getResources().getString(R.string.qa_exit_alert_text),
                    getResources().getString(R.string.cancel),
                    getResources().getString(R.string.sure_exit),
                    askExitAlertPositineveListener,
                    askExitAlertNegativeListener);

            initView(view);

            if (android.os.Build.VERSION.SDK_INT < 16) {
                correctIV.setBackgroundDrawable(correctIVDrawable);
            } else {
                correctIV.setBackground(correctIVDrawable);
            }

            ansCorrectLL.setVisibility(View.VISIBLE);
            ansDetailLL.setVisibility(View.GONE);

            getDetailAnswer();
//        Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            public void run() {
//                getDetailAnswer();
//            }
//        }, 1500);

        } catch (Exception e) {
            alert = AppUtils.getAlertDialog(mContext, getString(R.string.get_data_error), erroAlertListener);
            alert.show();
        }
        return view;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (null != correctIVDrawable && !correctIVDrawable.getBitmap().isRecycled()) {
            Log.i(TAG,"====correctIVDrawable===");
            correctIVDrawable.getBitmap().recycle();
            correctIVDrawable = null;
        }

        actBarBackBtn.setOnClickListener(null);
        qaExitBtn.setOnClickListener(null);
        qaNextbtn.setOnClickListener(null);

        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }

        if (askExitAlertDialog != null) {
            askExitAlertDialog.dismiss();
            askExitAlertDialog = null;
        }
        if( asyncHttpClient != null )
        {
            asyncHttpClient = null;
        }
        if (null != noHeartDrawable && !noHeartDrawable.getBitmap().isRecycled()) {
            noHeartDrawable.getBitmap().recycle();
            noHeartDrawable = null;
        }

        if (null != heartDrawable && !heartDrawable.getBitmap().isRecycled()) {
            heartDrawable.getBitmap().recycle();
            heartDrawable = null;
        }

        if( view != null )
        {
            view = null;
        }

        System.gc();
    }


    private void getDetailAnswer() {
        if (askExitAlertDialog.isShowing()) {
            askExitAlertDialog.dismiss();
        }
//        progressDialog.setMessage(getString(R.string.dialog_download_msg));
//        progressDialog.show();

        final String url = AppConfig.DOMAIN_SITE_PATE + AppConfig.REQUEST_CORRECT;
        Log.i(TAG, "url:  " + url);

        challengeId = sharedPreferences.getString(AppConfig.PREF_CHALLENGE_ID, "");
        uuidChose = sharedPreferences.getString(AppConfig.PREF_UNIQUE_ID, "");
        Log.i(TAG, "UUID:  " + uuidChose);
        asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.setTimeout(AppConfig.TIMEOUT);
        RequestParams params = new RequestParams();
        params.add("uid", uuidChose);
//        params.add("uid", "uuid-12345");
        params.add("time", AppUtils.getSystemTime());
        params.add("questionId", questionId);
        params.add("challengeId", challengeId);
        Log.i(TAG, "params:  " + params);
        asyncHttpClient.post(url, params, jsonHandler);
        params = null;
    }


    private void initView(View view) {
        actionBarTitle = (TextView) view.findViewById(R.id.action_bar_title);
        rightLine = (View) view.findViewById(R.id.right_line);
        leftLine = (View) view.findViewById(R.id.left_line);
        btnPopMenu = (Button) view.findViewById(R.id.btnPopMenu);
        actBarBackBtn = (Button) view.findViewById(R.id.btnBack);

        ansCorrectLL = (LinearLayout) view.findViewById(R.id.qa_explain_correct);
        ansDetailLL = (ScrollView) view.findViewById(R.id.qa_explain_detail);

        qaExplainTitle = (TextView) view.findViewById(R.id.qa_explain_title);
        qaExplainContent = (TextView) view.findViewById(R.id.qa_explain_content);
        dataSyncTV = (TextView) view.findViewById(R.id.dataSyncTV);
        qaExplainAnsNum = (TextView) view.findViewById(R.id.qa_explain_ansnum);
        qaExplainHeart1 = (ImageView) view.findViewById(R.id.qa_explain_heart1);
        qaExplainHeart2 = (ImageView) view.findViewById(R.id.qa_explain_heart2);
        qaExplainHeart3 = (ImageView) view.findViewById(R.id.qa_explain_heart3);
        correctIV = (ImageView) view.findViewById(R.id.correctIV);
        qaExitBtn = (Button) view.findViewById(R.id.qa_explain_exitbtn);
        qaNextbtn = (Button) view.findViewById(R.id.qa_explain_nextbtn);

        // action bar ui set
        actionBarTitle.setText(getString(R.string.qa_title));
        rightLine.setVisibility(View.GONE);
        leftLine.setVisibility(View.GONE);
        btnPopMenu.setVisibility(View.GONE);
        actBarBackBtn.setVisibility(View.GONE);

        actBarBackBtn.setOnClickListener(btnListener);
        qaExitBtn.setOnClickListener(btnListener);
        qaNextbtn.setOnClickListener(btnListener);

        correctIVDrawable = new BitmapDrawable(getResources(), getResources().openRawResource(R.drawable.qaans_correct));
        noHeartDrawable = new BitmapDrawable(getResources(), getResources().openRawResource(R.drawable.play_life_out));
        heartDrawable = new BitmapDrawable(getResources(), getResources().openRawResource(R.drawable.play_life));

        if (android.os.Build.VERSION.SDK_INT < 16) {
            qaExplainHeart1.setImageDrawable(noHeartDrawable);
            qaExplainHeart2.setImageDrawable(noHeartDrawable);
            qaExplainHeart3.setImageDrawable(noHeartDrawable);
        } else {
            qaExplainHeart1.setImageDrawable(noHeartDrawable);
            qaExplainHeart2.setImageDrawable(noHeartDrawable);
            qaExplainHeart3.setImageDrawable(noHeartDrawable);
        }

    }


    private void setContentData(String response) {
        ansCorrectLL.setVisibility(View.GONE);
        ansDetailLL.setVisibility(View.VISIBLE);

        questionInfo = gson.fromJson(response, QuestionInfo.class);
        Log.i(TAG, "questionInfo.getcontent:" + questionInfo.getResult().getContent());
        if (questionInfo.getResult().getTitle() == null) {
            qaExplainTitle.setVisibility(View.GONE);
        } else {
            qaExplainTitle.setVisibility(View.VISIBLE);
            qaExplainTitle.setText(questionInfo.getResult().getTitle());
        }

        qaExplainContent.setText(questionInfo.getResult().getContent());
        qaExplainAnsNum.setText(questionInfo.getResult().getToday() + getString(R.string.qascore_unit));

//        AppUtils.setMultiHeartBitmap(mContext, Integer.valueOf(questionInfo.getResult().getHeart()), qaExplainHeart1, qaExplainHeart2, qaExplainHeart3);

        setHeart(Integer.valueOf(questionInfo.getResult().getHeart()), qaExplainHeart1, qaExplainHeart2, qaExplainHeart3);

    }


    private void setHeart(int count, ImageView... imageViews)
    {
        Log.i(TAG, "count:"+count);
        if (count < 0) {
            for (ImageView iv : imageViews) {
//                iv.setImageBitmap(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.play_life_out));
            }
        }
        if (count == 0) {
//            for (ImageView iv : imageViews)
//                iv.setImageBitmap(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.play_life_out));
        } else {
            for (int i = 0; i < count; i++) {
                if (android.os.Build.VERSION.SDK_INT < 16) {
                    imageViews[i].setImageDrawable(heartDrawable);
                } else {
                    imageViews[i].setImageDrawable(heartDrawable);
                }
            }
            for (int j = count; j < (imageViews.length - count); j++) {
//                imageViews[j].setImageBitmap(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.play_life_out));
            }
        }
    }

    private View.OnClickListener btnListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btnBack:
//                    finish();
                    exitBtnEvent();
                    break;
                case R.id.qa_explain_exitbtn:
                    callSystemTime();
                    append = currentTime + "," +
                            uuidChose + "," +
                            "HealthQuestionActivity" + "," +
                            "BtnBackAskDialog" + "," +
                            "btnBack" + "\n";
                    transactionLogSave(append);
                    exitBtnEvent();
                    break;
                case R.id.qa_explain_nextbtn:
                    callSystemTime();
                    append = currentTime + "," +
                            uuidChose + "," +
                            "HealthQuestionActivity" + "," +
                            "HealthQuestionActivity" + "," +
                            "btnNext" + "\n";
                    transactionLogSave(append);
                    nextQuestion();
                    break;
            }
        }
    };


    private void nextQuestion() {
        fragmentManager = getFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();

        fragment = null;
        fragment = new HealthQuestionActivity();
        if (fragment != null) {
            fragmentTransaction
                    .replace(R.id.qa_frame_container, fragment, "HealthQuestionActivity")
                    .addToBackStack("HealthQuestionActivity")
                    .commit();
        } else {
            Log.e(TAG, "Error in creating fragment");
        }
    }

    private void exitBtnEvent() {
        askExitAlertDialog.show();

    }

    private View.OnClickListener erroAlertListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Log.i(TAG, " on error Litener click");
            if (alert != null) {
                alert.dismiss();
            }
            getActivity().finish();
        }

        public void onClick(DialogInterface dialog, int id) {
            Log.i(TAG, " on error Litener click");
            if (alert != null) {
                alert.dismiss();
            }

            getActivity().finish();

        }

    };

    private View.OnClickListener askExitAlertPositineveListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            callSystemTime();
            append = currentTime + "," +
                    uuidChose + "," +
                    "BtnBackAskDialog" + "," +
                    "HealthQuestionActivity" + "," +
                    "btnBackAskDialogCanael"+ "\n";
            transactionLogSave(append);
            if (askExitAlertDialog != null) {
                askExitAlertDialog.dismiss();
            }

        }
    };

    private View.OnClickListener askExitAlertNegativeListener = new View.OnClickListener() {
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
            resetChallengeData();

            if (askExitAlertDialog != null) {
                askExitAlertDialog.dismiss();
            }
            redirectToRankBtnEvent();
//            getActivity().finish();
        }
    };


    private void resetChallengeData() {
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


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }

        if (askExitAlertDialog != null) {
            askExitAlertDialog.dismiss();
            askExitAlertDialog = null;
        }
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    private View.OnKeyListener backListener = new View.OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            callSystemTime();
            append = currentTime + "," +
                    uuidChose + "," +
                    "HealthQuestionActivity" + "," +
                    "BtnBackAskDialog" + "," +
                    "btnBack" + "\n";
            transactionLogSave(append);
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    askExitAlertDialog.show();
                    return true;
                }
            }
            return false;
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
