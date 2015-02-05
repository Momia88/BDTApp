package com.coretronic.bdt.Adapter;

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
import com.coretronic.bdt.AppUtils;
import com.coretronic.bdt.DataModule.ArticleListDataInfo;
import com.coretronic.bdt.DataModule.HomeArticlesInfo;
import com.coretronic.bdt.R;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * Created by james on 2014/9/11.
 */
public class NewArticlesListBaseAdapter extends BaseAdapter {
    private Context context = null;
    private LayoutInflater layoutInflater;
    private String TAG = NewArticlesListBaseAdapter.class.getSimpleName();
    private List<HomeArticlesInfo.Result> homeArticleResultList = null;

    public NewArticlesListBaseAdapter(Context context, List<HomeArticlesInfo.Result> homeArticleResultList) {
        this.context = context;
        this.homeArticleResultList = homeArticleResultList;
        this.layoutInflater = LayoutInflater.from(context);
        Log.i(TAG, "articleResultList:" + homeArticleResultList);
        Log.i(TAG, "articleResultList.size():" + homeArticleResultList.size());
    }

    @Override
    public int getCount() {
        Log.i(TAG, "getcount:" + homeArticleResultList.size());
        return homeArticleResultList.size();
//        return 2;
    }

    @Override
    public Object getItem(int position) {

        return homeArticleResultList.get(position);
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
            convertView = layoutInflater.inflate(R.layout.main_newarticles_item, null);
            viewTag = new ViewTag(
                    (LinearLayout) convertView.findViewById(R.id.mainArticleLL),
                    (ImageView) convertView.findViewById(R.id.mainArticleIcon),
                    (TextView) convertView.findViewById(R.id.mainArticleTitle));
            convertView.setTag(viewTag);
        } else {
            Log.i(TAG, "convertView != null ");
            viewTag = (ViewTag) convertView.getTag();
//            viewTag.homeNewArticleItemLL.setBackgroundColor(R.color.white);
            (viewTag.homeNewArticleItemLL).setBackgroundColor(context.getResources().getColor(R.color.white));
//            viewTag.homeNewArticleItemLL.setBackgroundColor(R.color.white);
        }

        viewTag.homeNewsTitle.setText(homeArticleResultList.get(position).getTitle());

        if (homeArticleResultList.get(position).getArticletype().equals("daily")) {
            viewTag.homeNewsPhoto.setImageResource(R.drawable.pp_ic_wall);
        } else if (homeArticleResultList.get(position).getArticletype().equals("comment")) {
            viewTag.homeNewsPhoto.setImageResource(R.drawable.pp_ic_wall);
        }
        else if (homeArticleResultList.get(position).getArticletype().equals("news")) {
            viewTag.homeNewsPhoto.setImageResource(R.drawable.news_no_pic);
        }

        if( homeArticleResultList.get(position).getRead().equals("N"))
        {
            Log.i(TAG,"====NNNN====");
//            viewTag.homeNewArticleItemLL.setBackgroundColor(R.color.red);
            (viewTag.homeNewArticleItemLL).setBackgroundColor(context.getResources().getColor(R.color.not_read_bg));
        }
        else if(homeArticleResultList.get(position).getRead().equals("Y"))
        {
            Log.i(TAG,"====yyyyyy====");
            (viewTag.homeNewArticleItemLL).setBackgroundColor(context.getResources().getColor(R.color.white));
        }
        return convertView;
    }

    public void addItem(HomeArticlesInfo.Result item) {

        homeArticleResultList.add(item);
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