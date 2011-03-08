package com.songkick.snippets.servlet;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.songkick.snippets.logic.ReminderHandler;
import com.songkick.snippets.logic.ReminderHandler.MailType;

@SuppressWarnings("serial")
public class Reminder2Servlet extends HttpServlet {
	private ReminderHandler reminderHandler = new ReminderHandler();

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		reminderHandler.generateReminders(MailType.SecondReminder);
	}
}

