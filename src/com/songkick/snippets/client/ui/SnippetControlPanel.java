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
import com.songkick.common.client.ui.util.UI;
import com.songkick.common.model.UserDAO;
import com.songkick.snippets.client.AdminService;
import com.songkick.snippets.client.AdminServiceAsync;

public class SnippetControlPanel extends VerticalPanel {
	private final AdminServiceAsync adminService = GWT.create(AdminService.class);

	private Anchor thisWeekLink = new Anchor("Current week's snippets");
	private UserPanel userPanel = null;
	private UserList userList = null;
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
		HorizontalPanel titlePanel = new HorizontalPanel();
		HorizontalPanel mainPanel = new HorizontalPanel();
		
		titlePanel.setWidth("100%");
		titlePanel.setHeight("2em;");
		titlePanel.add(UI.makeLabel("Songkick Snippets Control Panel", "headerLabel"));
		titlePanel.setStylePrimaryName("Title");
		
		userPanel = new UserPanel();
		userList = new UserList(userPanel);
		
		Panel leftPanel = createLeftPanel();
		Panel rightPanel = createRightPanel();
		
		add(titlePanel);
		mainPanel.add(leftPanel);
		mainPanel.add(userPanel);
		mainPanel.add(rightPanel);
		add(mainPanel);
		
		setWidth("100%");
	}

	private Panel createRightPanel() {
		Panel panel = new VerticalPanel();

		panel.add(UI.makeLabel("Actions", "headerLabel"));
		panel.add(showUsersToRemindButton);
		panel.setStylePrimaryName("UserList");

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

		return panel;
	}

	private Panel createLeftPanel() {
		VerticalPanel panel = new VerticalPanel();

		panel.setSize("100%", "100%");
		panel.add(userList);
		
		VerticalPanel linkPanel = new VerticalPanel();
		linkPanel.add(thisWeekLink);
		linkPanel.setStylePrimaryName("StandardIndent");
		panel.add(linkPanel);

		return panel;
	}

	private void setWeek(Long week) {
		thisWeekLink.setHref("snippets?week=" + week);
	}
}
