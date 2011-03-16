package com.songkick.snippets.client.ui.util;

import java.util.Date;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.datepicker.client.DatePicker;

public class DateBox extends HorizontalPanel {
	private String name = null;
	private TextBox textBox = new TextBox();

	public DateBox(String name) {
		this.name = name;
		createUI();
	}

	private void createUI() {
		final DatePicker datePicker = new DatePicker();
		final Button dateButton = UI.makeButton("Date");
		final DialogBox dialog = new DialogBox();

		add(UI.makeLabel(name, "dialogLabel"));
		add(textBox);
		add(dateButton);

		dateButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				dialog.setWidget(datePicker);
				dialog.showRelativeTo(dateButton);
			}
		});

		datePicker.addValueChangeHandler(new ValueChangeHandler<Date>() {
			@Override
			public void onValueChange(ValueChangeEvent<Date> event) {
				Date date = event.getValue();

				String dateString = DateTimeFormat.getFormat(
						DateTimeFormat.PredefinedFormat.DATE_LONG).format(date);
				textBox.setText(dateString);

				dialog.hide();
			}
		});
	}

	public void setDateString(String text) {
		textBox.setText(text);
	}

	public String getDateString() {
		return textBox.getText();
	}

	public Date getDate() {
		String text = textBox.getText();

		DateTimeFormat format = DateTimeFormat
				.getFormat(DateTimeFormat.PredefinedFormat.DATE_LONG);
		return format.parse(text);
	}
}
