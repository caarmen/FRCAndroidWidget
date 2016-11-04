/*
 * French Revolutionary Calendar Android Widget
 * Copyright (C) 2016 Carmen Alvarez
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
package ca.rmen.android.frccommon;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import ca.rmen.android.frccommon.prefs.FRCPreferenceActivity;
import ca.rmen.android.frccommon.prefs.FRCPreferences;
import ca.rmen.android.frenchcalendar.R;
import ca.rmen.lfrc.FrenchRevolutionaryCalendar;
import ca.rmen.lfrc.FrenchRevolutionaryCalendarDate;

public class FRCConverterActivity extends Activity {

    private TextView mTextViewRomme;
    private TextView mTextViewEquinox;
    private TextView mTextViewVonMadler;
    private FrenchRevolutionaryCalendar mFrcRomme;
    private FrenchRevolutionaryCalendar mFrcEquinox;
    private FrenchRevolutionaryCalendar mFrcVonMadler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.converter);
        Locale locale = FRCPreferences.getInstance(getApplicationContext()).getLocale();
        mTextViewRomme = (TextView) findViewById(R.id.frcDateRomme);
        mTextViewEquinox = (TextView) findViewById(R.id.frcDateEquinox);
        mTextViewVonMadler = (TextView) findViewById(R.id.frcDateVonMadler);
        mFrcRomme = new FrenchRevolutionaryCalendar(locale, FrenchRevolutionaryCalendar.CalculationMethod.ROMME);
        mFrcEquinox = new FrenchRevolutionaryCalendar(locale, FrenchRevolutionaryCalendar.CalculationMethod.EQUINOX);
        mFrcVonMadler = new FrenchRevolutionaryCalendar(locale, FrenchRevolutionaryCalendar.CalculationMethod.VON_MADLER);
        DatePicker datePicker = (DatePicker) findViewById(R.id.datePicker);
        Calendar now = Calendar.getInstance();
        datePicker.init(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH), mOnDateChangedListener);
        update(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
        findViewById(R.id.btn_help).setOnClickListener(mOnHelpClickedListener);
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

    private void update(int year, int monthOfYear, int dayOfMonth) {
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.set(Calendar.YEAR, year);
        gregorianCalendar.set(Calendar.MONTH, monthOfYear);
        gregorianCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        FrenchRevolutionaryCalendarDate frenchDateRomme = mFrcRomme.getDate(gregorianCalendar);
        mTextViewRomme.setText(format(frenchDateRomme));
        FrenchRevolutionaryCalendarDate frenchDateEquinox = mFrcEquinox.getDate(gregorianCalendar);
        mTextViewEquinox.setText(format(frenchDateEquinox));
        FrenchRevolutionaryCalendarDate frenchDateVonMadler = mFrcVonMadler.getDate(gregorianCalendar);
        mTextViewVonMadler.setText(format(frenchDateVonMadler));
    }

    private String format(FrenchRevolutionaryCalendarDate date) {
        return getString(R.string.date_format,
                date.getWeekdayName(),
                date.dayOfMonth,
                date.getMonthName(),
                date.year,
                date.getObjectTypeName(),
                date.getDayOfYear());
    }

    private final DatePicker.OnDateChangedListener mOnDateChangedListener = new DatePicker.OnDateChangedListener() {
        @Override
        public void onDateChanged(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
            update(year, monthOfYear, dayOfMonth);
        }
    };

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

}
