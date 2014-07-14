package ca.rmen.android.frenchcalendar;

import java.util.Arrays;
import java.util.Set;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import ca.rmen.android.frenchcalendar.Constants.WidgetType;
import ca.rmen.android.frenchcalendar.render.FrenchCalendarAppWidgetRenderParams;
import ca.rmen.android.frenchcalendar.render.FrenchCalendarAppWidgetRenderParamsFactory;
import ca.rmen.android.frenchcalendar.render.FrenchCalendarAppWidgetRenderer;

/**
 * Receiver and AppWidgetProvider which updates a list of wide widgets or a list of narrow widgets.
 * 
 * At any given point, there will be at most two instances of this class:
 * <ul>
 * <li> one {@link FrenchCalendarAppWidgetWide} which will manage all of the wide widgets, and </li>
 * <li> one {@link FrenchCalendarAppWidgetNarrow} which will manage all of the narrow widgets.</li>
 * </ul>
 * These receivers are notified by the system when a widget of the given type is added or deleted,
 * or when widgets of the given type should be updated.
 * 
 * These receivers are also notified by the alarm set up by {@link FrenchCalendarScheduler}, which will
 * go off either once a minute, or once a day, depending on the preferences.
 */
abstract class FrenchCalendarAppWidget extends AppWidgetProvider {

    private final String TAG = Constants.TAG + getClass().getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v(TAG, "onReceive: action = " + intent.getAction() + ": component = " + (intent.getComponent() == null ? "" : intent.getComponent().getClassName()));
        final AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        final ComponentName provider = intent.getComponent();
        final int[] appWidgetIds = appWidgetManager.getAppWidgetIds(provider);
        if ((context.getPackageName() + FrenchCalendarScheduler.BROADCAST_MESSAGE_UPDATE).equals(intent.getAction())) {
            Set<Integer> allAppWidgetIds = FrenchCalendarAppWidgetManager.getAllAppWidgetIds(context);
            if (allAppWidgetIds.size() == 0) FrenchCalendarScheduler.getInstance(context).stop();
            else
                updateAll(context, appWidgetManager, appWidgetIds);
        }
        super.onReceive(context, intent);
    }

    /**
     * This is called by the parent class when the system broadcasts "android.appwidget.action.APPWIDGET_UPDATE".
     */
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.v(TAG, "onUpdate: appWidgetIds = " + Arrays.toString(appWidgetIds));
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        FrenchCalendarScheduler.getInstance(context).start();
    }

    /**
     * Rerender all the widgets (for this {@link AppWidgetProvider}).
     */
    private void updateAll(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.v(TAG, "updateAll:  appWidgetIds = " + Arrays.toString(appWidgetIds));
        for (int appWidgetId : appWidgetIds)
            update(context, appWidgetManager, appWidgetId);
    }

    /**
     * Rerender a single widget.
     */
    private void update(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        Log.v(TAG, "update: appWidgetId = " + appWidgetId);
        FrenchCalendarAppWidgetRenderParams renderParams = FrenchCalendarAppWidgetRenderParamsFactory.getRenderParams(getWidgetType());
        RemoteViews views = FrenchCalendarAppWidgetRenderer.render(context, getClass(), appWidgetId, renderParams);
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    protected abstract WidgetType getWidgetType();

    public static class FrenchCalendarAppWidgetNarrow extends FrenchCalendarAppWidget {
        @Override
        protected WidgetType getWidgetType() {
            return WidgetType.NARROW;
        }
    }

    public static class FrenchCalendarAppWidgetWide extends FrenchCalendarAppWidget {
        @Override
        protected WidgetType getWidgetType() {
            return WidgetType.WIDE;
        }
    }
}