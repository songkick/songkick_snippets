package com.songkick.snippets.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.songkick.snippets.logic.Authenticator;
import com.songkick.snippets.presentation.SnippetPresentation;
import com.songkick.snippets.server.data.DataStorage;
import com.songkick.snippets.server.data.DataStorageHandler;

@SuppressWarnings("serial")
public class SnippetsServlet extends HttpServlet {
	private SnippetPresentation snippetPresentation = new SnippetPresentation();
	private Authenticator authenticator = new Authenticator();
	private DataStorage dataStore = new DataStorageHandler();

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		resp.setContentType("text/html");

		// Don't allow access to non-Songkick users
		if (!authenticator.isSongkickUser(dataStore)) {
			return;
		}

		PrintWriter out = resp.getWriter();
		String query = req.getQueryString();
		
		if (query == null) {
			out.println("<title>Songkick snippets</title>");
			out.println("<link type=\"text/css\" rel=\"stylesheet\" href=\"SnippetReport.css\">");
			out.println("<h1>Songkick snippets</h1>");
			snippetPresentation.showUserList(out, dataStore);
			snippetPresentation.showWeekList(out);
		} else {
			if (query.startsWith("week=")) {
				Long week = Long.parseLong(query.substring(5, query.length()));

				out.println(snippetPresentation.getSnippetsHTML(week, dataStore));
			} else if (query.startsWith("person=")) {
				Long id = Long.parseLong(query.substring(7, query.length()));
			}
			out.println("<p><a href='snippets'>Return to week view</a>");
		}
	}
}
