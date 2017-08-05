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

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (visibility == View.VISIBLE) {
            post(new Runnable() {
                @Override
                public void run() {
                    recenterListView(mListViewDay);
                    recenterListView(mListViewMonth);
                    recenterListView(mListViewYear);
                }
            });
        }
    }

    private void recenterListView(ListView listView) {
        int position = listView.getCheckedItemPosition();
        int itemHeight = getResources().getDimensionPixelSize(R.dimen.date_picker_item_height);
        listView.setSelectionFromTop(position, itemHeight);
    }

    private void smoothScrollToPosition(ListView listView, int position) {
        int itemHeight = getResources().getDimensionPixelSize(R.dimen.date_picker_item_height);
        if (position < listView.getFirstVisiblePosition() || position > listView.getLastVisiblePosition()) {
            mIsScrolling = true;
        }
        listView.smoothScrollToPositionFromTop(position, itemHeight);
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
        mListViewDay.setItemChecked(date.dayOfMonth, true);
        mListViewMonth.setItemChecked(date.month, true);
        mListViewYear.setItemChecked(date.year, true);
    }

    @Nullable
    public FrenchRevolutionaryCalendarDate getDate() {
        if (!isDateValid()) return null;
        return new FrenchRevolutionaryCalendarDate(mLocale,
                mListViewYear.getCheckedItemPosition(),
                mListViewMonth.getCheckedItemPosition(),
                mListViewDay.getCheckedItemPosition(),
                0, 0, 0);
    }

    private static List<String> getDayNames(Locale locale) {
        FrenchRevolutionaryCalendarLabels cal = FrenchRevolutionaryCalendarLabels.getInstance(locale);
        List<String> result = new ArrayList<>();
        result.add("");
        for (int i = 1; i <= 30; i++) {
            int dayIndex = i % 10 == 0 ? 10 : i % 10;
            result.add(i + " " + cal.getWeekdayName(dayIndex));
        }
        result.add("");
        return result;
    }

    private static List<String> getMonthNames(Locale locale) {
        FrenchRevolutionaryCalendarLabels cal = FrenchRevolutionaryCalendarLabels.getInstance(locale);
        List<String> result = new ArrayList<>();
        result.add("");
        for (int i = 1; i <= 13; i++) {
            result.add(cal.getMonthName(i));
        }
        result.add("");
        return result;
    }

    private static List<String> getYears(boolean useRomanNumerals) {
        List<String> result = new ArrayList<>();
        result.add("");
        for (int i = 1; i <= 300; i++) {
            if (useRomanNumerals) {
                result.add(FRCDateUtils.getRomanNumeral(i));
            } else {
                result.add(String.valueOf(i));
            }
        }
        result.add("");
        return result;
    }

    private boolean isDateValid() {
        int day = mListViewDay.getCheckedItemPosition();
        int month = mListViewMonth.getCheckedItemPosition();
        int year = mListViewYear.getCheckedItemPosition();
        return (day >= 1 && day <= 30 && month >= 1 && month <= 13 && year >= 1)
                && (month < 13 || day <= 6);
    }

    private void selectValidDate() {
        int day = mListViewDay.getCheckedItemPosition();
        int month = mListViewMonth.getCheckedItemPosition();
        int year = mListViewYear.getCheckedItemPosition();
        if (day < 1) selectListViewItem(mListViewDay, 1);
        else if (day > 6 && month >= 13) selectListViewItem(mListViewDay, 6);
        else if (day > 30) selectListViewItem(mListViewDay, 30);

        if (month < 1) selectListViewItem(mListViewMonth, 1);
        else if (month > 13) selectListViewItem(mListViewMonth, 13);

        if (year <= 1) selectListViewItem(mListViewYear, 1);
    }

    private void selectListViewItem(ListView listView, int position) {
        listView.setItemChecked(position, true);
        smoothScrollToPosition(listView, position);
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
                smoothScrollToPosition((ListView) parent, position);
                notifyDateSelected();
            } else {
                selectValidDate();
            }
        }
    };

    private boolean mIsScrolling;
    private final AbsListView.OnScrollListener mListViewScrollListener = new AbsListView.OnScrollListener() {

        @Override
        public void onScrollStateChanged(final AbsListView view, int scrollState) {
            if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                if (mIsScrolling) {
                    mIsScrolling = false;
                    return;
                }
                // Initially: the new position will be the average of the first and last positions
                int newCheckedItemPosition = (view.getFirstVisiblePosition() + view.getLastVisiblePosition()) / 2;

                // If the first visible item is scrolled up so much that it's mostly hiding, ignore it
                // and increment the new position (to a larger value, lower in the list)
                View firstCell = view.getChildAt(0);
                int firstCellTop = firstCell.getTop();
                if (firstCellTop < -firstCell.getHeight() / 2) {
                    newCheckedItemPosition++;
                }
                // Make sure we're not out of bounds
                if (newCheckedItemPosition >= view.getAdapter().getCount()) {
                    newCheckedItemPosition = view.getAdapter().getCount() - 1;
                }
                view.setItemChecked(newCheckedItemPosition, true);
                smoothScrollToPosition((ListView) view, newCheckedItemPosition);
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
