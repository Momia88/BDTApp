package com.coretronic.bdt;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import java.io.*;


/**
 * Created by darren on 2014/10/20.
 */
public class FilePostTest extends Activity  {
    EditText keywordET;
    Button button_send;
    String log = "Hello! Tommy~~你好 \n sd \n asdsff";

    private RequestParams params = null;
    private AsyncHttpClient client = null;
    private String TAG = FilePostTest.class.getSimpleName();
    public JsonHttpResponseHandler jsonHandler = new JsonHttpResponseHandler() {
        public void onSuccess(int statusCode, org.apache.http.Header[] headers, org.json.JSONArray response) {
        /* compiled code.. */
            Log.i(TAG, "onSuccess = " + response);
        }

        public void onFailure(int statusCode, org.apache.http.Header[] headers, java.lang.String responseString, java.lang.Throwable throwable) {
        /* compiled code.. */
            Log.i(TAG, "onFailure");
        }

    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.file_post);
        keywordET = (EditText) findViewById(R.id.keywordET);
        button_send = (Button)findViewById(R.id.button_send);
        button_send.setOnClickListener(send_post);
        save();

    }

    public void save()
    {
        try {
            FileOutputStream outStream=this.openFileOutput("a.txt",Context.MODE_APPEND);
            outStream.write(log.getBytes());
            outStream.close();
        } catch (FileNotFoundException e) {
            return;
        }
        catch (IOException e){
            return ;
        }
    }



    private View.OnClickListener send_post = new View.OnClickListener()
    {
        @Override
        public void onClick(View v) {

            load();

        }


    };

    public void load()
    {
        try {
            FileInputStream inStream=this.openFileInput("a.txt");
            ByteArrayOutputStream stream=new ByteArrayOutputStream();
            byte[] buffer=new byte[1024];
            int length=-1;
            while((length=inStream.read(buffer))!=-1) {
                stream.write(buffer,0,length);
            }
            stream.close();
            inStream.close();
            Log.i(TAG, "==content: ==:" + stream.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e){
            return ;
        }
    }


}
