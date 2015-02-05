package com.coretronic.bdt.Utility;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import com.coretronic.bdt.WalkWay.Diary.DiaryMissionStep5;
import com.google.android.gms.maps.model.LatLng;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;

/**
 * Created by changyuanyu on 14/12/20.
 */
public class GetAddressPositionTask extends
        AsyncTask<String, Integer, LatLng> {
    private final String TAG = GetAddressPositionTask.class.getSimpleName();
    private Context context;
    private int type;
    public GetAddressPositionTask(Context context,int type)
    {
        this.context = context;
        this.type = type;
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected LatLng doInBackground(String... plookupString) {

        String lookupString = plookupString[0];
        final String lookupStringUriencoded = Uri.encode(lookupString);
        LatLng position = null;
        Geocoder geocoder = new Geocoder(context, Locale.TRADITIONAL_CHINESE);
        // best effort zoom
        try {
            if (geocoder != null) {
                List<Address> addresses = geocoder.getFromLocationName(
                        lookupString, 1);
                if (addresses != null && !addresses.isEmpty()) {
                    Address first_address = addresses.get(0);
                    position = new LatLng(first_address.getLatitude(),
                            first_address.getLongitude());
                }
            } else {
                Log.e(TAG, "geocoder was null, is the module loaded? "
                        );
            }

        } catch (IOException e) {
            Log.e(TAG, "geocoder failed, moving on to HTTP");
        }
        // try HTTP lookup to the maps API
        if (position == null) {
            HttpGet httpGet = new HttpGet(
                    "http://maps.google.com/maps/api/geocode/json?address="
                            + lookupStringUriencoded + "&sensor=true");
            HttpClient client = new DefaultHttpClient();
            HttpResponse response;
            StringBuilder stringBuilder = new StringBuilder();

            try {
                response = client.execute(httpGet);
                HttpEntity entity = response.getEntity();
                InputStream stream = entity.getContent();
                int b;
                while ((b = stream.read()) != -1) {
                    stringBuilder.append((char) b);
                }
            } catch (ClientProtocolException e) {
            } catch (IOException e) {
            }

            JSONObject jsonObject = new JSONObject();
            try {
                // Log.d("MAPSAPI", stringBuilder.toString());

                try {
                    jsonObject = new JSONObject(stringBuilder.toString());
                    Log.i(TAG, "location jsonObject:"+jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (jsonObject.getString("status").equals("OK")) {
                    jsonObject = jsonObject.getJSONArray("results")
                            .getJSONObject(0);
                    jsonObject = jsonObject.getJSONObject("geometry");
                    jsonObject = jsonObject.getJSONObject("location");
                    String lat = jsonObject.getString("lat");
                    String lng = jsonObject.getString("lng");

                    // Log.d("MAPSAPI", "latlng " + lat + ", "
                    // + lng);
                    Log.i(TAG,"josnObject location:"+jsonObject.getJSONObject("location"));
                    position = new LatLng(Double.valueOf(lat),
                            Double.valueOf(lng));
                }

            } catch (JSONException e) {
                Log.e(TAG, e.getMessage(), e);
            }

        }
        return position;
    }

    @Override
    protected void onPostExecute(LatLng result) {
        super.onPostExecute(result);
    }

};