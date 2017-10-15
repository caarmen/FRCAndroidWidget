/*
 * French Revolutionary Calendar Android Widget
 * Copyright (C) 2016 - 2017 Carmen Alvarez
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

import android.app.NotificationManager
import android.content.Context
import android.os.AsyncTask
import ca.rmen.android.frccommon.compat.ApiHelper
import ca.rmen.android.frccommon.compat.NotificationCompat
import ca.rmen.android.frccommon.prefs.FRCPreferences
import ca.rmen.android.frenchcalendar.R
import ca.rmen.lfrc.FrenchRevolutionaryCalendarDate

object FRCSystemNotification {
    private val NOTIFICATION_ID = R.drawable.ic_notif

    fun showNotification(context: Context) {
        ShowNotificationTask(context).execute()
    }

    fun hideNotification(context: Context) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(NOTIFICATION_ID)
    }

    private class ShowNotificationTask constructor(context: Context) : AsyncTask<Void, Void, FrenchRevolutionaryCalendarDate>() {
        private val mContext: Context = context

        override fun doInBackground(vararg params: Void?): FrenchRevolutionaryCalendarDate = FRCDateUtils.getToday(mContext)

        override fun onPostExecute(date: FrenchRevolutionaryCalendarDate) {
            val objectType = mContext.resources.getStringArray(R.array.daily_object_types)[date.objectType.ordinal]
            val priority = FRCPreferences.getInstance(mContext).systemNotificationPriority
            val notificationText = mContext.getString(R.string.notification_text,
                    date.weekdayName,
                    date.dayOfMonth,
                    date.monthName,
                    FRCDateUtils.formatNumber(mContext, date.year))
            val notificationLongText = mContext.getString(R.string.notification_long_text,
                    date.weekdayName,
                    date.dayOfMonth,
                    date.monthName,
                    FRCDateUtils.formatNumber(mContext, date.year),
                    objectType, date.objectOfTheDay)
            val converterAction = if (ApiHelper.getAPILevel() >= Constants.MIN_API_LEVEL_TWO_WAY_CONVERTER)
                Action.getConverterAction(mContext) else Action.getLegacyConverterAction(mContext)
            val shareAction = Action.getLightShareAction(mContext, date)
            val searchAction = Action.getLightSearchAction(mContext, date)
            val notification = NotificationCompat.createNotification(
                    mContext,
                    priority,
                    FRCDateUtils.getColor(mContext, date),
                    mContext.getString(R.string.app_full_name),
                    notificationText, notificationLongText,
                    converterAction.pendingIntent,
                    shareAction, searchAction)
            val notificationManager = mContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(NOTIFICATION_ID, notification)
        }
    }
}