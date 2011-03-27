package com.songkick.snippets.client.ui;

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
import com.songkick.snippets.client.ui.util.DateBox;
import com.songkick.snippets.client.ui.util.UI;
import com.songkick.snippets.shared.dao.UserDAO;

/**
 * Panel that displays a new or existing User record
 * 
 * @author dancrow
 */
public class UserPanel extends VerticalPanel {
	private UserList parent = null;
	private TextBox nameTextBox = new TextBox();
	private CheckBox adminCheckBox = new CheckBox("Administrator");
	private DateBox startDateBox = null;
	private DateBox endDateBox = null;
	private UserDAO existing = null;
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
			adminCheckBox.setValue(existing.isAdmin());
			startDateBox.setDateString(existing.getStartDate());
			endDateBox.setDateString(existing.getEndDate());

			emailList.setUser(existing);
		} else {
			nameTextBox.setText("");
			adminCheckBox.setValue(false);
			startDateBox.setDateString("");
			endDateBox.setDateString("");
			
			emailList.clear();
		}
		hasChanged = false;
	}

	private Panel createLabel(String label, TextBox textBox) {
		Panel panel = new HorizontalPanel();
		panel.add(UI.makeLabel(label, "dialogLabel"));
		panel.add(textBox);
		return panel;
	}

	private Panel createEditList() {
		Panel panel = new HorizontalPanel();
		Panel buttonPanel = new VerticalPanel();
		final Button addButton = UI.makeButton("Add");
		final Button deleteButton = UI.makeButton("Delete");

		emailList = new EmailList();

		panel.add(UI.makeLabel("Email addresses:", "dialogLabel"));
		panel.add(emailList);
		panel.add(buttonPanel);

		buttonPanel.add(addButton);
		buttonPanel.add(deleteButton);

		addButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				new AddEmailDialog(emailList, addButton);
				hasChanged = true;
			}
		});
		deleteButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				emailList.removeSelected();
				hasChanged = true;
			}
		});

		emailList.setHeight("3em");
		emailList.setWidth("20em");
		return panel;
	}

	public boolean hasChanged() {
		return hasChanged;
	}

	private void createUI() {
		HorizontalPanel buttonPanel = new HorizontalPanel();
		Button saveButton = UI.makeButton("Save");

		add(UI.makeLabel("User Record:", "headerLabel"));
		add(createLabel("Name:", nameTextBox));
		add(adminCheckBox);
		adminCheckBox.setStylePrimaryName("indentedCheckbox");
		add(createEditList());

		startDateBox = new DateBox("Started");
		endDateBox = new DateBox("Ended");
		add(startDateBox);
		add(endDateBox);

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

	private void addOrEditUser() {
		boolean newUser = false;
		if (existing == null) {
			existing = new UserDAO();
			newUser = true;
		}

		existing.setName(nameTextBox.getText());
		existing.setAdmin(adminCheckBox.getValue());
		existing.setStartDate(startDateBox.getDateString());
		existing.setEndDate(endDateBox.getDateString());
		List<String> emails = emailList.getEmails();
		existing.setEmailAddresses(emails);

		if (newUser) {
			parent.addUser(existing);
		} else {
			parent.updateUser(existing);
		}
	}
}
