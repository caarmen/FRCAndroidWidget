/*
 * French Revolutionary Calendar Android Widget
 * Copyright (C) 2014 Benoit 'BoD' Lubek (BoD@JRAF.org)
 * Copyright (C) 2011 - 2014 Carmen Alvarez
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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import ca.rmen.android.frccommon.Constants;
import ca.rmen.android.frccommon.prefs.FRCPreferences;

public class FRCBootCompletedBroadcastReceiver extends BroadcastReceiver {
    private final String TAG = Constants.TAG + FRCBootCompletedBroadcastReceiver.class.getSimpleName();

    @Override
    public void onReceive(final Context context, Intent intent) {
        Log.v(TAG, "onReceive: action = " + intent.getAction() + ": component = " + (intent.getComponent() == null ? "" : intent.getComponent().getClassName()));

        if (FRCPreferences.getInstance(context).getAndroidWearEnabled()) {
            // Schedule an alarm
            FRCWearScheduler.scheduleRepeatingAlarm(context);

            // Also send the value now
            FRCAndroidWearService.backgroundRemoveAndUpdateDays(context);

            // Also send the value in a minute (this allows the phone to finish booting and the Wear connexion to be up)
            FRCWearScheduler.scheduleOnceAlarm(context);
        }
    }
}
