/*
 * French Revolutionary Calendar Android Widget
 * Copyright (C) 2011 - 2014, 2017 Carmen Alvarez
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

package ca.rmen.android.frcwidget.render

import android.annotation.TargetApi
import android.appwidget.AppWidgetManager
import android.content.Context
import android.util.Log
import android.widget.TextView
import ca.rmen.android.frccommon.Constants

/**
 * Provides methods used to render widgets. These methods are available only on api level 16+
 *
 * @author calvarez
 */
object FRCRenderApi16 {
    private val TAG = Constants.TAG + FRCRenderApi16::class.java.simpleName

    /**
     * @return Looks at the size of the given widget, and returns the scale factor based on this widget's default size. All the widget's default dimensions
     * should be multiplied by this scale factor when rendering the widget.
     */
    @TargetApi(16)
    fun getScaleFactor(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int, defaultWidgetWidth: Float, defaultWidgetHeight: Float): Float {
        Log.v(TAG, "getScaleFactor for widget " + appWidgetId)
        val widgetOptions = appWidgetManager.getAppWidgetOptions(appWidgetId)
        val keys = widgetOptions.keySet()
        Log.v(TAG, "getScaleFactor: widget option keys: " + keys)

        val minWidth = widgetOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH)
        val minHeight = widgetOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT)
        val maxWidth = widgetOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH)
        val maxHeight = widgetOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT)
        Log.v(TAG, "getScaleFactor: min:" + minWidth + "x" + minHeight + ", max:" + maxWidth + "x" + maxHeight + ", default:" + defaultWidgetWidth + "x" + defaultWidgetHeight)
        if (maxWidth <= 0 || maxHeight <= 0) {
            return FRCRenderApi13.getMaxScaleFactor(context, defaultWidgetWidth, defaultWidgetHeight)
        }
        return Math.max(maxWidth / defaultWidgetWidth, maxHeight / defaultWidgetHeight)
    }

    @TargetApi(16)
    fun scaleShadow(textView: TextView, scaleFactor: Float) =
            textView.setShadowLayer(textView.shadowRadius * scaleFactor,
                    textView.shadowDx * scaleFactor,
                    textView.shadowDy * scaleFactor,
                    textView.shadowColor)
}
