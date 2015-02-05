package com.coretronic.bdt.WalkWay.Module;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.coretronic.bdt.AppUtils;
import com.coretronic.bdt.R;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * Created by Morris on 2014/9/3.
 */
public class WalkWayExListAdapter extends BaseExpandableListAdapter {
    private String TAG = WalkWayExListAdapter.class.getSimpleName();
    private List<String> groups;
    private List<List<WalkWayAreaPartInfo>> childern;
    private Context context;
    private LayoutInflater inflater;
    private ImageLoader imageLoader = ImageLoader.getInstance();

    public WalkWayExListAdapter(Context context, List<String> groups, List<List<WalkWayAreaPartInfo>> childern) {
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.groups = groups;
        this.childern = childern;
    }


    @Override
    public int getGroupCount() {
        return groups.size();
    }

    @Override
    public long getGroupId(int groupId) {
        return groupId;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groups.get(groupPosition);
    }

    @Override
    public long getChildId(int groupId, int childId) {
        return childId;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        int size = 0 ;
        if(childern != null && childern.get(groupPosition) != null){
            size = childern.get(groupPosition).size();
        }
        return size;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        if(childern != null && childern.get(groupPosition) != null){
            return childern.get(groupPosition).get(childPosition);
        }else{
            return null;
        }
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        GroupViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.walkway_item_group_layout, null);
            viewHolder = new GroupViewHolder();
            viewHolder.textView = (TextView) convertView.findViewById(R.id.group_text);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (GroupViewHolder) convertView.getTag();
        }
        viewHolder.textView.setText(groups.get(groupPosition));

        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ChildViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.walkway_area_list_item, null);
            viewHolder = new ChildViewHolder();
            viewHolder.icon = (ImageView) convertView.findViewById(R.id.ww_area_img_title);
            viewHolder.title = (TextView) convertView.findViewById(R.id.txt_title);
            viewHolder.area = (TextView) convertView.findViewById(R.id.txt_subTitle);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ChildViewHolder) convertView.getTag();
            imageLoader.cancelDisplayTask(viewHolder.icon);
            viewHolder.icon.setImageDrawable(null);
        }
        AppUtils.loadPhoto(context, viewHolder.icon, childern.get(groupPosition).get(childPosition).getImagePath());
        Log.d(TAG, "path:" + childern.get(groupPosition).get(childPosition).getImagePath());
        viewHolder.title.setText(childern.get(groupPosition).get(childPosition).getWalkwayArea());
        viewHolder.area.setText(childern.get(groupPosition).get(childPosition).getWalkwayName());
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int i, int i2) {
        return true;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }


    public static class GroupViewHolder {
        public TextView textView;
    }

    public static class ChildViewHolder {
            public ImageView icon;
            public TextView area;
            public TextView title;
   }
}
