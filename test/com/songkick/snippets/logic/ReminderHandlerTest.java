package com.songkick.snippets.logic;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.taskqueue.dev.LocalTaskQueue;
import com.google.appengine.api.taskqueue.dev.QueueStateInfo;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalTaskQueueTestConfig;
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

		User user = new User();
		user.setEmailAddress("dancrow@songkick.com");

		dataStore.save(user);

		handler.sendDigest(dataStore);

		checkResult();
	}

	private void checkResult() {
		LocalTaskQueue ltq = LocalTaskQueueTestConfig.getLocalTaskQueue();
		QueueStateInfo qsi = ltq.getQueueStateInfo().get(
				QueueFactory.getDefaultQueue().getQueueName());

		// There should now be one task on the queue
		assertEquals(qsi.getTaskInfo().size(), 1);
	}
}
