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

package ca.rmen.android.frccommon;


import android.content.Context;
import android.content.Intent;

import java.util.Locale;

import ca.rmen.android.frenchcalendar.R;
import ca.rmen.lfrc.FrenchRevolutionaryCalendarDate;

public class Share {
    private Share() {
        // prevent instantiation
    }

    public static Intent getShareIntent(Context context, FrenchRevolutionaryCalendarDate date) {
        // Prepare the text to share, based on the current date.
        String subject = context.getString(R.string.share_subject, date.getWeekdayName(), date.dayOfMonth, date.getMonthName(),
                date.year);
        String time = String.format(Locale.US, "%d:%02d:%02d", date.hour, date.minute, date.second);
        String objectType = context.getResources().getStringArray(R.array.daily_object_types)[date.getObjectType().ordinal()];
        String body = context.getString(R.string.share_body, date.getWeekdayName(), date.dayOfMonth, date.getMonthName(),
                date.year, time, objectType, date.getDayOfYear(), FRCDateUtils.getDaysSinceDay1());

        // Open an intent chooser to share our text.
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        shareIntent.putExtra(Intent.EXTRA_TEXT, body);
        return Intent.createChooser(shareIntent, context.getString(R.string.chooser_title));
    }
}
