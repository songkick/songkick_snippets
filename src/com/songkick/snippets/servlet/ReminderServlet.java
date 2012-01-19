package com.songkick.snippets.servlet;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.joda.time.DateTimeConstants;

import com.songkick.common.util.Debug;
import com.songkick.snippets.logic.DateHandler;
import com.songkick.snippets.logic.ReminderHandler;
import com.songkick.snippets.logic.ReminderHandler.MailType;
import com.songkick.snippets.server.data.DataStorage;
import com.songkick.snippets.server.data.DataStorageHandler;

/**
 * Servlet that triggers reminder emails. There are three types of reminder
 * email:
 * 
 * On the first day of the week two reminder emails are sent, each with slightly
 * different wording. They are only sent to users who have not yet submitted a
 * snippet for the current week.
 * 
 * On the second day of the week, a digest email is sent with all the snippets
 * submitted this week
 * 
 * Usually the first day of the week is Monday. But on weeks that are marked in
 * the database as holiday weeks, the first day is Tuesday.
 * 
 * @author dancrow
 * 
 */
@SuppressWarnings("serial")
public class ReminderServlet extends HttpServlet {
	private ReminderHandler reminderHandler = new ReminderHandler();
	private DataStorage dataStore = new DataStorageHandler();

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		String which = req.getParameter("which");

		Debug.log("ReminderServlet.doGet: which=" + which);

		int day = DateHandler.getDayOfWeek();
		boolean isBankHolidayWeek = DateHandler.weekHasHolidayMonday(dataStore);

		Debug.log("ReminderServlet.doGet: day=" + day + " isBankHolidayWeek="
				+ isBankHolidayWeek);

		if (which.equalsIgnoreCase("digest")) {
			generateDigest(day, isBankHolidayWeek);
			return;
		}

		if (isBankHolidayWeek && day == DateTimeConstants.TUESDAY) {
			Debug
					.log("ReminderServlet.doGet: executing because its a bank holiday week and a Tuesday");
			generateReminder(which);
			return;
		}
		if (!isBankHolidayWeek && day == DateTimeConstants.MONDAY) {
			Debug
					.log("ReminderServlet.doGet: executing because its not a bank holiday week and a Monday");
			generateReminder(which);
			return;
		}
		Debug.log("ReminderServlet.doGet: Not executing");
	}

	private void generateDigest(int day, boolean isBankHolidayWeek) {
		if (isBankHolidayWeek && day == DateTimeConstants.WEDNESDAY) {
			reminderHandler.generateReminders(MailType.Digest, dataStore);
			Debug
					.log("ReminderServlet.generateDigest: executing because its a Bank Holiday week and a Wednesday");
			return;
		} else if (!isBankHolidayWeek && day == DateTimeConstants.TUESDAY) {
			reminderHandler.generateReminders(MailType.Digest, dataStore);
			Debug
					.log("ReminderServlet.generateDigest: executing because its not a Bank Holiday week and a Tuesday");
			return;
		}
		Debug.log("ReminderServlet.generateDigest: not executing");
	}

	/**
	 * Does the work of executing the correct reminder/digest
	 * 
	 * @param which
	 */
	private void generateReminder(String which) {
		if (which.equalsIgnoreCase("one")) {
			reminderHandler.generateReminders(MailType.FirstReminder, dataStore);
		} else if (which.equalsIgnoreCase("two")) {
			reminderHandler.generateReminders(MailType.SecondReminder, dataStore);
		}
	}
}
