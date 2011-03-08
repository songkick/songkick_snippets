package com.songkick.snippets.client.ui;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;

public class UI {
	public static Label makeLabel(String name, String css) {
		Label label = new Label(name);
		label.setStylePrimaryName(css);
		return label;
	}

	public static Button makeButton(String name) {
		Button button = new Button(name);
		button.setStylePrimaryName("UserListButton");
		return button;
	}
}
