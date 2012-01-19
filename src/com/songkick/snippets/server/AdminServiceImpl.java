package com.songkick.snippets.server;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.songkick.common.model.EmailAddress;
import com.songkick.common.model.UserDAO;
import com.songkick.common.util.Debug;
import com.songkick.snippets.client.AdminService;
import com.songkick.snippets.client.model.HolidayDate;
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
	private DTOTranslator translator = new DTOTranslator();
	private ReviewsService reviewsService = new ReviewsService();

	@Override
	public List<UserDAO> getCurrentUserList() throws IllegalArgumentException {
		List<User> users = dataStore.getCurrentUsers();

		List<UserDAO> userDAOs = new ArrayList<UserDAO>();
		for (User next : users) {
			userDAOs.add(translator.createDAO(dataStore, next));
		}

		return userDAOs;
	}

	@Override
	public List<UserDAO> getFullUserList() throws IllegalArgumentException {
		List<User> users = dataStore.getAllUsers();

		List<UserDAO> userDAOs = new ArrayList<UserDAO>();
		for (User next : users) {
			userDAOs.add(translator.createDAO(dataStore, next));
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
		User user = getUser(dao);
		if (user == null) {
			Debug.log("No user found");
			return;
		}

		Debug.log("Deleting user " + user.getBestName());
		dataStore.delete(user);

		List<Snippet> snippets = dataStore.getSnippetsForUser(user);

		Debug.log("Also deleting all user snippets");
		for (Snippet snippet : snippets) {
			dataStore.delete(snippet);
		}

		Debug.log("Done");
	}

	@Override
	public void remindUser(UserDAO dao) {
		Debug.log("Reminding user " + dao.getName());

		boolean hasPrimary = false;
		for (EmailAddress email : dao.getEmailAddresses()) {
			if (email.isPrimary()) {
				Debug.log("Sending reminder to " + email.getEmail());
				reminderHandler.remindUser(email.getEmail(), MailType.FirstReminder,
						dataStore);
				hasPrimary = true;
			}
		}

		if (!hasPrimary) {
			Debug.log("No primary email address for " + dao);
		}
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
			results.add(translator.createDAO(dataStore, user));
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
			Debug.log("Checking snippet from week " + snippet.getWeekNumber() + ": "
					+ snippet.getSnippetText());
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

	@Override
	public void upgradeDatabase() {
		List<User> users = dataStore.getAllUsers();

		for (User user : users) {
			UserDAO dao = translator.createDAO(dataStore, user);
			translator.updateUser(dataStore, user, dao);
		}
	}

	@Override
	public List<HolidayDate> getHolidayDates() {
		return dataStore.getAllHolidayDates();
	}

	@Override
	public void setHolidayDates(List<HolidayDate> dates) {
		for (HolidayDate date : dates) {
			dataStore.save(date);
		}
	}

	@Override
	public boolean startReview(UserDAO dao, String date, String period) {

		for (User user : dataStore.getAllUsers()) {
			if (user.getId().equals(dao.getId())) {
				translator.updateUser(dataStore, user, dao);
				DateTime dueByDate = DateHandler.getDateFromString(date);
				reviewsService.createBlankSelfAssessmentFor(user, dueByDate, period);
				return true;
			}
		}
		return false;
	}

	@Override
	public List<UserDAO> getDirectReports() {
		User user = getAuthenticatedUser();
		if (user == null) {
			return null;
		}
		return getDirectReports(user);
	}

	private List<UserDAO> getDirectReports(User user) {
		List<User> allUsers = dataStore.getAllUsers();
		List<UserDAO> reports = new ArrayList<UserDAO>();

		for (User next : allUsers) {
			if (next.doesReportTo(user)) {
				reports.add(translator.createDAO(dataStore, next));
			}
		}
		return reports;
	}

	private User getAuthenticatedUser() {
		com.google.appengine.api.users.User currentUser = authenticator.getUser();
		List<User> users = dataStore.getCurrentUsers();

		for (User next : users) {
			if (next.matchesEmail(currentUser.getEmail())) {
				return next;
			}
		}
		return null;
	}

	@Override
	public UserDAO getCurrentUser() {
		return translator.createDAO(dataStore, getAuthenticatedUser());
	}
}
