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
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Locale;

import ca.rmen.android.frenchcalendar.R;

/**
 * Allows us to use a custom font on TextViews in the app.
 * 
 * @author calvarez
 */
public class Font {

    private static final String PREF_SETTING_CUSTOM_FONT = "setting_custom_font";

    /**
     * Load the font specified in the preferences.
     */
    private static Typeface getTypeface(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String defaultFontName = String.format(
                Locale.US, "%s/%s",
                context.getString(R.string.fonts_folder),
                context.getString(R.string.default_font));
        String fontName = sharedPreferences.getString(PREF_SETTING_CUSTOM_FONT, defaultFontName);
        try {
            return Typeface.createFromAsset(context.getAssets(), fontName);
        } catch (Exception e) {
            return Typeface.DEFAULT;
        }
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
