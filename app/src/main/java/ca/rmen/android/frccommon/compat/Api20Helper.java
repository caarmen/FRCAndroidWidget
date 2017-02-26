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

package ca.rmen.android.frccommon.compat;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.ColorInt;

import ca.rmen.android.frccommon.Action;

@TargetApi(20)
class Api20Helper {
    private Api20Helper() {
        // prevent instantiation
    }

    static Notification createNotification(
            Context context,
            int iconId,
            @ColorInt int color,
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
            //noinspection deprecation
            builder.addAction(action.iconId, action.title, action.pendingIntent);
        }
        Notification.WearableExtender extender = new Notification.WearableExtender()
                .setBackground(Bitmap.createBitmap(new int[]{color}, 1, 1, Bitmap.Config.ARGB_8888));
        builder.extend(extender);
        return builder.build();
    }

}
