/*
 * French Revolutionary Calendar Android Widget
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
package ca.rmen.android.frcwidget;

import java.util.HashSet;
import java.util.Set;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import ca.rmen.android.frenchcalendar.FrenchCalendarAppWidgetMinimalist;
import ca.rmen.android.frenchcalendar.FrenchCalendarAppWidgetNarrow;
import ca.rmen.android.frenchcalendar.FrenchCalendarAppWidgetWide;

class FRCAppWidgetManager {

    /**
     * @return all the widget ids for both narrow and wide widgets.
     */
    static Set<Integer> getAllAppWidgetIds(Context context) {
        Set<Integer> result = new HashSet<>();
        result.addAll(getAppWidgetIds(context, FrenchCalendarAppWidgetNarrow.class));
        result.addAll(getAppWidgetIds(context, FrenchCalendarAppWidgetWide.class));
        result.addAll(getAppWidgetIds(context, FrenchCalendarAppWidgetMinimalist.class));
        return result;
    }

    /**
     * @return the widget ids for the given widget class (wide or narrow)
     */
    private static Set<Integer> getAppWidgetIds(Context context, Class<?> appWidgetClass) {
        final AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        final ComponentName provider = new ComponentName(context, appWidgetClass);
        Set<Integer> result = new HashSet<>();
        final int[] appWidgetIds = appWidgetManager.getAppWidgetIds(provider);
        if (appWidgetIds != null) {
            for (int appWidgetId : appWidgetIds)
                result.add(appWidgetId);
        }
        return result;
    }

}
