/*
 * French Revolutionary Calendar Android Widget
 * Copyright (C) 2017 Carmen Alvarez
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
package ca.rmen.android.frccommon.compat

import android.content.Context
import android.os.Build
import android.support.annotation.ColorInt
import android.support.annotation.ColorRes

object ContextCompat {
    @ColorInt
    fun getColor(context: Context, @ColorRes colorRes: Int): Int =
            if (ApiHelper.apiLevel >= Build.VERSION_CODES.M) {
                Api23Helper.getColor(context, colorRes)
            } else {
                @Suppress("DEPRECATION")
                context.resources.getColor(colorRes)
            }
}
