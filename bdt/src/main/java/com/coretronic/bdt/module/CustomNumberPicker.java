package com.coretronic.bdt.module;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.NumberPicker;

/**
 * Created by Morris on 2014/8/7.
 */
public class CustomNumberPicker extends NumberPicker {
    public CustomNumberPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    private final int textSize = 32;

    @Override
    public void addView(View child) {
        super.addView(child);
        updateView(child);

    }

    @Override
    public void addView(View child, ViewGroup.LayoutParams params) {
        super.addView(child, params);
        updateView(child);

    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        super.addView(child, index, params);
        updateView(child);

    }
    public void updateView(View view)
    {
        if (view instanceof EditText){
            //這裡修改字體的屬性
            EditText et = (EditText) view;
            et.setTextSize(textSize);
            et.setFocusable(false);
        }
    }
}
