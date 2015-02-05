package com.coretronic.bdt.MessageWall;
/**
 *  我要找步道
 */

import android.app.Activity;
import android.app.Fragment;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import com.coretronic.bdt.R;

/**
 * Created by Morris on 2014/10/28.
 */
public class WallActivity extends Activity {
    private String TAG = WallActivity.class.getSimpleName();
    private LinearLayout wallLayout;
    private BitmapDrawable bgDrawable;
    private Drawable drawable;
    private int CAMERA_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wall_layout);
        wallLayout = (LinearLayout) findViewById(R.id.wall_layout);
        bgDrawable = new BitmapDrawable(getResources(), getResources().openRawResource(R.drawable.bg_walkway));
        bgDrawable.setAlpha(50);
        if (android.os.Build.VERSION.SDK_INT < 16) {
            wallLayout.setBackgroundDrawable(bgDrawable);
        } else {
            wallLayout.setBackground(bgDrawable);
        }

        Fragment fragment = new WallMsgList();
        if (fragment != null) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.frame_container, fragment, "WallListFragment")
//                    .addToBackStack("WallListFragment")
                    .commit();
        } else {
            Log.e("WWActivity", "Error in creating fragment");
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //recycle bg
        if (null != wallLayout.getBackground()) {
            wallLayout.getBackground().setCallback(null);
        }
        if (null != bgDrawable && !bgDrawable.getBitmap().isRecycled()) {
            bgDrawable.getBitmap().recycle();
        }
    }
}