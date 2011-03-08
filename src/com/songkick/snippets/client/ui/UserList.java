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
import com.songkick.snippets.shared.dao.UserDAO;

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
	private Button addSnippetButton = UI.makeButton("Add");
	private Button editSnippetsButton = UI.makeButton("Edit");
	private List<UserDAO> users = null;

	public UserList() {
		createUI();

		getUsers();
	}

	private void createUI() {
		this.setStylePrimaryName("UserList");
		
		Button sendReminderButton = UI.makeButton("Remind");
		Button addUserButton = UI.makeButton("Add");
		Button editUserButton = UI.makeButton("Edit");
		Button deleteUserButton = UI.makeButton("Delete");
		Button viewSnippetButton = UI.makeButton("View");
		HorizontalPanel topButtonPanel = new HorizontalPanel();
		HorizontalPanel bottomButtonPanel = new HorizontalPanel();

		add(UI.makeLabel("Current users:", "headerLabel"));
		add(userListBox);
		topButtonPanel.add(new Label("Users:"));
		topButtonPanel.add(addUserButton);
		topButtonPanel.add(editUserButton);
		topButtonPanel.add(deleteUserButton);
		topButtonPanel.add(sendReminderButton);
		bottomButtonPanel.add(new Label("Snippets:"));
		bottomButtonPanel.add(viewSnippetButton);
		bottomButtonPanel.add(editSnippetsButton);
		bottomButtonPanel.add(addSnippetButton);
		add(topButtonPanel);
		add(bottomButtonPanel);
		add(infoTextBox);

		addUserButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				new AddUserDialog(UserList.this, null);
			}
		});
		editUserButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				editUser();
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
		editSnippetsButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				editSnippets();
			}
		});
		addSnippetButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				addSnippet();
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
	
	private void addSnippet() {
		UserDAO user = getSelectedUser();
		if (user == null) {
			return;
		}
		AddSnippetDialog dialog = new AddSnippetDialog(user);
		dialog.showRelativeTo(addSnippetButton);
	}
	
	private void viewSnippets() {
		UserDAO user = getSelectedUser();
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
	
	private void editSnippets() {
		UserDAO user = getSelectedUser();
		if (user == null) {
			return;
		}
		
		EditSnippetsDialog dialog = new EditSnippetsDialog(user);
		
		dialog.showRelativeTo(editSnippetsButton);
	}

	private void remindUser() {
		final UserDAO user = getSelectedUser();
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
				inform(user.getName() + " reminded");
			}
		});
	}

	private UserDAO getSelectedUser() {
		int index = userListBox.getSelectedIndex();
		if (index==-1) {
			Window.alert("Select a user");
			return null;
		}
		return users.get(index);
	}

	private void deleteUser() {
		UserDAO user = getSelectedUser();
		if (user == null) {
			return;
		}

		if (Window.confirm("Are you sure you want to delete " + user.getName() + "?")) {
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
		adminService.getUserList(new AsyncCallback<List<UserDAO>>() {
			@Override
			public void onFailure(Throwable caught) {
				inform("connection failure");
			}

			@Override
			public void onSuccess(List<UserDAO> result) {
				populateUserList(result);
				users = result;
			}
		});
	}

	// Callback from AddUserDialog
	public void addUser(UserDAO user) {
		adminService.addUser(user, new AsyncCallback<Void>() {
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

	// Callback from AddUserDialog
	public void updateUser(UserDAO user) {
		adminService.updateUser(user, new AsyncCallback<Void>() {
			@Override
			public void onFailure(Throwable caught) {
				inform("Connection failure");
			}

			@Override
			public void onSuccess(Void result) {
				inform("User updated");
				getUsers();
			}
		});
	}
	
	private void editUser() {
		UserDAO user = getSelectedUser();
		if (user==null) {
			return;
		}
	
		new AddUserDialog(this, user);
	}

	private void populateUserList(List<UserDAO> users) {
		System.out.println("populateUserList from " + users);
		userListBox.clear();
		for (UserDAO user : users) {
			userListBox.addItem(user.getName() + " (" + user.getEmailAddresses().get(0) + ")");
		}
	}
}
