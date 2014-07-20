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
package ca.rmen.android.frcwidget.render;

import android.annotation.TargetApi;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import ca.rmen.android.frcwidget.Constants;

/**
 * Provides methods used to render widgets. These methods are available only on api level 16+
 * 
 * @author calvarez
 */
public class FRCRenderApi16 {
    private static final String TAG = Constants.TAG + FRCRenderApi16.class.getSimpleName();

    /**
     * @return Looks at the size of the given widget, and returns the scale factor based on this widget's default size. All the widget's default dimensions
     *         should be multiplied by this scale factor when rendering the widget.
     */
    @TargetApi(16)
    public static float getScaleFactor(Context context, AppWidgetManager appWidgetManager, int appWidgetId, FRCAppWidgetRenderParams renderParams) {
        Bundle widgetOptions = appWidgetManager.getAppWidgetOptions(appWidgetId);
        int minWidth = widgetOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);
        int minHeight = widgetOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT);
        int maxWidth = widgetOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH);
        int maxHeight = widgetOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT);
        float width = context.getResources().getDimension(renderParams.widthResourceId);
        float height = context.getResources().getDimension(renderParams.heightResourceId);
        if (maxWidth <= 0 || maxHeight <= 0) return 1.0f;
        Log.v(TAG, "getScaleFactor: min:" + minWidth + "x" + minHeight + ", max:" + maxWidth + "x" + maxHeight + ", default:" + width + "x" + height);
        return Math.min((float) maxWidth / width, (float) maxHeight / height);
    }
}