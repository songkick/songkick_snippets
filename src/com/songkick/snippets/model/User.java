package com.songkick.snippets.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Id;

public class User {
	@Id
	private Long id = null;
	private String emailAddress = null;
	private List<String> otherEmails = new ArrayList<String>();
	private List<String> primaryEmails = new ArrayList<String>();
	private String name = null;
	private String group = null;
	private List<Snippet> snippetList = null;
	private boolean isAdmin = false;
	private String startDate = null;
	private String endDate = null;

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
	
	public void setId(Long id) {
		this.id = id;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}
	
	public boolean matchesEmail(String email) {
		if (email.contains(emailAddress)) {
			return true;
		}
		
		for (String other: otherEmails) {
			if (email.contains(other)) {
				return true;
			}
		}
		
		return false;
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
		if (name != null) {
			return name;
		}
		return emailAddress;
	}

	public void setOtherEmails(List<String> otherEmails) {
		this.otherEmails = otherEmails;
	}

	public List<String> getOtherEmails() {
		return otherEmails;
	}

	public String toString() {
		return "id=" + id + " email: " + emailAddress + " PrimaryEmails: " + primaryEmails + " otherEmails: " + otherEmails;
	}

	public boolean isAdmin() {
		return isAdmin;
	}

	public void setAdmin(boolean isAdmin) {
		this.isAdmin = isAdmin;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public void setPrimaryEmails(List<String> primaryEmails) {
		this.primaryEmails = primaryEmails;
	}

	public List<String> getPrimaryEmails() {
		return primaryEmails;
	}
}
