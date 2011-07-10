package ca.rmen.android.frenchcalendar;

import java.io.InputStream;
import java.util.GregorianCalendar;

import ca.rmen.android.frenchcalendar.common.FrenchCalendarDate;
import ca.rmen.android.frenchcalendar.common.FrenchCalendarUtil;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.widget.RemoteViews;

public class FrenchCalendarAppWidget extends AppWidgetProvider {

	private boolean initialized = false;
	private FrenchCalendarUtil util = null;

	/*
	 * 
	 * @Override public void onReceive(Context context, Intent intent) {
	 * update(context); super.onReceive(context, intent); }
	 */
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		for (int appWidgetId : appWidgetIds)
			update(context, appWidgetManager, appWidgetId);
		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}

	private void init(final Context context,
			final AppWidgetManager appWidgetManager, final int appWidgetId) {
		if (util == null) {
			Runnable initter = new Runnable() {
				public void run() {
					final InputStream equinoxFile = context.getResources()
							.openRawResource(R.raw.equinoxdates);

					util = new FrenchCalendarUtil(equinoxFile,
							FrenchCalendarUtil.MODE_ROMME);
					initialized = true;
					update(context, appWidgetManager, appWidgetId);
				}
			};
			new Handler().post(initter);
		}
	}

	private void update(Context context,
			final AppWidgetManager appWidgetManager, final int appWidgetId) {
		debug(context, "update");
		if (!initialized) {
			debug(context, "init...");
			init(context, appWidgetManager, appWidgetId);
		} else {
			final RemoteViews views = new RemoteViews(context.getPackageName(),
					R.layout.appwidget);
			GregorianCalendar now = new GregorianCalendar();
			FrenchCalendarDate frenchDate = util.getDate(now);
			String frenchDateStr = frenchDate.toString();
			debug(context, frenchDateStr);
			views.setTextViewText(R.id.text_year, "" + frenchDate.year);
			views.setTextViewText(R.id.text_dayofmonth, "" + frenchDate.day);
			debug(context, "weekday " + frenchDate.getDayInWeek());
			CharSequence weekdayLabel = getLabel(context, R.array.weekdays,
					frenchDate.getDayInWeek() - 1);
			CharSequence monthLabel = getLabel(context, R.array.months,
					frenchDate.month - 1);
			views.setTextViewText(R.id.text_month, monthLabel);
			views.setTextViewText(R.id.text_weekday, weekdayLabel);
			debug(context, now.getTime());
			String timestamp = String.format("%02d:%02d", frenchDate.hour, frenchDate.minute);
			views.setTextViewText(R.id.text_time, timestamp);
			appWidgetManager.updateAppWidget(appWidgetId, views);
		}

	}

	private CharSequence getLabel(Context context, int arrayResource, int index) {
		CharSequence[] labels = context.getResources().getTextArray(
				arrayResource);
		if (index >= 0 && index < labels.length)
			return labels[index];
		return "";
	}
	private void debug(Context context, Object message)
	{
		Log.d(context.getPackageName(), getClass().getName() + ": " + message);
	}
}