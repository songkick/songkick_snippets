package com.songkick.snippets.logic;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.Query;
import com.songkick.snippets.model.Snippet;
import com.songkick.snippets.model.User;

public class ReminderHandler {
	private static final Logger log = Logger.getLogger(ReminderHandler.class
			.getName());
	private MailSender mailSender = new MailSender();

	public ReminderHandler() {
		ObjectifyService.register(Snippet.class);
		ObjectifyService.register(User.class);
	}

	/**
	 * Find all the users who don't have a snippet for this week and send each
	 * of them a reminder email
	 */
	public void generateReminders() {
		List<User> users = getUsers();

		log.severe("List of all users: " + users);

		Long currentWeek = DateHandler.getCurrentWeek();
		List<User> withCurrentSnippets = getUsersWithSnippetFor(currentWeek);

		log.severe("List of users with snippets for week " + currentWeek + ": "
				+ withCurrentSnippets);

		List<User> toRemind = new ArrayList<User>();

		for (User user : users) {
			boolean hasSnippet = false;
			// log.severe("Checking user: " + user);
			for (User withSnippetUser : withCurrentSnippets) {
				// log.severe("Checking against user with snippet: " +
				// withSnippetUser);
				if (user.getId() == withSnippetUser.getId()) {
					// log.severe("The user has a snippet");
					hasSnippet = true;
				}
			}

			if (!hasSnippet) {
				// log.severe("hasSnippet is false, adding to list");
				toRemind.add(user);
			}
		}

		log.severe("Users to remind: " + toRemind);

		for (User remindUser : toRemind) {
			remindUser(remindUser);
		}
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
	public void remindUser(String emailAddress) {
		Objectify ofy = ObjectifyService.begin();
		Query<User> q = ofy.query(User.class);

		for (User user : q) {
			if (user.getEmailAddress().equals(emailAddress)) {
				remindUser(user);
			}
		}
	}

	/**
	 * Send reminder to a user
	 * 
	 * @param user
	 */
	private void remindUser(User user) {
		log.severe("Sending reminder email to " + user.getEmailAddress());
		mailSender
				.sendEmail(
						user.getEmailAddress(),
						"Snippet reminder",
						"This is your automated snippet nag email. You haven't submitted a snippet email yet this week. "
								+ "Please reply to this message with your snippet by midnight on Monday.\n\n"
								+ "From Tuesday morning you can "
								+ "access the past week's snippet report at http://sksnippet.appspot.com/snippets.\n\n"
								+ "For more details about writing snippets, see: https://sites.google.com/a/songkick.com/weekly-snippets/home\n\n"
								+ "Thanks,\n\nThe Songkick Snippet System");
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

}
