package com.coretronic.bdt.Friend.Module;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.coretronic.bdt.R;

/**
 * Created by poter.hsu on 2014/12/16.
 */
public class FriendActionDialog extends Dialog {

    private final Context context;
    private final Button btnOk;
    private final Button btnCancel;
    private final ImageView ivImage;
    private final TextView tvMsg;

    public FriendActionDialog(final Context context) {
        super(context);
        this.context = context;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.friend_action_dialog);

        tvMsg = (TextView) findViewById(R.id.tv_friend_action_msg);
        ivImage = (ImageView) findViewById(R.id.iv_friend_action_img);
        btnOk = (Button) findViewById(R.id.btn_friend_action_dialog_ok);
        btnCancel = (Button) findViewById(R.id.btn_friend_action_dialog_cancel);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

    public FriendActionDialog setButtonOkText(String text) {
        this.btnOk.setText(text);
        return this;
    }

    public FriendActionDialog setButtonCancelText(String text) {
        this.btnCancel.setText(text);
        return this;
    }

    public FriendActionDialog setMessageText(String text) {
        this.tvMsg.setText(text);
        return this;
    }

    public FriendActionDialog setImage(int resourceId) {
        this.ivImage.setImageResource(resourceId);
        return this;
    }

    public FriendActionDialog setOnClickButtonOkListener(View.OnClickListener listener) {
        this.btnOk.setOnClickListener(listener);
        return this;
    }

    public FriendActionDialog setOnClickButtonCancelListener(View.OnClickListener listener) {
        this.btnCancel.setOnClickListener(listener);
        return this;
    }

}
