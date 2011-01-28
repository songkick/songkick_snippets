package com.songkick.snippets.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import nl.tomvanzummeren.appengine.inboundmail.Attachment;

/**
 * Represents a <code>MimeMessage</code> as an email message. This makes it easy
 * to:
 * 
 * <ul>
 * <li>extract attached file(s)</li>
 * <li>extract from, to, cc and bcc email addresses</li>
 * <li>extract HTML and/or plain text body</li>
 * </ul>
 * 
 * @author Tom van Zummeren
 */
public class EmailMessage {
	private static final Logger log = Logger.getLogger(EmailMessage.class
			.getName());
	
	private static final String PLAIN_TEXT_CONTENT_TYPE = "text/plain";
	private static final String HTML_CONTENT_TYPE = "text/html";

	private MimeMessage mimeMessage;
	private InternetAddress fromAddress;
	private InternetAddress[] toAddresses;
	private InternetAddress[] ccAddresses;
	private InternetAddress[] bccAddresses;
	private String subject;
	private List<Attachment> attachments;
	private List<Body> bodies;

	/**
	 * Constructs a new <code>EmailMessage</code> based on a mime message.
	 * 
	 * @param mimeMessage
	 *            email mime message
	 * @throws MessagingException
	 *             when an error occurs reading the mime message
	 * @throws IOException
	 *             when an error occurs working with I/O
	 */
	public EmailMessage(MimeMessage mimeMessage) throws MessagingException,
			IOException {
		
		this.mimeMessage = mimeMessage;
		bodies = new ArrayList<Body>();
		attachments = new ArrayList<Attachment>();

		parseMimeMessage();
	}

	/**
	 * Gets the plain text body of this email if any.
	 * 
	 * @return plain text body or <code>null</code> if this email doesn't have
	 *         one
	 */
	public String getPlainTextBody() {
		return findBodyOfType(PLAIN_TEXT_CONTENT_TYPE);
	}

	/**
	 * Gets the HTML body of this email if any.
	 * 
	 * @return HTML body or <code>null</code> if this email doesn't have one
	 */
	public String getHtmlBody() {
		return findBodyOfType(HTML_CONTENT_TYPE);
	}

	/**
	 * Gets all files attached to this email.
	 * 
	 * @return the attachments of this email
	 */
	public List<Attachment> getAttachments() {
		return attachments;
	}

	/**
	 * Gets the email address this email was sent from.
	 * 
	 * @return from email address
	 */
	public InternetAddress getFromAddress() {
		return fromAddress;
	}

	/**
	 * Gets all email addresses this email was sent to.
	 * 
	 * @return to email addresses
	 */
	public InternetAddress[] getToAddresses() {
		return toAddresses;
	}

	/**
	 * Gets all email addresses this email was sent to as a CC.
	 * 
	 * @return to email addresses
	 */
	public InternetAddress[] getCcAddresses() {
		return ccAddresses;
	}

	/**
	 * Gets all email addresses this email was sent to as a BCC.
	 * 
	 * @return to email addresses
	 */
	public InternetAddress[] getBccAddresses() {
		return bccAddresses;
	}

	/**
	 * Gets the subject of this email.
	 * 
	 * @return subject
	 */
	public String getSubject() {
		return subject;
	}

	/**
	 * Finds a specific body by content type.
	 * 
	 * @param contentType
	 *            content type of body
	 * @return the body of the requested type or <code>null</code> if it doesn't
	 *         exist
	 */
	private String findBodyOfType(String contentType) {
		for (Body body : bodies) {
			if (body.getContentType().startsWith(contentType)) {
				return body.getContent();
			}
		}
		return null;
	}

	/**
	 * Initializes the fields of this email by parsing the mime message this
	 * class was constructed with.
	 * 
	 * @throws MessagingException
	 *             when an error occurs reading the mime message
	 * @throws IOException
	 *             when an error occurs working with I/O
	 */
	private void parseMimeMessage() throws MessagingException, IOException {
		Address[] fromAddresses = mimeMessage.getFrom();
		if (fromAddresses.length > 0) {
			fromAddress = (InternetAddress) fromAddresses[0];
		}
		toAddresses = (InternetAddress[]) mimeMessage
				.getRecipients(Message.RecipientType.TO);
		ccAddresses = (InternetAddress[]) mimeMessage
				.getRecipients(Message.RecipientType.CC);
		bccAddresses = (InternetAddress[]) mimeMessage
				.getRecipients(Message.RecipientType.BCC);
		subject = mimeMessage.getSubject();
		
		parseContent(mimeMessage.getContent(), mimeMessage.getContentType());
	}
	
	/**
	 * Parses the content of the mime message recursively. The given content can
	 * be of type <code>InputStream</code>, <code>String</code> or
	 * <code>Multipart</code>. If the content is of the latter type it contains
	 * multiple parts. Each part can by itself by either of the three types
	 * mentioned before.
	 * 
	 * <p/>
	 * This method keeps running recursively until no <code>Multipart</code>
	 * content objects are left.
	 * 
	 * @param content
	 *            content to parse
	 * @param contentType
	 *            type of the content (e.g. "text/plain")
	 * @throws MessagingException
	 *             when an error occurs reading the mime message
	 * @throws IOException
	 *             when an error occurs working with I/O
	 */
	private void parseContent(Object content, String contentType)
			throws IOException, MessagingException {
		log.severe("parsing contents of type " + contentType);
		if (content instanceof Multipart) {
			log.severe("Multipart");
			Multipart multipart = (Multipart) content;
			log.severe("There are " + multipart.getCount() + " parts");
			for (int i = 0; i < multipart.getCount(); i++) {
				BodyPart bodyPart = multipart.getBodyPart(i);
				parseContent(bodyPart.getContent(), bodyPart.getContentType());
			}
		} else if (content instanceof InputStream) {
			log.severe("InputStream");
			InputStream inputStream = (InputStream) content;
			attachments.add(new Attachment(contentType.toLowerCase(),
					inputStream));
		} else if (content instanceof String) {
			String string = (String) content;
			log.severe("String: " + string);
			bodies.add(new Body(contentType.toLowerCase(), string));
		}
	}
}
