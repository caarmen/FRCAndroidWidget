/*
 * French Revolutionary Calendar Android Widget
 * Copyright (C) 2011 - 2017 Carmen Alvarez
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

package ca.rmen.android.frcwidget.render

import android.content.Context
import android.graphics.Typeface
import android.preference.PreferenceManager
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import ca.rmen.android.frenchcalendar.R

/**
 * Allows us to use a custom font on TextViews in the app.
 *
 * @author calvarez
 */
object Font {
    private val PREF_SETTING_CUSTOM_FONT = "setting_custom_font"

    /**
     * Load the font specified in the preferences.
     */
    private fun getTypeface(context: Context): Typeface {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val defaultFontName = context.getString(R.string.default_font)
        val fontName = sharedPreferences.getString(PREF_SETTING_CUSTOM_FONT, defaultFontName)
        return try {
            Typeface.createFromAsset(context.assets, fontName)
        } catch (e: Exception) {
            Typeface.DEFAULT
        }
    }

    /**
     * Sets the TypeFace for all TextViews in the given parent view, to our custom font.
     */
    fun applyFont(context: Context, parent: View) {
        val font = getTypeface(context)
        if (parent is TextView) {
            parent.typeface = font
        } else if (parent is ViewGroup) {
            for (i in 0 until parent.childCount) {
                // Apply the font recursively.
                applyFont(context, parent.getChildAt(i))
            }
        }
    }

}
