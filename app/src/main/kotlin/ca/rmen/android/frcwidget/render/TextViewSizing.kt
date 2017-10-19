/*
 * French Revolutionary Calendar Android Widget
 * Copyright (C) 2016-2017 Carmen Alvarez
 *
 * This program is free software you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program if not, see <http://www.gnu.org/licenses/>.
 */

package ca.rmen.android.frcwidget.render

import android.content.Context
import android.graphics.Paint
import android.support.annotation.DimenRes
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import ca.rmen.android.frccommon.Constants
import ca.rmen.android.frenchcalendar.BuildConfig

/**
 * Provides methods used to size TextViews
 *
 * @author calvarez
 */
object TextViewSizing {
    private val TAG = Constants.TAG + TextViewSizing::class.java.simpleName

    /**
     * Re-sizes the given text views so that they all fit vertically within the given bounds.
     * The text views will fit inside a height equal to the given height minus the given margins.
     */
    fun fitTextViewsVertically(context: Context,
                               @DimenRes widgetHeightRes: Int,
                               @DimenRes widgetTopMarginRes: Int,
                               @DimenRes widgetBottomMarginRes: Int,
                               vararg textViews: TextView) {
        val totalTextViewsAllowedHeight = ((
                context.resources.getDimensionPixelSize(widgetHeightRes)
                        - context.resources.getDimensionPixelSize(widgetTopMarginRes))
                - context.resources.getDimensionPixelSize(widgetBottomMarginRes)).toFloat()
        Log.v(TAG, "total height: (" +
                context.resources.getDimensionPixelSize(widgetHeightRes)
                + " - " +
                context.resources.getDimensionPixelSize(widgetTopMarginRes)
                + " - " +
                context.resources.getDimensionPixelSize(widgetBottomMarginRes)
                + ") = " + totalTextViewsAllowedHeight)
        fitTextViewsVertically(totalTextViewsAllowedHeight, Array(textViews.size) { textViews[it] })
    }

    private fun fitTextViewsVertically(totalHeight: Float, textViews: Array<TextView>) {
        Log.v(TAG, "fitTextViewsVertically: totalHeight " + totalHeight)
        logTextViewHeights(textViews)
        var totalTextViewsHeight = 0
        textViews.forEach { textView ->
            if (textView.visibility == View.VISIBLE) {
                totalTextViewsHeight += getTextHeight(textView)
            }
        }

        Log.v(TAG, "fitTextViewsVertically: totalTextViewsHeight " + totalTextViewsHeight)

        if (totalTextViewsHeight > totalHeight) {
            // If the height of all our text views combined exceeds our max allowable height,
            // we need to scale down the text views in two passes.
            val minTextViewHeight = totalHeight / textViews.size

            // Prepare the data for pass 2:
            val textViewsPass2 = HashSet<TextView>()
            var totalTextViewHeightPass2 = 0.toFloat()
            var totalHeightPass2 = totalHeight

            // Pass 1: resize text views according to the scaleFactor, but not smaller than they already are, and
            // not smaller than the min height.
            // Only text views which are still taller than the min height will go on to pass 2.
            val scaleFactor = totalHeight / totalTextViewsHeight
            Log.v(TAG, "fitTextViewsVertically: pass 1, scale factor = " + scaleFactor)
            textViews.forEach { textView ->
                if (textView.visibility == View.VISIBLE) {
                    val textViewHeight = getTextHeight(textView)
                    val newTextViewHeight = (scaleFactor * textViewHeight)
                    var newTextSize = scaleFactor * textView.textSize
                    Log.v(TAG, "fitTextViewsVertically: suggested new height " + newTextViewHeight + ": '" + textView.text + "'")
                    // This text view is already smaller than the min height, leave it alone
                    // and don't include it in pass 2.
                    when {
                        textViewHeight < minTextViewHeight -> {
                            totalHeightPass2 -= textViewHeight
                            newTextSize = textView.textSize
                        }
                    // The scale factor would make this text view smaller than the min height.
                    // Only scale it down to the min height, and don't include it in pass 2.
                        newTextViewHeight < minTextViewHeight -> {
                            totalHeightPass2 -= minTextViewHeight
                            newTextSize = (minTextViewHeight / textViewHeight) * textView.textSize
                        }
                    // This text view needs to be scaled down and needs to go on to pass 2.
                        else -> {
                            totalTextViewHeightPass2 += newTextViewHeight
                            textViewsPass2.add(textView)
                        }
                    }
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, newTextSize)
                }
            }

            // Pass 2: These are text views which had to be scaled down already, and may need
            // to be scaled down even further.
            if (totalTextViewHeightPass2 > totalHeightPass2) {
                resizeTextViews(totalHeightPass2 / totalTextViewHeightPass2, textViewsPass2)
            }

            logTextViewHeights(textViews)
        }
    }

    /**
     * Scale down each of the given TextViews by the given factor.
     */
    private fun resizeTextViews(scaleFactor: Float, textViews: Set<TextView>) {
        Log.v(TAG, "resizeTextViews: scale factor = " + scaleFactor)
        textViews.forEach { textView ->
            val newSize = scaleFactor * textView.textSize
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, newSize)
        }
    }

    private fun logTextViewHeights(textViews: Array<TextView>) {
        // Log our final text view heights
        if (BuildConfig.DEBUG) {
            textViews.forEach { textView ->
                val textViewHeight = getTextHeight(textView)
                Log.v(TAG, "height " + textViewHeight + ": '" + textView.text + "'")
            }
        }
    }

    private fun getTextHeight(textView: TextView): Int {
        val paint = TextPaint()
        paint.typeface = textView.typeface
        paint.textSize = textView.textSize
        val width = paint.measureText(textView.text.toString()).toInt()
        val layout = StaticLayout(textView.text, paint, width, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0f, true)
        return layout.height
    }

    /**
     * Processes every text view inside the given parent view: each text view will be resized down
     * if necessary, to fit within the given width.
     */
    fun fitTextViewsHorizontally(parent: View, @DimenRes maxWidthResId: Int) {
        // Scale the text size
        if (parent is TextView) {
            fitTextViewHorizontally(parent, maxWidthResId)
        }
        // Scale all the child views
        else if (parent is ViewGroup) {
            for (i in 0 until parent.childCount) {
                fitTextViewsHorizontally(parent.getChildAt(i), maxWidthResId)
            }
        }
    }

    /**
     * If the given TextView's text is too long for the given max width, set the text size on the
     * TextView to a smaller value so it will fit the width.
     */
    private fun fitTextViewHorizontally(textView: TextView, @DimenRes maxWidthResId: Int) {
        val maxWidth = textView.context.resources.getDimensionPixelSize(maxWidthResId)
        val paint = Paint()
        val originalTextSize = textView.textSize
        paint.textSize = originalTextSize
        val actualWidth = paint.measureText(textView.text.toString())
        if (actualWidth < maxWidth) return
        val shrunkenTextSize = (maxWidth / actualWidth) * originalTextSize
        Log.v(TAG, "Shrunk text size for '" + textView.text + "' from " + originalTextSize + "px to " + shrunkenTextSize + "px")
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, shrunkenTextSize)
    }
}
