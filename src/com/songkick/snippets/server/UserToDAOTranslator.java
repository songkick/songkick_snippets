package com.songkick.snippets.server;

import java.util.ArrayList;
import java.util.List;

import com.songkick.common.model.EmailAddress;
import com.songkick.common.model.UserDAO;
import com.songkick.snippets.model.User;
import com.songkick.snippets.server.data.DataStorage;

public class UserToDAOTranslator {
	public void updateUser(DataStorage dataStore, User user, UserDAO dao) {
		user.setName(dao.getName());
		user.setAdmin(dao.isAdmin());
		user.setStartDate(dao.getStartDate());
		user.setEndDate(dao.getEndDate());

		List<String> primary = new ArrayList<String>();
		List<String> other = new ArrayList<String>();
		for (EmailAddress address : dao.getEmailAddresses()) {
			if (address.isPrimary()) {
				primary.add(address.getEmail());
			} else {
				other.add(address.getEmail());
			}
		}
		user.setPrimaryEmails(primary);
		user.setOtherEmails(other);
		user.setEmailAddress(null);

		user.setGroup(dao.getGroup());
		dataStore.save(user);
	}

	// Create a DAO from a stored User object
	public UserDAO createDAO(User user) {
		UserDAO dao = new UserDAO();

		dao.setId(user.getId());
		dao.setName(user.getName());
		dao.setAdmin(user.isAdmin());
		dao.setStartDate(user.getStartDate());
		dao.setEndDate(user.getEndDate());

		if (user.getEmailAddress() != null) {
			dao.addPrimaryEmail(user.getEmailAddress());
		}
		if (user.getPrimaryEmails() != null) {
			for (String email : user.getPrimaryEmails()) {
				dao.addPrimaryEmail(email);
			}
		}
		if (user.getOtherEmails() != null) {
			for (String email : user.getOtherEmails()) {
				dao.addSecondaryEmail(email);
			}
		}
		dao.setGroup(user.getGroup());
		return dao;
	}
}
