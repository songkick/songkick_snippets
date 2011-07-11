package com.songkick.snippets.client.ui;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.songkick.common.client.ui.util.UI;

public class UserListButtonPanel extends HorizontalPanel {
	private HorizontalPanel buttonPanel = null;
	
	public UserListButtonPanel(String name) {
		createUI(name);
	}
	
	private void createUI(String name) {
		buttonPanel = new HorizontalPanel();
		setWidth("100%");
		setStylePrimaryName("ButtonPanel");
		add(UI.makeLabel(name, "dialogLabel"));
		add(buttonPanel);
	}
	
	public void addButton(Button button) {
		buttonPanel.add(button);
	}
}
