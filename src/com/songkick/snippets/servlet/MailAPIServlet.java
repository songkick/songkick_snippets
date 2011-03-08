package com.songkick.snippets.servlet;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.io.Serializable;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;
import com.songkick.snippets.logic.Authenticator;
import com.songkick.snippets.logic.DateHandler;
import com.songkick.snippets.logic.MailHandler;
import com.songkick.snippets.model.Snippet;
import com.songkick.snippets.model.User;
import com.songkick.snippets.util.Debug;

@SuppressWarnings("serial")
/**
 * Servlet to provide an API for the UploadEmails program. 
 * 
 * The servlet should be called with the From address in the from param. The body of a call contains a serialized java.lang.String containing the body of the email. 
 */
public class MailAPIServlet extends HttpServlet {
	private MailHandler handler = new MailHandler();
	private Authenticator authenticator = new Authenticator();

	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		String from = req.getParameter("from");

		if (!authenticator.isValidEmail(from)) {
			respond(resp, 401, "Unable to authenticate your email address");
			return;
		}

		ObjectInputStream inStream = new ObjectInputStream(req.getInputStream());
		try {
			Serializable object = (Serializable) inStream.readObject();
			String result = processMail(from, (String) object);

			Debug.log(result);

			respond(resp, 200, result);
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

	private String processMail(String from, String emailBody) {
		User user = handler.getOrCreateUser(from);

		Debug.log("Got user: " + user);

		Snippet snippet = new Snippet(user, emailBody);

		snippet.setDate(handler.getDateNow());
		snippet.setWeekNumber(DateHandler.getCurrentWeek());

		Debug.dbLog("Created snippet from email: " + snippet);

		Objectify ofy = ObjectifyService.begin();
		ofy.put(snippet);

		return "Got message from " + from + ": " + emailBody;
	}
}
