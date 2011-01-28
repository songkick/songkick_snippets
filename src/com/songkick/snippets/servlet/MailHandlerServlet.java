package com.songkick.snippets.servlet;

import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.songkick.snippets.logic.Authenticator;
import com.songkick.snippets.logic.MailHandler;
import com.songkick.snippets.logic.MailSender;

@SuppressWarnings("serial")
public class MailHandlerServlet extends HttpServlet {
	private static final Logger log = Logger.getLogger(MailHandlerServlet.class
			.getName());
	
	private Authenticator authenticator = new Authenticator();
	private MailHandler mailProcessor = new MailHandler();
	private MailSender sender = new MailSender();

	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		log.severe("Receiving incoming email");

		Properties props = new Properties();
		Session session = Session.getDefaultInstance(props, null);
		try {
			MimeMessage message = new MimeMessage(session, req.getInputStream());

			processEmail(message);
			
			log.severe("Incoming email handled");
		} catch (MessagingException e) {
			log.severe("MessagingException: " + e);
			e.printStackTrace();
		}
	}

	private void processEmail(MimeMessage message) {
		try {
			log.severe("Processing email from " + message.getFrom().toString());
			
			if (authenticator.authenticate(message.getFrom()[0])) {
				mailProcessor.handleEmail(message);
				return;
			}
			sender.sendEmail(message.getFrom()[0].toString(), "Could not process your email", "This service only accepts emails from @songkick.com accounts");
			
			log.severe("Could not authenticate sender address: " + message.getFrom()[0]);
		} catch (MessagingException e) {
			log.severe("MessagingException: " + e);
			e.printStackTrace();
		}
	}
}