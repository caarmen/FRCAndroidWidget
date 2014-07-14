/*
 * French Revolutionary Calendar Android Widget
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
package ca.rmen.android.frenchcalendar;

import java.util.Arrays;
import java.util.Set;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import ca.rmen.android.frenchcalendar.Constants.WidgetType;
import ca.rmen.android.frenchcalendar.prefs.FrenchCalendarPreferenceActivity;
import ca.rmen.android.frenchcalendar.render.FrenchCalendarAppWidgetRenderParams;
import ca.rmen.android.frenchcalendar.render.FrenchCalendarAppWidgetRenderParamsFactory;
import ca.rmen.android.frenchcalendar.render.FrenchCalendarAppWidgetRenderer;

/**
 * Receiver and AppWidgetProvider which updates a list of wide widgets or a list of narrow widgets.
 * 
 * At any given point, there will be at most two instances of this class:
 * <ul>
 * <li> one {@link FrenchCalendarAppWidgetWide} which will manage all of the wide widgets, and </li>
 * <li> one {@link FrenchCalendarAppWidgetNarrow} which will manage all of the narrow widgets.</li>
 * </ul>
 * These receivers are notified by the system when a widget of the given type is added or deleted,
 * or when widgets of the given type should be updated.
 * 
 * These receivers are also notified by the alarm set up by {@link FrenchCalendarScheduler}, which will
 * go off either once a minute, or once a day, depending on the preferences.
 */
abstract class FrenchCalendarAppWidget extends AppWidgetProvider {

    private final String TAG = Constants.TAG + getClass().getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v(TAG, "onReceive: action = " + intent.getAction() + ": component = " + (intent.getComponent() == null ? "" : intent.getComponent().getClassName()));
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName provider = intent.getComponent();
        final int[] appWidgetIds = appWidgetManager.getAppWidgetIds(provider);
        if ((context.getPackageName() + FrenchCalendarScheduler.BROADCAST_MESSAGE_UPDATE).equals(intent.getAction())) {
            Set<Integer> allAppWidgetIds = FrenchCalendarAppWidgetManager.getAllAppWidgetIds(context);
            if (allAppWidgetIds.size() == 0) FrenchCalendarScheduler.getInstance(context).cancel();
            else
                updateAll(context, appWidgetManager, appWidgetIds);
        }
        super.onReceive(context, intent);
    }

    /**
     * This is called by the parent class when the system broadcasts "android.appwidget.action.APPWIDGET_UPDATE".
     */
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.v(TAG, "onUpdate: appWidgetIds = " + Arrays.toString(appWidgetIds));
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        FrenchCalendarScheduler.getInstance(context).schedule();
    }

    /**
     * Rerender all the widgets (for this {@link AppWidgetProvider}).
     */
    private void updateAll(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.v(TAG, "updateAll:  appWidgetIds = " + Arrays.toString(appWidgetIds));
        for (int appWidgetId : appWidgetIds)
            update(context, appWidgetManager, appWidgetId);
    }

    /**
     * Rerender a single widget.
     */
    private void update(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        Log.v(TAG, "update: appWidgetId = " + appWidgetId);
        FrenchCalendarAppWidgetRenderParams renderParams = FrenchCalendarAppWidgetRenderParamsFactory.getRenderParams(getWidgetType());
        RemoteViews views = FrenchCalendarAppWidgetRenderer.render(context, renderParams);

        Intent intent = new Intent(context, FrenchCalendarPreferenceActivity.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        intent.addCategory(getClass().getName());
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        views.setOnClickPendingIntent(R.id.imageView1, pendingIntent);
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    protected abstract WidgetType getWidgetType();

    public static class FrenchCalendarAppWidgetNarrow extends FrenchCalendarAppWidget {
        @Override
        protected WidgetType getWidgetType() {
            return WidgetType.NARROW;
        }
    }

    public static class FrenchCalendarAppWidgetWide extends FrenchCalendarAppWidget {
        @Override
        protected WidgetType getWidgetType() {
            return WidgetType.WIDE;
        }
    }
}