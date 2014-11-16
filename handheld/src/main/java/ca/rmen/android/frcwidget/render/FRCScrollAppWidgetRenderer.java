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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.TextView;
import ca.rmen.android.frccommon.Constants;
import ca.rmen.android.frccommon.FRCDateUtils;
import ca.rmen.android.frccommon.prefs.FRCPreferences;
import ca.rmen.android.frccommon.prefs.FRCPreferences.DetailedView;
import ca.rmen.android.frenchcalendar.R;
import ca.rmen.lfrc.FrenchRevolutionaryCalendarDate;

/**
 * Responsible for drawing the scroll widgets.
 * TODO try to share as much as logic as possible between the scroll widgets and the minimalist widgets.
 * 
 * @author calvarez
 * 
 */
class FRCScrollAppWidgetRenderer implements FRCAppWidgetRenderer {
    private static final String TAG = Constants.TAG + FRCScrollAppWidgetRenderer.class.getSimpleName();
    private final FRCScrollAppWidgetRenderParams mParams;

    FRCScrollAppWidgetRenderer(FRCScrollAppWidgetRenderParams params) {
    	mParams = params;
    }

    public RemoteViews render(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        Log.v(TAG, "render");

        float defaultWidgetWidth = context.getResources().getDimension(mParams.widthResourceId);
        float defaultWidgetHeight= context.getResources().getDimension(mParams.heightResourceId);

        float scaleFactor = FRCRender.getScaleFactor(context, appWidgetManager, appWidgetId, defaultWidgetWidth, defaultWidgetHeight);

        FrenchRevolutionaryCalendarDate frenchDate = FRCDateUtils.getToday(context);

        // Create a view with the right scroll image as the background.
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(mParams.layoutResourceId, null, false);
        view.setBackgroundResource(mParams.scrollResourceIds[frenchDate.month - 1]);
        int width = (int) (scaleFactor * context.getResources().getDimensionPixelSize(mParams.widthResourceId));
        int height = (int) (scaleFactor * context.getResources().getDimensionPixelSize(mParams.heightResourceId));
        Log.v(TAG, "Creating widget of size " + width + "x" + height);

        // Set all the text fields for the date
        ((TextView) view.findViewById(R.id.text_year)).setText(String.valueOf(frenchDate.year));
        ((TextView) view.findViewById(R.id.text_dayofmonth)).setText(String.valueOf(frenchDate.dayOfMonth));
        ((TextView) view.findViewById(R.id.text_weekday)).setText(frenchDate.getWeekdayName());
        ((TextView) view.findViewById(R.id.text_month)).setText(frenchDate.getMonthName());

        // Set the text fields for the time.
        String timestamp = null;
        DetailedView detailedView = FRCPreferences.getInstance(context).getDetailedView();

        TextView timeView = (TextView) view.findViewById(R.id.text_time);
        if (detailedView == DetailedView.NONE) {
            timeView.setVisibility(View.GONE);
            timestamp = "";
        } else {
            timeView.setVisibility(View.VISIBLE);
            if (detailedView == DetailedView.TIME) timestamp = String.format(Locale.US, "%d:%02d", frenchDate.hour, frenchDate.minute);
            else
                timestamp = frenchDate.getDayOfYear();
        }
        ((TextView) view.findViewById(R.id.text_time)).setText(timestamp);

        FRCRender.scaleViews(view, scaleFactor);
        Font.applyFont(context, view);
        int widthSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
        int heightSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);

        view.measure(widthSpec, heightSpec);
        view.layout(0, 0, width - 1, height - 1);

        // Just in case the line with the month name is too long for the widget, we'll squeeze it so it fits.
        int textViewableWidth = (int) (scaleFactor * context.getResources().getDimensionPixelSize(mParams.textViewableWidthResourceId));
        squeezeMonthLine(context, view, textViewableWidth);
        squeezeTextView(context, timeView, textViewableWidth);
        return FRCRender.createRemoteViews(context, view, width, height);
    }

    private static void squeezeMonthLine(Context context, View view, int textViewableWidth) {
        TextView dateView = (TextView) view.findViewById(R.id.text_dayofmonth);
        TextView monthView = (TextView) view.findViewById(R.id.text_month);
        LinearLayout monthLine = (LinearLayout) monthView.getParent();
        TextView yearView = (TextView) monthLine.findViewById(R.id.text_year);

        int textWidth = dateView.getWidth() + monthView.getWidth() + (yearView == null ? 0 : yearView.getWidth());

        if (textWidth > textViewableWidth) {
            float squeezeFactor = (float) textViewableWidth / textWidth;

            Log.v(TAG, "SqueezeFactor: " + squeezeFactor);
            dateView.setTextScaleX(squeezeFactor);
            monthView.setTextScaleX(squeezeFactor);
            if (yearView != null) yearView.setTextScaleX(squeezeFactor);
        }
    }

    private static void squeezeTextView(Context context, TextView view, int textViewableWidth) {
        int textWidth = view.getWidth();
        if (textWidth > textViewableWidth) {
            float squeezeFactor = (float) textViewableWidth / textWidth;
            view.setTextScaleX(squeezeFactor);
        }
    }


}
