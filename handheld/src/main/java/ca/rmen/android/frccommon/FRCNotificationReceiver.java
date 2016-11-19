/*
 * French Revolutionary Calendar Android Widget
 * Copyright (C) 2016 Carmen Alvarez
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

package ca.rmen.android.frccommon;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import ca.rmen.android.frccommon.compat.Api4Helper;
import ca.rmen.android.frccommon.compat.ApiHelper;
import ca.rmen.android.frccommon.prefs.FRCPreferences;

public class FRCNotificationReceiver extends BroadcastReceiver {
    public static final String ACTION = "ca.rmen.android.frenchcalendar.NOTIFICATION";

    @Override
    public void onReceive(Context context, Intent intent) {
        FRCPreferences prefs = FRCPreferences.getInstance(context);
        if (prefs.getAndroidWearEnabled()) {
            if (ApiHelper.getAPILevel() >= Build.VERSION_CODES.DONUT) {
                Intent wearIntent = Api4Helper.createServiceIntent(context, FRCNotificationScheduler.ACTION_WEAR_UPDATE);
                context.startService(wearIntent);
            }
        }
        if (prefs.getSystemNotificationEnabled()) {
            FRCSystemNotification.showNotification(context);
        }
    }
}
