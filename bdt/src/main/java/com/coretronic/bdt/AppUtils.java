package com.coretronic.bdt;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.coretronic.bdt.Utility.CustomerOneBtnAlertDialog;
import com.coretronic.bdt.Utility.CustomerTwoBtnAlertDialog;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.graphics.BitmapFactory.Options;
import static android.graphics.BitmapFactory.decodeFile;
import static android.graphics.BitmapFactory.decodeResource;
import static android.graphics.BitmapFactory.decodeStream;

/**
 * Created by Morris on 2014/8/11.
 */
public class AppUtils {
    private static String TAG = AppUtils.class.getSimpleName();
    /*
    yyyy-MM-dd 1969-12-31
	yyyy-MM-dd 1970-01-01
	yyyy-MM-dd HH:mm 1969-12-31 16:00
	yyyy-MM-dd HH:mm 1970-01-01 00:00
	yyyy-MM-dd HH:mmZ 1969-12-31 16:00-0800
	yyyy-MM-dd HH:mmZ 1970-01-01 00:00+0000
	yyyy-MM-dd HH:mm:ss.SSSZ 1969-12-31 16:00:00.000-0800
	yyyy-MM-dd HH:mm:ss.SSSZ 1970-01-01 00:00:00.000+0000
	yyyy-MM-dd'T'HH:mm:ss.SSSZ 1969-12-31T16:00:00.000-0800
	yyyy-MM-dd'T'HH:mm:ss.SSSZ 1970-01-01T00:00:00.000+0000
    */
    public static String[] dateFormats = new String[]{
            "yyyy-MM-dd",
            "yyyy-MM-dd HH:mm",
            "yyyy-MM-dd HH:mmZ",
            "yyyy-MM-dd HH:mm:ss.SSSZ",
            "yyyy-MM-dd'T'HH:mm:ss.SSSZ",
    };

