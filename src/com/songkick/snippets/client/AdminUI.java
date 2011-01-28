package com.songkick.snippets.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;
import com.songkick.snippets.client.ui.SnippetControlPanel;

/**
 * Entry point for the Snippets Control Panel
 */
public class AdminUI implements EntryPoint {
	private final AdminServiceAsync adminService = GWT
			.create(AdminService.class);

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		adminService.isValidAdmin(new AsyncCallback<Boolean>() {
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Server connection failed " + caught);
			}

			@Override
			public void onSuccess(Boolean result) {
				if (result) {
					showMainUI();
				} else {
					Window.alert("Please login to an authorized admin account");
				}
			}
		});
	}

	private void showMainUI() {
		RootPanel.get().add(new SnippetControlPanel());
	}
}
