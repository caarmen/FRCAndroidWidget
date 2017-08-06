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
import android.os.Build;
import android.support.annotation.Nullable;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.Locale;

import ca.rmen.android.frccommon.Constants;
import ca.rmen.android.frccommon.compat.ApiHelper;
import ca.rmen.android.frenchcalendar.R;
import ca.rmen.lfrc.FrenchRevolutionaryCalendar;
import ca.rmen.lfrc.FrenchRevolutionaryCalendarDate;

@TargetApi(Constants.MIN_API_LEVEL_F2G_CONVERTER)
public class F2gConverterView extends LinearLayout {

    private static final String PRE_KITKAT_DATE_FORMAT = "d MMM yyyy";
    private Spinner mSpinnerMethod;
    private FRCDatePicker mFRCDatePicker;
    private TextView mGregorianDate;
    private TextView mObjectOfTheDay;
    private F2gConverterListener mListener;

    interface F2gConverterListener {
        void onFrcDateSelected(FrenchRevolutionaryCalendar.CalculationMethod method, FrenchRevolutionaryCalendarDate date);
    }

    public F2gConverterView(Context context) {
        super(context);
        initView(context);
    }

    public F2gConverterView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.converter_f2g, (ViewGroup) getRootView(), false);
        addView(view);
        mSpinnerMethod = findViewById(R.id.spinnerMethod);
        ArrayAdapter spinnerAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.spinner_method_short_labels));
        spinnerAdapter.setDropDownViewResource(R.layout.spinner_drop_down_item);
        mSpinnerMethod.setAdapter(spinnerAdapter);
        mSpinnerMethod.setOnItemSelectedListener(mSpinnerSelectedListener);
        mFRCDatePicker = findViewById(R.id.frcDatePicker);
        mFRCDatePicker.setOnDateSelectedListener(mFrcOnDateChangedListener);
        mGregorianDate = findViewById(R.id.gregorianDate);
        mObjectOfTheDay = findViewById(R.id.objectOfTheDay);
    }

    public void setListener(F2gConverterListener listener) {
        mListener = listener;
    }

    public void setLocale(Locale locale) {
        mFRCDatePicker.setLocale(locale);
        notifyChange(mFRCDatePicker.getDate());
    }

    public void setUseRomanNumerals(boolean useRomanNumerals) {
        mFRCDatePicker.setUseRomanNumerals(useRomanNumerals);
    }

    public void setMethod(FrenchRevolutionaryCalendar.CalculationMethod method) {
        for (int i = 0; i < mSpinnerMethod.getAdapter().getCount(); i++) {
            if (getCalculationMethodAtPosition(i) == method) {
                mSpinnerMethod.setSelection(i);
                break;
            }
        }
    }

    public void setGregorianDate(@Nullable GregorianCalendar date) {
        if (date == null) {
            mGregorianDate.setText(null);
        } else {
            if (ApiHelper.getAPILevel() < Build.VERSION_CODES.KITKAT) {
                //https://issuetracker.google.com/issues/36923488
                SimpleDateFormat sdf = new SimpleDateFormat(PRE_KITKAT_DATE_FORMAT, Locale.getDefault());
                mGregorianDate.setText(sdf.format(date.getTime()));
            } else {
                mGregorianDate.setText(DateUtils.formatDateTime(getContext(), date.getTimeInMillis(), DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR));
            }
        }
    }

    private FrenchRevolutionaryCalendar.CalculationMethod getSelectedCalculationMethod() {
        return getCalculationMethodAtPosition(mSpinnerMethod.getSelectedItemPosition());
    }

    private FrenchRevolutionaryCalendar.CalculationMethod getCalculationMethodAtPosition(int spinnerPosition) {
        String methodName = (String) mSpinnerMethod.getAdapter().getItem(spinnerPosition);
        final FrenchRevolutionaryCalendar.CalculationMethod method;

        if (getContext().getString(R.string.method_romme).equals(methodName)) {
            method = FrenchRevolutionaryCalendar.CalculationMethod.ROMME;
        } else if (getContext().getString(R.string.method_equinox).equals(methodName)) {
            method = FrenchRevolutionaryCalendar.CalculationMethod.EQUINOX;
        } else {
            method = FrenchRevolutionaryCalendar.CalculationMethod.VON_MADLER;
        }
        return method;
    }

    private final AdapterView.OnItemSelectedListener mSpinnerSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            notifyChange(mFRCDatePicker.getDate());
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            notifyChange(null);
        }
    };

    private final FRCDatePicker.OnDateSelectedListener mFrcOnDateChangedListener = new FRCDatePicker.OnDateSelectedListener() {
        @Override
        public void onFrenchDateSelected(FrenchRevolutionaryCalendarDate date) {
            notifyChange(date);
        }
    };

    private void notifyChange(@Nullable FrenchRevolutionaryCalendarDate frcDate) {
        if (frcDate != null) {
            mObjectOfTheDay.setText(mObjectOfTheDay.getContext().getString(R.string.object_of_the_day_format, frcDate.getObjectTypeName(), frcDate.getObjectOfTheDay()));
        } else {
            mObjectOfTheDay.setText(null);
        }
        if (mListener != null) mListener.onFrcDateSelected(getSelectedCalculationMethod(), frcDate);
    }
}
