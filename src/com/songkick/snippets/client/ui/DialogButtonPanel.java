package com.songkick.snippets.client.ui;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;

public class DialogButtonPanel extends HorizontalPanel {
	private HorizontalPanel leftPanel = new HorizontalPanel();
	private HorizontalPanel rightPanel = new HorizontalPanel();
	private Button cancelButton = new Button("Cancel");
	private Button okButton = new Button("OK");
	private SKDialog dialogBox = null;

	public DialogButtonPanel(SKDialog dialogBox) {
		this.dialogBox = dialogBox;
		createUI();
	}

	private void createUI() {
		add(leftPanel);
		add(rightPanel);
		rightPanel.setStylePrimaryName("buttonPanel");
		setWidth("100%");

		addButton(cancelButton);
		addButton(okButton);

		cancelButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				dialogBox.hide();
			}
		});

		okButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (dialogBox.save()) {
					dialogBox.hide();
				}
			}
		});
	}

	public void addButton(Button button) {
		rightPanel.add(button);
	}

	public void addLeftButton(Button button) {
		leftPanel.add(button);
	}
}
