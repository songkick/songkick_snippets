package com.songkick.snippets.logic;

import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.Weeks;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.songkick.common.shared.SKDateFormat;
import com.songkick.common.util.Debug;
import com.songkick.snippets.client.model.HolidayDate;
import com.songkick.snippets.server.data.DataStorage;

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

	/**
	 * Return true iff this is a week in which the Monday is a bank holiday in the
	 * UK, or other Songkick-wide holiday. THis is used by the ReminderServlet to
	 * figure out if reminders should go out on Tuesday instead of Monday
	 * 
	 * @return
	 */
	public static boolean weekHasHolidayMonday(DataStorage dataStore) {
		List<HolidayDate> holidays = dataStore.getAllHolidayDates();

		for (HolidayDate holiday : holidays) {
			DateTime date = getDateFromString(holiday.getDate());

			if (isMondayOfThisWeek(date)) {
				return true;
			}
		}

		return false;
	}

	private static boolean isMondayOfThisWeek(DateTime date) {
		if (date.getDayOfWeek() != DateTimeConstants.MONDAY) {
			return false;
		}

		DateTime now = new DateTime();

		// Is date in the same year as now?
		if (date.getWeekyear() != now.getWeekyear()) {
			return false;
		}

		// Is date in the same week as now?
		if (date.getWeekOfWeekyear() != now.getWeekOfWeekyear()) {
			return false;
		}

		return true;
	}

	public static DateTime getMondayOfThisWeek() {
		DateTime now = new DateTime();

		return now.withDayOfWeek(DateTimeConstants.MONDAY);
	}

	public static int getDayOfWeek() {
		return new DateTime().getDayOfWeek();
	}

	/**
	 * Return today's date as a String in the standard format
	 * 
	 * @return
	 */
	public static String getCurrentDate() {
		DateTime dt = new DateTime();
		return formatDate(dt);
	}
	
	public static String getYesterday() {
		DateTime dt = new DateTime();
		DateTime result = dt.dayOfWeek().addToCopy(-1);
		return formatDate(result);
	}

	public static String formatDate(DateTime dt) {
		DateTimeFormatter format = DateTimeFormat.forPattern(SKDateFormat.FORMAT);

		return format.print(dt);
	}

	public static DateTime getDateFromString(String dateString) {
		DateTimeFormatter formatter = DateTimeFormat
				.forPattern(SKDateFormat.FORMAT);
		
		return formatter.parseDateTime(dateString);
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

		DateTimeFormatter formatter = DateTimeFormat
				.forPattern(SKDateFormat.FORMAT);
		return start.toString(formatter);
	}

	public static DateTime weekToDateTime(Long week) {
		DateTime start = new DateTime(WEEK1);
		start = start.plusDays((int) (7 * week));

		return start;
	}
}
