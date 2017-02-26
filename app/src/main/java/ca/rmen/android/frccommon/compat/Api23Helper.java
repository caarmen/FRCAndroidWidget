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
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;

import ca.rmen.android.frccommon.Action;

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
            Action... actions) {
        Notification.Builder builder = new Notification.Builder(context)
                .setAutoCancel(true)
                .setContentTitle(tickerText)
                .setContentText(contentText)
                .setStyle(new Notification.BigTextStyle().bigText(bigText))
                .setSmallIcon(iconId)
                .setContentIntent(defaultIntent);

        for (Action action : actions) {
            Icon icon = Icon.createWithResource(context, action.iconId);
            builder.addAction(new Notification.Action.Builder(icon, action.title, action.pendingIntent).build());
        }
        return builder.build();
    }

    @ColorInt
    static int getColor(Context context, @ColorRes int colorRes) {
        return context.getColor(colorRes);
    }

}
