package com.songkick.snippets.shared.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class UserDAO implements Serializable {
	private Long id = null;
	private String name = null;
	private List<String> emailAddresses = new ArrayList<String>();
	private String group = null;
	private boolean isAdmin = false;
	private String startDate = null;
	private String endDate = null;
	
	public UserDAO() {
		// Tells us that this DAO was created in the UI
		id = -1L;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getEmailAddresses() {
		return emailAddresses;
	}

	public void setEmailAddresses(List<String> emailAddresses) {
		this.emailAddresses = emailAddresses;
	}
	
	public void addEmail(String email) {
		emailAddresses.add(email);
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public String toString() {
		return id + ": " + name + " emails: " + emailAddresses;
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
}
