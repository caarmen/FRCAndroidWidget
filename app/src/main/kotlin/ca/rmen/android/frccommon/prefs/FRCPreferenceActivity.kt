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

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.preference.CheckBoxPreference
import android.preference.ListPreference
import android.preference.PreferenceActivity
import android.preference.PreferenceCategory
import android.preference.PreferenceManager
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import ca.rmen.android.frccommon.Constants
import ca.rmen.android.frccommon.compat.Api11Helper
import ca.rmen.android.frccommon.compat.ApiHelper
import ca.rmen.android.frcwidget.FRCWidgetScheduler
import ca.rmen.android.frenchcalendar.R
import net.margaritov.preference.colorpicker.ColorPickerPreference

/**
 * Configuration screen. The settings in this screen will apply to all widgets: narrow, wide and minimalist.
 *
 * @author calvarez
 */
class FRCPreferenceActivity : PreferenceActivity() {
    companion object {
        private val TAG = Constants.TAG + FRCPreferenceActivity::class.java.simpleName
    }

    private val mOnSharedPreferenceChangeListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        when (key) {
            FRCPreferences.PREF_METHOD -> updatePreferenceSummary(key, R.string.setting_method_summary)
            FRCPreferences.PREF_LANGUAGE -> updatePreferenceSummary(key, R.string.setting_language_summary)
            FRCPreferences.PREF_CUSTOM_COLOR_ENABLED -> updatePreferenceSummary(key, 0)
            FRCPreferences.PREF_SYSTEM_NOTIFICATION_PRIORITY -> updatePreferenceSummary(key, R.string.setting_system_notification_priority_summary)
        }
    }
    private lateinit var mSystemNotificationPreferenceListener: FRCSystemNotificationPreferenceListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (ApiHelper.getAPILevel() >= Build.VERSION_CODES.HONEYCOMB) {
            Api11Helper.setDisplayHomeAsUpEnabled(this)
        }
        /*
         * From the documentation: https://developer.android.com/guide/topics/appwidgets/index.html
         * The App Widget host calls the configuration Activity and the configuration
         * Activity should always return a result. The result should include the App Widget ID
         * passed by the Intent that launched the Activity (saved in the Intent extras as EXTRA_APPWIDGET_ID).
         */
        Log.v(TAG, "intent = " + intent)
        var appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID
        if (intent.extras != null) {
            appWidgetId = intent.extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
            Log.v(TAG, "intent extras = " + intent.extras)
        }
        val resultValue = Intent()
        if (appWidgetId > 0) {
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            Toast.makeText(this, R.string.message_save, Toast.LENGTH_LONG).show()
        }
        setResult(Activity.RESULT_OK, resultValue)
    }

    override fun onStart() {
        super.onStart()
        Log.v(TAG, "onStart")
        // We have to load the preferences in onStart instead of on onCreate, in
        // case the user pressed home (and not back) to exit the preference screen
        // last time, and has added or removed widgets since.
        @Suppress("DEPRECATION")
        preferenceScreen?.removeAll()

        @Suppress("DEPRECATION")
        addPreferencesFromResource(R.xml.widget_settings)

        updatePreferenceSummary(FRCPreferences.PREF_METHOD, R.string.setting_method_summary)
        updatePreferenceSummary(FRCPreferences.PREF_LANGUAGE, R.string.setting_language_summary)
        updatePreferenceSummary(FRCPreferences.PREF_CUSTOM_COLOR_ENABLED, 0)
        updatePreferenceSummary(FRCPreferences.PREF_SYSTEM_NOTIFICATION_PRIORITY, R.string.setting_system_notification_priority_summary)

        if (ApiHelper.getAPILevel() < 16 || ApiHelper.getAPILevel() >= 26) {
            @Suppress("DEPRECATION")
            val category = preferenceScreen.findPreference(FRCPreferences.PREF_CATEGORY_NOTIFICATION) as PreferenceCategory
            val notificationPriorityPreference = category.findPreference(FRCPreferences.PREF_SYSTEM_NOTIFICATION_PRIORITY)
            category.removePreference(notificationPriorityPreference)
        }

        @Suppress("DEPRECATION")
        val pref = preferenceScreen.findPreference(FRCPreferences.PREF_CUSTOM_COLOR) as ColorPickerPreference
        pref.setAlphaSliderEnabled(true)

        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(mOnSharedPreferenceChangeListener)
        mSystemNotificationPreferenceListener = FRCSystemNotificationPreferenceListener(this)
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(mSystemNotificationPreferenceListener)
    }

    override fun onStop() {
        Log.v(TAG, "onStop")
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(mOnSharedPreferenceChangeListener)
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(mSystemNotificationPreferenceListener)
        // When we leave the preference screen, reupdate all our widgets
        FRCWidgetScheduler.getInstance(this).schedule(this)
        super.onStop()
    }

    override fun onDestroy() {
        Log.v(TAG, "onDestroy")
        super.onDestroy()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun updatePreferenceSummary(key: String, summaryResId: Int) {
        @Suppress("DEPRECATION")
        val pref = preferenceManager.findPreference(key)
        if (pref is ListPreference) {
            pref.summary = getString(summaryResId, pref.entry)
        } else if (FRCPreferences.PREF_CUSTOM_COLOR_ENABLED == key) {
            if ((pref as CheckBoxPreference).isChecked) {
                pref.setSummary(R.string.setting_custom_color_summary_enabled)
            } else {
                pref.setSummary(R.string.setting_custom_color_summary_disabled)
            }
        }
    }

}
