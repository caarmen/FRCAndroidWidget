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

package ca.rmen.android.frccommon.compat

import android.annotation.TargetApi
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build

/**
 * APIs available on API level 19 and above.
 */
@TargetApi(Build.VERSION_CODES.KITKAT)
object Api19Helper {
    fun scheduleExact(context: Context, nextAlarmTime: Long, pendingIntent: PendingIntent) {
        val mgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        mgr.setExact(AlarmManager.RTC, nextAlarmTime, pendingIntent)
    }
}