package com.songkick.snippets.server;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import com.songkick.snippets.model.User;
import com.songkick.snippets.server.data.DataStorage;
import com.songkick.snippets.server.data.DataStorageMock;

public class UserModelTests {

	private void addUser(DataStorage dataStore, String date) {
		User user = new User();
		user.setEndDate(date);

		dataStore.save(user);
	}
	
	private void testExpectation(DataStorage dataStore, Long week, int expected) {
		List<User> users = dataStore.getUsersForWeek(week);
		assertTrue(users.size() == expected);
	}

	@Test
	public void testEndDates() {
		DataStorageMock dataStore = new DataStorageMock();

		addUser(dataStore, "Mar 01, 2011");
		addUser(dataStore, "Mar 31, 2011");

		testExpectation(dataStore, 10L, 1);
		testExpectation(dataStore, 2L, 2);
		testExpectation(dataStore, 15L, 0);
	}
}
