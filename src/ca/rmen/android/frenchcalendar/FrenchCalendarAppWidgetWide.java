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
	@Override
	protected int getWidthResourceId() {
		return R.dimen.wide_widget_width;
	}
	@Override
	protected int getHeightResourceId() {
		return R.dimen.wide_widget_height;
	}
	@Override
	protected int getTextWidthResourceId() {
		return R.dimen.wide_widget_text_width;
	}
}
