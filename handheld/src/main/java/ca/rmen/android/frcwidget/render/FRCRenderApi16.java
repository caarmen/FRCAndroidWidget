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
package ca.rmen.android.frcwidget.render;

import android.annotation.TargetApi;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.util.Set;

import ca.rmen.android.frccommon.Constants;

/**
 * Provides methods used to render widgets. These methods are available only on api level 16+
 *
 * @author calvarez
 */
class FRCRenderApi16 {
    private static final String TAG = Constants.TAG + FRCRenderApi16.class.getSimpleName();

    /**
     * @return Looks at the size of the given widget, and returns the scale factor based on this widget's default size. All the widget's default dimensions
     * should be multiplied by this scale factor when rendering the widget.
     */
    @TargetApi(16)
    static float getScaleFactor(Context context, AppWidgetManager appWidgetManager, int appWidgetId, float defaultWidgetWidth, float defaultWidgetHeight) {
        Log.v(TAG, "getScaleFactor for widget " + appWidgetId);
        Bundle widgetOptions = appWidgetManager.getAppWidgetOptions(appWidgetId);
        Set<String> keys = widgetOptions.keySet();
        Log.v(TAG, "getScaleFactor: widget option keys: " + keys);

        int minWidth = widgetOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);
        int minHeight = widgetOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT);
        int maxWidth = widgetOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH);
        int maxHeight = widgetOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT);
        Log.v(TAG, "getScaleFactor: min:" + minWidth + "x" + minHeight + ", max:" + maxWidth + "x" + maxHeight + ", default:" + defaultWidgetWidth + "x" + defaultWidgetHeight);
        if (maxWidth <= 0 || maxHeight <= 0)
            return FRCRenderApi13.getMaxScaleFactor(context, defaultWidgetWidth, defaultWidgetHeight);
        return Math.max((float) maxWidth / defaultWidgetWidth, (float) maxHeight / defaultWidgetHeight);
    }

    @TargetApi(16)
    static void scaleShadow(TextView textView, float scaleFactor) {
        textView.setShadowLayer(textView.getShadowRadius() * scaleFactor,
                textView.getShadowDx() * scaleFactor,
                textView.getShadowDy() * scaleFactor,
                textView.getShadowColor());
    }
}
