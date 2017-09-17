/*
 * French Revolutionary Calendar Android Widget
 * Copyright (C) 2011 - 2014 Carmen Alvarez
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

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

import java.util.Arrays;
import java.util.Set;

import ca.rmen.android.frccommon.Constants;
import ca.rmen.android.frccommon.Constants.WidgetType;
import ca.rmen.android.frccommon.FRCDateUtils;
import ca.rmen.android.frcwidget.render.FRCAppWidgetRenderer;
import ca.rmen.android.frcwidget.render.FRCAppWidgetRendererFactory;
import ca.rmen.android.frenchcalendar.FrenchCalendarAppWidgetMinimalist;
import ca.rmen.android.frenchcalendar.FrenchCalendarAppWidgetNarrow;
import ca.rmen.android.frenchcalendar.FrenchCalendarAppWidgetWide;
import ca.rmen.android.frenchcalendar.R;
import ca.rmen.lfrc.FrenchRevolutionaryCalendarDate;

/**
 * Receiver and AppWidgetProvider which updates a list of wide widgets or a list of narrow widgets.
 * 
 * At any given point, there will be at most three instances of this class:
 * <ul>
 * <li> one {@link FrenchCalendarAppWidgetWide} which will manage all of the wide widgets, and </li>
 * <li> one {@link FrenchCalendarAppWidgetNarrow} which will manage all of the narrow widgets.</li>
 * <li> one {@link FrenchCalendarAppWidgetMinimalist} which will manage all of the minimalist widgets.</li>
 * </ul>
 * These receivers are notified by the system when a widget of the given type is added or deleted,
 * or when widgets of the given type should be updated.
 * 
 * These receivers are also notified by the alarm set up by {@link FRCWidgetScheduler}, which will
 * go off either once a minute, or once a day, depending on the preferences.
 */
public abstract class FRCAppWidgetProvider extends AppWidgetProvider {

    private final String TAG = Constants.TAG + getClass().getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v(TAG, "onReceive: action = " + intent.getAction() + ": component = " + (intent.getComponent() == null ? "" : intent.getComponent().getClassName()));
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName provider = intent.getComponent();
        final int[] appWidgetIds = appWidgetManager.getAppWidgetIds(provider);
        if (FRCWidgetScheduler.ACTION_WIDGET_UPDATE.equals(intent.getAction())) {
            Set<Integer> allAppWidgetIds = FRCAppWidgetManager.getAllAppWidgetIds(context);
            if (allAppWidgetIds.size() == 0) FRCWidgetScheduler.getInstance(context).cancel(context);
            else
                updateAll(context, appWidgetManager, appWidgetIds);
        }
        FRCWidgetScheduler.getInstance(context).scheduleTomorrow(context);
        super.onReceive(context, intent);
    }

    @Override
    @TargetApi(16)
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        newOptions.isEmpty();
        Log.v(TAG, "onAppWidgetOptionsChanged: appWidgetId  " + appWidgetId + ", newOptions = " + newOptions);
        update(context, appWidgetManager, appWidgetId);
    }

    /**
     * This is called by the parent class when the system broadcasts "android.appwidget.action.APPWIDGET_UPDATE".
     */
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.v(TAG, "onUpdate: appWidgetIds = " + Arrays.toString(appWidgetIds));
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        FRCWidgetScheduler.getInstance(context).schedule(context);
    }

    public static void updateAll(Context context) {
        updateAll(context, WidgetType.NARROW, FrenchCalendarAppWidgetNarrow.class);
        updateAll(context, WidgetType.WIDE, FrenchCalendarAppWidgetWide.class);
        updateAll(context, WidgetType.MINIMALIST, FrenchCalendarAppWidgetMinimalist.class);
    }

    private static void updateAll(Context context, WidgetType widgetType, Class<? extends AppWidgetProvider> providerClass) {
        AppWidgetManager appWidgetManager = (AppWidgetManager) context.getSystemService(Context.APPWIDGET_SERVICE);
        ComponentName provider = new ComponentName(context, providerClass);
        int[] ids = appWidgetManager.getAppWidgetIds(provider);
        for (int id : ids) {
            update(context, appWidgetManager, id, widgetType);
        }
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
        update(context, appWidgetManager, appWidgetId, getWidgetType());
    }

    private static void update(Context context, AppWidgetManager appWidgetManager, int appWidgetId, WidgetType widgetType) {
        FRCAppWidgetRenderer renderer = FRCAppWidgetRendererFactory.getRenderer(widgetType);
        RemoteViews views = renderer.render(context, appWidgetManager, appWidgetId);
        FrenchRevolutionaryCalendarDate date = FRCDateUtils.getToday(context.getApplicationContext());
        Intent intent = new Intent(context, FRCPopupActivity.class);
        intent.putExtra(FRCPopupActivity.EXTRA_DATE, date);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        views.setOnClickPendingIntent(R.id.rootView, pendingIntent);
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    protected abstract WidgetType getWidgetType();

}