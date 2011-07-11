package com.songkick.snippets.logic;

import java.util.ArrayList;
import java.util.List;

import com.songkick.common.util.MailSender;
import com.songkick.snippets.model.User;
import com.songkick.snippets.server.data.DataStorageHandler;

/**
 * Send notification messages to the list of current admins
 * 
 * @author dancrow
 */
public class AdminNotifier {
	private MailSender mailSender = new MailSender("snippet@songkick.com", "Snippets");
	
	private List<User> getAdmins() {
		DataStorageHandler dataStore = new DataStorageHandler();
		
		List<User> users = dataStore.getCurrentUsers();
		List<User> admins = new ArrayList<User>();
		
		for (User user: users) {
			if (user.isAdmin()) {
				admins.add(user);
			}
		}
		return admins;
	}
	
	public void notify(String subject, String message) {
		List<User> admins = getAdmins();
		
		for (User admin: admins) {
			mailSender.sendEmail(admin.getEmailAddress(), "Snippet Alert: " + subject, message);
		}
	}
}
