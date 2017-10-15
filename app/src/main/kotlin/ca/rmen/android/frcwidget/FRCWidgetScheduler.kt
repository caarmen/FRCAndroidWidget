/*
 * French Revolutionary Calendar Android Widget
 * Copyright (C) 2011 - 2017 Carmen Alvarez
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

package ca.rmen.android.frcwidget

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.util.Log
import ca.rmen.android.frccommon.Constants
import ca.rmen.android.frccommon.compat.Api19Helper
import ca.rmen.android.frccommon.compat.ApiHelper
import ca.rmen.android.frccommon.compat.IntentCompat
import ca.rmen.android.frccommon.prefs.FRCPreferences
import java.util.Calendar

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
class FRCWidgetScheduler private constructor(context: Context) {
    companion object {
        private val TAG = Constants.TAG + FRCWidgetScheduler::class.java.simpleName
        const val ACTION_WIDGET_UPDATE = "ca.rmen.android.frcwidget.UPDATE_WIDGET"

        @Volatile
        private var sInstance: FRCWidgetScheduler? = null

        private lateinit var mUpdateWidgetPendingIntent: PendingIntent
        private lateinit var mUpdateWidgetTomorrowPendingIntent: PendingIntent

        fun getInstance(context: Context) = sInstance ?: synchronized(this) {
            sInstance ?: FRCWidgetScheduler(context).also { sInstance = it }
        }
    }

    init {
        val updateWidgetIntent = Intent(ACTION_WIDGET_UPDATE)
        IntentCompat.setPackage(updateWidgetIntent, context.packageName)
        mUpdateWidgetPendingIntent = PendingIntent.getBroadcast(context.applicationContext, 0, updateWidgetIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        mUpdateWidgetTomorrowPendingIntent = PendingIntent.getBroadcast(context.applicationContext, 1, updateWidgetIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        val filterOn = IntentFilter(Intent.ACTION_SCREEN_ON)
        val filterOff = IntentFilter(Intent.ACTION_SCREEN_OFF)
        val screenBroadcastReceiver = ScreenBroadcastReceiver()
        context.applicationContext.registerReceiver(screenBroadcastReceiver, filterOn)
        context.applicationContext.registerReceiver(screenBroadcastReceiver, filterOff)
    }

    /**
     * Cancel any scheduled update alarms, reschedule an update alarm, and force an update now.
     */
    fun schedule(context: Context) {
        Log.v(TAG, "schedule")

        val frequency = FRCPreferences.getInstance(context).updateFrequency
        Log.v(TAG, "Start alarm with frequency " + frequency)
        // If we show the time, we will update the widget every decimal "minute" (86.4 Gregorian seconds) starting
        // one decimal "minute" from now.
        var nextAlarmTime = System.currentTimeMillis() + frequency
        val mgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        // If we only show the date, we will update the widget every day just before midnight
        if (frequency == FRCPreferences.FREQUENCY_DAYS) {
            nextAlarmTime = getTimeTomorrowMidnightMillis()
        }

        // Schedule the periodic updates.
        mgr.setRepeating(AlarmManager.RTC, nextAlarmTime, frequency.toLong(), mUpdateWidgetPendingIntent)
        scheduleTomorrow(context)

        // Also send a broadcast to force an update now.
        val updateInent = Intent(ACTION_WIDGET_UPDATE)
        IntentCompat.setPackage(updateInent, context.packageName)
        context.sendBroadcast(updateInent)

        Log.v(TAG, "Started updater")
    }

    /**
     * A bug (or maybe expected behavior?) has been observed on Marshmallow.
     * If the widget is supposed to update every night at midnight, the #schedule() method
     * uses the setRepeating() api.  If the device is idle (doze mode?) at midnight, we would expect the alarm
     * to be triggered when the device wakes up later.  This isn't the observed case however. It
     * appears that if the device is idle when a setRepeating alarm is supposed to trigger, that
     * specific alarm execution is skipped completely, and the alarm won't go off again until the next
     * scheduled execution (the subsequent day at midnight).  The user impact is that if the user
     * goes to bed before midnight every day, turning off the device screen before going to bed, the
     * widget will never (ever!) update.
     *
     * We attempt to workaround this by setting an exact alarm for tomorrow at midnight.  With the
     * setExact api, if the device is idle at midnight, the alarm may not trigger at midnight, but
     * at least it will trigger when the device wakes up later, which is good enough for us.
     */
    fun scheduleTomorrow(context: Context) {
        if (ApiHelper.getAPILevel() >= Build.VERSION_CODES.KITKAT) {
            val nextAlarmTime = getTimeTomorrowMidnightMillis()
            Api19Helper.scheduleExact(context, nextAlarmTime, mUpdateWidgetTomorrowPendingIntent)
        }
    }

    /**
     * Perhaps unintuitive but maybe simplest way to get a timestamp for "tomorrow at midnight".
     * This doesn't return a timestamp of tomorrow at midnight exactly, but tomorrow at midnight plus
     * X seconds: X is the number of seconds past the current minute.
     * For example, if right now it's April 16 at 14:42:37.558, this will return the timestamp for
     * April 17 at 00:00:37.558.
     */
    private fun getTimeTomorrowMidnightMillis(): Long {
        val cal = Calendar.getInstance()
        cal[Calendar.HOUR_OF_DAY] = 23
        cal[Calendar.MINUTE] = 59
        cal.add(Calendar.MINUTE, 1)
        return cal.timeInMillis
    }

    /**
     * Cancel any scheduled update alarms.
     */
    fun cancel(context: Context) {
        Log.v(TAG, "cancel")
        val mgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        mgr.cancel(mUpdateWidgetPendingIntent)
    }

    /**
     * In the mode where the time is displayed, we only want to be updating the widgets when the screen is on,
     * because the update will occur every minute.
     * TODO: this screen broadcast receiver will stop being triggered when the OS decides to kill
     * our app process to free memory. We need to find a solution (if possible) for when the user
     * has chosen to display the decimal clock.  In that case, we want to update the widget every
     * minute when the screen is on, and do nothing when the screen is off.  However, if we want to
     * "behave", if the user hasn't added any widget at all, we don't want to be doing anything
     * at all during screen on/off events: so we shouldn't add a receiver for these events in the manifest.
     */
    private inner class ScreenBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Log.v(TAG, "onReceive: intent = " + intent)

            when (intent.action) {
                Intent.ACTION_SCREEN_OFF -> {
                    val frequency = FRCPreferences.getInstance(context).updateFrequency
                    if (frequency < FRCPreferences.FREQUENCY_DAYS) {
                        cancel(context)
                    }
                }
                Intent.ACTION_SCREEN_ON -> {
                    schedule(context)
                }
            }
        }
    }
}
