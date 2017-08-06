/*
 * French Revolutionary Calendar Android Widget
 * Copyright (C) 2017 Carmen Alvarez
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package ca.rmen.android.frccommon.converter;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.NumberPicker;

import java.util.Locale;

import ca.rmen.android.frccommon.Constants;
import ca.rmen.android.frccommon.FRCDateUtils;
import ca.rmen.android.frenchcalendar.R;
import ca.rmen.lfrc.FrenchRevolutionaryCalendarDate;
import ca.rmen.lfrc.i18n.FrenchRevolutionaryCalendarLabels;

@TargetApi(Constants.MIN_API_LEVEL_F2G_CONVERTER)
public class FRCDatePicker extends LinearLayout {

    private static final String EXTRA_YEAR = "year";
    private static final String EXTRA_MONTH = "month";
    private static final String EXTRA_DAY = "day";
    private static final String EXTRA_SUPER_STATE = "super_state";
    private Locale mLocale;
    private NumberPicker mDayPicker;
    private NumberPicker mMonthPicker;
    private NumberPicker mYearPicker;
    private OnDateSelectedListener mListener;

    interface OnDateSelectedListener {
        void onFrenchDateSelected(FrenchRevolutionaryCalendarDate date);
    }

    public FRCDatePicker(Context context) {
        super(context);
        initView(context);
    }

    public FRCDatePicker(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        View view = View.inflate(context, R.layout.frc_date_picker, null);
        addView(view);
        mDayPicker = findViewById(R.id.spinnerDay);
        mMonthPicker = findViewById(R.id.spinnerMonth);
        mYearPicker = findViewById(R.id.spinnerYear);
        initNumberPicker(mDayPicker, 30);
        initNumberPicker(mMonthPicker, 13);
        initNumberPicker(mYearPicker, 300);
        setLocale(Locale.getDefault());
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        Bundle bundle = (Bundle) state;
        Parcelable superState = (Parcelable) bundle.get(EXTRA_SUPER_STATE);
        super.onRestoreInstanceState(superState);
        mYearPicker.setValue(bundle.getInt(EXTRA_YEAR));
        mMonthPicker.setValue(bundle.getInt(EXTRA_MONTH));
        mDayPicker.setValue(bundle.getInt(EXTRA_DAY));
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        Bundle result = new Bundle(4);
        result.putParcelable(EXTRA_SUPER_STATE, superState);
        result.putInt(EXTRA_YEAR, mYearPicker.getValue());
        result.putInt(EXTRA_MONTH, mMonthPicker.getValue());
        result.putInt(EXTRA_DAY, mDayPicker.getValue());
        return result;
    }

    public void setOnDateSelectedListener(OnDateSelectedListener listener) {
        mListener = listener;
    }

    public void setLocale(Locale locale) {
        mLocale = locale;
        setDisplayedValues(mDayPicker, getDayNames(locale));
        setDisplayedValues(mMonthPicker, getMonthNames(locale));
    }


    private void setDisplayedValues(NumberPicker numberPicker, String[] values) {
        numberPicker.setDisplayedValues(values);
        // Workaround to force a new layout to resize the number picker to fit the new labels.
        // Calling invalidate(), postInvalidate() and requestLayout() didn't work.
        numberPicker.setVisibility(View.GONE);
        numberPicker.setVisibility(View.VISIBLE);
    }

    public void setUseRomanNumerals(boolean useRomanNumerals) {
        setDisplayedValues(mYearPicker, useRomanNumerals ? getRomanNumeralYears() : null);
    }

    @Nullable
    public FrenchRevolutionaryCalendarDate getDate() {
        if (mDayPicker.getValue() > 6 && mMonthPicker.getValue() == 13) {
            return null;
        }
        return new FrenchRevolutionaryCalendarDate(mLocale,
                mYearPicker.getValue(),
                mMonthPicker.getValue(),
                mDayPicker.getValue(),
                0, 0, 0);
    }

    private static String[] getDayNames(Locale locale) {
        FrenchRevolutionaryCalendarLabels cal = FrenchRevolutionaryCalendarLabels.getInstance(locale);
        String[] result = new String[30];
        for (int i = 1; i <= 30; i++) {
            int dayIndex = i % 10 == 0 ? 10 : i % 10;
            result[i - 1] = (i + " " + cal.getWeekdayName(dayIndex));
        }
        return result;
    }

    private static String[] getMonthNames(Locale locale) {
        FrenchRevolutionaryCalendarLabels cal = FrenchRevolutionaryCalendarLabels.getInstance(locale);
        String[] result = new String[13];
        for (int i = 1; i <= 13; i++) {
            result[i - 1] = cal.getMonthName(i);
        }
        return result;
    }

    private static String[] getRomanNumeralYears() {
        String[] result = new String[300];
        for (int i = 1; i <= 300; i++) {
            result[i - 1] = FRCDateUtils.getRomanNumeral(i);
        }
        return result;
    }

    private void initNumberPicker(NumberPicker numberPicker, int maxValue) {
        if (numberPicker.getValue() == 0) {
            numberPicker.setMinValue(1);
            numberPicker.setMaxValue(maxValue);
        }
        numberPicker.setOnValueChangedListener(mValueChangedListener);
        numberPicker.setOnTouchListener(mIgnoreParentTouchListener);
    }

    private final NumberPicker.OnValueChangeListener mValueChangedListener = new NumberPicker.OnValueChangeListener() {
        @Override
        public void onValueChange(NumberPicker numberPicker, int oldVal, int newVal) {
            if (mListener != null) mListener.onFrenchDateSelected(getDate());
        }
    };

    // Setting on Touch Listener for handling the touch inside ScrollView
    // https://stackoverflow.com/questions/18367522/android-list-view-inside-a-scroll-view
    private final OnTouchListener mIgnoreParentTouchListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            // Disallow the touch request for parent scroll on touch of child view
            v.getParent().requestDisallowInterceptTouchEvent(true);
            return false;
        }
    };

}
