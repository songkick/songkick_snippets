package com.songkick.snippets.servlet;

import java.io.IOException;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.songkick.snippets.logic.Authenticator;
import com.songkick.snippets.logic.MailHandler;
import com.songkick.snippets.logic.MailSender;
import com.songkick.snippets.util.Debug;

@SuppressWarnings("serial")
/**
 * Handle incoming emails. The work is handled by the MailHandler class
 */
public class MailHandlerServlet extends HttpServlet {

	private Authenticator authenticator = new Authenticator();
	private MailHandler mailHandler = new MailHandler();
	private MailSender sender = new MailSender();

	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		Debug.log("Receiving incoming email");

		Properties props = new Properties();
		Session session = Session.getDefaultInstance(props, null);
		try {
			MimeMessage message = new MimeMessage(session, req.getInputStream());

			sendNotHereEmail(message);
			//processEmail(message);

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

	private void processEmail(MimeMessage message) {
		try {
			String fromAddress = message.getFrom()[0].toString();
			Debug.log("Processing email from " + fromAddress);
			Debug.dbLog("Received email from " + fromAddress);

			if (authenticator.isValidEmail(fromAddress)) {
				mailHandler.handleEmail(message);
				return;
			}

			sender.sendEmail(fromAddress, "Could not process your email",
					"This service only accepts emails from @songkick.com accounts");
			Debug.log("Could not authenticate sender address: " + fromAddress);
			Debug.dbLog("Could not authenticate");
		} catch (MessagingException e) {
			Debug.error("MessagingException: " + e);
			e.printStackTrace();
		}
	}
}