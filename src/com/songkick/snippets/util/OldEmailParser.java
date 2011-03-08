package com.songkick.snippets.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import nl.tomvanzummeren.appengine.inboundmail.Body;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;

public class OldEmailParser {
	private static final byte ESCAPE_CHAR = '=';
	private static final String PLAIN_TEXT_CONTENT_TYPE = "text/plain";
	private static final String HTML_CONTENT_TYPE = "text/html";
	private MimeMessage mimeMessage;
	private InternetAddress fromAddress;
	private InternetAddress[] toAddresses;
	private InternetAddress[] ccAddresses;
	private InternetAddress[] bccAddresses;
	private String subject;
	private List<Body> bodies;

	/**
	 * Constructs a new <code>EmailMessage</code> based on a mime message.
	 * 
	 * @param mimeMessage
	 *          email mime message
	 * @throws MessagingException
	 *           when an error occurs reading the mime message
	 * @throws IOException
	 *           when an error occurs working with I/O
	 */
	public OldEmailParser(MimeMessage mimeMessage) throws Exception {
		this.mimeMessage = mimeMessage;
		bodies = new ArrayList<Body>();

		Debug.log("OldEmailParser: ctor");
		parseMimeMessage();
	}

	/**
	 * Gets the plain text body of this email if any.
	 * 
	 * @return plain text body or <code>null</code> if this email doesn't have one
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
	 *          content type of body
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
	 * Initializes the fields of this email by parsing the mime message this class
	 * was constructed with.
	 * 
	 * @throws MessagingException
	 *           when an error occurs reading the mime message
	 * @throws IOException
	 *           when an error occurs working with I/O
	 */
	private void parseMimeMessage() throws MessagingException, Exception {
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

		Debug.log("Parsing contents");
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
	 *          content to parse
	 * @param contentType
	 *          type of the content (e.g. "text/plain")
	 * @throws Exception
	 * @throws MessagingException
	 */
	private void parseContent(Object content, String contentType)
			throws MessagingException, Exception {
		if (content instanceof Multipart) {
			Multipart multipart = (Multipart) content;
			for (int i = 0; i < multipart.getCount(); i++) {
				BodyPart bodyPart = multipart.getBodyPart(i);
				bodies.add(new Body(bodyPart.getContentType().toLowerCase(),
						(String) getContent(bodyPart)));
			}
		} else if (content instanceof InputStream) {
			throw new MessagingException("Attachement");
		} else if (content instanceof String) {
			String string = (String) content;
			bodies.add(new Body(contentType.toLowerCase(), string));
		}
	}

	private Object getContent(BodyPart part) throws Exception {
		String charset = contentType2Charset(part.getContentType(), null);
		Object content;
		try {
			content = part.getContent();
		} catch (Exception e) {
			try {
				byte[] out = IOUtils.toByteArray(part.getInputStream());
				out = decodeQuotedPrintable(out);
				if (charset != null) {
					content = new String(out, charset);
				} else {
					content = new String(out);
				}
			} catch (Exception e1) {
				throw e;
			}
		}
		return content;
	}

	private byte[] decodeQuotedPrintable(byte[] bytes) throws IOException {
		if (bytes == null) {
			return null;
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		for (int i = 0; i < bytes.length; i++) {
			int b = bytes[i];
			if (b == ESCAPE_CHAR) {
				try {
					if (bytes[i + 1] == 10) {
						// FIX skip newline, lenient
						++i;
					} else {
						int u = digit16(bytes[++i]);
						int l = digit16(bytes[++i]);
						out.write((char) ((u << 4) + l));
					}
				} catch (Exception e) {
					throw new IOException("Invalid quoted-printable encoding", e);
				}
			} else {
				out.write(b);
			}
		}
		return out.toByteArray();
	}

	private int digit16(byte b) throws IOException {
		int i = Character.digit(b, 16);
		if (i == -1) {
			throw new IOException("Invalid encoding: not a valid digit (radix 16): "
					+ b);
		}
		return i;
	}

	private String contentType2Charset(String contentType, String defaultCharset) {
		String charset = defaultCharset;
		if (contentType.indexOf("charset=") != -1) {
			String[] split = contentType.split("charset=");
			if (split.length > 1) {
				charset = split[1];
				if (charset.indexOf(';') >= 0) {
					charset = charset.substring(0, charset.indexOf(';'));
				}
				charset = charset.replaceAll("\"", "");
				charset = charset.trim();
			}
		}
		return charset;
	}
}
