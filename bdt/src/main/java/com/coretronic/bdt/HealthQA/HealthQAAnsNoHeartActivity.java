package com.coretronic.bdt.HealthQA;

import android.app.*;
import android.content.Context;
import android.content.DialogInterface;
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
public class HealthQAAnsNoHeartActivity extends Fragment {
    private final String TAG = HealthQAAnsNoHeartActivity.class.getSimpleName();
    private Context mContext;

    // action bar
    private TextView actionBarTitle = null;
    private View rightLine = null;
    private View leftLine = null;
    private Button btnPopMenu = null;
    private Button actBarBackBtn = null;

    private ProgressDialog progressDialog = null;
    private AlertDialog alert = null;

    private ImageView ansNoHeartPlayHeart1 = null;
    private ImageView ansNoHeartPlayHeart2 = null;
    private ImageView ansNoHeartPlayHeart3 = null;

    private ImageView noLifeBestPlayLife1 = null;
    private ImageView noLifeBestPlayLife2 = null;
    private ImageView noLifeBestPlayLife3 = null;

    private Button noLifeQABackRankBtn = null;
    private LinearLayout noHeartLL = null;
    private LinearLayout noHeartBestLL = null;
    private TextView noLifeTodayGrade = null;
    private TextView noLifeBestGrade = null;

    private ScrollView ansDetailLL = null;
    private SharedPreferences sharedPreferences;
    private AsyncHttpClient asyncHttpClient;

    private BitmapDrawable noHeartDrawable;

    // content
    private FragmentManager fragmentManager = null;
    private FragmentTransaction fragmentTransaction = null;
    private Fragment fragment = null;
    private String challengeId = "";
    private String uuidChose = "";
    private String todayScore = "";
    private String bestScore = "";
    private int heartCount = 0;
    private QuestionInfo questionInfo;
    private Gson gson = new Gson();
    //transactionLog
    private String currentTime;
    private String append;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getActivity().getSharedPreferences(AppConfig.SHAREDPREFERENCES_NAME, 0);
        asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.setTimeout(AppConfig.TIMEOUT);

        Bundle bundle = this.getArguments();
        todayScore = bundle.getString("todayScore");
        bestScore = bundle.getString("bestScore");
        heartCount = Integer.valueOf(bundle.getString("heartCount"));
        Log.i(TAG, "todayScore:" + todayScore);
        Log.i(TAG, "bestScore:" + bestScore);
        Log.i(TAG, "heartCount:"+heartCount);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.health_qa_no_heart, container, false);
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(backListener);
        Log.i(TAG, "view:" + view);
        mContext = view.getContext();
        progressDialog = new ProgressDialog(mContext);


        initView(view);

        if (Integer.valueOf(todayScore) >= Integer.valueOf(bestScore))
        {
            noHeartLL.setVisibility(View.GONE);
            noHeartBestLL.setVisibility(View.VISIBLE);
            noLifeTodayGrade.setTextColor(mContext.getResources().getColor(R.color.orange));

        }
        else
        {
            noHeartLL.setVisibility(View.VISIBLE);
            noHeartBestLL.setVisibility(View.GONE);
            noLifeTodayGrade.setTextColor(mContext.getResources().getColor(R.color.text_dark_blackbrown));

        }
