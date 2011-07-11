package com.songkick.snippets.server.data;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.songkick.common.shared.SKDateFormat;
import com.songkick.snippets.logic.DateHandler;
import com.songkick.snippets.model.User;

public class DataStorageBase {
	private static final DateTimeFormatter DATE_FORMAT = DateTimeFormat
			.forPattern(SKDateFormat.FORMAT);
	private static final DateTimeFormatter OLD_FORMAT = DateTimeFormat
			.forPattern("MM/dd/yyyy");

	private DateTime createDate(int month, int day, int year) {
		DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy/MM/dd");
		return formatter.parseDateTime(year + "/" + month + "/" + day);
	}

	/**
	 * Parse a date from a string. Because the backend and the UI disagreed for a
	 * while about the standard date format to use, the code has to be able to
	 * parse either format
	 * 
	 * @param dateString
	 * @param defaultDate
	 * @return
	 */
	private DateTime parseDate(String dateString, DateTime defaultDate) {
		if (dateString == null || dateString.length() < 1) {
			return defaultDate;
		}
		try {
			return DATE_FORMAT.parseDateTime(dateString);
		} catch (Exception e) {
			try {
				return OLD_FORMAT.parseDateTime(dateString);
			} catch (Exception e1) {
			}
		}
		return defaultDate;
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
		DateTime startDate = parseDate(user.getStartDate(), createDate(1, 1, 1900));
		DateTime endDate = parseDate(user.getEndDate(), createDate(1, 1, 9999));
		DateTime now = DateHandler.weekToDateTime(week);

		if (now.isAfter(startDate) && now.isBefore(endDate)) {
			return true;
		}

		return false;
	}
}
