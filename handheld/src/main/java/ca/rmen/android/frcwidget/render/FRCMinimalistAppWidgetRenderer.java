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

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.TextView;

import java.util.Locale;

import ca.rmen.android.frccommon.Constants;
import ca.rmen.android.frccommon.FRCDateUtils;
import ca.rmen.android.frccommon.prefs.FRCPreferences;
import ca.rmen.android.frccommon.prefs.FRCPreferences.DetailedView;
import ca.rmen.android.frenchcalendar.R;
import ca.rmen.lfrc.FrenchRevolutionaryCalendarDate;

/**
 * Responsible for drawing the minimalist widget.
 * TODO try to share as much as logic as possible between the scroll widgets and the minimalist widgets.
 *
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
        tvWeekday.setText(" " + frenchDate.getWeekdayName() + " ");
        tvDate.setText(date);

        // Set the text fields for the time.
        final String timestamp;
        DetailedView detailedView = FRCPreferences.getInstance(context).getDetailedView();

        TextView tvTime = (TextView) view.findViewById(R.id.text_time);
        if (detailedView == DetailedView.NONE) {
            tvTime.setVisibility(View.GONE);
        } else {
            tvTime.setVisibility(View.VISIBLE);
            if (detailedView == DetailedView.TIME)
                timestamp = String.format(Locale.US, "%d:%02d", frenchDate.hour, frenchDate.minute);
            else
                timestamp = " " + frenchDate.getDayOfYear() + " ";
            tvTime.setText(timestamp);
        }

        // Scale the views.
        float defaultWidgetWidth = context.getResources().getDimension(R.dimen.minimalist_widget_width);
        float defaultWidgetHeight = context.getResources().getDimension(R.dimen.minimalist_widget_height);
        float scaleFactor = FRCRender.getScaleFactor(context, appWidgetManager, appWidgetId, defaultWidgetWidth, defaultWidgetHeight);
        Log.v(TAG, "render: scaleFactor=" + scaleFactor);

        setTextColors(context, tvWeekday, frenchDate);
        setTextColors(context, tvDate, frenchDate);
        setTextColors(context, tvTime, frenchDate);

        FRCRender.scaleViews(view, scaleFactor);

        // Render the views to a bitmap and return a RemoteViews containing this image.
        int width = (int) (scaleFactor * defaultWidgetWidth);
        int height = (int) (scaleFactor * defaultWidgetHeight);
        Log.v(TAG, "Creating widget of size " + width + "x" + height);
        return FRCRender.createRemoteViews(context, view, width, height);
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
