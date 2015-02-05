package com.coretronic.bdt.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.coretronic.bdt.DataModule.HomeArticlesInfo;
import com.coretronic.bdt.DataModule.HomeMessageInfo;
import com.coretronic.bdt.R;

import java.util.List;

/**
 * Created by james on 2014/9/11.
 */
public class NewMessageListBaseAdapter extends BaseAdapter {
    private Context context = null;
    private LayoutInflater layoutInflater;
    private String TAG = NewMessageListBaseAdapter.class.getSimpleName();
    private List<HomeMessageInfo.Result> homeMessageResultList = null;

    public NewMessageListBaseAdapter(Context context, List<HomeMessageInfo.Result> homeMessageResultList) {
        this.context = context;
        this.homeMessageResultList = homeMessageResultList;
        this.layoutInflater = LayoutInflater.from(context);
        Log.i(TAG, "articleResultList:" + homeMessageResultList);
        Log.i(TAG, "articleResultList.size():" + homeMessageResultList.size());
    }

    @Override
    public int getCount() {
        Log.i(TAG, "getcount:" + homeMessageResultList.size());
        return homeMessageResultList.size();
//        return 2;
    }

    @Override
    public Object getItem(int position) {
        return homeMessageResultList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.i(TAG, "Into getView ");
        ViewTag viewTag;

        if (convertView == null) {
            Log.i(TAG, "convertView == null ");
            convertView = layoutInflater.inflate(R.layout.main_newmessage_item, null);
            viewTag = new ViewTag(
                    (LinearLayout) convertView.findViewById(R.id.mainMessageLL),
                    (ImageView) convertView.findViewById(R.id.mainMessageIcon),
                    (TextView) convertView.findViewById(R.id.mainMessageTitle));
            convertView.setTag(viewTag);
        } else {
            Log.i(TAG, "convertView != null ");
            viewTag = (ViewTag) convertView.getTag();
//            viewTag.homeNewArticleItemLL.setBackgroundColor(R.color.white);
            (viewTag.homeNewArticleItemLL).setBackgroundColor(context.getResources().getColor(R.color.white));
        }

        viewTag.homeNewsTitle.setText(homeMessageResultList.get(position).getTitle());

        if (homeMessageResultList.get(position).getArticletype().equals("daily")) {
            viewTag.homeNewsPhoto.setImageResource(R.drawable.pp_ic_wall);
        } else if (homeMessageResultList.get(position).getArticletype().equals("comment")) {
            viewTag.homeNewsPhoto.setImageResource(R.drawable.pp_ic_msg);
        }
        else if (homeMessageResultList.get(position).getArticletype().equals("system")) {
            viewTag.homeNewsPhoto.setImageResource(R.drawable.system_icon);
        }

        if( homeMessageResultList.get(position).getRead().equals("N"))
        {
//            viewTag.homeNewArticleItemLL.setBackgroundColor(R.color.red);
            (viewTag.homeNewArticleItemLL).setBackgroundColor(context.getResources().getColor(R.color.not_read_bg));
        }
        else if(homeMessageResultList.get(position).getRead().equals("Y"))
        {
            (viewTag.homeNewArticleItemLL).setBackgroundColor(context.getResources().getColor(R.color.white));
        }
        return convertView;
    }

    public void addItem(HomeMessageInfo.Result item) {

        homeMessageResultList.add(item);
    }

    class ViewTag {
        ImageView homeNewsPhoto = null;
        TextView homeNewsTitle = null;
        LinearLayout homeNewArticleItemLL = null;
        public ViewTag(LinearLayout homeNewArticleItemLL, ImageView homeNewsPhoto, TextView homeNewsTitle) {
            this.homeNewArticleItemLL = homeNewArticleItemLL;
            this.homeNewsPhoto = homeNewsPhoto;
            this.homeNewsTitle = homeNewsTitle;
        }

    }
}