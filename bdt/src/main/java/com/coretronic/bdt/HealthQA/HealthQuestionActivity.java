package com.coretronic.bdt.HealthQA;

import android.app.AlertDialog;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.coretronic.bdt.AppConfig;
import com.coretronic.bdt.AppUtils;
import com.coretronic.bdt.DataModule.HealthNewsQuesInfo;
import com.coretronic.bdt.HealthKnowledge.HealthArticleFinder;
import com.coretronic.bdt.R;
import com.coretronic.bdt.Utility.CustomerTwoBtnAlertDialog;
import com.coretronic.bdt.WalkWay.Module.ToolsPopupWindow;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by changyuanyu on 14/10/3.
 */
public class HealthQuestionActivity extends Fragment {
    final private String url = AppConfig.DOMAIN_SITE_PATE + AppConfig.REQUEST_RECOMMEND_QUERY_DOCTOR;
    public Button button_next;
    private PopupWindow menuPopupWindow;
    private TextView barTitle;
    private Button btnPopMenu;
    private Button btnBack;
    private Button ansBtn1;
    private Button ansBtn2;
    private Button ansBtn3;
    private View rightLine;
    private TextView matchData;
    private TextView questionTV;
    private TextView healthAnsBtn1;
    private TextView healthAnsBtn2;
    private TextView healthAnsBtn3;
    private int ans = 0;
    private Context mContext;
    private RelativeLayout navigationBar;
    private String TAG = HealthArticleFinder.class.getSimpleName();
    private SharedPreferences sharedPreferences;
    private View view = null;
    private FragmentManager fragmentManager = null;
    private FragmentTransaction fragmentTransaction = null;
    private Fragment fragment = null;

    private String challengeId = "";
    private ProgressDialog dialog = null;
    private RequestParams params = null;
    private AsyncHttpClient asyncHttpClient = null;
    private Gson gson = new Gson();
    private int visibleLast = 0;
    private HealthNewsQuesInfo healthNewsQuesInfo = null;
    private String getQuestionId = "";
    private CustomerTwoBtnAlertDialog askExitQuesAlert = null;
    private CustomerTwoBtnAlertDialog netWorkDisconnectAlertAlert = null;
    private AlertDialog.Builder alertDialog = null;

    private ImageView titleIV = null;
    private BitmapDrawable titleDrawable;

    //transactionLog
    private String currentTime;
    private String append;
    private String uuidChose;
    public JsonHttpResponseHandler jsonHandler = new JsonHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            Log.d(TAG, "onSuccess = " + response);
            try {
                healthNewsQuesInfo = gson.fromJson(response.toString(), HealthNewsQuesInfo.class);

                if (healthNewsQuesInfo.getMsgCode().equals(AppConfig.SUCCESS_CODE)) {
                    getQuestionId = healthNewsQuesInfo.getResult().getQuestionId();

                    ans = Integer.parseInt(healthNewsQuesInfo.getResult().getAnswer());
                    questionTV.setText(AppUtils.ToDBC(healthNewsQuesInfo.getResult().getQuestion()));
                    ansBtn1.setText(healthNewsQuesInfo.getResult().getItem1());
                    ansBtn2.setText(healthNewsQuesInfo.getResult().getItem2());
                    ansBtn3.setText(healthNewsQuesInfo.getResult().getItem3());
                } else {
//                    AppUtils.getAlertDialog(mContext, getString(R.string.data_result_error)).show();
                    netWorkDisconnectAlertAlert.show();
                }

            } catch (Exception e) {
//                AppUtils.getAlertDialog(mContext, getString(R.string.data_result_error)).show();
                netWorkDisconnectAlertAlert.show();
            }

            if (dialog != null) {
                dialog.dismiss();
            }

