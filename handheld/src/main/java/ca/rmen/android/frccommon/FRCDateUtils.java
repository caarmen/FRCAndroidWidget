/*
 * French Revolutionary Calendar Android Widget
 * Copyright (C) 2011 - 2014 Carmen Alvarez
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package ca.rmen.android.frccommon;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import android.content.Context;
import android.util.Log;
import ca.rmen.android.frccommon.prefs.FRCPreferences;
import ca.rmen.lfrc.FrenchRevolutionaryCalendar;
import ca.rmen.lfrc.FrenchRevolutionaryCalendar.CalculationMethod;
import ca.rmen.lfrc.FrenchRevolutionaryCalendarDate;

public class FRCDateUtils {
    private static final String TAG = Constants.TAG + FRCDateUtils.class.getSimpleName();

    public static final FrenchRevolutionaryCalendarDate getToday(Context context) {
        Log.v(TAG, "getToday");
        // Get the current timestamp in the French revolutionary calendar.
        GregorianCalendar now = new GregorianCalendar();
        Locale locale = FRCPreferences.getInstance(context).getLocale();
        CalculationMethod calculationMethod = FRCPreferences.getInstance(context).getCalculationMethod();
        FrenchRevolutionaryCalendar frcal = new FrenchRevolutionaryCalendar(locale, calculationMethod);
        FrenchRevolutionaryCalendarDate frenchDate = frcal.getDate(now);
        return frenchDate;
    }

    /**
     * @return the number of days since the first day of the French Republican Calendar (September 22, 1792.
     */
    public static final long getDaysSinceDay1() {
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
}
