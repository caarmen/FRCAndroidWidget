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

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import ca.rmen.android.frccommon.Action;
import ca.rmen.android.frccommon.Constants;


public final class NotificationCompat {

    private static final String TAG = Constants.TAG + NotificationCompat.class.getSimpleName();

    private NotificationCompat() {
        // prevent instantiation
    }

    public static Notification createNotification(
            Context context,
            int iconId,
            String tickerText,
            String contentText,
            String bigText,
            PendingIntent defaultIntent,
            Action... actions) {
        if (ApiHelper.getAPILevel() < 11) {
            Notification notification = new Notification();
            notification.tickerText = tickerText;
            notification.when = System.currentTimeMillis();
            //noinspection deprecation
            notification.icon = iconId;
            notification.contentIntent = defaultIntent;
            notification.flags = notification.flags | Notification.FLAG_AUTO_CANCEL;
            // Google removed setLatestEventInfo in sdk 23.
            //noinspection TryWithIdenticalCatches
            try {
                Method method = Notification.class.getMethod("setLatestEventInfo", Context.class, CharSequence.class, CharSequence.class, PendingIntent.class);
                method.invoke(notification, context, tickerText, contentText, defaultIntent);
            } catch (NoSuchMethodException e) {
                Log.v(TAG, "Error creating notification", e);
            } catch (IllegalAccessException e) {
                Log.v(TAG, "Error creating notification", e);
            } catch (InvocationTargetException e) {
                Log.v(TAG, "Error creating notification", e);
            }
            return notification;
        } else if (ApiHelper.getAPILevel() < 16) {
            return Api11Helper.createNotification(context, iconId, tickerText, contentText, defaultIntent);
        } else if (ApiHelper.getAPILevel() < 20) {
            return Api16Helper.createNotification(context, iconId, tickerText, contentText, bigText, defaultIntent, actions);
        } else if (ApiHelper.getAPILevel() < 23) {
            return Api20Helper.createNotification(context, iconId, tickerText, contentText, bigText, defaultIntent, actions);
        } else {
            return Api23Helper.createNotification(context, iconId, tickerText, contentText, bigText, defaultIntent, actions);
        }
    }

}
