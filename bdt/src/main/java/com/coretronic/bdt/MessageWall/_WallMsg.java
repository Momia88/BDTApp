package com.coretronic.bdt.MessageWall;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import com.coretronic.bdt.AppConfig;
import com.coretronic.bdt.MessageWall.Module.WallMessageList;
import com.coretronic.bdt.R;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Morris on 2014/10/17.
 */
public class _WallMsg extends Fragment {
    private String TAG = _WallMsg.class.getSimpleName();
    private Context mContext;
    private Button btnBack;
    private TextView barTitle;
    private SharedPreferences sharedPreferences;
    private ProgressDialog progressDialog = null;
    private String uuidChose;
    private AsyncHttpClient asyncHttpClient;
    private Gson gson = new Gson();
    private SimpleDateFormat formatter;
    private Date curDate;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getActivity().getSharedPreferences(AppConfig.SHAREDPREFERENCES_NAME, 0);
        asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.setTimeout(AppConfig.TIMEOUT);
        uuidChose = sharedPreferences.getString(AppConfig.PREF_UNIQUE_ID, "");
        formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        curDate = new Date(System.currentTimeMillis()); // 獲取當前時間

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.wall_msg_layout, container, false);
        mContext = v.getContext();
        progressDialog = new ProgressDialog(mContext);
        initView(v);
        return v;
    }

    private void initView(View v) {
        // navigation bar
        btnBack = (Button) v.findViewById(R.id.btnBack);
        barTitle = (TextView) v.findViewById(R.id.action_bar_title);
        barTitle.setText(getString(R.string.lb_ww_title));
        btnBack.setOnClickListener(btnListener);


    }


    // Location Listener
    AdapterView.OnItemClickListener listItemListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
            WallMessageList item = (WallMessageList) adapterView.getItemAtPosition(position);
        }
    };

    View.OnClickListener btnListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btnBack:
                    getActivity().finish();
                    break;
                case R.id.btnMsgAdd:
                    Fragment fragment = new WallMsgAdd();
                    if (fragment != null) {
                        getActivity().getFragmentManager().beginTransaction()
                                .replace(R.id.frame_container, fragment, "MsgEditorFragment")
                                .addToBackStack("MsgEditorFragment")
                                .commit();
                    } else {
                        Log.e(TAG, "Error in creating fragment");
                    }
                    break;
            }

        }
    };

    @Override
    public void onPause() {
        super.onPause();
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }
}

