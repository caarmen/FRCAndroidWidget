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

package ca.rmen.android.frccommon.converter

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.DatePicker
import android.widget.TextView
import ca.rmen.android.frccommon.FRCDateUtils
import ca.rmen.android.frccommon.prefs.FRCPreferenceActivity
import ca.rmen.android.frccommon.prefs.FRCPreferences
import ca.rmen.android.frenchcalendar.R
import ca.rmen.lfrc.FrenchRevolutionaryCalendar
import ca.rmen.lfrc.FrenchRevolutionaryCalendarDate
import java.util.Calendar
import java.util.GregorianCalendar

class FRCLegacyConverterActivity : Activity() {
    private lateinit var mTextViewRomme: TextView
    private lateinit var mTextViewEquinox: TextView
    private lateinit var mTextViewVonMadler: TextView
    private lateinit var mDatePicker: DatePicker
    private lateinit var mFrcRomme: FrenchRevolutionaryCalendar
    private lateinit var mFrcEquinox: FrenchRevolutionaryCalendar
    private lateinit var mFrcVonMadler: FrenchRevolutionaryCalendar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.converter_legacy)
        val locale = FRCPreferences.getInstance(applicationContext).locale
        mTextViewRomme = findViewById(R.id.frcDateRomme)
        mTextViewEquinox = findViewById(R.id.frcDateEquinox)
        mTextViewVonMadler = findViewById(R.id.frcDateVonMadler)
        mFrcRomme = FrenchRevolutionaryCalendar(locale, FrenchRevolutionaryCalendar.CalculationMethod.ROMME)
        mFrcEquinox = FrenchRevolutionaryCalendar(locale, FrenchRevolutionaryCalendar.CalculationMethod.EQUINOX)
        mFrcVonMadler = FrenchRevolutionaryCalendar(locale, FrenchRevolutionaryCalendar.CalculationMethod.VON_MADLER)
        mDatePicker = findViewById(R.id.datePicker)
        val now = Calendar.getInstance()
        mDatePicker.init(now[Calendar.YEAR], now[Calendar.MONTH], now[Calendar.DAY_OF_MONTH], mOnDateChangedListener)
        update(now[Calendar.YEAR], now[Calendar.MONTH], now[Calendar.DAY_OF_MONTH])
        findViewById<View>(R.id.btn_help).setOnClickListener(mOnHelpClickedListener)
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(mPrefsListener)
    }

    override fun onDestroy() {
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(mPrefsListener)
        super.onDestroy()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_settings) {
            val intent = Intent(this, FRCPreferenceActivity::class.java)
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun update(year: Int, monthOfYear: Int, dayOfMonth: Int) {
        val gregorianCalendar = GregorianCalendar()
        gregorianCalendar[Calendar.YEAR] = year
        gregorianCalendar[Calendar.MONTH] = monthOfYear
        gregorianCalendar[Calendar.DAY_OF_MONTH] = dayOfMonth

        val frenchDateRomme = mFrcRomme.getDate(gregorianCalendar)
        mTextViewRomme.text = format(frenchDateRomme)
        val frenchDateEquinox = mFrcEquinox.getDate(gregorianCalendar)
        mTextViewEquinox.text = format(frenchDateEquinox)
        val frenchDateVonMadler = mFrcVonMadler.getDate(gregorianCalendar)
        mTextViewVonMadler.text = format(frenchDateVonMadler)
    }

    private fun format(date: FrenchRevolutionaryCalendarDate): String {
        return getString(R.string.date_format,
                date.weekdayName,
                date.dayOfMonth,
                date.monthName,
                FRCDateUtils.formatNumber(this, date.year),
                date.objectTypeName,
                date.objectOfTheDay)
    }

    private val mOnDateChangedListener = DatePicker.OnDateChangedListener { _, year, monthOfYear, dayOfMonth ->
        update(year, monthOfYear, dayOfMonth)
    }

    private val mOnHelpClickedListener = View.OnClickListener { _ ->
        val message = TextUtils.concat(getText(R.string.romme_description),
                "\n",
                getText(R.string.equinox_description),
                "\n",
                getText(R.string.von_madler_description))
        AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .show()
    }

    private val mPrefsListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        if (FRCPreferences.PREF_LANGUAGE == key) {
            val locale = FRCPreferences.getInstance(applicationContext).locale
            mFrcRomme = FrenchRevolutionaryCalendar(locale, FrenchRevolutionaryCalendar.CalculationMethod.ROMME)
            mFrcEquinox = FrenchRevolutionaryCalendar(locale, FrenchRevolutionaryCalendar.CalculationMethod.EQUINOX)
            mFrcVonMadler = FrenchRevolutionaryCalendar(locale, FrenchRevolutionaryCalendar.CalculationMethod.VON_MADLER)
        }
        update(mDatePicker.year, mDatePicker.month, mDatePicker.dayOfMonth)
    }
}