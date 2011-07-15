package com.songkick.snippets.server;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.songkick.common.model.EmailAddress;
import com.songkick.common.model.UserDAO;
import com.songkick.common.util.Debug;
import com.songkick.snippets.client.AdminService;
import com.songkick.snippets.logic.Authenticator;
import com.songkick.snippets.logic.DateHandler;
import com.songkick.snippets.logic.ReminderHandler;
import com.songkick.snippets.logic.ReminderHandler.MailType;
import com.songkick.snippets.model.ReminderEmail;
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
	private UserToDAOTranslator translator = new UserToDAOTranslator();

	@Override
	public List<UserDAO> getCurrentUserList() throws IllegalArgumentException {
		List<User> users = dataStore.getCurrentUsers();

		List<UserDAO> userDAOs = new ArrayList<UserDAO>();
		for (User next : users) {
			userDAOs.add(translator.createDAO(next));
		}

		return userDAOs;
	}

	@Override
	public List<UserDAO> getFullUserList() throws IllegalArgumentException {
		List<User> users = dataStore.getAllUsers();

		List<UserDAO> userDAOs = new ArrayList<UserDAO>();
		for (User next : users) {
			userDAOs.add(translator.createDAO(next));
		}

		return userDAOs;
	}

	@Override
	public void addUser(UserDAO dao) {
		Debug.log("Adding user " + dao);

		assert (dao.getId() == -1);

		User user = new User();
		translator.updateUser(dataStore, user, dao);

		Debug.log("Done - updated user to " + user);
	}

	@Override
	public void updateUser(UserDAO dao) {
		assert (dao.getId() != -1);

		Debug.log("AdminServiceImpl.updateUser: " + dao);

		for (User user : dataStore.getAllUsers()) {
			if (user.getId().equals(dao.getId())) {
				translator.updateUser(dataStore, user, dao);
				return;
			}
		}
		Debug.error("Could not update user with ID=" + dao.getId());
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

		for (EmailAddress email : dao.getEmailAddresses()) {
			if (email.isPrimary()) {
				reminderHandler.remindUser(email.getEmail(), MailType.FirstReminder,
						dataStore);
			}
		}
		Debug.log("No primary email address for " + dao);
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
			results.add(translator.createDAO(user));
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

	@Override
	public String getDigest() {
		return ReminderEmail.generateDigest(dataStore);
	}

	@Override
	public void sendDigestToUser(UserDAO user) {
		List<User> users = new ArrayList<User>();

		users.add(getUser(user));
		reminderHandler.queueRemindersTo(users, MailType.Digest);
	}
}
