/*
 * French Revolutionary Calendar Android Widget
 * Copyright (C) 2016-2017 Carmen Alvarez
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
import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import ca.rmen.android.frccommon.Action

@TargetApi(16)
object Api16Helper {
    private val PREF_SETTING_SYSTEM_NOTIFICATION_PRIORITY_MAX = "max"
    private val PREF_SETTING_SYSTEM_NOTIFICATION_PRIORITY_HIGH = "high"
    private val PREF_SETTING_SYSTEM_NOTIFICATION_PRIORITY_LOW = "low"
    private val PREF_SETTING_SYSTEM_NOTIFICATION_PRIORITY_MIN = "min"

    @Suppress("DEPRECATION")
    fun getNotificationPriority(priorityPref: String): Int {
        if (priorityPref == PREF_SETTING_SYSTEM_NOTIFICATION_PRIORITY_MAX) return Notification.PRIORITY_MAX
        if (priorityPref == PREF_SETTING_SYSTEM_NOTIFICATION_PRIORITY_HIGH) return Notification.PRIORITY_HIGH
        if (priorityPref == PREF_SETTING_SYSTEM_NOTIFICATION_PRIORITY_LOW) return Notification.PRIORITY_LOW
        if (priorityPref == PREF_SETTING_SYSTEM_NOTIFICATION_PRIORITY_MIN) return Notification.PRIORITY_MIN
        return Notification.PRIORITY_DEFAULT
    }

    fun createNotification(context: Context,
                           priority: Int,
                           iconId: Int,
                           tickerText: String,
                           contextText: String,
                           bigText: String,
                           defaultIntent: PendingIntent,
                           actions: Array<Action>): Notification {
        @Suppress("DEPRECATION")
        val builder = Notification.Builder(context)
                .setPriority(priority)
                .setAutoCancel(true)
                .setContentTitle(tickerText)
                .setContentText(contextText)
                .setStyle(Notification.BigTextStyle().bigText(bigText))
                .setSmallIcon(iconId)
                .setContentIntent(defaultIntent)
        actions.forEach { action ->
            @Suppress("DEPRECATION")
            builder.addAction(action.iconId, action.title, action.pendingIntent)
        }
        return builder.build()
    }
}
