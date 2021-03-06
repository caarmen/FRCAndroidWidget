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
import android.view.View
import android.widget.RemoteViews
import android.widget.TextView
import ca.rmen.android.frccommon.Constants
import ca.rmen.android.frccommon.FRCDateUtils
import ca.rmen.android.frenchcalendar.R

/**
 * Responsible for drawing the scroll widgets.
 * @author calvarez
 *
 */
class FRCNarrowScrollAppWidgetRenderer : FRCAppWidgetRenderer {
    companion object {
        private val TAG = Constants.TAG + FRCNarrowScrollAppWidgetRenderer::class.java.simpleName
    }

    override fun render(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int): RemoteViews {
        Log.v(TAG, "render")

        val frenchDate = FRCDateUtils.getToday(context)

        // Create a view with the right scroll image as the background.
        val view = View.inflate(context, R.layout.appwidget_narrow, null)
        FRCRender.setBackgroundImage(context, view, R.drawable.vscroll_blank, frenchDate)

        // Set all the text fields for the date.
        // Add a space before and after the text: this font sometimes cuts off the last letter.
        val date = " " + frenchDate.dayOfMonth + " " + frenchDate.monthName + " "
        val tvYear = view.findViewById<TextView>(R.id.text_year)
        val tvDate = view.findViewById<TextView>(R.id.text_date)
        val tvWeekday = view.findViewById<TextView>(R.id.text_weekday)
        val tvDayOfYear = view.findViewById<TextView>(R.id.text_day_of_year)
        val tvTime = view.findViewById<TextView>(R.id.text_time)
        tvYear.text = " " + FRCDateUtils.formatNumber(context, frenchDate.year) + " "
        tvWeekday.text = frenchDate.weekdayName
        tvDate.text = date
        FRCRender.setDetailedViewText(context, tvDayOfYear, tvTime, frenchDate)

        Font.applyFont(context, view)
        TextViewSizing.fitTextViewsHorizontally(view, R.dimen.narrow_widget_text_width)
        TextViewSizing.fitTextViewsVertically(context,
                R.dimen.narrow_widget_height,
                R.dimen.narrow_top_bottom_margin,
                R.dimen.narrow_top_bottom_margin,
                tvYear, tvWeekday, tvDate, tvDayOfYear, tvTime)
        FRCRender.scaleWidget(context, view, appWidgetManager, appWidgetId,
                R.dimen.narrow_widget_width, R.dimen.narrow_widget_height)
        return FRCRender.createRemoteViews(context, view, appWidgetManager, appWidgetId, R.dimen.narrow_widget_width, R.dimen.narrow_widget_height)
    }

}
