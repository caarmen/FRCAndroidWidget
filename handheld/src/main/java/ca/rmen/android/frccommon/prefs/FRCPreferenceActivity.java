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
package ca.rmen.android.frccommon.prefs;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;

import ca.rmen.android.frcwear.FRCWearPreferenceListener;
import ca.rmen.android.frcwidget.FRCWidgetScheduler;
import ca.rmen.android.frenchcalendar.R;

/**
 * Configuration screen. The settings in this screen will apply to all widgets: both narrow and wide.
 *
 * @author calvarez
 */
public class FRCPreferenceActivity extends PreferenceActivity { // NO_UCD (use default)

    private static final String TAG = FRCPreferenceActivity.class.getSimpleName();
    private FRCWearPreferenceListener mWearPreferenceListener;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle icicle) {
        Log.v(TAG, "onCreate: bundle = " + icicle);
        super.onCreate(icicle);

        addPreferencesFromResource(R.xml.widget_settings);

        updatePreferenceSummary(FRCPreferences.PREF_METHOD, R.string.setting_method_summary);
        updatePreferenceSummary(FRCPreferences.PREF_DETAILED_VIEW, R.string.setting_detailed_view_summary);
        updatePreferenceSummary(FRCPreferences.PREF_LANGUAGE, R.string.setting_language_summary);
        /*
         * From the documentation: https://developer.android.com/guide/topics/appwidgets/index.html
         * The App Widget host calls the configuration Activity and the configuration
         * Activity should always return a result. The result should include the App Widget ID
         * passed by the Intent that launched the Activity (saved in the Intent extras as EXTRA_APPWIDGET_ID).
         */
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
        if (extras != null) appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        Intent resultValue = new Intent();
        if (appWidgetId > -1) resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        setResult(RESULT_OK, resultValue);

        // Don't show Android Wear stuff for old devices that don't support it
        if (Integer.valueOf(Build.VERSION.SDK) < Build.VERSION_CODES.JELLY_BEAN_MR2) {
            getPreferenceScreen().removePreference(findPreference(FRCPreferences.PREF_ANDROID_WEAR));
        }
        mWearPreferenceListener = new FRCWearPreferenceListener(getApplicationContext());
    }

    private void updatePreferenceSummary(String key, int summaryResId) {
        @SuppressWarnings("deprecation") ListPreference pref = (ListPreference) getPreferenceManager().findPreference(key);
        String summary = getString(summaryResId, pref.getEntry());
        pref.setSummary(summary);
    }

    @Override
    protected void onStart() {
        super.onStart();
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(mOnSharedPreferenceChangeListener);
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(mWearPreferenceListener);

    }

    @Override
    protected void onStop() {
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(mOnSharedPreferenceChangeListener);
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(mWearPreferenceListener);

        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.v(TAG, "onDestroy");
        super.onDestroy();
        // When we leave the preference screen, reupdate all our widgets
        FRCWidgetScheduler.getInstance(this).schedule();
    }

    private final OnSharedPreferenceChangeListener mOnSharedPreferenceChangeListener = new OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (FRCPreferences.PREF_METHOD.equals(key)) {
                updatePreferenceSummary(key, R.string.setting_method_summary);
            } else if (FRCPreferences.PREF_DETAILED_VIEW.equals(key)) {
                updatePreferenceSummary(key, R.string.setting_detailed_view_summary);
            } else if (FRCPreferences.PREF_LANGUAGE.equals(key)) {
                updatePreferenceSummary(key, R.string.setting_language_summary);
            }
        }
    };

}
