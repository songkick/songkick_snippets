package com.songkick.snippets.client.ui;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.songkick.common.client.ui.util.UI;
import com.songkick.common.model.UserDAO;
import com.songkick.snippets.client.AdminService;
import com.songkick.snippets.client.AdminServiceAsync;

/**
 * Handle user list and associated controls
 * 
 * @author dancrow
 */
public class UserList extends VerticalPanel {
	private final AdminServiceAsync adminService = GWT.create(AdminService.class);

	private ListBox userListBox = new ListBox();
	private TextBox infoTextBox = new TextBox();
	private Button addSnippetButton = UI.makeButton("Add");
	private Button editSnippetsButton = UI.makeButton("Edit");
	private CheckBox allUserCheckBox = new CheckBox("Show all users");
	private UserPanel userPanel = null;
	private List<UserDAO> users = null;

	public UserList(UserPanel userPanel) {
		this.userPanel = userPanel;

		createUI();

		getUsers();
	}

	private void createUI() {
		this.setStylePrimaryName("UserList");

		Button sendReminderButton = UI.makeButton("Remind");
		Button addUserButton = UI.makeButton("Add");
		Button deleteUserButton = UI.makeButton("Delete");
		Button viewSnippetButton = UI.makeButton("View");
		Panel topButtonPanel = new UserListButtonPanel("Users:");
		Panel bottomButtonPanel = new UserListButtonPanel("Snippets:");
		Panel controlPanel = new HorizontalPanel();

		controlPanel.add(allUserCheckBox);
		allUserCheckBox.setValue(true);
		
		allUserCheckBox.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				getUsers();
			}});

		add(UI.makeLabel("Current users:", "headerLabel"));
		add(controlPanel);
		add(userListBox);
		topButtonPanel.add(addUserButton);
		topButtonPanel.add(deleteUserButton);
		topButtonPanel.add(sendReminderButton);
		bottomButtonPanel.add(viewSnippetButton);
		bottomButtonPanel.add(editSnippetsButton);
		bottomButtonPanel.add(addSnippetButton);
		add(topButtonPanel);
		add(bottomButtonPanel);
		add(infoTextBox);

		addUserButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				userPanel.setUser(UserList.this, null);
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

		userListBox.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				editUser();
			}
		});

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
				ViewSnippetDialog viewer = new ViewSnippetDialog();

				viewer.setSnippets(result);
				viewer.center();
			}
		});
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
		if (index == -1) {
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

		if (Window.confirm("Are you sure you want to delete " + user.getName()
				+ "?")) {
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
		boolean all = allUserCheckBox.getValue();

		if (all) {
			adminService.getFullUserList(new AsyncCallback<List<UserDAO>>() {
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
		} else {
			adminService.getCurrentUserList(new AsyncCallback<List<UserDAO>>() {
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
		if (user == null) {
			return;
		}

		if (userPanel.hasChanged()) {
			EditUserCheckDialog dialog = new EditUserCheckDialog(userPanel, this,
					user);

			dialog.center();
		} else {
			userPanel.setUser(this, user);
		}
	}

	private void populateUserList(List<UserDAO> users) {
		System.out.println("populateUserList from " + users);
		userListBox.clear();
		for (UserDAO user : users) {
			userListBox.addItem(user.getName() + " ("
					+ user.getEmailAddresses().get(0) + ")");
		}
	}
}
