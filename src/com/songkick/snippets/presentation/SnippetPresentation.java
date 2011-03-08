package com.songkick.snippets.presentation;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.Query;
import com.songkick.snippets.logic.DateHandler;
import com.songkick.snippets.model.Snippet;
import com.songkick.snippets.model.SnippetInMemory;
import com.songkick.snippets.model.User;

/**
 * Simple presentation layer for snippets web page
 * 
 * @author dancrow
 */
public class SnippetPresentation {
	public SnippetPresentation() {
		ObjectifyService.register(Snippet.class);
		ObjectifyService.register(User.class);
	}

	public void showWeekList(PrintWriter out) throws IOException {
		Long lastWeek = DateHandler.getCurrentWeek() - 1;

		out.println("<b>By week:</b>");
		out.println("<ul>");

		// Display the weeks in reverse order
		for (Long week = lastWeek; week > 0; week--) {
			out.print("<li>");
			if (week == lastWeek) {
				out.print("<a href='snippets?week=" + week + "'>Last week</a>");
			} else {
				out.print("<a href='snippets?week=" + week + "'>Week from "
						+ DateHandler.weekToDate(week) + " to "
						+ DateHandler.weekToDate(week + 1));
			}
			out.println("</li>");
		}

		out.println("</ul>");
	}

	/**
	 * Show the list of users
	 * 
	 * @param out
	 * @throws IOException
	 */
	public void showUserList(PrintWriter out) throws IOException {
		out.println("<b>By user:</b>");
		out.print("<ul id=\"navlist\">");
		List<User> users = getUsers();
		for (User user : users) {
			out.print("<li><a href='snippets?person=" + user.getId() + "'>"
					+ user.getBestName().replaceAll(" ", "&nbsp;") + "</a></li> ");
		}
		out.println("</ul>");
	}

	/**
	 * Get all the snippets for a particular user, identified by id
	 * 
	 * @param name
	 * @return
	 */
	public String getSnippetsHTMLFor(Long userId) {
		Objectify ofy = ObjectifyService.begin();
		Query<User> q = ofy.query(User.class).filter("id", userId);
		List<User> userList = q.list();
		if (userList.size() != 1) {
			return "<html>Cannot locate user " + userId + "</html>";
		}

		User user = userList.get(0);

		Query<Snippet> snippetQuery = ofy.query(Snippet.class).filter("user", user);

		String html = "<html>";
		html += generateSnippetHTML(user, snippetQuery.list());
		html += "</html>";

		return html;
	}

	public List<User> getUsers() {
		Objectify ofy = ObjectifyService.begin();
		Query<User> q = ofy.query(User.class);
		List<User> results = new ArrayList<User>();

		for (User user : q) {
			results.add(user);
		}

		return results;
	}

	/**
	 * Get snippets for past week
	 * 
	 * @return
	 */
	public String getSnippetsHTML(Long week) {
		List<SnippetInMemory> snippets = getSnippets(week);
		List<User> users = getUsers();
		List<User> usersWithSnippet = new ArrayList<User>();

		// Add all the snippets for this week to their appropriate users
		for (SnippetInMemory snippet : snippets) {
			User nextUser = snippet.getUserInMemory();
			boolean newUser = true;

			for (User user : usersWithSnippet) {
				if (nextUser.getId().equals(user.getId())) {
					newUser = false;
					user.addSnippet(snippet.getSnippet());
				}
			}

			if (newUser) {
				nextUser.addSnippet(snippet.getSnippet());
				usersWithSnippet.add(nextUser);
			}
		}

		// Find out who didn't submit a snippet this week
		List<User> usersWithoutSnippet = new ArrayList<User>();
		for (User user : users) {
			boolean hasSnippet = false;
			for (User snippetUser : usersWithSnippet) {
				if (snippetUser.getId().equals(user.getId())) {
					hasSnippet = true;
				}
			}

			if (!hasSnippet) {
				usersWithoutSnippet.add(user);
			}
		}

		String text = "<html>";
		text += "<link type=\"text/css\" rel=\"stylesheet\" href=\"SnippetReport.css\">";
		text += "<title>Snippets for " + DateHandler.weekToDate(week) + " to "
				+ DateHandler.weekToDate(week + 1) + "</title>";
		text += "<h1>Songkick snippets for " + DateHandler.weekToDate(week)
				+ " to " + DateHandler.weekToDate(week + 1) + "</h1>";
		text += "<body>";
		for (User user : usersWithSnippet) {
			text += "<p>" + generateSnippetHTML(user, user.getSnippetList()) + "</p>";
		}

		if (usersWithoutSnippet.size() > 0) {
			text += "<p>No snippets received from: ";
			for (User user : usersWithoutSnippet) {
				text += user.getBestName() + ", ";
			}
			text += "</p>";
		}

		text += "</body>";
		text += "</html>";

		return text;
	}

	private String generateSnippetHTML(User user, List<Snippet> snippets) {
		String html = "";

		html += "<h2>From: "
				+ user.getBestName().replaceAll("<", "[").replaceAll(">", "]")
				+ "</h2>";

		for (Snippet snippet : snippets) {
			if (snippet.getSnippetText() != null) {
				html += "<p>" + snippet.getSnippetText() + "</p>";
				if (snippet.getDate() != null) {
					html += "<h3>Sent: " + snippet.getDate() + "</h3>";
				}
			}
		}

		return html;
	}

	/**
	 * Return the list of snippets for last week
	 * 
	 * @return
	 */
	private List<SnippetInMemory> getSnippets(Long week) {
		Objectify ofy = ObjectifyService.begin();
		Query<Snippet> q = ofy.query(Snippet.class).filter("weekNumber", week);
		List<SnippetInMemory> snippets = new ArrayList<SnippetInMemory>();
		for (Snippet snippet : q) {
			SnippetInMemory sim = new SnippetInMemory(snippet);
			sim.prep();
			snippets.add(sim);
		}
		return snippets;
	}
}