package com.coretronic.bdt.Friend;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import com.coretronic.bdt.R;

/**
 * Created by poter.hsu on 2014/12/12.
 */
public class FriendActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.friend);
        Fragment friendListFragment = new FriendListFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.friend_frame_container, friendListFragment, "FriendList").commit();
    }
}
