package ca.rmen.android.frenchcalendar.prefs;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.util.Log;
import ca.rmen.android.frenchcalendar.FrenchCalendarScheduler;
import ca.rmen.android.frenchcalendar.R;

public class FrenchCalendarPreferenceActivity extends PreferenceActivity { // NO_UCD (use default)

    private static final String TAG = FrenchCalendarPreferenceActivity.class.getSimpleName();
    private int mAppWidgetId = -1;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle icicle) {
        Log.v(TAG, "onCreate: bundle = " + icicle);
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
        Log.v(TAG, "onDestroy");
        super.onDestroy();
        FrenchCalendarScheduler.getInstance(this).start();
    }
}
