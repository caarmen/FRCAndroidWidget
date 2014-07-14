package ca.rmen.android.frenchcalendar;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class FrenchCalendarScheduler {
    private static final String TAG = Constants.TAG + FrenchCalendarScheduler.class.getSimpleName();
    public static final String BROADCAST_MESSAGE_UPDATE = ".UPDATE_WIDGET";
    private static final int FREQUENCY_DAYS = 86400000;

    private static FrenchCalendarScheduler INSTANCE;
    private final Context context;
    private final PendingIntent updatePendingIntent;

    private FrenchCalendarScheduler(Context context) {
        this.context = context.getApplicationContext();
        Intent updateIntent = new Intent(context.getPackageName() + BROADCAST_MESSAGE_UPDATE);
        updatePendingIntent = PendingIntent.getBroadcast(context, 0, updateIntent, 0);
        IntentFilter filterOn = new IntentFilter(Intent.ACTION_SCREEN_ON);
        IntentFilter filterOff = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        context.getApplicationContext().registerReceiver(screenBroadcastReceiver, filterOn);
        context.getApplicationContext().registerReceiver(screenBroadcastReceiver, filterOff);
    }

    public synchronized static FrenchCalendarScheduler getInstance(Context context) {
        if (INSTANCE == null) INSTANCE = new FrenchCalendarScheduler(context);
        return INSTANCE;
    }

    void start() {
        Log.v(TAG, "start");
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String frequencyPrefStr = sharedPreferences.getString(FrenchCalendarPrefs.PREF_FREQUENCY, FrenchCalendarPrefs.FREQUENCY_MINUTES);

        int frequency = Integer.parseInt(frequencyPrefStr);
        Log.v(TAG, "Start alarm with frequency " + frequency);
        // If we show the time, we will update the widget every decimal "minute" (86.4 Gregorian seconds) starting 
        // one decimal "minute" from now.
        long nextAlarmTime = System.currentTimeMillis() + frequency;
        AlarmManager mgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        // If we only show the date, we will update the widget every day just before midnight
        if (frequency == FREQUENCY_DAYS) {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.add(Calendar.MINUTE, 1);
            nextAlarmTime = cal.getTimeInMillis();
        }
        mgr.setRepeating(AlarmManager.RTC, nextAlarmTime, frequency, updatePendingIntent);

        Intent updateIntent = new Intent(context.getPackageName() + BROADCAST_MESSAGE_UPDATE);
        context.sendBroadcast(updateIntent);

        Log.v(TAG, "Started updater");
    }

    void stop() {
        Log.v(TAG, "stop");
        AlarmManager mgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        mgr.cancel(updatePendingIntent);
    }

    private final BroadcastReceiver screenBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.v(TAG, "onReceive: intent = " + intent);

            if (Intent.ACTION_SCREEN_OFF.equals(intent.getAction())) {
                stop();
            } else if (Intent.ACTION_SCREEN_ON.equals(intent.getAction())) {
                start();
            }
        }
    };

}
