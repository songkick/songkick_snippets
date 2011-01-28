package com.songkick.snippets.client;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface AdminServiceAsync {
	void getUserList(AsyncCallback<List<String>> callback)
			throws IllegalArgumentException;
	
	void addUser(String email, AsyncCallback<Void> callback);
	void deleteUser(String email, AsyncCallback<Void> callback);
	void remindUser(String email, AsyncCallback<Void> callback);
	
	void getCurrentWeek(AsyncCallback<Long> callback);

	void validateUser(String username, String password, AsyncCallback<Boolean> callback);
	
	void getSnippets(String email, AsyncCallback<List<String>> callback);
	
	void isValidAdmin(AsyncCallback<Boolean> callback);
}
