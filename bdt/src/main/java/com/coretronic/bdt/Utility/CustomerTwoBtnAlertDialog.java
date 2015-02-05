package com.coretronic.bdt.Utility;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import com.coretronic.bdt.R;

/**
 * Created by Morris on 14/12/3.
 */
public class CustomerTwoBtnAlertDialog extends Dialog {
    private Context context;
    private TextView btn_ok;
    private TextView btn_cancel;
    private TextView msgView;


    public CustomerTwoBtnAlertDialog(final Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_two_btn_alertdialog);
        this.context = context;
        msgView = (TextView) findViewById(R.id.msgTV);
        btn_ok = (TextView) findViewById(R.id.pp_dialog_btn_ok);
        btn_cancel = (TextView) findViewById(R.id.pp_dialog_btn_calcel);
        btn_cancel.setText(context.getString(R.string.pp_btn_cancel));
        btn_ok.setText(context.getString(R.string.pp_btn_ok));
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }


    public CustomerTwoBtnAlertDialog setMsg(String msg) {
        msgView.setText(msg);
        return this;
    }


    public CustomerTwoBtnAlertDialog setPositiveBtnText(String str) {
        btn_ok.setText(str);
        return this;
    }

    public CustomerTwoBtnAlertDialog setNegativeBtnText(String str) {
        btn_cancel.setText(str);
        return this;
    }

    public CustomerTwoBtnAlertDialog setPositiveListener(View.OnClickListener listener) {
        btn_ok.setOnClickListener(listener);
        return this;
    }

    public CustomerTwoBtnAlertDialog setNegativeListener(View.OnClickListener listener) {
        btn_cancel.setOnClickListener(listener);
        return this;
    }

}
