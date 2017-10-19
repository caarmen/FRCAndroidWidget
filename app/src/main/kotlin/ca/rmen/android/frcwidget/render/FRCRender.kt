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

package ca.rmen.android.frcwidget.render

import android.appwidget.AppWidgetManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.PorterDuff
import android.support.annotation.DimenRes
import android.support.annotation.DrawableRes
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RemoteViews
import android.widget.TextView
import ca.rmen.android.frccommon.Constants
import ca.rmen.android.frccommon.FRCDateUtils
import ca.rmen.android.frccommon.compat.ApiHelper
import ca.rmen.android.frccommon.prefs.FRCPreferences
import ca.rmen.android.frenchcalendar.R
import ca.rmen.lfrc.FrenchRevolutionaryCalendarDate
import java.util.Locale

/**
 * Provides methods used to render widgets.
 *
 * @author calvarez
 */
object FRCRender {
    private val TAG = Constants.TAG + FRCRender::class.java.simpleName

    /**
     * Depending on our api level and whether the launcher provides us the max
     * widget size, we calculate how much we should scale the widget components
     * (TextViews) before we draw them to a bitmap, for the sharpest image.
     *
     * @return The views in the widget should be scaled by this much before
     * drawing them to a bitmap.
     */
    private fun getScaleFactor(context: Context,
                               appWidgetManager: AppWidgetManager,
                               appWidgetId: Int,
                               @DimenRes defaultWidthResId: Int,
                               @DimenRes defaultHeightResId: Int): Float {
        val defaultWidgetWidth = context.resources.getDimension(defaultWidthResId)
        val defaultWidgetHeight = context.resources.getDimension(defaultHeightResId)
        var scaleFactor = 1.0f
        if (ApiHelper.apiLevel >= 16) {
            scaleFactor = FRCRenderApi16.getScaleFactor(context, appWidgetManager, appWidgetId, defaultWidgetWidth, defaultWidgetHeight)
        } else if (ApiHelper.apiLevel >= 13) {
            scaleFactor = FRCRenderApi13.getMaxScaleFactor(context, defaultWidgetWidth, defaultWidgetHeight)
        }
        return scaleFactor
    }

    fun scaleWidget(context: Context,
                    view: View,
                    appWidgetManager: AppWidgetManager,
                    appWidgetId: Int,
                    @DimenRes defaultWidthResId: Int,
                    @DimenRes defaultHeightResId: Int) {
        val scaleFactor = getScaleFactor(context, appWidgetManager, appWidgetId, defaultWidthResId, defaultHeightResId)
        scaleViews(view, scaleFactor)
    }

    /**
     * Rescale the given parent view and all its child views. The following will
     * be rescaled: 1) text sizes, 2) padding, 3) margins.
     */
    private fun scaleViews(parent: View, scaleFactor: Float) {
        // Scale the padding
        parent.setPadding((parent.paddingLeft * scaleFactor).toInt(), (parent.paddingTop * scaleFactor).toInt(),
                (parent.paddingRight * scaleFactor).toInt(), (parent.paddingBottom * scaleFactor).toInt())
        // Scale the margins, if any
        val layoutParams = parent.layoutParams as LinearLayout.LayoutParams?
        if (layoutParams != null) {
            layoutParams.leftMargin *= (layoutParams.leftMargin * scaleFactor).toInt()
            layoutParams.rightMargin = (layoutParams.rightMargin * scaleFactor).toInt()
            layoutParams.bottomMargin = (layoutParams.bottomMargin * scaleFactor).toInt()
            layoutParams.topMargin = (layoutParams.topMargin * scaleFactor).toInt()
            parent.layoutParams = layoutParams
        }
        // Scale the text size
        if (parent is TextView) {
            parent.setTextSize(TypedValue.COMPLEX_UNIT_PX, parent.textSize * scaleFactor)
            if (ApiHelper.apiLevel >= 16) {
                FRCRenderApi16.scaleShadow(parent, scaleFactor)
            }
        }
        // Scale all child views
        else if (parent is ViewGroup) {
            for (i in 0 until parent.childCount) {
                scaleViews(parent.getChildAt(i), scaleFactor)
            }
        }
    }

    /**
     * Draw the given view to a bitmap, set the bitmap on an ImageView, and
     * return a RemoteViews containing just this ImageView.
     *
     * @return a RemoteViews containing an ImageView which has a bitmap to which
     * the contents of the view parameter are drawn.
     */
    fun createRemoteViews(context: Context, view: View,
                          appWidgetManager: AppWidgetManager,
                          appWidgetId: Int,
                          @DimenRes defaultWidthResId: Int,
                          @DimenRes defaultHeightResId: Int): RemoteViews {
        val scaleFactor = getScaleFactor(context, appWidgetManager, appWidgetId, defaultWidthResId, defaultHeightResId)
        val width = scaleFactor * context.resources.getDimensionPixelSize(defaultWidthResId)
        val height = scaleFactor * context.resources.getDimensionPixelSize(defaultHeightResId)
        Log.v(TAG, "Creating widget of size " + width + "x" + height)
        val widthSpec = View.MeasureSpec.makeMeasureSpec(width.toInt(), View.MeasureSpec.EXACTLY)
        val heightSpec = View.MeasureSpec.makeMeasureSpec(height.toInt(), View.MeasureSpec.EXACTLY)

        view.measure(widthSpec, heightSpec)
        view.layout(0, 0, width.toInt() - 1, height.toInt() - 1)
        // Draw everything to a bitmap.
        val bitmap = Bitmap.createBitmap(width.toInt(), height.toInt(), Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)

        // Write that bitmap to the ImageView which will be our final view.
        val views = RemoteViews(context.packageName, R.layout.imageview)
        views.setImageViewBitmap(R.id.rootView, bitmap)
        return views
    }

    fun setBackgroundImage(context : Context,
                           view : View,
                           @DrawableRes backgroundDrawable : Int,
                           frenchDate : FrenchRevolutionaryCalendarDate) {
        val color = FRCDateUtils.getColor(context, frenchDate)
        view.setBackgroundResource(backgroundDrawable)
        val background = view.background
        background.setColorFilter(color, PorterDuff.Mode.MULTIPLY)
        @Suppress("DEPRECATION")
        view.setBackgroundDrawable(background)
    }

    fun setDetailedViewText(context : Context,
                            dayOfYearTextView : TextView,
                            timeTextView : TextView,
                            frenchDate : FrenchRevolutionaryCalendarDate) {
        val prefs = FRCPreferences.getInstance(context)
        if (prefs.isDayOfYearEnabled) {
            dayOfYearTextView.visibility = View.VISIBLE
            dayOfYearTextView.text = " " + frenchDate.objectOfTheDay + " "
        } else {
            dayOfYearTextView.visibility = View.GONE
        }

        if (prefs.isTimeEnabled) {
            timeTextView.visibility = View.VISIBLE
            timeTextView.text = java.lang.String.format(Locale.US, "%d:%02d", frenchDate.hour, frenchDate.minute)
        } else {
            timeTextView.visibility = View.GONE
        }
    }
}
