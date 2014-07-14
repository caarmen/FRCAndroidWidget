package ca.rmen.android.frenchcalendar.render;

public class FrenchCalendarAppWidgetRenderParams {
    final int layoutResourceId;
    final int widthResourceId;
    final int heightResourceId;
    final int textViewableWidthResourceId;
    final int[] scrollResourceIds;

    public FrenchCalendarAppWidgetRenderParams(int layoutResourceId, int widthResourceId, int heightResourceId, int textViewableWidthResourceId,
            int[] scrollResourceIds) {
        this.layoutResourceId = layoutResourceId;
        this.widthResourceId = widthResourceId;
        this.heightResourceId = heightResourceId;
        this.textViewableWidthResourceId = textViewableWidthResourceId;
        this.scrollResourceIds = scrollResourceIds;
    }
}