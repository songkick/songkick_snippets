package com.songkick.snippets.client;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("admin")
public interface AdminService extends RemoteService {
	List<String> getUserList() throws IllegalArgumentException;
	
	void addUser(String email);
	
	void deleteUser(String email);	
	Long getCurrentWeek();
	
	boolean validateUser(String username, String password);
	
	void remindUser(String email);
	
	List<String> getSnippets(String email);
	
	boolean isValidAdmin();
}
