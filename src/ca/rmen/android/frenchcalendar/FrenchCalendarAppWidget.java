package ca.rmen.android.frenchcalendar;

import java.io.InputStream;
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
import android.widget.RemoteViews;
import android.widget.TextView;
import ca.rmen.android.frenchcalendar.common.FrenchCalendarDate;
import ca.rmen.android.frenchcalendar.common.FrenchCalendarUtil;

public abstract class FrenchCalendarAppWidget extends AppWidgetProvider {

	public static final String SHARED_PREFS_NAME = "frenchcalwidgetprefs";
	public static final String PREF_METHOD = "setting_method";
	public static final String PREF_FREQUENCY = "setting_frequency";
	public static final String BROADCAST_MESSAGE_UPDATE = ".UPDATE_WIDGET";
	public static final String BROADCAST_MESSAGE_CONF_CHANGE = ".CONF_CHANGE";
	private static final String FREQUENCY_SECONDS = "864";
	private static final String FREQUENCY_MINUTES = "86400";
	private static final String FREQUENCY_DAYS = "86400000";
	private static final String EXTRA_WIDGET_CLASS = "WIDGET_CLASS";
	private boolean initialized = false;
	private FrenchCalendarUtil util = null;

	@Override
	public void onReceive(Context context, Intent intent) {
		debug(context,
				"received! "
						+ intent.getAction()
						+ ": "
						+ (intent.getComponent() == null ? "" : intent
								.getComponent().getClassName()));
		final AppWidgetManager appWidgetManager = AppWidgetManager
				.getInstance(context);
		final ComponentName provider = intent.getComponent();
		if ((context.getPackageName() + BROADCAST_MESSAGE_UPDATE).equals(intent
				.getAction())) {
			if (intent != null && intent.getExtras() != null) {
				String broadcaster = intent.getExtras().getString(
						EXTRA_WIDGET_CLASS);
				if (!getClass().getName().equals(broadcaster))
					return;
			}
			final int[] appWidgetIds = appWidgetManager
					.getAppWidgetIds(provider);
			if (appWidgetIds.length == 0) {
				stopWidgetNotifier(context);
			} else
				updateAll(context, appWidgetManager, appWidgetIds);
		} else if ((context.getPackageName() + BROADCAST_MESSAGE_CONF_CHANGE)
				.equals(intent.getAction())) {
			restartWidgetNotifier(context);
		}

		else if (Intent.ACTION_SCREEN_ON.equals(intent.getAction())) {
			startWidgetNotifier(context);
		} else if (Intent.ACTION_SCREEN_OFF.equals(intent.getAction())) {
			stopWidgetNotifier(context);
		}

		super.onReceive(context, intent);
	}

	private void restartWidgetNotifier(Context context) {
		stopWidgetNotifier(context);
		startWidgetNotifier(context);
	}

	private void startWidgetNotifier(Context context) {
		PendingIntent updatePendingIntent = createWidgetNotifier(context);
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		String frequencyPrefStr = sharedPreferences.getString(PREF_FREQUENCY,
				FREQUENCY_SECONDS);

		int frequency = Integer.parseInt(frequencyPrefStr);
		debug(context, "Start alarm with frequency " + frequency);
		AlarmManager mgr = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		mgr.setRepeating(AlarmManager.RTC, System.currentTimeMillis(),
				frequency, updatePendingIntent);
		debug(context, "Started updater");
	}

	private void stopWidgetNotifier(Context context) {
		debug(context, "Will cancel updater");
		PendingIntent updatePendingIntent = createWidgetNotifier(context);
		AlarmManager mgr = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		mgr.cancel(updatePendingIntent);
		debug(context, "Cancelled updater");
	}

	private PendingIntent createWidgetNotifier(Context context) {
		Intent updateIntent = new Intent(context.getPackageName()
				+ BROADCAST_MESSAGE_UPDATE);
		updateIntent.putExtra(EXTRA_WIDGET_CLASS, getClass().getName());
		updateIntent.addCategory(getClass().getName());
		PendingIntent updatePendingIntent = PendingIntent.getBroadcast(context,
				0, updateIntent, 0);
		return updatePendingIntent;
	}

	/*
	 * 
	 * @Override public void onReceive(Context context, Intent intent) {
	 * update(context); super.onReceive(context, intent); }
	 */
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		debug(context, "onUpdate");
		super.onUpdate(context, appWidgetManager, appWidgetIds);

