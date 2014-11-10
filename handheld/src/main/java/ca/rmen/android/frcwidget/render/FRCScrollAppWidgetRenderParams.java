/*
 * French Revolutionary Calendar Android Widget
 * Copyright (C) 2011 - 2014 Carmen Alvarez
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package ca.rmen.android.frcwidget.render;

class FRCScrollAppWidgetRenderParams {
    final int layoutResourceId;
    final int widthResourceId;
    final int heightResourceId;
    final int textViewableWidthResourceId;
    final int[] scrollResourceIds;

    FRCScrollAppWidgetRenderParams(int layoutResourceId, int widthResourceId, int heightResourceId, int textViewableWidthResourceId, int[] scrollResourceIds) {
        this.layoutResourceId = layoutResourceId;
        this.widthResourceId = widthResourceId;
        this.heightResourceId = heightResourceId;
        this.textViewableWidthResourceId = textViewableWidthResourceId;
        this.scrollResourceIds = scrollResourceIds;
    }

}