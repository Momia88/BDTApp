package com.coretronic.bdt.WalkWay;
/**
 *  我要找步道
 */

import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.LinearLayout;
import com.coretronic.bdt.R;

/**
 * Created by Morris on 2014/10/28.
 */
public class WalkWayActivity extends FragmentActivity {
    private static String TAG = WalkWayActivity.class.getSimpleName();
    private LinearLayout walkwayLayout;
    private BitmapDrawable bgDrawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.walkway_main_layout);
        walkwayLayout = (LinearLayout) findViewById(R.id.walkway_layout);
        bgDrawable = new BitmapDrawable(getResources(), getResources().openRawResource(R.drawable.bg_walkway));
        bgDrawable.setAlpha(150);
        if(bgDrawable != null) {
            if (android.os.Build.VERSION.SDK_INT < 16) {
                walkwayLayout.setBackgroundDrawable(bgDrawable);
            } else {

                walkwayLayout.setBackground(bgDrawable);
            }
        }else{
            Log.e(TAG,"bgDrawable == null");
        }

        Fragment fragment = new WalkWayIndex();
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_container, fragment, "WalkWayActivity")
                    .commit();
        } else {
            Log.e(TAG, "Error in creating fragment");
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
        //recycle bg
        if (null != walkwayLayout.getBackground()) {
            walkwayLayout.getBackground().setCallback(null);
        }
        if (null != bgDrawable && !bgDrawable.getBitmap().isRecycled()) {
            bgDrawable.getBitmap().recycle();
        }
    }
}