package com.songkick.snippets.server.data;

import java.util.ArrayList;
import java.util.List;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.Query;
import com.songkick.snippets.client.model.HolidayDate;
import com.songkick.snippets.logic.DateHandler;
import com.songkick.snippets.model.Snippet;
import com.songkick.snippets.model.User;
import com.songkick.snippets.model.reviews.PerformanceReview;
import com.songkick.snippets.model.reviews.ReviewData;

/**
 * API to hide the details of the data storage mechanism
 * 
 * @author dancrow
 * 
 */
public class DataStorageHandler extends DataStorageBase {

	public DataStorageHandler() {
		// Register the database classes used
		ObjectifyService.register(User.class);
		ObjectifyService.register(Snippet.class);
		ObjectifyService.register(HolidayDate.class);
		ObjectifyService.register(PerformanceReview.class);
		ObjectifyService.register(ReviewData.class);
	}

	@Override
	public void save(Object object) {
		Objectify ofy = ObjectifyService.begin();
		ofy.put(object);
	}

	@Override
	public void delete(Object object) {
		Objectify ofy = ObjectifyService.begin();
		ofy.delete(object);
	}

	@Override
	public List<User> getAllUsers() {
		Objectify ofy = ObjectifyService.begin();
		Query<User> users = ofy.query(User.class);
		List<User> results = new ArrayList<User>();

		for (User user : users) {
			results.add(transformUser(user));
		}

		return results;
	}

	@Override
	public List<HolidayDate> getAllHolidayDates() {
		Objectify ofy = ObjectifyService.begin();
		Query<HolidayDate> dates = ofy.query(HolidayDate.class);
		List<HolidayDate> results = new ArrayList<HolidayDate>();

		for (HolidayDate date : dates) {
			results.add(date);
		}

		return results;
	}

	private User transformUser(User user) {
		/*
		 * if (user.getEmailAddress() != null) {
		 * user.getPrimaryEmails().add(user.getEmailAddress());
		 * user.setEmailAddress(null); }
		 */
		return user;
	}

	@Override
	public List<User> getUsersForWeek(Long week) {
		List<User> users = getAllUsers();

		List<User> currentUsers = new ArrayList<User>();

		for (User user : users) {
			if (isCurrentUser(user, week)) {
				currentUsers.add(transformUser(user));
			}
		}

		return currentUsers;
	}

	@Override
	public List<User> getCurrentUsers() {
		return getUsersForWeek(DateHandler.getCurrentWeek());
	}

	@Override
	public List<Snippet> getSnippets() {
		Objectify ofy = ObjectifyService.begin();
		Query<Snippet> snippets = ofy.query(Snippet.class);

		return snippets.list();
	}

	/**
	 * Get a list of users who have a snippet for the specified week
	 * 
	 * @param week
	 * @return
	 */
	@Override
	public List<User> getUsersWithSnippetFor(Long week) {
		List<User> users = new ArrayList<User>();

		Objectify ofy = ObjectifyService.begin();
		Query<Snippet> q = ofy.query(Snippet.class).filter("weekNumber", week);

		for (Snippet snippet : q) {
			User user = ofy.get(snippet.getUser());
			if (!users.contains(user)) {
				users.add(user);
			}
		}
		return users;
	}

	@Override
	public User getUserById(Long id) {
		Objectify ofy = ObjectifyService.begin();
		Query<User> q = ofy.query(User.class).filter("id", id);
		List<User> userList = q.list();
		if (userList.size() != 1) {
			return null;
		}

		return transformUser(userList.get(0));
	}

	@Override
	public User getUserForSnippet(Snippet snippet) {
		Objectify ofy = ObjectifyService.begin();
		return ofy.get(snippet.getUser());
	}

	@Override
	public List<Snippet> getSnippetsForUser(User user) {
		Objectify ofy = ObjectifyService.begin();
		Query<Snippet> snippetQuery = ofy.query(Snippet.class).filter("user", user);

		return snippetQuery.list();
	}

	@Override
	public List<Snippet> getSnippetsByWeek(Long week) {
		Objectify ofy = ObjectifyService.begin();
		Query<Snippet> q = ofy.query(Snippet.class).filter("weekNumber", week);

		return q.list();
	}

	@Override
	public List<PerformanceReview> getAllReviews() {
		Objectify ofy = ObjectifyService.begin();
		Query<PerformanceReview> q = ofy.query(PerformanceReview.class);

		return q.list();
	}

	@Override
	public ReviewData getReviewDataById(Long id) {
		Objectify ofy = ObjectifyService.begin();
		Query<ReviewData> reviews = ofy.query(ReviewData.class);

		for (ReviewData review : reviews) {
			if (review.getId().equals(id)) {
				return review;
			}
		}
		return null;
	}
}
