package com.songkick.snippets.cron;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMultipart;

import com.songkick.snippets.util.Debug;
import com.songkick.snippets.util.ServerUtils;
import com.songkick.snippets.util.XoauthAuthenticator;
import com.sun.mail.imap.IMAPSSLStore;

/**
 * Pull all the emails from the Inbox of snippet@songkick.com. For each email,
 * get the From: and Body and upload them via the MailAPIServlet into the GAE
 * datastore.
 * 
 * Important - this code will not run successfully on GAE - the authentication
 * code does not work. It should be called from a cron job.
 * 
 * @author dancrow
 */
public class UploadEmails {

	public static void main(String args[]) {
		System.out.println("Starting");
		// Authenticate to GMail with OAuth
		IMAPSSLStore imapSSLStore = authenticate();
		if (imapSSLStore == null) {
			System.err.println("Authentication failed");
			return;
		}
		System.out.println("Successfully authenticated to IMAP");

		// Process all the messages in the Inbox
		try {
			Folder folder = imapSSLStore.getDefaultFolder();
			folder = folder.getFolder("INBOX");
			int messageCountBefore = folder.getMessageCount();
			folder.open(Folder.READ_WRITE);

			Message[] messages = folder.getMessages();
			for (Message message : messages) {
				if (processMessage(message)) {
					message.setFlag(Flags.Flag.DELETED, true);
				}
			}
			int messageCountAfter = folder.getMessageCount();
			folder.close(true);
			imapSSLStore.close();
			System.out.println("Done - processed "
					+ (messageCountBefore - messageCountAfter) + " messages");
			if (messageCountAfter > 0) {
				System.out.println("  " + messageCountAfter
						+ " messages remain unprocessed");
			}
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Authenticate to GMail with OAuth
	 * 
	 * @return a valid IMAPSSLStore if authentication succeeded, null otherwise
	 */
	private static IMAPSSLStore authenticate() {
		String email = "snippet@songkick.com";
		String oauthToken = "1/tU9GPGxzgb3zqjEYurV2v9aFTVb2qmGzRhLt9VptHvc";
		String oauthTokenSecret = "YYSdsYG2kkKvBHBKwVPtWxig";

		XoauthAuthenticator.initialize();
		try {
			return XoauthAuthenticator.connectToImap("imap.googlemail.com", 993,
					email, oauthToken, oauthTokenSecret,
					XoauthAuthenticator.getAnonymousConsumer(), false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Process a single email from the Inbox. Extract the sender and the email
	 * body and post that information to the Mail API
	 * 
	 * @param message
	 *          the email to process
	 * @return true iff the email was successfully posted to the server, false
	 *         otherwise
	 */
	private static boolean processMessage(Message message) {
		try {
			String from = message.getFrom()[0].toString();
			Debug.log("Got message from: " + from);
			Debug.log("" + message.getContent());

			Object content = message.getContent();

			if (content instanceof String) {
				return postEmail(from, (String) content);
			}

			if (content instanceof MimeMultipart) {
				MimeMultipart mmp = (MimeMultipart) message.getContent();
				for (int i = 0; i < mmp.getCount(); i++) {
					BodyPart bodyPart = mmp.getBodyPart(i);

					if (bodyPart.getContent() instanceof String) {
						return postEmail(from, (String) bodyPart.getContent());
					}
				}
			}

		} catch (MessagingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return false;
	}

	private static String cleanFrom(String from) {
		return from.replaceAll(" ", "%20");
	}

	private static boolean postEmail(String from, String body) {
		try {
			URL url = new URL("http://sksnippet.appspot.com/mailapi?from="
					+ cleanFrom(from));

			body = body.replaceAll("\n", "<br>");

			Debug.log("Posting to " + url);
			Debug.log("Posting body: " + body);

			ByteArrayOutputStream outStream = new ByteArrayOutputStream();
			ObjectOutputStream objOut = new ObjectOutputStream(outStream);
			StringWriter writer = new StringWriter();
			objOut.writeObject(body);

			ByteArrayInputStream inStream = new ByteArrayInputStream(
					outStream.toByteArray());

			try {
				ServerUtils.postData(inStream, url, writer);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}

			Debug.log("Post successfully completed");
			return true;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (ProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
}