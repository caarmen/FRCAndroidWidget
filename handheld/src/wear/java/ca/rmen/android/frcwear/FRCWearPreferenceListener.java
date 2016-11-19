/*
 * French Revolutionary Calendar Android Widget
 * Copyright (C) 2014 Benoit 'BoD' Lubek (BoD@JRAF.org)
 * Copyright (C) 2011 - 2016 Carmen Alvarez
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
package ca.rmen.android.frcwear;

import android.content.Context;
import android.content.SharedPreferences;

import ca.rmen.android.frccommon.FRCNotificationScheduler;
import ca.rmen.android.frccommon.prefs.FRCPreferences;

public class FRCWearPreferenceListener implements SharedPreferences.OnSharedPreferenceChangeListener {

    private final Context mContext;

    public FRCWearPreferenceListener(Context context) {
        mContext = context;
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        FRCPreferences prefs = FRCPreferences.getInstance(mContext);
        if (prefs.getAndroidWearEnabled() &&
                (FRCPreferences.PREF_METHOD.equals(key)
                || FRCPreferences.PREF_LANGUAGE.equals(key)
                || FRCPreferences.PREF_CUSTOM_COLOR_ENABLED.equals(key)
                || FRCPreferences.PREF_CUSTOM_COLOR.equals(key))) {
            // Update the Android Wear notification (if enabled)
            FRCAndroidWearService.backgroundUpdateToday(mContext);
        }
        if (FRCPreferences.PREF_ANDROID_WEAR.equals(key)) {
            if (prefs.getAndroidWearEnabled()) {
                // Schedule an alarm
                FRCNotificationScheduler.scheduleRepeatingAlarm(mContext);

                // Also send the value now
                FRCAndroidWearService.backgroundUpdateToday(mContext);

                // Also send the value in a minute (this allows the Wearable app to finish installing)
                FRCNotificationScheduler.scheduleOneShotWearableAlarm(mContext);
            } else {
                // Unschedule the alarm
                FRCNotificationScheduler.unscheduleRepeatingAlarm(mContext);
            }
        }
    }

}
