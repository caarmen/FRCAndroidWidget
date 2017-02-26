/*
 * French Revolutionary Calendar Android Widget
 * Copyright (C) 2014 Benoit 'BoD' Lubek (BoD@JRAF.org)
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
package ca.rmen.android.frccommon;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.text.format.DateUtils;

import ca.rmen.android.frccommon.prefs.FRCPreferences;

public class FRCNotificationScheduler {

    public static void scheduleRepeatingAlarm(Context context) {
        FRCPreferences prefs = FRCPreferences.getInstance(context);
        if (prefs.getSystemNotificationEnabled()) {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            PendingIntent pendingIntent = getPendingIntent(context);
            alarmManager.setInexactRepeating(AlarmManager.RTC, getTomorrowAtEight(), DateUtils.DAY_IN_MILLIS, pendingIntent);
        }
    }

    public static void unscheduleRepeatingAlarm(Context context) {
        FRCPreferences prefs = FRCPreferences.getInstance(context);
        if (!prefs.getSystemNotificationEnabled()) {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            PendingIntent pendingIntent = getPendingIntent(context);
            alarmManager.cancel(pendingIntent);
        }
    }

    private static long getTomorrowAtEight() {
        Calendar calendar = Calendar.getInstance();
        // At eight
        stripTime(calendar);
        calendar.set(Calendar.HOUR_OF_DAY, 8);
        // Tomorrow
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        return calendar.getTime().getTime();
    }

    private static void stripTime(Calendar cal) {
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
    }

    private static PendingIntent getPendingIntent(Context context) {
        Intent intent = new Intent(FRCNotificationReceiver.ACTION);
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

}
