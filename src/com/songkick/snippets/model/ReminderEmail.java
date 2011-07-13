package com.songkick.snippets.model;

import com.songkick.common.util.Debug;
import com.songkick.common.util.MailSender;
import com.songkick.snippets.logic.DateHandler;
import com.songkick.snippets.logic.ReminderHandler.MailType;
import com.songkick.snippets.presentation.SnippetPresentation;
import com.songkick.snippets.server.data.DataStorage;
import com.songkick.snippets.server.data.DataStorageHandler;

/**
 * Generate the appropriate reminder email, then call a MailSender to actually send it
 * 
 * @author dancrow
 */
public class ReminderEmail {
	private static MailSender mailSender = new MailSender("snippet@songkick.com", "Snippets", "text/plain");
	private static final boolean FAKE_SENDING = false;

	private static String digestCache = null;

	public static void remindUser(String emailAddress, MailType type) {
		if (FAKE_SENDING) {
			Debug.log("Should be sending email to: " + emailAddress
					+ " but fake sending is set");
			return;
		}

		if (type == MailType.Digest) {
			mailSender.sendEmail(emailAddress, "Weekly snippet digest",
					generateDigest(new DataStorageHandler()));
			return;
		}

		String text = "This is your automated snippet nag email. You haven't submitted a snippet yet this week. "
				+ "Please reply to this message with your snippet by midnight on Monday.\n\n"
				+ "From Tuesday morning you can "
				+ "access the past week's snippet report at http://sksnippet.appspot.com/snippets.\n\n"
				+ "For more details about snippets, see: https://sites.google.com/a/songkick.com/weekly-snippets/home\n\n"
				+ "Thanks,\n\nThe Songkick Snippet System";

		if (type == MailType.SecondReminder) {
			text = "This is your final snippet reminder. You haven't submitted a snippet yet this week. "
					+ "Please reply to this message with your snippet as soon as possible, and by midnight tonight at the latest.\n\n"
					+ "From Tuesday morning you can "
					+ "access the past week's snippet report at http://sksnippet.appspot.com/snippets.\n\n"
					+ "For more details about snippets, see: https://sites.google.com/a/songkick.com/weekly-snippets/home\n\n"
					+ "Thanks,\n\nThe Songkick Snippet System";
		}

		mailSender.sendEmail(emailAddress, "Snippet reminder", text);
	}

	public static String generateDigest(DataStorage dataStore) {
		if (digestCache == null) {
			SnippetPresentation presentation = new SnippetPresentation();
			digestCache = presentation.getSnippetsText(
					DateHandler.getCurrentWeek() - 1, dataStore);
		}

		Debug.log("Generated digest email: " + digestCache);
		
		return digestCache;
	}
}
