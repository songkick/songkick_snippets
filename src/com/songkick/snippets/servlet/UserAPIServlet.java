package com.songkick.snippets.servlet;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.songkick.common.util.Debug;
import com.songkick.snippets.model.User;
import com.songkick.snippets.server.data.DataStorage;
import com.songkick.snippets.server.data.DataStorageHandler;

@SuppressWarnings("serial")
/**
 * API to allow access to the user service
 */
public class UserAPIServlet extends HttpServlet {
	private DataStorage dataStore = new DataStorageHandler();
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
	throws IOException {
		String email = req.getParameter("email");
		
		if (email==null) {
			respond(resp, 500, "No email found");
			return;
		}
		
		List<User> users = dataStore.getAllUsers();
		
		for (User user:users) {
			if (user.matchesEmail(email)) {
				writeUser(resp, user);
				return;
			}
		}
		
		respond(resp, 404, "No matching email address found");
	}
	
	private void writeUser(HttpServletResponse resp, User user) {
		try {
			ServletOutputStream out = resp.getOutputStream();
			
			ObjectOutputStream objOut = new ObjectOutputStream(out);
			objOut.writeObject(user);

			objOut.writeObject(user);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
			respond(resp, 500, "Exception writing user object: " + e);
		}
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
}
