package com.coretronic.bdt.MessageWall;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.coretronic.bdt.*;
import com.coretronic.bdt.DataModule.WallDairyInfo;
import com.coretronic.bdt.MessageWall.Module.GoodInfo;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import org.apache.http.Header;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by james on 14/12/15.
 */
public class WallDiaryDetail extends Fragment {

    // content
    private Context mContext = null;
    private SharedPreferences sharedPreferences;
    private final String TAG = WallDiaryDetail.class.getSimpleName();
    private String UUIDChose = "";
    private String articleType = "";
    private String refId = "";
    private String sourceFrom = "";
    private String userName = "";
    private String userThumb = "";

    private ImageLoader imageLoader1 = ImageLoader.getInstance();
    private ImageLoader imageLoader2 = ImageLoader.getInstance();
    private ImageLoader imageLoader3 = ImageLoader.getInstance();
    private ImageLoader imageLoader4 = ImageLoader.getInstance();
    private ImageLoader imageLoader5 = ImageLoader.getInstance();

    // action bar
//    private Button btnBack;
//    private TextView barTitle;
//    private TextView actionBarTitle = null;
    //    private View rightLine = null;
//    private Button btnPopMenu = null;
    private Button actBarBackBtn = null;

    // content ui
    private LinearLayout missionLL1 = null;
    private LinearLayout missionLL2 = null;
    private LinearLayout missionLL3 = null;
    private LinearLayout missionLL4 = null;
    private LinearLayout missionLL5 = null;
    private LinearLayout weatherLL = null;
    private LinearLayout walkNumLL = null;
    private LinearLayout titleLL = null;
    private LinearLayout walkDateLL = null;
    private RelativeLayout whofellGoodLL = null;
//    private LinearLayout otherFeelGoodLL = null;

    private TextView goodNameTV1 = null;
    private TextView walk_unit = null;
    //    private TextView goodNameTV2 = null;
    private TextView goodNameNum = null;
    //    private TextView punctuationTV = null;
//    private TextView nameTV2 = null;
    private TextView walkwayDate = null;
    private TextView weatherDescript = null;
    private TextView walkNum = null;
    private TextView walkwayTitle = null;
    private TextView missionComment1 = null;
    private TextView missionComment2 = null;
    private TextView missionComment3 = null;
    private TextView missionComment4 = null;
    private TextView missionComment5 = null;

    private ImageView goodArticleIV = null;

    private ImageView missionPhoto1 = null;
    private ImageView missionPhoto2 = null;
    private ImageView missionPhoto3 = null;
    private ImageView missionPhoto4 = null;
    private ImageView missionPhoto5 = null;

    // sever variable
    private ProgressDialog dialog = null;
    private RequestParams params = null;
    private AsyncHttpClient asyncHttpClient = null;
    private Gson gson = new Gson();
    private boolean currentGoodArticlesState = false;

    // modle
    private WallDairyInfo wallDairyInfo = null;

    public JsonHttpResponseHandler jsonWallDataHandler = new JsonHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            Log.d(TAG, "onSuccess = " + response);
            try {

                wallDairyInfo = gson.fromJson(response.toString(), WallDairyInfo.class);
                if (wallDairyInfo.getMsgCode().equals(AppConfig.SUCCESS_CODE)) {

                    setDiaryContent();
//
                    if (Integer.valueOf(wallDairyInfo.getResult().getGood().size()) >= 2) {
                        setFeelGoodManContent(wallDairyInfo.getResult().getGood().get(0).getName(),
                                wallDairyInfo.getResult().getGood().get(1).getName(),
                                wallDairyInfo.getResult().getTotalGoodNum());
                    } else if (Integer.valueOf(wallDairyInfo.getResult().getGood().size()) == 1) {
                        setFeelGoodManContent(wallDairyInfo.getResult().getGood().get(0).getName(),
                                "",
                                wallDairyInfo.getResult().getTotalGoodNum());
                    } else if (Integer.valueOf(wallDairyInfo.getResult().getGood().size()) == 0) {
                        setFeelGoodManContent("",
                                "",
                                wallDairyInfo.getResult().getTotalGoodNum());
                    }
                    Log.i(TAG,"currentGoodArticlesState wall:"+currentGoodArticlesState);
                } else {
                    Log.i(TAG, "e0:" + wallDairyInfo.getMsgCode());
                    AppUtils.getAlertDialog(mContext, getString(R.string.data_result_error)).show();
                }

            } catch (Exception e) {
                Log.i(TAG, "e:" + e.getMessage());
                AppUtils.getAlertDialog(mContext, getString(R.string.data_result_error)).show();
            }

