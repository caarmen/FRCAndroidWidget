package ca.rmen.android.frcwidget.render;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.widget.RemoteViews;

public interface FRCAppWidgetRenderer {
    RemoteViews render(Context context, AppWidgetManager appWidgetManager, int appWidgetId);
}