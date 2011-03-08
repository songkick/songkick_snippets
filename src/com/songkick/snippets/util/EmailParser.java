package com.songkick.snippets.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.io.IOUtils;
import org.apache.geronimo.mail.util.QuotedPrintableDecoderStream;

public class EmailParser {
	private static final byte ESCAPE_CHAR = '=';

	private MimeMessage message = null;
	private String body = null;
	private String from = null;
	private String subject = null;

	public EmailParser(MimeMessage message) {
		this.message = message;

		parseMime();
	}

	private void parseMime() {
		try {
			from = message.getFrom()[0].toString();
			subject = message.getSubject();

			try {
				Object messageContent = getContent(message);

				Debug.log("Got contents: " + messageContent);

				if (messageContent instanceof String) {
					body = (String) messageContent;
					Debug.log("Got message body: " + body);
				} else if (messageContent instanceof MimeMultipart) {
					MimeMultipart multipart = (MimeMultipart) messageContent;
					for (int i = 0; i < multipart.getCount(); i++) {
						BodyPart bodyPart = multipart.getBodyPart(i);
						Debug.log("Checking bodyPart: " + bodyPart);
						Debug.log("Content type is " + bodyPart.getContentType());
						// if (bodyPart.getContentType().equalsIgnoreCase("text/plain")) {
						body = (String) getContent(bodyPart);
						Debug.log("Plain text: extracted " + body);
						// }
					}
				} else {
					Debug.log("Body is not readable");
				}
			} catch (Exception e) {
				Debug.log("Exception in parseMime: " + e.getMessage());
			}
		} catch (MessagingException e) {
			Debug.log("MessagingException in parseMime: " + e.getMessage());
		}
	}

	public String getFrom() {
		return from;
	}

	public String getBody() {
		return body;
	}

	private Object getContent(BodyPart part) throws Exception {
		Debug.log("getContent starts");
		String charset = contentType2Charset(part.getContentType(), null);
		Debug.log("getContent: charset is " + charset);
		Object content;
		try {
			content = part.getContent();
			Debug.log("Content is " + content);
		} catch (Exception e) {
			Debug.log("getContent exception: " + e);
			content = parseBadBodyPart(part, charset);
		}
		return content;
	}

	private Object parseBadBodyPart(BodyPart part, String charset) {
		try {
			if (part.getInputStream() instanceof QuotedPrintableDecoderStream) {
				Debug.log("Reading out of the QuotedPrintableDecoderStream");
				QuotedPrintableDecoderStream qpds = (QuotedPrintableDecoderStream) part.getInputStream();
				
				byte[] buffer = new byte[1024];
				String result = "";
				int remaining = 0;
				
				do {
					remaining = qpds.read(buffer);
					
					Debug.log("Read partial buffer: " + buffer);
					
					result += buffer.toString();
				} while (remaining>0);
				
				Debug.log("Final result is " + result);
				
				return result;
			}
			
			byte[] out = toByteArray(part.getInputStream());
			Debug.log("parseBadBodyPart: got input stream from bodyPart: " + out.toString());
			out = decodeQuotedPrintable(out);
			Debug.log("parseBadBodyPart: converted quoted printable to " + out.toString());
			Object content;
			if (charset != null) {
				content = new String(out, charset);
			} else {
				content = new String(out);
			}
			Debug.log("parseBadBodyPart: Got " + content);
			Debug.log("parseBadBodyPart: Content type is " + content.getClass().getName());
			
			
			return content;
		} catch (UnsupportedEncodingException e) {
			Debug.log("parseBadBodyPart: UnsupportedEncodingException " + e);
			e.printStackTrace();
		} catch (IOException e) {
			Debug.log("parseBadBodyPart: IOException " + e);
			e.printStackTrace();
		} catch (MessagingException e) {
			Debug.log("parseBadBodyPart: MessagingException " + e);
			e.printStackTrace();
		}
		return null;
	}

	private byte[] toByteArray(InputStream is) {
		String str = is.toString();
		return str.getBytes();
	}

	private Object getContent(MimeMessage part) throws Exception {
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
			Debug.log("decodeQuotedPrintable: returning null");
			return null;
		}
		Debug.log("decodeQuotedPrintable: starting");
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
					Debug.log("decodeQuotedPrintable: threw an exception: " + e);
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

	public String getSubject() {
		return subject;
	}
}
