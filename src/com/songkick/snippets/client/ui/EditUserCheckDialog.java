package com.songkick.snippets.client.ui;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.songkick.common.client.ui.util.UI;
import com.songkick.common.model.UserDAO;

public class EditUserCheckDialog extends DialogBox {
	private UserPanel userPanel = null;
	private UserList userList = null;
	private UserDAO dao = null;

	public EditUserCheckDialog(UserPanel userPanel, UserList userList, UserDAO dao) {
		this.userPanel = userPanel;
		this.userList = userList;
		this.dao = dao;

		createUI();
	}

	private void createUI() {
		VerticalPanel panel = new VerticalPanel();
		Button yesButton = UI.makeButton("Yes");
		Button noButton = UI.makeButton("No");

		panel.add(UI
				.makeLabel("The current user record has changed", "dialogLabel"));
		panel.add(UI
				.makeLabel("Do you want to discard the changes?", "dialogLabel"));

		panel.setWidth("34em");
		
		HorizontalPanel hPanel = new HorizontalPanel();
		HorizontalPanel buttonPanel = new HorizontalPanel();

		buttonPanel.add(yesButton);
		buttonPanel.add(noButton);
		hPanel.setWidth("100%");
		hPanel.add(buttonPanel);
		buttonPanel.setStylePrimaryName("buttonPanel");

		yesButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				userPanel.setUser(userList, dao);
				hide();
			}
		});

		noButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				hide();
			}
		});

		panel.add(hPanel);

		setWidget(panel);
		setText("Are you sure?");
	}
}