            if( netWorkDisconnectAlertAlert != null )
            {
                netWorkDisconnectAlertAlert.dismiss();
            }

        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            Log.d(TAG, "onFailure");
            if (dialog != null) {
                dialog.dismiss();
            }
            try {
//                AppUtils.getAlertDialog(mContext, getString(R.string.data_load_error)).show();
                netWorkDisconnectAlertAlert.show();
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
        }


    };

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getActivity().getSharedPreferences(AppConfig.SHAREDPREFERENCES_NAME, 0);
        challengeId = sharedPreferences.getString(AppConfig.PREF_CHALLENGE_ID, "");
        uuidChose = sharedPreferences.getString(AppConfig.PREF_UNIQUE_ID, "");


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.qa_question, container, false);
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(backListener);
        mContext = view.getContext();
        menuPopupWindow = new ToolsPopupWindow(mContext, null);


        askExitQuesAlert = AppUtils.getAlertDialog(mContext,
                getResources().getString(R.string.qa_exit_alert_text),
                getResources().getString(R.string.cancel),
                getString(R.string.sure_exit),
                askExitQuesAlertPositineveListener,
                askExitQuesAlertNegativeListener);

        netWorkDisconnectAlertAlert = AppUtils.getAlertDialog(mContext,
                getResources().getString(R.string.get_data_error),
                getResources().getString(R.string.retry),
                getString(R.string.sure_exit),
                netWorkDisconnectAlertPositineveListener,
                netWorkDisconnectAlertNegativeListener);
        netWorkDisconnectAlertAlert.setCancelable(false);
        netWorkDisconnectAlertAlert.setCanceledOnTouchOutside(false);

        initView(view);

        if (android.os.Build.VERSION.SDK_INT < 16) {
            titleIV.setBackgroundDrawable(titleDrawable);
        } else {
            titleIV.setBackground(titleDrawable);
        }


        try {
            barTitle.setText(R.string.qa_title);
            btnPopMenu.setVisibility(View.GONE);
            rightLine.setVisibility(View.GONE);

            requestQuestion();
        } catch (Exception e) {
            netWorkDisconnectAlertAlert.show();
        }

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i(TAG,"====on Destroy View");



        if (null != titleDrawable && !titleDrawable.getBitmap().isRecycled()) {
            Log.i(TAG,"====on Destroy View recycle");
            titleDrawable.getBitmap().recycle();
            titleDrawable = null;
        }

        if( params != null )
        {
            params = null;
        }

        if( asyncHttpClient != null )
        {
            asyncHttpClient = null;
        }

        if( menuPopupWindow != null )
        {
            menuPopupWindow = null;
        }

        if( view != null )
        {
            view = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (netWorkDisconnectAlertAlert != null) {
            netWorkDisconnectAlertAlert.dismiss();
            netWorkDisconnectAlertAlert = null;
        }
    }

    private void requestQuestion() {
        dialog = AppUtils.customProgressDialog(mContext, null, getString(R.string.dialog_read_msg));
        dialog.show();
        final String url = AppConfig.DOMAIN_SITE_PATE + AppConfig.REQUEST_QUESTION;
        params = new RequestParams();
        params.add("uid", sharedPreferences.getString(AppConfig.PREF_UNIQUE_ID, ""));
//        params.add("uid", "uuid-12345");
        params.add("time", AppUtils.getChineseSystemTime());
        params.add("challengeId", challengeId);
        Log.i(TAG, "params:" + params);
        asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.setTimeout(AppConfig.TIMEOUT);
        asyncHttpClient.post(url, params, jsonHandler);
    }


    private void initView(View view) {
        // navigation bar
        navigationBar = (RelativeLayout) view.findViewById(R.id.navigationBar_option);
        btnBack = (Button) view.findViewById(R.id.btnBack);
        btnPopMenu = (Button) view.findViewById(R.id.btnPopMenu);
        ansBtn1 = (Button) view.findViewById(R.id.healthAnsBtn1);
        ansBtn1.setOnClickListener(btnListener);
        ansBtn2 = (Button) view.findViewById(R.id.healthAnsBtn2);
        ansBtn2.setOnClickListener(btnListener);
        ansBtn3 = (Button) view.findViewById(R.id.healthAnsBtn3);
        ansBtn3.setOnClickListener(btnListener);
        btnBack.setOnClickListener(btnListener);
        btnPopMenu.setOnClickListener(btnListener);
        barTitle = (TextView) view.findViewById(R.id.action_bar_title);
        rightLine = (View) view.findViewById(R.id.right_line);
        titleIV = (ImageView) view.findViewById(R.id.titleIV);

        questionTV = (TextView) view.findViewById(R.id.question);

        titleDrawable = new BitmapDrawable(getResources(), getResources().openRawResource(R.drawable.qatitlephoto));
    }


    private View.OnClickListener btnListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btnBack:
                    askExitQuesAlert.show();
                    callSystemTime();
                    append = currentTime + "," +
                            uuidChose + "," +
                            "HealthQuestionActivity" + "," +
                            "BtnBackAskDialog" + "," +
                            "btnBack" + "\n";
                    transactionLogSave(append);
                    break;
                case R.id.btnPopMenu:
                    Log.d(TAG, "btnPopMenu");
                    menuPopupWindow.showAsDropDown(navigationBar, 0, 0);
                    break;
                case R.id.healthAnsBtn1:
                    callSystemTime();
                    append = currentTime + "," +
                            uuidChose + "," +
                            "HealthQuestionActivity" +
                            "," + "questionId:" +
                            getQuestionId + "," +
                            "btnHealthAns1" + "\n";
                    transactionLogSave(append);
                    checkAnswer(view);
