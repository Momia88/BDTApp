package com.coretronic.bdt.Utility;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.Log;
import android.widget.LinearLayout;

import java.io.InputStream;

/**
 * Created by james on 2014/10/13.
 */
public class AnimationUtility {

    private String TAG = AnimationUtility.class.getSimpleName();
    private Context context = null;
    private Bitmap spriteSheet = null;
    private Bitmap clipBM = null;
    private Handler animationHandler = new Handler();

    private int ROWS = 0;
    private int COLMS = 0;

    private int playTime = 0;
    private int currentXPos = 0;
    private int currentYPos = 0;
    private int currentFrame = 0;
    private Boolean repeat = true;
    private LinearLayout aniLL = null;
    private AnimationView animationView = null;

    public AnimationUtility(Context context,  LinearLayout aniLL, int spriteSheetId, int playTime, int mRows, int mColms, boolean repeatTF) {
//        super(context);
        this.context = context;
        this.playTime = playTime;
        this.aniLL = aniLL;
        this.ROWS = mRows;
        this.COLMS = mColms;
        this.repeat = repeatTF;
        spriteSheet = loadBitmMap(context, spriteSheetId);
        animationView = new AnimationView(context, null);
        aniLL.addView(animationView);
        drawAnimate();

    }


    private void drawAnimate() {
        Log.i(TAG, "drawAnimate currentFrame:" + currentFrame);

        clipBM = clipBitMapResource(spriteSheet);
//        if( animationView != null )
//        {
//            animationView = null;
//        }

        animationView.setBitmap(clipBM);


//        clipBM.recycle();
//        clipBM = null;

        currentXPos++;
        if (currentXPos >= COLMS) {
            currentYPos++;
            currentXPos = 0;
        }
        animationHandler.postDelayed(runnable, playTime);
    }


    private Bitmap loadBitmMap(Context context, int resId) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inPreferredConfig = Bitmap.Config.RGB_565;
        opts.inPurgeable = true;
        InputStream instream = context.getResources().openRawResource(resId);
        return BitmapFactory.decodeStream(instream, null, opts);
    }


    private Bitmap clipBitMapResource(Bitmap bitSource) {
        int clipWidth = bitSource.getWidth() / COLMS;
        int clipHeight = bitSource.getHeight() / ROWS;
        Log.i(TAG, "clipWidth/clipHeight:" + clipWidth + "/" + clipHeight);
        Log.i(TAG, "currentXPos/currentYPos:" + currentXPos + "/" + currentYPos);

        return Bitmap.createBitmap(bitSource, currentXPos * clipWidth, currentYPos * clipHeight, clipWidth, clipHeight);

    }


    public void stopAnimation() {
        if (runnable != null) {
            animationHandler.removeCallbacks(runnable);
        }
        if( clipBM != null )
        {
            clipBM.recycle();
            clipBM = null;
        }
    }

    private Runnable runnable = new Runnable() {

        @Override
        public void run() {
            currentFrame++;

            if ((currentFrame >= (ROWS * COLMS)) && (repeat == false)) {

                Log.i(TAG, "currentFrame>=" + (ROWS * COLMS));
                if (runnable != null) {
                    animationHandler.removeCallbacks(runnable);
                }
                spriteSheet.recycle();
                spriteSheet = null;
                return;
            } else if ((currentFrame >= (ROWS * COLMS)) && (repeat == true)) {
                currentFrame = 0;
                currentXPos = 0;
                currentYPos = 0;
            }
//            animationView.invalidate();
            drawAnimate();
        }
    };

}
