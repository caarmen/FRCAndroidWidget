/*
 * French Revolutionary Calendar Android Widget
 * Copyright (C) 2011 - 2017 Carmen Alvarez
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

package ca.rmen.android.frccommon

import android.content.Context
import android.support.annotation.ColorInt
import android.support.annotation.ColorRes
import android.util.Log
import ca.rmen.android.frccommon.compat.ContextCompat
import ca.rmen.android.frccommon.prefs.FRCPreferences
import ca.rmen.android.frenchcalendar.R
import ca.rmen.lfrc.FrenchRevolutionaryCalendar
import ca.rmen.lfrc.FrenchRevolutionaryCalendarDate
import java.util.Calendar
import java.util.GregorianCalendar

object FRCDateUtils {
    private val TAG = Constants.TAG + FRCDateUtils::class.java.simpleName

    fun getToday(context: Context): FrenchRevolutionaryCalendarDate {
        Log.v(TAG, "getToday")
        // Get the current timestamp in the French revolutionary calendar.
        val now = GregorianCalendar()
        val locale = FRCPreferences.getInstance(context).locale
        val calculationMethod = FRCPreferences.getInstance(context).calculationMethod
        val cal = FrenchRevolutionaryCalendar(locale, calculationMethod)
        return cal.getDate(now)
    }

    /**
     * @return the number of days since the first day of the French Republican Calendar (September 22, 1792.
     */
    val daysSinceDay1: Long
        get() {
            Log.v(TAG, "getDaysSinceDay1")
            val now = Calendar.getInstance()
            val day1 = Calendar.getInstance()
            day1[Calendar.DAY_OF_MONTH] = 22
            day1[Calendar.MONTH] = Calendar.SEPTEMBER
            day1[Calendar.YEAR] = 1792
            day1[Calendar.HOUR_OF_DAY] = now[Calendar.HOUR_OF_DAY]
            day1[Calendar.MINUTE] = now[Calendar.MINUTE]
            day1[Calendar.SECOND] = now[Calendar.SECOND]
            day1[Calendar.MILLISECOND] = now[Calendar.MILLISECOND]
            val elapsedMilliseconds = now.timeInMillis - day1.timeInMillis
            return elapsedMilliseconds / (1000 * 60 * 60 * 24)
        }

    /**
     * @return the color to display for the widget/notification for the given date (for now it's just based on the month)
     * TODO this might not be a "date utility" method, but I can't find a better place to put it.
     */
    @ColorInt
    fun getColor(context: Context, date: FrenchRevolutionaryCalendarDate): Int {
        val prefs = FRCPreferences.getInstance(context)
        if (prefs.isCustomColorEnabled) {
            return prefs.color
        }
        val colorResIdStr = "month_" + date.month
        @ColorRes
        val colorResId = context.resources.getIdentifier(colorResIdStr, "color", R::class.java.`package`.name)
        return ContextCompat.getColor(context, colorResId)
    }

    /**
     * @return the number as a Roman numeral if the roman numeral setting is true and the given number is between 1 and 4999 inclusive, an Arabic number representation otherwise.
     */
    fun formatNumber(context: Context, number: Int): String =
            if (FRCPreferences.getInstance(context).isRomanNumeralEnabled) {
                getRomanNumeral(number)
            } else {
                number.toString()
            }

    // http://stackoverflow.com/questions/12967896/converting-integers-to-roman-numerals-java
    private const val ROMAN_NUMERAL_MIN_VALUE = 1
    private const val ROMAN_NUMERAL_MAX_VALUE = 4999
    private val RN_1000 = arrayOf("", "M", "MM", "MMM", "MMMM")
    private val RN_100 = arrayOf("", "C", "CC", "CCC", "CD", "D", "DC", "DCC", "DCCC", "CM")
    private val RN_10 = arrayOf("", "X", "XX", "XXX", "XL", "L", "LX", "LXX", "LXXX", "XC")
    private val RN_1 = arrayOf("", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX")

    /**
     * @param number must be between 1 and 4999 inclusive, to return a roman numeral.
     * @return the roman numeral for the given number, if it is within the bounds.  Otherwise the arabic numeral is returned.
     */
    fun getRomanNumeral(number: Int): String =
            if (number < ROMAN_NUMERAL_MIN_VALUE || number > ROMAN_NUMERAL_MAX_VALUE) {
                number.toString()
            } else {
                RN_1000[number / 1000] +
                        RN_100[number % 1000 / 100] +
                        RN_10[number % 100 / 10] +
                        RN_1[number % 10]
            }
}