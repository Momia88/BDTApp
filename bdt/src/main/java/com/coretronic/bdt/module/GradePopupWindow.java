package com.coretronic.bdt.module;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.coretronic.bdt.AppConfig;
import com.coretronic.bdt.R;
import com.coretronic.bdt.Tutorial.TutorFirstPageActivity;

/**
 * Created by Morris on 2014/8/13.
 */
public class GradePopupWindow extends PopupWindow {
    private TextView tvGrade;
    private TextView tvVersion;
    private Button btnTeachVidoe;
    private Context mContext;
    private SharedPreferences sharedPreferences;


    public GradePopupWindow(Context context, OnDismissListener dismissListener) {
        super(context);
        this.mContext = context;
        sharedPreferences = context.getSharedPreferences(AppConfig.SHAREDPREFERENCES_NAME,0);

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.grade_popwindow_layout, null);

        setContentView(view);
        setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        // click
        setFocusable(true);
        // outside to hide
        setOutsideTouchable(true);
        // back to dismiss
        setBackgroundDrawable(new BitmapDrawable(context.getResources(), (Bitmap) null));
        setOnDismissListener(dismissListener);

        tvGrade = (TextView) view.findViewById(R.id.tvGrade);
        tvVersion = (TextView) view.findViewById(R.id.tvVersion);
        btnTeachVidoe = (Button) view.findViewById(R.id.btnTeachVideo);

        btnTeachVidoe.setOnClickListener(btnListener);
        tvGrade.setText("$ " + sharedPreferences.getInt(AppConfig.PREF_USER_GRADE, 1000));
        try {
            String versionName = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionName;
            tvVersion.setText(sharedPreferences.getString(AppConfig.PREF_APP_VERSION,"v"+versionName));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            tvVersion.setText(sharedPreferences.getString(AppConfig.PREF_APP_VERSION,"v1"));
        }

    }

    private View.OnClickListener btnListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(mContext, TutorFirstPageActivity.class);
            mContext.startActivity(intent);
        }
    };
}
