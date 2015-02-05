package com.coretronic.bdt.Utility;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.RadioButton;

/**
 * Created by james on 14/11/19.
 */
public class SegmentedUIComponent extends RadioButton {

    private float mX;

    public SegmentedUIComponent(Context context) {
        super(context);
    }

    public SegmentedUIComponent(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SegmentedUIComponent(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    private static final float TEXT_SIZE = 24.0f;

    @Override
    public void onDraw(Canvas canvas) {

        String text = this.getText().toString();
        Paint textPaint = new Paint();
        textPaint.setAntiAlias(true);
        float currentWidth = textPaint.measureText(text);
        float currentHeight = textPaint.measureText(text);
        Rect textBounds = new Rect();
        textPaint.getTextBounds(text, 0, text.length(), textBounds);

        Paint.FontMetrics metrics = textPaint.getFontMetrics();
        float totalHeight =  ( int ) (Math.ceil(metrics.descent - metrics.ascent) +  2 );


        Log.i("info","textBounds.height:"+textBounds.height());
        Log.i("info","currentHeight:"+currentHeight);
        Log.i("info","totalHeight:"+totalHeight);
//        final float scale =
//        getContext().getResources().getDisplayMetrics().density;
//        float textSize = (int) (TEXT_SIZE * scale + 0.5f);
        textPaint.setTextSize(this.getTextSize());
        textPaint.setTextAlign(Paint.Align.CENTER);

        float canvasWidth = canvas.getWidth();
        float canvasHeight = canvas.getHeight();
        float textWidth = textPaint.measureText(text);
        Log.i("info", "canvasHeight:" + canvasHeight);
        Log.i("info", "this.getHeight():" + this.getHeight());
        if (isChecked()) {
            GradientDrawable grad = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{0xff8C6D62, 0xff8C6D62});
            grad.setBounds(0, 0, this.getWidth(), this.getHeight());
            grad.draw(canvas);
            textPaint.setColor(Color.WHITE);
        } else {
            GradientDrawable grad = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{0xffFFFFFF, 0xffFFFFFF});
            grad.setBounds(0, 0, this.getWidth(), this.getHeight());
            grad.draw(canvas);
            textPaint.setColor(0xff8C6D62);
        }
        Log.i("info", "this.getHeight()2:" + this.getHeight());
        Log.i("info", "canvasHeight* 0.5f2:" + canvasHeight* 0.5f);
        float w = (this.getWidth() / 2) - currentWidth;
        float h = (this.getHeight()/2 + totalHeight);
        canvas.drawText(text, mX,h , textPaint);

        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(1.0f);
        Rect rect = new Rect(1, 1, this.getWidth()-1, this.getHeight()-1);
        canvas.drawRect(rect, paint);

    }

    @Override
    protected void onSizeChanged(int w, int h, int ow, int oh) {
        super.onSizeChanged(w, h, ow, oh);

        Log.i("info","w/h/ow/oh:"+w+"/"+h+"/"+ow+"/"+oh);
        mX = w * 0.5f;
    }
}
