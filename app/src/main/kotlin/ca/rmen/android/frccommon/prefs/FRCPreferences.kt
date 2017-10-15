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

package ca.rmen.android.frccommon.prefs

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import ca.rmen.android.frccommon.compat.NotificationCompat
import ca.rmen.lfrc.FrenchRevolutionaryCalendar
import java.util.Locale

/**
 * Preference names and values used by this app.
 *
 * @author calvarez
 */

class FRCPreferences private constructor(context: Context) {
    companion object {
        const val PREF_METHOD = "setting_method"
        const val PREF_ROMAN_NUMERAL = "setting_roman_numeral"
        const val PREF_LANGUAGE = "setting_language"
        const val PREF_SYSTEM_NOTIFICATION_PRIORITY = "setting_system_notification_priority"
        const val PREF_CUSTOM_COLOR = "setting_custom_color"
        const val PREF_CUSTOM_COLOR_ENABLED = "setting_custom_color_enabled"
        const val PREF_SYSTEM_NOTIFICATION = "setting_system_notification"
        const val PREF_CATEGORY_NOTIFICATION = "setting_category_notification"
        const val FREQUENCY_DAYS = 86400000
        private val PREF_DEPRECATED_DETAIL_VIEW = "settings_detailed_view"
        private val PREF_SHOW_TIME = "setting_show_time"
        private val PREF_SHOW_DAY_OF_YEAR = "setting_show_day_of_year"
        private val PREF_SYSTEM_NOTIFICATION_PRIORITY_DEFAULT = "default"
        private val FREQUENCY_MINUTES = 86400

        private lateinit var mSharedPrefs: SharedPreferences
        @Volatile
        private var sInstance: FRCPreferences? = null

        fun getInstance(context: Context): FRCPreferences =
                sInstance ?: synchronized(this) {
                    sInstance ?: FRCPreferences(context).also { sInstance = it }
                }
    }

    init {
        mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(context)
        migrateDetailedViewSetting()
    }

    private fun migrateDetailedViewSetting() {
        if (mSharedPrefs.contains(PREF_DEPRECATED_DETAIL_VIEW)) {
            val detailedViewValue = mSharedPrefs.getString(PREF_DEPRECATED_DETAIL_VIEW, "day_of_year")
            val editor = mSharedPrefs.edit()
            when (detailedViewValue) {
                "time" -> {
                    editor.putBoolean(PREF_SHOW_TIME, true)
                    editor.putBoolean(PREF_SHOW_DAY_OF_YEAR, false)
                }
                "day_of_year" -> {
                    editor.putBoolean(PREF_SHOW_TIME, false)
                    editor.putBoolean(PREF_SHOW_DAY_OF_YEAR, true)
                }
                "none" -> {
                    editor.putBoolean(PREF_SHOW_TIME, false)
                    editor.putBoolean(PREF_SHOW_DAY_OF_YEAR, false)
                }
            }
            editor.remove(PREF_DEPRECATED_DETAIL_VIEW).commit()
        }
    }

    val locale get() : Locale = Locale(mSharedPrefs.getString(PREF_LANGUAGE, "fr"))

    val isCustomColorEnabled get(): Boolean = mSharedPrefs.getBoolean(PREF_CUSTOM_COLOR_ENABLED, false)

    val color get(): Int = mSharedPrefs.getInt(PREF_CUSTOM_COLOR, -1)

    val calculationMethod
        get(): FrenchRevolutionaryCalendar.CalculationMethod {
            val methodPref = mSharedPrefs.getString(PREF_METHOD, "0").toInt()
            return FrenchRevolutionaryCalendar.CalculationMethod.values()[methodPref]
        }

    val isTimeEnabled get(): Boolean = mSharedPrefs.getBoolean(PREF_SHOW_TIME, false)

    val isRomanNumeralEnabled get(): Boolean = mSharedPrefs.getBoolean(PREF_ROMAN_NUMERAL, false)

    val isDayOfYearEnabled get(): Boolean = mSharedPrefs.getBoolean(PREF_SHOW_DAY_OF_YEAR, true)

    val updateFrequency get(): Int = if (isTimeEnabled) FREQUENCY_MINUTES else FREQUENCY_DAYS

    val systemNotificationEnabled get(): Boolean = mSharedPrefs.getBoolean(PREF_SYSTEM_NOTIFICATION, false)

    val systemNotificationPriority
        get(): Int {
            val priorityPref = mSharedPrefs.getString(PREF_SYSTEM_NOTIFICATION_PRIORITY, PREF_SYSTEM_NOTIFICATION_PRIORITY_DEFAULT)
            return NotificationCompat.getNotificationPriority(priorityPref)
        }

}
