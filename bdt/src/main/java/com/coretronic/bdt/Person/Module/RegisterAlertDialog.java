package com.coretronic.bdt.Person.Module;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import com.coretronic.bdt.R;

/**
 * Created by Morris on 14/12/3.
 */
public class RegisterAlertDialog extends Dialog {
    private View.OnClickListener listener;
    private Context context;
    private TextView btn_ok;
    private TextView btn_cancel;
    private TextView msgView;
    private TextView phoneNumView;


    public RegisterAlertDialog(final Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_alertdialog_with_editview);
        this.context = context;
        btn_cancel = (TextView) findViewById(R.id.pp_dialog_btn_calcel);
        btn_ok = (TextView) findViewById(R.id.pp_dialog_btn_ok);
        msgView = (TextView) findViewById(R.id.msgTV);
        phoneNumView = (TextView)findViewById(R.id.txtPhoneNum);

        btn_cancel.setText(context.getString(R.string.pp_btn_cancel));
        btn_ok.setText(context.getString(R.string.pp_btn_ok));

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

    public void setMsg(String msg){
        msgView.setText(msg);
    }

    public void setTextViewVisibility(boolean status){
        if(status){
            phoneNumView.setVisibility(View.VISIBLE);
        }else{
            phoneNumView.setVisibility(View.GONE);
        }
    }


    public void setPhoneNum(String phoneNum){
        phoneNumView.setText(phoneNum);
    }

    public void setPositiveListener(View.OnClickListener listener){
        btn_ok.setOnClickListener(listener);
    }

    public void setNegativeListener(View.OnClickListener listener){
        btn_cancel.setOnClickListener(listener);
    }

}