            if (dialog != null) {
                dialog.dismiss();
            }

        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            Log.d(TAG, "onFailure");
            if (dialog != null) {
                dialog.dismiss();
            }
            try {
                AppUtils.getAlertDialog(mContext, getString(R.string.data_load_error)).show();
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            super.onFailure(statusCode, headers, responseString, throwable);
            Log.i(TAG, "444");
            Log.i(TAG, "responseString:" + responseString);
            if (dialog != null) {
                dialog.dismiss();
            }
            AppUtils.getAlertDialog(mContext, getString(R.string.returndata_error)).show();
        }


    };


    public JsonHttpResponseHandler setGoodDataHandler = new JsonHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            Log.d(TAG, "onSuccess = " + response);
            try {

                if (wallDairyInfo.getMsgCode().equals(AppConfig.SUCCESS_CODE)) {

                    GoodInfo goodInfo = gson.fromJson(response.toString(), GoodInfo.class);
                    Log.i(TAG,"11111");

                    if (Integer.valueOf(goodInfo.getResult().getGood().size()) >= 2) {
                        Log.i(TAG,"2222");
                        setFeelGoodManContent(goodInfo.getResult().getGood().get(0).getName(),
                                goodInfo.getResult().getGood().get(1).getName(),
                                String.valueOf(goodInfo.getResult().getTotalGoodNum()));

                    } else if (Integer.valueOf(goodInfo.getResult().getGood().size()) == 1) {
                        Log.i(TAG,"33333");
                        setFeelGoodManContent(goodInfo.getResult().getGood().get(0).getName(),
                                "",
                                String.valueOf(goodInfo.getResult().getTotalGoodNum()));

                    } else if (Integer.valueOf(goodInfo.getResult().getGood().size()) == 0) {
                        Log.i(TAG, "4444");
                        setFeelGoodManContent("",
                                "",
                                String.valueOf(goodInfo.getResult().getTotalGoodNum()));
                    }
//                    if( currentGoodArticlesState == false)
//                    {
//                        currentGoodArticlesState = true;
//                    }
//                    else
//                    {
//                        currentGoodArticlesState = false;
//                    }
//                    AppUtils.getAlertDialog(mContext, getString(R.string.data_result_error)).show();
                } else {
                    AppUtils.getAlertDialog(mContext, getString(R.string.data_result_error)).show();
                }

            } catch (Exception e) {
                Log.i(TAG, "e:" + e.getMessage());
                AppUtils.getAlertDialog(mContext, getString(R.string.data_result_error)).show();
            }

            if (dialog != null) {
                dialog.dismiss();
            }

        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            Log.d(TAG, "onFailure");
            if (dialog != null) {
                dialog.dismiss();
            }
            try {
                AppUtils.getAlertDialog(mContext, getString(R.string.data_load_error)).show();
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            super.onFailure(statusCode, headers, responseString, throwable);
            Log.i(TAG, "444");
            Log.i(TAG, "responseString:" + responseString);
            if (dialog != null) {
                dialog.dismiss();
            }
            AppUtils.getAlertDialog(mContext, getString(R.string.returndata_error)).show();
        }


    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.diary_wall_detail);
        sharedPreferences = getActivity().getSharedPreferences(AppConfig.SHAREDPREFERENCES_NAME, 0);
        UUIDChose = sharedPreferences.getString(AppConfig.PREF_UNIQUE_ID, "");


