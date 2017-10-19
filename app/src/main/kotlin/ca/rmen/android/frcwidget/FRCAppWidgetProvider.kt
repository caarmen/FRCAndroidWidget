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

import android.annotation.TargetApi
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import ca.rmen.android.frccommon.Constants
import ca.rmen.android.frccommon.FRCDateUtils
import ca.rmen.android.frcwidget.render.FRCAppWidgetRendererFactory
import ca.rmen.android.frenchcalendar.R
import java.util.Arrays

/**
 * Receiver and AppWidgetProvider which updates a list of wide widgets or a list of narrow widgets.
 *
 * At any given point, there will be at most three instances of this class:
 * <ul>
 * <li> one [ca.rmen.android.frenchcalendar.FrenchCalendarAppWidgetWide] which will manage all of the wide widgets, and </li>
 * <li> one [ca.rmen.android.frenchcalendar.FrenchCalendarAppWidgetNarrow] which will manage all of the narrow widgets.</li>
 * <li> one [ca.rmen.android.frenchcalendar.FrenchCalendarAppWidgetMinimalist] which will manage all of the minimalist widgets.</li>
 * </ul>
 * These receivers are notified by the system when a widget of the given type is added or deleted,
 * or when widgets of the given type should be updated.
 *
 * These receivers are also notified by the alarm set up by [FRCWidgetScheduler], which will
 * go off either once a minute, or once a day, depending on the preferences.
 */
abstract class FRCAppWidgetProvider : AppWidgetProvider() {

    private val tag get()  :String = Constants.TAG + javaClass.simpleName
    override fun onReceive(context: Context, intent: Intent) {
        Log.v(tag, "onReceive: action = " + intent.action + ": component = " + intent.component?.className)
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(intent.component)
        if (FRCWidgetScheduler.ACTION_WIDGET_UPDATE == intent.action) {
            val allAppWidgetIds = FRCAppWidgetManager.getAllAppWidgetIds(context)
            if (allAppWidgetIds.isEmpty()) {
                FRCWidgetScheduler.getInstance(context).cancel(context)
            } else {
                updateAll(context, appWidgetManager, appWidgetIds)
            }
        }
        FRCWidgetScheduler.getInstance(context).scheduleTomorrow(context)
        super.onReceive(context, intent)
    }

    @TargetApi(16)
    override fun onAppWidgetOptionsChanged(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int, newOptions: Bundle) {
        newOptions.isEmpty
        Log.v(tag, "onAppWidgetOptionsChanged: appWidgetId  $appWidgetId, newOptions = $newOptions")
        update(context, appWidgetManager, appWidgetId)
    }

    /**
     * This is called by the parent class when the system broadcasts "android.appwidget.action.APPWIDGET_UPDATE".
     */
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        Log.v(tag, "onUpdate: appWidgetIds = " + Arrays.toString(appWidgetIds))
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        FRCWidgetScheduler.getInstance(context).schedule(context)
    }

    /**
     * Rerender all the widgets (for this [AppWidgetProvider]).
     */
    private fun updateAll(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        Log.v(tag, "updateAll: appWidgetIds = " + Arrays.toString(appWidgetIds))
        appWidgetIds.forEach { appWidgetId -> update(context, appWidgetManager, appWidgetId) }
    }

    /**
     * Rerender a single widget.
     */
    private fun update(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
        Log.v(tag, "update: appWidgetId = $appWidgetId")
        val renderer = FRCAppWidgetRendererFactory.getRenderer(getWidgetType())
        val views = renderer.render(context, appWidgetManager, appWidgetId)

        val date = FRCDateUtils.getToday(context.applicationContext)
        val intent = Intent(context, FRCPopupActivity::class.java)
        intent.putExtra(FRCPopupActivity.EXTRA_DATE, date)
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        views.setOnClickPendingIntent(R.id.rootView, pendingIntent)
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    protected abstract fun getWidgetType(): Constants.WidgetType
}
