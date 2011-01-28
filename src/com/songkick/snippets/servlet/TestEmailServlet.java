package com.songkick.snippets.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletException;
import javax.servlet.http.*;

@SuppressWarnings("serial")
public class TestEmailServlet extends HttpServlet {
	public static final Logger _log = Logger.getLogger(TestEmailServlet.class
			.getName());

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		try {

			Properties props = new Properties();
			Session session = Session.getDefaultInstance(props, null);
			MimeMessage message = new MimeMessage(session, req.getInputStream());

			// Extract out the important fields from the Mime Message
			String subject = message.getSubject();

			_log.info("Got an email. Subject = " + subject);

			String contentType = message.getContentType();
			_log.info("Email Content Type : " + contentType);

			printParts(message);
			// Parse out the Multiparts
			// Perform business logic based on the email
		} catch (Exception ex) {
			_log.log(Level.WARNING,
					"Failure in receiving email : " + ex.getMessage());
		}
	}

	private static void printParts(Part p) throws IOException,
			MessagingException {
		Object o = p.getContent();

		if (o instanceof String) {
			System.out.println("This is a String");
			String text = (String) o;
			
			while (text.length()>50) {
				System.out.println(text.substring(0, 50));
				text = text.substring(50, text.length());
			}
			System.out.println(text);
		} else if (o instanceof Multipart) {
			System.out.println("This is a Multipart");
			Multipart mp = (Multipart) o;

			int count = mp.getCount();
			for (int i = 0; i < count; i++) {
				printParts(mp.getBodyPart(i));
			}
		} else if (o instanceof InputStream) {
			System.out.println("This is just an input stream");
			InputStream is = (InputStream) o;
			int c;
			while ((c = is.read()) != -1)
				System.out.write(c);
		}
	}

}
