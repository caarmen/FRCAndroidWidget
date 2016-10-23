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

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.TextView;

import ca.rmen.android.frccommon.Constants;
import ca.rmen.android.frccommon.FRCDateUtils;
import ca.rmen.android.frenchcalendar.R;
import ca.rmen.lfrc.FrenchRevolutionaryCalendarDate;

/**
 * Responsible for drawing the scroll widgets.
 * @author calvarez
 */
class FRCWideScrollAppWidgetRenderer implements FRCAppWidgetRenderer {
    private static final String TAG = Constants.TAG + FRCWideScrollAppWidgetRenderer.class.getSimpleName();

    FRCWideScrollAppWidgetRenderer() {
    }

    public RemoteViews render(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        Log.v(TAG, "render");

        FrenchRevolutionaryCalendarDate frenchDate = FRCDateUtils.getToday(context);

        // Create a view with the right scroll image as the background.
        View view = View.inflate(context, R.layout.appwidget_wide, null);
        FRCRender.setBackgroundImage(context, view, R.drawable.hscroll_blank, frenchDate);

        // Set all the text fields for the date.
        // Add a space before and after the text: this font sometimes cuts off the last letter.
        String date = " " + frenchDate.dayOfMonth + " " + frenchDate.getMonthName() + " " + frenchDate.year + " ";
        TextView tvDate = (TextView) view.findViewById(R.id.text_date);
        TextView tvWeekday = (TextView) view.findViewById(R.id.text_weekday);
        TextView tvDetail = (TextView) view.findViewById(R.id.text_time);
        tvDate.setText(date);
        tvWeekday.setText(frenchDate.getWeekdayName());
        FRCRender.setDetailedViewText(context, tvDetail, frenchDate);

        Font.applyFont(context, view);
        TextViewSizing.fitTextViewsHorizontally(view, R.dimen.wide_widget_text_width);
        TextViewSizing.fitTextViewsVertically(context,
                R.dimen.wide_widget_height,
                R.dimen.wide_top_margin,
                R.dimen.wide_bottom_margin,
                tvWeekday, tvDate, tvDetail);

        FRCRender.scaleWidget(context, view, appWidgetManager, appWidgetId,
                R.dimen.wide_widget_width, R.dimen.wide_widget_height);

        return FRCRender.createRemoteViews(context, view, appWidgetManager, appWidgetId, R.dimen.wide_widget_width, R.dimen.wide_widget_height);
    }



}
