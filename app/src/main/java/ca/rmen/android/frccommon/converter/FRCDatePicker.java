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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ca.rmen.android.frccommon.Constants;
import ca.rmen.android.frccommon.FRCDateUtils;
import ca.rmen.android.frenchcalendar.R;
import ca.rmen.lfrc.FrenchRevolutionaryCalendarDate;
import ca.rmen.lfrc.i18n.FrenchRevolutionaryCalendarLabels;

@TargetApi(Constants.MIN_API_LEVEL_F2G_CONVERTER)
public class FRCDatePicker extends LinearLayout {

    private Locale mLocale;
    private ListView mListViewDay;
    private ListView mListViewMonth;
    private ListView mListViewYear;
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
        mListViewDay = findViewById(R.id.spinnerDay);
        mListViewMonth = findViewById(R.id.spinnerMonth);
        mListViewYear = findViewById(R.id.spinnerYear);
        setListeners(mListViewDay);
        setListeners(mListViewMonth);
        setListeners(mListViewYear);
        setLocale(Locale.getDefault());
    }

    public void setOnDateSelectedListener(OnDateSelectedListener listener) {
        mListener = listener;
    }

    public void setLocale(Locale locale) {
        mLocale = locale;
        int position = mListViewDay.getCheckedItemPosition();
        mListViewDay.setAdapter(new ArrayAdapter<>(getContext(), R.layout.frc_date_picker_item, getDayNames(locale)));
        if (position >= 0) selectListViewItem(mListViewDay, position);

        position = mListViewMonth.getCheckedItemPosition();
        mListViewMonth.setAdapter(new ArrayAdapter<>(getContext(), R.layout.frc_date_picker_item, getMonthNames(locale)));
        if (position >= 0) selectListViewItem(mListViewMonth, position);
    }

    public void setUseRomanNumerals(boolean useRomanNumerals) {
        int position = mListViewYear.getCheckedItemPosition();
        mListViewYear.setAdapter(new ArrayAdapter<>(getContext(), R.layout.frc_date_picker_item, getYears(useRomanNumerals)));
        if (position >= 0) selectListViewItem(mListViewYear, position);
    }

    public void setDate(@NonNull FrenchRevolutionaryCalendarDate date) {
        mListViewDay.setItemChecked(date.dayOfMonth - 1, true);
        mListViewMonth.setItemChecked(date.month - 1, true);
        mListViewYear.setItemChecked(date.year - 1, true);
    }

    @Nullable
    public FrenchRevolutionaryCalendarDate getDate() {
        if (mListViewDay.getCheckedItemPosition() < 0
                || mListViewMonth.getCheckedItemPosition() < 0
                || mListViewYear.getCheckedItemPosition() < 0) {
            return null;
        }
        int day = mListViewDay.getCheckedItemPosition() + 1;
        int month = mListViewMonth.getCheckedItemPosition() + 1;
        int year = mListViewYear.getCheckedItemPosition() + 1;

        return new FrenchRevolutionaryCalendarDate(mLocale, year, month, day, 0, 0, 0);
    }

    private static List<String> getDayNames(Locale locale) {
        FrenchRevolutionaryCalendarLabels cal = FrenchRevolutionaryCalendarLabels.getInstance(locale);
        List<String> result = new ArrayList<>();
        for (int i = 1; i <= 30; i++) {
            int dayIndex = i % 10 == 0 ? 10 : i % 10;
            result.add(i + " " + cal.getWeekdayName(dayIndex));
        }
        return result;
    }

    private static List<String> getMonthNames(Locale locale) {
        FrenchRevolutionaryCalendarLabels cal = FrenchRevolutionaryCalendarLabels.getInstance(locale);
        List<String> result = new ArrayList<>();
        for (int i = 1; i <= 13; i++) {
            result.add(cal.getMonthName(i));
        }
        return result;
    }

    private static List<String> getYears(boolean useRomanNumerals) {
        List<String> result = new ArrayList<>();
        for (int i = 1; i <= 300; i++) {
            if (useRomanNumerals) {
                result.add(FRCDateUtils.getRomanNumeral(i));
            } else {
                result.add(String.valueOf(i));
            }
        }
        return result;
    }

    private boolean isDateValid() {
        return mListViewMonth.getCheckedItemPosition() < mListViewMonth.getCount() - 1
                || mListViewDay.getCheckedItemPosition() <= 5;
    }

    private void selectValidDate() {
        selectListViewItem(mListViewDay, 5);
    }

    private void selectListViewItem(ListView listView, int position) {
        listView.setItemChecked(position, true);
        listView.setSelectionFromTop(position, listView.getHeight() / 2);
        notifyDateSelected();
    }

    private void notifyDateSelected() {
        if (mListener != null) mListener.onFrenchDateSelected(getDate());
    }

    private void setListeners(ListView listView) {
        listView.setOnItemClickListener(mListViewItemClickListener);
        listView.setOnScrollListener(mListViewScrollListener);
        listView.setOnTouchListener(mIgnoreParentTouchListener);
    }

    private final ListView.OnItemClickListener mListViewItemClickListener = new ListView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (isDateValid()) {
                notifyDateSelected();
            } else {
                selectValidDate();
            }
        }
    };

    private final AbsListView.OnScrollListener mListViewScrollListener = new AbsListView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(final AbsListView view, int scrollState) {
            if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                int middlePosition = (view.getFirstVisiblePosition() + view.getLastVisiblePosition()) / 2;
                view.setItemChecked(middlePosition, true);
                if (isDateValid()) notifyDateSelected();
                else selectValidDate();
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
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
