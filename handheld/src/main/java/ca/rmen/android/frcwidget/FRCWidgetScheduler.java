/*
 * French Revolutionary Calendar Android Widget
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
package ca.rmen.android.frcwidget;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetProvider;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import ca.rmen.android.frccommon.Constants;
import ca.rmen.android.frccommon.prefs.FRCPreferences;

/**
 * 
 * Periodically forces an update of all the widgets, depending on if they are set
 * to be updated once a minute, or once a day.
 * 
 * The update is done by sending a broadcast which will be received by the {@link AppWidgetProvider}s.
 * 
 * @author calvarez
 * 
 */
public class FRCWidgetScheduler {
    private static final String TAG = Constants.TAG + FRCWidgetScheduler.class.getSimpleName();
    static final String ACTION_WIDGET_UPDATE = "ca.rmen.android.frcwidget.UPDATE_WIDGET";

    private static FRCWidgetScheduler INSTANCE;
    private final Context context;
    private final PendingIntent updateWidgetPendingIntent;

    private FRCWidgetScheduler(Context context) {
        this.context = context.getApplicationContext();
        Intent updateWidgetIntent = new Intent(ACTION_WIDGET_UPDATE);
        updateWidgetPendingIntent = PendingIntent.getBroadcast(context, 0, updateWidgetIntent, 0);
        IntentFilter filterOn = new IntentFilter(Intent.ACTION_SCREEN_ON);
        IntentFilter filterOff = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        ScreenBroadcastReceiver screenBroadcastReceiver = new ScreenBroadcastReceiver();
        context.getApplicationContext().registerReceiver(screenBroadcastReceiver, filterOn);
        context.getApplicationContext().registerReceiver(screenBroadcastReceiver, filterOff);
    }

    public synchronized static FRCWidgetScheduler getInstance(Context context) {
        if (INSTANCE == null) INSTANCE = new FRCWidgetScheduler(context);
        return INSTANCE;
    }

    /**
     * Cancel any scheduled update alarms, reschedule an update alarm, and force an update now.
     */
    public void schedule() {
        Log.v(TAG, "schedule");

        int frequency = FRCPreferences.getInstance(context).getUpdateFrequency();
        Log.v(TAG, "Start alarm with frequency " + frequency);
        // If we show the time, we will update the widget every decimal "minute" (86.4 Gregorian seconds) starting 
        // one decimal "minute" from now.
        long nextAlarmTime = System.currentTimeMillis() + frequency;
        AlarmManager mgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        // If we only show the date, we will update the widget every day just before midnight
        if (frequency == FRCPreferences.FREQUENCY_DAYS) {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.add(Calendar.MINUTE, 1);
            nextAlarmTime = cal.getTimeInMillis();
        }

        // Schedule the periodic updates.
        mgr.setRepeating(AlarmManager.RTC, nextAlarmTime, frequency, updateWidgetPendingIntent);

        // Also send a broadcast to force an update now.
        Intent updateIntent = new Intent(ACTION_WIDGET_UPDATE);
        context.sendBroadcast(updateIntent);

        Log.v(TAG, "Started updater");
    }

    /**
     * Cancel any scheduled update alarms.
     */
    void cancel() {
        Log.v(TAG, "cancel");
        AlarmManager mgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        mgr.cancel(updateWidgetPendingIntent);
    }

    /**
     * We only want to be updating the widgets when the screen is on.
     */
    private class ScreenBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.v(TAG, "onReceive: intent = " + intent);

            if (Intent.ACTION_SCREEN_OFF.equals(intent.getAction())) {
                cancel();
            } else if (Intent.ACTION_SCREEN_ON.equals(intent.getAction())) {
                schedule();
            }
        }
    }

}
