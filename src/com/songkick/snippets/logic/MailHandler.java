package com.songkick.snippets.logic;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.songkick.snippets.model.Snippet;
import com.songkick.snippets.model.User;
import com.songkick.snippets.server.data.DataStorage;
import com.songkick.snippets.util.Debug;

/**
 * Does the core work of handling incoming snippet emails
 * 
 * @author dancrow
 */
public class MailHandler {
	// Date formats use by emails: see http://www.w3.org/Protocols/rfc822/
	private static final DateTimeFormatter[] FORMATS = new DateTimeFormatter[] {
			DateTimeFormat.forPattern("EEE, d MMM yy HH:mm:ss z"),
			DateTimeFormat.forPattern("EEE, d MMM yy HH:mm z"),
			DateTimeFormat.forPattern("EEE, d MMM yyyy HH:mm:ss z"),
			DateTimeFormat.forPattern("EEE, d MMM yyyy HH:mm z"),
			DateTimeFormat.forPattern("d MMM yy HH:mm z"),
			DateTimeFormat.forPattern("d MMM yy HH:mm:ss z"),
			DateTimeFormat.forPattern("d MMM yyyy HH:mm z"),
			DateTimeFormat.forPattern("d MMM yyyy HH:mm:ss"),
			DateTimeFormat.forPattern("EEE, d MMM yy HH:mm:ss"),
			DateTimeFormat.forPattern("EEE, d MMM yy HH:mm"),
			DateTimeFormat.forPattern("EEE, d MMM yyyy HH:mm:ss"),
			DateTimeFormat.forPattern("EEE, d MMM yyyy HH:mm"),
			DateTimeFormat.forPattern("d MMM yy HH:mm"),
			DateTimeFormat.forPattern("d MMM yy HH:mm:ss"),
			DateTimeFormat.forPattern("d MMM yyyy HH:mm"),
			DateTimeFormat.forPattern("d MMM yyyy HH:mm:ss") };

	public void processMail(String from, String date, String emailBody,
			DataStorage dataStore) {
		User user = getOrCreateUser(from, dataStore);

		Debug.log("Got user: " + user);

		Snippet snippet = new Snippet(user, emailBody);

		snippet.setDate(date);
		snippet.setWeekNumber(DateHandler.getWeekNumber(formatDate(date)));

		Debug.log("Created snippet from email: " + snippet);

		dataStore.save(snippet);
	}

	public String getDateNow() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
		Date date = new Date();
		return dateFormat.format(date);
	}

	private DateTime formatDate(String dateString) {
		System.out.println("Parsing date string " + dateString);

		for (DateTimeFormatter format : FORMATS) {
			DateTime date;
			try {
				date = format.parseDateTime(dateString);
				return date;
			} catch (Exception e) {
				System.err.println("Failed with exception: " + e);
			}
		}

		return null;
	}

	public User getOrCreateUser(String userEmail, DataStorage dataStore) {
		Debug.log("From " + userEmail);

		List<User> existingUsers = dataStore.getUsers();

		for (User user : existingUsers) {
			if (user.matchesEmail(userEmail)) {
				return user;
			}
		}

		// Didn't find an existing user, so create a new one
		Debug.log("Creating new user");
		User newUser = new User();
		newUser.setEmailAddress(userEmail);

		dataStore.save(newUser);
		return newUser;
	}
}
