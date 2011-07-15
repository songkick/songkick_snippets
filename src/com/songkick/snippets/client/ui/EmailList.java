package com.songkick.snippets.client.ui;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SingleSelectionModel;
import com.songkick.common.client.ui.util.UI;
import com.songkick.common.model.EmailAddress;
import com.songkick.common.model.UserDAO;

public class EmailList extends HorizontalPanel {
	private EmailCell emailCell = new EmailCell();
	private CellList<EmailAddress> cellList = new CellList<EmailAddress>(emailCell);
	private SingleSelectionModel<EmailAddress> selectionModel = new SingleSelectionModel<EmailAddress>();
	private ListDataProvider<EmailAddress> dataProvider = new ListDataProvider<EmailAddress>();

	private UserPanel parent = null;
	
	public EmailList(UserPanel parent) {
		this.parent = parent;
		createUI();
	}

	@Override
	public void clear() {
		dataProvider.setList(new ArrayList<EmailAddress>());
	}
	
	public void setUser(UserDAO user) {
		dataProvider.setList(user.getEmailAddresses());
	}

	private void createUI() {
		ScrollPanel scrollPanel = new ScrollPanel();
		scrollPanel.setSize("20em", "10em");
		scrollPanel.setStylePrimaryName("EmailScrollPanel");

		cellList.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);
		cellList.setSelectionModel(selectionModel);

		dataProvider.addDataDisplay(cellList);

		setSize("100%", "100%");

		scrollPanel.setWidget(cellList);
		scrollPanel.setAlwaysShowScrollBars(true);
		add(scrollPanel);
		
		VerticalPanel buttonPanel = new VerticalPanel();
		
		final Button addButton = UI.makeButton("Add", "Add a new email address");
		final Button deleteButton = UI.makeButton("Delete", "Delete the selected email address");
		final Button primaryButton = UI.makeButton("Toggle Primary", "Reminder and digest emails are only sent to primary addresses");
		buttonPanel.add(addButton);
		buttonPanel.add(deleteButton);
		buttonPanel.add(primaryButton);

		addButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				new AddEmailDialog(EmailList.this, addButton);
			}
		});
		deleteButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				removeSelected();
			}
		});
		
		primaryButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				togglePrimary();
			}
		});
		add(buttonPanel);
	}

	private void removeSelected() {
		EmailAddress email = selectionModel.getSelectedObject();
		if (email != null) {
			dataProvider.getList().remove(email);
			parent.touch();
		}
	}

	public List<EmailAddress> getEmails() {
		return dataProvider.getList();
	}

	public void addEmailToList(EmailAddress email) {
		dataProvider.getList().add(email);
		parent.touch();
	}

	private void togglePrimary() {
		EmailAddress address = selectionModel.getSelectedObject();
		if (address!=null) {
			address.setPrimary(!address.isPrimary());
			parent.touch();
			cellList.redraw();
		}
	}
}
