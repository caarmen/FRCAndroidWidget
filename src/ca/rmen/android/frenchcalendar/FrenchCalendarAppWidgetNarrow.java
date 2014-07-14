package ca.rmen.android.frenchcalendar;

public class FrenchCalendarAppWidgetNarrow extends FrenchCalendarAppWidget { // NO_UCD (use default)

    @Override
    protected int getLayoutResourceId() {
        return R.layout.appwidget;
    }

    @Override
    protected Class<?> getPreferenceActivityClass() {
        return FrenchCalendarNarrowPreferenceActivity.class;
    }

    @Override
    protected int getWidthResourceId() {
        return R.dimen.narrow_widget_width;
    }

    @Override
    protected int getHeightResourceId() {
        return R.dimen.narrow_widget_height;
    }

    @Override
    protected int getTextWidthResourceId() {
        return R.dimen.narrow_widget_text_width;
    }

}
