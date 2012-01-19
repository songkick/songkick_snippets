package com.songkick.snippets.servlet;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.songkick.common.util.Debug;
import com.songkick.snippets.logic.MailHandler;
import com.songkick.snippets.server.data.DataStorage;
import com.songkick.snippets.server.data.DataStorageHandler;

@SuppressWarnings("serial")
/**
 * Handle incoming emails. Emails sent to snippet@songkick.com are forwarded to snippet@sksnippets.appmail.com and are processed by this servlet.
 * The servlet parses the basic information from the email (sender, date sent, body) and passes it to a MailHandler for full processing.
 */
public class MailHandlerServlet extends HttpServlet {
	private MailHandler handler = new MailHandler();
	private DataStorage dataStore = new DataStorageHandler();

	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		Debug.log("Receiving incoming email");

		Properties props = new Properties();
		Session session = Session.getDefaultInstance(props, null);
		try {
			MimeMessage message = new MimeMessage(session, req.getInputStream());

			if (processMessage(message)) {
				Debug.log("Incoming email handled");
			} else {
				Debug.log("Failed to handle incoming email");
			}
		} catch (MessagingException e) {
			Debug.error("MessagingException: " + e);
			e.printStackTrace();
		}
	}

	private boolean processMessage(Message message) {
		try {
			String from = message.getFrom()[0].toString();
			Debug.log("Got message from: " + from);

			String date = getMessageDate(message);
			Debug.log("Message received date: " + date);

			Object content = message.getContent();
			Debug.log("" + content);

			if (content instanceof String) {
				handler.processMail(from, date, (String) content, dataStore);
				return true;
			}

			if (content instanceof MimeMultipart) {
				MimeMultipart mmp = (MimeMultipart) message.getContent();
				for (int i = 0; i < mmp.getCount(); i++) {
					BodyPart bodyPart = mmp.getBodyPart(i);

					if (bodyPart.getContent() instanceof String) {
						handler.processMail(from, date, (String) content, dataStore);
						return true;
					}
				}
			}

		} catch (MessagingException e) {
			Debug.log("processMessage MessagingException " + e);
			e.printStackTrace();
		} catch (IOException e) {
			Debug.log("processMessage IOException " + e);
			e.printStackTrace();
		}

		Debug.log("processMessage failed");
		return false;
	}

	/**
	 * Get the date a message was sent, returning a string that is in a format
	 * that JodaTime will successfully consume on the server side
	 * 
	 * @param message
	 * @return
	 */
	private String getMessageDate(Message message) {
		Date when = null;
		try {
			when = message.getReceivedDate();
			if (when == null) {
				when = message.getSentDate();
			}
			if (when == null) {
				Debug.log("Cannot find date from " + message);
				return null;
			}
		} catch (MessagingException e) {
			Debug.log("Cannot get message date: " + e);
			e.printStackTrace();
			return "";
		}

		Debug.log("Got date: " + when);

		DateFormat format = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");
		return format.format(when);
	}
}