package com.songkick.snippets.server.data;

import java.util.List;

import com.songkick.snippets.model.Snippet;
import com.songkick.snippets.model.User;

public interface DataStorage {
	public void save(Object object);
	
	public void delete(Object object);
	
	public List<User> getUsers();
	
	public List<Snippet> getSnippets();
	
	public List<User> getUsersWithSnippetFor(Long week);
	
	public User getUserById(Long id);

	public List<Snippet> getSnippetsForUser(User user);
	
	public List<Snippet> getSnippetsByWeek(Long week);
}
