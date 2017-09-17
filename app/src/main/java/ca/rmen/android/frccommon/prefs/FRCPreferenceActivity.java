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

import android.annotation.SuppressLint;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import net.margaritov.preference.colorpicker.ColorPickerPreference;

import ca.rmen.android.frccommon.Constants;
import ca.rmen.android.frccommon.compat.Api11Helper;
import ca.rmen.android.frccommon.compat.ApiHelper;
import ca.rmen.android.frcwidget.FRCWidgetScheduler;
import ca.rmen.android.frenchcalendar.R;

/**
 * Configuration screen. The settings in this screen will apply to all widgets: both narrow and wide.
 *
 * @author calvarez
 */
@SuppressLint("ExportedPreferenceActivity")
public class FRCPreferenceActivity extends PreferenceActivity { // NO_UCD (use default)

    private static final String TAG = Constants.TAG + FRCPreferenceActivity.class.getSimpleName();
    private final OnSharedPreferenceChangeListener mOnSharedPreferenceChangeListener = new OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            switch (key) {
                case FRCPreferences.PREF_METHOD:
                    updatePreferenceSummary(key, R.string.setting_method_summary);
                    break;
                case FRCPreferences.PREF_LANGUAGE:
                    updatePreferenceSummary(key, R.string.setting_language_summary);
                    break;
                case FRCPreferences.PREF_CUSTOM_COLOR_ENABLED:
                    updatePreferenceSummary(key, 0);
                    break;
                case FRCPreferences.PREF_SYSTEM_NOTIFICATION_PRIORITY:
                    updatePreferenceSummary(key, R.string.setting_system_notification_priority_summary);
                    break;
            }
        }
    };
    private FRCSystemNotificationPreferenceListener mSystemNotificationPreferenceListener;

    @Override
    protected void onCreate(Bundle icicle) {
        Log.v(TAG, "onCreate: bundle = " + icicle);
        super.onCreate(icicle);
        if (ApiHelper.getAPILevel() >= Build.VERSION_CODES.HONEYCOMB) {
            Api11Helper.setDisplayHomeAsUpEnabled(this);
        }
        /*
         * From the documentation: https://developer.android.com/guide/topics/appwidgets/index.html
         * The App Widget host calls the configuration Activity and the configuration
         * Activity should always return a result. The result should include the App Widget ID
         * passed by the Intent that launched the Activity (saved in the Intent extras as EXTRA_APPWIDGET_ID).
         */
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        Log.v(TAG, "intent = " + getIntent());
        int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
        if (extras != null) {
            appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
            Log.v(TAG, "intent extras = " + extras);
        }
        Intent resultValue = new Intent();
        if (appWidgetId > 0) {
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            Toast.makeText(this, R.string.message_save, Toast.LENGTH_LONG).show();
        }
        setResult(RESULT_OK, resultValue);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.v(TAG, "onStart");

        // We have to load the preferences in onStart instead of on onCreate, in
        // case the user pressed home (and not back) to exit the preference screen
        // last time, and has added or removed widgets since.
        //noinspection deprecation
        PreferenceScreen preferencesScreen = getPreferenceScreen();
        if (preferencesScreen != null)
            preferencesScreen.removeAll();

        //noinspection deprecation
        addPreferencesFromResource(R.xml.widget_settings);

        updatePreferenceSummary(FRCPreferences.PREF_METHOD, R.string.setting_method_summary);
        updatePreferenceSummary(FRCPreferences.PREF_LANGUAGE, R.string.setting_language_summary);
        updatePreferenceSummary(FRCPreferences.PREF_CUSTOM_COLOR_ENABLED, 0);
        updatePreferenceSummary(FRCPreferences.PREF_SYSTEM_NOTIFICATION_PRIORITY, R.string.setting_system_notification_priority_summary);

        if (ApiHelper.getAPILevel() < 16 || ApiHelper.getAPILevel() >= 26) {
            @SuppressWarnings("deprecation") PreferenceCategory category = (PreferenceCategory) getPreferenceScreen().findPreference(FRCPreferences.PREF_CATEGORY_NOTIFICATION);
            Preference notificationPriorityPreference = category.findPreference(FRCPreferences.PREF_SYSTEM_NOTIFICATION_PRIORITY);
            category.removePreference(notificationPriorityPreference);
        }

        //noinspection deprecation
        ColorPickerPreference pref = (ColorPickerPreference) getPreferenceScreen().findPreference(FRCPreferences.PREF_CUSTOM_COLOR);
        pref.setAlphaSliderEnabled(true);

        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(mOnSharedPreferenceChangeListener);
        mSystemNotificationPreferenceListener = new FRCSystemNotificationPreferenceListener(this);
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(mSystemNotificationPreferenceListener);
    }

    @Override
    protected void onStop() {
        Log.v(TAG, "onStop");
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(mOnSharedPreferenceChangeListener);
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(mSystemNotificationPreferenceListener);
        // When we leave the preference screen, reupdate all our widgets
        FRCWidgetScheduler.getInstance(this).schedule(this);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.v(TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void updatePreferenceSummary(String key, int summaryResId) {
        //noinspection deprecation
        Preference pref = getPreferenceManager().findPreference(key);
        if (pref instanceof ListPreference) {
            String summary = getString(summaryResId, ((ListPreference) pref).getEntry());
            pref.setSummary(summary);
        } else if (FRCPreferences.PREF_CUSTOM_COLOR_ENABLED.equals(key)) {
            if (((CheckBoxPreference) pref).isChecked()) {
                pref.setSummary(R.string.setting_custom_color_summary_enabled);
            } else {
                pref.setSummary(R.string.setting_custom_color_summary_disabled);
            }

        }
    }

}
