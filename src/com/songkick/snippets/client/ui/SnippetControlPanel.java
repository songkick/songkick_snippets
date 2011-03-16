package com.songkick.snippets.client.ui;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.songkick.snippets.client.AdminService;
import com.songkick.snippets.client.AdminServiceAsync;
import com.songkick.snippets.client.ui.util.UI;
import com.songkick.snippets.shared.dao.UserDAO;

public class SnippetControlPanel extends HorizontalPanel {
	private final AdminServiceAsync adminService = GWT.create(AdminService.class);

	private Anchor thisWeekLink = new Anchor("Current week's snippets");
	private UserPanel userPanel = null;
	private UserList userList = null;
	private Button sendReminderButton = UI.makeButton("Send reminders");
	private Button viewLogButton = UI.makeButton("View log");
	private Button showUsersToRemindButton = UI
			.makeButton("Show users to remind");

	public SnippetControlPanel() {
		createUI();
		
		adminService.getCurrentWeek(new AsyncCallback<Long>() {
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Failed to get current week: " + caught);
			}

			@Override
			public void onSuccess(Long result) {
				setWeek(result);
			}
		});
	}

	private void createUI() {
		VerticalPanel midPanel = new VerticalPanel();
		userPanel = new UserPanel();
		userList = new UserList(userPanel);
		
		Panel leftPanel = createLeftPanel();
		Panel rightPanel = createRightPanel();


		midPanel.add(UI.makeLabel("User Record:", "headerLabel"));
		midPanel.add(userPanel);
		
		add(leftPanel);
		add(midPanel);
		add(rightPanel);
	}

	private Panel createRightPanel() {
		Panel panel = new VerticalPanel();

		panel.add(UI.makeLabel("Actions", "headerLabel"));
		panel.add(sendReminderButton);
		panel.add(showUsersToRemindButton);
		panel.add(viewLogButton);
		panel.setStylePrimaryName("UserList");

		viewLogButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				adminService.getLog(new AsyncCallback<String>() {
					@Override
					public void onFailure(Throwable caught) {
						Window.alert("Connection failed");
					}

					@Override
					public void onSuccess(String result) {
						LogViewer viewer = new LogViewer(result);

						viewer.center();
					}
				});
			}
		});

		showUsersToRemindButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				adminService.getUsersToRemind(new AsyncCallback<List<UserDAO>>() {

					@Override
					public void onFailure(Throwable caught) {
						Window.alert("Connection failed");
					}

					@Override
					public void onSuccess(List<UserDAO> result) {
						String toShow = "";
						for (UserDAO dao : result) {
							toShow += dao.getName() + "\n";
						}

						LogViewer viewer = new LogViewer(toShow);
						viewer.center();
					}
				});
			}
		});

		sendReminderButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				adminService.remindUsers(new AsyncCallback<Void>() {
					@Override
					public void onFailure(Throwable caught) {
						Window.alert("Connection failed");
					}

					@Override
					public void onSuccess(Void result) {
						// TODO Auto-generated method stub
					}
				});
			}
		});

		return panel;
	}

	private Panel createLeftPanel() {
		VerticalPanel panel = new VerticalPanel();

		panel.setSize("100%", "100%");
		panel.add(userList);
		panel.add(thisWeekLink);

		return panel;
	}

	private void setWeek(Long week) {
		thisWeekLink.setHref("snippets?" + week);
	}
}
