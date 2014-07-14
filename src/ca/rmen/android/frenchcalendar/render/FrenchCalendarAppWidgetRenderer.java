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
package ca.rmen.android.frenchcalendar.render;

import java.util.GregorianCalendar;
import java.util.Locale;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.TextView;
import ca.rmen.android.frenchcalendar.Constants;
import ca.rmen.android.frenchcalendar.R;
import ca.rmen.android.frenchcalendar.prefs.FrenchCalendarPrefs;
import ca.rmen.lfrc.FrenchRevolutionaryCalendar;
import ca.rmen.lfrc.FrenchRevolutionaryCalendarDate;

/**
 * Responsible for drawing the widgets.
 * 
 * @author calvarez
 * 
 */
public class FrenchCalendarAppWidgetRenderer {
    private static final String TAG = Constants.TAG + FrenchCalendarAppWidgetRenderer.class.getSimpleName();

    public static RemoteViews render(Context context, FrenchCalendarAppWidgetRenderParams params) {
        Log.v(TAG, "render");

        // Get the current timestamp in the French revolutionary calendar.
        GregorianCalendar now = new GregorianCalendar();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String methodPrefStr = sharedPreferences.getString(FrenchCalendarPrefs.PREF_METHOD, "0");
        int mode = Integer.parseInt(methodPrefStr);
        FrenchRevolutionaryCalendar frcal = new FrenchRevolutionaryCalendar(mode);
        FrenchRevolutionaryCalendarDate frenchDate = frcal.getDate(now);

        // Create a view with the right scroll image as the background.
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(params.layoutResourceId, null, false);
        view.setBackgroundResource(params.scrollResourceIds[frenchDate.month - 1]);
        int width = context.getResources().getDimensionPixelSize(params.widthResourceId);
        int height = context.getResources().getDimensionPixelSize(params.heightResourceId);

        // Set all the text fields for the date
        ((TextView) view.findViewById(R.id.text_year)).setText(String.valueOf(frenchDate.year));
        ((TextView) view.findViewById(R.id.text_dayofmonth)).setText(String.valueOf(frenchDate.day));
        CharSequence weekdayLabel = getLabel(context, R.array.weekdays, frenchDate.getDayInWeek() - 1);
        CharSequence monthLabel = getLabel(context, R.array.months, frenchDate.month - 1);
        ((TextView) view.findViewById(R.id.text_weekday)).setText(weekdayLabel);
        ((TextView) view.findViewById(R.id.text_month)).setText(monthLabel);

        // Set the text fields for the time.
        String frequencyPrefStr = sharedPreferences.getString(FrenchCalendarPrefs.PREF_FREQUENCY, FrenchCalendarPrefs.FREQUENCY_MINUTES);

        String timestamp = null;
        TextView timeView = (TextView) view.findViewById(R.id.text_time);
        if (FrenchCalendarPrefs.FREQUENCY_SECONDS.equals(frequencyPrefStr)) {
            timeView.setVisibility(View.VISIBLE);
            timestamp = String.format(Locale.US, "%d:%02d:%02d", frenchDate.hour, frenchDate.minute, frenchDate.second);
        } else if (FrenchCalendarPrefs.FREQUENCY_MINUTES.equals(frequencyPrefStr)) {
            timeView.setVisibility(View.VISIBLE);
            timestamp = String.format(Locale.US, "%d:%02d", frenchDate.hour, frenchDate.minute);
        } else {
            timeView.setVisibility(View.GONE);
            timestamp = "";
        }
        ((TextView) view.findViewById(R.id.text_time)).setText(timestamp);

        Font.applyFont(context, view);

        view.measure(width, height);
        view.layout(0, 0, width - 1, height - 1);

        // Just in case the line with the month name is too long for the widget, we'll squeeze it so it fits.
        squeezeMonthLine(context, view, params.textViewableWidthResourceId);
        view.measure(width, height);
        view.layout(0, 0, width - 1, height - 1);

        // Draw everything to a bitmap.
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        // Write that bitmap to the ImageView which will be our final view.
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.imageview);
        views.setImageViewBitmap(R.id.imageView1, bitmap);

        return views;
    }

    private static CharSequence getLabel(Context context, int arrayResource, int index) {
        CharSequence[] labels = context.getResources().getTextArray(arrayResource);
        if (index >= 0 && index < labels.length) return labels[index];
        return "";
    }

    private static void squeezeMonthLine(Context context, View view, int textViewableWidthResourceId) {
        TextView dateView = (TextView) view.findViewById(R.id.text_dayofmonth);
        TextView monthView = (TextView) view.findViewById(R.id.text_month);
        LinearLayout monthLine = (LinearLayout) monthView.getParent();
        TextView yearView = (TextView) monthLine.findViewById(R.id.text_year);

        int textWidth = dateView.getWidth() + monthView.getWidth() + (yearView == null ? 0 : yearView.getWidth());
        int textViewableWidth = context.getResources().getDimensionPixelSize(textViewableWidthResourceId);

        if (textWidth > textViewableWidth) {
            float squeezeFactor = (float) textViewableWidth / textWidth;

            Log.v(TAG, "SqueezeFactor: " + squeezeFactor);
            dateView.setTextScaleX(squeezeFactor);
            monthView.setTextScaleX(squeezeFactor);
            if (yearView != null) yearView.setTextScaleX(squeezeFactor);
        }
    }

}
