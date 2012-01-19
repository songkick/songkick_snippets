package com.songkick.snippets.logic;

import java.util.List;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.songkick.common.util.Debug;
import com.songkick.snippets.server.data.DataStorage;

public class Authenticator {
	private UserService userService = UserServiceFactory.getUserService();

	public boolean isValidEmail(String fromAddress, DataStorage dataStore) {
		// Whitelist all @songkick.com email addresses
		if (fromAddress.contains("@songkick.com")) {
			return true;
		}

		List<com.songkick.snippets.model.User> users = dataStore.getCurrentUsers();

		// Whitelist all existing users
		for (com.songkick.snippets.model.User user : users) {
			if (user.matchesEmail(fromAddress)) {
				return true;
			}
		}

		return false;
	}

	public User getUser() {
		return userService.getCurrentUser();
	}

	public boolean isSongkickUser(DataStorage dataStore) {
		// Check this is a songkick.com user
		User user = getUser();

		if (user == null) {
			Debug.error("Could not get current user during authentication");
			return false;
		}

		return isValidEmail(user.getEmail(), dataStore);
	}

	public String isSongkickAdmin(String redirectURL, DataStorage dataStore) {
		User user = getUser();

		if (user == null) {
			Debug
					.error("Authenticator.isSongkickAdmin: no user found, could not authenticate");
			return userService.createLoginURL(redirectURL);
		}

		// Whitelist admin users
		if (user.getEmail().equals("dancrow@songkick.com")) {
			return null;
		}

		List<com.songkick.snippets.model.User> users = dataStore.getCurrentUsers();
		for (com.songkick.snippets.model.User modelUser : users) {
			if (modelUser.matchesEmail(user.getEmail()) && modelUser.isAdmin()) {
				return null;
			}
		}

		return userService.createLoginURL(redirectURL);
	}
}