    public static int getScreenWidth(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displaymetrics);
        return displaymetrics.widthPixels;
    }

    public static int getScreenHeight(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displaymetrics);
        return displaymetrics.heightPixels;
    }

    public static CustomerOneBtnAlertDialog getAlertDialog(Context mContext, String msg) {
        CustomerOneBtnAlertDialog dialog = new CustomerOneBtnAlertDialog(mContext);
        dialog.setMsg(msg);
        return dialog;
    }

    public static CustomerOneBtnAlertDialog getAlertDialog(Context mContext, String msg, String btnTxt) {
        CustomerOneBtnAlertDialog dialog = new CustomerOneBtnAlertDialog(mContext);
        dialog.setMsg(msg);
        dialog.setPositiveBtnText(btnTxt);
        return dialog;
    }

    public static CustomerOneBtnAlertDialog getAlertDialog(Context mContext, String msg, View.OnClickListener listener) {
        CustomerOneBtnAlertDialog dialog = new CustomerOneBtnAlertDialog(mContext);
        dialog.setMsg(msg);
        dialog.setPositiveListener(listener);
        return dialog;
    }

    public static CustomerTwoBtnAlertDialog getAlertDialog(final Context mContext, String msg, String okStr, String cancelStr, View.OnClickListener listener) {
        CustomerTwoBtnAlertDialog dialog = new CustomerTwoBtnAlertDialog(mContext);
        dialog.setMsg(msg)
                .setPositiveBtnText(okStr)
                .setNegativeBtnText(cancelStr)
                .setPositiveListener(listener);
        return dialog;
    }

    public static CustomerTwoBtnAlertDialog getAlertDialog(final Context mContext, String msg,
                                                           String okStr,
                                                           String cancelStr,
                                                           View.OnClickListener positiveListener,
                                                           View.OnClickListener negativeListener) {
        CustomerTwoBtnAlertDialog dialog = new CustomerTwoBtnAlertDialog(mContext);
        dialog.setMsg(msg)
                .setPositiveBtnText(okStr)
                .setNegativeBtnText(cancelStr)
                .setPositiveListener(positiveListener)
                .setNegativeListener(negativeListener);
        return dialog;
    }

    public static ProgressDialog customProgressDialog(Context mContext, String title, String msg) {
        ProgressDialog progressDialog = new ProgressDialog(new ContextThemeWrapper(mContext, R.style.customProgressDialog));
        progressDialog.setTitle(title);
        progressDialog.setMessage(msg);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);

        return progressDialog;
    }

    // NetWorking
    public static boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    // HttpPost
    public static String postResponse(String url, List<NameValuePair> params) {
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, HTTP.UTF_8);
            httpPost.setEntity(entity);
            HttpResponse responsePOST = httpClient.execute(httpPost);
            HttpEntity resEntity = responsePOST.getEntity();
            String json = EntityUtils.toString(resEntity);
            JSONObject jObject = new JSONObject(json);
            return jObject.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static String getChineseSystemTime() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR) - 1911;
        int mon = cal.get(Calendar.MONTH);
        String day = cal.get(Calendar.DAY_OF_MONTH) + "";
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        String changHr = hour + "";
        String changMin = minute + "";

        if (hour < 10) {
            changHr = "0" + hour;
        }

        if (minute < 10) {
            changMin = "0" + minute;
        }

        return "" + year + "/" + (mon + 1) + "/" + day + " " + changHr + ":" + changMin;
    }

    public static String getSystemTime() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int mon = cal.get(Calendar.MONTH);
        String day = cal.get(Calendar.DAY_OF_MONTH) + "";
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        String changHr = hour + "";
        String changMin = minute + "";

        if (hour < 10) {
            changHr = "0" + hour;
        }

        if (minute < 10) {
            changMin = "0" + minute;
        }

        return "" + year + "-" + (mon + 1) + "-" + day + " " + changHr + ":" + changMin;
    }

    /**
     * Returns the Uri for a photo stored on disk given the fileName
     *
     * @param fileName generate file name
     * @return uri
     */
    public static Uri getPhotoUri(String fileName) {
        // Get safe storage directory for photos
        File mediaStorageDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), AppConfig.JPEG_FILE_FOLDER_NAME);
        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d(TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        return Uri.fromFile(new File(mediaStorageDir.getPath() + File.separator + fileName));
    }

    public static boolean deletePhotoFromSD(Context mContext, String path, String folder, String fileName) {
        String fullPath = mContext.getExternalCacheDir() + path + folder;
        Log.i(TAG, "delete fullPath:" + fullPath);
        File file = new File(fullPath, fileName + ".png");
        Log.i(TAG, "file.exists():" + file.exists());
        if (file.exists()) {
            return file.delete();
        } else {
            return false;
        }

    }


    public static void clearFolderData(Context mContext, String path, String folder) {
        String fullPath = mContext.getExternalCacheDir() + path + folder;
        File dir = new File(fullPath);
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                new File(dir, children[i]).delete();
            }
        }
    }

    public static boolean savePhotoToSD(Context mContext, String path, String folder, String fileName, Bitmap image) {

        String fullPath = mContext.getExternalCacheDir() + path + folder;
//                Environment.getExternalStorageDirectory().getAbsolutePath() + path + folder;
        Log.i(TAG, "fullPath:" + fullPath);
        try {
            File dir = new File(fullPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            OutputStream fOut = null;
            File file = new File(fullPath, fileName + ".png");
            file.createNewFile();
            fOut = new FileOutputStream(file);

            image.compress(Bitmap.CompressFormat.PNG, 40, fOut);
            fOut.flush();
            fOut.close();

            return true;

        } catch (Exception e) {
            Log.e("saveToExternalStorage()", e.getMessage());
            return false;
        }

    }

    public static File savePhotoToCacheDir(Context mContext, String filePath, String fileName, Bitmap image) {

        String fullPah = mContext.getExternalCacheDir() + filePath;
        Log.i(TAG, "filePath:" + fullPah);
        try {
            File dir = new File(fullPah);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            File file = new File(fullPah, fileName);
            Log.i(TAG, "file AbsolutePath:" + file.getAbsolutePath());

            // create file
            file.createNewFile();
            OutputStream fout = new FileOutputStream(file);
            image.compress(Bitmap.CompressFormat.PNG, 85, fout);
            fout.flush();
            fout.close();
            return file;

        } catch (Exception e) {
            Log.e("saveToExternalStorage()", e.getMessage());
            return null;
        }

    }

    /**
     * renew album in cell phone
     *
     * @param mContext      Activity context
     * @param takenPhotoUri uri
     */
    public static void renewAlbum(Context mContext, Uri takenPhotoUri) {
//        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//        mediaScanIntent.setData(takenPhotoUri);
//        mContext.sendBroadcast(mediaScanIntent);
//        mediaScanIntent = null;
        // refresh photo album
//        AppUtils.renewAlbum(mContext, takenPhotoUri);
//        getActivity().sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory())));
//        MediaScannerConnection.scanFile(getActivity().getApplicationContext(), new String[]{takenPhotoUri.getPath()}, null, new MediaScannerConnection.OnScanCompletedListener() {
//
//            @Override
//            public void onScanCompleted(String path, Uri uri) {
//                // TODO Auto-generated method stub
//
//            }
//        });
    }


    /**
     * decode base64 to bitmap
     *
     * @param input base64 string
     * @return bitmap
     */
    public static Bitmap decodeBase64(String input) {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }


    /**
     * generate current file name timestamp
     *
     * @param prefix enter prefix name
     * @return
     */
    public static String getCameraSystemTimeFileName(String prefix) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS");
        Calendar calendar = Calendar.getInstance();
        String str = df.format(calendar.getTime());
        Log.i(TAG, "str:" + str);
        return prefix + str;
    }


    /**
     * 半形轉全形
     */
    public static String ToSBC(String input) {
        char[] c = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == 32) {
                c[i] = (char) 12288;
                continue;
            }
            if (c[i] < 127)
                c[i] = (char) (c[i] + 65248);
        }
        return new String(c);
    }

    /**
     * 全形轉半形
     */

    public static String ToDBC(String input) {
        char[] c = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == 12288) {
                c[i] = (char) 32;
                continue;
            }
            if (c[i] > 65280 && c[i] < 65375)
                c[i] = (char) (c[i] - 65248);
        }
        return new String(c);
    }


    /*
     * check line install
     */
    public static boolean checkLineSetup(Context mContext) {
        final String LINE_PACKAGE_NAME = "jp.naver.line.android";
        PackageManager pm = mContext.getPackageManager();
        List<ApplicationInfo> appList = pm.getInstalledApplications(0);
        for (ApplicationInfo app : appList) {
//            Log.i("info","app:" +  app );
            if (app.packageName.equals(LINE_PACKAGE_NAME)) {

                return true;
            }
        }
        return false;
    }

    /*
     *  load photo
     */
    public static void loadPhoto(Context mContext, ImageView imageView, String photoPath) {
        ImageLoader imageLoader = ImageLoader.getInstance();
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .build();
        ImageLoaderConfiguration config =
                new ImageLoaderConfiguration.Builder(mContext.getApplicationContext())
                        .defaultDisplayImageOptions(options)
                        .build();
        imageLoader.init(config);
        imageLoader.displayImage(photoPath, imageView);
        imageView.setVisibility(View.VISIBLE);
        imageLoader = null;
        options = null;
        config = null;
    }

    public static void loadPhoto(Context mContext, ImageView imageView, String photoPath, ImageLoader imageLoader) {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .build();
        ImageLoaderConfiguration config =
                new ImageLoaderConfiguration.Builder(mContext.getApplicationContext())
                        .defaultDisplayImageOptions(options)
                        .build();
        imageLoader.init(config);
        Log.i("123", "photo: " + photoPath);
        imageLoader.displayImage(photoPath, imageView);
        imageView.setVisibility(View.VISIBLE);
        imageLoader = null;
        options = null;
        config = null;
    }

    /**
     * set three heart bitmap
     *
     * @param mContext
     * @param count
     * @param imageViews
     */
    public static void setMultiHeartBitmap(Context mContext, int count, ImageView... imageViews) {
        if (count < 0) {
            for (ImageView iv : imageViews)
                iv.setImageBitmap(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.play_life_out));
        }
        if (count == 0) {
            for (ImageView iv : imageViews)
                iv.setImageBitmap(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.play_life_out));
        } else {
            for (int i = 0; i < count; i++) {
                imageViews[i].setImageBitmap(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.play_life));
            }
            for (int j = count; j < (imageViews.length - count); j++) {
                imageViews[j].setImageBitmap(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.play_life_out));
            }
        }
