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
	@Override
	protected int getDrawableResourceIdForMonth(int month) {
		if (month == 1)
			return R.drawable.hscroll1;
		if (month == 2)
			return R.drawable.hscroll2;
		if (month == 3)
			return R.drawable.hscroll3;
		if (month == 4)
			return R.drawable.hscroll4;
		if (month == 5)
			return R.drawable.hscroll5;
		if (month == 6)
			return R.drawable.hscroll6;
		if (month == 7)
			return R.drawable.hscroll7;
		if (month == 8)
			return R.drawable.hscroll8;
		if (month == 9)
			return R.drawable.hscroll9;
		if (month == 10)
			return R.drawable.hscroll10;
		if (month == 11)
			return R.drawable.hscroll11;
		if (month == 12)
			return R.drawable.hscroll12;
		if (month == 13)
			return R.drawable.hscroll13;
		return R.drawable.hscroll;
	}	
}
