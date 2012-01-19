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
	private List<Long> managers = new ArrayList<Long>();
	private String jobTitle = null;

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

	public List<String> getOutgoingEmails() {
		List<String> result = new ArrayList<String>();

		if (emailAddress != null) {
			result.add(emailAddress);
		}

		if (primaryEmails != null) {
			result.addAll(primaryEmails);
		}

		return result;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public boolean matchesEmail(String email) {
		if (emailAddress != null && email.contains(emailAddress)) {
			return true;
		}

		if (otherEmails != null) {
			for (String other : otherEmails) {
				if (email.contains(other)) {
					return true;
				}
			}
		}

		if (primaryEmails != null) {
			for (String other : primaryEmails) {
				if (email.contains(other)) {
					return true;
				}
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
		if (emailAddress != null) {
			return emailAddress;
		}

		if (primaryEmails.size() > 0) {
			return primaryEmails.get(0);
		}

		if (otherEmails.size() > 0) {
			return otherEmails.get(0);
		}

		return "unknown";
	}

	public void setOtherEmails(List<String> otherEmails) {
		this.otherEmails = otherEmails;
	}

	public List<String> getOtherEmails() {
		return otherEmails;
	}

	public String toString() {
		return "id=" + id + " startDate: " + startDate + " email: " + emailAddress
				+ " PrimaryEmails: " + primaryEmails + " otherEmails: " + otherEmails;
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

	public void addPrimaryEmail(String newPrimary) {
		primaryEmails.add(newPrimary);
	}

	public List<String> getPrimaryEmails() {
		return primaryEmails;
	}

	public List<Long> getManagers() {
		return managers;
	}

	public void setManagers(List<Long> managers) {
		this.managers = managers;
	}

	public boolean doesReportTo(User user) {
		for (Long id: managers) {
			if (user.getId().equals(id)) {
				return true;
			}
		}
		return false;
	}

	public void addManager(User user) {
		managers.add(user.getId());
	}

	public String getJobTitle() {
		return jobTitle;
	}

	public void setJobTitle(String jobTitle) {
		this.jobTitle = jobTitle;
	}
}
