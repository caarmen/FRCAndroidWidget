package ca.rmen.android.frenchcalendar.common;

/**
 * A timestamp in the french revolutionary calendar. Months are from 1 to 13,
 * days are from 1 to 30, hours are from 1 to 10, minutes and seconds are from 1
 * to 100.
 * 
 * @author calvarez
 * 
 */
public class FrenchCalendarDate {
	public int year;
	public int month;
	public int day;
	public int hour;
	public int minute;
	public int second;

	public FrenchCalendarDate(int year, int month, int day, int hour,
			int minute, int second) {
		this.year = year;
		this.month = month;
		this.day = day;
		this.hour = hour;
		this.minute = minute;
		this.second = second;
	}

	/**
	 * @return a number from 1 to 10.
	 */
	public int getDayInWeek() {
		int result= this.day - (10 * (this.day / 10));
		if(result == 0)
			result = 10;
		return result;
	}

	/**
	 * @return a number from 1 to 3.
	 */
	public int getWeekInMonth() {
		return this.day % 10;
	}

	public String toString() {
		return year + "-" + (month) + "-" + (day) + " " + hour + ":" + minute
				+ ":" + second;
	}
}
