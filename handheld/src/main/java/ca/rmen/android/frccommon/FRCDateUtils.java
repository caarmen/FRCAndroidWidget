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
package ca.rmen.android.frccommon;

import android.content.Context;
import android.os.Build;
import android.support.annotation.VisibleForTesting;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import ca.rmen.android.frccommon.prefs.FRCPreferences;
import ca.rmen.android.frenchcalendar.R;
import ca.rmen.lfrc.FrenchRevolutionaryCalendar;
import ca.rmen.lfrc.FrenchRevolutionaryCalendar.CalculationMethod;
import ca.rmen.lfrc.FrenchRevolutionaryCalendarDate;

public class FRCDateUtils {
    private static final String TAG = Constants.TAG + FRCDateUtils.class.getSimpleName();

    public static FrenchRevolutionaryCalendarDate getToday(Context context) {
        Log.v(TAG, "getToday");
        // Get the current timestamp in the French revolutionary calendar.
        GregorianCalendar now = new GregorianCalendar();
        Locale locale = FRCPreferences.getInstance(context).getLocale();
        CalculationMethod calculationMethod = FRCPreferences.getInstance(context).getCalculationMethod();
        FrenchRevolutionaryCalendar cal = new FrenchRevolutionaryCalendar(locale, calculationMethod);
        return cal.getDate(now);
    }

    /**
     * @return the number of days since the first day of the French Republican Calendar (September 22, 1792.
     */
    public static long getDaysSinceDay1() {
        Log.v(TAG, "getDaysSinceDay1");
        Calendar now = Calendar.getInstance();
        Calendar day1 = Calendar.getInstance();
        day1.set(Calendar.DAY_OF_MONTH, 22);
        day1.set(Calendar.MONTH, Calendar.SEPTEMBER);
        day1.set(Calendar.YEAR, 1792);
        day1.set(Calendar.HOUR_OF_DAY, now.get(Calendar.HOUR_OF_DAY));
        day1.set(Calendar.MINUTE, now.get(Calendar.MINUTE));
        day1.set(Calendar.SECOND, now.get(Calendar.SECOND));
        day1.set(Calendar.MILLISECOND, now.get(Calendar.MILLISECOND));
        long elapsedMilliseconds = now.getTimeInMillis() - day1.getTimeInMillis();
        return elapsedMilliseconds / (1000 * 60 * 60 * 24);
    }

    /**
     * @return the color to display for the widget/notification for the given date (for now it's just based on the month)
     * TODO this might not be a "date utility" method, but I can't find a better place to put it.
     */
    public static int getColor(Context context, FrenchRevolutionaryCalendarDate date) {
        FRCPreferences prefs = FRCPreferences.getInstance(context);
        if(prefs.isCustomColorEnabled()) {
            return prefs.getColor();
        }
        String colorResIdStr = "month_" + date.month;
        int colorResId = context.getResources().getIdentifier(colorResIdStr, "color", R.class.getPackage().getName());
        //noinspection deprecation
        if (Integer.valueOf(Build.VERSION.SDK) >= Build.VERSION_CODES.GINGERBREAD) {
            return ContextCompat.getColor(context, colorResId);
        } else {
            //noinspection deprecation
            return context.getResources().getColor(colorResId);
        }
    }

    /**
     * @return the number as a Roman numeral if the roman numeral setting is true and the given number is between 1 and 4999 inclusive, an Arabic number representation otherwise.
     */
    public static String formatNumber(Context context, int number) {
        if (FRCPreferences.getInstance(context).isRomanNumeralEnabled()) {
            return getRomanNumeral(number);
        } else {
            return String.valueOf(number);
        }
    }

    // http://stackoverflow.com/questions/12967896/converting-integers-to-roman-numerals-java
    private static final int ROMAN_NUMERAL_MIN_VALUE = 1;
    private static final int ROMAN_NUMERAL_MAX_VALUE = 4999;
    private static final String[] RN_1000 = {"", "M", "MM", "MMM", "MMMM"};
    private static final String[] RN_100 = {"", "C", "CC", "CCC", "CD", "D", "DC", "DCC", "DCCC", "CM"};
    private static final String[] RN_10 = {"", "X", "XX", "XXX", "XL", "L", "LX", "LXX", "LXXX", "XC"};
    private static final String[] RN_1 = {"", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX"};

    /**
     * @param number must be between 1 and 4999 inclusive, to return a roman numeral.
     * @return the roman numeral for the given number, if it is within the bounds.  Otherwise the arabic numeral is returned.
     */
    @VisibleForTesting
    static String getRomanNumeral(int number) {
        if (number < ROMAN_NUMERAL_MIN_VALUE || number > ROMAN_NUMERAL_MAX_VALUE) {
            return String.valueOf(number);
        }

        return RN_1000[number / 1000] +
                RN_100[number % 1000 / 100] +
                RN_10[number % 100 / 10] +
                RN_1[number % 10];
    }
}
