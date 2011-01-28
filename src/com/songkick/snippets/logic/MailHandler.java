package com.songkick.snippets.logic;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.Query;
import com.songkick.snippets.model.Snippet;
import com.songkick.snippets.model.User;
import com.songkick.snippets.util.EmailMessage;

/**
 * Does the core work of handling incoming snippet emails
 * 
 * @author dancrow
 */
public class MailHandler {
	private static final Logger log = Logger.getLogger(MailHandler.class
			.getName());

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
		log.severe("Incoming email: " + email);

		User user = getOrCreateUser(email);
		Snippet snippet = new Snippet(user, getMessageBody(email));

		snippet.setDate(getDateNow());
		snippet.setWeekNumber(DateHandler.getCurrentWeek());

		Objectify ofy = ObjectifyService.begin();
		ofy.put(snippet);
	}

	private String getDateNow() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
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

	private User getOrCreateUser(MimeMessage email) {
		String userEmail = getAddress(email);

		Objectify ofy = ObjectifyService.begin();
		Query<User> existingUsers = ofy.query(User.class);

		for (User user : existingUsers) {
			if (userEmail.contains(user.getEmailAddress())) {
				return user;
			}
		}
		
		// Didn't find an existing user, so create a new one
		User newUser = new User();
		newUser.setEmailAddress(userEmail);
		ofy.put(newUser);
		return newUser;
	}

	private String getMessageBody(MimeMessage mimeMessage) {
		try {
			EmailMessage email = new EmailMessage(mimeMessage);
			String body = email.getPlainTextBody().replaceAll("\n", "<br>");
			if (body == null) {
				body = email.getHtmlBody();
			}
			return body;
		} catch (MessagingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
