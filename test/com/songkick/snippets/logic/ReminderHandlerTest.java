package com.songkick.snippets.logic;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import com.songkick.snippets.model.User;

public class ReminderHandlerTest {
	@Test
	public void testAddition() {
		assertEquals(4, 2 + 2);
	}

	@Test
	public void testReminders() {
		ReminderHandler handler = new ReminderHandler();

		List<User> users = handler.getUsersWithoutSnippet(5L);

		System.out.println(users);
		
		assertEquals(users.size(), 5);
	}
}
