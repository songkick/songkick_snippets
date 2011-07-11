package com.songkick.snippets.server;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.songkick.common.model.UserDAO;
import com.songkick.common.util.Debug;
import com.songkick.snippets.client.AdminService;
import com.songkick.snippets.logic.Authenticator;
import com.songkick.snippets.logic.DateHandler;
import com.songkick.snippets.logic.ReminderHandler;
import com.songkick.snippets.logic.ReminderHandler.MailType;
import com.songkick.snippets.model.Snippet;
import com.songkick.snippets.model.User;
import com.songkick.snippets.server.data.DataStorage;
import com.songkick.snippets.server.data.DataStorageHandler;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class AdminServiceImpl extends RemoteServiceServlet implements
		AdminService {

	private Authenticator authenticator = new Authenticator();
	private ReminderHandler reminderHandler = new ReminderHandler();
	private DataStorage dataStore = new DataStorageHandler();

	@Override
	public List<UserDAO> getCurrentUserList() throws IllegalArgumentException {
		List<User> users = dataStore.getCurrentUsers();

		List<UserDAO> userDAOs = new ArrayList<UserDAO>();
		for (User next : users) {
			userDAOs.add(createDAO(next));
		}

		return userDAOs;
	}
	
	@Override
	public List<UserDAO> getFullUserList() throws IllegalArgumentException {
		List<User> users = dataStore.getAllUsers();

		List<UserDAO> userDAOs = new ArrayList<UserDAO>();
		for (User next : users) {
			userDAOs.add(createDAO(next));
		}

		return userDAOs;
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

		for (User user : dataStore.getCurrentUsers()) {
			if (user.getId().equals(dao.getId())) {
				updateUser(user, dao);
				return;
			}
		}
		Debug.error("Could not update user with ID=" + dao.getId());
	}

	private void updateUser(User user, UserDAO dao) {
		user.setName(dao.getName());
		user.setAdmin(dao.isAdmin());
		user.setStartDate(dao.getStartDate());
		user.setEndDate(dao.getEndDate());
		if (dao.getEmailAddresses().size() > 0) {
			user.setEmailAddress(dao.getEmailAddresses().get(0));
			if (dao.getEmailAddresses().size() > 1) {
				user.setOtherEmails(dao.getEmailAddresses().subList(1,
						dao.getEmailAddresses().size()));
			}
		}
		user.setGroup(dao.getGroup());
		dataStore.save(user);
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
				MailType.FirstReminder, dataStore);
	}

	private User getUser(UserDAO dao) {
		assert (dao.getId() != -1);

		return dataStore.getUserById(dao.getId());
	}

	@Override
	public List<String> getSnippets(UserDAO dao) {
		User user = getUser(dao);

		if (user == null) {
			return null;
		}

		List<Snippet> snippets = dataStore.getSnippetsForUser(user);
		List<String> results = new ArrayList<String>();

		for (Snippet snippet : snippets) {
			results.add(snippet.getSnippetText());
		}

		return results;
	}

	@Override
	public String isValidAdmin(String redirectURL) {
		return authenticator.isSongkickAdmin(redirectURL, dataStore);
	}

	@Override
	public void addSnippet(UserDAO dao, String snippetString, int week) {
		User user = getUser(dao);

		Snippet snippet = new Snippet(user, snippetString);

		if (week == -1) {
			snippet.setWeekNumber(DateHandler.getCurrentWeek());
		} else {
			snippet.setWeekNumber(new Long(week));
		}

		dataStore.save(snippet);
	}

	@Override
	public List<UserDAO> getUsersToRemind() {
		Long week = DateHandler.getCurrentWeek();

		Debug.log("getting users to remind: week=" + week);

		List<User> toRemind = reminderHandler.getUsersWithoutSnippet(week,
				dataStore);
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

		for (Snippet snippet : dataStore.getSnippetsForUser(user)) {
			if (snippet.getWeekNumber().equals(weekNumber)) {
				dataStore.delete(snippet);
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

		for (Snippet snippet : dataStore.getSnippetsForUser(user)) {
			if (snippet.getWeekNumber().equals(weekNumber)) {
				return snippet.getSnippetText();
			}
		}

		return null;
	}

	@Override
	public void deleteSnippet(UserDAO dao, Long week) {
		User user = getUser(dao);

		if (user == null) {
			return;
		}

		for (Snippet snippet : dataStore.getSnippetsForUser(user)) {
			if (snippet.getWeekNumber().equals(week)) {
				dataStore.delete(snippet);
			}
		}
	}
}
