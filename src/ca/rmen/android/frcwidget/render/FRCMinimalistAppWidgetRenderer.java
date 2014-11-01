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

import java.util.Locale;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.RemoteViews;
import ca.rmen.android.frcwidget.Constants;
import ca.rmen.android.frcwidget.FRCDateUtils;
import ca.rmen.android.frcwidget.prefs.FRCPreferences;
import ca.rmen.android.frcwidget.prefs.FRCPreferences.DetailedView;
import ca.rmen.android.frenchcalendar.R;
import ca.rmen.lfrc.FrenchRevolutionaryCalendarDate;

/**
 * Responsible for drawing the minimalist widget.
 * 
 * @author calvarez
 * 
 */
public class FRCMinimalistAppWidgetRenderer implements FRCAppWidgetRenderer {
    private static final String TAG = Constants.TAG + FRCMinimalistAppWidgetRenderer.class.getSimpleName();

    public RemoteViews render(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
    	Log.v(TAG, "render");

        FrenchRevolutionaryCalendarDate frenchDate = FRCDateUtils.getToday(context);
        float scaleFactor = 1.0f;
        float defaultWidgetWidth = context.getResources().getDimension(R.dimen.minimalist_widget_width);
        float defaultWidgetHeight= context.getResources().getDimension(R.dimen.minimalist_widget_height);
        @SuppressWarnings("deprecation")
        int sdk = Integer.valueOf(Build.VERSION.SDK);
        if (sdk >= 16) {
            scaleFactor = FRCScrollRenderApi16.getScaleFactor(context, appWidgetManager, appWidgetId, defaultWidgetWidth, defaultWidgetHeight);
        } else if (sdk >= 13) {
            scaleFactor = FRCRenderApi13.getMaxScaleFactor(context, defaultWidgetWidth, defaultWidgetHeight);
        }
        Log.v(TAG, "render: scaleFactor=" + scaleFactor);

        // Create a view with the right scroll image as the background.
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.appwidget_minimalist);

        // Set all the text fields for the date
        String date = frenchDate.dayOfMonth + " " + frenchDate.getMonthName() + " " +  frenchDate.year;
        views.setTextViewText(R.id.text_weekday, frenchDate.getWeekdayName());
        views.setTextViewText(R.id.text_date, date);
        views.setTextViewTextSize(R.id.text_weekday, TypedValue.COMPLEX_UNIT_SP, scaleFactor*context.getResources().getDimension(R.dimen.minimalist_weekday_textsize));
        views.setTextViewTextSize(R.id.text_date, TypedValue.COMPLEX_UNIT_SP, scaleFactor*context.getResources().getDimension(R.dimen.minimalist_month_textsize));
        views.setTextViewTextSize(R.id.text_time, TypedValue.COMPLEX_UNIT_SP, scaleFactor*context.getResources().getDimension(R.dimen.minimalist_weekday_textsize));

        // Set the text fields for the time.
        String timestamp = null;
        DetailedView detailedView = FRCPreferences.getInstance(context).getDetailedView();

        if (detailedView == DetailedView.NONE) {
        	views.setViewVisibility(R.id.text_time, View.GONE);
            timestamp = "";
        } else {
        	views.setViewVisibility(R.id.text_time, View.VISIBLE);
            if (detailedView == DetailedView.TIME) timestamp = String.format(Locale.US, "%d:%02d", frenchDate.hour, frenchDate.minute);
            else
                timestamp = frenchDate.getDayOfYear();
        }
        views.setTextViewText(R.id.text_time, timestamp);

        return views;
    }

}
