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

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.TextView;

import ca.rmen.android.frccommon.Constants;
import ca.rmen.android.frccommon.FRCDateUtils;
import ca.rmen.android.frenchcalendar.R;
import ca.rmen.lfrc.FrenchRevolutionaryCalendarDate;

/**
 * Responsible for drawing the scroll widgets.
 * @author calvarez
 */
class FRCWideScrollAppWidgetRenderer implements FRCAppWidgetRenderer {
    private static final String TAG = Constants.TAG + FRCWideScrollAppWidgetRenderer.class.getSimpleName();
    private final FRCScrollAppWidgetRenderParams mParams;

    FRCWideScrollAppWidgetRenderer(FRCScrollAppWidgetRenderParams params) {
    	mParams = params;
    }

    public RemoteViews render(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        Log.v(TAG, "render");

        FrenchRevolutionaryCalendarDate frenchDate = FRCDateUtils.getToday(context);

        // Create a view with the right scroll image as the background.
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(mParams.layoutResourceId, null, false);
        FRCRender.setBackgroundImage(context, view, mParams.scrollResourceId, frenchDate);

        // Set all the text fields for the date.
        // Add a space before and after the text: this font sometimes cuts off the last letter.
        String date = " " + frenchDate.dayOfMonth + " " + frenchDate.getMonthName() + " " + frenchDate.year + " ";
        ((TextView) view.findViewById(R.id.text_date)).setText(date);
        ((TextView) view.findViewById(R.id.text_weekday)).setText(frenchDate.getWeekdayName());
        FRCRender.setDetailedViewText(context, (TextView) view.findViewById(R.id.text_time), frenchDate);

        FRCRender.scaleWidget(context, view, appWidgetManager, appWidgetId,
                mParams.widthResourceId, mParams.heightResourceId, mParams.textViewableWidthResourceId);
        Font.applyFont(context, view);

        return FRCRender.createRemoteViews(context, view, appWidgetManager, appWidgetId, mParams.widthResourceId, mParams.heightResourceId);
    }


}