//        switch (count) {
//            case 0:
//                imageViews[0].setImageBitmap(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.play_life_out));
//                imageViews[1].setImageBitmap(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.play_life_out));
//                imageViews[2].setImageBitmap(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.play_life_out));
//                break;
//            case 1:
//                imageViews[0].setImageBitmap(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.play_life));
//                imageViews[1].setImageBitmap(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.play_life_out));
//                imageViews[2].setImageBitmap(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.play_life_out));
//                break;
//            case 2:
//                imageViews[0].setImageBitmap(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.play_life));
//                imageViews[1].setImageBitmap(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.play_life));
//                imageViews[2].setImageBitmap(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.play_life_out));
//                break;
//            case 3:
//                imageViews[0].setImageBitmap(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.play_life));
//                imageViews[1].setImageBitmap(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.play_life));
//                imageViews[2].setImageBitmap(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.play_life));
//                break;
//        }

    }

    /*
     * Adding shortcut on Home screen
     */
    public static void addShortcut(Context mContext) {

        final String SHORTCUT_ACTION = "com.android.launcher.action.INSTALL_SHORTCUT";
        Intent shortcutIntent = new Intent(mContext.getApplicationContext(),
                SplashScreen.class);

        shortcutIntent.setAction(Intent.ACTION_MAIN);

        Intent addIntent = new Intent();
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, mContext.getString(R.string.app_name));
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                Intent.ShortcutIconResource.fromContext(mContext.getApplicationContext(),
                        R.drawable.ic_launcher));
        addIntent.putExtra("duplicate", false);
        addIntent.setAction(SHORTCUT_ACTION);
        mContext.getApplicationContext().sendBroadcast(addIntent);
    }

    // Check Google player

    private void checkGooglePlayServices(Activity activity) {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity.getApplicationContext());
        if (resultCode != ConnectionResult.SUCCESS) {
            GooglePlayServicesUtil.getErrorDialog(resultCode, activity, AppConfig.RQS_GooglePlayServices).show();
        }
    }

    public static Bitmap decodeBitmapFromResource(Resources res, int resId,
                                                  int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final Options options = new Options();
        options.inJustDecodeBounds = true;
        decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;


        Bitmap bitmap = decodeResource(res, resId, options);
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        float scaleX = reqWidth / (float) width;
        float scaleY = reqHeight / (float) height;
        float baseScale = Math.min(scaleX, scaleY);

        Matrix mtx = new Matrix();
        mtx.setScale(baseScale, baseScale);

        Bitmap returnBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, mtx, true);
        bitmap = null;
        return returnBitmap;
    }

    public static int calculateInSampleSize(
            Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int width = options.outWidth;
        final int height = options.outHeight;
        Log.d(TAG, "--- width/height:" + width + "/" + height);

        int inSampleSize = 1;

        Log.i(TAG, "calculateInSampleSize.reqWidth:" + reqWidth);
        Log.i(TAG, "calculateInSampleSize.reqHeight:" + reqHeight);

        Log.i(TAG, "width:" + width);
        Log.i(TAG, "height:" + height);


        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            Log.i(TAG, "halfWidth width:" + halfWidth);
            Log.i(TAG, "halfHeight height:" + halfHeight);

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }


    public static int dpToPixel(Context context, int value) {
        int pixel = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, context.getResources().getDisplayMetrics());
        return pixel;
    }


    public static int pixToDp(Context context, int value) {
        int dp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, context.getResources().getDisplayMetrics());
        return dp;
    }


    public static Bitmap rotateAndResizeBitmap(String filePath, int reqWidth, int reqHeight) {
        Log.i(TAG, "filePath:" + filePath);
        Log.i(TAG, "reqWidth:" + reqWidth + "/" + "reqHeight:" + reqHeight);

        // file Exif
        ExifInterface exifInterface = null;
        Options options = new Options();
        options.inJustDecodeBounds = true;
        decodeFile(filePath, options);
        try {
            exifInterface = new ExifInterface(filePath);
            Log.i(TAG, "exifInterface = nwe ExifInterface()");
            Log.i(TAG, "options:" + options);
        } catch (IOException e) {
            e.printStackTrace();
            Log.i(TAG, "e:" + e.getMessage());
            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
//            return decodeFile(filePath, options);
            return null;
        }
        // orientation
        int tag = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);
        int orientation = 0;
        if (tag == ExifInterface.ORIENTATION_ROTATE_90) {
            orientation = 90;
        } else if (tag == ExifInterface.ORIENTATION_ROTATE_180) {
            orientation = 180;
        } else if (tag == ExifInterface.ORIENTATION_ROTATE_270) {
            orientation = 270;
        }

        try {
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
            Log.d(TAG, "insamplesize:" + options.inSampleSize);
//        options.inSampleSize = 4;
            options.inJustDecodeBounds = false;
            Matrix mtx = new Matrix();
            Bitmap bitmap = decodeFile(filePath, options);
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            Log.d(TAG, "--- bitmap:" + bitmap);
            Log.d(TAG, "--- bitmap width/height:" + width + "/" + height);

            float scaleX = reqWidth / (float) width;
            float scaleY = reqHeight / (float) height;
            float baseScale = Math.min(scaleX, scaleY);
            Log.d(TAG, "--- scaleX/scaleY:" + scaleX + "/" + scaleY);
            Log.d(TAG, "--- baseScale:" + baseScale);

            mtx.setScale(baseScale, baseScale);
            mtx.postRotate(orientation);
            return Bitmap.createBitmap(bitmap, 0, 0, width, height, mtx, true);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }


    public static String getPhotoDate(String path) {
        File file = new File(path);
        if (file.exists()) {
            Date lastModDate = new Date(file.lastModified());

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            Log.i(TAG, "Dated : " + lastModDate.toString());
            Log.i(TAG, "format : " + sdf.format(lastModDate));

            return sdf.format(lastModDate);
        }

//        ExifInterface intf = null;
//        try
//        {
//            intf = new ExifInterface(path);
//        }
//        catch(IOException e)
//        {
//            e.printStackTrace();
//        }
//
//        if(intf != null)
//        {
//            Log.i(TAG,":intf:"+intf);
//
//            String TAG_FLASH = intf.getAttribute(ExifInterface.TAG_FLASH);
//            String dateString = intf.getAttribute(ExifInterface.TAG_DATETIME);
//            return dateString;
//        }
        return "";
    }

    /**
     * use pick photo from album use decodeStream get bitmap
     *
     * @param filePathUri input Uri
     * @param reqWidth    request width
     * @param reqHeight   request height
     * @param mContext    context
     * @return bitmap
     */
    public static Bitmap rotateAndResizeBitmap(Uri filePathUri, int reqWidth, int reqHeight, Context mContext) {
        Log.i(TAG, "filePath:" + filePathUri);
        Log.i(TAG, "reqWidth:" + reqWidth + "/" + "reqHeight:" + reqHeight);

        // file Exif
        ExifInterface exifInterface = null;
        Options options = new Options();
        options.inJustDecodeBounds = true;
        try {
            decodeStream(mContext.getContentResolver().openInputStream(filePathUri), null, options);
            exifInterface = new ExifInterface(filePathUri.toString());
            Log.i(TAG, "exifInterface = nwe ExifInterface()");
            Log.i(TAG, "options:" + options);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "decodeStream e:" + e.getMessage());
            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
//            return BitmapFactory.decodeFile(filePath, options);
        }