//        AppUtils.setMultiHeartBitmap(mContext, heartCount, ansNoHeartPlayHeart1, ansNoHeartPlayHeart2, ansNoHeartPlayHeart3);
        Log.i(TAG,"heartCount:"+heartCount);
        noLifeTodayGrade.setText(getString(R.string.today_score) + getString(R.string.prefix_score) + todayScore);
        noLifeBestGrade.setText(getString(R.string.best_score) + getString(R.string.prefix_score) + bestScore);

        return view;
    }


    private void initView(View view) {
        actionBarTitle = (TextView) view.findViewById(R.id.action_bar_title);
        rightLine = (View) view.findViewById(R.id.right_line);
        leftLine = (View) view.findViewById(R.id.left_line);
        btnPopMenu = (Button) view.findViewById(R.id.btnPopMenu);
        actBarBackBtn = (Button) view.findViewById(R.id.btnBack);

        noLifeQABackRankBtn = (Button) view.findViewById(R.id.noLifeQABackRankBtn);

        noHeartLL = (LinearLayout) view.findViewById(R.id.no_heart_ll);
        noHeartBestLL = (LinearLayout) view.findViewById(R.id.no_heart_best_ll);

        noLifeTodayGrade = (TextView) view.findViewById(R.id.noLifeTodayGrade);
        noLifeBestGrade = (TextView) view.findViewById(R.id.noLifeBestGrade);

        ansNoHeartPlayHeart1 = (ImageView) view.findViewById(R.id.noLifePlayLife1);
        ansNoHeartPlayHeart2 = (ImageView) view.findViewById(R.id.noLifePlayLife2);
        ansNoHeartPlayHeart3 = (ImageView) view.findViewById(R.id.noLifePlayLife3);

        noLifeBestPlayLife1 = (ImageView) view.findViewById(R.id.noLifeBestPlayLife1);
        noLifeBestPlayLife2 = (ImageView) view.findViewById(R.id.noLifeBestPlayLife2);
        noLifeBestPlayLife3 = (ImageView) view.findViewById(R.id.noLifeBestPlayLife3);


        noHeartDrawable = new BitmapDrawable(getResources(), getResources().openRawResource(R.drawable.play_life_out));

        if (android.os.Build.VERSION.SDK_INT < 16) {
            ansNoHeartPlayHeart1.setImageDrawable(noHeartDrawable);
            ansNoHeartPlayHeart2.setImageDrawable(noHeartDrawable);
            ansNoHeartPlayHeart3.setImageDrawable(noHeartDrawable);

            noLifeBestPlayLife1.setImageDrawable(noHeartDrawable);
            noLifeBestPlayLife2.setImageDrawable(noHeartDrawable);
            noLifeBestPlayLife3.setImageDrawable(noHeartDrawable);

        } else {
            ansNoHeartPlayHeart1.setImageDrawable(noHeartDrawable);
            ansNoHeartPlayHeart2.setImageDrawable(noHeartDrawable);
            ansNoHeartPlayHeart3.setImageDrawable(noHeartDrawable);

            noLifeBestPlayLife1.setImageDrawable(noHeartDrawable);
            noLifeBestPlayLife2.setImageDrawable(noHeartDrawable);
            noLifeBestPlayLife3.setImageDrawable(noHeartDrawable);
        }

        // action bar ui set
        actionBarTitle.setText(getString(R.string.qa_title));
        rightLine.setVisibility(View.GONE);
        leftLine.setVisibility(View.GONE);
        actBarBackBtn.setVisibility(View.GONE);
        btnPopMenu.setVisibility(View.GONE);

        noLifeQABackRankBtn.setOnClickListener(btnListener);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();


        if (null != noHeartDrawable && !noHeartDrawable.getBitmap().isRecycled()) {
            Log.i(TAG, "====correctIVDrawable===");
            noHeartDrawable.getBitmap().recycle();
            noHeartDrawable = null;
        }

    }
        private View.OnClickListener btnListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btnBack:
//                    finish();
                    break;
                case R.id.noLifeQABackRankBtn:
                    callSystemTime();
                    append = currentTime + "," +
                            uuidChose + "," +
                            "HealthQAAnsNoHeartActivity" + "," +
                            "HealthQAMainActivity" + "," +
                            "btnBackRank" + "\n";
                    transactionLogSave(append);
                    redirectToRankBtnEvent();
                    break;
            }
        }
    };

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


    private View.OnKeyListener backListener = new View.OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    redirectToRankBtnEvent();
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

    private DialogInterface.OnClickListener erroAlertListener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int id) {
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
