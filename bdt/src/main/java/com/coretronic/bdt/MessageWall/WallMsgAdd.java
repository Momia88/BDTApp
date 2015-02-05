package com.coretronic.bdt.MessageWall;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.coretronic.bdt.AppConfig;
import com.coretronic.bdt.AppUtils;
import com.coretronic.bdt.R;
import com.coretronic.bdt.Utility.CustomerOneBtnAlertDialog;
import com.coretronic.bdt.Utility.CustomerTwoBtnAlertDialog;
import com.coretronic.bdt.Utility.PickPhotoUtility;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Morris on 2014/10/17.
 */
public class WallMsgAdd extends Fragment {
    private String TAG = WallMsgAdd.class.getSimpleName();
    private Context mContext;
    private Button btnBack;
    private TextView barTitle;
    private RelativeLayout navigationBar;
    private SharedPreferences sharedPreferences;
    private ProgressDialog progressDialog = null;
    private String uuidChose;
    private AsyncHttpClient asyncHttpClient;
    private Gson gson = new Gson();

    // view item
    private EditText mTxtMsgShare;
    private Button mBtnPhotoAdd;
    private LinearLayout mImgListView;
    private Button mBtnSummit;

    private final int PHOTO_REQUEST_TAKEPHOTO = 1;
    private final int PHOTO_REQUEST_GALLERY = 2;


    private float density = 1;
    private Bitmap bitmap;
    private List<File> fileList;
    private List<String> imageNameList;

    private String filePath;
    private String fileName = null;

    // camera photo path /DCIM/Camera
    private String cameraFileName = null;
    private Uri extraOutputFilePathUri = null;
    private CustomerOneBtnAlertDialog oneBtnAlertDialog;
    private CustomerTwoBtnAlertDialog twoBtnAlertDialog;

