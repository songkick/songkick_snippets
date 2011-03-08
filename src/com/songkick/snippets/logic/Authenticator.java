package com.songkick.snippets.logic;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.Query;
import com.songkick.snippets.util.Debug;

public class Authenticator {

	public boolean isValidEmail(String fromAddress) {
		// Whitelist all @songkick.com email addresses
		if (fromAddress.contains("@songkick.com")) {
			return true;
		}

		Objectify ofy = ObjectifyService.begin();
		Query<com.songkick.snippets.model.User> q = ofy.query(com.songkick.snippets.model.User.class);

		// Whitelist all existing users
		for (com.songkick.snippets.model.User user: q) {
			if (user.matchesEmail(fromAddress)) {
				return true;
			}
		}

		return false;
	}

	public boolean isSongkickUser() {
		// Check this is a songkick.com user
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();

		if (user == null) {
			Debug.error("Could not get current user during authentication");
			return false;
		}

		return isValidEmail(user.getEmail());
	}

	public String isSongkickAdmin(String redirectURL) {
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();

		if (user == null) {
			Debug.error("Authenticator.isSongkickAdmin: no user found, could not authenticate");
			return userService.createLoginURL(redirectURL);
		}

		// Whitelist admin users
		if (user.getEmail().equals("dancrow@songkick.com")) {
			return null;
		}

		Objectify ofy = ObjectifyService.begin();
		Query<com.songkick.snippets.model.User> q = ofy.query(com.songkick.snippets.model.User.class);
		for (com.songkick.snippets.model.User modelUser: q) {
			if (modelUser.matchesEmail(user.getEmail()) && modelUser.isAdmin()) {
				return null;
			}
		}

		return userService.createLoginURL(redirectURL);
	}
}
