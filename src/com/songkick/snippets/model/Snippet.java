package com.songkick.snippets.model;



import javax.persistence.Id;

import com.googlecode.objectify.Key;

/**
 * Represents a single snippet
 * 
 * @author dancrow
 */
public class Snippet {
	@Id
	private Long id;
	private String snippetText = null;
	private Key<User> user = null;
	private String date = null;
	private Long weekNumber = 0L;

	public Snippet() {
		// Needed for datastore
	}

	public Snippet(User user, String snippetText) {
		assert (snippetText != null);
		setUser(new Key<User>(User.class, user.getId()));
		setSnippetText(snippetText);
	}

	public Key<User> getUser() {
		return user;
	}

	public Long getWeekNumber() {
		return weekNumber;
	}

	public void setWeekNumber(Long weekNumber) {
		this.weekNumber = weekNumber;
	}

	public void setUser(Key<User> user) {
		this.user = user;
	}

	public String getSnippetText() {
		return snippetText;
	}

	public void setSnippetText(String snippetText) {
		this.snippetText = snippetText;
	}

	public Long getId() {
		return id;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String toString() {
		return "Week " + weekNumber + ": " + snippetText + " User=" + user;
	}
}
