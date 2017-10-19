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

package ca.rmen.android.frccommon

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.text.format.DateUtils
import ca.rmen.android.frccommon.prefs.FRCPreferences
import java.util.Calendar

object FRCNotificationScheduler {
    fun scheduleRepeatingAlarm(context: Context) {
        val prefs = FRCPreferences.getInstance(context)
        if (prefs.systemNotificationEnabled) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val pendingIntent = getPendingIntent(context)
            alarmManager.setInexactRepeating(AlarmManager.RTC, getTomorrowAtEight(), DateUtils.DAY_IN_MILLIS, pendingIntent)
        }
    }

    fun unscheduleRepeatingAlarm(context: Context) {
        val prefs = FRCPreferences.getInstance(context)
        if (!prefs.systemNotificationEnabled) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val pendingIntent = getPendingIntent(context)
            alarmManager.cancel(pendingIntent)
        }
    }

    private fun getTomorrowAtEight(): Long {
        val calendar = Calendar.getInstance()
        // At eight
        stripTime(calendar)
        calendar[Calendar.HOUR_OF_DAY] = 8
        // Tomorrow
        calendar.add(Calendar.DAY_OF_MONTH, 1)
        return calendar.time.time
    }

    private fun stripTime(cal: Calendar) {
        cal[Calendar.HOUR_OF_DAY] = 0
        cal[Calendar.MINUTE] = 0
        cal[Calendar.SECOND] = 0
        cal[Calendar.MILLISECOND] = 0
    }

    private fun getPendingIntent(context: Context): PendingIntent {
        val intent = Intent(FRCNotificationReceiver.ACTION)
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }
}
