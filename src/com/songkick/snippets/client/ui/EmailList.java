package com.songkick.snippets.client.ui;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SingleSelectionModel;
import com.songkick.common.model.UserDAO;

public class EmailList extends VerticalPanel {
	private UserDAO user = null;
	private TextCell textCell = new TextCell();
	private CellList<String> cellList = new CellList<String>(textCell);
	private SingleSelectionModel<String> selectionModel = new SingleSelectionModel<String>();
	private ListDataProvider<String> dataProvider = new ListDataProvider<String>();
	private List<String> emailList = new ArrayList<String>();

	public EmailList() {
		createUI();
	}

	public void setUser(UserDAO user) {
		this.user = user;
		
		emailList = new ArrayList<String>();
		
		emailList.addAll(user.getEmailAddresses());
		emailList.addAll(user.getPrimaryEmails());
		
		dataProvider.setList(emailList);
	}

	private void createUI() {
    cellList.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);
    cellList.setSelectionModel(selectionModel);
    
    dataProvider.addDataDisplay(cellList);
    
		/*list.setStylePrimaryName("emailList");
		list.setWidth("100%");
		list.setVisibleItemCount(5);*/

		if (user != null) {
	    dataProvider.setList(user.getEmailAddresses());
		}
	}

	public void removeSelected() {
		// TODO
	}

	public List<String> getEmails() {
		return dataProvider.getList();
	}
	
	public void addEmailToList(String email) {
		emailList.add(email);
	}
	
	public void makeSelectedPrimary() {
		/*String email = list.getItemText(list.getSelectedIndex());
		
		if (email!=null && user!=null) {
			user.getEmailAddresses().remove(email);
			user.getPrimaryEmails().add(email);
		}*/
	}
}
