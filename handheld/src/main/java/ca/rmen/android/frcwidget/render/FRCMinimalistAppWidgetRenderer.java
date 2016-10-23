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
import android.util.TypedValue;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.TextView;


import ca.rmen.android.frccommon.Constants;
import ca.rmen.android.frccommon.FRCDateUtils;
import ca.rmen.android.frenchcalendar.R;
import ca.rmen.lfrc.FrenchRevolutionaryCalendarDate;

/**
 * Responsible for drawing the minimalist widget.
 * @author calvarez
 */
class FRCMinimalistAppWidgetRenderer implements FRCAppWidgetRenderer {
    private static final String TAG = Constants.TAG + FRCMinimalistAppWidgetRenderer.class.getSimpleName();

    public RemoteViews render(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        Log.v(TAG, "render");

        View view = View.inflate(context, R.layout.appwidget_minimalist, null);

        // Set all the text fields for the date
        FrenchRevolutionaryCalendarDate frenchDate = FRCDateUtils.getToday(context);
        String date = " " + frenchDate.dayOfMonth + " " + frenchDate.getMonthName() + " " + frenchDate.year + " ";
        TextView tvWeekday = (TextView) view.findViewById(R.id.text_weekday);
        TextView tvDate = (TextView) view.findViewById(R.id.text_date);
        TextView tvTime = (TextView) view.findViewById(R.id.text_time);
        tvWeekday.setText(" " + frenchDate.getWeekdayName() + " ");
        tvDate.setText(date);
        FRCRender.setDetailedViewText(context, tvTime, frenchDate);

        setTextColors(context, tvWeekday, frenchDate);
        setTextColors(context, tvDate, frenchDate);
        setTextColors(context, tvTime, frenchDate);

        TextViewSizing.fitTextViewsHorizontally(view, R.dimen.minimalist_widget_width);

        FRCRender.scaleWidget(context, view, appWidgetManager, appWidgetId,
                R.dimen.minimalist_widget_width, R.dimen.minimalist_widget_height);
        Font.applyFont(context, view);

        return FRCRender.createRemoteViews(context, view, appWidgetManager, appWidgetId, R.dimen.minimalist_widget_width, R.dimen.minimalist_widget_height);
    }

    /**
     * Set the color of the text according to the preference or the current month.
     * Set the shadow on the text.
     */
    private static void setTextColors(Context context, TextView textView, FrenchRevolutionaryCalendarDate frenchDate) {
        int color = FRCDateUtils.getColor(context, frenchDate);
        textView.setTextColor(color);

        // Set the shadow programmatically.  In xml we can only specify pixels.
        // Programmatically we can specify the shadow in dp.
        float shadowDx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1f, context.getResources().getDisplayMetrics());
        float shadowDy = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1f, context.getResources().getDisplayMetrics());
        float shadowRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0.5f, context.getResources().getDisplayMetrics());
        textView.setShadowLayer(shadowRadius, shadowDx, shadowDy, 0xFF000000);
    }

}