		if (!initialized)
			init(context, appWidgetManager, appWidgetIds);
		else {
			restartWidgetNotifier(context);
		}
	}

	private void init(final Context context,
			final AppWidgetManager appWidgetManager, final int[] appWidgetIds) {
		startWidgetNotifier(context);
		IntentFilter filterOn = new IntentFilter(Intent.ACTION_SCREEN_ON);
		IntentFilter filterOff = new IntentFilter(Intent.ACTION_SCREEN_OFF);
		context.getApplicationContext().registerReceiver(this, filterOn);
		context.getApplicationContext().registerReceiver(this, filterOff);
		initialized = true;
		debug(context, "initialized");
	}

	public void updateAll(Context context,
			final AppWidgetManager appWidgetManager, final int[] appWidgetIds) {
		for (int appWidgetId : appWidgetIds)
			update(context, appWidgetManager, appWidgetId);
	}

	protected abstract int getLayoutResourceId();

	protected abstract Class getPreferenceActivityClass();
	
	protected abstract int getWidthResourceId();
	
	protected abstract int getHeightResourceId();

	public void update(Context context,
			final AppWidgetManager appWidgetManager, final int appWidgetId) {

		GregorianCalendar now = new GregorianCalendar();
		final InputStream equinoxFile = context.getResources().openRawResource(
				R.raw.equinoxdates);
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		String methodPrefStr = sharedPreferences.getString(PREF_METHOD, "0");
		int mode = Integer.parseInt(methodPrefStr);

		util = new FrenchCalendarUtil(equinoxFile, mode);
		FrenchCalendarDate frenchDate = util.getDate(now);
		
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(getLayoutResourceId(), null, false);
		int width = context.getResources().getDimensionPixelSize(getWidthResourceId());
		int height = context.getResources().getDimensionPixelSize(getHeightResourceId());
		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		
		Canvas canvas = new Canvas(bitmap);
		
		setText(context,view, R.id.text_year, ""+frenchDate.year);
		setText(context, view, R.id.text_dayofmonth, "" + frenchDate.day);
		CharSequence weekdayLabel = getLabel(context, R.array.weekdays,
				frenchDate.getDayInWeek() - 1);
		CharSequence monthLabel = getLabel(context, R.array.months,
				frenchDate.month - 1);
		setText(context,view, R.id.text_weekday, weekdayLabel);
		setText(context,view, R.id.text_month, monthLabel);

		String frequencyPrefStr = sharedPreferences.getString(PREF_FREQUENCY,
				FREQUENCY_SECONDS);

		String timestamp = null;
		TextView timeView = (TextView) view.findViewById(R.id.text_time);
		if (FREQUENCY_SECONDS.equals(frequencyPrefStr)) {
			timeView.setVisibility(View.VISIBLE);
			timestamp = String.format("%02d:%02d:%02d", frenchDate.hour,
					frenchDate.minute, frenchDate.second);
		} else if (FREQUENCY_MINUTES.equals(frequencyPrefStr)) {
			timeView.setVisibility(View.VISIBLE);
			timestamp = String.format("%02d:%02d", frenchDate.hour,
					frenchDate.minute);
		} else {
			timeView.setVisibility(View.GONE);
			timestamp = "";
		}
		setText(context, view, R.id.text_time, timestamp);
		
		view.measure(width, height);
		view.layout(0,0,width-1,height-1);		
		view.draw(canvas);
		
		final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.imageview);
		views.setImageViewBitmap(R.id.imageView1, bitmap);

		final Intent intent = new Intent(context, getPreferenceActivityClass());

		intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
		intent.addCategory(getClass().getName());
		final PendingIntent pendingIntent = PendingIntent.getActivity(context,
				0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

		views.setOnClickPendingIntent(R.id.imageView1, pendingIntent);
		appWidgetManager.updateAppWidget(appWidgetId, views);
	}

	private void setText(Context context, View view, int resourceId, CharSequence text)
	{
		Typeface font = Typeface.createFromAsset(context.getAssets(),"HoneyScript-SemiBold.ttf");
		TextView textView = (TextView) view.findViewById(resourceId);
		textView.setTypeface(font);
		textView.setText(text);
	}
	private CharSequence getLabel(Context context, int arrayResource, int index) {
		CharSequence[] labels = context.getResources().getTextArray(
				arrayResource);
		if (index >= 0 && index < labels.length)
			return labels[index];
		return "";
	}

	private void debug(Context context, Object message) {
		Log.d(context.getPackageName(), getClass().getName() + ": " + message);
	}
}