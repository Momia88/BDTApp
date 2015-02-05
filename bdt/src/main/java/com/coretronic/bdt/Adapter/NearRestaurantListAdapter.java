package com.coretronic.bdt.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.coretronic.bdt.DataModule.RestaurantDataInfo;
import com.coretronic.bdt.R;

import java.util.HashMap;

/**
 * Created by darren on 2014/12/9.
 */
public class NearRestaurantListAdapter extends BaseAdapter{
    private Context context = null;
    private LayoutInflater layoutInflater;
    private String TAG = NearRestaurantListAdapter.class.getSimpleName();
    private RestaurantDataInfo restaurantDataInfo = null;
    private HashMap<String, Integer> calcStar;

    public NearRestaurantListAdapter(Context context, RestaurantDataInfo restaurantDataInfo)
    {
        this.context = context;
        this.restaurantDataInfo = restaurantDataInfo;
        this.layoutInflater = LayoutInflater.from(context);
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        Log.i(TAG, "getCount:" + restaurantDataInfo.getResult().size());
        return restaurantDataInfo.getResult().size();
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        ImageView[]star=new ImageView[5];
        int[] iResId={R.id.img_star_one,R.id.img_star_two,R.id.img_star_three,R.id.img_star_four,R.id.img_star_five};
        int i;
        Log.i(TAG, "Into getView ");
        ViewTag viewTag;
        if (convertView == null) {
            Log.i(TAG, "convertView == null ");
            convertView = layoutInflater.inflate(R.layout.near_restaurant_item, null);
            viewTag = new ViewTag(
                    (TextView) convertView.findViewById(R.id.restaurant_name),
//                    (TextView) convertView.findViewById(R.id.img_star_count),
                    (TextView) convertView.findViewById(R.id.star_rating),
                    (TextView) convertView.findViewById(R.id.location_distance),
                    (TextView) convertView.findViewById(R.id.restaurant_address),
                    (TextView) convertView.findViewById(R.id.restaurant_phone),
                    (ImageView) convertView.findViewById(R.id.img_star_one),
                    (ImageView) convertView.findViewById(R.id.img_star_two),
                    (ImageView) convertView.findViewById(R.id.img_star_three),
                    (ImageView) convertView.findViewById(R.id.img_star_four),
                    (ImageView) convertView.findViewById(R.id.img_star_five));

            convertView.setTag(viewTag);
        }
        else
        {
            viewTag = (ViewTag) convertView.getTag();
        }
        Log.i(TAG,"position:"+position);

        viewTag.textView_name.setText(restaurantDataInfo.getResult().get(position).getName());
//      viewTag.textView_count.setText(restaurantDataInfo.getResult().get(position).get);
        viewTag.textView_rating.setText(restaurantDataInfo.getResult().get(position).getMeanRate()+" 顆星");
        viewTag.textView_distance.setText("距離"+restaurantDataInfo.getResult().get(position).getDist()+" 公里");
        viewTag.textView_address.setText(restaurantDataInfo.getResult().get(position).getAddress());
//      viewTag.textView_phone.setText(restaurantDataInfo.getResult().get(position).getPhone());
        String str = restaurantDataInfo.getResult().get(position).getMeanRate();
        if(str.equalsIgnoreCase("NULL")||str.equalsIgnoreCase("")){
            viewTag.textView_rating.setText((R.string.no_data_info) );
            for(i=0;i<5;i++){
                star[i]=(ImageView) convertView.findViewById(iResId[i]);
                star[i].setImageResource(R.drawable.near_restaurant_star_null);
            }
        }else{
            float MeanRate = Float.parseFloat(str);//MeanRate=null會有錯誤
            int star_int=(int)MeanRate;
            float star_decimal=MeanRate-star_int;
            for(i=0;i<star_int;i++){
                star[i]=(ImageView) convertView.findViewById(iResId[i]);
                star[i].setImageResource(R.drawable.near_restaurant_star_full);
            }
            for(i=star_int+1;i<5;i++){
                star[i]=(ImageView) convertView.findViewById(iResId[i]);
                star[i].setImageResource(R.drawable.near_restaurant_star_null);
            }

            //判斷小數點的星星
            star[star_int]=(ImageView) convertView.findViewById(iResId[star_int]);
            if(star_decimal<0.3){
                star[star_int].setImageResource(R.drawable.near_restaurant_star_null);
            }
            else if(star_decimal>0.3&&star_decimal<0.8){
                star[star_int].setImageResource(R.drawable.near_restaurant_star_half);
            }
            else{
                star[star_int].setImageResource(R.drawable.near_restaurant_star_full);
            }


        }

        //啟動google map傳入經緯度
        viewTag.textView_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String lat = restaurantDataInfo.getResult().get(position).getLat();
                String lng = restaurantDataInfo.getResult().get(position).getLng();
                String name = restaurantDataInfo.getResult().get(position).getName();
                Log.i(TAG,"lat: " + lat);
                Log.i(TAG,"lng: " + lng);
//                String strUri = "geo:"+lat+","+lng+"?z="+15;
                String strUri = "geo:"+lat+","+lng+"?q="+lat+","+lng+"("+name+")";
//                String strUri = "geo:<lat>,<long>?q=<lat>,<long>(Label+Name)";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(strUri));
                context.startActivity(intent);
            }
        });

