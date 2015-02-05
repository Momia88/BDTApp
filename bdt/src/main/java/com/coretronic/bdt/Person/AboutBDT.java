package com.coretronic.bdt.Person;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.coretronic.bdt.AppConfig;
import com.coretronic.bdt.R;

/**
 * Created by Morris on 14/11/25.
 */
public class AboutBDT extends Fragment {
    private static String TAG = AboutBDT.class.getSimpleName();

    private Button btnBack;
    private TextView barTitle;
    private Context mContext;
    private TextView versionName;
    private SharedPreferences sharedPreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
     }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.about_bdt, container, false);
        mContext = v.getContext();
        sharedPreferences = mContext.getSharedPreferences(AppConfig.SHAREDPREFERENCES_NAME, 0);
        initView(v);
        return v;
    }

    private void initView(View v) {
        // navigation bar
        btnBack = (Button) v.findViewById(R.id.btnBack);
        barTitle = (TextView) v.findViewById(R.id.action_bar_title);

        barTitle.setText(getString(R.string.lb_ww_title));
        btnBack.setOnClickListener(btnListener);

        versionName = (TextView) v.findViewById(R.id.tvVersionName);
        versionName.setText("版本號碼： v"  + sharedPreferences.getString(AppConfig.PREF_APP_VERSION,"1.0.0"));
    }


    private View.OnClickListener btnListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btnBack:
                    getActivity().getFragmentManager().popBackStackImmediate();
                    break;
       }
        }
    };
}