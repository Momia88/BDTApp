package com.coretronic.bdt.Utility;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;
import android.view.View;

/**
 * Created by james on 2014/10/13.
 */
public class AnimationView extends View {
    private String TAG = AnimationView.class.getSimpleName();

    private Paint paint = new Paint();
    private Bitmap clipBitmap = null;

    /*
     * Context : context
     * sprietSheetId: spriteSheet image file
     * mRows: spriteSheet rows number
     * mCols: spriteSheet colms number
     */
    public AnimationView(Context context, Bitmap bitmap) {
        super(context);
//        clipBitmap = bitmap;
    }

    public void setBitmap(Bitmap bitmap)
    {
        clipBitmap = bitmap;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.i(TAG, "onDraw");
        super.onDraw(canvas);
        canvas.drawBitmap(clipBitmap,0,0,paint);
    }


}
