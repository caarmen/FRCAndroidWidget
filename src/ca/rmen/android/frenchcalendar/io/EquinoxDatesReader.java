package ca.rmen.android.frenchcalendar.io;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import android.util.Log;

import ca.rmen.android.frenchcalendar.common.FrenchCalendarUtil;
import ca.rmen.android.frenchcalendar.lib.CSVReader;

public class EquinoxDatesReader {

	CSVReader reader;
	private static final String COL_YEAR = "Year";
	private static final String COL_DAY = "Date";
	private static final String COL_TIME = "Time";
	private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss z";
	private final SimpleDateFormat sdf;

	Map<Integer, Long> equinoxDates = new HashMap<Integer, Long>();

	public EquinoxDatesReader(InputStream is) throws IOException {
		sdf = new SimpleDateFormat(DATE_FORMAT);
		reader = new CSVReader(is);
		int line = 0;
		TimeZone parisTimeZone = TimeZone.getTimeZone(FrenchCalendarUtil.TIMEZONE_PARIS);
		while (reader.next()) {
			line++;
			String yearStr = reader.getValue(COL_YEAR);
			String dateStr = reader.getValue(COL_DAY);
			String timeStr = reader.getValue(COL_TIME);
			String timestampStr = dateStr + " " + timeStr;
			int year = Integer.parseInt(yearStr);
			try {

				Date date = sdf.parse(timestampStr);
				Calendar dateParis = Calendar.getInstance(parisTimeZone);
				dateParis.setTimeInMillis(date.getTime());
				dateParis.set(Calendar.HOUR_OF_DAY, 0);
				dateParis.set(Calendar.MINUTE, 0);
				dateParis.set(Calendar.SECOND, 0);
				dateParis.set(Calendar.MILLISECOND, 0);
				equinoxDates.put(year, dateParis.getTime().getTime());
			} catch (ParseException e) {
				Log.d(getClass().getName(), "Error reading equinox for line "
						+ line, e);
			}
		}
	}

	public Map<Integer, Long> getEquinoxDates() {
		return Collections.unmodifiableMap(equinoxDates);
	}
}
