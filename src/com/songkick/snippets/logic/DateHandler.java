package com.songkick.snippets.logic;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class DateHandler {
	// This is the base date for all week calculations - epoch is the first Monday of 2010
	private static final DateTime WEEK1 = new DateTime("2010-01-03T23:59");

	/**
	 * Get the week we are in right now
	 * 
	 * @return
	 */
	public static Long getCurrentWeek() {
		DateTime now = new DateTime();
		Period timePeriod = new Period(WEEK1, now);

		return (long) timePeriod.getWeeks();
	}

	/**
	 * Convert a week number to the date of the start of that week
	 * 
	 * @param week
	 * @return
	 */
	public static String weekToDate(Long week) {
		DateTime start = new DateTime(WEEK1);
		start = start.plusDays((int) (7 * week));

		DateTimeFormatter formatter = DateTimeFormat.forPattern("MM/dd/yyyy");
		return start.toString(formatter);
	}

}
