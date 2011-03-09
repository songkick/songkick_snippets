package com.songkick.snippets.logic;

import static com.google.appengine.api.taskqueue.TaskOptions.Builder.*;

import java.util.ArrayList;
import java.util.List;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.taskqueue.TaskOptions.Method;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.Query;
import com.songkick.snippets.model.Snippet;
import com.songkick.snippets.model.User;
import com.songkick.snippets.util.Debug;

public class ReminderHandler {
	public enum MailType {
		FirstReminder, SecondReminder, Digest
	};

	public ReminderHandler() {
		ObjectifyService.register(Snippet.class);
		ObjectifyService.register(User.class);
	}

	/**
	 * Create a task queue entry to send a single email
	 * 
	 * @param email
	 */
	private void addEmailToQueue(String email, MailType mailType) {
		// Enqueue the reminders
		Queue queue = QueueFactory.getQueue("reminder-queue");

		String url = "/reminderqueue?emaillist=" + cleanEmailAddress(email);

		url += "&type=" + mailType;

		TaskOptions to = withUrl(url).method(Method.GET);

		Debug.log("Queueing to URL " + to.getUrl());
		queue.add(to);
	}

	private String cleanEmailAddress(String email) {
		int addressStart = email.indexOf('<');
		if (addressStart == -1) {
			return email;
		}

		email = email.substring(addressStart + 1, email.length());
		
		int addressEnd = email.indexOf('>');
		if (addressEnd == -1) {
			return email;
		}

		return email.substring(0, addressEnd);
	}

	private void queueRemindersTo(List<User> users, MailType mailType) {
		Debug.log("Users to remind: " + users);

		for (User user : users) {
			addEmailToQueue(user.getEmailAddress(), mailType);
			if (user.getOtherEmails() != null) {
				for (String email : user.getOtherEmails()) {
					addEmailToQueue(email, mailType);
				}
			}
		}
	}

	public void testReminders() {
		List<User> users = new ArrayList<User>();

		Objectify ofy = ObjectifyService.begin();
		Query<User> q = ofy.query(User.class);

		for (User user : q) {
			if (user.getEmailAddress().contains("crow")) {
				users.add(user);
			}
		}

		queueRemindersTo(users, MailType.FirstReminder);
	}

	public List<User> getUsersWithoutSnippet(Long week) {
		List<User> users = getUsers();
		List<User> withCurrentSnippets = getUsersWithSnippetFor(week);

		List<User> toRemind = new ArrayList<User>();

		for (User user : users) {
			boolean hasSnippet = false;
			for (User withSnippetUser : withCurrentSnippets) {
				if (user.getId().equals(withSnippetUser.getId())) {
					hasSnippet = true;
				}
			}

			if (!hasSnippet) {
				toRemind.add(user);
			}
		}

		return toRemind;
	}

	/**
	 * Find all the users who don't have a snippet for this week and send each of
	 * them a reminder email
	 */
	public void generateReminders(MailType type) {
		Long currentWeek = DateHandler.getCurrentWeek();
		List<User> toRemind = getUsersWithoutSnippet(currentWeek);

		queueRemindersTo(toRemind, type);
	}
	
	public void sendDigest() {
		List<User> users = getUsers();
		//List<User> users = getAdminUsers();
		
		queueRemindersTo(users, MailType.Digest);
	}

	/**
	 * Get a list of users who have a snippet for the specified week
	 * 
	 * @param week
	 * @return
	 */
	private List<User> getUsersWithSnippetFor(Long week) {
		List<User> users = new ArrayList<User>();

		Objectify ofy = ObjectifyService.begin();
		Query<Snippet> q = ofy.query(Snippet.class).filter("weekNumber", week);

		for (Snippet snippet : q) {
			User user = ofy.get(snippet.getUser());
			if (!users.contains(user)) {
				users.add(user);
			}
		}
		return users;
	}

	/**
	 * Send a reminder email to the specified email address, without checking if
	 * the user is due for a reminder email. Used by the admin interface to test
	 * the reminder email content
	 * 
	 * @param emailAddress
	 */
	public void remindUser(String emailAddress, MailType type) {
		Debug.log("remindUser. emailAddress: " + emailAddress);
		Objectify ofy = ObjectifyService.begin();
		Query<User> q = ofy.query(User.class);

		Debug.log("Found matching users: " + q.list());
		List<User> users = new ArrayList<User>();
		for (User user : q) {
			if (user.getEmailAddress().equals(emailAddress)) {
				users.add(user);
			}
		}

		queueRemindersTo(users, type);
	}

	/**
	 * Return the list of all users
	 * 
	 * @return
	 */
	private List<User> getUsers() {
		Objectify ofy = ObjectifyService.begin();
		Query<User> q = ofy.query(User.class);
		List<User> users = new ArrayList<User>();
		for (User user : q) {
			users.add(user);
		}

		return users;
	}

	/**
	 * For testing purposes, get the list of admin users
	 * 
	 * @return
	 */
	private List<User> getAdminUsers() {
		Objectify ofy = ObjectifyService.begin();
		Query<User> q = ofy.query(User.class);
		List<User> users = new ArrayList<User>();
		for (User user : q) {
			if (user.isAdmin()) {
				users.add(user);
			}
		}

		return users;
	}
}
