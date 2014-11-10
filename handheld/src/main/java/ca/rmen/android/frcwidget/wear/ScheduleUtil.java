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
package ca.rmen.android.frcwidget.wear;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;

public class ScheduleUtil {
    private static final long ONE_DAY = 24 * 60 * 60 * 1000;

    public static void scheduleRepeatingAlarm(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = AndroidWearService.getPendingIntent(context, AndroidWearService.ACTION_UPDATE);
        alarmManager.setInexactRepeating(AlarmManager.RTC, getTomorrowAtEight(), ONE_DAY, pendingIntent);
    }

    public static void unscheduleRepeatingAlarm(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = AndroidWearService.getPendingIntent(context, AndroidWearService.ACTION_UPDATE);
        alarmManager.cancel(pendingIntent);
    }

    public static void scheduleOnceAlarm(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = AndroidWearService.getPendingIntent(context, AndroidWearService.ACTION_REMOVE_AND_UPDATE);
        alarmManager.set(AlarmManager.RTC, getInXSeconds(15), pendingIntent);
    }

    public static long getTomorrowAtEight() {
        Calendar calendar = Calendar.getInstance();
        // At eight
        stripTime(calendar);
        calendar.set(Calendar.HOUR_OF_DAY, 8);
        // Tomorrow
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        return calendar.getTime().getTime();
    }

    public static long getInXSeconds(int seconds) {
        return System.currentTimeMillis() + seconds * 1000;
    }


    public static void stripTime(Calendar cal) {
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
    }
}
