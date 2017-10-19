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

import android.annotation.TargetApi
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
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.Spinner
import android.widget.TextView
import ca.rmen.android.frccommon.Constants
import ca.rmen.android.frccommon.prefs.FRCPreferenceActivity
import ca.rmen.android.frccommon.prefs.FRCPreferences
import ca.rmen.android.frenchcalendar.R
import ca.rmen.lfrc.FrenchRevolutionaryCalendar
import ca.rmen.lfrc.FrenchRevolutionaryCalendarDate
import java.util.Calendar
import java.util.GregorianCalendar

@TargetApi(Constants.MIN_API_LEVEL_TWO_WAY_CONVERTER)
class FRCConverterActivity : Activity() {
    private lateinit var mMethodSpinner: Spinner
    private lateinit var mFrcDatePicker: FRCDatePicker
    private lateinit var mObjectOfTheDayTextView: TextView
    private lateinit var mGregorianDatePicker: DatePicker
    private lateinit var mFrc: FrenchRevolutionaryCalendar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.converter)
        val method = FRCPreferences.getInstance(this).calculationMethod
        val locale = FRCPreferences.getInstance(this).locale
        mFrc = FrenchRevolutionaryCalendar(locale, method)

        findViewById<View>(R.id.btn_help).setOnClickListener(mOnHelpClickedListener)

        mMethodSpinner = findViewById(R.id.spinner_method)
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, resources.getStringArray(R.array.spinner_method_short_labels))
        spinnerAdapter.setDropDownViewResource(R.layout.spinner_drop_down_item)
        mMethodSpinner.adapter = spinnerAdapter
        setMethod(method)
        mMethodSpinner.onItemSelectedListener = mSpinnerSelectedListener

        mFrcDatePicker = findViewById(R.id.frc_date_picker)
        mFrcDatePicker.setLocale(locale)
        mFrcDatePicker.setOnDateSelectedListener(mFrcDateSelectedListener)
        mFrcDatePicker.setUseRomanNumerals(FRCPreferences.getInstance(applicationContext).isRomanNumeralEnabled)

        mObjectOfTheDayTextView = findViewById(R.id.object_of_the_day)

        mGregorianDatePicker = findViewById(R.id.gregorian_date_picker)
        val frenchEraBegin = Calendar.getInstance()
        frenchEraBegin.set(1792, 8, 22)
        mGregorianDatePicker.minDate = frenchEraBegin.timeInMillis
        resetGregorianDatePicker(null)

        makePickersFitInLandscape()

        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(mPrefsListener)
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

    override fun onDestroy() {
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(mPrefsListener)
        super.onDestroy()
    }

    private fun setMethod(method: FrenchRevolutionaryCalendar.CalculationMethod) {
        for (i in 0 until mMethodSpinner.adapter.count) {
            if (getCalculationMethodAtPosition(i) == method) {
                mMethodSpinner.setSelection(i)
                break
            }
        }
    }

    private fun getSelectedCalculationMethod(): FrenchRevolutionaryCalendar.CalculationMethod =
            getCalculationMethodAtPosition(mMethodSpinner.selectedItemPosition)

    private fun getCalculationMethodAtPosition(spinnerPosition: Int): FrenchRevolutionaryCalendar.CalculationMethod {
        val methodName = mMethodSpinner.adapter.getItem(spinnerPosition)
        val method: FrenchRevolutionaryCalendar.CalculationMethod

        method = when (methodName) {
            getString(R.string.method_romme) -> FrenchRevolutionaryCalendar.CalculationMethod.ROMME
            getString(R.string.method_equinox) -> FrenchRevolutionaryCalendar.CalculationMethod.EQUINOX
            else -> FrenchRevolutionaryCalendar.CalculationMethod.VON_MADLER
        }
        return method
    }

    fun resetFrcDatePicker(@Suppress("UNUSED_PARAMETER") view: View) {
        val frcDate = FrenchRevolutionaryCalendarDate(
                FRCPreferences.getInstance(this).locale,
                1, 1, 1,
                0, 0, 0)
        mFrcDatePicker.date = frcDate
        mFrcDateSelectedListener.onFrenchDateSelected(frcDate)
    }

    private fun updateObjectOfTheDayText(frcDate: FrenchRevolutionaryCalendarDate) {
        mObjectOfTheDayTextView.text = getString(R.string.object_of_the_day_format, frcDate.objectTypeName, frcDate.objectOfTheDay)
    }

    fun resetGregorianDatePicker(@Suppress("UNUSED_PARAMETER") view: View?) {
        val calendar = Calendar.getInstance()
        mGregorianDatePicker.init(calendar[Calendar.YEAR], calendar[Calendar.MONTH], calendar[Calendar.DAY_OF_MONTH], mGregorianDateSelectedListener)
        mGregorianDateSelectedListener.onDateChanged(mGregorianDatePicker, calendar[Calendar.YEAR], calendar[Calendar.MONTH], calendar[Calendar.DAY_OF_MONTH])
    }

    // In case we have a small screen: we resize the date pickers so they can fit.
    private fun makePickersFitInLandscape() {
        val container = findViewById<ViewGroup>(R.id.date_pickers)
        if (container != null) {
            mFrcDatePicker.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    mFrcDatePicker.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    val maxWidth = container.width
                    val childViewsWidth: Int = (0 until container.childCount).sumBy { container.getChildAt(it).width }
                    if (childViewsWidth > maxWidth) {
                        val scale = maxWidth / childViewsWidth.toFloat()
                        for (i in 0 until container.childCount) {
                            val child = container.getChildAt(i)
                            child.scaleX = scale
                            child.scaleY = scale
                        }
                    }
                }
            })
        }
    }

    private val mOnHelpClickedListener = View.OnClickListener {
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

    private val mSpinnerSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            val locale = FRCPreferences.getInstance(applicationContext).locale
            mFrc = FrenchRevolutionaryCalendar(locale, getSelectedCalculationMethod())
            mFrcDateSelectedListener.onFrenchDateSelected(mFrcDatePicker.date)
        }

        override fun onNothingSelected(parent: AdapterView<*>?) = Unit
    }

    private val mFrcDateSelectedListener = object : FRCDatePicker.OnDateSelectedListener {
        override fun onFrenchDateSelected(frcDate: FrenchRevolutionaryCalendarDate?) {
            if (frcDate != null) {
                val gregDate = mFrc.getDate(frcDate)
                updateObjectOfTheDayText(frcDate)
                mGregorianDatePicker.updateDate(gregDate[Calendar.YEAR], gregDate[Calendar.MONTH], gregDate[Calendar.DAY_OF_MONTH])
            }
        }
    }

    private val mGregorianDateSelectedListener = DatePicker.OnDateChangedListener { _, year, monthOfYear, dayOfMonth ->
        val gregDate = GregorianCalendar()
        gregDate.set(year, monthOfYear, dayOfMonth)
        val frcDate = mFrc.getDate(gregDate)
        mFrcDatePicker.date = frcDate
        updateObjectOfTheDayText(frcDate)
    }

    private val mPrefsListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        if (FRCPreferences.PREF_LANGUAGE == key) {
            val locale = FRCPreferences.getInstance(applicationContext).locale
            mFrc = FrenchRevolutionaryCalendar(locale, getSelectedCalculationMethod())
            mFrcDatePicker.setLocale(locale)
            mFrcDateSelectedListener.onFrenchDateSelected(mFrcDatePicker.date)
        } else if (FRCPreferences.PREF_ROMAN_NUMERAL == key) {
            mFrcDatePicker.setUseRomanNumerals(FRCPreferences.getInstance(applicationContext).isRomanNumeralEnabled)
        }
    }
}
