package com.songkick.snippets.server.data;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.songkick.snippets.logic.DateHandler;
import com.songkick.snippets.model.User;
import com.songkick.snippets.util.Debug;

public class DataStorageBase {
	private static final DateTimeFormatter DATE_FORMAT = DateTimeFormat.forPattern("MMM dd, yyyy");
	
	private DateTime createDate(int month, int day, int year) {
		DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy/MM/dd");
		return formatter.parseDateTime(year + "/" + month + "/" + day);
	}

	protected boolean isCurrentUser(User user, Long week) {
		Debug.log("Checking user " + user + " is current");
		DateTime startDate = createDate(1, 1, 1900);

		if (user.getStartDate() != null) {
			try {
				startDate = DATE_FORMAT.parseDateTime(user.getStartDate());
			} catch (Exception e) {
			}
		}

		DateTime endDate = createDate(1, 1, 9999);
		if (user.getEndDate() != null) {
			Debug.log("user.getEndDate is " + user.getEndDate());
			try {
				endDate = DATE_FORMAT.parseDateTime(user.getEndDate());
			} catch (Exception e) {
				Debug.log("Parsed to: " + endDate);
			}
		}
		
		DateTime now = DateHandler.weekToDateTime(week);

		Debug.log("Date is " + now);
		
		if (now.isAfter(startDate) && now.isBefore(endDate)) {
			Debug.log("User is current");
			return true;
		}
		
		Debug.log("User is not current");

		return false;
	}
}
