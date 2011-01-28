package com.songkick.snippets.server;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.Query;
import com.songkick.snippets.client.AdminService;
import com.songkick.snippets.logic.Authenticator;
import com.songkick.snippets.logic.DateHandler;
import com.songkick.snippets.logic.ReminderHandler;
import com.songkick.snippets.model.Snippet;
import com.songkick.snippets.model.User;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class AdminServiceImpl extends RemoteServiceServlet implements
		AdminService {
	
	private Authenticator authenticator = new Authenticator();

	public AdminServiceImpl() {
		// Register the database classes we will use
		ObjectifyService.register(User.class);
		ObjectifyService.register(Snippet.class);
	}

	@Override
	public List<String> getUserList() throws IllegalArgumentException {
		Objectify ofy = ObjectifyService.begin();
		Query<User> q = ofy.query(User.class);

		List<String> users = new ArrayList<String>();
		for (User next : q) {
			users.add(next.getEmailAddress());
		}

		return users;
	}

	@Override
	public void addUser(String email) {
		Objectify ofy = ObjectifyService.begin();
		User user = new User();
		user.setEmailAddress(email);
		ofy.put(user);
	}

	@Override
	public Long getCurrentWeek() {
		return DateHandler.getCurrentWeek();
	}

	@Override
	public boolean validateUser(String username, String password) {
		if (username.equals("dancrow") && password.equals("sk47")) {
			return true;
		}

		return false;
	}

	@Override
	public void deleteUser(String email) {
		// TODO Auto-generated method stub

	}

	@Override
	public void remindUser(String email) {
		ReminderHandler handler = new ReminderHandler();

		handler.remindUser(email);
	}

	@Override
	public List<String> getSnippets(String email) {
		Objectify ofy = ObjectifyService.begin();
		Query<User> q = ofy.query(User.class).filter("emailAddress", email);

		List<User> users = q.list();
		if (users.size() == 0) {
			return null;
		}

		User user = users.get(0);

		Query<Snippet> query = ofy.query(Snippet.class).filter("user", user);
		List<String> results = new ArrayList<String>();

		for (Snippet snippet : query) {
			results.add(snippet.getSnippetText());
		}

		return results;
	}

	@Override
	public boolean isValidAdmin() {
		return authenticator.isSongkickAdmin();
	}
}
