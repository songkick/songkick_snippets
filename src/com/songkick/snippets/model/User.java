package com.songkick.snippets.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Id;

public class User {
	@Id
	private Long id = null;
	private String emailAddress = null;
	private String name = null;
	private String group = null;
	private List<Snippet> snippetList = null;

	public User() {
		// Needed for datastore
	}

	public List<Snippet> getSnippetList() {
		return snippetList;
	}

	public void setSnippetList(List<Snippet> snippetList) {
		this.snippetList = snippetList;
	}

	public void addSnippet(Snippet snippet) {
		if (snippetList == null) {
			snippetList = new ArrayList<Snippet>();
		}
		snippetList.add(snippet);
	}

	public Long getId() {
		return id;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}
	
	public String getBestName() {
		if (name!=null) {
			return name;
		}
		return emailAddress;
	}

	public String toString() {
		return "id=" + id + " email: " + emailAddress;
	}

}
