/*
 * French Revolutionary Calendar Android Widget
 * Copyright (C) 2014 Benoit 'BoD' Lubek (BoD@JRAF.org)
 * Copyright (C) 2011 - 2016 Carmen Alvarez
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
package ca.rmen.android.frcwear;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

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
        if (!FRCPreferences.getInstance(this).getAndroidWearEnabled()) {
            // We got triggered, but the setting is off so please don't do anything
            return;
        }

        FRCWearCommHelper wearCommHelper = FRCWearCommHelper.get();
        updateToday(this, wearCommHelper);
    }

    @Override
    public void onDestroy() {
        FRCWearCommHelper.get().disconnect();
        super.onDestroy();
    }

    public static void backgroundUpdateToday(final Context context) {
        final FRCWearCommHelper wearCommHelper = FRCWearCommHelper.get();
        wearCommHelper.connect(context);
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                synchronized (wearCommHelper) {
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
        int color = FRCDateUtils.getColor(context, frenchDate);
        wearCommHelper.updateToday(date, dayOfYear, color);
    }
}
