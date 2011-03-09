package com.songkick.snippets.logic;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.Query;
import com.songkick.snippets.model.Snippet;
import com.songkick.snippets.model.User;
import com.songkick.snippets.util.Debug;

/**
 * Does the core work of handling incoming snippet emails
 * 
 * @author dancrow
 */
public class MailHandler {

	public MailHandler() {
		// Register the database classes we will use
		ObjectifyService.register(User.class);
		ObjectifyService.register(Snippet.class);
	}
	
	public void processMail(String from, String emailBody) {
		User user = getOrCreateUser(from);

		Debug.log("Got user: " + user);

		Snippet snippet = new Snippet(user, emailBody);

		snippet.setDate(getDateNow());
		snippet.setWeekNumber(DateHandler.getCurrentWeek());

		Debug.dbLog("Created snippet from email: " + snippet);

		Objectify ofy = ObjectifyService.begin();
		ofy.put(snippet);
	}

	public String getDateNow() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
		Date date = new Date();
		return dateFormat.format(date);
	}
	
	public User getOrCreateUser(String userEmail) {
		Debug.log("From " + userEmail);

		Objectify ofy = ObjectifyService.begin();
		Query<User> existingUsers = ofy.query(User.class);

		for (User user : existingUsers) {
			if (user.matchesEmail(userEmail)) {
				return user;
			}
		}

		// Didn't find an existing user, so create a new one
		Debug.log("Creating new user");
		User newUser = new User();
		newUser.setEmailAddress(userEmail);
		ofy.put(newUser);
		return newUser;
	}
}
