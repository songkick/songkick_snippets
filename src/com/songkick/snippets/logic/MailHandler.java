package com.songkick.snippets.logic;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.Query;
import com.songkick.snippets.model.Snippet;
import com.songkick.snippets.model.User;
import com.songkick.snippets.util.Debug;
import com.songkick.snippets.util.EmailParser;

/**
 * Does the core work of handling incoming snippet emails
 * 
 * @author dancrow
 */
public class MailHandler {
	private MailSender sender = new MailSender();

	public MailHandler() {
		// Register the database classes we will use
		ObjectifyService.register(User.class);
		ObjectifyService.register(Snippet.class);
	}

	/**
	 * Handle an incoming email - create a new Snippet object from this email
	 * 
	 * @param email
	 */
	public void handleEmail(MimeMessage email) {
		Debug.log("Incoming email");

		User user = getOrCreateUser(email);

		Debug.log("Got user: " + user);

		String emailBody = getMessageBody(email);

		Debug.log("Parsed email body: " + emailBody);

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

	private String getAddress(MimeMessage email) {
		try {
			return email.getFrom()[0].toString();
		} catch (MessagingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public User getOrCreateUser(MimeMessage email) {
		String userEmail = getAddress(email);
		
		return getOrCreateUser(userEmail);
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

	private String getMessageBody(MimeMessage mimeMessage) {
		Debug.log("getMessageBody start");
		String from = null;
		try {
			from = mimeMessage.getFrom()[0].toString();
			EmailParser parser = new EmailParser(mimeMessage);
			Debug.log("Got parser");
			String body = parser.getBody();
			Debug.log("Got plain text body: " + body);
			
			if (body != null) {
				body = body.replaceAll("\n", "<br>");
			}

			if (body == null) {
				Debug.dbLog("Could not retrieve an email body from "
						+ mimeMessage.getFrom()[0]);
				Debug.error("Could not retrieve an email body from "
						+ mimeMessage.getFrom()[0]);
				sender
						.sendEmail(
								from,
								"Could not process your email",
								"Your email could not be processed. Please try re-sending using plain text rather than HTML."
										+ "If this error persists, please contact dancrow@songkick.com");
			}

			return body;
		} catch (Exception e) {
			if (from != null) {
				sender
						.sendEmail(
								from,
								"Could not process your email",
								"Ooops, sorry, I couldn't read your email. Could you try re-sending using plain text rather than HTML? "
										+ "If that doesn't work, please contact dancrow@songkick.com. Thanks.");
			}
			Debug.log("getMessageBody threw exception: " + e);
			Debug.dbLog("getMessageBody threw exception: " + e);
			e.printStackTrace();
		}
		return null;
	}
}
