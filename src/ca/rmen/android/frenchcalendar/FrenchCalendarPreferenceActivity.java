package ca.rmen.android.frenchcalendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;

public class FrenchCalendarPreferenceActivity extends PreferenceActivity
/* implements SharedPreferences.OnSharedPreferenceChangeListener */{
	int mAppWidgetId = -1;

	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		Log.d(getClass().getName(), "pref activity");
		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
				AppWidgetManager.INVALID_APPWIDGET_ID);
//		getPreferenceManager().setSharedPreferencesName(
	//			FrenchCalendarAppWidget.SHARED_PREFS_NAME);
		//SharedPreferences sharedPreferences = PreferenceManager
			//	.getDefaultSharedPreferences(this);
		// sharedPreferences.registerOnSharedPreferenceChangeListener(this);

		addPreferencesFromResource(R.xml.widget_settings);
		Intent resultValue = new Intent();
		resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
		setResult(RESULT_OK, resultValue);
	}

	@Override
	protected void onDestroy() {
		Log.d(getClass().getName(), "onDestroy begin " + mAppWidgetId);
		super.onDestroy();
		// TODO Auto-generated method stub
		//AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
		//RemoteViews views = new RemoteViews(getPackageName(),
//				R.layout.appwidget);
	//	appWidgetManager.updateAppWidget(mAppWidgetId, views);
		Intent updateIntent = new Intent(getPackageName() + FrenchCalendarAppWidget.BROADCAST_MESSAGE_CONF_CHANGE);
	//	updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, new int[]{mAppWidgetId});
		

		//PendingIntent updatePendingIntent = PendingIntent.getBroadcast(this, 0,
			//	updateIntent, 0);
		sendBroadcast(updateIntent);
		Log.d(getClass().getName(), "onDestroy end");
	}
	/*
	 * @Override public void onSharedPreferenceChanged(SharedPreferences
	 * preferences, String prefKey) { AppWidgetManager appWidgetManager =
	 * AppWidgetManager.getInstance(this); RemoteViews views = new
	 * RemoteViews(getPackageName(), R.layout.appwidget);
	 * appWidgetManager.updateAppWidget(mAppWidgetId, views);
	 * Log.d(getClass().getName(), "onSharedPrefChange " + prefKey);
	 * 
	 * }
	 */

	/*
	 * @Override protected void onPause() { super.onPause(); SharedPreferences
	 * sharedPreferences = PreferenceManager .getDefaultSharedPreferences(this);
	 * sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
	 * 
	 * }
	 */

}
