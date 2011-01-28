package com.songkick.snippets.client.ui;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.songkick.snippets.client.AdminService;
import com.songkick.snippets.client.AdminServiceAsync;

/**
 * Handle user list and associated controls
 * 
 * @author dancrow
 */
public class UserList extends VerticalPanel {
	private final AdminServiceAsync adminService = GWT
			.create(AdminService.class);

	private ListBox userListBox = new ListBox();
	private TextBox infoTextBox = new TextBox();

	public UserList() {
		createUI();

		getUsers();
	}

	private void createUI() {
		this.setStylePrimaryName("UserList");
		
		Button sendReminderButton = new Button("Remind");
		Button addUserButton = new Button("Add");
		Button deleteUserButton = new Button("Delete");
		Button viewSnippetButton = new Button("View snippets");
		HorizontalPanel buttonPanel = new HorizontalPanel();

		add(new Label("Current users:"));
		add(userListBox);
		buttonPanel.add(sendReminderButton);
		buttonPanel.add(addUserButton);
		buttonPanel.add(deleteUserButton);
		buttonPanel.add(viewSnippetButton);
		add(buttonPanel);
		add(infoTextBox);

		addUserButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				new AddUserDialog(UserList.this);
			}
		});
		deleteUserButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				deleteUser();
			}
		});
		sendReminderButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				remindUser();
			}
		});
		viewSnippetButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				viewSnippets();
			}
		});

		userListBox.setVisibleItemCount(20);
		userListBox.setSize("100%", "100%");

		infoTextBox.setWidth("34em");
	}

	private void inform(String message) {
		infoTextBox.setText(message);
		Timer t = new Timer() {
			public void run() {
				infoTextBox.setText("");
			}
		};

		t.schedule(5000);
	}
	
	private void viewSnippets() {
		String user = getSelectedUser();
		if (user == null) {
			return;
		}
		
		adminService.getSnippets(user, new AsyncCallback<List<String>>() {
			@Override
			public void onFailure(Throwable caught) {
				inform("Connection failed");
			}

			@Override
			public void onSuccess(List<String> result) {
				SnippetViewer viewer = new SnippetViewer();
				
				viewer.setSnippets(result);
				viewer.center();
			}});
	}

	private void remindUser() {
		String user = getSelectedUser();
		if (user == null) {
			return;
		}

		adminService.remindUser(user, new AsyncCallback<Void>() {
			@Override
			public void onFailure(Throwable caught) {
				inform("Connection failed");
			}

			@Override
			public void onSuccess(Void result) {
				inform("user reminded");
			}
		});
	}

	private String getSelectedUser() {
		String user = userListBox.getItemText(userListBox.getSelectedIndex());
		if (user == null || user.length() < 1) {
			Window.alert("Select a user to delete");
			return null;
		}
		return user;
	}

	private void deleteUser() {
		String user = getSelectedUser();
		if (user == null) {
			return;
		}

		if (Window.confirm("Are you sure you want to delete " + user + "?")) {
			adminService.deleteUser(user, new AsyncCallback<Void>() {
				@Override
				public void onFailure(Throwable caught) {
					inform("conection failure");
				}

				@Override
				public void onSuccess(Void result) {
					inform("User deleted");
					getUsers();
				}
			});
		}
	}

	private void getUsers() {
		userListBox.clear();
		userListBox.addItem("Loading...");
		adminService.getUserList(new AsyncCallback<List<String>>() {
			@Override
			public void onFailure(Throwable caught) {
				inform("connection failure");
			}

			@Override
			public void onSuccess(List<String> result) {
				populateUserList(result);
			}
		});
	}

	public void addUser(String emailAddress) {
		adminService.addUser(emailAddress, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				inform("Connection failure");
			}

			@Override
			public void onSuccess(Void result) {
				inform("User added");
				getUsers();
			}
		});
	}

	private void populateUserList(List<String> users) {
		System.out.println("populateUserList from " + users);
		userListBox.clear();
		for (String user : users) {
			userListBox.addItem(user);
		}
	}
}
