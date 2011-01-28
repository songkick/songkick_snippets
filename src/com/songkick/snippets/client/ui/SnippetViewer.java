package com.songkick.snippets.client.ui;

import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;

public class SnippetViewer extends DialogBox {
	
	private TextArea textArea = new TextArea();
	
	public SnippetViewer() {
		createUI();
	}
	
	private void createUI() {
		VerticalPanel panel = new VerticalPanel();
		Button closeButton = new Button("Close");
		
		textArea.setSize("600px", "500px");
		panel.add(textArea);
		panel.add(closeButton);
		
		closeButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				SnippetViewer.this.hide();
			}});
		
		setText("Snippet Viewer");
		setWidget(panel);
	}
	
	public void setSnippets(List<String> snippets) {
		String text = "";
		for (String snippet: snippets) {
			text += snippet + "\n\n";
		}
		textArea.setText(text);
	}
	
	public void setSnippets(String string) {
		textArea.setText(string);
	}

}
