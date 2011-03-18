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
		
		handler.processMail("dancrow@songkick.com", "2011-01-18T00:00", "test email", dataStore);
		
		assertTrue(dataStore.hasUser("dancrow@songkick.com"));
		
		List<Snippet> snippets = dataStore.getSnippets();
		for (Snippet snippet: snippets) {
			if (snippet.getWeekNumber()==2L) {
				return;
			}
		}
		
		fail();
	}
}
