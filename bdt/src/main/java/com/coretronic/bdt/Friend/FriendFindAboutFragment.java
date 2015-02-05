package com.coretronic.bdt.Friend;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.coretronic.bdt.R;

/**
 * Created by poter.hsu on 2014/12/15.
 */
public class FriendFindAboutFragment extends Fragment {

    private TextView tvSelfPhoneNumber;
    private String userTel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.friend_find_about, container, false);

        Bundle bundle = getArguments();
        userTel = bundle.getString("tel");

        initView(view);

        return view;
    }

    private void initView(View view) {
        tvSelfPhoneNumber = (TextView) view.findViewById(R.id.tv_self_phone_number);
        tvSelfPhoneNumber.setText(userTel);
    }
}
