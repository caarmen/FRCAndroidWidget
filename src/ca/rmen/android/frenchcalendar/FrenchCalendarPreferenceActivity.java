package ca.rmen.android.frenchcalendar;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;

public abstract class FrenchCalendarPreferenceActivity extends PreferenceActivity {
	int mAppWidgetId = -1;

	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
				AppWidgetManager.INVALID_APPWIDGET_ID);
		addPreferencesFromResource(R.xml.widget_settings);
		Intent resultValue = new Intent();
		resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
		setResult(RESULT_OK, resultValue);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Intent updateIntent = new Intent(getPackageName()
				+ FrenchCalendarAppWidget.BROADCAST_MESSAGE_CONF_CHANGE);
		updateIntent.addCategory(getWidgetCategory());
		sendBroadcast(updateIntent);
	}
	@Override
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
			Preference preference) {
		if("PREF_ABOUT".equals(preference.getKey()))
		{
			Intent aboutIntent = new Intent(this, FrenchCalendarAboutActivity.class);
			startActivity(aboutIntent);
			return true;
		}
		return super.onPreferenceTreeClick(preferenceScreen, preference);
	}
	abstract protected String getWidgetCategory();
}
