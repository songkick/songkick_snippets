package com.songkick.snippets.logic;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.taskqueue.dev.LocalTaskQueue;
import com.google.appengine.api.taskqueue.dev.QueueStateInfo;
import com.google.appengine.tools.development.LocalServerEnvironment;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalTaskQueueTestConfig;
import com.songkick.snippets.model.User;
import com.songkick.snippets.server.data.DataStorage;
import com.songkick.snippets.server.data.DataStorageMock;

public class ReminderHandlerTest {
	private LocalServiceTestHelper helper;

	private LocalTaskQueueTestConfig taskQueueConfig;

	@Before
	public void setUp() {
		taskQueueConfig = new LocalTaskQueueTestConfig();
		helper = new LocalServiceTestHelper(taskQueueConfig) {
			// Workaround help task queue find war/WEB-INF/queue.xml
			@Override
			protected LocalServerEnvironment newLocalServerEnvironment() {
				final LocalServerEnvironment lse = super.newLocalServerEnvironment();
				return new LocalServerEnvironment() {
					public File getAppDir() {
						return new File("war");
					}

					public String getAddress() {
						return lse.getAddress();
					}

					public int getPort() {
						return lse.getPort();
					}

					public void waitForServerToStart() throws InterruptedException {
						lse.waitForServerToStart();
					}
				};
			}
		};

		helper.setEnvAuthDomain("auth");
		helper.setEnvEmail("test@example.com");
		helper.setEnvIsAdmin(true);
		helper.setEnvIsLoggedIn(true);
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
    QueueStateInfo qsi = ltq.getQueueStateInfo().get(QueueFactory.getDefaultQueue().getQueueName());
    
    // There should now be one task on the queue
    assertEquals(qsi.getTaskInfo().size(), 1);
	}
}
