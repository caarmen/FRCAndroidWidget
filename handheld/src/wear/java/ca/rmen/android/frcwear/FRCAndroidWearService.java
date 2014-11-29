/*
 * French Revolutionary Calendar Android Widget
 * Copyright (C) 2014 Benoit 'BoD' Lubek (BoD@JRAF.org)
 * Copyright (C) 2011 - 2014 Carmen Alvarez
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package ca.rmen.android.frcwear;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import ca.rmen.android.frccommon.FRCDateUtils;
import ca.rmen.android.frccommon.prefs.FRCPreferences;
import ca.rmen.lfrc.FrenchRevolutionaryCalendarDate;

public class FRCAndroidWearService extends IntentService {

    public FRCAndroidWearService() {
        super("FRCAndroidWearService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        FRCWearCommHelper.get().connect(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences preferenceManager = PreferenceManager.getDefaultSharedPreferences(this);
        if (!FRCPreferences.getInstance(this).getAndroidWearEnabled()) {
            // We got triggered, but the setting is off so please don't do anything
            return;
        }

        FRCWearCommHelper wearCommHelper = FRCWearCommHelper.get();
        if (FRCWearScheduler.ACTION_WEAR_REMOVE_AND_UPDATE.equals(intent.getAction())) {
            wearCommHelper.removeToday();
        }
        updateToday(this, wearCommHelper);
    }

    @Override
    public void onDestroy() {
        FRCWearCommHelper.get().disconnect();
        super.onDestroy();
    }

    public static PendingIntent getPendingIntent(Context context, String action) {
        Intent intent = new Intent(context, FRCAndroidWearService.class);
        intent.setAction(action);
        return PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static void backgroundRemoveAndUpdateDays(final Context context) {
        final FRCWearCommHelper wearCommHelper = FRCWearCommHelper.get();
        wearCommHelper.connect(context);
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                synchronized (wearCommHelper) {
                    wearCommHelper.removeToday();

                    updateToday(context, wearCommHelper);
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                wearCommHelper.disconnect();
            }
        }.execute();
    }

    private static void updateToday(Context context, FRCWearCommHelper wearCommHelper) {
        FrenchRevolutionaryCalendarDate frenchDate = FRCDateUtils.getToday(context);
        String date = frenchDate.getWeekdayName() + " " + frenchDate.dayOfMonth + " " + frenchDate.getMonthName() + " " + frenchDate.year;
        String dayOfYear = frenchDate.getDayOfYear();
        int month = frenchDate.month;
        int color = FRCDateUtils.getColor(context, frenchDate);
        wearCommHelper.updateToday(date, dayOfYear, month, color);
    }
}
