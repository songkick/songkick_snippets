package com.songkick.snippets.client.ui;

import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.songkick.snippets.shared.dao.UserDAO;

/**
 * Dialog box for adding a new user or editing an existing user entry - see
 * UserList
 * 
 * @author dancrow
 */
public class AddUserDialog extends DialogBox {
	private UserList parent = null;
	private TextBox nameTextBox = new TextBox();
	private CheckBox adminCheckBox = new CheckBox("Administrator");
	private UserDAO existing = null;
	private EmailList emailList = null;

	public AddUserDialog(UserList parent, UserDAO existing) {
		this.parent = parent;
		this.existing = existing;

		if (existing != null) {
			nameTextBox.setText(existing.getName());
			adminCheckBox.setValue(existing.isAdmin());

			emailList = new EmailList(existing);
		}
		else {
			emailList = new EmailList();
		}

		createUI();
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

		panel.add(UI.makeLabel("Email addresses:", "dialogLabel"));
		panel.add(emailList);
		panel.add(buttonPanel);

		buttonPanel.add(addButton);
		buttonPanel.add(deleteButton);

		addButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				new AddEmailDialog(emailList, addButton);
			}
		});
		deleteButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				emailList.removeSelected();
			}
		});

		emailList.setHeight("3em");
		emailList.setWidth("20em");
		return panel;
	}

	private void createUI() {
		VerticalPanel panel = new VerticalPanel();
		HorizontalPanel buttonPanel = new HorizontalPanel();
		Button cancelButton = UI.makeButton("Cancel");
		Button addButton = UI.makeButton("Add");

		if (existing != null) {
			addButton.setText("Save");
		}

		panel.add(createLabel("Name:", nameTextBox));
		panel.add(adminCheckBox);
		adminCheckBox.setStylePrimaryName("indentedCheckbox");
		panel.add(createEditList());

		buttonPanel.add(cancelButton);
		buttonPanel.add(addButton);

		buttonPanel.setStylePrimaryName("buttonPanel");

		cancelButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				hide();
			}
		});
		addButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				addOrEditUser();
				hide();
			}
		});

		panel.add(buttonPanel);
		if (existing == null) {
			setText("Add new user");
		} else {
			setText("Edit existing user");
		}
		setWidget(panel);
		center();

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
		List<String> emails = emailList.getEmails();
		existing.setEmailAddresses(emails);

		if (newUser) {
			parent.addUser(existing);
		} else {
			parent.updateUser(existing);
		}
	}
}
