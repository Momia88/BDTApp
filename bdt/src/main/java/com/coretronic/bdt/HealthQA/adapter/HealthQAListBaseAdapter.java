package com.coretronic.bdt.HealthQA.adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.coretronic.bdt.AppUtils;
import com.coretronic.bdt.HealthQA.module.MemberRecordInfo;
import com.coretronic.bdt.R;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * Created by james on 2014/9/11.
 */
public class HealthQAListBaseAdapter extends BaseAdapter {

    private Context context = null;
    private LayoutInflater layoutInflater;
    private String TAG = HealthQAListBaseAdapter.class.getSimpleName();
    private ImageLoader imageLoader = ImageLoader.getInstance();
    private List<MemberRecordInfo.Users> listViewData = null;

    public HealthQAListBaseAdapter(Context context, List<MemberRecordInfo.Users> listViewData)
    {
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        this.listViewData = listViewData;
        Log.i(TAG, "listViewData:"+listViewData);
    }

    @Override
    public int getCount() {
        Log.i(TAG,"listViewData.size():"+listViewData.size());
        return listViewData.size();
    }

    @Override
    public Object getItem(int position) {

        return listViewData.get(position);
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
            convertView = layoutInflater.inflate(R.layout.health_qa_item, null);
            viewTag = new ViewTag((TextView) convertView.findViewById(R.id.qa_item_sn),
                    (ImageView) convertView.findViewById(R.id.qa_photo_item),
                    (TextView) convertView.findViewById(R.id.qa_name_item),
                    (TextView) convertView.findViewById(R.id.qaGradeCount)
            );
            convertView.setTag(viewTag);
        }
        else {
            Log.i(TAG, "convertView != null ");
            viewTag = (ViewTag) convertView.getTag();
            viewTag.qaPhotoItem.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.qa_photo_item));
        }
        viewTag.rankNum.setText(listViewData.get(position).getRank());
        viewTag.qaNameItem.setText(listViewData.get(position).getUserName());
        viewTag.qaGradeCount.setText(listViewData.get(position).getCount());
        Log.i(TAG, "position:"+position +"/ \n  thumb:"+listViewData.get(position).getThumb());
        if( listViewData.get(position).getThumb() != null )
        {
            viewTag.qaPhotoItem.setImageBitmap(AppUtils.decodeBase64(listViewData.get(position).getThumb()));
        }

        return convertView;
    }


    class ViewTag
    {
        ImageView qaPhotoItem = null;
        TextView rankNum = null;
        TextView qaNameItem = null;
        TextView qaGradeCount = null;
        public ViewTag(TextView serialNum,ImageView qaPhotoItem, TextView qaNameItem, TextView qaGradeCount)
        {


            this.rankNum = serialNum;
            this.qaPhotoItem = qaPhotoItem;
            this.qaNameItem = qaNameItem;
            this.qaGradeCount = qaGradeCount;
        }

    }
}