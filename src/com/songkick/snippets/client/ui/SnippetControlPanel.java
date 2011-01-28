package com.songkick.snippets.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.songkick.snippets.client.AdminService;
import com.songkick.snippets.client.AdminServiceAsync;

public class SnippetControlPanel extends VerticalPanel {
	private final AdminServiceAsync adminService = GWT
			.create(AdminService.class);

	private Anchor thisWeekLink = new Anchor("Current week's snippets");
	private UserList userList = new UserList();
	
	public SnippetControlPanel() {
		createUI();
		
		adminService.getCurrentWeek(new AsyncCallback<Long>() {
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onSuccess(Long result) {
				setWeek(result);
			}});
	}
	
	private void createUI() {
		VerticalPanel panel = new VerticalPanel();
		
		panel.setSize("100%", "100%");
		panel.add(userList);
		panel.add(thisWeekLink);
		
		RootPanel.get().add(panel);
	}
	
	private void setWeek(Long week) {
		thisWeekLink.setHref("snippets?" + week);
	}
}
