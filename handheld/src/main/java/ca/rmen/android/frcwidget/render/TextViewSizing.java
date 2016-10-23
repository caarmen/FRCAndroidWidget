/*
 * French Revolutionary Calendar Android Widget
 * Copyright (C) 2016 Carmen Alvarez
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

import android.content.Context;
import android.graphics.Paint;
import android.support.annotation.DimenRes;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import ca.rmen.android.frccommon.Constants;
import ca.rmen.android.frenchcalendar.BuildConfig;

/**
 * Provides methods used to size TextViews
 *
 * @author calvarez
 */
class TextViewSizing {

    private static final String TAG = Constants.TAG + TextViewSizing.class.getSimpleName();

    /**
     * Re-sizes the given text views so that they all fit vertically within the given bounds.
     * The text views will fit inside a height equal to the given height minus the given margins.
     */
    static void fitTextViewsVertically(Context context,
                                       @DimenRes int widgetHeightRes,
                                       @DimenRes int widgetTopMarginRes,
                                       @DimenRes int widgetBottomMarginRes,
                                       TextView... textViews) {
        float totalTextViewsAllowedHeight = (
                context.getResources().getDimensionPixelSize(widgetHeightRes)
                        - context.getResources().getDimensionPixelSize(widgetTopMarginRes))
                - context.getResources().getDimensionPixelSize(widgetBottomMarginRes);
        Log.v(TAG, String.format(Locale.US, "total height: (%s - %s - %s) = %s",
                context.getResources().getDimensionPixelSize(widgetHeightRes),
                context.getResources().getDimensionPixelSize(widgetTopMarginRes),
                context.getResources().getDimensionPixelSize(widgetBottomMarginRes),
                totalTextViewsAllowedHeight));
        fitTextViewsVertically(totalTextViewsAllowedHeight, textViews);
    }

    private static void fitTextViewsVertically(float totalHeight, TextView... textViews) {
        Log.v(TAG, String.format(Locale.US, "fitTextViewsVertically: totalHeight %s", totalHeight));
        logTextViewHeights(textViews);

        int totalTextViewsHeight = 0;
        for (TextView textView : textViews) {
            totalTextViewsHeight += getTextHeight(textView);
        }

        Log.v(TAG, String.format(Locale.US, "fitTextViewsVertically: totalTextViewsHeight %s", totalTextViewsHeight));

        if (totalTextViewsHeight > totalHeight) {
            // If the height of all our text views combined exceeds our max allowable height,
            // we need to scale down the text views in two passes.
            float minTextViewHeight = totalHeight / textViews.length;

            // Prepare the data for pass 2:
            Set<TextView> textViewsPass2 = new HashSet<>();
            float totalTextViewHeightPass2 = 0;
            float totalHeightPass2 = totalHeight;

            // Pass 1: resize text views according to the scaleFactor, but not smaller than they already are, and
            // not smaller than the min height.
            // Only text views which are still taller than the min height will go on to pass 2.
            float scaleFactor = totalHeight / totalTextViewsHeight;
            Log.v(TAG, "fitTextViewsVertically: pass 1, scale factor = " + scaleFactor);
            for (TextView textView : textViews) {
                int textViewHeight = getTextHeight(textView);
                float newTextViewHeight = scaleFactor * textViewHeight;
                float newTextSize = scaleFactor * textView.getTextSize();
                Log.v(TAG, String.format(Locale.US, "fitTextViewsVertically: suggested new height %s: '%s'",
                        newTextViewHeight,
                        textView.getText()));
                // This text view is already smaller than the min height, leave it alone
                // and don't include it in pass 2.
                if (textViewHeight < minTextViewHeight) {
                    totalHeightPass2 -= textViewHeight;
                    newTextSize = textView.getTextSize();
                }
                // The scale factor would make this text view smaller than the min height.
                // Only scale it down to the min height, and don't include it in pass 2.
                else if (newTextViewHeight < minTextViewHeight) {
                    totalHeightPass2 -= minTextViewHeight;
                    newTextSize = (minTextViewHeight / textViewHeight) * textView.getTextSize();
                }
                // This text view needs to be scaled down and needs to go on to pass 2.
                else {
                    totalTextViewHeightPass2 += newTextViewHeight;
                    textViewsPass2.add(textView);
                }
                textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, newTextSize);
            }

            // Pass 2: These are text views which had to be scaled down already, and may need
            // to be scaled down even further.
            if (totalTextViewHeightPass2 > totalHeightPass2) {
                resizeTextViews(totalHeightPass2 / totalTextViewHeightPass2, textViewsPass2);
            }

            logTextViewHeights(textViews);
        }
    }

    /**
     * Scale down each of the given TextViews by the given factor.
     */
    private static void resizeTextViews(float scaleFactor, Set<TextView> textViews) {
        Log.v(TAG, "resizeTextViews: scale factor = " + scaleFactor);
        for (TextView textView : textViews) {
            float newSize = scaleFactor * textView.getTextSize();
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, newSize);
        }
    }

    private static void logTextViewHeights(TextView... textViews) {
        // Log our final text view heights
        if (BuildConfig.DEBUG) {
            for (TextView textView : textViews) {
                int textViewHeight = getTextHeight(textView);
                Log.v(TAG, String.format(Locale.US, "height %s: '%s'", textViewHeight, textView.getText()));
            }
        }
    }

    private static int getTextHeight(TextView textView) {
        TextPaint paint = new TextPaint();
        paint.setTypeface(textView.getTypeface());
        paint.setTextSize(textView.getTextSize());
        int width = (int) paint.measureText(textView.getText().toString());
        StaticLayout layout = new StaticLayout(textView.getText(), paint, width, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0f, true);
        return layout.getHeight();
    }

    /**
     * Processes every text view inside the given parent view: each text view will be resized down
     * if necessary, to fit within the given width.
     */
    static void fitTextViewsHorizontally(View parent, @DimenRes int maxWidthResId) {
        // Scale the text size
        if (parent instanceof TextView) {
            fitTextViewHorizontally((TextView) parent, maxWidthResId);
        }
        // Scale all child views
        else if (parent instanceof ViewGroup) {
            final ViewGroup parentGroup = (ViewGroup) parent;
            final int childCount = parentGroup.getChildCount();
            for (int i = 0; i < childCount; i++) {
                final View v = parentGroup.getChildAt(i);
                fitTextViewsHorizontally(v, maxWidthResId);
            }
        }
    }

    /**
     * If the given TextView's text is too long for the given max width, set the text size on the
     * TextView to a smaller value so it will fit the width.
     */
    private static void fitTextViewHorizontally(TextView textView, @DimenRes int maxWidthResId) {
        float maxWidth = textView.getContext().getResources().getDimensionPixelSize(maxWidthResId);
        Paint paint = new Paint();
        float originalTextSize = textView.getTextSize();
        paint.setTextSize(originalTextSize);
        float actualWidth = paint.measureText(textView.getText().toString());
        if (actualWidth < maxWidth) return;
        float shrunkenTextSize = (maxWidth / actualWidth) * originalTextSize;
        Log.v(TAG, "Shrunk text size for '" + textView.getText() + "' from " + originalTextSize + "px to " + shrunkenTextSize + "px");
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, shrunkenTextSize);
    }

}
