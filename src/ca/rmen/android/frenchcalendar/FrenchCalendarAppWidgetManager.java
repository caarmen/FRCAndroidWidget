package ca.rmen.android.frenchcalendar;

import java.util.HashSet;
import java.util.Set;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;

class FrenchCalendarAppWidgetManager {

    /**
     * @return all the widget ids for both narrow and wide widgets.
     */
    static Set<Integer> getAllAppWidgetIds(Context context) {
        Set<Integer> result = new HashSet<Integer>();
        result.addAll(getAppWidgetIds(context, FrenchCalendarAppWidgetWide.class));
        result.addAll(getAppWidgetIds(context, FrenchCalendarAppWidgetNarrow.class));
        return result;
    }

    /**
     * @return the widget ids for the given widget class (wide or narrow)
     */
    private static Set<Integer> getAppWidgetIds(Context context, Class<?> appWidgetClass) {
        final AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        final ComponentName provider = new ComponentName(context, appWidgetClass);
        Set<Integer> result = new HashSet<Integer>();
        final int[] appWidgetIds = appWidgetManager.getAppWidgetIds(provider);
        if (appWidgetIds != null) {
            for (int appWidgetId : appWidgetIds)
                result.add(appWidgetId);
        }
        return result;
    }

}
