package com.songkick.snippets.server.data;

import java.util.ArrayList;
import java.util.List;

import com.googlecode.objectify.Key;
import com.songkick.snippets.logic.DateHandler;
import com.songkick.snippets.model.Snippet;
import com.songkick.snippets.model.User;

public class DataStorageMock extends DataStorageBase implements DataStorage {
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
	public List<User> getCurrentUsers() {
		return getUsersForWeek(DateHandler.getCurrentWeek());
	}

	@Override
	public List<User> getUsersForWeek(Long week) {
		List<User> users = getAllUsers();
		List<User> current = new ArrayList<User>();
		
		for (User user: users) {
			if (isCurrentUser(user, week)) {
				current.add(user);
			}
		}
		
		return current;
	}

	public boolean hasUser(String email) {
		List<User> users = getCurrentUsers();

		for (User user : users) {
			if (user.matchesEmail(email)) {
				return true;
			}
		}
		return false;
	}

	private User getUser(Key<User> key) {
		List<User> users = getCurrentUsers();
		
		for (User user: users) {
			if (user.getId().equals(key.getId())) {
				return user;
			}
		}
		
		return null;
	}
	
	@Override
	public List<User> getUsersWithSnippetFor(Long week) {
		List<Snippet> snippets = getSnippets();
		List<User> users = new ArrayList<User>();
		
		for (Snippet snippet: snippets) {
			if (snippet.getWeekNumber().equals(week)) {
				users.add(getUser(snippet.getUser()));
			}
		}
		
		System.out.println("DataStorageMock.getUsersWithSnippetFor(" + week + ") returns " + users);
		
		return users;
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
		List<Snippet> snippets = getSnippets();
		List<Snippet> results = new ArrayList<Snippet>();
		
		for (Snippet snippet: snippets) {
			System.out.println("DataStorageMock.getSnippetsByWeek examining " + snippet + " (week=" + snippet.getWeekNumber() + ")");
			if (snippet.getWeekNumber().equals(week)) {
				results.add(snippet);
			}
		}
		
		System.out.println("DataStorageMock.getSnippetsByWeek(" + week + ") returns " + results);
		
		return results;
	}

	@Override
	public List<User> getAllUsers() {
		List<User> users = new ArrayList<User>();

		for (Object obj : store) {
			if (obj instanceof User) {
				users.add((User) obj);
			}
		}
		return users;
	}

	@Override
	public User getUserForSnippet(Snippet snippet) {
		return getUser(snippet.getUser());
	}
}
