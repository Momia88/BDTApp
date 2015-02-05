package com.coretronic.bdt.Person.Module;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.coretronic.bdt.R;

import java.util.List;

/**
 * Created by james on 2014/9/11.
 */
public class NotifyListAdapter extends BaseAdapter {
    private Context context = null;
    private LayoutInflater layoutInflater;
    private String TAG = NotifyListAdapter.class.getSimpleName();
    private List<NotifyInfo.Result> notifyList = null;

    public NotifyListAdapter(Context context, List<NotifyInfo.Result> notifyList) {
        this.context = context;
        this.notifyList = notifyList;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        Log.i(TAG, "getcount:" + notifyList.size());
        return notifyList.size();
//        return 2;
    }

    @Override
    public Object getItem(int position) {
        return notifyList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.i(TAG, "Into getView ");
        NotifyInfo.Result result = notifyList.get(position);
        if (result == null) {
            return null;
        }
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.notify_list_item, null);
            viewHolder = new ViewHolder();
            viewHolder.layout = (LinearLayout) convertView.findViewById(R.id.notifyItemLayout);
            viewHolder.mPhoto = (ImageView) convertView.findViewById(R.id.notifyPhoto);
            viewHolder.mTitle = (TextView) convertView.findViewById(R.id.notifyTitle);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            viewHolder.mPhoto.setImageBitmap(null);
        }

        if("1".equals(result.getState())){
            viewHolder.layout.setBackgroundColor(Color.WHITE);
        }else {
            viewHolder.layout.setBackgroundColor(context.getResources().getColor(R.color.block_clolr_15));
        }

        //article、health 、invite 、message
        if ("article".equals(result.getMessageType())) {
            viewHolder.mPhoto.setImageResource(R.drawable.pp_ic_wall);
        } else if ("health".equals(result.getMessageType())) {
            viewHolder.mPhoto.setImageResource(R.drawable.news_no_pic);
        } else if ("invite".equals(result.getMessageType())) {
            viewHolder.mPhoto.setImageResource(R.drawable.pp_ic_friend);
        } else if ("message".equals(result.getMessageType())) {
            viewHolder.mPhoto.setImageResource(R.drawable.pp_ic_msg);
        } else if ("system".equals(result.getMessageType())) {
            viewHolder.mPhoto.setImageResource(R.drawable.system_icon);
        }
        viewHolder.mTitle.setText(result.getTitle());
        return convertView;
    }

    class ViewHolder {
        LinearLayout layout = null;
        ImageView mPhoto = null;
        TextView mTitle = null;

    }
}