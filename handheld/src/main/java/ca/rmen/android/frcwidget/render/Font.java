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

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Allows us to use a custom font on TextViews in the app.
 * 
 * @author calvarez
 */
public class Font {

    private static Typeface sTypeface;

    /**
     * Lazy initialize the font.
     */
    private static Typeface getTypeface(Context context) {
        if (sTypeface == null) sTypeface = Typeface.createFromAsset(context.getAssets(), "Gabrielle.ttf");
        return sTypeface;
    }

    /**
     * Sets the TypeFace for all TextViews in the given parent view, to our custom font.
     */
    public static void applyFont(final Context context, final View parent) {
        Typeface font = getTypeface(context);
        if (parent instanceof TextView) {
            ((TextView) parent).setTypeface(font);
        } else if (parent instanceof ViewGroup) {
            final ViewGroup parentGroup = (ViewGroup) parent;
            final int childCount = parentGroup.getChildCount();
            for (int i = 0; i < childCount; i++) {
                final View v = parentGroup.getChildAt(i);
                // Apply the font recursively.
                applyFont(context, v);
            }
        }
    }
}
