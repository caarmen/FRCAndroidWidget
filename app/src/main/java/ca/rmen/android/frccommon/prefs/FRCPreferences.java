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
package ca.rmen.android.frccommon.prefs;

import java.util.Locale;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import ca.rmen.lfrc.FrenchRevolutionaryCalendar.CalculationMethod;

/**
 * Preference names and values used by this app.
 *
 * @author calvarez
 */
public class FRCPreferences {

    static final String PREF_METHOD = "setting_method";
    private static final String PREF_DEPRECATED_DETAILED_VIEW = "setting_detailed_view";
    private static final String PREF_SHOW_TIME = "setting_show_time";
    private static final String PREF_ROMAN_NUMERAL = "setting_roman_numeral";
    private static final String PREF_SHOW_DAY_OF_YEAR = "setting_show_day_of_year";
    public static final String PREF_LANGUAGE = "setting_language";
    static final String PREF_CUSTOM_COLOR = "setting_custom_color";
    static final String PREF_CUSTOM_COLOR_ENABLED = "setting_custom_color_enabled";
    static final String PREF_SYSTEM_NOTIFICATION = "setting_system_notification";
    private static final int FREQUENCY_MINUTES = 86400;
    public static final int FREQUENCY_DAYS = 86400000;

    private static FRCPreferences sInstance = null;

    private final SharedPreferences mSharedPrefs;

    public synchronized static FRCPreferences getInstance(Context context) {
        if (sInstance == null) sInstance = new FRCPreferences(context);
        return sInstance;
    }

    private FRCPreferences(Context context) {
        mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        migrateDetailedViewSetting();
    }

    private void migrateDetailedViewSetting() {
        if (mSharedPrefs.contains(PREF_DEPRECATED_DETAILED_VIEW)) {
            String detailedViewValue = mSharedPrefs.getString(PREF_DEPRECATED_DETAILED_VIEW, "day_of_year");
            SharedPreferences.Editor editor = mSharedPrefs.edit();
            if ("time".equals(detailedViewValue)) {
                editor.putBoolean(PREF_SHOW_TIME, true);
                editor.putBoolean(PREF_SHOW_DAY_OF_YEAR, false);
            } else if ("day_of_year".equals(detailedViewValue)) {
                editor.putBoolean(PREF_SHOW_TIME, false);
                editor.putBoolean(PREF_SHOW_DAY_OF_YEAR, true);
            } else if ("none".equals(detailedViewValue)) {
                editor.putBoolean(PREF_SHOW_TIME, false);
                editor.putBoolean(PREF_SHOW_DAY_OF_YEAR, false);
            }
            editor.remove(PREF_DEPRECATED_DETAILED_VIEW).commit();
        }
    }

    public Locale getLocale() {
        String language = mSharedPrefs.getString(PREF_LANGUAGE, "fr");
        return new Locale(language);
    }

    public boolean isCustomColorEnabled() {
        return mSharedPrefs.getBoolean(PREF_CUSTOM_COLOR_ENABLED, false);
    }
    
    public int getColor() {
        return mSharedPrefs.getInt(PREF_CUSTOM_COLOR, -1);
    }

    public CalculationMethod getCalculationMethod() {
        String methodPrefStr = mSharedPrefs.getString(PREF_METHOD, "0");
        int calculationMethodInt = Integer.parseInt(methodPrefStr);
        return CalculationMethod.values()[calculationMethodInt];
    }

    public boolean isTimeEnabled() {
        return mSharedPrefs.getBoolean(PREF_SHOW_TIME, false);
    }

    public boolean isRomanNumeralEnabled(){
        return mSharedPrefs.getBoolean(PREF_ROMAN_NUMERAL, false);
    }

    public boolean isDayOfYearEnabled() {
        return mSharedPrefs.getBoolean(PREF_SHOW_DAY_OF_YEAR, true);
    }

    public int getUpdateFrequency() {
        return isTimeEnabled() ? FREQUENCY_MINUTES : FREQUENCY_DAYS;
    }

    public boolean getSystemNotificationEnabled() {
        return mSharedPrefs.getBoolean(PREF_SYSTEM_NOTIFICATION, false);
    }

}
