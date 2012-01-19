package com.songkick.snippets.client.ui;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.songkick.common.client.ui.util.DateBox;
import com.songkick.common.client.ui.util.UI;
import com.songkick.common.model.EmailAddress;
import com.songkick.common.model.UserDAO;
import com.songkick.snippets.client.Application;

/**
 * Panel that displays a new or existing User record
 * 
 * @author dancrow
 */
public class UserPanel extends VerticalPanel {
	private UserList parent = null;
	private TextBox nameTextBox = new TextBox();
	private TextBox jobTitleTextBox = new TextBox();
	private CheckBox adminCheckBox = new CheckBox("Administrator");
	private DateBox startDateBox = null;
	private DateBox endDateBox = null;
	private UserDAO existing = null;
	private SelectUserBox managerTextBox = new SelectUserBox("Manger");
	private EmailList emailList = null;
	private boolean hasChanged = false;

	public UserPanel() {
		createUI();
	}

	public void setUser(UserList parent, UserDAO existing) {
		this.parent = parent;
		this.existing = existing;

		if (existing != null) {
			nameTextBox.setText(existing.getName());
			jobTitleTextBox.setText(existing.getJobTitle());
			adminCheckBox.setValue(existing.isAdmin());
			startDateBox.setDateString(existing.getStartDate());
			endDateBox.setDateString(existing.getEndDate());
			managerTextBox.setUsers(getUserNames(existing.getManagers()));

			emailList.setUser(existing);
		} else {
			nameTextBox.setText("");
			jobTitleTextBox.setText("");
			adminCheckBox.setValue(false);
			startDateBox.setDateString("");
			endDateBox.setDateString("");
			managerTextBox.clear();
			
			emailList.clear();
		}
		hasChanged = false;
	}
	
	private List<String> getUserNames(List<Long> ids) {
		List<String> names = new ArrayList<String>();
		
		for (Long id: ids) {
			names.add(Application.getUserById(id).getName());
		}
		
		return names;
	}

	private Panel createLabel(String label, TextBox textBox) {
		Panel panel = new HorizontalPanel();
		panel.add(UI.makeLabel(label, "dialogLabel"));
		panel.add(textBox);
		return panel;
	}

	private Panel createEditList() {
		Panel panel = new HorizontalPanel();
		
		emailList = new EmailList(this);

		panel.add(UI.makeLabel("Email addresses:", "dialogLabel"));
		panel.add(emailList);

		return panel;
	}
	
	public void touch() {
		hasChanged = true;
	}

	public boolean hasChanged() {
		return hasChanged;
	}

	private void createUI() {
		HorizontalPanel buttonPanel = new HorizontalPanel();
		Button saveButton = UI.makeButton("Save");

		add(UI.makeLabel("User Record:", "headerLabel"));
		add(createLabel("Name:", nameTextBox));
		add(createLabel("Title:", jobTitleTextBox));
		add(adminCheckBox);
		adminCheckBox.setStylePrimaryName("indentedCheckbox");
		add(createEditList());

		startDateBox = new DateBox("Started");
		endDateBox = new DateBox("Ended");
		add(startDateBox);
		add(endDateBox);
		
		add(managerTextBox);

		setWidth("400px");
		setStylePrimaryName("UserPanel");

		nameTextBox.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				hasChanged = true;
			}
		});

		buttonPanel.add(saveButton);
		buttonPanel.setStylePrimaryName("buttonPanel");
		saveButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				addOrEditUser();
				hasChanged = false;
			}
		});

		add(buttonPanel);
		nameTextBox.setFocus(true);
	}

	private List<Long> getIdList(List<String> users) {
		List<Long> ids = new ArrayList<Long>();
		
		for (UserDAO dao: Application.getUsers()) {
			if (users.contains(dao.getName())) {
				ids.add(dao.getId());
			}
		}
		
		return ids;
	}
	
	private void addOrEditUser() {
		boolean newUser = false;
		if (existing == null) {
			existing = new UserDAO();
			newUser = true;
		}

		existing.setName(nameTextBox.getText());
		existing.setJobTitle(jobTitleTextBox.getText());
		existing.setAdmin(adminCheckBox.getValue());
		existing.setStartDate(startDateBox.getDateString());
		existing.setEndDate(endDateBox.getDateString());
		existing.setManagers(getIdList(managerTextBox.getUsers()));
		List<EmailAddress> emails = new ArrayList<EmailAddress>();
		for (EmailAddress email: emailList.getEmails()) {
			emails.add(email);
		}
		existing.setEmailAddresses(emails);

		if (newUser) {
			parent.addUser(existing);
		} else {
			parent.updateUser(existing);
		}
	}
}
