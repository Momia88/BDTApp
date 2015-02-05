package com.coretronic.bdt.WalkWay.Module;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.coretronic.bdt.AppUtils;
import com.coretronic.bdt.R;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * Called when the activity is first created.
 */
public class WalkWayChartsAdapter extends BaseAdapter {
    private String TAG = WalkWayChartsAdapter.class.getSimpleName();
    private List<ChartsRankInfo> walkWayChartsInfos;
    private Context context;
    private LayoutInflater inflater;
    private ImageLoader imageLoader = ImageLoader.getInstance();
    private TextView mTxtRank;
    private ImageView mImgFriendPhoto;
    private TextView mTxtFriendName;
    private TextView mTxtWalkwayNum;



    public WalkWayChartsAdapter(Context context, List<ChartsRankInfo> infos) {
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.walkWayChartsInfos = infos;
    }

    @Override
    public int getCount() {
        return walkWayChartsInfos.size();
    }

    @Override
    public Object getItem(int position) {
        return walkWayChartsInfos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.walkway_charts_list_item, null);
            viewHolder = new ViewHolder();
            viewHolder.mTxtRank = (TextView) convertView.findViewById(R.id.txt_rank);
            viewHolder.mImgFriendPhoto = (ImageView) convertView.findViewById(R.id.ww_img_friend_photo);
            viewHolder.mTxtFriendName = (TextView) convertView.findViewById(R.id.txt_friend_name);
            viewHolder.mTxtWalkwayNum = (TextView) convertView.findViewById(R.id.txt_walkway_num);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            viewHolder.mImgFriendPhoto.setImageBitmap(null);
        }
        if(walkWayChartsInfos.get(position).getThumb() != null){
            viewHolder.mImgFriendPhoto.setImageBitmap(AppUtils.base64ToBitmap(walkWayChartsInfos.get(position).getThumb()));
        }
        viewHolder.mTxtRank.setText(walkWayChartsInfos.get(position).getRank());
        viewHolder.mTxtFriendName.setText(walkWayChartsInfos.get(position).getUserName());
        viewHolder.mTxtWalkwayNum.setText(walkWayChartsInfos.get(position).getCount() + " 個");

        return convertView;
    }


    public static class ViewHolder {
        public TextView mTxtRank;
        public ImageView mImgFriendPhoto;
        public TextView mTxtFriendName;
        public TextView mTxtWalkwayNum;
    }

}