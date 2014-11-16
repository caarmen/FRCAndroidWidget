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
package ca.rmen.android.frcwidget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import ca.rmen.android.frcwidget.prefs.FRCPreferences;
import ca.rmen.android.frcwidget.wear.AndroidWearService;
import ca.rmen.android.frcwidget.wear.FRCWearScheduler;

public class FRCBootCompletedBroadcastReceiver extends BroadcastReceiver {
    private final String TAG = Constants.TAG + getClass().getSimpleName();

    @Override
    public void onReceive(final Context context, Intent intent) {
        Log.v(TAG, "onReceive: action = " + intent.getAction() + ": component = " + (intent.getComponent() == null ? "" : intent.getComponent().getClassName()));

        SharedPreferences preferenceManager = PreferenceManager.getDefaultSharedPreferences(context);
        if (FRCPreferences.getInstance(context).getAndroidWearEnabled()) {
            // Schedule an alarm
            FRCWearScheduler.scheduleRepeatingAlarm(context);

            // Also send the value now
            AndroidWearService.backgroundRemoveAndUpdateDays(context);

            // Also send the value in a minute (this allows the phone to finish booting and the Wear connexion to be up)
            FRCWearScheduler.scheduleOnceAlarm(context);
        }
    }
}
