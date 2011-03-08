package com.songkick.snippets.logic;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.songkick.snippets.util.Debug;

public class MailSender {
	public void sendEmail(String toEmail, String subject, String body) {
		Properties props = new Properties();
		Session session = Session.getDefaultInstance(props, null);

		InternetAddress recipient;
		try {
			recipient = new InternetAddress(toEmail, false);
		} catch (AddressException e1) {
			Debug.error("Could not create InternetAddress from " + toEmail + ". AddressException: " + e1);
			e1.printStackTrace();
			return;
		}
		
		try {
			Address[] replyTo = new InternetAddress[1];
			replyTo[0] = new InternetAddress("snippet@songkick.com", "Snippets");
			
			MimeMessage msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress("dancrow@songkick.com", "Snippets"));
			msg.setReplyTo(replyTo);
			msg.addRecipient(MimeMessage.RecipientType.TO, recipient);
			msg.setSubject(subject);
			msg.setText(body);
			Transport.send(msg);
			Debug.error("Message sent (async)");
		} catch (UnsupportedEncodingException e) {
			Debug.error("UnsupportedEncodingException: " + e);
			e.printStackTrace();
		} catch (MessagingException e) {
			Debug.error("MessagingException: " + e);
			e.printStackTrace();
		}
	}
}
