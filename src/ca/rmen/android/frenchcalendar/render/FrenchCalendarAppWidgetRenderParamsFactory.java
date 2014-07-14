package ca.rmen.android.frenchcalendar.render;

import ca.rmen.android.frenchcalendar.Constants.WidgetType;
import ca.rmen.android.frenchcalendar.R;

public class FrenchCalendarAppWidgetRenderParamsFactory {

    private final static int[] NARROW_SCROLL_RESOURCE_IDS = new int[] { R.drawable.vscroll1, R.drawable.vscroll2, R.drawable.vscroll3, R.drawable.vscroll4,
            R.drawable.vscroll5, R.drawable.vscroll6, R.drawable.vscroll7, R.drawable.vscroll8, R.drawable.vscroll9, R.drawable.vscroll10,
            R.drawable.vscroll11, R.drawable.vscroll12, R.drawable.vscroll13, };
    private final static int[] WIDE_SCROLL_RESOURCE_IDS = new int[] { R.drawable.hscroll1, R.drawable.hscroll2, R.drawable.hscroll3, R.drawable.hscroll4,
            R.drawable.hscroll5, R.drawable.hscroll6, R.drawable.hscroll7, R.drawable.hscroll8, R.drawable.hscroll9, R.drawable.hscroll10,
            R.drawable.hscroll11, R.drawable.hscroll12, R.drawable.hscroll13, };

    public static FrenchCalendarAppWidgetRenderParams getRenderParams(WidgetType widgetType) {
        switch (widgetType) {
            case WIDE:
                return new FrenchCalendarAppWidgetRenderParams(R.layout.appwidget_wide, R.dimen.wide_widget_width, R.dimen.wide_widget_height,
                        R.dimen.wide_widget_text_width, NARROW_SCROLL_RESOURCE_IDS);
            case NARROW:
            default:
                return new FrenchCalendarAppWidgetRenderParams(R.layout.appwidget, R.dimen.narrow_widget_width, R.dimen.narrow_widget_height,
                        R.dimen.narrow_widget_text_width, WIDE_SCROLL_RESOURCE_IDS);
        }
    }

}
