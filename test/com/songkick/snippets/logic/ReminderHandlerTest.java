package com.songkick.snippets.logic;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertTrue;

import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.taskqueue.dev.LocalTaskQueue;
import com.google.appengine.api.taskqueue.dev.QueueStateInfo;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalTaskQueueTestConfig;
import com.songkick.snippets.model.ReminderEmail;
import com.songkick.snippets.model.Snippet;
import com.songkick.snippets.model.User;
import com.songkick.snippets.server.data.DataStorage;
import com.songkick.snippets.server.data.DataStorageMock;

public class ReminderHandlerTest {

	private final LocalServiceTestHelper helper = new LocalServiceTestHelper(
			new LocalTaskQueueTestConfig());

	@Before
	public void setUp() {
		helper.setUp();
	}

	@After
	public void tearDown() {
		helper.tearDown();
	}

	@Test
	public void testDigest() {
		ReminderHandler handler = new ReminderHandler();
		DataStorage dataStore = new DataStorageMock();

		QueueFactory.getDefaultQueue().add(
				TaskOptions.Builder.withTaskName("reminder-queue"));

		createUserWithSnippet(dataStore, "dancrow@songkick.com", null);

		handler.sendDigest(dataStore);

		checkResult();
	}

	private void createUserWithSnippet(DataStorage dataStore, String username,
			String snippetString) {
		User user = new User();
		user.setEmailAddress(username);
		dataStore.save(user);

		if (snippetString != null) {
			Snippet snippet = new Snippet(user, snippetString);
			snippet.setWeekNumber(DateHandler.getCurrentWeek() - 1);

			dataStore.save(snippet);
		}
	}

	/**
	 * Create the test data for testDigestFormat. Both creates new users and
	 * associated snippets and returns the list of test objects so the desired
	 * output can be calculated
	 * 
	 * @param dataStore
	 * @return
	 */
	private List<UserSnippet> getSnippetTestData(DataStorage dataStore) {
		List<UserSnippet> result = new ArrayList<UserSnippet>();

		result.add(new UserSnippet(dataStore, "dancrow@songkick.com",
				"My snippet for the week"));
		result.add(new UserSnippet(dataStore, "crow@mac.com", "A second snippet"));

		return result;
	}

	@Test
	/**
	 * Test if the digest is being generated in the right format
	 */
	public void testDigestFormat() {
		DataStorage dataStore = new DataStorageMock();
		List<UserSnippet> userSnippets = getSnippetTestData(dataStore);

		String digest = ReminderEmail.generateDigest(dataStore);

		String desiredOutput = "";
		for (UserSnippet userSnippet : userSnippets) {
			desiredOutput += "From: " + userSnippet.getUsername() + "\n";
			desiredOutput += userSnippet.getSnippetText() + "\n\n\n";
		}
		desiredOutput = desiredOutput.trim();

		System.out.println(digest);

		assertTrue(digest.contains(desiredOutput));
	}

	private void checkResult() {
		LocalTaskQueue ltq = LocalTaskQueueTestConfig.getLocalTaskQueue();
		QueueStateInfo qsi = ltq.getQueueStateInfo().get(
				QueueFactory.getDefaultQueue().getQueueName());

		// There should now be one task on the queue
		assertEquals(qsi.getTaskInfo().size(), 1);
	}

	class UserSnippet {
		private String username;
		private String snippetText;

		public UserSnippet(DataStorage dataStore, String username,
				String snippetText) {
			setUsername(username);
			setSnippetText(snippetText);

			createUserWithSnippet(dataStore, username, snippetText);
		}

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public String getSnippetText() {
			return snippetText;
		}

		public void setSnippetText(String snippetText) {
			this.snippetText = snippetText;
		}
	}
}
