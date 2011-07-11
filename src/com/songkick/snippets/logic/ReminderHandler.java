package com.songkick.snippets.logic;

import static com.google.appengine.api.taskqueue.TaskOptions.Builder.withUrl;

import java.util.ArrayList;
import java.util.List;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.taskqueue.TaskOptions.Method;
import com.songkick.common.util.Debug;
import com.songkick.snippets.model.User;
import com.songkick.snippets.server.data.DataStorage;

public class ReminderHandler {
	public enum MailType {
		FirstReminder, SecondReminder, Digest
	};

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

	public List<User> getUsersWithoutSnippet(Long week, DataStorage dataStore) {
		List<User> users = dataStore.getCurrentUsers();
		List<User> withCurrentSnippets = dataStore.getUsersWithSnippetFor(week);

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
	public void generateReminders(MailType type, DataStorage dataStore) {
		Long currentWeek = DateHandler.getCurrentWeek();
		List<User> toRemind = getUsersWithoutSnippet(currentWeek, dataStore);

		queueRemindersTo(toRemind, type);
	}
	
	/**
	 * Send a digest of last week's snippets to all users
	 * 
	 * @param dataStore
	 */
	public void sendDigest(DataStorage dataStore) {
		List<User> users = dataStore.getCurrentUsers();
		
		queueRemindersTo(users, MailType.Digest);
	}

	/**
	 * Send a reminder email to the specified email address, without checking if
	 * the user is due for a reminder email. Used by the admin interface to test
	 * the reminder email content
	 * 
	 * @param emailAddress
	 */
	public void remindUser(String emailAddress, MailType type, DataStorage dataStore) {
		Debug.log("remindUser. emailAddress: " + emailAddress);
		
		List<User> users = dataStore.getCurrentUsers();
		List<User> toRemind = new ArrayList<User>();
		for (User user : users) {
			if (user.getEmailAddress().equals(emailAddress)) {
				toRemind.add(user);
			}
		}

		queueRemindersTo(toRemind, type);
	}
}