//      判斷電話號碼是否為NULL
        if (restaurantDataInfo.getResult().get(position).getPhone().equalsIgnoreCase("NULL")) {
            viewTag.textView_phone.setText((R.string.nullphone) );
        }

//        播打電話
        else {
            viewTag.textView_phone.setText("電話:   " + restaurantDataInfo.getResult().get(position).getPhone());
            viewTag.textView_phone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder askCalloutDialog = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.customAlertDialog));
                    LayoutInflater factory = LayoutInflater.from(context);
                    View customAlertViewLayout = factory.inflate(R.layout.custom_alertdialog_view, null);
                    askCalloutDialog.setView(customAlertViewLayout);
                    TextView customeAlertTV = (TextView) customAlertViewLayout.findViewById(R.id.msgTV);
                    customeAlertTV.setText((R.string.dialog_msg_callout_ask));


                    askCalloutDialog.setPositiveButton((R.string.dialog_msg_callout_cancel), null);
                    askCalloutDialog.setNegativeButton((R.string.dialog_msg_callout), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Uri uri = Uri.parse("tel:" + restaurantDataInfo.getResult().get(position).getPhone());
                            Intent calloutIntent = new Intent();
                            calloutIntent.setAction(Intent.ACTION_CALL);
                            calloutIntent.setData(uri);
                            context.startActivity(calloutIntent);
                        }
                    });
                    askCalloutDialog.show();
                }
            });
        }


        return convertView;
    }

//    private HashMap<String, Integer> calcStar(float MeanRate) {
//        HashMap<String, Integer> map = new HashMap<String, Integer>();
//        final int totalNum = 5;
//
//        int yellowNum, halfNum, whiteNum;
//
//        yellowNum = (int)Math.floor(MeanRate);
//
//        halfNum = 0;
//        if (MeanRate >= 0.3 && MeanRate <= 0.7) {
//            halfNum = 1;
//        }
//
//
//        whiteNum = totalNum - yellowNum - halfNum;
//
//        map.put("yellow", yellowNum);
//        map.put("half", halfNum);
//        map.put("white", whiteNum);
//
//        Integer yellowNum1 = map.get("yellow"); // get 2
//
//        return map;
//    }

    class ViewTag
    {
        TextView textView_name = null;
        //        TextView textView_count = null;
        TextView textView_rating = null;
        TextView textView_distance = null;
        TextView textView_address = null;
        TextView textView_phone = null;
        ImageView image_star_one=null;
        ImageView image_star_two=null;
        ImageView image_star_three=null;
        ImageView image_star_four=null;
        ImageView image_star_five=null;
        public ViewTag(TextView textView_name,
//                       TextView textView_count,
                       TextView textView_rating,
                       TextView textView_distance,
                       TextView textView_address,
                       TextView textView_phone,
                       ImageView image_star_one,
                       ImageView image_star_two,
                       ImageView image_star_three,
                       ImageView image_star_four,
                       ImageView image_star_five)
        {
            this.textView_name = textView_name;
//            this.textView_count = textView_count;
            this.textView_rating = textView_rating;
            this.textView_distance = textView_distance;
            this.textView_address = textView_address;
            this.textView_phone = textView_phone;
            this.image_star_one=image_star_one;
            this.image_star_two=image_star_two;
            this.image_star_three=image_star_three;
            this.image_star_four=image_star_four;
            this.image_star_five=image_star_five;

        }
    }

}