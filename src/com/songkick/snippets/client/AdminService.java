package com.songkick.snippets.client;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.songkick.common.model.UserDAO;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("admin")
public interface AdminService extends RemoteService {
	List<UserDAO> getCurrentUserList() throws IllegalArgumentException;
	List<UserDAO> getFullUserList() throws IllegalArgumentException;
	
	void addUser(UserDAO dao);
	void updateUser(UserDAO dao);
	
	void deleteUser(UserDAO dao);	
	Long getCurrentWeek();
	
	void remindUser(UserDAO dao);
	
	List<String> getSnippets(UserDAO dao);
	
	String isValidAdmin(String redirectURL);
	
	List<UserDAO> getUsersToRemind();
	
	void addSnippet(UserDAO dao, String snippet, int week);
	
	public void replaceSnippet(UserDAO dao, String snippet, Long week);
	
	public String getSnippet(UserDAO user, Long weekNumber);
	
	public void deleteSnippet(UserDAO user, Long week);
}
