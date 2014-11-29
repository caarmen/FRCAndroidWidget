/*
 * French Revolutionary Calendar Android Widget
 * Copyright (C) 2014 Benoit 'BoD' Lubek (BoD@JRAF.org)
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
package ca.rmen.android.frcwear;

import android.content.Context;
import android.content.SharedPreferences;

import ca.rmen.android.frccommon.prefs.FRCPreferences;

public class FRCWearPreferenceListener implements SharedPreferences.OnSharedPreferenceChangeListener {

    private final Context mContext;

    public FRCWearPreferenceListener(Context context) {
        mContext = context;
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (FRCPreferences.PREF_METHOD.equals(key)
                || FRCPreferences.PREF_LANGUAGE.equals(key)
                || FRCPreferences.PREF_CUSTOM_COLOR_ENABLED.equals(key)
                || FRCPreferences.PREF_CUSTOM_COLOR.equals(key)) {
            // Update the Android Wear notification (if enabled)
            updateWearNotificationIfEnabled(sharedPreferences);
        } else if (FRCPreferences.PREF_ANDROID_WEAR.equals(key)) {
            boolean androidWearEnabled = sharedPreferences.getBoolean(FRCPreferences.PREF_ANDROID_WEAR, false);
            if (androidWearEnabled) {
                // Schedule an alarm
                FRCWearScheduler.scheduleRepeatingAlarm(mContext);

                // Also send the value now
                FRCAndroidWearService.backgroundRemoveAndUpdateDays(mContext);

                // Also send the value in a minute (this allows the Wearable app to finish installing)
                FRCWearScheduler.scheduleOnceAlarm(mContext);
            } else {
                // Unschedule the alarm
                FRCWearScheduler.unscheduleRepeatingAlarm(mContext);
            }
        }
    }

    private void updateWearNotificationIfEnabled(SharedPreferences sharedPreferences) {
        boolean androidWearEnabled = sharedPreferences.getBoolean(FRCPreferences.PREF_ANDROID_WEAR, false);
        if (androidWearEnabled) {
            // Update the value now
            FRCAndroidWearService.backgroundRemoveAndUpdateDays(mContext);
        }
    }
}
