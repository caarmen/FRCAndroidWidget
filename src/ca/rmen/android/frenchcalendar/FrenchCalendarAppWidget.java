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
import ca.rmen.android.frenchcalendar.FrenchCalendarAppWidgetRenderer.FrenchCalendarAppWidgetRenderParams;

abstract class FrenchCalendarAppWidget extends AppWidgetProvider {

    private final String TAG = Constants.TAG + getClass().getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v(TAG, "onReceive: action = " + intent.getAction() + ": component = " + (intent.getComponent() == null ? "" : intent.getComponent().getClassName()));

        final AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        final ComponentName provider = intent.getComponent();
        final int[] appWidgetIds = appWidgetManager.getAppWidgetIds(provider);
        Log.v(TAG, "onReceive: appWidgetIds = " + Arrays.toString(appWidgetIds));
        if ((context.getPackageName() + FrenchCalendarScheduler.BROADCAST_MESSAGE_UPDATE).equals(intent.getAction())) {
            Log.v(TAG, "Received my scheduled update");
            Set<Integer> allAppWidgetIds = FrenchCalendarAppWidgetManager.getAllAppWidgetIds(context);
            if (allAppWidgetIds.size() == 0) FrenchCalendarScheduler.getInstance(context).stop();
            else
                updateAll(context, appWidgetManager, appWidgetIds);
        }
        super.onReceive(context, intent);
    }

    /**
     * 
     */
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.v(TAG, "onUpdate: appWidgetIds = " + Arrays.toString(appWidgetIds));
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        FrenchCalendarScheduler.getInstance(context).start();
    }

    private void updateAll(Context context, final AppWidgetManager appWidgetManager, final int[] appWidgetIds) {
        Log.v(TAG, "updateAll:  appWidgetIds = " + Arrays.toString(appWidgetIds));
        for (int appWidgetId : appWidgetIds)
            update(context, appWidgetManager, appWidgetId);
    }

    private void update(Context context, final AppWidgetManager appWidgetManager, final int appWidgetId) {
        Log.v(TAG, "update: appWidgetId = " + appWidgetId);

        FrenchCalendarAppWidgetRenderParams renderParams = getRenderParams();
        RemoteViews views = FrenchCalendarAppWidgetRenderer.render(context, getClass(), appWidgetId, renderParams);
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    protected abstract FrenchCalendarAppWidgetRenderParams getRenderParams();

}