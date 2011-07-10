package ca.rmen.android.frenchcalendar;

public class FrenchCalendarAppWidgetWide extends FrenchCalendarAppWidget {

	@Override
	protected int getLayoutResourceId() {
		return R.layout.appwidget_wide;
	}
	@Override
	protected Class getPreferenceActivityClass() {
		return FrenchCalendarWidePreferenceActivity.class;
	}
}
