package com.songkick.snippets.client.ui;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.songkick.common.model.UserDAO;

public class EmailList extends VerticalPanel {
	private UserDAO user = null;
	private ListBox list = new ListBox();

	public EmailList() {
		createUI();
	}

	public void setUser(UserDAO user) {
		this.user = user;
		
		list.clear();
		for (String email : user.getEmailAddresses()) {
			addEmail(email);
		}
	}

	private void createUI() {
		ScrollPanel scrollPanel = new ScrollPanel();

		scrollPanel.setSize("100%", "100%");
		add(scrollPanel);

		scrollPanel.setWidget(list);
		list.setStylePrimaryName("emailList");
		list.setWidth("100%");list.setVisibleItemCount(5);

		if (user != null) {
			for (String email : user.getEmailAddresses()) {
				addEmail(email);
			}
		}
	}

	public void removeSelected() {
		// TODO
	}

	public List<String> getEmails() {
		List<String> emails = new ArrayList<String>();
		
		for (int i=0; i<list.getItemCount(); i++) {
			emails.add(list.getItemText(i));
		}
		
		return emails;
	}

	public void addEmail(String email) {
		list.addItem(email);
	}
}
