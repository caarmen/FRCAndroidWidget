/*
 * French Revolutionary Calendar Android Widget
 * Copyright (C) 2016 - 2017 Carmen Alvarez
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
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import ca.rmen.android.frccommon.Constants;
import ca.rmen.android.frccommon.prefs.FRCPreferenceActivity;
import ca.rmen.android.frccommon.prefs.FRCPreferences;
import ca.rmen.android.frenchcalendar.R;
import ca.rmen.lfrc.FrenchRevolutionaryCalendar;
import ca.rmen.lfrc.FrenchRevolutionaryCalendarDate;

@TargetApi(Constants.MIN_API_LEVEL_TWO_WAY_CONVERTER)
public class FRCConverterActivity extends Activity {

    private Spinner mMethodSpinner;
    private FRCDatePicker mFrcDatePicker;
    private TextView mObjectOfTheDayTextView;
    private DatePicker mGregorianDatePicker;

    private FrenchRevolutionaryCalendar mFrc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.converter);

        FrenchRevolutionaryCalendar.CalculationMethod method = FRCPreferences.getInstance(this).getCalculationMethod();
        Locale locale = FRCPreferences.getInstance(this).getLocale();
        mFrc = new FrenchRevolutionaryCalendar(locale, method);

        findViewById(R.id.btn_help).setOnClickListener(mOnHelpClickedListener);

        mMethodSpinner = findViewById(R.id.spinner_method);
        ArrayAdapter spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.spinner_method_short_labels));
        spinnerAdapter.setDropDownViewResource(R.layout.spinner_drop_down_item);
        mMethodSpinner.setAdapter(spinnerAdapter);
        setMethod(method);
        mMethodSpinner.setOnItemSelectedListener(mSpinnerSelectedListener);

        mFrcDatePicker = findViewById(R.id.frc_date_picker);
        mFrcDatePicker.setLocale(locale);
        mFrcDatePicker.setOnDateSelectedListener(mFrcDateSelectedListener);

        mObjectOfTheDayTextView = findViewById(R.id.object_of_the_day);

        mGregorianDatePicker = findViewById(R.id.gregorian_date_picker);
        Calendar frenchEraBegin = Calendar.getInstance();
        frenchEraBegin.set(1792, 8, 22);
        mGregorianDatePicker.setMinDate(frenchEraBegin.getTimeInMillis());
        resetGregorianDatePicker(null);

        makePickersFitInLandscape();

        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(mPrefsListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            Intent intent = new Intent(this, FRCPreferenceActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(mPrefsListener);
        super.onDestroy();
    }

    private void setMethod(FrenchRevolutionaryCalendar.CalculationMethod method) {
        for (int i = 0; i < mMethodSpinner.getAdapter().getCount(); i++) {
            if (getCalculationMethodAtPosition(i) == method) {
                mMethodSpinner.setSelection(i);
                break;
            }
        }
    }

    private FrenchRevolutionaryCalendar.CalculationMethod getSelectedCalculationMethod() {
        return getCalculationMethodAtPosition(mMethodSpinner.getSelectedItemPosition());
    }

    private FrenchRevolutionaryCalendar.CalculationMethod getCalculationMethodAtPosition(int spinnerPosition) {
        String methodName = (String) mMethodSpinner.getAdapter().getItem(spinnerPosition);
        final FrenchRevolutionaryCalendar.CalculationMethod method;

        if (getString(R.string.method_romme).equals(methodName)) {
            method = FrenchRevolutionaryCalendar.CalculationMethod.ROMME;
        } else if (getString(R.string.method_equinox).equals(methodName)) {
            method = FrenchRevolutionaryCalendar.CalculationMethod.EQUINOX;
        } else {
            method = FrenchRevolutionaryCalendar.CalculationMethod.VON_MADLER;
        }
        return method;
    }

    public void resetFrcDatePicker(@SuppressWarnings("UnusedParameters") View view) {
        FrenchRevolutionaryCalendarDate frcDate = new FrenchRevolutionaryCalendarDate(
                FRCPreferences.getInstance(this).getLocale(),
                1, 1, 1,
                0, 0, 0);
        mFrcDatePicker.setDate(frcDate);
        mFrcDateSelectedListener.onFrenchDateSelected(frcDate);
    }

    private void updateObjectOfTheDayText(FrenchRevolutionaryCalendarDate frcDate) {
        mObjectOfTheDayTextView.setText(getString(R.string.object_of_the_day_format, frcDate.getObjectTypeName(), frcDate.getObjectOfTheDay()));
    }

    public void resetGregorianDatePicker(@SuppressWarnings("UnusedParameters") View view) {
        Calendar calendar = Calendar.getInstance();
        mGregorianDatePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), mGregorianDateSelectedListener);
        mGregorianDateSelectedListener.onDateChanged(mGregorianDatePicker, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
    }

    // In case we have a small screen: we resize the date pickers so they can fit.
    private void makePickersFitInLandscape() {
        final ViewGroup container = findViewById(R.id.date_pickers);
        if (container != null) {
            mFrcDatePicker.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    mFrcDatePicker.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    int maxWidth = container.getWidth();
                    int childViewsWidth = 0;
                    for (int i = 0; i < container.getChildCount(); i++) {
                        childViewsWidth += container.getChildAt(i).getWidth();
                    }
                    if (childViewsWidth > maxWidth) {
                        float scale = maxWidth / (float) childViewsWidth;
                        for (int i = 0; i < container.getChildCount(); i++) {
                            View child = container.getChildAt(i);
                            child.setScaleX(scale);
                            child.setScaleY(scale);
                        }
                    }
                }
            });
        }
    }

    private final View.OnClickListener mOnHelpClickedListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            CharSequence message = TextUtils.concat(getText(R.string.romme_description),
                    "\n",
                    getText(R.string.equinox_description),
                    "\n",
                    getText(R.string.von_madler_description));
            new AlertDialog.Builder(FRCConverterActivity.this)
                    .setMessage(message)
                    .setPositiveButton(android.R.string.ok, null)
                    .show();
        }
    };

    private final AdapterView.OnItemSelectedListener mSpinnerSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            Locale locale = FRCPreferences.getInstance(getApplicationContext()).getLocale();
            mFrc = new FrenchRevolutionaryCalendar(locale, getSelectedCalculationMethod());
            mFrcDateSelectedListener.onFrenchDateSelected(mFrcDatePicker.getDate());
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };

    private final FRCDatePicker.OnDateSelectedListener mFrcDateSelectedListener = new FRCDatePicker.OnDateSelectedListener() {
        @Override
        public void onFrenchDateSelected(FrenchRevolutionaryCalendarDate frcDate) {
            if (frcDate != null) {
                GregorianCalendar gregDate = mFrc.getDate(frcDate);
                updateObjectOfTheDayText(frcDate);
                mGregorianDatePicker.updateDate(gregDate.get(Calendar.YEAR), gregDate.get(Calendar.MONTH), gregDate.get(Calendar.DAY_OF_MONTH));
            }
        }
    };


    private final DatePicker.OnDateChangedListener mGregorianDateSelectedListener = new DatePicker.OnDateChangedListener() {
        @Override
        public void onDateChanged(DatePicker datePicker, int year, int month, int day) {
            GregorianCalendar gregDate = new GregorianCalendar();
            gregDate.set(year, month, day);
            FrenchRevolutionaryCalendarDate frcDate = mFrc.getDate(gregDate);
            mFrcDatePicker.setDate(frcDate);
            updateObjectOfTheDayText(frcDate);
        }
    };

    private final SharedPreferences.OnSharedPreferenceChangeListener mPrefsListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (FRCPreferences.PREF_LANGUAGE.equals(key)) {
                Locale locale = FRCPreferences.getInstance(getApplicationContext().getApplicationContext()).getLocale();
                mFrc = new FrenchRevolutionaryCalendar(locale, getSelectedCalculationMethod());
                mFrcDatePicker.setLocale(locale);
                mFrcDateSelectedListener.onFrenchDateSelected(mFrcDatePicker.getDate());
            } else if (FRCPreferences.PREF_ROMAN_NUMERAL.equals(key)) {
                mFrcDatePicker.setUseRomanNumerals(FRCPreferences.getInstance(getApplicationContext().getApplicationContext()).isRomanNumeralEnabled());
            }
        }
    };

}
