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

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import ca.rmen.android.frenchcalendar.FrenchCalendarAppWidgetMinimalist
import ca.rmen.android.frenchcalendar.FrenchCalendarAppWidgetNarrow
import ca.rmen.android.frenchcalendar.FrenchCalendarAppWidgetWide

object FRCAppWidgetManager {

    /**
     * @return all the widget ids for both narrow and wide widgets.
     */
    fun getAllAppWidgetIds(context : Context) : Set<Int> {
        val result = HashSet<Int>()
        result.addAll(getAppWidgetIds(context, FrenchCalendarAppWidgetNarrow::class.java))
        result.addAll(getAppWidgetIds(context, FrenchCalendarAppWidgetWide::class.java))
        result.addAll(getAppWidgetIds(context, FrenchCalendarAppWidgetMinimalist::class.java))
        return result
    }

    /**
     * @return the widget ids for the given widget class (wide or narrow)
     */
    private fun getAppWidgetIds(context : Context, appWidgetClass : Class<out FRCAppWidgetProvider>) : Set<Int> {
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val provider = ComponentName(context, appWidgetClass)
        val result = HashSet<Int>()
        val appWidgetIds = appWidgetManager.getAppWidgetIds(provider)
        appWidgetIds?.forEach { appWidgetId -> result.add(appWidgetId) }
        return result
    }
}
