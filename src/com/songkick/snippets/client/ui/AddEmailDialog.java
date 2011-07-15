package com.songkick.snippets.client.ui;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.songkick.common.model.EmailAddress;

public class AddEmailDialog extends DialogBox {
	private HorizontalPanel panel = new HorizontalPanel();
	private TextBox emailTextBox = new TextBox();
	private Button addButton = new Button("Add");
	private EmailList emailListBox = null;
	private CheckBox primaryCheckBox = new CheckBox("primary");
	private Widget near = null;

	public AddEmailDialog(EmailList emailListBox, Widget near) {
		this.emailListBox = emailListBox;
		this.near = near;
		createUI();
	}

	private void createUI() {
		panel.add(new Label("Enter new email:"));
		panel.add(emailTextBox);
		panel.add(primaryCheckBox);
		panel.add(addButton);

		primaryCheckBox.setTitle("Check to send reminder and digest emails to this address");
		primaryCheckBox.setValue(true);
		
		setWidget(panel);
		setText("Add new email");

		addButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				addEmail();
			}
		});
		emailTextBox.addKeyPressHandler(new KeyPressHandler() {
			public void onKeyPress(KeyPressEvent event) {
				if (KeyCodes.KEY_ENTER == event.getNativeEvent().getKeyCode()) {
					addEmail();
				}
			}
		});

		showRelativeTo(near);

		emailTextBox.setFocus(true);
	}

	private void addEmail() {
		String email = emailTextBox.getText().trim();
		EmailAddress address = new EmailAddress();
		address.setEmail(email);
		address.setPrimary(primaryCheckBox.getValue());
		emailListBox.addEmailToList(address);
		hide();
	}
}
