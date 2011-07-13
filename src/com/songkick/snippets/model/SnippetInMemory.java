package com.songkick.snippets.model;

import com.songkick.snippets.server.data.DataStorage;

public class SnippetInMemory {
	private Snippet snippet = null;
	private User user = null;
	
	public SnippetInMemory(Snippet snippet) {
		this.snippet = snippet;
	}
	
	public void prep(DataStorage dataStore) {
		user = dataStore.getUserForSnippet(snippet);
		
		if (user==null) {
			System.out.println("SnippetInMemory.prep: no user found for " + snippet);
		}
	}

	public User getUserInMemory() {
		return user;
	}
	
	public Snippet getSnippet() {
		return snippet;
	}
}
