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

package ca.rmen.android.frccommon.compat;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.drawable.Icon;

@TargetApi(23)
class Api23Helper {
    private Api23Helper() {
        // prevent instantiation
    }

    static Notification createNotification(
            Context context,
            int iconId,
            String tickerText,
            String contentText,
            String bigText,
            PendingIntent defaultIntent,
            int actionIconId,
            CharSequence actionText,
            PendingIntent actionIntent) {
        Notification.Builder builder = new Notification.Builder(context)
                .setAutoCancel(true)
                .setLocalOnly(true)
                .setContentTitle(tickerText)
                .setContentText(contentText)
                .setStyle(new Notification.BigTextStyle().bigText(bigText))
                .setSmallIcon(iconId)
                .setContentIntent(defaultIntent);

        if (actionIconId > 0) {
            Icon icon = Icon.createWithResource(context, actionIconId);
            Notification.Action action = new Notification.Action.Builder(icon, actionText, actionIntent).build();
            builder.addAction(action);
        }
        return builder.build();
    }

}
