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
import android.widget.TextView;
import com.coretronic.bdt.DataModule.StoreDataInfo;
import com.coretronic.bdt.R;

/**
 * Created by imying.huang on 2014/12/10.
 */
public class NearStoreListAdapter extends BaseAdapter {
    private Context context = null;
    private LayoutInflater layoutInflater;
    private String TAG = NearStoreListAdapter.class.getSimpleName();
    private StoreDataInfo storeDataInfo = null;

    public NearStoreListAdapter(Context context, StoreDataInfo storeDataInfo)
    {
        this.context = context;
        this.storeDataInfo = storeDataInfo;
        this.layoutInflater = LayoutInflater.from(context);
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        Log.i(TAG, "getCount:" + storeDataInfo.getResult().size());
        return storeDataInfo.getResult().size();
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
        Log.i(TAG, "Into getView ");
        ViewTag viewTag;

        if (convertView == null) {
            Log.i(TAG, "convertView == null ");
            convertView = layoutInflater.inflate(R.layout.near_store_item, null);
            viewTag = new ViewTag(
                    (TextView) convertView.findViewById(R.id.restaurant_name),
//                    (TextView) convertView.findViewById(R.id.img_star_count),
//                    (TextView) convertView.findViewById(R.id.star_rating),
                    (TextView) convertView.findViewById(R.id.location_distance),
                    (TextView) convertView.findViewById(R.id.restaurant_address),
                    (TextView) convertView.findViewById(R.id.restaurant_phone));
            convertView.setTag(viewTag);
        }
        else
        {
            viewTag = (ViewTag) convertView.getTag();
        }
        Log.i(TAG,"position:"+position);

        viewTag.textView_name.setText(storeDataInfo.getResult().get(position).getName());
//        viewTag.textView_count.setText(restaurantDataInfo.getResult().get(position).get);
//        viewTag.textView_count.setVisibility(View.GONE);
//        viewTag.textView_rating.setVisibility(View.GONE);
//        viewTag.textView_rating.setText(storeDataInfo.getResult().get(position).getMeanRate()+" 顆星");
        viewTag.textView_distance.setText("距離"+storeDataInfo.getResult().get(position).getDist()+" 公里");
        viewTag.textView_address.setText(storeDataInfo.getResult().get(position).getAddress());
//        viewTag.textView_phone.setText(restaurantDataInfo.getResult().get(position).getPhone());

        //啟動google map傳入經緯度
        viewTag.textView_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String lat = storeDataInfo.getResult().get(position).getLat();
                String lng = storeDataInfo.getResult().get(position).getLng();
                String name = storeDataInfo.getResult().get(position).getName();
                Log.i(TAG,"lat: " + lat);
                Log.i(TAG,"lng: " + lng);
//                String strUri = "geo:"+lat+","+lng+"?z="+15;
                String strUri = "geo:"+lat+","+lng+"?q="+lat+","+lng+"("+name+")";
//                String strUri = "geo:<lat>,<long>?q=<lat>,<long>(Label+Name)";
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(strUri));
                context.startActivity(intent);
            }
        });

        //播打電話
        if (storeDataInfo.getResult().get(position).getPhone() != "NULL") {
            viewTag.textView_phone.setText("電話:   " + storeDataInfo.getResult().get(position).getPhone());
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
                            Uri uri = Uri.parse("tel:" + storeDataInfo.getResult().get(position).getPhone());
                            Intent calloutIntent = new Intent();
                            calloutIntent.setAction(Intent.ACTION_CALL);
                            calloutIntent.setData(uri);
                            context.startActivity(calloutIntent);
                        }
                    });
                    askCalloutDialog.show();
                }
            });
        } else {
            viewTag.textView_phone.setText((R.string.phone) + "  " + (R.string.no_data_info));
        }


        return convertView;
    }

    class ViewTag
    {
        TextView textView_name = null;
        //        TextView textView_count = null;
//        TextView textView_rating = null;
        TextView textView_distance = null;
        TextView textView_address = null;
        TextView textView_phone = null;
        public ViewTag(TextView textView_name,
//                       TextView textView_count,
//                       TextView textView_rating,
                       TextView textView_distance,
                       TextView textView_address,
                       TextView textView_phone)
        {
            this.textView_name = textView_name;
//            this.textView_count = textView_count;
//            this.textView_rating = textView_rating;
            this.textView_distance = textView_distance;
            this.textView_address = textView_address;
            this.textView_phone = textView_phone;
        }
    }

}