package com.coretronic.bdt.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.coretronic.bdt.R;
import com.coretronic.bdt.module.MapClusterItem;

import java.util.List;

/**
 * Created by Morris on 2014/9/23.
 */
public class DialogListAdapter extends BaseAdapter {

    private List<MapClusterItem> itemList;
    private Context context;
    private LayoutInflater inflater;

    public DialogListAdapter(Context context,  List<MapClusterItem> itemList,LayoutInflater inflater) {
        this.context = context;
        this.itemList = itemList;
        this.inflater = inflater;

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
        if(convertView == null){
            convertView = inflater.inflate(R.layout.dialog_list_item,null);
            viewHolder = new ViewHolder();
            viewHolder.textView = (TextView)convertView.findViewById(R.id.dialog_doctor_name);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }

        viewHolder.textView.setText(itemList.get(position).name);
        return convertView;
    }

    public void addItem(MapClusterItem item){
        itemList.add(item);
    }

    public static class ViewHolder{
        public TextView textView;
    }
}
