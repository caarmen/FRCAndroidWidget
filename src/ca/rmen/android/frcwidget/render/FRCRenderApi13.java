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
import android.content.Context;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import ca.rmen.android.frcwidget.Constants;

/**
 * Provides methods used to render widgets. These methods are available only on api level 13+
 * 
 * @author calvarez
 */
public class FRCRenderApi13 {
    private static final String TAG = Constants.TAG + FRCRenderApi13.class.getSimpleName();

    /**
     * @return Looks at the size of the whole screen, and returns the scale factor based on this widget's default size. All the widget's default dimensions
     *         should be multiplied by this scale factor when rendering the widget.
     */
    @TargetApi(13)
    public static float getMaxScaleFactor(Context context, FRCAppWidgetRenderParams renderParams) {
        Log.v(TAG, "getMaxScaleFactor");
        Display defaultDisplay = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Point point = new Point();
        defaultDisplay.getSize(point);
        float width = context.getResources().getDimension(renderParams.widthResourceId);
        float height = context.getResources().getDimension(renderParams.heightResourceId);
        int maxWidth = point.x / 2;
        int maxHeight = point.y / 2;
        Log.v(TAG, "getScaleFactor: using whole screen as max size: " + maxWidth + "x" + maxHeight);
        if (maxWidth <= 0 || maxHeight <= 0) return 1.0f;
        return Math.max((float) maxWidth / width, (float) maxHeight / height);
    }
}
