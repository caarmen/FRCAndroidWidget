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
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Build;
import android.util.TypedValue;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.TextView;
import ca.rmen.android.frenchcalendar.R;

/**
 * Provides methods used to render widgets.
 * 
 * @author calvarez
 */
public class FRCRender {

    /**
     * Depending on our api level and whether the launcher provides us the max
     * widget size, we calculate how much we should scale the widget components
     * (TextViews) before we draw them to a bitmap, for the sharpest image.
     * 
     * @return The views in the widget should be scaled by this much before
     *         drawing them to a bitmap.
     */
    static float getScaleFactor(Context context, AppWidgetManager appWidgetManager, int appWidgetId, float defaultWidgetWidth, float defaultWidgetHeight) {
        float scaleFactor = 1.0f;
        @SuppressWarnings("deprecation")
        int sdk = Integer.valueOf(Build.VERSION.SDK);
        if (sdk >= 16) {
            scaleFactor = FRCRenderApi16.getScaleFactor(context, appWidgetManager, appWidgetId, defaultWidgetWidth, defaultWidgetHeight);
        } else if (sdk >= 13) {
            scaleFactor = FRCRenderApi13.getMaxScaleFactor(context, defaultWidgetWidth, defaultWidgetHeight);
        }
        return scaleFactor;
    }

    /**
     * Rescale the given parent view and all its child views. The following will
     * be rescaled: 1) text sizes, 2) padding, 3) margins.
     */
    static void scaleViews(View parent, float scaleFactor) {

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

    /**
     * Draw the given view to a bitmap, set the bitmap on an ImageView, and
     * return a RemoteViews containing just this ImageView.
     * 
     * @return a RemoteViews containing an ImageView which has a bitmap to which
     *         the contents of the view parameter are drawn.
     */
    static RemoteViews createRemoteViews(Context context, View view, int width, int height) {
        int widthSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
        int heightSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);

        view.measure(widthSpec, heightSpec);
        view.layout(0, 0, width - 1, height - 1);
        // Draw everything to a bitmap.
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        // Write that bitmap to the ImageView which will be our final view.
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.imageview);
        views.setImageViewBitmap(R.id.rootView, bitmap);
        return views;

    }
}
