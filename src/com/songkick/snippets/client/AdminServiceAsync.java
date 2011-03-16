package com.songkick.snippets.client;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.songkick.snippets.shared.dao.UserDAO;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface AdminServiceAsync {
	void getUserList(AsyncCallback<List<UserDAO>> callback)
			throws IllegalArgumentException;
	
	void addUser(UserDAO dao, AsyncCallback<Void> callback);
	void updateUser(UserDAO dao, AsyncCallback<Void> callback);
	void deleteUser(UserDAO dao, AsyncCallback<Void> callback);
	void remindUser(UserDAO dao, AsyncCallback<Void> callback);
	
	void getCurrentWeek(AsyncCallback<Long> callback);
	
	void getSnippets(UserDAO dao, AsyncCallback<List<String>> callback);
	public void getSnippet(UserDAO dao, Long weekNumber, AsyncCallback<String> callback);
	
	void isValidAdmin(String redirectURL, AsyncCallback<String> callback);
	
	void remindUsers(AsyncCallback<Void> callback);
	
	void addSnippet(UserDAO dao, String snippet, int week, AsyncCallback<Void> callback);
	public void replaceSnippet(UserDAO user, String snippet, Long weekNumber, AsyncCallback<Void> callback);
	
	public void getLog(AsyncCallback<String> callback);
	
	public void getUsersToRemind(AsyncCallback<List<UserDAO>> callback);
}
