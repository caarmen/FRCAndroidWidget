package ca.rmen.android.frenchcalendar;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;

abstract class FrenchCalendarPreferenceActivity extends PreferenceActivity {
    private int mAppWidgetId = -1;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        addPreferencesFromResource(R.xml.widget_settings);
        Intent resultValue = new Intent();
        if (mAppWidgetId > -1) resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        setResult(RESULT_OK, resultValue);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent updateIntent = new Intent(getPackageName() + FrenchCalendarAppWidget.BROADCAST_MESSAGE_CONF_CHANGE);
        updateIntent.addCategory(getWidgetCategory());
        sendBroadcast(updateIntent);
    }

    abstract protected String getWidgetCategory();
}
