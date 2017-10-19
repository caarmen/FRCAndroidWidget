/*
 * French Revolutionary Calendar Android Widget
 * Copyright (C) 2017 Carmen Alvarez
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

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Icon
import android.support.annotation.ColorInt
import ca.rmen.android.frccommon.Action
import ca.rmen.android.frenchcalendar.R

object Api26Helper {
    private const val NOTIFICATION_CHANNEL_ID = "FRC_NOTIFICATION_CHANNEL"

    fun createNotification(context: Context,
                           iconId: Int,
                           @ColorInt color: Int,
                           tickerText: String,
                           contentText: String,
                           bigText: String,
                           defaultIntent: PendingIntent,
                           actions: Array<Action>): Notification {
        val notificationChannel = NotificationChannel(NOTIFICATION_CHANNEL_ID, context.getString(R.string.app_name), NotificationManager.IMPORTANCE_DEFAULT)
        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(notificationChannel)
        val builder = Notification.Builder(context, notificationChannel.id)
                .setAutoCancel(true)
                .setColor(color)
                .setContentTitle(tickerText)
                .setContentText(contentText)
                .setStyle(Notification.BigTextStyle().bigText(bigText))
                .setSmallIcon(iconId)
                .setContentIntent(defaultIntent)
        actions.forEach { action ->
            val icon = Icon.createWithResource(context, action.iconId)
            builder.addAction(Notification.Action.Builder(icon, action.title, action.pendingIntent).build())
        }
        val extender = Notification.WearableExtender()
                .setBackground(Bitmap.createBitmap(intArrayOf(color), 1, 1, Bitmap.Config.ARGB_8888))
        builder.extend(extender)
        return builder.build()
    }
}
