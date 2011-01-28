package com.songkick.snippets.logic;

import java.util.logging.Logger;

import javax.mail.Address;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

public class Authenticator {
	public static final Logger log = Logger.getLogger(Authenticator.class
			.getName());
	
	public boolean authenticate(Address fromAddress) {
		if (fromAddress.toString().contains("@songkick.com")) {
			return true;
		}

		// Set to false to whitelist to songkick domain
		return false;
	}
	
	public boolean isSongkickUser() {
		// Check this is a songkick.com user
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		
		if (user==null || !user.getEmail().endsWith("@songkick.com")) {
			return false;
		}
		
		return true;
	}
	
	public boolean isSongkickAdmin() {
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		
		log.severe("Checking for admin rights on user: " + user);
		
		if (user==null) {
			log.severe("No user");
			return false;
		}
		
		// Whitelist admin users
		if (user.getEmail().equals("dancrow@songkick.com")) {

			log.severe("Whitelisted");
			return true;
		}

		log.severe("Not an admin");
		
		return false;
	}
}
