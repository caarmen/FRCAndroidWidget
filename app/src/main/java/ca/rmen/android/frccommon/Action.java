/*
 * French Revolutionary Calendar Android Widget
 * Copyright (C) 2016 - 2017 Carmen Alvarez
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


import android.app.PendingIntent;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.DrawableRes;

import java.util.Locale;

import ca.rmen.android.frccommon.converter.FRCConverterActivity;
import ca.rmen.android.frccommon.converter.FRCLegacyConverterActivity;
import ca.rmen.android.frccommon.prefs.FRCPreferenceActivity;
import ca.rmen.android.frccommon.prefs.FRCPreferences;
import ca.rmen.android.frenchcalendar.R;
import ca.rmen.lfrc.FrenchRevolutionaryCalendarDate;

public class Action {
    public final String title;
    @DrawableRes
    public final int iconId;
    public final Intent intent;
    public final PendingIntent pendingIntent;

    private Action(@DrawableRes int iconId, String title, Intent intent, PendingIntent pendingIntent) {
        this.iconId = iconId;
        this.title = title;
        this.intent = intent;
        this.pendingIntent = pendingIntent;
    }

    public static Action getLightSearchAction(Context context, FrenchRevolutionaryCalendarDate date) {
        return getSearchAction(context, date, R.drawable.ic_action_search);
    }

    public static Action getDarkSearchAction(Context context, FrenchRevolutionaryCalendarDate date) {
        return getSearchAction(context, date, R.drawable.ic_action_search_dark);
    }

    private static Action getSearchAction(Context context, FrenchRevolutionaryCalendarDate date, @DrawableRes int iconId) {
        Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
        intent.putExtra(SearchManager.QUERY, date.getObjectOfTheDay());
        // No apps can handle ACTION_WEB_SEARCH.  We'll try a more generic intent instead
        if (context.getPackageManager().queryIntentActivities(intent, 0).isEmpty()) {
            intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            Locale locale = FRCPreferences.getInstance(context.getApplicationContext()).getLocale();
            intent.putExtra(Intent.EXTRA_TEXT, date.getObjectOfTheDay().toLowerCase(locale));
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(context, R.id.action_search, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return new Action(iconId, context.getString(R.string.popup_action_search, date.getObjectOfTheDay()), intent, pendingIntent);
    }

    public static Action getLightShareAction(Context context, FrenchRevolutionaryCalendarDate date) {
        return getShareAction(context, date, R.drawable.ic_action_share);
    }

    public static Action getDarkShareAction(Context context, FrenchRevolutionaryCalendarDate date) {
        return getShareAction(context, date, R.drawable.ic_action_share_dark);
    }

    public static Action getShareAction(Context context, FrenchRevolutionaryCalendarDate date, @DrawableRes int iconId) {
        // Prepare the text to share, based on the current date.
        String subject = context.getString(R.string.share_subject, date.getWeekdayName(), date.dayOfMonth, date.getMonthName(),
                FRCDateUtils.formatNumber(context, date.year));
        String time = String.format(Locale.US, "%d:%02d:%02d", date.hour, date.minute, date.second);
        String objectType = context.getResources().getStringArray(R.array.daily_object_types)[date.getObjectType().ordinal()];
        String body = context.getString(R.string.share_body, date.getWeekdayName(), date.dayOfMonth, date.getMonthName(),
                FRCDateUtils.formatNumber(context, date.year), time, objectType, date.getObjectOfTheDay(), FRCDateUtils.getDaysSinceDay1());

        // Open an intent chooser to share our text.
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        shareIntent.putExtra(Intent.EXTRA_TEXT, body);
        Intent intent = Intent.createChooser(shareIntent, context.getString(R.string.chooser_title));
        PendingIntent pendingIntent = PendingIntent.getActivity(context, R.id.action_share, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return new Action(iconId, context.getString(R.string.popup_action_share), intent, pendingIntent);
    }

    public static Action getConverterAction(Context context) {
        Intent intent = new Intent(context.getApplicationContext(), FRCConverterActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, R.id.action_converter, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return new Action(R.drawable.ic_action_converter, context.getString(R.string.popup_action_converter), intent, pendingIntent);
    }

    public static Action getLegacyConverterAction(Context context) {
        Intent intent = new Intent(context.getApplicationContext(), FRCLegacyConverterActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, R.id.action_converter, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return new Action(R.drawable.ic_action_converter, context.getString(R.string.popup_action_converter), intent, pendingIntent);
    }

    public static Action getSettingsAction(Context context) {
        Intent intent = new Intent(context.getApplicationContext(), FRCPreferenceActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, R.id.action_settings, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return new Action(R.drawable.ic_action_settings, context.getString(R.string.popup_action_settings), intent, pendingIntent);
    }
}
