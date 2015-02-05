package com.coretronic.bdt.HealthQA;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import com.coretronic.bdt.R;

/**
 * Created by james on 14/11/19.
 */
public class HealthQAMainActivity extends Activity {
    private final String TAG = HealthQAMainActivity.class.getSimpleName();
    private Context mContext;


    private LinearLayout qaMainLayout = null;

    private FragmentManager fragmentManager = null;
    private FragmentTransaction fragmentTransaction = null;
    private Fragment fragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.health_qa_main_layout);
        mContext = this;
        qaMainLayout = (LinearLayout) findViewById(R.id.qa_main_layout);


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



    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            Log.d(TAG, "onKeyDown back");
//
//            this.finish();
////            getFragmentManager().popBackStack();
//
//        }
//        return false;
//    }

}
