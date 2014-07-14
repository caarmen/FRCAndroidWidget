package ca.rmen.android.frenchcalendar;

import ca.rmen.android.frenchcalendar.FrenchCalendarAppWidgetRenderer.FrenchCalendarAppWidgetRenderParams;

public class FrenchCalendarAppWidgetNarrow extends FrenchCalendarAppWidget { // NO_UCD (use default)

    @Override
    protected FrenchCalendarAppWidgetRenderParams getRenderParams() {
        return new FrenchCalendarAppWidgetRenderParams(R.layout.appwidget, R.dimen.narrow_widget_width, R.dimen.narrow_widget_height,
                R.dimen.narrow_widget_text_width);
    }

}
