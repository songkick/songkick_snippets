package com.songkick.snippets.server.data;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.songkick.snippets.logic.DateHandler;
import com.songkick.snippets.model.User;

public abstract class DataStorageBase implements DataStorage {
	private static final DateTimeFormatter OLD_FORMAT = DateTimeFormat
			.forPattern("MM/dd/yyyy");

	/**
	 * Parse a date from a string. Because the backend and the UI disagreed for a
	 * while about the standard date format to use, the code has to be able to
	 * parse either format
	 * 
	 * @param dateString
	 * @param defaultDate
	 * @return
	 */
	private DateTime parseDate(String dateString, String defaultDateString) {
		if (dateString == null || dateString.length() < 1) {
			return DateHandler.getDateFromString(defaultDateString);
		}
		try {
			return DateHandler.getDateFromString(dateString);
		} catch (Exception e) {
			try {
				return OLD_FORMAT.parseDateTime(dateString);
			} catch (Exception e1) {
			}
		}
		return DateHandler.getDateFromString(defaultDateString);
	}

	/**
	 * Is this user current - is the current date after their start date and
	 * before their end date?
	 * 
	 * @param user
	 * @param week
	 * @return
	 */
	protected boolean isCurrentUser(User user, Long week) {
		Long startWeek = DateHandler.getWeekNumber(parseDate(user.getStartDate(), "Jan 11, 2011"));
		Long endWeek = DateHandler.getWeekNumber(parseDate(user.getEndDate(), "Dec 31, 9999"));
		
		if (startWeek.compareTo(week)<=0 && endWeek.compareTo(week)>=0) {
			return true;
		}
		return false;
	}
}
