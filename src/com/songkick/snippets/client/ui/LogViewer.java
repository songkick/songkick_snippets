package com.songkick.snippets.client.ui;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;

public class LogViewer extends DialogBox {

	private TextArea textArea = new TextArea();
	
	public LogViewer(String log) {
		createUI();
		textArea.setText(log);
	}
	
	private void createUI() {
		VerticalPanel panel = new VerticalPanel();
		Button doneButton = new Button("OK");
		
		textArea.setSize("40em", "24em");
		
		panel.add(textArea);
		panel.add(doneButton);
		doneButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				LogViewer.this.hide();
			}});
		
		setWidget(panel);
		setText("Log viewer");
	}
	
}

