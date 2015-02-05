package com.coretronic.bdt.WalkWay.Module;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.coretronic.bdt.AppConfig;
import com.coretronic.bdt.R;
import com.coretronic.bdt.WalkWay.WalkWayListDetailInfo;
import com.google.gson.Gson;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Called when the activity is first created.
 */
public class WalkWayListAdapter extends BaseAdapter {
    private String TAG = WalkWayListAdapter.class.getSimpleName();
    private List<WalkwayInfo> walkwayInfos;
    private Context context;
    private LayoutInflater inflater;
    private Gson gson = new Gson();

    //transactionLog
    private String currentTime;
    private String append;
    private String uuidChose;
    private SharedPreferences sharedPreferences;

    public WalkWayListAdapter(Context context, List<WalkwayInfo> infos) {
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.walkwayInfos = infos;
        sharedPreferences = context.getSharedPreferences(AppConfig.SHAREDPREFERENCES_NAME, 0);
        uuidChose = sharedPreferences.getString(AppConfig.PREF_UNIQUE_ID, "");

    }

    @Override
    public int getCount() {
        return walkwayInfos.size();
    }

    @Override
    public Object getItem(int position) {
        return walkwayInfos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.walkway_list_item, null);
            viewHolder = new ViewHolder();
            viewHolder.title = (TextView) convertView.findViewById(R.id.txt_title);
            viewHolder.subTitle = (TextView) convertView.findViewById(R.id.txt_subTitle);
            viewHolder.feature = (TextView) convertView.findViewById(R.id.txt_feature);
            viewHolder.address = (TextView) convertView.findViewById(R.id.txt_walkway_address);
            viewHolder.carPark = (TextView) convertView.findViewById(R.id.txt_park_available);
            viewHolder.distance = (TextView) convertView.findViewById(R.id.txt_distance);
            viewHolder.btnNext = (Button) convertView.findViewById(R.id.btn_walkway_know_more);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.title.setText(walkwayInfos.get(position).getWalkwayName());
        viewHolder.subTitle.setText(walkwayInfos.get(position).getWalkwayTitle());
        viewHolder.feature.setText(walkwayInfos.get(position).getWalkwayFeature());
        viewHolder.address.setText(walkwayInfos.get(position).getWalkwayAddress());
        viewHolder.carPark.setText(walkwayInfos.get(position).getParking());
        viewHolder.distance.setText(walkwayInfos.get(position).getKilometers());
        viewHolder.btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                String json = gson.toJson(walkwayInfos.get(position), WalkwayInfo.class);
                callSystemTime();
                append = currentTime + "," +
                        uuidChose + "," +
                        "WalkWayInfoList" + "," +
                        walkwayInfos.get(position).getWalkwayId() + "," +
                        "btnWalkWayKnowMore" + "\n";
                transactionLogSave(append);
                Fragment fragment = new WalkWayListDetailInfo();
                Fragment cuttentFragment = ((FragmentActivity)context).getSupportFragmentManager().findFragmentByTag("WalkWayInfoList");
                if (fragment != null) {
                    Bundle bundle = new Bundle();
                    bundle.putString("WalkWayId", walkwayInfos.get(position).getWalkwayId());
                    fragment.setArguments(bundle);
                    ((FragmentActivity)context).getSupportFragmentManager().beginTransaction()
                            .hide(cuttentFragment)
                            .add(R.id.frame_container, fragment,"WalkWayDetailInfo")
                            .addToBackStack("WalkWayDetailInfo")
                            .commit();
                } else {
                    Log.e(TAG, "Error in creating fragment");
                }
            }
        });
        return convertView;
    }




    public static class ViewHolder {
        public TextView title;
        public TextView subTitle;
        public TextView feature;
        public TextView address;
        public TextView carPark;
        public TextView distance;
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