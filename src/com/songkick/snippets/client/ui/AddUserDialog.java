package com.songkick.snippets.client.ui;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class AddUserDialog extends DialogBox {

	private UserList parent = null;
	
	public AddUserDialog(UserList parent) {
		this.parent = parent;
		
		createUI();
	}

	private void createUI() {
		VerticalPanel panel = new VerticalPanel();
		HorizontalPanel buttonPanel = new HorizontalPanel();
		Button cancelButton = new Button("Cancel");
		Button addButton = new Button("Add");
		final TextBox emailAddressTextBox = new TextBox();
		HorizontalPanel innerPanel = new HorizontalPanel();

		innerPanel.add(new Label("New email address: "));
		innerPanel.add(emailAddressTextBox);

		buttonPanel.add(cancelButton);
		buttonPanel.add(addButton);

		cancelButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				hide();
			}
		});
		addButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				parent.addUser(emailAddressTextBox.getText());
				hide();
			}
		});

		panel.add(innerPanel);
		panel.add(buttonPanel);

		setText("Add new user");
		setWidget(panel);
		center();

		emailAddressTextBox.setFocus(true);
	}
}
