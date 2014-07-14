package ca.rmen.android.frenchcalendar;

import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.Set;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
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

    private final String TAG = Constants.TAG + getClass().getSimpleName();
    public static final String SHARED_PREFS_NAME = "frenchcalwidgetprefs";
    public static final String PREF_METHOD = "setting_method";
    private static final String FONT_FILE = "Gabrielle.ttf";
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

        String frequencyPrefStr = sharedPreferences.getString(FrenchCalendarPrefs.PREF_FREQUENCY, FrenchCalendarPrefs.FREQUENCY_MINUTES);

        String timestamp = null;
        TextView timeView = (TextView) view.findViewById(R.id.text_time);
        if (FrenchCalendarPrefs.FREQUENCY_SECONDS.equals(frequencyPrefStr)) {
            timeView.setVisibility(View.VISIBLE);
            timestamp = String.format("%d:%02d:%02d", frenchDate.hour, frenchDate.minute, frenchDate.second);
        } else if (FrenchCalendarPrefs.FREQUENCY_MINUTES.equals(frequencyPrefStr)) {
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

            Log.v(TAG, "SqueezeFactor: " + squeezeFactor);
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
}