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
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.RemoteViews
import android.widget.TextView
import ca.rmen.android.frccommon.Constants
import ca.rmen.android.frccommon.FRCDateUtils
import ca.rmen.android.frenchcalendar.R
import ca.rmen.lfrc.FrenchRevolutionaryCalendarDate

/**
 * Responsible for drawing the minimalist widget.
 * @author calvarez
 */
class FRCMinimalistAppWidgetRenderer : FRCAppWidgetRenderer {
    companion object {
        private val TAG = Constants.TAG + FRCMinimalistAppWidgetRenderer::class.java.simpleName
    }

    override fun render(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int): RemoteViews {
        Log.v(TAG, "render")

        val view = View.inflate(context, R.layout.appwidget_minimalist, null)

        // Set all the text fields for the date
        val frenchDate = FRCDateUtils.getToday(context)
        val date = " " + frenchDate.dayOfMonth + " " + frenchDate.monthName + " " + FRCDateUtils.formatNumber(context, frenchDate.year) + " "
        val tvWeekday = view.findViewById<TextView>(R.id.text_weekday)
        val tvDate = view.findViewById<TextView>(R.id.text_date)
        val tvDayOfYear = view.findViewById<TextView>(R.id.text_day_of_year)
        val tvTime = view.findViewById<TextView>(R.id.text_time)
        tvWeekday.text = " " + frenchDate.weekdayName + " "
        tvDate.text = date
        FRCRender.setDetailedViewText(context, tvDayOfYear, tvTime, frenchDate)

        setTextColors(context, tvWeekday, frenchDate)
        setTextColors(context, tvDate, frenchDate)
        setTextColors(context, tvDayOfYear, frenchDate)
        setTextColors(context, tvTime, frenchDate)

        TextViewSizing.fitTextViewsHorizontally(view, R.dimen.minimalist_widget_width)

        FRCRender.scaleWidget(context, view, appWidgetManager, appWidgetId, R.dimen.minimalist_widget_width, R.dimen.minimalist_widget_height)
        Font.applyFont(context, view)
        return FRCRender.createRemoteViews(context, view, appWidgetManager, appWidgetId, R.dimen.minimalist_widget_width, R.dimen.minimalist_widget_height)
    }

    /**
     * Set the color of the text according to the preference or the current month.
     * Set the shadow on the text.
     */
    private fun setTextColors(context: Context, textView: TextView, frenchDate: FrenchRevolutionaryCalendarDate) {
        val color = FRCDateUtils.getColor(context, frenchDate)
        textView.setTextColor(color)

        // Set the shadow programmatically.  In xml we can only specify pixels.
        // Programmatically we can specify the shadow in dp.
        val shadowDx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1f, context.resources.displayMetrics)
        val shadowDy = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1f, context.resources.displayMetrics)
        val shadowRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0.5f, context.resources.displayMetrics)
        textView.setShadowLayer(shadowRadius, shadowDx, shadowDy, 0xff000000.toInt())
    }
}
