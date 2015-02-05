package com.coretronic.bdt.module;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.NumberPicker;
import com.coretronic.bdt.R;

import java.util.Calendar;

/**
 * Created by Morris on 2014/8/7.
 */
public class ChineseDatePickerDialog extends Dialog implements NumberPicker.OnValueChangeListener {
    private String TAG;
    private Context mContext;
    private Calendar calendar = Calendar.getInstance();
    private Button btnSumit;
    private CustomNumberPicker np1, np2, np3;
    private int yearNow;
    private int monthNow;
    private int dayNow;
    private int year;
    private int month;
    private int day;
    private View.OnClickListener clickListener;

    public ChineseDatePickerDialog(Context context, View.OnClickListener clickListener) {
        super(context);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.numberpicker_dialog);

        this.clickListener = clickListener;
        mContext = context;
        setCancelable(false);

        yearNow = calendar.get(Calendar.YEAR) - 1911;
        monthNow = calendar.get(Calendar.MONTH) + 1;
        dayNow = calendar.get(Calendar.DAY_OF_MONTH);
        buildDialog();

    }

    private void buildDialog() {
        np1 = (CustomNumberPicker) findViewById(R.id.yearPicker);
        np2 = (CustomNumberPicker) findViewById(R.id.monthPicker);
        np3 = (CustomNumberPicker) findViewById(R.id.dayPicker);
        btnSumit = (Button) findViewById(R.id.button);
        np1.setMinValue(1);
        np1.setMaxValue(yearNow);
        year = yearNow - 45;
        np1.setValue(year);
        np1.setOnValueChangedListener(this);

        np2.setMinValue(1);
        np2.setMaxValue(12);
        month = 6;
        np2.setValue(month);
        np2.setOnValueChangedListener(this);

        np3.setMinValue(1);
        np3.setMaxValue(getDayNum(yearNow, monthNow));
        day = 15;
        np3.setValue(day);
        np3.setOnValueChangedListener(this);

        btnSumit.setOnClickListener(clickListener);
    }

    @Override
    public void onValueChange(NumberPicker numberPicker, int oldValue, int newValue) {
        switch (numberPicker.getId()) {
            case R.id.yearPicker:
                year = newValue;
                np3.setMaxValue(getDayNum(year, month));
                break;
            case R.id.monthPicker:
                month = newValue;
                np3.setMaxValue(getDayNum(year, month));
                break;
            case R.id.dayPicker:
                day = newValue;
                break;
        }
    }

    public int getYearNow() {
        return year;
    }

    public int getMonthNow() {
        return month;
    }

    public int getDayNow() {
        return day;
    }


    private int getDayNum(int year, int month) {
        calendar.set(Calendar.YEAR, year + 1911);
        calendar.set(Calendar.MONTH, month - 1);
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    public void setDefaultDay(int year, int month,int day) {
        np1.setValue(year);
        np2.setValue(month);
        np3.setValue(day);
    }
}
