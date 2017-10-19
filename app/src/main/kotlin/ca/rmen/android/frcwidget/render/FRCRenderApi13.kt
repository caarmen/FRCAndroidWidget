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
import android.content.Context
import android.graphics.Point
import android.util.Log
import android.view.WindowManager
import ca.rmen.android.frccommon.Constants

/**
 * Provides methods used to render widgets. These methods are available only on api level 13+
 *
 * @author calvarez
 */
object FRCRenderApi13 {
    private val TAG = Constants.TAG + FRCRenderApi13::class.java.simpleName

    /**
     * @return Looks at the size of the whole screen, and returns the scale factor based on this widget's default size. All the widget's default dimensions
     *         should be multiplied by this scale factor when rendering the widget.
     */
    @TargetApi(13)
    fun getMaxScaleFactor(context: Context, defaultWidgetWidth: Float, defaultWidgetHeight: Float): Float {
        Log.v(TAG, "getMaxScaleFactor")
        val defaultDisplay = (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
        val point = Point()
        defaultDisplay.getSize(point)
        val maxWidth = point.x / 2
        val maxHeight = point.y / 2
        Log.v(TAG, "getScaleFactor: using whole screen as max size: " + maxWidth + "x" + maxHeight)
        if (maxWidth <= 0 || maxHeight <= 0) return 1.0f
        return Math.max(maxWidth / defaultWidgetWidth, maxHeight / defaultWidgetHeight)
    }

}
