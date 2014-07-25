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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.TextView;
import ca.rmen.android.frcwidget.Constants;
import ca.rmen.android.frcwidget.FRCDateUtils;
import ca.rmen.android.frcwidget.prefs.FRCPreferences;
import ca.rmen.android.frcwidget.prefs.FRCPreferences.DetailedView;
import ca.rmen.android.frenchcalendar.R;
import ca.rmen.lfrc.FrenchRevolutionaryCalendarDate;

/**
 * Responsible for drawing the widgets.
 * 
 * @author calvarez
 * 
 */
public class FRCAppWidgetRenderer {
    private static final String TAG = Constants.TAG + FRCAppWidgetRenderer.class.getSimpleName();

    public static RemoteViews render(Context context, FRCAppWidgetRenderParams params, float scaleFactor) {
        Log.v(TAG, "render: scaleFactor =" + scaleFactor);

        FrenchRevolutionaryCalendarDate frenchDate = FRCDateUtils.getToday(context);

        // Create a view with the right scroll image as the background.
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(params.layoutResourceId, null, false);
        view.setBackgroundResource(params.scrollResourceIds[frenchDate.month - 1]);
        int width = (int) (scaleFactor * context.getResources().getDimensionPixelSize(params.widthResourceId));
        int height = (int) (scaleFactor * context.getResources().getDimensionPixelSize(params.heightResourceId));
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

        scaleViews(view, scaleFactor);
        Font.applyFont(context, view);
        int widthSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
        int heightSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);

        view.measure(widthSpec, heightSpec);
        view.layout(0, 0, width - 1, height - 1);

        // Just in case the line with the month name is too long for the widget, we'll squeeze it so it fits.
        int textViewableWidth = (int) (scaleFactor * context.getResources().getDimensionPixelSize(params.textViewableWidthResourceId));
        squeezeMonthLine(context, view, textViewableWidth);
        squeezeTextView(context, timeView, textViewableWidth);
        view.measure(widthSpec, heightSpec);
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

    /**
     * Rescale the given parent view and all its child views. The following will be rescaled:
     * 1) text sizes, 2) padding, 3) margins.
     */
    private static void scaleViews(View parent, float scaleFactor) {

        // Scale the padding
        parent.setPadding((int) (parent.getPaddingLeft() * scaleFactor), (int) (parent.getPaddingTop() * scaleFactor),
                (int) (parent.getPaddingRight() * scaleFactor), (int) (parent.getPaddingBottom() * scaleFactor));
        // Scale the margins, if any
        LinearLayout.LayoutParams layoutParams = (android.widget.LinearLayout.LayoutParams) parent.getLayoutParams();
        if (layoutParams != null) {
            layoutParams.leftMargin *= scaleFactor;
            layoutParams.rightMargin *= scaleFactor;
            layoutParams.bottomMargin *= scaleFactor;
            layoutParams.topMargin *= scaleFactor;
            parent.setLayoutParams(layoutParams);
        }
        // Scale the text size
        if (parent instanceof TextView) {
            TextView textView = (TextView) parent;
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textView.getTextSize() * scaleFactor);
        }
        // Scale all child views
        else if (parent instanceof ViewGroup) {
            final ViewGroup parentGroup = (ViewGroup) parent;
            final int childCount = parentGroup.getChildCount();
            for (int i = 0; i < childCount; i++) {
                final View v = parentGroup.getChildAt(i);
                scaleViews(v, scaleFactor);
            }
        }
    }

}
