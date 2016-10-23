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
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.DimenRes;
import android.support.annotation.DrawableRes;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.TextView;

import java.util.Locale;

import ca.rmen.android.frccommon.Constants;
import ca.rmen.android.frccommon.FRCDateUtils;
import ca.rmen.android.frccommon.prefs.FRCPreferences;
import ca.rmen.android.frenchcalendar.R;
import ca.rmen.lfrc.FrenchRevolutionaryCalendarDate;

/**
 * Provides methods used to render widgets.
 *
 * @author calvarez
 */
class FRCRender {

    private static final String TAG = Constants.TAG + FRCRender.class.getSimpleName();

    /**
     * Depending on our api level and whether the launcher provides us the max
     * widget size, we calculate how much we should scale the widget components
     * (TextViews) before we draw them to a bitmap, for the sharpest image.
     *
     * @return The views in the widget should be scaled by this much before
     * drawing them to a bitmap.
     */
    private static float getScaleFactor(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId, @DimenRes int defaultWidthResId, @DimenRes int defaultHeightResId) {
        float defaultWidgetWidth = context.getResources().getDimension(defaultWidthResId);
        float defaultWidgetHeight = context.getResources().getDimension(defaultHeightResId);
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

    static void scaleWidget(Context context,
                      View view,
                      AppWidgetManager appWidgetManager,
                      int appWidgetId,
                      @DimenRes int defaultWidthResId,
                      @DimenRes int defaultHeightResId,
                      @DimenRes int defaultTextWidthResId) {

        float scaleFactor = getScaleFactor(context, appWidgetManager, appWidgetId, defaultWidthResId, defaultHeightResId);
        scaleViews(view, scaleFactor);
        // Just in case the line with the month name is too long for the widget, we'll squeeze it so it fits.
        int textViewableWidth = (int) (scaleFactor * context.getResources().getDimensionPixelSize(defaultTextWidthResId));
        shrinkTextViews(view, textViewableWidth);
    }

    /**
     * Rescale the given parent view and all its child views. The following will
     * be rescaled: 1) text sizes, 2) padding, 3) margins.
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
            @SuppressWarnings("deprecation")
            int sdk = Integer.valueOf(Build.VERSION.SDK);
            if (sdk >= 16) {
                FRCRenderApi16.scaleShadow(textView, scaleFactor);
            }
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

    private static void shrinkTextViews(View parent, float maxWidth) {
        // Scale the text size
        if (parent instanceof TextView) {
            shrinkText((TextView) parent, maxWidth);
        }
        // Scale all child views
        else if (parent instanceof ViewGroup) {
            final ViewGroup parentGroup = (ViewGroup) parent;
            final int childCount = parentGroup.getChildCount();
            for (int i = 0; i < childCount; i++) {
                final View v = parentGroup.getChildAt(i);
                shrinkTextViews(v, maxWidth);
            }
        }
    }

    /**
     * If the given TextView's text is too long for the given max width, set the text size on the
     * TextView to a smaller value so it will fit the width.
     */
    private static void shrinkText(TextView textView, float maxWidth) {
        Paint paint = new Paint();
        float originalTextSize = textView.getTextSize();
        paint.setTextSize(originalTextSize);
        float actualWidth = paint.measureText(textView.getText().toString());
        if (actualWidth < maxWidth) return;
        float shrunkenTextSize = (maxWidth / actualWidth) * originalTextSize;
        Log.v(TAG, "Shrunk text size for '" + textView.getText() + "' from " + originalTextSize + "px to " + shrunkenTextSize + "px");
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, shrunkenTextSize);
    }

    /**
     * Draw the given view to a bitmap, set the bitmap on an ImageView, and
     * return a RemoteViews containing just this ImageView.
     *
     * @return a RemoteViews containing an ImageView which has a bitmap to which
     * the contents of the view parameter are drawn.
     */
    static RemoteViews createRemoteViews(Context context, View view,
                                         AppWidgetManager appWidgetManager,
                                         int appWidgetId,
                                         @DimenRes int defaultWidthResId,
                                         @DimenRes int defaultHeightResId) {
        float scaleFactor = getScaleFactor(context, appWidgetManager, appWidgetId, defaultWidthResId, defaultHeightResId);
        int width = (int) (scaleFactor * context.getResources().getDimensionPixelSize(defaultWidthResId));
        int height = (int) (scaleFactor * context.getResources().getDimensionPixelSize(defaultHeightResId));
        Log.v(TAG, "Creating widget of size " + width + "x" + height);
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

    static void setBackgroundImage(Context context,
                                   View view,
                                   @DrawableRes int backgroundDrawable,
                                   FrenchRevolutionaryCalendarDate frenchDate) {
        int color = FRCDateUtils.getColor(context, frenchDate);
        view.setBackgroundResource(backgroundDrawable);
        Drawable background = view.getBackground();
        background.setColorFilter(color, PorterDuff.Mode.MULTIPLY);
        //noinspection deprecation
        view.setBackgroundDrawable(background);
    }

    static void setDetailedViewText(Context context,
                                    TextView detailedViewTextView,
                                    FrenchRevolutionaryCalendarDate frenchDate) {

        FRCPreferences.DetailedView detailedView = FRCPreferences.getInstance(context).getDetailedView();
        if (detailedView == FRCPreferences.DetailedView.NONE) {
            detailedViewTextView.setVisibility(View.GONE);
        } else {
            final String detailedViewText;
            detailedViewTextView.setVisibility(View.VISIBLE);
            if (detailedView == FRCPreferences.DetailedView.TIME)
                detailedViewText = String.format(Locale.US, "%d:%02d", frenchDate.hour, frenchDate.minute);
            else
                detailedViewText = " " + frenchDate.getDayOfYear() + " ";
            detailedViewTextView.setText(detailedViewText);
        }
    }
}
