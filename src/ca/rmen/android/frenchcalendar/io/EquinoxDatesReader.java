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

/**
 * @author calvarez Reads in a CSV file with timestamps for autumn equinoxes for
 *         a list of years. The CSV file has three columns: Year, Date, Time.
 *         Year: the year for which we want to know the equinox time. Date: the
 *         date (day) of the equinox. Time: a timestamp (with timezone) of the
 *         equinox moment.
 */
public class EquinoxDatesReader {

	CSVReader reader;
	private static final String COL_YEAR = "Year";
	private static final String COL_DAY = "Date";
	private static final String COL_TIME = "Time";
	private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss z";
	public static final TimeZone TIMEZONE_PARIS = TimeZone.getTimeZone("Europe/Paris");
	
	private final SimpleDateFormat sdf;

	Map<Integer, Integer> equinoxDates = new HashMap<Integer, Integer>();

	public EquinoxDatesReader(InputStream is) throws IOException {
		sdf = new SimpleDateFormat(DATE_FORMAT);
		reader = new CSVReader(is);
		int line = 0;
		while (reader.next()) {
			line++;
			String yearStr = reader.getValue(COL_YEAR);
			String dateStr = reader.getValue(COL_DAY);
			String timeStr = reader.getValue(COL_TIME);
			String timestampStr = dateStr + " " + timeStr;
			int year = Integer.parseInt(yearStr);
			try {

				// The timezone of the equinox moment.
				Date date = sdf.parse(timestampStr);

				// Determine the date (without time) in Paris, for the equinox
				// moment.

				// Create a date object in the Paris timezone.
				Calendar dateParis = Calendar
						.getInstance(TIMEZONE_PARIS);
				// Set the equinox moment in the Paris timezone
				dateParis.setTimeInMillis(date.getTime());

				// Get the day of month of the equinox. We assume we always know
				// the month (for autumn, it is September), so we only need to
				// store the day.
				int day = dateParis.get(Calendar.DAY_OF_MONTH);
				equinoxDates.put(year, day);
			} catch (ParseException e) {
				Log.d(getClass().getName(), "Error reading equinox for line "
						+ line, e);
			}
		}
	}

	public Map<Integer, Integer> getEquinoxDates() {
		return Collections.unmodifiableMap(equinoxDates);
	}
}
