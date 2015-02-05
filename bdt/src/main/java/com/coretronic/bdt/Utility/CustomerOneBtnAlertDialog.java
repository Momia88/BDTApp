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
public class CustomerOneBtnAlertDialog extends Dialog {
    private Context context;
    private TextView btn_ok;
    private TextView msgView;


    public CustomerOneBtnAlertDialog(final Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_one_btn_alertdialog);
        this.context = context;
        msgView = (TextView) findViewById(R.id.msgTV);
        btn_ok = (TextView) findViewById(R.id.pp_dialog_btn_ok);
        btn_ok.setText(context.getString(R.string.pp_btn_ok));
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

    public CustomerOneBtnAlertDialog setMsg(String msg) {
        msgView.setText(msg);
        return this;
    }

    public CustomerOneBtnAlertDialog setPositiveBtnText(String str) {
        btn_ok.setText(str);
        return this;
    }

    public CustomerOneBtnAlertDialog setPositiveListener(View.OnClickListener listener) {
        btn_ok.setOnClickListener(listener);
        return this;
    }


}
