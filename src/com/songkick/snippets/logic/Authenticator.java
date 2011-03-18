package com.songkick.snippets.logic;

import java.util.List;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.songkick.snippets.server.data.DataStorage;
import com.songkick.snippets.util.Debug;

public class Authenticator {

	public boolean isValidEmail(String fromAddress, DataStorage dataStore) {
		// Whitelist all @songkick.com email addresses
		if (fromAddress.contains("@songkick.com")) {
			return true;
		}

		List<com.songkick.snippets.model.User> users = dataStore.getUsers();
		
		// Whitelist all existing users
		for (com.songkick.snippets.model.User user: users) {
			if (user.matchesEmail(fromAddress)) {
				return true;
			}
		}

		return false;
	}

	public boolean isSongkickUser(DataStorage dataStore) {
		// Check this is a songkick.com user
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();

		if (user == null) {
			Debug.error("Could not get current user during authentication");
			return false;
		}

		return isValidEmail(user.getEmail(), dataStore);
	}

	public String isSongkickAdmin(String redirectURL, DataStorage dataStore) {
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

		List<com.songkick.snippets.model.User> users = dataStore.getUsers();
		for (com.songkick.snippets.model.User modelUser: users) {
			if (modelUser.matchesEmail(user.getEmail()) && modelUser.isAdmin()) {
				return null;
			}
		}

		return userService.createLoginURL(redirectURL);
	}
}
