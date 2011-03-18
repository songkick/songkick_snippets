package com.songkick.snippets.logic;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;

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
	
	public void processMail(String from, String date, String emailBody, DataStorage dataStore) {
		User user = getOrCreateUser(from, dataStore);

		Debug.log("Got user: " + user);

		Snippet snippet = new Snippet(user, emailBody);

		snippet.setDate(date);
		snippet.setWeekNumber(DateHandler.getWeekNumber(new DateTime(date)));

		Debug.log("Created snippet from email: " + snippet);

		dataStore.save(snippet);
	}

	public String getDateNow() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
		Date date = new Date();
		return dateFormat.format(date);
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
