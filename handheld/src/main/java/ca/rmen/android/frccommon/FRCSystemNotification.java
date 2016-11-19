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

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import ca.rmen.android.frccommon.compat.NotificationCompat;
import ca.rmen.android.frenchcalendar.R;
import ca.rmen.lfrc.FrenchRevolutionaryCalendarDate;

public class FRCSystemNotification {
    private static final int NOTIFICATION_ID = R.drawable.ic_notif;

    public static void showNotification(Context context) {
        new ShowNotificationTask(context).execute();
    }

    private static final class ShowNotificationTask extends AsyncTask<Void, Void, FrenchRevolutionaryCalendarDate> {

        private final Context mContext;

        ShowNotificationTask(Context context) {
            mContext = context;
        }

        @Override
        protected FrenchRevolutionaryCalendarDate doInBackground(Void[] params) {
            return FRCDateUtils.getToday(mContext);
        }

        @Override
        protected void onPostExecute(FrenchRevolutionaryCalendarDate date) {
            String objectType = mContext.getResources().getStringArray(R.array.daily_object_types)[date.getObjectType().ordinal()];
            String notificationText = mContext.getString(R.string.notification_text,
                    date.getWeekdayName(),
                    date.dayOfMonth,
                    date.getMonthName(),
                    date.year);
            String notificationLongText = mContext.getString(R.string.notification_long_text,
                    date.getWeekdayName(),
                    date.dayOfMonth,
                    date.getMonthName(),
                    date.year,
                    objectType, date.getDayOfYear());
            PendingIntent defaultIntent = PendingIntent.getActivity(mContext, 1, new Intent(mContext, FRCConverterActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
            PendingIntent shareIntent = PendingIntent.getActivity(mContext, 2, Share.getShareIntent(mContext, date), PendingIntent.FLAG_UPDATE_CURRENT);
            Notification notification = NotificationCompat.createNotification(
                    mContext,
                    R.drawable.ic_notif,
                    mContext.getString(R.string.app_full_name),
                    notificationText, notificationLongText, defaultIntent,
                    R.drawable.ic_action_share, mContext.getString(R.string.popup_action_share), shareIntent);
            NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(NOTIFICATION_ID, notification);
        }
    }
}
