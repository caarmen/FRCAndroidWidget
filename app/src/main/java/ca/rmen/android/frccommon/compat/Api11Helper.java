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

package ca.rmen.android.frccommon.compat;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;

@TargetApi(11)
public class Api11Helper {
    private Api11Helper() {
        // prevent instantiation
    }

    public static void setDisplayHomeAsUpEnabled(Activity activity) {
        if (!activity.isTaskRoot()) {
            ActionBar actionBar = activity.getActionBar();
            if (actionBar != null) actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    static Notification createNotification(Context context, int iconId, String tickerText, String contentText, PendingIntent pendingIntent) {
        //noinspection deprecation
        return new Notification.Builder(context)
                .setAutoCancel(true)
                .setContentTitle(tickerText)
                .setContentText(contentText)
                .setSmallIcon(iconId)
                .setContentIntent(pendingIntent)
                .getNotification();
    }

}
