package com.coretronic.bdt.module;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;
import com.coretronic.bdt.AppConfig;
import com.coretronic.bdt.AppUtils;
import com.coretronic.bdt.R;

/**
 * Created by Morris on 2014/8/13.
 */
public class HealthSharePopupWindow extends PopupWindow {
    private String TAG = HealthSharePopupWindow.class.getSimpleName();
    private Button allArticleBtn;
    private Context mContext;


    public HealthSharePopupWindow(Context context, OnDismissListener dismissListener) {
        super(context);
        this.mContext = context;
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.menu_news_share_popwindow_layout, null);

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

        allArticleBtn = (Button) view.findViewById(R.id.lineBtn);

        allArticleBtn.setOnClickListener(btnListener);
    }

    private View.OnClickListener btnListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent();

            switch (view.getId()) {

                // Line
                case R.id.lineBtn:
//                    intent.setClass(mContext, PharmacyFinder.class);
//                    mContext.startActivity(intent);
                      if( AppUtils.checkLineSetup(mContext) == true )
                      {
                          Log.i(TAG, "installed line");
                          intent.setAction(Intent.ACTION_SEND);
                          intent = mContext.getPackageManager().getLaunchIntentForPackage(AppConfig.LINE_PACKAGE_NAME);
                          intent.setType("text/plain");
                          intent.putExtra(Intent.EXTRA_TEXT, "this is share text");
                          mContext.startActivity(intent);

                      }
                    else
                      {
                          Log.i(TAG, "no setup line");
                          AppUtils.getAlertDialog(mContext, mContext.getString(R.string.line_nosetup_alert)).show();
                      }
                    break;


            }
            dismiss();
        }
    };
}
