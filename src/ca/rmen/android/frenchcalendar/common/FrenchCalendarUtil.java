package ca.rmen.android.frenchcalendar.common;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.TimeZone;

import ca.rmen.android.frenchcalendar.io.EquinoxDatesReader;

public class FrenchCalendarUtil {

	public static final String TIMEZONE_PARIS = "Europe/Paris";
	public static final int MODE_EQUINOX = 0;
	public static final int MODE_ROMME = 1;
	public static final int MODE_CONTINUOUS = 2;
	public static final int MODE_128 = 3;
	private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss z";
	private static final String FRENCH_ERA_BEGIN = "1792-09-22 00:00:00 CET";
	private static final String FRENCH_ERA_END = "1811-09-23 00:00:00 CET";
	private static final long NUM_MILLISECONDS_IN_DAY = 1000 * 60 * 60 * 24;

	private Date frenchEraBegin;
	private Date frenchEraEnd;
	private Map<Integer, Long> autumnEquinoxes;
	private int mode;

	public FrenchCalendarUtil(InputStream is, int mode) {
		setMode(mode);
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
		try {
			frenchEraBegin = sdf.parse(FRENCH_ERA_BEGIN);
			frenchEraEnd = sdf.parse(FRENCH_ERA_END);
		} catch (ParseException e) {
			debug("Error reading French epoch " + FRENCH_ERA_BEGIN, e);
		}

		EquinoxDatesReader reader = null;
		try {
			reader = new EquinoxDatesReader(is);
		} catch (IOException e) {
			debug("Error loading equinox dates", e);
		}
		if (reader != null)
			autumnEquinoxes = reader.getEquinoxDates();
	}

	public void setMode(int mode) {
		this.mode = mode;
	}

	public FrenchCalendarDate getDate(GregorianCalendar gregorianDate) {
		TimeZone parisTimeZone = TimeZone.getTimeZone(TIMEZONE_PARIS);
		Calendar gregorianDateParis = Calendar.getInstance(parisTimeZone);
		gregorianDateParis.setTimeInMillis(gregorianDate.getTimeInMillis());

		if (mode == MODE_EQUINOX
				|| (gregorianDateParis.getTime().after(frenchEraBegin) && gregorianDateParis
						.getTime().before(frenchEraEnd))) {
			return getDateEquinox(gregorianDateParis);
		} else if (mode == MODE_ROMME) {
			return getDateRomme(gregorianDateParis);
		} else {
			throw new IllegalArgumentException("Can't convert date "
					+ gregorianDate + " in mode " + mode);
		}

	}

	private FrenchCalendarDate getDateEquinox(Calendar gregorianDateParis) {

		int gyear = gregorianDateParis.get(Calendar.YEAR);

		Long gAutumnEquinoxTimestamp = autumnEquinoxes.get(gyear);
		if (gAutumnEquinoxTimestamp == null)
			throw new IllegalArgumentException("Date not supported: "
					+ gregorianDateParis);
		TimeZone parisTimeZone = TimeZone.getTimeZone(TIMEZONE_PARIS);
		Calendar gAutumnEquinox = Calendar.getInstance(parisTimeZone);
		
		gAutumnEquinox.setTimeInMillis(gAutumnEquinoxTimestamp);

		Calendar g1stVendemiaire = gAutumnEquinox;
		// Case 1, date from January to September
		if (gregorianDateParis.compareTo(gAutumnEquinox) < 0) {
			Long g1stVendemiaireTimestamp = autumnEquinoxes.get(gyear - 1);
			if (g1stVendemiaireTimestamp == null)
				throw new IllegalArgumentException("Date not supported: "
						+ gregorianDateParis);

			g1stVendemiaire.setTimeInMillis(g1stVendemiaireTimestamp);
		}
		// Case 2, date from September to December
		else {

		}
		int frenchYear = g1stVendemiaire.get(Calendar.YEAR) - (frenchEraBegin.getYear() + 1900)
				+ 1;
		int numberDaysInFrenchYear = (int) ((gregorianDateParis
				.getTimeInMillis() - g1stVendemiaire.getTimeInMillis()) / NUM_MILLISECONDS_IN_DAY);
		FrenchCalendarDate result = getFrenchDate(frenchYear, numberDaysInFrenchYear);
		return result;
	}

	private FrenchCalendarDate getDateRomme(Calendar gregorianDateParis) {
		long numMillisSinceEndOfFrenchEra = (gregorianDateParis.getTimeInMillis() + gregorianDateParis.get(Calendar.DST_OFFSET) - frenchEraEnd
				.getTime());
		long fakeEndFrenchEraTimestamp = new GregorianCalendar(10020, 0, 1)
				.getTimeInMillis();
		long fakeFrenchTimestamp = fakeEndFrenchEraTimestamp
				+ numMillisSinceEndOfFrenchEra;
		TimeZone parisTimeZone = TimeZone.getTimeZone(TIMEZONE_PARIS);
		
		Calendar fakeFrenchDate = Calendar.getInstance(parisTimeZone);
		fakeFrenchDate.setTimeInMillis(fakeFrenchTimestamp);
		int frenchYear = fakeFrenchDate.get(Calendar.YEAR);
		int frenchDayInYear = fakeFrenchDate.get(Calendar.DAY_OF_YEAR);
		FrenchCalendarDate result = getFrenchDate(frenchYear-10000, frenchDayInYear-1);
		return result;
	}

	private FrenchCalendarDate getFrenchDate(int frenchYear,
			int numberDaysInFrenchYear) {
		int numberMonthInFrenchYear = numberDaysInFrenchYear / 30;
		int numberDaysInFrenchMonth = numberDaysInFrenchYear
				- (numberMonthInFrenchYear * 30);

		FrenchCalendarDate result = new FrenchCalendarDate(frenchYear,
				numberMonthInFrenchYear + 1, numberDaysInFrenchMonth + 1, 0, 0,
				0);
		return result;
	}

	private void debug(Object o) {
		debug(o, null);
	}

	private void debug(Object o, Throwable t) {
		System.out.println(getClass().getName() + ": " + o);
		if (t != null)
			t.printStackTrace();
	}
}
