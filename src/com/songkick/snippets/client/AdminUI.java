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
	private final AdminServiceAsync adminService = GWT.create(AdminService.class);

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		adminService.isValidAdmin("http://sksnippet.appspot.com/",
				new AsyncCallback<String>() {
					@Override
					public void onFailure(Throwable caught) {
						Window.alert("Server connection failed " + caught);
					}

					@Override
					// result is either null, indicating that the user authenticated as
					// an admin, or it contains a redirect URL that takes the user to the
					// login page. After visiting that URL, the user will be redirected
					// back to this entry point
					public void onSuccess(String result) {
						if (result == null) {
							showMainUI();
						} else {
							Window.open(result, "_self", "");
						}
					}
				});
	}

	private void showMainUI() {
		RootPanel.get().add(new SnippetControlPanel());
	}
}
