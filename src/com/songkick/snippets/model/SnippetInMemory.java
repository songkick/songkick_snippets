package com.songkick.snippets.model;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;

public class SnippetInMemory {
	private Snippet snippet = null;
	private User user = null;
	
	public SnippetInMemory(Snippet snippet) {
		this.snippet = snippet;
	}
	
	public void prep() {
		Objectify ofy = ObjectifyService.begin();
		user = ofy.get(snippet.getUser());
	}

	public User getUserInMemory() {
		return user;
	}
	
	public Snippet getSnippet() {
		return snippet;
	}
}
