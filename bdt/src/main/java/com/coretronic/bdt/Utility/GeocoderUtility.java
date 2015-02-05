package com.coretronic.bdt.Utility;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by james on 14/10/21.
 */
public class GeocoderUtility {
    private final String TAG = GeocoderUtility.class.getSimpleName();

    private Activity context = null;

    public  GeocoderUtility(Activity mContext)
    {
        context = mContext;
    }

    /**
     * 透過經度取得地址
     * @param lat
     * @param lng
     * @param type: 0地區( ex:竹南鎮) 1:縣市 2:全部地址
     * @return 指定type的內容，找不到回傳""
     */
    public  String getGeocoder(double lat, double lng, int type) {
        String result = "";
        Log.i("info:","Geocoder. isPresent():"+Geocoder.isPresent());
        if( lat == 0.0 || lng == 0.0)
        {
            return result;
        }

        try {


            Geocoder gc = new Geocoder(context, Locale.TRADITIONAL_CHINESE);
            List<Address> lstAddress = null;

            lstAddress = gc.getFromLocation(lat, lng, 1);
            Log.i("info","lstAddress:"+lstAddress);
            gc = null;
            switch (type)
            {
                case 0:
                result = lstAddress.get(0).getLocality();
                break;
                case 1:
                    result = lstAddress.get(0).getAdminArea();
                    break;
                case 2:
                    result = lstAddress.get(0).getAddressLine(0);
                    break;
                case 3:
                    result = lstAddress.get(0).getLocality() +";"+ lstAddress.get(0).getAdminArea();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("error", "error:" + e.getMessage());
            result = "";
        }

        return result;
    }

}
