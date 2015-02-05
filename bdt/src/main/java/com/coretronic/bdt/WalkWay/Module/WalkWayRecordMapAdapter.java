package com.coretronic.bdt.WalkWay.Module;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.coretronic.bdt.AppConfig;
import com.coretronic.bdt.R;
import com.coretronic.bdt.WalkWay.WalkWayAreaDetial;
import com.google.gson.Gson;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Called when the activity is first created.
 */
public class WalkWayRecordMapAdapter extends BaseAdapter {
    private String TAG = WalkWayRecordMapAdapter.class.getSimpleName();
    private List<String> mapArea;
    private TypedArray mapAreaIconTypeDeep;
    private TypedArray mapAreaIconTypeLight;
    private TypedArray mapPercentTypeDeep;
    private TypedArray mapPercentTypeLight;

    private List<WalkWayAreaInfo> recordMapInfos;
    private Context context;
    private LayoutInflater inflater;
    private Gson gson = new Gson();

    //transactionLog
    private String currentTime;
    private String append;
    private String uuidChose;
    private SharedPreferences sharedPreferences;


    public WalkWayRecordMapAdapter(Context context, List<WalkWayAreaInfo> infos) {
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        sharedPreferences = context.getSharedPreferences(AppConfig.SHAREDPREFERENCES_NAME, 0);
        uuidChose = sharedPreferences.getString(AppConfig.PREF_UNIQUE_ID, "");
        Resources rs = context.getResources();
        mapArea = Arrays.asList(rs.getStringArray(R.array.ww_map_area));
        mapAreaIconTypeDeep = rs.obtainTypedArray(R.array.ww_map_typearray_deep);
        mapAreaIconTypeLight = rs.obtainTypedArray(R.array.ww_map_typearray_light);
        mapPercentTypeDeep = rs.obtainTypedArray(R.array.ww_percent_typearray_deep);
        mapPercentTypeLight = rs.obtainTypedArray(R.array.ww_percent_typearray_light);
        this.recordMapInfos = infos;

    }

    @Override
    public int getCount() {
        return recordMapInfos.size();
    }

    @Override
    public Object getItem(int position) {
        return recordMapInfos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.walkway_record_map_item, null);
            viewHolder = new ViewHolder();
            viewHolder.layout = (LinearLayout) convertView.findViewById(R.id.ww_area_item_layout);
            viewHolder.imgIcon = (ImageView) convertView.findViewById(R.id.ww_img_tw_map);
            viewHolder.percentIcon = (ImageView) convertView.findViewById(R.id.ww_img_percent);
            viewHolder.areaName = (TextView) convertView.findViewById(R.id.ww_lb_tw_area);
            viewHolder.walkwayNum = (TextView) convertView.findViewById(R.id.ww_lb_walkway_num);
            viewHolder.btnNext = (Button) convertView.findViewById(R.id.ww_btn_record_next);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            viewHolder.layout.setBackgroundColor(convertView.getResources().getColor(R.color.white));

        }
        final WalkWayAreaInfo walkWayAreaInfo = recordMapInfos.get(position);
        String areaName = walkWayAreaInfo.getPartArea();
        viewHolder.areaName.setText(areaName);
        // resource position
        int i = mapArea.indexOf(areaName);
        // percent
//        int wwVisited = walkWayAreaInfo.getVisiteds();
        int wwTotal = walkWayAreaInfo.getWalkways();
        int wwVisited = walkWayAreaInfo.getVisiteds();
        int percent = 0;
        if(wwTotal != 0) {
            percent = (int) (((float) wwVisited / (float) wwTotal) * 10);
        }
        Log.d(TAG, "percent:" + percent);
        if ((position % 2) == 0) {
            viewHolder.imgIcon.setImageResource(mapAreaIconTypeLight.getResourceId(i, -1));
            viewHolder.percentIcon.setImageResource(mapPercentTypeLight.getResourceId(percent, -1));
        } else {
            viewHolder.layout.setBackgroundColor(convertView.getResources().getColor(R.color.block_ded6de));
            viewHolder.imgIcon.setImageResource(mapAreaIconTypeDeep.getResourceId(i, -1));
            viewHolder.percentIcon.setImageResource(mapPercentTypeDeep.getResourceId(percent, -1));
        }
        walkWayAreaInfo.setMapIconRid(mapAreaIconTypeLight.getResourceId(i, -1));
        walkWayAreaInfo.setPercentRid(mapPercentTypeLight.getResourceId(percent, -1));

        viewHolder.walkwayNum.setText(wwVisited + "/" + wwTotal);
        viewHolder.btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new WalkWayAreaDetial();
                callSystemTime();
                append = currentTime + "," +
                        uuidChose + "," +
                        "WalkWayRecordMap" + "," +
                        "WalkWayRecordMapDetial" + "," +
                        "btnWalkWayRecordMapknowMore" + "\n";
                transactionLogSave(append);
                if (fragment != null) {
                    Bundle bundle = new Bundle();
                    bundle.putString("WalkWayMapArea", gson.toJson(walkWayAreaInfo));
                    fragment.setArguments(bundle);
                    ((FragmentActivity) context).getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frame_container, fragment, "WalkWayRecordMapArea")
                            .addToBackStack("WalkWayRecordMapArea")
                            .commit();
                } else {
                    Log.e(TAG, "Error in creating fragment");
                }
            }
        });
        return convertView;
    }


    public static class ViewHolder {
        public LinearLayout layout;
        public ImageView imgIcon;
        public ImageView percentIcon;
        public TextView areaName;
        public TextView walkwayNum;
        public Button btnNext;
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