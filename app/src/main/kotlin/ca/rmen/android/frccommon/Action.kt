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

package ca.rmen.android.frccommon

import android.app.PendingIntent
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.support.annotation.DrawableRes
import ca.rmen.android.frccommon.converter.FRCConverterActivity
import ca.rmen.android.frccommon.converter.FRCLegacyConverterActivity
import ca.rmen.android.frccommon.prefs.FRCPreferenceActivity
import ca.rmen.android.frccommon.prefs.FRCPreferences
import ca.rmen.android.frenchcalendar.R
import ca.rmen.lfrc.FrenchRevolutionaryCalendarDate
import java.util.Locale

data class Action private constructor(@DrawableRes val iconId: Int, val title: String, val intent: Intent, @JvmField val pendingIntent: PendingIntent) {
    companion object {
        fun getLightSearchAction(context: Context, date: FrenchRevolutionaryCalendarDate): Action = getSearchAction(context, date, R.drawable.ic_action_search)
        fun getDarkSearchAction(context: Context, date: FrenchRevolutionaryCalendarDate): Action = getSearchAction(context, date, R.drawable.ic_action_search_dark)
        fun getLightShareAction(context: Context, date: FrenchRevolutionaryCalendarDate): Action = getShareAction(context, date, R.drawable.ic_action_share)
        fun getDarkShareAction(context: Context, date: FrenchRevolutionaryCalendarDate): Action = getShareAction(context, date, R.drawable.ic_action_share_dark)

        private fun getSearchAction(context: Context, date: FrenchRevolutionaryCalendarDate, @DrawableRes iconId: Int): Action {
            var intent = Intent(Intent.ACTION_WEB_SEARCH)
            intent.putExtra(SearchManager.QUERY, date.objectOfTheDay)
            // No apps can handle ACTION_WEB_SEARCH.  We'll try a more generic intent instead
            if (context.packageManager.queryIntentActivities(intent, 0).isEmpty()) {
                intent = Intent(Intent.ACTION_SEND)
                intent.type = "text/plain"
                val locale = FRCPreferences.getInstance(context).locale
                intent.putExtra(Intent.EXTRA_TEXT, date.objectOfTheDay.toLowerCase(locale))
            }
            val pendingIntent = PendingIntent.getActivity(context, R.id.action_search, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            return Action(iconId, context.getString(R.string.popup_action_search, date.objectOfTheDay), intent, pendingIntent)
        }

        private fun getShareAction(context: Context, date: FrenchRevolutionaryCalendarDate, @DrawableRes iconId: Int): Action {
            // Prepare the text to share, based on the current date.
            val subject = context.getString(R.string.share_subject, date.weekdayName, date.dayOfMonth, date.monthName,
                    FRCDateUtils.formatNumber(context, date.year))
            val time = java.lang.String.format(Locale.US, "%d:%02d:%02d", date.hour, date.minute, date.second)
            val objectType = context.resources.getStringArray(R.array.daily_object_types)[date.objectType.ordinal]
            val body = context.getString(R.string.share_body, date.weekdayName, date.dayOfMonth, date.monthName,
                    FRCDateUtils.formatNumber(context, date.year), time, objectType, date.objectOfTheDay, FRCDateUtils.getDaysSinceDay1())

            // Open an intent chooser to share our text.
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
            shareIntent.putExtra(Intent.EXTRA_TEXT, body)
            val intent = Intent.createChooser(shareIntent, context.getString(R.string.chooser_title))
            val pendingIntent = PendingIntent.getActivity(context, R.id.action_share, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            return Action(iconId, context.getString(R.string.popup_action_share), intent, pendingIntent)
        }

        fun getConverterAction(context: Context): Action {
            val intent = Intent(context.applicationContext, FRCConverterActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(context, R.id.action_converter, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            return Action(R.drawable.ic_action_converter, context.getString(R.string.popup_action_converter), intent, pendingIntent)
        }

        fun getLegacyConverterAction(context: Context): Action {
            val intent = Intent(context.applicationContext, FRCLegacyConverterActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(context, R.id.action_converter, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            return Action(R.drawable.ic_action_converter, context.getString(R.string.popup_action_converter), intent, pendingIntent)
        }

        fun getSettingsAction(context: Context): Action {
            val intent = Intent(context.applicationContext, FRCPreferenceActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(context, R.id.action_settings, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            return Action(R.drawable.ic_action_settings, context.getString(R.string.popup_action_settings), intent, pendingIntent)
        }
    }
}