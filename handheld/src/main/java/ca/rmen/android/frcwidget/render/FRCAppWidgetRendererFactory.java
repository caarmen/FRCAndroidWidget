/*
 * French Revolutionary Calendar Android Widget
 * Copyright (C) 2011 - 2016 Carmen Alvarez
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

import ca.rmen.android.frccommon.Constants.WidgetType;

/**
 * Provide the correct rendering parameters for the widget, depending on the type of widget (wide or narrow).
 * 
 * @author calvarez
 */
public class FRCAppWidgetRendererFactory {

	public static FRCAppWidgetRenderer getRenderer(WidgetType widgetType) {
		switch (widgetType) {
		case WIDE:
			return new FRCWideScrollAppWidgetRenderer();
		case NARROW:
			return new FRCNarrowScrollAppWidgetRenderer();
		case MINIMALIST:
		default:
			return new FRCMinimalistAppWidgetRenderer();
		}

    }

}
