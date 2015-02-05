package com.coretronic.bdt.MessageWall.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.coretronic.bdt.R;

import java.util.List;

/**
 * Created by Morris on 2014/9/23.
 */
public class CustomerListAdapter extends BaseAdapter {

    private List<String> itemList;
    private Context context;
    private LayoutInflater inflater;


    public CustomerListAdapter(Context context, List<String> itemList) {
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
            convertView = inflater.inflate(R.layout.list_item_text, null);
            viewHolder = new ViewHolder();
            viewHolder.textView = (TextView) convertView.findViewById(R.id.textGoodFriend);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.textView.setText(itemList.get(position));
        return convertView;
    }

    public static class ViewHolder {
        private TextView textView;
    }
}
