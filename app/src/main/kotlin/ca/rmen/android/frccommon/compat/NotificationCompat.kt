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

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.support.annotation.ColorInt
import android.support.annotation.DrawableRes
import android.util.Log
import ca.rmen.android.frccommon.Action
import ca.rmen.android.frccommon.Constants
import ca.rmen.android.frenchcalendar.R
import java.lang.reflect.InvocationTargetException

object NotificationCompat {
    private val TAG = Constants.TAG + NotificationCompat::class.java.simpleName

    fun getNotificationPriority(priority: String): Int =
            if (ApiHelper.apiLevel < 16) {
                0
            } else {
                Api16Helper.getNotificationPriority(priority)
            }

    fun createNotification(context: Context,
                           priority: Int,
                           @ColorInt color: Int,
                           tickerText: String,
                           contentText: String,
                           bigText: String,
                           defaultIntent: PendingIntent,
                           vararg actions: Action): Notification {
        @DrawableRes val iconId: Int = R.drawable.ic_notif
        when {
            ApiHelper.apiLevel < 11 -> {
                val notification = Notification()
                notification.tickerText = tickerText
                notification.`when` = System.currentTimeMillis()
                @Suppress("DEPRECATION")
                notification.icon = iconId
                notification.contentIntent = defaultIntent
                notification.flags = notification.flags or Notification.FLAG_AUTO_CANCEL
                // Google removed setLatestEventInfo in sdk 23.
                try {
                    val method = Notification::class.java.getMethod("setLatestEventInfo", Context::class.java, CharSequence::class.java, CharSequence::class.java, PendingIntent::class.java)
                    method.invoke(notification, context, tickerText, contentText, defaultIntent)
                } catch (e: NoSuchMethodException) {
                    Log.v(TAG, "Error creating notification", e)
                } catch (e: IllegalAccessException) {
                    Log.v(TAG, "Error creating notification", e)
                } catch (e: InvocationTargetException) {
                    Log.v(TAG, "Error creating notification", e)
                }
                return notification
            }
            ApiHelper.apiLevel < 16 -> return Api11Helper.createNotification(context, iconId, tickerText, contentText, defaultIntent)
        // convoluted way to pass actions. We have to convert from vararg to array, because the spread operator (*) crashes on cupcake.
        // Arrays.copyOf() is used by the spread operator, and this doesn't exist on cupcake.
            ApiHelper.apiLevel < 20 -> return Api16Helper.createNotification(context, priority, iconId, tickerText, contentText, bigText, defaultIntent, Array(actions.size) { actions[it] })
            ApiHelper.apiLevel < 23 -> return Api20Helper.createNotification(context, priority, iconId, color, tickerText, contentText, bigText, defaultIntent, Array(actions.size) { actions[it] })
            ApiHelper.apiLevel < 26 -> return Api23Helper.createNotification(context, priority, iconId, color, tickerText, contentText, bigText, defaultIntent, Array(actions.size) { actions[it] })
            else -> return Api26Helper.createNotification(context, iconId, color, tickerText, contentText, bigText, defaultIntent, Array(actions.size) { actions[it] })
        }
    }
}
