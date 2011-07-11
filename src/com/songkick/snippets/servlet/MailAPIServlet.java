package com.songkick.snippets.servlet;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.io.Serializable;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.songkick.common.util.Debug;
import com.songkick.snippets.logic.AdminNotifier;
import com.songkick.snippets.logic.Authenticator;
import com.songkick.snippets.logic.MailHandler;
import com.songkick.snippets.server.data.DataStorage;
import com.songkick.snippets.server.data.DataStorageHandler;

@SuppressWarnings("serial")
/**
 * Servlet to provide an API for the UploadEmails program. 
 * 
 * The servlet should be called with the From address and the Date the email was sent in the query params. 
 * The body of a call contains a serialized java.lang.String containing 
 * the body of the email. 
 */
public class MailAPIServlet extends HttpServlet {
	private MailHandler handler = new MailHandler();
	private Authenticator authenticator = new Authenticator();
	private DataStorage dataStore = new DataStorageHandler();
	private AdminNotifier adminNotifier = new AdminNotifier();

	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		String from = req.getParameter("from");

		if (!authenticator.isValidEmail(from, dataStore)) {
			respond(resp, 401, "Unable to authenticate your email address");
			return;
		}

		String date = req.getParameter("date");

		ObjectInputStream inStream = new ObjectInputStream(req.getInputStream());
		try {
			Serializable object = (Serializable) inStream.readObject();
			handler.processMail(from, date, (String) object, dataStore);

			if (((String) object).length() < 2) {
				adminNotifier.notify("short snippet received",
						"Received a very short (<2 characters) snippet from " + from
								+ " with date " + date);
			}

			Debug.log("Got email from " + from + " on " + date + ": " + object);

			respond(resp, 200, "");
			return;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		respond(resp, 500, "Could not deserialize class");
		return;
	}

	/**
	 * Send back a response with status code and descriptive text output to the
	 * Servlet Response
	 * 
	 * @param resp
	 * @param statusCode
	 * @param message
	 */
	private void respond(HttpServletResponse resp, int statusCode, String message) {
		try {
			PrintWriter writer = resp.getWriter();

			writer.println(statusCode);
			writer.println(message);
			writer.close();
		} catch (IOException e) {
			Debug.error("Cannot respond: " + e);
			e.printStackTrace();
		}
	}

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		respond(resp, 501, "Only POST is supported by this API");
	}
}
