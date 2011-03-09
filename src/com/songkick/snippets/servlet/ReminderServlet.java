package com.songkick.snippets.servlet;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.songkick.snippets.logic.ReminderHandler;
import com.songkick.snippets.logic.ReminderHandler.MailType;

@SuppressWarnings("serial")
public class ReminderServlet extends HttpServlet {
	private ReminderHandler reminderHandler = new ReminderHandler();

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		String which = req.getParameter("which");

		if (which.equalsIgnoreCase("one")) {
			reminderHandler.generateReminders(MailType.FirstReminder);
		} else if (which.equalsIgnoreCase("two")) {
			reminderHandler.generateReminders(MailType.SecondReminder);
		} else if (which.equalsIgnoreCase("digest")) {
			reminderHandler.sendDigest();
		}
	}
}
