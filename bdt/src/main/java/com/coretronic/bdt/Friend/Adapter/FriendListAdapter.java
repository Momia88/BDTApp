package com.coretronic.bdt.Friend.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.coretronic.bdt.AppUtils;
import com.coretronic.bdt.R;

import java.util.List;

/**
 * Created by poter.hsu on 2014/12/12.
 */
public class FriendListAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private List<FriendListItem> friendListItemList;

    public FriendListAdapter(Context context, List<FriendListItem> friendListItemList) {
        this.inflater = LayoutInflater.from(context);
        this.friendListItemList = friendListItemList;
    }

    @Override
    public int getCount() {
        return friendListItemList.size();
    }

    @Override
    public Object getItem(int position) {
        return friendListItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TagView tagView = null;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.friend_list_item, null);

            tagView = new TagView(
                    (ImageView) convertView.findViewById(R.id.iv_friend_thumb),
                    (TextView) convertView.findViewById(R.id.tv_friend_name)
            );

            convertView.setTag(tagView);
        } else {
            tagView = (TagView) convertView.getTag();
        }

        String encodedThumb = friendListItemList.get(position).getFriendThumb();
        if (encodedThumb == null) {
            tagView.ivThumb.setImageResource(R.drawable.pp_ic_nobody);
        } else {
            tagView.ivThumb.setImageBitmap(AppUtils.base64ToBitmap(encodedThumb));
        }

        tagView.tvName.setText(friendListItemList.get(position).getFriendName());

        return convertView;
    }

    private class TagView {
        public ImageView ivThumb;
        public TextView tvName;

        public TagView(ImageView ivThumb, TextView tvName) {
            this.ivThumb = ivThumb;
            this.tvName = tvName;
        }
    }
}
