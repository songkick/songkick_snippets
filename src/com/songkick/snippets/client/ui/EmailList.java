package com.songkick.snippets.client.ui;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.songkick.snippets.shared.dao.UserDAO;

public class EmailList extends VerticalPanel {
	private UserDAO user = null;
	private VerticalPanel list = new VerticalPanel();

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
		list.setWidth("100%");

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
		
		for (int i=0; i<list.getWidgetCount(); i++) {
			RadioButton button = (RadioButton) list.getWidget(i);
			
			emails.add(button.getText());
		}
		
		return emails;
	}

	public void addEmail(String email) {
		RadioButton button = new RadioButton("emailList");

		button.setText(email);
		list.add(button);
	}

}
