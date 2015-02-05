package com.coretronic.bdt.MessageWall.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.coretronic.bdt.AppUtils;
import com.coretronic.bdt.MessageWall.Module.WallMessageInfo;
import com.coretronic.bdt.R;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * Created by Morris on 2014/9/23.
 */
public class MessageListAdapter extends BaseAdapter {

    private List<WallMessageInfo> itemList;
    private Context context;
    private LayoutInflater inflater;
    private ImageLoader imageLoader = ImageLoader.getInstance();


    public MessageListAdapter(Context context, List<WallMessageInfo> itemList) {
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
            convertView = inflater.inflate(R.layout.wall_msg_list_item, null);
            viewHolder = new ViewHolder();
            viewHolder.mImgUserPic = (ImageView) convertView.findViewById(R.id.imgUserPic);
            viewHolder.mImgFirstPic = (ImageView) convertView.findViewById(R.id.imgFirstPic);
            viewHolder.mTxtUserName = (TextView) convertView.findViewById(R.id.txtUserName);
            viewHolder.mTxtMsgTime = (TextView) convertView.findViewById(R.id.txtMsgTime);
            viewHolder.mTxtMsg = (TextView) convertView.findViewById(R.id.txtMsg);
            viewHolder.mTxtGoodPoint = (TextView) convertView.findViewById(R.id.txtGoodPoint);
            viewHolder.mTxtFriendMsg = (TextView) convertView.findViewById(R.id.txtFriendMsg);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            viewHolder.mImgUserPic.setImageResource(R.drawable.pp_ic_img_default);
            viewHolder.mImgFirstPic.setImageResource(R.drawable.qanews_no_image);
        }
        WallMessageInfo wallMessageInfo = itemList.get(position);
        if (wallMessageInfo != null) {
            if (wallMessageInfo.getThumb() != null) {
                viewHolder.mImgUserPic.setImageBitmap(AppUtils.base64ToBitmap(wallMessageInfo.getThumb()));
            }
            if (wallMessageInfo.getPicUrl() != null) {
                AppUtils.loadPhoto(context, viewHolder.mImgFirstPic, wallMessageInfo.getPicUrl());
            } else {
                viewHolder.mImgFirstPic.setVisibility(View.GONE);
            }

            viewHolder.mTxtUserName.setText(wallMessageInfo.getUserName());
            viewHolder.mTxtMsgTime.setText(wallMessageInfo.getDate());
            viewHolder.mTxtMsg.setText(wallMessageInfo.getTitle());
            viewHolder.mTxtGoodPoint.setText(String.valueOf(wallMessageInfo.getGoods()));
            viewHolder.mTxtFriendMsg.setText(String.valueOf(wallMessageInfo.getMessages()));
        }
        return convertView;
    }

    public void addItem(WallMessageInfo item) {
        itemList.add(item);
    }

    public static class ViewHolder {
        private ImageView mImgUserPic;
        private ImageView mImgFirstPic;

        private TextView mTxtUserName;
        private TextView mTxtMsgTime;
        private TextView mTxtMsg;
        private TextView mTxtGoodPoint;
        private TextView mTxtFriendMsg;

    }
}
