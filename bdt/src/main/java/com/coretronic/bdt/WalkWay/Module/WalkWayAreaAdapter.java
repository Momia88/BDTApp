package com.coretronic.bdt.WalkWay.Module;

import android.content.Context;
import android.util.Log;
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
public class WalkWayAreaAdapter extends BaseAdapter {
    private String TAG = WalkWayAreaAdapter.class.getSimpleName();
    private List<WalkWayAreaPartInfo> walkWayAreaPartInfos;
    private Context context;
    private LayoutInflater inflater;
    private ImageLoader imageLoader = ImageLoader.getInstance();


    public WalkWayAreaAdapter(Context context, List<WalkWayAreaPartInfo> infos) {
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.walkWayAreaPartInfos = infos;
    }

    @Override
    public int getCount() {
        return walkWayAreaPartInfos.size();
    }

    @Override
    public Object getItem(int position) {
        return walkWayAreaPartInfos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.walkway_area_list_item, null);
            viewHolder = new ViewHolder();
            viewHolder.icon = (ImageView) convertView.findViewById(R.id.ww_area_img_title);
            viewHolder.title = (TextView) convertView.findViewById(R.id.txt_title);
            viewHolder.area = (TextView) convertView.findViewById(R.id.txt_subTitle);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            imageLoader.cancelDisplayTask(viewHolder.icon);
            viewHolder.icon.setImageDrawable(null);
        }
        AppUtils.loadPhoto(context, viewHolder.icon, walkWayAreaPartInfos.get(position).getImagePath());
        Log.d(TAG, "path:" + walkWayAreaPartInfos.get(position).getImagePath());
        viewHolder.title.setText(walkWayAreaPartInfos.get(position).getWalkwayArea());
        viewHolder.area.setText(walkWayAreaPartInfos.get(position).getWalkwayName());

        return convertView;
    }


    public static class ViewHolder {
        public ImageView icon;
        public TextView area;
        public TextView title;
    }

}