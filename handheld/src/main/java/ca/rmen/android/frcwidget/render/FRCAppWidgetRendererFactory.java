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

import ca.rmen.android.frccommon.Constants.WidgetType;
import ca.rmen.android.frenchcalendar.R;

/**
 * Provide the correct rendering parameters for the widget, depending on the type of widget (wide or narrow).
 * 
 * @author calvarez
 */
public class FRCAppWidgetRendererFactory {

	public static FRCAppWidgetRenderer getRenderer(WidgetType widgetType) {
		switch (widgetType) {
		case WIDE:
			return new FRCScrollAppWidgetRenderer(
					new FRCScrollAppWidgetRenderParams(R.layout.appwidget_wide,
							R.dimen.wide_widget_width,
							R.dimen.wide_widget_height,
							R.dimen.wide_widget_text_width,
							R.drawable.hscroll_blank));
		case NARROW:
			return new FRCScrollAppWidgetRenderer(
					new FRCScrollAppWidgetRenderParams(
							R.layout.appwidget_narrow,
							R.dimen.narrow_widget_width,
							R.dimen.narrow_widget_height,
							R.dimen.narrow_widget_text_width,
							R.drawable.vscroll_blank));
		case MINIMALIST:
		default:
			return new FRCMinimalistAppWidgetRenderer();
		}

    }

}
