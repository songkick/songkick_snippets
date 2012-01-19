package com.songkick.snippets.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.songkick.common.util.Debug;
import com.songkick.snippets.model.Snippet;
import com.songkick.snippets.model.User;
import com.songkick.snippets.server.data.DataStorage;
import com.songkick.snippets.server.data.DataStorageHandler;

@SuppressWarnings("serial")
/**
 * Allow programmatic access to the datastore. Allows backup of data, for example.
 */
public class AccessServlet extends HttpServlet {
	private DataStorage dataStore = new DataStorageHandler();

	private enum Format {
		plain, json
	};

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		String what = req.getParameter("what");
		String username = req.getParameter("user");
		String weekNumber = req.getParameter("week");

		if (what == null) {
			writeError(resp, 500, "No 'what' parameter");
			return;
		}
		if (username == null) {
			writeError(resp, 500, "No 'user' parameter");
			return;
		}

		Format format = Format.plain;
		if (req.getParameter("format") != null) {
			format = Format.json;
		}

		if (what.equals("snippets")) {
			getSnippets(username, weekNumber, resp, format);
			return;
		}

		writeError(resp, 500, "Invalid 'what' option");
	}

	private User getUser(String username) {
		List<User> users = dataStore.getCurrentUsers();

		for (User user : users) {
			if (user.matchesEmail(username)) {
				return user;
			}
		}

		return null;
	}

	/**
	 * Send back a response with status code and descriptive text output to the
	 * Servlet Response
	 * 
	 * @param resp
	 * @param statusCode
	 * @param message
	 */
	private void writeError(HttpServletResponse resp, int statusCode,
			String message) {
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

	private void getSnippets(String username, String weekNumber,
			HttpServletResponse resp, Format format) {
		if (username.equals("all")) {
			List<User> users = dataStore.getCurrentUsers();

			for (User user : users) {
				getSnippetsForUser(user, weekNumber, resp, format);
			}
			return;
		}

		User user = getUser(username);

		if (user == null) {
			writeError(resp, 500, "Unknown user name " + username);
			return;
		}

		getSnippetsForUser(user, weekNumber, resp, format);
	}

	@SuppressWarnings("unchecked")
	private void getSnippetsForUser(User user, String weekNumber,
			HttpServletResponse resp, Format format) {
		int week = -1;
		if (weekNumber != null) {
			week = Integer.parseInt(weekNumber);
		}

		List<Snippet> snippets = dataStore.getSnippetsForUser(user);

		try {
			PrintWriter writer = resp.getWriter();

			JSONObject json = new JSONObject();
			JSONArray snippetArray = new JSONArray();
			if (format == Format.json) {
				json.put("user", user.getBestName());
				JSONArray emailArray = new JSONArray();
				emailArray.add(user.getOutgoingEmails().get(0));
				for (String email : user.getOtherEmails()) {
					emailArray.add(email);
				}
				json.put("email", emailArray);
			}
			for (Snippet snippet : snippets) {
				if (week == -1 || week == snippet.getWeekNumber()) {
					if (format == Format.json) {
						JSONObject snippetObject = new JSONObject();
						snippetObject.put("week", snippet.getWeekNumber());
						snippetObject.put("text", snippet.getSnippetText());

						snippetArray.add(snippetObject);
					} else {
						writer.println(snippet.getSnippetText());
					}
				}
			}
			if (format == Format.json) {
				json.put("snippets", snippetArray);
				writer.println(json.toString());
			}
			writer.close();
		} catch (IOException e) {
			writeError(resp, 500, "Could not get response writer");
			e.printStackTrace();
		}
	}
}
