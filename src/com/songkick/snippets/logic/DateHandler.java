package com.songkick.snippets.logic;

import org.joda.time.DateTime;
import org.joda.time.Weeks;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.songkick.snippets.shared.SKDateFormat;
import com.songkick.snippets.util.Debug;

public class DateHandler {
	// This is the base date for all week calculations - epoch is the first Monday
	// of 2011
	private static final DateTime WEEK1 = new DateTime("2011-01-03T23:59");

	/**
	 * Get the week we are in right now
	 * 
	 * @return
	 */
	public static Long getCurrentWeek() {
		DateTime now = new DateTime();

		return getWeekNumber(now);
	}

	public static Long getWeekNumber(DateTime date) {
		if (date == null) {
			date = new DateTime();
		}

		Weeks numWeeks = Weeks.weeksBetween(WEEK1, date);
		long weeks = numWeeks.getWeeks();

		if (weeks < 1) {
			Debug.error("Incorrect week number generated (" + weeks + ") for now="
					+ date);
		}

		return weeks;
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

		DateTimeFormatter formatter = DateTimeFormat.forPattern(SKDateFormat.FORMAT);
		return start.toString(formatter);
	}
	
	public static DateTime weekToDateTime(Long week) {
		DateTime start = new DateTime(WEEK1);
		start = start.plusDays((int) (7 * week));
		
		return start;
	}
}
