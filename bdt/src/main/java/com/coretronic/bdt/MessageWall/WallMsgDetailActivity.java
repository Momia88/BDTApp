package com.coretronic.bdt.MessageWall;
/**
 *  我要找步道
 */

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import com.coretronic.bdt.NewArticlesListActivity;
import com.coretronic.bdt.NewMessageListActivity;
import com.coretronic.bdt.Person.NotifyListActivity;
import com.coretronic.bdt.R;
import android.app.Fragment;

/**
 * Created by Morris on 2014/10/28.
 */
public class WallMsgDetailActivity extends Activity {
    private String TAG = WallMsgDetailActivity.class.getSimpleName();
    private LinearLayout wallLayout;
    private BitmapDrawable bgDrawable;
    private Button btnBack;
    private TextView barTitle;
    private Drawable drawable;
    private ScrollView scrollView;
    private int CAMERA_REQUEST = 1;
    // get bundle data
    private String refId = "";
    private String articleType = "";
    private String sourceFrom = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wall_detail_layout);
        Bundle bundle = null;
        if (getIntent() != null) {
            bundle = getIntent().getExtras();
            Log.i(TAG, "bundle: " + bundle);
            if (bundle != null) {
                refId = bundle.getString("refId");
                articleType = bundle.getString("articleType");
                sourceFrom = bundle.getString("sourceFrom");
            }
        }

        btnBack = (Button) findViewById(R.id.btnBack);
        barTitle = (TextView) findViewById(R.id.action_bar_title);
        btnBack.setOnClickListener(btnListener);

        Fragment detailFragment = null;
        Fragment friendFragment = new WallFriendMsg();
        if (articleType.equals("daily")) {
            Log.i(TAG, "articleType diary");
            detailFragment = new WallDiaryDetail();
        } else {
            detailFragment = new WallMsgDetail();
        }

        if (detailFragment != null && friendFragment != null) {
            detailFragment.setArguments(bundle);
            friendFragment.setArguments(bundle);
            getFragmentManager().beginTransaction()
                    .replace(R.id.frame_detail_container, detailFragment, "WallMsgDetail")
                    .replace(R.id.frame_msg_container, friendFragment, "FriendMsg")
                    .commit();
        } else {
            Log.e(TAG, "Error in creating fragment");
        }
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        scrollView.smoothScrollTo(0, 0);
    }


    View.OnClickListener btnListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btnBack:
                    backEvent();
                    break;
            }

        }
    };


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            backEvent();
        }
        return false;
    }

    private void backEvent() {
        try {
            if (!(sourceFrom.equals(""))) {
                Intent intent = new Intent();
                // back to main New message list activity
                if (sourceFrom.equals("NewArticlesListActivity")) {
                    intent.setClass(this, NewArticlesListActivity.class);
                } else if (sourceFrom.equals("NewMessageListActivity")) {
                    intent.setClass(this, NewMessageListActivity.class);
                } else if (sourceFrom.equals(WallActivity.class.getSimpleName())) {
                    intent.setClass(this, WallActivity.class);
                } else if (sourceFrom.equals(NotifyListActivity.class.getSimpleName())) {
                    intent.setClass(this, NotifyListActivity.class);
                }else{
                    finish();
                    return;
                }
                startActivity(intent);
                finish();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}