package com.songkick.snippets.servlet;

import java.io.IOException;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.songkick.snippets.logic.MailSender;
import com.songkick.snippets.util.Debug;

@SuppressWarnings("serial")
/**
 * Send a "redirect" email alerting users to the new snippet@songkick.com email address
 */
public class MailHandlerServlet extends HttpServlet {
	private MailSender sender = new MailSender();

	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		Debug.log("Receiving incoming email");

		Properties props = new Properties();
		Session session = Session.getDefaultInstance(props, null);
		try {
			MimeMessage message = new MimeMessage(session, req.getInputStream());

			sendNotHereEmail(message);

			Debug.log("Incoming email handled");
		} catch (MessagingException e) {
			Debug.error("MessagingException: " + e);
			e.printStackTrace();
		}
	}

	private void sendNotHereEmail(MimeMessage message) {
		String from;
		try {
			from = message.getFrom()[0].toString();
		} catch (MessagingException e) {
			e.printStackTrace();
			return;
		}

		sender
				.sendEmail(
						from,
						"The snippets service has moved",
						"The snippets service has moved. Please re-send your snippet to: snippet@songkick.com and use this address in future.\n\n"
								+ "Thanks,\n\nThe Songkick Snippet System");
	}
}