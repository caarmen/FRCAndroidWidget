package ca.rmen.android.frenchcalendar;

import ca.rmen.android.frenchcalendar.FrenchCalendarAppWidgetRenderer.FrenchCalendarAppWidgetRenderParams;

public class FrenchCalendarAppWidgetNarrow extends FrenchCalendarAppWidget { // NO_UCD (use default)

    private final static int[] SCROLL_RESOURCE_IDS = new int[] { R.drawable.vscroll1, R.drawable.vscroll2, R.drawable.vscroll3, R.drawable.vscroll4,
            R.drawable.vscroll5, R.drawable.vscroll6, R.drawable.vscroll7, R.drawable.vscroll8, R.drawable.vscroll9, R.drawable.vscroll10,
            R.drawable.vscroll11, R.drawable.vscroll12, R.drawable.vscroll13, };

    @Override
    protected FrenchCalendarAppWidgetRenderParams getRenderParams() {
        return new FrenchCalendarAppWidgetRenderParams(R.layout.appwidget, R.dimen.narrow_widget_width, R.dimen.narrow_widget_height,
                R.dimen.narrow_widget_text_width, SCROLL_RESOURCE_IDS);
    }

}
