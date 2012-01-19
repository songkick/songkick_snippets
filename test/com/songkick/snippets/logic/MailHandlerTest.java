package com.songkick.snippets.logic;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Test;

import com.songkick.snippets.model.Snippet;
import com.songkick.snippets.server.data.DataStorageMock;

public class MailHandlerTest {

	@Test
	public void testWeekStorage() {
		MailHandler handler = new MailHandler();
		DataStorageMock dataStore = new DataStorageMock();

		// Process an incoming "email"
		handler.processMail("dancrow@songkick.com", "2011-01-18T00:00",
				"test email", dataStore);

		// Make sure we created a user correctly
		assertTrue(dataStore.hasUser("dancrow@songkick.com"));

		// Make sure we stored the incoming snippet text correctly
		List<Snippet> snippets = dataStore.getSnippets();
		for (Snippet snippet : snippets) {
			if (snippet.getWeekNumber() == DateHandler.getCurrentWeek()) {
				if (snippet.getSnippetText().equals("test email")) {
					return;
				}
			}
		}

		fail();
	}
}
