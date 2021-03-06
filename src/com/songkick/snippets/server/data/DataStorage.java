package com.songkick.snippets.server.data;

import java.util.List;

import com.songkick.snippets.client.model.HolidayDate;
import com.songkick.snippets.model.Snippet;
import com.songkick.snippets.model.User;
import com.songkick.snippets.model.reviews.PerformanceReview;
import com.songkick.snippets.model.reviews.ReviewData;

public interface DataStorage {
	public void save(Object object);
	
	public void delete(Object object);
	
	public List<User> getCurrentUsers();
	
	public List<User> getUsersForWeek(Long week);
	
	public List<User> getAllUsers();
	
	public List<Snippet> getSnippets();
	
	public List<User> getUsersWithSnippetFor(Long week);
	
	public User getUserById(Long id);

	public List<Snippet> getSnippetsForUser(User user);
	
	public List<Snippet> getSnippetsByWeek(Long week);
	
	public User getUserForSnippet(Snippet snippet);
	
	public List<HolidayDate> getAllHolidayDates();
	
	public List<PerformanceReview> getAllReviews();
	
	public ReviewData getReviewDataById(Long id);
}
