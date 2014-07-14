package ca.rmen.android.frenchcalendar;

import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.Set;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;
import ca.rmen.lfrc.FrenchRevolutionaryCalendar;
import ca.rmen.lfrc.FrenchRevolutionaryCalendarDate;

public abstract class FrenchCalendarAppWidget extends AppWidgetProvider {

    private final String TAG = Constants.TAG + getClass().getSimpleName();
    public static final String SHARED_PREFS_NAME = "frenchcalwidgetprefs";
    private FrenchRevolutionaryCalendar util = null;

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

    public void updateAll(Context context, final AppWidgetManager appWidgetManager, final int[] appWidgetIds) {
        Log.v(TAG, "updateAll:  appWidgetIds = " + Arrays.toString(appWidgetIds));
        for (int appWidgetId : appWidgetIds)
            update(context, appWidgetManager, appWidgetId);
    }

    protected abstract int getLayoutResourceId();

    protected abstract int getWidthResourceId();

    protected abstract int getHeightResourceId();

    protected abstract int getTextWidthResourceId();

    public void update(Context context, final AppWidgetManager appWidgetManager, final int appWidgetId) {
        Log.v(TAG, "update: appWidgetId = " + appWidgetId);

        GregorianCalendar now = new GregorianCalendar();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String methodPrefStr = sharedPreferences.getString(FrenchCalendarPrefs.PREF_METHOD, "0");
        int mode = Integer.parseInt(methodPrefStr);

        FrenchRevolutionaryCalendar frcal = new FrenchRevolutionaryCalendar(mode);
        FrenchRevolutionaryCalendarDate frenchDate = frcal.getDate(now);
        RemoteViews views = FrenchCalendarAppWidgetRenderer.render(context, getClass(), appWidgetId, frenchDate, getLayoutResourceId(), getWidthResourceId(),
                getHeightResourceId(), getDrawableResourceIdForMonth(frenchDate.month), getTextWidthResourceId());
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    /**
     * This is so ugly, but better for performance.
     * 
     * @param month
     * @return the resource id for the bitmap of the scroll for the given month
     */
    protected int getDrawableResourceIdForMonth(int month) {
        if (month == 1) return R.drawable.vscroll1;
        if (month == 2) return R.drawable.vscroll2;
        if (month == 3) return R.drawable.vscroll3;
        if (month == 4) return R.drawable.vscroll4;
        if (month == 5) return R.drawable.vscroll5;
        if (month == 6) return R.drawable.vscroll6;
        if (month == 7) return R.drawable.vscroll7;
        if (month == 8) return R.drawable.vscroll8;
        if (month == 9) return R.drawable.vscroll9;
        if (month == 10) return R.drawable.vscroll10;
        if (month == 11) return R.drawable.vscroll11;
        if (month == 12) return R.drawable.vscroll12;
        if (month == 13) return R.drawable.vscroll13;
        return R.drawable.vscroll;
    }
}