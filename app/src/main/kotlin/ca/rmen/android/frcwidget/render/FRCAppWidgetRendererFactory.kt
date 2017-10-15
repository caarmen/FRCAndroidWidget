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

import ca.rmen.android.frccommon.Constants

/**
 * Provide the correct rendering parameters for the widget, depending on the type of widget (wide or narrow).
 *
 * @author calvarez
 */
object FRCAppWidgetRendererFactory {
    fun getRenderer(widgetType: Constants.WidgetType): FRCAppWidgetRenderer = when (widgetType) {
        Constants.WidgetType.WIDE -> FRCWideScrollAppWidgetRenderer()
        Constants.WidgetType.NARROW -> FRCNarrowScrollAppWidgetRenderer()
        else -> FRCMinimalistAppWidgetRenderer()
    }
}