    public JsonHttpResponseHandler jsonHandler = new JsonHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
            Log.d(TAG, "onSuccess = " + response);
            try {
                if (response.get("msgCode").equals("A01")) {
                    oneBtnAlertDialog.setMsg("傳送成功")
                            .setPositiveListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    oneBtnAlertDialog.dismiss();
                                    String str = mContext.getExternalCacheDir() + filePath;
                                    deleteAllFileInFolder(str);
                                    getActivity().getFragmentManager().popBackStackImmediate();
                                }
                            }).show();
                } else {
                    oneBtnAlertDialog = AppUtils.getAlertDialog(mContext, getString(R.string.send_data_error));
                    oneBtnAlertDialog.show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (JsonSyntaxException e) {
                oneBtnAlertDialog = AppUtils.getAlertDialog(mContext, getString(R.string.send_data_error));
                oneBtnAlertDialog.show();
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            Log.d(TAG, "onFailure");
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
            oneBtnAlertDialog = AppUtils.getAlertDialog(mContext, getString(R.string.data_load_error));
            oneBtnAlertDialog.show();
        }
    };

    public AsyncHttpResponseHandler fileHandler = new AsyncHttpResponseHandler() {


        @Override
        public void onProgress(int bytesWritten, int totalSize) {
            super.onProgress(bytesWritten, totalSize);
//            Log.d(TAG, "bytesWritten:" + bytesWritten);
//            Log.d(TAG, "totalSize:" + totalSize);

        }

        @Override
        public void onSuccess(int i, Header[] headers, byte[] bytes) {
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
            try {
                Log.d(TAG, "onSuccess:" + new String(bytes, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            insertWallMsg();
        }

        @Override
        public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
            try {
                Log.d(TAG, "onFailure:" + new String(bytes, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFinish() {
            super.onFinish();
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
            Log.d(TAG, "onFinish:");

        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getActivity().getSharedPreferences(AppConfig.SHAREDPREFERENCES_NAME, 0);
        asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.setTimeout(AppConfig.TIMEOUT);
        uuidChose = sharedPreferences.getString(AppConfig.PREF_UNIQUE_ID, "");
        fileList = new ArrayList<File>();
        imageNameList = new ArrayList<String>();
        filePath = AppConfig.JPEG_WALL_IMAGE_FOLDER;
        try {
            File dir = new File(filePath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        deleteAllFileInFolder(filePath);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.wall_msg_add_layout, container, false);
        mContext = v.getContext();
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        density = metrics.density;
        Log.d(TAG, "density: " + density);
        progressDialog = new ProgressDialog(mContext);
        oneBtnAlertDialog = new CustomerOneBtnAlertDialog(mContext);
        twoBtnAlertDialog = new CustomerTwoBtnAlertDialog(mContext);
        initView(v);
        return v;
    }

    private void initView(View v) {
        // navigation bar
        navigationBar = (RelativeLayout) v.findViewById(R.id.navigationBar_option);
        btnBack = (Button) v.findViewById(R.id.btnBack);
        barTitle = (TextView) v.findViewById(R.id.action_bar_title);

        barTitle.setText(getString(R.string.pp_add_article_title));
        btnBack.setOnClickListener(btnListener);

        // button
        mTxtMsgShare = (EditText) v.findViewById(R.id.txtMsgShare);
        mBtnPhotoAdd = (Button) v.findViewById(R.id.btnPhotoAdd);
        mImgListView = (LinearLayout) v.findViewById(R.id.imgListView);
        mBtnSummit = (Button) v.findViewById(R.id.btnSummit);
        mBtnSummit.setOnClickListener(btnListener);
        mBtnPhotoAdd.setOnClickListener(btnListener);
    }


    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        if(oneBtnAlertDialog != null){
            oneBtnAlertDialog.dismiss();
        }
        if(twoBtnAlertDialog != null){
            twoBtnAlertDialog.dismiss();
        }
        if ((bitmap != null) && (!bitmap.isRecycled())) {
            bitmap.recycle();
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult");
        switch (requestCode) {
            case PHOTO_REQUEST_TAKEPHOTO:
                try {
                    Log.d(TAG, "PHOTO_REQUEST_TAKEPHOTO");
                    Log.d(TAG, extraOutputFilePathUri.getPath());
                    String path = saveTransportPhoto(extraOutputFilePathUri.getPath());
                    if (path != null) {
                        addImage(path);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case PHOTO_REQUEST_GALLERY:
                Log.d(TAG, "PHOTO_REQUEST_GALLERY");
                try {
                    if (data != null) {
                        Log.d(TAG, "data != null");
                        Log.d(TAG, data.getData().getPath());
                        String photoPath = PickPhotoUtility.getPath(mContext, data.getData());
                        String fPath = saveTransportPhoto(photoPath);
                        if (fPath != null) {
                            addImage(fPath);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;


        }
        super.onActivityResult(requestCode, resultCode, data);

    }


    private void addImage(String imgPath) {
        int reqWidth = (int) (150 * density);
        int reqHeight = (int) (150 * density);
        bitmap = AppUtils.rotateAndResizeBitmap(imgPath, reqWidth, reqHeight);
        ImageView imageView = new ImageView(mContext);
        imageView.setPadding(10, 10, 10, 10);
        imageView.setImageBitmap(bitmap);
        if (bitmap != null) {
            mImgListView.addView(imageView);
        }
        bitmap = null;
    }

    View.OnClickListener btnListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btnBack:
                    Log.i(TAG, "btnBack");
                    getActivity().getFragmentManager().popBackStackImmediate();
                    break;
                case R.id.btnPhotoAdd:
                    addPicDialog();
                    break;
                case R.id.btnSummit:
                    updateFile();
                    break;
            }

        }
    };

    //提示對話框方法
    private void addPicDialog() {
        twoBtnAlertDialog.setMsg("加上圖片")
                .setPositiveBtnText("拍照")
                .setPositiveListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        twoBtnAlertDialog.dismiss();
                        // 調用系統的拍照功能
                        cameraFileName = AppUtils.getCameraSystemTimeFileName(AppConfig.JPEG_FILE_PREFIX) + AppConfig.JPEG_FILE_SUFFIX;
                        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        extraOutputFilePathUri = AppUtils.getPhotoUri(cameraFileName);
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, extraOutputFilePathUri);
                        if (cameraIntent.resolveActivity(mContext.getPackageManager()) != null) {
                            startActivityForResult(cameraIntent, PHOTO_REQUEST_TAKEPHOTO);
                        } else {
                            Toast.makeText(mContext, "無照相app", 1).show();
                        }
                    }
                })
                .setNegativeBtnText("相冊")
                .setNegativeListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        twoBtnAlertDialog.dismiss();
                        Intent intent = new Intent(Intent.ACTION_PICK, null);
                        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                        startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
                    }
                })
                .show();
//        new AlertDialog.Builder(getActivity())
//                .setTitle("加上圖片")
//                .setPositiveButton("拍照", new DialogInterface.OnClickListener() {
//
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                        // 調用系統的拍照功能
//                        cameraFileName = AppUtils.getCameraSystemTimeFileName(AppConfig.JPEG_FILE_PREFIX) + AppConfig.JPEG_FILE_SUFFIX;
//                        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                        extraOutputFilePathUri = AppUtils.getPhotoUri(cameraFileName);
//                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, extraOutputFilePathUri);
//                        if (cameraIntent.resolveActivity(mContext.getPackageManager()) != null) {
//                            startActivityForResult(cameraIntent, PHOTO_REQUEST_TAKEPHOTO);
//                        } else {
//                            AppUtils.getAlertDialog(mContext, "無照相app").show();
//                        }
//                    }
//                })
//                .setNegativeButton("相冊", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                        Intent intent = new Intent(Intent.ACTION_PICK, null);
//                        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
//                        startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
//                    }
//                }).show();
    }

    private String saveTransportPhoto(String fPath) {
        int reqPhotoWidth = AppConfig.PHOTO_SIZE_1024;
        int reqPhotoHeight = AppConfig.PHOTO_SIZE_1024;
        // resize
        Bitmap tempBitmap = AppUtils.rotateAndResizeBitmap(fPath, reqPhotoWidth, reqPhotoHeight);
        fileName = uuidChose + "-" + System.currentTimeMillis() + ".png";
        // save file
        File f = AppUtils.savePhotoToCacheDir(mContext, filePath, fileName, tempBitmap);
        if (f != null) {
            fileList.add(f);
            imageNameList.add(fileName);
            Log.d(TAG, "fileName:" + fileName);
            Log.d(TAG, "fileList:" + f.getAbsolutePath());
            return f.getAbsolutePath();
        } else {
            return null;
        }
    }

    private void insertWallMsg() {
        progressDialog.setMessage(getString(R.string.dialog_download_msg));
        progressDialog.show();
        final String url = AppConfig.DOMAIN_SITE_PATE + AppConfig.INSERT_COMMENT;
        Log.i(TAG, "url:  " + url);
        uuidChose = sharedPreferences.getString(AppConfig.PREF_UNIQUE_ID, "");
        Log.i(TAG, "UUID:  " + uuidChose);
        RequestParams params = new RequestParams();
        params.add("uid", uuidChose);
        params.add("time", AppUtils.getSystemTime());
        params.add("comment", mTxtMsgShare.getText().toString());
        // new TypeToken<List<String>>(){}.getType()
        params.add("images", gson.toJson(imageNameList));
        params.add("articleType", "comment");
        Log.i(TAG, "params:  " + params.toString());

        asyncHttpClient.post(url, params, jsonHandler);
    }

    private void updateFile() {
        progressDialog.setMessage(getString(R.string.dialog_download_msg));
        progressDialog.show();
        final String url = AppConfig.FILE_UPLOAD_PATH;
        Log.i(TAG, "url:  " + url);
        uuidChose = sharedPreferences.getString(AppConfig.PREF_UNIQUE_ID, "");
        try {
            RequestParams params = new RequestParams();
            for (int i = 0; i < fileList.size(); i++) {
                params.put("file[" + i + "]", fileList.get(i));
            }
            Log.i(TAG, "params:  " + params.toString());

            asyncHttpClient.post(url, params, fileHandler);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void deleteAllFileInFolder(String path) {
        File dir = new File(path);
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                new File(dir, children[i]).delete();
            }
        }
    }

}