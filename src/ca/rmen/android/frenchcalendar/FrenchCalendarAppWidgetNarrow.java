package ca.rmen.android.frenchcalendar;

public class FrenchCalendarAppWidgetNarrow extends FrenchCalendarAppWidget {

	@Override
	protected int getLayoutResourceId() {
		return R.layout.appwidget;
	}

	@Override
	protected Class getPreferenceActivityClass() {
		return FrenchCalendarNarrowPreferenceActivity.class;
	}


}
