package ca.rmen.android.frenchcalendar;

import java.io.InputStream;
import java.util.GregorianCalendar;

import ca.rmen.android.frenchcalendar.common.FrenchCalendarDate;
import ca.rmen.android.frenchcalendar.common.FrenchCalendarUtil;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

public class FrenchCalendarAppWidget extends AppWidgetProvider {

	private FrenchCalendarUtil util = null;
	/*
	
	@Override
	public void onReceive(Context context, Intent intent) {
		update(context);
		super.onReceive(context, intent);
	}
*/
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		for(int appWidgetId : appWidgetIds)
			update(context, appWidgetManager, appWidgetId);
		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}
	
	private void init(Context context)
	{
		if(util == null)
		{
	        final InputStream equinoxFile = context.getResources().openRawResource(R.raw.equinoxdates);
			
			util = new FrenchCalendarUtil(equinoxFile, FrenchCalendarUtil.MODE_ROMME);
		}
	}
	private void update(Context context, final AppWidgetManager appWidgetManager, final int appWidgetId)
	{
		Log.d(getClass().getName(),"init...");
		init(context);
        Log.d(getClass().getName(), "update");
        final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.appwidget);
        GregorianCalendar now = new GregorianCalendar();
        FrenchCalendarDate frenchDate = util.getDate(now);
        String frenchDateStr = frenchDate.toString();
        Log.d(getClass().getName(), frenchDateStr);
        views.setTextViewText(R.id.textView1, frenchDateStr);
        appWidgetManager.updateAppWidget(appWidgetId, views);
		
	}

}