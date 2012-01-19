package com.songkick.snippets.client;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.songkick.common.model.UserDAO;
import com.songkick.snippets.client.model.HolidayDate;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("admin")
public interface AdminService extends RemoteService {
	List<UserDAO> getCurrentUserList() throws IllegalArgumentException;
	List<UserDAO> getFullUserList() throws IllegalArgumentException;
	
	public UserDAO getCurrentUser();
	
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
	
	public String getDigest();
	public void sendDigestToUser(UserDAO user);
	
	public void upgradeDatabase();
	
	public List<UserDAO> getDirectReports();
	
	// Holidays
	public List<HolidayDate> getHolidayDates();
	public void setHolidayDates(List<HolidayDate> dates);
	
	// Reviews
	public boolean startReview(UserDAO user, String dueDate, String period);
}
