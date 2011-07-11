package com.songkick.snippets.servlet;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.songkick.common.util.Debug;
import com.songkick.snippets.logic.ReminderHandler.MailType;
import com.songkick.snippets.model.ReminderEmail;

@SuppressWarnings("serial")
public class ReminderTaskQueue extends HttpServlet {
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		
		Debug.log("Task queue fired");
		
		String emailListParam = req.getParameter("emaillist");
		String mailType = req.getParameter("type");
		
		MailType type = Enum.valueOf(MailType.class, mailType);

		Debug.log("Query: " + req.getQueryString());
		Debug.log("emailList: " + emailListParam);
		Debug.log("mailType: " + type);
		
		String[] emails = emailListParam.split(",");
		
		for (String email: emails) {
			
			Debug.log("TaskQueue sends email to " + email);
			ReminderEmail.remindUser(email, type);
		}
	}
	
	
}