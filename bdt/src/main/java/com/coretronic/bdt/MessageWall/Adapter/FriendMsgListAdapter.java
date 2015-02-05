package com.coretronic.bdt.MessageWall.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.coretronic.bdt.AppUtils;
import com.coretronic.bdt.MessageWall.Module.FriendMessage;
import com.coretronic.bdt.R;

import java.util.List;

/**
 * Created by Morris on 2014/9/23.
 */
public class FriendMsgListAdapter extends BaseAdapter {

    private List<FriendMessage> itemList;
    private Context context;
    private LayoutInflater inflater;


    public FriendMsgListAdapter(Context context, List<FriendMessage> itemList) {
        this.context = context;
        this.itemList = itemList;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return itemList.size();
    }

    @Override
    public Object getItem(int position) {
        return itemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.wall_detail_friend_msg_list_item, null);
            viewHolder = new ViewHolder();
            viewHolder.mImgUserPic = (ImageView) convertView.findViewById(R.id.imgUserPic);
            viewHolder.mTxtUserName = (TextView) convertView.findViewById(R.id.txtUserName);
            viewHolder.mTxtMsgTime = (TextView) convertView.findViewById(R.id.txtMsgTime);
            viewHolder.mTxtMsg = (TextView) convertView.findViewById(R.id.txtMsg);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            viewHolder.mImgUserPic.setImageResource(R.drawable.pp_ic_img_default);
        }
        FriendMessage friendMessage = itemList.get(position);
        if(friendMessage !=null) {
            if(friendMessage.getThumb() != null) {
                viewHolder.mImgUserPic.setImageBitmap(AppUtils.base64ToBitmap(friendMessage.getThumb()));
            }
            viewHolder.mTxtUserName.setText(friendMessage.getRealName());
            viewHolder.mTxtMsgTime.setText(friendMessage.getTime());
            viewHolder.mTxtMsg.setText(friendMessage.getMessage());
        }
        return convertView;
    }

    public static class ViewHolder {
        private ImageView mImgUserPic;
        private TextView mTxtUserName;
        private TextView mTxtMsgTime;
        private TextView mTxtMsg;
    }
}