//        // orientation
        int tag = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);
        int orientation = 0;
        if (tag == ExifInterface.ORIENTATION_ROTATE_90) {
            orientation = 90;
        } else if (tag == ExifInterface.ORIENTATION_ROTATE_180) {
            orientation = 180;
        } else if (tag == ExifInterface.ORIENTATION_ROTATE_270) {
            orientation = 270;
        }
        Log.i(TAG, "orientation:" + orientation);

        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        Log.d(TAG, "insamplesize:" + options.inSampleSize);
//        options.inSampleSize = 4;
        options.inJustDecodeBounds = false;
        Matrix mtx = new Matrix();
        Bitmap bitmap = null;
        try {
            bitmap = decodeStream(mContext.getContentResolver().openInputStream(filePathUri), null, options);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.e(TAG, "decodeStream:" + e.getMessage());
        }
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Log.d(TAG, "--- bitmap:" + bitmap);
        Log.d(TAG, "--- bitmap width/height:" + width + "/" + height);

        float scaleX = reqWidth / (float) width;
        float scaleY = reqHeight / (float) height;
        float baseScale = Math.min(scaleX, scaleY);
        Log.d(TAG, "--- scaleX/scaleY:" + scaleX + "/" + scaleY);
        Log.d(TAG, "--- baseScale:" + baseScale);

        mtx.setScale(baseScale, baseScale);
        mtx.postRotate(orientation);

        return Bitmap.createBitmap(bitmap, 0, 0, width, height, mtx, true);
    }

    public static Bitmap rotateAndResizeBitmap(InputStream is, Uri filePathUri, int reqWidth, int reqHeight, Context mContext) {
        Log.i(TAG, "filePath:" + filePathUri);
        Log.i(TAG, "reqWidth:" + reqWidth + "/" + "reqHeight:" + reqHeight);

        // file Exif
        ExifInterface exifInterface = null;
        Options options = new Options();
        options.inJustDecodeBounds = true;
        try {
//            BitmapFactory.decodeStream(mContext.getContentResolver().openInputStream(filePathUri),null, options);
            BitmapFactory.decodeStream(is, null, options);
//            if (is != null) {
//                is.close();
//            }
            exifInterface = new ExifInterface(filePathUri.toString());
            Log.i(TAG, "exifInterface = nwe ExifInterface()");
            Log.i(TAG, "options:" + options);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "decodeStream e:" + e.getMessage());
            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
//            return BitmapFactory.decodeFile(filePath, options);
        }

