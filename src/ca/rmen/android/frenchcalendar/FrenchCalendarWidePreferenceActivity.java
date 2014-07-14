package ca.rmen.android.frenchcalendar;

public class FrenchCalendarWidePreferenceActivity extends // NO_UCD (use default)
        FrenchCalendarPreferenceActivity {
    @Override
    protected String getWidgetCategory() {
        return FrenchCalendarAppWidgetWide.class.getName();
    }

}
