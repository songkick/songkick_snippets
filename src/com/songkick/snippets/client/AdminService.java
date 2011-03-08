package com.songkick.snippets.client;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.songkick.snippets.shared.dao.UserDAO;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("admin")
public interface AdminService extends RemoteService {
	List<UserDAO> getUserList() throws IllegalArgumentException;
	
	void addUser(UserDAO dao);
	void updateUser(UserDAO dao);
	
	void deleteUser(UserDAO dao);	
	Long getCurrentWeek();
	
	void remindUser(UserDAO dao);
	
	List<String> getSnippets(UserDAO dao);
	
	String isValidAdmin(String redirectURL);
	
	void remindUsers();
	
	List<UserDAO> getUsersToRemind();
	
	void addSnippet(UserDAO dao, String snippet, int week);
	
	public void replaceSnippet(UserDAO dao, String snippet, Long week);
	
	String getLog();
	
	public void getIMAPEmail();
	
	public String getSnippet(UserDAO user, Long weekNumber);
}
