package com.songkick.snippets.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.Query;
import com.songkick.snippets.client.AdminService;
import com.songkick.snippets.logic.Authenticator;
import com.songkick.snippets.logic.DateHandler;
import com.songkick.snippets.logic.ReminderHandler;
import com.songkick.snippets.logic.ReminderHandler.MailType;
import com.songkick.snippets.model.LogEntry;
import com.songkick.snippets.model.Snippet;
import com.songkick.snippets.model.User;
import com.songkick.snippets.shared.dao.UserDAO;
import com.songkick.snippets.util.Debug;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class AdminServiceImpl extends RemoteServiceServlet implements
		AdminService {

	private Authenticator authenticator = new Authenticator();
	private ReminderHandler reminderHandler = new ReminderHandler();

	public AdminServiceImpl() {
		// Register the database classes we will use
		ObjectifyService.register(User.class);
		ObjectifyService.register(Snippet.class);
		ObjectifyService.register(LogEntry.class);
	}

	@Override
	public List<UserDAO> getUserList() throws IllegalArgumentException {
		Objectify ofy = ObjectifyService.begin();
		Query<User> q = ofy.query(User.class);

		List<UserDAO> users = new ArrayList<UserDAO>();
		for (User next : q) {
			users.add(createDAO(next));
		}

		return users;
	}

	// Create a DAO from a stored User object
	private UserDAO createDAO(User user) {
		UserDAO dao = new UserDAO();

		dao.setId(user.getId());
		dao.setName(user.getName());
		dao.setAdmin(user.isAdmin());
		dao.setStartDate(user.getStartDate());
		dao.setEndDate(user.getEndDate());
		if (user.getEmailAddress() != null) {
			dao.addEmail(user.getEmailAddress());
		}
		if (user.getOtherEmails() != null) {
			for (String other : user.getOtherEmails()) {
				dao.addEmail(other);
			}
		}
		dao.setGroup(user.getGroup());
		return dao;
	}

	@Override
	public void addUser(UserDAO dao) {
		assert (dao.getId() == -1);

		User user = new User();
		updateUser(user, dao);
	}

	@Override
	public void updateUser(UserDAO dao) {
		assert (dao.getId() != -1);

		Debug.log("AdminServiceImpl.updateUser: " + dao);
		Objectify ofy = ObjectifyService.begin();
		Query<User> q = ofy.query(User.class);

		for (User user : q) {
			if (user.getId().equals(dao.getId())) {
				updateUser(user, dao);
				return;
			}
		}
		Debug.error("Could not update user with ID=" + dao.getId());
	}

	private void updateUser(User user, UserDAO dao) {
		Objectify ofy = ObjectifyService.begin();
		user.setName(dao.getName());
		user.setAdmin(dao.isAdmin());
		user.setStartDate(dao.getStartDate());
		user.setEndDate(user.getEndDate());
		if (dao.getEmailAddresses().size() > 0) {
			user.setEmailAddress(dao.getEmailAddresses().get(0));
			if (dao.getEmailAddresses().size() > 1) {
				user.setOtherEmails(dao.getEmailAddresses().subList(1,
						dao.getEmailAddresses().size()));
			}
		}
		user.setGroup(dao.getGroup());
		ofy.put(user);
	}

	@Override
	public Long getCurrentWeek() {
		return DateHandler.getCurrentWeek();
	}

	@Override
	public void deleteUser(UserDAO dao) {
		// TODO Auto-generated method stub

	}

	@Override
	public void remindUser(UserDAO dao) {
		assert (dao.getEmailAddresses().size() > 0);

		reminderHandler.remindUser(dao.getEmailAddresses().get(0),
				MailType.FirstReminder);
	}

	@Override
	public void remindUsers() {
		reminderHandler.testReminders();
	}

	private User getUser(UserDAO dao) {
		assert (dao.getId() != -1);

		Objectify ofy = ObjectifyService.begin();
		Query<User> q = ofy.query(User.class).filter("id", dao.getId());

		List<User> users = q.list();
		if (users.size() == 0) {
			return null;
		}

		return users.get(0);
	}

	@Override
	public List<String> getSnippets(UserDAO dao) {
		User user = getUser(dao);

		if (user == null) {
			return null;
		}

		Objectify ofy = ObjectifyService.begin();
		Query<Snippet> query = ofy.query(Snippet.class).filter("user", user);
		List<String> results = new ArrayList<String>();

		for (Snippet snippet : query) {
			results.add(snippet.getSnippetText());
		}

		return results;
	}

	@Override
	public String isValidAdmin(String redirectURL) {
		return authenticator.isSongkickAdmin(redirectURL);
	}

	@Override
	public void addSnippet(UserDAO dao, String snippetString, int week) {
		User user = getUser(dao);

		Objectify ofy = ObjectifyService.begin();
		Snippet snippet = new Snippet(user, snippetString);

		if (week == -1) {
			snippet.setWeekNumber(DateHandler.getCurrentWeek());
		} else {
			snippet.setWeekNumber(new Long(week));
		}
		ofy.put(snippet);
	}

	private static String getLogEntry(LogEntry entry) {
		return entry.getDate() + " " + entry.getEntry();
	}

	@Override
	public String getLog() {
		Objectify ofy = ObjectifyService.begin();
		Query<LogEntry> query = ofy.query(LogEntry.class);
		List<LogEntry> list = query.list();

		Collections.sort(list);

		String log = "";
		for (LogEntry entry : list) {
			log += getLogEntry(entry) + "\n";
		}

		return log;
	}

	@Override
	public List<UserDAO> getUsersToRemind() {
		Long week = DateHandler.getCurrentWeek();

		Debug.log("getting users to remind: week=" + week);

		List<User> toRemind = reminderHandler.getUsersWithoutSnippet(week);
		List<UserDAO> results = new ArrayList<UserDAO>();

		for (User user : toRemind) {
			results.add(createDAO(user));
		}

		return results;
	}

	@Override
	public void replaceSnippet(UserDAO dao, String snippetString, Long weekNumber) {
		User user = getUser(dao);

		if (user == null) {
			return;
		}

		Objectify ofy = ObjectifyService.begin();
		Query<Snippet> query = ofy.query(Snippet.class).filter("user", user);
		for (Snippet snippet : query) {
			if (snippet.getWeekNumber().equals(weekNumber)) {
				ofy.delete(snippet);
			}
		}
		
		addSnippet(dao, snippetString, weekNumber.intValue());
	}

	@Override
	public String getSnippet(UserDAO dao, Long weekNumber) {
		User user = getUser(dao);

		if (user == null) {
			return null;
		}

		Objectify ofy = ObjectifyService.begin();
		Query<Snippet> query = ofy.query(Snippet.class).filter("user", user);

		for (Snippet snippet : query) {
			if (snippet.getWeekNumber().equals(weekNumber)) {
				return snippet.getSnippetText();
			}
		}

		return null;
	}
}
