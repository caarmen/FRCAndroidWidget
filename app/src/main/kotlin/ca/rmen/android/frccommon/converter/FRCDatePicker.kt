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

package ca.rmen.android.frccommon.converter

import android.annotation.TargetApi
import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import android.view.View.OnTouchListener
import android.widget.LinearLayout
import android.widget.NumberPicker
import ca.rmen.android.frccommon.Constants
import ca.rmen.android.frccommon.FRCDateUtils
import ca.rmen.android.frenchcalendar.R
import ca.rmen.lfrc.FrenchRevolutionaryCalendarDate
import ca.rmen.lfrc.i18n.FrenchRevolutionaryCalendarLabels
import java.util.Locale

@TargetApi(Constants.MIN_API_LEVEL_TWO_WAY_CONVERTER)
class FRCDatePicker : LinearLayout {
    companion object {
        private const val EXTRA_YEAR = "year"
        private const val EXTRA_MONTH = "month"
        private const val EXTRA_DAY = "day"
        private const val EXTRA_SUPER_STATE = "super_state"
    }

    private lateinit var mLocale: Locale
    private lateinit var mDayPicker: NumberPicker
    private lateinit var mMonthPicker: NumberPicker
    private lateinit var mYearPicker: NumberPicker
    private var mListener: OnDateSelectedListener? = null

    interface OnDateSelectedListener {
        fun onFrenchDateSelected(frcDate: FrenchRevolutionaryCalendarDate?)
    }

    constructor(context: Context) : super(context) {
        initView(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView(context)
    }

    private fun initView(context: Context) {
        val view = View.inflate(context, R.layout.frc_date_picker, null)
        addView(view)
        mDayPicker = findViewById(R.id.spinnerDay)
        mMonthPicker = findViewById(R.id.spinnerMonth)
        mYearPicker = findViewById(R.id.spinnerYear)
        initNumberPicker(mDayPicker, 30)
        initNumberPicker(mMonthPicker, 13)
        initNumberPicker(mYearPicker, 300)
        setLocale(Locale.getDefault())
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        val bundle = state as Bundle
        val superState = bundle[EXTRA_SUPER_STATE] as Parcelable
        super.onRestoreInstanceState(superState)
        mYearPicker.value = bundle.getInt(EXTRA_YEAR)
        mMonthPicker.value = bundle.getInt(EXTRA_MONTH)
        mDayPicker.value = bundle.getInt(EXTRA_DAY)
    }

    override fun onSaveInstanceState(): Parcelable {
        val superState = super.onSaveInstanceState()
        val result = Bundle(4)
        result.putParcelable(EXTRA_SUPER_STATE, superState)
        result.putInt(EXTRA_YEAR, mYearPicker.value)
        result.putInt(EXTRA_MONTH, mMonthPicker.value)
        result.putInt(EXTRA_DAY, mDayPicker.value)
        return result
    }

    fun setOnDateSelectedListener(listener: OnDateSelectedListener) {
        mListener = listener
    }

    fun setLocale(locale: Locale) {
        mLocale = locale
        setDisplayedValues(mDayPicker, getDayNames(locale))
        setDisplayedValues(mMonthPicker, getMonthNames(locale))
    }

    private fun setDisplayedValues(numberPicker: NumberPicker, values: Array<String>?) {
        numberPicker.displayedValues = values
        // Workaround to force a new layout to resize the number picker to fit the new labels.
        // Calling invalidate(), postInvalidate() and requestLayout() didn't work.
        numberPicker.visibility = View.GONE
        numberPicker.visibility = View.VISIBLE
    }

    fun setUseRomanNumerals(useRomanNumerals: Boolean) =
            setDisplayedValues(mYearPicker, if (useRomanNumerals) getRomanNumeralYears() else null)

    var date
        get(): FrenchRevolutionaryCalendarDate? =
            if (mDayPicker.value > 6 && mMonthPicker.value == 13) null
            else FrenchRevolutionaryCalendarDate(mLocale,
                    mYearPicker.value,
                    mMonthPicker.value,
                    mDayPicker.value,
                    0, 0, 0)
        set(frcDate) {
            if (frcDate != null) {
                mYearPicker.value = frcDate.year
                mMonthPicker.value = frcDate.month
                setValidRanges()
                mDayPicker.value = frcDate.dayOfMonth
            }
        }

    private fun getDayNames(locale: Locale): Array<String> {
        val cal = FrenchRevolutionaryCalendarLabels.getInstance(locale)
        return Array(30) { i ->
            val dayIndex = if ((i + 1) % 10 == 0) 10 else (i + 1) % 10
            (i + 1).toString() + " " + cal.getWeekdayName(dayIndex)
        }
    }

    private fun getMonthNames(locale: Locale): Array<String> {
        val cal = FrenchRevolutionaryCalendarLabels.getInstance(locale)
        return Array(13) { i ->
            cal.getMonthName(i + 1)
        }
    }

    private fun getRomanNumeralYears(): Array<String> =
            Array(300) { i ->
                FRCDateUtils.getRomanNumeral(i + 1)
            }

    private fun initNumberPicker(numberPicker: NumberPicker, maxValue: Int) {
        if (numberPicker.value == 0) {
            numberPicker.minValue = 1
            numberPicker.maxValue = maxValue
        }
        numberPicker.setOnValueChangedListener(mValueChangedListener)
        numberPicker.setOnTouchListener(mIgnoreParentTouchListener)
    }

    private fun setValidRanges() {
        if (mMonthPicker.value == 13 && mDayPicker.maxValue == 30) {
            if (mDayPicker.value > 6) mDayPicker.value = 5
            mDayPicker.maxValue = 6
        } else if (mMonthPicker.value < 13 && mDayPicker.maxValue == 6) {
            mDayPicker.maxValue = 30
        }
    }

    private val mValueChangedListener = NumberPicker.OnValueChangeListener { _, _, _ ->
        setValidRanges()
        mListener?.onFrenchDateSelected(date)
    }

    // Setting on Touch Listener for handling the touch inside ScrollView
    // https://stackoverflow.com/questions/18367522/android-list-view-inside-a-scroll-view
    private val mIgnoreParentTouchListener = OnTouchListener { v, _ ->
        // Disallow the touch request for parent scroll on touch of child view
        v.parent.requestDisallowInterceptTouchEvent(true)
        false
    }
}
