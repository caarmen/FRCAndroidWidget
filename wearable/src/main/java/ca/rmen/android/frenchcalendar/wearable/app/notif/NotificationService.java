/*
 * French Revolutionary Calendar Android Widget
 * Copyright (C) 2014 Benoit 'BoD' Lubek (BoD@JRAF.org)
 * Copyright (C) 2011 - 2014 Carmen Alvarez
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
package ca.rmen.android.frenchcalendar.wearable.app.notif;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.TextAppearanceSpan;
import android.util.Log;

import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.WearableListenerService;

import ca.rmen.android.frenchcalendar.R;
import ca.rmen.android.frenchcalendar.common.WearCommConstants;

public class NotificationService extends WearableListenerService {
    private static final String TAG = NotificationService.class.getSimpleName();

    private static final int NOTIFICATION_ID = 0;

    private String mDate;
    private String mDayOfYear;
    private int mColor;

    public NotificationService() {}

    @Override
    public void onPeerConnected(Node peer) {}

    @Override
    public void onPeerDisconnected(Node peer) {}

    @Override
    public void onMessageReceived(MessageEvent messageEvent) { }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        Log.d(TAG, "count=" + dataEvents.getCount());

        for (DataEvent dataEvent : dataEvents) {
            if (dataEvent.getType() != DataEvent.TYPE_CHANGED) continue;

            DataItem dataItem = dataEvent.getDataItem();
            Uri uri = dataItem.getUri();
            Log.d(TAG, "uri=" + uri);
            String path = uri.getPath();
            Log.d(TAG, "path=" + path);
            DataMapItem dataMapItem = DataMapItem.fromDataItem(dataItem);
            DataMap dataMap = dataMapItem.getDataMap();
            mDate = dataMap.getString(WearCommConstants.EXTRA_DATE);
            mDayOfYear = dataMap.getString(WearCommConstants.EXTRA_DAY_OF_YEAR);
            mColor = dataMap.getInt(WearCommConstants.EXTRA_COLOR);
            showNotification();
        }
    }

    private void showNotification() {
        Log.d(TAG, "showNotification");
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = createNotification();
        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    private Notification createNotification() {
        Notification.Builder mainNotifBuilder = new Notification.Builder(this);

        // A small icon is mandatory even if it will be hidden - without this the system refuses to show the notification...
        mainNotifBuilder.setSmallIcon(R.drawable.icon);

        // Title
        String title = mDayOfYear;
        SpannableString spannableTitle = new SpannableString(title);
        Object span = new TextAppearanceSpan(this, R.style.NotificationContentTitleTextAppearance);
        spannableTitle.setSpan(span, 0, title.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mainNotifBuilder.setContentTitle(spannableTitle);

        // Text
        String text = mDate;
        SpannableString spannableText = new SpannableString(text);
        span = new TextAppearanceSpan(this, R.style.NotificationContentTextTextAppearance);
        spannableText.setSpan(span, 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mainNotifBuilder.setContentText(spannableText);

        // Low priority (let's face it)
        mainNotifBuilder.setPriority(Notification.PRIORITY_MIN);

        // Wear specifics
        Notification.WearableExtender wearableExtender = new Notification.WearableExtender();
        wearableExtender.setHintHideIcon(true);
        // Set the background color depending on month
        wearableExtender.setBackground(createBitmapForMonth(mColor));

        Notification.Builder wearableNotifBuilder = wearableExtender.extend(mainNotifBuilder);
        return wearableNotifBuilder.build();
    }

    private Bitmap createBitmapForMonth(int color) {
        int[] colors = {color};
        return Bitmap.createBitmap(colors, 1, 1, Bitmap.Config.ARGB_8888);
    }
}
