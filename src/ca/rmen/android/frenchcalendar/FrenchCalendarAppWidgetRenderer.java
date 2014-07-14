package ca.rmen.android.frenchcalendar;

import java.util.GregorianCalendar;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.TextView;
import ca.rmen.lfrc.FrenchRevolutionaryCalendar;
import ca.rmen.lfrc.FrenchRevolutionaryCalendarDate;

class FrenchCalendarAppWidgetRenderer {
    private static final String TAG = Constants.TAG + FrenchCalendarAppWidgetRenderer.class.getSimpleName();

    static class FrenchCalendarAppWidgetRenderParams {
        private final int layoutResourceId;
        private final int widthResourceId;
        private final int heightResourceId;
        private final int textViewableWidthResourceId;
        private final int[] scrollResourceIds;

        FrenchCalendarAppWidgetRenderParams(int layoutResourceId, int widthResourceId, int heightResourceId, int textViewableWidthResourceId,
                int[] scrollResourceIds) {
            this.layoutResourceId = layoutResourceId;
            this.widthResourceId = widthResourceId;
            this.heightResourceId = heightResourceId;
            this.textViewableWidthResourceId = textViewableWidthResourceId;
            this.scrollResourceIds = scrollResourceIds;
        }
    }

    static RemoteViews render(Context context, Class<?> widgetClass, int appWidgetId, FrenchCalendarAppWidgetRenderParams params) {

        GregorianCalendar now = new GregorianCalendar();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String methodPrefStr = sharedPreferences.getString(FrenchCalendarPrefs.PREF_METHOD, "0");
        int mode = Integer.parseInt(methodPrefStr);

        FrenchRevolutionaryCalendar frcal = new FrenchRevolutionaryCalendar(mode);
        FrenchRevolutionaryCalendarDate frenchDate = frcal.getDate(now);
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(params.layoutResourceId, null, false);
        view.setBackgroundResource(params.scrollResourceIds[frenchDate.month - 1]);
        int width = context.getResources().getDimensionPixelSize(params.widthResourceId);
        int height = context.getResources().getDimensionPixelSize(params.heightResourceId);
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);

        ((TextView) view.findViewById(R.id.text_year)).setText(String.valueOf(frenchDate.year));
        ((TextView) view.findViewById(R.id.text_dayofmonth)).setText(String.valueOf(frenchDate.day));
        CharSequence weekdayLabel = getLabel(context, R.array.weekdays, frenchDate.getDayInWeek() - 1);
        CharSequence monthLabel = getLabel(context, R.array.months, frenchDate.month - 1);
        ((TextView) view.findViewById(R.id.text_weekday)).setText(weekdayLabel);
        ((TextView) view.findViewById(R.id.text_month)).setText(monthLabel);

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
        ((TextView) view.findViewById(R.id.text_time)).setText(timestamp);
        Font.applyFont(context, view);

        view.measure(width, height);
        view.layout(0, 0, width - 1, height - 1);

        squeezeMonthLine(context, view, params.textViewableWidthResourceId);
        view.measure(width, height);
        view.layout(0, 0, width - 1, height - 1);
        view.draw(canvas);

        final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.imageview);
        views.setImageViewBitmap(R.id.imageView1, bitmap);

        final Intent intent = new Intent(context, FrenchCalendarPreferenceActivity.class);

        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        intent.addCategory(widgetClass.getName());
        final PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        views.setOnClickPendingIntent(R.id.imageView1, pendingIntent);
        return views;
    }

    private static CharSequence getLabel(Context context, int arrayResource, int index) {
        CharSequence[] labels = context.getResources().getTextArray(arrayResource);
        if (index >= 0 && index < labels.length) return labels[index];
        return "";
    }

    private static void squeezeMonthLine(Context context, View view, int textViewableWidthResourceId) {
        TextView dateView = (TextView) view.findViewById(R.id.text_dayofmonth);
        TextView monthView = (TextView) view.findViewById(R.id.text_month);
        LinearLayout monthLine = (LinearLayout) monthView.getParent();
        TextView yearView = (TextView) monthLine.findViewById(R.id.text_year);

        int textWidth = dateView.getWidth() + monthView.getWidth() + (yearView == null ? 0 : yearView.getWidth());
        int textViewableWidth = context.getResources().getDimensionPixelSize(textViewableWidthResourceId);

        if (textWidth > textViewableWidth) {
            float squeezeFactor = (float) textViewableWidth / textWidth;

            Log.v(TAG, "SqueezeFactor: " + squeezeFactor);
            resizeTextView(context, dateView, squeezeFactor);
            resizeTextView(context, monthView, squeezeFactor);
            resizeTextView(context, yearView, squeezeFactor);
        }
    }

    private static void resizeTextView(Context context, TextView textView, float squeezeFactor) {
        if (textView == null) return;
        textView.setTextScaleX(squeezeFactor);
    }

}