//                    Intent intent = new Intent(HealthQuestionActivity.this, HealthAnswerActivity.class);
//                    startActivity(intent);
//                    intent = null;
//                    finish();
                    break;
                case R.id.healthAnsBtn2:
                    callSystemTime();
                    append = currentTime + "," +
                            uuidChose + "," +
                            "HealthQuestionActivity" +
                            "," + "questionId:" +
                            getQuestionId + "," +
                            "btnHealthAns2" + "\n";
                    transactionLogSave(append);
                    checkAnswer(view);
                    break;
                case R.id.healthAnsBtn3:
                    callSystemTime();
                    append = currentTime + "," +
                            uuidChose + "," +
                            "HealthQuestionActivity" +
                            "," + "questionId:" +
                            getQuestionId + "," +
                            "btnHealthAns3" + "\n";
                    transactionLogSave(append);
                    checkAnswer(view);
                    break;
            }
        }
    };


    private void checkAnswer(View view) {
        Boolean answerTF = false;
//        Intent intent = new Intent(HealthQuestionActivity.this, HealthAnswerActivity.class);
        Bundle bundle = new Bundle();

        switch (view.getId()) {
            case R.id.healthAnsBtn1:
                if (ans == 1) {
                    answerTF = true;
                }

                break;
            case R.id.healthAnsBtn2:
                if (ans == 2) {
                    answerTF = true;
                }
                break;
            case R.id.healthAnsBtn3:
                if (ans == 3) {
                    answerTF = true;
                }
                break;
        }

        Log.i(TAG, "answerTF:" + answerTF + "/ans:" + ans + "/questionId:" + getQuestionId);
//        bundle.putBoolean("answerTF", answerTF);
        bundle.putString("questionId", getQuestionId);
//        intent.putExtras(bundle);
//        startActivity(intent);

        fragmentManager = getFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();

        // if answer is correct
        if (answerTF == true) {

            fragment = new HealthQAAnsCorrectActivity();
            fragment.setArguments(bundle);
            if (fragment != null) {
                fragmentTransaction
                        .replace(R.id.qa_frame_container, fragment, "HealthQAAnsCorrectActivity")
//                        .addToBackStack("HealthQAAnsCorrectActivity")
                        .commit();
            } else {
                Log.e(TAG, "Error in creating fragment");
            }

        }
        // answer is incorrect
        else {
            fragment = new HealthQAAnsFailActivity();
            fragment.setArguments(bundle);
            if (fragment != null) {
                fragmentTransaction
                        .replace(R.id.qa_frame_container, fragment, "HealthQAAnsFailActivity")
                        .addToBackStack("HealthQAAnsFailActivity")
                        .commit();
            } else {
                Log.e(TAG, "Error in creating fragment");
            }

        }


    }

    private View.OnKeyListener backListener = new View.OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    askExitQuesAlert.show();
                    return true;
                }
            }
            return false;
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onRsume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "onPause");
        if (menuPopupWindow != null) {
            menuPopupWindow.dismiss();
        }

        if (askExitQuesAlert != null) {
            askExitQuesAlert.dismiss();
            askExitQuesAlert = null;
        }



        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }

//        clearListData();

    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event)
//    {
//        if (keyCode == KeyEvent.KEYCODE_BACK)
//        {
////                finish();
//
//            return false;
//        }
//        return false;
//    }

    private View.OnClickListener netWorkDisconnectAlertPositineveListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            requestQuestion();

        }
    };

    private View.OnClickListener netWorkDisconnectAlertNegativeListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (askExitQuesAlert != null) {
                askExitQuesAlert.dismiss();
            }
            fragmentManager = null;
            fragmentTransaction = null;
            fragment = null;
            fragmentManager = getFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();

            fragment = new HealthQARankActivity();
            if (fragment != null) {


                fragmentTransaction
                        .replace(R.id.qa_frame_container, fragment, "HealthQARankActivity")
                        .commit();
            } else {
                Log.e(TAG, "Error in creating fragment");
            }
        }


    };

    private View.OnClickListener askExitQuesAlertPositineveListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            callSystemTime();
            append = currentTime + "," +
                    uuidChose + "," +
                    "BtnBackAskDialog" + "," +
                    "HealthQuestionActivity" + "," +
                    "btnBackAskDialogCanael"+ "\n";
            transactionLogSave(append);
            if (askExitQuesAlert != null) {
                askExitQuesAlert.dismiss();
            }

        }
    };

    private View.OnClickListener askExitQuesAlertNegativeListener = new View.OnClickListener() {
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
            if (askExitQuesAlert != null) {
                askExitQuesAlert.dismiss();
            }
            // reset challegId
            sharedPreferences.edit()
                    .putString(AppConfig.PREF_CHALLENGE_ID, "")
                    .commit();
            redirectToRankPage();

        }
    };


    private void redirectToRankPage() {
        fragmentManager = null;
        fragmentTransaction = null;
        fragment = null;
        fragmentManager = getFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();

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