//        // orientation
        int tag = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);
        int orientation = 0;
        if (tag == ExifInterface.ORIENTATION_ROTATE_90) {
            orientation = 90;
        } else if (tag == ExifInterface.ORIENTATION_ROTATE_180) {
            orientation = 180;
        } else if (tag == ExifInterface.ORIENTATION_ROTATE_270) {
            orientation = 270;
        }
        Log.i(TAG, "orientation:" + orientation);

        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        Log.d(TAG, "insamplesize:" + options.inSampleSize);
//        options.inSampleSize = 4;
        options.inJustDecodeBounds = false;
        Matrix mtx = new Matrix();
        Bitmap bitmap = null;
        //            bitmap = BitmapFactory.decodeStream(mContext.getContentResolver().openInputStream(filePathUri), null, options);
        try {
//            bitmap = BitmapFactory.decodeStream(is, null, options);
            bitmap = BitmapFactory.decodeStream(is);
//            if (is != null) {
//                is.close();

//            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, " e rotateAndResizeBitmap:" + e.getMessage());
        }

        Log.i(TAG, "bitmap :" + bitmap);
        if (bitmap == null) {
            Log.i(TAG, "bitmap is null ");
        } else {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            Log.d(TAG, "--- bitmap:" + bitmap);
            Log.d(TAG, "--- bitmap width/height:" + width + "/" + height);

            float scaleX = reqWidth / (float) width;
            float scaleY = reqHeight / (float) height;
            float baseScale = Math.min(scaleX, scaleY);
            Log.d(TAG, "--- scaleX/scaleY:" + scaleX + "/" + scaleY);
            Log.d(TAG, "--- baseScale:" + baseScale);

            mtx.setScale(baseScale, baseScale);
            mtx.postRotate(orientation);
            Bitmap returnBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, mtx, true);
            return returnBitmap;
        }
        return bitmap;
    }


    /**
     * check image orientation use decodeFile
     *
     * @param filePath input file real path
     * @return
     */
    public static int checkImageOrientation(String filePath) {
        // file Exif
        ExifInterface exifInterface = null;
        Options options = new Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        try {
            exifInterface = new ExifInterface(filePath);
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
        // orientation
        int tag = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);
        int orientation = 0;
        if (tag == ExifInterface.ORIENTATION_ROTATE_90) {
            orientation = 90;
        } else if (tag == ExifInterface.ORIENTATION_ROTATE_180) {
            orientation = 180;
        } else if (tag == ExifInterface.ORIENTATION_ROTATE_270) {
            orientation = 270;
        }

        Log.i(TAG, "checkImageOrientation:" + orientation);
        return tag;
    }


    /**
     * check image orientation use decodeStream
     *
     * @param filePathUri input uri
     * @param mContext    context
     * @return return orientation number
     */
    public static int checkImageOrientation(Uri filePathUri, Context mContext) {
        // file Exif
        ExifInterface exifInterface = null;
        Options options = new Options();
        options.inJustDecodeBounds = true;

        try {
            Log.i(TAG, "checkImageOrientation path:" + filePathUri);
            BitmapFactory.decodeStream(mContext.getContentResolver().openInputStream(filePathUri), null, options);
            exifInterface = new ExifInterface(filePathUri.toString());
        } catch (IOException e) {
            e.printStackTrace();
            Log.i(TAG, "checkImageOrientation e:" + e.getMessage());
            return 0;
        }
        // orientation
        int tag = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);
        int orientation = 0;
        if (tag == ExifInterface.ORIENTATION_ROTATE_90) {
            orientation = 90;
        } else if (tag == ExifInterface.ORIENTATION_ROTATE_180) {
            orientation = 180;
        } else if (tag == ExifInterface.ORIENTATION_ROTATE_270) {
            orientation = 270;
        }

        return tag;
    }


    public static int checkImageOrientation(InputStream fis, Uri filePathUri, Context mContext) {
        // file Exif
        ExifInterface exifInterface = null;
        Options options = new Options();
        options.inJustDecodeBounds = true;

        try {
//            BitmapFactory.decodeStream(mContext.getContentResolver().openInputStream(filePathUri), null, options);
            BitmapFactory.decodeStream(fis, null, options);
//            fis.close();
            exifInterface = new ExifInterface(filePathUri.toString());
        } catch (IOException e) {
            e.printStackTrace();
            Log.i(TAG, "e checkImageOrientation3 orientation:" + e.getMessage());
            return 0;
        }
        // orientation
        int tag = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);
        int orientation = 0;
        if (tag == ExifInterface.ORIENTATION_ROTATE_90) {
            orientation = 90;
        } else if (tag == ExifInterface.ORIENTATION_ROTATE_180) {
            orientation = 180;
        } else if (tag == ExifInterface.ORIENTATION_ROTATE_270) {
            orientation = 270;
        }

        Log.i(TAG, "checkImageOrientation3 orientation:" + orientation);
        return tag;
    }

    //wall
    public static void hideSoftKeyborad(Activity activity) {
        if (activity != null && activity.getWindow() != null && activity.getWindow().getDecorView() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            if (activity.getCurrentFocus() != null && activity.getCurrentFocus().getWindowToken() != null) {
                inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
            }
        }
    }

    public static void setupUI(final Activity activity, View view) {
        //Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyborad(activity);
                    return false;
                }
            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(activity, innerView);
            }
        }
    }


    /**
     * convert 10000 to 10,000
     *
     * @param str intput int type or String type
     * @return 10, 000
     */
    public static String numberFormat(String str) {
        DecimalFormat df = new DecimalFormat("###,###,###,##0");

        return String.valueOf(df.format(Integer.valueOf(str)));
    }

    public static String numberFormat(int val) {
        DecimalFormat df = new DecimalFormat("###,###,###,##0");

        return String.valueOf(df.format(val));
    }


    /**
     * Covert dp to px
     *
     * @param dp
     * @param context
     * @return pixel
     */
    public static float convertDpToPixel(float dp, Context context) {
        float px = dp * getDensity(context);
        return px;
    }

    /**
     * Covert px to dp
     *
     * @param px
     * @param context
     * @return dp
     */
    public static float convertPixelToDp(float px, Context context) {
        float dp = px / getDensity(context);
        return dp;
    }

    /**
     * 取得螢幕密度
     * 120dpi = 0.75
     * 160dpi = 1 (default)
     * 240dpi = 1.5
     *
     * @param context
     * @return
     */
    public static float getDensity(Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return metrics.density;
    }

    // base64 to bitmap
    public static Bitmap base64ToBitmap(String str) {
        byte[] data = Base64.decode(str, Base64.DEFAULT);
        BitmapFactory.Options option = new BitmapFactory.Options();
        option.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(data, 0, data.length, option);
        int minLength = Math.min(option.outWidth, option.outHeight);
        int insampleSize = minLength / AppConfig.PHOTO_SIZE_256;
        option.inJustDecodeBounds = false;
        option.inSampleSize = insampleSize;
        return BitmapFactory.decodeByteArray(data, 0, data.length, option);
    }

    // Bitmap to Base64
    public static String bitmapTobase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        bitmap.recycle();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    // yyyy-MM-dd to Chinest date
    public static String dateToChineseDate(String str) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = null;

            date = simpleDateFormat.parse(str);

            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            int year = cal.get(Calendar.YEAR) - 1911;
            int month = cal.get(Calendar.MONTH) + 1;
            int day = cal.get(Calendar.DAY_OF_MONTH);
            String chineseBirth = "民國 " + year + " 年 " + month + " 月 " + day + " 日";
            return chineseBirth;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}
