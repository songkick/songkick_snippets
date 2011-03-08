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
import com.google.gwt.user.client.ui.VerticalPanel;
import com.songkick.snippets.client.AdminService;
import com.songkick.snippets.client.AdminServiceAsync;
import com.songkick.snippets.shared.dao.UserDAO;

public class EditSnippetsDialog extends DialogBox {
	private final AdminServiceAsync adminService = GWT.create(AdminService.class);
	
	private ListBox weekListBox = new ListBox();
	private TextArea snippetTextArea= new TextArea();
	private UserDAO user= null;
	private Long displayedWeek = -1L;
	
	public EditSnippetsDialog(UserDAO user) {
		this.user = user;
		createUI();
	}
	
	private void createUI() {
		VerticalPanel outerPanel = new VerticalPanel();
		
		HorizontalPanel panel = new HorizontalPanel();
		Button okButton = new Button("Save");
		Button cancelButton = new Button("Cancel");

		VerticalPanel leftPanel = new VerticalPanel();
		leftPanel.add(new Label("Week:"));
		leftPanel.add(weekListBox);
		
		panel.add(leftPanel);
		
		snippetTextArea.setSize("34em", "10em");
		panel.add(new Label("Snippet:"));
		panel.add(snippetTextArea);

		HorizontalPanel buttonPanel = new HorizontalPanel();

		buttonPanel.add(cancelButton);
		buttonPanel.add(okButton);

		outerPanel.add(panel);
		outerPanel.add(buttonPanel);

		okButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				saveSnippet();
			}
		});
		cancelButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				EditSnippetsDialog.this.hide();
			}
		});

		setWidget(outerPanel);
		setText("Edit snippet for " + user.getName());
		
		// Get the current week number
		adminService.getCurrentWeek(new AsyncCallback<Long>() {
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Cannot get week number");
			}

			@Override
			public void onSuccess(final Long result) {
				weekListBox.clear();
				weekListBox.addItem("This week (" + result + ")");
				weekListBox.addItem("Last week (" + (result - 1) + ")");
				
				for (int i=(int) (result-2); i>1; i--) {
					weekListBox.addItem("Week " + i);
				}
				
				
				weekListBox.addChangeHandler(new ChangeHandler() {
					@Override
					public void onChange(ChangeEvent event) {
						int selected = weekListBox.getSelectedIndex();
						
						if (selected>-1) {
							showSnippet(result-selected);
						}
					}});
			}
		});
	}
	
	private void showSnippet(Long weekNumber) {
		displayedWeek = weekNumber;
		adminService.getSnippet(user, weekNumber, new AsyncCallback<String>() {
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Cannot get snippet");
			}

			@Override
			public void onSuccess(String result) {
				snippetTextArea.setText(result);
			}});
	}
	
	private void saveSnippet() {
		if (displayedWeek!=-1) {
		String snippet = snippetTextArea.getText();
		adminService.replaceSnippet(user, snippet, displayedWeek, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Cannot contact server: " + caught);
			}

			@Override
			public void onSuccess(Void result) {
				EditSnippetsDialog.this.hide();
			}});
		}
	}
}
