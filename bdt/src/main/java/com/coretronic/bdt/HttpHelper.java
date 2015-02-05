package com.coretronic.bdt;

import android.content.Context;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;

/**
 * Created by Morris on 2014/3/6.
 */
public class HttpHelper {
    private final String TAG = "HttpHelper";
    private final HttpClient httpClient;
    private Context mContext;

    public HttpHelper(Context mContext) {
        this.mContext = mContext;
        HttpParams params = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(params, 2000);
//        HttpConnectionParams.setSoTimeout(params, 1);
        httpClient = new DefaultHttpClient(params);
    }

    public String getResponse(String url) {
        try {
            HttpGet httpGet = new HttpGet(url);
            // response
            HttpResponse response = httpClient.execute(httpGet);
            HttpEntity resEntity = response.getEntity();
            return EntityUtils.toString(resEntity);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String postResponse(String url, ArrayList<NameValuePair> params) {
        try {
            HttpPost httpPost = new HttpPost(url);
            // set params
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, HTTP.UTF_8);
            httpPost.setEntity(entity);
            // response
            HttpResponse responsePOST = httpClient.execute(httpPost);
            HttpEntity resEntity = responsePOST.getEntity();
            return EntityUtils.toString(resEntity);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
