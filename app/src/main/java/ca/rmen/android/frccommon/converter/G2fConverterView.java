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

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;
import java.util.GregorianCalendar;

import ca.rmen.android.frccommon.FRCDateUtils;
import ca.rmen.android.frenchcalendar.R;
import ca.rmen.lfrc.FrenchRevolutionaryCalendarDate;

public class G2fConverterView extends LinearLayout {

    private DatePicker mGregorianDatePicker;
    private TextView mTextViewRomme;
    private TextView mTextViewEquinox;
    private TextView mTextViewVonMadler;
    private G2fConverterListener mListener;

    interface G2fConverterListener {
        void onGregorianDateSelected(GregorianCalendar date);
    }
    public G2fConverterView(Context context) {
        super(context);
        initView(context);
    }

    public G2fConverterView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public void setListener(G2fConverterListener listener) {
        mListener = listener;
    }

    private void initView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.converter_g2f, (ViewGroup) getRootView(), false);
        addView(view);
        mTextViewRomme = findViewById(R.id.frcDateRomme);
        mTextViewEquinox = findViewById(R.id.frcDateEquinox);
        mTextViewVonMadler = findViewById(R.id.frcDateVonMadler);
        mGregorianDatePicker = findViewById(R.id.datePicker);
        Calendar now = Calendar.getInstance();
        mGregorianDatePicker.init(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH), mOnDateChangedListener);
    }

    void updateFrcDateTexts(FrenchRevolutionaryCalendarDate frenchDateRomme, FrenchRevolutionaryCalendarDate frenchDateEquinox, FrenchRevolutionaryCalendarDate frenchDateVonMadler) {
        mTextViewRomme.setText(format(frenchDateRomme));
        mTextViewEquinox.setText(format(frenchDateEquinox));
        mTextViewVonMadler.setText(format(frenchDateVonMadler));
    }

    GregorianCalendar getDate() {
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.set(Calendar.YEAR, mGregorianDatePicker.getYear());
        gregorianCalendar.set(Calendar.MONTH, mGregorianDatePicker.getMonth());
        gregorianCalendar.set(Calendar.DAY_OF_MONTH, mGregorianDatePicker.getDayOfMonth());
        return gregorianCalendar;
    }

    private String format(FrenchRevolutionaryCalendarDate date) {
        return getContext().getString(R.string.date_format,
                date.getWeekdayName(),
                date.dayOfMonth,
                date.getMonthName(),
                FRCDateUtils.formatNumber(getContext(), date.year),
                date.getObjectTypeName(),
                date.getObjectOfTheDay());
    }

    private final DatePicker.OnDateChangedListener mOnDateChangedListener = new DatePicker.OnDateChangedListener() {
        @Override
        public void onDateChanged(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
            if (mListener != null) {
                GregorianCalendar gregorianCalendar = new GregorianCalendar();
                gregorianCalendar.set(Calendar.YEAR, year);
                gregorianCalendar.set(Calendar.MONTH, monthOfYear);
                gregorianCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                mListener.onGregorianDateSelected(gregorianCalendar);
            }
        }
    };
}
