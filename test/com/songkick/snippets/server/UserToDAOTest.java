package com.songkick.snippets.server;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.songkick.common.model.EmailAddress;
import com.songkick.common.model.UserDAO;
import com.songkick.snippets.model.User;
import com.songkick.snippets.server.data.DataStorage;
import com.songkick.snippets.server.data.DataStorageMock;

public class UserToDAOTest {
	@Test
	/**
	 * Test whether the conversion from a User object to a UserDAO preserves email addresses correctly
	 */
	public void testUserToUserDAO() {
		UserToDAOTranslator impl = new UserToDAOTranslator();

		User user = createUser("dancrow@songkick.com", null, null);
		checkDAOEmail(impl.createDAO(user), "dancrow@songkick.com", true);

		user = createUser(null, "dancrow@songkick.com", null);
		checkDAOEmail(impl.createDAO(user), "dancrow@songkick.com", true);

		user = createUser(null, null, "dancrow@songkick.com");
		checkDAOEmail(impl.createDAO(user), "dancrow@songkick.com", false);
	}

	@Test
	/**
	 * Test whether conversion from User to UserDAO and back to User works
	 */
	public void testUserToUser() {
		// Check that the email field is correctly turned into a primary email
		// address
		User user = roundTrip("dancrow@songkick.com", null, null);
		assertTrue(checkUserEmails(user, null, "dancrow@songkick.com", null));

		// Check that an email address->primary and other emails->secondary
		user = roundTrip("dancrow@songkick.com", null, "crow@mac.com");
		assertTrue(checkUserEmails(user, null, "dancrow@songkick.com",
				"crow@mac.com"));

		// Check that other emails->secondary
		user = roundTrip(null, null, "crow@mac.com");
		assertTrue(checkUserEmails(user, null, null, "crow@mac.com"));

		// Check that email address and primary emails are merged into primary
		user = roundTrip("dancrow@songkick.com", "crow@mac.com", null);
		assertTrue(checkUserEmails(user, null, "dancrow@songkick.com,crow@mac.com",
				null));

		// Check the full combination
		user = roundTrip("dancrow@songkick.com", "crow1@mac.com", "crow2@mac.com");
		assertTrue(checkUserEmails(user, null,
				"dancrow@songkick.com,crow1@mac.com", "crow2@mac.com"));
	}
	
	@Test
	/**
	 * Test whether a UserDAO correctly preserves email fields when translating to a User object 
	 */
	public void testDAOToUser() {
		UserToDAOTranslator impl = new UserToDAOTranslator();
		DataStorage dataStore = new DataStorageMock();
		UserDAO dao = new UserDAO();

		List<EmailAddress> list = new ArrayList<EmailAddress>();
		list.add(createEmailAddress("crow@mac.com", true));
		list.add(createEmailAddress("john@mac.com", false));
		dao.setEmailAddresses(list);

		User user = new User();
		impl.updateUser(dataStore, user, dao);

		System.out.println("DAO is " + dao);
		System.out.println("User is " + user);

		assertTrue(checkUserEmails(user, null, "crow@mac.com", "john@mac.com"));
	}

	// ============================================================================
	
	private EmailAddress createEmailAddress(String email, boolean isPrimary) {
		EmailAddress address = new EmailAddress();
		address.setEmail(email);
		address.setPrimary(isPrimary);
		return address;
	}

	private boolean checkEmail(User user, String email) {

		if (user.getEmailAddress() == null && email != null) {
			System.out.println("user.getEmailAddress is null, email is not");
			return false;
		}

		if (email == null && user.getEmailAddress() != null) {
			System.out.println("email is null but user.getEmailAddres is "
					+ user.getEmailAddress());
			return false;
		}

		return true;
	}

	private List<String> stringToArray(String string) {
		String[] items = string.split(",");
		List<String> results = new ArrayList<String>();

		for (String item : items) {
			results.add(item);
		}
		return results;
	}

	private boolean checkArray(List<String> userList, String checkString) {
		if (checkString == null) {
			if (userList == null) {
				return true;
			}
			if (userList.size() == 0) {
				return true;
			}
			return false;
		}

		if (userList == null) {
			return false;
		}

		List<String> checkList = stringToArray(checkString);

		if (checkList.size() != userList.size()) {
			return false;
		}

		for (String item : checkList) {
			if (!userList.contains(item)) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Test whether the given User object has the specified email, primary email
	 * and other email field values
	 * 
	 * @param user
	 * @param email
	 * @param primary
	 * @param secondary
	 * @return
	 */
	private boolean checkUserEmails(User user, String email, String primary,
			String secondary) {
		System.out.println("checkUserEmails. user is " + user + " email is "
				+ email + " primary is " + primary + " secondary is " + secondary);

		boolean emailGood = checkEmail(user, email);
		if (!emailGood) {
			System.out.println("email not good");
			return false;
		}

		boolean primaryGood = checkArray(user.getPrimaryEmails(), primary);
		if (!primaryGood) {
			System.out.println("primary not good");
			return false;
		}

		boolean secondaryGood = checkArray(user.getOtherEmails(), secondary);
		if (!secondaryGood) {
			System.out.println("secondary not good");
			return false;
		}

		return true;
	}

	private User roundTrip(String email, String primary, String secondary) {
		UserToDAOTranslator impl = new UserToDAOTranslator();
		DataStorage dataStore = new DataStorageMock();
		User user = createUser(email, primary, secondary);
		UserDAO dao = impl.createDAO(user);
		User user1 = new User();
		impl.updateUser(dataStore, user1, dao);

		return user1;
	}

	/**
	 * Create a user object with the specified emailAddress, PrimaryEmails and
	 * OtherEmails field values
	 * 
	 * @param email
	 * @param primary
	 * @param other
	 * @return
	 */
	private User createUser(String email, String primary, String other) {
		User user = new User();
		if (email != null) {
			user.setEmailAddress(email);
		}
		if (primary != null) {
			List<String> emailAddress = new ArrayList<String>();
			emailAddress.add(primary);
			user.setPrimaryEmails(emailAddress);
		}
		if (other != null) {
			List<String> emailAddress = new ArrayList<String>();
			emailAddress.add(other);
			user.setOtherEmails(emailAddress);
		}

		System.out.println("Created user: " + user);
		return user;
	}

	private void checkDAOEmail(UserDAO dao, String email, boolean isPrimary) {
		System.out.println("Checking DAO: " + dao);
		for (EmailAddress address : dao.getEmailAddresses()) {
			if (address.getEmail().equals(email) && address.isPrimary() == isPrimary) {
				return;
			}
		}

		fail();
	}
}
