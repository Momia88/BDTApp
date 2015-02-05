package com.coretronic.bdt.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.coretronic.bdt.DataModule.DailyStepRecord;
import com.coretronic.bdt.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.text.ParseException;


/**
 * Called when the activity is first created.
 */
public class PersonalStepRecordBaseAdapter extends BaseAdapter {
    private LayoutInflater layoutInflater;
    private Context context;
    private DailyStepRecord dailyStepRecord;
    private int position;
    private String TAG = PersonalStepRecordBaseAdapter.class.getSimpleName();
//    private LinearLayout linearLayout;
//    private String dateFormat;


    public PersonalStepRecordBaseAdapter(Context context, DailyStepRecord dailyStepRecord) {
        this.context = context;

        this.dailyStepRecord = dailyStepRecord;
        this.layoutInflater = LayoutInflater.from(context);

        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        Log.i(TAG, "getCount:" + dailyStepRecord.getResult().size());
        return dailyStepRecord.getResult().size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.i(TAG, "into getView");
        ViewTag viewTag;

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.person_step_record_item, null);
            viewTag = new ViewTag(
                    (TextView) convertView.findViewById(R.id.date),
                    (TextView) convertView.findViewById(R.id.daily_count),
                    (LinearLayout) convertView.findViewById(R.id.backRound_color));
            convertView.setTag(viewTag);
            Log.i(TAG, "=========1=========");

        }
        else
        {
            viewTag = (ViewTag) convertView.getTag();
            Log.i(TAG, "=========2=========");
        }
        Log.i(TAG,"position:"+position);


        //從model抓到的值為yyyy-MM-dd轉為yyyy/MM/dd
        String dateString = dailyStepRecord.getResult().get(position).getStart();
        Log.i(TAG, "dateString" + dateString);
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
        Date transDay = null;
        try {
            transDay = sdf1.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Log.i(TAG, "transDay1" + transDay);

        Date aa1 = transDay;
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy/MM/dd");
        String aa2 = sdf2.format(aa1);
        Log.i(TAG, "transDay2" + aa2);

        //西元轉換中國曆
        SimpleDateFormat sdf = new SimpleDateFormat("MMMMdd日 EEEE");
        DateFormat df = DateFormat.getDateInstance();
        Date date = null;
            try {
                date = df.parse(aa2);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        Log.i(TAG, "stringCalendar" + sdf.format(calendar.getTime()));

        //設定文字項目
        viewTag.dailyCount.setText(dailyStepRecord.getResult().get(position).getCount() + " 步");
        viewTag.date.setText(sdf.format(calendar.getTime()));

        //背景顏色區隔（奇數列 偶數列）
        if (position % 2 == 0) {
            viewTag.linearLayout.setBackgroundColor(Color.parseColor("#ffffffff"));

        }else {
            viewTag.linearLayout.setBackgroundColor(Color.parseColor("#5E3819"));
            viewTag.linearLayout.getBackground().setAlpha(80);
        }

        return convertView;
    }

    class ViewTag {
        TextView date = null;
        TextView dailyCount = null;
        LinearLayout linearLayout = null;

        public ViewTag(TextView date, TextView dailyCount, LinearLayout linearLayout) {
            this.date = date;
            this.dailyCount = dailyCount;
            this.linearLayout = linearLayout;
        }
    }
}