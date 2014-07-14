package ca.rmen.android.frenchcalendar;

import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.TextView;
import ca.rmen.lfrc.FrenchRevolutionaryCalendar;
import ca.rmen.lfrc.FrenchRevolutionaryCalendarDate;

public abstract class FrenchCalendarAppWidget extends AppWidgetProvider {

    public static final String SHARED_PREFS_NAME = "frenchcalwidgetprefs";
    public static final String PREF_METHOD = "setting_method";
    public static final String PREF_FREQUENCY = "setting_frequency";
    public static final String BROADCAST_MESSAGE_UPDATE = ".UPDATE_WIDGET";
    public static final String BROADCAST_MESSAGE_CONF_CHANGE = ".CONF_CHANGE";
    private static final String FREQUENCY_SECONDS = "864";
    private static final String FREQUENCY_MINUTES = "86400";
    private static final int FREQUENCY_DAYS = 86400000;
    private static final String EXTRA_WIDGET_CLASS = "WIDGET_CLASS";
    private static final String FONT_FILE = "Gabrielle.ttf";
    private boolean initialized = false;
    private FrenchRevolutionaryCalendar util = null;

    @Override
    public void onReceive(Context context, Intent intent) {
        debug(context, "onReceive: action = " + intent.getAction() + ": component = "
                + (intent.getComponent() == null ? "" : intent.getComponent().getClassName()));

        final AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        final ComponentName provider = intent.getComponent();
        final int[] appWidgetIds = appWidgetManager.getAppWidgetIds(provider);
        debug(context, "onReceive: appWidgetIds = " + Arrays.toString(appWidgetIds));
        if ((context.getPackageName() + BROADCAST_MESSAGE_UPDATE).equals(intent.getAction())) {
            debug(context, "Received my scheduled update");
            if (intent != null && intent.getExtras() != null) {
                String broadcaster = intent.getExtras().getString(EXTRA_WIDGET_CLASS);
                if (!getClass().getName().equals(broadcaster)) return;
            }
            if (appWidgetIds.length == 0) {
                stopWidgetNotifier(context);
            } else
                updateAll(context, appWidgetManager, appWidgetIds);
        } else if ((context.getPackageName() + BROADCAST_MESSAGE_CONF_CHANGE).equals(intent.getAction())) {
            debug(context, "Preferences changed");
            restartWidgetNotifier(context);
            updateAll(context, appWidgetManager, appWidgetIds);
        } else if (Intent.ACTION_SCREEN_OFF.equals(intent.getAction())) {
            stopWidgetNotifier(context);
        } else if (Intent.ACTION_SCREEN_ON.equals(intent.getAction())) {
            startWidgetNotifier(context);
        }
        super.onReceive(context, intent);
    }

    private void restartWidgetNotifier(Context context) {
        debug(context, "restartWidgetNotifier");
        stopWidgetNotifier(context);
        startWidgetNotifier(context);
    }

    private void startWidgetNotifier(Context context) {
        debug(context, "startWidgetNotifier");
        PendingIntent updatePendingIntent = createWidgetNotifier(context);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String frequencyPrefStr = sharedPreferences.getString(PREF_FREQUENCY, FREQUENCY_MINUTES);

        int frequency = Integer.parseInt(frequencyPrefStr);
        debug(context, "Start alarm with frequency " + frequency);
        // If we show the time, we will update the widget every decimal "minute" (86.4 Gregorian seconds) starting 
        // one decimal "minute" from now.
        long nextAlarmTime = System.currentTimeMillis() + frequency;
        AlarmManager mgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        // If we only show the date, we will update the widget every day just before midnight
        if (frequency == FREQUENCY_DAYS) {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.add(Calendar.MINUTE, 1);
            nextAlarmTime = cal.getTimeInMillis();
        }
        mgr.setRepeating(AlarmManager.RTC, nextAlarmTime, frequency, updatePendingIntent);

        debug(context, "Started updater");
    }

    private void stopWidgetNotifier(Context context) {
        debug(context, "stopWidgetNotifier");
        PendingIntent updatePendingIntent = createWidgetNotifier(context);
        AlarmManager mgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        mgr.cancel(updatePendingIntent);
    }

    private PendingIntent createWidgetNotifier(Context context) {
        Intent updateIntent = new Intent(context.getPackageName() + BROADCAST_MESSAGE_UPDATE);
        updateIntent.putExtra(EXTRA_WIDGET_CLASS, getClass().getName());
        updateIntent.addCategory(getClass().getName());
        PendingIntent updatePendingIntent = PendingIntent.getBroadcast(context, 0, updateIntent, 0);
        return updatePendingIntent;
    }

