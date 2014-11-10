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
package ca.rmen.android.frcwidget.wear;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import ca.rmen.android.frcwidget.FRCDateUtils;
import ca.rmen.android.frcwidget.prefs.FRCPreferences;
import ca.rmen.lfrc.FrenchRevolutionaryCalendarDate;

public class AndroidWearService extends IntentService {
    public static final String ACTION_UPDATE = "ACTION_UPDATE";
    public static final String ACTION_REMOVE_AND_UPDATE = "ACTION_REMOVE_AND_UPDATE";

    public AndroidWearService() {
        super("AndroidWearService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        WearCommHelper.get().connect(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences preferenceManager = PreferenceManager.getDefaultSharedPreferences(this);
        if (!FRCPreferences.getInstance(this).getAndroidWearEnabled()) {
            // We got triggered, but the setting is off so please don't do anything
            return;
        }

        WearCommHelper wearCommHelper = WearCommHelper.get();
        if (ACTION_REMOVE_AND_UPDATE.equals(intent.getAction())) {
            wearCommHelper.removeToday();
        }
        FrenchRevolutionaryCalendarDate frenchDate = FRCDateUtils.getToday(this);
        String date = frenchDate.dayOfMonth + " " + frenchDate.getMonthName() + " " + frenchDate.year;
        String dayOfYear = frenchDate.getDayOfYear();
        int month = frenchDate.month;
        wearCommHelper.updateToday(date, dayOfYear, month);
    }

    @Override
    public void onDestroy() {
        WearCommHelper.get().disconnect();
        super.onDestroy();
    }

    public static PendingIntent getPendingIntent(Context context, String action) {
        Intent intent = new Intent(context, AndroidWearService.class);
        intent.setAction(action);
        return PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static void backgroundRemoveAndUpdateDays(final Context context) {
        final WearCommHelper wearCommHelper = WearCommHelper.get();
        wearCommHelper.connect(context);
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                synchronized (wearCommHelper) {
                    wearCommHelper.removeToday();

                    FrenchRevolutionaryCalendarDate frenchDate = FRCDateUtils.getToday(context);
                    String date = frenchDate.dayOfMonth + " " + frenchDate.getMonthName() + " " + frenchDate.year;
                    String dayOfYear = frenchDate.getDayOfYear();
                    int month = frenchDate.month;
                    wearCommHelper.updateToday(date, dayOfYear, month);
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                wearCommHelper.disconnect();
            }
        }.execute();
    }
}
