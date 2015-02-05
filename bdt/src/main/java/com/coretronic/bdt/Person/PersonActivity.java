package com.coretronic.bdt.Person;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import com.coretronic.bdt.R;

/**
 * Created by Morris on 14/11/21.
 */
public class PersonActivity extends Activity {
    private String TAG = PersonActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.person_main);

        Fragment fragment = new PersonMenu();
        if (fragment != null) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.person_frame_container, fragment, "PersionMenu")
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
    }
}