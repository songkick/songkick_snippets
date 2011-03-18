package com.songkick.snippets.server.data;

import java.util.ArrayList;
import java.util.List;

import com.songkick.snippets.model.Snippet;
import com.songkick.snippets.model.User;

public class DataStorageMock implements DataStorage {
	private Long idCounter = 1L;
	private List<Object> store = new ArrayList<Object>();

	@Override
	public void save(Object object) {
		store.add(object);

		if (object instanceof User) {
			User user = (User) object;
			user.setId(idCounter++);
		}
	}
	
	@Override
	public void delete(Object object) {
		store.remove(object);
	}

	@Override
	public List<Snippet> getSnippets() {
		List<Snippet> snippets = new ArrayList<Snippet>();

		for (Object obj : store) {
			if (obj instanceof Snippet) {
				snippets.add((Snippet) obj);
			}
		}

		return snippets;
	}

	@Override
	public List<User> getUsers() {
		List<User> users = new ArrayList<User>();

		for (Object obj : store) {
			if (obj instanceof User) {
				users.add((User) obj);
			}
		}

		return users;
	}

	public boolean hasUser(String email) {
		List<User> users = getUsers();

		for (User user : users) {
			if (user.matchesEmail(email)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public List<User> getUsersWithSnippetFor(Long week) {
		// Can't be implemented in the mock (yet)
		return null;
	}

	@Override
	public User getUserById(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Snippet> getSnippetsForUser(User user) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Snippet> getSnippetsByWeek(Long week) {
		// TODO Auto-generated method stub
		return null;
	}
}
