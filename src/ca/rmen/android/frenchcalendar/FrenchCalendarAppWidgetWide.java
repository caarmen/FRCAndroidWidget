package ca.rmen.android.frenchcalendar;

import ca.rmen.android.frenchcalendar.FrenchCalendarAppWidgetRenderer.FrenchCalendarAppWidgetRenderParams;

public class FrenchCalendarAppWidgetWide extends FrenchCalendarAppWidget { // NO_UCD (use default)
    private final static int[] SCROLL_RESOURCE_IDS = new int[] { R.drawable.hscroll1, R.drawable.hscroll2, R.drawable.hscroll3, R.drawable.hscroll4,
            R.drawable.hscroll5, R.drawable.hscroll6, R.drawable.hscroll7, R.drawable.hscroll8, R.drawable.hscroll9, R.drawable.hscroll10,
            R.drawable.hscroll11, R.drawable.hscroll12, R.drawable.hscroll13, };

    @Override
    protected FrenchCalendarAppWidgetRenderParams getRenderParams() {
        return new FrenchCalendarAppWidgetRenderParams(R.layout.appwidget_wide, R.dimen.wide_widget_width, R.dimen.wide_widget_height,
                R.dimen.wide_widget_text_width, SCROLL_RESOURCE_IDS);
    }

}
