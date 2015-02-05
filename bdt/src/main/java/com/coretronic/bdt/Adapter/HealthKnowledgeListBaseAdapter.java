package com.coretronic.bdt.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.coretronic.bdt.AppConfig;
import com.coretronic.bdt.AppUtils;
import com.coretronic.bdt.DataModule.ArticleListDataInfo;
import com.coretronic.bdt.R;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by james on 2014/9/11.
 */
public class HealthKnowledgeListBaseAdapter extends BaseAdapter {
    private Context context = null;
    private LayoutInflater layoutInflater;
    private String TAG = HealthKnowledgeListBaseAdapter.class.getSimpleName();
    private List<ArticleListDataInfo.Result> articleResultList = null;
    private ImageLoader imageLoader = ImageLoader.getInstance();
    //transactionLog
    private String currentTime;
    private String append;
    private String uuidChose;
    private SharedPreferences sharedPreferences;

    public HealthKnowledgeListBaseAdapter(Context context, List<ArticleListDataInfo.Result>  articleResultList)
    {
        this.context = context;
        this.articleResultList = articleResultList;
        this.layoutInflater = LayoutInflater.from(context);
        Log.i(TAG,"articleResultList:"+articleResultList);
        Log.i(TAG,"articleResultList.size():"+articleResultList.size());
        sharedPreferences = context.getSharedPreferences(AppConfig.SHAREDPREFERENCES_NAME, 0);
        uuidChose = sharedPreferences.getString(AppConfig.PREF_UNIQUE_ID, "");
    }

    @Override
    public int getCount() {
        Log.i(TAG,"getcount:"+articleResultList.size());
        return articleResultList.size();
//        return 2;
    }

    @Override
    public Object getItem(int position) {

        return articleResultList.get(position);
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
            convertView = layoutInflater.inflate(R.layout.health_knowledge_item, null);
            viewTag = new ViewTag(
                    (ImageView) convertView.findViewById(R.id.news_photo),
                    (ImageView) convertView.findViewById(R.id.news_hot),
                    (TextView) convertView.findViewById(R.id.news_title));
            convertView.setTag(viewTag);
        }
        else {
            Log.i(TAG, "convertView != null ");
            viewTag = (ViewTag) convertView.getTag();
            imageLoader.cancelDisplayTask(viewTag.img);
            viewTag.img.setImageResource(R.drawable.news_no_pic);
        }

        Log.i(TAG, "articleResultList.get(position).getNewsTitle():" + articleResultList.get(position).getNewsTitle());
//        viewTag.newsTitle.setText( articleResultList.get(position).getNewsTitle() );
        viewTag.newsTitle.setText(articleResultList.get(position).getNewsTitle());
        if(articleResultList.get(position).getNewsPhoto()!=null) {
            Log.i(TAG,"photo path != null/position:" + position + "--- url = " + articleResultList.get(position).getNewsPhoto());
            AppUtils.loadPhoto(context, viewTag.img, articleResultList.get(position).getNewsPhoto(), imageLoader);
        }

//        viewTag.img_01.setImageResource(R.drawable.news_hot);


        if(articleResultList.get(position).getPopular().equals("hot")) {
            viewTag.img_01.setImageResource(R.drawable.news_hot);
            viewTag.img_01.setVisibility(View.VISIBLE);
        } else {
            viewTag.img_01.setVisibility(View.GONE);
        }

        Log.i(TAG,"position = " + position + "--- url = " + articleResultList.get(position).getNewsPhoto());

        return convertView;
    }

    public void addItem(ArticleListDataInfo.Result item){
        articleResultList.add(item);
    }

    class ViewTag
    {
        ImageView img = null;
        ImageView img_01 = null;
        TextView newsTitle = null;
        public ViewTag(ImageView img, ImageView img_01, TextView newsTitle)
        {
            this.img = img;
            this.img_01 = img_01;
            this.newsTitle = newsTitle;
        }

    }

    public void transactionLogSave(String append) {
        try {
            FileOutputStream outStream = new FileOutputStream("/sdcard/outputLog.txt", true);
            outStream.write(append.getBytes());
            outStream.close();
        } catch (FileNotFoundException e) {
            return;
        } catch (IOException e) {
            return;
        }
    }

    //抓取系統時間
    private void callSystemTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
        Date curDate = new Date(System.currentTimeMillis()); // 獲取當前時間
        currentTime = formatter.format(curDate);
        Log.i(TAG, "==Time==: " + currentTime);

    }
}