    /*
     * 
     * @Override public void onReceive(Context context, Intent intent) {
     * update(context); super.onReceive(context, intent); }
     */
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        debug(context, "onUpdate: appWidgetIds = " + Arrays.toString(appWidgetIds));
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        if (!initialized) init(context);
        else {
            restartWidgetNotifier(context);
        }
        updateAll(context, appWidgetManager, appWidgetIds);
    }

    private void init(final Context context) {
        debug(context, "init");
        startWidgetNotifier(context);
        IntentFilter filterOn = new IntentFilter(Intent.ACTION_SCREEN_ON);
        IntentFilter filterOff = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        context.getApplicationContext().registerReceiver(this, filterOn);
        context.getApplicationContext().registerReceiver(this, filterOff);
        initialized = true;
        debug(context, "initialized");
    }

    public void updateAll(Context context, final AppWidgetManager appWidgetManager, final int[] appWidgetIds) {
        debug(context, "updateAll:  appWidgetIds = " + Arrays.toString(appWidgetIds));
        for (int appWidgetId : appWidgetIds)
            update(context, appWidgetManager, appWidgetId);
    }

    protected abstract int getLayoutResourceId();

    protected abstract int getWidthResourceId();

    protected abstract int getHeightResourceId();

    protected abstract int getTextWidthResourceId();

    public void update(Context context, final AppWidgetManager appWidgetManager, final int appWidgetId) {
        debug(context, "update: appWidgetId = " + appWidgetId);

        GregorianCalendar now = new GregorianCalendar();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String methodPrefStr = sharedPreferences.getString(PREF_METHOD, "0");
        int mode = Integer.parseInt(methodPrefStr);

        util = new FrenchRevolutionaryCalendar(mode);
        FrenchRevolutionaryCalendarDate frenchDate = util.getDate(now);

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(getLayoutResourceId(), null, false);
        int scrollResourceId = getDrawableResourceIdForMonth(frenchDate.month);
        view.setBackgroundResource(scrollResourceId);
        int width = context.getResources().getDimensionPixelSize(getWidthResourceId());
        int height = context.getResources().getDimensionPixelSize(getHeightResourceId());
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);

        setText(context, view, R.id.text_year, "" + frenchDate.year);
        setText(context, view, R.id.text_dayofmonth, "" + frenchDate.day);
        CharSequence weekdayLabel = getLabel(context, R.array.weekdays, frenchDate.getDayInWeek() - 1);
        CharSequence monthLabel = getLabel(context, R.array.months, frenchDate.month - 1);
        setText(context, view, R.id.text_weekday, weekdayLabel);
        setText(context, view, R.id.text_month, monthLabel);

        String frequencyPrefStr = sharedPreferences.getString(PREF_FREQUENCY, FREQUENCY_SECONDS);

        String timestamp = null;
        TextView timeView = (TextView) view.findViewById(R.id.text_time);
        if (FREQUENCY_SECONDS.equals(frequencyPrefStr)) {
            timeView.setVisibility(View.VISIBLE);
            timestamp = String.format("%d:%02d:%02d", frenchDate.hour, frenchDate.minute, frenchDate.second);
        } else if (FREQUENCY_MINUTES.equals(frequencyPrefStr)) {
            timeView.setVisibility(View.VISIBLE);
            timestamp = String.format("%d:%02d", frenchDate.hour, frenchDate.minute);
        } else {
            timeView.setVisibility(View.GONE);
            timestamp = "";
        }
        setText(context, view, R.id.text_time, timestamp);

        view.measure(width, height);
        view.layout(0, 0, width - 1, height - 1);

        squeezeMonthLine(context, view);
        view.measure(width, height);
        view.layout(0, 0, width - 1, height - 1);
        view.draw(canvas);

        final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.imageview);
        views.setImageViewBitmap(R.id.imageView1, bitmap);

        final Intent intent = new Intent(context, FrenchCalendarPreferenceActivity.class);

        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        intent.addCategory(getClass().getName());
        final PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        views.setOnClickPendingIntent(R.id.imageView1, pendingIntent);
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    private void setText(Context context, View view, int resourceId, CharSequence text) {
        Typeface font = Typeface.createFromAsset(context.getAssets(), FONT_FILE);
        TextView textView = (TextView) view.findViewById(resourceId);
        textView.setTypeface(font);
        textView.setText(text);
    }

    private CharSequence getLabel(Context context, int arrayResource, int index) {
        CharSequence[] labels = context.getResources().getTextArray(arrayResource);
        if (index >= 0 && index < labels.length) return labels[index];
        return "";
    }

    private void squeezeMonthLine(Context context, View view) {
        TextView dateView = (TextView) view.findViewById(R.id.text_dayofmonth);
        TextView monthView = (TextView) view.findViewById(R.id.text_month);
        LinearLayout monthLine = (LinearLayout) monthView.getParent();
        TextView yearView = (TextView) monthLine.findViewById(R.id.text_year);

        int textWidth = dateView.getWidth() + monthView.getWidth() + (yearView == null ? 0 : yearView.getWidth());
        int textViewableWidthResourceId = getTextWidthResourceId();
        int textViewableWidth = context.getResources().getDimensionPixelSize(textViewableWidthResourceId);

        if (textWidth > textViewableWidth) {
            float squeezeFactor = (float) textViewableWidth / textWidth;

            debug(context, squeezeFactor);
            resizeTextView(context, dateView, squeezeFactor);
            resizeTextView(context, monthView, squeezeFactor);
            resizeTextView(context, yearView, squeezeFactor);
        }
    }

    private void resizeTextView(Context context, TextView textView, float squeezeFactor) {
        if (textView == null) return;
        textView.setTextScaleX(squeezeFactor);
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

    private void debug(Context context, Object message) {
        Log.d(context.getPackageName(), getClass().getSimpleName() + ": " + message);
    }
}