package com.songkick.snippets.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.songkick.snippets.client.AdminService;
import com.songkick.snippets.client.AdminServiceAsync;
import com.songkick.snippets.shared.dao.UserDAO;

public class AddSnippetDialog extends DialogBox {
	private final AdminServiceAsync adminService = GWT.create(AdminService.class);

	private UserDAO user = null;
	private TextArea snippetTextBox = new TextArea();
	private TextBox weekTextBox = new TextBox();
	private ListBox weekChooser = new ListBox();

	public AddSnippetDialog(UserDAO user) {
		this.user = user;

		createUI();
	}

	private void createUI() {
		VerticalPanel panel = new VerticalPanel();
		Button okButton = new Button("Save");
		Button cancelButton = new Button("Cancel");

		snippetTextBox.setSize("34em", "10em");
		panel.add(new Label("Snippet:"));
		panel.add(snippetTextBox);

		HorizontalPanel hPanel = new HorizontalPanel();

		hPanel.add(new Label("Week:"));
		hPanel.add(weekChooser);
		hPanel.add(new Label("or enter:"));
		hPanel.add(weekTextBox);
		panel.add(hPanel);

		HorizontalPanel buttonPanel = new HorizontalPanel();

		buttonPanel.add(cancelButton);
		buttonPanel.add(okButton);

		panel.add(buttonPanel);

		okButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				saveSnippet();
			}
		});
		cancelButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				AddSnippetDialog.this.hide();
			}
		});

		setWidget(panel);
		setText("Add snippet for " + user.getName());

		adminService.getCurrentWeek(new AsyncCallback<Long>() {
			@Override
			public void onFailure(Throwable caught) {
			}

			@Override
			public void onSuccess(final Long result) {
				weekChooser.clear();
				weekChooser.addItem("This week (" + result + ")");
				weekChooser.addItem("Last week (" + (result - 1) + ")");
				
				weekChooser.addChangeHandler(new ChangeHandler() {
					@Override
					public void onChange(ChangeEvent event) {
						int selected = weekChooser.getSelectedIndex();
						
						if (selected==0) {
							weekTextBox.setText("" + result);
						} else if (selected==1) {
							weekTextBox.setText("" + (result-1));
						}
					}});
			}
		});
	}

	private void saveSnippet() {
		String snippet = snippetTextBox.getText().replaceAll("\n", "<br>");
		int weekNumber;

		try {
			weekNumber = Integer.parseInt(weekTextBox.getText());
		} catch (NumberFormatException e) {
			Window.alert("You must specify a valid week number");
			return;
		}
		adminService.addSnippet(user, snippet, weekNumber,
				new AsyncCallback<Void>() {
					@Override
					public void onFailure(Throwable caught) {
						Window.alert("Connection failed");
					}

					@Override
					public void onSuccess(Void result) {
						AddSnippetDialog.this.hide();
					}
				});
	}
}