//        Intent intent = this.getIntent();
//        Bundle bundle = intent.getExtras();
//        refId = bundle.getString("refId","");


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.diary_wall_detail, container, false);
        mContext = v.getContext();
        dialog = new ProgressDialog(mContext);
        initView(v);
        Bundle bundle = getArguments();
        if (bundle != null) {
            Log.i(TAG, "Wall diary detail bundle: " + bundle);
            refId = bundle.getString("refId");
            articleType = bundle.getString("articleType");
            sourceFrom = bundle.getString("sourceFrom");
            userName = bundle.getString("userName");
            userThumb = bundle.getString("userThumb");
            Log.i(TAG, "Wall diary detail bundle refId:" + refId);
            Log.i(TAG, "Wall diary detail bundle articleType:" + articleType);
            Log.i(TAG, "Wall diary detail bundle sourceFrom:" + sourceFrom);
            Log.i(TAG, "Wall diary detail bundle userName:" + userName);
            Log.i(TAG, "Wall diary detail bundle userThumb:" + userThumb);
            requestWallData();
        }
        return v;
    }

    private void initView(View v) {
//        btnBack = (Button) v.findViewById(R.id.btnBack);
//        barTitle = (TextView) v.findViewById(R.id.action_bar_title);
//
//        barTitle.setText(getString(R.string.lb_ww_title));
//        btnBack.setOnClickListener(btnListener);
//

        walkwayDate = (TextView) v.findViewById(R.id.walkwayDate);
        weatherDescript = (TextView) v.findViewById(R.id.weatherDescript);
        walkNum = (TextView) v.findViewById(R.id.walkNum);
        walkwayTitle = (TextView) v.findViewById(R.id.walkwayTitle);
        walk_unit = (TextView) v.findViewById(R.id.walk_unit);

        weatherLL = (LinearLayout) v.findViewById(R.id.weatherLL);
        walkNumLL = (LinearLayout) v.findViewById(R.id.walkNumLL);
        titleLL = (LinearLayout) v.findViewById(R.id.titleLL);
        walkDateLL = (LinearLayout) v.findViewById(R.id.walkDateLL);
        whofellGoodLL = (RelativeLayout) v.findViewById(R.id.whofellGoodLL);

        missionLL1 = (LinearLayout) v.findViewById(R.id.missionLL1);
        missionLL2 = (LinearLayout) v.findViewById(R.id.missionLL2);
        missionLL3 = (LinearLayout) v.findViewById(R.id.missionLL3);
        missionLL4 = (LinearLayout) v.findViewById(R.id.missionLL4);
        missionLL5 = (LinearLayout) v.findViewById(R.id.missionLL5);

//        otherFeelGoodLL = (LinearLayout) findViewById(R.id.otherFeelGoodLL);
//        punctuationTV = (TextView) findViewById(R.id.punctuationTV);
//        nameTV2 = (TextView) findViewById(R.id.nameTV2);

        missionComment1 = (TextView) v.findViewById(R.id.missionComment1);
        missionComment2 = (TextView) v.findViewById(R.id.missionComment2);
        missionComment3 = (TextView) v.findViewById(R.id.missionComment3);
        missionComment4 = (TextView) v.findViewById(R.id.missionComment4);
        missionComment5 = (TextView) v.findViewById(R.id.missionComment5);

        goodNameTV1 = (TextView) v.findViewById(R.id.goodNameTV1);
//        goodNameTV2 = (TextView) findViewById(R.id.goodNameTV2);
//        goodNameNum = (TextView) findViewById(R.id.goodNameNum);

        goodArticleIV = (ImageView) v.findViewById(R.id.goodArticleIV);

        missionPhoto1 = (ImageView) v.findViewById(R.id.missionPhoto1);
        missionPhoto2 = (ImageView) v.findViewById(R.id.missionPhoto2);
        missionPhoto3 = (ImageView) v.findViewById(R.id.missionPhoto3);
        missionPhoto4 = (ImageView) v.findViewById(R.id.missionPhoto4);
        missionPhoto5 = (ImageView) v.findViewById(R.id.missionPhoto5);


        // action bar ui set
//        actionBarTitle.setText(getActivity().getString(R.string.pp_wall));
//        rightLine.setVisibility(View.GONE);
//        btnPopMenu.setVisibility(View.GONE);


        //click listener event
        whofellGoodLL.setOnClickListener(btnListener);
        goodArticleIV.setOnClickListener(btnListener);
//        actBarBackBtn.setOnClickListener(btnListener);
        goodNameTV1.setOnClickListener(btnListener);
    }

    private View.OnClickListener btnListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent();
            switch (view.getId()) {
                case R.id.whofellGoodLL:
                    Log.d(TAG, "whofellGoodLL");
                    break;
                case R.id.goodArticleIV:
                    Log.d(TAG, "goodArticleIV");
                    insertGoodData();
                    break;
                case R.id.goodNameTV1:
                    Bundle bundle = new Bundle();
                    bundle.putString("refId",refId);
                    bundle.putString("articleType",articleType);
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
            }
            startActivity(intent);
            getActivity().finish();
        }
    }

    private void requestWallData() {
        dialog = AppUtils.customProgressDialog(mContext, null, getString(R.string.dialog_read_msg));
        dialog.show();
        final String url = AppConfig.DOMAIN_SITE_PATE + AppConfig.REQUEST_COMMUNITY_DETAIL;
        params = new RequestParams();
        params.add("uid", sharedPreferences.getString(AppConfig.PREF_UNIQUE_ID, ""));
//        params.add("uid", "U0000000000000000020");
//        params.add("uid", "uuid-12345");
        params.add("time", AppUtils.getChineseSystemTime());
        params.add("refId", refId);
        params.add("articleType", articleType);
        Log.i(TAG, "requestWallData params:" + params);
        asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.setTimeout(AppConfig.TIMEOUT);
        asyncHttpClient.post(url, params, jsonWallDataHandler);
    }


    private void insertGoodData() {
        dialog = AppUtils.customProgressDialog(mContext, null, getString(R.string.dialog_read_msg));
        dialog.show();
        String url = "";
        Log.i(TAG,"currentGoodArticlesState now:"+currentGoodArticlesState);
        if (currentGoodArticlesState == false) {
            url = AppConfig.DOMAIN_SITE_PATE + AppConfig.INSERT_GOOD;
        } else if(currentGoodArticlesState == true){
            url = AppConfig.DOMAIN_SITE_PATE + AppConfig.UPDATE_RECOVER_GOOD;
        }

        params = new RequestParams();
        params.add("uid", sharedPreferences.getString(AppConfig.PREF_UNIQUE_ID, ""));
//        params.add("uid", "U0000000000000000020");
//        params.add("uid", "uuid-12345");
        params.add("time", AppUtils.getSystemTime());
//        params.add("refId", "d000002");
        params.add("refId", refId);
        Log.i(TAG, "params:" + params);
        asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.setTimeout(AppConfig.TIMEOUT);
        asyncHttpClient.post(url, params, setGoodDataHandler);
    }

    private void setFeelGoodManContent(String name1, String name2, String totalNum) {


        try {

            if( name1.equals("自己"))
            {
                currentGoodArticlesState = true;
            }
            else
            {
                currentGoodArticlesState = false;
            }
            setGoodArticleBtnState();

            Log.i(TAG,"11111-1");
            if (Integer.valueOf(totalNum) >= 2) {
                Log.i(TAG,"11111-2");
                goodNameTV1.setText(name1 +
                                getString(R.string.punctuation) +
                                name2 +
                                getString(R.string.other) +
                                totalNum +
                                getString(R.string.comment_person) +
                                getString(R.string.feel_good_articles)
                );
            } else if (Integer.valueOf(totalNum) == 1) {
                Log.i(TAG,"11111-3");
                goodNameTV1.setText(
                        getString(R.string.just)+

                        name1 +" "+
                                getString(R.string.total)+
                                totalNum +
                                getString(R.string.comment_person) +
                                getString(R.string.feel_good_articles)
                );
            } else if (Integer.valueOf(totalNum) == 0) {
                Log.i(TAG,"11111-4");
                goodNameTV1.setText(
                        totalNum +
                                getString(R.string.comment_person) +
                                getString(R.string.feel_good_articles)
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "setFeelGoodManContent e:" + e.getMessage());
        }
    }


    private void setGoodArticleBtnState() {
        Log.i(TAG,"currentGoodArticlesState:"+currentGoodArticlesState);
        // 未按讚
        if (currentGoodArticlesState == true) {
            Log.i(TAG,"currentGoodArticlesState == true");
            goodArticleIV.setImageDrawable(null);
            goodArticleIV.setImageDrawable(getResources().getDrawable(R.drawable.ic_good_article_press));
//            goodArticleIV.setImageBitmap(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_good_article_press));

        } else if (currentGoodArticlesState == false) {
            Log.i(TAG,"currentGoodArticlesState == false");
            goodArticleIV.setImageDrawable(null);
            goodArticleIV.setImageDrawable(getResources().getDrawable(R.drawable.ic_good_article));
//            goodArticleIV.setImageBitmap(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_good_article));
        }
    }

    private void setDiaryContent() {
        if (wallDairyInfo != null) {

            // --- set walkway info ---
            if (wallDairyInfo.getResult().getPhotoDate().equals("")) {
                walkDateLL.setVisibility(View.GONE);
            } else {
                walkDateLL.setVisibility(View.VISIBLE);

                String photoDate = String.valueOf(wallDairyInfo.getResult().getPhotoDate());
                Log.i(TAG, "photoDate:" + photoDate);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Calendar cal = Calendar.getInstance();
                try {
                    Date dt = sdf.parse(photoDate);
                    Log.i(TAG, "dt:" + dt);
                    sdf = new SimpleDateFormat("MM月dd日(E)");
                    walkwayDate.setText(sdf.format(dt));
                } catch (ParseException e) {
                    e.printStackTrace();
                    walkDateLL.setVisibility(View.GONE);
                }

            }


            if (wallDairyInfo.getResult().getWeather().equals("")) {
                weatherLL.setVisibility(View.GONE);
            } else {
                weatherLL.setVisibility(View.VISIBLE);
                weatherDescript.setText(String.valueOf(wallDairyInfo.getResult().getWeather()));
            }

            if (Integer.valueOf(wallDairyInfo.getResult().getSteps()) == 0) {
//                walkNumLL.setVisibility(View.GONE);

                walkNum.setText(getString(R.string.no_record));
                walk_unit.setVisibility(View.GONE);
            } else {
                walkNumLL.setVisibility(View.VISIBLE);
                walkNum.setText(String.valueOf(wallDairyInfo.getResult().getSteps()));
            }

            Log.i(TAG,"wallDairyInfo.getResult().getIsGood():"+wallDairyInfo.getResult().getIsGood());
            if (Integer.valueOf(wallDairyInfo.getResult().getIsGood()) == 0) {
                currentGoodArticlesState = false;

            } else {
                currentGoodArticlesState = true;
            }

            if (!(wallDairyInfo.getResult().getWalkwayName().equals(""))) {
                walkwayTitle.setText(getString(R.string.diary_wall_iwentto) + String.valueOf(wallDairyInfo.getResult().getWalkwayName()));
            }

            // --- set mission photo and comment ---
            if ((wallDairyInfo.getResult().getMission1Photo() == null) || (wallDairyInfo.getResult().getMission1Photo().equals(""))) {
                missionLL1.setVisibility(View.GONE);
            } else {
                Log.i(TAG, "wallDairyInfo.getResult().getMission1Photo():" + wallDairyInfo.getResult().getMission1Photo());
                Log.i(TAG, "missionComment1:" + String.valueOf(wallDairyInfo.getResult().getMission1Comment()));
                AppUtils.loadPhoto(mContext, missionPhoto1, wallDairyInfo.getResult().getMission1Photo(), imageLoader1);
                missionComment1.setText(String.valueOf(wallDairyInfo.getResult().getMission1Comment()));
            }

            if ((wallDairyInfo.getResult().getMission2Photo() == null) || (wallDairyInfo.getResult().getMission2Photo().equals(""))) {
//                AppUtils.loadPhoto(mContext, missionPhoto2, wallDairyInfo.getResult().getMission2Photo(), imageLoader2);
                missionLL2.setVisibility(View.GONE);
            } else {
                Log.i(TAG, "wallDairyInfo.getResult().getMission2Photo():" + wallDairyInfo.getResult().getMission2Photo());
                AppUtils.loadPhoto(mContext, missionPhoto2, wallDairyInfo.getResult().getMission2Photo(), imageLoader2);
                missionComment2.setText(String.valueOf(wallDairyInfo.getResult().getMission2Comment()));
            }
//
            if ((wallDairyInfo.getResult().getMission3Photo() == null) || (wallDairyInfo.getResult().getMission3Photo().equals(""))) {
//                AppUtils.loadPhoto(mContext, missionPhoto3, wallDairyInfo.getResult().getMission3Photo(), imageLoader3);
                missionLL3.setVisibility(View.GONE);
            } else {
                AppUtils.loadPhoto(mContext, missionPhoto3, wallDairyInfo.getResult().getMission3Photo(), imageLoader3);
                missionComment3.setText(String.valueOf(wallDairyInfo.getResult().getMission3Comment()));
            }


            if ((wallDairyInfo.getResult().getMission4Photo() == null) || (wallDairyInfo.getResult().getMission4Photo().equals(""))) {
//                AppUtils.loadPhoto(mContext, missionPhoto4, wallDairyInfo.getResult().getMission4Photo(), imageLoader4);
                missionLL4.setVisibility(View.GONE);
            } else {
                AppUtils.loadPhoto(mContext, missionPhoto4, wallDairyInfo.getResult().getMission4Photo(), imageLoader4);
                missionComment4.setText(String.valueOf(wallDairyInfo.getResult().getMission4Comment()));
            }

            if ((wallDairyInfo.getResult().getMission5Photo() == null) || (wallDairyInfo.getResult().getMission5Photo().equals(""))) {
//                AppUtils.loadPhoto(mContext, missionPhoto5, wallDairyInfo.getResult().getMission5Photo(), imageLoader5);
                missionLL5.setVisibility(View.GONE);
            } else {
                AppUtils.loadPhoto(mContext, missionPhoto5, wallDairyInfo.getResult().getMission5Photo(), imageLoader5);
                missionComment5.setText(String.valueOf(wallDairyInfo.getResult().getMission5Comment()));
            }
        }
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        imageLoader1.destroy();
//        imageLoader2.destroy();
//        imageLoader3.destroy();
//        imageLoader4.destroy();
//        imageLoader5.destroy();
//        if (wallDairyInfo.getResult().getMission1Photo() != null) {
//            imageLoader.cancelDisplayTask(missionPhoto1);
//        }
//
//        if (wallDairyInfo.getResult().getMission2Photo() != null) {
//            imageLoader.cancelDisplayTask(missionPhoto2);
//        }
//
//        if (wallDairyInfo.getResult().getMission3Photo() != null) {
//            imageLoader.cancelDisplayTask(missionPhoto3);
//        }
//
//        if (wallDairyInfo.getResult().getMission4Photo() != null) {
//            imageLoader.cancelDisplayTask(missionPhoto4);
//        }
//
//        if (wallDairyInfo.getResult().getMission5Photo() != null) {
//            imageLoader.cancelDisplayTask(missionPhoto5);
//        }

    }
}
